package com.ymarq.eu.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by eu on 1/31/2015.
 */
public class ImageViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setBackgroundColor(Color.BLACK);

        if (i!=null && i.hasExtra(Intent.EXTRA_TEXT))//otherwise it will crash later
        {
            String imageLocation = i.getStringExtra(Intent.EXTRA_TEXT);
            Bitmap b2 = loadImage(imageLocation);

            imageView.setImageBitmap(b2);

            setContentView(imageView);
        }
    }

    private Bitmap loadImage(String imgPath) {
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            return bitmap;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}