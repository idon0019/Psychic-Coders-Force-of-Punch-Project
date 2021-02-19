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

    MyAppProfileDatabase databaseHelper;
    public MyAppUnitTest() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        sampleProfile = new ProfileModel(10, "bob","tom", "28", 100.5f, 167.5f);
        databaseHelper = new MyAppProfileDatabase(appContext);
    }
    @Before
    public void before() {

    }

    @Test
    public void addUserValid() {
        // Context of the app under test.
        assertTrue(databaseHelper.addStudent(sampleProfile));

        databaseHelper.deleteStudent(sampleProfile.getId());
    }

    @Test
    public void removeUserValid() {
        databaseHelper.addStudent(sampleProfile);
        assertTrue(databaseHelper.deleteStudent(sampleProfile.getId()));
        List<PunchModel> lst = databaseHelper.getAllPunchesFromProfile(sampleProfile.getId());

        assertEquals(0, lst.size()); //ensure all punches are deleted
    }

    @After
    public void after() {

    }
}