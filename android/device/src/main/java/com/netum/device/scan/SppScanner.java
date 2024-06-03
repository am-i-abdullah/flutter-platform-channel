package com.netum.device.scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;

import com.netum.device.SPPManager;
import com.netum.device.callback.BleScanCallback;
import com.netum.device.callback.BleScanPresenterImp;
import com.netum.device.callback.SppScanAndConnectCallback;
import com.netum.device.data.BleDevice;
import com.netum.device.data.BleScanState;
import com.netum.device.utils.BleLog;

import java.util.List;
import java.util.UUID;

public class SppScanner {

    public static SppScanner getInstance() {
        return SppScannerHolder.sSppScanner;
    }

    private static class SppScannerHolder {
        private static final SppScanner sSppScanner = new SppScanner();
    }

    private BleScanState mBleScanState = BleScanState.STATE_IDLE;

    private boolean scanIsRegister=false;
    private final SppScanReceiver mSppScanReceiver = new SppScanReceiver() {

        @Override
        public void onScanStarted(boolean success) {
            BleScanPresenterImp callback = mSppScanReceiver.getBleScanPresenterImp();
            if (callback != null) {
                callback.onScanStarted(success);
            }
        }

        @Override
        public void onLeScan(final BleDevice bleDevice) {
            ParcelUuid[] uuid= bleDevice.getDevice().getUuids();
            if (mSppScanReceiver.ismNeedConnect()) {
                final SppScanAndConnectCallback callback = (SppScanAndConnectCallback)
                        mSppScanReceiver.getBleScanPresenterImp();
                if (callback != null) {
                    callback.onLeScan(bleDevice);
                    if(bleDevice.getRssi()==0) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SPPManager.getInstance().connect(bleDevice, callback);
                            }
                        }, 100);
                    }
                }
            } else {
                BleScanCallback callback = (BleScanCallback) mSppScanReceiver.getBleScanPresenterImp();
                if (callback != null) {
                    callback.onLeScan(bleDevice);
                }
            }
        }

        @Override
        public void onScanning(BleDevice result) {
            BleScanPresenterImp callback = mSppScanReceiver.getBleScanPresenterImp();
            if (callback != null) {
                callback.onScanning(result);
            }
        }

        @Override
        public void onScanFinished(List<BleDevice> bleDeviceList) {
            if (mSppScanReceiver.ismNeedConnect()) {
                final SppScanAndConnectCallback callback = (SppScanAndConnectCallback)
                        mSppScanReceiver.getBleScanPresenterImp();
                if (bleDeviceList == null || bleDeviceList.size() < 1) {
                    if (callback != null) {
                        callback.onScanFinished(null);
                    }
                } else {
                    if (callback != null) {
                        callback.onScanFinished(bleDeviceList);
                    }
                    final List<BleDevice> list = bleDeviceList;
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            SPPManager.getInstance().connect(list.get(0), callback);
                        }
                    }, 100);
                }
            } else {
                BleScanCallback callback = (BleScanCallback) mSppScanReceiver.getBleScanPresenterImp();
                if (callback != null) {
                    callback.onScanFinished(bleDeviceList);
                }
            }
        }
    };


    public void scan(UUID[] serviceUUIDs, String[] names, String mac, boolean fuzzy, boolean filter,
                     long timeOut, final BleScanCallback callback) {

        startLeScan(serviceUUIDs, names, mac, fuzzy,filter,false, timeOut, callback);
    }

    public void scanAndConnect(UUID[] serviceUUIDs, String[] names, String mac, boolean fuzzy,boolean filter,
                               long timeOut, SppScanAndConnectCallback callback) {

        startLeScan(serviceUUIDs, names, mac, fuzzy,filter,true, timeOut, callback);
    }

    private synchronized void startLeScan(UUID[] serviceUUIDs, String[] names, String mac, boolean fuzzy,boolean filter,
                                          boolean needConnect, long timeOut, BleScanPresenterImp imp) {

        if (mBleScanState != BleScanState.STATE_IDLE) {
            BleLog.w("scan action already exists, complete the previous scan action first");
            if (imp != null) {
                imp.onScanStarted(false);
            }
            return;
        }
        mSppScanReceiver.prepare(names, mac, fuzzy,filter, needConnect, timeOut, imp);

        SPPManager.getInstance().getBluetoothAdapter().startDiscovery();
        IntentFilter filters = new IntentFilter();
        filters.addAction(BluetoothDevice.ACTION_FOUND);
        filters.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filters.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filters.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filters.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        SPPManager.getInstance().getContext().registerReceiver(mSppScanReceiver, filters);
        scanIsRegister=true;
        mBleScanState = BleScanState.STATE_SCANNING;
        mSppScanReceiver.notifyScanStarted(true);
    }

    public synchronized void stopLeScan() {
        if(scanIsRegister)
            SPPManager.getInstance().getContext().unregisterReceiver(mSppScanReceiver);
        scanIsRegister=false;
        mBleScanState = BleScanState.STATE_IDLE;
        mSppScanReceiver.notifyScanStopped();
    }

    public BleScanState getScanState() {
        return mBleScanState;
    }
}
