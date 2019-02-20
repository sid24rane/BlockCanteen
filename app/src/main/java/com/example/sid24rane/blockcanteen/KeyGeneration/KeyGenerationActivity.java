package com.example.sid24rane.blockcanteen.KeyGeneration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;
import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.RegisterPINActivity;
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
import java.util.regex.Pattern;

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

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
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
                String email_address = email.getText().toString().trim();

                if(!TextUtils.isEmpty(fname) &&
                        !TextUtils.isEmpty(email_address)){

                    if(isValidEmailId(email_address)){
                        try {

                            progressDialog = new ProgressDialog(KeyGenerationActivity.this);
                            progressDialog.setMessage("Creating profile please wait..");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            JSONObject userJSON = new JSONObject();
                            userJSON.put("fullName", fname);
                            userJSON.put("emailAddress", email_address);


                            generateKeyPairAndStoreData(userJSON);
                            progressDialog.dismiss();
                            Intent intent = new Intent(KeyGenerationActivity.this,RegisterPINActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                            finish();

                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Invalid Email Address.", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(KeyGenerationActivity.this, "Please fill in all the fields!", Toast.LENGTH_LONG).show();
                }

            }
        });


    }


    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private void generateKeyPairAndStoreData(JSONObject userJSON) throws Exception {
        Log.d(TAG, "generateKeyPair invoked");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(ecSpec, random);
        mKeyPair = keyGen.generateKeyPair();
        DataInSharedPreferences.storingData(mKeyPair, userJSON);
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
}
