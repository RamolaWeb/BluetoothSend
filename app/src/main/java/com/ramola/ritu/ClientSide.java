package com.ramola.ritu;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ClientSide extends AsyncTask<String, Void, Void> {
    private final String TAG=ClientSide.class.getSimpleName();
    private BluetoothAdapter bluetoothAdapter;
    private HomeActivity activity;
    private BluetoothSocket bluetoothSocket;
    private byte[] imageByte;
    private BufferedOutputStream bufferedOutputStream;

    public ClientSide(BluetoothAdapter bluetoothAdapter, HomeActivity activity, byte[] imageByte) {
        this.bluetoothAdapter = bluetoothAdapter;
        this.activity = activity;
        this.imageByte = imageByte;
    }

    @Override
    protected Void doInBackground(String... strings) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(strings[0]);
        try {
            bluetoothAdapter.cancelDiscovery();
            bluetoothSocket = device.createRfcommSocketToServiceRecord(ServerSide.uuid);
            bluetoothSocket.connect();
        } catch (IOException e) {
            try {
                Log.d("reconnecting","reconnecting");

                bluetoothSocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                bluetoothSocket.connect();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

        try {
            bufferedOutputStream = new BufferedOutputStream(bluetoothSocket.getOutputStream());
            bufferedOutputStream.write(imageByte);
            bufferedOutputStream.flush();
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.d(TAG,"image send");
        Toast.makeText(activity.getApplicationContext(),"image send",Toast.LENGTH_SHORT).show();
    }
}
