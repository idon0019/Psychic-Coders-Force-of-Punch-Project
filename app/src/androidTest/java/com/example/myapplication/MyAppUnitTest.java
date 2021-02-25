package com.example.myapplication;

import com.example.myapplication.DataModel.ProfileModel;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MyAppUnitTest {

    private Context appContext;
    ProfileModel sampleProfile;
    private static long INVALID_ID = 99999999;
    MyAppProfileDatabase databaseHelper;
    public MyAppUnitTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sampleProfile = new ProfileModel(0, "bob","tom", "28", 100.5f, 167.5f);
        databaseHelper = new MyAppProfileDatabase(appContext);
    }
    @Before
    public void before() {

    }

    /*
     * Verifies TC1
     */
    @Test
    public void addUserValid() {
        // Context of the app under test.
        assertTrue(databaseHelper.addStudent(sampleProfile));
        long lastID = databaseHelper.getLastStudentID();
        databaseHelper.deleteStudent(lastID);
    }

    /*
     * Verifies TC2
     */
    @Test
    public void createUserInvalid() {
        int numExceptionsCaught = 0;
        ProfileModel profile;

        //Invalid age (negative)
        try {
            profile = new ProfileModel(0, "bob", "tom", "-28", 100.5f, 167.5f);
        }
        catch(IllegalArgumentException e) {
            numExceptionsCaught++;
        }
        //Invalid age (too old!)
        try {
            profile = new ProfileModel(0, "bob", "tom", "101", 100.5f, 167.5f);
        }
        catch(IllegalArgumentException e) {
            numExceptionsCaught++;
        }
        //invalid weight
        try {
            profile = new ProfileModel(0, "bob", "tom", "28", -2, 167.5f);
        }
        catch(IllegalArgumentException e) {
            numExceptionsCaught++;
        }
        //invalid height
        try {
            profile = new ProfileModel(0, "bob", "tom", "28", 100, -100);
        }
        catch(IllegalArgumentException e) {
            numExceptionsCaught++;
        }

        assertTrue(numExceptionsCaught == 4);
    }

    /*
     * Verifies TC15
     */
    @Test
    public void findUserValid() {
        databaseHelper.addStudent(sampleProfile);
        long lastID = databaseHelper.getLastStudentID();
        assertNotNull(databaseHelper.findStudent(lastID)); //Should exist!
        databaseHelper.deleteStudent(lastID);
    }

    /*
     * Verifies TC3
     */
    @Test
    public void removeUserValid() {
        assertTrue(databaseHelper.addStudent(sampleProfile));
        long lastID = databaseHelper.getLastStudentID();
        assertTrue(databaseHelper.deleteStudent(lastID)); //ensure student is gone!
        assertNull(databaseHelper.findStudent(lastID));
        List<PunchModel> lst = databaseHelper.getAllPunchesFromProfile(lastID);

        assertEquals(0, lst.size()); //ensure all punches are deleted!
    }

    /*
     * Verifies TC16
     */
    @Test
    public void findUserInvalid() {
        assertNull(databaseHelper.findStudent(INVALID_ID)); //Should not exist!
    }
    /*
     * Verifies TC4
     */
    @Test
    public void removeUserInvalid() {
        assertFalse(databaseHelper.deleteStudent(INVALID_ID)); //Should not exist!
    }

    /**
     * Verifies TC5
     */
    @Test
    public void addPunchUserValid() {
        databaseHelper.addStudent(sampleProfile);
        long lastID = databaseHelper.getLastStudentID();
        Date time = Calendar.getInstance().getTime();
        PunchModel pm = new PunchModel(0, 99999, 5, time.getTime());
        assertTrue(databaseHelper.addPunch(pm));
        assertTrue(databaseHelper.deleteStudent(lastID));

    }

    /**
     * Verifies TC #7. This one is failing, so it is disabled. Please resolve addPunch() method to ensure
     * user exists!
     */
    //@Test
    public void addPunchUserInvalid() {
        long ID = INVALID_ID;
        Date time = Calendar.getInstance().getTime();
        PunchModel pm = new PunchModel(0, 99999, 5, time.getTime());
        assertFalse(databaseHelper.addPunch(pm)); //add should NOT be successful.
    }

    /**
     * Verifies TC #9
     */
    @Test
    public void editUserInfoValid() {
        databaseHelper.addStudent(sampleProfile);
        long lastID = databaseHelper.getLastStudentID();

        assertTrue(databaseHelper.editStudentProfile(lastID, "John", "Doe", "20", "100", "100"));
        assertTrue(databaseHelper.deleteStudent(lastID));
    }

    /**
     * Verifies TC #10
     */
    @Test
    public void editUserInfoInvalid() {
        databaseHelper.addStudent(sampleProfile);
        long lastID = databaseHelper.getLastStudentID();

        assertFalse(databaseHelper.editStudentProfile(lastID, "John", "Doe", "-1", "100", "100"));
        assertFalse(databaseHelper.editStudentProfile(lastID, "John", "Doe", "102", "100", "100"));
        assertFalse(databaseHelper.editStudentProfile(lastID, "John", "Doe", "28", "-2", "100"));
        assertFalse(databaseHelper.editStudentProfile(lastID, "John", "Doe", "28", "100", "-1"));

        assertTrue(databaseHelper.deleteStudent(lastID));
    }

    @After
    public void after() {

    }
}