package com.blazeminds.pos;

/*
  Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.ConstantRoute.AreaName;
import static com.blazeminds.pos.ConstantRoute.Name;
import static com.blazeminds.pos.ConstantRoute.RouteID;
import static com.blazeminds.pos.ConstantRoute.VisitsDone;


public class ListViewAdapter4Route extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    
    
    public ListViewAdapter4Route(Context activity, ArrayList<HashMap<String, String>> list) {
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
        final ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item4route, null);
            holder = new ViewHolder();
            holder.txtName = convertView.findViewById(R.id.CustName4Route);
            holder.txtMapName = convertView.findViewById(R.id.MapName4Route);
            //holder.txtVisits = (TextView) convertView.findViewById(R.id.Visits4Route);
            holder.switchDone = convertView.findViewById(R.id.Visits4Route);
            
            holder.txtRouteID = convertView.findViewById(R.id.ROUTEID);

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
        
        final HashMap map = list.get(position);
        
        try {
            
            holder.txtName.setText(map.get(Name).toString());
            holder.txtRouteID.setText(map.get(RouteID).toString());
            
            
            //holder.txtVisits.setText( map.get(Visits).toString() );
            
            if (map.get(VisitsDone).toString().equalsIgnoreCase("0")) {
                holder.switchDone.setChecked(false);
            } else {
                holder.switchDone.setChecked(true);
                
            }
            
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
            
            
            holder.switchDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    
                    
                    PosDB db =PosDB.getInstance(activity);
                    
                    if (isChecked) {
                        
                        db.OpenDb();
                        db.DeleteYesterdayOldRoutes();
                        db.updateRouteVisitDone(map.get(RouteID).toString(), 1);
                        Log.d("SwitchDone", "Checked");
                        db.CloseDb();
                    } else {
                        db.OpenDb();
                        db.DeleteYesterdayOldRoutes();
                        db.updateRouteVisitDone(map.get(RouteID).toString(), 0);
                        Log.d("SwitchDone", "UnChecked");
                        db.CloseDb();
                        
                    }
                    
                }
            });
            
        } catch (Exception e) {
            System.out.println("Error LIstViewAdapterRoute: " + e.toString());
        }
        
        
        return convertView;
    }
    
    private class ViewHolder {
        TextView txtName;
        TextView txtMapName;
        TextView txtRouteID;
        //TextView txtVisits;
        ToggleButton switchDone;
        
    }
    
}