package com.ymarq.eu.notifications;

/**
 * Created by eu on 1/21/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ymarq.eu.ymarq.R;

import java.util.ArrayList;

public class SimpleListActivity extends Activity {
    public static final String TITLE_EXTRA = "title extra";
    public static final String TEXT_VALUES_EXTRA = "text values extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications_simple_list);

        Intent createIntent = getIntent();
        String title = createIntent.getStringExtra(TITLE_EXTRA);
        ArrayList<String> textValues=
                createIntent.getStringArrayListExtra(TEXT_VALUES_EXTRA);

        if(title != null)
            setTitle(title);
        if(textValues != null)
            ((ListView)findViewById(R.id.listView)).setAdapter(
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                            textValues));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_simple_text, menu);
        return true;
    }

}
