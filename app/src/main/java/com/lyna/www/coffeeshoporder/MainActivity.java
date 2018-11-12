package com.lyna.www.coffeeshoporder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    OrderDBHelper orderDBHelper;
    SQLiteDatabase mdb;

    TextView textViewDate, textViewNumOfOrders;
    Button buttonNewOrder;
    GridView listViewOrderList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orderDBHelper = new OrderDBHelper(this, "orders.db", null, 1);
        mdb = orderDBHelper.getWritableDatabase();

//        InitializeMenuDB();

        textViewDate = findViewById(R.id.textViewDate);
        textViewNumOfOrders = findViewById(R.id.textViewNumOfOrders);
        buttonNewOrder = findViewById(R.id.buttonNewOrder);
        listViewOrderList = findViewById(R.id.listViewOrderList);

        //String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        textViewDate.setText(format.format(new Date()));

        buttonNewOrder.setOnClickListener(this);

        ArrayList<String> order_list = GetOrderList();

        if(order_list != null) {
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, order_list);
        }
        else {
            order_list = new ArrayList<>();
            order_list.add("Order list");
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, order_list);
        }

        listViewOrderList.setAdapter(arrayAdapter);
        listViewOrderList.setOnItemClickListener(this);
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

    public ArrayList<String> GetOrderList() {

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

//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        mdb.execSQL("INSERT INTO db_order VALUES(null, '" +currentDateTimeString + "', 5000, 0 );");
//
//        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        mdb.execSQL("INSERT INTO db_order VALUES(null, '" +currentDateTimeString + "', 8000, 0 );");
//
//        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        mdb.execSQL("INSERT INTO db_order VALUES(null, '" +currentDateTimeString + "', 7500, 0 );");
    }
}
