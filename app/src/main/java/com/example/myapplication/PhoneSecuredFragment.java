package com.example.myapplication;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class PhoneSecuredFragment extends Fragment {

    public static final String REQUEST_KEY = "phoneSecured";
    private NavController navController;
    private long accountID;
    private Resources res;

    public PhoneSecuredFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_secured, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = new Bundle();
        navController = Navigation.findNavController(view);
        res = getResources();

        ImageButton imgBtnConfirm = view.findViewById(R.id.BtnConfirm);
        Button btnCancel = view.findViewById(R.id.BtnCancel);

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong(res.getString(R.string.account_id_key));
            bundle.putLong(res.getString(R.string.account_id_key), accountID);
        });

        // confirm and move to measure punch
        imgBtnConfirm.setOnClickListener(v -> {
            getParentFragmentManager().setFragmentResult(MeasuringPunchFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_phoneSecuredFragment_to_measuringPunchFragment);
        });

        // moves back to student profile.
        btnCancel.setOnClickListener(v -> {
            onBackEvent(bundle);
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackEvent(bundle);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    }

    private void onBackEvent(Bundle bundle) {
        getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
        navController.navigate(R.id.action_phoneSecuredFragment_to_studentProfileFragment);
    }
}