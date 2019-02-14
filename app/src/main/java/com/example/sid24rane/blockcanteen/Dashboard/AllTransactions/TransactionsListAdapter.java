package com.example.sid24rane.blockcanteen.Dashboard.AllTransactions;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sid24rane.blockcanteen.R;

import java.util.ArrayList;

public class TransactionsListAdapter extends RecyclerView.Adapter<TransactionsListAdapter.ViewHolder> {

    private ArrayList<TransactionModel> transactionModelArrayList;

    public TransactionsListAdapter(ArrayList<TransactionModel> transactionModelArrayList) {
        this.transactionModelArrayList = transactionModelArrayList;
    }

    @NonNull
    @Override
    public TransactionsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionsListAdapter.ViewHolder(mview);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsListAdapter.ViewHolder holder, int position) {
        TransactionModel transaction = transactionModelArrayList.get(position);
        holder.address.setText(String.valueOf(transaction.getReceiverKey()));
        holder.amount.setText(String.valueOf(transaction.getAmount()));
        holder.timestamp.setText(String.valueOf(transaction.getUnixTimeStamp()));

    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView address;
        public TextView amount;
        public TextView timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            this.address = (TextView) itemView.findViewById(R.id.address);
            this.amount = (TextView) itemView.findViewById(R.id.amount);
            this.timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }
}
