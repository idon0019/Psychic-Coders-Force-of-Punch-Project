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


    @After
    public void after() {

    }
}