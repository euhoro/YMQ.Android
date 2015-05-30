package com.ymarq.eu.messagestree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ymarq.eu.common.YmarqCallback;
import com.ymarq.eu.products.ProductsBuyerFragment2;
import com.ymarq.eu.ymarq.R;

/**
 * MessageActivity is a main Activity to show a ListView containing Message items
 *
 * @author Adil Soomro
 *
 */
public class MessageTreeActivity extends FragmentActivity implements YmarqCallback{

    @Override
    public void onYmarqItemSelected(Uri contentUri) {
            Intent intent = new Intent(this, FragmentMessageTree.class)
                    .setData(contentUri);
            startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_tree);
        if (savedInstanceState == null) {
            //getFragmentManager().beginTransaction()
            //        .add(R.id.container, new FragmentContactsNew())
            //        .commit();


            //tablet2
            if (savedInstanceState == null) {
                // Create the detail fragment and add it to the activity
                // using a fragment transaction.

                Bundle arguments = new Bundle();
                arguments.putParcelable(FragmentMessageTree.DETAIL_URI, getIntent().getData());

                FragmentMessageTree fragment = new FragmentMessageTree();
                fragment.setArguments(arguments);


                getSupportFragmentManager().beginTransaction()
                        .add(R.id.messages_detail, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seller_chat, menu);
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