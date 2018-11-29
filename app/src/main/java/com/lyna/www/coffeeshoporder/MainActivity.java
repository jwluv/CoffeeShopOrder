package com.lyna.www.coffeeshoporder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private DrawerLayout drawerLayout;
    Toolbar toolbar;

    OrderDBHelper orderDBHelper;
    SQLiteDatabase mdb;

    TextView textViewDate;
    Button buttonNewOrder;
    GridView listViewOrderList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initializeNavigationView();

        orderDBHelper = new OrderDBHelper(this, "orders.db", null, 1);
        mdb = orderDBHelper.getWritableDatabase();

//        InitializeMenuDB();

        textViewDate = findViewById(R.id.textViewDate);
        buttonNewOrder = findViewById(R.id.buttonNewOrder);
        listViewOrderList = findViewById(R.id.listViewOrderList);

        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        textViewDate.setText(format.format(new Date()));

        buttonNewOrder.setOnClickListener(this);

        ArrayList<String> order_list = GetOrderListNotServed();

        if(order_list != null) {
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, order_list);
        }
        else {
            order_list = new ArrayList<>();
            order_list.add("Empty Order");
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, order_list);
        }

        listViewOrderList.setAdapter(arrayAdapter);
        listViewOrderList.setOnItemClickListener(this);
    }

    public void initializeNavigationView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.first){
                    getFragmentManager().beginTransaction().replace(R.id.main_framelayout, new NoticeFragment()).commit();

                }
                if(item.getItemId() == R.id.second){
                    getFragmentManager().beginTransaction().replace(R.id.main_framelayout, new LocationFragment()).commit();
                }

                drawerLayout.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String pos = parent.getItemAtPosition(position).toString();

        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, OrderActivity.class);
        bundle.putString("ItemPosFromMain", pos);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public ArrayList<String> GetOrderListNotServed() {

        ArrayList<String> order_list = new ArrayList<>();
        String query = "SELECT * FROM db_order where served == 0";
        Cursor cursor = mdb.rawQuery(query, null);

        while(cursor.moveToNext()) {
            String datetime;
            int order_id;

            order_id = cursor.getInt(cursor.getColumnIndex("_id"));
           // datetime = cursor.getString(cursor.getColumnIndex("datetime"));

           // order_list.add(String.valueOf(order_id) + ", " + datetime);
            order_list.add(String.valueOf(order_id));
        }

        if(order_list.size()>0)
            return order_list;
        else
            return null;

    }

    public void InitializeMenuDB(){

        String[] menus = new String[]{"Americano", "Caffe Latte", "Egg Toast", "Ham Toast", "Sandwitch", "Scone"};
        Integer[] prices = new Integer[]{2000, 3000, 3000, 4000, 4000, 2500};

        String query = "SELECT * FROM db_menu;";
        Cursor cursor = mdb.rawQuery(query, null);

        if(cursor.getCount()==0) {
            for (int i = 0; i < menus.length; i++)
                mdb.execSQL("INSERT INTO db_menu VALUES(null, '" + menus[i] + "', '" + prices[i] + "' );");
        }

    }

}
