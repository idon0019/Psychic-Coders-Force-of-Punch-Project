package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

public class SecondFragment extends Fragment {

    private TextView txtNumResults;
    private ListView listView;
    private NavController navController;
    private MyAppProfileDatabase profileDatabase;


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
        Button btnShowAll = view.findViewById(R.id.BtnShowAll);
        listView = view.findViewById(R.id.ListProfiles);
        txtNumResults = view.findViewById(R.id.TxtNumResults);


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

        /*
         * Show all button - Displays all the students in the database.
         * Calls the getNumberOfStudentsFromDatabase method in the database helper class and returns
         * the number of students in the database, and passes the database helper object to the
         * showStudentsInList() method within this class.
         */
        btnShowAll.setOnClickListener(v -> {

            int results = profileDatabase.getNumberOfStudentsFromDatabase();
            txtNumResults.setText("Results: " + results);
            showStudentsInList(profileDatabase);

        });

        /*
         * A listener for when the user clicks on an item in the list
         */
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            ProfileModel profileModel = (ProfileModel) parent.getItemAtPosition(position);

            Bundle bundle = new Bundle();
            bundle.putLong("accountID", profileModel.getId());
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
        ArrayAdapter<ProfileModel> studentArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, profileDatabase.getStudents());
        listView.setAdapter(studentArrayAdapter);
    }
}