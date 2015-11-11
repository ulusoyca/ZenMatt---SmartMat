package com.zenmat.mobile.demo.zenmat.impl;
import android.bluetooth.BluetoothDevice;

/**
 * Created by Cagatay on 11/28/2014.
 */
public class Mat {
    private BluetoothDevice device;

    private volatile int hashCode = 0;
    public Mat(BluetoothDevice device) {
        this.device = device;
    }
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if (!(obj instanceof Mat)) {
            return false;
        }
        Mat lock = (Mat) obj;
        return device.getAddress().equals(lock.getDevice().getAddress());
    }
    public int hashCode () {
        final int multiplier = 11;
        if (hashCode == 0) {
            int code = 121;
            code = multiplier * code + device.getAddress().hashCode();
            hashCode = code;
        }
        return hashCode;
    }
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
    public BluetoothDevice getDevice() {
        return device;
    }
}
