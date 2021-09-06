package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.blazeminds.pos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;



/**
 * Created by Blazeminds on 2/22/2018.
 */

public class AdapterHospitalSurvey extends BaseAdapter {
    
    public JSONArray list;
    Activity activity;

    public AdapterHospitalSurvey(Context activity, JSONArray list) {
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
        AdapterHospitalSurvey.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.hospital_survey, null);
            holder = new AdapterHospitalSurvey.ViewHolder();
            holder.InvestmentDetails = convertView.findViewById(R.id.InvestmentDetails);

            holder.NameEdtxt = convertView.findViewById(R.id.NameEdtxt);
            holder.txtSecond = convertView.findViewById(R.id.second);




/*
            txtCode = (TextView) convertView.findViewById(R.id.Code);
            txtFirst = (TextView) convertView.findViewById(R.id.DBCustName);
            txtSecond = (TextView) convertView.findViewById(R.id.DBComp);
*/
            
            
            convertView.setTag(holder);
        } else {
            holder = (AdapterHospitalSurvey.ViewHolder) convertView.getTag();
        }
    final    AdapterHospitalSurvey.ViewHolder   myholder=holder;






        FilterWithSpaceAdapter     myAdapterDoctor1 = new FilterWithSpaceAdapter<>(activity, R.layout.layout_custom_spinner, R.id.item, new ArrayList());
     holder.   NameEdtxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {




                // update the adapater


                }




            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        holder.txtSecond.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        holder.InvestmentDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    JSONObject rowObject= new JSONObject(list.getJSONObject(position).toString());
                rowObject.put("investment_details",  s.toString());

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
            holder.InvestmentDetails.setText(list.getJSONObject(position).getString("contact"));


        holder.NameEdtxt.setText(list.getJSONObject(position).getString("name"));
        holder.txtSecond.setText(list.getJSONObject(position).getString("position"));

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return convertView;
        
    }
    
    private class ViewHolder {


        EditText txtSecond,InvestmentDetails;
        EditText NameEdtxt;

    }
}
