package com.cesquerr.becky.app;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.cesquerr.becky.R;
import com.cesquerr.becky.ble.BleManager;
import com.cesquerr.becky.ble.BleUtils;

import java.nio.ByteBuffer;

public class PadActivityPredefinedValues extends UartInterfaceActivity {
    // Log
    private final static String TAG = PadActivityPredefinedValues.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pad_predefined_values);

        mBleManager = BleManager.getInstance(this);

        // UI
        ImageButton leftArrowImageButton = (ImageButton) findViewById(R.id.leftImageButton);
        leftArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton upLeftArrowImageButton = (ImageButton) findViewById(R.id.upLeftImageButton);
        upLeftArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton upArrowImageButton = (ImageButton) findViewById(R.id.upImageButton);
        upArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton upRightArrowImageButton = (ImageButton) findViewById(R.id.upRightImageButton);
        upRightArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton rightArrowImageButton = (ImageButton) findViewById(R.id.rightImageButton);
        rightArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton downRightArrowImageButton = (ImageButton) findViewById(R.id.downRightImageButton);
        downRightArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton downArrowImageButton = (ImageButton) findViewById(R.id.downImageButton);
        downArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton downLeftArrowImageButton = (ImageButton) findViewById(R.id.downLeftImageButton);
        downLeftArrowImageButton.setOnTouchListener(mPadButtonTouchListener);
        ImageButton stopImageButton = (ImageButton) findViewById(R.id.stopImageButton);
        stopImageButton.setOnTouchListener(mPadButtonTouchListener);

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

        final Character debugSeparator = '+';
        final String tag = (String) view.getTag();

        // We initialize the data to send with their default values
        String dataStreamIdentifier = "!C";
        String command = tag;
        String commandValue = null;

        Log.d(TAG, "Button pressed: " + command);

        // We look for separator '+' which is mandatory for all debug commands
        int separatorIndex = tag.indexOf(debugSeparator);
        if(separatorIndex != -1) {
            // We are in debug mode, let's set the right data stream identifier
            dataStreamIdentifier = "!D";

            // We extract both the command and its value
            command = tag.substring(0, separatorIndex);
            commandValue = tag.substring(separatorIndex + 1, tag.length());
            // We have to normalize the number of bytes sent through within the data stream.
            // We have to ensure we always sent 3 bytes for the command value
            final int numberOfRequiredBytes = 3;
            if(commandValue.length() < 3) {
                for(int i = commandValue.length(); i < numberOfRequiredBytes; i++) {
                    commandValue = "0" + commandValue;
                }
            }
        }

        // We retrieve if the was pressed or released
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            view.setPressed(true);
            sendTouchEvent(dataStreamIdentifier, command, commandValue, true);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            view.setPressed(false);
            sendTouchEvent(dataStreamIdentifier, command, commandValue, false);
            return true;
        }
        return false;
        }
    };

    private void sendTouchEvent(String dataStreamIdentifier, String command, String commandValue, boolean pressed) {
        String data = String.format("%s%s%s%s",
                                    dataStreamIdentifier,
                                    command,
                                    commandValue != null ? commandValue : "",
                                    pressed ? "1" : "0");
        ByteBuffer buffer = ByteBuffer.allocate(data.length()).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.put(data.getBytes());
        sendDataWithCRC(buffer.array());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pad_predefined_values, menu);
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
    public void onDisconnected() {
        super.onDisconnected();
        Log.d(TAG, "Disconnected. Back to previous activity");
        setResult(-1);      // Unexpected Disconnect
        finish();
    }
}
