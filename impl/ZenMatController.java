package com.zenmat.mobile.demo.zenmat.impl;

import android.content.Context;

/**
 * Created by CgTy on 10.11.2015.
 */
public class ZenMatController implements IMatController {

    private ZenMatControllerImpl mIZenMatControllerImpl;

    public ZenMatController(Context mContext, IMatDiscoveryEventCallback iZenMatDiscoveryEventCallback, IMatEventCallback iMatEventCallback) throws ZenMatErrorType.BluetoothError.BluetoothNotSupported, ZenMatErrorType.BluetoothError.BluetoothLeNotSupported {
        mIZenMatControllerImpl = new ZenMatControllerImpl(mContext, iZenMatDiscoveryEventCallback, iMatEventCallback);
    }

    @Override
    public void startSearchZenMats() {
        mIZenMatControllerImpl.startSearchZenMats();
    }

    @Override
    public void stopSearchForZenMats() {
        mIZenMatControllerImpl.stopSearchForZenMats();
    }
}
