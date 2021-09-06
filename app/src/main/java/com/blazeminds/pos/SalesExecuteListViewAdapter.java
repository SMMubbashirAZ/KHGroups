package com.blazeminds.pos;

/*
  Created by Saad Kalim on 06-Apr-15.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.likebamboo.widget.SwipeListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.AreYouSure;
import static com.blazeminds.pos.Constant.EDIT_ORDER_ITEM;
import static com.blazeminds.pos.Constant.EDIT_ORDER_NEWQTY;
import static com.blazeminds.pos.Constant.EDIT_ORDER_OLDQTY;
import static com.blazeminds.pos.Constant.ORDER_CUST_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_DATE_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_ID_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_NEW_AMOUNT_COLUMN;
import static com.blazeminds.pos.Constant.populateSalesExecuteList;

public class SalesExecuteListViewAdapter extends BaseAdapter {
    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    PosDB db;
    String totalAmount = "";
    double totAmount = 0, discount = 0;
    
    SwipeListView lView;
    TextView noItemTxt;
    String DateTime;
    ArrayList<HashMap<String, String>> data;
    private int mRightWidth = 0;
    
    public SalesExecuteListViewAdapter(Context activity, int rightWidth, ArrayList<HashMap<String, String>> list, PosDB dba, SwipeListView listView, TextView NoItem, String dateTime) {
        super();
        this.activity = (Activity) activity;
        this.list = list;
        this.db = dba;
        this.lView = listView;
        this.noItemTxt = NoItem;
        this.DateTime = dateTime;
        mRightWidth = rightWidth;
        
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
        
        HashMap map = list.get(position);
        
        try {
            
            holder.txtOrderID.setText(map.get(ORDER_ID_COLUMN).toString());
            String dbDate = map.get(ORDER_DATE_COLUMN).toString();
            holder.txtCustomerName.setText(map.get(ORDER_CUST_COLUMN).toString());
            /*holder.txtTotalAmount.setText(map.get(ORDER_AMOUNT_COLUMN).toString());*/
            holder.txtTotalNewAmount.setText(map.get(ORDER_NEW_AMOUNT_COLUMN).toString());
            holder.custType = map.get("cust_type").toString();
            
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
                    
                    if (daysPassed >= 7) {
                        Toast.makeText(AppContextProvider.getContext(), "Edit Time Exceeded", Toast.LENGTH_SHORT).show();
                    } else {
                        EditOrder(activity, db, holder.txtOrderID.getText().toString(), lView, noItemTxt, holder.custType);
                    }
                    
                }
            });
            
            holder.DeleteOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AreYouSure(activity, db, 1, holder.txtOrderID.getText().toString(), lView, noItemTxt);
                }
            });
            
            holder.ViewOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        
        
        
        double day = hours / 24;
        
        
        
        return day;
        
    }
    
    public void EditOrder(final Context ctx, final PosDB db, final String orderID, final SwipeListView lView, final TextView noItemTxt, final String custType) {
        
        final Dialog dialog = new Dialog(ctx);
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_edit_order_execute);

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
        final ListView ItemListHeader = dialog.findViewById(R.id.ItemListH);
        final Button Update = dialog.findViewById(R.id.UpdateOrder);
        Button Dismiss = dialog.findViewById(R.id.Dismiss);
        Button ExecuteBtn = dialog.findViewById(R.id.Execute);
        final EditText notesTV = dialog.findViewById(R.id.notesTxtView);
        final TableRow BtnLay = dialog.findViewById(R.id.ButtonLayout);
        
        db.OpenDb();
        CustomerName.setText(db.getCustomerName4EditText(orderID));
        String dbDate = db.getDate4EditText(orderID);
        //data = db.getSalesExecuteForEditList( orderID );
        //SharedPreferences settings = activity.getSharedPreferences("MY_PREF", MODE_PRIVATE);
        //totalAmount = settings.getString("key","");
        notesTV.setText(db.getNotes4EditText(orderID));
        Total2.setText(db.getSaleOrderTotal4EditText(orderID));
        discountTxt.setText(db.getSaleOrderDiscount4EditText(orderID) + " %");
        data = db.getOrderDetailExecuteForEditList(orderID);
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
        
        HashMap<String,String> temp = new HashMap<>();
        
        temp.put(EDIT_ORDER_ITEM, "Item Name");
        temp.put(EDIT_ORDER_OLDQTY, "Qty");
        temp.put(EDIT_ORDER_NEWQTY, "Qty_EXE");
        
        Hlist.add(temp);
        
        
        EditOrderListViewHeaderAdapter adapterH = new EditOrderListViewHeaderAdapter(ctx, Hlist);
        ItemListHeader.setAdapter(adapterH);
        
        
        EditOrderSalesExecuteListViewAdapter adapter1 = new EditOrderSalesExecuteListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 1, custType);
        
        ItemList.setAdapter(adapter1);
        
        
        final ArrayList<HashMap<String, String>> finalData = data;
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                db.OpenDb();
                db.updateSalesOrderNewVales(Integer.parseInt(orderID), GetValuesTOUpdate(finalData, orderID), notesTV.getText().toString());
                /*db.UpdateChangesSalesOrder( Integer.parseInt(orderID), 1 );*/
                
                
                data = null;
                //data = db.getSalesExecuteForEditList( orderID );
                data = db.getOrderDetailExecuteForEditList(orderID);
                for (int i = 0; i < finalData.size(); i++) {
                    
                    HashMap map = finalData.get(i);
                    
                    String itemID = map.get("productId").toString();
                    String newQTY = map.get("qty").toString();
                    String exeQTY = map.get("qtyExe").toString();
                    if (newQTY.equalsIgnoreCase("")) {
                        newQTY = "0";
                    }
                    if (exeQTY.equalsIgnoreCase("")) {
                        exeQTY = "0";
                    }
                    ArrayList<HashMap<String, String>> dataDetail = db.getOrderDetailAgainstProduct(orderID, itemID);
                    HashMap<String, String> detailsMap = new HashMap<>();
                    double result = 0, scheme = 0, schemeQty = 0, quantity = 0, quantityExe = 0, schemeQuantity = 0;
                    if (dataDetail.size() > 0) {
                        for (int j = 0; j < dataDetail.size(); j++) {
                            detailsMap = dataDetail.get(j);
                            result = pricingCalculation(Double.parseDouble(exeQTY), detailsMap.get("tradePrice"), detailsMap.get("discount1"), detailsMap.get("discount2"), detailsMap.get("tradeOffer"), detailsMap.get("scheme"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"));
                            totAmount += result;
                            if (!detailsMap.get("scheme").equalsIgnoreCase("") && Double.parseDouble(detailsMap.get("scheme")) != 0) {
                                scheme = Double.parseDouble(exeQTY) / Double.parseDouble(detailsMap.get("scheme"));
                                schemeQty = Double.parseDouble(detailsMap.get("schemeFormula")) / scheme;
                                schemeQuantity = Math.floor(scheme) * Double.parseDouble(detailsMap.get("schemeFormula"));
                            } else {
                                scheme = 0;
                                schemeQuantity = 0;
                            }
                            quantity = Double.parseDouble(newQTY) + schemeQuantity;
                            quantityExe = Double.parseDouble(exeQTY) + schemeQuantity;
                        }
//                        quantity = Double.parseDouble(newQTY ) + (scheme * schemeQty);
//                        quantityExe = Double.parseDouble(exeQTY ) + (scheme * schemeQty);
                        
                        db.updateOrderDetails(Integer.parseInt(orderID), itemID, String.format("%.2f", quantity), String.format("%.2f", quantityExe), detailsMap.get("tradePrice"), detailsMap.get("discount1"), detailsMap.get("discount2"), detailsMap.get("tradeOffer"), detailsMap.get("scheme"), String.valueOf(schemeQuantity), detailsMap.get("schemeFormula"), detailsMap.get("schemeProduct"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"), String.format("%.2f", result));
                    }
                    
                }
                
                //SharedPreferences settings = activity.getSharedPreferences("MY_PREF", MODE_PRIVATE);
                //totalAmount = settings.getString("key","");
                double afterDivideDiscount = 0, afterSubtractDiscount = 0;
                if (custType.equalsIgnoreCase("1")) {
                    discount = db.getDiscountForSaleOrder(totAmount);
                    if (discount > 0) {
                        afterDivideDiscount = discount / 100;
                        afterSubtractDiscount = 1 - afterDivideDiscount;
                        totAmount = totAmount * afterSubtractDiscount;
                    }
                }
                //totalTxt.setText(totalAmount);
                db.updateSalesOrderTotalExecute(Integer.parseInt(orderID), String.valueOf(totAmount), String.valueOf(discount));
                
                db.CloseDb();
                
                Log.d("Concat", GetValuesTOUpdate(finalData, orderID));
                
                
                EditOrderListViewAdapter adapter1 = new EditOrderListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 1, "");
                
                ItemList.setAdapter(adapter1);
                
                populateSalesExecuteList(lView, db, ctx, noItemTxt, DateTime);
                
                dialog.dismiss();
            }
        });
        
        //final ArrayList<HashMap<String, String>> finalDataExe = data;
        ExecuteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ctx, "Exe Done",Toast.LENGTH_SHORT).show();
                
                String notesTxt = notesTV.getText().toString();
                AreYouSureExecute(ctx, db, orderID, lView, noItemTxt, finalData, ItemList, BtnLay, notesTxt, dialog, ItemListHeader, custType);
                
                
            }
        });
        
        Dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                populateSalesExecuteList(lView, db, ctx, noItemTxt, DateTime);
                dialog.dismiss();
                
            }
        });

/*
        DialogLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    DialogLayout.setFocusable(true);

                }
                return false;
            }
        });
*/



/*
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    DialogLayout.setFocusable(true);

                    return true;
                }
                return false;
            }
        });
*/
        
        dialog.show();
        
        
    }
    
    public void AreYouSureExecute(final Context ctx, final PosDB db, final String orderID, final SwipeListView lView, final TextView noItemTxt, final ArrayList<HashMap<String, String>> finalData, final ListView ItemList, final TableRow BtnLay, final String notesTxt, final Dialog dialogParent, final ListView ItemListHeader, final String custType) {
        
        final Dialog dialog = new Dialog(ctx);
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_are_you_sure);
        
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
        
        
        //  dialog.setTitle(ItemName);
        TextView msg = dialog.findViewById(R.id.msg);
        Button Yes = dialog.findViewById(R.id.YesBtn);
        Button No = dialog.findViewById(R.id.NoBtn);
        
        msg.setText("This action can't be reversed.");
        msg.setVisibility(View.VISIBLE);
        
        dialog.show();
        
        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                db.OpenDb();
                db.executeUpdateSalesOrderNewVales(Integer.parseInt(orderID), GetValuesTOUpdate(finalData, orderID), notesTxt);
                /*db.UpdateChangesSalesOrder( Integer.parseInt(orderID), 1 );*/
                
                
                data = null;
                //data = db.getSalesExecuteForEditList( orderID );
                data = db.getOrderDetailExecuteForEditList(orderID);
                for (int i = 0; i < finalData.size(); i++) {
                    
                    HashMap map = finalData.get(i);
                    
                    String itemID = map.get("productId").toString();
                    String newQTY = map.get("qty").toString();
                    String exeQTY = map.get("qtyExe").toString();
                    if (newQTY.equalsIgnoreCase("")) {
                        newQTY = "0";
                    }
                    if (exeQTY.equalsIgnoreCase("")) {
                        exeQTY = "0";
                    }
                    ArrayList<HashMap<String, String>> dataDetail = db.getOrderDetailAgainstProduct(orderID, itemID);
                    HashMap<String, String> detailsMap = new HashMap<>();
                    double result = 0, scheme = 0, schemeQty = 0, quantity = 0, quantityExe = 0, schemeQuantity = 0;
                    if (dataDetail.size() > 0) {
                        for (int j = 0; j < dataDetail.size(); j++) {
                            detailsMap = dataDetail.get(j);
                            result = pricingCalculation(Double.parseDouble(exeQTY), detailsMap.get("tradePrice"), detailsMap.get("discount1"), detailsMap.get("discount2"), detailsMap.get("tradeOffer"), detailsMap.get("scheme"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"));
                            totAmount += result;
                            if (!detailsMap.get("scheme").equalsIgnoreCase("") && Double.parseDouble(detailsMap.get("scheme")) != 0) {
                                scheme = Double.parseDouble(exeQTY) / Double.parseDouble(detailsMap.get("scheme"));
                                schemeQty = Double.parseDouble(detailsMap.get("schemeFormula")) / scheme;
                                schemeQuantity = Math.floor(scheme) * Double.parseDouble(detailsMap.get("schemeFormula"));
                                
                            } else {
                                scheme = 0;
                                schemeQuantity = 0;
                            }
                            
                            quantity = Double.parseDouble(newQTY) + schemeQuantity;
                            quantityExe = Double.parseDouble(exeQTY) + schemeQuantity;
                        }
                        /*quantity = Double.parseDouble(newQTY) + (scheme * schemeQty);
                        quantityExe = Double.parseDouble(exeQTY) + (scheme * schemeQty);*/
                        
                        db.updateOrderDetails(Integer.parseInt(orderID), itemID, String.format("%.2f", quantity), String.format("%.2f", quantityExe), detailsMap.get("tradePrice"), detailsMap.get("discount1"), detailsMap.get("discount2"), detailsMap.get("tradeOffer"), detailsMap.get("scheme"), String.valueOf(schemeQuantity), detailsMap.get("schemeFormula"), detailsMap.get("schemeProduct"), detailsMap.get("tax1"), detailsMap.get("tax2"), detailsMap.get("tax3"), String.format("%.2f", result));
                    }
                    
                }
                
                //SharedPreferences settings = activity.getSharedPreferences("MY_PREF", MODE_PRIVATE);
                //totalAmount = settings.getString("key","");
                //totalTxt.setText(totalAmount);
                double afterDivideDiscount = 0, afterSubtractDiscount = 0;
                if (custType.equalsIgnoreCase("1")) {
                    discount = db.getDiscountForSaleOrder(totAmount);
                    if (discount > 0) {
                        afterDivideDiscount = discount / 100;
                        afterSubtractDiscount = 1 - afterDivideDiscount;
                        totAmount = totAmount * afterSubtractDiscount;
                    }
                }
                db.updateSalesOrderTotalExecute(Integer.parseInt(orderID), String.valueOf(totAmount), String.valueOf(discount));
                
                db.CloseDb();
                
                Log.d("Concat", GetValuesTOUpdate(finalData, orderID));
                
                
                EditOrderListViewAdapter adapter1 = new EditOrderListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 1, "");
                
                ItemList.setAdapter(adapter1);
                
                populateSalesExecuteList(lView, db, ctx, noItemTxt, DateTime);
                
                //dialog.dismiss();
                dialogParent.dismiss();
                Toast.makeText(AppContextProvider.getContext(), "Order Executed", Toast.LENGTH_SHORT).show();
                
                dialog.dismiss();
                
            }
        });
        
        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        
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
        TextView Total2 = dialog.findViewById(R.id.Total);
        TextView notesTV = dialog.findViewById(R.id.notesTxtView);
        TextView reasonTV = dialog.findViewById(R.id.reasonTxtView);
        TableRow reasonRow = dialog.findViewById(R.id.reasonRow);
        View view = dialog.findViewById(R.id.view);
        final ListView ItemList = dialog.findViewById(R.id.ItemList);
        ListView ItemListHeader = dialog.findViewById(R.id.ItemListH);
        Button Dismiss = dialog.findViewById(R.id.DismissBtn);
        Button printBtn = dialog.findViewById(R.id.PrintBtn);
        view.setVisibility(View.GONE);
        printBtn.setVisibility(View.GONE);
        Dismiss.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        //Dismiss.setGravity(View.TEXT_ALIGNMENT_CENTER);
        final TableRow BtnLay = dialog.findViewById(R.id.ButtonLayout);
        reasonRow.setVisibility(View.GONE);
        db.OpenDb();
        CustomerName.setText(db.getCustomerName4EditText(orderID));
        String dbDate = db.getDate4EditText(orderID);
        Total2.setText(db.getSaleOrderTotal4EditText(orderID));
        //data = db.getSalesOrderForEditList( orderID );
        data = db.getOrderDetailForEditList(orderID);
        notesTV.setText(db.getNotes4EditText(orderID));
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
        
        HashMap<String,String> temp = new HashMap<>();
        
        temp.put(EDIT_ORDER_ITEM, "Item Name");
        temp.put(EDIT_ORDER_OLDQTY, "Qty");
        temp.put(EDIT_ORDER_NEWQTY, "Qty_Exe");
        
        Hlist.add(temp);
        
        
        EditOrderListViewHeaderAdapter adapterH = new EditOrderListViewHeaderAdapter(ctx, Hlist);
        ItemListHeader.setAdapter(adapterH);
        
        
        EditOrderSalesExecuteListViewAdapter adapter1 = new EditOrderSalesExecuteListViewAdapter(ctx, data, db, orderID, ItemList, BtnLay, 2);
        
        ItemList.setAdapter(adapter1);
        
        
        Dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                dialog.dismiss();
                
            }
        });
        
        
        dialog.show();
        
        
    }
    
    private String GetValuesTOUpdate(ArrayList<HashMap<String, String>> list, String orderID) {
        
        String result = "";
        
        for (int i = 0; i < list.size(); i++) {
            
            HashMap map = list.get(i);
            
            String itemID = map.get("productId").toString();
            String oldQTY = map.get("qty").toString();
            String newQTY = map.get("qtyExe").toString();
            String exeQTY = map.get("qtyExe").toString();
            
            
            if (itemID.isEmpty()) {
                result = result + "0" + "!!";
            } else {
                result = result + itemID + "!!";
            }
            
            if (oldQTY.isEmpty()) {
                result = result + "0" + "!!";
            } else {
                result = result + oldQTY + "!!";
            }
            
            if (newQTY.isEmpty()) {
                result = result + "0" + "!!";
                
            } else {
                result = result + newQTY + "!!";
                
            }
            
            if (exeQTY.isEmpty()) {
                result = result + "0" + "!!!";
            } else {
                result = result + exeQTY + "!!!";
            }
            
            
        }
        result = result + orderID + "--";
        
        return result;
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