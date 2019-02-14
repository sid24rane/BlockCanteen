package com.example.sid24rane.blockcanteen.Dashboard.AllTransactions;

public class TransactionModel {
    private String amount;
    private String receiver_key;
    private String unixTimeStamp;

    public String getAmount() {
        return amount;
    }

    public String getReceiverKey() {
        return receiver_key;
    }

    public String getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public TransactionModel(String amount, String receiver_key, String unixTimeStamp) {
        this.amount = amount;
        this.receiver_key = receiver_key;
        this.unixTimeStamp = unixTimeStamp;
    }
}
