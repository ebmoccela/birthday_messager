package io.github.ebmoccela.birthday_scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //bundle from calendar view
        Bundle extras = intent.getExtras();
        Bundle bundle = intent.getExtras();
        ArrayList <String> test = bundle.getStringArrayList("e");
        Log.d("bundle", "onReceive: " + test);
        Log.d("context", "onReceive: " + context);

        ArrayList<String> eventInfo;
        String eNumber = null;
        String eMessage = null;
//
        if (extras != null) {
            eventInfo = extras.getStringArrayList("eventInfo");

            eNumber = eventInfo.get(1);
            eMessage = eventInfo.get(2);
//            // and get whatever type user account id is
        }

        //port number for android "5554"
        String selectedNumber = eNumber;
        String message = eMessage;
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(selectedNumber, null, message, null, null);
        //Log.d("something", "onReceive: ");    //checks to see if message was received
    }
}
