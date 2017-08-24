package com.drarig29.wifionboot;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Intent received");

        Toast.makeText(context, "Activation du wifi", Toast.LENGTH_SHORT).show();

        new WifiHelper(context);
        WifiHelper.connectToRegisteredSsid("SFR_81D0");
    }
}
