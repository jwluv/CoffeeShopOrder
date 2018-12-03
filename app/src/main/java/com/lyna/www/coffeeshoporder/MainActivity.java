package com.lyna.www.coffeeshoporder;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView toolbar_playpause;
    int playState = 0;

    LinearLayout buttonHome, buttonNewOrder, buttonMenuEdit;

    final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 0;

    private MusicService mServiceBinder;
    private ServiceConnection myConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            mServiceBinder=((MusicService.MyBinder) binder).getService();
        }
        public void onServiceDisconnected(ComponentName className) { mServiceBinder = null; }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        }

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_playpause = toolbar.findViewById(R.id.toolbar_playpause);
        toolbar_playpause.setOnClickListener(this);

        buttonHome = findViewById(R.id.buttonHome);
        buttonNewOrder = findViewById(R.id.buttonNewOrder);
        buttonMenuEdit = findViewById(R.id.buttonMenuEdit);

        buttonHome.setOnClickListener(this);
        buttonNewOrder.setOnClickListener(this);
        buttonMenuEdit.setOnClickListener(this);

        initializeNavigationView();

        getFragmentManager().beginTransaction().replace(R.id.main_frame, new OrderListFragment()).commit();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION:
                if(grantResults.length > 0 || grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.i("", "Permission(Access Course Location) has been granted by user");
                break;
        }
    }

    public void initializeNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.main_navigationView);

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.nav_notice){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, new NoticeFragment()).commit();
        }
        if(menuItem.getItemId() == R.id.nav_location){
            Intent intent = new Intent(this, MapActivity.class);
            startActivity(intent);
        }
        if(menuItem.getItemId() == R.id.nav_sale){
            getFragmentManager().beginTransaction().replace(R.id.main_frame, new SaleFragment()).commit();
        }


        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onClick(View v) {

        if(v == toolbar_playpause) {
            if (playState == 1) {
                playState = 0;
                mServiceBinder.stop();
                toolbar_playpause.setBackgroundResource(R.drawable.baseline_play_circle_outline_black_18dp);
            } else {
                playState = 1;
                mServiceBinder.play(1);
                toolbar_playpause.setBackgroundResource(R.drawable.baseline_pause_circle_outline_black_18dp);
            }
        }
        else if(v == buttonHome)
            getFragmentManager().beginTransaction().replace(R.id.main_frame, new OrderListFragment()).commit();
        else if(v == buttonNewOrder)
            getFragmentManager().beginTransaction().replace(R.id.main_frame, new OrderFragment()).commit();
        else if(v == buttonMenuEdit)
            getFragmentManager().beginTransaction().replace(R.id.main_frame, new MenuEditFragment()).commit();
    }

}
