package com.hastverket.profileimagemaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PhotoCropper extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_cropper);

//        InputStream is = getResources().openRawResource(R.raw.test360_small);
        InputStream is = getResources().openRawResource(R.raw.test360);

//        BitmapFactory.Options downscale = new BitmapFactory.Options();
//        downscale.inSampleSize = 4;
        BitmapFactory.Options info = new BitmapFactory.Options();
        info.inJustDecodeBounds = true;

//        Bitmap image = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.raw.test360, null);
        BitmapFactory.decodeStream(is, null, info);

        int width = info.outWidth;
        int height = info.outHeight;

        Bitmap output = null;

        try {
            BitmapRegionDecoder regionDecode = BitmapRegionDecoder.newInstance(is, false);
            Bitmap part = regionDecode.decodeRegion(new Rect(width/2, 0, width, height), null);

            output = Bitmap.createBitmap(width, height, part.getConfig(), part.hasAlpha());
            int size = part.getWidth()*part.getHeight();
            int[] pixels = new int[size];

            part.getPixels(pixels, 0, part.getWidth(), 0, 0, part.getWidth(), part.getHeight());
            output.setPixels(pixels, 0, part.getWidth(), 0,0, part.getWidth(), part.getHeight());

            part.recycle();
            part = regionDecode.decodeRegion(new Rect(0, 0, width/2, height), null);

            part.getPixels(pixels, 0, part.getWidth(), 0, 0, part.getWidth(), part.getHeight());
            output.setPixels(pixels, 0, part.getWidth(), width/2,0, part.getWidth(), part.getHeight());

            part.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "PhotoCropper");
        boolean res = path.mkdir();

        Log.d("PhotoCropper", "create folder " + res);

        File file = new File(path, "180flip.jpg");

        Log.d("PhotoCropper", "Save file as " + file.getAbsolutePath());

        if (output != null) {
            try {
                file.createNewFile();

                output.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ImageView iv = (ImageView) findViewById(R.id.imageview);

        if (output != null) {
            iv.setImageBitmap(output);
        }
    }
}
