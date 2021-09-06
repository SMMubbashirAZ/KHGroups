package com.blazeminds.pos;

/*
 * Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.autocomplete_resource.InputFilterMinMax;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.SalesOrderListViewAdapter.SelectedCustomerId_InSOL;
import static com.blazeminds.pos.SalesOrderListViewAdapter.SelectedCustomerTypeId;


public class EditOrderListViewAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> editOrderList;
    Activity activity;
    PosDB db;
    String OrderID;
    ListView lView;
    TableRow BtnLayout;
    
    TableRow notesRow;
    int Cases;
    TextView discountTV;
    String custType;
    String Qty;
    HashMap<String, String> map2 = new HashMap<>();
    private ArrayList<HashMap<String, String>> result;
    private double respons = 0;
    private double quantity = 0, schemeQty = 0, discount1 = 0, discount2 = 0;
    private String quantityStr = "0", discount1Str = "0", discount2Str = "0", tradeOfferValue = "0";
    private String mobEmpDiscountType;
    
    
    //double amountTxt = 0;
    
    public EditOrderListViewAdapter(Context activity, ArrayList<HashMap<String, String>> editOrderList, PosDB dba, String order_id, ListView itemList, TableRow Lay, int cases, String mobEmpDiscountType) {
        super();
        this.activity = (Activity) activity;
        this.editOrderList = editOrderList;
        this.db = dba;
        this.OrderID = order_id;
        this.lView = itemList;
        this.BtnLayout = Lay;
        this.Cases = cases;
        this.mobEmpDiscountType = mobEmpDiscountType;
        //this.totalTV = totalTV;
        // this.custType = custType;
        
    }
    
    public EditOrderListViewAdapter(Context activity, ArrayList<HashMap<String, String>> editOrderList, PosDB dba, String order_id, ListView itemList, TableRow Lay, int cases, String custType, String mobEmpDiscountType) {
        super();
        this.activity = (Activity) activity;
        this.editOrderList = editOrderList;
        this.db = dba;
        this.OrderID = order_id;
        this.lView = itemList;
        this.BtnLayout = Lay;
        this.Cases = cases;
        this.custType = custType;
        this.mobEmpDiscountType = mobEmpDiscountType;
        
        
        //this.discountTV = discountTV;
        
    }

    /*public EditOrderListViewAdapter(Context activity, ArrayList<HashMap<String, String>> editOrderList , PosDB dba, String order_id, ListView itemList, TableRow Lay,TableRow notesRow, int cases) {
		super();
        this.activity = (Activity) activity;
        this.editOrderList = editOrderList;
        this.db = dba;
        this.OrderID = order_id;
        this.lView = itemList;
        this.BtnLayout = Lay;
        this.notesRow = notesRow;
        this.Cases = cases;

    }*/
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return editOrderList.size();
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
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return editOrderList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        // TODO Auto-generated method stub
        final ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            
            if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                convertView = inflater.inflate(R.layout.layout_edit_order_new_for_english, null);
                holder = new ViewHolder();
                holder.FrameLayout = convertView.findViewById(R.id.EditLayout);
                
                holder.txtItemID = convertView.findViewById(R.id.ItemID);
                //holder.txtTotal2 = (TextView) convertView.findViewById(R.id.ItemTotal2);
                holder.txtItemName = convertView.findViewById(R.id.ItemName);
                holder.txtOldQty = convertView.findViewById(R.id.OldQty);
                holder.NewQty = convertView.findViewById(R.id.QtyTxt);
                holder.NewQtyTxt = convertView.findViewById(R.id.QtyTxtView);
                holder.itemDetailsTV = convertView.findViewById(R.id.itemDetailsTxtView);
                holder.discountTxtView = convertView.findViewById(R.id.discountTxtView);
                holder.discountTxtView2 = convertView.findViewById(R.id.discountTxtView2);
                holder.discountTxt = convertView.findViewById(R.id.discountTxt);
                holder.discount2Txt = convertView.findViewById(R.id.discount2Txt);
                holder.tradeOfferEdtTxt = convertView.findViewById(R.id.tradeOfferEdtTxt);
                holder.tradeOfferTxtView = convertView.findViewById(R.id.tradeOfferTxtView);
                
                /*holder.DeleteBtn = (Button) convertView.findViewById(R.id.DeleteOrder);*/
                
                
                switch (Cases) {
                    
                    case 1: {
                        holder.NewQty.setVisibility(View.VISIBLE);
                        holder.NewQtyTxt.setVisibility(View.GONE);
                        
                        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                            holder.discountTxt.setVisibility(View.VISIBLE);
                            holder.discount2Txt.setVisibility(View.VISIBLE);
                            holder.discountTxtView.setVisibility(View.GONE);
                            
                            
                        } else {
                            
                            holder.discount2Txt.setVisibility(View.GONE);
                            holder.discountTxt.setVisibility(View.GONE);
                            holder.discountTxtView.setVisibility(View.GONE);
                            holder.discountTxtView2.setVisibility(View.GONE);
                        }
                        
                    }
                    break;
                    
                    case 2: {
                        holder.NewQty.setVisibility(View.GONE);
                        holder.NewQtyTxt.setVisibility(View.VISIBLE);
                        
                        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                            holder.discountTxt.setVisibility(View.GONE);
                            holder.discount2Txt.setVisibility(View.GONE);
                            holder.discountTxtView.setVisibility(View.VISIBLE);
                            holder.discountTxtView2.setVisibility(View.VISIBLE);
                            
                        } else {
                            
                            holder.discountTxt.setVisibility(View.GONE);
                            holder.discount2Txt.setVisibility(View.GONE);
                            holder.discountTxtView.setVisibility(View.GONE);
                            holder.discountTxtView2.setVisibility(View.GONE);
                        }
                    }
                    break;
                }
                
                convertView.setTag(holder);
            } else {
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
                holder.discountTxtView = convertView.findViewById(R.id.discountTxtView);
                holder.discountTxt = convertView.findViewById(R.id.discountTxt);
                holder.tradeOfferEdtTxt = convertView.findViewById(R.id.tradeOfferEdtTxt);
                holder.tradeOfferTxtView = convertView.findViewById(R.id.tradeOfferTxtView);
                
                /*holder.DeleteBtn = (Button) convertView.findViewById(R.id.DeleteOrder);*/
                
                
                switch (Cases) {
                    
                    case 1: {
                        holder.NewQty.setVisibility(View.VISIBLE);
                        holder.NewQtyTxt.setVisibility(View.GONE);
                        
                        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                            holder.discountTxt.setVisibility(View.VISIBLE);
                            holder.discountTxtView.setVisibility(View.GONE);
                            
                            holder.tradeOfferEdtTxt.setVisibility(View.GONE);
                            holder.tradeOfferTxtView.setVisibility(View.GONE);
                        } else {
                            holder.tradeOfferEdtTxt.setVisibility(View.VISIBLE);
                            holder.tradeOfferTxtView.setVisibility(View.GONE);
                            
                            holder.discountTxt.setVisibility(View.GONE);
                            holder.discountTxtView.setVisibility(View.GONE);
                        }
                        
                    }
                    break;
                    
                    case 2: {
                        holder.NewQty.setVisibility(View.GONE);
                        holder.NewQtyTxt.setVisibility(View.VISIBLE);
                        
                        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                            holder.discountTxt.setVisibility(View.GONE);
                            holder.discountTxtView.setVisibility(View.VISIBLE);
                            
                            holder.tradeOfferEdtTxt.setVisibility(View.GONE);
                            holder.tradeOfferTxtView.setVisibility(View.GONE);
                        } else {
                            holder.tradeOfferEdtTxt.setVisibility(View.GONE);
                            holder.tradeOfferTxtView.setVisibility(View.VISIBLE);
                            
                            holder.discountTxt.setVisibility(View.GONE);
                            holder.discountTxtView.setVisibility(View.GONE);
                        }
                    }
                    break;
                }
                
                convertView.setTag(holder);
                
                
            }
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
            
            final HashMap<String, String> map = editOrderList.get(position);
            
            if (!map.get("productName").equals("")) {
                if (Cases == 2) {
                    
                    if (map.get("multi_scheme").equals("1")) {
                        
                        holder.FrameLayout.setBackgroundColor(Color.rgb(207, 250, 195));
                        
                    }
                }
                holder.txtItemID.setText(map.get("productId"));
                holder.txtItemName.setText(map.get("productName"));
                if (!map.get("qtyExe").equalsIgnoreCase(""))
                    quantity = Double.parseDouble(map.get("qtyExe"));
                if (!map.get("schemeQty").equalsIgnoreCase(""))
                    schemeQty = Double.parseDouble(map.get("schemeQty"));
                
                holder.txtOldQty.setText(String.format("%.2f", Double.parseDouble((map.get("qty"))/* - schemeQty*/)));
                //holder.txtTotal2.setText(map.get(EDIT_ORDER_TOTAL2).toString());
                holder.NewQty.setText(String.format("%.2f", (quantity/* - schemeQty*/)));
                holder.NewQtyTxt.setText(String.format("%.2f", (quantity/* - schemeQty*/)));
                
                
                if (mobEmpDiscountType.equals("1")) {
                    holder.discountTxtView.setText(map.get("discount1"));
                    holder.discountTxt.setText(map.get("discount1"));
                    holder.discountTxtView2.setText(map.get("discount2"));
                    holder.discount2Txt.setText(map.get("discount2"));
                    
                } else {
                    holder.tradeOfferEdtTxt.setText(map.get("tradeOffer"));
                    holder.tradeOfferTxtView.setText(map.get("tradeOffer"));
                    
                }
                
                db.OpenDb();
                int minDiscount = db.getMobUserMinDiscount();
                int maxDiscount = db.getMobUserMaxDiscount();
                db.CloseDb();
                if (db.getAppSettingsValueByKey("en_percent_discount") != 0){
              
         
                }
                else {
                    holder.discountTxt.setFilters(new InputFilter[]{new InputFilterMinMax(String.valueOf(minDiscount), String.valueOf(maxDiscount))});
                    holder.discount2Txt.setFilters(new InputFilter[]{new InputFilterMinMax(String.valueOf(0), String.valueOf(10))});
                }
            }
            
            
          
            holder.itemDetailsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                    
                    if (!editOrderList.get(position).get("qtyExe").equalsIgnoreCase(""))
                        quantity = Double.parseDouble(editOrderList.get(position).get("qtyExe"));
                    if (!map.get("schemeQty").equalsIgnoreCase(""))
                        schemeQty = Double.parseDouble(map.get("schemeQty"));
                    quantityStr = String.valueOf(quantity/* - schemeQty*/);
                    if (editOrderList.get(position).get("discount1") != null) {
                        if (!editOrderList.get(position).get("discount1").equalsIgnoreCase(""))
                            discount1 = Double.parseDouble(editOrderList.get(position).get("discount1"));
                    }
                    discount1Str = String.valueOf(discount1);
                    if (editOrderList.get(position).get("discount2") != null) {
                        if (!editOrderList.get(position).get("discount2").equalsIgnoreCase(""))
                            discount2 = Double.parseDouble(editOrderList.get(position).get(
                                    "discount2"));
                    }
                    discount2Str = String.valueOf(discount2);
                    if (editOrderList.get(position).get("tradeOffer") != null) {
                        if (!editOrderList.get(position).get("tradeOffer").equals("")) {
                            tradeOfferValue = editOrderList.get(position).get("tradeOffer");
                        }
                    }
                    Log.e("GetPosition", String.valueOf(editOrderList.get(position).get("productId")));
                    initiatePopupWindow2(view, editOrderList.get(position).get("productId"),
                            quantityStr, discount1Str, discount2Str, OrderID, tradeOfferValue);
                }
            });
            
            
            holder.NewQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.e("hasFocus", "!hasFocus");
                    //holder.NewQty.setFocusable(true);
                    
                    holder.NewQty.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        
                        }
                        
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                Log.e("PrintText", charSequence.toString());
                                //editOrderList.get(position).put("qty",String.valueOf(charSequence));
                                editOrderList.get(position).put("qtyExe", String.valueOf(charSequence));
                            } catch (Exception e) {
                                Log.e("hasFocusError", e.getMessage());
                                map.put("qty", "0");
                                map.put("qtyExe", "0");
                            }
                        }
                        
                        @Override
                        public void afterTextChanged(Editable editable) {
                        
                        }
                    });
                    
                    
                }
            });
            
            
            holder.discountTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    
                    holder.discountTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        
                        }
                        
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                Log.e("PrintText", charSequence.toString());
                                editOrderList.get(position).put("discount1", String.valueOf(charSequence));
                            } catch (Exception e) {
                                map.put("discount1", "0");
                            }
                        }
                        
                        @Override
                        public void afterTextChanged(Editable editable) {
                        
                        }
                    });
                    
                    
                }
            });
            
            holder.discount2Txt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    
                    holder.discount2Txt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        
                        }
                        
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                Log.e("PrintText", charSequence.toString());
                                editOrderList.get(position).put("discount2",
                                        String.valueOf(charSequence));
                            } catch (Exception e) {
                                map.put("discount2", "0");
                            }
                        }
                        
                        @Override
                        public void afterTextChanged(Editable editable) {
                        
                        }
                    });
                    
                    
                }
            });
            
            
        } else {
            
            final HashMap<String, String> map = editOrderList.get(position);
            
            
            if (!map.get("productName").equals("")) {
                
                if (Cases == 2) {
                    
                    if (map.get("multi_scheme").equals("1")) {
                        
                        holder.FrameLayout.setBackgroundColor(Color.rgb(207, 250, 195));
                        
                    }
                }
                holder.txtItemID.setText(map.get("productId"));
                holder.txtItemName.setText(map.get("productName"));
                if (!map.get("qtyExe").equalsIgnoreCase(""))
                    quantity = Double.parseDouble(map.get("qtyExe"));
                if (!map.get("schemeQty").equalsIgnoreCase(""))
                    schemeQty = Double.parseDouble(map.get("schemeQty"));
                
                holder.txtOldQty.setText(String.format("%.2f", Double.parseDouble((map.get("qty"))/* - schemeQty*/)));
                //holder.txtTotal2.setText(map.get(EDIT_ORDER_TOTAL2).toString());
                holder.NewQty.setText(String.format("%.2f", (quantity/* - schemeQty*/)));
                holder.NewQtyTxt.setText(String.format("%.2f", (quantity/* - schemeQty*/)));
                
                
                if (mobEmpDiscountType.equals("1")) {
                    holder.discountTxtView.setText(map.get("discount1"));
                    holder.discountTxt.setText(map.get("discount1"));
                    
                } else {
                    holder.tradeOfferEdtTxt.setText(map.get("tradeOffer"));
                    holder.tradeOfferTxtView.setText(map.get("tradeOffer"));
                    
                }
                
                db.OpenDb();
                int minDiscount = db.getMobUserMinDiscount();
                int maxDiscount = db.getMobUserMaxDiscount();
                db.CloseDb();
                if (db.getAppSettingsValueByKey("en_percent_discount") != 0){
    
                
                }
                else{
                    holder.discountTxt.setFilters(new InputFilter[]{new InputFilterMinMax(String.valueOf(minDiscount), String.valueOf(maxDiscount))});
                }
                
                
            }
            
            
            holder.itemDetailsTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    
                    
                    if (!editOrderList.get(position).get("qtyExe").equalsIgnoreCase(""))
                        quantity = Double.parseDouble(editOrderList.get(position).get("qtyExe"));
                    if (!map.get("schemeQty").equalsIgnoreCase(""))
                        schemeQty = Double.parseDouble(editOrderList.get(position).get("schemeQty"));
                    quantityStr = String.valueOf(quantity/* - schemeQty*/);
                    if (editOrderList.get(position).get("discount1") != null) {
                        if (!editOrderList.get(position).get("discount1").equalsIgnoreCase(""))
                            discount1 = Double.parseDouble(editOrderList.get(position).get("discount1"));
                    }
                    discount1Str = String.valueOf(discount1);
                    if (editOrderList.get(position).get("tradeOffer") != null) {
                        if (!editOrderList.get(position).get("tradeOffer").equals("")) {
                            tradeOfferValue = editOrderList.get(position).get("tradeOffer");
                        }
                    }
                    Log.e("GetPosition", String.valueOf(editOrderList.get(position).get("productId")));
                    initiatePopupWindow(view, editOrderList.get(position).get("productId"), quantityStr, discount1Str, OrderID, tradeOfferValue);
                }
            });
            
            
            holder.NewQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Log.e("hasFocus", "!hasFocus");
                    //holder.NewQty.setFocusable(true);
                    
                    holder.NewQty.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        
                        }
                        
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                Log.e("PrintText", charSequence.toString());
                                //editOrderList.get(position).put("qty",String.valueOf(charSequence));
                                editOrderList.get(position).put("qtyExe", String.valueOf(charSequence));
                            } catch (Exception e) {
                                Log.e("hasFocusError", e.getMessage());
                            }
                        }
                        
                        @Override
                        public void afterTextChanged(Editable editable) {
                        
                        }
                    });
                    
                }
            });
            
            
            holder.discountTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    
                    holder.discountTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        
                        }
                        
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                Log.e("PrintText", charSequence.toString());
                                editOrderList.get(position).put("discount1", String.valueOf(charSequence));
                            } catch (Exception e) {
                            
                            }
                        }
                        
                        @Override
                        public void afterTextChanged(Editable editable) {
                        
                        }
                    });
                    
                    
                }
            });
            
            holder.tradeOfferEdtTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    
                    holder.tradeOfferEdtTxt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        
                        }
                        
                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            try {
                                if( Double.parseDouble(holder.tradeOfferEdtTxt.getText().toString())>     db.  getSelectedItemPrice( editOrderList.get(position).get("productId"))) {
                                    holder.tradeOfferEdtTxt.setText( String.valueOf(db.  getSelectedItemPrice( editOrderList.get(position).get("productId"))));
    
                                    Toast.makeText(activity, "T.O cannot be more than product price.", Toast.LENGTH_LONG).show();
                            }
                                else {
                                    editOrderList.get(position).put("tradeOffer", holder.tradeOfferEdtTxt.getText().toString());
                                }
                            } catch (Exception e) {
                            
                            }
                        }
                        
                        @Override
                        public void afterTextChanged(Editable editable) {
                        
                        }
                    });
                    
                    
                }
            });
        
        /*
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
                        } else if (holder.discountTxt.isFocused()) {
                            Rect outRect = new Rect();
                            holder.discountTxt.getGlobalVisibleRect(outRect);
                            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                                holder.discountTxt.clearFocus();
                                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                        }
                    }
                    return false;
                }
            });*/
        }
        
        
        return convertView;
    }
    
    private void initiatePopupWindow(View v, String productId, String quantity, String discount1Str, String orderID, String tradeOfferValue/*, final String customerTypeId, final String productId, int incre*/) {
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
            final TableRow discount1Row = popUpView.findViewById(R.id.discount1Row);
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
            //discount1Row.setVisibility(View.GONE);
            
            db.OpenDb();
            
            //ArrayList<HashMap<String, String>> data = db.getPricingDataByItemId(productId);
            Log.e("GetDataSize1", orderID + " : " + productId);
            ArrayList<HashMap<String, String>> data;
            String[] val = db.getBrandAndProductTypeAndSubType(productId);
            
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId_InSOL);
            
            data = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId_InSOL, val[1], val[0], quantity,
                    productId, val1[0], val1[1], val[2]);
            
            double result, res = 0, schemeQty, schem;
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    HashMap<String, String> map = data.get(i);
                    tradePrice.setText(map.get("tradePrice"));
                    discount1.setText(discount1Str);
                    discount2.setText(map.get("discount2"));
                    if (mobEmpDiscountType.equalsIgnoreCase("1") && (tradeOfferValue==null || tradeOfferValue.equals("") || tradeOfferValue.equals("0"))) {
                        tradeOffer.setText(map.get("tradeOffer"));
                    } else {
                        tradeOffer.setText(tradeOfferValue);
                    }
                    schem = Double.parseDouble(map.get("scheme"));
                    if (schem != 0) {
                        res = Math.floor(Quantity / schem/*Double.parseDouble(map.get("scheme"))*/);
                    }
                    schemeQty = Double.parseDouble(map.get("schemeVal")) * res;
                    if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                        scheme.setText(String.format("%.0f", schemeQty) + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same");
                    } else {
                        scheme.setText(String.format("%.0f", schemeQty) + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct")));
                    }
                    
                    
//                    tax1.setText(map.get("tax1"));
//                    tax2.setText(map.get("tax2"));
//                    tax3.setText(map.get("tax3"));
    
                    tax1.setText(db.getTaxOneByOrderId(OrderID));
                    tax2.setText(db.getTaxTwoByOrderId(OrderID));
                    tax3.setText(db.getTaxThreeByOrderId(OrderID));
                    
                    
                    //subTotal.setText(map.get("subTotal"));
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),
                                discount1Str
                                , map.get("discount2"), map.get("tradeOffer"), map.get("scheme"),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                    } else {
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"), map.get(
                                "discount1"), map.get("discount2"), tradeOfferValue, map.get("scheme"), tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                    }
                    subTotal.setText(String.format("%.2f", result));
                    // break;
                }
            } else {
                
                
                ArrayList<HashMap<String, String>> getPricing = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId_InSOL, val[1], val[0],
                        quantity,
                        productId, val1[0], val1[1], val[2]);
                
                Log.e("GetPopupSize", String.valueOf(getPricing.size()));
                
                if (getPricing.size() > 0) {
                    for (int i = 0; i < getPricing.size(); i++) {
                        HashMap<String, String> map = getPricing.get(i);
                        tradePrice.setText(map.get("tradePrice"));
                        discount1.setText(map.get("discount1"));
                        discount2.setText(map.get("discount2"));
                        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                            tradeOffer.setText(map.get("tradeOffer"));
                        } else {
                            tradeOffer.setText(tradeOfferValue);
                        }
                        //scheme.setText(schemeValHidden + " ( "+map.get("scheme") + " / " + map.get("schemeVal") +" )");
                        if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                            scheme.setText("0" + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same");
                        } else {
                            scheme.setText("0" + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct")));
                        }
                        
                        
//                        if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 0) {
//                            tax1.setText(map.get("tax1"));
//                            tax2.setText(map.get("tax2"));
//                            tax3.setText(map.get("tax3"));
//                        } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 1) {
//                            tax1.setText(map.get("tax_filer_1"));
//                            tax2.setText(map.get("tax_filer_2"));
//                            tax3.setText(map.get("tax_filer_3"));
//                        }
    
                        tax1.setText(db.getTaxOneByOrderId(OrderID));
                        tax2.setText(db.getTaxTwoByOrderId(OrderID));
                        tax3.setText(db.getTaxThreeByOrderId(OrderID));
                        if (mobEmpDiscountType.equalsIgnoreCase("1"))
                            result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map
                            .get("discount1")*/discount1Str, map.get("discount2"), map.get("tradeOffer"), map.get("scheme"), tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                        else
                            result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map
                            .get("discount1")*/map.get("discount1"), map.get("discount2"), tradeOfferValue, map.get("scheme"), tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                        
                        //double res = 10 * Double.parseDouble(map.get("tradePrice")) * Double.parseDouble(map.get("discount1")) * Double.parseDouble(map.get("discount2")) * Double.parseDouble(map.get("tradeOffer")) * Double.parseDouble( map.get("scheme")) * Double.parseDouble(map.get("tax1")) * Double.parseDouble(map.get("tax2"))* Double.parseDouble(map.get("tax3"));
                        subTotal.setText(String.format("%.2f", result));
                        //break;
                    }
                }
                
                
                db.CloseDb();
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    private void initiatePopupWindow2(View v, String productId, String quantity,
                                      String discount1Str, String discount1Str2, String orderID,
                                      String tradeOfferValue/*, final String customerTypeId, final String productId, int incre*/) {
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
            final TableRow discount1Row = popUpView.findViewById(R.id.discount1Row);
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
            //discount1Row.setVisibility(View.GONE);
            
            db.OpenDb();
            
            //ArrayList<HashMap<String, String>> data = db.getPricingDataByItemId(productId);
            Log.e("GetDataSize1", orderID + " : " + productId);
            /*ArrayList<HashMap<String, String>> data = db.getOrderDetailAgainstProduct(orderID, productId);*/
            ArrayList<HashMap<String, String>> data;
            String[] val = db.getBrandAndProductTypeAndSubType(productId);
            
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId_InSOL);
            
            data = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId_InSOL, val[1], val[0], quantity,
                    productId, val1[0], val1[1], val[2]);
            
            double result, res = 0, schemeQty, schem;
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    HashMap<String, String> map = data.get(i);
                    tradePrice.setText(map.get("tradePrice"));
                    discount1.setText(discount1Str);
                    discount2.setText(discount1Str2);
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        tradeOffer.setText(map.get("tradeOffer"));
                    } else {
                        tradeOffer.setText(tradeOfferValue);
                    }
                    schem = Double.parseDouble(map.get("scheme"));
                    if (schem != 0) {
                        res = Math.floor(Quantity / schem/*Double.parseDouble(map.get("scheme"))*/);
                    }
                    schemeQty = Double.parseDouble(map.get("schemeVal")) * res;
                    if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                        scheme.setText(String.format("%.0f", schemeQty) + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same");
                    } else {
                        scheme.setText(String.format("%.0f", schemeQty) + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct")));
                    }
                    /*    if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 0) {
                     */
//                    tax1.setText(map.get("tax1"));
//                    tax2.setText(map.get("tax2"));
//                    tax3.setText(map.get("tax3"));
                    tax1.setText(db.getTaxOneByOrderId(OrderID));
                    tax2.setText(db.getTaxTwoByOrderId(OrderID));
                    tax3.setText(db.getTaxThreeByOrderId(OrderID));
                  /*  } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 1) {
                        tax1.setText(map.get("tax_filer_1").equals("")?"0":map.get("tax_filer_1"));
                        tax2.setText(map.get("tax_filer_2").equals("")?"0":map.get("tax_filer_2"));
                        tax3.setText(map.get("tax_filer_3").equals("")?"0":map.get("tax_filer_3"));
                    }*/
                    //subTotal.setText(map.get("subTotal"));
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"), discount1Str, discount1Str2, map.get("tradeOffer"), map.get("scheme"), tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                    } else {
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"), discount1Str2, map.get("discount2"), tradeOfferValue, map.get("scheme"),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                    }
                    subTotal.setText(String.format("%.2f", result));
                    // break;
                }
            } else {
                
                
                ArrayList<HashMap<String, String>> getPricing = db.getPricingData(SelectedCustomerTypeId, SelectedCustomerId_InSOL, val[1], val[0], quantity,
                        productId, val1[0], val1[1], val[2]);
                
                Log.e("GetPopupSize", String.valueOf(getPricing.size()));
                
                if (getPricing.size() > 0) {
                    for (int i = 0; i < getPricing.size(); i++) {
                        HashMap<String, String> map = getPricing.get(i);
                        tradePrice.setText(map.get("tradePrice"));
                        discount1.setText(map.get("discount1"));
                        discount2.setText(map.get("discount2"));
                        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                            tradeOffer.setText(map.get("tradeOffer"));
                        } else {
                            tradeOffer.setText(tradeOfferValue);
                        }
                        //scheme.setText(schemeValHidden + " ( "+map.get("scheme") + " / " + map.get("schemeVal") +" )");
                        if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                            scheme.setText("0" + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same");
                        } else {
                            scheme.setText("0" + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct")));
                        }
//                        if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 0) {
//
//                            tax1.setText(map.get("tax1"));
//                            tax2.setText(map.get("tax2"));
//                            tax3.setText(map.get("tax3"));
//                        } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 1) {
//                            tax1.setText(map.get("tax_filer_1"));
//                            tax2.setText(map.get("tax_filer_2"));
//                            tax3.setText(map.get("tax_filer_3"));
//                        }
                        tax1.setText(db.getTaxOneByOrderId(OrderID));
                        tax2.setText(db.getTaxTwoByOrderId(OrderID));
                        tax3.setText(db.getTaxThreeByOrderId(OrderID));
                        if (mobEmpDiscountType.equalsIgnoreCase("1"))
                            result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get("discount1")*/discount1Str, map.get("discount2"), map.get("tradeOffer"), map.get("scheme"),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                        else
                            result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get("discount1")*/map.get("discount1"), map.get("discount2"), tradeOfferValue, map.get("scheme"),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString(),tax1.getText().toString(),tax2.getText().toString(),tax3.getText().toString());
                        
                        //double res = 10 * Double.parseDouble(map.get("tradePrice")) * Double.parseDouble(map.get("discount1")) * Double.parseDouble(map.get("discount2")) * Double.parseDouble(map.get("tradeOffer")) * Double.parseDouble( map.get("scheme")) * Double.parseDouble(map.get("tax1")) * Double.parseDouble(map.get("tax2"))* Double.parseDouble(map.get("tax3"));
                        subTotal.setText(String.format("%.2f", result));
                        //break;
                    }
                }
                
                
                db.CloseDb();
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    private double pricingCalculation(HashMap<String, String> dataMap,
                                      double Quantity,
                                      String tradePrice,
                                      String discount1,
                                      String discount2, String tradeOffer, String scheme,
                                      String tax1, String tax2, String tax3, String taxf1,
                                      String taxf2, String taxf3) {
        
        double tp, disc1, disc2, to, schem, tax_1, tax_2, tax_3, tax_f_1, tax_f_2, tax_f_3;
        double schemeValue = 0, totalpieces2, totAmount, actualTotalAmount, tradeOfferValue,
                discount_1, discount_11, discount_2, discount_22, totalTax, afterAmmount,
                afterAmmountMrp, afterAmmountMrp1;
        tp
                =
                Double.parseDouble(tradePrice);
        disc1
                =
                Double.parseDouble(discount1);
        disc2
                =
                Double.parseDouble(discount2);
        if
        (tradeOffer.equalsIgnoreCase("")) {
            to = 0;
        } else
            to = Double.parseDouble(tradeOffer);
        schem = Double.parseDouble(scheme);
        int isTaxMrp = Integer.parseInt(dataMap.get("is_taxable"));
        int tax_mrp = Integer.parseInt(dataMap.get("tax_mrp"));
        tax_1 = Double.parseDouble(tax1);
        tax_2 = Double.parseDouble(tax2);
        tax_3 = Double.parseDouble(tax3);
        
        tax_f_1 = Double.parseDouble(taxf1);
        tax_f_2 = Double.parseDouble(taxf2);
        tax_f_3 = Double.parseDouble(taxf3);
        
        if (schem != 0) {
            schemeValue = Quantity / schem;
        }
        
        totalpieces2 = Quantity + schemeValue;
        
        
        totAmount = Quantity * tp;
        
        actualTotalAmount = totalpieces2 * tp;
        tradeOfferValue = to * Quantity;
        
        totAmount = totAmount - tradeOfferValue;
        afterAmmount = totAmount;
        discount_1 = disc1 / 100;
        discount_1 = discount_1 * totAmount;
        totAmount = totAmount - discount_1;
        
        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;
        
        if (isTaxMrp == 2) {
            
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            
            if (tax_mrp == 0) {
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 0) {
                    
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    totAmount = totAmount + actualTotalAmount;
                    
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 1) {
                    
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    totAmount = totAmount + actualTotalAmount;
                    
                }
                
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
                    totAmount = totAmount + actualTotalAmount;
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
                    totAmount = totAmount + actualTotalAmount;
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }
            
            
        } else if (isTaxMrp == 3) {
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            if (tax_mrp == 0) {
                
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    
                    totAmount = totAmount + (totalTax * totAmount);
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    
                    totAmount = totAmount + (totalTax * totAmount);
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {
                
                
                afterAmmountMrp = Quantity * (Integer.parseInt(dataMap.get("mrp_price")) - to);
                
                discount_11 = disc1 / 100;
                discount_11 = discount_11 * afterAmmountMrp;
                afterAmmountMrp1 = afterAmmountMrp - discount_11;
                
                discount_22 = disc2 / 100;
                discount_22 = discount_22 * afterAmmountMrp1;
                afterAmmountMrp1 = afterAmmountMrp1 - discount_22;
                
         /*       actualTotalAmount = (afterAmmountMrp) - discount_1;
    
                actualTotalAmount = actualTotalAmount - discount_2;*/
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 0) {
                    
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    
                    
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
                    
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSOL) == 1) {
                    
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    
                    
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
                    
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }
            
            
        }
        Log.e("pricingCalculation", totAmount + "");
        return totAmount;
        
    }
    
    private class ViewHolder {
        TextView txtItemID;
        //TextView txtTotal2;
        TextView txtItemName;
        TextView txtOldQty;
        TextView NewQtyTxt;
        EditText NewQty;
        /*Button DeleteBtn;*/
        // Umais here
        ImageButton itemDetailsTV;
        EditText discountTxt, discount2Txt;
        TextView discountTxtView, discountTxtView2;
        // End Umais
        EditText tradeOfferEdtTxt;
        TextView tradeOfferTxtView;
        LinearLayout FrameLayout;
        
        
    }
    
    
}