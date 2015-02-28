package com.dxy.receiverservice.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;

import com.dxy.receiverservice.R;
import com.dxy.receiverservice.activity.ReceiverServiceActivity;
import com.dxy.receiverservice.Util;


import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 2015/2/27.
 */
public class PlayService extends Service {
    private MediaPlayer mediaPlayer;
    private Timer mTimer = new Timer();
    private CurTimerTask mTimerTask;
    private Intent intent;
    private boolean isPlay;
    public NotificationManager mNotificationManager;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.tmp);
            mediaPlayer.setLooping(false);
           /* mediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
            mediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);*/
            mediaPlayer.setOnPreparedListener(onPreparedListener);
        }
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    }

    //region @description listener
    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            sendMessage(Util.prepared);
        }
    };
/*
    private MediaPlayer.OnSeekCompleteListener onSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {

        }
    };
    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
    };

    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    };
    private MediaPlayer.OnVideoSizeChangedListener onVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

        }
    };*/

    //endregion
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                final int op = bundle.getInt(Util.BUNDLE_FIELD_OP);
                switch (op) {
                    case Util.play:
                        play();
                        break;
                    case Util.stop:
                        stop();
                        break;
                    case Util.pause:
                        pause();
                        break;
                    case Util.pre:
                        int position = bundle.getInt(Util.BUNDLE_FIELD_POSITION);
                        setPlayPosition(position);
                        break;
                }
            }
        }
        //flags=START_STICKY_COMPATIBILITY;
        flags = START_REDELIVER_INTENT;
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void play() {
        showButtonNotify();
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            if (mTimerTask != null) {
                mTimerTask.cancel();  //将原任务从队列中移除
            }
            mTimerTask = new CurTimerTask();
            mTimer.schedule(mTimerTask, 0, 100);
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void setPlayPosition(int position) {
        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            int currentPosition = position * duration / 100;
            mediaPlayer.seekTo(currentPosition);
            play();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync();    // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
        }
    }

    public void sendMessage(int type) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            //handleProgress.sendEmptyMessage(0);
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putInt(Util.BUNDLE_FIELD_TYPE,type);
            bundle.putInt(Util.BUNDLE_FIELD_POSITION, position);
            bundle.putInt(Util.BUNDLE_FIELD_DURATION, duration);
            message.setData(bundle);
            handleProgress.sendMessage(message);
        }
    }

    // 更新进度条
    class CurTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                sendMessage(Util.play);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            try {
                if (msg != null) {
                    int position = msg.getData().getInt(Util.BUNDLE_FIELD_POSITION);
                    int duration = msg.getData().getInt(Util.BUNDLE_FIELD_DURATION);
                    if (duration > 0) {
                        intent = new Intent(Util.ACTION_NAME);
                        Bundle bundle = msg.getData();
                        //bundle.putInt("op", op);
                        intent.putExtras(bundle);
                        sendBroadcast(intent);
                    }
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        };
    };
    /**
     * 带按钮的通知栏
     */
    @SuppressLint("NewApi")
    public void showButtonNotify() {
        Notification.Builder mBuilder = new Notification.Builder(this);
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_custom_button);
        mRemoteViews.setImageViewResource(R.id.custom_song_icon, R.mipmap.sing_icon);
        //API3.0 以上的时候显示按钮，否则消失
        mRemoteViews.setTextViewText(R.id.notice_custom_song_singer, getString(R.string.notification_song_single_name));
        mRemoteViews.setTextViewText(R.id.notice_custom_song_name, getString(R.string.notification_song_name));
        //如果版本号低于（3。0），那么不显示按钮
        if (Util.getSystemVersion() <= 9) {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.GONE);
        } else {
            mRemoteViews.setViewVisibility(R.id.ll_custom_button, View.VISIBLE);
        }
        //点击的事件处理
        Intent buttonIntent = new Intent(Util.MUSIC_ACTION_NAME);
        /* 播放*/
        buttonIntent.putExtra(Util.BUNDLE_FIELD_OP, Util.play);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_play = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_play, intent_play);
		/* 暂停*/
        buttonIntent.putExtra(Util.BUNDLE_FIELD_OP, Util.pause);
        PendingIntent intent_pause = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.btn_custom_pause, intent_pause);
        Intent openIntent = new Intent(this, ReceiverServiceActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,100,openIntent,0);
        mBuilder.setContent(mRemoteViews)
                //.setContentIntent(getDefaultIntent(Notification.FLAG_ONGOING_EVENT))
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setTicker(getString(R.string.notification_playing))
                .setPriority(Notification.PRIORITY_MAX)// 设置该通知优先级
                .setOngoing(true)
                .setSmallIcon(R.mipmap.sing_icon);
        Notification notify = mBuilder.build();
        notify.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(200, notify);
    }

    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性: 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefaultIntent(int flags) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

}
