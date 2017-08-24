package com.drarig29.wifionboot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class WifiListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_list);

        ListView listView = (ListView) findViewById(R.id.wifiList);

        new WifiHelper(getApplicationContext());
        final ArrayList<String> ssidList = WifiHelper.getSsidList();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ssidList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                Intent i = new Intent();
                i.putExtra("ssid", ssidList.get(position));
                setResult(1, i);
                finish();
            }
        });
    }
}
