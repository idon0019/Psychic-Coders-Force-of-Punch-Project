package com.example.myapplication.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DataModel.PunchModel;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MyAppProfileDatabase extends SQLiteOpenHelper {

    // Profile table
    public static final String STUDENT_TABLE = "STUDENT_TABLE";
    public static final String COLUMN_STUDENT_PHOTO = "STUDENT_PHOTO";
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
        super(context, "MyAppDatabase.db", null, 5);
    }

    /**
     * Method gets called first time the database is accessed
     * @param database : Database to operate on.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        String createTableStatement = "CREATE TABLE " + STUDENT_TABLE
                + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_STUDENT_PHOTO + " TEXT, "
                + COLUMN_STUDENT_FIRSTNAME + " TEXT, "
                + COLUMN_STUDENT_LASTNAME + " TEXT, "
                + COLUMN_STUDENT_AGE + " TEXT, "
                + COLUMN_STUDENT_WEIGHT + " FLOAT, "
                + COLUMN_STUDENT_HEIGHT + " FLOAT)";

        database.execSQL(createTableStatement);

        String createTable = "CREATE TABLE " + PUNCH_TABLE
                + " (" + PUNCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PUNCH_ACCOUNT_ID + " INTEGER, "
                + PUNCH_FORCE + " REAL, "
                + PUNCH_DATE + " INTEGER)";

        database.execSQL(createTable);
    }


    /**
     * Add a student to the database
     * @param profileModel : Profile object to add into database.
     * @return True if insertion is successful, false if otherwise.
     */
    public boolean addStudent(ProfileModel profileModel) {
        SQLiteDatabase database = this.getWritableDatabase(); // Insert data
        ContentValues cv = new ContentValues(); // Stores data in pairs

        cv.put(COLUMN_STUDENT_PHOTO, profileModel.getPhotoPath());
        cv.put(COLUMN_STUDENT_FIRSTNAME, profileModel.getFirstName());
        cv.put(COLUMN_STUDENT_LASTNAME, profileModel.getLastName());
        cv.put(COLUMN_STUDENT_AGE, profileModel.getAge());
        cv.put(COLUMN_STUDENT_WEIGHT, profileModel.getWeight());
        cv.put(COLUMN_STUDENT_HEIGHT, profileModel.getHeight());

        long insert = database.insert(STUDENT_TABLE, null, cv);

        return insert != -1;
    }

    // gets the last student added
    public long getLastStudentID() {
        String queryString = "SELECT * FROM " + STUDENT_TABLE + " ORDER BY " + COLUMN_ID + " DESC LIMIT 1";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);
        long id = -1;

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getLong(0);
        }

        cursor.close();

        return id;
    }

    public ProfileModel findStudent(long accountID) {
        SQLiteDatabase database = this.getReadableDatabase();
        String queryString = "SELECT *" + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = database.rawQuery(queryString, null);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToNext();
        return createModelFromCursor(cursor);
    }

    private ProfileModel createModelFromCursor(Cursor cursor) {
        int columnID = cursor.getInt(0);
        String imageUri = cursor.getString(1);
        String columnFirstName = cursor.getString(2);
        String columnLastName = cursor.getString(3);
        String columnAge = cursor.getString(4);
        float columnWeight = cursor.getFloat(5);
        float columnHeight = cursor.getFloat(6);

        return new ProfileModel(columnID, imageUri, columnFirstName, columnLastName, columnAge, columnWeight, columnHeight);
    }
    /**
     * Retrieves all profiles in database
     * @return all user profiles in database
     */
    public List<ProfileModel> getStudents() {
        List<ProfileModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + STUDENT_TABLE;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.moveToFirst()) { // Moves cursor to the first row, returns false if list is empty
            do {
                ProfileModel newProfile = createModelFromCursor(cursor);
                returnList.add(newProfile);
            }while(cursor.moveToNext());
        }

        cursor.close();
        database.close();

        return returnList;
    }

    /**
     * Returns the image path of an account.
     * @param accountID Account ID to look at.
     * @return A string path.
     */
    public String getImagePathFromDatabase(long accountID) {
        String imagePath;

        SQLiteDatabase database = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_PHOTO + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.getCount() == 0) {
            return "";
        }

        cursor.moveToNext();
        imagePath = cursor.getString(cursor.getPosition());
        cursor.close();

        return imagePath;
    }

    /**
     * Returns a list of all paths used by all profiles.
     * @return A list of all stored paths.
     */
    public List<String> getAllImagePathsFromDatabase() {
        List<String> allImagePaths = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_STUDENT_PHOTO + " FROM " + STUDENT_TABLE;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() < 1) {
            return null;
        }

        cursor.moveToFirst();
        do {
            allImagePaths.add(cursor.getString(0));
        } while (cursor.moveToNext());

        cursor.close();
        return allImagePaths;
    }

    /**
     * Gets the first name of the student of which profile the user chose.
     * @param accountID : ID of account to look through.
     * @return Student first name.
     */
    public String getFirstNameFromDatabase(long accountID) {
        String studentFirstName;

        SQLiteDatabase database = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_FIRSTNAME + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = database.rawQuery(queryString, null);

        if (cursor.getCount() < 1 ) {
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

        SQLiteDatabase database = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_LASTNAME + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = database.rawQuery(queryString, null);
        cursor.moveToNext();
        studentLastName = cursor.getString(cursor.getPosition());
        cursor.close();

        return studentLastName;
    }

    /**
     * Gets the birth date of the student.
     * @param accountID : ID of account to look through.
     * @return A string representation of their birthday in dd/mm/yyyy
     */
    public String getDOBFromDatabase(long accountID) {
        String studentDOB;

        SQLiteDatabase database = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_AGE + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = database.rawQuery(queryString, null);
        cursor.moveToNext();
        studentDOB = cursor.getString(cursor.getPosition());
        cursor.close();

        return studentDOB;
    }

    /**
     * Returns student age.
     * @param accountID : ID of account to look through
     * @return Age of student.
     */
    public int getAgeFromDatabase(long accountID) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
        try {
            date = sdf.parse(getDOBFromDatabase(accountID));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) return 0;

        Calendar studentDOB = Calendar.getInstance();
        Calendar todayDate = Calendar.getInstance();

        studentDOB.setTime(date);

        int year = studentDOB.get(Calendar.YEAR);
        int month = studentDOB.get(Calendar.MONTH);
        int day = studentDOB.get(Calendar.DAY_OF_MONTH);

        studentDOB.set(year, month, day);

        int studentAge = todayDate.get(Calendar.YEAR) - studentDOB.get(Calendar.YEAR);

        if (todayDate.get(Calendar.DAY_OF_YEAR) < studentDOB.get(Calendar.DAY_OF_YEAR)) {
            studentAge--;
        }
        return studentAge;
    }

    /**
     * Gets the weight of the student.
     * @param accountID : ID of account to look through.
     * @return Student weight.
     */
    public String getWeightFromDatabase(long accountID) {
        String studentWeight;

        SQLiteDatabase database = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_WEIGHT + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = database.rawQuery(queryString, null);

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

        SQLiteDatabase database = this.getReadableDatabase();
        String queryString = "SELECT " + COLUMN_STUDENT_HEIGHT + " FROM " + STUDENT_TABLE + " WHERE " + COLUMN_ID + " = " + accountID;

        Cursor cursor = database.rawQuery(queryString, null);

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

        String filePath = getImagePathFromDatabase(accountID);
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(getImagePathFromDatabase(accountID));
            file.delete();
        }

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
    public boolean editStudentProfile(long id, String imageUri, String fname, String lname, String age, String weight, String height) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase database = this.getWritableDatabase();

        try{
            new ProfileModel(id, imageUri, fname, lname, age, Float.parseFloat(weight), Float.parseFloat(height));
        } catch (IllegalArgumentException e) {
            return false;
        }
        cv.put(COLUMN_STUDENT_PHOTO, imageUri);
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
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues newData = new ContentValues();

        newData.put(PUNCH_ACCOUNT_ID, punchModel.getAccountID());
        newData.put(PUNCH_FORCE, punchModel.getForce());
        newData.put(PUNCH_DATE, punchModel.getDate());

        long insert = database.insert(PUNCH_TABLE, null, newData);
        database.close();

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
        SQLiteDatabase database = this.getReadableDatabase();
        long value = 0;

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
            value = (long) cursor.getDouble(3);

        cursor.close();
        database.close();

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
        double value;

        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst())
            value = cursor.getDouble(2);
        else
            value = 0;

        cursor.close();

        return value;
    }

    /**
     * Checks if profile has previous punch data.
     * @param accountID : Account ID to check for.
     * @return True if account has punch data, false if otherwise.
     */
    public boolean hasPunchData(long accountID) {
        List<PunchModel> punches;
        punches = getAllPunchesFromProfile(accountID);

        return punches.size() != 0;
    }

    /**
     * Returns all punches of a given account id.
     * @param accountID : ID of account to look through.
     * @return An array of all matching punches.
     */
    public List<PunchModel> getAllPunchesFromProfile(long accountID) {
        List<PunchModel> returnList = new ArrayList<>();

        String query = "SELECT * FROM " + PUNCH_TABLE + " WHERE " + PUNCH_ACCOUNT_ID + " = " + accountID;
        SQLiteDatabase database = this.getReadableDatabase();

        Cursor cursor = database.rawQuery(query, null);

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
        database.close();
        return returnList;
    }

    /**
     * This method gets called whenever the version of the database changes
     * @param database : Database to migrate.
     * @param oldVersion : Old version
     * @param newVersion : New version
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String dropTable;
        String createTable;

        // Updates the database based on previous versions.
        switch (oldVersion) {
            case 2:
                dropTable = "DROP TABLE " + PUNCH_TABLE;

                database.execSQL(dropTable);

                createTable = "CREATE TABLE " + PUNCH_TABLE
                        + " (" + PUNCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + PUNCH_ACCOUNT_ID + " INTEGER, "
                        + PUNCH_FORCE + " REAL, "
                        + PUNCH_DATE + " INTEGER)";

                database.execSQL(createTable);
            case 3: // preserved for posterity. technically this case doesn't need execution since
                    // later migrations already take care of the transition safely.
//                dropTable = "DROP TABLE " + STUDENT_TABLE;
//
//                database.execSQL(dropTable); // deletes student table
//                database.execSQL("DELETE FROM "+PUNCH_TABLE); // must delete all values from punch table as well
//
//                createTable = "CREATE TABLE " + STUDENT_TABLE
//                        + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                        + COLUMN_STUDENT_FIRSTNAME + " TEXT, "
//                        + COLUMN_STUDENT_LASTNAME + " TEXT, "
//                        + COLUMN_STUDENT_AGE + " TEXT, "
//                        + COLUMN_STUDENT_WEIGHT + " FLOAT, "
//                        + COLUMN_STUDENT_HEIGHT + " FLOAT)";
//
//                database.execSQL(createTable);
            case 4:
                dropTable = "DROP TABLE " + STUDENT_TABLE;

                database.execSQL(dropTable); // deletes student table
                database.execSQL("DELETE FROM "+PUNCH_TABLE); // must delete all values from punch table as well

                createTable = "CREATE TABLE " + STUDENT_TABLE
                        + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_STUDENT_PHOTO + " TEXT, "
                        + COLUMN_STUDENT_FIRSTNAME + " TEXT, "
                        + COLUMN_STUDENT_LASTNAME + " TEXT, "
                        + COLUMN_STUDENT_AGE + " TEXT, "
                        + COLUMN_STUDENT_WEIGHT + " FLOAT, "
                        + COLUMN_STUDENT_HEIGHT + " FLOAT)";

                database.execSQL(createTable);

        }
    }
}
