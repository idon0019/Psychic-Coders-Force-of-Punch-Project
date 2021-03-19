package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.example.myapplication.ImageMaker.BitmapMaker;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class AddNewUserFragment extends Fragment {

    private EditText edtFirstName, edtLastName, edtWeight, edtHeight;
    private TextView txtAge;
    private ImageButton imgAdd;
    private ProgressBar progressBar;
    private NavController navController;

    private Resources res;
    private Calendar calendar;
    private DatePickerDialog dialog;
    private Uri imageUri;
    private File photo = null;
    private File oldPhoto = null;
    private String photoPath = null;
    private boolean imageLoaded = true;


    // sets the launcher for getting an image from the camera
    ActivityResultLauncher<Intent> getCameraImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Bitmap bitmap = null;
                    if (getSizeFromURI(imageUri) == 0) { // if no photo was taken then delete the temp file and don't update the image
                        photo.delete();
                    } else { // if a photo was taken then update the photo and delete the old photo if one exists
                        imgAdd.setVisibility(View.GONE); // hides the view while the image loads.
                        imageLoaded = false;
                        progressBar.setVisibility(View.VISIBLE);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        photoPath = photo.toString();

                        Bitmap finalBitmap = bitmap;
                        new Thread(() -> {
                            BitmapMaker.bitmapToFile(finalBitmap, photo);
                            BitmapMaker.downscaleAndSaveBitMap(photoPath, imgAdd, photo);
                            imageLoaded = true;
                            BitmapMaker.setImage(photoPath, imgAdd, progressBar);
                        }).start();

                        if (oldPhoto != null) // if an old photo exists then delete it
                            oldPhoto.delete();

                        imgAdd.setBackgroundResource(R.color.image_background_transparent);
                    }
                }
            });

    // sets the launcher for getting an image from the gallery
    ActivityResultLauncher<Intent> getGalleryImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent getImage = result.getData();
                    Bitmap bitmap = null;
                    if (getImage != null) { // sets the image if an image was chosen
                        // this uri will be the true uri of the final selected profile photo
                        imgAdd.setVisibility(View.GONE);
                        imageLoaded = false;
                        progressBar.setVisibility(View.VISIBLE);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), getImage.getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        photoPath = photo.toString();
                        Bitmap finalBitmap = bitmap;

                        new Thread(() -> {
                            BitmapMaker.bitmapToFile(finalBitmap, photo);
                            BitmapMaker.downscaleAndSaveBitMap(photoPath, imgAdd, photo);
                            imageLoaded = true;
                            BitmapMaker.setImage(photoPath, imgAdd, progressBar);
                        }).start();


                        if (oldPhoto != null) // if a camera image already exists then delete it
                            oldPhoto.delete();
                        imgAdd.setBackgroundResource(R.color.image_background_transparent);
                    } else {
                        photo.delete();
                    }
                }
            });

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
    }); // empty

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
        progressBar = view.findViewById(R.id.addUserProgressBar);
        progressBar.setVisibility(View.GONE); // hides the progress bar at first
        res = getResources();

        // Opens date picker dialog to choose birthday.
        btnDate.setOnClickListener(v -> {
            calendar = Calendar.getInstance();

            int d = calendar.get(Calendar.DAY_OF_MONTH);
            int m = calendar.get(Calendar.MONTH);
            int y = calendar.get(Calendar.YEAR) - 10;

            dialog = new DatePickerDialog(getActivity(),
                    (view1, year, month, dayOfMonth) -> txtAge.setText(String.format(res.getString(R.string.date_picker_text), dayOfMonth, month + 1, year)),
                    y,
                    m,
                    d);
            dialog.show();
        });

        // Creates new profile if all fields are filled, or prompts user for more information.
        // Button disabled until image is loaded.
        btnSubmit.setOnClickListener(v -> {
            ProfileModel profileModel = null;
            boolean valid = false;
            String error = res.getString(R.string.submit_missing_fields);

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

                valid = !edtFirstName.getText().toString().equals("") && !edtLastName.getText().toString().equals("") && photoPath != null;
            } catch (Exception e) {
                // no special error handler.
            }

            if (!imageLoaded) {
                error = res.getString(R.string.submit_image_not_loaded);
                valid = false;
            }

            if (valid) {
                // Reference to the new profile database
                MyAppProfileDatabase databaseHelper = new MyAppProfileDatabase(getActivity());
                boolean success = databaseHelper.addStudent(profileModel);
                if (success) {
                    Toast.makeText(getActivity(), "Profile created", Toast.LENGTH_SHORT).show();

                    long id = databaseHelper.getLastStudentID();

                    Bundle accountID = new Bundle();
                    accountID.putLong(res.getString(R.string.account_id_key), id);
                    getParentFragmentManager().setFragmentResult("studentProfile", accountID);
                    navController.navigate(R.id.action_addNewUserFragment_to_studentProfileFragment);
                } else {
                    Toast.makeText(getActivity(), "Profile could not be added", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
            }

        });

        // Opens image picker to let user choose a profile image.
        imgAdd.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                // builds the image picker dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(res.getString(R.string.dialog_name));
                builder.setMessage(res.getString(R.string.dialog_message));

                builder.setPositiveButton(R.string.dialog_camera, ((dialog1, which) -> {
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // creates a temp file
                    oldPhoto = photo;
                    try {
                        photo = BitmapMaker.createNewImageFile(requireContext());
                        imageUri = FileProvider.getUriForFile(requireContext(),
                                "com.example.myapplication.fileprovider",
                                photo);
                        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        getCameraImage.launch(takePhotoIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                builder.setNegativeButton(R.string.dialog_gallery, ((dialog1, which) -> {
                    // creates a temp file and return its uri
                    oldPhoto = photo;
                    try {
                        photo = BitmapMaker.createNewImageFile(requireContext());
                        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
                        pickPhotoIntent.setType("image/*");
                        getGalleryImage.launch(pickPhotoIntent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));

                builder.setNeutralButton(R.string.dialog_cancel,
                        (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder permissionDialog = new AlertDialog.Builder(getActivity());

                permissionDialog.setTitle(res.getString(R.string.permission_name));
                permissionDialog.setMessage(res.getString(R.string.permission_message));
                permissionDialog.setNeutralButton(R.string.permission_ok, (((dialog1, which) -> {
                })));
                AlertDialog permissions = permissionDialog.create();
                permissions.show();

            } else {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        });

        btnCancel.setOnClickListener(v -> onBackEvent());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackEvent();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    /**
     * Sets the appropriate back event interaction. Navigates back to user select screen.
     */
    private void onBackEvent() {
        if (photo != null)
            photo.delete();
        Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        navController.navigate(R.id.action_addNewUserFragment_to_secondFragment);
    }

    /**
     * Gets the size of a photo from its URI.
     * @param contentURI Photo URI.
     * @return Size of the photo.
     */
    public long getSizeFromURI(Uri contentURI) {
        long result = 0;
        Cursor cursor = requireContext().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor != null) { // Source is Dropbox or other similar local file path
            cursor.moveToFirst();
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            result = cursor.getLong(sizeIndex);
            cursor.close();
        }
        return result;
    }
}