package com.drarig29.wifionboot;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;

import static android.content.Context.WIFI_SERVICE;

class WifiHelper {

    private static WifiManager wifiManager;

    private static final String TAG = WifiHelper.class.getSimpleName();

    WifiHelper(Context context) {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
    }

    static boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }

    static ArrayList<String> getSsidList() {
        ArrayList<String> list = new ArrayList<>();

        for (WifiConfiguration wifi : wifiManager.getConfiguredNetworks()) {
            String ssid = wifi.SSID;
            ssid = ssid.substring(1, ssid.length() - 1); //remove the " "
            list.add(ssid);
        }

        return list;
    }

    static boolean isNetworkConfigured(String ssid) {
        boolean b = false;
        ssid = "\"" + ssid + "\"";

        for (WifiConfiguration wifi : wifiManager.getConfiguredNetworks()) {
            if (wifi.SSID.equals(ssid)) {
                b = true;
                break;
            }
        }

        return b;
    }

    static void addNetwork(String ssid, String key) {
        Log.i(TAG, "Configuring " + ssid + "...");

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + ssid + "\"";

        //WEP
        /*
        conf.wepKeys[0] = "\"" + networkPass + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
         */

        //WPA
        conf.preSharedKey = "\"" + key + "\"";

        //Open networks
        /*
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
         */

        wifiManager.addNetwork(conf);
    }

    static void connectToRegisteredSsid(String ssid) {
        Log.i(TAG, "Enabling wifi...");

        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);

        while (wifiManager.getConfiguredNetworks() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "Waiting for wifi...");
        }

        Log.i(TAG, "Connecting to " + ssid + "...");

        for (WifiConfiguration wifi : wifiManager.getConfiguredNetworks())
            if (wifi.SSID != null && wifi.SSID.equals("\"" + ssid + "\"")) {
                wifiManager.enableNetwork(wifi.networkId, true);
                break;
            }
    }
}
