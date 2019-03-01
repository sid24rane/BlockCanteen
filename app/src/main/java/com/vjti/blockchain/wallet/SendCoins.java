package com.vjti.blockchain.wallet;


import android.annotation.SuppressLint;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vjti.blockchain.wallet.utilities.ConnectionLiveData;
import com.vjti.blockchain.wallet.utilities.ConnectionModel;

public class SendCoins extends AppCompatActivity {

    private Button sendUsingPublicKey;
    private Button sendUsingQr;
    private EditText receiverPublicKey;
    private EditText receiverName;
    private EditText amount;
    private EditText message;
    private LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    public static final int MobileData = 2;
    public static final int WifiData = 1;
    private CoordinatorLayout coordinatorLayout;
    private boolean first = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_send_coins);

        checkConnection();
        init();

    }

    private void init() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        sendUsingPublicKey = (Button) findViewById(R.id.send_using_public_key);
        sendUsingQr = (Button) findViewById(R.id.send_using_qr);
        amount = (EditText) findViewById(R.id.amount);
        message = (EditText)findViewById(R.id.message);
        receiverPublicKey = (EditText) findViewById(R.id.publicKey);;
        receiverName = (EditText) findViewById(R.id.name);

        sendUsingQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRScannerActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        sendUsingPublicKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int amt = 0;
                String publicKey = null;
                String name= null;
                String messageTyped= null;

                try{
                    amt = Integer.valueOf(String.valueOf(amount.getText()));
                    publicKey = receiverPublicKey.getText().toString();
                    name = receiverName.getText().toString();
                    messageTyped = message.getText().toString();
                }catch (Exception e){
                    Log.d("Send", e.toString());
                }

                boolean checkField = true;

                if(TextUtils.isEmpty(publicKey) || publicKey.length() != 124){
                    showErrorInSnackBar("Please enter a valid public key!");
                    checkField = false;
                } else if(TextUtils.isEmpty(name)){
                    showErrorInSnackBar("Please type receiver's name!");
                    checkField = false;
                } else if ((amt <= 0)){
                    showErrorInSnackBar(" Cheeky! But amount must be greater than 0");
                    checkField = false;
                } else if(TextUtils.isEmpty(messageTyped)){
                    showErrorInSnackBar("Please type a lovely message!");
                    checkField = false;
                }

                if(checkField){
                    Intent intent = new Intent(SendCoins.this, CheckPinAndMakeTransactionActivity.class);
                    intent.putExtra("amount", String.valueOf(amt));
                    intent.putExtra("message", name + ": " + messageTyped);
                    intent.putExtra("receiverPublicKey", publicKey);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
            }
        });
    }

    @SuppressLint("Range")
    private void showErrorInSnackBar(String error){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, error, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#FFEBEE"));
        snackbar.show();
    }

    private void checkConnection(){

        /* Live data object and setting an oberser on it */
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getApplicationContext());
        connectionLiveData.observe(this, new Observer<ConnectionModel>() {
            @Override
            public void onChanged(@Nullable ConnectionModel connection) {
                /* every time connection state changes, we'll be notified and can perform action accordingly */
                if (connection.getIsConnected()) {
                    switch (connection.getType()) {
                        case WifiData:
                        case MobileData:
                            if (!first){
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Internet Connected!", Snackbar.LENGTH_LONG);
                                View view = snackbar.getView();
                                CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                                params.gravity = Gravity.TOP;
                                view.setLayoutParams(params);
                                snackbar.show();
                            }
                            break;
                    }
                } else {
                    first = false;
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(Color.RED);
                    View view = snackbar.getView();
                    CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)view.getLayoutParams();
                    params.gravity = Gravity.TOP;
                    view.setLayoutParams(params);
                    TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        first = true;
    }
    /* required to make activity life cycle owner */
    @Override
    public LifecycleRegistry getLifecycle() {
        return lifecycleRegistry;
    }
}
