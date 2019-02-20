package com.example.sid24rane.blockcanteen.utilities;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class JSONDump {

    private static String fileName = "blockCanteen.json";
    private static String TAG = "JSONDump";

    public static void saveData(Context context, String mJsonResponse) {
        Log.d(TAG, "saveData() invoked");
        try {
            Log.d(TAG, "dir : " + context.getFilesDir().getPath() + "/" + fileName);
            FileWriter file = new FileWriter(context.getFilesDir().getPath() + "/" + fileName);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            Log.e(TAG, "Error in Writing: " + e.getLocalizedMessage());
        }
    }


    public static String getData(Context context) {
        Log.d(TAG, "getData() invoked");
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e(TAG, "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static String getDataFromPath(String path) {
        Log.d(TAG, "getDataFromPath() invoked" + path);
        try {
            File f = new File(path);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            Log.d("File : ", new String(buffer));
            return new String(buffer).replaceAll("\\s+", "");
        } catch (IOException e) {
            Log.e(TAG, "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }

    public static void downloadProfile( String data, Context context) {

        if(checkExternalMedia()){
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/vjti-wallet");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            String filename = "credentials.json";
            File file = new File (myDir, filename);
            if (file.exists ())
                file.delete ();
            try {
                FileOutputStream out = new FileOutputStream(file);
                out.write(data.getBytes());
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(context, new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("ExternalStorage", "Scanned " + path + ":");
                            Log.d("ExternalStorage", "-> uri=" + uri);
                        }
                    });

        }else{
            Toast.makeText(context, "External Storage Not available", Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return mExternalStorageWriteable;

    }
}
