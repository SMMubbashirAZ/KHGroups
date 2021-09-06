package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.HashMap;

public class NavDrawerListAdapter extends BaseAdapter {
    
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;
    
    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }
    
    @Override
    public int getCount() {
        return navDrawerItems.size();
    }
    
    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }
        
        
        // use View, not LinearLayout
        View navigationDrawerItemLayout = convertView.findViewById(R.id.left_drawer);
        View navigationDrawerHeaderLayout = convertView.findViewById(R.id.content_frame);
        
        
        ImageView imgIcon = convertView.findViewById(R.id.icon);
        TextView txtTitle = convertView.findViewById(R.id.title);
        TextView txtCount = convertView.findViewById(R.id.counter);


		/*
        String SettingsInfo = ShowMenuAsSettings();

        Log.d("Nav Menu Tirtle",navMenuTitles[2]);

        Log.d("SettingsInfo",SettingsInfo);
        if (SettingsInfo.contains("inv0")) {

            */
/*int pos = navDrawerItems.indexOf(navMenuTitles[2]);
            navDrawerItems.remove(pos);
*//*

            navDrawerItems.remove("Inventory");
            mDrawerItmes[2]="a";

            //mDrawerList.notifyDataSetChanged();



            Log.d("Remove Inv","Done");


        }

        if (SettingsInfo.contains("cust0")) {

            navDrawerItems.remove((navMenuTitles[3]));

        }
        if (SettingsInfo.contains("po0")) //{

            navDrawerItems.remove((navMenuTitles[4]));

        //}
        if (SettingsInfo.contains("sumry0")) //{

            navDrawerItems.remove((navMenuTitles[5]));

        //}
        if (SettingsInfo.contains("syncpg0")) //{

            navDrawerItems.remove((navMenuTitles[6]));

        //}


        for(String x : navMenuTitles) {
            Log.d("NAV Array Alist", x+"\n");
        }
*/
        
        
        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
        txtTitle.setText(navDrawerItems.get(position).getTitle());
        
        // displaying count
        // check whether it set visible or not
        if (navDrawerItems.get(position).getCounterVisibility()) {
            txtCount.setText(navDrawerItems.get(position).getCount());
        } else {
            // hide the counter view
            txtCount.setVisibility(View.GONE);
        }
        
        return convertView;
    }
    
    
    public String ShowMenuAsSettings() {
        
        String Options = "";
        ArrayList<HashMap<String, String>> DBVal;
        
        PosDB db = PosDB.getInstance(context);
        db.OpenDb();
        DBVal = db.SettingsDataHash();
        db.CloseDb();
        
        
        Log.d("DBSetSize", DBVal.size() + "");
        
        String ASync = "", SyncDur = "", Inv = "", Cust = "", PO = "", Sumry = "", SyncPg = "", EPass = "";
        String TI = "", TO = "", Pass = "";
        
        HashMap<String, String> f;
        
        if (DBVal.size() > 0) {
            
            for (int i = 0; i < DBVal.size(); i++) {
                
                f = DBVal.get(i);
                
                ASync = f.get("async");
                SyncDur = f.get("syncdur");
                Inv = f.get("inv");
                Cust = f.get("cust");
                PO = f.get("po");
                Sumry = f.get("sumry");
                SyncPg = f.get("syncpg");
                EPass = f.get("epass");
                TI = f.get("ti");
                TO = f.get("to");
                Pass = f.get("pass");
                
                
            }
        }
        
        Log.d("ASync", ASync);
        Log.d("TI", TI);
        Log.d("TO", TO);
        Log.d("SyncDur", SyncDur);
        Log.d("Inv", Inv);
        Log.d("Cust", Cust);
        Log.d("Sumry", Sumry);
        Log.d("SyncPg", SyncPg);
        Log.d("Pass", Pass);
        Log.d("EPass", EPass);
        Log.d("PO", PO);
        
        
        return "async" + ASync + "syncdur" + SyncDur + "inv" + Inv + "cust" + Cust + "sumry" + Sumry + "syncpg" + SyncPg + "pass" + Pass + "epass" + EPass + "po" + PO;
    }
    
    
}
