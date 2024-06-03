package com.netum.device.scan;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.netum.device.SPPManager;
import com.netum.device.callback.BleScanPresenterImp;
import com.netum.device.data.BleDevice;
import com.netum.device.data.BleMsg;
import com.netum.device.instruction.ServiceList;
import com.netum.device.utils.BleLog;
import com.netum.device.utils.HexUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class SppScanPresenter extends Thread {

    private BleScanPresenterImp mBleScanPresenterImp;


    private final List<BleDevice> mBleDeviceList = new ArrayList<>();

    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private boolean mHandling;

    private static final class ScanHandler extends Handler {

        private final WeakReference<SppScanPresenter> mSdpScanDiscovery;

        ScanHandler(Looper looper, SppScanPresenter sppScanPresenter) {
            super(looper);
            mSdpScanDiscovery = new WeakReference<>(sppScanPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            SppScanPresenter sppScanPresenter = mSdpScanDiscovery.get();
            if (sppScanPresenter != null) {
                if (msg.what == BleMsg.MSG_SCAN_DEVICE) {
                    final BleDevice bleDevice = (BleDevice) msg.obj;
                    if (bleDevice != null) {
                        sppScanPresenter.handleResult(bleDevice);
                    }
                }
            }
        }
    }

    private void handleResult(final BleDevice bleDevice) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onLeScan(bleDevice);
            }
        });
        checkDevice(bleDevice);
    }

    public void prepare(BleScanPresenterImp bleScanPresenterImp) {
        mBleScanPresenterImp = bleScanPresenterImp;

        mHandlerThread = new HandlerThread(BleScanPresenter.class.getSimpleName());
        mHandlerThread.start();
        mHandler = new ScanHandler(mHandlerThread.getLooper(), this);
        mHandling = true;
        try {
            bluetoothServerSocket= SPPManager.getInstance().getBluetoothAdapter().listenUsingRfcommWithServiceRecord("SDPService", ServiceList.SPPServiceUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BluetoothSocket mSocket= null;
    }

    public BleScanPresenterImp getBleScanPresenterImp() {
        return mBleScanPresenterImp;
    }

    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (device == null)
            return;

        if (!mHandling)
            return;

        Message message = mHandler.obtainMessage();
        message.what = BleMsg.MSG_SCAN_DEVICE;
        message.obj = new BleDevice(device, rssi, scanRecord, System.currentTimeMillis());
        mHandler.sendMessage(message);
    }



    private void checkDevice(BleDevice bleDevice) {
        correctDeviceAndNextStep(bleDevice);
    }

    private void correctDeviceAndNextStep(final BleDevice bleDevice) {
        AtomicBoolean hasFound = new AtomicBoolean(false);
        for (BleDevice result : mBleDeviceList) {
            if (result.getDevice().equals(bleDevice.getDevice())) {
                hasFound.set(true);
            }
        }
        if (!hasFound.get()) {
            BleLog.i("device detected  ------"
                    + "  name: " + bleDevice.getName()
                    + "  mac: " + bleDevice.getMac()
                    + "  Rssi: " + bleDevice.getRssi()
                    + "  scanRecord: " + HexUtil.formatHexString(bleDevice.getScanRecord(), true));

            mBleDeviceList.add(bleDevice);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onScanning(bleDevice);
                }
            });
        }
    }

    public final void notifyScanStarted(final boolean success) {
        mBleDeviceList.clear();
        removeHandlerMsg();

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onScanStarted(success);
            }
        });
        start();
    }

    public final void notifyScanStopped() {
        try {
            bluetoothServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandling = false;
        mHandlerThread.quit();
        removeHandlerMsg();
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onScanFinished(mBleDeviceList);
            }
        });
    }

    public final void removeHandlerMsg() {
        mMainHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }

    public abstract void onScanStarted(boolean success);

    public abstract void onLeScan(BleDevice bleDevice);

    public abstract void onScanning(BleDevice bleDevice);

    public abstract void onScanFinished(List<BleDevice> bleDeviceList);


    BluetoothServerSocket bluetoothServerSocket;

    @Override
    public void run() {
        while (!isInterrupted() && mHandling) {
            BluetoothSocket mSocket = null;
            try {
                mSocket = bluetoothServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(mSocket!=null)
            {
                BluetoothDevice device=mSocket.getRemoteDevice();
                if(mSocket.isConnected())
                {
                    try {
                        mSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onLeScan(device, 0, null);
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
        }
    }
}
