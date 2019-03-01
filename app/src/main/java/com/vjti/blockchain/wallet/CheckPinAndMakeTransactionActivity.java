package com.vjti.blockchain.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.hanks.passcodeview.PasscodeView;
import com.vjti.blockchain.wallet.data.DataInSharedPreferences;
import com.vjti.blockchain.wallet.utilities.NetworkUtils;
import com.vjti.blockchain.wallet.utilities.TransactionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

public class CheckPinAndMakeTransactionActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private String amount;
    private String receiverPublicKey;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_check_pin);

        Intent intent = getIntent();
        amount = intent.getStringExtra("amount");
        message = intent.getStringExtra("message");
        receiverPublicKey = intent.getStringExtra("receiverPublicKey");


        PasscodeView passcodeView = (PasscodeView) findViewById(R.id.password);
        String pin = SecurePreferences.getStringValue("pin", "");
        passcodeView.setLocalPasscode(pin);

        passcodeView.setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
            }
            @Override
            public void onSuccess(String number) {
                newTransaction(amount, receiverPublicKey);
            }
        });
    }

    private void newTransaction(String amount, String receiverPublicKey){
        String publicKey = DataInSharedPreferences.retrievingPublicKey();
        makeTransaction(amount, publicKey, receiverPublicKey);
    }


    private void makeTransaction(final String amount, String sender_pub, String receiver_pub){
        Log.d(TAG, "maketransaction() invoked");

        JSONObject json = new JSONObject();

        try {
            json.put("bounty",amount);
            json.put("receiver_public_key", receiver_pub);
            json.put("sender_public_key",sender_pub);
            json.put("message", message);
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

                        if(response.split(" ")[0].equals("False")){
                            Intent i = new Intent(CheckPinAndMakeTransactionActivity.this, TransactionResultActivity.class);
                            i.putExtra("result", "false");
                            i.putExtra("amount", amount);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                            startActivity(i);
                            finish();
                        }

                        try {
                            JSONObject responseJson = new JSONObject(response.toString());

                            String send_this = responseJson.getString("send_this");
                            String sign_this = responseJson.getString("sign_this");
                            Log.d(TAG, "send_this : "+  send_this);
                            Log.d(TAG, "sign_this : " + sign_this);

                            String signedString = new TransactionUtils().signString(sign_this, CheckPinAndMakeTransactionActivity.this);

                            sendTransaction(send_this, signedString);

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("makeTransaction err:", anError.toString());
                        Intent i = new Intent(CheckPinAndMakeTransactionActivity.this, TransactionResultActivity.class);
                        i.putExtra("result", "false");
                        i.putExtra("amount",amount);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                        startActivity(i);
                        finish();

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
                        Intent i = new Intent(CheckPinAndMakeTransactionActivity.this, TransactionResultActivity.class);
                        i.putExtra("result","true");
                        i.putExtra("amount", amount);
                        i.putExtra("message", message);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "sndTxn onError= " + anError.toString());
                    }
                });
    }
}
