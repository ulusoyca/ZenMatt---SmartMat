package com.zenmat.mobile.demo.zenmat.impl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Cagatay on 11/21/2014. This class is implemented to customize the scan behavior
 * It will be useful for extending the default behavior in the future.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
final class BluetoothLeScanCallback implements BluetoothAdapter.LeScanCallback {

    private static final String TAG = "CustomLeScanCallBack";
    private final BluetoothLe mBluetoothLe;
    private Set<BluetoothDevice> devList = new HashSet<>();

    //==============================================================================
    // Constructor
    //==============================================================================
    BluetoothLeScanCallback(BluetoothLe ble) {
        this.mBluetoothLe = ble;
    }

    //==============================================================================
    // Scan Callback
    //==============================================================================
    @Override
    public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
        // In case there are manufacturer specific data in the ad packet, parse the manufacturers
        //HashMap<Integer,byte[]> manufacturerList = getManufacturerList(scanRecord);
        // if (* manufacturerList.containsKey(LockProfile.ZENMAT_COMPANY_IDENTIFIER) && !devList.contains(device))
        if (device.getAddress().equals("90:59:AF:0A:A8:DD")) {
            Log.d(TAG, "Found the Zen Mat:  " + device.getName() + " Address:" + device.getAddress());
            // TODO:
            // For demo purposes the target device address is embedded but...
            // This can be extended in either of the following ways:
            // 1- Add a company identifier or manufacturer specific data to the ad data and filter
            // 2- Using NFC or other Out Of Bands get the device address from mat OOB
            // 3- Do OOB pairing which is extension to the 2nd.
            devList.add(device);
            Mat mat = new Mat(device);
            mBluetoothLe.getLockDiscoveryEventCallback().onZenMatFound(mat);
        }
    }

    //==============================================================================
    // Advertisement Packet Parsing
    //==============================================================================
    private static HashMap<Integer,byte[]> getManufacturerList(byte data[]) {
        HashMap<Integer,byte[]> manufacturerList = new HashMap<>();
        int position=0; // Read byte by byte. Position is the byte position.
        int dataLen = data.length; // Advertisement Packet Data length
        int manufacturerId; // Bluetooth SIG company id
        while ((position+1) < dataLen) {
            // Read Advertisement Struct
            int structPos = position;
            // Get the length of the struct
            int structLen = Utils.DataManipulation.convertByteToInteger(data[position]);
            // Check if there is Ad Data
            if (adStructHasData(dataLen, structPos, structLen)) {
                // Start Reading Data
                ++position;
                // Get the Advertisement Struct Type
                int adStructType = Utils.DataManipulation.convertByteToInteger(data[position]);
                ++position;
                int len = structLen - 1;  // Decrease by 1 byte for struct type
                switch( adStructType ) {
                    case 0xFF: // Manufacturer Specific Data
                        int ptr = position;
                        manufacturerId = ((int)data[ptr]) & 0xFF;
                        ptr++;
                        manufacturerId |= (((int)data[ptr]) & 0xFF ) << 8;
                        ptr++;
                        len = len - 2; // 2 bytes for company id
                        byte[] manufacturerSpecificData = new byte[31];
                        System.arraycopy(data, ptr, manufacturerSpecificData, 0, len);
                        manufacturerList.put(manufacturerId,manufacturerSpecificData);
                        break;
                    default:
                        break;
                }
                position = structPos + structLen + 1;
            } else {
                break;
            }
        }
        return  manufacturerList;
    }

    private static boolean adStructHasData(int dataLen, int structPos, int structLen) {
        return structLen != 0 && structPos + structLen <= dataLen;
    }

    //==============================================================================
    // Getters
    //==============================================================================
    Set<BluetoothDevice> getDevList() {
        return devList;
    }

}
