package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.KeyGeneration.KeyGenerationActivity;

import okhttp3.OkHttpClient;

public class SplashScreenActivity extends AppCompatActivity {

    private final String PREFS_NAME = "DataFile";
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                networkInit();

                if (checkKeyPair()){
                    Intent intent = new Intent(SplashScreenActivity.this,DashboardActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }else{
                    Intent intent = new Intent(SplashScreenActivity.this,KeyGenerationActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }

            }

            private void networkInit() {
                OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .build();
                AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
            }
        },3000);


    }

    private boolean checkKeyPair() {
        Log.d(TAG, "checkKeyPair() invoked");
        SharedPreferences preferences =  getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String public_key = preferences.getString("publicKey",null);
        String private_key = preferences.getString("privateKey",null);
        if (public_key == null || private_key == null){
            Log.d(TAG, "either private or public key doesn't exist");
            return false;
        }else{
            return true;
        }
    }
}
