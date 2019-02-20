package com.example.sid24rane.blockcanteen.Dashboard.Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.QRGeneratorActivity;
import com.example.sid24rane.blockcanteen.QRScannerActivity;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.JSONDump;
import com.example.sid24rane.blockcanteen.utilities.NetworkUtils;

import org.json.JSONObject;

import okhttp3.OkHttpClient;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class HomeFragment extends Fragment {

    private Button send;
    private Button receive;
    private TextView mBalanceTextView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private final String TAG = getClass().getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefresh = false;

    private Button download;

    private static final int REQUEST_STORAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        send = (Button) view.findViewById(R.id.send);
        receive = (Button) view.findViewById(R.id.receive);
        mBalanceTextView = (TextView)  view.findViewById(R.id.balance);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) view.findViewById(R.id.error_message);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);

        download = (Button) view.findViewById(R.id.download);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
                {
                    if(!checkPermission())
                    {
                        requestPermission();
                    }
                }
                String data = JSONDump.getData(getContext());
                JSONDump.downloadProfile(data, getContext());
            }
        });

        getUserBalance();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = checkConnection();
                if(isConnected){
                    Intent intent = new Intent(getContext(),QRScannerActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                }
                else{
                    showToast(isConnected);
                }
            }
        });

        receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),QRGeneratorActivity.class);
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

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getContext(), "Permission Granted, Now you can scan QR Code using Camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getContext(), "Permission Denied, You cannot access Camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},
                                                            REQUEST_STORAGE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean checkConnection() {
        //TODO: lib
        return true;
    }

    private void showToast(Boolean isConnected){
        if(!isConnected)
            Toast.makeText(getContext(), "Please connect to Internet", Toast.LENGTH_LONG).show();
    }

    private void getUserBalance() {

        networkInit();
        showBalanceView();
        String publicKey = DataInSharedPreferences.retrievingPublicKey();
        new FetchBalanceTask().execute(publicKey);
    }

    private void networkInit(){
        OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                .build();
        AndroidNetworking.initialize(getContext(),okHttpClient);
    }

    private void showBalanceView() {
        Log.d(TAG, "showBalanceView() invoked");
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mBalanceTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        Log.d(TAG, "showErrorMessage() invoked");
        mBalanceTextView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchBalanceTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute() invoked");
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (balance != null) {
                showBalanceView();
                mBalanceTextView.setText(balance);
            } else {
                showBalanceView();
            }
        }

    }

}
