package com.ymarq.eu.news;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.ymarq.R;

import java.util.List;

/**
 * Created by eu on 2/12/2015.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

        private final List<NewsModel> mNews;
        private final LayoutInflater mInflater;
        NewsViewHolder.IViewHolderClicks mListener;
        PhoneEngine mPhoneEngine ;

        public void setListener ( NewsViewHolder.IViewHolderClicks listener)
        {
            mListener = listener;
        }
        public NewsAdapter(Context context , List<NewsModel> news) {
            mInflater = LayoutInflater.from(context);
            mNews = news;
            mPhoneEngine = PhoneEngine.getInstance();
            mPhoneEngine.setApplicationContext(context.getApplicationContext());
        }

        @Override
        public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.news_item, parent, false);

            NewsViewHolder v = new NewsViewHolder(view,mListener);
            return v;
        }

        @Override
        public void onBindViewHolder(NewsViewHolder holder, int position) {
            NewsModel newsModel = mNews.get(position);
            holder.mTitleTextView.setText(newsModel.getNewsModel().Product.Description);


            String contact = mPhoneEngine.getContactNameByUserId(newsModel.getNewsModel().Product.UserId);
            holder.mFromTextView.setText("from: @"+ contact);

            switch (newsModel.getNewsModel().getNotificationType())
            {
                case 0: {
                    holder.mCountTextView.setText("Sell");
                    break;
                }
                case 1:{
                    holder.mCountTextView.setText("Buy");
                    break;
                    }
                case 2: {
                    holder.mCountTextView.setText("New");//product
                    break;
                }
            }

            String count = Integer.toString(newsModel.getNewsModel().Messages.size());
            holder.mTextMessagesCount.setText(count);
        }

        @Override
        public int getItemCount() {
            return mNews.size();
        }

        public NewsModel getItem (int position){
            return mNews.get(position);
        }

        public void clear() {
            mNews.clear();
        }

        public void add(NewsModel newsModel) {
            mNews.add(newsModel);
        }

        public void remove(NewsModel newsModel) {
            mNews.remove(newsModel);
        }

        public static class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView mTitleTextView ;
            TextView mCountTextView ;
            ImageView mImageView ;
            TextView mTextMessagesCount;
            TextView mFromTextView ;
            public NewsViewHolder.IViewHolderClicks mListener;

            NewsViewHolder(View itemView , NewsViewHolder.IViewHolderClicks listener) {
                super(itemView);
                mListener = listener;
                mTitleTextView = (TextView)itemView.findViewById(R.id.news_title);
                mCountTextView = (TextView)itemView.findViewById(R.id.news_product_count);
                mImageView = (ImageView)itemView.findViewById(R.id.news_icon);
                mFromTextView = (TextView)itemView.findViewById(R.id.news_product_from);
                mTextMessagesCount =(TextView)itemView.findViewById(R.id.news_messages_count);

                mImageView.setOnClickListener(this);
                // Is this needed or handled automatically by RecyclerView.ViewHolder?
                itemView.setOnClickListener(this);
                mTextMessagesCount.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v instanceof ImageView){
                    mListener.onNews((ImageView) v, getPosition());
                } else if (v instanceof Button){
                    mListener.onNews((Button) v, getPosition());
                }else {
                    mListener.onNews(v, getPosition());
                }
            }

            public static interface IViewHolderClicks {
                public void onNews(View caller,int position);
                public void onNews(ImageView callerImage,int position);
                public void onNews(Button callerImage,int position);
            }
        }

    }
