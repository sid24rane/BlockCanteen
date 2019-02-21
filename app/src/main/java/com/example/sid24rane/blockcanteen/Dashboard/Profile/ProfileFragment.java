package com.example.sid24rane.blockcanteen.Dashboard.Profile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.AES;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ProfileFragment extends Fragment {

    private TextView name;
    private TextView emailAddress;
    private TextView publicKey;
    private Button downloadProfile;
    private EditText userSecret;
    private ImageView userAvatar;
    private final String TAG = getClass().getSimpleName();
    private static final int REQUEST_STORAGE = 1;
    private ImageView copyKey;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name =(TextView) view.findViewById(R.id.name);
        emailAddress =(TextView) view.findViewById(R.id.emailAddress);
        publicKey =(TextView) view.findViewById(R.id.publicKey);
        publicKey.setSelected(true);

        downloadProfile = (Button) view.findViewById(R.id.downloadProfile) ;
        userSecret = (EditText) view.findViewById(R.id.userSecret);
        userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
        copyKey = (ImageView) view.findViewById(R.id.copy);

        setUserAvatar();
        loadUserDetailsFromSharedPreferences();

        downloadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadProfile();
            }
        });

        copyKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    setClipboard(getContext(),publicKey.getText().toString());
            }
        });

        return view;
    }

    private void setClipboard(Context context, String text) {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Public Key Copied", text);
            clipboard.setPrimaryClip(clip);
    }

    private void setUserAvatar() {
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(SecurePreferences.getStringSetValue("emailAddress",null));
        String user_name = SecurePreferences.getStringValue("fullName",null);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(user_name.substring(0, 1).toUpperCase(), color);
        userAvatar.setImageDrawable(drawable);
    }

    private void downloadProfile(){
        Log.d(TAG, "downloadProfile");
        String user_secret = userSecret.getText().toString();
        if (!TextUtils.isEmpty(user_secret)){
            if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
            {
                if(!checkPermission())
                {
                    requestPermission();
                }else{
                    storeUserProfile(user_secret);
                }
            }
        }else{
            Toast.makeText(getContext(), "Please enter Secret phrase!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserDetailsFromSharedPreferences(){
        Log.d(TAG,"loadUserDetailsFromSharedPreferences()" );
        name.setText(SecurePreferences.getStringValue("fullName", ""));
        emailAddress.setText(SecurePreferences.getStringValue("emailAddress", ""));
        publicKey.setText(SecurePreferences.getStringValue("publicKey", ""));
    }


    private void storeUserProfile(String secretKey){


        JSONObject user = new JSONObject();

        try {

            user.put("name",SecurePreferences.getStringValue("fullName",""));
            user.put("email",SecurePreferences.getStringValue("emailAddress",""));
            user.put("publicKey",SecurePreferences.getStringValue("publicKey",""));
            user.put("privateKey",SecurePreferences.getStringValue("privateKey",""));
            user.put("pin",SecurePreferences.getStringValue("pin",""));

            downloadProfile(user.toString(),secretKey);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        Toast.makeText(getContext(), "Permission Granted, Storing your wallet!", Toast.LENGTH_LONG).show();
                        storeUserProfile(userSecret.getText().toString());
                    }else {
                        Toast.makeText(getContext(), "Permission Denied, We need storage permission to store your wallet securely!", Toast.LENGTH_LONG).show();
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

    public void downloadProfile(String data,String secret) {

        Log.d("PRE-ENCRYPT",data);
        String encrypted = AES.encrypt(data,secret) ;
        Log.d("POST-ENCRYPT",encrypted);

        if(checkExternalMedia()){
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/VJTI-Wallet");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            String filename = "credentials.json";
            File file = new File (myDir, filename);
            if (file.exists ())
                file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                out.write(encrypted.getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(getContext(), new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("ExternalStorage", "Scanned " + path + ":");
                            Log.d("ExternalStorage", "-> uri=" + uri);
                        }
                    });

        }else{
            Toast.makeText(getContext(), "External Storage Not available!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkExternalMedia() {

        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return mExternalStorageWriteable;

    }

}
