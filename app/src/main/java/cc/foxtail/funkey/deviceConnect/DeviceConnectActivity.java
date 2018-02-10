package cc.foxtail.funkey.deviceConnect;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import cc.foxtail.funkey.navigation.NavigationActivity;
import cc.foxtail.funkey.OnDeviceClickListener;
import cc.foxtail.funkey.R;

public class DeviceConnectActivity extends AppCompatActivity implements DeviceConnectContract.View {
    private static final int REQUEST_ENABLE_BLUETOOTH = 100;
    private static final int REQUEST_LOCATION_ACCESS = 101;

    private ProgressDialog progressDialog;
    private DeviceConnectPresenter deviceConnectPresenter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_connect);

        progressDialog = new ProgressDialog(DeviceConnectActivity.this);
        toolbar = findViewById(R.id.toolbar);

        TextView toolbarTextView = toolbar.findViewById(R.id.toolbar_text_view);
        toolbar.findViewById(R.id.name_spinner).setVisibility(View.GONE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);

        toolbarTextView.setText(getResources().getString(R.string.connect_device));

        deviceConnectPresenter = new DeviceConnectPresenter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(deviceConnectPresenter.getReceiver(), filter);

        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(deviceConnectPresenter.getReceiver(), filter2);
        deviceConnectPresenter.setView(this);

        RecyclerView pairedDeviceRecyclerView = findViewById(R.id.paired_devices_recycler_view);
        pairedDeviceRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerView searchedDeviceRecyclerView = findViewById(R.id.searched_devices_recycler_view);
        searchedDeviceRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        Button scanButton = findViewById(R.id.button_scan);

        LinearLayoutManager pairedDeviceLayoutManager = new LinearLayoutManager(getBaseContext());
        pairedDeviceLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager searchedDeviceLayoutManager = new LinearLayoutManager(getBaseContext());
        searchedDeviceLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        DeviceListAdapter pairedDeviceListAdapter = new DeviceListAdapter(new OnDeviceClickListener() {
            @Override
            public void onClick(String address) {
                tryConnect(address);
            }
        });

        DeviceListAdapter searchedDeviceListAdapter = new DeviceListAdapter(new OnDeviceClickListener() {
            @Override
            public void onClick(String address) {
                tryConnect(address);
            }
        });

        deviceConnectPresenter.setAdapter(pairedDeviceListAdapter, searchedDeviceListAdapter);

        pairedDeviceRecyclerView.setLayoutManager(pairedDeviceLayoutManager);
        pairedDeviceRecyclerView.setAdapter(pairedDeviceListAdapter);

        searchedDeviceRecyclerView.setLayoutManager(searchedDeviceLayoutManager);
        searchedDeviceRecyclerView.setAdapter(searchedDeviceListAdapter);


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceConnectPresenter.doDiscovery();
            }
        });

        deviceConnectPresenter.findPairedDevice();

        checkPermissions();
        deviceConnectPresenter.enableBlueTooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Device Connect Activity on Resume");
    }

    public void tryConnect(String address) {
        if (Objects.equals(address, ""))
            return;

        deviceConnectPresenter.stopDiscovery();

        unregisterReceiver(deviceConnectPresenter.getReceiver());

        Intent intent = new Intent();
        intent.putExtra("ADDRESS", address);

        setResult(RESULT_OK, intent);

        finish();
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_ACCESS);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, NavigationActivity.class));
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                deviceConnectPresenter.findPairedDevice();
            }
        }
    }

    @Override
    public void enableBlueTooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
    }

    @Override
    public void showProgressDialog(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(deviceConnectPresenter.getReceiver());
        } catch (IllegalArgumentException e) {
        }
    }
}
