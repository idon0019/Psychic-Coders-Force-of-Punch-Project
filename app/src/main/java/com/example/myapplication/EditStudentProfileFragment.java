package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;
import com.example.myapplication.ImageMaker.BitmapMaker;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;


@SuppressWarnings("ALL")
public class EditStudentProfileFragment extends Fragment {

    public static final String REQUEST_KEY = "editStudent";
    private EditText edtFirstName, edtLastName, edtWeight, edtHeight;
    private TextView txtAge;
    private ImageView imgEdit;
    private NavController navController;
    private long accountID;
    private MyAppProfileDatabase database;

    private Calendar calendar;
    private DatePickerDialog dialog;
    private Resources res;

    private Uri imageUri = null, // this uri will be the true uri of the final selected profile photo
            tempImageUri; // this uri will change any time the user taps the choose image option
    private File photo = null;
    private File initialPhoto = null; // the profile picture the user started with. stored until the end when the user is done editing their profile
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
                        imageUri = tempImageUri;
                        photoPath = photo.toString();
                        BitmapMaker.setImage(photoPath, imgEdit);

                        // saves a downscaled version of the image instead of a full version. Uses ImgViewProfilePicture to maximize quality.
                        BitmapMaker.downscaleAndSaveBitMap(photoPath, imgEdit, photo);
                        if (oldPhoto != null && oldPhoto != initialPhoto) // if an old photo exists then delete it
                            oldPhoto.delete();
                    }
                    imgEdit.setBackgroundResource(R.color.image_background_transparent);
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
                        imageUri = getImage.getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        photoPath = photo.toString();
                        BitmapMaker.bitmapToFile(bitmap, photo);
                        BitmapMaker.downscaleAndSaveBitMap(photoPath, imgEdit, photo);
                        BitmapMaker.setImage(photoPath, imgEdit);

                        if (oldPhoto != null) // if a camera image already exists then delete it
                            oldPhoto.delete();
                        imgEdit.setBackgroundResource(R.color.image_background_transparent);
                    } else {
                        photo.delete();
                    }
                }
            });

    public EditStudentProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_student_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        database = new MyAppProfileDatabase(getActivity());

        Button btnSubmit = view.findViewById(R.id.BtnSubmit);
        Button btnCancel = view.findViewById(R.id.BtnCancel);
        Button btnDate = view.findViewById(R.id.BtnDate);
        imgEdit = view.findViewById(R.id.ImgEdit);
        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        txtAge = view.findViewById(R.id.TxtAge);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        res = getResources();

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong(res.getString(R.string.account_id_key));

            photo = new File(database.getImagePathFromDatabase(accountID));
            photoPath = photo.toString();
            initialPhoto = photo;
            imgEdit.setImageBitmap(BitmapFactory.decodeFile(photo.toString()));
            edtFirstName.setText(database.getFirstNameFromDatabase(accountID));
            edtLastName.setText(database.getLastNameFromDatabase(accountID));
            txtAge.setText(database.getDOBFromDatabase(accountID));
            edtWeight.setText(database.getWeightFromDatabase(accountID));
            edtHeight.setText(database.getHeightFromDatabase(accountID));

        });

        btnDate.setOnClickListener(v -> {
            calendar = Calendar.getInstance();

            int d = calendar.get(Calendar.DAY_OF_MONTH);
            int m = calendar.get(Calendar.MONTH);
            int y = calendar.get(Calendar.YEAR)-10;

            dialog = new DatePickerDialog(getActivity(),
                    (view1, year, month, dayOfMonth) -> txtAge.setText(String.format(res.getString(R.string.date_picker_text), dayOfMonth, month+1, year)), //txtAge.setText(dayOfMonth + "/" + (month+1) + "/" + year); },
                    y,
                    m,
                    d);
            dialog.show();
        });

        btnSubmit.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
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

                valid = !edtFirstName.getText().toString().equals("") && !edtLastName.getText().toString().equals("") && photoPath != null;
            } catch (Exception e) {
                //
            }

            if (valid) {
                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_LONG).show();

                // lock in the changes, thus deleting initialPhoto if it is not the same as photo
                if (initialPhoto != null && !photoPath.equals(initialPhoto.toString())) {
                    initialPhoto.delete();
                }

                bundle.putLong(res.getString(R.string.account_id_key), accountID);
                getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
                database.editStudentProfile(accountID, photoPath, edtFirstName.getText().toString(), edtLastName.getText().toString(), txtAge.getText().toString(), edtWeight.getText().toString(), edtHeight.getText().toString());
                navController.navigate(R.id.action_editStudentProfileFragment_to_studentProfileFragment);
            } else {
                Toast.makeText(getActivity(), "Please choose profile photo and fill out all fields", Toast.LENGTH_LONG).show();
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

        imgEdit.setOnClickListener(v -> {
            // builds the image picker dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(res.getString(R.string.dialog_name));
            builder.setMessage(res.getString(R.string.dialog_message));

            builder.setPositiveButton(R.string.dialog_camera, ((dialog1, which) -> {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // creates a temp file and return its uri
                try {
                    tempImageUri = createImageFileURI();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
                getCameraImage.launch(takePhotoIntent);
            }));

            builder.setNegativeButton(R.string.dialog_gallery, ((dialog1, which) -> {
                // creates a temp file and return its uri
                try {
                    tempImageUri = createImageFileURI();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
                pickPhotoIntent.setType("image/*");
                getGalleryImage.launch(pickPhotoIntent);
            }));

            builder.setNeutralButton(R.string.dialog_cancel,
                    (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    /**
     * Sets the appropriate back event interaction. Navigates back to user select screen.
     */
    private void onBackEvent() {
        Bundle bundle = new Bundle();

        Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_LONG).show();

        // undo the changes, thus deleting photo if it is not the same as initialPhoto
        if (photo != initialPhoto && initialPhoto != null) {
            photo.delete();
        }

        bundle.putLong(res.getString(R.string.account_id_key), accountID);
        getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
        navController.navigate(R.id.action_editStudentProfileFragment_to_studentProfileFragment);
    }

    /**
     * Creates an empty image file with an auto-generated name and stores it on the file system for later use.
     * Returns a URI of the created file.
     * @return An image file URI.
     * @throws IOException Creating file failed.
     */
    public Uri createImageFileURI() throws IOException {
        if (photo != null)
            oldPhoto = photo;

        photo = BitmapMaker.createNewImageFile(getContext());

        if (photo != null && getContext() != null)
            return FileProvider.getUriForFile(getContext(), "com.example.myapplication.fileprovider", photo);
        else
            return null;
    }

    /**
     * Gets the size of a photo from its URI.
     * @param contentURI Photo URI.
     * @return Size of the photo.
     */
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
}