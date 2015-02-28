package com.dxy.receiverservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.dxy.receiverservice.service.PlayService;
import com.dxy.receiverservice.Util;

/**
 * Created by admin on 2015/2/27.
 */
public class MusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            Intent it = new Intent(context, PlayService.class);
            it.putExtras(bundle);
            int op = bundle.getInt(Util.BUNDLE_FIELD_OP);
            if (op == Util.stop) {
                context.stopService(it);
            } else {
                context.startService(it);
            }
        }
    }
}
