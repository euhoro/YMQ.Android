package com.ymarq.eu.subscriptions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.common.IOnSubscriptionsReceived;
import com.ymarq.eu.entities.DataSubscription;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.services.DeviceService;
import com.ymarq.eu.tabs.ContentFragment;
import com.ymarq.eu.products.BuyerProductsActivity;

import com.ymarq.eu.ymarq.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eu on 2/11/2015.
 */
public class FragmentSubscriptions extends ContentFragment implements IOnSubscriptionsReceived , SubscriptionsAdapter.SubscriptionViewHolder.IMyViewHolderClicks {
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    private static final String LOG_TAG = FragmentSubscriptions.class.getSimpleName();
    private static final int MENU_DELETE = 88;

    Button mButton;
    EditText mEdit;
    RecyclerView mListView;
    private DataUser mCurrentUser;
    SubscriptionsAdapter subscriptionArrayAdapter;
    private CloudEngine mCloudEngine = CloudEngine.getInstance();
    AlertDialog mAlertDialog = null;

    public FragmentSubscriptions() {
        mCloudEngine.setSubscriptionsReceivedListener(this);
        //todo implement colors
    }

    public void fireOnSubscriptionsReceived(List<DataSubscription> subscriptions){
        subscriptionArrayAdapter.clear();
        for(DataSubscription subscription: subscriptions)
            subscriptionArrayAdapter.add(new SubscriptionModel(subscription));
        //subscriptionArrayAdapter.addAll(subscriptions);
        subscriptionArrayAdapter.notifyDataSetChanged();
    }

    public void fireOnOneSubscriptionReceived(DataSubscription subscription){
        subscriptionArrayAdapter.add(new SubscriptionModel(subscription));
        subscriptionArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_buyer2, container, false);


        mButton = (Button)rootView.findViewById(R.id.BtnAddSubscription);
        mEdit   = (EditText)rootView.findViewById(R.id.txtAddSubscriptionInput);
        mListView = (RecyclerView)rootView.findViewById(R.id.listview_subscription_list);

        registerForContextMenu(mListView);

        mEdit.clearFocus();


        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        String subscription = mEdit.getText().toString();
                        ShowDialogAddSubscription();
                        mEdit.setText("");
                        //mEdit.clearFocus();
                    }
                });


        List<SubscriptionModel> subscriptions = new ArrayList<SubscriptionModel>();

        subscriptionArrayAdapter = new SubscriptionsAdapter(getActivity(),subscriptions);
        subscriptionArrayAdapter.setListener(this);

        //ListView lv1 = (ListView) rootView.findViewById(R.id.listview_subscription_list);


        //mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
        //    @Override
        //    public boolean onItemLongClick(AdapterView<?> parent, View view,
        //                                   int position, long id) {
        //        //MyList.this
        //        Toast.makeText(getActivity(),
        //                "Item in position " + position + " clicked",
        //                Toast.LENGTH_LONG).show();
        //        // Return true to consume the click event. In this case the
        //        // onListItemClick listener is not called anymore.
        //        return true;
        //    }
        //});
//

        mListView.setAdapter(subscriptionArrayAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT))
        {
            String userData = intent.getStringExtra(Intent.EXTRA_TEXT);
            mCurrentUser = DataUser.getFromJson(userData);
            mCloudEngine.GetSubscriptions(mCurrentUser.Id,true);
            DeviceService.startActionSubscriptionToDeviceAsync(getActivity(),mCurrentUser.Id);
        }

        //
        //mListView.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
//
        //        DataSubscription selectedSubscription = null;
//
        //        NavigateToSearchedProducts(selectedSubscription);
        //    }
        //    });
        return rootView;
    }

    private void NavigateToSearchedProducts(DataSubscription selectedSubscription) {
        Intent ourIntent = new Intent(getActivity(), BuyerProductsActivity.class)
                .putExtra(Intent.EXTRA_TEXT, selectedSubscription.getAsJSON() );
        ourIntent.putExtra(Intent.EXTRA_TITLE, mCurrentUser.getAsJSON() );
        startActivity(ourIntent);
    }

    private void scrollMyListViewToBottom() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                //mListView.setSelection(subscriptionArrayAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listview_subscription_list) {
            RecyclerView lv = (RecyclerView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SubscriptionModel obj = subscriptionArrayAdapter.getItem(acmi.position);

            menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_DELETE:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                SubscriptionModel obj = subscriptionArrayAdapter.getItem(info.position);
                //(DataSubscription) mListView.getItemAtPosition(info.position);

                //Log.d(Tag, "removing item pos=" + info.position);
                subscriptionArrayAdapter.remove(obj);
                mCloudEngine.DeleteSubscription(obj.getmDataSubscription(),true);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    String ShowDialogAddSubscription()
    {
        String res = null;
        Activity activity = this.getActivity();
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

        alert.setTitle(getActivity().getResources().getString(R.string.label_add_new_subscription_description));
        alert.setMessage(getActivity().getResources().getString(R.string.label_subscription_description));

// Set an EditText view to get User input
        final EditText input = new EditText(activity);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // res = input.getText().toString();
                // Do something with value!
                DataSubscription ds = new DataSubscription(input.getText().toString(), mCurrentUser.Id);

                mCloudEngine.Subscribe(ds, true);
                //subscriptionArrayAdapter.add(new SubscriptionModel(ds));

                //todo:remove this
                //Thread.sleep(1000);
                //mCloudEngine.GetSubscriptionsAsync(mCurrentUser.Id);
                NavigateToSearchedProducts(ds);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        mAlertDialog = alert.show();
        return res;
    }

    @Override
    public void onSubscription(View caller,int position) {
        Log.e("Poh-tah-tos", "");
        DataSubscription selectedSubscription = subscriptionArrayAdapter.getItem(position).getmDataSubscription();

        NavigateToSearchedProducts(selectedSubscription);
    }


    //@Override
    //public void onLongSubscription(View caller,int position) {
    //    Log.e("Poh-tah-tos", "");
    //    //DataSubscription selectedSubscription = subscriptionArrayAdapter.getItem(position).getmDataSubscription();
//
    //    //NavigateToSearchedProducts(selectedSubscription);
    //}

    @Override
    public void onSubscription(ImageView callerImage,int position) {
        Log.e("Poh-tah-tos","");
    }

    @Override
    public void onSubscription(Button callerButton,int position) {
        Log.e("Poh-tah-tos","");
        SubscriptionModel obj = subscriptionArrayAdapter.getItem(position);
        //(DataSubscription) mListView.getItemAtPosition(info.position);
        mCloudEngine.DeleteSubscription(obj.getmDataSubscription(),true);
        //Log.d(Tag, "removing item pos=" + info.position);
        subscriptionArrayAdapter.remove(obj);
        subscriptionArrayAdapter.notifyItemRemoved(position);

    }

    //@Override
    public void onSubscriptionMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.listview_subscription_list) {
            RecyclerView lv = (RecyclerView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            SubscriptionModel obj = subscriptionArrayAdapter.getItem(acmi.position);

            menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        }
    }
}
