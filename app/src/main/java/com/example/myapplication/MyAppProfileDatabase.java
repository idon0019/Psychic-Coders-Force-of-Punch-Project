package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyAppProfileDatabase extends SQLiteOpenHelper {


    public static final String STUDENT_TABLE = "STUDENT_TABLE";
    public static final String COLUMN_STUDENT_FIRSTNAME = "STUDENT_FIRSTNAME";
    public static final String COLUMN_STUDENT_LASTNAME = "STUDENT_LASTNAME";
    public static final String COLUMN_STUDENT_WEIGHT = "STUDENT_WEIGHT";
    public static final String COLUMN_STUDENT_HEIGHT = "STUDENT_HEIGHT";
    public static final String COLUMN_STUDENT_AGE = "STUDENT_AGE";
    public static final String COLUMN_ID = "ID";

    public ProfileModel pModel;


    public MyAppProfileDatabase(@Nullable Context context) {
        super(context, "MyAppDatabase.db", null, 1);
    }

    /**
     * Method gets called first time the database is accessed
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + STUDENT_TABLE
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STUDENT_FIRSTNAME + " TEXT, "
                + COLUMN_STUDENT_LASTNAME + " TEXT, "
                + COLUMN_STUDENT_AGE + " INT, "
                + COLUMN_STUDENT_WEIGHT + " FLOAT, "
                + COLUMN_STUDENT_HEIGHT + " FLOAT)";

        db.execSQL(createTableStatement);
    }


    /**
     * Add a student to the database
     * @param profileModel
     * @return
     */
    public boolean addStudent(ProfileModel profileModel) {
        SQLiteDatabase db = this.getWritableDatabase(); // Insert data
        ContentValues cv = new ContentValues(); // Stores data in pairs

        cv.put(COLUMN_STUDENT_FIRSTNAME, profileModel.getFirstName());
        cv.put(COLUMN_STUDENT_LASTNAME, profileModel.getLastName());
        cv.put(COLUMN_STUDENT_AGE, profileModel.getAge());
        cv.put(COLUMN_STUDENT_WEIGHT, profileModel.getWeight());
        cv.put(COLUMN_STUDENT_HEIGHT, profileModel.getHeight());

        long insert = db.insert(STUDENT_TABLE, null, cv);

        if (insert == -1)
            return false;
        else
            return true;
    }

    /**
     * Retrieves all profiles in database
     * @return all user profiles in database
     */
    public List<ProfileModel> getStudents() {
        List<ProfileModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + STUDENT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst()) { // Moves cursor to the first row, returns false if list is empty
            do {
                int columnID = cursor.getInt(0);
                String columnFirstName = cursor.getString(1);
                String columnLastName = cursor.getString(2);
                int columnAge = cursor.getInt(3);
                float columnWeight = cursor.getFloat(4);
                float columnHeight = cursor.getFloat(5);

                ProfileModel newProfile = new ProfileModel(columnID, columnFirstName, columnLastName, columnAge, columnWeight, columnHeight);
                returnList.add(newProfile);

            }while(cursor.moveToNext());
        } else {
            // empty
        }

        cursor.close();
        db.close();

        return returnList;
    }

    /**
     * Gets the number of students in the database
     * @return
     */
    public int getNumberOfStudentsFromDatabase() {
        int num = 0;

        String stringQuery = "SELECT COUNT(*) FROM " + STUDENT_TABLE;
        Cursor cursor = getReadableDatabase().rawQuery(stringQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            num = cursor.getInt(0);
        }

        cursor.close();

        return num;
    }

    /**
     * Gets the first name of the student of which profile the user chose.
     * @param profileModel
     * @return
     */
    public String getFirstNameFromDatabase(ProfileModel profileModel) {
        String someString = "";

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_FIRSTNAME + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + profileModel.getId();

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();


        someString = cursor.getString(cursor.getPosition());


        return someString;
    }

    /**
     * Deletes a student from database
     * @param profileModel
     * @return
     */
    public boolean deleteStudent(ProfileModel profileModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + profileModel.getId();

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.moveToFirst())
            return true;
        else
            return false;
    }


    /**
     * This method gets called whenever the version of the database changes
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
