package com.vjti.blockchain.wallet.Dashboard.AllTransactions;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vjti.blockchain.wallet.R;

import java.util.ArrayList;

import im.delight.android.identicons.Identicon;

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
        holder.amount.setText(String.valueOf(transaction.getAmount()));
        holder.timestamp.setText(String.valueOf(transaction.getUnixTimeStamp()));
        holder.identicon.show(String.valueOf(transaction.getReceiverKey()));
        holder.message.setText(String.valueOf(transaction.getMessage()));

        if(Integer.valueOf(transaction.getAmount()) < 0){
            //Red Colour
            holder.transactionCard.setCardBackgroundColor(Color.parseColor("#ffebee"));
            holder.amount.setTextColor(Color.parseColor("#f44336"));
        }else{
            //Green Colour
            holder.transactionCard.setCardBackgroundColor(Color.parseColor("#e8f5e9"));
            holder.amount.setTextColor(Color.parseColor("#4caf50"));
        }

        holder.identicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Public Key Copied", transaction.getReceiverKey());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(view.getContext(), "Public Key Copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionModelArrayList.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public CardView transactionCard;
        public TextView amount;
        public TextView timestamp;
        public Identicon identicon;
        public TextView message;

        public ViewHolder(View itemView) {
            super(itemView);
            this.transactionCard = (CardView) itemView.findViewById(R.id.transaction_card_view);
            this.amount = (TextView) itemView.findViewById(R.id.amount);
            this.timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            this.identicon = (Identicon) itemView.findViewById(R.id.identicon);
            this.message = (TextView) itemView.findViewById(R.id.message);
        }
    }
}
