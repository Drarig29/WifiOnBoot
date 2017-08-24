package com.drarig29.wifionboot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText txtSsid, txtPassword;
    SharedPreferences settings;

    public static final String SSID_SHARED_PREFS = "SSID";
    public static final String PASSWORD_SHARED_PREFS = "PASSWORD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new WifiHelper(getApplicationContext());
        settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        txtSsid = (EditText) findViewById(R.id.txtSsid);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        txtSsid.setText(settings.getString(SSID_SHARED_PREFS, null));
        txtPassword.setText(settings.getString(PASSWORD_SHARED_PREFS, null));

        findViewById(R.id.btnShowNetworks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!WifiHelper.isWifiEnabled()) {
                    Toast.makeText(MainActivity.this, R.string.please_enable_wifi, Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, WifiListActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.btnAddNetwork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emptySsidOrPassword())
                    return;

                WifiHelper.addNetwork(txtSsid.getText().toString(), txtPassword.getText().toString());
            }
        });

        findViewById(R.id.btnConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emptySsidOrPassword())
                    return;

                String ssid = txtSsid.getText().toString();

                if (!WifiHelper.isNetworkConfigured(ssid))
                    WifiHelper.addNetwork(ssid, txtPassword.getText().toString());

                WifiHelper.connectToRegisteredSsid(ssid);
            }
        });
    }

    boolean emptySsidOrPassword() {
        boolean empty = false;

        if (TextUtils.isEmpty(txtSsid.getText().toString().trim())) {
            txtSsid.setError(getString(R.string.please_enter_ssid));
            empty = true;
        }

        if (TextUtils.isEmpty(txtPassword.getText().toString().trim())) {
            txtPassword.setError(getString(R.string.please_enter_password));
            empty = true;
        }

        return empty;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1)
            return;

        if (data == null)
            return;

        String ssid = data.getStringExtra("ssid");
        ((EditText) findViewById(R.id.txtSsid)).setText(ssid);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SSID_SHARED_PREFS, txtSsid.getText().toString());
        editor.putString(PASSWORD_SHARED_PREFS, txtPassword.getText().toString());

        editor.apply();
    }
}
