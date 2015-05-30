package com.ymarq.eu.contacts;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.ymarq.R;

/**
 * Created by eu on 2/5/2015.
 */
public class ContactCursorAdapter extends CursorAdapter {
    private static final String TAG = "ContactsAdapter";
    DisplayImageOptions options;
    //Context mContext;
    PhoneEngine mPhoneEngine;

    public ContactCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        //mContext = context;
        mPhoneEngine = PhoneEngine.getInstance();
        mPhoneEngine.setApplicationContext(context.getApplicationContext());
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //final ProductsViewHolder holder = new ProductsViewHolder();
        View view = LayoutInflater.from(mContext).inflate(R.layout.contacts_item, parent, false);

        final CheckBox statusView = (CheckBox) view.findViewById(R.id.statusCheckBox);


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

        return view;
    }

    // Create a View for the ToDoItem at specified position
    // Remember to check whether convertView holds an already allocated View
    // before created a new View.
    // Consider using the ViewHolder pattern to make scrolling more efficient
    // See: http://developer.android.com/training/improving-layouts/smooth-scrolling.html
    //todo : viewholder

    @Override
    public void bindView(View itemLayout, Context context, Cursor cursor) {
        {
            final CheckBox statusView = (CheckBox) itemLayout.findViewById(R.id.statusCheckBox);

            int isFriend = cursor.getInt(FragmentContactsNew.COLUMN_USER_IS_FRIEND);

            statusView.setChecked(isFriend == 1 ? true : false);

            final TextView nameView = (TextView) itemLayout.findViewById(R.id.name_label);
            nameView.setText(cursor.getString(FragmentContactsNew.COLUMN_USER_NICKNAME));

            final TextView dateView = (TextView) itemLayout.findViewById(R.id.telephone_label);
            dateView.setText(cursor.getString(FragmentContactsNew.COLUMN_USER_PHONE_NUMBER));


            //final CheckBox checkInvite =  (CheckBox)itemLayout.findViewById(R.id.switchInvite);
            //checkInvite.setChecked(cursor.getString(FragmentContactsNew.COLUMN_USER_NICKNAME));

            //checkInvite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //    @Override
            //    public void onCheckedChanged(CompoundButton buttonView,
            //                                 boolean isChecked) {
            //        Log.i(TAG, "Entered onInviteCheckedChanged()");
            //        boolean toInvite = checkInvite.isChecked();
            //        contactItem.setIsSelected4Invite(toInvite);
            //    }
            //});

            //final TextView checkInviteText = (TextView) itemLayout.findViewById(R.id.invite_label);
            final TextView checkNotifyText = (TextView) itemLayout.findViewById(R.id.notify_label);
            final ImageView checkHasApp = (ImageView) itemLayout.findViewById(R.id.iconHasApp);

            final String userId = cursor.getString(FragmentContactsNew.COLUMN_USER_ID2);
            // Return the View you just created
            if (userId != null && userId.length() > 0) {
                //checkInvite.setVisibility(View.GONE);
                //checkInviteText.setVisibility(View.GONE);
                checkHasApp.setVisibility(View.VISIBLE);

                statusView.setVisibility(View.VISIBLE);
                checkNotifyText.setVisibility(View.VISIBLE);

                //itemLayout.setOnClickListener(new View.OnClickListener() {
                //    @Override
                //    public void onClick(View v) {
                //        DataUser user = new DataUser();
                //        user.Id = userId;
                //        Intent ourIntent = new Intent(mContext, MainTabbedActivity.class).putExtra(Intent.EXTRA_TEXT, user.getAsJSON());
                //        mContext.startActivity(ourIntent);
                //    }
                //});
            } else {
                //checkInvite.setVisibility(View.VISIBLE);
                //checkInviteText.setVisibility(View.VISIBLE);
                checkHasApp.setVisibility(View.GONE);

                //statusView.setVisibility(View.GONE);
                checkNotifyText.setVisibility(View.GONE);
            }


            final long userIdPhone = cursor.getLong(FragmentContactsNew.COL_USER_ID);
            statusView.setTag(userIdPhone);
            //statusView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            //    @Override
            //    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
            //    }
            //});
            statusView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox)v;

                    //String s = "";
                    long userId = (long)v.getTag();
                    //String s = cursor.getString(FragmentContactsNew.COLUMN_USER_NICKNAME)
                    mPhoneEngine.updateUserFriend(userId,checkBox.isChecked());
                }
            });

           //return itemLayout;
        }
    }
}