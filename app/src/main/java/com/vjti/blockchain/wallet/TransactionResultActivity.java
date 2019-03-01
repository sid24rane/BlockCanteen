package com.vjti.blockchain.wallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vjti.blockchain.wallet.Dashboard.DashboardActivity;

public class TransactionResultActivity extends AppCompatActivity {

    private Button backTodashboard;
    private TextView result;
    private ImageView result_image;
    private TextView amount;
    private TextView sub_result;
    private TextView receiverName;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_transaction_result);
        init();
    }

    private void init() {

        backTodashboard = (Button) findViewById(R.id.backToDashboard);
        result = (TextView) findViewById(R.id.result);
        result_image = (ImageView) findViewById(R.id.res_image);
        amount = (TextView) findViewById(R.id.amount);
        sub_result = (TextView) findViewById(R.id.sub_result);
        message = (TextView) findViewById(R.id.message);
        receiverName = (TextView) findViewById(R.id.receiver);

        Intent i = getIntent();
        String res = i.getStringExtra("result");
        String amt = i.getStringExtra("amount");
        String qrString = i.getStringExtra("message");

        String messageString = null, receiverNameString = null;
        if(qrString.contains(":")){
            receiverNameString = qrString.split(":")[0];
            messageString = qrString.split(":")[1];
        }

        if (res.equals("true")){
            result.setText("The transaction was successful !");
            result_image.setImageResource(R.drawable.ic_check_circle_green_500_24dp);
            sub_result.setText("You have successfully sent");
            amount.setText(amt + " VJ-Coins");
            receiverName.setText( "Receiver: " + receiverNameString);
            message.setText("Message: "  + messageString);

        }else{
            result.setText("Oops! The transaction has failed! Please try again later.");
            result_image.setImageResource(R.drawable.ic_highlight_off_red_500_24dp);
            sub_result.setText("You have failed to send");
            amount.setText(amt + " VJ-Coins");
        }

        backTodashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransactionResultActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
                finish();
            }
        });

    }
}
