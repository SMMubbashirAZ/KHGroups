package com.blazeminds.pos;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Blazeminds on 3/7/2018.
 */

public class ExpenseListHeaderAdapter extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    public ExpenseListHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
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
        
        final ExpenseListHeaderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_4_expense_header, null);
            holder = new ExpenseListHeaderAdapter.ViewHolder();
            
            holder.amountTV = convertView.findViewById(R.id.amountTxt);
            holder.expenseTypeTV = convertView.findViewById(R.id.expenseTypeTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.dateTV = convertView.findViewById(R.id.dateTxt);
            holder.shopTV = convertView.findViewById(R.id.shopTxt);
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (ExpenseListHeaderAdapter.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(i);
        
        try {
            
            holder.dateTV.setText(map.get("exp_datetime").toString());
            holder.expenseTypeTV.setText(map.get("exp_type").toString());
            holder.amountTV.setText(map.get("exp_amount").toString());
            
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            
            holder.shopTV.setText(map.get("exp_shop").toString());
            
            
        } catch (NullPointerException e) {
            System.out.println("Error Expense ListHEADERAdapter: " + e.toString());
        }
        return convertView;
    }
    
    public String convertDate(String dbDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
        DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
        Date dt = null;
        try {
            dt = df.parse(dbDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return dfProper.format(dt);
    }
    
    private class ViewHolder {
        TextView amountTV;
        TextView expenseTypeTV;
        /*TextView txtTotalAmount;*/
        TextView dateTV;
        TextView shopTV;
        
    }
}
