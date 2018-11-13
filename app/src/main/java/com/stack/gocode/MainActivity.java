package com.stack.gocode;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;

import com.stack.gocode.primaryFragments.ActionsFragment;
import com.stack.gocode.primaryFragments.ArduinoRunnerFragment;
import com.stack.gocode.primaryFragments.DebuggerFragment;
import com.stack.gocode.primaryFragments.DefuzzifierFragment;
import com.stack.gocode.primaryFragments.DifferenceFragment;
import com.stack.gocode.primaryFragments.FlagsFragment;
import com.stack.gocode.primaryFragments.FuzzyActionsFragment;
import com.stack.gocode.primaryFragments.FuzzyFlagFragment;
import com.stack.gocode.primaryFragments.ModesFragment;
import com.stack.gocode.primaryFragments.NeuralNetFragment;
import com.stack.gocode.primaryFragments.StartScreen;
import com.stack.gocode.primaryFragments.TablesFragment;
import com.stack.gocode.primaryFragments.VideoRecordingFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private boolean debug = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "The start button is disabled in debug mode.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_frame, new StartScreen()).commit();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new FlagsFragment()).commit();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_communicate) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new ArduinoRunnerFragment()).commit();
            // replace ArduinoRunnerFragment with DebuggerFragment
        } else if (id == R.id.nav_actions) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new ActionsFragment()).commit();
        } else if (id == R.id.nav_modes) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new ModesFragment()).commit();
        } else if (id == R.id.nav_tables) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new TablesFragment()).commit();
        } else if (id == R.id.nav_flags) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FlagsFragment()).commit();
        } else if (id == R.id.nav_fuzzy_flags) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FuzzyFlagFragment()).commit();
        } else if (id == R.id.nav_defuzzifiers) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new DefuzzifierFragment()).commit();
        } else if (id == R.id.nav_fuzzy_actions) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FuzzyActionsFragment()).commit();
        } else if (id == R.id.nav_differences) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new DifferenceFragment()).commit();
        } else if (id == R.id.nav_video_record) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new VideoRecordingFragment()).commit();
        } else if (id == R.id.nav_neural_net) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new NeuralNetFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
