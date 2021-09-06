package com.blazeminds;

import android.app.Application;
import android.content.Context;

/**
 * Created by Blazeminds on 11/12/2018.
 */

public class AppContextProvider extends Application {

    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;

    /**
     * Returns the application context
     *
     * @return application context
     */
    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

    }

}

