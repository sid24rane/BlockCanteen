package com.example.sid24rane.blockcanteen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.utilities.AES;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.adorsys.android.securestoragelibrary.SecurePreferences;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RestoreWalletActivity extends AppCompatActivity {

    private Button uploadFile;
    private EditText secret;
    private Button restoreProfile;
    private TextView pathToFile;
    private String TAG = getClass().getSimpleName();
    private String path;
    private ProgressDialog progressDialog;
    private final int REQUEST_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_restore);

        uploadFile = (Button)  findViewById(R.id.uploadFile);
        secret = (EditText) findViewById(R.id.secret);
        restoreProfile = (Button) findViewById(R.id.restoreProfile);
        pathToFile = (TextView) findViewById(R.id.pathToFile);

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP)
                {
                    if(!checkPermission())
                    {
                        requestPermission();
                    }else{
                        openFile("text/*");
                    }
                }

            }
        });

        restoreProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.LOLLIPOP)
                {
                    if(!checkPermission())
                    {
                        requestPermission();
                        restoreUserProfile();

                    }else{
                        restoreUserProfile();
                    }
                }

            }
        });

    }
    private void restoreUserProfile(){

        progressDialog = new ProgressDialog(RestoreWalletActivity.this);
        progressDialog.setMessage("Restoring profile please wait..");
        progressDialog.setCancelable(false);
        progressDialog.getCurrentFocus();
        progressDialog.show();

        Thread mThread = new Thread() {
            @Override
            public void run() {
                try {

                    String secretKey = secret.getText().toString();

                    if(!TextUtils.isEmpty(secretKey) && secretKey.length() >= 12) {

                        if(!TextUtils.isEmpty(path)){

                            String userProfile = getDataFromPath(path, secretKey);
                            if (!TextUtils.isEmpty(userProfile)){

                                try {

                                    JSONObject user = new JSONObject(userProfile);

                                    SecurePreferences.setValue("fullName", user.getString("name"));
                                    SecurePreferences.setValue("emailAddress",user.getString("email"));
                                    SecurePreferences.setValue("publicKey", user.getString("publicKey"));
                                    SecurePreferences.setValue("privateKey", user.getString("privateKey"));
                                    SecurePreferences.setValue("pin",user.getString("pin"));

                                    showToast("Welcome back, Profile restored successfully!");

                                    progressDialog.dismiss();

                                    Intent intent = new Intent(RestoreWalletActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                                    finish();

                                } catch (JSONException e) {
                                    progressDialog.dismiss();
                                    showToast("Invalid Secret key!");
                                    e.printStackTrace();
                                }

                            }else{
                                progressDialog.dismiss();
                                showToast("Invalid Secret key!");
                            }

                        }else{
                            progressDialog.dismiss();
                            showToast("Invalid Path!");
                        }

                    }else{
                        progressDialog.dismiss();
                        showToast("Secret key should be 12 characters or more!");
                    }

                    progressDialog.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        mThread.start();

    }

    private void showToast(final String toast)
    {
        runOnUiThread(() -> Toast.makeText(RestoreWalletActivity.this, toast, Toast.LENGTH_SHORT).show());
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(RestoreWalletActivity.this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(RestoreWalletActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(RestoreWalletActivity.this, "Permission Granted!", Toast.LENGTH_LONG).show();
                        restoreUserProfile();
                    }else {
                        Toast.makeText(RestoreWalletActivity.this, "Permission Denied! We need storage permission to restore your wallet", Toast.LENGTH_LONG).show();
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
        new android.support.v7.app.AlertDialog.Builder(RestoreWalletActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 7:
                if(resultCode==RESULT_OK){
                    String tempPath = data.getData().getPath();
                    Log.d(TAG, "FilePath "+ tempPath);
                    if(tempPath.contains(":")){
                        String idArr[] = tempPath.split(":");
                        if(idArr.length == 2)
                        {
                            String type = idArr[0];
                            String realDocId = idArr[1];
                            path = Environment.getExternalStorageDirectory() + "/" + realDocId;
                            Toast.makeText(RestoreWalletActivity.this, "Path: "+ path, Toast.LENGTH_SHORT).show();
                        }
                    } else if(tempPath.contains("//")){
                        // for xiaomi devices
                        String arr[] = tempPath.split("//");
                        if(arr.length == 2)
                        {
                            String realDocId = arr[1];
                            path = "/" + realDocId;
                            Toast.makeText(RestoreWalletActivity.this, "Path: "+ path, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        // for Samsung Devices
                        path = tempPath;
                        Toast.makeText(RestoreWalletActivity.this, "Path: "+ path, Toast.LENGTH_SHORT).show();
                    }

                    //Setting the textView with the path
                    pathToFile.setText(path);
                }
                break;
        }
    }

    private void openFile(String mimeType) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.putExtra("CONTENT_TYPE", mimeType);
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (getPackageManager().resolveActivity(sIntent, 0) != null){
            // it is device with Samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "Open file");
        }
        try {
            startActivityForResult(chooserIntent, 7);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getDataFromPath(String path, String secret) {
        Log.d(TAG, "getDataFromPath() invoked " + path);
        try {
            File f = new File(path);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String data = new String(buffer);
            Log.d("PRE-DECRYPT",data);
            String decrypted = AES.decrypt(data,secret);
            //Log.d("POST-DECRYPT",decrypted);
            return decrypted;
        } catch (IOException e) {
            Log.e(TAG, "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }
}
