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

    public MyAppProfileDatabase(@Nullable Context context) {
        super(context, "MyAppDatabase.db", null, 4);
    }

    /**
     * Method gets called first time the database is accessed
     * @param db : Database to operate on.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + STUDENT_TABLE
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STUDENT_FIRSTNAME + " TEXT, "
                + COLUMN_STUDENT_LASTNAME + " TEXT, "
                + COLUMN_STUDENT_AGE + " TEXT, "
                + COLUMN_STUDENT_WEIGHT + " FLOAT, "
                + COLUMN_STUDENT_HEIGHT + " FLOAT)";

        db.execSQL(createTableStatement);

        String createTable = "CREATE TABLE " + PUNCH_TABLE
                + " (" + PUNCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PUNCH_ACCOUNT_ID + " INTEGER, "
                + PUNCH_FORCE + " REAL, "
                + PUNCH_DATE + " INTEGER)";

        db.execSQL(createTable);
    }


    /**
     * Add a student to the database
     * @param profileModel : Profile object to add into database.
     * @return True if insertion is successful, false if otherwise.
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

        return insert != -1;
    }

    // gets the last student added
    public long getLastStudentID() {
        String queryString = "SELECT * FROM " + STUDENT_TABLE + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        long id = -1;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(0);
        }

        cursor.close();

        return id;
    }

    public ProfileModel findStudent(long accountID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT *" + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToNext();
        return createModelFromCursor(cursor);
    }

    private ProfileModel createModelFromCursor(Cursor cursor) {
        int columnID = cursor.getInt(0);
        String columnFirstName = cursor.getString(1);
        String columnLastName = cursor.getString(2);
        String columnAge = cursor.getString(3);
        float columnWeight = cursor.getFloat(4);
        float columnHeight = cursor.getFloat(5);

        return new ProfileModel(columnID, columnFirstName, columnLastName, columnAge, columnWeight, columnHeight);
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
                ProfileModel newProfile = createModelFromCursor(cursor);
                returnList.add(newProfile);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return returnList;
    }

    /**
     * Gets the number of students in the database.
     * @return The number of students.
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
     * @param accountID : ID of account to look through.
     * @return Student first name.
     */
    public String getFirstNameFromDatabase(long accountID) {
        String studentFirstName;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_FIRSTNAME + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        if (cursor.getCount() < 0 ) {
            return "";
        }
        cursor.moveToNext();
        studentFirstName = cursor.getString(cursor.getPosition());
        cursor.close();

        return studentFirstName;
    }

    /**
     * Gets the last name of the student.
     * @param accountID : ID of account to look through.
     * @return Student last name.
     */
    public String getLastNameFromDatabase(long accountID) {
        String studentLastName;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_LASTNAME + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToNext();
        studentLastName = cursor.getString(cursor.getPosition());
        cursor.close();

        return studentLastName;
    }

    /**
     * Gets the age of the student.
     * @param accountID : ID of account to look through.
     * @return A string representation of their birthday in dd/mm/yyyy
     */
    public String getAgeFromDatabase(long accountID) {
        String studentAge;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_AGE + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);
        cursor.moveToNext();
        studentAge = cursor.getString(cursor.getPosition());
        cursor.close();

        return studentAge;
    }

    /**
     * Gets the weight of the student.
     * @param accountID : ID of account to look through.
     * @return Student weight.
     */
    public String getWeightFromDatabase(long accountID) {
        String studentWeight;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_WEIGHT + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();

        studentWeight = cursor.getString(cursor.getPosition());
        cursor.close();

        return studentWeight;
    }

    /**
     * Gets the height of the student.
     * @param accountID : ID of account to look through.
     * @return Student height.
     */
    public String getHeightFromDatabase(long accountID) {
        String studentHeight;

        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_HEIGHT + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = db.rawQuery(queryString, null);

        cursor.moveToNext();

        studentHeight = cursor.getString(cursor.getPosition());
        cursor.close();

        return studentHeight;
    }

    /**
     * Deletes a student from database and all punches of that student
     * @param accountID : The account id of the student in the database
     * @return True if a student was deleted.
     */
    public boolean deleteStudent(long accountID) {
        String where = "id=?";
        String[] args = {Long.toString(accountID)};
        SQLiteDatabase database = this.getWritableDatabase();
        int numDeleted = database.delete(STUDENT_TABLE, where, args);
        if (numDeleted > 0)
            database.delete(PUNCH_TABLE, PUNCH_ACCOUNT_ID + " = ?", args);

        database.close();
        return numDeleted > 0;
    }

    /**
     * Edits the student profile.
     * @param id : ID of account to edit.
     * @param fname : First name of student.
     * @param lname : Last name of student.
     * @param age : Age of student.
     * @param weight : Weight of student.
     * @param height : Height of student.
     * @return True if edit successful, false if otherwise.
     */
    public boolean editStudentProfile(long id, String fname, String lname, String age, String weight, String height) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase database = this.getWritableDatabase();

        try{
            new ProfileModel(id, fname, lname, age, Float.parseFloat(weight), Float.parseFloat(height));
        } catch (IllegalArgumentException e) {
            return false;
        }
        cv.put(COLUMN_STUDENT_FIRSTNAME, fname);
        cv.put(COLUMN_STUDENT_LASTNAME, lname);
        cv.put(COLUMN_STUDENT_AGE, age);
        cv.put(COLUMN_STUDENT_WEIGHT, weight);
        cv.put(COLUMN_STUDENT_HEIGHT, height);

        return (database.update(STUDENT_TABLE, cv, COLUMN_ID + " = " + id, null) > 0);
    }


    /**
     * Creates a new punch in the table
     * @param punchModel : Punch to insert into database.
     * @return True if insertion successful, false if otherwise.
     */
    public boolean addPunch(PunchModel punchModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues newData = new ContentValues();

        newData.put(PUNCH_ACCOUNT_ID, punchModel.getAccountID());
        newData.put(PUNCH_FORCE, punchModel.getForce());
        newData.put(PUNCH_DATE, punchModel.getDate());

        long insert = db.insert(PUNCH_TABLE, null, newData);
        db.close();

        return insert != -1;
    }

    /**
     * Returns the date of a specific punch by using account id and punch force.
     * @param accountID : ID of account to look at.
     * @param force : Force value to search for.
     * @return String representation of the punch date.
     */
    public long getDateFromPunchForce(long accountID, double force) {
        String query = "SELECT * FROM " + PUNCH_TABLE + " WHERE " + PUNCH_FORCE + " = " + force + " AND " + PUNCH_ACCOUNT_ID + " = " + accountID;
        SQLiteDatabase db = this.getReadableDatabase();
        long value = 0;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
            value = (long) cursor.getDouble(3);

        cursor.close();
        db.close();

        return value;
    }

    /**
     * Gets the highest punch score of account.
     * @param accountID : Account to look through.
     * @return Punch score.
     */
    public double getHighScore(long accountID) {
        String query = "SELECT * FROM " +
                PUNCH_TABLE + " WHERE " +
                PUNCH_ACCOUNT_ID + " = " +
                accountID + " ORDER BY " +
                PUNCH_FORCE + " DESC LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        double value = cursor.getDouble(2);
        cursor.close();

        return value;
    }

    /**
     * Returns all punches of a given account id.
     * @param accountID : ID of account to look through.
     * @return An array of all matching punches.
     */
    public List<PunchModel> getAllPunchesFromProfile(long accountID) {
        List<PunchModel> returnList = new ArrayList<>();

        String query = "SELECT * FROM " + PUNCH_TABLE + " WHERE " + PUNCH_ACCOUNT_ID + " = " + accountID;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                long accountId = cursor.getLong(1);
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

    /**
     * This method gets called whenever the version of the database changes
     * @param db : Database to migrate.
     * @param oldVersion : Old version
     * @param newVersion : New version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable;
        String createTable;

        // Updates the database based on previous versions.
        switch (oldVersion) {
            case 2:
                dropTable = "DROP TABLE " + PUNCH_TABLE;

                db.execSQL(dropTable);

                createTable = "CREATE TABLE " + PUNCH_TABLE
                        + " (" + PUNCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + PUNCH_ACCOUNT_ID + " INTEGER, "
                        + PUNCH_FORCE + " REAL, "
                        + PUNCH_DATE + " INTEGER)";

                db.execSQL(createTable);
            case 3:
                dropTable = "DROP TABLE " + STUDENT_TABLE;

                db.execSQL(dropTable); // deletes student table
                db.execSQL("DELETE FROM "+PUNCH_TABLE); // must delete all values from punch table as well

                createTable = "CREATE TABLE " + STUDENT_TABLE
                        + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_STUDENT_FIRSTNAME + " TEXT, "
                        + COLUMN_STUDENT_LASTNAME + " TEXT, "
                        + COLUMN_STUDENT_AGE + " TEXT, "
                        + COLUMN_STUDENT_WEIGHT + " FLOAT, "
                        + COLUMN_STUDENT_HEIGHT + " FLOAT)";

                db.execSQL(createTable);

        }
    }
}
