package com.cyberchat.app;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS = 2;

    private BluetoothAdapter bluetoothAdapter;
    private Button btnEnableBluetooth;
    private Button btnFindDevices;
    private Button btnStartChat;
    private TextView tvStatus;
    private TextView tvBluetoothStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupBluetooth();
        setupClickListeners();
        checkPermissions();
    }

    private void initViews() {
        btnEnableBluetooth = findViewById(R.id.btn_enable_bluetooth);
        btnFindDevices = findViewById(R.id.btn_find_devices);
        btnStartChat = findViewById(R.id.btn_start_chat);
        tvStatus = findViewById(R.id.tv_status);
        tvBluetoothStatus = findViewById(R.id.tv_bluetooth_status);
    }

    private void setupBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            tvBluetoothStatus.setText(R.string.bt_not_supported);
            tvStatus.setText("ERROR");
            tvStatus.setTextColor(getResources().getColor(R.color.cyber_red));
            return;
        }

        updateBluetoothStatus();
    }

    private void setupClickListeners() {
        btnEnableBluetooth.setOnClickListener(v -> {
            if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }
        });

        btnFindDevices.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivity(intent);
        });

        btnStartChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            startActivity(intent);
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions = new String[] {
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                };
            }

            boolean needsPermission = false;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    needsPermission = true;
                    break;
                }
            }

            if (needsPermission) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS);
            }
        }
    }

    private void updateBluetoothStatus() {
        if (bluetoothAdapter == null) {
            return;
        }

        if (bluetoothAdapter.isEnabled()) {
            tvBluetoothStatus.setText(R.string.bt_enabled);
            tvStatus.setText(R.string.status_online);
            tvStatus.setTextColor(getResources().getColor(R.color.status_online));
            btnFindDevices.setEnabled(true);
            btnStartChat.setEnabled(true);
            btnEnableBluetooth.setText("BLUETOOTH ENABLED");
            btnEnableBluetooth.setEnabled(false);
        } else {
            tvBluetoothStatus.setText(R.string.bt_disabled);
            tvStatus.setText(R.string.status_offline);
            tvStatus.setTextColor(getResources().getColor(R.color.status_offline));
            btnFindDevices.setEnabled(false);
            btnStartChat.setEnabled(false);
            btnEnableBluetooth.setText(R.string.enable_bluetooth);
            btnEnableBluetooth.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBluetoothStatus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.bt_enabled, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
            }
            updateBluetoothStatus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show();
            }
        }
    }
}