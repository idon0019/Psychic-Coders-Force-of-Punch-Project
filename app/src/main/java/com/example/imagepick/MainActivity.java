package com.example.imagepick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ImageView mImageView;
    Button mChooseBtn;

    private static final int IMAG_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //VIEWS
        mImageView = findViewById(R.id.image_view);
        mChooseBtn = findViewById(R.id.choose_image_btn);

        //handle button click
        mChooseBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, PERMISSION_CODE);

                    }
                    else{
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else{
                    //System os is less than marshmallow
                    pickImageFromGallery();
                }
            }
        });
    }

    private void pickImageFromGallery() {
        //intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAG_PICK_CODE);
    }

    //handle result of runtime permission
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
       switch(requestCode){
           case PERMISSION_CODE:{
               if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                   pickImageFromGallery();
               }
               else{
                   Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
               }
           }
       }
    }

    //handle result of picked image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == IMAG_PICK_CODE){
            //set image to image view
            mImageView.setImageURI(data.getData());
        }
    }
}