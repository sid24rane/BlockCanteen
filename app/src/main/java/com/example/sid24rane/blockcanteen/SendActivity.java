package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.NetworkUtils;
import com.example.sid24rane.blockcanteen.utilities.TransactionUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class SendActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
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
                newTransaction();
                Toast.makeText(SendActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SendActivity.this,DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });
    }

    private void newTransaction(){
        String publicKey = DataInSharedPreferences.retrievingPublicKey(SendActivity.this);
        makeTransaction(amount.getText().toString(), publicKey, receiverPublicKey);
    }


    private void makeTransaction(String amount, String sender_pub, String receiver_pub){
        Log.d(TAG, "maketransaction() invoked");

        JSONObject json = new JSONObject();

        try {
            json.put("bounty",amount);
            json.put("receiver_public_key", receiver_pub);
            json.put("sender_public_key",sender_pub);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(NetworkUtils.getMakeTransactionUrl())
                .addJSONObjectBody(json)
                .setContentType("application/json")
                .setTag("makeTransaction")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("makeTransaction res: ", response.toString());
                        try {
                            JSONObject responseJson = new JSONObject(response.toString());

                            String send_this = responseJson.getString("send_this");
                            String sign_this = responseJson.getString("sign_this");
                            Log.d(TAG, "send_this : "+  send_this);
                            Log.d(TAG, "sign_this : " + sign_this);

                            String signedString = new TransactionUtils().signString(sign_this, SendActivity.this);

                            sendTransaction(send_this, signedString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("makeTransaction err:", anError.toString());

                    }
                });
    }

    private void sendTransaction(String transaction, String signature){

        Log.d(TAG, "Transaction : " + transaction);
        Log.d(TAG, "Signature : " + signature);

        JSONObject json = new JSONObject();

        try {
            json.put("transaction", transaction);
            json.put("signature", signature);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(NetworkUtils.getSendTransactionUrl())
                .addJSONObjectBody(json)
                .setContentType("application/json")
                .setTag("sendTransaction")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "sndTxn onResponse= " + response.toString());
                        //TODO : update UI
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "sndTxn onError= " + anError.toString());
                    }
                });
    }

}
