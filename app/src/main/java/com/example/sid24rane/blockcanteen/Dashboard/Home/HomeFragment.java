package com.example.sid24rane.blockcanteen.Dashboard.Home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.QRGeneratorActivity;
import com.example.sid24rane.blockcanteen.QRScannerActivity;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.NetworkUtils;

import org.json.JSONObject;

import okhttp3.OkHttpClient;


public class HomeFragment extends Fragment {

    private Button send;
    private Button receive;
    private TextView mBalanceTextView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private final String TAG = getClass().getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        send = (Button) view.findViewById(R.id.send);
        receive = (Button) view.findViewById(R.id.receive);
        mBalanceTextView = (TextView)  view.findViewById(R.id.balance);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) view.findViewById(R.id.error_message);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        getUserBalance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = checkConnection();
                if(isConnected){
                    Intent intent = new Intent(getContext(),QRScannerActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
                else{
                    showToast(isConnected);
                }
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                getUserBalance();
            }
        });

        // Scheme colors for animation
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light)
        );

        return view;
    }


    private boolean checkConnection() {
        //TODO: lib
        return true;
    }

    private void showToast(Boolean isConnected){
        if(!isConnected)
            Toast.makeText(getContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
    }

    private void getUserBalance() {

        networkInit();
        showBalanceView();
        String publicKey = DataInSharedPreferences.retrievingPublicKey(getContext());
        new FetchBalanceTask().execute(publicKey);
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

                JSONObject json = new JSONObject();
                json.put("public_key",pub_key);
                AndroidNetworking.post(NetworkUtils.getCheckBalanceUrl())
                        .addJSONObjectBody(json)
                        .setContentType("application/json")
                        .setTag("test")
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "network onResponse= " + response);
                                showBalanceView();
                                mBalanceTextView.setText(response);
                                result[0] = response;
                                if (isRefresh) swipeRefreshLayout.setRefreshing(false);
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
