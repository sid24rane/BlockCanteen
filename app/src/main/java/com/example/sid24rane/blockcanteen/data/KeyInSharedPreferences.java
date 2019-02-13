package com.example.sid24rane.blockcanteen.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static android.content.Context.MODE_PRIVATE;

public class KeyInSharedPreferences {

    private static final String PREFS_NAME = "KeyFile";
    private static final String TAG = "KeyInSharedPreferences";

    public static void storingKeyPair(KeyPair pair, Context context){
        Log.d(TAG, "storingKeyPair() invoked");
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        String pubKey = getPublicKeyAsString(pair, context);
        String privateKey = getPrivateKeyAsString(pair, context);

        editor.putString("publicKey", pubKey);
        editor.putString("privateKey", privateKey);
        editor.commit();
    }

    public static String retrievingPublicKey(Context context){
        Log.d(TAG, "retrievingPublicKey() invoked");
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String publicKey = mPrefs.getString("publicKey", "");
        Log.d("Retrieved PublicKey ", publicKey);

        return publicKey;
    }

    public static String retrievingPrivateKey(Context context){
        Log.d(TAG, "retrievingPrivateKey() invoked");
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String privateKey = mPrefs.getString("privateKey", "");
        Log.d("Retrieved PrivateKey ", privateKey);

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
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        PrivateKey privKey = (PrivateKey) kf.generatePrivate(keySpec);
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

}
