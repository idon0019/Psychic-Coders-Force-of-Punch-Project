package com.example.sqlitetest.DBHelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.sqlitetest.Models.PunchModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper  extends SQLiteOpenHelper {

    private static final String PUNCH_TABLE = "PUNCH_TABLE";
    private static final String ID = "ID";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String FORCE = "FORCE";
    private static final String DATE = "DATE";

    /**
     * Creates or returns the application database.
     * @param context
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, "app_data.db", null, 1);
    }

    /**
     * Creates all tables in the database
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + PUNCH_TABLE + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACCOUNT_ID + " INTEGER, " + FORCE + " REAL, " + DATE + " INTEGER)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addPunch(PunchModel punchModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newData = new ContentValues();

        newData.put(ACCOUNT_ID, punchModel.getAccountID());
        newData.put(FORCE, punchModel.getForce());
        newData.put(DATE, punchModel.getDate());

        long insert = db.insert(PUNCH_TABLE, null, newData);
        db.close();

        if (insert == -1)
            return false;
        else
            return true;
    }

    public List<PunchModel> getAllPunches() {
        List<PunchModel> returnList = new ArrayList<>();

        String query = "SELECT * FROM " + PUNCH_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int accountId = cursor.getInt(1);
                double force = cursor.getDouble(2);
                long date = cursor.getLong(3);

                PunchModel punch = new PunchModel(id, accountId, force, date);
                returnList.add(punch);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return returnList;
    }

    public void removeLastPunch() {
        PunchModel punch;
        int punchID;

        String selectLast = "SELECT * FROM " + PUNCH_TABLE + " ORDER BY " + ID + " DESC LIMIT 1"; //selects the last item

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectLast, null);

        if (cursor.moveToFirst()) {
            punchID = cursor.getInt(0);
            db.delete(PUNCH_TABLE, ID + " = ?", new String[]{String.valueOf(punchID)});
        }

        cursor.close();
        db.close();
    }


}
