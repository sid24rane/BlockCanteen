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
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

import static android.content.Context.MODE_PRIVATE;

public class DataInSharedPreferences {

    private static final String PREFS_NAME = "DataFile";
    private static final String TAG = "DataInSharedPreferences";

    public static String getPrefsName() {
        return PREFS_NAME;
    }

    public void storingData(KeyPair pair, JSONObject details) throws JSONException {
        Log.d(TAG, "storingKeyPair() invoked");
        String pubKey = getPublicKeyAsString(pair);
        String privateKey = getPrivateKeyAsString(pair);
        SecurePreferences.setValue("fullName", String.valueOf(details.get("fullName")));
        SecurePreferences.setValue("emailAddress", String.valueOf(details.get("emailAddress")));
        SecurePreferences.setValue("publicKey", pubKey.replaceAll("\\s+", ""));
        SecurePreferences.setValue("privateKey", privateKey.replaceAll("\\s+", ""));
    }

    public static String retrievingPublicKey(){
        Log.d(TAG, "retrievingPublicKey() invoked");
        String publicKey = SecurePreferences.getStringValue("publicKey", "");
        Log.d("PublicKeyString ", publicKey);
        return publicKey;
    }

    public static String retrievingPrivateKey(){
        Log.d(TAG, "retrievingPrivateKey() invoked");
        String privateKey = SecurePreferences.getStringValue("privateKey", "");
        Log.d("PrivateKeyString", privateKey);

        return privateKey;
    }

    private String getPublicKeyAsString(KeyPair keyPair){
        Log.d(TAG, "getPublicKeyAsString() invoked");
        PublicKey mPublicKey = keyPair.getPublic();
        String publicKey = new String(android.util.Base64.encode(mPublicKey.getEncoded(), android.util.Base64.DEFAULT));
        Log.d(TAG,"PublicKeyString: " +  publicKey);
        return publicKey;
    }

    private String getPrivateKeyAsString(KeyPair keyPair){
        PrivateKey mPrivateKey = keyPair.getPrivate();
        String privateKey = null;
        privateKey = new String(android.util.Base64.encode(mPrivateKey.getEncoded(), android.util.Base64.DEFAULT));
        Log.d(TAG,"PrivateKeyString: " +  privateKey);
        return privateKey;
    }

    public PrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
        String privateKeyPEM = key;
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----\n", "");
        privateKeyPEM = privateKeyPEM.replace("-----END PRIVATE KEY-----", "");
        privateKeyPEM = privateKeyPEM.replace("\n", "");
        byte[] encoded = new byte[0];
        encoded = android.util.Base64.decode(privateKeyPEM, android.util.Base64.DEFAULT);
        KeyFactory kf = KeyFactory.getInstance("EC");
        PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
        return privKey;
    }

}
