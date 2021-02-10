package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.util.ArrayList;
import java.util.List;


public class StudentProfileFragment extends Fragment {

    private Button btnBack, btnHome, btnSubmit, btnDeleteProfile;
    private TextView txtFirstName, txtLastName, txtAge, txtWeight, txtHeight;
    private NavController navController;

    private ProfileModel profileModel;

    public StudentProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        btnBack = view.findViewById(R.id.BtnBack);
        btnHome = view.findViewById(R.id.BtnHome);
        btnSubmit = view.findViewById(R.id.BtnSubmit);
        btnDeleteProfile = view.findViewById(R.id.BtnDeleteProfile);

        txtFirstName = view.findViewById(R.id.TxtFirstName);
        txtLastName = view.findViewById(R.id.TxtLastName);
        txtAge = view.findViewById(R.id.TxtAge);
        txtWeight = view.findViewById(R.id.TxtWeight);
        txtHeight = view.findViewById(R.id.TxtHeight);

        // Database helper object
        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        getParentFragmentManager().setFragmentResultListener("accountID", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                int accountID = result.getInt("accountID");

                // Set the empty text in the student profile screen to the first name of the student
                txtFirstName.setText(database.getFirstNameFromDatabase(accountID));
            }
        });

        /**
         * Back button - Navigate to previous screen
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to select a user screen
                navController.navigate(R.id.action_studentProfileFragment_to_secondFragment);
            }
        });

        /**
         * Home button - Navigate back to Main Menu
         */
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Navigate to main menu
                navController.navigate(R.id.action_studentProfileFragment_to_firstFragment);
            }
        });

        /**
         * Delete button - Delete student profile
         */
        btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

                database.deleteStudent(profileModel);

                // Navigate back to select user screen
                navController.navigate(R.id.action_studentProfileFragment_to_secondFragment);
            }
        });

    }

}