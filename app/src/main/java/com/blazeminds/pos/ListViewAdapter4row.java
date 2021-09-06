package com.blazeminds.pos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


import static com.blazeminds.pos.Constant.REPORT_FIRST_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_SECOND_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_THIRD_COLUMN;

/**
 * Created by Blazeminds on 2/22/2018.
 */

public class ListViewAdapter4row extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;

    public ListViewAdapter4row(Context activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        return list.size();
    }
    
    @Override
    public Object getItem(int i) {
        return list.get(i);
    }
    
    @Override
    public long getItemId(int i) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        ListViewAdapter4row.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_row, null);
            holder = new ListViewAdapter4row.ViewHolder();
            holder.txtFirst = convertView.findViewById(R.id.first);
            holder.txtSecond = convertView.findViewById(R.id.second);
            holder.txtThird = convertView.findViewById(R.id.third);
            holder.txtFourth = convertView.findViewById(R.id.fourth);

/*
            txtCode = (TextView) convertView.findViewById(R.id.Code);
            txtFirst = (TextView) convertView.findViewById(R.id.DBCustName);
            txtSecond = (TextView) convertView.findViewById(R.id.DBComp);
*/
            
            
            convertView.setTag(holder);
        } else {
            holder = (ListViewAdapter4row.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            holder.txtFirst.setText(map.get(REPORT_FIRST_COLUMN).toString());
            holder.txtSecond.setText(map.get(REPORT_SECOND_COLUMN).toString());
            //holder.txtSecond.setText(map.get("search").toString());
            holder.txtThird.setText(map.get(REPORT_THIRD_COLUMN).toString());
            holder.txtFourth.setText(map.get(REPORT_FOURTH_COLUMN).toString());
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
