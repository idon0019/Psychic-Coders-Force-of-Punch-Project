package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.util.Calendar;

public class AddNewUserFragment extends Fragment {
    private static final int IMAG_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    private EditText edtFirstName, edtLastName, edtWeight, edtHeight;
    private Button btnCancel, btnSubmit, btnDate;
    private TextView txtAge;
    private ImageButton imgAdd;
    private NavController navController;

    private Resources res;
    private Calendar calendar;
    private DatePickerDialog dialog;
    private Uri imageUri;

    // sets the launcher for getting an image from the camera
    ActivityResultLauncher<Intent> getCameraImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent camera = result.getData();
                    Bundle extras = camera.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imgAdd.setImageBitmap(imageBitmap);
                }
            });

    // sets the launcher for getting an image from the gallery
    ActivityResultLauncher<Intent> getGalleryImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent getImage = result.getData();
                    Uri image = getImage.getData();
                    imageUri = image;
                    imgAdd.setImageURI(image);
                }
            });

    public AddNewUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        btnCancel = view.findViewById(R.id.BtnCancel);
        btnSubmit = view.findViewById(R.id.BtnSubmit);
        btnDate = view.findViewById(R.id.BtnDate);

        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        txtAge = view.findViewById(R.id.TxtAge);
        imgAdd = view.findViewById(R.id.ImgAdd);
        res = getResources();

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();

                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR)-10;

                dialog = new DatePickerDialog(getActivity(),
                        (view1, year, month, dayOfMonth) -> { txtAge.setText(dayOfMonth + "/" + (month+1) + "/" + year); },
                        y,
                        m,
                        d);
                dialog.show();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileModel profileModel = null;
                boolean valid = false;
                try {
                    profileModel = new ProfileModel(
                            -1,
                            imageUri.toString(),
                            edtFirstName.getText().toString(),
                            edtLastName.getText().toString(),
                            txtAge.getText().toString(),
                            Float.parseFloat(edtWeight.getText().toString()),
                            Float.parseFloat(edtHeight.getText().toString())
                    );
                    valid = true;
                }catch (Exception e) {
                    Toast.makeText(getActivity(), "Invalid Entry", Toast.LENGTH_LONG).show();
                }

                if (valid) {
                    // Reference to the new profile database
                    MyAppProfileDatabase databaseHelper = new MyAppProfileDatabase(getActivity());
                    boolean success = databaseHelper.addStudent(profileModel);
                    if (success) {
                        Toast.makeText(getActivity(), "Profile created", Toast.LENGTH_LONG).show();

                        long id = databaseHelper.getLastStudentID();

                        Bundle accountID = new Bundle();
                        accountID.putLong("accountID", id);
                        getParentFragmentManager().setFragmentResult("studentProfile", accountID);
                        navController.navigate(R.id.action_addNewUserFragment_to_studentProfileFragment);
                    } else {
                        Toast.makeText(getActivity(), "Profile could not be added", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_LONG).show();
                navController.navigate(R.id.action_addNewUserFragment_to_secondFragment);
            }
        });

        imgAdd.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(res.getString(R.string.dialog_name));
            builder.setMessage(res.getString(R.string.dialog_message));

            builder.setPositiveButton(R.string.dialog_camera, ((dialog1, which) -> {
                Intent takePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getCameraImage.launch(takePhoto);
            }));

            builder.setNegativeButton(R.string.dialog_gallery, ((dialog1, which) -> {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getGalleryImage.launch(pickPhoto);
            }));

            builder.setNeutralButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }
}