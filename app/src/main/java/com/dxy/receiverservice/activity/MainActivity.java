package com.dxy.receiverservice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.dxy.receiverservice.R;
import com.dxy.receiverservice.Util;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    Button receiveButton;
    Button stopServiceButton;
    SeekBar mSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiveButton = (Button) this.findViewById(R.id.play_music_button);
        stopServiceButton = (Button) this.findViewById(R.id.stop_play_service);
        receiveButton.setOnClickListener(this);
        stopServiceButton.setOnClickListener(this);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        registerBoradcastReceiver();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.play_music_button:
                startActivity(new Intent(this, ReceiverServiceActivity.class));
                break;
            case R.id.stop_play_service:
                Intent intent = new Intent(Util.MUSIC_ACTION_NAME);
                int  op = Util.stop;
                Bundle bundle = new Bundle();
                bundle.putInt(Util.BUNDLE_FIELD_OP, op);
                intent.putExtras(bundle);
                sendBroadcast(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Util.ACTION_NAME);
        //×¢²á¹ã²¥
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Util.ACTION_NAME)) {
                Bundle bundle = intent.getExtras();
                int type = bundle.getInt(Util.BUNDLE_FIELD_TYPE);
                int position = bundle.getInt(Util.BUNDLE_FIELD_POSITION);
                int duration = bundle.getInt(Util.BUNDLE_FIELD_DURATION);
                switch (type) {
                    case Util.prepared:
                        break;
                }
                long pos = mSeekBar.getMax() * position / duration;
                mSeekBar.setProgress((int) pos);
            }
        }
    };
}
