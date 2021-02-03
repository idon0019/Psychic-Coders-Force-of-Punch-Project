package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

public class SecondFragment extends Fragment {

    private Button btnBack, btnHome, btnAdd, btnShowAll;
    private ListView listView;
    private NavController navController;
    private ArrayAdapter studentArrayAdapter;
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
        btnBack = view.findViewById(R.id.BtnBack);
        btnHome = view.findViewById(R.id.BtnHome);
        btnAdd = view.findViewById(R.id.BtnAddAccount);
        btnShowAll = view.findViewById(R.id.BtnShowAll);
        listView = view.findViewById(R.id.ListProfiles);



        profileDatabase = new MyAppProfileDatabase(getActivity());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_secondFragment_to_firstFragment);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavOptions navOptions = new NavOptions.Builder().setPopUpTo(R.id.firstFragment, true).build();
                navController.navigate(R.id.action_secondFragment_to_firstFragment);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_secondFragment_to_addNewUserFragment);
            }
        });

        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAppProfileDatabase profileDatabase = new MyAppProfileDatabase(getActivity());
                showStudentsInList(profileDatabase);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProfileModel profileModel = (ProfileModel) parent.getItemAtPosition(position);

                navController.navigate(R.id.action_secondFragment_to_studentProfileFragment);
            }
        });

    }


    private void showStudentsInList(MyAppProfileDatabase profileDatabase) {
        studentArrayAdapter = new ArrayAdapter<ProfileModel>(getActivity(), android.R.layout.simple_list_item_1, profileDatabase.getStudents());
        listView.setAdapter(studentArrayAdapter);
    }
}