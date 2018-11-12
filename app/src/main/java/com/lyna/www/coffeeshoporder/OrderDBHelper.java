package com.lyna.www.coffeeshoporder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrderDBHelper extends SQLiteOpenHelper {
    private static final String name = "";
    private static final SQLiteDatabase.CursorFactory factory = null;
    private static final int version = 1;

    public OrderDBHelper(Context context) {
        super(context, name, factory, version);
    }

    public OrderDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE db_menu (_id INTEGER PRIMARY KEY AUTOINCREMENT, menu TEXT, price integer);");
        db.execSQL("CREATE TABLE db_order (_id INTEGER PRIMARY KEY AUTOINCREMENT, datetime TEXT, totalprice integer, served integer);");
        db.execSQL("CREATE TABLE db_order_menu (datetime TEXT, menu TEXT, ea integer);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE db_menu ;");
        db.execSQL("DROP TABLE db_order ;");
        onCreate(db);
//        Toast.makeText(this.,"onUpgrade", Toast.LENGTH_LONG).show();
    }

    public void deleteRecord(SQLiteDatabase mdb, String country) {
       // mdb.execSQL("DELETE FROM db_menu WHERE country='" + country + "';");
    }

}
