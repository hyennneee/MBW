package com.example.mbw.DB;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class RouteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RouteBookmark.db";
    public static final String TABLE_NAME = "routesBM";
    public static final String COLUMN_DEPARTURE = "departure";
    public static final String COLUMN_DESTINATION = "destination";
    public static final String COLUMN_SX = "sx";
    public static final String COLUMN_SY = "sy";
    public static final String COLUMN_EX = "ex";
    public static final String COLUMN_EY = "ey";

    public RouteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +
                "(departure text, destination text , sx text, sy text, ex text, ey text, primary key(departure, destination))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS routesBM");
        onCreate(db);
    }

    public boolean insertRouteBM(RouteDB value) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db != null) {
            String sql = "insert or replace into routesBM(departure, destination, sx, sy, ex, ey) values(?, ?, ?, ?, ?, ?)";
            Object[] params = {value.getDeparture(), value.getDestination(), value.getSX(), value.getSY(), value.getEX(), value.getEY()};
            db.execSQL(sql, params);
        }

        return true;
    }

    public Integer deleteRouteBM(String departure, String destination) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "departure=? and destination=?",
                new String[]{departure, destination});
    }

    public ArrayList<RouteDB> getAllRoutesBM() {

        ArrayList<RouteDB> array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from routesBM", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(new RouteDB(res.getString(res.getColumnIndex(COLUMN_DEPARTURE))
                    ,res.getString(res.getColumnIndex(COLUMN_DESTINATION))
                    ,res.getString(res.getColumnIndex(COLUMN_SX)), res.getString(res.getColumnIndex(COLUMN_SY)), res.getString(res.getColumnIndex(COLUMN_EX)), res.getString(res.getColumnIndex(COLUMN_EY))));
            res.moveToNext();
        }

        return array_list;
    }
}

