package com.blazeminds.pos;

import android.app.Activity;
import androidx.appcompat.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowSchemeListAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> list;
    private PosDB db;
    private ViewHolder holder;
    private ArrayList<HashMap<String, String>> holdList;
    private List<String> nameList;
    
    public ShowSchemeListAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        this.activity = activity;
        this.list = list;
        holdList = new ArrayList<>();
        db = PosDB.getInstance(activity);
        db.OpenDb();
        nameList = new ArrayList<>();
    }
    
    public ArrayList<HashMap<String, String>> getHoldList() {
        
        
        
        return list;
    }
    
    public int checkScheme() {
        
        int i = 0;
        for (HashMap<String, String> map : list) {
            
            if (map.get("p_id").equals("0")) {
                
                
                Toast.makeText(activity, "Select Product at row " + (i + 1), Toast.LENGTH_SHORT).show();
                return i;
            }
            i++;
            
        }
        
        return -1;
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
    public int getItemViewType(int position) {
        
        
        return position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        
        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            
            convertView = inflater.inflate(R.layout.popup_scheme_listview_adapter, null);
            
            holder = new ViewHolder();
            
            holder.search_product = convertView.findViewById(R.id.search_product);
            holder.l1_l1 = convertView.findViewById(R.id.l1_l1);
            holder.textView = convertView.findViewById(R.id.product_tv);
            holder.divider = convertView.findViewById(R.id.divider);
            
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        {
            boolean a = false;
            for (String s : nameList) {
                if (holder.search_product.getText().toString().equals("") || holder.search_product.getText().toString().equals(s)) {
                    holder.search_product.setError(null);
                    
                    a = true;
                }
            }
            
            if (!a) {
                holder.search_product.setError("Product not found");
            }
        }
        
        PopulateAutoComplete(holder, list.get(position), position);
        
        
        if (list.get(position).get("divider").equals("true")) {
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.divider.setVisibility(View.GONE);
        }
        
        if (list.get(position).get("visibility").equals("true")) {
            holder.textView.setText(("Scheme available for " + db.getProductCategoryType(list.get(position).get(
                    "p_type_id")) + " " + list.get(position).get("schemeVal")));
        } else holder.textView.setVisibility(View.GONE);
        
        
        return convertView;
    }
    
    private void PopulateAutoComplete(final ViewHolder holder, final HashMap<String, String> hashMaps,
                                      final int p) {
        
        
        hashMaps.get("b_id");
        hashMaps.get("p_type_id");

        ArrayList<HashMap<String, String>> listCheck=   db.getPricingDataforscheme(
                hashMaps.get("scheme_item_search_SelectedCustomerTypeId"),
                hashMaps.get("scheme_item_search_SelectedCustomerId"),
                hashMaps.get("scheme_item_search_val_1"),
                hashMaps.get("scheme_item_search_val_0"),
                hashMaps.get("scheme_item_search_allQty"),
                hashMaps.get("scheme_item_search_ProVal"),
                hashMaps.get("scheme_item_search_val1_0"),
                hashMaps.get("scheme_item_search_val1_1"),
                hashMaps.get("scheme_item_search_val_2"));
       String[] product_type_id= listCheck.get(0).get("product_type_id").split(",");
        String[] product_sub_type_id= listCheck.get(0).get("product_sub_type_id").split(",");
        String[] brand_id= listCheck.get(0).get("brand_id").split(",");
        String[] p_id= listCheck.get(0).get("p_id").split(",");

       // final ArrayList<HashMap<String, String>> arrayList = db.getSelectedProductWithTypeId(hashMaps.get("p_type_id"));
        final ArrayList<HashMap<String, String>> arrayList= new ArrayList<HashMap<String, String>>();
if(p_id.length>0)
{
    for(int i =0; i<p_id.length;i++){
    if(!p_id[i].equals("0"))
    {
        arrayList.addAll(db.getSelectedProductWithId((p_id[i])));
    }
    }
}
if(arrayList.size()==0 &&product_sub_type_id.length>0)
{
    for(int i =0; i<product_sub_type_id.length;i++){
        if(!product_sub_type_id[i].equals("0"))
        {
            arrayList.addAll(db.getSelectedProductWithSubTypeId((product_sub_type_id[i])));
        }
    }
}

        if(arrayList.size()==0 && product_type_id.length>0)
        {
            for(int i =0; i<product_type_id.length;i++){
                if(!product_type_id[i].equals("0"))
                {
                    arrayList.addAll(db.getSelectedProductWithTypeId((product_type_id[i])));
                }
            }
        }

        if(arrayList.size()==0 && brand_id.length>0)
        {
            for(int i =0; i<brand_id.length;i++){
                if(!brand_id[i].equals("0"))
                {
                    arrayList.addAll(db.getSelectedProductWithBrandId((brand_id[i])));
                }
            }
        }

        List<String> list_name = new ArrayList<>();
        final List<String> list_id = new ArrayList<>();
        
        
        for (HashMap<String, String> map : arrayList) {
            
            list_name.add(map.get("name"));
            
            list_id.add(map.get("id"));
            
        }
        
        
        if (list_name.size() != nameList.size()) {
            nameList.addAll(list_name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_dropdown_item, list_name);
        
        holder.search_product.setAdapter(adapter);

        holder.search_product.setThreshold(1);
        holder.search_product.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                
                
                if (!hasFocus) {
                    
                    
                    if (hashMaps.get("p_id").equals("0") && !holder.search_product.getText().toString().equals("")) {
                        
                        holder.search_product.setError("Product not found");
                        
                        
                    } else holder.search_product.setError(null);
                    
                    
                }
                
            }
        });
        holder.search_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                
                for (HashMap<String, String> map : arrayList) {
                    
                    if (map.get("name").equals(parent.getItemAtPosition(position))) {
                        hashMaps.put("item_search",map.get("name"));
                        list.get(p).put("p_id", map.get("id"));
                    }
                    
                }
                
            }
        });
        if(hashMaps.containsKey("item_search") && hashMaps.containsKey("item_search_id"))
        {
            list.get(p).put("p_id", hashMaps.get("item_search_id"));
            holder.search_product.setText(hashMaps.get("item_search"));
            holder.search_product.setError(null);
        }
        
    }
    
    
    private class ViewHolder {
        
        
        AutoCompleteTextView search_product;
        TextView textView;
        View divider;
        LinearLayoutCompat l1_l1;
        
        
    }
}
