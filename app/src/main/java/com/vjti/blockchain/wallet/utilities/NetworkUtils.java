package com.vjti.blockchain.wallet.utilities;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static String extension = "http://";
    private static String ip_address = "chain.vjti-bct.in";
    private static String port = "9000";

    private static final String CHECK_BALANCE_URL =
            getAddress() + "/checkBalance";

    private static final String MAKE_TRANSACTION_URL =
            getAddress() + "/makeTransaction";

    private static final String SEND_TRANSACTION_URL =
            getAddress() + "/sendTransaction";

    private static final String TRANSACTION_HISTORY =
            getAddress() + "/transactionHistory";

    public static String getCheckBalanceUrl() {
        return CHECK_BALANCE_URL;
    }

    public static String getMakeTransactionUrl() {
        return MAKE_TRANSACTION_URL;
    }

    public static String getSendTransactionUrl() {
        return SEND_TRANSACTION_URL;
    }

    public static String getTransactionHistoryUrl() {
        return TRANSACTION_HISTORY;
    }

    private static String getAddress(){
        return extension + ip_address + ":" + port;
    }


}
