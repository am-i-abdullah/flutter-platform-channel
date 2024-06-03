package com.netum.device;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Looper;

import com.netum.device.bluetooth.SppBluetooth;
import com.netum.device.bluetooth.MultipleBluetoothSPPController;
import com.netum.device.callback.BleScanCallback;
import com.netum.device.callback.BleWriteCallback;
import com.netum.device.callback.SppConnectCallback;
import com.netum.device.callback.SppScanAndConnectCallback;
import com.netum.device.data.BleDevice;
import com.netum.device.data.BleScanState;
import com.netum.device.exception.OtherException;
import com.netum.device.instruction.ServiceList;
import com.netum.device.scan.ScanRuleConfig;
import com.netum.device.scan.SdpScanner;
import com.netum.device.scan.SppScanner;
import com.netum.device.utils.BleLog;

import java.util.List;
import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SPPManager {

    private Application context;
    private ScanRuleConfig scanRuleConfig;
    private BluetoothAdapter bluetoothAdapter;
    private MultipleBluetoothSPPController multipleBluetoothController;
    private BluetoothManager bluetoothManager;

    public static final int DEFAULT_SCAN_TIME = 10000;
    private static final int DEFAULT_MAX_MULTIPLE_DEVICE = 7;
    private static final int DEFAULT_OPERATE_TIME = 5000;
    private static final int DEFAULT_CONNECT_RETRY_COUNT = 0;
    private static final int DEFAULT_CONNECT_RETRY_INTERVAL = 5000;
    private static final int DEFAULT_MTU = 23;
    private static final int DEFAULT_MAX_MTU = 512;
    private static final int DEFAULT_WRITE_DATA_SPLIT_COUNT = 20;
    private static final int DEFAULT_CONNECT_OVER_TIME = 10000;

    private int maxConnectCount = DEFAULT_MAX_MULTIPLE_DEVICE;
    private int operateTimeout = DEFAULT_OPERATE_TIME;
    private int reConnectCount = DEFAULT_CONNECT_RETRY_COUNT;
    private long reConnectInterval = DEFAULT_CONNECT_RETRY_INTERVAL;
    private int splitWriteNum = DEFAULT_WRITE_DATA_SPLIT_COUNT;
    private long connectOverTime = DEFAULT_CONNECT_OVER_TIME;

    public static SPPManager getInstance() {
        return SppManagerHolder.sBleManager;
    }

    private static class SppManagerHolder {
        private static final SPPManager sBleManager = new SPPManager();
    }

    public void init(Application app) {
        if (context == null && app != null) {
            context = app;
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            multipleBluetoothController = new MultipleBluetoothSPPController();
            scanRuleConfig = new ScanRuleConfig();
        }
    }

    /**
     * Get the Context
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get the BluetoothManager
     *
     * @return
     */
    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    /**
     * Get the BluetoothAdapter
     *
     * @return
     */
    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    /**
     * get the ScanRuleConfig
     *
     * @return
     */
    public ScanRuleConfig getScanRuleConfig() {
        return scanRuleConfig;
    }

    /**
     * Get the multiple Bluetooth Controller
     *
     * @return
     */
    public MultipleBluetoothSPPController getMultipleBluetoothController() {
        return multipleBluetoothController;
    }

    /**
     * Configure scan and connection properties
     *
     * @param config
     */
    public void initScanRule(ScanRuleConfig config) {
        this.scanRuleConfig = config;
    }

    /**
     * Get the maximum number of connections
     *
     * @return
     */
    public int getMaxConnectCount() {
        return maxConnectCount;
    }

    /**
     * Set the maximum number of connections
     *
     * @param count
     * @return BleManager
     */
    public SPPManager setMaxConnectCount(int count) {
        if (count > DEFAULT_MAX_MULTIPLE_DEVICE)
            count = DEFAULT_MAX_MULTIPLE_DEVICE;
        this.maxConnectCount = count;
        return this;
    }

    /**
     * Get operate timeout
     *
     * @return
     */
    public int getOperateTimeout() {
        return operateTimeout;
    }

    /**
     * Set operate timeout
     *
     * @param count
     * @return BleManager
     */
    public SPPManager setOperateTimeout(int count) {
        this.operateTimeout = count;
        return this;
    }

    /**
     * Get connect retry count
     *
     * @return
     */
    public int getReConnectCount() {
        return reConnectCount;
    }

    /**
     * Get connect retry interval
     *
     * @return
     */
    public long getReConnectInterval() {
        return reConnectInterval;
    }

    /**
     * Set connect retry count and interval
     *
     * @param count
     * @return BleManager
     */
    public SPPManager setReConnectCount(int count) {
        return setReConnectCount(count, DEFAULT_CONNECT_RETRY_INTERVAL);
    }

    /**
     * Set connect retry count and interval
     *
     * @param count
     * @return BleManager
     */
    public SPPManager setReConnectCount(int count, long interval) {
        if (count > 10)
            count = 10;
        if (interval < 0)
            interval = 0;
        this.reConnectCount = count;
        this.reConnectInterval = interval;
        return this;
    }

    /**
     * Get operate connect Over Time
     *
     * @return
     */
    public long getConnectOverTime() {
        return connectOverTime;
    }

    /**
     * Set connect Over Time
     *
     * @param time
     * @return BleManager
     */
    public SPPManager setConnectOverTime(long time) {
        if (time <= 0) {
            time = 100;
        }
        this.connectOverTime = time;
        return this;
    }

    /**
     * print log?
     *
     * @param enable
     * @return BleManager
     */
    public SPPManager enableLog(boolean enable) {
        BleLog.isPrint = enable;
        return this;
    }

    /**
     * Start Sdp Process
     * @param sdpCallback
     */
    public void startSdp(SppScanAndConnectCallback sdpCallback) {
        if (sdpCallback == null) {
            throw new IllegalArgumentException("SdpScanAndConnectCallback can not be Null!");
        }

        if (!isBlueEnable()) {
            BleLog.e("Bluetooth not enable!");
            sdpCallback.onScanStarted(false);
            return;
        }

        SdpScanner.getInstance().start(sdpCallback);
    }

    /**
     * Stop Sdp Process
     */
    public void stopSdp() {
        SdpScanner.getInstance().stopProcess();
    }

    /**
     * scan device around
     *
     * @param callback
     */
    public void scan(BleScanCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleScanCallback can not be Null!");
        }

        if (!isBlueEnable()) {
            BleLog.e("Bluetooth not enable!");
            callback.onScanStarted(false);
            return;
        }

        UUID[] serviceUUIDs = scanRuleConfig.getServiceUuids();
        String[] deviceNames = scanRuleConfig.getDeviceNames();
        String deviceMac = scanRuleConfig.getDeviceMac();
        boolean fuzzy = scanRuleConfig.isFuzzy();
        boolean filter= scanRuleConfig.isFilter();
        long timeOut = scanRuleConfig.getScanTimeOut();

        SppScanner.getInstance().scan(serviceUUIDs, deviceNames, deviceMac, fuzzy,filter, timeOut, callback);
    }

    /**
     * scan device then connect
     *
     * @param callback
     */
    public void scanAndConnect(SppScanAndConnectCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("SppScanAndConnectCallback can not be Null!");
        }

        if (!isBlueEnable()) {
            BleLog.e("Bluetooth not enable!");
            callback.onScanStarted(false);
            return;
        }

        UUID[] serviceUuids = scanRuleConfig.getServiceUuids();
        String[] deviceNames = scanRuleConfig.getDeviceNames();
        String deviceMac = scanRuleConfig.getDeviceMac();
        boolean fuzzy = scanRuleConfig.isFuzzy();
        boolean filter= scanRuleConfig.isFilter();
        long timeOut = scanRuleConfig.getScanTimeOut();

        SppScanner.getInstance().scanAndConnect(serviceUuids, deviceNames, deviceMac, fuzzy,filter, timeOut, callback);
    }


    /**
     * connect a known device
     *
     * @param bleDevice
     * @param sppConnectCallback
     * @return
     */
    public BluetoothSocket connect(BleDevice bleDevice, SppConnectCallback sppConnectCallback) {
        if (sppConnectCallback == null) {
            throw new IllegalArgumentException("BleGattCallback can not be Null!");
        }

        if (!isBlueEnable()) {
            BleLog.e("Bluetooth not enable!");
            sppConnectCallback.onConnectFail(bleDevice, new OtherException("Bluetooth not enable!"));
            return null;
        }

        if (Looper.myLooper() == null || Looper.myLooper() != Looper.getMainLooper()) {
            BleLog.w("Be careful: currentThread is not MainThread!");
        }

        if (bleDevice == null || bleDevice.getDevice() == null) {
            sppConnectCallback.onConnectFail(bleDevice, new OtherException("Not Found Device Exception Occurred!"));
        } else {
            SppBluetooth sppBluetooth = multipleBluetoothController.buildConnecting(bleDevice);
            boolean autoConnect = scanRuleConfig.isAutoConnect();
            return sppBluetooth.connect(bleDevice, autoConnect, sppConnectCallback);
        }

        return null;
    }

    /**
     * connect a device through its mac without scan,whether or not it has been connected
     *
     * @param mac
     * @param sppConnectCallback
     * @return
     */
    public BluetoothSocket connect(String mac, SppConnectCallback sppConnectCallback) {
        BluetoothDevice bluetoothDevice = getBluetoothAdapter().getRemoteDevice(mac);
        BleDevice bleDevice = new BleDevice(bluetoothDevice, 0, null, 0);
        return connect(bleDevice, sppConnectCallback);
    }


    /**
     * Cancel scan
     */
    public void cancelScan() {
        SppScanner.getInstance().stopLeScan();
    }

    public void ScannerCommand(BleDevice bleDevice,
                      byte[] data,
                      BleWriteCallback callback) {
        write(bleDevice,ServiceList.BLEServiceUUID.toString() , ServiceList.WriteUUID.toString(), data, true, callback);
    }

    /**
     * write
     *
     * @param bleDevice
     * @param uuid_service
     * @param uuid_write
     * @param data
     * @param callback
     */
    public void write(BleDevice bleDevice,
                      String uuid_service,
                      String uuid_write,
                      byte[] data,
                      BleWriteCallback callback) {
        write(bleDevice, uuid_service, uuid_write, data, true, callback);
    }

    /**
     * write
     *
     * @param bleDevice
     * @param uuid_service
     * @param uuid_write
     * @param data
     * @param split
     * @param callback
     */
    public void write(BleDevice bleDevice,
                      String uuid_service,
                      String uuid_write,
                      byte[] data,
                      boolean split,
                      BleWriteCallback callback) {

        write(bleDevice, uuid_service, uuid_write, data, split, true, 0, callback);
    }

    /**
     * write
     *
     * @param bleDevice
     * @param uuid_service
     * @param uuid_write
     * @param data
     * @param split
     * @param sendNextWhenLastSuccess
     * @param intervalBetweenTwoPackage
     * @param callback
     */
    public void write(BleDevice bleDevice,
                      String uuid_service,
                      String uuid_write,
                      byte[] data,
                      boolean split,
                      boolean sendNextWhenLastSuccess,
                      long intervalBetweenTwoPackage,
                      BleWriteCallback callback) {

        if (callback == null) {
            throw new IllegalArgumentException("BleWriteCallback can not be Null!");
        }

        if (data == null) {
            BleLog.e("data is Null!");
            callback.onWriteFailure(new OtherException("data is Null!"));
            return;
        }

        if (data.length > 20 && !split) {
            BleLog.w("Be careful: data's length beyond 20! Ensure MTU higher than 23, or use spilt write!");
        }

        SppBluetooth sppBluetooth = multipleBluetoothController.getBluetooth(bleDevice);
        if (sppBluetooth == null) {
            callback.onWriteFailure(new OtherException("This device not connect!"));
        } else {
            sppBluetooth.write(data, callback);
        }
    }

    /**
     * Open bluetooth
     */
    public void enableBluetooth() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.enable();
        }
    }

    /**
     * Disable bluetooth
     */
    public void disableBluetooth() {
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled())
                bluetoothAdapter.disable();
        }
    }

    /**
     * judge Bluetooth is enable
     *
     * @return
     */
    public boolean isBlueEnable() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     *
     * @param bluetoothDevice
     * @return
     */
    public BleDevice convertBleDevice(BluetoothDevice bluetoothDevice) {
        return new BleDevice(bluetoothDevice);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BleDevice convertBleDevice(ScanResult scanResult) {
        if (scanResult == null) {
            throw new IllegalArgumentException("scanResult can not be Null!");
        }
        BluetoothDevice bluetoothDevice = scanResult.getDevice();
        int rssi = scanResult.getRssi();
        ScanRecord scanRecord = scanResult.getScanRecord();
        byte[] bytes = null;
        if (scanRecord != null)
            bytes = scanRecord.getBytes();
        long timestampNanos = scanResult.getTimestampNanos();
        return new BleDevice(bluetoothDevice, rssi, bytes, timestampNanos);
    }

    public SppBluetooth getBleBluetooth(BleDevice bleDevice) {
        if (multipleBluetoothController != null) {
            return multipleBluetoothController.getBluetooth(bleDevice);
        }
        return null;
    }

    public BluetoothSocket getBluetoothSocket(BleDevice bleDevice) {
        SppBluetooth bleBluetooth = getBleBluetooth(bleDevice);
        if (bleBluetooth != null)
            return bleBluetooth.getBluetoothSocket();
        return null;
    }

    public BleScanState getScanSate() {
        return SppScanner.getInstance().getScanState();
    }

    public BleScanState getSdpState() {
        return SdpScanner.getInstance().getScanState();
    }

    public List<BleDevice> getAllConnectedDevice() {
        if (multipleBluetoothController == null)
            return null;
        return multipleBluetoothController.getDeviceList();
    }

    public boolean isConnected(BleDevice bleDevice) {
        BluetoothSocket bluetoothSocket=getBluetoothSocket(bleDevice);
        if (bluetoothSocket != null)
            return bluetoothSocket.isConnected();
        return false;
    }

    public boolean isConnected(String mac) {
        List<BleDevice> list = getAllConnectedDevice();
        for (BleDevice bleDevice : list) {
            if (bleDevice != null) {
                if (bleDevice.getMac().equals(mac)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void disconnect(BleDevice bleDevice) {
        if (multipleBluetoothController != null) {
            multipleBluetoothController.disconnect(bleDevice);
        }
    }

    public void disconnectAllDevice() {
        if (multipleBluetoothController != null) {
            multipleBluetoothController.disconnectAllDevice();
        }
    }

    public void destroy() {
        if (multipleBluetoothController != null) {
            multipleBluetoothController.destroy();
        }
    }


}
