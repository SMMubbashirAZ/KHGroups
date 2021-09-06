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
 * Created by Blazeminds on 4/18/2018.
 */

public class SupportDetailListHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public SupportDetailListHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        
        final SupportDetailListHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_support_detail_header, null);
            holder = new SupportDetailListHeaderAdapter.ViewHolder();
            
            holder.datetimeTxt = convertView.findViewById(R.id.datetimeTxt);
            holder.messageTxt = convertView.findViewById(R.id.messageTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.adminTxt = convertView.findViewById(R.id.adminTxt);
            //holder.notesTxt = (TextView) convertView.findViewById(R.id.notesTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (SupportDetailListHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.datetimeTxt.setText(map.get("support_detail_datetime").toString());
            holder.messageTxt.setText(map.get("support_detail_message").toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.adminTxt.setText(map.get("support_detail_person").toString());
            //holder.notesTxt.setText(map.get("sv_longitude").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Support Detail HEADER Adapter: " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView datetimeTxt;
        TextView messageTxt;
        /*TextView txtTotalAmount;*/
        TextView adminTxt;
        // TextView notesTxt;
        
    }
}