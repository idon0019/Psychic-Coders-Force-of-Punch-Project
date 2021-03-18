package com.example.myapplication.ImageMaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.example.myapplication.Executors.ImageExecutor;
import com.example.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executor;

/**
 * Class used to down scale bitmaps into smaller sizes so the app loads faster.
 * Also handles the creation and saving of bitmaps.
 */
public class BitmapMaker {
    /**
     * Downscales a bitmap.
     *
     * @param path      Bitmap to downscale.
     * @param reqWidth  Width of desired image.
     * @param reqHeight Height of desired image.
     * @return Downscaled image.
     */
    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Calculates the sample size to use when decoding.
     *
     * @param options   Bitmap options.
     * @param reqWidth  Width of desired image.
     * @param reqHeight Height of desired image.
     * @return Sample size.
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Sets the image using a separate thread.
     *
     * @param path  Path of the image file.
     * @param image ImageView to populate.
     */
    public static void setImage(String path, ImageView image) {
        Runnable run = () -> {
            final Bitmap bitmap = BitmapMaker.decodeSampledBitmapFromResource(path, image.getMaxWidth(), image.getMaxHeight());
            image.post(() -> image.setImageBitmap(bitmap));
        };

        Executor executor = new ImageExecutor();
        executor.execute(run);
    }

    /**
     * Creates a new image file.
     * @param context Application context.
     * @return Image file.
     * @throws IOException Error creating file.
     */
    public static File createNewImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String fileName = "JPEG_" + timeStamp + "_";

        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    /**
     * Saves a bitmap into an existing file.
     * @param bitmap Bitmap to save.
     * @param file File to save into.
     */
    public static void bitmapToFile(Bitmap bitmap, File file) { // File name like "image.png"
        //create a file to write bitmap data
        try {
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos); // YOU can also save it in JPEG
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Downscales and save the bitmap to a given file. This method is a simple combination of
     * decodeSampledBitMapFromResource() and bitmapToFile().
     * @param path Path of image file to downscale.
     * @param view View to fit the image to.
     * @param file File to save into.
     */
    public static void downscaleAndSaveBitMap(String path, ImageView view, File file) {
        Bitmap bitmap = decodeSampledBitmapFromResource(path, view.getWidth(), view.getHeight());
        bitmapToFile(bitmap, file);
    }
}
