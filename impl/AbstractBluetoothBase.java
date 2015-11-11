package com.zenmat.mobile.demo.zenmat.impl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import com.zenmat.mobile.demo.zenmat.impl.Utils;


/**
 * An abstract class that contains common Bluetooth functions for Classic and
 * Low Energy Bluetooth.
 * @author Lukoton - Cagatay Ulusoy
 */
abstract class AbstractBluetoothBase {
    private BluetoothAdapter mBluetoothAdapter;
    protected Context context;
    static String localBluetoothAddress;

    /**
     * Returns <code>true</true> if Bluetooth Hardware is available for this device.
     * @return <code>true</true> if Bluetooth hardware is available;
     *         <code>false</true> otherwise
     */
    static boolean deviceHasBluetoothHardware(Context context) {
        return getBluetoothManager(context) != null;
    }

    /**
     * Enables bluetooth adapter of the Android device programmatically.
     * @param mBluetoothAdapter the bluetooth adapter instance
     */
    protected void enableBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        initBluetoothAdapter();
        if (mBluetoothAdapter != null && !(mBluetoothAdapter.isEnabled())) {
            mBluetoothAdapter.enable();
        }
    }

    /**
     * Instantiates the Bluetooth adapter instance if null and enables the adapter programmatically.
     * @return <code>true</true> If the BluetoothAdapter is not null and initialized;
     *         <code>false</true> otherwise
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected BluetoothAdapter initBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            if (Utils.ApiInformer.isDeviceAndroidApiLevelMin18()) {
                BluetoothManager btm = getBluetoothManager(context);
                mBluetoothAdapter = btm != null ? btm.getAdapter() : null;
            } else {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            enableBluetoothAdapter(mBluetoothAdapter);
            localBluetoothAddress = mBluetoothAdapter.getAddress();
        }
        return mBluetoothAdapter;
    }

    /**
     * Gets bluetooth adapter instance. If the adapter is null, then it initializes new instance
     * @return the bluetooth adapter
     */
    protected BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter != null) {
            return mBluetoothAdapter;
        } else {
            return initBluetoothAdapter();
        }
    }

    /**
     * Gets context that initializes the Bluetooth instance.
     * @return the context
     * @throws NullPointerException the null pointer exception
     */
    Context getContext() {
        return context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static BluetoothManager getBluetoothManager(Context context) {
        if (Utils.ApiInformer.isDeviceAndroidApiLevelMin18()) {
            return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        } else {
            return null;
        }
    }
}
