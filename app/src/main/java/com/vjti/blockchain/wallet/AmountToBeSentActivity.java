package com.vjti.blockchain.wallet;

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
import android.widget.Toast;

import com.vjti.blockchain.wallet.Dashboard.DashboardActivity;
import com.vjti.blockchain.wallet.utilities.ConnectionLiveData;
import com.vjti.blockchain.wallet.utilities.ConnectionModel;

public class AmountToBeSentActivity extends AppCompatActivity {

    private Button send;
    private String receiverPublicKey;
    private String receiverName;
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
        setContentView(R.layout.activity_send);

        checkConnection();
        init();

    }

    private void init() {

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        Intent i = getIntent();
        receiverPublicKey = i.getStringExtra("publicKey");
        receiverName = i.getStringExtra("receiverName");

        send = (Button) findViewById(R.id.send);
        amount = (EditText) findViewById(R.id.amount);
        message = (EditText)findViewById(R.id.message);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amt = Integer.valueOf(String.valueOf(amount.getText()));
                if (!(amt <= 0)){
                    String messageTyped = message.getText().toString();
                    if(!TextUtils.isEmpty(messageTyped)){
                        Log.d("Message", messageTyped);
                        Intent intent = new Intent(AmountToBeSentActivity.this, CheckPINActivity.class);
                        intent.putExtra("amount", String.valueOf(amount.getText()));
                        intent.putExtra("message", receiverName + ": " + messageTyped);
                        intent.putExtra("receiverPublicKey", receiverPublicKey);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                    }else{
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Please type a lovely message!", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);
                        snackbar.show();
                    }
                }else{
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, " Cheeky! But amount must be greater than 0", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();
                }

            }
        });
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
