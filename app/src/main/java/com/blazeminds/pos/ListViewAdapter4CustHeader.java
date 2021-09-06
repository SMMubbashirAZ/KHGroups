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

import static com.blazeminds.pos.Constant.CODE_COLUMN;
import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;

/**
 * Created by Blazeminds on 11/3/2018.
 */

public class ListViewAdapter4CustHeader extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public ListViewAdapter4CustHeader(Context activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

/*
    TextView txtCode;
    TextView txtFirst;
    TextView txtSecond;
*/
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        // TODO Auto-generated method stub
        ListViewAdapter4CustHeader.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item4cust_header, null);
            holder = new ListViewAdapter4CustHeader.ViewHolder();
            holder.txtCode = convertView.findViewById(R.id.Code);
            holder.txtFirst = convertView.findViewById(R.id.DBCustName);
            holder.txtSecond = convertView.findViewById(R.id.DBComp);
            holder.txtThird = convertView.findViewById(R.id.DBPriceValues);
            holder.txtFourth = convertView.findViewById(R.id.DBBrandNameValues);
            
            
            convertView.setTag(holder);
        } else {
            holder = (ListViewAdapter4CustHeader.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            
            holder.txtCode.setText(map.get(CODE_COLUMN).toString());
            holder.txtFirst.setText(map.get(FIRST_COLUMN).toString());
            holder.txtSecond.setText(map.get(SECOND_COLUMN).toString());
            holder.txtThird.setText(map.get(THIRD_COLUMN).toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error: " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView txtCode;
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        TextView txtFourth;
    }
    
    
}
