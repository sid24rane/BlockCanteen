package com.vjti.blockchain.wallet.Dashboard.Home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.vjti.blockchain.wallet.ReceiveCoinsActivity;
import com.vjti.blockchain.wallet.R;
import com.vjti.blockchain.wallet.SendCoinsActivity;
import com.vjti.blockchain.wallet.data.DataInSharedPreferences;
import com.vjti.blockchain.wallet.utilities.NetworkUtils;

import org.json.JSONObject;


public class HomeFragment extends Fragment {

    private Button send;
    private Button receive;
    private TextView mBalanceTextView;
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
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        getUserBalance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((isNetworkAvailable())) {
                    Intent intent = new Intent(getContext(), SendCoinsActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                }else{
                    Toast.makeText(getContext(), "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),ReceiveCoinsActivity.class);
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


    private void getUserBalance() {
        showBalanceView();
        String publicKey = DataInSharedPreferences.retrievingPublicKey();
        new FetchBalanceTask().execute(publicKey);
    }

    private void showBalanceView() {
        Log.d(TAG, "showBalanceView() invoked");
        mBalanceTextView.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public class FetchBalanceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute() invoked");
            super.onPreExecute();
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
                                if (isRefresh) swipeRefreshLayout.setRefreshing(false);
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
            if (balance != null) {
                showBalanceView();
                mBalanceTextView.setText(balance);
            } else {
                showBalanceView();
            }
        }
    }
}
