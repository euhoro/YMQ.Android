package com.ymarq.eu.messagestree;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ymarq.eu.ymarq.R;

import java.util.List;

/**
 * Created by eu on 2/19/2015.
 */
public class ExpandableListViewAdapter2 extends BaseExpandableListAdapter {

    private static final class ViewHolder {
        TextView message_text;
        TextView message_sender_name;
        TextView message_date;
        ImageView message_type_icon;
        ImageView message_user_icon;
        View message_arrow;
        //View message_arrow_right;
        LinearLayout message_buble;
    }

    private final List<PersonConversationModel> itemList;
    private final LayoutInflater inflater;

    public ExpandableListViewAdapter2(Context context, List<PersonConversationModel> itemList) //,String currentUser, boolean isBuyer
    {
        this.inflater = LayoutInflater.from(context);
        this.itemList = itemList;
    }

    @Override
    public MessageModel getChild(int groupPosition, int childPosition) {

        return itemList.get(groupPosition).getPersonMessageModelList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemList.get(groupPosition).getPersonMessageModelList().size();
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             final ViewGroup parent) {
        MessageModel message = (MessageModel)this.getChild(groupPosition, childPosition);

        //todo : refactor this - the get view is exactly like the parent
        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_item_youtube, parent, false);


            holder.message_text = (TextView) convertView.findViewById(R.id.message_text);
            holder.message_sender_name = (TextView) convertView.findViewById(R.id.conversation_reference_user);
            holder.message_date = (TextView) convertView.findViewById(R.id.message_date_sender);
            holder.message_type_icon = (ImageView) convertView.findViewById(R.id.conversation_type_thumbnail);

            holder.message_user_icon = (ImageView) convertView.findViewById(R.id.conversation_user_thumbnail);
            holder.message_buble = (LinearLayout) convertView.findViewById(R.id.conversation_event_text_bubble);
            holder.message_arrow = (View) convertView.findViewById(R.id.conversation_arrow);
            //holder.message_arrow_right = (View) convertView.findViewById(R.id.conversation_arrow_right);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.message_type_icon.setVisibility(View.INVISIBLE);
        holder.message_text.setText(message.Content);
        holder.message_date.setText(message.CreateDate);
        holder.message_sender_name.setText(message.SenderName);

           //Check whether message is mine to show green background and align to right
            if(message.isMine())
            {
                holder.message_user_icon.setImageResource(R.drawable.ic_account_box_grey600_36dp);
                holder.message_arrow.setBackgroundResource(R.drawable.arrow_green);
                holder.message_buble.setBackgroundResource(R.drawable.conversation_green);

                //holder.message_arrow.setVisibility(View.VISIBLE);
                //holder.message_arrow_right.setVisibility(View.GONE);
            }

            else
            {
                //holder.message_arrow.setVisibility(View.GONE);
                //holder.message_arrow_right.setVisibility(View.VISIBLE);
//
                holder.message_user_icon.setImageResource(R.drawable.ic_account_circle_grey600_48dp);
            }
        holder.message_user_icon.setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public PersonConversationModel getGroup(int groupPosition) {
        return itemList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return itemList.size();
    }

    @Override
    public long getGroupId(final int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final PersonConversationModel message = getGroup(groupPosition);

        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.chat_item_youtube, parent, false);


            holder.message_text = (TextView) convertView.findViewById(R.id.message_text);
            holder.message_date = (TextView) convertView.findViewById(R.id.message_date_sender);
            holder.message_sender_name = (TextView) convertView.findViewById(R.id.conversation_reference_user);
            holder.message_type_icon = (ImageView) convertView.findViewById(R.id.conversation_type_thumbnail);
            holder.message_user_icon = (ImageView) convertView.findViewById(R.id.conversation_user_thumbnail);
            holder.message_buble = (LinearLayout) convertView.findViewById(R.id.conversation_event_text_bubble);
            holder.message_arrow = (View) convertView.findViewById(R.id.conversation_arrow);
           //holder.message_arrow_right = (View) convertView.findViewById(R.id.conversation_arrow_right);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.message_text.setText(message.Content);
        holder.message_sender_name.setText(message.SenderName);
        if (message.isMine() == false) {
            //String data = DateFormat.format("MMM-dd hh:mm", message.CreateDate).toString();
            holder.message_date.setText(message.CreateDate);

            holder.message_user_icon.setImageResource(R.drawable.ic_account_box_grey600_36dp);

           //holder.message_arrow.setVisibility(View.VISIBLE);
           //holder.message_arrow_right.setVisibility(View.GONE);
        }
        else
        {
            holder.message_arrow.setBackgroundResource(R.drawable.arrow_green);

            //holder.message_arrow.setVisibility(View.GONE);
            //holder.message_arrow_right.setVisibility(View.VISIBLE);

            holder.message_buble.setBackgroundResource(R.drawable.conversation_green);
            //this is green - should be easy to distinguish
            //if this is a header it should be easy to know it is private message for someone else
            holder.message_date.setText(message.CreateDate);
            holder.message_user_icon.setImageResource(R.drawable.ic_account_circle_grey600_48dp);
        }

        holder.message_sender_name.setText(message.SenderName);
        //header// if it is private
        if (message.isPrivate()) {
            holder.message_type_icon.setImageResource(R.drawable.ic_chat_private2);

            holder.message_date.setText(message.CreateDate );
        } else {
            holder.message_type_icon.setImageResource(R.drawable.ic_chat_public2);
        }

        if(message.isStatusMessage())
        {
            holder.message_text.setBackgroundDrawable(null);
        }
        holder.message_user_icon.setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}