package com.example.myapplication.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.myapplication.DataModel.ProfileModel;
import com.example.myapplication.Executors.ImageExecutor;
import com.example.myapplication.ImageMaker.BitmapMaker;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class ProfileAdapter extends ArrayAdapter<ProfileModel> {
    private final ArrayList<ProfileModel> models;
    private final Executor executor = new ImageExecutor();
    public ProfileAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ProfileModel> models) {
        super(context, resource, models);
        this.models = models;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item, null);
        }

        ProfileModel model = models.get(position);

        if (model != null) {
            ImageView image = (ImageView) v.findViewById(R.id.ImgViewProfileIcon);
            TextView text = (TextView) v.findViewById(R.id.TxtProfile);

            if (image != null) {
                String path = model.getPhotoPath();
                Runnable active = new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = BitmapMaker.decodeSampledBitmapFromResource(path, image.getMaxWidth(), image.getMaxHeight());
                        image.post(() -> image.setImageBitmap(bitmap));
                    }
                };
                executor.execute(active);
            }


            if (text != null) {
                text.setText(model.toString());
            }
        }

        return v;
    }
}
