package com.ymarq.eu.notifications;

import com.ymarq.eu.ymarq.R;

public class DreamService  extends android.service.dreams.DreamService {
    public DreamService() {
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        setContentView(R.layout.ymarq_dream);
    }
}
