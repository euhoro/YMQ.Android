package com.ymarq.eu.contacts;

/**
 * Created by eu on 2/10/2015.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ymarq.eu.activities.MainTabbedActivity;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.IOnContactsReceived;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataFriendContact;
import com.ymarq.eu.entities.DataGroupFriends;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.services.DeviceService;
import com.ymarq.eu.ymarq.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentContactsNew extends android.support.v4.app.Fragment implements IOnContactsReceived, LoaderManager.LoaderCallbacks<Cursor>{

    private static final int USER_LOADER = 6;

    private static final String[] USER_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & product tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the product table
            // using the location set by the User, which is only in the Location table.
            // So the convenience is worth it.
            ProductsContract.UserEntry.TABLE_NAME + "." + ProductsContract.UserEntry._ID,
            ProductsContract.UserEntry.COLUMN_USER_ID2,
            ProductsContract.UserEntry.COLUMN_USER_PHONE_NUMBER ,
            ProductsContract.UserEntry.COLUMN_USER_NICKNAME,
            ProductsContract.UserEntry.COLUMN_USER_IS_FRIEND,
            ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT
    };

    private static final String USER_SORT_ORDER = ProductsContract.UserEntry.COLUMN_USER_ID2 + " DESC "+ "," + ProductsContract.UserEntry.COLUMN_USER_NICKNAME +" ASC";
    private static final String USER_CONDITION = ProductsContract.UserEntry.COLUMN_USER_IS_CONTACT +" = ?";
    private static final String [] USER_CONDITION_PARAM = new String[]{"1"};

    // These indices are tied to user columns.  If changes, these
    // must change.
    public static final int COL_USER_ID = 0;
    public static final int COLUMN_USER_ID2=1;
    public static final int COLUMN_USER_PHONE_NUMBER =2;
    public static final int COLUMN_USER_NICKNAME=3;
    public static final int COLUMN_USER_IS_FRIEND=4;
    public static final int COLUMN_USER_IS_CONTACT=5;
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

      return new CursorLoader(getActivity(),ProductsContract.UserEntry.CONTENT_URI,
                USER_COLUMNS,USER_CONDITION ,USER_CONDITION_PARAM,USER_SORT_ORDER );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    ToggleButton mButtonOk;
    CheckBox mRememeberFriends;
    ToggleButton mButtonSelectAll;
    ListView mListViewContacts;
    ContactCursorAdapter mAdapter ;
    private CloudEngine mCloudEngine;
    private PhoneEngine mPhoneEngine;
    private List<DataFriendContact> mPhoneList2;
    private DataUser mUserData;
    private String mInvitationMessage =" https://play.google.com/store/apps/details?id=com.ymarq.eu.ymarq";
    private boolean mFromSettings;
    public FragmentContactsNew() {
    }

    @Override
    public void fireOnContactsReceived(List<DataFriendContact> contactsStatus) {
        //mPhoneEngine.updateUsersProvider(mUserData.Id, contactsStatus);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(USER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        mInvitationMessage = getResources().getString(R.string.label_invitation) + " : https://play.google.com/store/apps/details?id=com.ymarq.eu.ymarq";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String userJson = intent.getStringExtra(Intent.EXTRA_TEXT);
            mUserData = DataUser.getFromJson(userJson);
        }

        if (intent!=null && intent.hasExtra(Intent.EXTRA_TITLE)) {
            mInvitationMessage = intent.getStringExtra(Intent.EXTRA_TITLE);
        }

        if (intent!=null && intent.hasExtra(ContactsActivityNew.EXTRA_SETTINGS)) {
            mFromSettings = false;
        }
        else {
            mFromSettings = true;
        }


        View rootView = inflater.inflate(R.layout.fragment_contacts_activity_new, container, false);

        mCloudEngine = CloudEngine.getInstance();
        mCloudEngine.setContactsReceivedListener(this);
        mCloudEngine.setApplicationContext(getActivity().getApplicationContext());

        mPhoneEngine = PhoneEngine.getInstance();
        mPhoneEngine.setApplicationContext(getActivity().getApplicationContext());

        mListViewContacts = (ListView)rootView.findViewById(R.id.listview_contacts);
        //mListViewContacts.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        //mPhoneList = new ArrayList<>();

        //Cursor cursor =  getActivity().getContentResolver().query(
        //        ProductsContract.UserEntry.CONTENT_URI,
        //        USER_COLUMNS,USER_CONDITION ,USER_CONDITION_PARAM,USER_SORT_ORDER );
        //// Create a new TodoListAdapter for this ListActivity's ListView

        mAdapter = new ContactCursorAdapter(getActivity(),null,0);

        //if (mFromSettings) //with this you can stop always update but now there is a setting with witch you can dismiss he dialog
        // UpdateContactsStatus();


        DeviceService.startActionUpdateContactsStatus(getActivity(), mUserData.Id, false);

        // TODO - Attach the adapter to this ListActivity's ListView //eugen
        mListViewContacts.setAdapter(mAdapter);
        mListViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long userId = (long) view.getTag();
                //mButtonOk.setChecked(true);
            }
        });

        mListViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                if (cursor != null ) {
                    String userId = cursor.getString(COLUMN_USER_ID2);
                    if (userId != null && userId.length() > 0) {
                        DataUser user = new DataUser();
                        user.Id = userId;
                        Intent ourIntent = new Intent(getActivity(), MainTabbedActivity.class).putExtra(Intent.EXTRA_TEXT, user.getAsJSON());
                        getActivity().startActivity(ourIntent);
                    }
                }
            }
        });


        mButtonSelectAll = (ToggleButton)rootView.findViewById(R.id.selectAllButton);
        mButtonSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for(ContactItem c:mPhoneList ) {
                //    c.setIsSelected(mButtonSelectAll.isChecked() == false);
                //    //c.setIsSelected4Invite(mButtonSelectAll.isChecked() == false);
                //}
                //mAdapter.notifyDataSetChanged();
                mPhoneEngine.updateAllUsersFriends3(!mButtonSelectAll.isChecked());
                mAdapter.notifyDataSetChanged();//todo this should be done automaticy ??
            }
        });
        mButtonOk = (ToggleButton)rootView.findViewById(R.id.submitButton);

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the user made changes in the friends list , this should be updated
                //todo : in the future there should be a list of friends per product
                DeviceService.startActionUpdateFriends(getActivity(), mUserData.Id, true);
                //
                mButtonOk.setChecked(false);
                //writeContactsToSharedPreferences(ContactItem.getJsonFromList(mPhoneList),YMQConst.TEXT_SHARED_PREF_CONTACTS_SHARED_KEY);

                SendSmses();
                mButtonOk.setVisibility(View.GONE);

                //if this is from menu
                getActivity().finish();
            }
        });

        mRememeberFriends = (CheckBox)rootView.findViewById(R.id.rememberFriends);

        mRememeberFriends.setChecked(getCheckedValue());



        mRememeberFriends.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCheckedValue(isChecked);
            }
        });
        return rootView;
    }

    private boolean getCheckedValue() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String key = getActivity().getResources().getString(R.string.pref_pref_show_contacts_on_add_product_key);

        boolean def = true;

        boolean val = prefs.getBoolean(key, def);
        return val;
    }

    private boolean setCheckedValue(boolean isChecked) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        String key = getActivity().getResources().getString(R.string.pref_pref_show_contacts_on_add_product_key);

        //Set<String> sharedString = prefs.getStringSet(getString(R.string.pref_pref_show_contacts_on_add_product_key),new HashSet<String>());;
        editor.putBoolean(getString(R.string.pref_pref_show_contacts_on_add_product_key), isChecked);
        editor.commit();
        editor.apply();

        boolean val = prefs.getBoolean(key, true);
        return val;
    }

    //private void UpdateFriendsStatus() {
    //    //mPhoneList = mPhoneEngine.readContactFromProvider();
//
    //    UpdateContactsStatus();
//
    //    //mAdapter = new ContactsAdapter(getActivity(),mPhoneList);
    //}

    private void UpdateContactsStatus() {


    }

    //public void ReadFromDevice2(){
    //    Cursor phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
    //    mPhoneList = new ArrayList<>();
    //    while (phones.moveToNext())
    //    {
    //        ContactItem c = new ContactItem();
    //        c.setName (phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
    //        c.setTelephone(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
    //        mPhoneList.add(c);
    //    }
    //    //mAdapter = new ContactsAdapter(getActivity(),mPhoneList);
    //    phones.close();
    //    writeContactsToSharedPreferences(ContactItem.getJsonFromList(mPhoneList),YMQConst.TEXT_SHARED_PREF_CONTACTS_KEY);
    //}

    //private void writeContactsToSharedPreferences( String data,String key ) {
    //     SharedPreferences preferences = getActivity().getSharedPreferences(YMQConst.SHARED_PREF_KEY, getActivity().MODE_PRIVATE);
    //    SharedPreferences.Editor editor = preferences.edit();
    //    editor.putString(key, data);
    //    editor.apply();
    //}

    //private List<ContactItem> readContactsFromSharedPreferences2() {
    //    SharedPreferences preferences = getActivity().getSharedPreferences(YMQConst.SHARED_PREF_KEY, getActivity().MODE_PRIVATE);
    //    String text = preferences.getString(YMQConst.TEXT_SHARED_PREF_CONTACTS_SHARED_KEY, "");
    //    if (text==null || text.equals(""))
    //        return null;
    //    else
    //        return ContactItem.getContactItemsFromJson(text);
    //}

    private void SendSmses()
    {
        List<DataFriendContact> phoneList = mPhoneEngine.readContactFromProvider(true);
        ArrayList<String> numbers = new ArrayList<>();
        String numbersStr = "";
        for(DataFriendContact c: phoneList)
        {
            if (c.getIsSelected() && (c.getOriginalUserId()==null || c.getOriginalUserId().length()==0) )
            {
                numbers.add(c.getName());
                    numbersStr+=c.PhoneNumberOriginal+";";
            }
        }
        if (numbersStr.length()>0)
            sendSMS(numbersStr,mInvitationMessage);
    }

    private void sendSMS(String phoneNumbers, String smsText) {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            // THIS PHONE HAS SMS FUNCTIONALITY
            Toast.makeText(getActivity(), "No Sms feature found on this device !!", Toast.LENGTH_LONG).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) //At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getActivity()); //Need to change the build to API 19

            //Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phoneNumbers.toString()));
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.setData(Uri.parse("smsto:" + Uri.encode(phoneNumbers)));

            //sendIntent.setType("text/plain");
            //sendIntent.putExtra(Intent.EXTRA_TEXT, smsText);
            sendIntent.putExtra("sms_body", smsText);

            if (defaultSmsPackageName != null)//Can be null in case that there is no default, then the User would be able to choose any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            getActivity().startActivity(sendIntent);

        }
        else //For early versions, do what worked for you before.
        {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            sendIntent.putExtra("sms_body", smsText);
            sendIntent.putExtra("address",  phoneNumbers);
            getActivity().startActivity(sendIntent);
        }
    }
}
