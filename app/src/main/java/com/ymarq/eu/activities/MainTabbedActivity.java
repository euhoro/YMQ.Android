package com.ymarq.eu.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.YmarqCallback;
import com.ymarq.eu.contacts.ContactsActivityNew;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.login.LoginActivity;
import com.ymarq.eu.messagestree.FragmentMessageTree;
import com.ymarq.eu.messagestree.MessageTreeActivity;
import com.ymarq.eu.news.NewsActivityNew;
import com.ymarq.eu.products.ProductsBuyerFragment2;
import com.ymarq.eu.subscriptions.SubscriptionActivity;
import com.ymarq.eu.tabs.SlidingTabsColorsFragment;
import com.ymarq.eu.utilities.UrlHelper;
import com.ymarq.eu.ymarq.R;

import java.io.File;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainTabbedActivity extends FragmentActivity implements YmarqCallback {

    public static final String TAG = "MainTabbedActivity";

    public void onYmarqItemSelected(Uri contentUri)
    {
        Intent intent = new Intent(this, MessageTreeActivity.class)
                .setData(contentUri);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mUniquePhoneId = UrlHelper.GetPhoneId4(this);
        PhoneEngine.getInstance().setApplicationContext(getApplicationContext());
        String userSerialized = PhoneEngine.getInstance().getUserDataById2(mUniquePhoneId, true);

        //if there is nothing se
        if(false == userSerialized.length()>0)
              //&& (getIntent()!=null && false == getIntent().hasExtra(Intent.EXTRA_TEXT)))
        {
            Intent ourIntent = new Intent(this, LoginActivity.class);
            startActivity(ourIntent);
            finish();
        }
        else {
            this.getIntent().putExtra(Intent.EXTRA_TEXT, userSerialized);
        }

        setContentView(R.layout.activity_main_tabbed);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsColorsFragment fragment = new SlidingTabsColorsFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        //sets the focus out of texbox
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Create global configuration and initialize ImageLoader with this config
        //ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                //.maxImageWidthForMemoryCache(800)
                //.maxImageHeightForMemoryCache(800)
                //.httpConnectTimeout(5000)
                //.httpReadTimeout(30000)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2000000)) // You can pass your own memory cache implementation
                .discCache(new UnlimitedDiscCache(getPhotoDirectory())) // You can pass your own disc cache implementation
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        ImageLoader.getInstance().init(config);

        Intent intent = getIntent();
        if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String userJson = intent.getStringExtra(Intent.EXTRA_TEXT);
            mUserData2 = DataUser.getFromJson(userJson);

            int tabToOpen = intent.getIntExtra("FirstTab", -1);
            if (tabToOpen!=-1) {
                // Open the right tab
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //@Override
    //public boolean onPrepareOptionsMenu(Menu menu) {
    //    MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
    //    return super.onPrepareOptionsMenu(menu);
    //}

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        //savedInstanceState.putString("CurrentUser", mUserData2.getAsJSON());
    }

///camera
public Uri CapturedImageURI;
    private CloudEngine mCloudEngine = CloudEngine.getInstance();
    public String CurrentPhotoPath;
    public DataUser mUserData2;

    public Uri getCapturedImageURI() {
        return CapturedImageURI;
    }

    public void setCapturedImageURI(Uri capturedImageURI) {
        CapturedImageURI = capturedImageURI;
    }

    public String getCurrentPhotoPath() {
        return CurrentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        CurrentPhotoPath = currentPhotoPath;
    }

    // Storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";

    // Required for camera operations in order to save the image file on resume.
    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {


        //todo remove this or make it handle rotation only

        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        }
        super.onRestoreInstanceState(savedInstanceState);
        //String mUser = savedInstanceState.getString("CurrentUser");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent ourIntent = new Intent(this, SettingsActivity.class);
            startActivity(ourIntent);
            return true;
        }
        //else if (id == R.id.action_map) {
        //    OpenPrefferedLocationApp();
        //    return true;
        //}
        //else if (id == R.id.action_news) {
        //    OpenNews();
        //    return true;
        //}
        else if (id == R.id.action_contacts) {
            OpenContacts();
            return true;
        }
        else if (id == R.id.action_subscriptions) {
            OpenSubscriptions();
            return true;
        }
        //from old sample
        //todo show an image on top
        //supportInvalidateOptionsMenu();
        //return true;

        return super.onOptionsItemSelected(item);
    }

    public File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if(externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(pictureDir, "Ymarq");
            if (!outputDir.exists()) {
                if(!outputDir.mkdirs()) {
                    Toast.makeText(this, "Failed to create directory: " + outputDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    outputDir = null;
                }
            }
        }

        return outputDir;
    }

    private void OpenContacts()
    {
        Intent intent = new Intent(this,ContactsActivityNew.class);
        intent.putExtra(Intent.EXTRA_TEXT,mUserData2.getAsJSON());
        startActivity(intent);
    }

    private void OpenSubscriptions()
    {
        Intent intent = new Intent(this,SubscriptionActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,mUserData2.getAsJSON());
        startActivity(intent);
    }

    private void OpenNews()
    {
        Intent intent = new Intent(this,NewsActivityNew.class);
        intent.putExtra(Intent.EXTRA_TEXT,mUserData2.getAsJSON());
        startActivity(intent);
    }

    private void OpenPrefferedLocationApp()
    {
        Uri geoLocation = Uri.parse("geo:0:0?").buildUpon().appendQueryParameter("q","2172797").build();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager())!= null)
            startActivity(intent);
        else
        {}
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        //super.onActivityResult();
    }


}