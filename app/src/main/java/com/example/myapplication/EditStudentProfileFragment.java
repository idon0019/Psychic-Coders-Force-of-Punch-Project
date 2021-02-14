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
import android.widget.EditText;

import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;


public class EditStudentProfileFragment extends Fragment {

    private EditText edtFirstName, edtLastName, edtAge, edtWeight, edtHeight;
    private Button btnSubmit, btnCancel;
    private NavController navController;
    private int accountID;
    private MyAppProfileDatabase database;

    public EditStudentProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        database = new MyAppProfileDatabase(getActivity());

        btnSubmit = view.findViewById(R.id.BtnSubmit);
        btnCancel = view.findViewById(R.id.BtnCancel);

        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        edtAge = view.findViewById(R.id.EdtAge);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        getParentFragmentManager().setFragmentResultListener("accountID2", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                accountID = result.getInt("accountID2");

                // Set the empty text in the student profile screen to the first name of the student
                edtFirstName.setText(database.getFirstNameFromDatabase(accountID));
                edtLastName.setText(database.getLastNameFromDatabase(accountID));
                edtAge.setText(database.getAgeFromDatabase(accountID));
                edtWeight.setText(database.getWeightFromDatabase(accountID));
                edtHeight.setText(database.getHeightFromDatabase(accountID));
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("accountID", accountID);
                getParentFragmentManager().setFragmentResult("accountID", bundle);
                database.editStudentProfile(accountID, edtFirstName.getText().toString(), edtLastName.getText().toString(), edtAge.getText().toString(), edtWeight.getText().toString(), edtHeight.getText().toString());
                navController.navigate(R.id.action_editStudentProfileFragment_to_studentProfileFragment);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}