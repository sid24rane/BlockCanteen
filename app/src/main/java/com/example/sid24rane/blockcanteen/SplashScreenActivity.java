package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;

import okhttp3.OkHttpClient;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                networkInit();

                Intent intent = new Intent(SplashScreenActivity.this,DashboardActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }

            private void networkInit() {
                OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                        .build();
                AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
            }
        },3000);

    }
}
