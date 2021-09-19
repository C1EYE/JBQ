package com.example.jbq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.jbq.bean.PedometerBean;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DB_NAME = "PedmometerDB";
    public static final String TABLE_NAME = "pedometer";
    public static final String[] COLUMNS =
            {
                    "id",
                    "stepCount",
                    "calorie",
                    "distance",
                    "pace",
                    "speed",
                    "startTime",
                    "lastStepTime",
                    "day"
            };

    public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            "id integer PRIMARY KEY AUTOINCREMENT DEFAULT NULL," +
            "stepCount integer," +
            "calorie float," +
            "distance float DEFAULT NULL," +
            "pace integer," +
            "speed float," +
            "startTime Timestamp DEFAULT NULL," +
            "lastStepTime Timestamp DEFAULT NULL," +
            "day Timestamp DEFAULT NULL" +
            ")";


    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    public DBHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void writeToDatabase(PedometerBean data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stepCount", data.getStepCount());
        values.put("calorie", data.getCalorie());
        values.put("distance", data.getDistance());
        values.put("pace", data.getPace());
        values.put("speed", data.getSpeed());
        values.put("startTime", data.getStartTime());
        values.put("lastStepTime", data.getLastStepTime());
        values.put("day", data.getDay());
        db.insert(DBHelper.TABLE_NAME, null, values);
        db.close();
    }

    public PedometerBean getByDaytime(long dayTime) {
        Cursor cursor = null;
        PedometerBean bean = new PedometerBean();
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.rawQuery("select * from" + DBHelper.TABLE_NAME + " where day=" + dayTime, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[1]));
                float calorie = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMNS[2]));
                float distance = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[4]));
                float speed = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[7]));
                long day = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[8]));
                bean.setId(id);
                bean.setStepCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(day);
            }
        }
        cursor.close();
        db.close();
        return bean;
    }

    public ArrayList<PedometerBean> getFromDatabase() {
        int pageSize = 20;
        int offVal = 0;
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        cursor = db.query(DBHelper.TABLE_NAME, null, null, null, null, null,
                "day desc limit " + pageSize + " offset " + offVal,
                null);
        if (cursor != null && cursor.getCount() > 0) {
            ArrayList<com.example.jbq.bean.PedometerBean> data = new ArrayList<>();
            while (cursor.moveToNext()) {
                PedometerBean bean = new PedometerBean();
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[0]));
                int stepCount = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[1]));
                float calorie = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMNS[2]));
                float distance = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMNS[3]));
                int pace = cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMNS[4]));
                float speed = cursor.getFloat(cursor.getColumnIndex(DBHelper.COLUMNS[5]));
                long startTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[6]));
                long lastStepTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[7]));
                long createTime = cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMNS[8]));
                bean.setId(id);
                bean.setStepCount(stepCount);
                bean.setCalorie(calorie);
                bean.setDistance(distance);
                bean.setPace(pace);
                bean.setSpeed(speed);
                bean.setStartTime(startTime);
                bean.setLastStepTime(lastStepTime);
                bean.setDay(createTime);
                data.add(bean);
            }
            cursor.close();
            db.close();
            return data;
        }
        return null;
    }

    public void updateToDatabase(ContentValues values, long datTime) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.update(TABLE_NAME, values, "day=?", new String[]{String.valueOf(datTime)});
        database.close();
    }
}

