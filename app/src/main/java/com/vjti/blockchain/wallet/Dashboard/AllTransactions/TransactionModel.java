package com.vjti.blockchain.wallet.Dashboard.AllTransactions;

public class TransactionModel {
    private String amount;
    private String receiver_key;
    private String unixTimeStamp;
    private String message;

    public String getAmount() {
        return amount;
    }

    public String getReceiverKey() {
        return receiver_key;
    }

    public String getUnixTimeStamp() {
        return unixTimeStamp;
    }

    public String getMessage() {
        return message;
    }

    public TransactionModel(String amount, String receiver_key, String unixTimeStamp, String message) {
        this.amount = amount;
        this.receiver_key = receiver_key;
        this.unixTimeStamp = unixTimeStamp;
        this.message = message;
    }
}
