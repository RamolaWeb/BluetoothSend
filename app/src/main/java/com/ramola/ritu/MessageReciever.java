package com.ramola.ritu;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public class MessageReciever extends BroadcastReceiver {
    private static final String TAG = BroadcastReceiver.class.getSimpleName();
    private HomeActivity activity;
    public static final String ACTION_IMAGE = "com.ramola.ritu.actionImage";
    public static final String INTENT_IMAGE = "intent of image";

    public static final String ACTION_RECIEVE = "com.ramola.ritu.actionRecieve";
    public static final String INTENT_RECIEVE = "intent of recieve";

    public MessageReciever(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"recieve");
        switch (intent.getAction()) {
            case ACTION_IMAGE:
                activity.displayImage(true);
                activity.displayList(false);
                activity.displaySearchLayout(false, false);
                activity.imageView.setImageBitmap(BitmapFactory.decodeFile(intent.getStringExtra(INTENT_IMAGE)));
                break;
            case ACTION_RECIEVE:
                activity.displayList(false);
                activity.displaySearchLayout(false,true);
                Toast.makeText(context, intent.getStringExtra(INTENT_RECIEVE), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
