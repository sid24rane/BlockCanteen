package com.example.sid24rane.blockcanteen.utilities;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.sid24rane.blockcanteen.data.KeyInSharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.Signature;
import java.util.Arrays;

public class TransactionUtils {
    public static String TAG = "TransactionUtils";

    public static void makeTransaction(String amount, String sender_pub, String receiver_pub, final Context context){
        Log.d(TAG, "maketransaction() invoked");

        AndroidNetworking.post(NetworkUtils.getMakeTransactionUrl())
                .addUrlEncodeFormBodyParameter("bounty",amount)
                .addUrlEncodeFormBodyParameter("receiver_public_key", sender_pub)
                .addUrlEncodeFormBodyParameter("sender_public_key",receiver_pub)
                .setContentType("application/x-www-form-urlencoded")
                .setTag("makeTransaction")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("makeTransaction res: ", response.toString());
                        try {
                            JSONObject responseJson = new JSONObject(response.toString());

                            String send_this = responseJson.getString("send_this");
                            String sign_this = responseJson.getString("sign_this");
                            Log.d(TAG, "send_this : "+  send_this);
                            Log.d(TAG, "sign_this : " + sign_this);

                            String signedString = signString(sign_this, context);

                            sendTransaction(send_this, signedString);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("makeTransaction err:", anError.toString());

                    }
                });
    }

    public static void sendTransaction(String transaction, String signature){

        Log.d(TAG, "Transaction : " + transaction);
        Log.d(TAG, "Signature : " + signature);

        AndroidNetworking.post(NetworkUtils.getSendTransactionUrl())
                .addUrlEncodeFormBodyParameter("transaction", transaction)
                .addUrlEncodeFormBodyParameter("signature", signature)
                .setContentType("application/x-www-form-urlencoded")
                .setTag("sendTransaction")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "sndTxn onResponse= " + response.toString());
                        //TODO : update UI
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "sndTxn onError= " + anError.toString());
                    }
                });
    }


    public static BigInteger extractR(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startR + 2, startR + 2 + lengthR));
    }

    public static BigInteger extractS(byte[] signature) throws Exception {
        int startR = (signature[1] & 0x80) != 0 ? 3 : 2;
        int lengthR = signature[startR + 1];
        int startS = startR + 2 + lengthR;
        int lengthS = signature[startS + 1];
        return new BigInteger(Arrays.copyOfRange(signature, startS + 2, startS + 2 + lengthS));
    }

    public static byte[] derSign(BigInteger r, BigInteger s) throws Exception {
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

    public static String getSignatureString(byte[] sign) throws Exception {
        // System.out.println("Signature: " + new BigInteger(1, sign).toString(16));

        BigInteger r = extractR(sign);
        BigInteger s = extractS(sign);
        String realSign = "[" + r.toString() + ", " + s.toString() + "]";
        return realSign;
    }

    public static String signString(String stringToBeSigned, Context context) throws Exception {
        Log.d(TAG, "signString() invoked");

        String privateKey = KeyInSharedPreferences.retrievingPrivateKey(context);

        Signature dsa = Signature.getInstance("SHA256withECDSA");
        dsa.initSign(KeyInSharedPreferences.getPrivateKeyFromString(privateKey)); // Pass the private Key that we need.

        // The string that needs to be signed.
        byte[] strByte = stringToBeSigned.getBytes("UTF-8");
        dsa.update(strByte);

        // Actual Signing of the String 'stringToBeSigned' with PrivateKey 'priv'.
        byte[] sign = dsa.sign();
        String signature = getSignatureString(sign);
        return signature;
    }
}
