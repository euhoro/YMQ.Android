package com.ymarq.eu.news;

/**
 * Created by eu on 2/12/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ymarq.eu.business.CloudEngine;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.IOnNotificationsReceived;
import com.ymarq.eu.data.ProductsContract;
import com.ymarq.eu.entities.DataNotifications;
import com.ymarq.eu.entities.DataNotificationsModel;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.messagestree.MessageTreeActivity;
import com.ymarq.eu.tabs.ContentFragment;
import com.ymarq.eu.ymarq.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by eu on 2/11/2015.
 */
public class NewsFragment extends ContentFragment implements NewsAdapter.NewsViewHolder.IViewHolderClicks ,IOnNotificationsReceived {

    private static final String LOG_TAG = NewsFragment.class.getSimpleName();
    private static final int MENU_DELETE = 88;

    Button mButton;
    EditText mEdit;
    RecyclerView mListView;
    private DataUser mCurrentUser;
    NewsAdapter newsArrayAdapter;
    private CloudEngine mCloudEngine = CloudEngine.getInstance();

    public NewsFragment() {
        mCloudEngine.setNotificationsReceived(this);
        //todo implement colors
    }

    public void fireOnNotificationsReceived(List<DataNotifications> subscriptions){
        newsArrayAdapter.clear();
        for(DataNotifications subscription: subscriptions)
            newsArrayAdapter.add(new NewsModel(subscription));
        //subscriptionArrayAdapter.addAll(subscriptions);
        newsArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_news2, container, false);


        mButton = (Button)rootView.findViewById(R.id.BtnAddNews);
        mEdit   = (EditText)rootView.findViewById(R.id.txtAddNewsInput);
        mListView = (RecyclerView)rootView.findViewById(R.id.listview_news_list);

        registerForContextMenu(mListView);

        mEdit.clearFocus();


        //Button.setOnClickListener(
        //       new View.OnClickListener()
        //       {
        //           public void onClick(View view)
        //           {
        //               String subscription = mEdit.getText().toString();
        //               NavigateToChatRoom();
        //               mEdit.setText("");
        //               //mEdit.clearFocus();
        //           }
        //       });


        List<NewsModel> subscriptions = new ArrayList<NewsModel>();

        newsArrayAdapter = new NewsAdapter(getActivity(),subscriptions);
        newsArrayAdapter.setListener(this);

        mListView.setAdapter(newsArrayAdapter);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT))
        {
            String userData = intent.getStringExtra(Intent.EXTRA_TEXT);
            mCurrentUser = DataUser.getFromJson(userData);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR , (24 * 1 *(-1)));//1 day before
            // Get current date of calendar which point to the yesterday now
            Date yesterday = calendar.getTime();

            if (intent.hasExtra(Intent.EXTRA_TEMPLATE))
            {
                String notifications = intent.getStringExtra(Intent.EXTRA_TEMPLATE);
                DataNotificationsModel d = DataNotificationsModel.getFromJson(notifications);
                List<DataNotifications> ns = GetNotificationsFromModel(d);
                fireOnNotificationsReceived(ns);
            }
            else {
                //mCloudEngine.GetNotificationsByUserDate(mCurrentUser.Id, yesterday,true);
                //PhoneEngine.getInstance().getNotifications(mCurrentUser.Id);
            }
        }

        return rootView;
    }

    private List<DataNotifications>  GetNotificationsFromModel(DataNotificationsModel model)
    {
        List<DataNotifications> notifications = new ArrayList<DataNotifications>();
        for (DataNotifications data:model.SellerNotifications) {
            notifications.add(data);
            data.setNotificationType(0);
        }
        for (DataNotifications data:model.BuyerNotifications) {
            notifications.add(data);
            data.setNotificationType(1);
        }
        for (DataNotifications data:model.NewProducts) {
            notifications.add(data);
            data.setNotificationType(2);
        }
        return  notifications;
    }

    private void NavigateToChatRoom(DataNotifications selectedSubscription) {
        //Intent ourIntent = new Intent(getActivity(), MessageTreeActivity.class)
                //.putExtra(Intent.EXTRA_TEXT, selectedSubscription.Product.getAsJSON());
        Intent ourIntent = new Intent(getActivity(), MessageTreeActivity.class);
        ourIntent.setData(ProductsContract.ProductEntry.buildProductsUserWithProductId2(
                selectedSubscription.Product.getUserId(), selectedSubscription.Product.Id));
        ourIntent.putExtra(Intent.EXTRA_TITLE, mCurrentUser.getAsJSON() );
        startActivity(ourIntent);
    }

    @Override
    public void onNews(View caller,int position) {
        Log.e("Poh-tah-tos", "");
        DataNotifications selectedSubscription = newsArrayAdapter.getItem(position).getNewsModel();

        NavigateToChatRoom(selectedSubscription);
    }


    //@Override
    //public void onLongSubscription(View caller,int position) {
    //    Log.e("Poh-tah-tos", "");
    //    //DataSubscription selectedSubscription = subscriptionArrayAdapter.getItem(position).getmDataSubscription();
//
    //    //NavigateToSearchedProducts(selectedSubscription);
    //}

    @Override
    public void onNews(ImageView callerImage,int position) {
        Log.e("Poh-tah-tos","");
    }

    @Override
    public void onNews(Button callerButton,int position) {
        //Log.e("Poh-tah-tos","");
        //NewsModel obj = newsArrayAdapter.getItem(position);
        ////(DataSubscription) mListView.getItemAtPosition(info.position);
//
        ////Log.d(Tag, "removing item pos=" + info.position);
        //newsArrayAdapter.remove(obj);
        //newsArrayAdapter.notifyItemRemoved(position);
        ////mCloudEngine.DeleteSubscriptionAsync(obj.getNewsModel());
    }

    //@Override
    public void onSubscriptionMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //if (v.getId() == R.id.listview_subscription_list) {
        //    RecyclerView lv = (RecyclerView) v;
        //    AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        //    SubscriptionModel obj = newsArrayAdapter.getItem(acmi.position);
//
        //    menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete");
        //}
    }
}

