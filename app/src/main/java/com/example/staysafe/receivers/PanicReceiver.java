package com.example.staysafe.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.staysafe.AlertActivity;

public class PanicReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alert = new Intent(context.getApplicationContext(), AlertActivity.class);
        context.startActivity(alert);
    }
}
