package com.zenmat.mobile.demo.zenmat.impl;

/**
 * Created by CgTy on 7.11.2015.
 */
public interface IMatEventCallback {
    void onMatIsReadyToReceiveData();
    void onMatCommunicationFailure(Exception e);
    void onMatReceivedNotification(int state);
}
