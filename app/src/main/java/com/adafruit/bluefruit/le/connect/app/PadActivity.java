package com.adafruit.bluefruit.le.connect.app;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.adafruit.bluefruit.le.connect.R;
import com.adafruit.bluefruit.le.connect.ble.BleManager;

import java.nio.ByteBuffer;

public class PadActivity extends UartInterfaceActivity {
    // Log
    private final static String TAG = PadActivityPredefinedValues.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pad);

        mBleManager = BleManager.getInstance(this);

        // UI
        ImageButton upArrowImageButton = (ImageButton) findViewById(R.id.upImageButton);
        upArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton leftArrowImageButton = (ImageButton) findViewById(R.id.leftImageButton);
        leftArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton rightArrowImageButton = (ImageButton) findViewById(R.id.rightImageButton);
        rightArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton downArrowImageButton = (ImageButton) findViewById(R.id.downImageButton);
        downArrowImageButton.setOnTouchListener(mPadButtonTouchListener);

        ImageButton plusImageButton = (ImageButton) findViewById(R.id.plusImageButton);
        plusImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton minusImageButton = (ImageButton) findViewById(R.id.minusImageButton);
        minusImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton autonomousButton = (ImageButton) findViewById(R.id.autonomousImageButton);
        autonomousButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton remoteButton = (ImageButton) findViewById(R.id.remoteImageButton);
        remoteButton.setOnTouchListener(mPadButtonTouchListener);

        // Start services
        onServicesDiscovered();
    }

    View.OnTouchListener mPadButtonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final String command = (String) view.getTag();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                view.setPressed(true);
                sendTouchEvent(command, true);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                view.setPressed(false);
                sendTouchEvent(command, false);
                return true;
            }
            return false;
        }
    };

    private void sendTouchEvent(String command, boolean pressed) {
        String data = "!C" + command + (pressed ? "1" : "0");
        ByteBuffer buffer = ByteBuffer.allocate(data.length()).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.put(data.getBytes());
        sendDataWithCRC(buffer.array());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
    @Override
    public void onConfigurationChanged (Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        adjustAspectRatio();
    }
    */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // Set full screen mode
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    public void onClickExit(View view) {
        finish();
    }

    @Override
    public void onDisconnected() {
        super.onDisconnected();
        Log.d(TAG, "Disconnected. Back to previous activity");
        setResult(-1);      // Unexpected Disconnect
        finish();
    }
}
