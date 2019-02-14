package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.TransactionUtils;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class SendActivity extends AppCompatActivity {

    private Button send;
    private TextView name;
    private TextView email;
    private String receiverPublicKey;
    private EditText amount;
    private KonfettiView viewKonfetti;

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
        viewKonfetti = (KonfettiView) findViewById(R.id.viewKonfetti);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String sender_pub_key = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAExcIsvLH3vegArqtP7wEdyly11xAcrpV4IBIUCVM+HXoPMMpNFX8hYDjOPL4IUT4swqDkrhj1gS+XWukiGpttzQ==";
                String publicKey = DataInSharedPreferences.retrievingPublicKey(SendActivity.this);

                TransactionUtils.makeTransaction(amount.getText().toString(),publicKey,receiverPublicKey, SendActivity.this);

                viewKonfetti.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(12, 5))
                        .setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                        .streamFor(300, 5000L);

                Intent intent = new Intent(SendActivity.this,DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
    }

    private void fetchRecieverUserDetails(String receiverPublicKey) {
        //TODO retreive user details from firebase
    }

}
