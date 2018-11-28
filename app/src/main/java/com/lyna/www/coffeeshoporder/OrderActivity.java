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

public class OrderActivity extends AppCompatActivity implements View.OnClickListener{
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

    TextView textViewOrderedMenu, textViewStatus;

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

        orderItemAdapter = new OrderItemAdapter(this, arrayList);

        recyclerViewMenuList.setAdapter(orderItemAdapter);
        orderNumber = (TextView)findViewById(R.id.orderNumber);

        textViewOrderedMenu = findViewById(R.id.textViewOrderedMenu);
        textViewStatus = findViewById(R.id.textViewStatus);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {

            int id = Integer.parseInt(bundle.getString("ItemPosFromMain", "No Data"));
            GetOrderItem(id);
            orderNumber.setText(String.valueOf(id));
//            buttonOrderPayment.setBackgroundResource(R.drawable.button_payment_completed);
        }
        else {
            order_query = "SELECT max(_id) FROM db_order";
            Cursor cursor = mdb.rawQuery(order_query, null);

            if(cursor.getCount()>0){
                cursor.moveToFirst();
                int id = cursor.getInt(0);
                if(id != 0)
                    id += 1;
                orderNumber.setText(String.valueOf(id));
            }
            ReadMenuFromDB();
//            buttonOrderPayment.setBackgroundResource(R.drawable.button_payment);
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
                break;

        }

    }

    public void GetOrderItem(int order_id) {

        ArrayList<String> order_list = new ArrayList<>();
        String query = "SELECT * FROM db_order where _id == '" + order_id + "'";
        Cursor cursor = mdb.rawQuery(query, null);
        String str = "";

        orderNumber.setText(String.valueOf(order_id));

        if(cursor.moveToFirst()) {
            String datetime;
            int total_price;

            datetime = cursor.getString(cursor.getColumnIndex("datetime"));
            total_price = cursor.getInt(cursor.getColumnIndex("totalprice"));

            query = "SELECT * FROM db_order_menu where datetime == '" + datetime + "'";
            cursor = mdb.rawQuery(query, null);

            while(cursor.moveToNext()) {
               String menu = cursor.getString(cursor.getColumnIndex("menu"));
               int ea  = cursor.getInt(cursor.getColumnIndex("ea"));

               if(ea != 0)
                 str += menu + ": " + ea + " 개\n";
            }
            str += "\nTotal: " + String.valueOf(total_price) + "원";
            textViewOrderedMenu.setText(str);
            textViewStatus.setText("\n결제완료/서빙 준비중");
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

        String query = "SELECT * FROM db_order where _id == '" + Integer.parseInt(orderNumber.getText().toString()) + "'";
        Cursor cursor = mdb.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            Toast.makeText(getApplicationContext(), "이미 결제된 주문입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

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
        if(total_price != 0) {
            mdb.execSQL("INSERT INTO db_order VALUES(null, '" + currentDateTimeString + "', '" + total_price + "', 0);");

            textViewStatus.setText("\n결제완료/서빙 준비중");
        }
        else
            Toast.makeText(getApplicationContext(), "Add the menu if you want to order", Toast.LENGTH_SHORT).show();

    }

    public void OrderServingCompleted(){

        int i = Integer.parseInt(orderNumber.getText().toString());
        String query = "SELECT * FROM db_order where _id == '" + Integer.parseInt(orderNumber.getText().toString()) + "'";
        Cursor cursor = mdb.rawQuery(query, null);

        if(!cursor.moveToFirst()) {
            Toast.makeText(getApplicationContext(), "미 결제된 주문입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        query = "UPDATE db_order SET served=1 WHERE _id='" + orderNumber.getText().toString() +"'";
        mdb.execSQL(query);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void displayOrder() {

        HashMap<String, Object> hashMap;
        int total_price = 0;
        String strOrder = "";

        for(int i=0; i<orderItemAdapter.getItemCount(); i++){
            hashMap = orderItemAdapter.getItem(i);
            if( Integer.parseInt((String)hashMap.get("ea")) != 0){
                String str_price = (String)hashMap.get("price");
                String str_ea = (String)hashMap.get("ea");
                int ea = Integer.parseInt(str_ea);

                total_price += Integer.parseInt(str_price)*ea;
                strOrder += (String)hashMap.get("menu") + ": " + ea + "개 \n";
            }


        }

        strOrder += "\nTotal: " + String.valueOf(total_price) + " 원";

        textViewOrderedMenu.setText(strOrder);
    }
}
