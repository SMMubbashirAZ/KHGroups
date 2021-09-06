package com.blazeminds.pos.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by Blazeminds on 2/22/2018.
 */

public class CustomerLastOrderAdapter extends BaseAdapter {
    
    public JSONArray list;
    Activity activity;
PosDB db;
    public CustomerLastOrderAdapter(Context activity, JSONArray list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        db = PosDB.getInstance(activity);
    }
    
    @Override
    public int getCount() {
        return list.length();
    }
    public JSONArray GetList() {
        return list;
    }
    @Override
    public JSONObject getItem(int i) {
        try{return list.getJSONObject(i);}
        catch (JSONException e)
        {
            return  new JSONObject();
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
        CustomerLastOrderAdapter.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            
            convertView = inflater.inflate(R.layout.customer_last_order_details, null);
            holder = new CustomerLastOrderAdapter.ViewHolder();
         
          holder.ExportLocalLL = convertView.findViewById(R.id.ExportLocalLL);
         holder.RateTV = convertView.findViewById(R.id.RateTV);
        holder.ItemTV = convertView.findViewById(R.id.ItemTV);
        holder.QtyTV = convertView.findViewById(R.id.QtyTV);
        
     
         
        

            
            
            convertView.setTag(holder);
        } else {
            holder = (CustomerLastOrderAdapter.ViewHolder) convertView.getTag();
        }
    
    
  
        try {
            JSONObject map = list.getJSONObject(position);
          
          
         
            holder.     RateTV.setText(map.getString("prd_amount"));
    
            holder.     ItemTV.setText(map.getString("name"));
            holder.     QtyTV.setText(map.getString("qty"));
      

         
           
        } catch (NullPointerException e) {
            System.out.println("Error: " + e.toString());
        }
        catch (JSONException e) {
            System.out.println("Error: " + e.toString());
        }
        return convertView;
        
    }
    
    private class ViewHolder {
     
        LinearLayout ExportLocalLL;
        TextView RateTV,ItemTV,QtyTV;
    }
}
