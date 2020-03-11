package com.example.mbw.AddPath;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {

        protected static final String TAG = "DataAdapter";

        // TODO : TABLE 이름을 명시해야함
        protected static final String TABLE_NAME = "stationInfo";

        private final Context mContext;
        private SQLiteDatabase mDb;
        private DataBaseHelper mDbHelper;

        public DataAdapter(Context context)
        {
            this.mContext = context;
            mDbHelper = new DataBaseHelper(mContext);
        }

        public DataAdapter createDatabase() throws SQLException
        {
            try
            {
                mDbHelper.createDataBase();
            }
            catch (IOException mIOException)
            {
                Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
                throw new Error("UnableToCreateDatabase");
            }
            return this;
        }

        public DataAdapter open() throws SQLException
        {
            try
            {
                mDbHelper.openDataBase();
                mDbHelper.close();
                mDb = mDbHelper.getReadableDatabase();
            }
            catch (SQLException mSQLException)
            {
                Log.e(TAG, "open >>"+ mSQLException.toString());
                throw mSQLException;
            }
            return this;
        }

        public void close()
        {
            mDbHelper.close();
        }


    public List getTableData(String selectedLine)
    {
        try
        {
            // line에 해당하는 stationName 받아오기
            String sql = "select stationName from stationInfo where line = '"+selectedLine+"'";

            // 모델 넣을 리스트 생성
            List itemList = new ArrayList();

            // TODO : 모델 선언
            //AddSubItem item = null;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                // 칼럼의 마지막까지
                while( mCur.moveToNext() ) {

                    // TODO : 커스텀 모델 생성
                    //item = new AddSubItem(null, null, null);

                    // TODO : Record 기술
                    //item.setId(mCur.getString(0));

                    // 리스트에 넣기
                    itemList.add(mCur.getString(0));
                }

            }
            return itemList;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }



    public String getStationId(String selectedLine, String stationName)
    {
        try
        {
            // line에 해당하는 stationName 받아오기
            String sql = "select id from stationInfo where line = '"+selectedLine+"' and stationName = '"+stationName+"'";

            // 모델 넣을 리스트 생성
            String stationId =null;
            // TODO : 모델 선언
            //AddSubItem item = null;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                // 칼럼의 마지막까지
                while( mCur.moveToNext() ) {

                    // TODO : 커스텀 모델 생성
                    //item = new AddSubItem(null, null, null);

                    // TODO : Record 기술
                    //item.setId(mCur.getString(0));

                    // 리스트에 넣기
                    stationId = mCur.getString(0);
                }

            }
            return stationId;
        }
        catch (SQLException mSQLException)
        {
            Log.e(TAG, "getTestData >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }



        /*
        ArrayList 버젼
        public ArrayList<AddSubItem> getTableData(String selectedLine)
        {
            try
            {
                // line에 해당하는 stationName 받아오기
                String sql = "select stationName from stationInfo where line = '"+selectedLine+"'";

                // 모델 넣을 리스트 생성
                ArrayList<AddSubItem> itemList = new ArrayList<AddSubItem>();

                // TODO : 모델 선언
                AddSubItem item = null;

                Cursor mCur = mDb.rawQuery(sql, null);
                if (mCur!=null)
                {
                    // 칼럼의 마지막까지
                    while( mCur.moveToNext() ) {

                        // TODO : 커스텀 모델 생성
                        item = new AddSubItem(null, null, null);

                        // TODO : Record 기술
                        item.setId(mCur.getString(0));
                        item.setStationName(mCur.getString(1));
                        item.setLine(mCur.getString(2));

                        // 리스트에 넣기
                        itemList.add(item);
                        Log.i("id", item.getId());
                        Log.i("name", item.getStationName());
                        Log.i("line", item.getLine());
                    }

                }
                return itemList;
            }
            catch (SQLException mSQLException)
            {
                Log.e(TAG, "getTestData >>"+ mSQLException.toString());
                throw mSQLException;
            }
        }

         */


}
