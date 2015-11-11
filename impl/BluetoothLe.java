package com.zenmat.mobile.demo.zenmat.impl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;


import java.util.Iterator;
import java.util.List;
import java.util.UUID;


/**
 * This Class contains Bluetooth Low Energy methods.
 */
class BluetoothLe extends AbstractBluetoothBase implements IBluetoothOperator {
    private final BluetoothLeScanCallback mBluetoothLeScanCallback;
    private static final String TAG = "LowEnergyBt";
    protected BluetoothGatt mBluetoothGatt;
    private final IBluetoothLeEventCallback mBleEventCallback;
    private IMatDiscoveryEventCallback mMatDiscoveryEventCallback;

    //==============================================================================
    // Constructor
    //==============================================================================
    BluetoothLe(Context context, IBluetoothLeEventCallback callback, IMatDiscoveryEventCallback matDiscoveryEventCallback) {
        this.mMatDiscoveryEventCallback = matDiscoveryEventCallback;
        this.context = context;
        mBleEventCallback = callback;
        initBluetoothAdapter();
        mBluetoothLeScanCallback = new BluetoothLeScanCallback(this);
    }

    //==============================================================================
    // BLE Scan
    //==============================================================================
    /**
     * Starts Bluetooth Low Energy scanning for any BLE devices in range.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startSearch() {
        // Clear the found device list set.
        getCustomLeScanCallBack().getDevList().clear();
        enableBluetoothAdapter(getBluetoothAdapter());
        getBluetoothAdapter().startLeScan(getCustomLeScanCallBack());
    }

    /**
     * Stops BLE scanning
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopSearch() {
        if (Utils.ApiInformer.isDeviceAndroidApiLevelMin18()) {
            getBluetoothAdapter().stopLeScan(mBluetoothLeScanCallback);
            Log.d(TAG, "Stopped BLE scanning.");
        }
    }

    //==============================================================================
    // BLE Connect & Disconnect
    //==============================================================================
    /**
     * Connects to the remote device through Bluetooth Low Energy
     * @param device Remote Bluetooth device
     * @return <code>true</code>  if the connection is established
     * <code>false</code> otherwise.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connect(BluetoothDevice device) {
        boolean result;
        // connect from scratches
        BluetoothLeGattCallback bluetoothLeGattCallback = new BluetoothLeGattCallback(this);
        mBluetoothGatt = device.connectGatt(getContext(), false, bluetoothLeGattCallback);
        result = mBluetoothGatt != null;
        return result;
    }

    /**
     * Disconnects the device. It is still possible to reconnect to it later with this Gatt client
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnect(BluetoothDevice device) {
        int connState = BluetoothLe.getBluetoothManager(getContext()).getConnectionState(device, BluetoothProfile.GATT);
        if (connState == BluetoothProfile.STATE_CONNECTED) {
            mBluetoothGatt.disconnect();
        }
    }

    //==============================================================================
    // BLE Services
    //==============================================================================
    /* Request to discover all services available on the remote devices
    *  results are delivered through callback object (CustomBluetoothGattCallback)
    */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    void startServicesDiscovery() throws ZenMatErrorType.Internal.GattNullInConnectedState {
        if (mBluetoothGatt != null && getBluetoothAdapter() != null) {
            mBluetoothGatt.discoverServices();
        } else {
            throw new ZenMatErrorType.Internal.GattNullInConnectedState();
        }
    }

    /**
     * Returns Bluetooth GATT services. If the GATT is not connected or service discovery not performed,
     * it will return and empty list.
     * @return GATT Services-
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static List<BluetoothGattService> getBluetoothGattServices(BluetoothGatt gatt) throws ZenMatErrorType.Internal.GattNullInConnectedState {
        if (gatt != null) {
            return gatt.getServices();
        } else {
            throw new ZenMatErrorType.Internal.GattNullInConnectedState();
        }
    }

    //==============================================================================
    // BLE Characteristic
    //==============================================================================
    /**
     * Returns characteristic matching the Service UUID and the Characteristic UUID
     * @param servUuid UUID of the service that characteristic belongs
     * @param charUuid UUID of the characteristic
     * @return characteristic. Null if not found!
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    BluetoothGattCharacteristic getCharacteristicWithUuid(UUID servUuid, UUID charUuid) throws ZenMatErrorType.Internal.GattNullInConnectedState, ZenMatErrorType.Internal.RequestedServiceNotDiscovered, ZenMatErrorType.Internal.RequestedCharacteristicNotDiscovered {
        if (mBluetoothGatt == null) {
            throw new ZenMatErrorType.Internal.GattNullInConnectedState();
        }
        BluetoothGattService service =  mBluetoothGatt.getService(servUuid);
        if (service == null) {
            throw new ZenMatErrorType.Internal.RequestedServiceNotDiscovered();
        }
        BluetoothGattCharacteristic ch = service.getCharacteristic(charUuid);
        if (ch == null) {
            throw new ZenMatErrorType.Internal.RequestedCharacteristicNotDiscovered();
        }
        return ch;
    }

    /**
     * write the Client Characteristic Configuration Descriptor (CCCD) for
     * enabling/disabling notifications/indications to a specific
     * characteristic.
     *
     * @param ch
     *            the characteristic to write the CCCD descriptor to
     * @param enabled
     *            false to disable notifications/indications, true to enable
     *            them
     * @return boolean false if CCCD descriptor was not found or if the
     *         operation was not initiated successful, otherwise returns true
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean enableNotificationOrIndicationForCharacteristic(BluetoothGattCharacteristic ch, boolean enabled) throws ZenMatErrorType.BluetoothError.CharacteristicNotificationEnableError {
        // see:
        // https://developer.bluetooth.org/gatt/descriptors/Pages/DescriptorViewer.aspx?u=org.bluetooth.descriptor.gatt.client_characteristic_configuration.xml
        BluetoothGattDescriptor descriptor = ch.getDescriptor(ZenMatProfile.Descriptor.getCharacteristicClientConfigDesc());
        if (descriptor == null) {
            return false;
        }
        int properties = ch.getProperties();
        if ((BluetoothGattCharacteristic.PROPERTY_NOTIFY & properties) != 0) {
            // set notifications, heart rate measurement etc
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            boolean descrSuccess = descriptor.setValue(val);
            Log.i(TAG, "NOTIFY: " + descrSuccess);
        }
        else if ((BluetoothGattCharacteristic.PROPERTY_INDICATE & properties) != 0) {
            // set notifications, temperature measurement etc
            byte[] val = enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            boolean descrSuccess = descriptor.setValue(val);
            Log.i(TAG, "INDICATE: " + descrSuccess);
        }
        boolean success = mBluetoothGatt.writeDescriptor(descriptor);
        Log.i(TAG, "writeDescriptor success: " + success);
        return success;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    static boolean isServiceIncluded(BluetoothGatt gatt, UUID servUUID) throws ZenMatErrorType.Internal.GattNullInConnectedState {
        List<BluetoothGattService> services = getBluetoothGattServices(gatt);
        Iterator<BluetoothGattService> iterator = services.iterator();
        while (iterator.hasNext()) {
            BluetoothGattService service = iterator.next();
            if (service.getUuid().equals(servUUID)) {
                return true;
            }
        }
        return false;
    }

    /*
     * <p> <b>Note:</b> if the characteristic supports Notifications and
     * Indications then automatically only Notifications will be enabled and
     * Indications will be ignored. <br>If we need to define what to enable then
     * one way would be to cre ate separate methods called
     * "setCharacteristicNotification" and "setCharacteristicIndication"
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected boolean setCharacteristicNotificationOrIndication(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (mBluetoothGatt == null) {
            throw new NullPointerException("mBluetoothGatt object is null!");
        }
        boolean isSettingNotificationLocallySuccess = mBluetoothGatt.setCharacteristicNotification(characteristic, enable);
        if (isSettingNotificationLocallySuccess) {
            try {
                return enableNotificationOrIndicationForCharacteristic(characteristic, enable);
            } catch (ZenMatErrorType.BluetoothError.CharacteristicNotificationEnableError characteristicNotificationEnableError) {
                characteristicNotificationEnableError.printStackTrace();
            }
        }
        return false;
    }

    //==============================================================================
    // Getters & Setters
    //==============================================================================
    BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }
    private BluetoothLeScanCallback getCustomLeScanCallBack() {
        return mBluetoothLeScanCallback;
    }

    IBluetoothLeEventCallback getBleEventCallback() {
        return mBleEventCallback;
    }

    IMatDiscoveryEventCallback getLockDiscoveryEventCallback() {
        return mMatDiscoveryEventCallback;
    }

}
