package com.example.sid24rane.blockcanteen.Dashboard.AllTransactions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.utilities.NetworkUtils;

import java.util.ArrayList;


public class AllTransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<TransactionModel> transactionModelArrayList;
    private TransactionsListAdapter transactionsListAdapter;
    private final String TAG = getClass().getSimpleName();

    public AllTransactionsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_transations, container, false);
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.transactionlist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        TransactionsListAdapter transactionsListAdapter = new TransactionsListAdapter(transactionModelArrayList);
        recyclerView.setAdapter(transactionsListAdapter);

        load();

        return view;

    }

    private void load() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading transactions please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // TODO : retrieve publicKey from SharedPref
        String publicKey = "";

        // call to network
        AndroidNetworking.post(NetworkUtils.getTransactionHistoryUrl())
                .addUrlEncodeFormBodyParameter("public_key", publicKey)
                .setContentType("application/x-www-form-urlencoded")
                .setTag("TransactionHistory")
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

        progressDialog.dismiss();
        transactionsListAdapter.notifyDataSetChanged();

    }
}
