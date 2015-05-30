package com.ymarq.eu.common;

/**
 * Created by eu on 5/16/2015.
 */
//tablet2

import android.net.Uri;

/**
 * A callback interface that all activities containing this fragment must
 * implement. This mechanism allows activities to be notified of item
 * selections.
 */
public interface YmarqCallback {
    /**
     * DetailFragmentCallback for when an item has been selected.
     */
    public void onYmarqItemSelected(Uri dateUri);
}
