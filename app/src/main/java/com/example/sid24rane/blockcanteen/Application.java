package com.example.sid24rane.blockcanteen;

import com.example.sid24rane.blockcanteen.utilities.ConnectivityReceiver;

public class Application extends android.app.Application {

    private static Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized Application getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}