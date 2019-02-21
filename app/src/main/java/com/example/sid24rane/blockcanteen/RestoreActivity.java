package com.example.sid24rane.blockcanteen;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.utilities.EncryptUtils;
import com.example.sid24rane.blockcanteen.utilities.JSONDump;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class RestoreActivity extends AppCompatActivity {

    private Button uploadFile;
    private EditText secret;
    private Button restoreProfile;
    private final String TAG = getClass().getSimpleName();
    private String path;
    private static final int REQUEST_STORAGE = 1;

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

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile("application/json");
            }
        });

        restoreProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M)
                {
                    if(!checkPermission())
                    {
                        requestPermission();
                    }
                }

                EncryptUtils e = new EncryptUtils();

                // Decrypt json with the secret
                String dataFromDump = JSONDump.getDataFromPath(path);
                Log.d(TAG,"Decrypted :" + dataFromDump );

                String secretKeyFromUser= secret.getText().toString();
                
//                JSONObject json = new JSONObject(decrypted);
//                Log.d(TAG, json.toString());

                //TODO Storing the data in sharedPreferences
                //new DataInSharedPreferences().storingUserDetails(json, RestoreActivity.this);

//                Intent intent = new Intent(RestoreActivity.this, DashboardActivity.class);
//                startActivity(intent);
//                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

            }
        });

    }
    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(RestoreActivity.this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(RestoreActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(RestoreActivity.this, "Permission Granted, Now you can scan QR Code using Camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(RestoreActivity.this, "Permission Denied, You cannot access Camera", Toast.LENGTH_LONG).show();
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
        new android.support.v7.app.AlertDialog.Builder(RestoreActivity.this)
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
                    String idArr[] = tempPath.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];
                        path = Environment.getExternalStorageDirectory() + "/" + realDocId;
                    }
                    Toast.makeText(RestoreActivity.this, path , Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    private void openFile(String mimeType) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        // if you want any file type, you can skip next line
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
}
