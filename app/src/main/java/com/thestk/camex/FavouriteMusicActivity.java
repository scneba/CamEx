package com.thestk.camex;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
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

import com.thestk.camex.adapters.FavouriteMusicAdapter;
import com.thestk.camex.adapters.GridSpacingItemDecoration;
import com.thestk.camex.database.DataContract;
import com.thestk.camex.models.DataModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thestk.camex.HelperMethods.runLayoutAnimation;

/**
 * Created by Neba on 15-Oct-17.
 */

public class FavouriteMusicActivity extends AppCompatActivity implements FavouriteMusicAdapter.CustomRecyclerOnClick {

    private static final String MUSIC_LIST = "music_list";
    private static final String LAYOUT_MAN_STATE = "lay_man_state";
    private static boolean isTablet = false;
    private final String SELECTED_MUSIC = "selected_music";
    private final int MY_LOADER_ID = 500;
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
    private ArrayList<DataModel> musicList;
    private FavouriteMusicAdapter favouriteMusicAdapter;
    private boolean isTablet600 = false;
    private boolean isTablet720 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favourite_music_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        reload_rl.setVisibility(View.GONE);
        //restore state if already saved

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
        if (savedInstanceState != null && savedInstanceState.containsKey(MUSIC_LIST)) {

            musicList = savedInstanceState.getParcelableArrayList(MUSIC_LIST);
            favouriteMusicAdapter = new FavouriteMusicAdapter(this, musicList, this);
            recyclerView.setAdapter(favouriteMusicAdapter);
            mLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LAYOUT_MAN_STATE));

        } else {

            musicList = new ArrayList<>();
            favouriteMusicAdapter = new FavouriteMusicAdapter(this, musicList, this);
            recyclerView.setAdapter(favouriteMusicAdapter);

            if (getSupportLoaderManager().getLoader(MY_LOADER_ID) == null) {
                getSupportLoaderManager().initLoader(MY_LOADER_ID, null, new GetFavourites()).forceLoad();
            } else {
                getSupportLoaderManager().restartLoader(MY_LOADER_ID, null, new GetFavourites()).forceLoad();
            }

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
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
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onCustomClick(int position, ImageView imageView) {

        if (musicList.get(position).getType().equalsIgnoreCase("music")) {
            Intent intent = new Intent(this, MusicDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(MUSIC_LIST, musicList);
            bundle.putInt(SELECTED_MUSIC, position);
            intent.putExtras(bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this, imageView, imageView.getTransitionName()).toBundle();
                startActivity(intent, explodeBundle);

            } else {
                startActivity(intent);
            }
        } else if (musicList.get(position).getType().equalsIgnoreCase("movies")) {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(MUSIC_LIST, musicList);
            bundle.putInt(SELECTED_MUSIC, position);
            intent.putExtras(bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this, imageView, imageView.getTransitionName()).toBundle();
                startActivity(intent, explodeBundle);

            } else {
                startActivity(intent);
            }
        }
    }

    private class GetFavourites implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Uri uri = DataContract.DataEntry.CONTENT_URI;
            progressBar.setVisibility(View.VISIBLE);
            return new CursorLoader(getApplicationContext(), uri, null, null, null, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            progressBar.setVisibility(View.GONE);


            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    DataModel dataModel = new DataModel();
                    dataModel.setId(cursor.getInt(cursor.getColumnIndex(DataContract.DataEntry._ID)));
                    dataModel.setData_id(cursor.getInt(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_DATA_ID)));
                    dataModel.setTitle(cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_TITLE)));
                    dataModel.setArtist_actors(cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_ARTIST_ACTORS)));
                    dataModel.setYoutube_id(cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_YOUTUBE_ID)));
                    dataModel.setMusic_genre(cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_MUSIC_GENRE)));
                    dataModel.setImage(cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_IMAGE)));
                    dataModel.setAspect_ratio(cursor.getFloat(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_ASPECT_RATIO)));
                    dataModel.setType(cursor.getString(cursor.getColumnIndex(DataContract.DataEntry.COLUMN_TYPE)));

                    musicList.add(dataModel);
                    cursor.moveToNext();
                }
                Log.e("size", "" + musicList.size());
                DatabaseUtils.dumpCursor(cursor);

                //notify adapter to update
                runLayoutAnimation(recyclerView);
            }


            getSupportLoaderManager().destroyLoader(MY_LOADER_ID);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
}
