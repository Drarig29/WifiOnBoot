package com.drarig29.wifionboot;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ_STORAGE = 1;
    private static final int REQUEST_WRITE_STORAGE = 2;

    EditText txtSsid, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new WifiHelper(getApplicationContext());

        txtSsid = (EditText) findViewById(R.id.txtSsid);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        checkAndRequestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_STORAGE);

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

                if (!WifiHelper.isWifiEnabled()) {
                    Toast.makeText(MainActivity.this, R.string.please_enable_wifi, Toast.LENGTH_SHORT).show();
                    return;
                }

                WifiHelper.addNetwork(txtSsid.getText().toString(), txtPassword.getText().toString());
            }
        });

        findViewById(R.id.btnSaveConfig).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptySsidOrPassword())
                    return;

                checkAndRequestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_STORAGE);
            }
        });

        findViewById(R.id.btnConnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emptySsidOrPassword())
                    return;

                if (!WifiHelper.isWifiEnabled()) {
                    Toast.makeText(MainActivity.this, R.string.please_enable_wifi, Toast.LENGTH_SHORT).show();
                    return;
                }

                String ssid = txtSsid.getText().toString();

                if (!WifiHelper.isNetworkConfigured(ssid))
                    WifiHelper.addNetwork(ssid, txtPassword.getText().toString());

                WifiHelper.connectToRegisteredSsid(ssid);
            }
        });
    }

    private void checkAndRequestPermission(String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{permission},
                    requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_READ_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    checkConfigFile();
                else
                    warnUserWhenPermissionDenied(getString(R.string.warn_read_storage_denied), requestCode);

                break;

            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ConfigFileHelper.saveConfig(txtSsid.getText().toString(), txtPassword.getText().toString());
                else
                    warnUserWhenPermissionDenied(getString(R.string.warn_write_storage_denied), requestCode);

                break;
        }
    }

    private void warnUserWhenPermissionDenied(String message, final int requestCode) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message + "\n\n" + getString(R.string.question_allow_it))

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        switch (requestCode) {
                            case REQUEST_READ_STORAGE:
                                Toast.makeText(MainActivity.this, R.string.cant_load_config, Toast.LENGTH_SHORT).show();
                                break;

                            case REQUEST_WRITE_STORAGE:
                                Toast.makeText(MainActivity.this, R.string.cant_save_config, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                })

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        switch (requestCode) {
                            case REQUEST_READ_STORAGE:
                                ActivityCompat.requestPermissions(
                                        MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        requestCode);
                                break;

                            case REQUEST_WRITE_STORAGE:
                                ActivityCompat.requestPermissions(
                                        MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        requestCode);
                                break;
                        }
                    }
                })

                .create()
                .show();
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
