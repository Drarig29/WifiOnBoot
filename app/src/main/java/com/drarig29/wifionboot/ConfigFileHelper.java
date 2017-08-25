package com.drarig29.wifionboot;


import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

class ConfigFileHelper {

    private static final String TAG = ConfigFileHelper.class.getSimpleName();

    private static final File CONFIG_FILE = new File(Environment.getExternalStorageDirectory(), "wifionboot.conf");

    static class Config {
        String ssid, key;
    }

    static Config getConfig() {
        Config c = new Config();

        try {
            BufferedReader br = new BufferedReader(new FileReader(CONFIG_FILE));
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

    static void saveConfig(String ssid, String key) {
        FileOutputStream os;
        try {
            os = new FileOutputStream(CONFIG_FILE);

            String data = String.format("ssid=\"%s\"\n" +
                    "key=\"%s\"\n", ssid, key);

            os.write(data.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static boolean fileExists() {
        return CONFIG_FILE.exists();
    }
}
