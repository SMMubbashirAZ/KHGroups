package com.blazeminds.pos;

/*
  Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.adapter.SingleProductFilterRecylarAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.likebamboo.widget.SwipeListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.blazeminds.pos.Constant.AreYouSure;
import static com.blazeminds.pos.Constant.EDIT_ORDER_ITEM;
import static com.blazeminds.pos.Constant.EDIT_ORDER_ITEM_DETAILS;
import static com.blazeminds.pos.Constant.EDIT_ORDER_ITEM_DISCOUNT;
import static com.blazeminds.pos.Constant.EDIT_ORDER_NEWQTY;
import static com.blazeminds.pos.Constant.EDIT_ORDER_OLDQTY;
import static com.blazeminds.pos.Constant.ORDER_CUST_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_DATE_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_EXE_COMPLETE;
import static com.blazeminds.pos.Constant.ORDER_ID_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_NEW_AMOUNT_COLUMN;
import static com.blazeminds.pos.Constant.getDateTimeSHORT;
import static com.blazeminds.pos.Constant.populateReturnOrderList;


public class SalesReturnListViewAdapter extends BaseAdapter {
    public static String SelectedCustomerId_InSRD = "0";
    public static String SelectedCustomerTypeId_InSRD = "0";
    public ArrayList<HashMap<String, String>> list;
    public FilterWithSpaceAdapter<String> myAdapter2;
    public String[] item2 = new String[]{"Please search..."};
    private SingleProductFilterRecylarAdapter filterProductAdapter;
    Activity activity;
    PosDB db;
    String totalAmount = "";
    double totAmount = 0, discount = 0;
    SwipeListView lView;
    TextView noItemTxt;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    String SelectedProductId = "0";
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    String mobEmpDiscountType = "";
    ArrayList<HashMap<String, String>> data;
    int position1 = -2;
    private ArrayList<HashMap<String, String>> tempList;
    private EditReturnListViewAdapter adapter1;
    private double totalTemp = 0;
    private String pName = "";
    private int mRightWidth = 0;
    private String p_name, p_qty, p_disc, p_disc2, p_Id;
    private double t_o_v = 0, d_v_1 = 0, d_v_2 = 0, t_type = 0, t_mrp_type = 0, t_val = 0;
    private ArrayList<HashMap<String, String>> schemeHolder;
    private Dialog schemeDialog;
    private ShowSchemeListAdapter showSchemeListAdapter;
    
    public SalesReturnListViewAdapter(Context activity, int rightWidth, ArrayList<HashMap<String, String>> list, PosDB dba, SwipeListView listView, TextView NoItem) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        this.db = dba;
        this.lView = listView;
        this.noItemTxt = NoItem;
        mRightWidth = rightWidth;
        tempList = new ArrayList<>();
        
        schemeDialog = new Dialog(activity);
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    
    @Override
    public int getViewTypeCount() {
        
        if (getCount() > 0)
            
            return getCount();
        else
            return 1;
    }
    
    @Override
    public int getItemViewType(int position) {
        
        return position;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        
        // TODO Auto-generated method stub
        final ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();
        
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_row_swipe_issue, null);
            holder = new ViewHolder();
            
            holder.txtDate = convertView.findViewById(R.id.DateTxt);
            holder.txtOrderID = convertView.findViewById(R.id.OrderID);
            holder.txtCustomerName = convertView.findViewById(R.id.CustomerNameTxt);
            /*holder.txtTotalAmount = (TextView) convertView.findViewById(R.id.TotalAmountTxt);*/
            holder.txtTotalNewAmount = convertView.findViewById(R.id.TotalNewAmountTxt);
            
            holder.EditOrder = convertView.findViewById(R.id.EditOrderBtn);
            holder.DeleteOrder = convertView.findViewById(R.id.DeleteOrderBtn);
            holder.ViewOrder = convertView.findViewById(R.id.ViewOrderBtn);
            
            holder.item_left = convertView.findViewById(R.id.item_left);
            holder.item_right = convertView.findViewById(R.id.item_right);
            holder.MainLayout = convertView.findViewById(R.id.CustomSwipeLayout);
            
            
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            holder.item_left.setLayoutParams(lp1);
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(mRightWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            holder.item_right.setLayoutParams(lp2);
			/*holder.item_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText( activity, "right onclick " + position,Toast.LENGTH_SHORT).show();


                }
            });*/
            
            
            convertView.setTag(holder);
            
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        final HashMap map = list.get(position);
        
        try {
            
            if (map.get(ORDER_EXE_COMPLETE).toString().equals("1")) {
                holder.item_left.setBackgroundColor(activity.getResources().getColor(R.color.green_back));
            } else {
                holder.item_left.setBackgroundColor(activity.getResources().getColor(R.color.mdtp_white));
            }
            
            holder.txtOrderID.setText(map.get(ORDER_ID_COLUMN).toString());
            String dbDate = map.get(ORDER_DATE_COLUMN).toString();
            holder.txtCustomerName.setText(map.get(ORDER_CUST_COLUMN).toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.txtTotalNewAmount.setText(map.get(ORDER_NEW_AMOUNT_COLUMN).toString());
            holder.custType = map.get("cust_type").toString();
            
            SelectedCustomerTypeId_InSRD = map.get("cust_type").toString();
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
            DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
            Date dt = null;
            try {
                dt = df.parse(dbDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            
            holder.txtDate.setText(dfProper.format(dt));
            
            
            holder.DateTimeLong = dt.getTime();
            final double daysPassed = CheckDays(dt.getTime());
            
            holder.EditOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    if (map.get(ORDER_EXE_COMPLETE).toString().equalsIgnoreCase("0")) {
                        
                        if (daysPassed >= 7) {
                            Toast.makeText(AppContextProvider.getContext(), "Edit Time Exceeded", Toast.LENGTH_SHORT).show();
                        } else {
                            SelectedCustomerId_InSRD = list.get(position).get("cust_id");
                            SelectedCustomerTypeId_InSRD = list.get(position).get("cust_type");
                            EditOrder(activity, db, holder.txtOrderID.getText().toString(), lView, noItemTxt, holder.custType);
                        }
                    } else {
                        Toast.makeText(AppContextProvider.getContext(), "Can't Edit.\nSale Return Executed", Toast.LENGTH_SHORT).show();
                        
                    }
                    
                }
            });
            
            holder.DeleteOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AreYouSure(activity, db, 3, holder.txtOrderID.getText().toString(), lView, noItemTxt);
                }
            });
            
            holder.ViewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectedCustomerId_InSRD = list.get(position).get("cust_id");
                    SelectedCustomerTypeId_InSRD = list.get(position).get("cust_type");
                    ViewOrder(activity, db, holder.txtOrderID.getText().toString(), lView, noItemTxt);
                    
                }
            });
            
        } catch (NullPointerException e) {
            System.out.println("Error Sales Order ListAdapter: " + e.toString());
        }
        return convertView;
    }
    
    public double CheckDays(long Last) {
        
        
        long Current = db.getCurrentTime();
        
        long diff = Current - Last;
        
        
        long timeInSeconds = diff / 1000;
        long hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        
        
        double day = Math.abs(hours / 24);
        
        
        return day;
        
    }
    
    public void EditOrder(final Context ctx, final PosDB db, final String orderID, final SwipeListView lView, final TextView noItemTxt, final String customerType) {
        
        final Dialog dialog = new Dialog(ctx);
        mobEmpDiscountType = db.getMobEmpDiscountType();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_edit_order);
        data = db.getOrderDetailReturnForEditList(orderID);

/*
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
*/
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT); //set below the setContentview
        
        //final RelativeLayout DialogLayout = dialog.findViewById(R.id.DialogLayout);
        
        TextView CustomerName = dialog.findViewById(R.id.CustomerName);
        TextView DateTxt = dialog.findViewById(R.id.Date);
        TextView Total2 = dialog.findViewById(R.id.Total);
        final TextView discountTxt = dialog.findViewById(R.id.discountTxt);
        final ListView ItemList = dialog.findViewById(R.id.ItemList);
        ListView ItemListHeader = dialog.findViewById(R.id.ItemListH);
        final Button Update = dialog.findViewById(R.id.UpdateOrder);
        Button Dismiss = dialog.findViewById(R.id.Dismiss);
        final TextView notesTV = dialog.findViewById(R.id.notesTxtView);
        final TableRow BtnLay = dialog.findViewById(R.id.ButtonLayout);
        final ScrollView scrollView = dialog.findViewById(R.id.popup_scrollview);
        
        /*Abdul Begin*/
        
        ItemList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.requestDisallowInterceptTouchEvent(true);
                int action = event.getActionMasked();
                
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        
        final AutoCompleteTextView etProduct, etProduct2;
        final EditText etQuantity, etQuantity2;
        final Spinner etDisc, etDisc2, etDisc22;
        final EditText tradeOffer;
        TextView tvDetails, tvDetails2;
        final Button btnAdd;
        final LinearLayout l1;
        final TableRow l2;
        
        l1 = dialog.findViewById(R.id.default_edit_l1);
        l2 = dialog.findViewById(R.id.default_edit_l2_english);
        etProduct = dialog.findViewById(R.id.popup_edit_add_item);
        etQuantity = dialog.findViewById(R.id.popup_edit_add_qty);
        etProduct2 = dialog.findViewById(R.id.popup_edit_add_item1);
        etQuantity2 = dialog.findViewById(R.id.popup_edit_add_qty1);
        etDisc = dialog.findViewById(R.id.popup_edit_add_disc);
        etDisc2 = dialog.findViewById(R.id.popup_edit_add_disc1);
        etDisc22 = dialog.findViewById(R.id.popup_edit_add_disc2);
        tradeOffer = dialog.findViewById(R.id.popup_edit_add_trade_offer);
        tvDetails = dialog.findViewById(R.id.popup_edit_details_show);
        tvDetails2 = dialog.findViewById(R.id.popup_edit_details_show1);
        btnAdd = dialog.findViewById(R.id.popup_edit_submit_item);
	  /*  etProduct.setEnabled(false);
		etQuantity.setEnabled(false);
        etDisc.setEnabled(false);
        tradeOffer.setEnabled(false);
        tvDetails.setEnabled(false);*/
        if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
            
            l1.setVisibility(View.GONE);
            l2.setVisibility(View.VISIBLE);
        } else {
            l1.setVisibility(View.VISIBLE);
            l2.setVisibility(View.GONE);
        }
        
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            tradeOffer.setVisibility(View.GONE);
            etDisc.setVisibility(View.VISIBLE);
            if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                etDisc2.setVisibility(View.VISIBLE);
                etDisc22.setVisibility(View.VISIBLE);
            }
            Log.e("discount", "0");
            
        } else {
            tradeOffer.setVisibility(View.VISIBLE);
            etDisc.setVisibility(View.GONE);
            Log.e("discount", "2");
        }
        
        ArrayList<String> drop = new ArrayList<>();
        //drop.add("Disc");
        for (int i = 0; i <= db.getMobUserMaxDiscount(); i++) {
            drop.add(String.valueOf(i));
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, drop);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etDisc.setAdapter(adapter);
        ArrayList<String> drop2 = new ArrayList<>();
        //drop.add("Disc");
        for (int i = 0; i <= 10; i++) {
            drop2.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, drop2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        etDisc2.setAdapter(adapter);
        etDisc22.setAdapter(adapter2);
        
        myAdapter2 = new FilterWithSpaceAdapter<>(activity, R.layout.layout_custom_spinner, R.id.item, item2);
        etProduct.setFocusable(false);

        etProduct.setHint("Select Product");
        etProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etProduct.setText("");
                etProduct.setError(null);
                pName="";
                openSearchProductFilter(etProduct,db);
            }
        });
        etProduct2.setFocusable(false);

        etProduct2.setHint("Select Product");
        etProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etProduct2.setText("");
                etProduct.setError(null);
                pName="";
                openSearchProductFilter(etProduct2,db);
            }
        });
      /*  etProduct.setAdapter(myAdapter2);
        
        etProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                
                db.OpenDb();
                myAdapter2 = new FilterWithSpaceAdapter<>(activity, R.layout.layout_custom_spinner, R.id.item, item2);
                etProduct.setAdapter(myAdapter2);
                myAdapter2.notifyDataSetChanged();
                
                // query the database based on the user input
                
                item2 = getInventoryNameFromDb(Constant.testInput(userInput.toString()), db);
                
                //discountDrop.setSelection(0, true);
                
                db.CloseDb();
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            
            }
        });
        
        etProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pName = adapterView.getItemAtPosition(i).toString();
            }
        });
        
        
        etProduct2.setAdapter(myAdapter2);
        
        etProduct2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            
            }
            
            @Override
            public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                
                db.OpenDb();
                myAdapter2 = new FilterWithSpaceAdapter<>(activity, R.layout.layout_custom_spinner, R.id.item, item2);
                etProduct2.setAdapter(myAdapter2);
                myAdapter2.notifyDataSetChanged();
                
                // query the database based on the user input
                
                item2 = getInventoryNameFromDb(Constant.testInput(userInput.toString()), db);
                
                //discountDrop.setSelection(0, true);
                
                db.CloseDb();
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
            
            }
        });
        
        etProduct2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pName = adapterView.getItemAtPosition(i).toString();
            }
        });
        */
        
        tvDetails2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout parent = (LinearLayout) view.getParent();
                View discount = null, tradeOffer = null;
                EditText discount1 = null;
                String discountValue = "0", discountValue2 = "0";
                EditText tradeOfferEdtTxt;
                String tradeOfferValue = "0";
                
                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                    
                    discountValue = etDisc2.getSelectedItem().toString();
                    
                    discountValue2 = etDisc22.getSelectedItem().toString();
                    
                    
                } else {
                    tradeOffer = parent.getChildAt(2);
                    tradeOfferEdtTxt = (EditText) tradeOffer;
                    tradeOfferValue = tradeOfferEdtTxt.getText().toString();
                }
                // View schemeValView = parent.getChildAt(3);
                
                // EditText schemeValHiddenEdtTxt = (EditText) schemeValView;
                String quantity = etQuantity2.getText().toString();
                String schemeValHidden = "0";
                db.OpenDb();
                SelectedProductId = db.getInventoryID(pName);
                db.CloseDb();
                if (tradeOfferValue.equalsIgnoreCase("")) {
                    tradeOfferValue = "0";
                }
                if (discountValue.equalsIgnoreCase("")) {
                    discountValue = "0";
                }
                
                if (discountValue2.equalsIgnoreCase("")) {
                    discountValue2 = "0";
                }
                
                
                initiatePopupWindow(
                        view,
                        customerType,
                        SelectedProductId,
                        0,
                        quantity,
                        schemeValHidden,
                        discountValue, discountValue2,
                        tradeOfferValue);
            }
        });
        
        
        tvDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout parent = (LinearLayout) view.getParent();
                View discount = null, tradeOffer = null;
                EditText discount1 = null;
                String discountValue = "0", discountValue2 = "0";
                EditText tradeOfferEdtTxt;
                String tradeOfferValue = "0";
                
                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                    
                    discountValue = etDisc.getSelectedItem().toString();
                    
                    
                } else {
                    tradeOffer = parent.getChildAt(2);
                    tradeOfferEdtTxt = (EditText) tradeOffer;
                    tradeOfferValue = tradeOfferEdtTxt.getText().toString();
                }
                // View schemeValView = parent.getChildAt(3);
                
                // EditText schemeValHiddenEdtTxt = (EditText) schemeValView;
                String quantity = etQuantity.getText().toString();
                String schemeValHidden = "0";
                db.OpenDb();
                SelectedProductId = db.getInventoryID(pName);
                db.CloseDb();
                if (tradeOfferValue.equalsIgnoreCase("")) {
                    tradeOfferValue = "0";
                }
                if (discountValue.equalsIgnoreCase("")) {
                    discountValue = "0";
                }
                
                if (discountValue2.equalsIgnoreCase("")) {
                    discountValue2 = "0";
                }
                
                
                initiatePopupWindow(
                        view,
                        customerType,
                        SelectedProductId,
                        0,
                        quantity,
                        schemeValHidden,
                        discountValue, discountValue2,
                        tradeOfferValue);
            }
        });
        
        
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
	/*			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
				
				builder.setTitle("Work in progress");
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.dismiss();
						
					}
				});
				
				builder.show();*/
                
                if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                    final HashMap<String, String> map = new HashMap<>();
                    
                    p_name = etProduct2.getText().toString();
                    p_qty = etQuantity2.getText().toString();
                    p_disc = etDisc2.getSelectedItem().toString();
                    
                    p_disc2 = etDisc22.getSelectedItem().toString();
                    
                    if (!p_name.equals("") && !p_qty.equals("")) {
                        
                        db.OpenDb();
                        
                        if (!p_qty.equals("0")) {
                            p_Id = db.getInventoryID(p_name);
                            if (!p_Id.equals("")) {
                                if (data != null) {
                                    
                                    boolean check = true;
                                    String p_getName = db.getSelectedItemName(p_Id);
                                    for (HashMap<String, String> s : data) {
                                        if (s.get("productName").equals(p_getName)) {
                                            check = false;
                                        }
                                    }
                                    
                                    if (check) {
                                        
                                        map.put("productId", p_Id);
                                        map.put("productName", p_getName);
                                        map.put("qty", String.format("%.2f", (Double.parseDouble(p_qty))));
                                        map.put("qtyExe", String.format("%.2f", (Double.parseDouble(p_qty))));
                                        map.put("schemeQty", "");
                                        
                                        if (mobEmpDiscountType.equals("1")) {
                                            map.put("discount1", p_disc);
                                            
                                            map.put("discount2", p_disc2);
                                            
                                            map.put("tradeOffer", "0");
                                            Log.e("CheckErrorDiscount", p_disc);
                                        } else {
                                            /*map.put("tradeOffer", data.get(0).get("tradeOffer"));*/
                                            map.put("tradeOffer", tradeOffer.getText().toString());
                                            map.put("discount1", "0");
                                            Log.e("CheckErrorTrade", tradeOffer.getText().toString());
                                        }
                                        
                                        data.add(map);
                                        tempList.add(map);
                                        adapter1 = new EditReturnListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 1, customerType, mobEmpDiscountType);
                                        
                                        ItemList.setAdapter(adapter1);
                                        ItemList.setSelection(data.size());
                                        etProduct2.setText("");
                                        etQuantity2.setText("");
                                        etDisc2.setSelection(0);
                                        etDisc22.setSelection(0);
                                        tradeOffer.setText("");
                                        
                                        
                                    } else
                                        etProduct2.setError("Product already exist in order editOrderList");
                                }
                            } else {
                                etProduct2.setError("Product not found");
                            }
                        } else {
                            etQuantity2.setError("Enter valid quantity");
                        }
                    } else {
                        if (p_name.equals("")) {
                            etProduct2.setError("Select product first");
                        }
                        if (p_qty.equals("")) {
                            etQuantity2.setError("Enter quantity first");
                        }
                    }
                    Log.e("G-Product Id", "1 : " + map.get("productId"));
                    Log.e("G-productName", "2 : " + map.get("productName"));
                    Log.e("G-qty", "3 : " + map.get("qty"));
                    Log.e("G-qtyExe", "4 : " + map.get("qtyExe"));
                    Log.e("G-schemeQty", "5 : " + map.get("schemeQty"));
                    Log.e("G-discount1", "6 : " + map.get("discount1"));
                    Log.e("G-tradeOffer", "7 : " + map.get("tradeOffer"));
                } else {
                    final HashMap<String, String> map = new HashMap<>();
                    
                    p_name = etProduct.getText().toString();
                    p_qty = etQuantity.getText().toString();
                    p_disc = etDisc.getSelectedItem().toString();
                    
                    if (!p_name.equals("") && !p_qty.equals("")) {
                        
                        db.OpenDb();
                        
                        if (!p_qty.equals("0")) {
                            p_Id = db.getInventoryID(p_name);
                            if (!p_Id.equals("")) {
                                if (data != null) {
                                    
                                    boolean check = true;
                                    String p_getName = db.getSelectedItemName(p_Id);
                                    for (HashMap<String, String> s : data) {
                                        if (s.get("productName").equals(p_getName)) {
                                            check = false;
                                        }
                                    }
                                    
                                    if (check) {
                                        
                                        map.put("productId", p_Id);
                                        map.put("productName", p_getName);
                                        map.put("qty", String.format("%.2f", (Double.parseDouble(p_qty))));
                                        map.put("qtyExe", String.format("%.2f", (Double.parseDouble(p_qty))));
                                        map.put("schemeQty", "");
                                        
                                        if (mobEmpDiscountType.equals("1")) {
                                            map.put("discount1", p_disc);
                                            
                                            map.put("tradeOffer", "0");
                                            Log.e("CheckErrorDiscount", p_disc);
                                        } else {
                                            /*map.put("tradeOffer", data.get(0).get("tradeOffer"));*/
                                            map.put("tradeOffer", tradeOffer.getText().toString());
                                            map.put("discount1", "0");
                                            Log.e("CheckErrorTrade", tradeOffer.getText().toString());
                                        }
                                        
                                        data.add(map);
                                        tempList.add(map);
                                        adapter1 = new EditReturnListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 1, customerType, mobEmpDiscountType);
                                        
                                        ItemList.setAdapter(adapter1);
                                        ItemList.setSelection(data.size());
                                        etProduct.setText("");
                                        etQuantity.setText("");
                                        etDisc.setSelection(0);
                                        tradeOffer.setText("");
                                        
                                        
                                    } else
                                        etProduct.setError("Product already exist in order editOrderList");
                                }
                            } else {
                                etProduct.setError("Product not found");
                            }
                        } else {
                            etQuantity.setError("Enter valid quantity");
                        }
                    } else {
                        if (p_name.equals("")) {
                            etProduct.setError("Select product first");
                        }
                        if (p_qty.equals("")) {
                            etQuantity.setError("Enter quantity first");
                        }
                    }
                    Log.e("G-Product Id", "1 : " + map.get("productId"));
                    Log.e("G-productName", "2 : " + map.get("productName"));
                    Log.e("G-qty", "3 : " + map.get("qty"));
                    Log.e("G-qtyExe", "4 : " + map.get("qtyExe"));
                    Log.e("G-schemeQty", "5 : " + map.get("schemeQty"));
                    Log.e("G-discount1", "6 : " + map.get("discount1"));
                    Log.e("G-tradeOffer", "7 : " + map.get("tradeOffer"));
                }
                
            }
        });
        /*Abdul End*/
        
        db.OpenDb();
        CustomerName.setText(db.getCustomerName4EditTextSALESRETURN(orderID));
        String dbDate = db.getDate4EditTextSALESRETURN(orderID);
        //data = db.getSalesRETURNForEditList( orderID );
        
        //SharedPreferences settings = activity.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        //totalAmount = settings.getString("key","");
        notesTV.setText(db.getNotes4EditTextSALESRETURN(orderID));
        discountTxt.setText(db.getDiscount4EditTextSALESRETURN(orderID) + " %");
        Total2.setText(db.getTotal4EditTextSALESRETURN(orderID));
        
        // Unccomment below line
        
        db.CloseDb();
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat dfProper = new SimpleDateFormat("dd-MMMM-yyyy");
        Date dt = null;
        try {
            dt = df.parse(dbDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        DateTxt.setText(dfProper.format(dt));
        //DateTxt.setText( dbDate );
        
        
        ArrayList<HashMap<String, String>> Hlist = new ArrayList<>();
        Hlist.clear();
        
        final HashMap<String, String> temp = new HashMap<>();
        
        temp.put(EDIT_ORDER_ITEM, "Item Name");
        temp.put(EDIT_ORDER_OLDQTY, "Old Qty");
        temp.put(EDIT_ORDER_NEWQTY, "New Qty");
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            temp.put(EDIT_ORDER_ITEM_DISCOUNT, "Disc");
        } else {
            temp.put(EDIT_ORDER_ITEM_DISCOUNT, "T.O");
        }
        temp.put(EDIT_ORDER_ITEM_DETAILS, "Details");
        
        
        Hlist.add(temp);
        
        
        EditOrderListViewHeaderAdapter adapterH = new EditOrderListViewHeaderAdapter(ctx, Hlist);
        ItemListHeader.setAdapter(adapterH);
        
        
        adapter1 = new EditReturnListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 1, customerType, mobEmpDiscountType);
        
        ItemList.setAdapter(adapter1);
        
        final ArrayList<HashMap<String, String>> finalData = data;
        
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                db.OpenDb();
                
                
                schemeHolder = new ArrayList<>();
                ArrayList<HashMap<String, String>> hold_b_p_type = new ArrayList<>();
                String[] b_p_type;
                for (int i = 0; i < data.size(); i++) {
                    
                    b_p_type = db.getBrandAndProductTypeAndSubType(data.get(i).get("productId"));
                    
                    HashMap<String, String> mapHold = new HashMap<>();
                    
                    mapHold.put("b_id", b_p_type[1]);
                    mapHold.put("p_type_id", b_p_type[0]);
                    mapHold.put("value", "0");
                    hold_b_p_type.add(mapHold);
                }
                
                
                double holder = 0;
                for (int i = 0; i < data.size(); i++) {
                    
                    
                    ArrayList<HashMap<String, String>> listCheck;
                    
                    String[] val =
                            db.getBrandAndProductTypeAndSubType(data.get(i).get("productId"));
                    
                    String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId_InSRD);
                    
                    
                    listCheck = db.getPricingDataforscheme(SelectedCustomerTypeId_InSRD, SelectedCustomerId_InSRD,
                            val[1], val[0], data.get(i).get("qtyExe"),
                            data.get(i).get("productId"), val1[0], val1[1], val[2]);
                    
                    double scheme_hold_count = 0;
                    
                    if (listCheck != null) {
                        
                        if (listCheck.size() > 0) {
                            for (int j = 0; j < listCheck.size(); j++) {
                                if (listCheck.get(j).get("multi").equals("1") && !listCheck.get(j).get(
                                        "scheme").equals("0") && !listCheck.get(j).get("schemeVal").equals("0")) {
                                    
                                    hold_b_p_type.get(i).put("value",
                                            data.get(i).get("qtyExe"));
                                    
                                    for (HashMap<String, String> hashMap : hold_b_p_type) {
                                        
                                        if (hashMap.get("b_id").equals(val[1]) && hashMap.get("p_type_id").equals(val[0])) {
                                            
                                            scheme_hold_count =
                                                    scheme_hold_count + Double.parseDouble(hashMap.get("value"));
                                            
                                            
                                        }
                                        
                                    }
                                    holder = ((Math.floor(scheme_hold_count / Double.parseDouble(listCheck.get(j).get("scheme")))) * Double.parseDouble(listCheck.get(j).get("schemeVal")));
                                    Log.e("HOLder", holder + "");
                                    if (holder != 0)
                                        if ((holder % Double.parseDouble(listCheck.get(j).get(
                                                "schemeVal")) == 0) && (hold_b_p_type.get(i).get("b_id").equals(val[1]) && hold_b_p_type.get(i).get("p_type_id").equals(val[0]))) {
                                            
                                            scheme_hold_count = (scheme_hold_count %
                                                    Double.parseDouble(listCheck.get(j).get(
                                                            "scheme")));
                                            if (scheme_hold_count < 0)
                                                scheme_hold_count = 0;
                                            hold_b_p_type.get(i).put("value", String.valueOf(scheme_hold_count));
                                            HashMap<String, String> listCheck2 = listCheck.get(j);
                                            
                                            listCheck2.put("b_id", val[1]);
                                            listCheck2.put("p_type_id", val[0]);
                                            listCheck2.put("p_id", "0");
                                            listCheck2.put("l1_l1", "false");
                                            
                                            for (int k = 0; k < ((int) holder); k++) {
                                                
                                                HashMap<String, String> map =
                                                        new HashMap<>(listCheck2);
                                                if (k == 0) {
                                                    map.put("visibility", "true");
                                                } else {
                                                    map.put("visibility", "false");
                                                }
                                                
                                                if ((k + 1) == ((int) holder)) {
                                                    map.put("divider", "true");
                                                } else {
                                                    map.put("divider", "false");
                                                }
                                                

                                                map.put("group_id", String.valueOf(i));
                                                map.put("item_search",db.getSelectedItemName(data.get(i).get("productId")));
                                                map.put("item_search_id",data.get(i).get("productId"));
                                                map.put("scheme_item_search_SelectedCustomerTypeId",SelectedCustomerTypeId_InSRD);
                                                map.put("scheme_item_search_SelectedCustomerId",SelectedCustomerId_InSRD);
                                                map.put("scheme_item_search_val_1",val[1]);
                                                map.put("scheme_item_search_val_0",val[0]);
                                                map.put("scheme_item_search_allQty",data.get(i).get("qtyExe"));
                                                map.put("scheme_item_search_ProVal",  data.get(i).get("productId"));
                                                map.put("scheme_item_search_val1_0", val1[0]);
                                                map.put("scheme_item_search_val1_1", val1[1]);
                                                map.put("scheme_item_search_val_2", val[2]);
                                                schemeHolder.add(map);
                                                
                                                
                                            }
                                        }
                                    
                                }
                                
                            }
                        }
                    }
                    
                }
                
                
                if (schemeHolder.size() > 0) {
                    
                    Log.e("schemeHolder_SR",schemeHolder.size()+"");
                    
                    schemeDialog.setTitle("Scheme Offer");
                    schemeDialog.setContentView(R.layout.show_scheme_popup_layout);
                    
                    final ListView listView = schemeDialog.findViewById(R.id.scheme_list);
                    
                    Button next, back;
                    
                    next = schemeDialog.findViewById(R.id.next_scheme);
                    back = schemeDialog.findViewById(R.id.back_scheme);
                    
                    showSchemeListAdapter = new ShowSchemeListAdapter(activity,
                            schemeHolder);
                    
                    listView.setAdapter(showSchemeListAdapter);
                    
                    
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            
                            
                            position1 = showSchemeListAdapter.checkScheme();
                            if (position1 == -1) {
                                
                                
                                schemeDialog.dismiss();
                                
                                Log.e("schemeHolder", schemeHolder.size() + "");
                                
                                db.deleteSalesReturnDetailScheme(orderID);
                                
                                for (int i = 0; i < schemeHolder.size(); i++) {
                                    
                                    
                                    db.createReturnDetails(
                                            String.valueOf(orderID),
                                            schemeHolder.get(i).get("p_id"),
                                            "1",
                                            "1",
                                            "0", /*"0"*/
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "0",
                                            "1",
                                            0,
                                            0,
                                            0,
                                            0,
                                            0,
                                            0,
                                            "0");
                                }
                                
                                double totAmount = 0;
                                String tax1 = "0", tax2 = "0", tax3 = "0";
                                
                                double totalTemp2 = insertReturnDetails(tempList, orderID);
                                db.updateCustomerLastUpdate(SelectedCustomerId_InSRD,
                                        getDateTimeSHORT());
                                Log.e("GetEditNewOrder", String.valueOf(totalTemp2));
                                //Log.e("ConcatOrders", GetValuesTOUpdate(data, orderID));
                                db.updateSalesReturnNewVales(Integer.parseInt(orderID), GetValuesTOUpdate(data,
                                        orderID),
                                        notesTV.getText().toString());
                                /*db.UpdateChangesSalesOrder( Integer.parseInt(orderID), 1 );*/
                                
                                //data = null;
                                //data = db.getSalesOrderForEditList( orderID );
                                // data = db.getOrderDetailForEditList(orderID);
                                
                                
                                for (int i = 0; i < data.size(); i++) {
                                    
                                    HashMap<String, String> map = data.get(i);
                                    
                                    String itemID = map.get("productId");
                                    String newQTY = map.get("qty");
                                    String exeQTY = map.get("qtyExe");
                                    String schemeQTY = map.get("schemeQty");
                                    String discount1 = "0", discount2 = "0";
                                    String tradeOffer = "0";
                                    if (mobEmpDiscountType.equals("1")) {
                                        discount1 = map.get("discount1");
                                        if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                                            discount2 = map.get("discount2");
                                        }
                                    } else {
                                        tradeOffer = map.get("tradeOffer");
                                    }
                                    
                                    
                                    Log.e("GetEditHashMap", itemID + " : " + newQTY + " : " + exeQTY + " : " + schemeQTY + " : " + discount1 + " : " + tradeOffer);
                                    
                                    if (newQTY.equalsIgnoreCase("")) {
                                        newQTY = "0";
                                    }
                                    if (exeQTY.equalsIgnoreCase("")) {
                                        exeQTY = "0";
                                    }
                                    if (schemeQTY.equalsIgnoreCase("")) {
                                        schemeQTY = "0";
                                    }
                                    if (discount1.equalsIgnoreCase("")) {
                                        discount1 = "0";
                                    }
                                    if (discount2.equalsIgnoreCase("")) {
                                        discount2 = "0";
                                    }
                                    if (tradeOffer.equalsIgnoreCase("")) {
                                        tradeOffer = "0";
                                    }
                                    
                                    String[] val =
                                            db.getBrandAndProductTypeAndSubType(data.get(i).get("productId"));
                                    
                                    String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId_InSRD);
                                    
                                    // ArrayList<HashMap<String, String>> dataDetail = db
                                    // .getOrderDetailAgainstProduct(orderID, itemID);
                                    
                                    ArrayList<HashMap<String, String>> dataDetail = db.getPricingData(SelectedCustomerTypeId_InSRD, SelectedCustomerId_InSRD, val[1],
                                            val[0], data.get(i).get("qty"), data.get(i).get("productId"), val1[0], val1[1], val[2]);
                                    
                                    
                                    HashMap<String, String> detailsMap = new HashMap<>();
                                    double result = 0, scheme, schemeQty = 0, quantity = 0, quantityExe = 0, schemeQuantity = 0;
                                    if (dataDetail.size() > 0) {
                                        for (int j = 0; j < dataDetail.size(); j++) {
                                            detailsMap = dataDetail.get(j);
                                            if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                                if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                                                    result =
                                                            pricingCalculation(detailsMap,
                                                                    Double.parseDouble(exeQTY),
                                                                    detailsMap.get("tradePrice"),/*detailsMap.get
                                            ("discount1")*/discount1, discount2, detailsMap.get(
                                                                            "tradeOffer"), detailsMap.get("scheme"),
                                                                    detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"),
                                                                    detailsMap.get("tax_filer_1"), detailsMap.get(
                                                                            "tax_filer_2"), detailsMap.get("tax_filer_3"));
                                                    
                                                } else {
                                                    result =
                                                            pricingCalculation(detailsMap,
                                                                    Double.parseDouble(exeQTY),
                                                                    detailsMap.get("tradePrice"),/*detailsMap.get
                                            ("discount1")*/discount1, detailsMap.get("discount2"), detailsMap.get("tradeOffer"), detailsMap.get("scheme"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"),
                                                                    detailsMap.get("tax_filer_1"), detailsMap.get(
                                                                            "tax_filer_2"), detailsMap.get("tax_filer_3"));
                                                    
                                                }
                                            } else {
                                                result =
                                                        pricingCalculation(detailsMap,
                                                                Double.parseDouble(exeQTY),
                                                                detailsMap.get("tradePrice"),/*detailsMap.get
                                        ("discount1")*/detailsMap.get("discount1"), detailsMap.get("discount2"), tradeOffer, detailsMap.get("scheme"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"),
                                                                detailsMap.get("tax_filer_1"), detailsMap.get(
                                                                        "tax_filer_2"), detailsMap.get("tax_filer_3"));
                                            }
                                            
                                            totAmount += result;
                                            Log.e("GetEditUpdateOrder", String.valueOf(totAmount));
                                            
                                            if (!detailsMap.get("scheme").equalsIgnoreCase("") && Double.parseDouble(detailsMap.get("scheme")) != 0) {
                                                scheme =
                                                        Double.parseDouble(exeQTY) / Double.parseDouble(detailsMap.get("scheme"));
                                                schemeQty = Double.parseDouble(detailsMap.get("schemeVal")) / scheme;
                                                schemeQuantity = Math.floor(scheme) * Double.parseDouble(detailsMap.get("schemeVal"));
                                            } else {
                                                scheme = 0;
                                                schemeQuantity = 0;
                                            }
                                            quantity = Double.parseDouble(newQTY) + schemeQuantity;
                                            quantityExe = Double.parseDouble(exeQTY) + schemeQuantity;
                                            
                                        }
                                        //quantity = Double.parseDouble(newQTY) + (scheme * schemeQty);
                                        //quantityExe = Double.parseDouble(exeQTY) + (scheme * schemeQty);
                                        
                                        DecimalFormat df = new DecimalFormat("###.##");
                                        result = Double.parseDouble(df.format(result));
                                        
                                        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                            Log.e("GetOrderValuesID: ", orderID);
                                            if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                                                
                                                db.updateReturnDetails(Integer.parseInt(orderID), itemID,
                                                        String.format("%.2f", quantity), String.format("%.2f",
                                                                quantityExe), detailsMap.get("tradePrice"),
                                                        discount1, discount2,/* detailsMap.get("tradeOffer")
                                                         */tradeOffer, detailsMap.get("scheme"),
                                                        String.valueOf(schemeQuantity),
                                                        detailsMap.get("schemeVal"),
                                                        detailsMap.get("schemeProduct"),
                                                        detailsMap.get("tax1"), detailsMap.get(
                                                                "tax2"), detailsMap.get("tax3"), String.format("%.2f", result), t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, detailsMap.get("mrp_price"));
                                            } else {
                                                db.updateReturnDetails(Integer.parseInt(orderID), itemID,
                                                        String.format("%.2f", quantity),
                                                        String.format("%.2f", quantityExe),
                                                        detailsMap.get("tradePrice"), discount1,
                                                        detailsMap.get("discount2"),/* detailsMap
                                                        .get("tradeOffer")*/tradeOffer, detailsMap.get("scheme"), String.valueOf(schemeQuantity), detailsMap.get("schemeVal"), detailsMap.get("schemeProduct"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"), String.valueOf(result), t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, detailsMap.get("mrp_price"));
                                            }
                                        } else {
                                            Log.e("GetOrderValuesID: ", orderID);
                                            db.updateReturnDetails(Integer.parseInt(orderID), itemID,
                                                    String.format("%.2f", quantity),
                                                    String.format("%.2f", quantityExe),
                                                    detailsMap.get("tradePrice"), detailsMap.get(
                                                            "discount1"), detailsMap.get(
                                                            "discount2"), tradeOffer,
                                                    detailsMap.get("scheme"),
                                                    String.valueOf(schemeQuantity),
                                                    detailsMap.get("schemeVal"), detailsMap.get(
                                                            "schemeProduct"), detailsMap.get(
                                                            "tax1"), detailsMap.get("tax2"),
                                                    detailsMap.get("tax3"),
                                                    String.valueOf(result), t_o_v, d_v_1, d_v_2,
                                                    t_type, t_mrp_type, t_val, detailsMap.get(
                                                            "mrp_price"));
                                        }
                                    }
                                    
                                }
                                
                                //String discount = discountTxt.getText().toString();
                                double afterDivideDiscount = 0, afterSubtractDiscount = 0;
                                if (customerType.equalsIgnoreCase("1")) {
                                    discount = db.getDiscountForSaleOrder(totAmount);
                                    if (discount > 0) {
                                        afterDivideDiscount = discount / 100;
                                        afterSubtractDiscount = 1 - afterDivideDiscount;
                                        totAmount = totAmount * afterSubtractDiscount;
                                    }
                                }
                                //totalTxt.setText(totalAmount);
                                Log.e("GetData", totalTemp2 + " : " + totAmount + " : " + totalTemp2 + " : " + (totAmount + totalTemp2));
                                //totalTemp2 = totAmount + totalTemp2;
                                DecimalFormat df = new DecimalFormat("###.##");
                                totAmount = Double.parseDouble(df.format(totAmount));
                                
                                db.updateSalesReturnTotal(Integer.parseInt(orderID), String.valueOf(totAmount), String.valueOf(discount));
                                
                                db.CloseDb();
                                
                                
                                EditReturnListViewAdapter adapter1 = new EditReturnListViewAdapter(ctx, data, db,
                                        orderID,
                                        ItemList, BtnLay, 1, customerType, mobEmpDiscountType);
                                
                                ItemList.setAdapter(adapter1);
                                
                                populateReturnOrderList(lView, db, ctx, noItemTxt);
                                new MainActivity().updateSync(ctx);
                                dialog.dismiss();
                                
                                
                            } else {
                                listView.smoothScrollToPosition(position1);
                                
                                
                            }
                        }
                    });
                    
                    
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            
                            schemeDialog.cancel();
                            
                        }
                    });
                    
                    
                    schemeDialog.show();
                    Window window = schemeDialog.getWindow();
                    
                    
                    if (window != null) {
                        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                    }
                    
                    
                } else {
                    
                    
                    double totAmount = 0;
                    
                    db.deleteSalesReturnDetailScheme(orderID);
                    
                    Log.e("CheckDataSize", String.valueOf(tempList.size()));
                    double totalTemp2 = insertReturnDetails(tempList, orderID);
                    
                    Log.e("GetEditNewOrder", String.valueOf(totalTemp2));
                    
                    db.updateSalesReturnNewVales(Integer.parseInt(orderID), GetValuesTOUpdate(data, orderID), notesTV.getText().toString());
                    
                    
                    /*db.UpdateChangesSalesOrder( Integer.parseInt(orderID), 1 );*/
                    
                    //data = null;
                    //data = db.getSalesOrderForEditList( orderID );
                    //data = db.getOrderDetailReturnForEditList(orderID);
                    
                    
                    //data = db.getOrderDetailReturnForEditList(orderID);
                    for (int i = 0; i < data.size(); i++) {
                        
                        HashMap<String, String> map = data.get(i);
                        
                        String itemID = map.get("productId");
                        String newQTY = map.get("qty");
                        String exeQTY = map.get("qtyExe");
                        String schemeQTY = map.get("schemeQty");
                        String discount1 = "0", discount2 = "0";
                        String tradeOffer = "0";
                        if (mobEmpDiscountType.equals("1")) {
                            discount1 = map.get("discount1");
                            if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                                discount2 = map.get("discount2");
                            }
                        } else {
                            tradeOffer = map.get("tradeOffer");
                            Log.e("TradeOffer", tradeOffer);
                        }
                        
                        
                        Log.e("GetEditHashMap", itemID + " : " + newQTY + " : " + exeQTY + " : " + schemeQTY + " : " + discount1 + " : " + tradeOffer);
                        
                        if (newQTY.equalsIgnoreCase("")) {
                            newQTY = "0";
                        }
                        if (exeQTY.equalsIgnoreCase("")) {
                            exeQTY = "0";
                        }
                        if (schemeQTY.equalsIgnoreCase("")) {
                            schemeQTY = "0";
                        }
                        if (discount1.equalsIgnoreCase("")) {
                            discount1 = "0";
                        }
                        if (discount2.equalsIgnoreCase("")) {
                            discount2 = "0";
                        }
                        if (tradeOffer.equalsIgnoreCase("")) {
                            tradeOffer = "0";
                        }
                        
                        
                        String[] val =
                                db.getBrandAndProductTypeAndSubType(data.get(i).get("productId"));
                        
                        String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId_InSRD);
                        
                        
                        ArrayList<HashMap<String, String>> dataDetail = db.getPricingData(SelectedCustomerTypeId_InSRD, SelectedCustomerId_InSRD,
                                val[1], val[0], data.get(i).get("qtyExe"),
                                data.get(i).get("productId"), val1[0], val1[1], val[2]);
                        
                        /*   ArrayList<HashMap<String, String>> dataDetail = db.getReturnDetailAgainstProduct(orderID, itemID);*/
                        HashMap<String, String> detailsMap = new HashMap<>();
                        double result = 0, scheme = 0, schemeQty = 0, quantity = 0, quantityExe = 0, schemeQuantity = 0;
                        if (dataDetail.size() > 0) {
                            for (int j = 0; j < dataDetail.size(); j++) {
                                detailsMap = dataDetail.get(j);
                                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                    if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                                        result = pricingCalculation(detailsMap,
                                                Double.parseDouble(exeQTY),
                                                detailsMap.get("tradePrice"),/*detailsMap.get
                                           ("discount1")*/discount1, discount2, detailsMap.get("tradeOffer"), detailsMap.get("scheme"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"),
                                                detailsMap.get("tax_filer_1"), detailsMap.get(
                                                        "tax_filer_2"), detailsMap.get("tax_filer_3"));
                                    } else {
                                        result = pricingCalculation(detailsMap,
                                                Double.parseDouble(exeQTY),
                                                detailsMap.get("tradePrice"),/*detailsMap.get("discount1")*/discount1, detailsMap.get("discount2"), detailsMap.get("tradeOffer"), detailsMap.get("scheme"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"),
                                                detailsMap.get("tax_filer_1"), detailsMap.get(
                                                        "tax_filer_2"), detailsMap.get("tax_filer_3"));
                                    }
                                } else {
                                    result = pricingCalculation(detailsMap, Double.parseDouble(exeQTY),
                                            detailsMap.get("tradePrice"),/*detailsMap.get("discount1")*/detailsMap.get("discount1"), detailsMap.get("discount2"), tradeOffer, detailsMap.get("scheme"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"),
                                            detailsMap.get("tax_filer_1"), detailsMap.get(
                                                    "tax_filer_2"), detailsMap.get("tax_filer_3"));
                                }
                                
                                totAmount += result;
                                Log.e("GetEditUpdateOrder", String.valueOf(totAmount));
                                DecimalFormat df = new DecimalFormat("###.##");
                                result = Double.parseDouble(df.format(result));
                                if (!detailsMap.get("scheme").equalsIgnoreCase("") && Double.parseDouble(detailsMap.get("scheme")) != 0) {
                                    scheme =
                                            Double.parseDouble(exeQTY) / Double.parseDouble(detailsMap.get("scheme"));
                                    schemeQty = Double.parseDouble(detailsMap.get("schemeVal")) / scheme;
                                    schemeQuantity = Math.floor(scheme) * Double.parseDouble(detailsMap.get("schemeVal"));
                                } else {
                                    scheme = 0;
                                    schemeQuantity = 0;
                                }
                                quantity = Double.parseDouble(newQTY) + schemeQuantity;
                                quantityExe = Double.parseDouble(exeQTY) + schemeQuantity;
                                
                            }
                            //quantity = Double.parseDouble(newQTY) + (scheme * schemeQty);
                            //quantityExe = Double.parseDouble(exeQTY) + (scheme * schemeQty);
                            if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                                if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                                    db.updateReturnDetails(Integer.parseInt(orderID), itemID,
                                            String.format("%.2f", quantity), String.format("%.2f"
                                                    , quantityExe), detailsMap.get("tradePrice"),
                                            discount1, discount2,/* detailsMap.get("tradeOffer")*/tradeOffer, detailsMap.get("scheme"), String.valueOf(schemeQuantity), detailsMap.get("schemeVal"), detailsMap.get("schemeProduct"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"), String.format("%.2f", result), t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, detailsMap.get("mrp_price"));
                                } else {
                                    db.updateReturnDetails(Integer.parseInt(orderID), itemID,
                                            String.format("%.2f", quantity), String.format("%.2f"
                                                    , quantityExe), detailsMap.get("tradePrice"),
                                            discount1, detailsMap.get("discount2"),/* detailsMap.get("tradeOffer")*/tradeOffer, detailsMap.get("scheme"), String.valueOf(schemeQuantity), detailsMap.get("schemeVal"), detailsMap.get("schemeProduct"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"), String.valueOf(result), t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, detailsMap.get("mrp_price"));
                                }
                                
                            } else {
                                db.updateReturnDetails(Integer.parseInt(orderID), itemID,
                                        String.format("%.2f", quantity), String.format("%.2f", quantityExe), detailsMap.get("tradePrice"),
                                        detailsMap.get("discount1"), detailsMap.get("discount2"),
                                        tradeOffer, detailsMap.get("scheme"),
                                        String.valueOf(schemeQuantity), detailsMap.get("schemeVal"), detailsMap.get("schemeProduct"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"), String.valueOf(result), t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, detailsMap.get("mrp_price"));
                            }
                        }
                        
                    }
                    
                    //String discount = discountTxt.getText().toString();
                    double afterDivideDiscount = 0, afterSubtractDiscount = 0;
                    if (customerType.equalsIgnoreCase("1")) {
                        discount = db.getDiscountForSaleOrder(totAmount);
                        if (discount > 0) {
                            afterDivideDiscount = discount / 100;
                            afterSubtractDiscount = 1 - afterDivideDiscount;
                            totAmount = totAmount * afterSubtractDiscount;
                        }
                    }
                    //totalTxt.setText(totalAmount);
                    //Log.e("GetData", String.valueOf(totalTemp2 + " : " + totAmount + " : " + totalTemp2) + " : " + (totAmount + totalTemp2));
                    //totalTemp2 = totAmount + totalTemp2;
                    //   db.updateSalesOrderTotal(Integer.parseInt(orderID), String.valueOf(totAmount), String.valueOf(totAmount), String.valueOf(discount));
                    DecimalFormat df = new DecimalFormat("###.##");
                    totAmount = Double.parseDouble(df.format(totAmount));
                    db.updateSalesReturnTotal(Integer.parseInt(orderID), String.valueOf(totAmount), String.valueOf(discount));
                    db.CloseDb();
                    
                    Log.d("Concat", GetValuesTOUpdate(data, orderID));
                    
                    
                    EditReturnListViewAdapter adapter1 = new EditReturnListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 1, customerType, mobEmpDiscountType);
                    
                    ItemList.setAdapter(adapter1);
                    
                    populateReturnOrderList(lView, db, ctx, noItemTxt);
                    new MainActivity().updateSync(ctx);
                    dialog.dismiss();
                }
                
            }
        });
        
        Dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                populateReturnOrderList(lView, db, ctx, noItemTxt);
                dialog.dismiss();
                
            }
        });
        
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.show();
        
        
    }
    private void openSearchProductFilter(final AutoCompleteTextView prodAutoCompleteTxt, final PosDB db){
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_search_diialog_layout, null);

//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.filter_search_dialog_layout,
//                new FrameLayout(getActivity()), false);

        EditText search = view.findViewById(R.id.filtersearch_search_edt);
        TextView title = view.findViewById(R.id.filtersearch_mainhd_tv);
        ImageView cancel = view.findViewById(R.id.custom_dialog_approved_close);
        filterProductAdapter = new SingleProductFilterRecylarAdapter(getInventoryNameFromDb(db), activity);
        RecyclerView recyclerView = view.findViewById(R.id.filtersearch_recylarview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        // Set adapter to recyclerView
        recyclerView.setAdapter(filterProductAdapter);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filterProduct(editable.toString(), getInventoryNameFromDb(db));
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        final AlertDialog myDialog = builder.create();
        filterProductAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prodAutoCompleteTxt.setText("" + ((List<String>) view.getTag()).get(1));
                pName="" + ((List<String>) view.getTag()).get(1);
                myDialog.dismiss();
            }
        });
//        myDialog.setContentView(view);

        title.setText("Search Product");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

//        myDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        myDialog.getWindow().setDimAmount(0.90f);
//        myDialog.getWindow().getDecorView().setBackground(new ColorDrawable(Color.TRANSPARENT));
//        myDialog.setCanceledOnTouchOutside(true);
        myDialog.show();

    }
    private void filterProduct(String text, List<MyObject> filtr_list) {
        //new array list that will hold the filtered data
        List<MyObject> filterdNames = new ArrayList<>();
        //looping through existing elements
        for (MyObject s : filtr_list) {
            //if the existing elements contains the search input
            if (s.getObjectName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
//        if (filterdNames.size()==0){
//            Toast.makeText(fragmentActivity, "No Record Found", Toast.LENGTH_SHORT).show();
//        }
        //calling a method of the adapter class and passing the filtered list
        filterProductAdapter.filterList(filterdNames);
    }

    public void ViewOrder(final Context ctx, final PosDB db, final String orderID, final SwipeListView lView, final TextView noItemTxt) {
        
        final Dialog dialog = new Dialog(ctx);
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_view_order);

/*
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
*/
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT); //set below the setContentview
        
        //final RelativeLayout DialogLayout = dialog.findViewById(R.id.DialogLayout);
        
        TextView CustomerName = dialog.findViewById(R.id.CustomerName);
        TextView DateTxt = dialog.findViewById(R.id.Date);
        TextView notesTV = dialog.findViewById(R.id.notesTxtView);
        TextView reasonTV = dialog.findViewById(R.id.reasonTxtView);
        TextView Total2 = dialog.findViewById(R.id.Total);
        final ListView ItemList = dialog.findViewById(R.id.ItemList);
        ListView ItemListHeader = dialog.findViewById(R.id.ItemListH);
        Button Dismiss = dialog.findViewById(R.id.DismissBtn);
        Button printBtn = dialog.findViewById(R.id.PrintBtn);
        
        final TableRow BtnLay = dialog.findViewById(R.id.ButtonLayout);
        
        db.OpenDb();
        // Unccomment below line
        mobEmpDiscountType = db.getMobEmpDiscountType();
        CustomerName.setText(db.getCustomerName4EditTextSALESRETURN(orderID));
        String dbDate = db.getDate4EditTextSALESRETURN(orderID);
        //data = db.getSalesRETURNForEditList( orderID );
        Total2.setText(db.getTotal4EditTextSALESRETURN(orderID));
        data = db.getOrderDetailReturnForEditListView(orderID);
        String notesReturnFromDB = db.getNotes4EditTextSALESRETURN(orderID);
        String reasonReturnFromDB = db.getReason4EditTextSALESRETURN(orderID);
        String returnReasonNameWithID = db.getReasonName4ReturnReasonWithId(Integer.parseInt(reasonReturnFromDB));
        
        db.CloseDb();
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        DateFormat dfProper = new SimpleDateFormat("dd-MMMM-yyyy");
        Date dt = null;
        try {
            dt = df.parse(dbDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        DateTxt.setText(dfProper.format(dt));
        notesTV.setText(notesReturnFromDB);
        if (returnReasonNameWithID.equalsIgnoreCase("")) {
            reasonTV.setText("0");
        } else {
            reasonTV.setText(returnReasonNameWithID);
        }
        //DateTxt.setText( dbDate );
        
        
        ArrayList<HashMap<String, String>> Hlist = new ArrayList<>();
        Hlist.clear();
        
        HashMap<String, String> temp = new HashMap<>();
        
        temp.put(EDIT_ORDER_ITEM, "Item Name");
        temp.put(EDIT_ORDER_OLDQTY, "Old Qty");
        temp.put(EDIT_ORDER_NEWQTY, "New Qty");
        if (mobEmpDiscountType.equalsIgnoreCase("1")) {
            temp.put(EDIT_ORDER_ITEM_DISCOUNT, "Disc");
            if (BuildConfig.FLAVOR.equalsIgnoreCase("english") || BuildConfig.FLAVOR.equalsIgnoreCase("lq_foods")) {
                temp.put("Disc2", "Disc2");
            }
        } else {
            temp.put(EDIT_ORDER_ITEM_DISCOUNT, "T.O");
        }
        temp.put(EDIT_ORDER_ITEM_DETAILS, "Details");
        
        Hlist.add(temp);
        
        
        EditOrderListViewHeaderAdapter adapterH = new EditOrderListViewHeaderAdapter(ctx, Hlist);
        ItemListHeader.setAdapter(adapterH);
        
        if (data.size() == 0) {
            
            Toast.makeText(ctx, "Order details not available", Toast.LENGTH_SHORT).show();
            
            return;
        }
        
        EditReturnListViewAdapter adapter1 = new EditReturnListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 2, mobEmpDiscountType);
        
        ItemList.setAdapter(adapter1);
        
        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                try {
                    findBT();
                    openBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    sendData(orderID);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    closeBT();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // dialog.dismiss();
            }
        });
        
        
        Dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                dialog.dismiss();
                
            }
        });
        
        
        dialog.show();
        
        
    }
    
    void findBT() {
        
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            
            if (mBluetoothAdapter == null) {
                //myLabel.setText("No bluetooth adapter available");
                Toast.makeText(AppContextProvider.getContext(), "No Bluetooth Adapter Available", Toast.LENGTH_LONG).show();
            }
            
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetooth, 0);
            }
            
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    
                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the editOrderList of paired devices
                    if (device.getName().equals("MTP-II")) {
                        mmDevice = device;
                        //myLabel.setText("Bluetooth device found.");
                        Toast.makeText(AppContextProvider.getContext(), "Bluetooth Device Found.", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            } else {
                Toast.makeText(AppContextProvider.getContext(), "Bluetooth Device Not Found.", Toast.LENGTH_LONG).show();
                //myLabel.setText("Bluetooth device NOT found.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // tries to open a connection to the bluetooth printer device
    void openBT() {
        try {
            
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            
            beginListenForData();
            Toast.makeText(AppContextProvider.getContext(), "Bluetooth Opened.", Toast.LENGTH_LONG).show();
            //myLabel.setText("Bluetooth Opened");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    void beginListenForData() {
        try {
            final Handler handler = new Handler();
            
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            
            workerThread = new Thread(new Runnable() {
                public void run() {
                    
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        
                        try {
                            
                            int bytesAvailable = mmInputStream.available();
                            
                            if (bytesAvailable > 0) {
                                
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                
                                for (int i = 0; i < bytesAvailable; i++) {
                                    
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                        readBufferPosition = 0;
                                        
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                //myLabel.setText(data);
                                                Toast.makeText(AppContextProvider.getContext(), "" + data, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                            
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                        
                    }
                }
            });
            
            workerThread.start();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // this will send text data to be printed by the bluetooth printer
    void sendData(String orderId) {
        try {
            
            // the text typed by the user
            db.OpenDb();
            
            
            //String msg = myTextbox.getText().toString();
            String msg = "        " + " " + db.getMobCompany().toUpperCase() + " " + "     ";
            msg += "\n";
            // msg += "Item"+"\t"+"Qty"+"\t"+"SubTotal";
            msg += "        " + " Sale Return # " + orderId + "        " + "        ";
            msg += "\n";
            String dbDate = db.getDate4EditTextSALESRETURN(orderId);
            //convertDate(dbDate);
            msg += "Date : " + convertDate(dbDate);
            msg += "\n";
            msg += "Customer : " + db.getCustomerName4EditTextSALESRETURN2(orderId);
            msg += "\n";
            msg += "Salesman : " + db.getMobFName();
            msg += "\n\n";
            msg += "Item" + "        " + "  " + "Qty" + "       " + "SubTotal";
            msg += "\n";
            msg += "--------------------------------";
            msg += "\n";
            ArrayList<HashMap<String, String>> list = db.getReturnDetailForInvoice(orderId);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    HashMap map = list.get(i);
                    String invName = map.get("invName").toString() + "          ";
                    msg += "- " + invName.substring(0, 10);
                    String qty = map.get("qtyExe").toString() + "    ";
                    msg += "    " + qty.substring(0, 3);
                    String subTotal = map.get("subTotal").toString() + "    ";
                    msg += "      " + subTotal.substring(0, 5);
                    msg += "\n";
                    //if (!map.get("invSku").equals("")) {
                    msg += " SKU : " + map.get("invSku");
                    //}
                    //if (!map.get("tradePrice").equals("0")){
                    msg += " T.P : " + map.get("tradePrice");
                    //}
                    if (!map.get("discount1").equals("0")) {
                        msg += " Disc1 : " + map.get("discount1") + " %";
                    }
                    if (!map.get("discount2").equals("0")) {
                        msg += " Disc2 : " + map.get("discount2") + " %";
                    }
                    if (!map.get("tradeOffer").equals("0")) {
                        msg += " T.O : " + map.get("tradeOffer");
                    }
                    if (!map.get("scheme").equals("0")) {
                        msg += " Scheme : " + map.get("scheme");
                    }
                    if (!map.get("tax1").equals("0")) {
                        msg += " TAX1 : " + map.get("tax1") + " %";
                    }
                    if (!map.get("tax2").equals("0")) {
                        msg += " TAX2 : " + map.get("tax2") + " %";
                    }
                    if (!map.get("tax3").equals("0")) {
                        msg += " TAX3 : " + map.get("tax3") + " %";
                    }
                    msg += "\n";
                    
                }
            }
            //msg += "Multiple Product here";
            msg += "--------------------------------";
            msg += "\n";
            String total = db.getTotal4EditTextSALESRETURN(orderId) + "       ";
            msg += "                 " + "Total : " + total.substring(0, 7);
            msg += "\n";
            msg += "Printed By : " + db.getMobFName();
            msg += "\n";
            msg += "Print Time: " + convertDate2(getDateTime());
            msg += "\n\n";
            msg += "Software Powered By";
            msg += "\n";
            msg += "Blaze Minds Solutions".toUpperCase();
            msg += "\n";
            msg += "Website : " + "www.blazeminds.com";
            
            msg += "\n";
            msg += "Contact : +92 321 3880558";
            db.CloseDb();
            msg += "\n";
            msg += "\n";
            msg += "\n";
            Log.d("MSG Print --> ", msg);
            
            mmOutputStream.write(msg.getBytes());
            
            db.updateSalesReturnPrintExecute(Integer.parseInt(orderId));
            // tell the user data were sent
            // myLabel.setText("Data sent.");
            //Toast.makeText(activity,"Data Sent.",Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // close the connection to bluetooth printer.
    void closeBT() {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            // myLabel.setText("Bluetooth Closed");
            Toast.makeText(AppContextProvider.getContext(), "Bluetooth Closed.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        
        DateFormat df = DateFormat.getDateTimeInstance();
        
        //SelectedDate = dateFormat.format(new Date());
        
        return dateFormat.format(new Date());
        //return df.format(new Date());
    }
    
    String convertDate(String dbDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
        DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
        Date dt = null;
        try {
            dt = df.parse(dbDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return dfProper.format(dt);
    }
    
    String convertDate2(String dbDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
        DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mma");
        Date dt = null;
        try {
            dt = df.parse(dbDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        
        return dfProper.format(dt);
    }
    
    private String GetValuesTOUpdate(ArrayList<HashMap<String, String>> list, String orderID) {
        
        String result = "";
        
        for (int i = 0; i < list.size(); i++) {
            
            HashMap map = list.get(i);
            
            String itemID = map.get("productId").toString();
            String oldQTY = map.get("qty").toString();
            String newQTY = map.get("qtyExe").toString();
            
            
            result = result + itemID + "!!";
            
            result = result + oldQTY + "!!";
            result = result + newQTY + "!!!";
            result = result + orderID + "--";
            
            
        }
        
        return result;
    }
    
    private double insertReturnDetails(ArrayList<HashMap<String, String>> dataList, String oID) {
        
        ArrayList<HashMap<String, String>> list;
        HashMap<String, String> map = new HashMap<>();
        double res = 0.0, quantity = 0;
        String tradeOffer = "0";
        String tax1 = "0", tax2 = "0", tax3 = "0";
        db.OpenDb();
        /*   int orderId = db.getMaxOrderIdFromSaleOrder();*/
        
        for (int i = 0; i < dataList.size(); i++) {
            
            String[] val =
                    db.getBrandAndProductTypeAndSubType(data.get(i).get("productId"));
            
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId_InSRD);
            
            list = db.getPricingData(SelectedCustomerTypeId_InSRD, SelectedCustomerId_InSRD, val[1],
                    val[0], data.get(i).get("qty"), data.get(i).get("productId"), val1[0], val1[1], val[2]);
            /*list = db.getPricingData(customerType, dataList.get(i).get("productId"));*/
            if (list.size() > 0) {
                for (int j = 0; j < list.size(); j++) {
                    
                    map = list.get(j);
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        res = pricingCalculation(map, Double.parseDouble(dataList.get(i).get("qty")),
                                map.get("tradePrice"), dataList.get(i).get("discount1"), map.get("discount2"), map.get("tradeOffer"), map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map.get("tax_filer_1"), map.get("tax_filer_2"), map.get("tax_filer_3"));
                        Log.e("GetTrackRes", String.valueOf(res));
                        totalTemp = totalTemp + res;
                    } else {
                       /* if (allTradeOfferList.get(i).getText().toString().isEmpty()) {
                        tradeOffer = "0";
                        } else {
                            tradeOffer = allTradeOfferList.get(i).getText().toString();
                        }*/
                        
                        String dis = "0";
                        
                        if (mobEmpDiscountType.equals("1")) {
                            dis = dataList.get(i).get("discount1");
                        } else {
                            tradeOffer = dataList.get(i).get("tradeOffer");
                        }
                        res =
                                pricingCalculation(map,
                                        Double.parseDouble(dataList.get(i).get("qty")),
                                        map.get("tradePrice"),/*map.get("discount1")*/
                                        dis,
                                        map.get("discount2"),
                                        tradeOffer,
                                        map.get("scheme"),
                                        map.get("tax1"),
                                        map.get("tax2"),
                                        map.get("tax3"),
                                        map.get("tax_filer_1"),
                                        map.get("tax_filer_2"),
                                        map.get("tax_filer_3"));
                        totalTemp = totalTemp + res;
                    }
                }
                quantity = Double.parseDouble(dataList.get(i).get("qty")) + Double.parseDouble("0");
                
                if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                    if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 0) {
                        
                        tax1 = map.get("tax1");
                        tax2 = map.get("tax2");
                        tax3 = map.get("tax3");
                        
                        
                    } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 1) {
                        tax1 = map.get("tax_filer_1");
                        tax2 = map.get("tax_filer_2");
                        tax3 = map.get("tax_filer_3");
                        
                    }
                    db.createReturnDetails(String.valueOf(oID), dataList.get(i).get("productId"),
                            String.format("%.2f", quantity), String.format("%.2f", quantity),
                            map.get("tradePrice"),/*map.get("discount1")*/dataList.get(i).get(
                                    "discount1"), map.get("discount2"), tradeOffer, map.get(
                                    "scheme"), "0", map.get("schemeVal"),
                            map.get("schemeProduct"), tax1, tax2, tax3, String.format("%.2f",
                                    res), "0", t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, map.get("mrp_price"));
                } else {
                    if (!dataList.get(i).get("productId").equals("0"))
                        db.createReturnDetails(String.valueOf(oID), dataList.get(i).get("productId"), String.format("%.2f", quantity), String.format("%.2f", quantity), map.get("tradePrice"),/*map.get("discount1")*/dataList.get(i).get("discount1"), map.get("discount2"), /*map.get("tradeOffer")*/tradeOffer, map.get("scheme"), "0", map.get("schemeVal"), map.get("schemeProduct"), tax1, tax2, tax3, String.format("%.2f", res), "0", t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, map.get("mrp_price"));
                }
            } else {
                quantity = Double.parseDouble(dataList.get(i).get("qty")) + Double.parseDouble("0");
                totalTemp = totalTemp + res;
                if (!dataList.get(i).get("productId").equals("0"))
                    db.createReturnDetails(String.valueOf(oID), dataList.get(i).get("productId"),
                            String.format("%.2f", quantity), String.format("%.2f", quantity), "0"
                            , /*"0"*/dataList.get(i).get("discount1"), "0", "0", "0", "0", "0",
                            "0", "0", "0", "0", "0", "0", t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, map.get("mrp_price"));
            }
            t_o_v = 0;
            d_v_1 = 0;
            d_v_2 = 0;
            t_type = 0;
            t_mrp_type = 0;
            t_val = 0;
        }
        
        
        db.CloseDb();
        return totalTemp;
    }
    
    private String[] getInventoryNameFromDb(String searchTerm, PosDB db) {
        
        // add items on the array dynamically
        db.OpenDb();
        List<MyObject> products = db.GetInventoryAutoCompleteData(searchTerm);
        
        db.CloseDb();
        int rowCount = products.size();
        
        String[] item = new String[rowCount];
        int x = 0;
        
        for (MyObject record : products) {
            
            item[x] = record.objectName;
            x++;
        }
        
        return item;
    }
    private List<MyObject> getInventoryNameFromDb( PosDB db) {

        // add items on the array dynamically
        db.OpenDb();
        List<MyObject> products = db.GetInventoryAutoCompleteData("");
        db.CloseDb();
        int rowCount = products.size();

        List<MyObject> item = new ArrayList<>();
        int x = 0;

        for (MyObject record : products) {

            item.add(record);
//            item[x] = record.objectName;
            x++;
        }

        return item;
    }

    private void initiatePopupWindow(View v, final String customerTypeId, final String productId,
                                     int incre, String quantity, String schemeValHidden, String discountValue, String discountValue2, String tradeOfferValue) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity
            
            View popUpView = activity.getLayoutInflater().inflate(R.layout.custom_details_popup, null);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            int widht = displaymetrics.widthPixels;
            int height = displaymetrics.heightPixels;
            
            PopupWindow popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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
            
            popupWindow.setBackgroundDrawable(new ColorDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            
            double Quantity = Double.parseDouble(quantity);
            
            //discount1Row.setVisibility(View.GONE);
            
            db.OpenDb();
            
            //tradePrice.setText(db.getTradePrice(customerTypeId, productId));
            // Previous work trying new
            //productId = db.getInventoryID(pro.getText().toString());
            Log.e("CheckPopUp", customerTypeId + " : " + productId);
            
            
            String[] val = db.getBrandAndProductTypeAndSubType(productId);
            
            
            String[] val1 = db.getCustomerTypeAndSubTypeID(SelectedCustomerId_InSRD);
            
            
            ArrayList<HashMap<String, String>> data = db.getPricingData(SelectedCustomerTypeId_InSRD,
                    SelectedCustomerId_InSRD, val[1], val[0], quantity,
                    productId, val1[0], val1[1], val[2]);
            Log.e("CheckPopUp", String.valueOf(data.size()));
            double result = 0;
            //double schemeValue = 0 , totalpieces2, totAmount, actualTotalAmount, tradeOfferValue, discount_1,discount_2, totalTax;
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    HashMap<String, String> map = data.get(i);
                    tradePrice.setText(map.get("tradePrice"));
                    discount1.setText(discountValue);
                    discount2.setText(discountValue2);
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        tradeOffer.setText(map.get("tradeOffer"));
                    } else {
                        tradeOffer.setText(tradeOfferValue);
                    }
                    //scheme.setText(schemeValHidden + " ( "+map.get("scheme") + " / " + map.get("schemeVal") +" )");
                    if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                        scheme.setText(schemeValHidden + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same");
                    } else {
                        scheme.setText(schemeValHidden + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct")));
                    }
                    
                    if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 0) {
                        
                        tax1.setText(map.get("tax1"));
                        tax2.setText(map.get("tax2"));
                        tax3.setText(map.get("tax3"));
                    } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 1) {
                        tax1.setText(map.get("tax_filer_1"));
                        tax2.setText(map.get("tax_filer_2"));
                        tax3.setText(map.get("tax_filer_3"));
                    }
                    
                    if (mobEmpDiscountType.equalsIgnoreCase("1"))
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get
                        ("discount1")*/discountValue, discountValue2, map.get("tradeOffer"),
                                map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get(
                                        "tax3"), map.get("tax_filer_1"), map.get("tax_filer_2"),
                                map.get("tax_filer_3"));
                    else
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get
                        ("discount1")*/map.get("discount1"), map.get("discount2"), tradeOfferValue, map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map.get("tax_filer_1"), map.get("tax_filer_2"),
                                map.get("tax_filer_3"));
                    
                    //double res = 10 * Double.parseDouble(map.get("tradePrice")) * Double.parseDouble(map.get("discount1")) * Double.parseDouble(map.get("discount2")) * Double.parseDouble(map.get("tradeOffer")) * Double.parseDouble( map.get("scheme")) * Double.parseDouble(map.get("tax1")) * Double.parseDouble(map.get("tax2"))* Double.parseDouble(map.get("tax3"));
                    subTotal.setText(String.format("%.2f", result));
                    //break;
                }
            }
            
            
            db.CloseDb();
            
            
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
        
        tp = Double.parseDouble(tradePrice);
        disc1 = Double.parseDouble(discount1);
        disc2 = Double.parseDouble(discount2);
        if (tradeOffer.equalsIgnoreCase("")) {
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
        
        t_o_v = tradeOfferValue;
        
        totAmount = totAmount - tradeOfferValue;
        afterAmmount = totAmount;
        
        discount_1 = disc1 / 100;
        discount_1 = discount_1 * totAmount;
        totAmount = totAmount - discount_1;
        
        d_v_1 = discount_1;
        
        
        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;
        
        d_v_2 = discount_2;
        
        
        t_type = isTaxMrp;
        t_mrp_type = tax_mrp;
        
        
        if (isTaxMrp == 2) {
            
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            
            if (tax_mrp == 0) {
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 0) {
                    
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                    
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 1) {
                    
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                    
                }
                
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }
            
            
        } else if (isTaxMrp == 3) {
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            if (tax_mrp == 0) {
                
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + (totalTax * totAmount);
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    t_val = actualTotalAmount;
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
                
                if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 0) {
                    
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    
                    t_val = (totalTax * afterAmmountMrp1);
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
                    
                } else if (db.CheckFilerNonFilerCust(SelectedCustomerId_InSRD) == 1) {
                    
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    
                    t_val = (totalTax * afterAmmountMrp1);
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
                    
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }
            
            
        }
        Log.e("pricingCalculation", totAmount + "");
        return totAmount;
        
    }
    
    private class ViewHolder {
        TextView txtDate;
        TextView txtOrderID;
        TextView txtCustomerName;
        /*TextView txtTotalAmount;*/
        TextView txtTotalNewAmount;
        
        ImageButton EditOrder, DeleteOrder, ViewOrder;
        
        View item_left;
        
        View item_right;
        
        LinearLayout MainLayout;
        
        long DateTimeLong;
        String custType;
        
    }
}