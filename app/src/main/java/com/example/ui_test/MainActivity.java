package com.example.ui_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    GraphView graph;
    TextView infoText;
    final String pattern = "####.#";
    final DecimalFormat format = new DecimalFormat(pattern);
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM, YYYY");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar time = Calendar.getInstance();
        Random rand = new Random();

        // links infoText in code to TextView in layout
        infoText = findViewById(R.id.infoText);

        // Links graph in code to graphView in the layout
        graph = findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        graph.addSeries(series);

        // touch listener that displays data point.
        series.setOnDataPointTapListener((series1, dataPoint) -> updateText(dataPoint));

        time.set(90, 3, 4);
        Date date = time.getTime();
        graph.getViewport().setMinX(date.getTime()); // sets minimum x value label
        series.appendData(new DataPoint(date, rand.nextDouble()), true, 100);

        // creates some data points
        for (int i = 0; i < 40; i++) {
            time.set(90 + i, 3, 12);
            date = time.getTime();
            series.appendData(new DataPoint(date, rand.nextDouble()), true, 100);
        }

        graph.getViewport().setMaxX(date.getTime()); // sets maximum x value label

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
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

    private Context getActivity() {
        return this;
    }

    // sets the infoView text when a datapoint is tapped.
    private void updateText(DataPointInterface dataPoint) {
        Date data = new Date((long) dataPoint.getX());
        String force = this.format.format(dataPoint.getY());

        String text = "Date: "+this.dateFormat.format(data)+"\nForce Value: "+force;

        this.infoText.setText(text);
    }
}