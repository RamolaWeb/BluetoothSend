package com.ramola.ritu;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BluetoothBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG=BluetoothBroadCastReceiver.class.getSimpleName();
    private HomeActivity activity;

    public BluetoothBroadCastReceiver(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int code=intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,-1);
                activity.setStatus(code);
                break;
            case BluetoothDevice.ACTION_FOUND:
                activity.displayList(true);
                BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                activity.addDevice(device);
                Log.d(TAG,device.getName()+" "+device.getAddress());
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                activity.doIFNoDeviceFound();
                activity.displaySearchLayout(false,false);
                break;
        }
    }
}
