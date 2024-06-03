package com.netum.device.scan;


import com.netum.device.BLEManager;
import com.netum.device.instruction.ServiceList;

import java.util.UUID;

public class ScanRuleConfig {

    private UUID[] mServiceUuids = null;
    private String[] mDeviceNames = null;
    private String mDeviceMac = null;
    private boolean mAutoConnect = false;
    private boolean mFuzzy = false;
    private boolean mFilter = false;
    private long mScanTimeOut = BLEManager.DEFAULT_SCAN_TIME;

    public UUID[] getServiceUuids() {
        return mServiceUuids;
    }

    public String[] getDeviceNames() {
        return mDeviceNames;
    }

    public String getDeviceMac() {
        return mDeviceMac;
    }

    public boolean isAutoConnect() {
        return mAutoConnect;
    }

    public boolean isFuzzy() {
        return mFuzzy;
    }

    public boolean isFilter() {
        return mFilter;
    }

    public long getScanTimeOut() {
        return mScanTimeOut;
    }

    public static class Builder {

        private UUID[] mServiceUuids = null;
        private String[] mDeviceNames = null;
        private String mDeviceMac = null;
        private boolean mAutoConnect = false;
        private boolean mFuzzy = false;
        private boolean mFilter = true;
        private long mTimeOut = BLEManager.DEFAULT_SCAN_TIME;

        /*public Builder setServiceUuids(UUID[] uuids) {
            this.mServiceUuids = uuids;
            return this;
        }*/

        public Builder setDeviceName(boolean fuzzy, String... name) {
            this.mFuzzy = fuzzy;
            this.mDeviceNames = name;
            return this;
        }

        public Builder setDeviceMac(String mac) {
            this.mDeviceMac = mac;
            return this;
        }

        public Builder setAutoConnect(boolean autoConnect) {
            this.mAutoConnect = autoConnect;
            return this;
        }

        public Builder setScanTimeOut(long timeOut) {
            this.mTimeOut = timeOut;
            return this;
        }

        public Builder setFilter(boolean filter) {
            this.mFilter = filter;
            return this;
        }

        void applyConfig(ScanRuleConfig config) {
            if(this.mServiceUuids==null)
            {
                this.mServiceUuids=new UUID[1];
                this.mServiceUuids[0]= ServiceList.BLEScanUUID1;
            }
            config.mServiceUuids = this.mServiceUuids;
            config.mDeviceNames = this.mDeviceNames;
            config.mDeviceMac = this.mDeviceMac;
            config.mAutoConnect = this.mAutoConnect;
            config.mFuzzy = this.mFuzzy;
            config.mFilter = this.mFilter;
            config.mScanTimeOut = this.mTimeOut;
        }

        public ScanRuleConfig build() {
            ScanRuleConfig config = new ScanRuleConfig();
            applyConfig(config);
            return config;
        }

    }


}
