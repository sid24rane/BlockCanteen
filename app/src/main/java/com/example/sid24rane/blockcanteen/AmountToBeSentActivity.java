package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;

public class AmountToBeSentActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private Button send;
    private String receiverPublicKey;
    private EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_send);
        init();
    }

    private void init() {

        Intent i = getIntent();
        receiverPublicKey = i.getStringExtra("publicKey");

        send = (Button) findViewById(R.id.send);
        amount = (EditText) findViewById(R.id.amount);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AmountToBeSentActivity.this, CheckPINActivity.class);
                intent.putExtra("amount", amount.getText().toString());
                intent.putExtra("receiverPublicKey", receiverPublicKey);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
    }


}
