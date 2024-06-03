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

public class SdpScanner {

    public static SdpScanner getInstance() {
        return SppScannerHolder.sSppScanner;
    }

    private static class SppScannerHolder {
        private static final SdpScanner sSppScanner = new SdpScanner();
    }

    private BleScanState mBleScanState = BleScanState.STATE_IDLE;

    private SppScanPresenter mSppScanPresenter =null;

    public void getSdpScanDiscovery(){
        if(mSppScanPresenter!=null)
            mSppScanPresenter=null;
        mSppScanPresenter = new SppScanPresenter() {

            @Override
            public void onScanStarted(boolean success) {
                BleScanPresenterImp callback = mSppScanPresenter.getBleScanPresenterImp();
                if (callback != null) {
                    callback.onScanStarted(success);
                }
            }

            @Override
            public void onLeScan(final BleDevice bleDevice) {
                final SppScanAndConnectCallback callback = (SppScanAndConnectCallback)
                        mSppScanPresenter.getBleScanPresenterImp();
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
            }

            @Override
            public void onScanning(BleDevice result) {
                BleScanPresenterImp callback = mSppScanPresenter.getBleScanPresenterImp();
                if (callback != null) {
                    callback.onScanning(result);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> bleDeviceList) {
                SppScanAndConnectCallback callback = (SppScanAndConnectCallback) mSppScanPresenter.getBleScanPresenterImp();
                if (callback != null) {
                    callback.onScanFinished(bleDeviceList);
                }
            }
        };
    }


    public void start(SppScanAndConnectCallback sdpCallback) {
        startProcess(sdpCallback);
    }

    private synchronized void startProcess( BleScanPresenterImp sdp) {

        if (mBleScanState != BleScanState.STATE_IDLE) {
            BleLog.w("scan action already exists, complete the previous scan action first");
            if (sdp != null) {
                sdp.onScanStarted(false);
            }
            return;
        }
        getSdpScanDiscovery();
        mSppScanPresenter.prepare(sdp);
        mBleScanState = BleScanState.STATE_SCANNING;
        mSppScanPresenter.notifyScanStarted(true);
    }

    public synchronized void stopProcess() {
        mBleScanState = BleScanState.STATE_IDLE;
        mSppScanPresenter.notifyScanStopped();
    }

    public BleScanState getScanState() {
        return mBleScanState;
    }
}
