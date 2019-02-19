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
        String pubKey = getPublicKeyAsString(pair);
        String privateKey = getPrivateKeyAsString(pair);

        editor.putString("publicKey", pubKey.replaceAll("\\s+", ""));
        editor.putString("privateKey", privateKey.replaceAll("\\s+", ""));
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

    private static String getPublicKeyAsString(KeyPair keyPair){
        Log.d(TAG, "getPublicKeyAsString() invoked");
        PublicKey mPublicKey = keyPair.getPublic();
        String publicKey = new String(android.util.Base64.encode(mPublicKey.getEncoded(), android.util.Base64.DEFAULT));
        Log.d(TAG,"PublicKeyString: " +  publicKey);
        return publicKey;
    }

    private static String getPrivateKeyAsString(KeyPair keyPair){
        PrivateKey mPrivateKey = keyPair.getPrivate();
        String privateKey = null;
        privateKey = new String(android.util.Base64.encode(mPrivateKey.getEncoded(), android.util.Base64.DEFAULT));
        Log.d(TAG,"PrivateKeyString: " +  privateKey);
        return privateKey;
    }

    public  static PrivateKey getPrivateKeyFromString(String key) throws GeneralSecurityException {
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

    public static void storingUserDetails(JSONObject userJSON, Context context){
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
            Log.d(TAG, "storingUserDetails() done");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject getUserDetails(Context context) throws JSONException {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        JSONObject json = new JSONObject();
        json.put("firstName", mPrefs.getString("firstName", ""));
        json.put("lastName", mPrefs.getString("lastName", ""));
        json.put("emailAddress", mPrefs.getString("emailAddress", ""));
        json.put("userType", mPrefs.getString("userType", ""));
        json.put("userDepartment", mPrefs.getString("userDepartment", ""));
        json.put("yearOfAdmission", mPrefs.getString("yearOfAdmission", ""));
        json.put("publicKey", mPrefs.getString("publicKey", ""));

        Log.d(TAG, "getUserDetails() " + json.toString());
        return json;
    }
}
