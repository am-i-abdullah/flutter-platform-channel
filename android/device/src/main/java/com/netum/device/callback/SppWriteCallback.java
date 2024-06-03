package com.netum.device.callback;


import com.netum.device.exception.BleException;

public abstract class SppWriteCallback extends BleBaseCallback{

    public abstract void onWriteSuccess(int current, int total, byte[] justWrite);

    public abstract void onWriteFailure(BleException exception);

}
