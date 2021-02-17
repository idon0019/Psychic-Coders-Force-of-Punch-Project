package com.example.myapplication;

import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

public class StudentGraph extends Fragment {
    public static final String GRAPH_TITLE = "Punch Force vs Attempts";
    public static final float POINT_RADIUS = 15f;
    public static final float TEXT_SIZE = 80;
    private GraphView graph;
    private TextView txtPunchInfo, txtPunchData;
    private int accountID;
    private ImageButton btnHome, btnBack;
    private NavController navController;

    public StudentGraph() {
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
        btnBack = view.findViewById(R.id.BtnBack);
        btnHome = view.findViewById(R.id.BtnHome);
        txtPunchInfo = view.findViewById(R.id.TxtPunchInfo);
        txtPunchData = view.findViewById(R.id.TxtPunchData);

        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        getParentFragmentManager().setFragmentResultListener("studentgraph", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                accountID = result.getInt("accountID");
                populateGraph(database, accountID);

                populatePunchData(accountID, database);
            }
        });

        /**
         * Back button - Navigate to previous screen
         */
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to select a user screen
                Bundle bundle = new Bundle();
                bundle.putInt("accountID", accountID);
                getParentFragmentManager().setFragmentResult("accountID", bundle);
                navController.navigate(R.id.action_studentGraph_to_studentProfileFragment);
            }
        });

        /**
         * Home button - Navigate back to Main Menu
         */
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Navigate to main menu
                navController.navigate(R.id.action_studentGraph_to_firstFragment);
            }
        });


    }

    /**
     * Populates the graph
     */
    private void populateGraph(MyAppProfileDatabase database, int accountID) {
        long date;
        int i;
        List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);

        // creates a data series and sets some display properties
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        series.setDataPointsRadius(POINT_RADIUS);
        series.setDrawDataPoints(true);

        // adds a listener to respond when data points are tapped
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                String text = "";
                double force = dataPoint.getY();
                Date date = new Date(database.getDateFromPunchForce(force));
                DateFormat df = new SimpleDateFormat("dd/MMMM/yy 'at' HH:mm");
                DecimalFormat myFormat = new DecimalFormat("#.##");

                text += "Attempt " + (int)dataPoint.getX() + ":\n";
                text += "Date: " + df.format(date) + " \n";
                text += "Force: " + myFormat.format(force);

                txtPunchInfo.setText(text);
            }
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
        //graph.getGridLabelRenderer().setHumanRounding(false);
    }

    /**
     * Populates the punchData textview
     *
     * @return
     */
    private boolean populatePunchData(int accountID, MyAppProfileDatabase db) {
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
}