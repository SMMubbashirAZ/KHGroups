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

import static com.blazeminds.pos.Constant.EDIT_ORDER_ITEM;
import static com.blazeminds.pos.Constant.EDIT_ORDER_ITEM_DETAILS;
import static com.blazeminds.pos.Constant.EDIT_ORDER_ITEM_DISCOUNT;
import static com.blazeminds.pos.Constant.EDIT_ORDER_NEWQTY;
import static com.blazeminds.pos.Constant.EDIT_ORDER_OLDQTY;

public class EditOrderListViewHeaderAdapter extends BaseAdapter {
	public ArrayList<HashMap<String, String>> list;
	Activity activity;
	//int cases;

    /*public EditOrderListViewHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> editOrderList , int cases ) {
		super();
        this.activity = (Activity) activity;
        this.editOrderList = editOrderList;
        this.cases = cases;
    }*/
	
	public EditOrderListViewHeaderAdapter(Context activity, ArrayList<HashMap<String, String>> list) {
		super();
		this.activity = (Activity) activity;
		this.list = list;
		//this.cases = cases;
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		// TODO Auto-generated method stub
		final ViewHolder holder;
		LayoutInflater inflater = activity.getLayoutInflater();
		
		if (BuildConfig.FLAVOR.equalsIgnoreCase("english")||BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")){
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_edit_order_header_for_english, null);
				holder = new ViewHolder();
				holder.txtItemName = convertView.findViewById(R.id.ItemHeader);
				holder.txtOldQty = convertView.findViewById(R.id.QtyHeader);
				holder.NewQty = convertView.findViewById(R.id.NewQtyHeader);
				holder.itemDetails = convertView.findViewById(R.id.ItemDetailHeader);
				holder.itemDiscount = convertView.findViewById(R.id.ItemDiscountHeader);
				holder.itemDiscount2 = convertView.findViewById(R.id.ItemDiscountHeader2);
				
				//holder.DeleteBtn = (Button) convertView.findViewById(R.id.DeleteOrder);

            /*switch ( cases ){

                case 1:{
                    holder.itemDetails.setVisibility(View.VISIBLE);
                }
                break;

                case 2:{
                    holder.itemDetails.setVisibility(View.GONE);
                }
                break;
            }*/
				
				convertView.setTag(holder);
				
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			HashMap map = list.get(position);
			
			
			try {
				
				holder.txtItemName.setText(map.get(EDIT_ORDER_ITEM).toString());
				holder.txtOldQty.setText(map.get(EDIT_ORDER_OLDQTY).toString());
				holder.NewQty.setText(map.get(EDIT_ORDER_NEWQTY).toString());
				holder.itemDetails.setText(map.get(EDIT_ORDER_ITEM_DETAILS).toString());
				holder.itemDiscount.setText(map.get(EDIT_ORDER_ITEM_DISCOUNT).toString());
				holder.itemDiscount2.setText(map.get("Disc2").toString());
				//holder.itemDetails.setVisibility(View.GONE);
				
				
			} catch (NullPointerException e) {
				System.out.println("Error Edit Order ListAdapterHEADER: " + e.toString());
			}
		}else {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_edit_order_header, null);
				holder = new ViewHolder();
				holder.txtItemName = convertView.findViewById(R.id.ItemHeader);
				holder.txtOldQty = convertView.findViewById(R.id.QtyHeader);
				holder.NewQty = convertView.findViewById(R.id.NewQtyHeader);
				holder.itemDetails = convertView.findViewById(R.id.ItemDetailHeader);
				holder.itemDiscount = convertView.findViewById(R.id.ItemDiscountHeader);
				
				//holder.DeleteBtn = (Button) convertView.findViewById(R.id.DeleteOrder);

            /*switch ( cases ){

                case 1:{
                    holder.itemDetails.setVisibility(View.VISIBLE);
                }
                break;

                case 2:{
                    holder.itemDetails.setVisibility(View.GONE);
                }
                break;
            }*/
				
				convertView.setTag(holder);
				
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			HashMap map = list.get(position);
			
			
			try {
				
				holder.txtItemName.setText(map.get(EDIT_ORDER_ITEM).toString());
				holder.txtOldQty.setText(map.get(EDIT_ORDER_OLDQTY).toString());
				holder.NewQty.setText(map.get(EDIT_ORDER_NEWQTY).toString());
				holder.itemDetails.setText(map.get(EDIT_ORDER_ITEM_DETAILS).toString());
				holder.itemDiscount.setText(map.get(EDIT_ORDER_ITEM_DISCOUNT).toString());
				
				//holder.itemDetails.setVisibility(View.GONE);
				
				
			} catch (NullPointerException e) {
				System.out.println("Error Edit Order ListAdapterHEADER: " + e.toString());
			}
		}
		
		return convertView;
	}
	
	private class ViewHolder {
		TextView txtItemName;
		TextView txtOldQty;
		TextView NewQty;
		TextView itemDetails;
		TextView itemDiscount,itemDiscount2;
		
	}
	
	
}