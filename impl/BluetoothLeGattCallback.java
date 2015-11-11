package com.zenmat.mobile.demo.zenmat.impl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.util.Log;

import java.util.List;

/**
 * This class is used for customizing the default BluetoothGattCallback behavior.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class BluetoothLeGattCallback extends BluetoothGattCallback {

    private static final String TAG = "BluetoothGattCallback";
    private final BluetoothLe mBluetoothLe;

    BluetoothLeGattCallback(BluetoothLe le) {
        mBluetoothLe = le;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTING:
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    try {
                        getBluetoothLe().startServicesDiscovery();
                        getBluetoothLe().getBleEventCallback().onGattConnected(gatt);
                    } catch (ZenMatErrorType.Internal.GattNullInConnectedState gattNullInConnectedState) {
                        getBluetoothLe().getBleEventCallback().onGattConnectionSetupFail(gatt, status);
                    }
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    if(getBluetoothGatt() != null) {
                        getBluetoothGatt().close();
                    }
                    getBluetoothLe().getBleEventCallback().onGattDisconnected(gatt, status);
                    break;
            }
        } else {
            getBluetoothLe().getBleEventCallback().onGattConnectionSetupFail(gatt, status);
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "Services Discovery Success");
            findServicesAndCharacteristics();
            getBluetoothLe().getBleEventCallback().onServicesDiscovered(gatt);
        } else {
            Log.w(TAG, "Service Discovery Error");
            getBluetoothLe().getBleEventCallback().onServiceDiscoveryFail(gatt, status);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "Notification Received!");
        getBluetoothLe().getBleEventCallback().onCharacteristicNotificationReceived(gatt, characteristic);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        Log.d(TAG, "onDescriptorWrite");
        switch (status) {
            case BluetoothGatt.GATT_SUCCESS:
                getBluetoothLe().getBleEventCallback().onCharacteristicNotificationEnabled(gatt, descriptor);
                break;
            case BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION:
            case BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION:
                // bonds automatically
                Log.w(TAG, "Failed to write descriptor because of "
                        + "GATT_INSUFFICIENT_AUTHENTICATION" + " and "
                        + "GATT_INSUFFICIENT_ENCRYPTION: " + descriptor.getUuid());
                getBluetoothLe().getBleEventCallback().onCharacteristicNotificationEnablingFail(gatt, descriptor, status);
                break;
            default:
                Log.w(TAG, "Failed to write descriptor: " + descriptor.getUuid());
                getBluetoothLe().getBleEventCallback().onCharacteristicNotificationEnablingFail(gatt, descriptor, status);
                break;
        }
    }

    protected void findServicesAndCharacteristics() {
        if (getBluetoothGatt() == null) {
            Log.w(TAG, "mBluetoothGatt is null");
            return;
        }
        List<BluetoothGattService> services = getBluetoothGatt().getServices();
        for (int i = 0; i < services.size(); i++) {
            onServiceFound(services.get(i));
            Log.d(TAG, "Service UUID: " + services.get(i).getUuid().toString());
            List<BluetoothGattCharacteristic> characteristics = services.get(i).getCharacteristics();
            for (int j = 0; j < characteristics.size(); j++) {
                onCharFound(characteristics.get(j));
            }
        }
        onCharsFoundCompleted();
    }
    /**
     * Callback that gets called every time a characteristic is found
     *
     * @param characteristic
     *            the characteristic that was found
     */
    protected void onCharFound(final BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "Char UUID:" + characteristic.getUuid());
    }

    /**
     * Callback that gets called whenever a service is found
     *
     * @param service
     */
    protected void onServiceFound(final BluetoothGattService service) {
        Log.d(TAG, "Service UUID:" + service.getUuid());
    }

    /**
     * called once the {@link BluetoothLeGattCallback#findServicesAndCharacteristics()}
     * method is completed
     */
    protected void onCharsFoundCompleted() {
        Log.d(TAG, "OnCharsFoundCompleted");
    }

    private BluetoothGatt getBluetoothGatt() {
        return getBluetoothLe().getBluetoothGatt();
    }

    private BluetoothLe getBluetoothLe() {
        return mBluetoothLe;
    }
}

