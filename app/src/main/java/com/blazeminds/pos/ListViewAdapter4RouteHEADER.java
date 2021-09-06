package com.blazeminds.pos;

/*
  Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.ConstantRoute.AreaName;
import static com.blazeminds.pos.ConstantRoute.Name;
import static com.blazeminds.pos.ConstantRoute.Visits;


public class ListViewAdapter4RouteHEADER extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    
    public ListViewAdapter4RouteHEADER(Context activity, ArrayList<HashMap<String, String>> list) {
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
    TextView txtThird;
    TextView txtFourth;
    TextView txtFifth;
*/
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item4route_header, null);
            holder = new ViewHolder();
            holder.txtName = convertView.findViewById(R.id.CustName4Route);
            holder.txtMapName = convertView.findViewById(R.id.MapName4Route);
            holder.txtVisits = convertView.findViewById(R.id.Visits4Route);

/*
            txtCode = (TextView) convertView.findViewById(R.id.Code);
            txtFirst = (TextView) convertView.findViewById(R.id.DBInventNameValues);
            txtSecond = (TextView) convertView.findViewById(R.id.DBQtyValues);
            txtThird = (TextView) convertView.findViewById(R.id.DBPriceValues);
            txtFourth = (TextView) convertView.findViewById(R.id.DBBrandNameValues);
            txtFifth = (TextView) convertView.findViewById(R.id.DBTypeNameValues);
*/
            
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        HashMap map = list.get(position);
        
        try {
            
            holder.txtName.setText(map.get(Name).toString());
            
            
            holder.txtVisits.setText(map.get(Visits).toString());
            
            holder.txtMapName.setText(map.get(AreaName).toString());

/*
                if( map.get(AreaName).toString().equalsIgnoreCase("null") || map.get(AreaName).toString().isEmpty() )
                    holder.txtMapName.setText("");
                if( !(map.get(AreaName).toString().equalsIgnoreCase("null")) || !(map.get(AreaName).toString().isEmpty()) )
                    holder.txtMapName.setText(map.get(AreaName).toString());

                if( map.get(Visits).toString().equalsIgnoreCase("null") || map.get(Visits).toString().isEmpty() )
                    holder.txtVisits.setText("");
                if( !(map.get(Visits).toString().equalsIgnoreCase("null")) || !(map.get(Visits).toString().isEmpty()) )
                    holder.txtVisits.setText(map.get(Visits).toString());
*/
        
        } catch (Exception e) {
            System.out.println("Error LIstViewAdapterRoute: " + e.toString());
        }
        
        
        return convertView;
    }
    
    private class ViewHolder {
        TextView txtName;
        TextView txtMapName;
        TextView txtVisits;
        
    }
    
}