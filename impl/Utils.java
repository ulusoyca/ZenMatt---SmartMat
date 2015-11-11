package com.zenmat.mobile.demo.zenmat.impl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by CgTy on 11.9.2015.
 */
class Utils {


    static class ApiInformer {

        /**
         * Checks if Android API Level is minimum 18. This is important to support BLE API
         */
        static boolean isDeviceAndroidApiLevelMin18() {
            return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2;
        }
    }


    /**
     * A wrapper that contains String manipulation logic
     */
    static class DataManipulation {
        static int convertByteToInteger(byte b) {
            return ((int) b) & 0xFF;
        }
    }
}
