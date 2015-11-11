package com.zenmat.mobile.demo.zenmat.impl;


import android.bluetooth.BluetoothDevice;

import java.util.UUID;

/**
 * Created by CgTy on 5.3.2015.
 */
final class ZenMatProfile {


    private static final String MAT_SERVICE_UUID = "0000ffe0-0000-1000-8000-00805f9b34fb"; // Service UUID
    private static final String MAT_STATUS_CHARACTERISTIC_UUID = "0000ffe1-0000-1000-8000-00805f9b34fb"; // Characteristic UUID
    private static final String CHARACTERISTIC_CLIENT_CONFIG_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    /**
     * ...
     * See {@link BluetoothLeScanCallback#onLeScan(BluetoothDevice, int, byte[])}, but ...
     */
    //static final int ZENMAT_COMPANY_IDENTIFIER = xxx;

    static class Service {
        final static private UUID MAT_SERVICE = UUID.fromString(MAT_SERVICE_UUID);
        public static UUID getMatServiceUuid() {
            return MAT_SERVICE;
        }
    }

    static class Characteristic {
        final static private UUID MAT_STATUS_CHARACTERISTIC = UUID.fromString(MAT_STATUS_CHARACTERISTIC_UUID);
        public static UUID getMatStatusCharUuid() {
            return MAT_STATUS_CHARACTERISTIC;
        }
    }

    static class Descriptor {
        final private static UUID CHARACTERISTIC_CLIENT_CONFIG_DESC = UUID.fromString(CHARACTERISTIC_CLIENT_CONFIG_UUID);
        public static UUID getCharacteristicClientConfigDesc() {
            return CHARACTERISTIC_CLIENT_CONFIG_DESC;
        }
    }

}
