package com.example.sid24rane.blockcanteen.KeyGeneration;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.sid24rane.blockcanteen.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


public class KeyGenerationActivity extends AppCompatActivity {

    private final String PREFS_NAME = "KeyFile";
    private final String CLASS_NAME = getClass().getSimpleName();
    private static String publicKey;
    private static String privateKey;
    private static KeyPair mKeyPair;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_generation);
        // Access a Cloud Firestore instance from your Activity

        try {
            generateKeyPair();
            getKeysAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    private static String getKey(String filename) throws IOException {
        // Read key from file
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }

    private  PrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("\n", "");
        byte[] encoded = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encoded = Base64.getDecoder().decode(privateKeyPEM);
        }
        KeyFactory kf = KeyFactory.getInstance("EC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        PrivateKey privKey = (PrivateKey) kf.generatePrivate(keySpec);
        return privKey;
    }

    private PublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----\n", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("\n", "");
        byte[] encoded = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encoded = Base64.getDecoder().decode(publicKeyPEM);
        }
        KeyFactory kf = KeyFactory.getInstance("EC");
        PublicKey pubKey = (PublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
        return pubKey;
    }

    public static BigInteger extractR(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startR + 2, startR + 2 + lengthR));
    }

    public static BigInteger extractS(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        int startS = startR + 2 + lengthR;
        int lengthS = signature[startS + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startS + 2, startS + 2 + lengthS));
    }

    public static byte[] derSign(BigInteger r, BigInteger s) throws Exception {
        byte[] rb = r.toByteArray();
        byte[] sb = s.toByteArray();
        int off = (2 + 2) + rb.length;
        int tot = off + (2 - 2) + sb.length;
        byte[] der = new byte[tot + 2];
        der[0] = 0x30;
        der[1] = (byte) (tot & 0xff);
        der[2 + 0] = 0x02;
        der[2 + 1] = (byte) (rb.length & 0xff);
        System.arraycopy(rb, 0, der, 2 + 2, rb.length);
        der[off + 0] = 0x02;
        der[off + 1] = (byte) (sb.length & 0xff);
        System.arraycopy(sb, 0, der, off + 2, sb.length);
        return der;
    }

    public static String getSignatureString(byte[] sign) throws Exception {
        // System.out.println("Signature: " + new BigInteger(1, sign).toString(16));

        BigInteger r = extractR(sign);
        BigInteger s = extractS(sign);
        String realSign = "[" + r.toString() + ", " + s.toString() + "]";
        return realSign;
    }


    public void sendRegistrationDetails(UserModel user){
        // Create a new user with a first and last name
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> object = new HashMap<>();
        object.put("firstName", user.getFirstName());
        object.put("lastName", user.getLastName());
        object.put("email", user.getEmail());
        object.put("id", user.getId());
        object.put("publicKey", user.getPublicKey());

        db.collection("users")
                .add(object)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(CLASS_NAME, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(CLASS_NAME, "Error adding document", e);
                    }
                });
    }

    public void generateKeyPair() throws Exception {
        Log.d(CLASS_NAME, "generateKeyPair invoked");
        // Generate a Key Pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(ecSpec, random);
        mKeyPair = keyGen.generateKeyPair();

        // Store this keyPair in Shared Preferences
        storingKeyPair(mKeyPair);

        // Restoring Pub/Priv Keys from String
        PrivateKey restoredPriv = getPrivateKeyFromString(privateKey);
        PublicKey restoredPub = getPublicKeyFromString(publicKey);

    }

    private void storingKeyPair(KeyPair pair){
        Log.d(CLASS_NAME, "StoringKeyPair invoked ");
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String jsonPair = gson.toJson(pair);
        editor.putString("keyPair", jsonPair);
        editor.commit();
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getKeysAsString(){
        PublicKey mPublicKey = mKeyPair.getPublic();
        PrivateKey mPrivateKey = mKeyPair.getPrivate();

        // Storing the Pub/Priv Key as String
        publicKey = new String(Base64.getEncoder().encode(mPublicKey.getEncoded()));
        privateKey = new String(Base64.getEncoder().encode(mPrivateKey.getEncoded()));
        Log.d("Public key: ", publicKey);
        Log.d("Private key: ", privateKey);

    }


    public String signString(String stringToBeSigned) throws Exception {
        Log.d(CLASS_NAME, "signString invoked");
        KeyPair keyPair = retrievingKeyPair();
        // Signing a String
        Signature dsa = Signature.getInstance("SHA256withECDSA");
        dsa.initSign(keyPair.getPrivate()); // Pass the private Key that we need.

        // The string that needs to be signed.
        byte[] strByte = stringToBeSigned.getBytes("UTF-8");
        dsa.update(strByte);

        // Actual Signing of the String 'stringToBeSigned' with PrivateKey 'priv'.
        byte[] sign = dsa.sign();
        String signature = getSignatureString(sign);

        //TODO : make a network request to send [signature, stringToBeSigned, publicKey]
        return signature;
    }

    private KeyPair retrievingKeyPair(){
        Log.d(CLASS_NAME, "retrievingKeyPair invoked");
        Gson gson = new Gson();
        SharedPreferences  mPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String jsonPair = mPrefs.getString("keyPair", "");
        KeyPair keyPair = gson.fromJson(jsonPair, KeyPair.class);
        Log.d("Retrieved KeyPair", keyPair.getPublic().toString()
                 + " " +  keyPair.getPrivate().toString());
        return keyPair;
    }

}
