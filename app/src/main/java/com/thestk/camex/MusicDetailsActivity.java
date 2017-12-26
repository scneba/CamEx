package com.thestk.camex;

import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.thestk.camex.database.DataContract;
import com.thestk.camex.database.UserSharedPreference;
import com.thestk.camex.models.DataModel;
import com.thestk.camex.web.CheckInternetConnection;
import com.thestk.camex.web.DatabaseUpdateService;
import com.thestk.camex.web.DeveloperKey;
import com.thestk.camex.web.RequestHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Neba on 07-Oct-17.
 */

public class MusicDetailsActivity extends YouTubeBaseActivity {

    private static final int CURSOR_LOADER_ID = 10;
    private static final int ASYNC_LOADER_ID = 11;
    private static final String MUSIC_LIST = "music_list";
    private static Handler uiHandler;
    private final String SELECTED_MUSIC = "selected_music";
    private final String DATAMODEL = "data_model";
    private final String ISFAVOURITE = "is_favourite";
    private final String MODEL_ID = "model_id";
    @BindView(R.id.player)
    YouTubePlayerView youTubePlayerView;
    @BindView(R.id.tvtitle)
    TextView tv_title;
    @BindView(R.id.likes_imv)
    ImageView likes_imv;
    @BindView(R.id.share_imv)
    ImageView share_imv;
    @BindView(R.id.views_imv)
    ImageView views_imv;
    @BindView(R.id.rating)
    RatingBar ratingBar;
    @BindView(R.id.tv_views)
    TextView tv_views;
    @BindView(R.id.tv_likes)
    TextView tv_likes;
    @BindView(R.id.play_with_youtube)
    TextView tv_play_with_youtube;
    @BindView(R.id.imv_previous_pic)
    ImageView previous_pic_imv;
    @BindView(R.id.imv_next_pic)
    ImageView next_pic_imv;
    @BindView(R.id.favStar)
    ImageView favStar;
    @BindView(R.id.tv_next_music)
    TextView tv_next_music;
    @BindView(R.id.tv_previous_music)
    TextView tv_previous_music;
    @BindView(R.id.pbload)
    ProgressBar progressBar;
    @BindView(R.id.scrollview)
    ScrollView scrollView;
    //booleans to check current status
    private boolean isInFav = false;
    private boolean isLiked = false;
    private boolean isRated = false;
    private ArrayList<DataModel> musicList;
    private int currentPosition;
    private DataModel dataModel;

    private Uri music_uri = null;

    private int userId = -1;

    private UpdateReceiver updateReceiver;

    private UserSharedPreference userSharedPreference;
    private boolean canRestoreState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_detail_view);

        ButterKnife.bind(this);

        musicList = getIntent().getExtras().getParcelableArrayList(MUSIC_LIST);
        currentPosition = getIntent().getExtras().getInt(SELECTED_MUSIC);
        setupImageViews(currentPosition);

        uiHandler = new android.os.Handler(Looper.getMainLooper());//handler to run background activities
        updateReceiver = new UpdateReceiver();
        userSharedPreference = new UserSharedPreference(getApplicationContext());


        //check if state was saved
        if (savedInstanceState != null && savedInstanceState.containsKey(DATAMODEL)) {
            progressBar.setVisibility(View.GONE);

            dataModel = savedInstanceState.getParcelable(DATAMODEL);
            isInFav = savedInstanceState.getBoolean(ISFAVOURITE);
            setUpYoutubePlayer();
            updateViewsWithRecievedData();

            if (isInFav) {
                favStar.setImageResource(android.R.drawable.star_big_on);
                //build movie uri from cursor
                dataModel.setId(savedInstanceState.getInt(MODEL_ID));
            } else {
                favStar.setImageResource(android.R.drawable.star_big_off);
            }
            canRestoreState = true;

        } else {
            dataModel = musicList.get(currentPosition);
            //setup the youtube player
            setUpYoutubePlayer();
            startCusorLoader();
            startAsyncLoader();
        }

        //update user id
        if (userSharedPreference.isLoggedIn()) {
            userId = userSharedPreference.getUserData().getUser_id();
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (ratingBar.getRating() == 0.0f) {
                    return;
                }
                if (userSharedPreference.isLoggedIn()) {
                    Intent intent = new Intent(getApplicationContext(), DatabaseUpdateService.class);
                    intent.setAction(DatabaseUpdateService.ACTION_UPDATE_RATING);
                    Bundle bundle = new Bundle();
                    bundle.putInt(DatabaseUpdateService.USER_ID, userSharedPreference.getUserData().getUser_id());
                    bundle.putInt(DatabaseUpdateService.DATA_ID, dataModel.getData_id());
                    bundle.putInt(DatabaseUpdateService.UPDATE_VALUE, new Double(rating).intValue());
                    intent.putExtras(bundle);
                    startService(intent);
                } else {
                    ratingBar.setRating(0.0f);
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(MusicDetailsActivity.this).toBundle();
                        startActivity(intent, explodeBundle);
                    } else {
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (canRestoreState) {
            bundle.putParcelable(DATAMODEL, dataModel);
            bundle.putBoolean(ISFAVOURITE, isInFav);
            bundle.putInt(MODEL_ID, dataModel.getId());
        }
    }

    /**
     * Start the cursor loader to get data from sqlite
     */
    private void startCusorLoader() {

        if (getLoaderManager().getLoader(CURSOR_LOADER_ID) == null) {
            getLoaderManager().initLoader(CURSOR_LOADER_ID, null, new CheckIfFavourite()).forceLoad();
        } else {
            getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, new CheckIfFavourite()).forceLoad();
        }

    }

    /**
     * start the asynctask loader to get data from mysql
     */
    private void startAsyncLoader() {
        if (CheckInternetConnection.isNetworkAvailable(this)) {
            Log.e("connection", "connection available");
            if (getLoaderManager().getLoader(ASYNC_LOADER_ID) == null) {
                getLoaderManager().initLoader(ASYNC_LOADER_ID, null, new UpdateDataInfo()).forceLoad();
            } else {
                getLoaderManager().restartLoader(ASYNC_LOADER_ID, null, new UpdateDataInfo()).forceLoad();
            }
        } else {
            showMessageDialog(getResources().getString(R.string.message), getResources().getString(R.string.no_int), 1);
        }
    }

    /**
     * Update the current views with data received by intent
     */
    private void updateViewsWithRecievedData() {

        //check if user has rated the data
        if (dataModel.getUser_has_rated().equalsIgnoreCase("true")) {
            //user has rated
            ratingBar.setRating(Float.parseFloat(Integer.toString(dataModel.getUser_rating())));
            isRated = true;
        }
        //check if user has liked post
        if (dataModel.getUser_has_liked().equalsIgnoreCase("true")) {
            //user has liked post, change imageview background
            likes_imv.setBackgroundColor(getColor(R.color.colorAccent));
            isLiked = true;
        }

        tv_title.setText(dataModel.getTitle() + "/n" + dataModel.getArtist_actors());
        tv_views.setText(HelperMethods.getStringViewsFromInt(dataModel.getViews(), ""));
        tv_likes.setText(HelperMethods.getStringViewsFromInt(dataModel.getLikes(), "likes"));

    }

    /**
     * Setup the youtube player
     */
    private void setUpYoutubePlayer() {

        youTubePlayerView.initialize(DeveloperKey.DEVELOPER_KEY,
                new YouTubePlayer.OnInitializedListener() {

                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean wasRestored) {
                        if (youTubePlayer == null) return;

                        // do any work here to cue video, play video, etc.
                        if (!wasRestored) {
                            youTubePlayer.cueVideo(dataModel.getYoutube_id());
                            youTubePlayer.play();
                        } else {
                            youTubePlayer.play();
                        }
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                        YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }


    /**
     * Setup the image views for next and previous data
     *
     * @param currentPosition int
     */
    private void setupImageViews(int currentPosition) {
        DataModel prevModel = musicList.get(currentPosition > 0 ? currentPosition - 1 : musicList.size() - 1);
        tv_previous_music.setText(prevModel.getTitle() + "\n" + prevModel.getArtist_actors());
        String prevUrl = getString(R.string.imageUrl) + "/img/" + prevModel.getImage();
        Log.e("url", prevUrl);
        Picasso.with(getApplicationContext()).load(prevUrl).fit()
                .error(R.drawable.image_placeholder)
                .placeholder(R.drawable.image_placeholder)
                .into(previous_pic_imv);


        DataModel nextModel = musicList.get(currentPosition < musicList.size() - 1 ? currentPosition + 1 : 0);
        tv_next_music.setText(nextModel.getTitle() + "\n" + prevModel.getArtist_actors());
        String nextUrl = getString(R.string.imageUrl) + "/img/" + nextModel.getImage();
        Log.e("url", nextUrl);
        Picasso.with(getApplicationContext()).load(nextUrl).fit()
                .error(R.drawable.image_placeholder)
                .placeholder(R.drawable.image_placeholder)
                .into(next_pic_imv);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @OnClick(R.id.play_with_youtube)
    void openYoutubePlayer() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + dataModel.getYoutube_id()));
            startActivity(intent);
        } catch (Exception e) {
            String link_toshare = "https://www.youtube.com/watch?v=" + dataModel.getYoutube_id();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link_toshare));
            startActivity(browserIntent);

        }

    }

    @OnClick(R.id.likes_imv)
    void updateLikes() {

        if (userSharedPreference.isLoggedIn()) {
            if (isLiked) {
                likes_imv.setBackgroundColor(getColor(R.color.colorWhite));
            } else {
                likes_imv.setBackgroundColor(getColor(R.color.colorPrimary));
            }


            Intent intent = new Intent(getApplicationContext(), DatabaseUpdateService.class);
            intent.setAction(DatabaseUpdateService.ACTION_UPDATE_LIKE);
            Bundle bundle = new Bundle();
            bundle.putInt(DatabaseUpdateService.USER_ID, userSharedPreference.getUserData().getUser_id());
            bundle.putInt(DatabaseUpdateService.DATA_ID, dataModel.getData_id());
            intent.putExtras(bundle);
            startService(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivity(intent, explodeBundle);
            } else {
                startActivity(intent);
            }
        }
    }


    @OnClick(R.id.share_imv)
    void shareYoutubeLink() {

        String link_toshare = "https://www.youtube.com/watch?v=" + dataModel.getYoutube_id();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, link_toshare);

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
    }

    @OnClick(R.id.imv_next_pic)
    void play_nex_vid() {

        Intent intent = new Intent(getApplicationContext(), MusicDetailsActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            next_pic_imv.setTransitionName(getString(R.string.transition_name));
            Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(MusicDetailsActivity.this, next_pic_imv, next_pic_imv.getTransitionName()).toBundle();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(MUSIC_LIST, musicList);
            bundle.putInt(SELECTED_MUSIC, currentPosition < musicList.size() - 1 ? currentPosition + 1 : 0);
            intent.putExtras(bundle);

            startActivity(intent, explodeBundle);
            finish();

        } else {
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.imv_previous_pic)
    void play_previous_vid() {
        Intent intent = new Intent(getApplicationContext(), MusicDetailsActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            previous_pic_imv.setTransitionName(getString(R.string.transition_name));
            Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(MusicDetailsActivity.this, previous_pic_imv, previous_pic_imv.getTransitionName()).toBundle();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(MUSIC_LIST, musicList);
            bundle.putInt(SELECTED_MUSIC, currentPosition > 0 ? currentPosition - 1 : musicList.size() - 1);
            intent.putExtras(bundle);

            startActivity(intent, explodeBundle);
            finish();

        } else {
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.favStar)
    void handleFavourite() {
        if (isInFav) {
            deleteData(dataModel);
        } else {
            insertMusic(dataModel);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DatabaseUpdateService.ACTION_UPDATE_LIKE);
        intentFilter.addAction(DatabaseUpdateService.ACTION_UPDATE_RATING);
        registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
    }

    /**
     * insert data into favourite database
     *
     * @param dataModel {@link DataModel}
     */
    private void insertMusic(DataModel dataModel) {
        ContentValues values = new ContentValues();
        values.put(DataContract.DataEntry.COLUMN_DATA_ID, dataModel.getData_id());
        values.put(DataContract.DataEntry.COLUMN_TYPE, dataModel.getType());
        values.put(DataContract.DataEntry.COLUMN_TITLE, dataModel.getTitle());
        values.put(DataContract.DataEntry.COLUMN_ARTIST_ACTORS, dataModel.getArtist_actors());
        values.put(DataContract.DataEntry.COLUMN_MUSIC_GENRE, dataModel.getMusic_genre());
        values.put(DataContract.DataEntry.COLUMN_YOUTUBE_ID, dataModel.getYoutube_id());
        values.put(DataContract.DataEntry.COLUMN_VIEWS, dataModel.getViews());
        values.put(DataContract.DataEntry.COLUMN_LIKES, dataModel.getLikes());
        values.put(DataContract.DataEntry.COLUMN_IMAGE, dataModel.getImage());
        values.put(DataContract.DataEntry.COLUMN_ASPECT_RATIO, dataModel.getAspect_ratio());


        try {
            Uri movieUri = getApplicationContext().getContentResolver().insert(DataContract.DataEntry.CONTENT_URI, values);

            int id = -1;
            try {
                String lastSegment = movieUri.getLastPathSegment();
                id = Integer.parseInt(lastSegment);
                dataModel.setId(id);
            } catch (Exception e) {
            }

            if (movieUri != null) {
                Toast.makeText(MusicDetailsActivity.this, getString(R.string.success_add_fav), Toast.LENGTH_SHORT).show();
                isInFav = true;
                favStar.setImageResource(android.R.drawable.star_big_on);

                //update widget to show current favourites
                Intent my_intent = new Intent(this, AppWidgetService.class);
                my_intent.setAction(AppWidgetService.WIDGET_INTENT_SERVICE);
                my_intent.putExtra(AppWidgetService.WIDGET_ID, 0);
                this.startService(my_intent);
            }

        } catch (SQLException exception) {
            Toast.makeText(MusicDetailsActivity.this, getString(R.string.unable_insert), Toast.LENGTH_SHORT);
        }

    }

    /**
     * @param dataModel {@link DataModel}
     */
    private void deleteData(DataModel dataModel) {
        Uri uri = DataContract.DataEntry.CONTENT_URI;
        String id = Integer.toString(dataModel.getId());

        String whereString = DataContract.DataEntry._ID + " = ? ";
        String[] selectionArguments = new String[]{id};

        int numDeleted = getApplicationContext().getContentResolver().delete(uri, whereString, selectionArguments);
        if (numDeleted > 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.success_remove_fav), Toast.LENGTH_SHORT).show();
            favStar.setImageResource(android.R.drawable.btn_star_big_off);
            isInFav = false;

            //update widget to show current favourites

            Intent my_intent = new Intent(this, AppWidgetService.class);
            my_intent.setAction(AppWidgetService.WIDGET_INTENT_SERVICE);
            my_intent.putExtra(AppWidgetService.WIDGET_ID, 0);
            this.startService(my_intent);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.unable_remove), Toast.LENGTH_SHORT).show();

        }
    }

    private void updateUI(Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();

        if (action != null && action.equalsIgnoreCase(DatabaseUpdateService.ACTION_UPDATE_LIKE)) {
            //update like button
            if (isLiked) {
                //liked
                likes_imv.setBackgroundColor(getColor(R.color.colorWhite));
                dataModel.setUser_has_liked("false");
                dataModel.setLikes(dataModel.getLikes() - 1);
                tv_likes.setText(HelperMethods.getStringViewsFromInt(dataModel.getLikes(), getString(R.string.likes)));
                isLiked = false;
            } else {
                //unliked
                likes_imv.setBackgroundColor(getColor(R.color.colorAccent));
                dataModel.setUser_has_liked("true");
                dataModel.setLikes(dataModel.getLikes() + 1);
                tv_likes.setText(HelperMethods.getStringViewsFromInt(dataModel.getLikes(), getString(R.string.likes)));
                isLiked = true;
            }


        } else if (action != null && action.equalsIgnoreCase(DatabaseUpdateService.ACTION_UPDATE_RATING)) {
            int value = bundle.getInt(DatabaseUpdateService.UPDATE_VALUE);
            //set ratings to this value
            if (value == 0) {
                if (isRated) {
                    ratingBar.setRating(Float.parseFloat(Integer.toString(dataModel.getUser_rating())));
                } else {
                    ratingBar.setRating(Float.parseFloat(Integer.toString(value)));
                }
            } else {
                ratingBar.setRating(Float.parseFloat(Integer.toString(value)));
                dataModel.setUser_has_rated("true");
                dataModel.setUser_rating(value);
            }

        }

    }

    /**
     * Function shows a dismissable dialog to the user with important information
     *
     * @param title {@link String} title of dialog
     * @param body  {@link String} body of dialog
     * @param type  int indicates the action to perform on dialog click
     */

    private void showMessageDialog(final String title, String body, final int type) {
        //use libary to beautify alertdialog
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(MusicDetailsActivity.this);
        dialogBuilder.withTitle(title)
                .withIcon(android.R.drawable.ic_dialog_alert)
                .withMessage(body)
                .withMessageColor(getResources().getColor(R.color.colorWhite))
                .withTitleColor(getResources().getColor(R.color.colorWhite))
                .withDialogColor(getResources().getColor(R.color.colorPrimary))
                .withDividerColor(getResources().getColor(R.color.colorWhite2))
                .withButton1Text(getString(R.string.exit))                                      //def gone
                .withButton2Text(getString(R.string.reload))                                  //def gone
                .isCancelableOnTouchOutside(true)
                .withEffect(Effectstype.RotateLeft)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        onBackPressed();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                        if (type == 1) {
                            if (CheckInternetConnection.isNetworkAvailable(getApplicationContext())) {
                                startAsyncLoader();
                            } else {
                                showMessageDialog(getResources().getString(R.string.message), getResources().getString(R.string.no_int), 1);
                            }
                        }
                    }
                })
                .show();
    }

    /**
     * receiver to update UI from background service results
     */
    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("receiver", "intent received");
            updateUI(intent);
        }
    }

    private class CheckIfFavourite implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
            switch (id) {
                case CURSOR_LOADER_ID: {
                    Uri uri = DataContract.DataEntry.buildUriWithDataId(dataModel.getData_id());
                    return new CursorLoader(MusicDetailsActivity.this,
                            uri,
                            null,
                            null,
                            null,
                            null);
                }

                default:
                    throw new RuntimeException("Loader Not Implemented: " + id);
            }
        }

        @Override
        public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
            boolean cursorHasData = false;
            if (data != null && data.moveToFirst()) {
                isInFav = true;
                favStar.setImageResource(android.R.drawable.star_big_on);
                //build movie uri from cursor
                dataModel.setId(data.getInt(data.getColumnIndex(DataContract.DataEntry._ID)));
            } else {
                isInFav = false;
                favStar.setImageResource(android.R.drawable.star_big_off);

            }

            getLoaderManager().destroyLoader(CURSOR_LOADER_ID);
        }

        @Override
        public void onLoaderReset(android.content.Loader<Cursor> loader) {

        }
    }

    private class UpdateDataInfo implements LoaderManager.LoaderCallbacks<String> {

        private RequestHttp requestHttp = new RequestHttp();
        private int requestcode = 0;
        private String errorMessage = "";

        @Override
        public Loader<String> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<String>(MusicDetailsActivity.this) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    progressBar.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.INVISIBLE);

                }

                @Override
                public String loadInBackground() {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", dataModel.getData_id() + "");
                    hashMap.put("user_id", userId + "");
                    Log.e("user_id", "" + userId);
                    String url = getString(R.string.baseUrl) + "/data/getbyid";


                    String response = null;
                    try {
                        response = requestHttp.setUP(url, RequestHttp.Method.POST, 10000, 10000).withData(hashMap).sendAndReadString();
                        requestcode = requestHttp.getResponseCode();
                    } catch (SocketTimeoutException e) {
                        e.printStackTrace();
                        errorMessage = getString(R.string.connection_timeout);
                        return null;
                    } catch (IOException ee) {
                        ee.printStackTrace();
                        errorMessage = getString(R.string.failed_server);
                        return null;
                    } catch (Exception e) {
                        errorMessage = getString(R.string.failed_server);
                    }

                    return response;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<String> loader, String response) {

            progressBar.setVisibility(View.GONE);
            if (requestcode == 200) {
                try {
                    Log.e("response", response);
                    JSONObject jsonObject = new JSONObject(response);
                    Integer music_id = Integer.parseInt(jsonObject.getString("id"));
                    String title = jsonObject.getString("title");
                    String type = jsonObject.getString("type");
                    String artist_actors = jsonObject.getString("artist_actors");
                    String music_genre = jsonObject.getString("music_genre");
                    String image = jsonObject.getString("image");
                    String youtube_id = jsonObject.getString("youtube_id");

                    String update_views = jsonObject.getString("update_views");
                    String created_at = jsonObject.getString("created_at");

                    String user_has_liked = jsonObject.getString("user_liked");
                    String user_has_rated = jsonObject.getString("user_rated");

                    int user_rating = Integer.parseInt(jsonObject.getString("user_rating"));


                    Integer views = Integer.parseInt(jsonObject.getString("views"));
                    Float ratings = 0.0f;
                    Integer total_rating = 0;
                    try {
                        ratings = Float.parseFloat(jsonObject.getString("rating"));
                        total_rating = Integer.parseInt(jsonObject.getString("number_ratings"));
                    } catch (Exception e) {

                    }
                    Integer likes = 0;
                    try {
                        likes = Integer.parseInt(jsonObject.getString("likes"));
                    } catch (Exception e) {
                    }

                    dataModel.setTitle(title);
                    dataModel.setType(type);
                    dataModel.setArtist_actors(artist_actors);
                    dataModel.setImage(image);
                    dataModel.setLikes(likes);
                    dataModel.setViews(views);
                    dataModel.setRating(ratings);
                    dataModel.setMusic_genre(music_genre);
                    dataModel.setUpdate_views(update_views);
                    dataModel.setYoutube_id(youtube_id);
                    dataModel.setCreated_at(created_at);

                    dataModel.setUser_has_liked(user_has_liked);
                    dataModel.setUser_has_rated(user_has_rated);
                    dataModel.setUser_rating(user_rating);
                    dataModel.setTotal_ratings(total_rating);

                    scrollView.setVisibility(View.VISIBLE);
                    setUpYoutubePlayer();
                    updateViewsWithRecievedData();
                    canRestoreState = true;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //show dialogue
                if (errorMessage != "") {
                    showMessageDialog(getString(R.string.message), errorMessage, 1);
                }
            }

            getLoaderManager().destroyLoader(ASYNC_LOADER_ID);
        }

        @Override
        public void onLoaderReset(Loader<String> loader) {

        }
    }


}
