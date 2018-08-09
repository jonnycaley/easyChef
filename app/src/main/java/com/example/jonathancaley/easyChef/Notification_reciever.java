//NOTE: Every line of code on this page was executed by Jonny Caley B518801

package com.example.jonathancaley.easyChef;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class Notification_reciever extends BroadcastReceiver {
    //method for dealing with notifications
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeating_Intent = new Intent(context,WelcomePage.class);
        repeating_Intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, repeating_Intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setContentIntent(pendingIntent).setSmallIcon(android.R.drawable.arrow_up_float).setContentTitle("easyChef").setContentText("Use up your leftover ingredients today!").setAutoCancel(true);
        notificationManager.notify(1,builder.build());
    }
}
