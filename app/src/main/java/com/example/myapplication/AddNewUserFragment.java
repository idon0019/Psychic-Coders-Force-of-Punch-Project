package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

public class AddNewUserFragment extends Fragment {

    private EditText edtFirstName, edtLastName, edtAge, edtWeight, edtHeight;
    private Button btnBack, btnHome, btnSubmit;
    private NavController navController;

    private DatePickerDialog.OnDateSetListener dateSetListener;

    public AddNewUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        btnBack = view.findViewById(R.id.BtnBack);
        btnHome = view.findViewById(R.id.BtnHome);
        btnSubmit = view.findViewById(R.id.BtnSubmit);

        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        edtAge = view.findViewById(R.id.EdtAge);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_addNewUserFragment_to_secondFragment);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                navController.navigate(R.id.action_addNewUserFragment_to_firstFragment);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileModel profileModel = null;
                boolean valid = false;
                try {
                    profileModel = new ProfileModel(
                            -1,
                            edtFirstName.getText().toString(),
                            edtLastName.getText().toString(),
                            Integer.parseInt(edtAge.getText().toString()),
                            Float.parseFloat(edtWeight.getText().toString()),
                            Float.parseFloat(edtHeight.getText().toString())
                    );
                    valid = true;
                }catch (Exception e) {
                    Toast.makeText(getActivity(), "Invalid Entry", Toast.LENGTH_LONG).show();
                }

                if (valid) {
                    // Reference to the new profile database
                    MyAppProfileDatabase databaseHelper = new MyAppProfileDatabase(getActivity());
                    boolean success = databaseHelper.addStudent(profileModel);
                    if (success == true) {
                        Toast.makeText(getActivity(), "Profile added", Toast.LENGTH_LONG).show();

                        int id = databaseHelper.getLastStudentID();

                        Bundle accountID = new Bundle();
                        accountID.putInt("accountID", id);
                        getParentFragmentManager().setFragmentResult("accountID", accountID);

                        navController.navigate(R.id.action_addNewUserFragment_to_studentProfileFragment);
                    } else {
                        Toast.makeText(getActivity(), "Profile could be added", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}