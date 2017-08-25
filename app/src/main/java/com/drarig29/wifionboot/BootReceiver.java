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

        ConfigFileHelper.Config c = ConfigFileHelper.getConfig(context);

        if (!WifiHelper.isWifiEnabled())
            WifiHelper.enableAndWaitForWifi();

        if (!WifiHelper.isNetworkConfigured(c.ssid))
            WifiHelper.addNetwork(c.ssid, c.key);

        WifiHelper.connectToRegisteredSsid(c.ssid);
    }
}
