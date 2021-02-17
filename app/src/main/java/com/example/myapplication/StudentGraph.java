package com.example.myapplication;

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

import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;

public class StudentGraph extends Fragment {
    private GraphView graph;
    private int accountID;
    private Button btnHome, btnBack;
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

        MyAppProfileDatabase database = new MyAppProfileDatabase(getActivity());

        getParentFragmentManager().setFragmentResultListener("studentgraph", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                accountID = result.getInt("accountID");
                populateGraph(database, accountID);
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
        List<PunchModel> punches = database.getAllPunchesFromProfile(accountID);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph.addSeries(series);

        date = punches.get(0).getDate();
        graph.getViewport().setMinX(date);
        series.appendData(new DataPoint(date, punches.get(0).getForce()), true, 100);

        for (int i = 1; i < punches.size(); i++) {
            date = punches.get(i).getDate();
            series.appendData(new DataPoint(date, punches.get(i).getForce()), true, 100);
        }

        graph.getViewport().setMaxX(date);

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
}