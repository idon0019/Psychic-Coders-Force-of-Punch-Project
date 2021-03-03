package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

    public static final String REQUEST_KEY = "studentProfile";

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
        txtForcePunchResult = view.findViewById(R.id.TxtPunchForceResult);

        graph = view.findViewById(R.id.Graphview);
        txtGraph = view.findViewById(R.id.TxtGraph);
        parentLayout = view.findViewById(R.id.parentLayout);
        scrollView = view.findViewById(R.id.scrollview);
        List<PunchModel> punchModels = new ArrayList<>();

        // Database helper object
        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        // Fragment listener that takes in account id to populate the profile with.
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                accountID = result.getLong("accountID");

                // Set the empty text in the student profile screen to the first name of the student
                txtFirstName.setText(database.getFirstNameFromDatabase(accountID));
                txtLastName.setText(database.getLastNameFromDatabase(accountID));
                txtAge.setText(database.getAgeFromDatabase(accountID) + "\nAge: " + getStudentAge(database));
                txtWeight.setText(database.getWeightFromDatabase(accountID));
                txtHeight.setText(database.getHeightFromDatabase(accountID));

                List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);
                if (punches.size() == 0) {
                    // Debug method used to insert fake punch data for an account.
                    //insertFakePunchData(accountID, database);
                }

                // sets a click listener on the mini graph that moves to the StudentGraph fragment.
                if (hasPunchData(accountID, database)) { // if punch data exists sets navigation to studentgraph
                    populateGraph(database, accountID);
                    graph.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Navigate back to select a user screen
                            Bundle bundle = new Bundle();
                            bundle.putLong("accountID", accountID);
                            getParentFragmentManager().setFragmentResult(StudentGraphFragment.REQUEST_KEY, bundle);
                            navController.navigate(R.id.action_studentProfileFragment_to_studentGraph);
                        }
                    });
                    txtForcePunchResult.setText(database.getHighScore(accountID));
                } else { // if data doesn't exist then show an error toast when graph is tapped.
                    graph.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), R.string.no_punch_data, Toast.LENGTH_SHORT).show();
                        }
                    });
                    txtForcePunchResult.setText("No data");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Delete Student");
                builder.setMessage("Are you sure you want to remove " + database.getFirstNameFromDatabase(accountID) + " " + database.getLastNameFromDatabase(accountID) + "?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleteStudent = database.deleteStudent(accountID);
                        if (deleteStudent)
                            Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(), "Delete failed", Toast.LENGTH_LONG).show();

                        // Navigate back to select user screen
                        navController.navigate(R.id.action_studentProfileFragment_to_secondFragment);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // when pressed lets the user record a new punch.
        btnRecordPunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong("accountID", accountID);
                getParentFragmentManager().setFragmentResult(PhoneSecuredFragment.REQUEST_KEY, bundle);
                navController.navigate(R.id.action_studentProfileFragment_to_phoneSecuredFragment);
            }
        });


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle2 = new Bundle();
                bundle2.putLong("accountID", accountID);
                getParentFragmentManager().setFragmentResult(EditStudentProfileFragment.REQUEST_KEY, bundle2);
                navController.navigate(R.id.action_studentProfileFragment_to_editStudentProfileFragment);
            }
        });
    }

    /**
     * Returns student age.
     * @param database : Database helper class.
     * @return Age of student.
     */
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
     * Checks if profile has previous punch data.
     * @param accountID : Account ID to check for.
     * @param database : database helper class
     * @return True if account has punch data, false if otherwise.
     */
    private boolean hasPunchData(long accountID, MyAppProfileDatabase database) {
        List<PunchModel> punches;

        punches = database.getAllPunchesFromProfile(accountID);

        if (punches.size() != 0)
            return true;
        else
            return false;
    }

    /**
     * Populates the mini graph on the profile page.
     * @param database : Database helper class.
     * @param accountID : Account ID of profile.
     */
    private void populateGraph(MyAppProfileDatabase database, long accountID) {
        long date;
        List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph.addSeries(series);

        // sets the minimum x-value
        date = punches.get(0).getDate();
        graph.getViewport().setMinX(date);

        // inserts all data points on the graph and draws data points with default radius.
        series.appendData(new DataPoint(date, punches.get(0).getForce()), true, 100);
        series.setDrawDataPoints(true);

        for (int i = 1; i < punches.size(); i++) {
            date = punches.get(i).getDate();
            series.appendData(new DataPoint(date, punches.get(i).getForce()), true, 100);
        }

        // sets the maximum x-value
        graph.getViewport().setMaxX(date);

        // makes graph labels invisible.
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getViewport().setXAxisBoundsManual(true);
    }

    /**
     * Debug method to insert fake punch data.
     * @param accountID : Account to make fake data for.
     * @param db : Database helper class.
     */
    public void insertFakePunchData(long accountID, MyAppProfileDatabase db) {
        Date date;
        Calendar time;
        time = Calendar.getInstance();
        Random rand = new Random();
        PunchModel newPunch;

        // This will insert a set number of fake data points with a random double punch value.
        for (int i = 0; i < 12; i++) {
            time.set(100, i, 12);
            date = time.getTime();
            newPunch = new PunchModel(0, accountID, rand.nextDouble(), date.getTime());
            db.addPunch(newPunch);
        }

    }
}