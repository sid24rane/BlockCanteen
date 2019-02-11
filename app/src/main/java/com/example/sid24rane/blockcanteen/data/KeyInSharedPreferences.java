package com.example.sid24rane.blockcanteen.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import static android.content.Context.MODE_PRIVATE;

public class KeyInSharedPreferences {

    private static final String PREFS_NAME = "KeyFile";
    private static final String TAG = "KeyInSharedPreferences";

    public static void storingKeyPair(KeyPair pair, Context context){
        Log.d(TAG, "storingKeyPair() invoked");
        android.content.SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String jsonPair = gson.toJson(pair);
        editor.putString("keyPair", jsonPair);
        editor.commit();
    }

    public static KeyPair retrievingKeyPair(Context context){
        Log.d(TAG, "retrievingKeyPair() invoked");
        Gson gson = new Gson();
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String jsonPair = mPrefs.getString("keyPair", "");
        KeyPair keyPair = gson.fromJson(jsonPair, KeyPair.class);
        Log.d("Retrieved KeyPair", keyPair.getPublic().toString()
                + " " +  keyPair.getPrivate().toString());
        return keyPair;
    }

    public static String getPublicKeyAsString(Context context){
        Log.d(TAG, "getPublicKeyAsString() invoked");
        KeyPair mKeyPair = retrievingKeyPair(context);
        PublicKey mPublicKey = mKeyPair.getPublic();
        String publicKey = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            publicKey = new String(Base64.getEncoder().encode(mPublicKey.getEncoded()));
        }

        Log.d(TAG,"Public Key : " +  publicKey);
        return publicKey;
    }

    private String getPrivateKeyAsString(Context context){
        KeyPair mKeyPair = retrievingKeyPair(context);
        PrivateKey mPrivateKey = mKeyPair.getPrivate();
        String privateKey = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            privateKey = new String(Base64.getEncoder().encode(mPrivateKey.getEncoded()));
        }

        Log.d(TAG,"Private Key : " +  privateKey);
        return privateKey;
    }

}
