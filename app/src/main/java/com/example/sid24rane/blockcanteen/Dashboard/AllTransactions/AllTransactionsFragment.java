package com.example.sid24rane.blockcanteen.Dashboard.AllTransactions;

import android.app.ProgressDialog;
import android.os.Bundle;
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
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.App;
import com.example.sid24rane.blockcanteen.Dashboard.Home.HomeFragment;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.ConnectivityReceiver;
import com.example.sid24rane.blockcanteen.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllTransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private TransactionsListAdapter transactionsListAdapter;
    private final String TAG = getClass().getSimpleName();
    private boolean isRefresh = false;
    private LinearLayout linearLayout;

    public AllTransactionsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_transations, container, false);
        setHasOptionsMenu(true);

        linearLayout = (LinearLayout) view.findViewById(R.id.empty_view);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.transactionlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        transactionModelArrayList = new ArrayList<>();
        transactionsListAdapter = new TransactionsListAdapter(transactionModelArrayList);
        recyclerView.setAdapter(transactionsListAdapter);

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

    private boolean checkConnection() {
        return ConnectivityReceiver.isConnected();
    }

    private void showToast(Boolean isConnected){
        if(!isConnected)
            Toast.makeText(getContext(), "Please connect to Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        App.getInstance().setConnectivityListener(AllTransactionsFragment.this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showToast(isConnected);
    }

    private void load() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading transactions please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String publicKey = DataInSharedPreferences.retrievingPublicKey(getContext());

        final JSONObject json = new JSONObject();
        try {
            json.put("public_key", publicKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // call to network
        AndroidNetworking.post(NetworkUtils.getTransactionHistoryUrl())
                .addJSONObjectBody(json)
                .setContentType("application/json")
                .setTag("TransactionHistory")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
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
                                    String timestamp = String.valueOf(jsonObject.get("timestamp"));
                                    TransactionModel transactionModel = new TransactionModel(amt, address, timestamp);
                                    transactionModelArrayList.add(transactionModel);
                                    transactionsListAdapter.notifyDataSetChanged();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "sndTxn onError= " + anError.toString());
                    }
                });

        progressDialog.dismiss();
        transactionsListAdapter.notifyDataSetChanged();

    }
}
