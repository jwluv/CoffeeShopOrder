package com.lyna.www.coffeeshoporder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{
    OrderDBHelper orderDBHelper;
    SQLiteDatabase mdb;
    Cursor cursor;
    String order_query;

    TextView orderNumber;
    Button buttonOrderMenuEdit;
    Button buttonOrderPayment, buttonOrderCancel, buttonOrderCompleted;

    RecyclerView recyclerViewMenuList;
    RecyclerView.LayoutManager layoutManager;
    OrderItemAdapter orderItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        orderDBHelper = new OrderDBHelper(this, "orders.db", null, 1);
        mdb = orderDBHelper.getWritableDatabase();

        buttonOrderMenuEdit = findViewById(R.id.buttonOrderMenuEdit);
        buttonOrderMenuEdit.setOnClickListener(this);

        buttonOrderPayment = findViewById(R.id.buttonOrderPayment);
        buttonOrderPayment.setOnClickListener(this);

        buttonOrderCancel = findViewById(R.id.buttonOrderCancel);
        buttonOrderCancel.setOnClickListener(this);

        buttonOrderCompleted = findViewById(R.id.buttonOrderCompleted);
        buttonOrderCompleted.setOnClickListener(this);

        recyclerViewMenuList = findViewById(R.id.recyclerViewMenuList);

        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();

        layoutManager = new LinearLayoutManager(this);
        recyclerViewMenuList.setLayoutManager(layoutManager);
//        orderItemAdapter = new OrderItemAdapter(arrayList);

        orderItemAdapter = new OrderItemAdapter(this, arrayList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                long postId = position;
                // do what ever you want to do with it
            }
        });


        recyclerViewMenuList.setAdapter(orderItemAdapter);
        orderNumber = (TextView)findViewById(R.id.orderNumber);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {

//            Integer.parseInt(bundle.getString("ItemPosFromMain", "No Data"));
//            String str = (String)hashMap.get("menu") + (String)hashMap.get("ea");;

        }
        else {
            order_query = "SELECT max(_id) FROM db_order";
            Cursor cursor = mdb.rawQuery(order_query, null);

            if(cursor.getCount()>0){
                cursor.moveToFirst();
                int id = cursor.getInt(0);
                if(id != 0)
                    id += 1;
                orderNumber.setText("주문번호 : " + String.valueOf(id));
            }
            ReadMenuFromDB();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()) {
            case R.id.buttonOrderMenuEdit:
                intent = new Intent(this, MenuEditActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonOrderPayment:
                OrderPayment();
                break;
            case R.id.buttonOrderCancel:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.buttonOrderCompleted:
                OrderServingCompleted();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

        }

    }

    public void ReadMenuFromDB(){
        String query = "SELECT * FROM db_menu";
        Cursor cursor = mdb.rawQuery(query, null);

        while(cursor.moveToNext()) {
            String menu = cursor.getString(cursor.getColumnIndex("menu"));
            String price = cursor.getString(2);

            HashMap<String,Object> hashMap = new HashMap<String,Object>();
            hashMap.put("menu", menu);
            hashMap.put("price", price);
            hashMap.put("ea", "0");
            orderItemAdapter.addItem(20,hashMap);
        }
    }

    public void OrderPayment(){
        HashMap<String, Object> hashMap;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        int total_price = 0;

        for(int i=0; i<orderItemAdapter.getItemCount(); i++){
            hashMap = orderItemAdapter.getItem(i);
            if( (String)hashMap.get("ea") != "0"){
                String str_price = (String)hashMap.get("price");
                String str_ea = (String)hashMap.get("ea");
                int ea = Integer.parseInt(str_ea);
                mdb.execSQL("INSERT INTO db_order_menu VALUES('" + currentDateTimeString + "', '" + (String)hashMap.get("menu") + "', '" + ea + "' );");

                total_price += Integer.parseInt(str_price)*ea;
            }
        }
        if(total_price != 0)
            mdb.execSQL("INSERT INTO db_order VALUES(null, '" + currentDateTimeString + "', '" + total_price + "', 0);");
        else
            Toast.makeText(getApplicationContext(), "Add the menu if you want to order", Toast.LENGTH_SHORT).show();


//        String query = "SELECT max(_id) FROM db_order";
//        Cursor cursor = mdb.rawQuery(query, null);
//
//        if(cursor.getCount()>0) {
//            cursor.moveToFirst();
//            int id = cursor.getInt(0);
//
//            hashMap.put("price", editTextMenuEditPrice.getText().toString());
//            hashMap1. String.valueOf(id));
//        }
    }

    public void OrderServingCompleted(){
        String query = "UPDATE db_order SET served=1 WHERE _id='" + orderNumber.getText().toString() +"'";
        mdb.execSQL(query);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        String pos = adapterView.getItemAtPosition(i);
//
//        Bundle bundle = new Bundle();
//        Intent intent = new Intent(this, OrderActivity.class);
//        bundle.putString("ItemPosFromMain", pos);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }
}
