package com.example.myapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
/*import android.widget.TextView;*/
/*import java.text.DecimalFormat;*/


public class Acceleration implements SensorEventListener {
    private Double maxAcceleration = 0.0;
    private double acceleration = 0;
    private SensorManager senManager;
    private Sensor sen;
/*    private TextView accelView; // TextView to display the current acceleration
    private TextView maxAccelView; // TextView to display the max acceleration
    String pattern = "##.#"; // Used to format the force
    DecimalFormat format = new DecimalFormat(pattern);*/

    public Acceleration(SensorManager manager/*, TextView aView, TextView mAView*/) {
        this.senManager = manager;
        assert senManager != null;

        //creates variable that gets data from the linear accelerometer
        sen = senManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //registers a listener for sen
        senManager.registerListener(this, sen, SensorManager.SENSOR_DELAY_NORMAL);
        /*this.accelView = aView;
        this.maxAccelView = mAView;*/
    }

    /**
     * Triggers when the sensors returns a value (which is at a set interval)
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        Double linAcceleration;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        linAcceleration = Math.sqrt(x * x + y * y + z * z); //gets the total linear acceleration from each axis
        this.acceleration = linAcceleration;

        // updates to a new maxAcceleration is newer value is larger
        // also updates the view if maxAcceleration changed
        if (this.maxAcceleration < linAcceleration) {
            this.maxAcceleration = linAcceleration;
//            this.maxAccelView.setText("Max acceleration: " + format.format(this.maxAcceleration));
        }

        /*// displays the current linear acceleration
        this.accelView.setText(format.format(linAcceleration));
        this.accelView.setText("Current Acceleration: "+ this.accelView.getText());*/
    }


    public double calculateForce() {
        double bagMass;
        double force;
        bagMass = 40;/*arbitrary number, macro implementation possible*/
        force = bagMass * this.maxAcceleration;
        return acceleration;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    public double getMaxAcceleration() {
        return this.maxAcceleration;
    }

    public void resetMaxAcceleration() {
        this.maxAcceleration = 0.0;
    }
}
