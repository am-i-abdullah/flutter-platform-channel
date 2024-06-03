package com.netum.device.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.netum.device.SPPManager;
import com.netum.device.callback.BleWriteCallback;
import com.netum.device.callback.SppConnectCallback;
import com.netum.device.data.BleConnectStateParameter;
import com.netum.device.data.BleDevice;
import com.netum.device.data.BleMsg;
import com.netum.device.data.BleWriteState;
import com.netum.device.exception.BleException;
import com.netum.device.exception.OtherException;
import com.netum.device.instruction.ServiceList;
import com.netum.device.utils.BleLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SppBluetooth extends Thread {
    private SppConnectCallback sppConnectCallback;

    private BleBluetooth.LastState lastState;
    private boolean isActiveDisconnect = false;
    private BleDevice mDevice;
    private BluetoothSocket mBluetoothSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private final MainHandler mainHandler = new MainHandler(Looper.getMainLooper());
    private int connectRetryCount = 0;

    public SppBluetooth(BleDevice bleDevice) {
        mDevice = bleDevice;
    }

    public synchronized void addConnectGattCallback(SppConnectCallback callback) {
        sppConnectCallback = callback;
    }

    public synchronized void removeConnectGattCallback() {
        sppConnectCallback = null;
    }

    public String getDeviceKey() { return mDevice.getKey(); }

    public BleDevice getDevice() {
        return mDevice;
    }

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }


    public synchronized BluetoothSocket connect(BleDevice bleDevice,
                                              boolean autoConnect,
                                                SppConnectCallback callback) {
        return connect(bleDevice, autoConnect, callback, 0);
    }

    public synchronized BluetoothSocket connect(BleDevice bleDevice,
                                              boolean autoConnect,
                                                SppConnectCallback callback,
                                              int connectRetryCount) {
        BleLog.i("connect device: " + bleDevice.getName()
                + "\nmac: " + bleDevice.getMac()
                + "\nautoConnect: " + autoConnect
                + "\ncurrentThread: " + Thread.currentThread().getId()
                + "\nconnectCount:" + (connectRetryCount + 1));
        if (connectRetryCount == 0) {
            this.connectRetryCount = 0;
        }

        addConnectGattCallback(callback);

        lastState = BleBluetooth.LastState.CONNECT_CONNECTING;
        if (sppConnectCallback != null) {
            sppConnectCallback.onStartConnect();
        }
        try {
            // Standard SerialPortService ID
            mBluetoothSocket = mDevice.getDevice().createRfcommSocketToServiceRecord(ServiceList.SPPServiceUUID);
        } catch (Exception ce){
            //serialSocket = connectViaReflection(device);
        }
        Message message = mainHandler.obtainMessage();
        message.what = BleMsg.MSG_CONNECTING;
        mainHandler.sendMessageDelayed(message, SPPManager.getInstance().getConnectOverTime());
        return mBluetoothSocket;
    }



    public synchronized void write(byte[] data, BleWriteCallback bleWriteCallback) {
        if (data == null || data.length <= 0) {
            if (bleWriteCallback != null)
                bleWriteCallback.onWriteFailure(new OtherException("the data to be written is empty"));
            return;
        }

        if (mBluetoothSocket == null
                || !mBluetoothSocket.isConnected()) {
            if (bleWriteCallback != null)
                bleWriteCallback.onWriteFailure(new OtherException("this bluetoothSocket not connected!"));
            return;
        }
        try {
            mOutputStream.write(data);
            bleWriteCallback.onWriteSuccess(BleWriteState.DATA_WRITE_SINGLE, BleWriteState.DATA_WRITE_SINGLE, data);
        } catch (IOException e) {
            e.printStackTrace();
            if (bleWriteCallback != null)
                bleWriteCallback.onWriteFailure(new OtherException("bluetoothSocket write data fail"));
        }
    }


    public synchronized void disconnect() {
        lastState = BleBluetooth.LastState.CONNECT_IDLE;
        isActiveDisconnect = true;
        if (mBluetoothSocket != null) {
            try {
                _socketClose(mBluetoothSocket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        refreshDeviceCache();
    }

    private synchronized void refreshDeviceCache() {
        try {
            final Method refresh = BluetoothSocket.class.getMethod("refresh");
            if (refresh != null && mBluetoothSocket != null) {
                boolean success = (Boolean) refresh.invoke(mBluetoothSocket);
                BleLog.i("refreshDeviceCache, is success:  " + success);
            }
        } catch (Exception e) {
            BleLog.i("exception occur while refreshing device: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private final class MainHandler extends Handler {

        MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BleMsg.MSG_CONNECTING:
                {
                    if (mBluetoothSocket != null) {
                        try {
                            mInputStream = mBluetoothSocket.getInputStream();
                            mOutputStream = mBluetoothSocket.getOutputStream();
                            mBluetoothSocket.connect();
                        } catch (IOException e) {
                            try {
                                Thread.sleep(1000);

                            } catch (InterruptedException ex) {
                            }
                        }
                        if(_socketConnected(mBluetoothSocket)) {
                            lastState = BleBluetooth.LastState.CONNECT_CONNECTED;
                            isActiveDisconnect = false;
                            SPPManager.getInstance().getMultipleBluetoothController().removeConnecting(SppBluetooth.this);
                            SPPManager.getInstance().getMultipleBluetoothController().addBluetooth(SppBluetooth.this);
                            if (sppConnectCallback != null)
                                sppConnectCallback.onConnectSuccess(mDevice, mBluetoothSocket, 1);
                            start();
                        }else {
                            lastState = BleBluetooth.LastState.CONNECT_FAILURE;
                            SPPManager.getInstance().getMultipleBluetoothController().removeConnecting(SppBluetooth.this);
                            if (sppConnectCallback != null)
                                sppConnectCallback.onConnectFail(mDevice, new OtherException("BluetoothSocket connection failed"));
                        }

                    } else {
                        lastState = BleBluetooth.LastState.CONNECT_FAILURE;
                        SPPManager.getInstance().getMultipleBluetoothController().removeConnecting(SppBluetooth.this);
                        if (sppConnectCallback != null)
                            sppConnectCallback.onConnectFail(mDevice, new OtherException("Failed to obtain BluetoothSocket"));
                    }

                }
                break;
                case BleMsg.MSG_CONNECT_FAIL: {
                    disconnect();
                    refreshDeviceCache();
                    if (connectRetryCount < SPPManager.getInstance().getReConnectCount()) {
                        BleLog.e("Connect fail, try reconnect " + SPPManager.getInstance().getReConnectInterval() + " millisecond later");
                        ++connectRetryCount;

                        Message message = mainHandler.obtainMessage();
                        message.what = BleMsg.MSG_RECONNECT;
                        mainHandler.sendMessageDelayed(message, SPPManager.getInstance().getReConnectInterval());
                    } else {
                        lastState = BleBluetooth.LastState.CONNECT_FAILURE;
                        SPPManager.getInstance().getMultipleBluetoothController().removeConnecting(SppBluetooth.this);

                        BleConnectStateParameter para = (BleConnectStateParameter) msg.obj;
                        int status = para.getStatus();
                        if (sppConnectCallback != null)
                            sppConnectCallback.onConnectFail(mDevice, new BleException(BleException.ERROR_CODE_OTHER, "Connect fail") {
                            });
                    }
                }
                break;

                case BleMsg.MSG_DISCONNECTED: {
                    lastState = BleBluetooth.LastState.CONNECT_DISCONNECT;
                    SPPManager.getInstance().getMultipleBluetoothController().removeBluetooth(SppBluetooth.this);

                    disconnect();
                    refreshDeviceCache();
                    mainHandler.removeCallbacksAndMessages(null);

                    BleConnectStateParameter para = (BleConnectStateParameter) msg.obj;
                    boolean isActive = para.isActive();
                    int status = para.getStatus();
                    if (sppConnectCallback != null)
                        sppConnectCallback.onDisConnected(isActive, mDevice, mBluetoothSocket, status);
                }
                break;

                case BleMsg.MSG_RECONNECT: {
                    connect(mDevice, false, sppConnectCallback, connectRetryCount);
                }
                break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    public void run() {
        int available;
        while (!isInterrupted() && _socketConnected(mBluetoothSocket)) {
            try {
                /*if ((available = mInputStream.available()) > 0) {
                    byte[] frame = new byte[available];
                    mInputStream.read(frame, 0, available);
                    if(frame.length==2 && frame[1]==11)
                    {

                    }else {
                        if (sppConnectedCallback != null) {
                            sppConnectedCallback.onDataReceiving(mDevice,frame);
                        }
                    }
                }*/
                byte[] buffer = new byte[1024];
                if ((available = mInputStream.read(buffer)) > 0) {
                    if(available==2 && buffer[1]==11)
                    {

                    }else {
                        byte[] frame = new byte[available];
                        for (int i = 0; i < available; i++) {
                            frame[i]=buffer[i];
                        }
                        if (sppConnectCallback != null) {
                            sppConnectCallback.onDataReceiving(mDevice,frame);
                        }
                    }
                }
                //写入数据_tx(mBluetoothDevice, mOutputStream);
            } catch (IOException e) {
                Message message = mainHandler.obtainMessage();
                message.what = BleMsg.MSG_DISCONNECTED;
                BleConnectStateParameter para = new BleConnectStateParameter(-1);
                para.setActive(isActiveDisconnect);
                message.obj = para;
                mainHandler.sendMessage(message);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
        try {
            _socketClose(mBluetoothSocket);
        } catch (IOException e) {
        }
        Message message = mainHandler.obtainMessage();
        message.what = BleMsg.MSG_DISCONNECTED;
        BleConnectStateParameter para = new BleConnectStateParameter(1);
        para.setActive(isActiveDisconnect);
        message.obj = para;
        mainHandler.sendMessage(message);
    }

    private boolean _socketConnected(BluetoothSocket socket) {
        return socket != null && socket.isConnected();
    }

    private void _socketClose(BluetoothSocket socket) throws IOException {
        if (socket == null)
            return;
        if (socket.isConnected())
            socket.close();
    }
}
