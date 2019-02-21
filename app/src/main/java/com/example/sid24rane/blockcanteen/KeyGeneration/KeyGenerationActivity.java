package com.example.sid24rane.blockcanteen.KeyGeneration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.RegisterPINActivity;
import com.example.sid24rane.blockcanteen.RestoreActivity;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.regex.Pattern;


public class KeyGenerationActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private KeyPair mKeyPair;
    private EditText fullName;
    private EditText email;
    private Button register;
    private Button restore;
    private ProgressDialog progressDialog;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_key_generation);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

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

                            generateKeyPairAndStoreData(fname,email_address);

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
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Invalid Email Address", Snackbar.LENGTH_LONG);
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setTextColor(Color.RED);
                        snackbar.show();
                    }

                }else{
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please fill in all the fields!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    snackbar.show();
                }

            }
        });


    }


    private void generateKeyPairAndStoreData(String name,String email) throws Exception {
        Log.d(TAG, "generateKeyPair invoked");
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(ecSpec, random);
        mKeyPair = keyGen.generateKeyPair();
        DataInSharedPreferences.storingData(mKeyPair, name, email);
    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

}
