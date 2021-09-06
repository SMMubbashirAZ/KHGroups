package com.blazeminds.pos.sharedPref;

import android.content.Context;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;
    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }
}
