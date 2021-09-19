package com.example.jbq.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.jbq.bean.PedometerBean;

public class DBHelper extends SQLiteOpenHelper {

    public static final int VERSION = 1;
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

    public static final String CREATE_SQL = "CREATE TABLE" + TABLE_NAME + "(" +
            "id integer PRIMARY KEY AUTOINCREMENT DEFAULT NULL," +
            "stepCount integer," +
            "calorie double," +
            "distance double DEFAULT null" +
            "pace integer," +
            "speed double," +
            "startTime Timestamp DEFAULT NULL," +
            "lastStepTime Timestamp DEFAULT NULL," +
            "day Timestamp DEFAULT NULL" +
            ")";


    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void writeToDatabase(PedometerBean data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("stepCount", data.getStepCount());
        values.put("calorie", data.getCalorie());
        values.put("distance", data.getDistance());
        values.put("pace", data.getPace());
        values.put("speed", data.getSpeed());
        values.put("startTime", data.getStartTime());
        values.put("lastStepTime", data.getLastStepTime());
        values.put("day", data.getCreateTime());
        db.insert(DBHelper.TABLE_NAME, null, values);
        db.close();
    }

    public PedometerBean
}
