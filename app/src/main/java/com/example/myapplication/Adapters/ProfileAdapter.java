package com.example.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.Executors.ImageExecutor;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Custom ArrayAdapter to display an image icon and a text box.
 */
public class ProfileAdapter extends ArrayAdapter<ProfileModel> {
    private final ArrayList<ProfileModel> models;
    private final Executor executor = new ImageExecutor();
    public ProfileAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProfileModel> models) {
        super(context, resource, models);
        this.models = models;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        ProfileModel model = models.get(position);

        if (model != null) {
            ImageView image = v.findViewById(R.id.ImgViewProfileIcon);
            TextView text = v.findViewById(R.id.TxtProfile);

            if (image != null) {
                String path = model.getPhotoPath();
                /*
                Creates a new runnable that is fed into an executor. This is necessary
                since using:

                    new Thread(() -> {
                        ... ;
                        image.post(...);
                    });

                like in BitmapMaker.setImage() will cause the images to display out of order randomly,
                or not show at all. While the cause of this bug is unlikely to be random it is not known.
                Using a separate executor will alleviate the problem of mismatched/disappearing profile icons.
                 */
                Runnable active = () -> image.post(() -> image.setImageBitmap(BitmapFactory.decodeFile(path)));
                executor.execute(active);
            }


            if (text != null) {
                text.setText(model.toString());
            }
        }

        return v;
    }
}
