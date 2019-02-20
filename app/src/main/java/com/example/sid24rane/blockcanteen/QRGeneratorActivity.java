package com.example.sid24rane.blockcanteen;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QRGeneratorActivity extends AppCompatActivity{
    // qr code generation
    public final static int WHITE = 0xFFFFFFFF;
    public final static int BLACK = 0xFF000000;
    public final static int WIDTH = 400;
    public final static int HEIGHT = 400;
    private ImageView qrcode;
    private TextView publicKey;
    private TextView infotext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenerator);

        qrcode = (ImageView) findViewById(R.id.qrcode);
        publicKey = (TextView) findViewById(R.id.publicKey);
        infotext = (TextView) findViewById(R.id.infoText);

        try {
            generateQRCode();
            infotext.setText("InfoText");
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void generateQRCode() throws WriterException {
        String pubKey = DataInSharedPreferences.retrievingPublicKey();
        Log.d("PUBLIC KEY", pubKey);
        Bitmap bitmap = encodeAsBitmap(pubKey);
        qrcode.setImageBitmap(bitmap);
        publicKey.setText(pubKey);

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
