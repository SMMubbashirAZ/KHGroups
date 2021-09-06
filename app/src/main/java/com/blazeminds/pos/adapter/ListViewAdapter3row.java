package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blazeminds.pos.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.REPORT_FIRST_COLUMN;

import static com.blazeminds.pos.Constant.REPORT_SECOND_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_THIRD_COLUMN;

/**
 * Created by Blazeminds on 2/22/2018.
 */

public class ListViewAdapter3row extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;

    public ListViewAdapter3row(Context activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        return list.size();
    }
    
    @Override
    public HashMap<String, String> getItem(int i) {
        return list.get(i);
    }
    
    @Override
    public long getItemId(int i) {
        return 0;
    }
    void itemClik()
    {

    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        ListViewAdapter3row.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_3_row, null);
            holder = new ListViewAdapter3row.ViewHolder();
            holder.txtFirst = convertView.findViewById(R.id.first);
            holder.txtSecond = convertView.findViewById(R.id.second);
            holder.txtThird = convertView.findViewById(R.id.third);
           holder.txtFirst.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {

               }
           });
            holder.txtSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.txtThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
/*
            txtCode = (TextView) convertView.findViewById(R.id.Code);
            txtFirst = (TextView) convertView.findViewById(R.id.DBCustName);
            txtSecond = (TextView) convertView.findViewById(R.id.DBComp);
*/
            
            
            convertView.setTag(holder);
        } else {
            holder = (ListViewAdapter3row.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            holder.txtFirst.setText(map.get(REPORT_FIRST_COLUMN).toString());
            holder.txtSecond.setText(map.get(REPORT_SECOND_COLUMN).toString());
            //holder.txtSecond.setText(map.get("search").toString());
            holder.txtThird.setText(map.get(REPORT_THIRD_COLUMN).toString());
 
        } catch (NullPointerException e) {
            System.out.println("Error: " + e.toString());
        }
        return convertView;
        
    }
    
    private class ViewHolder {
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
        
    }
}
