package com.example.sid24rane.blockcanteen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.security.KeyPair;
import java.security.PublicKey;

public class QRGeneratorActivity extends AppCompatActivity {

    private final String PREFS_NAME = "KeyFile";

    // qr code generation
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;
    private ImageView qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        qrcode = (ImageView) findViewById(R.id.qrcode);
        generateQRCode();

    }

    private void generateQRCode() {

        Gson gson = new Gson();
        SharedPreferences preferences =  getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String jsonPair = preferences.getString("keyPair",null);
        KeyPair keyPair = gson.fromJson(jsonPair, KeyPair.class);
        PublicKey Key = keyPair.getPublic();
        String publicKey = new String(android.util.Base64.encode(Key.getEncoded(), Base64.DEFAULT));

        Log.d("PUBLIC KEY",publicKey);

        if (keyPair == null){
            Intent intent = new Intent(QRGeneratorActivity.this,KeyGenerationActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        }else{
            try {
                Bitmap bitmap = encodeAsBitmap(publicKey);
                qrcode.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
