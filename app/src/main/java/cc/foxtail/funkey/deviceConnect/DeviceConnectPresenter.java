package cc.foxtail.funkey.deviceConnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Set;


public class DeviceConnectPresenter implements DeviceConnectContract.Presenter {

    private DeviceConnectContract.View view;
    private DeviceListAdapter pairedAdapter;
    private DeviceListAdapter searchedAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver receiver;

    public BroadcastReceiver getReceiver() {
        return receiver;
    }

    public DeviceConnectPresenter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                        if (device.getName() != null) {
                            System.out.println("TEST" + device.getName() + "\n" + device.getAddress());
                            searchedAdapter.addDevice(device.getName() + "\n" + device.getAddress());
                            searchedAdapter.notifyDataSetChanged();
                        }
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    view.dismissProgressDialog();

                    if (searchedAdapter.getItemCount() == 0) {
                        searchedAdapter.addDevice("검색된 기기가 없습니다.");
                        searchedAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

    }

    @Override
    public void setView(DeviceConnectContract.View view) {
        this.view = view;
    }

    @Override
    public void setAdapter(DeviceListAdapter pairedAdapter, DeviceListAdapter searchedAdapter) {
        this.pairedAdapter = pairedAdapter;
        this.searchedAdapter = searchedAdapter;
    }

    @Override
    public void findPairedDevice() {
        pairedAdapter.clearDeviceList();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedAdapter.addDevice(device.getName() + "\n" + device.getAddress());
                pairedAdapter.notifyDataSetChanged();
            }
        } else {
            pairedAdapter.addDevice("패어링된 기기가 없습니다.");
            pairedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void enableBlueTooth() {
        if (!bluetoothAdapter.isEnabled()) {
            view.enableBlueTooth();
        }
    }

    @Override
    public void doDiscovery() {
        searchedAdapter.clearDeviceList();

        view.showProgressDialog("탐색중 ..");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.startDiscovery();
            return;
        }

        bluetoothAdapter.startDiscovery();

    }

    @Override
    public void stopDiscovery() {
        bluetoothAdapter.cancelDiscovery();
    }


}
