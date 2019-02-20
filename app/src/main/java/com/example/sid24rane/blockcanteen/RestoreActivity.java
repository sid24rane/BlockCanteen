package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

public class RestoreActivity extends AppCompatActivity {

    private Button uploadFile;
    private EditText secret;
    private Button restoreProfile;
    private final String TAG = getClass().getSimpleName();
    private String path;
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
                openFile("*/*");
                //TODO : take input file from user
            }
        });

        restoreProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
