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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.util.Calendar;

public class AddNewUserFragment extends Fragment {

    private EditText edtFirstName, edtLastName, edtWeight, edtHeight;
    private Button btnCancel, btnSubmit, btnDate;
    private TextView txtAge;
    private NavController navController;

    private Calendar calendar;
    private DatePickerDialog dialog;

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
        btnCancel = view.findViewById(R.id.BtnCancel);
        btnSubmit = view.findViewById(R.id.BtnSubmit);
        btnDate = view.findViewById(R.id.BtnDate);

        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        txtAge = view.findViewById(R.id.TxtAge);


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();

                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);

                dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        System.out.println("\nday : " + dayOfMonth + "\nmonth : " + month + "\nyear : " + year + "\n");
                        txtAge.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                    }
                }, d, m, y);
                dialog.show();
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
                            txtAge.getText().toString(),
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
                    if (success) {
                        Toast.makeText(getActivity(), "Profile added", Toast.LENGTH_LONG).show();

                        long id = databaseHelper.getLastStudentID();

                        Bundle accountID = new Bundle();
                        accountID.putLong("accountID", id);
                        getParentFragmentManager().setFragmentResult("accountID", accountID);

                        navController.navigate(R.id.action_addNewUserFragment_to_studentProfileFragment);
                    } else {
                        Toast.makeText(getActivity(), "Profile could be added", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_addNewUserFragment_to_secondFragment);
            }
        });
    }
}