package com.lyna.www.coffeeshoporder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuEditActivity extends AppCompatActivity implements View.OnClickListener{

    OrderDBHelper orderDBHelper;
    SQLiteDatabase mdb;

    EditText editTextMenuEditName, editTextMenuEditPrice;
    Button buttonMenuEditAdd;
    RecyclerView recyclerViewMenuItem;
    Button buttonMenuEditSave, buttonMenuEditCancel;

    RecyclerView.LayoutManager layoutManager;
    MenuItemAdapter menuItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);

        orderDBHelper = new OrderDBHelper(this, "orders.db", null, 1);
        mdb = orderDBHelper.getWritableDatabase();

        editTextMenuEditName = findViewById(R.id.editTextMenuEditName);
        editTextMenuEditPrice = findViewById(R.id.editTextMenuEditPrice);
        buttonMenuEditAdd = findViewById(R.id.buttonMenuEditAdd);
        recyclerViewMenuItem = findViewById(R.id.recyclerViewMenuItem);
        buttonMenuEditSave = findViewById(R.id.buttonMenuEditSave);
        buttonMenuEditCancel = findViewById(R.id.buttonMenuEditCancel);

        buttonMenuEditAdd.setOnClickListener(this);
        buttonMenuEditSave.setOnClickListener(this);
        buttonMenuEditCancel.setOnClickListener(this);

        ArrayList<HashMap<String,Object>> arrayList = new ArrayList<HashMap<String,Object>>();

        recyclerViewMenuItem = (RecyclerView) findViewById(R.id.recyclerViewMenuItem);

        layoutManager = new LinearLayoutManager(this);
        recyclerViewMenuItem.setLayoutManager(layoutManager);
        menuItemAdapter = new MenuItemAdapter(arrayList);
        recyclerViewMenuItem.setAdapter(menuItemAdapter);

        ReadMenuFromDB();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.buttonMenuEditAdd:
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("menu", editTextMenuEditName.getText().toString());
                hashMap.put("price", editTextMenuEditPrice.getText().toString());
                menuItemAdapter.addItem(20, hashMap);
                editTextMenuEditName.setText("");
                editTextMenuEditPrice.setText("");
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                break;
            case R.id.buttonMenuEditSave:
                mdb.execSQL("DELETE FROM db_menu");
                for (int i = 0; i < menuItemAdapter.getItemCount(); i++) {

                        HashMap<String,Object> hashMap1 = menuItemAdapter.getItem(i);
                        mdb.execSQL("INSERT INTO db_menu VALUES(null, '" + hashMap1.get("menu") + "', '" + hashMap1.get("price") + "' );");

                }
                break;
            case R.id.buttonMenuEditCancel:
                Intent intent = new Intent(this, MainActivity.class);
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
            menuItemAdapter.addItem(20,hashMap);
        }
    }
}
