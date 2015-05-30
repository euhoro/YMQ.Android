package com.ymarq.eu.business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by eu on 4/30/2015.
 */
public class StorageEngine {
    private static StorageEngine ourInstance = new StorageEngine();

    public static StorageEngine getInstance() {
        return ourInstance;
    }

    private StorageEngine() {
    }

    public String getEncoded(String localPath)
    {
        Bitmap rotated = BitmapFactory.decodeFile(localPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotated.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        byte[] bytes = baos.toByteArray();
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedString;
    }
}
