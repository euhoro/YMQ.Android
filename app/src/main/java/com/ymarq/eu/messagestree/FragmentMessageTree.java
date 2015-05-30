package com.ymarq.eu.messagestree;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.ymarq.eu.activities.ImageViewActivity;
import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.IOnImageReceived;
import com.ymarq.eu.common.IOnMessagesReceived;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataApiResult;
import com.ymarq.eu.entities.DataMessage;
import com.ymarq.eu.entities.DataProduct;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.ymarq.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by eu on 3/31/2015.
 */
public class FragmentMessageTree extends Fragment implements IOnMessagesReceived, IOnImageReceived, LoaderManager.LoaderCallbacks<Cursor> {

    private BroadcastReceiver receiverMessages;
    public FragmentMessageTree() {
        setHasOptionsMenu(true);
    }

    //tablet2
    public static final String DETAIL_URI = "URI";
    Uri mUri;

    public void onProductsChanged(String newLocation) {

        //tablet2
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            String productId = ProductsContract.ProductEntry.getProductId2FromUri(uri);

            DataProduct dp = mPhoneEngine.getProductsDataByProductId(productId);

            Uri updatedUri = ProductsContract.ProductEntry.buildProductsUserWithProductId2(dp.UserId, productId);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    private static final int DETAIL_LOADER = 0;

    private static final String[] PRODUCT_COLUMNS = {
            ProductsContract.UserEntry.TABLE_NAME + "." + ProductsContract.UserEntry.COLUMN_USER_NICKNAME,
            ProductsContract.UserEntry.TABLE_NAME + "." + ProductsContract.UserEntry.COLUMN_USER_ID2,
            ProductsContract.ProductEntry.COLUMN_PRODUCT_ID2,
            ProductsContract.ProductEntry.COLUMN_DESCRIPTION,
            ProductsContract.ProductEntry.COLUMN_IMAGE_LINK,

            ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_FRIENDS,
            ProductsContract.ProductEntry.COLUMN_PRODUCT_NOTIFY_OTHERS,
            ProductsContract.ProductEntry.COLUMN_PRODUCT_GIVEAWAY
    };

    private static final int COLUMN_USER_NICKNAME = 0;
    private static final int COLUMN_USER_ID2 = 1;
    private static final int COLUMN_PRODUCT_ID2 = 2;
    private static final int COLUMN_DESCRIPTION = 3;
    private static final int COLUMN_IMAGE_LINK = 4;

    private static final int COLUMN_PRODUCT_NOTIFY_FRIENDS = 5;
    private static final int COLUMN_PRODUCT_NOTIFY_OTHERS = 6;
    private static final int COLUMN_PRODUCT_GIVEAWAY = 7;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        if (mProduct2 != null)
            UpdateActionBar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//tablet2
        CursorLoader cursorLoader = null;
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            cursorLoader = new CursorLoader(
                    getActivity(),
                    mUri,
                    PRODUCT_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return cursorLoader;
    }


    //Log.v(LOG_TAG, "In onCreateLoader");
    //Intent intent = getActivity().getIntent();
    //if (getIntentStatus(intent)) {
    //    return null;
    //}
//
    //// Now create and return a CursorLoader that will take care of
    //// creating a Cursor for the data being displayed.
    //return new CursorLoader(
    //        getActivity(),
    //        intent.getData(),
    //        PRODUCT_COLUMNS,
    //        null,
    //        null,
    //        null
    //);
    // }


    private boolean getIntentStatus(Intent intent) {
        return intent == null || intent.getData() == null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        //Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }

        mProduct2 = new DataProduct();
        mProduct2.Id = data.getString(COLUMN_PRODUCT_ID2);
        mProduct2.Description = data.getString(COLUMN_DESCRIPTION);
        mProduct2.Image = data.getString(COLUMN_IMAGE_LINK);
        mProduct2.UserId = data.getString(COLUMN_USER_ID2);

        mProduct2.NotifyFriends = data.getInt(COLUMN_PRODUCT_NOTIFY_FRIENDS) == 1 ? true : false;
        mProduct2.NotifyOthers = data.getInt(COLUMN_PRODUCT_NOTIFY_OTHERS) == 1 ? true : false;
        mProduct2.GiveAway = data.getInt(COLUMN_PRODUCT_GIVEAWAY) == 1 ? true : false;

        //if (!data.moveToFirst()) { return; }

        //display imageView
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setCustomView(R.layout.action_bar_custom_view);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        if (mImageView == null) {
            mImageView = (ImageView) actionBar.getCustomView().findViewById(R.id.actionBarLogo);
            mTextDescription = (TextView) actionBar.getCustomView().findViewById(R.id.actionBarDescription);
            mTextUser = (TextView) actionBar.getCustomView().findViewById(R.id.actionBarUser);
        }


        isBuyer = !mProduct2.getUserId().equals(mCurrentUser.getId());
        if (!isBuyer) {
            mPrivateSend.setVisibility(View.GONE);
        }

        UpdateActionBar();


        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareProductIntent());
        }

        //StartTimer();
        refreshMessages();
    }

    private void UpdateActionBar() {
        //mImageView.setImageBitmap(mProduct.Id);
        GetProductImage(mProduct2);


        //sender = Utility.sender[rand.nextInt( Utility.sender.length-1)];

        getActivity().setTitle(mProduct2.Description);
        mTextDescription.setText(mProduct2.Description);

        String contactName = getContact();

        //copyDataBase();

        mTextUser.setText("@" + contactName);
        //TextView detailTextView = (TextView)getView().findViewById(R.id.detail_text);
        //detailTextView.setText(mForecast);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private String getContact() {
        String contactName = mPhoneEngine.getContactNameByUserId(mProduct2.UserId);
        if (contactName == null || contactName.equals("")) {
            contactName = mProduct2.UserName;
        }
        return contactName;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Called when the activity is first created.
     */

    private ShareActionProvider mShareActionProvider;
    List<PersonConversationModel> messages2;
    ExpandableListViewAdapter2 adapter;
    ExpandableListView viewConversations;

    EditText text;
    static Random rand = new Random();
    static String sender;
    final int mRefreshInterval = 40000;

    PhoneEngine mPhoneEngine;
    CloudEngine mCloudEngine;
    DataProduct mProduct2;
    DataUser mCurrentUser;
    private final String mConfigTxtFile = "config.txt";
    //Timer mTimer;
    //TimerTask mTimerTask;
    boolean mIsStarted;
    ImageView mImageView;
    TextView mTextDescription;
    TextView mTextUser;
    Button mPrivateSend;
    Button mPublicSend;
    boolean isBuyer = false;
    private static final String LOG_TAG = FragmentMessageTree.class.getSimpleName();
    private static final String SHARE_PRODUCT_LINK = "";


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        //cannot get it to work with 2.3 and switched back to 4.0 and up
        //ShareActionProvider mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(menuItem);
        //mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        mShareActionProvider = (ShareActionProvider) menuItem.getActionProvider();

        //atach intent to share action provider
        if (mProduct2 != null && mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareProductIntent());
        } else {
            Log.d(LOG_TAG, "Share provider is null??");
        }
    }

    private Intent createShareProductIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_PRODUCT_LINK + mProduct2.Image);
        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fireOnMessagessReceived(List<DataMessage> messages) {
        FillMessageData(messages);

        //expandAll children
        int count = adapter.getGroupCount();
        for (int position = 1; position <= count; position++)//count - expand only the first one
            viewConversations.expandGroup(position - 1);
    }

    private void FillMessageData(List<DataMessage> myMessages) {

        Map<String, PersonConversationModel> senders = new HashMap<String, PersonConversationModel>();
        String roomConversationName = UUID.randomUUID().toString();
        for (DataMessage dataMessage : myMessages) {

            //if (!senders.containsKey(dataMessage.SenderId) || senders.get(dataMessage.SenderId).getToUserId() == null ) {
            //public messages
            //todo refactor this line //.getToUserId().equals("00000000-0000-0000-0000-000000000000")publicmessages
            if (dataMessage.getToUserId() == null || dataMessage.getToUserId().equals("")) {
                if (!senders.containsKey(roomConversationName)) {
                    PersonConversationModel conversation = new PersonConversationModel(dataMessage, mCurrentUser.Id);
                    senders.put(roomConversationName, conversation);
                } else {
                    PersonConversationModel val = senders.get(roomConversationName);
                    val.getPersonMessageModelList().add(new MessageModel(dataMessage, mCurrentUser.Id));
                    senders.put(roomConversationName, val);
                }
            } else {
                //PersonConversationModel val = senders.get(dataMessage.SenderName);
                //val.getPersonMessageModelList().add(new MessageModel(dataMessage,mCurrentUser.Id));
                //senders.put(dataMessage.SenderName,val);
                if (isBuyer && !dataMessage.getToUserId().equals(mCurrentUser.Id) && !dataMessage.SenderId.equals(mCurrentUser.Id))
                    continue;//if the messages are not from me or to me - do not display

                String roomConversationNamePrivate = dataMessage.SenderId + "-" + dataMessage.ToUserId;
                String roomConversationNamePrivateReverse = dataMessage.ToUserId + "-" + dataMessage.SenderId;

                if (dataMessage.SenderId.equals(dataMessage.ToUserId))
                    continue;//todo - fix this bug so it will never get to this point.

                if (!senders.containsKey(roomConversationNamePrivate) && !senders.containsKey(roomConversationNamePrivateReverse)) {
                    PersonConversationModel conversation = new PersonConversationModel(dataMessage, mCurrentUser.Id);
                    senders.put(roomConversationNamePrivate, conversation);
                } else {
                    PersonConversationModel val = senders.get(roomConversationNamePrivate);
                    if (val == null) {
                        val = senders.get(roomConversationNamePrivateReverse);
                        val.getPersonMessageModelList().add(new MessageModel(dataMessage, mCurrentUser.Id));
                        senders.put(roomConversationNamePrivateReverse, val);
                    } else {
                        val.getPersonMessageModelList().add(new MessageModel(dataMessage, mCurrentUser.Id));
                        senders.put(roomConversationNamePrivate, val);
                    }
                }
            }
        }

        messages2.clear();
        for (PersonConversationModel model : senders.values())
            messages2.add(model);

        adapter = new ExpandableListViewAdapter2(getActivity(), messages2);//,mCurrentUser.Id,isBuyer);
        viewConversations.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (receiverMessages != null)
                getActivity().unregisterReceiver(receiverMessages);
        }
        catch(Exception ex) {
            //do nothing
            String a = ex.toString();
        }
        //if (mTimer != null) {
        //    mTimer.cancel();
        //    mIsStarted = false;
        //}
    }

    //@Override
    //public void onResume() {
    //crash
    //    super.onResume();
    //    StartTimer();
    //    //mTimer.cancel();
    //}

    @Override
    public void fireOnImageReceived(Bitmap bitmap) {
        String imageLocation = GetProductFileName2();

        saveImage(bitmap, imageLocation);

        LoadFileInImageView(imageLocation);
    }

    private void LoadFileInImageView(final String imageLocation) {
        Bitmap b2 = loadImage(imageLocation);

        mImageView.setImageBitmap(b2);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowBigImage(v, imageLocation);

            }
        });
    }

    private void ShowBigImage(View v, String imageLocation) {
        Intent intent = new Intent(v.getContext(),
                ImageViewActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, imageLocation);
        startActivity(intent);
    }

    String GetProductFileName2() {
        return this.getPhotoDirectory().getAbsolutePath() + "/JPEG_" + mProduct2.Id + ".jpg";
    }

    File getPhotoDirectory() {
        File outputDir = null;
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File pictureDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            outputDir = new File(pictureDir, "Ymarq");
            if (!outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    Toast.makeText(getActivity(), "Failed to create directory: " + outputDir.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    outputDir = null;
                }
            }
        }

        return outputDir;
    }

    private void saveImage(Bitmap bmp, String filename) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //public void SendMesage2(String message) {
    //    DataMessage dm = new DataMessage(message,mCurrentUser.Id,mProduct.Id);
    //    mCloudEngine.SendMessageAsync(dm);
    //    //messagesAdapter.add(dm);
    //    int count = adapter.getGroupCount();
    //    viewConversations.setSelection(count-1);
    //    hideKeyboard();
    //}

    private void hideKeyboard() {
        InputMethodManager inputManager =
                (InputMethodManager) this.
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //if (this.getCurrentFocus() !=null) {
        //    inputManager.hideSoftInputFromWindow(
        //            this.getCurrentFocus().getWindowToken(),
        //            InputMethodManager.HIDE_NOT_ALWAYS);
        //}

        inputManager.hideSoftInputFromWindow(text.getWindowToken(), 0);
    }

    private void refreshMessages() {
        mCloudEngine.GetMessages(UUID.fromString(mProduct2.Id), true);

        //todo : make this work - currently for new added products, this does not
        //DataApiResult<List<DataMessage>> messages =  mPhoneEngine.getMessagesByProductId2(mProduct2.Id.toString());
        //fireOnMessagessReceived(messages.Result);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // your oncreate code

        //registerReceiver();


//tablet2
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(FragmentMessageTree.DETAIL_URI);
        }


        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.chat_room_tree, container, false);

        mCloudEngine = CloudEngine.getInstance();
        mPhoneEngine = PhoneEngine.getInstance();
        mPhoneEngine.setApplicationContext(getActivity().getApplicationContext());
        mCloudEngine.setMessagesReceivedListener(this);
        mCloudEngine.setImageReceivedListener(this);

        String userJson = mPhoneEngine.getUserDataById2("", true);
        mCurrentUser = DataUser.getFromJson(userJson);

        String prodId2 = "";
        if (!getIntentStatus(intent))// && intent.hasExtra(Intent.EXTRA_TEXT))//otherwise it will crash later
        {
            String productUri = intent.getDataString();

            mUri = Uri.parse(productUri);

            //mTimer = new Timer();
            //final int FPS = 40;
            //mTimerTask = new UpdateMessages();

        }
        //else if(mUri!=null)
        //{

        //prodId2 = ProductsContract.ProductEntry.getProductId2FromUri(mUri);


        //String serializedProduct = "";

        //String serializedProduct = intent.getStringExtra(Intent.EXTRA_TEXT);
        //String previousLocation = intent.getStringExtra(Intent.EXTRA_TITLE);

        //mProduct = DataProduct.getFromJson(serializedProduct);
        //String userJson = readUserFromFile();

        //set new timer so it will start querying
        //mTimer = new Timer();
        //final int FPS = 40;
        //mTimerTask = new UpdateMessages();


        //tablet2 is this necessary ?

        //onProductsChanged(prodId2);
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.chat_room_tree);

        //}

        if (mUri != null) {
            prodId2 = ProductsContract.ProductEntry.getProductId2FromUri(mUri);
            mPhoneEngine.updateProductNotification(prodId2, 0);//update notifications to 0 (have been read)
            //mTimer = new Timer();
            //final int FPS = 40;
            //mTimerTask = new UpdateMessages();


            //tablet2 is this necessary ?
            if (getIntentStatus(intent)) {
                onProductsChanged(prodId2);
            }
        }


        //getActionBar().setIcon("Test");
        messages2 = new ArrayList<PersonConversationModel>();

        viewConversations = (ExpandableListView) rootView.findViewById(R.id.expandlable_view);
        text = (EditText) rootView.findViewById(R.id.text);
        mPrivateSend = (Button) rootView.findViewById(R.id.message_button_private);
        mPrivateSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        mPublicSend = (Button) rootView.findViewById(R.id.message_button_public);
        mPublicSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        viewConversations.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        adapter = new ExpandableListViewAdapter2(getActivity(), messages2);
        viewConversations.setAdapter(adapter);
        //addNewMessage(new MessageModel("mmm, well, using 9 patches png to show them.", true));


        //viewConversations.setOnLongClickListener(new View.OnLongClickListener() {
        //    @Override
        //    public boolean onLongClick(View v) {
        //        return false;
        //    }
        //});

        viewConversations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    // You now have everything that you would as if this was an OnChildClickListener()
                    // Add your logic here.

                    // Return true as we are handling the event.
                    MessageModel message = adapter.getChild(groupPosition, childPosition);
                    if (message.SenderId.equals(mCurrentUser.Id) || (isBuyer && !message.SenderId.equals(mProduct2.UserId)))
                        return false;

                    ShowDialogSendMessage(message.SenderId);
                    return true;
                } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);

                    PersonConversationModel message = adapter.getGroup(groupPosition);

                    if (message.SenderId.equals(mCurrentUser.Id) || (isBuyer && !message.SenderId.equals(mProduct2.UserId)))
                        return false;

                    ShowDialogSendMessage(message.SenderId);
                    return false;
                } else
                    return false;
            }
        });

        return rootView;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("Blabla");
        filter.addAction("SOME_OTHER_ACTION");

        receiverMessages = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refreshMessages();
            }
        };
        getActivity().registerReceiver(receiverMessages, filter);
    }


    String ShowDialogSendMessage(final String toUserId) {
        String res = null;
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle(getActivity().getResources().getString(R.string.label_add_new_message));
        alert.setMessage(getActivity().getResources().getString(R.string.label_message));

// Set an EditText view to get User input
        final EditText input = new EditText(getActivity());
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                DataMessage dm = new DataMessage(input.getText().toString(), mCurrentUser.Id, mProduct2.Id);
                if (toUserId == null || toUserId == mCurrentUser.getId() || isBuyer)
                    dm.ToUserId = "";
                else
                    dm.ToUserId = toUserId;

                dm.ToUserId = toUserId;
                SendMessageFinalAndRefresh(dm);

                InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(input.getWindowToken(), 0);

                //mCloudEngine.SubscribeAsync(ds);
                //subscriptionArrayAdapter.add(new SubscriptionModel(ds));

                //todo:remove this
                //Thread.sleep(1000);
                //mCloudEngine.GetSubscriptionsAsync(mCurrentUser.Id);
                //NavigateToSearchedProducts(ds);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
        return res;
    }

    private void GetProductImage(DataProduct dataProduct) {
        String imageLocation = GetProductFileName2();
        File file = new File(imageLocation);
        if (file.exists())
            LoadFileInImageView(imageLocation);
        else
            mCloudEngine.GetImage(mProduct2, true);
    }

    private Bitmap loadImage(String imgPath) {
        BitmapFactory.Options options;
        try {
            options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //private void StartTimer() {
    //    try {
    //        if (mIsStarted == false) {
    //            mTimer.scheduleAtFixedRate(mTimerTask, 0, mRefreshInterval);// 1000/FPS);
    //            mIsStarted = true;
    //        } else {//new
    //            //mTimer.cancel();
    //            //mTimer.scheduleAtFixedRate(mTimerTask, 0, mRefreshInterval);// 1000/FPS);
    //            //mIsStarted = true;
    //        }
    //    } catch (Exception ex) {
    //        //todo refactor this to loader
    //        String s = ex.toString();
    //    }
    //}

    private void copyDataBase() {
        File f = new File("/data/data/com.ymarq.eu.ymarq/databases/ymarq.db");
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(f);
            fos = new FileOutputStream("/mnt/sdcard/db_dump.db");
            while (true) {
                int i = fis.read();
                if (i != -1) {
                    fos.write(i);
                } else {
                    break;
                }
            }
            fos.flush();
            //Toast.makeText(this, "DB dump OK", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(this, "DB dump ERROR", Toast.LENGTH_LONG).show();
        } finally {
            try {
                fos.close();
                fis.close();
            } catch (IOException ioe) {
            }
        }

    }

    public void onEnterTextBox(View v) {
        //getListView().setSelection(messages.size()-1);
    }

    public void sendMessage(View v) {
        String newMessage = text.getText().toString().trim();
        if (newMessage.length() > 0 && mProduct2 != null) {
            text.setText("");

            DataMessage dm = new DataMessage(newMessage, mCurrentUser.Id, mProduct2.Id);


            if (v.getId() == R.id.message_button_private)
                dm.ToUserId = mProduct2.UserId;

            SendMessageFinalAndRefresh(dm);
            hideKeyboard();

            //MessageModel messageModel2 = new MessageModel(dm,mCurrentUser.Id);
            ////todo: remove this s**
            //// Create a calendar object with today date. Calendar is in java.util pakage.
            //Calendar calendar = Calendar.getInstance();
            //// Get current date of calendar which point to the yesterday now
            //Date today = calendar.getTime();
            ////messageModel.CreateDate = today;
            //messageModel2.SenderName = mCurrentUser.Name;
//
            //addNewMessage(messageModel);
            //new SendMessage().execute();
            //addNewMessage(messageModel);
        }
    }

    private void SendMessageFinalAndRefresh(DataMessage message) {
        mCloudEngine.SendMessage(message, true);
        refreshMessages();
    }
    //private class SendMessage extends AsyncTask<Void, String, String>
    //{
    //	@Override
    //	protected String doInBackground(Void... params) {
    //		try {
    //			Thread.sleep(2000); //simulate a network call
    //		}catch (InterruptedException e) {
    //			e.printStackTrace();
    //		}
    //
    //		this.publishProgress(String.format("%s started writing", sender));
    //		try {
    //			Thread.sleep(2000); //simulate a network call
    //		}catch (InterruptedException e) {
    //			e.printStackTrace();
    //		}
    //		this.publishProgress(String.format("%s has entered text", sender));
    //		try {
    //			Thread.sleep(3000);//simulate a network call
    //		}catch (InterruptedException e) {
    //			e.printStackTrace();
    //		}
    //
    //
    //		return Utility.messages[rand.nextInt(Utility.messages.length-1)];
    //
    //
    //	}
    //	@Override
    //	public void onProgressUpdate(String... v) {
    //
    //		if(messages.get(messages.size()-1).isStatusMessage)//check wether we have already added a status message
    //		{
    //			messages.get(messages.size()-1).setContent(v[0]); //update the status for that
    //			adapter.notifyDataSetChanged();
    //			getListView().setSelection(messages.size()-1);
    //		}
    //		else{
    //			addNewMessage(new MessageModel(true,v[0])); //add new message, if there is no existing status message
    //		}
    //	}
    //	@Override
    //	protected void onPostExecute(String text) {
    //		if(messages.get(messages.size()-1).isStatusMessage)//check if there is any status message, now remove it.
    //		{
    //			messages.remove(messages.size()-1);
    //		}
    //
    //		addNewMessage(new MessageModel(text, false)); // add the orignal message from server.
    //	}
    //
//
    //}
    //void addNewMessage(MessageModel m)
    //{
    //    messages.add(m);
    //    adapter.notifyDataSetChanged();
    //    getListView().setSelection(messages.size()-1);
    //}
}
