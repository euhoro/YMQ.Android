package com.ymarq.eu.contacts;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.ymarq.eu.entities.DataUser;
import com.ymarq.eu.ymarq.R;


public class ContactsActivityNew extends FragmentActivity {

    public static String EXTRA_SETTINGS = "EXTRA_SETTINGS";
    public static Intent CreateIntent(Context context,DataUser currentUser,boolean bFromSettings)
    {
        Intent intent = new Intent(context, ContactsActivityNew.class);
        intent.putExtra(Intent.EXTRA_TEXT, currentUser.getAsJSON());
        intent.putExtra(EXTRA_SETTINGS, "AddProduct");
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_contacts_activity_new);
       if (savedInstanceState == null) {
           getSupportFragmentManager().beginTransaction()
                   .add(R.id.container, new FragmentContactsNew())
                   .commit();
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
