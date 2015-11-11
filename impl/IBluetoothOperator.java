package com.zenmat.mobile.demo.zenmat.impl;

import android.bluetooth.BluetoothDevice;

/**
 * Created by CgTy on 5.3.2015.
 */
public interface IBluetoothOperator {
    void startSearch();
    void stopSearch();
    boolean connect(BluetoothDevice device);
    void disconnect(BluetoothDevice device);
}
