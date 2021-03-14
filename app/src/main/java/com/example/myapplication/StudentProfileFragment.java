package com.example.myapplication;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentProfileFragment extends Fragment {

    public static final String REQUEST_KEY = "studentProfile";

    private TextView txtFirstName, txtLastName, txtAge, txtWeight, txtHeight, txtForcePunchResult;
    private GraphView graph;
    private ImageView imgViewProfile;

    private NavController navController;
    private Resources res;
    private long accountID;

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
        ImageButton btnBack = view.findViewById(R.id.BtnBack);
        ImageButton btnHome = view.findViewById(R.id.BtnHome);
        Button btnDeleteProfile = view.findViewById(R.id.BtnDeleteProfile);
        Button btnRecordPunch = view.findViewById(R.id.BtnRecordPunch);
        Button btnEditProfile = view.findViewById(R.id.BtnEditProfile);

        imgViewProfile = view.findViewById(R.id.ImgViewProfilePicture);
        txtFirstName = view.findViewById(R.id.TxtFirstName);
        txtLastName = view.findViewById(R.id.TxtLastName);
        txtAge = view.findViewById(R.id.TxtAge);
        txtWeight = view.findViewById(R.id.TxtWeight);
        txtHeight = view.findViewById(R.id.TxtHeight);
        txtForcePunchResult = view.findViewById(R.id.TxtPunchForceResult);

        graph = view.findViewById(R.id.Graphview);
        res = getResources();

        // Database helper object
        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        // Fragment listener that takes in account id to populate the profile with.
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong("accountID");
            DecimalFormat df = new DecimalFormat(res.getString(R.string.number_format));

            String path = database.getImagePathFromDatabase(accountID);
            imgViewProfile.setImageBitmap(BitmapFactory.decodeFile(path));
            txtFirstName.setText(database.getFirstNameFromDatabase(accountID));
            txtLastName.setText(database.getLastNameFromDatabase(accountID));
            txtAge.setText(String.format(res.getString(R.string.student_age), database.getAgeFromDatabase(accountID), getStudentAge(database)));
            txtWeight.setText(database.getWeightFromDatabase(accountID));
            txtHeight.setText(database.getHeightFromDatabase(accountID));

            // sets a click listener on the mini graph that moves to the StudentGraph fragment.
            if (database.hasPunchData(accountID)) { // if punch data exists sets navigation to studentgraph
                populateGraph(database, accountID);
                graph.setOnClickListener(v -> {
                    // Navigate back to select a user screen
                    Bundle bundle = new Bundle();
                    bundle.putLong("accountID", accountID);
                    getParentFragmentManager().setFragmentResult(StudentGraphFragment.REQUEST_KEY, bundle);
                    navController.navigate(R.id.action_studentProfileFragment_to_studentGraph);
                });
                txtForcePunchResult.setText(df.format(database.getHighScore(accountID)));
            } else { // if data doesn't exist then show an error toast when graph is tapped.
                graph.setOnClickListener(v -> Toast.makeText(getContext(), R.string.no_punch_data, Toast.LENGTH_SHORT).show());
                txtForcePunchResult.setText(R.string.no_punch_record);
            }
        });

        // Back button - navigates to the user select (second fragment)
        btnBack.setOnClickListener(v -> {
            // Navigate back to select a user screen
            navController.navigate(R.id.action_studentProfileFragment_to_secondFragment);
        });

        // Home button - navigates to the FirstFragment
        btnHome.setOnClickListener(v -> {

            // Navigate to main menu
            navController.navigate(R.id.action_studentProfileFragment_to_firstFragment);
        });

        // Deletes the profile. Will popup a dialog asking the user to confirm this action.
        btnDeleteProfile.setOnClickListener(v -> {
            MyAppProfileDatabase database1 = new MyAppProfileDatabase(getActivity());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // shows a popup to confirm deletion
            builder.setTitle("Delete Student");
            builder.setMessage("Are you sure you want to remove " + database1.getFirstNameFromDatabase(accountID) + " " + database1.getLastNameFromDatabase(accountID) + "?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                boolean deleteStudent = database1.deleteStudent(accountID);
                if (deleteStudent)
                    Toast.makeText(getActivity(), "Account deleted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getActivity(), "Delete failed", Toast.LENGTH_LONG).show();

                // Navigate back to select user screen
                navController.navigate(R.id.action_studentProfileFragment_to_secondFragment);
            });


            builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        // when pressed lets the user record a new punch.
        btnRecordPunch.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("accountID", accountID);
            getParentFragmentManager().setFragmentResult(PhoneSecuredFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_studentProfileFragment_to_phoneSecuredFragment);
        });

        // moves to EditStudentProfile
        btnEditProfile.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putLong("accountID", accountID);
            getParentFragmentManager().setFragmentResult(EditStudentProfileFragment.REQUEST_KEY, bundle2);
            navController.navigate(R.id.action_studentProfileFragment_to_editStudentProfileFragment);
        });
    }

    /**
     * Returns student age.
     * @param database : Database helper class.
     * @return Age of student.
     */
    private int getStudentAge(MyAppProfileDatabase database) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.CANADA);
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

        studentDOB.set(year, month, day);

        int studentAge = todayDate.get(Calendar.YEAR) - studentDOB.get(Calendar.YEAR);

        if (todayDate.get(Calendar.DAY_OF_YEAR) < studentDOB.get(Calendar.DAY_OF_YEAR)) {
            studentAge--;
        }
        return studentAge;
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
}