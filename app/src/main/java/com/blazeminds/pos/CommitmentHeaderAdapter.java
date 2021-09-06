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

/**
 * Created by Blazeminds on 4/25/2018.
 */

public class CommitmentHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public CommitmentHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        CommitmentHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_commitment_header, null);
            holder = new CommitmentHeaderAdapter.ViewHolder();
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
            holder = (CommitmentHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            holder.txtFirst.setText(map.get("comm_datetime").toString());
            holder.txtSecond.setText(map.get("comm_customer_name").toString());
            //holder.txtSecond.setText(map.get("search").toString());
            holder.txtThird.setText(map.get("comm_sale_amount").toString());
            holder.txtFourth.setText(map.get("comm_gift_amount").toString());
        } catch (NullPointerException e) {
            System.out.println("Error Commitment Header Adapter: " + e.toString());
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

