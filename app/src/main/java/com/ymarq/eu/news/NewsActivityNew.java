package com.ymarq.eu.news;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ymarq.eu.common.YmarqCallback;
import com.ymarq.eu.messagestree.FragmentMessageTree;
import com.ymarq.eu.messagestree.MessageTreeActivity;
import com.ymarq.eu.news.NewsFragment;
import com.ymarq.eu.products.ProductsBuyerFragment2;
import com.ymarq.eu.products.ProductsSellerFragment;
import com.ymarq.eu.ymarq.R;

//tablet2
public class NewsActivityNew extends FragmentActivity implements YmarqCallback {


    //private final String LOG_TAG = NewsActivityNew.class.getSimpleName();
    //private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_news);


        if (findViewById(R.id.messages_detail) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.messages_detail, new FragmentMessageTree(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    private final String LOG_TAG = NewsActivityNew.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    protected void onResume() {
    //notsuere this is necesary on tablet2
        super.onResume();
        String location = ""; //Utility.getPreferredLocation( this );
           // update the location in our second pane using the fragment manager
           if (location != null && !location.equals(mLocation)) {
               ProductsBuyerFragment2 ff = (ProductsBuyerFragment2)getSupportFragmentManager().findFragmentById(R.id.fragment_news);


               //tablet2
         if ( null != ff ) {
            //ff.onProductsChanged();
        }
                    FragmentMessageTree df = (FragmentMessageTree)getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                    if ( null != df ) {
                        df.onProductsChanged(location);
                    }
        mLocation = location;
    }
}


   @Override
   public void onYmarqItemSelected(Uri contentUri) {
       if (mTwoPane) {
           //tablet2

           // In two-pane mode, show the detail view in this activity by
           // adding or replacing the detail fragment using a
           // fragment transaction.
           Bundle args = new Bundle();
           args.putParcelable(FragmentMessageTree.DETAIL_URI, contentUri);

           FragmentMessageTree fragment = new FragmentMessageTree();
           fragment.setArguments(args);

           getSupportFragmentManager().beginTransaction()
                   .replace(R.id.messages_detail, fragment, DETAILFRAGMENT_TAG)
                   .commit();
       } else {
           Intent intent = new Intent(this, MessageTreeActivity.class)
                   .setData(contentUri);
           startActivity(intent);
       }
   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts_activity_new, menu);
        return true;
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
}
