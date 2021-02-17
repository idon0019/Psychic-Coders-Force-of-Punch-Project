package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.DataModel.PunchModel;

import java.util.List;

public class PunchResultFragment extends Fragment {

    private Button btnTryAgain, btnRecord;
    private NavController navController;
    private TextView txtPunchResult;
    private String punchString;
    private SensorManager senManager;
    private double punchScore;



    public PunchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_punch_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        btnTryAgain = view.findViewById(R.id.BtnTryAgain);
        btnRecord = view.findViewById(R.id.BtnRecord);

        txtPunchResult = view.findViewById(R.id.TxtPunchForceResult);
        Acceleration punchSensor = new Acceleration(senManager);
        punchScore = punchSensor.calculateForce();

        Bundle bundle = new Bundle();
        bundle.putDouble("punchScore", punchScore);
        getParentFragmentManager().setFragmentResult("punchScore", bundle);


        getParentFragmentManager().setFragmentResultListener("punchScore", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                punchScore = result.getDouble("punchScore");
                punchString = punchScore + " N";
                txtPunchResult.setText(punchString);
            }
        });


        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_punchResultFragment_to_phoneSecuredFragment);
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_punchResultFragment_to_studentProfileFragment);
            }
        });
    }
}