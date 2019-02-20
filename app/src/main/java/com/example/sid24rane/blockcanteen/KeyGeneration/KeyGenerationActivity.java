package com.example.sid24rane.blockcanteen.KeyGeneration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.RestoreActivity;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.example.sid24rane.blockcanteen.utilities.EncryptUtils;
import com.example.sid24rane.blockcanteen.utilities.JSONDump;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.adorsys.android.securestoragelibrary.SecurePreferences;


public class KeyGenerationActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private KeyPair mKeyPair;
    private EditText fullName;
    private EditText email;
    private Button register;
    private Button restore;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_key_generation);

        fullName = (EditText) findViewById(R.id.fullName);
        email = (EditText) findViewById(R.id.email_id);

        register = (Button) findViewById(R.id.submit);
        restore = (Button) findViewById(R.id.restore);

        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KeyGenerationActivity.this,RestoreActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname = fullName.getText().toString();
                String email_address = email.getText().toString();

                if(!TextUtils.isEmpty(fname) &&
                        !TextUtils.isEmpty(email_address)){

                    try {
                        progressDialog = new ProgressDialog(KeyGenerationActivity.this);
                        progressDialog.setMessage("Creating profile please wait..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        JSONObject userJSON = new JSONObject();
                        userJSON.put("fullName", fname);
                        userJSON.put("emailAddress", email_address);
                        generateKeyPairAndStoreData(userJSON);

                        Intent intent = new Intent(KeyGenerationActivity.this,DashboardActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);

                    } catch (Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(KeyGenerationActivity.this, "Please fill in all the fields!", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void saveRegistrationDetailsAsJson(JSONObject userJSON, String secretKey){
        Log.d(TAG ,"saveRegistrationDetails() invoked");
          String userJSONString = userJSON.toString();
        try {
            SecretKey secret = new EncryptUtils().generateKey(secretKey);
            String encryptedJSON= new String(new EncryptUtils().encryptMsg(userJSONString, secret));
            Log.d("Encrypted : " , encryptedJSON);

            //dump JSON
            JSONDump.saveData(KeyGenerationActivity.this, encryptedJSON);
            progressDialog.dismiss();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

    }

    private void generateKeyPairAndStoreData(JSONObject userJSON) throws Exception {
        Log.d(TAG, "generateKeyPair invoked");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(ecSpec, random);
        mKeyPair = keyGen.generateKeyPair();
        new DataInSharedPreferences().storingData(mKeyPair, userJSON);

    }

}
