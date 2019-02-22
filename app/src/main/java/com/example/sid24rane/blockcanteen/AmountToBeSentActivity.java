package com.example.sid24rane.blockcanteen;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.utilities.ConnectionLiveData;
import com.example.sid24rane.blockcanteen.utilities.ConnectionModel;

public class AmountToBeSentActivity extends AppCompatActivity {

    private Button send;
    private String receiverPublicKey;
    private EditText amount;
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


        Intent i = getIntent();
        receiverPublicKey = i.getStringExtra("publicKey");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        send = (Button) findViewById(R.id.send);
        amount = (EditText) findViewById(R.id.amount);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amt = Integer.valueOf(amount.getText().toString());
                if (!(amt <=0)){
                    Intent intent = new Intent(AmountToBeSentActivity.this, CheckPINActivity.class);
                    intent.putExtra("amount", amount.getText().toString());
                    intent.putExtra("receiverPublicKey", receiverPublicKey);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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
                                snackbar.show();
                            }
                            break;
                    }
                } else {
                    first = false;
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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
