package com.thestk.camex.web;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.thestk.camex.R;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * Created by Neba on 11-Oct-17.
 */

public class DatabaseUpdateService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public static final String ACTION_UPDATE_LIKE = "com.thestk.camex.updateviews";
    public static final String ACTION_UPDATE_RATING = "com.thestk.camex.updaterating";

    public static final String ACTION_UPDATE_LIKE_BUTTON = "com.thestk.camex.updatelikebutton";
    public static final String ACTION_UPDATE_RATINGS_BAR = "com.thestk.camex.updateratingsbar";

    public static final String RECEIVER_ACTION = "com.thestk.updatevies";

    public static final String UPDATE_VALUE = "update_value";

    public static final String USER_ID = "user_id";
    public static final String DATA_ID = "data_id";

    private RequestHttp requestHttp;


    public DatabaseUpdateService() {
        super("update_service");
        requestHttp = new RequestHttp();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e("service", "service called");
        String action = intent.getAction();
        if (action != null && action.equalsIgnoreCase(ACTION_UPDATE_LIKE)) {
            Bundle bundle = intent.getExtras();
            int user_id = bundle.getInt(USER_ID);
            int data_id = bundle.getInt(DATA_ID);
            if (CheckInternetConnection.isNetworkAvailable(getApplicationContext())) {
                updateLIkes(user_id, data_id);
            }

        } else if (action != null && action.equalsIgnoreCase(ACTION_UPDATE_RATING)) {

            Bundle bundle = intent.getExtras();
            int user_id = bundle.getInt(USER_ID);
            int data_id = bundle.getInt(DATA_ID);
            int rating = bundle.getInt(UPDATE_VALUE);

            if (CheckInternetConnection.isNetworkAvailable(getApplicationContext())) {
                updateRating(user_id, data_id, rating);
            } else {
                Intent intent1 = new Intent();
                intent1.setAction(ACTION_UPDATE_RATING);
                Bundle bundle1 = new Bundle();
                bundle.putInt(UPDATE_VALUE, 0);
                intent1.putExtras(bundle1);
                sendBroadcast(intent1);

            }

        }

    }

    private void updateLIkes(int user_id, int data_id) {
        String url = getApplicationContext().getString(R.string.baseUrl) + "/data/updatelike";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", Integer.toString(user_id));
        hashMap.put("data_id", Integer.toString(data_id));


        String response = null;
        try {
            response = requestHttp.setUP(url, RequestHttp.Method.POST, 10000, 10000).withData(hashMap).sendAndReadString();

            if (requestHttp.getResponseCode() == 200) {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equalsIgnoreCase("200")) {
                    //request successful
                    Intent intent = new Intent();
                    intent.setAction(ACTION_UPDATE_LIKE);

                    sendBroadcast(intent);
                } else {
                    //request failed
                }
            }


        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException ee) {
        } catch (Exception e) {
        }

    }

    private void updateRating(int user_id, int data_id, int rating) {
        String url = getApplicationContext().getString(R.string.baseUrl) + "/data/updaterating";
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("user_id", Integer.toString(user_id));
        hashMap.put("data_id", Integer.toString(data_id));
        hashMap.put("rating", Integer.toString(rating));

        String response = null;
        try {
            response = requestHttp.setUP(url, RequestHttp.Method.POST, 10000, 10000).withData(hashMap).sendAndReadString();

            if (requestHttp.getResponseCode() == 200) {
                Log.e("response", response);
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("code").equalsIgnoreCase("200")) {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_UPDATE_RATING);
                    Bundle bundle = new Bundle();
                    bundle.putInt(UPDATE_VALUE, rating);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_UPDATE_RATING);
                    Bundle bundle = new Bundle();
                    bundle.putInt(UPDATE_VALUE, 0);
                    intent.putExtras(bundle);
                    sendBroadcast(intent);
                }
            }


        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException ee) {
        } catch (Exception e) {
        }

    }

}
