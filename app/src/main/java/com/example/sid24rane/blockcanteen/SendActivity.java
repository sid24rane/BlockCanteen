package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.TransactionUtils;

public class SendActivity extends AppCompatActivity {

    private Button send;
    private TextView name;
    private TextView email;
    private String receiverPublicKey;
    private EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        init();
        fetchRecieverUserDetails(receiverPublicKey);

    }


    private void init() {

        Intent i = getIntent();
        receiverPublicKey = i.getStringExtra("publicKey");

        send = (Button) findViewById(R.id.send);
        name = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        amount = (EditText) findViewById(R.id.amount);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String publicKey = DataInSharedPreferences.retrievingPublicKey(SendActivity.this);

                TransactionUtils.makeTransaction(amount.getText().toString(),publicKey,receiverPublicKey, SendActivity.this);

                Toast.makeText(SendActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SendActivity.this,DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
    }

    private void fetchRecieverUserDetails(String receiverPublicKey) {
        //TODO retreive user details from SharedPref
    }

}
