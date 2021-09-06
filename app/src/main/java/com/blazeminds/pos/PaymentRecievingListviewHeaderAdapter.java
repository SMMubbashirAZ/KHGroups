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
 * Created by Blazeminds on 1/9/2018.
 */

public class PaymentRecievingListviewHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public PaymentRecievingListviewHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final PaymentRecievingListviewHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item4payment_recieving_header, null);
            holder = new PaymentRecievingListviewHeaderAdapter.ViewHolder();
            
            holder.orderIdTxt = convertView.findViewById(R.id.orderIdTxt);
            holder.amountTxt = convertView.findViewById(R.id.amountTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.recievingTxt = convertView.findViewById(R.id.recievingTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (PaymentRecievingListviewHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.orderIdTxt.setText(map.get("orderId").toString());
            holder.amountTxt.setText(map.get("amount").toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.recievingTxt.setText(map.get("recieving").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Payment Recieving ListHEADERAdapter: " + e.toString());
        }
        return convertView;
    }
    
    private class ViewHolder {
        TextView orderIdTxt;
        TextView amountTxt;
        /*TextView txtTotalAmount;*/
        TextView recievingTxt;
        
    }
}
