package com.example.myapplication.DataModel;

import java.util.Calendar;
import java.util.Date;

public class ProfileModel {
    private static final int MIN_ALLOWED_AGE = 5;
    private static final int MAX_ALLOWED_AGE = 100;

    private long id;
    private String firstName;
    private String lastName;
    private String age;
    private float weight;
    private float height;


    public ProfileModel(long id, String firstName, String lastName, String age, float weight, float height) {
        setId(id);
        setFirstName(firstName);
        setLastName(lastName);
        setAge(age);
        setWeight(weight);
        setHeight(height);
    }

    public ProfileModel() {

    }

    public String toString() {
        return id + ". " + firstName + " " + lastName + ", " + age;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {

        this.lastName = lastName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        String array[] = age.split("/");
        Calendar calendar = Calendar.getInstance();
        int age_num = calendar.get(Calendar.YEAR) - Integer.parseInt(array[2]);
        if (age_num < MIN_ALLOWED_AGE || age_num > MAX_ALLOWED_AGE) {
            throw new IllegalArgumentException("Invalid age (must be between 0 and 120");
        }

        this.age = age;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Invalid Height");
        }
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        if (height < 0) {
            throw new IllegalArgumentException("Invalid Height");
        }
        this.height = height;
    }
}
