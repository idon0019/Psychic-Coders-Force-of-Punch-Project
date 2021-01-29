package com.example.sqlitetest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ButtonBarLayout;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sqlitetest.DBHelpers.DatabaseHelper;
import com.example.sqlitetest.Models.PunchModel;

import org.w3c.dom.Text;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button addButton;
    Button removeButton;
    TextView dataView;
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addButton);
        removeButton = findViewById(R.id.removeButtom);
        dataView = findViewById(R.id.dataView);
        dataView.setMovementMethod(new ScrollingMovementMethod());

        updateView();

        // when clicked adds a new row of data
        addButton.setOnClickListener((v) -> {
                PunchModel newPunch = new PunchModel(0, rand.nextInt(1000), rand.nextDouble(), LocalDateTime.now().getSecond());
                DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
                helper.addPunch(newPunch);
                updateView();
        });

        // when clicked removes the last row of data
        removeButton.setOnClickListener((v) -> {
                DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
                helper.removeLastPunch();
                updateView();
        });
    }

    /**
     * Refreshes the dataView when called.
     */
    private void updateView() {
        DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
        List<PunchModel> punches = helper.getAllPunches();

        String text = "";

        for (int i = 0; i < punches.size(); i++) {
            text += punches.get(i).toString();
        }

        this.dataView.setText(text);
    }
}