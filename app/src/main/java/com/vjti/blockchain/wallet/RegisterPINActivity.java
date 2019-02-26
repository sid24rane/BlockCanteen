package com.vjti.blockchain.wallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.vjti.blockchain.wallet.Dashboard.DashboardActivity;
import com.vjti.blockchain.wallet.KeyGeneration.KeyGenerationActivity;
import com.vjti.blockchain.wallet.data.DataInSharedPreferences;
import com.hanks.passcodeview.PasscodeView;

import java.security.Key;
import java.security.KeyPair;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

public class RegisterPINActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_register_pin);

        PasscodeView passcodeView = (PasscodeView) findViewById(R.id.password);
        passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
            }

            @Override
            public void onSuccess(String number) {
                SecurePreferences.setValue("pin", number);
                Intent i = new Intent(RegisterPINActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
