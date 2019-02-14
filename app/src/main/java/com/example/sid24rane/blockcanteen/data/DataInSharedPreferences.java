package com.example.sid24rane.blockcanteen.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.sid24rane.blockcanteen.RestoreActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static android.content.Context.MODE_PRIVATE;

public class DataInSharedPreferences {

    private static final String PREFS_NAME = "DataFile";
    private static final String TAG = "DataInSharedPreferences";

    public static String getPrefsName() {
        return PREFS_NAME;
    }

    public static void storingKeyPair(KeyPair pair, Context context){
        Log.d(TAG, "storingKeyPair() invoked");
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        String pubKey = getPublicKeyAsString(pair, context);
        String privateKey = getPrivateKeyAsString(pair, context);

        //TODO : remove these pairs after testing
        //String pubKey = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEX8SWHr9f+UqdGPow8JgXbu785ivTodsfa64u9CO8qAqnwiVJegHi8smwY8Nv7h/zLiMjy370CgM4jS7WMeJwbg==";
        //String privateKey = "MD4CAQAwEAYHKoZIzj0CAQYFK4EEAAoEJzAlAgEBBCAiOOtgpFImH/M1rfwOY8Wx83MiJcWkEx3iEx7i/Jl5NA==";

        editor.putString("publicKey", pubKey);
        editor.putString("privateKey", privateKey);
        editor.commit();
    }

    public static String retrievingPublicKey(Context context){
        Log.d(TAG, "retrievingPublicKey() invoked");
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String publicKey = mPrefs.getString("publicKey", "");
        Log.d("PublicKeyString ", publicKey);

        return publicKey;
    }

    public static String retrievingPrivateKey(Context context){
        Log.d(TAG, "retrievingPrivateKey() invoked");
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String privateKey = mPrefs.getString("privateKey", "");
        Log.d("PrivateKeyString", privateKey);

        return privateKey;
    }

    private static String getPublicKeyAsString(KeyPair keyPair, Context context){
        Log.d(TAG, "getPublicKeyAsString() invoked");
        //KeyPair mKeyPair = retrievingKeyPair(context);

        PublicKey mPublicKey = keyPair.getPublic();
        String publicKey = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            publicKey = new String(Base64.getEncoder().encode(mPublicKey.getEncoded()));
        }

        Log.d(TAG,"Public Key : " +  publicKey);
        return publicKey;
    }

    private static String getPrivateKeyAsString(KeyPair keyPair, Context context){
        //KeyPair mKeyPair = retrievingKeyPair(context);
        //PrivateKey mPrivateKey = mKeyPair.getPrivate();

        PrivateKey mPrivateKey = keyPair.getPrivate();
        String privateKey = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            privateKey = new String(Base64.getEncoder().encode(mPrivateKey.getEncoded()));
        }

        Log.d(TAG,"Private Key : " +  privateKey);
        return privateKey;
    }

    public  static PrivateKey getPrivateKeyFromString(String key) throws IOException, GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("\n", "");
        byte[] encoded = new byte[0];
        //String publicKey = new String(android.util.Base64.encode(Key.getEncoded(), Base64.DEFAULT));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encoded = Base64.getDecoder().decode(privateKeyPEM);
        }

        KeyFactory kf = KeyFactory.getInstance("EC");
        PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        return privKey;
    }

    private static PublicKey getPublicKeyFromString(String key) throws IOException, GeneralSecurityException {
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

    public static void storingUserDetails(JSONObject userJSON, Context context){
        Log.d(TAG, "storingUserDetails() invoked");
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();

        try {
            editor.putString("firstName", String.valueOf(userJSON.get("firstName")));
            editor.putString("lastName", String.valueOf(userJSON.get("lastName")));
            editor.putString("emailAddress", String.valueOf(userJSON.get("emailAddress")));
            editor.putString("userType", String.valueOf(userJSON.get("userType")));
            editor.putString("userDepartment", String.valueOf(userJSON.get("userDepartment")));
            editor.putString("yearOfAdmission", String.valueOf(userJSON.get("yearOfAdmission")));

            if(context instanceof RestoreActivity){
                editor.putString("publicKey", String.valueOf(userJSON.get("publicKey")));
                editor.putString("privateKey", String.valueOf(userJSON.get("privateKey")));
            }

            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
