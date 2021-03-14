package com.example.myapplication;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

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

import android.os.Environment;
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

import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@SuppressWarnings("ALL")
public class EditStudentProfileFragment extends Fragment {

    public static final String REQUEST_KEY = "editStudent";
    private EditText edtFirstName, edtLastName, edtWeight, edtHeight;
    private TextView txtAge;
    private ImageView imgAdd;
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
                        imgAdd.setImageURI(tempImageUri);
                        imageUri = tempImageUri;
                        photoPath = photo.toString();
                        if (oldPhoto != null && oldPhoto != initialPhoto) // if an old photo exists then delete it
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
                        if (photo != null && photo != initialPhoto) // if a camera image already exists then delete it
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
        imgAdd = view.findViewById(R.id.ImgAdd);

        edtFirstName = view.findViewById(R.id.EdtFirstName);
        edtLastName = view.findViewById(R.id.EdtLastName);
        txtAge = view.findViewById(R.id.TxtAge);
        edtWeight = view.findViewById(R.id.EdtWeight);
        edtHeight = view.findViewById(R.id.EdtHeight);

        res = getResources();

        getParentFragmentManager().setFragmentResultListener(REQUEST_KEY, this, (requestKey, result) -> {
            accountID = result.getLong("accountID");

            photo = new File(database.getImagePathFromDatabase(accountID));
            initialPhoto = photo;
            imgAdd.setImageBitmap(BitmapFactory.decodeFile(photo.toString()));
            edtFirstName.setText(database.getFirstNameFromDatabase(accountID));
            edtLastName.setText(database.getLastNameFromDatabase(accountID));
            txtAge.setText(database.getAgeFromDatabase(accountID));
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

            Toast.makeText(getActivity(), "Updated", Toast.LENGTH_LONG).show();

            // lock in the changes, thus deleting initialPhoto if it is not the same as photo
            if (initialPhoto != null && !photoPath.equals(initialPhoto.toString())){
                initialPhoto.delete();
            }

            bundle.putLong("accountID", accountID);
            getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
            database.editStudentProfile(accountID, photoPath, edtFirstName.getText().toString(), edtLastName.getText().toString(), txtAge.getText().toString(), edtWeight.getText().toString(), edtHeight.getText().toString());
            navController.navigate(R.id.action_editStudentProfileFragment_to_studentProfileFragment);
        });

        btnCancel.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_LONG).show();

            // undo the changes, thus deleting photo if it is not the same as initialPhoto
            if (photo != initialPhoto && initialPhoto != null) {
                photo.delete();
            }

            bundle.putLong("accountID", accountID);
            getParentFragmentManager().setFragmentResult(StudentProfileFragment.REQUEST_KEY, bundle);
            navController.navigate(R.id.action_editStudentProfileFragment_to_studentProfileFragment);
        });

        imgAdd.setOnClickListener(v -> {
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
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getGalleryImage.launch(pickPhotoIntent);
            }));

            builder.setNeutralButton(R.string.dialog_cancel,
                    (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private long getSizeFromURI(Uri contentURI) {
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
        String path = "";

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