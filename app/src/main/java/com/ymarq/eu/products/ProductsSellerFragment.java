package com.ymarq.eu.products;

/**
 * Created by eu on 2/11/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ymarq.eu.contacts.ContactsActivityNew;
import com.ymarq.eu.activities.MainTabbedActivity;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.IOnProductsReceived;
import com.ymarq.eu.common.view.MarkableImageView;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.messagestree.MessageTreeActivity;
import com.ymarq.eu.services.DeviceService;
import com.ymarq.eu.tabs.ContentFragment;
import com.ymarq.eu.utilities.YMQConst;
import com.ymarq.eu.ymarq.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by eu on 2/1/2015.
 */
public class ProductsSellerFragment extends ContentFragment implements IOnProductsReceived, Button.OnClickListener ,LoaderManager.LoaderCallbacks<Cursor>{
    private static int LOADER_PRODUCTS = 0;
    public static final String KEY_TITLE = "title";
    public static final String KEY_INDICATOR_COLOR = "indicator_color";
    public static final String KEY_DIVIDER_COLOR = "divider_color";


    private static final String[] PRODUCTS_COLUMNS = {
            ProductsContract.UserEntry.TABLE_NAME + "." + ProductsContract.UserEntry._ID,
            ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,
            ProductsContract.ProductEntry.COLUMN_DESCRIPTION,
            ProductsContract.ProductEntry.COLUMN_IMAGE_LINK,
            ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS,

            ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS,
            ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS ,
            ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY
    };

    // These indices are tied to above
    // must change.
    public static final int COL_USER_ID = 0;
    public static final int COLUMN_PRODUCT_ID2 = 1;
    public static final int COLUMN_DESCRIPTION = 2;
    public static final int COLUMN_IMAGE_LINK =3;
    public static final int COLUMN_PRODUCT_NOTIFICATIONS = 4;

    public static final int COLUMN_PRODUCT_NOTIFY_FRIENDS = 5;
    public static final int COLUMN_PRODUCT_NOTIFY_OTHERS = 6;
    public static final int COLUMN_PRODUCT_GIVEAWAY = 7;

    public static final String PRODUCTS_ORDER = ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFICATIONS+ " DESC";
    public static final String PRODUCTS_FILTER = ProductsContract.ProductEntry.COLUMN_USER_ID2 + " = ?";

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if( mCurrentDataUser == null) {//todo - this might ned a fix since when it is not the phone owner might cause issues
            String userJson = PhoneEngine.getInstance().getUserDataById2(null, true);
            //String userJson = DeviceService.startActionGetCurrentUser(getActivity());
            mCurrentDataUser = DataUser.getFromJson(userJson);
        }

        Uri productsForUserIdUri = ProductsContract.ProductEntry.buildProductsWithUserId2(
                mCurrentDataUser.Id);

        return new CursorLoader(getActivity(),productsForUserIdUri,
                PRODUCTS_COLUMNS, PRODUCTS_FILTER, new String[]{ mCurrentDataUser.Id }, PRODUCTS_ORDER);

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (data == null)
        {
            return;
        }
        productsArrayAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        productsArrayAdapter.swapCursor(null);
    }

    public static final String KEY_PRODUCT_LIST_KEY = "products";

    private static final int MENU_DELETE = 99;
    private static final int MENU_UPDATE = 98;
    DisplayImageOptions options;

    //camera take pictures
    int TAKE_PHOTO_CODE = 0;
    public static int count=0;
    String dir;
    static String LOGGER_TAG = "LOGGERWEB";
    ProductsAdapter productsArrayAdapter;
    List<DataProduct> mProductList;
    DataUser mCurrentDataUser;
    Button takePictureButton;
    Button buyerButton;
    AutoCompleteTextView mEdit;
    GridView mGridView;

    // Activity result key for camera
    static final int REQUEST_TAKE_PHOTO = 11511;
    // Image view for showing our image.
    private MarkableImageView mImageView;
    public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    private CloudEngine mCloudEngine2 = CloudEngine.getInstance();
    public ProductsSellerFragment() {
        super();//base
    }

    @Override
    public void onResume()//fix
    {
        super.onResume();

        getLoaderManager().restartLoader(LOADER_PRODUCTS, null, this);
    }

    public static ProductsSellerFragment newInstance(CharSequence title, int indicatorColor,
                                                int dividerColor) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_INDICATOR_COLOR, indicatorColor);
        bundle.putInt(KEY_DIVIDER_COLOR, dividerColor);

        ProductsSellerFragment fragment = new ProductsSellerFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void fireOnProductsReceived(List<DataProduct> products) {
    }

    @Override
    public void fireOnOneProductReceived(DataProduct product)
    {
        DataMessage dm = new DataMessage(product.Description,mCurrentDataUser.Id,product.Id);
        mCloudEngine2.SendMessage(dm, true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setHasOptionsMenu(true);

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.gridview_products_list) {
            GridView lv = (GridView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;

            menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Cursor cursor = (Cursor) mGridView.getItemAtPosition(info.position);

                if (cursor != null) {
                    DataProduct obj = new DataProduct();
                    obj.Id = cursor.getString(COLUMN_PRODUCT_ID2);
                    obj.UserId = mCurrentDataUser.Id;

                    DeviceService.startActionDeleteProduct(getActivity(),obj.getAsJSON(),true);
                }
                return true;
            case MENU_UPDATE:
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action
        inflater.inflate(R.menu.menu_refresh, menu);
        inflater.inflate(R.menu.menu_camera, menu);
        inflater.inflate(R.menu.menu_contacts_activity_new, menu);
        inflater.inflate(R.menu.menu_buyer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatementun
        //if (id == R.id.action_refresh)
        //{
        //    GetUserProductsFromCloud2(mCurrentDataUser.Id);
        //    return true;
        //}
        //else if (id == R.id.action_camera)
        //{
        //    dispatchTakePictureIntent();
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        // Check if there is a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // here,counter will be incremented each time,and the picture taken by camera will be stored as 1.jpg,2.jpg and likewise.
        count++;
        String file = dir+count+".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {}

        Uri outputFileUri = Uri.fromFile(newfile);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activityforre to handle the intent
        MainTabbedActivity activity = (MainTabbedActivity)getActivity();

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
// Create the File where the photo should go.
// If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
// Error occurred while creating the File
                Toast toast = Toast.makeText(activity, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
// Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                activity.setCapturedImageURI(fileUri);
                activity.setCurrentPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        activity.getCapturedImageURI());
                try {
                    getParentFragment().startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
                catch (Exception ex) {
                    Toast.makeText(activity, "There was a problem taking the picture.Check that the camera is not in use ", Toast.LENGTH_SHORT);
                }
            }
        }
    }

    /**
     * The activity returns with the photo.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getActivity(), "User Canceled", Toast.LENGTH_LONG).show();
            return;
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {

            MainTabbedActivity activity = (MainTabbedActivity)getActivity();
            if( activity.getCurrentPhotoPath() == null)
            {
                Toast.makeText(activity, "Please try againt with different orientation ", Toast.LENGTH_LONG).show();
                return;
            }

            addPhotoToGallery();


            //Bitmap rotated = getBitmapFromFullImage(activity.getCurrentPhotoPath());
            Bitmap imageBitmap = BitmapFactory.decodeFile(activity.getCurrentPhotoPath());

            Bitmap rotated = getBitmapRotated90(imageBitmap);

            imageBitmap = null;

            String pathRotated = saveToInternalSorage2(rotated, activity.CapturedImageURI);

            rotated = null;

            String encodedString = "";

            ShowDialogSeller(pathRotated, encodedString, mCurrentDataUser.Id);
            mEdit.setText("");
        } else {
            Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private Bitmap getBitmapFromFullImage2(MainTabbedActivity activity) {
        Bitmap bitmap = decodeScaledBitmapFromSdCard(activity.getCurrentPhotoPath(),1600,1200);
        Bitmap rotated = getBitmapRotated90(bitmap);
        bitmap = null;
        return rotated;
    }

    private Bitmap getBitmapRotated90(Bitmap imageBitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        Bitmap resized2 = Bitmap.createScaledBitmap(imageBitmap,(int)(imageBitmap.getWidth()*0.2), (int)(imageBitmap.getHeight()*0.2), true);

        return Bitmap.createBitmap(resized2, 0, 0, resized2.getWidth(),resized2.getHeight(), matrix, false);
    }


    public static Bitmap decodeScaledBitmapFromSdCard(String filePath,
                                                      int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    private boolean getCheckedValue() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getActivity().getResources().getString(R.string.pref_pref_show_contacts_on_add_product_key);

        boolean def = true;

        boolean val = prefs.getBoolean(key, def);
        return val;
    }

    String ShowDialogSeller(final String localPath2, final String encodedString, final String userId)
    {
        String res = null;
        Activity activity = this.getActivity();
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setTitle(getActivity().getResources().getString(R.string.label_add_new_product_description));
        View checkBoxView = View.inflate(getActivity(), R.layout.alert_with_choises, null);

        final CheckBox checkBoxNotifyFriends = (CheckBox) checkBoxView.findViewById(R.id.checkbox_notify_friends);
        final CheckBox checkBoxGiveAway = (CheckBox) checkBoxView.findViewById(R.id.checkbox_to_give_avawy);
        final EditText input = (EditText) checkBoxView.findViewById(R.id.editBox_description);
        input.requestFocus();

        alert.setView(checkBoxView);

        alert.setPositiveButton(getActivity().getResources().getString(R.string.label_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // res = input.getText().toString();
                // Do something with value!
                DataProduct dataProduct = new DataProduct();
                dataProduct.Description = input.getText().toString();
                dataProduct.Id ="";
                dataProduct.ImageContent = encodedString;

                dataProduct.Image = localPath2;

                dataProduct.UserId = userId;
                dataProduct.GiveAway=checkBoxGiveAway.isChecked();
                dataProduct.NotifyFriends=checkBoxNotifyFriends.isChecked();
                //mCloudEngine2.PublishProduct(dataProduct,true);
                DeviceService.startActionAddProductAsync(getActivity(),dataProduct.getAsJSON());

                if (checkBoxNotifyFriends.isChecked() && getCheckedValue() == true) {
                    Intent intent = ContactsActivityNew.CreateIntent(getActivity(),mCurrentDataUser,false);
                    startActivity(intent);
                }
            }
        });

        alert.setNegativeButton(getActivity().getResources().getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
        return res;
    }

    private String saveToInternalSorage2(Bitmap bitmapImage, Uri uriOriginal ){

        String path2 = uriOriginal.getPath()+".resized.jpg";

        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(path2);

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path2;
    }

    /**
     * Creates the image file to which the image must be saved.
     * @return
     * @throws IOException
     */
    protected File createImageFile() throws IOException {
// Create an image file name
        MainTabbedActivity activity = (MainTabbedActivity)getActivity();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getPhotoDirectory();
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
// Save a file: path for use with ACTION_VIEW intents

        activity.setCurrentPhotoPath("file:" + image.getAbsolutePath());
        return image;
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//Intent.ACTION_MEDIA_MOUNTED
        MainTabbedActivity activity = (MainTabbedActivity)getActivity();
        File f = new File(activity.getCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);
    }
    /**
     * Deal with button clicks.
     * @param v
     */
    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }


    /**
     * Scale the photo down and fit it to our image views.
     *
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
// Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
// Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
// Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
// Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private Cursor GetUserProductsFromCloud2(String userId) {

        //get the data if not already in cusror
        //mCloudEngine2.setProductsReceivedListener(this);
        //mPhoneEngine.getProductsAsync(userId,true);
        DeviceService.startActionGetProductsAsync(getActivity(),userId,true);

        Uri productsForUserIdUri = ProductsContract.ProductEntry.buildProductsWithUserId2(
                userId);

        Cursor cur = getActivity().getContentResolver().query(productsForUserIdUri,
                PRODUCTS_COLUMNS, PRODUCTS_FILTER, new String[]{ mCurrentDataUser.Id }, PRODUCTS_ORDER);

        return cur;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_seller, container, false);

        takePictureButton = (Button)rootView.findViewById(R.id.btnCam);
        // Set OnItemClickListener so we can be notified on button clicks
        takePictureButton.setOnClickListener(this);

        buyerButton = (Button)rootView.findViewById(R.id.btnBuyer);
        // Set OnItemClickListener so we can be notified on button clicks
        buyerButton.setOnClickListener(this);


        mEdit   = (AutoCompleteTextView)rootView.findViewById(R.id.editText);
        mEdit.clearFocus();

        List<DataProduct> productsList = new ArrayList<DataProduct>();

        mGridView = (GridView) rootView.findViewById(R.id.gridview_products_list);
        registerForContextMenu(mGridView);

        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String serializedUser = intent.getStringExtra(Intent.EXTRA_TEXT);
            mCurrentDataUser = DataUser.getFromJson(serializedUser);
        }
        else {
            //todo refactor
            String userJson = PhoneEngine.getInstance().getUserDataById2(null, true);
            //String userJson = DeviceService.startActionGetCurrentUser(getActivity());
            mCurrentDataUser = DataUser.getFromJson(userJson);
        }

        Cursor cursor = GetUserProductsFromCloud2(mCurrentDataUser.Id);
        productsArrayAdapter = new ProductsAdapter(getActivity(),cursor,0);
        mGridView.setAdapter(productsArrayAdapter);

        //getLoaderManager().initLoader(LOADER_PRODUCTS,null,this);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

               if (cursor != null) {
                   Intent intent = new Intent(getActivity(), MessageTreeActivity.class)
                           .setData(ProductsContract.ProductEntry.buildProductsUserWithProductId2(
                                   mCurrentDataUser.Id, cursor.getString(COLUMN_PRODUCT_ID2)
                           ));

                   startActivity(intent);
               }
           }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        MainTabbedActivity activity = (MainTabbedActivity)getActivity();

        if (mProductList!=null) {
            outState.putString(KEY_PRODUCT_LIST_KEY, DataProduct.getJsonFromList(mProductList));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            String  products = savedInstanceState.getString(KEY_PRODUCT_LIST_KEY, "");
            List<DataProduct> productsList = DataProduct.getProductsFromJson(products);
        }
        if( mCurrentDataUser == null)
        {
            SharedPreferences preferences = getActivity().getSharedPreferences(YMQConst.SHARED_PREF_KEY, getActivity().MODE_PRIVATE);
            String text = preferences.getString(YMQConst.TEXT_SHARED_PREF_USER_KEY, "");
            mCurrentDataUser = DataUser.getFromJson(text);
        }
        getLoaderManager().initLoader(LOADER_PRODUCTS, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}