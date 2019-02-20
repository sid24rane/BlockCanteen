package com.example.sid24rane.blockcanteen.utilities;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.data.DataInSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Arrays;

public class TransactionUtils {
    private String TAG = "TransactionUtils";

    private BigInteger extractR(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startR + 2, startR + 2 + lengthR));
    }

    private BigInteger extractS(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        int startS = startR + 2 + lengthR;
        int lengthS = signature[startS + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startS + 2, startS + 2 + lengthS));
    }

    private byte[] derSign(BigInteger r, BigInteger s) throws Exception {
        byte[] rb = r.toByteArray();
        byte[] sb = s.toByteArray();
        int off = (2 + 2) + rb.length;
        int tot = off + (2 - 2) + sb.length;
        byte[] der = new byte[tot + 2];
        der[0] = 0x30;
        der[1] = (byte) (tot & 0xff);
        der[2 + 0] = 0x02;
        der[2 + 1] = (byte) (rb.length & 0xff);
        System.arraycopy(rb, 0, der, 2 + 2, rb.length);
        der[off + 0] = 0x02;
        der[off + 1] = (byte) (sb.length & 0xff);
        System.arraycopy(sb, 0, der, off + 2, sb.length);
        return der;
    }

    public String getSignatureString(byte[] sign) throws Exception {
        BigInteger r = extractR(sign);
        BigInteger s = extractS(sign);
        String realSign = "[" + r.toString() + ", " + s.toString() + "]";
        return realSign;
    }

    public String signString(String stringToBeSigned, Context context) throws Exception {
        Log.d(TAG, "signString() invoked");
        String privateKey = new DataInSharedPreferences().retrievingPrivateKey();
        PrivateKey pk = new DataInSharedPreferences().getPrivateKeyFromString(privateKey);

        Log.d(TAG, "PrivateKey: \n" + pk.toString());
        Signature dsa = Signature.getInstance("SHA256withECDSA");
        dsa.initSign(pk);
        // The string that needs to be signed.
        byte[] strByte = stringToBeSigned.getBytes("UTF-8");
        dsa.update(strByte);
        // Actual Signing of the String 'stringToBeSigned' with PrivateKey 'priv'.
        byte[] sign = dsa.sign();
        String signature = getSignatureString(sign);
        return signature;
    }
}
