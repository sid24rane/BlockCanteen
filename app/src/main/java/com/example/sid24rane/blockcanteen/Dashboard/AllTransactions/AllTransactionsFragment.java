package com.example.sid24rane.blockcanteen.Dashboard.AllTransactions;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.ConnectionLiveData;
import com.example.sid24rane.blockcanteen.utilities.ConnectionModel;
import com.example.sid24rane.blockcanteen.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AllTransactionsFragment extends Fragment{

    private RecyclerView recyclerView;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private TransactionsListAdapter transactionsListAdapter;
    private final String TAG = getClass().getSimpleName();
    private boolean isRefresh = false;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog ;


    public AllTransactionsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_transations, container, false);
        setHasOptionsMenu(true);

        linearLayout = (LinearLayout) view.findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        progressDialog = new ProgressDialog(getContext());
        recyclerView = (RecyclerView) view.findViewById(R.id.transactionlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        transactionModelArrayList = new ArrayList<>();
        transactionsListAdapter = new TransactionsListAdapter(transactionModelArrayList);
        recyclerView.setAdapter(transactionsListAdapter);

        checkConnection();
        load();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                load();
                swipeRefreshLayout.setRefreshing(false);
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

    private void checkConnection() {
        if (!isNetworkAvailable()){
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void load() {
        progressDialog.setMessage("Loading transactions please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String publicKey = DataInSharedPreferences.retrievingPublicKey();
        new FetchAllTransactionsTask().execute(publicKey);

        progressDialog.dismiss();
        transactionsListAdapter.notifyDataSetChanged();

    }

    private void showTransactionsInLayout(String response){
        Log.d(TAG, "sndTxn onResponse= " + response.toString());
        if (isRefresh) transactionModelArrayList.clear();
        try {
            JSONArray jsonArray = new JSONArray(response);
            if (jsonArray.length() == 0){
                progressDialog.dismiss();
                linearLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }else{
                linearLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                for (int i=0;i<jsonArray.length();i++){
                    String str = jsonArray.getString(i);
                    JSONObject jsonObject = new JSONObject(str);
                    String amt = jsonObject.getString("amount");
                    String address = jsonObject.getString("address");
                    String unixSeconds = jsonObject.getString("timestamp");

                    Date date = new java.util.Date(Long.valueOf(unixSeconds)*1000L);
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+5:30"));
                    String formattedDate = sdf.format(date);

                    TransactionModel transactionModel = new TransactionModel(amt, address, formattedDate);
                    transactionModelArrayList.add(transactionModel);
                    transactionsListAdapter.notifyDataSetChanged();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class FetchAllTransactionsTask extends AsyncTask<String, Void, String> {
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
                Log.d(TAG, "makingNetworkCallToFetchAllTransactions");

                final String[] result = new String[1];
                String pub_key = params[0];

                JSONObject json = new JSONObject();
                json.put("public_key",pub_key);

                AndroidNetworking.post(NetworkUtils.getTransactionHistoryUrl())
                        .addJSONObjectBody(json)
                        .setContentType("application/json")
                        .setTag("getTransactionHistory")
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.d(TAG, "network onResponse= " + response);
                                showTransactionsInLayout(response);
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
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute() invoked");
        }

    }

}
