package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sid24rane.blockcanteen.Dashboard.DashboardActivity;

public class TransactionResultActivity extends AppCompatActivity {

    private Button backTodashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_result);

        backTodashboard = (Button) findViewById(R.id.backToDashboard);

        Intent i = getIntent();
        String res = i.getStringExtra("result");
        Toast.makeText(TransactionResultActivity.this, res, Toast.LENGTH_SHORT).show();

        backTodashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionResultActivity.this, DashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                finish();
            }
        });
    }
}
