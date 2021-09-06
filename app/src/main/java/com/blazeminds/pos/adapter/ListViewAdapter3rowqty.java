package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.REPORT_FIRST_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_SECOND_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_THIRD_COLUMN;

/**
 * Created by Blazeminds on 2/22/2018.
 */

public class ListViewAdapter3rowqty extends BaseAdapter {
    
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    PosDB db;

    public ListViewAdapter3rowqty(Context activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        db= PosDB.getInstance(activity);
        db.OpenDb();
        db.OpenDb();
    }
    
    @Override
    public int getCount() {
        return list.size();
    }
    
    @Override
    public HashMap<String, String> getItem(int i) {
        return list.get(i);
    }
    
    @Override
    public long getItemId(int i) {
        return 0;
    }
    void itemClik()
    {

    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        ListViewAdapter3rowqty.ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_3_row_qty, null);
            holder = new ListViewAdapter3rowqty.ViewHolder();
            holder.txtFirst = convertView.findViewById(R.id.first);
            holder.txtSecond = convertView.findViewById(R.id.second);
            holder.txtThird = convertView.findViewById(R.id.third);
            holder.verifyStock_LV= convertView.findViewById(R.id.verifyStock_Linearlayout);
            
           holder.verifyStock_LV.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   TextView txtTechCharacteristic = (TextView) v.findViewById(R.id.first);
                   String txt = txtTechCharacteristic.getText().toString();
                   TextView txtTechCharacteristicdate = (TextView) v.findViewById(R.id.third);
                   String txtdate = txtTechCharacteristicdate.getText().toString();
    
                   AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
    
                   builderSingle.setTitle( "ID: "+txt+" Date: "+txtdate);
    
                   final ListViewAdapter2row arrayAdapter = new ListViewAdapter2row(activity, db.getPatientOrderList_By_OrderID(txt));
    
    
                   builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
    
                   builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
//						String strName = arrayAdapter.getItem(which);
//						AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
//						builderInner.setMessage(strName);
//						builderInner.setTitle("Your Selected Item is");
//						builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog,int which) {
//								dialog.dismiss();
//							}
//						});
                           //				builderInner.show();
                       }
                   });
                   builderSingle.show();
                   
                   
               }
           });
/*
            txtCode = (TextView) convertView.findViewById(R.id.Code);
            txtFirst = (TextView) convertView.findViewById(R.id.DBCustName);
            txtSecond = (TextView) convertView.findViewById(R.id.DBComp);
*/
            
            
            convertView.setTag(holder);
        } else {
            holder = (ListViewAdapter3rowqty.ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            holder.txtFirst.setText(map.get(REPORT_FIRST_COLUMN).toString());
          //  holder.txtSecond.setText(map.get(REPORT_SECOND_COLUMN).toString());
            //holder.txtSecond.setText(map.get("search").toString());
            holder.txtThird.setText(map.get(REPORT_THIRD_COLUMN).toString());
 
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
        LinearLayout verifyStock_LV;
        
    }
}
