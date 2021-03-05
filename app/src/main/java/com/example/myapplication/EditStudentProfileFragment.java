package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.res.Resources;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.util.Calendar;


public class EditStudentProfileFragment extends Fragment {

    public static final String REQUEST_KEY = "editStudent";
    private EditText edtFirstName, edtLastName, edtWeight, edtHeight;
    private TextView txtAge;
    private NavController navController;
    private long accountID;
    private MyAppProfileDatabase database;

    private Calendar calendar;
    private DatePickerDialog dialog;
    private Resources res;

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

        Button btnSubmit = view.findViewById(R.id.BtnSubmit);
        Button btnCancel = view.findViewById(R.id.BtnCancel);
        Button btnDate = view.findViewById(R.id.BtnDate);

        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        txtAge = view.findViewById(R.id.TxtAge);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        res = getResources();

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong("accountID");

            // Set the empty text in the student profile screen to the first name of the student
            edtFirstName.setText(database.getFirstNameFromDatabase(accountID));
            edtLastName.setText(database.getLastNameFromDatabase(accountID));
            txtAge.setText(database.getAgeFromDatabase(accountID));
            edtWeight.setText(database.getWeightFromDatabase(accountID));
            edtHeight.setText(database.getHeightFromDatabase(accountID));
        });

        btnDate.setOnClickListener(v -> {
            calendar = Calendar.getInstance();

            int d = calendar.get(Calendar.DAY_OF_MONTH);
            int m = calendar.get(Calendar.MONTH);
            int y = calendar.get(Calendar.YEAR)-10;

            dialog = new DatePickerDialog(getActivity(),
                    (view1, year, month, dayOfMonth) -> txtAge.setText(String.format(res.getString(R.string.date_picker_text), dayOfMonth, (month+1), year)), //txtAge.setText(dayOfMonth + "/" + (month+1) + "/" + year); },
                    y,
                    m,
                    d);
            dialog.show();
        });

        btnSubmit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_LONG).show();

            bundle.putLong("accountID", accountID);
            getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
            database.editStudentProfile(accountID, edtFirstName.getText().toString(), edtLastName.getText().toString(), txtAge.getText().toString(), edtWeight.getText().toString(), edtHeight.getText().toString());
            navController.navigate(R.id.action_editStudentProfileFragment_to_studentProfileFragment);
        });

        btnCancel.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_LONG).show();

            bundle.putLong("accountID", accountID);
            getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_editStudentProfileFragment_to_studentProfileFragment);
        });
    }
}