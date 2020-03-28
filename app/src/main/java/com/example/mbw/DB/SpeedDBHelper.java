package com.example.mbw.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SpeedDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WalkingSpeed.db";
    public static final String TABLE_NAME = "walkingSpeed";
    public static final String COLUMN_SPEED = "speed";

    public SpeedDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME +
                "(id int, speed text, primary key(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
        onCreate(db);
    }

    public boolean insertSpeed(int id, String speed) {
        SQLiteDatabase db = this.getWritableDatabase();
        if(db != null) {
            String sql = "insert or replace into walkingSpeed(id, speed) values(?, ?)";
            Object[] params = {id, speed};
            db.execSQL(sql, params);
        }

        return true;
    }


    public int count(){
        int cnt;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from walkingSpeed", null);
        cnt = cursor.getCount();
        return cnt;
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }

    public String getSpeed() {

        String speed;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from walkingSpeed", null);
        res.moveToFirst();
        speed = res.getString(res.getColumnIndex(COLUMN_SPEED));

        return speed;
    }
}
