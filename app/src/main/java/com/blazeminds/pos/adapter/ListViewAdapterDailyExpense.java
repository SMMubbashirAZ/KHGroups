package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.blazeminds.pos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by Blazeminds on 2/22/2018.
 */

public class ListViewAdapterDailyExpense extends BaseAdapter {
    
    public JSONArray list;
    Activity activity;

    public ListViewAdapterDailyExpense(Context activity, JSONArray list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
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
       public JSONArray getList() {
     return list;
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
        ListViewAdapterDailyExpense.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.input_daily_expense, null);
            holder = new ListViewAdapterDailyExpense.ViewHolder();
            holder.txtFirst = convertView.findViewById(R.id.first);
            holder.txtSecond = convertView.findViewById(R.id.second);

           holder.txtFirst.setOnClickListener(new View.OnClickListener() {
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
            holder = (ListViewAdapterDailyExpense.ViewHolder) convertView.getTag();
        }

        holder.txtSecond.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    JSONObject rowObject= new JSONObject(list.getJSONObject(position).toString());
                rowObject.put("amount",  s.toString());

                    list.put(position,rowObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        
        try {

            JSONObject map = list.getJSONObject(position);
            holder.txtFirst.setText(map.getString("iname").toString());
if(map.has("amount")){

    holder.txtSecond.setText(map.getString("amount").toString());
}
            //holder.txtSecond.setText(map.get("search").toString());

 
        } catch (JSONException e) {
            System.out.println("Error: " + e.toString());
        }
        return convertView;
        
    }
    
    private class ViewHolder {
        TextView txtFirst;
        EditText txtSecond;

    }
}
