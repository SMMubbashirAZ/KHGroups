package com.blazeminds.pos;

import android.app.Activity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.blazeminds.pos.autocomplete_resource.InputFilterMinMax;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Blazeminds on 1/9/2018.
 */

public class PaymentRecievingListViewAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    public ArrayList<String> list1;
    Activity activity;
    
    
    public PaymentRecievingListViewAdapter(Activity activity, ArrayList<HashMap<String, String>>/*ArrayList<String>*/ list) {
        super();
        this.activity = activity;
        
        
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
        final PaymentRecievingListViewAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_payment_recieving_row, null);
            holder = new PaymentRecievingListViewAdapter.ViewHolder();
            //  holder.txtCode = (TextView) convertView.findViewById(R.id.Code);
            //  holder.txtPic = (ImageView) convertView.findViewById(R.id.Pic);
            holder.orderIdTxtView = convertView.findViewById(R.id.orderIdTxt);
            holder.amountTxtView = convertView.findViewById(R.id.amountTxt);
            holder.recievingEdtTxt = convertView.findViewById(R.id.recievingEdtTxt);
            // holder.txtFourth = (TextView) convertView.findViewById(R.id.DBBrandNameValues);
            
            
            convertView.setTag(holder);
        } else {
            holder = (PaymentRecievingListViewAdapter.ViewHolder) convertView.getTag();
        }
        
        final HashMap<String,String> map = list.get(i);
        
        
        try {
            
            
            holder.orderIdTxtView.setText(map.get("orderId"));
            //holder.amount = Integer.parseInt(map.get("amount").toString());
            // holder.amountRecieved = Integer.parseInt(map.get("amountRecieved").toString());
            //holder.result = holder.amount - holder.amountRecieved;
            holder.amountTxtView.setText(map.get("total"));
            holder.recievingEdtTxt.setText(map.get("recieving"));
            //  holder.txtFourth.setText(map.get(FOURTH_COLUMN).toString());
            
            
        } catch (Exception e) {
            System.out.println("Error IMG: " + e.toString());
        }
        
        
        holder.recievingEdtTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    
                    try {
                        map.put("recieving", holder.recievingEdtTxt.getText().toString());
                        
                    } catch (Exception e) {
                        map.put("recieving", "0");
                        
                    }
                } else {
                    try {
                        map.put("recieving", holder.recievingEdtTxt.getText().toString());
                        
                    } catch (Exception e) {
                        map.put("recieving", "0");
                        
                    }
                }
            }
        });
        
        holder.recievingEdtTxt.setFilters(new InputFilter[]{new InputFilterMinMax("1", holder.amountTxtView.getText().toString())});
        
        return convertView;
    }
    
    private class ViewHolder {
        //TextView txtCode;
        //ImageView txtPic;
        TextView orderIdTxtView;
        TextView amountTxtView;
        EditText recievingEdtTxt;
        int amount = 0, amountRecieved = 0, result = 0;
        //TextView txtFourth;
        
    }
}
