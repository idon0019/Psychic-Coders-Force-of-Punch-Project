package com.example.myapplication.DataModel;

import com.example.myapplication.R;
import com.example.myapplication.StudentGraph;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data model for Punch
 */
public class PunchModel {
    private long id;
    private long accountID;
    private double force;
    private long date;

    public PunchModel(long id, long accountID, double force, long date) {
        this.id = id;
        this.accountID = accountID;
        this.force = force;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public double getForce() {
        return force;
    }

    public void setForce(double force) {
        this.force = force;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String toString (String dateFormat, String numFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date date = new Date(this.date);
        DecimalFormat myFormat = new DecimalFormat(numFormat);

        String text = "Force: " + myFormat.format(force) + "\nDate: " + df.format(date) + "\n\n";

        return text;
    }
}