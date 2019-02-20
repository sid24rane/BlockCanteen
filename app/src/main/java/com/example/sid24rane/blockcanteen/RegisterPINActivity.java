package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.hanks.passcodeview.PasscodeView;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

public class RegisterPINActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pin);

        PasscodeView passcodeView = (PasscodeView) findViewById(R.id.password);
        passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
                Toast.makeText(RegisterPINActivity.this, "Incorrect Pin!", Toast.LENGTH_SHORT).show();
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
