package com.example.myapplication;

import android.app.Application;

public class GetStudentID extends Application {

    private int userId;

    public GetStudentID(){

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

}
