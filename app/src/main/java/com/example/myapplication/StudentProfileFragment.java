package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class StudentProfileFragment extends Fragment {

    private ImageButton btnBack, btnHome;
    private Button btnDeleteProfile, btnRecordPunch, btnEditProfile;
    private TextView txtFirstName, txtLastName, txtAge, txtWeight, txtHeight, txtForcePunchResult, txtGraph;
    private GraphView graph;

    private NavController navController;
    private LinearLayout parentLayout;
    private ScrollView scrollView;
    private long accountID;

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
        btnDeleteProfile = view.findViewById(R.id.BtnDeleteProfile);
        btnRecordPunch = view.findViewById(R.id.BtnRecordPunch);
        btnEditProfile = view.findViewById(R.id.BtnEditProfile);

        txtFirstName = view.findViewById(R.id.TxtFirstName);
        txtLastName = view.findViewById(R.id.TxtLastName);
        txtAge = view.findViewById(R.id.TxtAge);
        txtWeight = view.findViewById(R.id.TxtWeight);
        txtHeight = view.findViewById(R.id.TxtHeight);

        graph = view.findViewById(R.id.Graphview);
        txtGraph = view.findViewById(R.id.TxtGraph);
        parentLayout = view.findViewById(R.id.parentLayout);
        scrollView = view.findViewById(R.id.scrollview);
        List<PunchModel> punchModels = new ArrayList<>();

        // Database helper object
        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());


        getParentFragmentManager().setFragmentResultListener("studentProfile", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                accountID = result.getLong("accountID");

                // Set the empty text in the student profile screen to the first name of the student
                txtFirstName.setText(database.getFirstNameFromDatabase(accountID));
                txtLastName.setText(database.getLastNameFromDatabase(accountID));
                txtAge.setText(database.getAgeFromDatabase(accountID) + ", Age: " + getStudentAge(database));
                txtWeight.setText(database.getWeightFromDatabase(accountID));
                txtHeight.setText(database.getHeightFromDatabase(accountID));

                List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);
                if (punches.size() == 0) {
                    //insertFakePunchData(accountID, database);
                }

                if (hasPunchData(accountID, database)) { // if punch data exists navigate to studentgraph
                    populateGraph(database, accountID);
                    graph.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Navigate back to select a user screen
                            Bundle bundle = new Bundle();
                            bundle.putLong("accountID", accountID);
                            getParentFragmentManager().setFragmentResult("studentgraph", bundle);
                            navController.navigate(R.id.action_studentProfileFragment_to_studentGraph);
                        }
                    });
                } else { // if data doesn't exist then show an error toast
                    graph.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), R.string.no_punch_data, Toast.LENGTH_SHORT).show();
                        }
                    });
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
                Bundle bundle = new Bundle();
                bundle.putLong("accountID", accountID);
                getParentFragmentManager().setFragmentResult("phoneSecured", bundle);
                navController.navigate(R.id.action_studentProfileFragment_to_phoneSecuredFragment);
            }
        });


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("accountID2", accountID);
                getParentFragmentManager().setFragmentResult("accountID2", bundle2);
                navController.navigate(R.id.action_studentProfileFragment_to_editStudentProfileFragment);
            }
        });
    }

    private int getStudentAge(MyAppProfileDatabase database) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = sdf.parse(database.getAgeFromDatabase(accountID));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) return 0;

        Calendar studentDOB = Calendar.getInstance();
        Calendar todayDate = Calendar.getInstance();

        studentDOB.setTime(date);

        int year = studentDOB.get(Calendar.YEAR);
        int month = studentDOB.get(Calendar.MONTH);
        int day = studentDOB.get(Calendar.DAY_OF_MONTH);

        studentDOB.set(year, month + 1, day);

        int studentAge = todayDate.get(Calendar.YEAR) - studentDOB.get(Calendar.YEAR);

        if (todayDate.get(Calendar.DAY_OF_YEAR) < studentDOB.get(Calendar.DAY_OF_YEAR)) {
            studentAge--;
        }
        return studentAge;
    }

    /**
     * Checks if user has punch data
     */
    private boolean hasPunchData(long accountID, MyAppProfileDatabase db) {
        List<PunchModel> punches;

        punches = db.getAllPunchesFromProfile(accountID);

        if (punches.size() != 0)
            return true;
        else
            return false;
    }

    /**
     * Populates the graph
     */
    private void populateGraph(MyAppProfileDatabase database, long accountID) {
        long date;
        List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph.addSeries(series);

        date = punches.get(0).getDate();
        graph.getViewport().setMinX(date);
        series.appendData(new DataPoint(date, punches.get(0).getForce()), true, 100);
        series.setDrawDataPoints(true);

        for (int i = 1; i < punches.size(); i++) {
            date = punches.get(i).getDate();
            series.appendData(new DataPoint(date, punches.get(i).getForce()), true, 100);
        }

        graph.getViewport().setMaxX(date);

        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getViewport().setXAxisBoundsManual(true);
    }

    /**
     * Debug method to populate punch table with 20 data points
     *
     * @Test
     */
    public void insertFakePunchData(long accountID, MyAppProfileDatabase db) {
        Date date;
        Calendar time;
        time = Calendar.getInstance();
        Random rand = new Random();
        PunchModel newPunch;

        for (int i = 0; i < 20; i++) {
            time.set(100 + i, 10, 12);
            date = time.getTime();
            newPunch = new PunchModel(0, accountID, rand.nextDouble(), date.getTime());
            db.addPunch(newPunch);
        }

    }
}