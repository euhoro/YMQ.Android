package com.ymarq.eu.tabs;

/**
 * Created by eu on 2/1/2015.
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ymarq.eu.business.PhoneEngine;
import com.ymarq.eu.common.view.SlidingTabLayout;
import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.news.NewsFragment;
import com.ymarq.eu.products.ProductsBuyerFragment2;
import com.ymarq.eu.products.ProductsSellerFragment;
import com.ymarq.eu.subscriptions.FragmentSubscriptions;
import com.ymarq.eu.ymarq.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A basic sample which shows how to use {@link com.example.android.common.view.SlidingTabLayout}
 * to display a custom {@link android.support.v4.view.ViewPager} title strip which gives continuous feedback to the User
 * when scrolling.
 */
public class SlidingTabsColorsFragment extends Fragment {

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * This class represents a tab to be displayed by {@link android.support.v4.view.ViewPager} and it's associated
     * {@link SlidingTabLayout}.
     */
    static class SamplePagerItem {
        private final CharSequence mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;

        SamplePagerItem(CharSequence title, int indicatorColor, int dividerColor) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
        }

        ///**
        // * @return A new {@link Fragment} to be displayed by a {@link android.support.v4.view.ViewPager}
        // */
        Fragment createFragment() {
            //return ContentFragment.newInstance(mTitle, mIndicatorColor, mDividerColor);
            return new FragmentSubscriptions();
        }

        /**
         * @return the title which represents this tab. In this sample this is used directly by
         * {@link android.support.v4.view.PagerAdapter#getPageTitle(int)}
         */
        CharSequence getTitle() {
            return mTitle;
        }

        /**
         * @return the color to be used for indicator on the {@link SlidingTabLayout}
         */
        int getIndicatorColor() {
            return mIndicatorColor;
        }

        /**
         * @return the color to be used for right divider on the {@link SlidingTabLayout}
         */
        int getDividerColor() {
            return mDividerColor;
        }
    }

    static final String LOG_TAG = "SlidingTabsColorsFragment";

    /**
     * A custom {@link android.support.v4.view.ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the User when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link android.support.v4.view.ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * List of {@link SamplePagerItem} which represent this sample's tabs.
     */
    private List<SamplePagerItem> mTabs = new ArrayList<SamplePagerItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BEGIN_INCLUDE (populate_tabs)
        /**
         * Populate our tab list with tabs. Each item contains a title, indicator color and divider
         * color, which are used by {@link SlidingTabLayout}.
         */
        mTabs.add(new SamplePagerItem(
                getString(R.string.title_section_buy), // Title
                getResources().getColor(R.color.roundButtonColor), // Indicator color
                Color.GRAY // Divider color
        ));

        mTabs.add(new SamplePagerItem(
                getString(R.string.title_section_sell), // Title
                getResources().getColor(R.color.roundButtonColorGreenish), // Indicator color
                Color.GRAY // Divider color
        ));

        //mTabs.add(new SamplePagerItem(
        //        getString(R.string.action_add_subscription), // Title
        //        Color.YELLOW, // Indicator color
        //        Color.GRAY // Divider color
        //));
//
        //mTabs.add(new SamplePagerItem(
        //        getString(R.string.title_section_friends), // Title
        //        Color.YELLOW, // Indicator color
        //        Color.GRAY // Divider color
        //));

        // END_INCLUDE (populate_tabs)
    }

    /**
     * Inflates the {@link android.view.View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of
     * {@link SampleFragmentPagerAdapter}. The {@link SlidingTabLayout} is then given the
     * {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SampleFragmentPagerAdapter(getChildFragmentManager()));
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        //mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        // BEGIN_INCLUDE (tab_colorizer)
        // Set a TabColorizer to customize the indicator and divider colors. Here we just retrieve
        // the tab at the position, and return it's set color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }

        });
        // END_INCLUDE (tab_colorizer)
        // END_INCLUDE (setup_slidingtablayout)

        Intent intent = getActivity().getIntent();
        if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {

            int tabToOpen = intent.getIntExtra("FirstTab", -1);
            if (tabToOpen!=-1) {
                // Open the right tab
                mViewPager.setCurrentItem(tabToOpen);
            }
        }
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.app.FragmentPagerAdapter} used to display pages in this sample. The individual pages
     * are instances of {@link ContentFragment} which just display three lines of text. Each page is
     * created by the relevant {@link SamplePagerItem} for the requested position.
     * <p>
     * The important section of this class is the {@link #getPageTitle(int)} method which controls
     * what is displayed in the {@link SlidingTabLayout}.
     */
    class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

        SampleFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the {@link android.support.v4.app.Fragment} to be displayed at {@code position}.
         * <p>
         * Here we return the value returned from {@link SamplePagerItem#createFragment()}.
         */
        @Override
        public Fragment getItem(int i) {
            boolean isMe = true;
            Intent intent = getActivity().getIntent();
            if (intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                String userJson = intent.getStringExtra(Intent.EXTRA_TEXT);
                DataUser user = DataUser.getFromJson(userJson);

                String userSerialized = PhoneEngine.getInstance().getUserDataById2("", true);
                DataUser userMe = DataUser.getFromJson(userSerialized);
                if (false == userMe.Id.equals(user.Id))
                    isMe = false;
            }
            switch(i)
            {
                case 0:
                    if (isMe) {
                        return new ProductsBuyerFragment2();
                    }
                    else
                    {
                        return  new FragmentSubscriptions();
                    }
                case 1:
                    return new ProductsSellerFragment ();
                //case 2:
                //    return mTabs.get(i).createFragment();
                default:
                    return null;
            }

            //if (i == 0)
            //{
            //    //return FragmentSellerNew.newInstance("sell",  Color.RED, // Indicator color
            //    //       Color.GRAY);// // Divider color)
            //    return new ProductsSellerFragment();// new ImageGridFragment();//
            //}
            ////else if (i == 1){
            //else{
            //    //return new FragmentBuyerNew();
            //    return mTabs.get(i).createFragment();
            //}
            ////else if (i == 2) {
            //    return new NewsFragment();
            //}
//
            //else {
//
            //    return new FragmentContactsNew();
            //    ////return new FragmentBuyerNew();
            //    ////return mTabs.get(i).createFragment();
            //    //return ContentFragment.newInstance("other",  Color.YELLOW, // Indicator color
            //    //        Color.GRAY);// // Divider color)
            //    ////return FragmentSellerNew.newInstance("other",  Color.YELLOW, // Indicator color
            //    ////               Color.GRAY);// // Divider color)
            //    ////return new FragmentSellerNew();
            //}
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we return the value returned from {@link SamplePagerItem#getTitle()}.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }
        // END_INCLUDE (pageradapter_getpagetitle)

    }

}