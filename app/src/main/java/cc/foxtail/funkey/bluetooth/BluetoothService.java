package cc.foxtail.funkey.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import cc.foxtail.funkey.OnStateChangeListener;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int STATE_LISTEN = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_FAIL = 4;

    private BluetoothAdapter bluetoothAdapter;

    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    private OnStateChangeListener onStateChangListener;

    private int connectionState;

    public BluetoothService(OnStateChangeListener onStateChangListener) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.onStateChangListener = onStateChangListener;
    }


    public void getDeviceInfo(String address) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        Log.d(TAG, "Device address : " + address);

        connect(device);
    }

    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + connectionState + " -> " + state);
        connectionState = state;
        if (onStateChangListener != null)
            onStateChangListener.onChanged(state);
    }

    private void connectionFailed() {
        setState(STATE_FAIL);
    }

    private void connectionLost() {
        setState(STATE_FAIL);
    }


    public synchronized void connect(BluetoothDevice device) {
        Log.d(TAG, "connect to: " + device.getName());


        preventMultiConnect();

        connectThread = new ConnectThread(device);

        connectThread.start();
        setState(STATE_CONNECTING);

    }

    public synchronized void connected(BluetoothSocket socket) {
        Log.d(TAG, "connected");

        preventMultiConnect();

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();

        setState(STATE_CONNECTED);
    }

    private void preventMultiConnect() {
        if (connectThread != null && connectThread.isAlive()) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null && connectedThread.isAlive()) {
            connectedThread.cancel();
            connectedThread = null;
        }

    }


    public void write(byte[] out) {
        ConnectedThread currentConnectedThread;
        synchronized (this) {
            if (connectionState != STATE_CONNECTED)
                return;
            currentConnectedThread = connectedThread;
        } //
        currentConnectedThread.write(out);
    }

    public void disconnect() {
        if (connectedThread != null && connectedThread.isAlive())
            connectedThread.cancel();
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN connectThread");
            setName("ConnectThread");

            bluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.d(TAG, "Connect Success");

            } catch (IOException e) {

                connectionFailed();
                e.printStackTrace();
                Log.d(TAG, "Connect Fail");

                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
//                this.start();
                return;
            }

            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            connected(mmSocket);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            boolean protocol = false;
            boolean sensorData = false;
            boolean sendProtocol = false;

            byte[] buffer;
            ArrayList<Integer> arr_byte = new ArrayList<Integer>();

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    int data = mmInStream.read();

                    if (data == 0x46 && !sensorData) {
                        protocol = true;
                    } else if (data == 0x55 || data == 0x56 || data == 0x57 || data == 0x58 || data == 0x59 || data == 0x5a) {
                        sensorData = true;
                        arr_byte.add(data);
                    } else {
                        arr_byte.add(data);
                    }

                    if (protocol && arr_byte.size() > 3 && data == 0x4f && sensorData) {
                        sendProtocol = true;
                    } else if ((protocol && arr_byte.size() == 2) && (data == 0x4f || data == 0x45) && !sensorData) {
                        sendProtocol = true;
                    } else if (protocol && arr_byte.size() == 4) {
                        sendProtocol = true;
                    }

                    if (sendProtocol) {

                        buffer = new byte[arr_byte.size()];
                        for (int i = 0; i < arr_byte.size(); i++) {
                            buffer[i] = arr_byte.get(i).byteValue();
                        }
                        onStateChangListener.onReceived(buffer);
                        protocol = false;
                        sensorData = false;
                        sendProtocol = false;
                        arr_byte = new ArrayList<>();
                    }

                } catch (IOException e) {
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }


        public void cancel() {
            try {
                mmInStream.close();
                mmOutStream.close();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
}
