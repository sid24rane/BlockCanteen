package com.example.sid24rane.blockcanteen.Dashboard.Home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.QRGeneratorActivity;
import com.example.sid24rane.blockcanteen.QRScannerActivity;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.utilities.NetworkUtils;

import okhttp3.OkHttpClient;


public class HomeFragment extends Fragment {

    private Button send;
    private Button receive;
    private TextView mBalanceTextView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private final String TAG = getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        send = (Button) view.findViewById(R.id.send);
        receive = (Button) view.findViewById(R.id.receive);
        mBalanceTextView = (TextView)  view.findViewById(R.id.balance);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) view.findViewById(R.id.error_message);

        getUserBalance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),QRScannerActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),QRGeneratorActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        return view;
    }


    private void getUserBalance() {

        networkInit();
        showBalanceView();

        // TODO : Fetch public key from SharedPref
        //String pub_key = KeyInSharedPreferences.getPublicKeyAsString(getContext());
        String pub_key = "MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAExcIsvLH3vegArqtP7wEdyly11xAcrpV4IBIUCVM+HXoPMMpNFX8hYDjOPL4IUT4swqDkrhj1gS+XWukiGpttzQ==";
        new FetchBalanceTask().execute(pub_key);
    }

    private void networkInit(){
        OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                .build();
        AndroidNetworking.initialize(getContext(),okHttpClient);
    }

    private void showBalanceView() {
        Log.d(TAG, "showBalanceView() invoked");
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mBalanceTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        Log.d(TAG, "showErrorMessage() invoked");
        mBalanceTextView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchBalanceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute() invoked");
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            try {
                Log.d(TAG, "makingNetworkCall ");
                final String[] result = new String[1];
                String pub_key = params[0];

                AndroidNetworking.post(NetworkUtils.getCheckBalanceUrl())
                        .addUrlEncodeFormBodyParameter("public_key",pub_key)
                        .setContentType("application/x-www-form-urlencoded")
                        .setTag("test")
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "network onResponse= " + response);
                                showBalanceView();
                                mBalanceTextView.setText(response);
                                result[0] = response;
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d(TAG, "network onError= " + anError);
                            }
                        });

                return result[0];

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String balance) {
            Log.d(TAG, "onPostExecute() invoked with balance:" + balance);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (balance != null) {
                showBalanceView();
                mBalanceTextView.setText(balance);
            } else {
                showBalanceView();
            }
        }


    }

}
