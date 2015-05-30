package com.ymarq.eu.products;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ymarq.eu.common.view.MarkableImageView;
import com.ymarq.eu.ymarq.R;

/**
 * Created by eu on 3/31/2015.
 */
public class ProductsAdapter extends CursorAdapter {
    DisplayImageOptions options;
    public ProductsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //final ProductsViewHolder holder = new ProductsViewHolder();
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_item_small2, parent, false);

        options = new DisplayImageOptions.Builder()
                //.imageScaleType(ImageScaleType.EXACTLY)//test only
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .displayer(new RoundedBitmapDisplayer(10))//recently added for roundcorners
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        return view;
    }

    /*
    This is where we fill-in the views with the contents of the cursor.
    */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //int idx_prod_Id = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2);
        //int idx_prod_notifications = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS);
        //int idx_prod_description = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_DESCRIPTION);
        //int idx_prod_link = cursor.getColumnIndex(ProductsContract.ProductEntry.COLUMN_IMAGE_LINK);

        final MarkableImageView imageView = (MarkableImageView) view.findViewById(R.id.image);
        imageView.setChecked(false);//cursor.getInt(ProductsSellerFragment.COLUMN_PRODUCT_NOTIFICATIONS) > 0);

        //imageView.setMinimumHeight(imageView.getWidth());

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);

        final TextView textViewDescription = (TextView) view.findViewById(R.id.text_view_description);
        //textViewDescription.setVisibility(View.VISIBLE);
        textViewDescription.setText(cursor.getString(ProductsSellerFragment.COLUMN_DESCRIPTION));

        //view.setTag(holder);
        final ImageView imgFriends = (ImageView) view.findViewById(R.id.iconFriends);
        final ImageView imgGiveAway = (ImageView) view.findViewById(R.id.iconGiveAway);
        final ImageView imgEnvelope = (ImageView) view.findViewById(R.id.iconImageEnvelope);


        imgFriends.setVisibility(cursor.getInt(ProductsSellerFragment.COLUMN_PRODUCT_NOTIFY_FRIENDS) >0 ? View.VISIBLE :  View.INVISIBLE);
        imgGiveAway.setVisibility(cursor.getInt(ProductsSellerFragment.COLUMN_PRODUCT_GIVEAWAY) >0 ? View.VISIBLE : View.INVISIBLE);
        imgEnvelope.setVisibility(cursor.getInt(ProductsSellerFragment.COLUMN_PRODUCT_NOTIFICATIONS) > 0 ? View.VISIBLE : View.INVISIBLE);

        //imagePath = this.getPhotoDirectory().getAbsolutePath()+"/JPEG_"+mThumbIds.get(position).Id+".jpg";
        ImageLoader.getInstance()
                .displayImage(cursor.getString(ProductsSellerFragment.COLUMN_IMAGE_LINK), imageView, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        //todo save the image to file
                        int width = loadedImage.getWidth();
                        int height = loadedImage.getHeight();

                        progressBar.setVisibility(View.GONE);
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        progressBar.setProgress(Math.round(100.0f * current / total));
                    }
                });
    }
}
    //static class ProductsViewHolder {
    //    MarkableImageView imageView;
    //    ProgressBar progressBar;
    //    TextView textViewDescription;
    //}