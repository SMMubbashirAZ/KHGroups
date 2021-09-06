package com.blazeminds.pos;

/*
  Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class EditOrderSalesExecuteListViewAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    PosDB db;
    String OrderID;
    ListView lView;
    TableRow BtnLayout;
    int cases;
    String custType;
    String Qty;
    HashMap<String, String> map2 = new HashMap<>();
    private ArrayList<HashMap<String, String>> result;
    private double respons = 0;
    private double quantity = 0, quantityExe = 0, schemeQty = 0;
    private String quantityStr = "";
    
    public EditOrderSalesExecuteListViewAdapter(Context activity, ArrayList<HashMap<String, String>> list, PosDB dba, String order_id, ListView itemList, TableRow Lay, int cases) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        this.db = dba;
        this.OrderID = order_id;
        this.lView = itemList;
        this.BtnLayout = Lay;
        this.cases = cases;
        
    }
    
    public EditOrderSalesExecuteListViewAdapter(Context activity, ArrayList<HashMap<String, String>> list, PosDB dba, String order_id, ListView itemList, TableRow Lay, int cases, String custType) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        this.db = dba;
        this.OrderID = order_id;
        this.lView = itemList;
        this.BtnLayout = Lay;
        this.cases = cases;
        this.custType = custType;
        
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
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_edit_order_new, null);
            holder = new ViewHolder();
            holder.FrameLayout = convertView.findViewById(R.id.EditLayout);
            
            holder.txtItemID = convertView.findViewById(R.id.ItemID);
            //holder.txtTotal2 = (TextView) convertView.findViewById(R.id.ItemTotal2);
            holder.txtItemName = convertView.findViewById(R.id.ItemName);
            holder.txtOldQty = convertView.findViewById(R.id.OldQty);
            holder.NewQty = convertView.findViewById(R.id.QtyTxt);
            holder.NewQtyTxt = convertView.findViewById(R.id.QtyTxtView);
            holder.itemDetailsTV = convertView.findViewById(R.id.itemDetailsTxtView);
            /*holder.DeleteBtn = (Button) convertView.findViewById(R.id.DeleteOrder);*/
            
            switch (cases) {
                
                case 1: {
                    holder.NewQty.setVisibility(View.VISIBLE);
                    holder.NewQtyTxt.setVisibility(View.GONE);
                    //holder.itemDetailsTV.setVisibility(View.VISIBLE);
                }
                break;
                
                case 2: {
                    holder.NewQty.setVisibility(View.GONE);
                    holder.NewQtyTxt.setVisibility(View.VISIBLE);
                    // holder.itemDetailsTV.setVisibility(View.GONE);
                }
                break;
            }
            
            convertView.setTag(holder);

/*
            holder.NewQty.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if( s.toString().isEmpty() || s.toString().trim().isEmpty() ){
                        Fmap.put(EDIT_ORDER_NEWQTY, "0" );
                    }else{
                        Fmap.put(EDIT_ORDER_NEWQTY, s );
                    }


                }

                @Override
                public void afterTextChanged(Editable s) {


                }
            });

*/
        
        
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        final HashMap<String,String> map = list.get(position);
        
        
        try {
            
            holder.txtItemID.setText(map.get("productId"));
            holder.txtItemName.setText(map.get("productName"));
            
            if (!map.get("qty").equalsIgnoreCase(""))
                quantity = Double.parseDouble(map.get("qty"));
            if (!map.get("qtyExe").equalsIgnoreCase(""))
                quantityExe = Double.parseDouble(map.get("qtyExe"));
            if (!map.get("schemeQty").equalsIgnoreCase(""))
                schemeQty = Double.parseDouble(map.get("schemeQty"));
            
            holder.txtOldQty.setText(String.format("%.0f", (quantity/* - schemeQty*/)));
            //holder.txtTotal2.setText(map.get(EDIT_ORDER_TOTAL2).toString());
            holder.NewQty.setText(String.format("%.0f", (quantityExe/* - schemeQty*/)));
            holder.NewQtyTxt.setText(String.format("%.0f", (quantityExe/* - schemeQty*/)));

/*
            holder.DeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   // AreYouSure( activity, db, 1, OrderID );

                    //RefreshList( lView, OrderID );
                }
            });
*/
        
        
        } catch (NullPointerException e) {
            System.out.println("Error Edit Order Sales Execute ListAdapter: " + e.toString());
        } catch (NumberFormatException e) {
            System.out.println("Error Edit Sale Order Execute ListAdapter: " + e.toString());
            
        }
        
        holder.itemDetailsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                
                if (!map.get("qtyExe").equalsIgnoreCase(""))
                    quantityExe = Double.parseDouble(map.get("qtyExe"));
                if (!map.get("schemeQty").equalsIgnoreCase(""))
                    schemeQty = Double.parseDouble(map.get("schemeQty"));
                quantityStr = String.valueOf(quantityExe /*- schemeQty*/);
                initiatePopupWindow(view, map.get("productId"), quantityStr, OrderID);
            }
        });
        
        
        holder.NewQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    BtnLayout.setVisibility(View.VISIBLE);
                    
                    try {
                        // Below Line Added by Umais new
                        map.put("qty", holder.NewQty.getText().toString());
                        map.put("qtyExe", holder.NewQty.getText().toString());
                    } catch (Exception e) {
                        map.put("qty", "0");
                        map.put("qtyExe", "0");
                    }
                    
                    /*Toast.makeText( activity, "No Focus", Toast.LENGTH_SHORT ).show();*/
                    
                    
                } else {
                    
                    BtnLayout.setVisibility(View.GONE);
                    
                    try {
                        // Below Line Added by Umais new
                        map.put("qty", holder.NewQty.getText().toString());
                        map.put("qtyExe", holder.NewQty.getText().toString());
                    } catch (Exception e) {
                        map.put("qty", "0");
                        map.put("qtyExe", "0");
                    }
                    /*Toast.makeText( activity, "Focus", Toast.LENGTH_SHORT ).show();*/
                    
                    
                }
                
                
            }
        });

        /*holder.NewQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // double oldQTY = 0;
                double amountTxt = 0;

                try {


                    db.OpenDb();
                    for (int j = 0; j < editOrderList.size(); j++) {

                        // result = db.getPricingData(custType,  editOrderList.get(j).get("productId"));
                        result = db.getOrderDetailAgainstProduct(OrderID,  editOrderList.get(j).get("productId"));
                        if (editOrderList.get(j).get("qtyExe").equalsIgnoreCase("")){
                            Qty = "0";
                        }
                        else {
                            Qty = editOrderList.get(j).get("qtyExe");
                        }
                        if (result.size() > 0 ){
                            for (int a = 0 ; a < result.size() ; a++) {
                                map2 = result.get(a);
                                respons = pricingCalculation(Double.parseDouble(Qty),map2.get("tradePrice"),map2.get("discount1"),map2.get("discount2"),map2.get("tradeOffer"),map2.get("scheme"),map2.get("tax1"),map2.get("tax2"),map2.get("tax3"));
                                //res = 10 * Double.parseDouble(map.get("tradePrice")) * Double.parseDouble(map.get("discount1")) * Double.parseDouble(map.get("discount2")) * Double.parseDouble(map.get("tradeOffer")) * Double.parseDouble( map.get("scheme")) * Double.parseDouble(map.get("tax1")) * Double.parseDouble(map.get("tax2"))* Double.parseDouble(map.get("tax3"));
                                //break;
                            }
                        }

                        //oldQTY = oldQTY + Long.parseLong(allQty.get(j).getText().toString());

                        amountTxt = amountTxt + respons;

                        //amountTxt = amountTxt + (Double.parseDouble(allDBAmount.get(j).getText().toString()) * Double.parseDouble(allQty.get(j).getText().toString()));
                    }
                } catch (NumberFormatException e) {
                    //oldQTY += 0;
                    amountTxt += 0;


                }

                db.CloseDb();

                SharedPreferences settings = activity.getSharedPreferences("MY_PREF", MODE_PRIVATE);

                // Writing data to SharedPreferences
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("key",String.format("%.2f",amountTxt) );
                editor.commit();
                //TotalQty.setText( String.valueOf( "Total qty:\n"+ oldQTY+newValue ) );
                //TotalQty.setText(String.valueOf("Total Quantity:\n" + oldQTY));
                //TotalAmount.setText(String.valueOf("Total Amount:\n" +  String.format("%.2f",amountTxt)));

                //TotalAmountString = String.valueOf(String.format("%.2f",amountTxt));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/
        
        
        holder.FrameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (holder.NewQty.isFocused()) {
                        Rect outRect = new Rect();
                        holder.NewQty.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            holder.NewQty.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                    }
                }
                return false;
            }
        });
        
        holder.NewQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                
                
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    
                    if (holder.NewQty.isFocused()) {
                        Rect outRect = new Rect();
                        holder.NewQty.getGlobalVisibleRect(outRect);
                        holder.NewQty.clearFocus();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        
                    }
                    
                    return true;
                }
                
                return false;
            }
        });


/*
        holder.NewQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {



                if (actionId == EditorInfo.IME_ACTION_DONE) {


                    View view = activity.getCurrentFocus();
                    if (view != null) {
                        holder.NewQty.setFocusable(false);
                        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }


                    return true;
                }

                return false;
            }
        });
*/
        
        
        return convertView;
    }
    
    private void initiatePopupWindow(View v, String productId, String quantity, String orderID/*, final String customerTypeId, final String productId, int incre*/) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = activity.getLayoutInflater();
            View popUpView = inflater.inflate(R.layout.custom_details_popup, null);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            int widht = displaymetrics.widthPixels;
            int height = displaymetrics.heightPixels;
            
            PopupWindow popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            final TextView tradePrice = popUpView.findViewById(R.id.tradePriceTextView);
            final TextView discount1 = popUpView.findViewById(R.id.Discount1TextView);
            final TextView discount2 = popUpView.findViewById(R.id.Discount2TextView);
            final TextView tradeOffer = popUpView.findViewById(R.id.tradeOfferTextView);
            final TextView scheme = popUpView.findViewById(R.id.schemeTextView);
            final TextView tax1 = popUpView.findViewById(R.id.Tax1TextView);
            final TextView tax2 = popUpView.findViewById(R.id.tax2TextView);
            final TextView tax3 = popUpView.findViewById(R.id.tax3TextView);
            final TextView subTotal = popUpView.findViewById(R.id.subTotalTextView);
            
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            
            double Quantity = Double.parseDouble(quantity);
            //tradePrice.setId(incre);

			/*allTradePrice.add(tradePrice);
			allDiscount1.add(discount1);
			allDiscount2.add(discount2);
			allTradeOffer.add(tradeOffer);
			allScheme.add(scheme);
			allTax1.add(tax1);
			allTax2.add(tax2);
			allTax3.add(tax3);
			allSubTotal.add(subTotal);*/
            
            db.OpenDb();
            
            //ArrayList<HashMap<String, String>> data = db.getPricingDataByItemId(productId);
            ArrayList<HashMap<String, String>> data = db.getOrderDetailAgainstProduct(orderID, productId);
            double result, res, schemeQty;
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    HashMap<String, String> map = data.get(i);
                    tradePrice.setText(map.get("tradePrice"));
                    discount1.setText(map.get("discount1"));
                    discount2.setText(map.get("discount2"));
                    tradeOffer.setText(map.get("tradeOffer"));
                    res = Math.floor(Quantity / Double.parseDouble(map.get("scheme")));
                    schemeQty = Double.parseDouble(map.get("schemeVal")) * res;
                    //scheme.setText(String.format("%.0f",schemeQty) + " ( "+map.get("scheme") + " / " + map.get("schemeFormula")+" )" );
                    if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                        scheme.setText(String.format("%.0f", schemeQty) + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same");
                    } else {
                        scheme.setText(String.format("%.0f", schemeQty) + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct")));
                    }
                    tax1.setText(map.get("tax1"));
                    tax2.setText(map.get("tax2"));
                    tax3.setText(map.get("tax3"));
                    //subTotal.setText(map.get("subTotal"));
                    result = pricingCalculation(Quantity, map.get("tradePrice"), map.get("discount1"), map.get("discount2"), map.get("tradeOffer"), map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"));
                    subTotal.setText(String.format("%.2f", result));
                    // break;
                }
            }
            
            
            db.CloseDb();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private double pricingCalculation(double Quantity, String tradePrice, String discount1, String discount2, String tradeOffer, String scheme, String tax1, String tax2, String tax3) {
        
        double tp, disc1, disc2, to, schem, tax_1, tax_2, tax_3;
        double schemeValue = 0, totalpieces2, totAmount, actualTotalAmount, tradeOfferValue, discount_1, discount_2, totalTax;
        tp = Double.parseDouble(tradePrice);
        disc1 = Double.parseDouble(discount1);
        disc2 = Double.parseDouble(discount2);
        to = Double.parseDouble(tradeOffer);
        schem = Double.parseDouble(scheme);
        tax_1 = Double.parseDouble(tax1);
        tax_2 = Double.parseDouble(tax2);
        tax_3 = Double.parseDouble(tax3);
        
        if (schem != 0) {
            schemeValue = Quantity / schem;
        }
        totalpieces2 = Quantity + schemeValue;
        totAmount = Quantity * tp;
        actualTotalAmount = totalpieces2 * tp;
        tradeOfferValue = to * Quantity;
        totAmount = totAmount - tradeOfferValue;
        discount_1 = disc1 / 100;
        discount_1 = discount_1 * totAmount;
        totAmount = totAmount - discount_1;
        
        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;
        
        totalTax = (tax_1 + tax_2 + tax_3) / 100;
        actualTotalAmount = actualTotalAmount * totalTax;
        totAmount = totAmount + actualTotalAmount;
        
        
        return totAmount;
        
    }
    
    private class ViewHolder {
        TextView txtItemID;
        //TextView txtTotal2;
        TextView txtItemName;
        TextView txtOldQty;
        TextView NewQtyTxt;
        EditText NewQty;
        TextView itemDetailsTV;
        /*Button DeleteBtn;*/
        
        LinearLayout FrameLayout;
        
    }
    
    
}