package com.vjti.blockchain.wallet;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vjti.blockchain.wallet.data.DataInSharedPreferences;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import de.adorsys.android.securestoragelibrary.SecurePreferences;


public class QRGeneratorActivity extends AppCompatActivity{
    // qr code generation
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;
    private ImageView qrcode;
    private TextView publicKey;
    private TextView name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_qrgenerator);


        qrcode = (ImageView) findViewById(R.id.qrcode);
        publicKey = (TextView) findViewById(R.id.publicKey);
        name = (TextView) findViewById(R.id.name);

        try {
            generateQRCode();
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void generateQRCode() throws WriterException {
        String pubKey = DataInSharedPreferences.retrievingPublicKey();
        String userName = SecurePreferences.getStringValue("fullName", "");
        String qrString = userName + ":" + pubKey;
        Bitmap bitmap = encodeAsBitmap(qrString);
        qrcode.setImageBitmap(bitmap);
        publicKey.setText(pubKey);
        name.setText(userName);
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
