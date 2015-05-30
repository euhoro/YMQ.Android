package com.ymarq.eu.subscriptions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymarq.eu.ymarq.R;

import java.util.List;

/**
 * Created by eu on 2/11/2015.
 */
public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsAdapter.SubscriptionViewHolder>{

    private final List<SubscriptionModel> mSubscriptions;
    private final LayoutInflater mInflater;
    SubscriptionViewHolder.IMyViewHolderClicks mListener;

    public void setListener ( SubscriptionViewHolder.IMyViewHolderClicks listener)
    {
        mListener = listener;
    }
    public SubscriptionsAdapter(Context context , List<SubscriptionModel> subscriptions) {
       mInflater = LayoutInflater.from(context);
       mSubscriptions = subscriptions;
    }

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.subscriptions_item2, parent, false);

       SubscriptionViewHolder v = new SubscriptionViewHolder(view,mListener);
        return v;
    }

    @Override
    public void onBindViewHolder(SubscriptionViewHolder holder, int position) {
        SubscriptionModel subscriptionModel = mSubscriptions.get(position);
        holder.mTitleTextView.setText(subscriptionModel.getmDataSubscription().SearchText);
    }

    @Override
         public int getItemCount() {
        return mSubscriptions.size();
    }

    public SubscriptionModel getItem (int position){
        return mSubscriptions.get(position);
    }

    public void clear() {
        mSubscriptions.clear();
    }

    public void add(SubscriptionModel subscription) {
        mSubscriptions.add(subscription);
    }

    public void remove(SubscriptionModel subscription) {
        mSubscriptions.remove(subscription);
    }

    public static class SubscriptionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTitleTextView ;
        TextView mCountTextView ;
        ImageView mImageView ;
        Button mButton;
        public IMyViewHolderClicks mListener;

        SubscriptionViewHolder(View itemView , IMyViewHolderClicks listener) {
            super(itemView);
            mListener = listener;
            mTitleTextView = (TextView)itemView.findViewById(R.id.subscription_title);
            mCountTextView = (TextView)itemView.findViewById(R.id.subscription_product_count);
            mImageView = (ImageView)itemView.findViewById(R.id.subscription_icon);
            mButton =(Button)itemView.findViewById(R.id.subscription_button);

            mImageView.setOnClickListener(this);
            // Is this needed or handled automatically by RecyclerView.ViewHolder?
            itemView.setOnClickListener(this);
            mButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof ImageView){
                mListener.onSubscription((ImageView) v,getPosition());
            } else if (v instanceof Button){
                mListener.onSubscription((Button) v,getPosition());
            }else {
                mListener.onSubscription(v,getPosition());
            }
        }

        public static interface IMyViewHolderClicks {
            public void onSubscription(View caller,int position);
            public void onSubscription(ImageView callerImage,int position);
            public void onSubscription(Button callerImage,int position);
        }
    }

}
