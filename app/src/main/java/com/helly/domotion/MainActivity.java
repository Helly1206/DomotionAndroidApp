package com.helly.domotion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static String Credentials = "";
    private static boolean LoginDone = false;
    private static String CurrentFragment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref),Context.MODE_PRIVATE);
        boolean SettingsPassword = sharedPref.getBoolean(getString(R.string.saved_password), true);

        if ((SettingsPassword) && (!LoginDone)) {
            LoginDialog loginDialog = new LoginDialog();
            loginDialog.showDialog(getSupportFragmentManager());
            if (CurrentFragment.isEmpty()) {
                CurrentFragment = getResources().getString(R.string.nav_login);
            }
        } else {
            if (CurrentFragment.isEmpty()) {
                CurrentFragment = getResources().getString(R.string.nav_devices);
            }
        }
        showFragment(CurrentFragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log) {
            Intent intent = new Intent(this, LogActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_browser) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref),Context.MODE_PRIVATE);
            String url = sharedPref.getString(getString(R.string.saved_url), getString(R.string.settings_default_url));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_devices) {
            CurrentFragment = getResources().getString(R.string.nav_devices);
        } else if (id == R.id.nav_controls) {
            CurrentFragment = getResources().getString(R.string.nav_controls);
        } else if (id == R.id.nav_timers) {
            CurrentFragment = getResources().getString(R.string.nav_timers);
        } else if (id == R.id.nav_holidays) {
            CurrentFragment = getResources().getString(R.string.nav_holidays);
        } else {
            CurrentFragment = getResources().getString(R.string.nav_none);
        }

        showFragment(CurrentFragment);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void setCredentials(String cred) {
        Credentials = cred;
    }

    public static String getCredentials() {
        return Credentials;
    }

    public static void setLoginDone(boolean Done) {
        LoginDone = Done;
    }

    public void Restart() {
        if ((CurrentFragment.isEmpty()) || (CurrentFragment.equals(getResources().getString(R.string.nav_login)))) {
            CurrentFragment = getResources().getString(R.string.nav_devices);
        }
        showFragment(CurrentFragment);
    }

    private void showFragment(String CurrentFragment) {
        Fragment fragment = null;
        int ImageResource = R.drawable.ic_lightbulb_outline_black;
        TextView tv = findViewById(R.id.textViewFragment);
        ImageView iv = findViewById(R.id.imageViewFragment);

        if (CurrentFragment.equals(getResources().getString(R.string.nav_devices))) {
            fragment = new DevicesFragment();
            ImageResource = R.drawable.ic_lightbulb_outline_black;
        } else if (CurrentFragment.equals(getResources().getString(R.string.nav_controls))) {
            fragment = new ControlsFragment();
            ImageResource = R.drawable.ic_settings_remote_black;
        } else if (CurrentFragment.equals(getResources().getString(R.string.nav_timers))) {
            fragment = new TimersFragment();
            ImageResource = R.drawable.ic_timer_black;
        } else if (CurrentFragment.equals(getResources().getString(R.string.nav_holidays))) {
            fragment = new HolidaysFragment();
            ImageResource = R.drawable.ic_people_outline_black;
        }

        iv.setImageResource(ImageResource);
        if (!CurrentFragment.isEmpty()) {
            tv.setText(CurrentFragment);
        } else {
            tv.setText(getResources().getString(R.string.nav_none));
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment_area, fragment);
            ft.commit();
        }
    }
}