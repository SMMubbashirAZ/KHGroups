package com.blazeminds.pos;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SchemeDisplayListAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> list;
    
    
    private PosDB db;
    
    
    public SchemeDisplayListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
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
    public int getViewTypeCount() {
        return getCount();
    }
    
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            
            convertView = inflater.inflate(R.layout.display_scheme_list_adapter, null);
            
            holder = new ViewHolder();
            
            holder.display = convertView.findViewById(R.id.tv);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        
        StringBuilder display = new StringBuilder();
        /* " [product_sub_type_id, scheme, max, subTotal, product_type_id, multi, brand_id, tax1, tax2, min, tax_filer_3, discount1, tax_filer_1, discount2, tax_filer_2, schemeProduct, tradeOffer, tradePrice, schemeVal, tax3]"*/
        display.append(db.getSelectedProductName(list.get(position).get("p_id")));
        
        display.append("\nScheme (" + list.get(position).get("scheme") + "/" + list.get(position).get("schemeVal") + ")");
        
        if (!list.get(position).get("brand_id").equals("0"))
            display.append("\nScheme on Brand => " + (db.getBrandName(list.get(position).get("brand_id")) + " (Available)"));
        
        if (!list.get(position).get("product_type_id").equals("0"))
            display.append("\nScheme on Product Type => " + db.getProductCategoryType(list.get(position).get(
                    "product_type_id")) + " (Available)");
        
        display.append("\nMinimum Quantity => " + (list.get(position).get("min")));
        
        display.append("\nMaximum Quantity => " + (list.get(position).get("max")));
        
        holder.display.setText(display);
        
        return convertView;
    }
    
    
    private class ViewHolder {
        
        
        TextView display;
        
        
    }
}
