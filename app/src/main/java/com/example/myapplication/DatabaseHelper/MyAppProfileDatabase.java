package com.example.myapplication.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DataModel.PunchModel;

import java.util.ArrayList;
import java.util.List;

public class MyAppProfileDatabase extends SQLiteOpenHelper {

    // Profile table
    public static final String STUDENT_TABLE = "STUDENT_TABLE";
    public static final String COLUMN_STUDENT_FIRSTNAME = "STUDENT_FIRSTNAME";
    public static final String COLUMN_STUDENT_LASTNAME = "STUDENT_LASTNAME";
    public static final String COLUMN_STUDENT_WEIGHT = "STUDENT_WEIGHT";
    public static final String COLUMN_STUDENT_HEIGHT = "STUDENT_HEIGHT";
    public static final String COLUMN_STUDENT_AGE = "STUDENT_AGE";
    public static final String COLUMN_ID = "ID";

    // Punch table
    private static final String PUNCH_TABLE = "PUNCH_TABLE";
    private static final String PUNCH_ID = "ID";
    private static final String PUNCH_ACCOUNT_ID = "ACCOUNT_ID";
    private static final String PUNCH_FORCE = "FORCE";
    private static final String PUNCH_DATE = "DATE";

    //TODO: Add in the names of the Punch tables. Also update the onCreate and onUpgrade methods.
    public ProfileModel pModel;


    public MyAppProfileDatabase(@Nullable Context context) {
        super(context, "MyAppDatabase.db", null, 3);
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
                + COLUMN_STUDENT_AGE + " STRING, "
                + COLUMN_STUDENT_WEIGHT + " FLOAT, "
                + COLUMN_STUDENT_HEIGHT + " FLOAT)";

        db.execSQL(createTableStatement);

        String createTable = "CREATE TABLE " + PUNCH_TABLE
                + " (" + PUNCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PUNCH_ACCOUNT_ID + "INTEGER, "
                + PUNCH_FORCE + " REAL, "
                + PUNCH_DATE + " INTEGER)";

        db.execSQL(createTable);
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

    // gets the last student added
    public int getLastStudentID() {
        String queryString = "SELECT * FROM " + STUDENT_TABLE + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        int id = -1;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(0);
        }

        cursor.close();

        return id;
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
                String columnAge = cursor.getString(3);
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
     * @param accountID
     * @return
     */
    public String getFirstNameFromDatabase(int accountID) {
        String studentFirstName;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_FIRSTNAME + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();

        studentFirstName = cursor.getString(cursor.getPosition());

        return studentFirstName;
    }

    public String getLastNameFromDatabase(int accountID) {
        String studentLastName;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_LASTNAME + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();

        studentLastName = cursor.getString(cursor.getPosition());

        return studentLastName;
    }

    public String getAgeFromDatabase(int accountID) {
        String studentAge;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_AGE + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();

        studentAge = cursor.getString(cursor.getPosition());

        return studentAge;
    }

    public String getWeightFromDatabase(int accountID) {
        String studentWeight;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_WEIGHT + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();

        studentWeight = cursor.getString(cursor.getPosition());

        return studentWeight;
    }

    public String getHeightFromDatabase(int accountID) {
        String studentHeight;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_HEIGHT + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();

        studentHeight = cursor.getString(cursor.getPosition());

        return studentHeight;
    }

    /**
     * Deletes a student from database
     * @param accountID : The account id of the student in the database
     * @return
     */
    public boolean deleteStudent(int accountID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        // Deletes the account profile
        Cursor cursor = db.rawQuery(queryString, null);
        db.close();

        if (!cursor.moveToFirst()) {
            removeAccountPunches(accountID);
            return true;
        }
        else
            return false;
    }

    public void editStudentProfile(int id, String fname, String lname, String age, String weight, String height) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase database = this.getWritableDatabase();

        cv.put(COLUMN_STUDENT_FIRSTNAME, fname);
        cv.put(COLUMN_STUDENT_LASTNAME, lname);
        cv.put(COLUMN_STUDENT_AGE, age);
        cv.put(COLUMN_STUDENT_WEIGHT, weight);
        cv.put(COLUMN_STUDENT_HEIGHT, height);

        database.update(STUDENT_TABLE, cv, COLUMN_ID + " = " + id, null);
    }


    /**
     * Creates a new punch in the table
     * @param punchModel
     * @return
     */

    public boolean addPunch(PunchModel punchModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newData = new ContentValues();

        newData.put(PUNCH_ACCOUNT_ID, punchModel.getAccountID());
        newData.put(PUNCH_FORCE, punchModel.getForce());
        newData.put(PUNCH_DATE, punchModel.getDate());

        long insert = db.insert(PUNCH_TABLE, null, newData);
        db.close();

        if (insert == -1)
            return false;
        else
            return true;
    }

    /**
     * Returns all punches in the database
     * @return
     */
    public List<PunchModel> getAllPunchesFromProfile(int accountID) {
        List<PunchModel> returnList = new ArrayList<>();

        String query = "SELECT * FROM " + PUNCH_TABLE + " WHERE " + PUNCH_ACCOUNT_ID + " = " + accountID;
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

    // Deletes all punches with given account id
    public void removeAccountPunches(int accountID) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(PUNCH_TABLE, PUNCH_ACCOUNT_ID + " = ?", new String[]{String.valueOf(accountID)});

        db.close();
    }


    /**
     * This method gets called whenever the version of the database changes
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE PUNCH_TABLE";

        db.execSQL(dropTable);

        String createTable = "CREATE TABLE " + PUNCH_TABLE
                + " (" + PUNCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PUNCH_ACCOUNT_ID + " INTEGER, "
                + PUNCH_FORCE + " REAL, "
                + PUNCH_DATE + " INTEGER)";

        db.execSQL(createTable);
    }
}
