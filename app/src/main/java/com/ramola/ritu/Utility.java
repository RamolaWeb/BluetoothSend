package com.ramola.ritu;


import android.bluetooth.BluetoothAdapter;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utility {

    public static String getStatus(int code) {
        switch (code) {
            case BluetoothAdapter.STATE_ON:
                return "Active";
            case BluetoothAdapter.STATE_OFF:
                return "UnActive";
            case BluetoothAdapter.STATE_TURNING_OFF:
                return "Turning off";
            case BluetoothAdapter.STATE_TURNING_ON:
                return "Turning on";
            default:
                return "";
        }
    }

    public static String SaveImage(InputStream inputStream, String filename) {
        File folder = Environment.getExternalStoragePublicDirectory("Blue Send");
        if (!folder.exists()) {
            folder.mkdir();
        }

        File file = new File(folder, filename + ".png");
        if (file.exists()) {
            return null;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            copyFile(inputStream,fileOutputStream);
            Log.d("FileSave", "File " + file.getName() + " has saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private static void copyFile(InputStream inputStream, OutputStream out) {

        byte buf[] = new byte[1024];
        int len=0,size=0,s,sm=0;
        Log.d("tag", "reading");
        try {
            while ((len = inputStream.read(buf)) != -1) {

                out.write(buf, 0, len);
                s=len;
                size+=s;
                Log.d("len", "" +size);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
