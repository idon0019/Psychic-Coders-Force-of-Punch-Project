package com.example.myapplication;

import android.content.res.Resources;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentGraphFragment extends Fragment {
    public static final String GRAPH_TITLE = "Punch Force vs Attempts";
    public static final float POINT_RADIUS = 15f;
    public static final float TEXT_SIZE = 80;
    public static final String REQUEST_KEY = "studentGraph";
    private GraphView graph;
    private TextView txtPunchInfo, txtPunchData;
    private long accountID;
    private NavController navController;
    private Resources res;

    public StudentGraphFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        graph = view.findViewById(R.id.Graph);
        navController = Navigation.findNavController(view);
        ImageButton btnBack = view.findViewById(R.id.BtnBack);
        ImageButton btnHome = view.findViewById(R.id.BtnHome);
        txtPunchInfo = view.findViewById(R.id.TxtPunchInfo);
        txtPunchData = view.findViewById(R.id.TxtPunchData);
        res = getResources();

        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong("accountID");
            populateGraph(database, accountID);

            populatePunchData(accountID, database);
        });

        /*
         * Back button - Navigate to previous screen
         */
        btnBack.setOnClickListener(v -> onBackEvent());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackEvent();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        /*
         * Home button - Navigate back to Main Menu
         */
        btnHome.setOnClickListener(v -> {
            // Navigate to main menu
            navController.navigate(R.id.action_studentGraph_to_firstFragment);
        });


    }

    /**
     * Sets the back event interaction. Moves back to the student profile screen along with
     * appropriate accountID.
     */
    private void onBackEvent() {
        // Navigate back to select a user screen
        Bundle bundle = new Bundle();
        bundle.putLong(res.getString(R.string.account_id_key), accountID);
        getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
        navController.navigate(R.id.action_studentGraph_to_studentProfileFragment);
    }

    /**
     * Populates the graph
     */
    private void populateGraph(MyAppProfileDatabase database, long accountID) {
        int i;
        List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);

        // creates a data series and sets some display properties
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        series.setDataPointsRadius(POINT_RADIUS);
        series.setDrawDataPoints(true);

        // adds a listener to respond when data points are tapped
        series.setOnDataPointTapListener((series1, dataPoint) -> {
            double force = dataPoint.getY();
            Date date = new Date(database.getDateFromPunchForce(accountID, force));
            DateFormat dateFormat = new SimpleDateFormat(res.getString(R.string.date_format), Locale.CANADA);
            DecimalFormat decimalFormat = new DecimalFormat(res.getString(R.string.number_format));

            txtPunchInfo.setText(String.format(res.getString(R.string.data_point_details), (int)dataPoint.getX(), dateFormat.format(date), decimalFormat.format(force)));
        });

        graph.addSeries(series);

        graph.getViewport().setMinX(0);
        series.appendData(new DataPoint(1, punches.get(0).getForce()), true, 100);

        for (i = 1; i < punches.size(); i++) {
            series.appendData(new DataPoint(i+1, punches.get(i).getForce()), true, 100);
        }

        graph.getViewport().setMaxX(i+1);

        graph.setTitle(GRAPH_TITLE);
        graph.setTitleColor(Color.WHITE);
        graph.setTitleTextSize(TEXT_SIZE);

        // changes label color
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);
        graph.getGridLabelRenderer().reloadStyles();

        // set date label formatter
        //graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

        // sets the axis and viewport to be scrollable and scalable
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
    }

    /**
     * Populates the punchData textview.
     *
     */
    private void populatePunchData(long accountID, MyAppProfileDatabase db) {
        List<PunchModel> punchData = db.getAllPunchesFromProfile(accountID);
        StringBuilder display = new StringBuilder();


        for (int i = 0; i < punchData.size(); i++) {
            display.append(punchData.get(i).toString(i+1, res.getString(R.string.date_format), res.getString(R.string.number_format)));
        }

        txtPunchData.setText(display.toString());
    }
}