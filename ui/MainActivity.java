package com.zenmat.mobile.demo.zenmat.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zenmat.mobile.demo.zenmat.R;
import com.zenmat.mobile.demo.zenmat.impl.IMatDiscoveryEventCallback;
import com.zenmat.mobile.demo.zenmat.impl.IMatEventCallback;
import com.zenmat.mobile.demo.zenmat.impl.Mat;
import com.zenmat.mobile.demo.zenmat.impl.ZenMatController;
import com.zenmat.mobile.demo.zenmat.impl.ZenMatErrorType;

import java.util.Date;

/**
 * Created by CgTy on 7.11.2015.
 */
public class MainActivity extends Activity implements IMatDiscoveryEventCallback, IMatEventCallback {

    private static final String TAG = "Activity";
    private final int INCREASE_LEVEL = 1;
    private final int DECREASE_LEVEL = 2;
    private ZenMatController mMatController;
    private Button now;
    private Button history;
    int bgNr =34;
    long startTime = -1;
    long endTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        changeBackground(bgNr);
        try {
            mMatController = new ZenMatController(this, this, this);
        } catch (ZenMatErrorType.BluetoothError.BluetoothNotSupported bluetoothNotSupported) {
            bluetoothNotSupported.printStackTrace();
        } catch (ZenMatErrorType.BluetoothError.BluetoothLeNotSupported bluetoothLeNotSupported) {
            bluetoothLeNotSupported.printStackTrace();
        }
        now = (Button) findViewById(R.id.now);
        now.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                changeBackground(bgNr);
            }
        });
        history = (Button) findViewById(R.id.history);
        final LinearLayout layout = (LinearLayout)findViewById(R.id.lay_2);
        history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                changeBackground(0);
            }
        });

        mMatController.startSearchZenMats();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bgNr = updateBgNumber(3, DECREASE_LEVEL);
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeBackground(bgNr);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void changeBackground(final int i) {
        runOnUiThread(new Runnable() {
            public void run() {
                Resources res = getResources();
                int sdk = Build.VERSION.SDK_INT;
                int drawableId = getResources().getIdentifier("u_" + i, "drawable", getPackageName());
                Drawable drawable = res.getDrawable(drawableId);
                final LinearLayout layout = (LinearLayout) findViewById(R.id.lay_1);
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    layout.setBackgroundDrawable(drawable);
                } else {
                    layout.setBackgroundResource(drawableId);
                }
            }
        });
    }

    @Override
    public void onZenMatFound(Mat mat) {
        Toast.makeText(this,"ZenMaTT found. Now connecting.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMatIsReadyToReceiveData() {
        Log.d(TAG, "Ready to receive notifications");
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, "Ready!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMatCommunicationFailure(Exception e) {
        Log.d(TAG, "Communication error with Mat.");
        mMatController.startSearchZenMats();
    }

    @Override
    public void onMatReceivedNotification(int state) {
        Log.d(TAG, "Mat state is: " + state);
        if (state == 0) {
            endTime = new Date().getTime();
            long level = (endTime - startTime)/1000;
            Log.d(TAG, "Increasing the level by " + level);
            changeBackground(updateBgNumber((int) level, INCREASE_LEVEL));
        } else {
            startTime = new Date().getTime();
        }
    }

    private int updateBgNumber(int levelUnit,int operation) {
        switch (operation) {
            case INCREASE_LEVEL:
                bgNr = bgNr - levelUnit;
                break;
            case DECREASE_LEVEL:
                bgNr = bgNr + levelUnit;
                break;
        }
        if (bgNr < 1) {
            bgNr = 1;
        } else if (bgNr > 34) {
            bgNr = 34;
        }
        return bgNr;
    }
}
