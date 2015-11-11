package com.zenmat.mobile.demo.zenmat.impl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;


class ZenMatControllerImpl implements IMatController, IBluetoothLeEventCallback, IMatDiscoveryEventCallback {

    private final String TAG = "MatControllerImpl";
    private IMatDiscoveryEventCallback mIZenMatDiscoveryEventCallback;
    private Context mContext;
    private IBluetoothOperator mBluetoothOperator;
    private IMatEventCallback mIMatEventCallback;

    //==============================================================================
    // Constructor
    //==============================================================================
    ZenMatControllerImpl(Context context, IMatDiscoveryEventCallback iDiscovery, IMatEventCallback iMatController) throws ZenMatErrorType.BluetoothError.BluetoothNotSupported, ZenMatErrorType.BluetoothError.BluetoothLeNotSupported {
        mContext = context;
        this.mIZenMatDiscoveryEventCallback = iDiscovery;
        this.mIMatEventCallback = iMatController;
        if (AbstractBluetoothBase.deviceHasBluetoothHardware(context)) {
            if (Utils.ApiInformer.isDeviceAndroidApiLevelMin18() && AbstractBluetoothBase.deviceHasBluetoothHardware(context)) {
                Log.d(TAG, "Bluetooth Low Energy Supported");
                // Instantiate Bluetooth Le Class
                mBluetoothOperator = new BluetoothLe(context, this, this);
            } else {
                throw new ZenMatErrorType.BluetoothError.BluetoothLeNotSupported("Bluetooth Low Energy is not supported. Classic Only...");
                // TODO Implement a Classic Bluetooth which extends AbstractBluetoothBase
                //mBluetoothOperator = new BluetoothClassic(...);
            }
        } else {
            Log.d(TAG, "Bluetooth is not supported");
            throw new ZenMatErrorType.BluetoothError.BluetoothNotSupported("Device does not support neither Classic nor LE Bluetooth.");
        }
    }

    //==============================================================================
    // ILockController Implementation
    //==============================================================================
    /**
     * Start search for lukoton locks in range.
     */
    @Override
    public void startSearchZenMats() {
        getBluetoothLe().startSearch();
    }

    /**
     * Stop search for lukoton locks in range.
     */
    @Override
    public void stopSearchForZenMats() {
        getBluetoothLe().stopSearch();
    }

    @Override
    public void onZenMatFound(Mat mat) {
        mIZenMatDiscoveryEventCallback.onZenMatFound(mat);
        try {
            startOperation(mat);
        } catch (ZenMatErrorType.BluetoothError.TargetMatIsNull targetMatIsNull) {
            targetMatIsNull.printStackTrace();
        } catch (ZenMatErrorType.Internet.DeviceNotConnectedToInternet deviceNotConnectedToInternet) {
            deviceNotConnectedToInternet.printStackTrace();
        }
    }


    protected void startOperation(final Mat mat) throws ZenMatErrorType.BluetoothError.TargetMatIsNull, ZenMatErrorType.Internet.DeviceNotConnectedToInternet {
        if (mat == null) {
            throw new ZenMatErrorType.BluetoothError.TargetMatIsNull();
        }
        Thread thread = new Thread(new Runnable() {
            public void run() {
                connect(mat);
            }
        });
        thread.start();
    }

    //==============================================================================
    // Bluetooth Connection & Disconnection
    //==============================================================================
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void connect(Mat mat) {
        // Stop searching if it is performing search
        stopSearchForZenMats();
        BluetoothDevice device  = mat.getDevice();
        int connState;
        if (Utils.ApiInformer.isDeviceAndroidApiLevelMin18()) {
            BluetoothGatt gatt = getBluetoothLe().getBluetoothGatt();
            connState = BluetoothLe.getBluetoothManager(getContext()).getConnectionState(device, BluetoothProfile.GATT);
            if (gatt != null && connState == BluetoothProfile.STATE_CONNECTED) {
                // TODO LATER
            }
        } else {
            // TODO Classic BT
        }
        getBluetoothLe().connect(device);
    }

    @Override
    public void onGattConnected(BluetoothGatt gatt) {
        Log.d(TAG, "Mat is connected.");
    }

    @Override
    public void onGattDisconnected(BluetoothGatt gatt, int status) {
        startSearchZenMats();
        Log.w(TAG, "Disconnected. Started to search again.");
        if (status == 62) {
            // For CYANOGEN implementation
            Log.e(TAG, "Disconnected due to a CYANOGEN problem: " + status);
        }
    }

    @Override
    public void onGattConnectionSetupFail(BluetoothGatt gatt, int status) {
        mIMatEventCallback.onMatCommunicationFailure(new ZenMatErrorType.BluetoothError.ConnectionSetupError(status));
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt) {
        try {
            enableNotifications(gatt);
        } catch (ZenMatErrorType.BluetoothError.CharacteristicNotificationEnableError characteristicNotificationEnableError) {
                mIMatEventCallback.onMatCommunicationFailure(characteristicNotificationEnableError);
        }
    }

    @Override
    public void onServiceDiscoveryFail(BluetoothGatt gatt, int status) {
        mIMatEventCallback.onMatCommunicationFailure(new ZenMatErrorType.BluetoothError.ServiceDiscoveryError(status));
    }

    @Override
    public void onCharacteristicNotificationEnabled(BluetoothGatt gatt, BluetoothGattDescriptor descriptor) {
        mIMatEventCallback.onMatIsReadyToReceiveData();
    }

    @Override
    public void onCharacteristicNotificationEnablingFail(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        mIMatEventCallback.onMatCommunicationFailure(new ZenMatErrorType.BluetoothError.DescriptorWriteError(status, descriptor));
    }

    @Override
    public void onCharacteristicNotificationFail(BluetoothGatt gatt, Exception e) {
        mIMatEventCallback.onMatCommunicationFailure(e);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCharacteristicNotificationReceived(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "Notification Received.");
        byte[] value = characteristic.getValue();
        int type = Utils.DataManipulation.convertByteToInteger(value[0]);
        if (value.length > 1) {
            Exception e = new ZenMatErrorType.BluetoothError.BadFormattedCharacteristicNotificationReceived("Length is " + value.length);
            onCharacteristicNotificationFail(gatt, e);
        }
        mIMatEventCallback.onMatReceivedNotification(type);
    }

    private void enableNotifications(BluetoothGatt gatt) throws ZenMatErrorType.BluetoothError.CharacteristicNotificationEnableError {
        BluetoothGattCharacteristic ch = null;
        try {
            if (getBluetoothLe().isServiceIncluded(gatt, ZenMatProfile.Service.getMatServiceUuid())) {
                Log.d(TAG, "Enabling lock Status notification");
                ch = (getBluetoothLe().getCharacteristicWithUuid(ZenMatProfile.Service.getMatServiceUuid(), ZenMatProfile.Characteristic.getMatStatusCharUuid()));
                getBluetoothLe().setCharacteristicNotificationOrIndication(ch, true);
            }
        } catch (ZenMatErrorType.Internal.GattNullInConnectedState gattNullInConnectedState) {
            mIMatEventCallback.onMatCommunicationFailure(gattNullInConnectedState);
            gattNullInConnectedState.printStackTrace();
        } catch (ZenMatErrorType.Internal.RequestedServiceNotDiscovered requestedServiceNotDiscovered) {
            mIMatEventCallback.onMatCommunicationFailure(requestedServiceNotDiscovered);
        } catch (ZenMatErrorType.Internal.RequestedCharacteristicNotDiscovered requestedCharacteristicNotDiscovered) {
            mIMatEventCallback.onMatCommunicationFailure(requestedCharacteristicNotDiscovered);
        }
    }

    private BluetoothLe getBluetoothLe() {
        return (BluetoothLe) mBluetoothOperator;
    }

    private Context getContext() {
        return mContext;
    }

}