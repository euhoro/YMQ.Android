package com.ymarq.eu;

/**
 * Created by eu on 3/3/2015.
 */

import android.app.Application;

import com.ymarq.eu.utilities.UrlHelper;

//@ReportsCrashes(formKey = "", formUri = "http://www.yourselectedbackend.com/reportpath")
public class YmarqApplication extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
        super.onCreate();
        //ACRA.init(this);

        String userUnique = UrlHelper.GetPhoneId4(getApplicationContext());

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                    "/sdcard/Pictures/Ymarq", "http://ymarq.azurewebsites.net/home/uploadError",userUnique ));
        }
    }
}