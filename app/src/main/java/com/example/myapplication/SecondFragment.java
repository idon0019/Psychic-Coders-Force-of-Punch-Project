package com.example.myapplication;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.Adapters.ProfileAdapter;
import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class SecondFragment extends Fragment {

    private TextView txtNumResults;
    private ListView listView;
    private NavController navController;
    private MyAppProfileDatabase profileDatabase;
    private Resources res;

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        ImageButton btnBack = view.findViewById(R.id.BtnBack);
        ImageButton btnHome = view.findViewById(R.id.BtnHome);
        ImageButton btnAdd = view.findViewById(R.id.BtnAddAccount);
        listView = view.findViewById(R.id.ListProfiles);
        res = getResources();

        profileDatabase = new MyAppProfileDatabase(getActivity());

        btnBack.setOnClickListener(v -> navController.navigate(R.id.action_secondFragment_to_firstFragment));

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_secondFragment_to_firstFragment);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        btnHome.setOnClickListener(v -> navController.navigate(R.id.action_secondFragment_to_firstFragment));
        btnAdd.setOnClickListener(v -> navController.navigate(R.id.action_secondFragment_to_addNewUserFragment));

        showStudentsInList(profileDatabase);

        /*
         * A listener for when the user clicks on an item in the list
         */
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            ProfileModel profileModel = (ProfileModel) parent.getItemAtPosition(position);

            Bundle bundle = new Bundle();
            bundle.putLong(res.getString(R.string.account_id_key), profileModel.getId());
            getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
            // Navigate to student profile screen
            navController.navigate(R.id.action_secondFragment_to_studentProfileFragment);
        });
    }

    /**
     * Gets all the students in the database
     * @param profileDatabase - Database helper class
     */
    private void showStudentsInList(MyAppProfileDatabase profileDatabase) {
        ProfileAdapter studentArrayAdapter = new ProfileAdapter(getContext(), R.layout.list_item, (ArrayList<ProfileModel>) profileDatabase.getStudents());
        listView.setAdapter(studentArrayAdapter);
    }
}