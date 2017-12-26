package com.thestk.camex.web;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thestk.camex.MusicDetailsActivity;
import com.thestk.camex.R;
import com.thestk.camex.models.DataModel;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by Neba on 14-Oct-17.
 */

public class FCMNotifyService extends FirebaseMessagingService {

    private static final String MUSIC_LIST = "music_list";
    private final String TAG = "FCM";
    private final String SELECTED_MUSIC = "selected_music";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
        sendNotification(remoteMessage.getData());

    }

    private void sendNotification(Map<String, String> messageBody) {

        //get data
        DataModel dataModel = new DataModel();
        dataModel.setId(-1);
        dataModel.setData_id(Integer.parseInt(messageBody.get("id")));

        dataModel.setTitle(messageBody.get("title"));
        dataModel.setArtist_actors(messageBody.get("artist_actors"));
        dataModel.setYoutube_id(messageBody.get("youtube_id"));
        dataModel.setMusic_genre(messageBody.get("music_genre"));
        dataModel.setImage(messageBody.get("image"));
        dataModel.setType(messageBody.get("type"));
        ArrayList<DataModel> musicList = new ArrayList<>();
        musicList.add(dataModel);


        Intent intent = new Intent(this, MusicDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MUSIC_LIST, musicList);
        bundle.putInt(SELECTED_MUSIC, 0);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getApplicationContext().getString(R.string.neww) + " " + messageBody.get("type"))
                .setContentText(messageBody.get("title"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
