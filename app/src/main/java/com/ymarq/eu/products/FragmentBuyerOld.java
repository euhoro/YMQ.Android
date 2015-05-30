package com.ymarq.eu.products;

/**
 * Created by eu on 3/31/2015.
 */

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.IOnProductsReceived;
import com.ymarq.eu.common.view.MarkableImageView;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataSubscription;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.messagestree.MessageTreeActivity;
import com.ymarq.eu.ymarq.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentBuyerOld extends Fragment implements IOnProductsReceived {

    ImageAdapterFrag productsArrayAdapter;
    private CloudEngine mCloudEngine = CloudEngine.getInstance();
    private PhoneEngine mPhoneEngine = PhoneEngine.getInstance();
    DataSubscription mDataSubscription;
    DataUser mCurrentUser;
    private DisplayImageOptions options;
    private static final int MENU_DELETE = 99;
    private static final int MENU_UPDATE = 98;
    GridView mGridView;

    public FragmentBuyerOld() {
        mCloudEngine.setProductsReceivedListener(this);
    }

    @Override
    public void fireOnProductsReceived(List<DataProduct> products) {
        productsArrayAdapter.clear();
        productsArrayAdapter.addAll(products);
        productsArrayAdapter.notifyDataSetChanged();
        //FetchProductsTask ftp = new FetchProductsTask(getActivity().getApplicationContext(),null);
        if (products.size() == 0) {
            Toast.makeText(getActivity(), "No products found ! Please try again later or wait for products to be added", Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        for(DataProduct dp : products)
        {
            //long userid = ftp.addUser(dp.UserId,"",false,false,"","","","",false);
            //long longprodId = ftp.addProduct(userid,dp.UserId,dp.Id,dp.Description,dp.Image);
            mPhoneEngine.addProductToProvider(dp);
        }
    }

    @Override
    public void fireOnOneProductReceived(DataProduct products) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mPhoneEngine.setApplicationContext(getActivity().getApplicationContext());
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .displayer(new RoundedBitmapDisplayer(10))//recently added for roundcorners
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        final View rootView = inflater.inflate(R.layout.fragment_buyer_subscription_products, container, false);


        List<DataProduct> subscriptionProducts = new ArrayList<DataProduct>();

        productsArrayAdapter = new ImageAdapterFrag(getActivity(), subscriptionProducts);

        mGridView = (GridView) rootView.findViewById(R.id.gridview_products_list);
        registerForContextMenu(mGridView);

        mGridView.setAdapter(productsArrayAdapter);

        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT))
        {
            String subscriptionText = intent.getStringExtra(Intent.EXTRA_TEXT);
            mDataSubscription = DataSubscription.getFromJson(subscriptionText);

            String userSerialized = intent.getStringExtra(Intent.EXTRA_TITLE);
            mCurrentUser = DataUser.getFromJson(userSerialized);

            GetProductsBySubscription(mDataSubscription);

            Button btnDel = (Button)rootView.findViewById(R.id.BtnDeleteSubscription);
            btnDel.setOnClickListener(
                    new View.OnClickListener()
                    {
                        public void onClick(View view) {
                            DeleteSubsscription(mDataSubscription);
                        }
                    });
        }

        //
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataProduct selectedProduct = productsArrayAdapter.getItem(position);

                //Intent ourIntent = new Intent(rootView.getContext(), MessageTreeActivity.class)
                //        .putExtra(Intent.EXTRA_TEXT, selectedProduct.getAsJSON());


                Intent ourIntent = new Intent(getActivity(), MessageTreeActivity.class)
                        .setData(ProductsContract.ProductEntry.buildProductsUserWithProductId2(
                                selectedProduct.UserId, selectedProduct.Id
                        ));


                ourIntent.putExtra(Intent.EXTRA_TITLE, getClass().toString());
                startActivity(ourIntent);
            }
        });


        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mCurrentUser.Id !="76971ea670ff89be")
            return;

        if (v.getId() == R.id.gridview_products_list) {
            GridView lv = (GridView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //Object obj =  lv.getItemAtPosition(acmi.position);//this can cause crash uncaught exception - casting DataProduct

            menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
            menu.add(Menu.NONE, MENU_UPDATE, Menu.NONE, "Update");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                DataProduct obj = (DataProduct) mGridView.getItemAtPosition(info.position);

                //Log.d(Tag, "removing item pos=" + info.position);
                //productsArrayAdapter.remove(obj);
                productsArrayAdapter.notifyDataSetChanged();
                mCloudEngine.DeleteProduct(obj,true);

                return true;
            case MENU_UPDATE:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void DeleteSubsscription(DataSubscription dataSubscription) {
        mCloudEngine.DeleteSubscription(dataSubscription,true);
        getActivity().finish();
    }

    private void GetProductsBySubscription(DataSubscription ds) {
        mCloudEngine.GetProductsBySubscription(mCurrentUser.Id, ds.SearchText,true);
    }

    private void GetProductsBySubscriptionOld2tobeDeleted(DataSubscription ds)
    {
        List<DataProduct> products = new ArrayList<DataProduct>();

        DataProduct dp = new DataProduct();
        dp.Description ="Descrp";
        dp.Hashtag ="#hash";
        dp.Id ="idDummy";
        dp.Image ="image";
        dp.ImageContent ="imagecontent";
        dp.UserId ="userIDsubby";

        products.add(dp);

        productsArrayAdapter.clear();
        productsArrayAdapter.addAll(products);
    }

    public class ImageAdapterFrag extends BaseAdapter {
        private static final int PADDING = 8;
        private static final int WIDTH = 250;
        private static final int HEIGHT = 250;
        private Context mContext;
        private List<DataProduct> mProducts;

        // Store the list of image IDs
        public ImageAdapterFrag(Context c, List<DataProduct> products) {
            mContext = c;
            this.mProducts = products;
        }

        public void clear() {
            mProducts.clear();
        }

        public void addAll(List<DataProduct> products) {
            mProducts.addAll(products);
        }

        public void remove(DataProduct product) {
            mProducts.remove(product);
        }

        public void add(DataProduct product) {
            mProducts.add(product);
        }

        // Return the number of items in the Adapter
        @Override
        public int getCount() {
            return mProducts.size();
        }

        // Return the data item at position
        @Override
        public DataProduct getItem(int position) {
            return mProducts.get(position);
        }

        // Will get called to provide the ID that
        // is passed to OnItemClickListener.onItemClick()
        @Override
        public long getItemId(int position) {
            return mProducts.get(position).hashCode();
        }

        //old
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
                //view = LayoutInflater.from(mContext).inflate(R.layout.universal_item_grid, parent, false);
                view = LayoutInflater.from(mContext).inflate(R.layout.product_item_small2, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (MarkableImageView) view.findViewById(R.id.image);
                holder.imageView.setChecked(mProducts.get(position).getNumberOfNotifications()>0);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                //holder.textViewDescription = (TextView) view.findViewById(R.id.text_view_description);
                //holder.textViewDescription.setText(mProducts.get(position).Description);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            //imagePath = this.getPhotoDirectory().getAbsolutePath()+"/JPEG_"+mThumbIds.get(position).Id+".jpg";
            ImageLoader.getInstance()
                    .displayImage(mProducts.get(position).Image, holder.imageView, options, new SimpleImageLoadingListener() {
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
                            //todo save the image to file
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

    static class ViewHolder {
        MarkableImageView imageView;
        ProgressBar progressBar;
        TextView textViewDescription;
    }
}