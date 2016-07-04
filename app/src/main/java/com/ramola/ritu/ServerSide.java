package com.ramola.ritu;


import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class ServerSide extends IntentService {
    public static final UUID uuid = UUID.fromString("12ae873d-d006-47c8-bd57-5ad59d67e525");
    public static final String SERVICE_NAME = "BlueSend";
    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter adapter;
    private HomeActivity activity;
    private Random random;
    private String filePath;
    public ServerSide() {
        super("Server Service");
    }

    public ServerSide(BluetoothAdapter adapter, HomeActivity activity) {
        super("Server Service");
        this.adapter = adapter;
        this.activity = activity;

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("Tag", "ui");
        random = new Random();
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            Log.d("Tag", "ui1");
            while (true) {
                try {
                    bluetoothServerSocket = adapter.listenUsingRfcommWithServiceRecord(SERVICE_NAME, uuid);
                    bluetoothSocket = bluetoothServerSocket.accept();
                    Intent i1 = new Intent();
                    i1.setAction(MessageReciever.ACTION_RECIEVE);
                    i1.addCategory(Intent.CATEGORY_DEFAULT);
                    i1.putExtra(MessageReciever.INTENT_RECIEVE,"Receiving...");
                    sendBroadcast(i1);
                    filePath = Utility.SaveImage(bluetoothSocket.getInputStream(), "File " + random.nextInt(1000));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent i = new Intent();
                i.setAction(MessageReciever.ACTION_IMAGE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                i.putExtra(MessageReciever.INTENT_IMAGE, filePath);
                sendBroadcast(i);
                Log.d("Tag", "ui1111111");

            }
        } else {
            Log.d("as", "false");
        }
    }
}
