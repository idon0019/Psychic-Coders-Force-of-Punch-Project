package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PunchResultFragment extends Fragment {

    public static final String REQUEST_KEY = "punchResult";
    private Button btnTryAgain, btnRecord;
    private NavController navController;
    private TextView txtPunchResult, txtPreviousRecord, txtHighScore;
    private String punchString;
    private double punchScore;
    private long accountID;
    private MyAppProfileDatabase database;
    private Resources res;

    public PunchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_punch_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        database = new MyAppProfileDatabase(getActivity());

        btnTryAgain = view.findViewById(R.id.BtnTryAgain);
        btnRecord = view.findViewById(R.id.BtnRecord);

        txtPunchResult = view.findViewById(R.id.TxtPunchForceResult);
        txtPreviousRecord = view.findViewById(R.id.TxtPreviousPunchForce);
        txtHighScore = view.findViewById(R.id.TxtNewHighScore);

        res = getResources();

        Bundle bundle = new Bundle();

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                DecimalFormat df = new DecimalFormat("####");
                double highScore;
                punchScore = result.getDouble("punchScore");
                punchString = df.format(punchScore) + " N";
                txtPunchResult.setText(punchString);

                accountID = result.getLong("accountID");
                bundle.putLong("accountID", accountID);

                // displays new high score message if necessary
                if (punchScore < database.getHighScore(accountID))
                    txtHighScore.setText(res.getText(R.string.new_highscore));

                // displays the previous punch force record.
                if (database.hasPunchData(accountID)) {
                    highScore = database.getHighScore(accountID);
                    if (punchScore < highScore)
                        txtHighScore.setText(res.getText(R.string.new_highscore));

                    txtPreviousRecord.setText(String.format(res.getString(R.string.previous_record), df.format(highScore)));
                }
            }
        });


        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().setFragmentResult(PhoneSecuredFragment.REQUEST_KEY, bundle);
                navController.navigate(R.id.action_punchResultFragment_to_phoneSecuredFragment);
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date time = Calendar.getInstance().getTime();

                PunchModel punch = new PunchModel(0, accountID, punchScore, time.getTime());
                database.addPunch(punch);

                getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
                navController.navigate(R.id.action_punchResultFragment_to_studentProfileFragment);
            }
        });
    }

    public void isNewHighscore(long accountID, double punchScore) {
        double currentHighScore;

        currentHighScore = database.getHighScore(accountID);

        if (currentHighScore > punchScore) {

        }
    }
}