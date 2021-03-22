package com.example.myapplication;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
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
import com.example.myapplication.ImageMaker.BitmapMaker;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.util.List;

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
        // makes graph labels invisible.
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        res = getResources();

        // Database helper object
        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        // Fragment listener that takes in account id to populate the profile with.
        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong(res.getString(R.string.account_id_key));
            DecimalFormat df = new DecimalFormat(res.getString(R.string.number_format));

            String path = database.getImagePathFromDatabase(accountID);
            BitmapMaker.setImage(path, imgViewProfile);
            txtFirstName.setText(database.getFirstNameFromDatabase(accountID));
            txtLastName.setText(database.getLastNameFromDatabase(accountID));
            txtAge.setText(String.format(res.getString(R.string.student_age), database.getDOBFromDatabase(accountID), database.getAgeFromDatabase(accountID)));
            txtWeight.setText(database.getWeightFromDatabase(accountID));
            txtHeight.setText(database.getHeightFromDatabase(accountID));

            // sets a click listener on the mini graph that moves to the StudentGraph fragment.
            if (database.hasPunchData(accountID)) { // if punch data exists sets navigation to studentgraph
                populateGraph(database, accountID);
                graph.setOnClickListener(v -> {
                    // Navigate back to select a user screen
                    Bundle bundle = new Bundle();
                    bundle.putLong(res.getString(R.string.account_id_key), accountID);
                    getParentFragmentManager().setFragmentResult(StudentGraphFragment.REQUEST_KEY, bundle);
                    navController.navigate(R.id.action_studentProfileFragment_to_studentGraph);
                });
                txtForcePunchResult.setText(String.format(res.getString(R.string.punch_highscore), df.format(database.getHighScore(accountID))));
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

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_studentProfileFragment_to_secondFragment);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

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
            builder.setMessage("Are you sure you want to remove " + database1.getFirstNameFromDatabase(accountID) + " " + database1.getLastNameFromDatabase(accountID) + "? This operation cannot be undone.");

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
            bundle.putLong(res.getString(R.string.account_id_key), accountID);
            getParentFragmentManager().setFragmentResult(PhoneSecuredFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_studentProfileFragment_to_phoneSecuredFragment);
        });

        // moves to EditStudentProfile
        btnEditProfile.setOnClickListener(v -> {
            Bundle bundle2 = new Bundle();
            bundle2.putLong(res.getString(R.string.account_id_key), accountID);
            getParentFragmentManager().setFragmentResult(EditStudentProfileFragment.REQUEST_KEY, bundle2);
            navController.navigate(R.id.action_studentProfileFragment_to_editStudentProfileFragment);
        });
    }

    /**
     * Populates the mini graph on the profile page.
     * @param database : Database helper class.
     * @param accountID : Account ID of profile.
     */
    private void populateGraph(MyAppProfileDatabase database, long accountID) {
        long date;
        int i = 0;
        List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph.addSeries(series);

        // sets the minimum x-value
        date = punches.get(0).getDate();
        graph.getViewport().setMinX(date);

        // inserts all data points on the graph and draws data points with default radius.
        series.setDrawDataPoints(true);

        graph.getViewport().setMinX(0);
        series.appendData(new DataPoint(1, punches.get(0).getForce()), true, 100);

        for (i = 1; i < punches.size(); i++) {
            series.appendData(new DataPoint(i+1, punches.get(i).getForce()), true, 100);
        }

        graph.getViewport().setMaxX(i+1);
        graph.getViewport().setXAxisBoundsManual(true);
    }
}