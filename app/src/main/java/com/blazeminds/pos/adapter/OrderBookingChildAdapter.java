package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.activities.OrderBookingActivity;
import com.blazeminds.pos.model.OnvalueChanged;
import com.blazeminds.pos.model.ProductModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderBookingChildAdapter extends RecyclerView.Adapter<OrderBookingChildAdapter.MyViewHolder> {

    Activity activity;

    public static PopupWindow popupWindowOrderBooking;
    private final ArrayList<ProductModel> productList;
    public static ArrayList<ProductModel> selectedProductList = new ArrayList<>();
    private String CustId,CustTypeId;
    private OnvalueChanged onvalueChanged;
    private PosDB db;

    private double t_o_v = 0, d_v_1 = 0, d_v_2 = 0, t_type = 0, t_mrp_type = 0, t_val = 0;

    ArrayList<HashMap<String, String>> result = new ArrayList<>();
    HashMap<String, String> map2 = new HashMap<>();
    String mobEmpDiscountType = "", mobEmpSaleType = "";
    double disc=0.0,respons=0.0,trad=0.0;

    public OrderBookingChildAdapter(Activity activity, ArrayList<ProductModel> productList, String custId,
                                    String custTypeId, PosDB db,OnvalueChanged onvalueChanged) {
        this.activity = activity;
        this.productList = productList;
        this.db=db;
        this.CustId= custId;
        this.onvalueChanged=onvalueChanged;
        this.CustTypeId= custTypeId;
    }

    public ArrayList<ProductModel> getList() {
        for (int i = 0; i < selectedProductList.size(); i++) {
            if (selectedProductList.get(i).getProd_qty() == 0) {
                selectedProductList.remove(i);
            }
        }
        return selectedProductList;
    }
    
    
    @Override
    public MyViewHolder onCreateViewHolder(  ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_child_order_booking_recylerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(  final MyViewHolder holder,final  int position) {

        db.OpenDb();
        mobEmpSaleType = db.getMobEmpSaleType();
//        for (int i = 0; i < selectedProductList.size(); i++) {
//            if (selectedProductList.get(i).getProd_id().equals(productList.get(position).getProd_id())) {
//                holder.edt_order_qty.setText(selectedProductList.get(i).getProd_qty());
//                holder.tv_total_order_unit.setText(selectedProductList.get(i).getProd_qty());
//            }
//        }

//        BulkTradeOfferTableRow = rootView.findViewById(R.id.BulkTradeOfferTableRow);
        mobEmpDiscountType = db.getMobEmpDiscountType();
        String[] val =
                db.getBrandAndProductTypeAndSubType(productList.get(position).getProd_id());
        String[] val1 = db.getCustomerTypeAndSubTypeID(CustId);
        double schemeValue = 0;
        ArrayList<HashMap<String, String>> data = db.getPricingData(CustTypeId, CustId,
                val[1], val[0], "0",
                productList.get(position).getProd_id(), val1[0], val1[1], val[2]);

        if (data.size() > 0) {
            for (int i = 0; i < data.size(); i++) {
                HashMap<String, String> map = data.get(i);
                disc=Double.parseDouble(map.get("discount2"));
                trad=Double.parseDouble(map.get("tradePrice"));

            }
        }
        final ProductModel product =  productList.get(position);
        product.setDiscount(String.valueOf(disc));
        product.setTradePrice(String.valueOf(trad));
        db.CloseDb();


        String productName =productList.get(position).getProd_name();

        String gtemp=productName.replace("(","");
        String gtemp2=gtemp.replace(")","");
        holder.tv_product_name.setText(gtemp2);
        holder.edt_order_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // double currencCartPrice = onvalueChanged.getCartValue();

                int productIndex = selectedProductList.indexOf(product);

                if (s.length() > 0) {
                    if(s.charAt(0) == '.'){
                        
                    }else{
                        db.OpenDb();

 


                        String[] val =
                                db.getBrandAndProductTypeAndSubType(product.getProd_id());

                        String[] val1 = db.getCustomerTypeAndSubTypeID(CustId);


                        double schemeValue = 0;
                        ArrayList<HashMap<String, String>> data = db.getPricingData(CustTypeId, CustId,
                                val[1], val[0], s.toString(),
                                product.getProd_id(), val1[0], val1[1], val[2]);
                        double result = 0;
                        //double schemeValue = 0 , totalpieces2, totAmount, actualTotalAmount, tradeOfferValue, discount_1,discount_2, totalTax;
                        if (data.size() > 0) {
                            HashMap<String,String> newMap=data.get(0);
                            product.setTradePrice(newMap.get("tradePrice"));
                            product.setTradeOffer(newMap.get("tradeOffer"));
                            product.setTradeOffer(newMap.get("tradeOffer"));
                            if (newMap.get("multi").equals("0") && !newMap.get("scheme").equalsIgnoreCase("") &&
                                    Double.parseDouble(newMap.get("scheme")) != 0){
                                if (!newMap.get("schemeVal").equalsIgnoreCase("") &&
                                        Double.parseDouble(newMap.get("schemeVal")) != 0){
                                    schemeValue = (Math.floor(Double.parseDouble(s.toString()) / Double.parseDouble(newMap.get("scheme"))))
                                            * Double.parseDouble(newMap.get("schemeVal"));
                                }
                            }else{
                                schemeValue=0;
                            }

                            product.setSchemeValue(String.valueOf(schemeValue));
                            String SchemeTxt="",tax1="", tax2 = "", tax3="";
                            if (db.CheckFilerNonFilerCust(CustId) == 0) {
                                tax1=(newMap.get("tax1"));
                                tax2=(newMap.get("tax2"));
                                tax3=(newMap.get("tax3"));
                            }else{
                                tax1=(newMap.get("tax_filer_1"));
                                tax2=(newMap.get("tax_filer_2"));
                                tax3=(newMap.get("tax_filer_3"));
                            }

                            if (newMap.get("schemeProduct").equalsIgnoreCase("0")) {
                                SchemeTxt=((schemeValue + " ( " + newMap.get("scheme") + " / " + newMap.get("schemeVal") + " ) / Same"));
                            } else {
                                SchemeTxt=((schemeValue + " ( " + newMap.get("scheme") + " / " + newMap.get("schemeVal") + " ) / " + db.getSelectedInventoryName(newMap.get("schemeProduct"))));
                            }
                            product.setSchemeTxt(String.valueOf(SchemeTxt));
                            product.setTax1(tax1);
                            product.setTax2(tax2);
                            product.setTax3(tax3);
                            product.setTaxf1(newMap.get("tax_filer_1"));
                            product.setTaxf2(newMap.get("tax_filer_2"));
                            product.setTaxf3(newMap.get("tax_filer_3"));
                            product.setIsTaxMrp(newMap.get("tax_mrp"));
                            product.setIs_taxable(newMap.get("is_taxable"));
                            product.setMrp_price(newMap.get("mrp_price"));

                        }


                        db.CloseDb();
                    }
                    if (s.charAt(0) == '.') {
                        holder.edt_order_qty.setError("Wrong Format");

//                        formatError = false;
                    } else {

                        if ( productIndex == -1 ){
                            ProductModel tempProduct = product;
                            tempProduct.setProd_qty(Integer.parseInt( s.toString() ));
                            selectedProductList.add(tempProduct);
                        }else {
                            ProductModel tempProduct = product;
                            tempProduct.setProd_qty(Integer.parseInt( s.toString() ));

                            selectedProductList.set(productIndex,tempProduct);
                        }

                        holder.tv_total_order_unit.setText(s);
//                        OrderBookingActivity.total_price+=price(disc,s.toString(),
//                                Double.parseDouble(productList.get(position).getProd_price()),position);

                        double price = calculateTotalPrice();
                        onvalueChanged.setCartValue(price);
//                        double totalPrice = currencCartPrice + price(disc,s.toString(),
//                                Double.parseDouble(productList.get(position).getProd_price()),position);
//                        onvalueChanged.setCartValue(totalPrice);
                      ;//ChangeValue();





//                        notifyDataSetChanged();
                    }
                }else{
                    holder.tv_total_order_unit.setText(s);
//                    double  result = OrderBookingActivity.total_price - price(disc,"0",
//                            Double.parseDouble(productList.get(position).getProd_price()),position);
//                    double totalPrice = currencCartPrice - price(disc,"1",
//                            Double.parseDouble(productList.get(position).getProd_price()),position);
                    if (productIndex != -1) {
                        selectedProductList.remove(productIndex);
                    }
                    double price = calculateTotalPrice();
                    onvalueChanged.setCartValue(price);
//                    OrderBookingActivity.total_price =price(disc,"0",
//                            Double.parseDouble(productList.get(position).getProd_price()),position);
                   // onvalueChanged.OnChangeValue();
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.tv_product_price.setText("Rs."+productList.get(position).getProd_price() +"/-");
        holder.iv_product_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.OpenDb();
                int discount1 = -1;

                String quantity= "";
                if (holder.edt_order_qty.getText().toString().equals(null) || holder.edt_order_qty.getText().toString().equals("")) {

                    quantity="0";
                }else{
                    quantity=holder.edt_order_qty.getText().toString();
                }
                int maxDiscount = db.getMobUserMaxDiscount();

                if (mobEmpDiscountType.equalsIgnoreCase("1")){
                    String[] val =
                            db.getBrandAndProductTypeAndSubType(productList.get(position).getProd_id());
                    String[] val1 = db.getCustomerTypeAndSubTypeID(CustId);

                    result = db.getPricingData(CustTypeId, CustId,
                            val[1], val[0], quantity,
                            productList.get(position).getProd_id(), val1[0], val1[1],
                            val[2]);

                    if (result.size() > 0 ){
                        for (int i = 0; i < result.size(); i++) {
                            map2 = result.get(i);
                        }

                    }

                }

                initiatePopupWindow(view, CustTypeId, productList.get(position).getProd_id(), quantity, "0", "0");


            }
        });

    }


    public double calculateTotalPrice( ){

        double price = 0.0;
        for (int i = 0; i < selectedProductList.size(); i++) {

            ProductModel model=selectedProductList.get(i);
        price+= pricingCalculation(Double.parseDouble(String.valueOf(model.getProd_qty())),Integer.parseInt(model.getIsTaxMrp())
                ,Integer.parseInt(model.getTax_mrp()==null?"0":model.getTax_mrp()) ,Integer.parseInt(model.getMrp_price()),model.getTradePrice(),"0",model.
                getDiscount(),model.getTradeOffer(),model.getSchemeValue(),model.getTax1(),model.getTax2(),model.getTax3(),model.getTaxf1(),model.getTaxf2(),model.getTaxf3());
//            price +=  price(Double.parseDouble(selectedProductList.get(i).getDiscount()),String.valueOf(selectedProductList.get(i).getProd_qty()),
//                    Double.parseDouble(selectedProductList.get(i).getProd_price()),Double.parseDouble(selectedProductList.get(i).getTradePrice()));
        }
        return  price;
    }
    private double pricingCalculation(double Quantity,
                                      int isTaxMrp,
                                      int tax_mrp,
                                      int mrp_price,
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
//        int isTaxMrp =is_taxable;
//        int tax_mrp = Integer.parseInt(dataMap.get("tax_mrp"));
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

                if (db.CheckFilerNonFilerCust(CustId) == 0) {

                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;

                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {

                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;

                }

                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {

                if (db.CheckFilerNonFilerCust(CustId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = (Quantity * mrp_price) * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = (Quantity * mrp_price) * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }


        } else if (isTaxMrp == 3) {
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            if (tax_mrp == 0) {


                if (db.CheckFilerNonFilerCust(CustId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    t_val = (totalTax * totAmount);
                    totAmount = totAmount + (totalTax * totAmount);
                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    t_val = (totalTax * totAmount);
                    totAmount = totAmount + (totalTax * totAmount);
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {


                afterAmmountMrp = Quantity * (mrp_price - to);

                discount_11 = disc1 / 100;
                discount_11 = discount_11 * afterAmmountMrp;
                afterAmmountMrp1 = afterAmmountMrp - discount_11;

                discount_22 = disc2 / 100;
                discount_22 = discount_22 * afterAmmountMrp1;
                afterAmmountMrp1 = afterAmmountMrp1 - discount_22;

         /*       actualTotalAmount = (afterAmmountMrp) - discount_1;

                actualTotalAmount = actualTotalAmount - discount_2;*/

                if (db.CheckFilerNonFilerCust(CustId) == 0) {

                    totalTax = (tax_1 + tax_2 + tax_3) / 100;

                    t_val = (totalTax * afterAmmountMrp1);
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));

                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {

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


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name,tv_product_price,tv_total_order_unit;
        ImageView iv_product_details;
        EditText edt_order_qty;
//        LinearLayout layoutTripDetails,layout_parent;

        public MyViewHolder(View view) {
            super(view);

//            layoutTripDetails = view.findViewById(R.id.layoutTripDetails);
            tv_product_name = view.findViewById(R.id.tv_product_name);
            tv_product_price = view.findViewById(R.id.tv_product_price);
            tv_total_order_unit = view.findViewById(R.id.tv_total_order_unit);
            iv_product_details = view.findViewById(R.id.iv_product_details);
            edt_order_qty = view.findViewById(R.id.edt_order_qty);
//            tvTripTitle = view.findViewById(R.id.tvTripTitle);
//            layout_parent = view.findViewById(R.id.layout_parent);


//            ivIndicator = view.findViewById(R.id.ivIndicator);

        }
    }

    private void initiatePopupWindow(View v, final String customerTypeId, final String productId,
                                     String quantity, String discountValue,
                                     String tradeOfferValue) {
        try {
            //We need to get the instance of the LayoutInflater, use the context of this activity

            View popUpView = activity.getLayoutInflater().inflate(R.layout.custom_details_popup, null);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displaymetrics);
            int widht = displaymetrics.widthPixels;
            int height = displaymetrics.heightPixels;

            popupWindowOrderBooking = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
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

            popupWindowOrderBooking.setBackgroundDrawable(new ColorDrawable());
            popupWindowOrderBooking.setOutsideTouchable(true);
            popupWindowOrderBooking.showAtLocation(v, Gravity.CENTER, 0, 0);

            double Quantity = Double.parseDouble(quantity.equals("") ? "0" : quantity);

            //discount1Row.setVisibility(View.GONE);

            db.OpenDb();


            if (discountValue.equals("Disc")) {
                discountValue = "0";
            }


            String[] val =
                    db.getBrandAndProductTypeAndSubType(productId);

            String[] val1 = db.getCustomerTypeAndSubTypeID(CustId);


            double schemeValue = 0;
            ArrayList<HashMap<String, String>> data = db.getPricingData(CustTypeId, CustId,
                    val[1], val[0], quantity,
                    productId, val1[0], val1[1], val[2]);
            double result = 0;
            //double schemeValue = 0 , totalpieces2, totAmount, actualTotalAmount, tradeOfferValue, discount_1,discount_2, totalTax;
            if (data.size() > 0) {
                for (int i = 0; i < data.size(); i++) {
                    HashMap<String, String> map = data.get(i);
                    tradePrice.setText(map.get("tradePrice"));
                    discount1.setText(discountValue);
                    discount2.setText(map.get("discount2"));
                    if (mobEmpDiscountType.equalsIgnoreCase("1")) {
                        tradeOffer.setText(map.get("tradeOffer"));
                    } else {
                        tradeOffer.setText(tradeOfferValue);
                    }
                    //scheme.setText(schemeValHidden + " ( "+map.get("scheme") + " / " + map.get("schemeVal") +" )");

                    schemeValue = (Math.floor(Double.parseDouble(quantity) / Double.parseDouble(map.get(
                            "scheme")))) * Double.parseDouble(map.get("schemeVal"));
                    if (map.get("schemeProduct").equalsIgnoreCase("0")) {
                        scheme.setText((schemeValue + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / Same"));
                    } else {
                        scheme.setText((schemeValue + " ( " + map.get("scheme") + " / " + map.get("schemeVal") + " ) / " + db.getSelectedInventoryName(map.get("schemeProduct"))));
                    }

                    if (db.CheckFilerNonFilerCust(CustId) == 0) {

                        tax1.setText(map.get("tax1"));
                        tax2.setText(map.get("tax2"));
                        tax3.setText(map.get("tax3"));
                    } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
                        tax1.setText(map.get("tax_filer_1"));
                        tax2.setText(map.get("tax_filer_2"));
                        tax3.setText(map.get("tax_filer_3"));
                    }


                    if (mobEmpDiscountType.equalsIgnoreCase("1"))
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get("discount1")*/discountValue, map.get("discount2"), map.get("tradeOffer"), map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));
                    else
                        result = pricingCalculation(map, Quantity, map.get("tradePrice"),/* map.get("discount1")*/map.get("discount1"), map.get("discount2"), tradeOfferValue, map.get("scheme"), map.get("tax1"), map.get("tax2"), map.get("tax3"), map2.get("tax_filer_1"), map2.get("tax_filer_2"), map2.get("tax_filer_3"));

                    //double res = 10 * Double.parseDouble(map.get("tradePrice")) * Double.parseDouble(map.get("discount1")) * Double.parseDouble(map.get("discount2")) * Double.parseDouble(map.get("tradeOffer")) * Double.parseDouble( map.get("scheme")) * Double.parseDouble(map.get("tax1")) * Double.parseDouble(map.get("tax2"))* Double.parseDouble(map.get("tax3"));
                    subTotal.setText((String.format("%.2f", result)));
                    //break;
                }
            }


            db.CloseDb();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private double price(double disc2,String quantity,double tp ,double trade){
        double discount_2=0,totAmount=0,tradeOffer=0;
       // productList.get(pos).setProd_qty(Integer.parseInt(quantity));
        totAmount= Double.parseDouble(quantity) * tp;

        tradeOffer = trade *  Double.parseDouble(quantity);
        totAmount = totAmount - tradeOffer;
        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;
        Log.d("TAG", "price: "+totAmount);
        return totAmount;


    }

    private double pricingCalculation(HashMap<String, String> dataMap,
                                      double Quantity,
                                      String tradePrice,
                                      String discount1,
                                      String discount2, String tradeOffer, String scheme,
                                      String tax1, String tax2, String tax3, String taxf1,
                                      String taxf2, String taxf3) {

//        double tp, disc1, disc2, to, schem, tax_1, tax_2, tax_3, tax_f_1, tax_f_2, tax_f_3;
//        double schemeValue = 0, totalpieces2, totAmount, actualTotalAmount, tradeOfferValue,
//                discount_1, discount_11, discount_2, discount_22, totalTax, afterAmmount,
//                afterAmmountMrp, afterAmmountMrp1;
//        tp = Double.parseDouble(tradePrice);
//        disc1 = Double.parseDouble(discount1);
//        disc2 = Double.parseDouble(discount2);
//        if (tradeOffer.equalsIgnoreCase("")) {
//            to = 0;
//        } else
//            to = Double.parseDouble(tradeOffer);
//        schem = Double.parseDouble(scheme);
//        int isTaxMrp = Integer.parseInt(dataMap.get("is_taxable"));
//        int tax_mrp = Integer.parseInt(dataMap.get("tax_mrp"));
//        tax_1 = Double.parseDouble(tax1);
//        tax_2 = Double.parseDouble(tax2);
//        tax_3 = Double.parseDouble(tax3);
//
//        tax_f_1 = Double.parseDouble(taxf1);
//        tax_f_2 = Double.parseDouble(taxf2);
//        tax_f_3 = Double.parseDouble(taxf3);
//
//        if (schem != 0) {
//            schemeValue = Quantity / schem;
//        }
//
//        totalpieces2 = Quantity + schemeValue;
//
//
//        totAmount = Quantity * tp;
//
//        actualTotalAmount = totalpieces2 * tp;
//        tradeOfferValue = to * Quantity;
//
//        t_o_v = tradeOfferValue;
//
//        totAmount = totAmount - tradeOfferValue;
//        afterAmmount = totAmount;
//
//        discount_1 = disc1 / 100;
//        discount_1 = discount_1 * totAmount;
//        totAmount = totAmount - discount_1;
//
//        d_v_1 = discount_1;
//
//
//        discount_2 = disc2 / 100;
//        discount_2 = discount_2 * totAmount;
//        totAmount = totAmount - discount_2;
//
//        d_v_2 = discount_2;
//
//
//        t_type = isTaxMrp;
//        t_mrp_type = tax_mrp;
//
//
//        if (isTaxMrp == 2) {
//
//            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
//
//            if (tax_mrp == 0) {
//
//                if (db.CheckFilerNonFilerCust(CustId) == 0) {
//
//                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
//                    actualTotalAmount = actualTotalAmount * totalTax;
//                    t_val = actualTotalAmount;
//                    totAmount = totAmount + actualTotalAmount;
//
//                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
//
//                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
//                    actualTotalAmount = actualTotalAmount * totalTax;
//                    t_val = actualTotalAmount;
//                    totAmount = totAmount + actualTotalAmount;
//
//                }
//
//                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
//            } else if (tax_mrp == 1) {
//
//                if (db.CheckFilerNonFilerCust(CustId) == 0) {
//                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
//                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
//                    t_val = actualTotalAmount;
//                    totAmount = totAmount + actualTotalAmount;
//                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
//                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
//                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
//                    t_val = actualTotalAmount;
//                    totAmount = totAmount + actualTotalAmount;
//                }
//                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
//            }
//
//
//        } else if (isTaxMrp == 3) {
//            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
//            if (tax_mrp == 0) {
//
//
//                if (db.CheckFilerNonFilerCust(CustId) == 0) {
//                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
//                    t_val = (totalTax * totAmount);
//                    totAmount = totAmount + (totalTax * totAmount);
//                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
//                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
//                    t_val = (totalTax * totAmount);
//                    totAmount = totAmount + (totalTax * totAmount);
//                }
//                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
//            } else if (tax_mrp == 1) {
//
//
//                afterAmmountMrp = Quantity * (Integer.parseInt(dataMap.get("mrp_price")) - to);
//
//                discount_11 = disc1 / 100;
//                discount_11 = discount_11 * afterAmmountMrp;
//                afterAmmountMrp1 = afterAmmountMrp - discount_11;
//
//                discount_22 = disc2 / 100;
//                discount_22 = discount_22 * afterAmmountMrp1;
//                afterAmmountMrp1 = afterAmmountMrp1 - discount_22;
//
//         /*       actualTotalAmount = (afterAmmountMrp) - discount_1;
//
//                actualTotalAmount = actualTotalAmount - discount_2;*/
//
//                if (db.CheckFilerNonFilerCust(CustId) == 0) {
//
//                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
//
//                    t_val = (totalTax * afterAmmountMrp1);
//                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
//
//                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
//
//                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
//
//                    t_val = (totalTax * afterAmmountMrp1);
//                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));
//
//                }
//                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
//            }
//
//
//        }
//        Log.e("pricingCalculation", totAmount + "");
//        return totAmount;
        //TODO -------------
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

                if (db.CheckFilerNonFilerCust(CustId) == 0) {

                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;

                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {

                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;

                }

                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {

                if (db.CheckFilerNonFilerCust(CustId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = (Quantity * Integer.parseInt(dataMap.get("mrp_price"))) * totalTax;
                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
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


                if (db.CheckFilerNonFilerCust(CustId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    t_val = (totalTax * totAmount);
                    totAmount = totAmount + (totalTax * totAmount);
                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    t_val = (totalTax * totAmount);
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

                if (db.CheckFilerNonFilerCust(CustId) == 0) {

                    totalTax = (tax_1 + tax_2 + tax_3) / 100;

                    t_val = (totalTax * afterAmmountMrp1);
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));

                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {

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


}
