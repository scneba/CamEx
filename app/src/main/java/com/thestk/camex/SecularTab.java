package com.thestk.camex;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import butterknife.Unbinder;

import static com.thestk.camex.HelperMethods.runLayoutAnimation;

/**
 * Created by Neba on 07-Oct-17.
 */

public class SecularTab extends Fragment implements LoaderManager.LoaderCallbacks<String>, MusicAdapter.CustomRecyclerOnClick {
    //loader id
    private final int MY_LOADER_ID = 101;
    private final String MUSIC_LIST = "music_list";
    private final String SELECTED_MUSIC = "selected_music";
    private final String LAYOUT_MAN_STATE = "lay_man_state";
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
    private Unbinder unbinder;
    private boolean isTablet600 = false;
    private boolean isTablet720 = false;
    //setup strings for url request
    private String url;
    private String genre = "secular";
    private String subcat = "views";
    //holds any http error
    private String errorMessage = "";
    //recipe list and adapter definition
    private ArrayList<DataModel> musicList;
    private MusicAdapter musicAdapter;
    //http object to be used in this activity
    private RequestHttp requestHttp;
    private boolean isTablet = false;

    private UserSharedPreference userSharedPreference;
    private int user_id = -1;
    private int responseCode = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        url = getActivity().getString(R.string.baseUrl) + "/music/get";
        View view = inflater.inflate(R.layout.secular_tab, container, false);

        unbinder = ButterKnife.bind(this, view);
        requestHttp = new RequestHttp();

        reload_rl.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        userSharedPreference = new UserSharedPreference(getContext());
        if (userSharedPreference.isLoggedIn()) {
            user_id = userSharedPreference.getUserData().getUser_id();
        }


        if (view.findViewById(R.id.sw600dp) != null) {
            isTablet600 = true;
        } else if (view.findViewById(R.id.sw700dp) != null) {
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

            musicList = savedInstanceState.getParcelableArrayList(MUSIC_LIST);
            musicAdapter = new MusicAdapter(getActivity(), musicList, MusicAdapter.Genre.GOSPEL, SecularTab.this);
            recyclerView.setAdapter(musicAdapter);
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_MAN_STATE));

        } else {

            musicList = new ArrayList<>();
            musicAdapter = musicAdapter = new MusicAdapter(getActivity(), musicList, MusicAdapter.Genre.GOSPEL, SecularTab.this);
            recyclerView.setAdapter(musicAdapter);

            startLoader();
        }


        tv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoader();
            }
        });
        setHasOptionsMenu(true);
        return view;
    }


    private void startLoader() {
        if (CheckInternetConnection.isNetworkAvailable(getContext())) {

            if (getActivity().getSupportLoaderManager().getLoader(MY_LOADER_ID) == null) {
                getActivity().getSupportLoaderManager().initLoader(MY_LOADER_ID, null, SecularTab.this).forceLoad();
            } else {
                getActivity().getSupportLoaderManager().restartLoader(MY_LOADER_ID, null, SecularTab.this).forceLoad();
            }
        } else {
            reload_rl.setVisibility(View.VISIBLE);
            tv_message.setText(getString(R.string.no_int));
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save parcelable music list and the position of the current list item
        if (musicList.size() > 0) {
            outState.putParcelableArrayList(MUSIC_LIST, musicList);
            outState.putParcelable(LAYOUT_MAN_STATE, mLayoutManager.onSaveInstanceState());
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.search_icon: {
                return true;
            }
            case R.id.top_rated: {
                subcat = "ratings";
                getActivity().getSupportLoaderManager().initLoader(MY_LOADER_ID, null, SecularTab.this).forceLoad();
                return true;
            }
            case R.id.top_liked: {
                subcat = "likes";
                getActivity().getSupportLoaderManager().initLoader(MY_LOADER_ID, null, SecularTab.this).forceLoad();
                return true;
            }
            case R.id.top_viewed: {
                subcat = "views";
                getActivity().getSupportLoaderManager().initLoader(MY_LOADER_ID, null, SecularTab.this).forceLoad();
                return true;
            }
            case R.id.favourites: {
                Intent intent = new Intent(getActivity(), FavouriteMusicActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
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
        return new AsyncTaskLoader<String>(getActivity()) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                progressBar.setVisibility(View.VISIBLE);
                reload_rl.setVisibility(View.INVISIBLE);
                musicList.clear();
                musicAdapter.notifyDataSetChanged();
            }

            @Override
            public String loadInBackground() {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("genre", genre);
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

        getActivity().getSupportLoaderManager().destroyLoader(MY_LOADER_ID);
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

                            String update_views = jsonObject.getString("update_views");
                            String created_at = jsonObject.getString("created_at");

                            String user_has_liked = jsonObject.getString("user_liked");
                            String user_has_rated = jsonObject.getString("user_rated");

                            int user_rating = Integer.parseInt(jsonObject.getString("user_rating"));


                            Integer views = Integer.parseInt(jsonObject.getString("views"));
                            Float ratings = 0.0f;
                            Integer total_rating = 0;
                            Float aspect_ratio = 1.0f;
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
                            dataModel.setMusicData(-1, music_id, type, title, artist_actors, music_genre, youtube_id, image, aspect_ratio, views, ratings, total_rating, likes, update_views, created_at,
                                    user_has_liked, user_has_rated, user_rating);

                            musicList.add(dataModel);

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


    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onCustomClick(int position, ImageView imageView) {
        Intent intent = new Intent(getActivity(), MusicDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(MUSIC_LIST, musicList);
        bundle.putInt(SELECTED_MUSIC, position);
        intent.putExtras(bundle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(), imageView, imageView.getTransitionName()).toBundle();
            startActivity(intent, explodeBundle);

        } else {
            startActivity(intent);
        }
    }

}
