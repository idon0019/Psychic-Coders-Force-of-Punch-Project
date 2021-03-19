package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.DatabaseHelper.MyAppProfileDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FirstFragment extends Fragment {

    private static final int FADE_DURATION_MS = 500;
    private static final float IMAGE_ALPHA_MAX = 1.0f;
    private static final float IMAGE_ALPHA_MIN = 0.2f;
    private NavController navController;

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MyAppProfileDatabase database = new MyAppProfileDatabase(getContext());
        navController = Navigation.findNavController(view);

        //Get Text View
        TextView tx = view.findViewById(R.id.textView4);

        ImageView img = view.findViewById(R.id.logo);
        //Set up tap functionality for the text.
        tx.setOnClickListener(v -> navController.navigate(R.id.action_firstFragment_to_secondFragment));

        Animation fadeIn = new AlphaAnimation(IMAGE_ALPHA_MIN,IMAGE_ALPHA_MAX);
        fadeIn.setDuration(FADE_DURATION_MS);

        Animation fadeOut = new AlphaAnimation(IMAGE_ALPHA_MAX,IMAGE_ALPHA_MIN);
        fadeOut.setDuration(FADE_DURATION_MS);

        Animation.AnimationListener listener;


        listener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //Log.d("My App", "Animation start!");
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                //Log.d("My App", "Animation done!");
                if (animation == fadeIn) {
                    img.startAnimation(fadeOut);
                } else {
                    img.startAnimation(fadeIn);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("My App", "Animation repeat!");
            }
        };
        fadeIn.setAnimationListener(listener);
        fadeOut.setAnimationListener(listener);
        //myAnimation.setRepeatMode(Animation.INFINITE);
        //myAnimation.setRepeatCount(10);
        img.startAnimation(fadeIn);

        cleanUnusedImages(database);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    }

    /**
     * Deletes all unused images in the Pictures folder. Note that this method is only used
     * in case of app crashes or premature app closure. This method does not replace proper
     * file management during image selection. Unused image files should still be deleted
     * immediately, not at app relaunch.
     * @param database Database holding currently used images.
     */
    private void cleanUnusedImages(@NotNull MyAppProfileDatabase database) {
        List<String> allImagePaths = database.getAllImagePathsFromDatabase();
        File directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] files = directory.listFiles();
        boolean delete = true;

        if (files == null || allImagePaths == null)
            return;


        for (File file: files) {
            for (String fileName: allImagePaths) {
                if (file.getAbsolutePath().equals(fileName))
                    delete = false;
            }

            if (delete)
                file.delete();
            delete = true;
        }
    }
}