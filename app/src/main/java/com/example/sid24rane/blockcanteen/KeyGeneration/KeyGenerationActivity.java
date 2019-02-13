package com.example.sid24rane.blockcanteen.KeyGeneration;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sid24rane.blockcanteen.R;
import com.example.sid24rane.blockcanteen.data.KeyInSharedPreferences;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KeyGenerationActivity extends AppCompatActivity {

    //TODO 1 : Generate Unique ID for each user ==> firebase
    //TODO 2 : Change encoding method
    //TODO 4 : Handle Network requests

    private final String TAG = getClass().getSimpleName();
    private static String publicKey;
    private static String privateKey;
    private static KeyPair mKeyPair;

    private EditText firstname;
    private EditText lastname;
    private EditText email;
    private EditText id;
    private Button register;
    private Spinner userType;
    private Spinner department;
    private EditText entry;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_key_generation);

        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email_id);
        register = (Button) findViewById(R.id.submit);
        userType = (Spinner) findViewById(R.id.userType);
        department = (Spinner) findViewById(R.id.department);
        entry = (EditText) findViewById(R.id.entry);

        loadSpinnerData();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String fname = firstname.getText().toString();
                String lname = lastname.getText().toString();
                String email_address = email.getText().toString();
                //String enrollment_id = id.getText().toString();
                String year_of_admission = entry.getText().toString();

                // Submit to firebase!

                try {
                    generateKeyPair();
                    KeyInSharedPreferences.retrievingPublicKey(KeyGenerationActivity.this);
                    KeyInSharedPreferences.retrievingPrivateKey(KeyGenerationActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
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

    public void sendRegistrationDetails(UserModel user){
        // Create a new user with a first and last name
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> object = new HashMap<>();
        object.put("firstName", user.getFirstName());
        object.put("lastName", user.getLastName());
        object.put("email", user.getEmail());
        object.put("id", user.getId());
        object.put("publicKey", user.getPublicKey());

        db.collection("users")
                .add(object)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void generateKeyPair() throws Exception {
        Log.d(TAG, "generateKeyPair invoked");
        // Generate a Key Pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256r1");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(ecSpec, random);
        mKeyPair = keyGen.generateKeyPair();

        //String publicKey = new String(android.util.Base64.encode(Key.getEncoded(), Base64.DEFAULT));

        KeyInSharedPreferences.storingKeyPair(mKeyPair, KeyGenerationActivity.this);
    }

    private static String getKey(String filename) throws IOException {
        // Read key from file
        String strKeyPEM = "";
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null) {
            strKeyPEM += line + "\n";
        }
        br.close();
        return strKeyPEM;
    }


}
