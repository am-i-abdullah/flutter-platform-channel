package com.netum.example.common;


import com.netum.device.data.BleDevice;

public interface Observer {

    void disConnected(BleDevice bleDevice);
}
