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


public class KeyGenerationActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static KeyPair mKeyPair;

    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private Button register;
    private Spinner userType;
    private Spinner department;
    private EditText entry;
    private EditText secret;
    private ProgressDialog progressDialog;
    private Button restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_key_generation);

        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email_id);
        entry = (EditText) findViewById(R.id.entry);
        secret = (EditText) findViewById(R.id.secret);

        userType = (Spinner) findViewById(R.id.userType);
        department = (Spinner) findViewById(R.id.department);
        register = (Button) findViewById(R.id.submit);
        restore = (Button) findViewById(R.id.restore);

        loadSpinnerData();


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


                String fname = firstname.getText().toString();
                String lname = lastname.getText().toString();
                String email_address = email.getText().toString();
                String user_type = userType.getSelectedItem().toString();
                String user_department = department.getSelectedItem().toString();
                String year_of_admission = entry.getText().toString();
                String secretKey = secret.getText().toString();


                if(secretKey.length() == 16 && !secretKey.isEmpty() &&
                        !TextUtils.isEmpty(fname) &&
                        !TextUtils.isEmpty(lname) &&
                        !TextUtils.isEmpty(email_address) &&
                        !TextUtils.isEmpty(year_of_admission)){

                    try {
                        progressDialog = new ProgressDialog(KeyGenerationActivity.this);
                        progressDialog.setMessage("Creating profile please wait..");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        generateKeyPair();

                        JSONObject userJSON = new JSONObject();
                        userJSON.put("firstName", fname);
                        userJSON.put("lastName", lname);
                        userJSON.put("emailAddress", email_address);
                        userJSON.put("userType", user_type);
                        userJSON.put("userDepartment", user_department);
                        userJSON.put("yearOfAdmission", year_of_admission);
                        userJSON.put("privateKey", DataInSharedPreferences.retrievingPrivateKey(KeyGenerationActivity.this));
                        userJSON.put("publicKey", DataInSharedPreferences.retrievingPublicKey(KeyGenerationActivity.this));

                        //TODO : save data in both: SharedPref as well as JSONDump
                        saveRegistrationDetailsAsJson(userJSON, secretKey);
                        DataInSharedPreferences.storingUserDetails(userJSON, KeyGenerationActivity.this);

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

    private void loadSpinnerData() {

        // departments
        List<String> departmentNameData = new Data().departmentNameData();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, departmentNameData);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(dataAdapter);

        // usertype
        List<String> usertypeData = new Data().usertypeData();
        ArrayAdapter<String> usertypedataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, usertypeData);
        usertypedataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userType.setAdapter(dataAdapter);
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

    private void generateKeyPair() throws Exception {
        Log.d(TAG, "generateKeyPair invoked");

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(ecSpec, random);
        mKeyPair = keyGen.generateKeyPair();
        //String publicKey = new String(android.util.Base64.encode(Key.getEncoded(), Base64.DEFAULT));
        DataInSharedPreferences.storingKeyPair(mKeyPair, KeyGenerationActivity.this);

    }

}
