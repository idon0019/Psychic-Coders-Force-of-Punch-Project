package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView txtUser, txtAddPhoto, txtFirstName, txtLastName, txtHeight, txtWeight, txtVersion, txtSubmitted;
    ImageButton imgAdd;
    EditText edtFirstName, edtLastName, edtHeight, edtWeight;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtFirstName = (EditText) findViewById(R.id.EdtFirstName);
        edtLastName = (EditText) findViewById(R.id.EdtLastName);
        edtHeight = (EditText) findViewById(R.id.Edtheight);
        edtWeight = (EditText) findViewById(R.id.EdtWeight);
        imgAdd = (ImageButton) findViewById(R.id.ImgAdd);
        btnSubmit = (Button) findViewById(R.id.BtnSubmit);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}