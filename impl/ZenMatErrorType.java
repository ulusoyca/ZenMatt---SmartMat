package com.zenmat.mobile.demo.zenmat.impl;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;


/**
 * Created by CgTy on 4.9.2015.
 */
public class ZenMatErrorType {

    static class Internal {
        static class GattNullInConnectedState extends Exception {
        }
        static class RequestedServiceNotDiscovered extends Exception {
        }
        static class RequestedCharacteristicNotDiscovered extends Exception {
        }
    }

    public static class BluetoothError {
        public static class CharacteristicNotificationEnableError extends Exception {
        }

        public static class TargetMatIsNull extends Exception {
        }

        public static class BluetoothNotSupported extends Exception {
            public BluetoothNotSupported(String s) {
                super(s);
            }
        }

        public static class BluetoothLeNotSupported extends Exception {
            public BluetoothLeNotSupported(String s) {
                super(s);
            }
        }

        public static class ConnectionSetupError extends Exception {
            private int status;
            public ConnectionSetupError( int status) {
                this.status  = status;
            }
            int getStatus() {
                return status;
            }
        }

        public static class BadFormattedCharacteristicNotificationReceived extends Exception {
            public BadFormattedCharacteristicNotificationReceived(String s) {
                super(s);
            }
        }

        public static class ServiceDiscoveryError extends Exception {
            private int statusCode;

            public ServiceDiscoveryError(int status) {
                statusCode = status;
            }

            public int getStatusCode() {
                return statusCode;
            }
        }

        public static class CharacteristicReadError extends Exception {
            private int statusCode;
            private BluetoothGattCharacteristic characteristic;
            public CharacteristicReadError(int status, BluetoothGattCharacteristic characteristic) {
                statusCode = status;
                this.characteristic = characteristic;
            }
            public BluetoothGattCharacteristic getCharacteristic() {
                return characteristic;
            }
            public int getStatusCode() {
                return statusCode;
            }
        }

        public static class CharacteristicWriteError extends Exception {
            private int statusCode;
            private BluetoothGattCharacteristic characteristic;
            public CharacteristicWriteError(int status, BluetoothGattCharacteristic characteristic) {
                statusCode = status;
                this.characteristic = characteristic;
            }
            public BluetoothGattCharacteristic getCharacteristic() {
                return characteristic;
            }
            public int getStatusCode() {
                return statusCode;
            }
        }

        public static class DescriptorWriteError extends Exception {
            private int statusCode;
            private BluetoothGattDescriptor descriptor;
            public DescriptorWriteError(int status, BluetoothGattDescriptor descriptor) {
            }
            public BluetoothGattDescriptor getDescriptor() {
                return descriptor;
            }
            public int getStatusCode() {
                return statusCode;
            }
        }
    }


    public static class Internet {
        public static class CertificateNotInstalledToDevice extends Exception {
        }
        public static class DeviceNotConnectedToInternet extends Exception {
        }
        public static class HttpClientError extends Exception {
            private int statusCode;
            private String message;
            public HttpClientError(int httpStatus) {
                statusCode = httpStatus;
            }
            public int getStatusCode() {
                return statusCode;
            }

            @Override
            public String getMessage() {
                message = "HttpClientError with status code" + statusCode;
                return message;
            }
        }
        public static class HttpServerError extends Exception {
            private int statusCode;
            private String message;
            public HttpServerError(int httpStatus) {
                statusCode = httpStatus;
            }
            public int getStatusCode() {
                return statusCode;
            }
            @Override
            public String getMessage() {
                message = "HttpServerError with status code" + statusCode;
                return message;
            }
        }
        public static class HttpUnknownError extends Exception {
            private int statusCode;
            public HttpUnknownError(int httpStatus) {
                statusCode = httpStatus;
            }
            public int getStatusCode() {
                return statusCode;
            }
        }
        public static class HttpResponseNull extends Exception {
        }
    }


}
