package com.blazeminds.pos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SchemeProductListAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> list;
    private ViewHolder holder;
    private PosDB db;
    
    public SchemeProductListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        this.activity = activity;
        this.list = list;
        db = PosDB.getInstance(activity);
        db.OpenDb();
    }
    
    @Override
    public int getCount() {
        return list.size();
    }
    
    @Override
    public Object getItem(int position) {
        return null;
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            
            convertView = inflater.inflate(R.layout.popup_scheme_product_list_adapter, null);
            
            holder = new ViewHolder();
            
            holder.productAutoComplete = convertView.findViewById(R.id.search_product);
            
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        PopulateAutoComplete(holder, list.get(position));
        
        return convertView;
    }
    
    
    private void PopulateAutoComplete(ViewHolder holder, HashMap<String, String> hashMaps) {
        
        
        hashMaps.get("b_id");
        hashMaps.get("p_type_id");
        
        ArrayList<HashMap<String, String>> arrayList = db.getSelectedProductWithTypeId(hashMaps.get("p_type_id"));
        
        
        List<String> list_name = new ArrayList<>();
        final List<String> list_id = new ArrayList<>();
        
        
        for (HashMap<String, String> map : arrayList) {
            
            list_name.add(map.get("name"));
            list_id.add(map.get("id"));
            
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_dropdown_item_1line, list_name);
        
        holder.productAutoComplete.setAdapter(adapter);
        
        holder.productAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                Toast.makeText(activity,
                        parent.getAdapter().getItem(position).toString() + " : " + list_id.get(position),
                        Toast.LENGTH_LONG).show();
            }
        });
        
    }
    
    private class ViewHolder {
        AutoCompleteTextView productAutoComplete;
    }
}
