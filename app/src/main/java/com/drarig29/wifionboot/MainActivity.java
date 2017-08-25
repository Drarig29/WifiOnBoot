package com.drarig29.wifionboot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText txtSsid, txtPassword;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new WifiHelper(getApplicationContext());

        txtSsid = (EditText) findViewById(R.id.txtSsid);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        checkConfigFile();

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

    void checkConfigFile() {
        if (!ConfigFileHelper.fileExists())
            return;

        ConfigFileHelper.Config c = ConfigFileHelper.getConfig();
        txtSsid.setText(c.ssid);
        txtPassword.setText(c.key);
    }

    boolean emptySsidOrPassword() {
        boolean empty = false;

        TextInputLayout ssidLayout = (TextInputLayout) findViewById(R.id.ssidParent);
        TextInputLayout passwordLayout = (TextInputLayout) findViewById(R.id.passwordParent);

        if (TextUtils.isEmpty(txtSsid.getText().toString().trim())) {
            ssidLayout.setErrorEnabled(true);
            ssidLayout.setError(getString(R.string.please_enter_ssid));
            empty = true;
        } else
            ssidLayout.setErrorEnabled(false);

        if (TextUtils.isEmpty(txtPassword.getText().toString().trim())) {
            passwordLayout.setErrorEnabled(true);
            passwordLayout.setError(getString(R.string.please_enter_password));
            empty = true;
        } else
            passwordLayout.setErrorEnabled(false);

        return empty;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1 || data == null)
            return;

        String ssid = data.getStringExtra("ssid");
        ((EditText) findViewById(R.id.txtSsid)).setText(ssid);
    }
}
