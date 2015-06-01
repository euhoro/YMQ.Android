package com.ymarq.eu.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.ymarq.eu.activities.MainTabbedActivity;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.IOnUserReceived;
import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.services.DeviceService;
import com.ymarq.eu.sync.YmarqSyncAdapter;
import com.ymarq.eu.utilities.MyLocation;
import com.ymarq.eu.utilities.UrlHelper;
import com.ymarq.eu.utilities.YMQConst;
import com.ymarq.eu.ymarq.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>,IOnUserReceived{

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new android.support.v4.content.CursorLoader(this,
                    // Retrieve data rows for the device User's 'profile' contact.
                    Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                            ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                    // Select only email addresses.
                    ContactsContract.Contacts.Data.MIMETYPE +
                            " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                    .CONTENT_ITEM_TYPE},

                    // Show primary email addresses first. Note that there won't be
                    // a primary email address if the User hasn't specified one.
                    ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
            List<String> emails = new ArrayList<String>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                emails.add(cursor.getString(ProfileQuery.ADDRESS));
                cursor.moveToNext();
            }

            addEmailsToAutoComplete(emails);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {

        }

    /**
     * A dummy authentication store containing known User names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    //private static final String[] DUMMY_CREDENTIALS = new String[]{
    //        "foo@example.com:hello", "bar@example.com:world"
    //};
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    Context context ;

    public static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_REG_ID = "property_reg_id";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCMDemo";
    String SENDER_ID = "228482397663";

    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mNickNameView;
    private AutoCompleteTextView mPhoneNumberView;
    private Spinner mSpinnerCountryCode;
    public static final String BROADCAST = "PACKAGE_NAME.android.action.broadcast";
    //private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private DataUser mUserData;
    private LocationManager mLocationManager;
    private Location mLocation;
    private boolean isSendContacts = false;

    final static long MIN_TIME_INTERVAL = 60 * 1000L;
    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute

    //eugen
    // UI references.
    //private AutoCompleteTextView mNickNameView;
    private String mUniqueEmail;
    private CloudEngine mCloudEngine;

    private PhoneEngine mPhoneEngine;

    private String mUniquePhoneId;
    private String mNickName;
    private String mPhoneNumber;
    private String mEmail;
    private String mCountryCode;
    private String mRegistrationId;


    MyLocation myLocation = new MyLocation();
    Spinner mPhoneCodesView;
    private final String mConfigTxtFile = "config.txt";
    MyLocation.LocationResult mLocationResult = new MyLocation.LocationResult() {

        @Override
        public void gotLocation(final Location location) {
            //Got the location!
            mLocation = location;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mCloudEngine = CloudEngine.getInstance();

        mCloudEngine.applicationContext = this.getApplicationContext();
        mCloudEngine.setUserReceivedListener(this);

        mPhoneEngine = PhoneEngine.getInstance();
        mPhoneEngine.setApplicationContext(this.getApplicationContext());

        mUniquePhoneId = UrlHelper.GetPhoneId4(this);
        //move to next activity is already logged in
        String userSerialized = mPhoneEngine.getUserDataById2(mUniquePhoneId, true);

        context = getApplicationContext();

        // Check device for Play Services APK. If check succeeds, proceed with
        //  GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            mRegistrationId = getRegistrationId(context, userSerialized);
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        //if the User was  authenticated then move to the nex screen / add login always with the credentials stored
        if (userSerialized.length()>0)
        {
            mUserData = DataUser.getFromJson(userSerialized);
            if( mUserData.RegistrationId.equals(mRegistrationId)) {
                startMainActivity();//todo add login always or once
                //mCloudEngine.Login(mUserData.Id, true);
                return;
            }
        }


        //this happens on new install
        //if (mRegistrationId.isEmpty()) {

        //in any case we get the registration again
        registerInBackground();
        //}

        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSpinnerCountryCode = (Spinner) findViewById(R.id.phone_codes);

        //TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //String countryCode = tm.getSimCountryIso();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mNickNameView = (AutoCompleteTextView) findViewById(R.id.nick_name);
        mPhoneNumberView = (AutoCompleteTextView) findViewById(R.id.phone_number);
        mPhoneNumberView.requestFocus();
        populateAutoComplete();

        //eugen
        // Set up the login form.
        mPhoneCodesView = (Spinner) findViewById(R.id.phone_codes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.pref_country_codes, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        mPhoneCodesView.setAdapter(adapter);
        mPhoneCodesView.setSelection(100);

        //mNickNameView = (AutoCompleteTextView) findViewById(R.id.nick_name);
        populateDefaultEmail();
        mEmailView.setText(mUniqueEmail);
        mNickNameView.setText(mNickName);

       //mPasswordView = (EditText) findViewById(R.id.password);
       //mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
       //    @Override
       //    public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
       //        if (id == R.id.login || id == EditorInfo.IME_NULL) {
       //            attemptLogin();
       //            return true;
       //        }
       //        return false;
       //    }
       //});

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private String getRegistrationId(Context context,String userSerialized) {

        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(LoginActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void fireOnOneUserReceived(DataApiResult<DataUser> result)
    {
        showProgress(false);
        if (result == null)
        {
            return;
            //error
        }

        HandleLoginLogonResult(result);
       // StartNotificationServiceFirstTime();
       // startMainActivity();
    }

    private void writeUserToSharedPreferences2(String data) {
        SharedPreferences preferences = getSharedPreferences(YMQConst.SHARED_PREF_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(YMQConst.TEXT_SHARED_PREF_USER_KEY, data);
        editor.apply();
    }

    private void writeUserToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(mConfigTxtFile, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();

            DataUser mCurrentDataUser = DataUser.getFromJson(data);
            PhoneEngine.getInstance().addUser(mCurrentDataUser.Id, mCurrentDataUser.Name, true, false, mCurrentDataUser.Email, "0", "0", mCurrentDataUser.Phone, false, mCurrentDataUser.RegistrationId);
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    private void startMainActivity(){
       hideKeyboard();

       Intent ourIntent = new Intent(this, MainTabbedActivity.class)
               .putExtra(Intent.EXTRA_TEXT, mUserData.getAsJSON() );

       startActivity(ourIntent);

        YmarqSyncAdapter.initializeSyncAdapter(this);

        finish();
    }

    private void hideKeyboard() {
        InputMethodManager inputManager =
                (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(mPhoneNumberView.getWindowToken(), 0);
    }


    private void SendContacts(String userId,String userPhoneNumber)
    {
        String countryCode = mSpinnerCountryCode.getSelectedItem().toString();
        String[] items = countryCode.split(" ");
        int cCodeInt =Integer.parseInt(items[0].replace("(+", "").trim());
        DeviceService.startActionHandleContactsFirst(this, cCodeInt,userId);
    }

    private void StartNotificationServiceFirstTime() {

        //the service should no longer running at start since it is power by the gcm

        Intent intent2 = new Intent(BROADCAST);
        Bundle extras = new Bundle();
        extras.putString("send_data", "product_item_small2");
        intent2.putExtras(extras);
        sendBroadcast(intent2);

        //the order is very important here since the users should be already udated
        DeviceService.startActionUpdateContactsStatus(this.getApplicationContext(), mUserData.Id, false);
        DeviceService.startActionGetProductsAsync(this.getApplicationContext(), mUserData.getId(),false);//buyer
        //DeviceService.startActionGetProductsAsync(this.getApplicationContext(), mUserData.Id,true);//seller

        cleanYmarqDirectory();
    }

    private boolean cleanYmarqDirectory()
    {
        // here we can check if the user had this app installed
        //reinstalled
        File outputDir = null;
        File pictureDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        outputDir = new File(pictureDir, "Ymarq");
        if (outputDir.exists()) {
            String[] children = outputDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(outputDir, children[i]).delete();
            }
        }
        return true;
    }

    private void populateAutoComplete() {
        //getLoaderManager().initLoader(0, null, this);
    }

    private void populateDefaultEmail() {
        myLocation.getLocation(this, mLocationResult);

        mUniquePhoneId = UrlHelper.GetPhoneId4(this);

        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(this).getAccounts();

        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                mUniqueEmail = account.name;

                if (mUniqueEmail.contains("@")){
                    String[] separated = mUniqueEmail.split("@");
                    mNickName = separated[0];
                }
                return;
            }
        }
    }

    //public Location getLocation() {
    //    Location location=null;
    //    try {
    //        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//
    //        double lat;
    //        double lng;
    //        // getting GPS status
    //        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
    //        // getting network status
    //        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
    //        if (!isGPSEnabled && !isNetworkEnabled) {
    //            // no network provider is enabled
    //        } else {
    //            // First get location from Network Provider
    //            if (isNetworkEnabled) {
    //                mLocationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,  MIN_TIME_BW_UPDATES,  MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    //                Log.d("Network", "Network");
    //                if (mLocationManager != null) {
    //                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    //                    if (location != null) {
    //                        lat = location.getLatitude();
    //                        lng = location.getLongitude();
    //                    }
    //                }
    //            }
    //            //get the location by gps
    //            if (isGPSEnabled) {
    //                if (location == null) {
    //                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
    //                    Log.d("GPS Enabled", "GPS Enabled");
    //                    if (mLocationManager != null) {location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    //                        if (location != null) {
    //                            lat = location.getLatitude();
    //                            lng = location.getLongitude();
    //                        }
    //                    }
    //                }
    //            }
    //        }
//
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
//
    //    return location;
    //}

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        //if (mAuthTask2 != null) {
        //    return;
        //}

        if(mCloudEngine.IsBusy)
            return;

        // Reset errors.
        mEmailView.setError(null);
        //mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        mEmail = mEmailView.getText().toString();
        //String password = mPasswordView.getText().toString();

        mNickName = mNickNameView.getText().toString();
        mPhoneNumber = mPhoneNumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        //// Check for a valid password, if the User entered one.
        //if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
        //    mPasswordView.setError(getString(R.string.error_invalid_password));
        //    focusView = mPasswordView;
        //    cancel = true;
        //}

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }


        // Check for a valid nickname.
        if (TextUtils.isEmpty(mNickName)) {
            mNickNameView.setError(getString(R.string.error_field_required));
            focusView = mNickNameView;
            cancel = true;
        } else if (!isNickNameValid(mNickName)) {
            mNickNameView.setError(getString(R.string.error_invalid_nickname));
            focusView = mNickNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mPhoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_field_required));
            focusView = mPhoneNumberView;
            cancel = true;
        }
        // Check for a valid phone.
        else if (isPhone10DigitsAdditionalZero(mPhoneNumber)){
            mPhoneNumberView.setText(mPhoneNumberView.getText().toString().substring(1));
            mPhoneNumber = mPhoneNumber.substring(1);
        }
        else if (!isPhoneNumber(mPhoneNumber)) {
            mPhoneNumberView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneNumberView;
            cancel = true;
        }
        else if (mRegistrationId.isEmpty()) {
            mPhoneNumberView.setError(getString(R.string.error_invalid_gcm));
            focusView = mPhoneNumberView;
            registerInBackground();
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the User login attempt.
            showProgress(true);
            //mAuthTask2 = new UserLoginTask(email, password, mUniquePhoneId);
            //mAuthTask2.execute((Void) null);
            //(+972 )Israel545989828
            mCountryCode = mSpinnerCountryCode.getSelectedItem().toString();
            mCountryCode = mCountryCode.substring(1, mCountryCode.lastIndexOf(')')).trim();

            mPhoneNumber = mCountryCode + mPhoneNumber;

            mUserData = new DataUser(mUniquePhoneId,mEmail,mNickName,mPhoneNumber);
            mUserData.RegistrationId = mRegistrationId;

            DataApiResult<DataUser> result = mCloudEngine.LoginLogon(mUserData, true);


            //HandleLoginLogonResult(email, nickName, countryCode, phoneNumberFull, result);
        }
    }

    private void HandleLoginLogonResult(DataApiResult<DataUser> result) {
        //mEmail, mNickName, mCountryCode, mPhoneNumber,
        View focusView;
        boolean cancel;
        if (result != null && result.Error == null && result.Result!=null) {
            DataUser d = result.Result;
            if(d.Email.equals( mEmail)&&
                    d.Name.equals( mNickName) &&
                    d.Phone.equals(mPhoneNumber) &&
                    d.RegistrationId.equals(mRegistrationId))
            {
                writeUserToFile(mUserData.getAsJSON());

                //

                SendContacts(mUserData.Id,mPhoneNumber);

                StartNotificationServiceFirstTime();

                showProgress(false);

                startMainActivity();
            }
            else
            {
                String error = getResources().getString(R.string.label_login_previous_data_found) ;
                Toast.makeText(this, error, Toast.LENGTH_SHORT)
                        .show();

                showProgress(false);
                if (!d.Email.equals( mEmail)) {
                    mEmailView.setError(getResources().getString(R.string.label_login_different_email));
                    mEmailView.setText(d.Email);
                    focusView = mEmailView;
                    cancel = true;
                }
                if (! d.Name.equals( mNickName)) {
                    mNickNameView.setError(getResources().getString(R.string.label_login_different_name));
                    mNickNameView.setText(d.Name);
                    focusView = mNickNameView;
                    cancel = true;
                }
                // Check for a valid phone.
                if (!d.Phone.equals(mPhoneNumber)){
                    mPhoneNumberView.setError(getResources().getString(R.string.label_login_different_phone));
                    mPhoneNumberView.setText("0" + d.Phone.replace(mCountryCode, ""));
                    focusView = mPhoneNumberView;
                    cancel = true;
                }
            }
        }
        else
        {
            String error = getResources().getString(R.string.label_login_failed) ;

            if (result != null && result.Error != null)
                error+=":"+ result.Error;

            Toast.makeText(this,error, Toast.LENGTH_LONG)
                    .show();
            //todo: switch to pasword when added
            showProgress(false);
            mPhoneNumberView.setError(error);
            focusView = mPhoneNumberView;
            cancel = true;
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isNickNameValid(String nickName) {
        //TODO: Replace this with your own logic
        return nickName.length()>5;
    }

    private boolean isPhoneNumber(String phoneNumber) {
        //TODO: Replace this with your own logic
        return phoneNumber.length()== 9 && !phoneNumber.substring(0,1).equals("0");
    }

    private boolean isPhone10DigitsAdditionalZero(String phoneNumber) {
        //TODO: Replace this with your own logic
        return phoneNumber.length()== 10 &&  phoneNumber.substring(0,1).equals("0");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<String,String,String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    mRegistrationId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + mRegistrationId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, mRegistrationId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
                String s = msg;
            }
        }.execute(null, null, null);

    }


    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    ///    //todo: check how to clean up
    ///    //@Override
    ///    //protected void onCancelled() {
    ///    //    mAuthTask2 = null;
    ///    //    showProgress(false);
    ///    //}
    ///}

   //public void onClick(final View view) {
   //    if (view == findViewById(R.id.send)) {
   //        new AsyncTask<String, String ,String>(){
   //            @Override
   //            protected String doInBackground(String... params) {
   //                String msg = "";
   //                try {
   //                    Bundle data = new Bundle();
   //                    data.putString("my_message", "Hello World");
   //                    data.putString("my_action",
   //                            "com.google.android.gcm.demo.app.ECHO_NOW");
   //                    String id = Integer.toString(msgId.incrementAndGet());
   //                    gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
   //                    msg = "Sent message";
   //                } catch (IOException ex) {
   //                    msg = "Error :" + ex.getMessage();
   //                }
   //                return msg;
   //            }

   //            @Override
   //            protected void onPostExecute(String msg) {
   //                mDisplay.append(msg + "\n");
   //            }
   //        }.execute(null, null, null);
   //    } else if (view == findViewById(R.id.clear)) {
   //        mDisplay.setText("");
   //    }
   //}
}



