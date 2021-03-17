package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class AddNewUserFragment extends Fragment {

    private EditText edtFirstName, edtLastName, edtWeight, edtHeight;
    private TextView txtAge;
    private ImageButton imgAdd;
    private NavController navController;

    private Resources res;
    private Calendar calendar;
    private DatePickerDialog dialog;
    private Uri imageUri = null, // this uri will be the true uri of the final selected profile photo
            tempImageUri; // this uri will change any time the user taps the choose image option
    private File photo = null;
    private File oldPhoto = null;
    private String photoPath = null;


    // sets the launcher for getting an image from the camera
    ActivityResultLauncher<Intent> getCameraImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    long imageSize = getSizeFromURI(tempImageUri);
                    if (imageSize == 0) { // if no photo was taken then delete the temp file and don't update the image
                        photo.delete();
                    } else { // if a photo was taken then update the photo and delete the old photo if one exists
                        imgAdd.setImageURI(tempImageUri);
                        imageUri = tempImageUri;
                        photoPath = photo.toString();
                        if (oldPhoto != null) // if an old photo exists then delete it
                            oldPhoto.delete();
                    }
                }
            });

    // sets the launcher for getting an image from the gallery
    ActivityResultLauncher<Intent> getGalleryImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent getImage = result.getData();
                    if (getImage != null) { // sets the image if an image was chosen
                        imageUri = getImage.getData();
                        imgAdd.setImageURI(imageUri);
                        photoPath = getPicturePath(imageUri);
                        if (photo != null) // if a camera image already exists then delete it
                        {
                            photo.delete();
                        }
                    }
                }
            });

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

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
        Button btnCancel = view.findViewById(R.id.BtnCancel);
        Button btnSubmit = view.findViewById(R.id.BtnSubmit);
        Button btnDate = view.findViewById(R.id.BtnDate);

        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        txtAge = view.findViewById(R.id.TxtAge);
        imgAdd = view.findViewById(R.id.ImgAdd);
        res = getResources();

        btnDate.setOnClickListener(v -> {
            calendar = Calendar.getInstance();

            int d = calendar.get(Calendar.DAY_OF_MONTH);
            int m = calendar.get(Calendar.MONTH);
            int y = calendar.get(Calendar.YEAR) - 10;

            dialog = new DatePickerDialog(getActivity(),
                    (view1, year, month, dayOfMonth) -> txtAge.setText(String.format(res.getString(R.string.date_picker_text), dayOfMonth, month+1, year)),
                    y,
                    m,
                    d);
            dialog.show();
        });

        btnSubmit.setOnClickListener(v -> {
            ProfileModel profileModel = null;
            boolean valid = false;
            try {
                profileModel = new ProfileModel(
                        -1,
                        photoPath,
                        edtFirstName.getText().toString(),
                        edtLastName.getText().toString(),
                        txtAge.getText().toString(),
                        Float.parseFloat(edtWeight.getText().toString()),
                        Float.parseFloat(edtHeight.getText().toString())
                );
                valid = true;

                if (edtFirstName.getText().toString().equals("") || edtLastName.getText().toString().equals(""))
                    valid = false;
            } catch (Exception e) {
                // no special error handler.
            }

            if (valid) {
                // Reference to the new profile database
                MyAppProfileDatabase databaseHelper = new MyAppProfileDatabase(getActivity());
                boolean success = databaseHelper.addStudent(profileModel);
                if (success) {
                    Toast.makeText(getActivity(), "Profile created", Toast.LENGTH_SHORT).show();

                    long id = databaseHelper.getLastStudentID();

                    Bundle accountID = new Bundle();
                    accountID.putLong("accountID", id);
                    getParentFragmentManager().setFragmentResult("studentProfile", accountID);
                    navController.navigate(R.id.action_addNewUserFragment_to_studentProfileFragment);
                } else {
                    Toast.makeText(getActivity(), "Profile could not be added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please choose profile photo and fill out all fields", Toast.LENGTH_LONG).show();
            }

        });

        imgAdd.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(
                    getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // builds the image picker dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(res.getString(R.string.dialog_name));
                builder.setMessage(res.getString(R.string.dialog_message));

                builder.setPositiveButton(R.string.dialog_camera, ((dialog1, which) -> {
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // checks if a photo already exists
                    if (photo != null)
                        oldPhoto = photo;

                    // saves the photo to a file
                    try {
                        photo = createImageFile();
                    } catch (IOException e) {
                        //
                    }

                    if (photo != null && getContext() != null) {
                        tempImageUri = FileProvider.getUriForFile(getContext(), "com.example.myapplication.fileprovider", photo);
                    }
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
                    getCameraImage.launch(takePhotoIntent);
                }));

                builder.setNegativeButton(R.string.dialog_gallery, ((dialog1, which) -> {
                    Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    getGalleryImage.launch(pickPhotoIntent);
                }));

                builder.setNeutralButton(R.string.dialog_cancel,
                        (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){
                AlertDialog.Builder permissionDialog = new AlertDialog.Builder(getActivity());

                permissionDialog.setTitle(res.getString(R.string.permission_name));
                permissionDialog.setMessage(res.getString(R.string.permission_message));
                permissionDialog.setNeutralButton(R.string.permission_ok, (((dialog1, which) -> {})));
                AlertDialog permissions = permissionDialog.create();
                permissions.show();

            } else{
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        btnCancel.setOnClickListener(v -> {
            onBackEvent();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackEvent();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    private void onBackEvent() {
        Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        navController.navigate(R.id.action_addNewUserFragment_to_secondFragment);
    }

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public long getSizeFromURI(Uri contentURI) {
        long result = 0;
        Cursor cursor = getContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor != null) { // Source is Dropbox or other similar local file path
            cursor.moveToFirst();
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            result = cursor.getLong(sizeIndex);
            cursor.close();
        }
        return result;
    }

    private String getPicturePath(Uri uri) {
        String path;

        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContext().getContentResolver().query(uri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        path = cursor.getString(columnIndex);
        cursor.close();

        return path;
    }
}