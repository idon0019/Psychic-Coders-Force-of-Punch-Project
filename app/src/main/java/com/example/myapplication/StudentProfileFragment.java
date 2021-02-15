package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class StudentProfileFragment extends Fragment {

    private Button btnBack, btnHome, btnSubmit, btnDeleteProfile, btnRecordPunch, btnEditProfile;
    private TextView txtFirstName, txtLastName, txtAge, txtWeight, txtHeight, txtPunchData, txtForcePunchResult;
    private GraphView graph;

    private NavController navController;
    private LinearLayout parentLayout;
    private ScrollView scrollView;
    private int accountID;

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
        btnRecordPunch = view.findViewById(R.id.BtnRecordPunch);
        btnEditProfile = view.findViewById(R.id.BtnEditProfile);

        txtFirstName = view.findViewById(R.id.TxtFirstName);
        txtLastName = view.findViewById(R.id.TxtLastName);
        txtAge = view.findViewById(R.id.TxtAge);
        txtWeight = view.findViewById(R.id.TxtWeight);
        txtHeight = view.findViewById(R.id.TxtHeight);
        txtPunchData = view.findViewById(R.id.TxtPunchData);

        graph = view.findViewById(R.id.graph);
        parentLayout = view.findViewById(R.id.parentLayout);
        scrollView = view.findViewById(R.id.scrollview);
        List<PunchModel> punchModels = new ArrayList<>();

        // Database helper object
        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        getParentFragmentManager().setFragmentResultListener("accountID", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                accountID = result.getInt("accountID");

                // Set the empty text in the student profile screen to the first name of the student
                txtFirstName.setText(database.getFirstNameFromDatabase(accountID));
                txtLastName.setText(database.getLastNameFromDatabase(accountID));
                txtAge.setText(database.getAgeFromDatabase(accountID));
                txtWeight.setText(database.getWeightFromDatabase(accountID));
                txtHeight.setText(database.getHeightFromDatabase(accountID));

                List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);
                if (punches.size() == 0) {
                    insertFakePunchData(accountID, database);
                }

                if (populatePunchData(accountID, database, scrollView))
                    populateGraph();
                else {
                    parentLayout.removeView(graph);
                    txtPunchData.setText("No Punch Data");
                }
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

                boolean deleteStudent = database.deleteStudent(accountID);
                if (deleteStudent)
                    Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(), "Delete failed", Toast.LENGTH_LONG).show();


                // Navigate back to select user screen
                navController.navigate(R.id.action_studentProfileFragment_to_secondFragment);
            }
        });

        btnRecordPunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_studentProfileFragment_to_phoneSecuredFragment);
            }
        });


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("accountID2", accountID);
                getParentFragmentManager().setFragmentResult("accountID2", bundle2);
                navController.navigate(R.id.action_studentProfileFragment_to_editStudentProfileFragment);
            }
        });
    }

    /**
     * Populates the punchData textview
     *
     * @return
     */
    private boolean populatePunchData(int accountID, MyAppProfileDatabase db, ScrollView view) {
        boolean hasPunch = true;

        List<PunchModel> punchData = db.getAllPunchesFromProfile(accountID);
        String display = "";

        if (punchData.size() == 0) {
            hasPunch = false;
        }

        for (int i = 0; i < punchData.size(); i++) {
            display += punchData.get(i).toString();
        }

        txtPunchData.setText(display);

        return hasPunch;
    }

    /**
     * Populates the graph
     */
    private void populateGraph() {
        Date date;
        Calendar time;

        time = Calendar.getInstance();
        Random rand = new Random();

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph.addSeries(series);

        time.set(100, 3, 4);
        date = time.getTime();
        graph.getViewport().setMinX(date.getTime());
        series.appendData(new DataPoint(date, rand.nextDouble()), true, 100);

        for (int i = 0; i < 40; i++) {
            time.set(100 + i, 3, 12);
            date = time.getTime();
            series.appendData(new DataPoint(date, rand.nextDouble()), true, 100);
        }

        graph.getViewport().setMaxX(date.getTime());

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // activate horizontal zooming and scrolling
        graph.getViewport().setScalable(true);

        // activate horizontal scrolling
        graph.getViewport().setScrollable(true);

        // activate horizontal and vertical zooming and scrolling
        graph.getViewport().setScalableY(true);

        // activate vertical scrolling
        graph.getViewport().setScrollableY(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);
    }

    /**
     * Debug method to populate punch table with 20 data points
     * @Test
     */
    public void insertFakePunchData(int accountID, MyAppProfileDatabase db) {
        Date date;
        Calendar time;
        time = Calendar.getInstance();
        Random rand = new Random();
        PunchModel newPunch;

        for (int i = 0; i < 20; i++) {
            time.set(100 + i, 3, 12);
            date = time.getTime();
            newPunch = new PunchModel(0, accountID, rand.nextDouble(), date.getTime());
            db.addPunch(newPunch);
        }

    }
}