package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class MeasuringPunchFragment extends Fragment implements SensorEventListener {

    public static final String REQUEST_KEY = "measuringPunch";
    private NavController navController;
    private SensorManager senManager;
    private Double maxAcceleration = 0.0;
    private Sensor sen;
    private long accountID;
    private Bundle bundle;
    private Resources res;

    public MeasuringPunchFragment() {
        // Required empty public constructor
    }

      @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measuring_punch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        senManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sen = senManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        senManager.registerListener(this, sen, SensorManager.SENSOR_DELAY_NORMAL);

        navController = Navigation.findNavController(view);
        Button btnCancel = view.findViewById(R.id.BtnCancel);
        res = getResources();

        bundle = new Bundle();

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong(res.getString(R.string.account_id_key));
            bundle.putLong(res.getString(R.string.account_id_key), accountID);
        });

        // cancels measuring punch and moves back to previous page.
        btnCancel.setOnClickListener(v -> {
            onBackEvent();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackEvent();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void onBackEvent() {
        getParentFragmentManager().setFragmentResult(PhoneSecuredFragment.REQUEST_KEY, bundle);
        navController.navigate(R.id.action_measuringPunchFragment_to_phoneSecuredFragment);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Double linAcceleration;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        linAcceleration = Math.sqrt(x * x + y * y + z * z); //gets the total linear acceleration from each axis

        // updates to a new maxAcceleration is newer value is larger
        if (this.maxAcceleration < linAcceleration) {
            this.maxAcceleration = linAcceleration;
        }

        // if the acceleration more than 10% less than the peak, then the peak is stable
        double lowerAccelerationThreshold = 10.0;
        if (linAcceleration < (maxAcceleration*0.9) && maxAcceleration > lowerAccelerationThreshold) {
            double punchScore = calculateForce();
            bundle.putDouble("punchScore", punchScore);
            getParentFragmentManager().setFragmentResult(PunchResultFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_measuringPunchFragment_to_punchResultFragment);
        }
    }

    public double calculateForce() {
        double bagMass;
        double force;
        bagMass = 10;/*arbitrary number, macro implementation possible*/
        force = bagMass * this.maxAcceleration;
        return force;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        senManager.registerListener(this, sen, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        senManager.unregisterListener(this);
    }
}