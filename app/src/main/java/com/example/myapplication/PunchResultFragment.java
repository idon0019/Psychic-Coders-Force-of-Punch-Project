package com.example.myapplication;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DataModel.PunchModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class PunchResultFragment extends Fragment {

    public static final String REQUEST_KEY = "punchResult";
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

        Button btnTryAgain = view.findViewById(R.id.BtnTryAgain);
        Button btnRecord = view.findViewById(R.id.BtnRecord);

        txtPunchResult = view.findViewById(R.id.TxtPunchForceResult);
        txtPreviousRecord = view.findViewById(R.id.TxtPreviousPunchForce);
        txtHighScore = view.findViewById(R.id.TxtNewHighScore);

        res = getResources();

        Bundle bundle = new Bundle();

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            DecimalFormat df = new DecimalFormat("####");
            double highScore;
            punchScore = result.getDouble("punchScore");
            punchString = df.format(punchScore) + " N";
            txtPunchResult.setText(punchString);

            accountID = result.getLong(res.getString(R.string.account_id_key));
            bundle.putLong(res.getString(R.string.account_id_key), accountID);

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
        });

        // Navigates tp the phone secured fragment, allowing the user to measure their punch again.
        btnTryAgain.setOnClickListener(v -> {
            getParentFragmentManager().setFragmentResult(PhoneSecuredFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_punchResultFragment_to_phoneSecuredFragment);
        });

        // Records the punch in the database and goes back to user profile.
        btnRecord.setOnClickListener(v -> {
            Date time = Calendar.getInstance().getTime();

            PunchModel punch = new PunchModel(0, accountID, punchScore, time.getTime());
            database.addPunch(punch);

            getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_punchResultFragment_to_studentProfileFragment);
        });

        // Navigates back to student profile without recording punch.
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(getActivity(), "Punch not recorded.", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
                navController.navigate(R.id.action_punchResultFragment_to_studentProfileFragment);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }
}