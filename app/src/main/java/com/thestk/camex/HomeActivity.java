package com.thestk.camex;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.thestk.camex.database.UserSharedPreference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {


    //bind views to make life easy
    @BindView(R.id.btnMusic)
    Button musicButton;
    @BindView(R.id.btnMovies)
    Button moviesButton;
    @BindView(R.id.btnFashion)
    Button fashionButton;
    @BindView(R.id.btnTourism)
    Button tourismButton;

    private UserSharedPreference userSharedPreference;

    //transition bundle
    private Bundle explodeBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userSharedPreference = new UserSharedPreference(getApplicationContext());


        // get menu from navigationView
        Menu menu = navigationView.getMenu();

        // find MenuItem to change
        MenuItem menuItem = menu.findItem(R.id.bar_log_out);

        if (userSharedPreference.isLoggedIn()) {
            menuItem.setTitle(getString(R.string.logout));
        } else {
            menuItem.setTitle(getString(R.string.login));
        }


        ButterKnife.bind(this);
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.updates));


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_icon) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.bar_add_content) {
            // Handle the camera action
        } else if (id == R.id.bar_feedback) {

        } else if (id == R.id.bar_share) {

        } else if (id == R.id.bar_favourites) {
            Intent intent = new Intent(this, FavouriteMusicActivity.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivity(intent, explodeBundle);

            } else {
                startActivity(intent);
            }
            return true;

        } else if (id == R.id.bar_log_out) {
            if (userSharedPreference.isLoggedIn()) {
                if (userSharedPreference.logoutUser()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.user_logged_out), Toast.LENGTH_SHORT).show();
                    item.setTitle(getString(R.string.login));
                }

            } else {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Bundle explodeBundle = ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this).toBundle();
                    startActivity(intent, explodeBundle);
                } else {
                    startActivity(intent);
                }

            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //butterknife button click methods
    @OnClick(R.id.btnMusic)
    void openMusicActivity() {
        Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, explodeBundle);
        } else {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnMovies)
    void openMoviesActivity() {
        Intent intent = new Intent(getApplicationContext(), MoviesActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, explodeBundle);
        } else {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnFashion)
    void openFashionActivity() {
        Intent intent = new Intent(getApplicationContext(), FashionActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, explodeBundle);
        } else {
            startActivity(intent);
        }
    }

    @OnClick(R.id.btnTourism)
    void openTourismActivity() {
        Intent intent = new Intent(getApplicationContext(), TourismActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            explodeBundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, explodeBundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
