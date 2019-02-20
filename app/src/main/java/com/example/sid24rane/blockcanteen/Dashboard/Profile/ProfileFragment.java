package com.example.sid24rane.blockcanteen.Dashboard.Profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import de.adorsys.android.securestoragelibrary.SecurePreferences;


public class ProfileFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private TextView name;
    private TextView emailAddress;
    private TextView publicKey;
    private Button downloadProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name =(TextView) view.findViewById(R.id.name);
        emailAddress =(TextView) view.findViewById(R.id.emailAddress);
        publicKey =(TextView) view.findViewById(R.id.publicKey);
        downloadProfile = (Button) view.findViewById(R.id.downloadProfile) ;

        loadUserDetailsFromSharedPreferences();

        downloadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "downloadProfile");
                //TODO : downloadProfile to device as file
            }
        });
        return view;
    }

    private void loadUserDetailsFromSharedPreferences(){
        Log.d(TAG,"loadUserDetailsFromSharedPreferences()" );
        name.setText(SecurePreferences.getStringValue("fullName", ""));
        emailAddress.setText(SecurePreferences.getStringValue("emailAddress", ""));
        publicKey.setText(SecurePreferences.getStringValue("publicKey", ""));
    }

}
