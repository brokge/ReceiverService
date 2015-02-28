package com.dxy.receiverservice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dxy.receiverservice.R;
import com.dxy.receiverservice.Util;

/**
 * Created by admin on 2015/2/27.
 */
public class ReceiverServiceActivity extends ActionBarActivity implements View.OnClickListener {
    private Button playBtn;
    private Button stopBtn;
    private Button pauseBtn;
    private Button exitBtn;
    private Button closeBtn;
    private SeekBar mSeekBar;
    private TextView mCurrentTxt;
    private TextView mTotalTxt;
    private Intent intent;
    private String TAG = "ReceiverServiceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        playBtn = (Button) findViewById(R.id.play);
        stopBtn = (Button) findViewById(R.id.stop);
        pauseBtn = (Button) findViewById(R.id.pause);
        exitBtn = (Button) findViewById(R.id.exit);
        closeBtn = (Button) findViewById(R.id.close);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mCurrentTxt = (TextView) findViewById(R.id.current_time);
        mTotalTxt = (TextView) findViewById(R.id.total_time);

        playBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
        closeBtn.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                intent = new Intent(Util.MUSIC_ACTION_NAME);
                Bundle bundle = new Bundle();
                int op = Util.pause;
                bundle.putInt(Util.BUNDLE_FIELD_OP, op);
                intent.putExtras(bundle);
                sendBroadcast(intent);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i = seekBar.getProgress();
                intent = new Intent(Util.MUSIC_ACTION_NAME);
                Bundle bundle = new Bundle();
                int op = Util.pre;
                bundle.putInt(Util.BUNDLE_FIELD_OP, op);
                bundle.putInt(Util.BUNDLE_FIELD_POSITION, i);
                intent.putExtras(bundle);
                sendBroadcast(intent);
                Log.e(TAG, i + "");

            }
        });
        registerBoradcastReceiver();
    }

    @Override
    public void onClick(View v) {
        int op = -1;
        intent = new Intent(Util.MUSIC_ACTION_NAME);
        switch (v.getId()) {
            case R.id.play:
                op = Util.play;
                break;
            case R.id.pause:
                op = Util.pause;
                break;
            case R.id.stop:
                op = Util.stop;
                break;
            case R.id.close:
                this.finish();
                break;
            case R.id.exit:
                op = Util.exit;
                this.finish();
                break;
        }
        Bundle bundle = new Bundle();
        bundle.putInt(Util.BUNDLE_FIELD_OP, op);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (intent != null) {
            stopService(intent);
        }
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
                long pos = mSeekBar.getMax() * position / duration;
                mCurrentTxt.setText(Util.getTime(position));
                mTotalTxt.setText(Util.getTime(duration));
                switch (type) {
                    case Util.prepared:
                        break;
                }
                mSeekBar.setProgress((int) pos);
            }
        }
    };


}
