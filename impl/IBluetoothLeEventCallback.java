package com.zenmat.mobile.demo.zenmat.impl;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;


/**
 * Created by CgTy on 4.9.2015.
 */
interface IBluetoothLeEventCallback extends IMatDiscoveryEventCallback {
    void onGattConnected(BluetoothGatt gatt);
    void onGattDisconnected(BluetoothGatt gatt, int status);
    void onGattConnectionSetupFail(BluetoothGatt gatt, int status);
    void onServicesDiscovered(BluetoothGatt gatt);
    void onServiceDiscoveryFail(BluetoothGatt gatt, int status);
    void onCharacteristicNotificationReceived(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    void onCharacteristicNotificationFail(BluetoothGatt gatt, Exception e);
    void onCharacteristicNotificationEnabled(BluetoothGatt gatt, BluetoothGattDescriptor descriptor);
    void onCharacteristicNotificationEnablingFail(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
}
