package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.hanks.passcodeview.PasscodeView;

public class CheckPINActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_pin);

        PasscodeView passcodeView = (PasscodeView) findViewById(R.id.password);
        passcodeView.setLocalPasscode("sd");
        passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {

            }
            @Override
            public void onSuccess(String number) {
                Intent i = new Intent(CheckPINActivity.this,TransactionResultActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
