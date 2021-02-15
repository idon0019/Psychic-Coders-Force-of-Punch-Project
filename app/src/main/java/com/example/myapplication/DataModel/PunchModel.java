package com.example.myapplication.DataModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Data model for Punch
 */
public class PunchModel {
    private int id;
    private int accountID;
    private double force;
    private long date;

    public PunchModel(int id, int accountID, double force, long date) {
        this.id = id;
        this.accountID = accountID;
        this.force = force;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountID() {
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

    public String toString () {
        DateFormat df = new SimpleDateFormat("dd/MMMM/yy 'at' HH:mm:ss");
        Date date = new Date(this.date);

        String text = "Force: " + this.force + "\nDate: " + df.format(date) + "\n\n";

        return text;
    }
}