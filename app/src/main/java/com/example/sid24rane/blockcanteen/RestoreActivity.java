package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.EncryptUtils;
import com.example.sid24rane.blockcanteen.utilities.JSONDump;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class RestoreActivity extends AppCompatActivity {

    private Button uploadFile;
    private EditText secret;
    private Button restoreProfile;
    private final String TAG = getClass().getSimpleName();

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
                //TODO : take input file from user
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, 7);

            }
        });

        restoreProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EncryptUtils e = new EncryptUtils();

                // Decrypt json with the secret
                String dataFromDump = JSONDump.getData(RestoreActivity.this);
                String secretKeyFromUser= secret.getText().toString();
                SecretKey secretKey = null;
                try {
                    secretKey = e.generateKey(secretKeyFromUser);
                    String decrypted = new String(e.decryptMsg(dataFromDump.getBytes(), secretKey));
                    Log.d(TAG,"Decrypted :" + decrypted );

                    //TODO : check Decrypted for JSON string regex

                    JSONObject json = new JSONObject(decrypted);
                    Log.d(TAG, json.toString());

                    //TODO Storing the data in sharedPreferences
                    //new DataInSharedPreferences().storingUserDetails(json, RestoreActivity.this);

                    Intent intent = new Intent(RestoreActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeySpecException e1) {
                    e1.printStackTrace();
                } catch (InvalidKeyException e1) {
                    e1.printStackTrace();
                } catch (NoSuchPaddingException e1) {
                    e1.printStackTrace();
                } catch (BadPaddingException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (InvalidParameterSpecException e1) {
                    e1.printStackTrace();
                } catch (IllegalBlockSizeException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (InvalidAlgorithmParameterException e1) {
                    e1.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){

            case 7:

                if(resultCode==RESULT_OK){

                    String PathHolder = data.getData().getPath();

                    Toast.makeText(RestoreActivity.this, PathHolder , Toast.LENGTH_LONG).show();

                }
                break;

        }
    }
}
