package com.thestk.camex;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.thestk.camex.adapters.GridSpacingItemDecoration;
import com.thestk.camex.adapters.MusicAdapter;
import com.thestk.camex.database.UserSharedPreference;
import com.thestk.camex.models.DataModel;
import com.thestk.camex.web.CheckInternetConnection;
import com.thestk.camex.web.RequestHttp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.thestk.camex.HelperMethods.runLayoutAnimation;

/**
 * Created by Neba on 07-Oct-17.
 */

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>, MusicAdapter.CustomRecyclerOnClick {


    private static final String MUSIC_LIST = "music_list";
    private static final String LAYOUT_MAN_STATE = "lay_man_state";
    //loader id
    private static int MY_LOADER_ID = 400;
    private static boolean isTablet = false;
    private final String SELECTED_MUSIC = "selected_movie";
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.pbload)
    ProgressBar progressBar;
    @BindView(R.id.reload_ll)
    RelativeLayout reload_rl;
    @BindView(R.id.tv_message)
    TextView tv_message;
    @BindView(R.id.tv_reload)
    TextView tv_reload;
    //recycler manager
    RecyclerView.LayoutManager mLayoutManager;
    //setup strings for url request
    private String url;
    //holds any http error
    private String errorMessage = "";
    private String subcat = "views";
    private int responseCode = -1;
    //recipe list and adapter definition
    private ArrayList<DataModel> movieList;
    private MusicAdapter movieAdapter;
    //http object to be used in this activity
    private RequestHttp requestHttp;
    private UserSharedPreference userSharedPreference;
    private int user_id = -1;

    private boolean isTablet600 = false;
    private boolean isTablet720 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ButterKnife.bind(this);
        url = getString(R.string.baseUrl) + "/movies/get";
        requestHttp = new RequestHttp();

        reload_rl.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        userSharedPreference = new UserSharedPreference(this);
        if (userSharedPreference.isLoggedIn()) {
            user_id = userSharedPreference.getUserData().getUser_id();
        }


        if (findViewById(R.id.sw600dp) != null) {
            isTablet600 = true;
        } else if (findViewById(R.id.sw700dp) != null) {
            isTablet720 = true;
        }


        if (isTablet600) {
            mLayoutManager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 5, true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else if (isTablet720) {
            mLayoutManager = new StaggeredGridLayoutManager(4, LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 5, true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        } else {
            mLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 5, true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }


        //restore state if already saved
        if (savedInstanceState != null && savedInstanceState.containsKey(MUSIC_LIST)) {

            movieList = savedInstanceState.getParcelableArrayList(MUSIC_LIST);
            movieAdapter = new MusicAdapter(this, movieList, MusicAdapter.Genre.GOSPEL, MoviesActivity.this);
            recyclerView.setAdapter(movieAdapter);
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_MAN_STATE));

        } else {

            movieList = new ArrayList<>();
            movieAdapter = new MusicAdapter(this, movieList, MusicAdapter.Genre.GOSPEL, MoviesActivity.this);
            recyclerView.setAdapter(movieAdapter);

            startLoader();
        }


    }

    @OnClick(R.id.tv_reload)
    void reloadData() {
        startLoader();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save parcelable music list and the position of the current list item
        if (movieList.size() > 0) {
            outState.putParcelableArrayList(MUSIC_LIST, movieList);
            outState.putParcelable(LAYOUT_MAN_STATE, mLayoutManager.onSaveInstanceState());
        }

    }

    private void startLoader() {
        if (CheckInternetConnection.isNetworkAvailable(this)) {

            if (this.getSupportLoaderManager().getLoader(MY_LOADER_ID) == null) {
                getSupportLoaderManager().initLoader(MY_LOADER_ID, null, MoviesActivity.this).forceLoad();
            } else {
                getSupportLoaderManager().restartLoader(MY_LOADER_ID, null, MoviesActivity.this).forceLoad();
            }
        } else {
            reload_rl.setVisibility(View.VISIBLE);
            tv_message.setText(getString(R.string.no_int));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }
            case R.id.search_icon: {
                return true;
            }
            case R.id.top_rated: {
                subcat = "ratings";
                getSupportLoaderManager().initLoader(MY_LOADER_ID, null, MoviesActivity.this).forceLoad();
                return true;
            }
            case R.id.top_liked: {
                subcat = "likes";
                getSupportLoaderManager().initLoader(MY_LOADER_ID, null, MoviesActivity.this).forceLoad();
                return true;
            }
            case R.id.top_viewed: {
                subcat = "views";
                getSupportLoaderManager().initLoader(MY_LOADER_ID, null, MoviesActivity.this).forceLoad();
                return true;
            }
            case R.id.favourites: {
                Intent intent = new Intent(this, FavouriteMusicActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                    startActivity(intent, explodeBundle);

                } else {
                    startActivity(intent);
                }
                return true;

            }

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            @Override
            protected void onStartLoading() {
                Log.e("loader called", "loader called again");
                super.onStartLoading();
                progressBar.setVisibility(View.VISIBLE);
                reload_rl.setVisibility(View.INVISIBLE);
                movieList.clear();
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public String loadInBackground() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("subcat", subcat);
                hashMap.put("user_id", Integer.toString(user_id));


                String response = null;
                try {
                    response = requestHttp.setUP(url, RequestHttp.Method.POST, 10000, 10000).withData(hashMap).sendAndReadString();

                    responseCode = requestHttp.getResponseCode();
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
    public void onLoadFinished(Loader<String> loader, String data) {

        progressBar.setVisibility(View.GONE);


        if (HelperMethods.REQUEST_SUCCESSFUL == responseCode) {
            if (data != null) {
                Log.e("response", data);
                try {
                    JSONArray jsonArray = new JSONArray(data);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Integer music_id = Integer.parseInt(jsonObject.getString("id"));
                            String title = jsonObject.getString("title");
                            String type = jsonObject.getString("type");
                            String artist_actors = jsonObject.getString("artist_actors");
                            String music_genre = jsonObject.getString("music_genre");
                            String image = jsonObject.getString("image");
                            String youtube_id = jsonObject.getString("youtube_id");

                            String synopsis = jsonObject.getString("synop_desc");
                            String contacts = jsonObject.getString("contacts");

                            String update_views = jsonObject.getString("update_views");
                            String created_at = jsonObject.getString("created_at");

                            String user_has_liked = jsonObject.getString("user_liked");
                            String user_has_rated = jsonObject.getString("user_rated");

                            int user_rating = Integer.parseInt(jsonObject.getString("user_rating"));


                            Integer views = Integer.parseInt(jsonObject.getString("views"));
                            Float ratings = 0.0f;
                            Integer total_rating = 0;
                            Float aspect_ratio = 1f;
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


                            try {
                                aspect_ratio = Float.parseFloat(jsonObject.getString("aspect_ratio"));
                            } catch (Exception e) {
                            }
                            DataModel dataModel = new DataModel();
                            dataModel.setMovieData(-1, music_id, type, title, artist_actors, synopsis, contacts, music_genre, youtube_id, image, aspect_ratio, views, ratings, total_rating, likes, update_views, created_at,
                                    user_has_liked, user_has_rated, user_rating);

                            movieList.add(dataModel);

                        }

                        //notify adapter to update
                        runLayoutAnimation(recyclerView);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                reload_rl.setVisibility(View.VISIBLE);
                tv_message.setText(getString(R.string.failed_server));
            }
        } else {
            reload_rl.setVisibility(View.VISIBLE);
            tv_message.setText(errorMessage);
        }

        getSupportLoaderManager().destroyLoader(MY_LOADER_ID);

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onCustomClick(int position, ImageView imageView) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this, imageView, imageView.getTransitionName()).toBundle();
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(MUSIC_LIST, movieList);
            bundle.putInt(SELECTED_MUSIC, position);
            intent.putExtras(bundle);

            startActivity(intent, explodeBundle);

        } else {
            startActivity(intent);
        }

    }
}
