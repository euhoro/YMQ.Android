package com.ymarq.eu.gallery;

/**
 * Created by eu on 2/11/2015.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.ymarq.R;

import java.io.File;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private static final int PADDING = 8;
    private static final int WIDTH = 250;
    private static final int HEIGHT = 250;
    private Context mContext;
    private List<DataProduct> mThumbIds;

    // Store the list of image IDs
    public ImageAdapter(Context c, List<DataProduct> ids) {
        mContext = c;
        this.mThumbIds = ids;
    }

    public void clear() {
        mThumbIds.clear();
    }

    public void addAll(List<DataProduct> products) {
        mThumbIds.addAll(products);
    }

    public void remove(DataProduct product) {
        mThumbIds.remove(product);
    }

    // Return the number of items in the Adapter
    @Override
    public int getCount() {
        return mThumbIds.size();
    }

    // Return the data item at position
    @Override
    public DataProduct getItem(int position) {
        return mThumbIds.get(position);
    }

    // Will get called to provide the ID that
    // is passed to OnItemClickListener.onItemClick()
    @Override
    public long getItemId(int position) {
        return mThumbIds.get(position).hashCode();
    }

    // Return an ImageView for each item referenced by the Adapter
    //@Override
    //public View getView(int position, View convertView, ViewGroup parent) {
//
    //    ImageView imageView = (ImageView) convertView;
//
    //    // if convertView's not recycled, initialize some attributes
    //    if (imageView == null) {
    //        imageView = new ImageView(mContext);
    //        imageView.setLayoutParams(new GridView.LayoutParams(WIDTH, HEIGHT));
    //        imageView.setPadding(PADDING, PADDING, PADDING, PADDING);
    //        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    //    }
//
    //    String imagePath = this.getPhotoDirectory().getAbsolutePath()+"/JPEG_"+mThumbIds.get(position).Id+".jpg";
//
    //    Bitmap b = loadImage(imagePath);
    //    imageView.setImageBitmap(b);
    //    return imageView;
    //}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.universal_item_grid, parent, false);
            holder = new ViewHolder();
            assert view != null;
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //imagePath = this.getPhotoDirectory().getAbsolutePath()+"/JPEG_"+mThumbIds.get(position).Id+".jpg";
        ImageLoader.getInstance()
                .displayImage(mThumbIds.get(position).Image, holder.imageView, null , new SimpleImageLoadingListener() { //use options instead of null
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.progressBar.setProgress(0);
                        holder.progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        holder.progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });

        return view;
    }


    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
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

    File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(pictureDir, "Ymarq");
            if (!outputDir.exists()) {
                if(!outputDir.mkdirs()) {
                    //Toast.makeText(this, "Failed to create directory: " + outputDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    outputDir = null;
                }
            }
        }

        return outputDir;
    }
}

