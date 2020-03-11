package com.example.mbw.DB;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class PlaceDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PlaceBookmark.db";
    public static final String TABLE_NAME = "places";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PLACE = "place";
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";

    public PlaceDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +
                "(name text, place text, x text, y text, primary key(name))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS places");
        onCreate(db);
    }

    public boolean insertPlace(PlaceDB value) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db != null) {
            String sql = "insert or replace into places(name, place, x, y) values(?, ?, ?, ?)";
            Object[] params = {value.getName(), value.getPlace(), value.getX(), value.getY()};
            db.execSQL(sql, params);
        }
        return true;
    }

    public Integer deletePlace(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "name=?",
                new String[]{name});
    }

    public ArrayList<PlaceDB> getAllPlaces() {

        ArrayList<PlaceDB> array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from places", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            array_list.add(new PlaceDB(res.getString(res.getColumnIndex(COLUMN_NAME))
                    ,res.getString(res.getColumnIndex(COLUMN_PLACE))
                    ,res.getString(res.getColumnIndex(COLUMN_X)), res.getString(res.getColumnIndex(COLUMN_Y))));
            res.moveToNext();
        }

        return array_list;
    }
}

