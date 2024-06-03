package com.netum.device.callback;

import com.netum.device.data.BleDevice;

import java.util.List;

public abstract class SppScanAndConnectCallback extends SppConnectCallback implements BleScanPresenterImp {
    public abstract void onScanFinished(List<BleDevice> scanResultList);

    public void onLeScan(BleDevice bleDevice) {
    }
}
