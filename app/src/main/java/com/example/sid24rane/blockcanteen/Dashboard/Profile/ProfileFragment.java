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


public class ProfileFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private TextView name;
    private TextView emailAddress;
    private TextView userType;
    private TextView userDepartment;
    private TextView yearOfAdmission;
    private TextView publicKey;
    private Button downloadProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name =(TextView) view.findViewById(R.id.name);
        emailAddress =(TextView) view.findViewById(R.id.emailAddress);
        userType =(TextView) view.findViewById(R.id.userType);
        userDepartment =(TextView) view.findViewById(R.id.userDepartment);
        yearOfAdmission =(TextView) view.findViewById(R.id.yearOfAdmission);
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
        try {
            JSONObject json = DataInSharedPreferences.getUserDetails(getContext());
            name.setText(json.getString("firstName") + " " + json.getString("lastName"));
            emailAddress.setText(json.getString("emailAddress"));
            userType.setText(json.getString("userType"));
            userDepartment.setText(json.getString("userDepartment"));
            yearOfAdmission.setText(json.getString("yearOfAdmission"));
            publicKey.setText(json.getString("publicKey"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
