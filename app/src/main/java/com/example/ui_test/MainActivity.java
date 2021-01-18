package com.example.ui_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Date date;
    private Calendar time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = Calendar.getInstance();
        Random rand = new Random();


        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph.addSeries(series);

        // touch listener that displays data point.
        // TODO: format the data points into readable text.
        series.setOnDataPointTapListener((series1, dataPoint) -> Toast.makeText(getActivity(), "" + dataPoint, Toast.LENGTH_SHORT).show());

        time.set(100, 3, 4);
        date = time.getTime();
        graph.getViewport().setMinX(date.getTime());
        series.appendData(new DataPoint(date, rand.nextDouble()), true, 100);

        time.set(100, 3, 12);
        date = time.getTime();
        series.appendData(new DataPoint(date, rand.nextDouble()), true, 100);

        for (int i = 0; i < 40; i++) {
            time.set(100+i, 3, 12);
            date = time.getTime();
            series.appendData(new DataPoint(date, rand.nextDouble()), true, 100);
        }

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space

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

    private Context getActivity() {
        return this;
    }
}