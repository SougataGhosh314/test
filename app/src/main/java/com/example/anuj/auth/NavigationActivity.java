package com.example.anuj.auth;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anuj.auth.SessionManagement.ServiceManager;
import com.example.anuj.auth.SessionManagement.SessionManager;

public class NavigationActivity extends AppCompatActivityExt
        implements NavigationView.OnNavigationItemSelectedListener  {

    SessionManager sessionManager;
    TextView rsmiText;
    View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Utils.darkenStatusBar(this, R.color.colorPrimary);

        sessionManager = new SessionManager(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            String str = SessionManager.getMemberShipNo();
            Menu menu = navigationView.getMenu();
            v = navigationView.getHeaderView(0);
            rsmiText = v.findViewById(R.id.rsmIDNev);
            rsmiText.setText("RSAMI ID: "+str+"");
            //menu.findItem(R.id.nav_pkg_manage).setVisible(false);//In case you want to remove menu item
            navigationView.setNavigationItemSelectedListener(this);
        }
//        rsmiIDNev = navigationView.findViewById(R.id.rsmIDNev);
//        rsmiIDNev.setText("R.S.M.I ID No: "+sessionManager.getMemberShipNo());


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.movie_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, MovieActivity.class);
                startActivity(intent);
            }
        });

        setClickListeners();

    }

    public void setClickListeners() {
        findViewById(R.id.sports_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.sanjog_hall_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.bar_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.swimming_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.party_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.trinco_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.guest_activity_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NavigationActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            Intent intent = new Intent(NavigationActivity.this, HistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_facilities) {
            Intent intent = new Intent(NavigationActivity.this, FacilitiesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(NavigationActivity.this, ContactUsActivity.class);
            startActivity(intent);

        }  else if (id == R.id.nav_membershipRules) {
            Intent intent = new Intent(NavigationActivity.this, MembershipRulesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about_app) {
            Intent intent = new Intent(NavigationActivity.this, AboutAppActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notices) {
            Intent intent = new Intent(NavigationActivity.this, NoticesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(NavigationActivity.this, FeedbackActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_announcements) {
            Intent intent = new Intent(NavigationActivity.this, AnnouncementsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nevmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        if (item.getItemId() == R.id.feedback_icon) {
            startActivity(new Intent(NavigationActivity.this, FeedbackActivity.class));
        }
*/
        ProgressDialog mProgress;
        if (item.getItemId() == R.id.logout_icon) {
            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Signing Out");
            mProgress.show();

            sessionManager.logoutUser();
        }



        return super.onOptionsItemSelected(item);
    }



}
