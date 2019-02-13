package com.example.sid24rane.blockcanteen.utilities;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

public class TransactionUtils {
    public static String TAG = "TransactionUtils";

    public static void makeTransaction(String amount, String sender_pub, String receiver_pub){

        //TODO : check the request input params
        AndroidNetworking.post(NetworkUtils.getMakeTransactionUrl())
                .addUrlEncodeFormBodyParameter("bounty",amount)
                .addUrlEncodeFormBodyParameter("receiver_public_key", sender_pub)
                .addUrlEncodeFormBodyParameter("sender_public_key",receiver_pub)
                .setContentType("application/x-www-form-urlencoded")
                .setTag("makeTransaction")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "network onResponse= " + response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "network onError= " + anError);
                    }
                });
    }

    public static void sendTransaction(String transaction, String signature){

        //TODO : check the request input params
        AndroidNetworking.post(NetworkUtils.getSendTransactionUrl())
                .addUrlEncodeFormBodyParameter("transaction", transaction)
                .addUrlEncodeFormBodyParameter("signature", signature)
                .setContentType("application/x-www-form-urlencoded")
                .setTag("sendTransaction")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "network onResponse= " + response);

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "network onError= " + anError);
                    }
                });
    }

}
