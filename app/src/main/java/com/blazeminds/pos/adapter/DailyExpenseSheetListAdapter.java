package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Blazeminds on 2/22/2018.
 */

public class DailyExpenseSheetListAdapter extends BaseAdapter {
    
    public JSONArray list;
    Activity activity;
    PosDB db;

    public DailyExpenseSheetListAdapter(Context activity, JSONArray list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        db = PosDB.getInstance(activity);
        db.OpenDb();
    }
    
    @Override
    public int getCount() {
        return list.length();
    }
    
    @Override
    public JSONObject getItem(int i) {

        try {
            return list.getJSONObject(i);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
    
    @Override
    public long getItemId(int i) {
        return 0;
    }
    void itemClik()
    {

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        final DailyExpenseSheetListAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_daily_expense_list_adap, null);
            holder = new DailyExpenseSheetListAdapter.ViewHolder();
            holder.txtFirst = convertView.findViewById(R.id.first);

            holder.verifyStock_LV= convertView.findViewById(R.id.verifyStock_Linearlayout);
            holder.PatientOrderListView= convertView.findViewById(R.id.PatientOrderListView);


            
            convertView.setTag(holder);
        } else {
            holder = (DailyExpenseSheetListAdapter.ViewHolder) convertView.getTag();
        }

        holder.verifyStock_LV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(holder.PatientOrderListView.getVisibility()==View.GONE)
{

    holder.PatientOrderListView.setVisibility(View.VISIBLE);
    ResizeList(      holder. PatientOrderListView);
}
else
    {
        holder.PatientOrderListView.setVisibility(View.GONE);
    }
            }});

        
        try {
            JSONObject map = list.getJSONObject(position);
            holder.txtFirst.setText(map.getString("date").toString());
if(map.has("Approved") && map.getString("Approved").equals("1") ) {

    holder.txtFirst.setBackgroundResource(R.color.green_back);
}

ListViewAdapterDailyExpenseDetails     listAdapt= new ListViewAdapterDailyExpenseDetails(activity,new JSONArray());
            try {
                listAdapt= new ListViewAdapterDailyExpenseDetails(activity,map.getJSONArray("details"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listAdapt.notifyDataSetChanged();

            holder.     PatientOrderListView.setAdapter(listAdapt);
            ResizeList(      holder. PatientOrderListView);
//            holder.PatientOrderListView.setVisibility(View.GONE);
holder.PatientOrderListView.setEnabled(false);
        } catch (JSONException e) {
            System.out.println("Error: " + e.toString());
        }
        return convertView;
        
    }
    
    private class ViewHolder {
        TextView txtFirst;
ListView PatientOrderListView;
        LinearLayout verifyStock_LV;
        
    }


    void ResizeList(ListView lv)
    {
        ListAdapter listadp = lv.getAdapter();
        if (listadp != null) {

            int totalHeight = 0;
            for (int i = 0; i < listadp.getCount(); i++) {
                View listItem = listadp.getView(i, null, lv);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = lv.getLayoutParams();
            params.height = (totalHeight + (lv.getDividerHeight() * (listadp.getCount() )));
            lv.setLayoutParams(params);
            lv.requestLayout();}



    }
}
