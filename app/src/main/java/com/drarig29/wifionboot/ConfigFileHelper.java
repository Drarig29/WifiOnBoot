package com.drarig29.wifionboot;


import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

class ConfigFileHelper {

    private static final String TAG = ConfigFileHelper.class.getSimpleName();

    private static final String CONFIG_FILE = "wifionboot.conf";

    static class Config {
        String ssid, key;
    }

    static Config getConfig(Context context) {
        Config c = new Config();

        try {
            FileInputStream input = context.openFileInput(CONFIG_FILE);
            InputStreamReader inputStreamReader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(inputStreamReader);

            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains("ssid"))
                    c.ssid = WifiHelper.removeQuotes(line.substring(line.indexOf("=") + 1));

                if (line.contains("key"))
                    c.key = WifiHelper.removeQuotes(line.substring(line.indexOf("=") + 1));
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return c;
    }

    static void saveConfig(Context context, String ssid, String key) {
        FileOutputStream os;

        try {
            os = context.openFileOutput(CONFIG_FILE, Context.MODE_PRIVATE);

            String data = String.format("ssid=\"%s\"\n" +
                    "key=\"%s\"\n", ssid, key);

            os.write(data.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean fileExists(Context context) {
        return context.getFileStreamPath(CONFIG_FILE).exists();
    }
}
