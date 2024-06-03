package com.netum.device.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.netum.device.SPPManager;
import com.netum.device.data.BleDevice;
import com.netum.device.utils.SppLruHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleBluetoothSPPController {
    private final SppLruHashMap<String, SppBluetooth> sppLruHashMap;
    private final HashMap<String, SppBluetooth> sppTempHashMap;


    public MultipleBluetoothSPPController() {
        sppLruHashMap=new SppLruHashMap<>(SPPManager.getInstance().getMaxConnectCount());
        sppTempHashMap= new HashMap<>();
    }

    public synchronized SppBluetooth buildConnecting(BleDevice bleDevice) {
        SppBluetooth sppBluetooth = new SppBluetooth(bleDevice);
        if (!sppTempHashMap.containsKey(sppBluetooth.getDeviceKey())) {
            sppTempHashMap.put(sppBluetooth.getDeviceKey(), sppBluetooth);
        }
        return sppBluetooth;
    }

    public synchronized void removeConnecting(SppBluetooth sppBluetooth) {
        if (sppBluetooth == null) {
            return;
        }
        if (sppTempHashMap.containsKey(sppBluetooth.getDeviceKey())) {
            sppTempHashMap.remove(sppBluetooth.getDeviceKey());
        }
    }

    public synchronized void addBluetooth(SppBluetooth sppBluetooth) {
        if (sppBluetooth == null) {
            return;
        }
        if (!sppLruHashMap.containsKey(sppBluetooth.getDeviceKey())) {
            sppLruHashMap.put(sppBluetooth.getDeviceKey(), sppBluetooth);
        }
    }

    public synchronized void removeBluetooth(SppBluetooth sppBluetooth) {
        if (sppBluetooth == null) {
            return;
        }
        if (sppLruHashMap.containsKey(sppBluetooth.getDeviceKey())) {
            sppLruHashMap.remove(sppBluetooth.getDeviceKey());
        }
    }

    public synchronized boolean isContainDevice(BleDevice bleDevice) {
        return bleDevice != null && sppLruHashMap.containsKey(bleDevice.getKey());
    }

    public synchronized boolean isContainDevice(BluetoothDevice bluetoothDevice) {
        return bluetoothDevice != null && sppLruHashMap.containsKey(bluetoothDevice.getName() + bluetoothDevice.getAddress());
    }

    public synchronized SppBluetooth getBluetooth(BleDevice bleDevice) {
        if (bleDevice != null) {
            if (sppLruHashMap.containsKey(bleDevice.getKey())) {
                return sppLruHashMap.get(bleDevice.getKey());
            }
        }
        return null;
    }

    public synchronized void disconnect(BleDevice bleDevice) {
        if (isContainDevice(bleDevice)) {
            getBluetooth(bleDevice).disconnect();
        }
    }

    public synchronized void disconnectAllDevice() {
        for (Map.Entry<String, SppBluetooth> stringSppBluetoothEntry : sppLruHashMap.entrySet()) {
            stringSppBluetoothEntry.getValue().disconnect();
        }
        sppLruHashMap.clear();
    }

    public synchronized void destroy() {
        for (Map.Entry<String, SppBluetooth> stringSppBluetoothEntry : sppLruHashMap.entrySet()) {
            stringSppBluetoothEntry.getValue().destroy();
        }
        sppLruHashMap.clear();
        for (Map.Entry<String, SppBluetooth> stringSppBluetoothEntry : sppTempHashMap.entrySet()) {
            stringSppBluetoothEntry.getValue().destroy();
        }
        sppTempHashMap.clear();
    }

    public synchronized List<SppBluetooth> getBluetoothList() {
        List<SppBluetooth> sppBluetoothList = new ArrayList<>(sppLruHashMap.values());
        Collections.sort(sppBluetoothList, new Comparator<SppBluetooth>() {
            @Override
            public int compare(SppBluetooth lhs, SppBluetooth rhs) {
                return lhs.getDeviceKey().compareToIgnoreCase(rhs.getDeviceKey());
            }
        });
        return sppBluetoothList;
    }

    public synchronized List<BleDevice> getDeviceList() {
        refreshConnectedDevice();
        List<BleDevice> deviceList = new ArrayList<>();
        for (SppBluetooth sppBluetooth : getBluetoothList()) {
            if (sppBluetooth != null) {
                deviceList.add(sppBluetooth.getDevice());
            }
        }
        return deviceList;
    }

    public void refreshConnectedDevice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<SppBluetooth> bluetoothList = getBluetoothList();
            for (int i = 0; bluetoothList != null && i < bluetoothList.size(); i++) {
                SppBluetooth sppBluetooth = bluetoothList.get(i);
                if (!SPPManager.getInstance().isConnected(sppBluetooth.getDevice())) {
                    removeBluetooth(sppBluetooth);
                }
            }
        }
    }

}
