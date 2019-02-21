package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.androidnetworking.AndroidNetworking;
import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.KeyGeneration.KeyGenerationActivity;

import de.adorsys.android.securestoragelibrary.SecurePreferences;
import okhttp3.OkHttpClient;

public class SplashScreenActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                networkInit();

                    if (checkKeyPair() && checkPin()){
                        Intent intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                    }else if(!checkPin()){

                        if(checkKeyPair()){
                            Intent intent = new Intent(SplashScreenActivity.this, RegisterPINActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                        }else{
                            Intent intent = new Intent(SplashScreenActivity.this, KeyGenerationActivity.class);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                        }
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
        Log.d(TAG, "checkCredentials() invoked");
        if (SecurePreferences.contains("publicKey") && SecurePreferences.contains("privateKey") ){
            Log.d(TAG, "either private or public key doesn't exist or pin doesnt exist");
            return true;
        }else{
            return false;
        }
    }

    private boolean checkPin()
    {
        if(SecurePreferences.contains("pin")){
            return true;
        }else{
            return false;
        }
    }

}
