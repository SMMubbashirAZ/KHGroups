package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.activities.CashSummaryActivity;
import com.blazeminds.pos.model.MainCartModel;
import com.blazeminds.pos.model.ProductModel;
import com.blazeminds.pos.utils.Helper;

import java.util.ArrayList;

public class CashMemoMainRecyclerviewAdapter extends RecyclerView.Adapter<CashMemoMainRecyclerviewAdapter.MyViewHolder> {
    private ArrayList<MainCartModel> mainList;
    private Activity activity;

    private String CustId,CustTypeId;
    private PosDB db;
    CashMemoProductDetailRecyclerviewAdapter ProductMemoAdapter;
    public CashMemoMainRecyclerviewAdapter(ArrayList<MainCartModel> mainList, Activity activity, PosDB db, String selectedCustomerId, String selectedTypeCustomerId) {
        this.mainList = mainList;
        this.activity = activity;
        this.db=db;
        this.CustId=selectedCustomerId;
        this.CustTypeId=selectedTypeCustomerId;
    }

    @Override
    public MyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cash_memo_brand, parent, false);
        return new  MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int i) {
        if (mainList.get(i).isExpanded()) {
            holder.layoutTripDetails.setVisibility(View.VISIBLE);
            holder.ivIndicator.getLayoutParams().height = 25;
            holder.ivIndicator.getLayoutParams().width = 30;
            holder.ivIndicator.requestLayout();
            holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_dwn_arrow));
        } else {
            holder.layoutTripDetails.setVisibility(View.GONE);
            holder.ivIndicator.getLayoutParams().height = 30;
            holder.ivIndicator.getLayoutParams().width = 30;
            holder.ivIndicator.requestLayout();
            holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_frwd_icon));
        }
        holder.brand_name.setText(mainList.get(i).getB_name());
        int qty = 0;
        double tt_price = 0.0,disc=0.0,temp_total=0.0;
        for (int j = 0; j < mainList.get(i).getProductList().size(); j++) {
            qty += mainList.get(i).getProductList().get(j).getProd_qty();
            if(mainList.get(i).getProductList().get(j).getDiscount()!=null&&
                    !mainList.get(i).getProductList().get(j).getDiscount().isEmpty()){
                disc=Double.parseDouble(mainList.get(i).getProductList().get(j).getDiscount());
            }else{
                disc=0.0;
            }

            ProductModel model = mainList.get(i).getProductList().get(j);
            //
//double Quantity,
//                                      int isTaxMrp,
//                                      int tax_mrp,
//                                      int mrp_price,
//                                      String tradePrice,
//                                      String discount1,
//                                      String discount2, String tradeOffer, String scheme,
//                                      String tax1, String tax2, String tax3, String taxf1,
//                                      String taxf2, String taxf3

            temp_total= pricingCalculation(model.getProd_qty(),Integer.parseInt(model.getIsTaxMrp()==null?"0":model.getIsTaxMrp())
                    ,Integer.parseInt(model.getTax_mrp()==null?"0":model.getTax_mrp()),Integer.parseInt(model.getMrp_price()==null?"0":model.getMrp_price())
                    ,model.getTradePrice()==null?"0":model.getTradePrice(),"0",
                    model.getDiscount()==null?"0":model.getDiscount(),model.getTradeOffer()==null?"0":model.getTradeOffer(),
                    model.getSchemeValue()==null?"0":model.getSchemeValue()
                    ,model.getTax1()==null?"0":model.getTax1(),model.getTax2()==null?"0":model.getTax2(),
                    model.getTax3()==null?"0":model.getTax3(),model.getTaxf1()==null?"0":model.getTaxf1(),
                    model.getTaxf2()==null?"0":model.getTaxf2(),model.getTaxf3()==null?"0":model.getTaxf3());
//            temp_total= pricingCalculation(disc,"1",
//                    Double.parseDouble(mainList.get(i).getProductList().get(j).getProd_price()));

//            tt_price+=temp_total*mainList.get(i).getProductList().get(j).getProd_qty();
            tt_price+=temp_total;
        }
        holder.b_total_price.setText("Rs." + tt_price);
        holder.b_total_unit.setText(qty + " units");
        final int finalI = i;
        ProductMemoAdapter = new CashMemoProductDetailRecyclerviewAdapter(mainList.get(i).getProductList(), activity);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        holder.rv_child.setLayoutManager(layoutManager);
        holder.rv_child.addItemDecoration(new DividerItemDecoration(holder.rv_child.getContext(), DividerItemDecoration.VERTICAL));
        holder.rv_child.setAdapter(ProductMemoAdapter);
        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mainList.get(finalI).isExpanded()) {

                    holder.ivIndicator.getLayoutParams().height = 30;
                    holder.ivIndicator.getLayoutParams().width = 30;
                    holder.ivIndicator.requestLayout();
                    holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_frwd_icon));
                    mainList.get(finalI).setExpanded(false);
                    Helper.collapse(holder.layoutTripDetails);
                } else {
                    holder.ivIndicator.getLayoutParams().height = 25;
                    holder.ivIndicator.getLayoutParams().width = 30;
                    holder.ivIndicator.requestLayout();
                    holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_dwn_arrow));
                    mainList.get(finalI).setExpanded(true);
                    Helper.expand(holder.layoutTripDetails);
                    collapseCard(finalI); //to collapse expanded card
                }
            }
        });

    }
    private double price(double disc2,String quantity,double tp ){
        double discount_2=0,totAmount=0;
        // productList.get(pos).setProd_qty(Integer.parseInt(quantity));
        totAmount= Double.parseDouble(quantity) * tp;

        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;
        Log.d("TAG", "price: "+totAmount);
        return totAmount;


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

//        t_o_v = tradeOfferValue;

        totAmount = totAmount - tradeOfferValue;
        afterAmmount = totAmount;

        discount_1 = disc1 / 100;
        discount_1 = discount_1 * totAmount;
        totAmount = totAmount - discount_1;

//        d_v_1 = discount_1;


        discount_2 = disc2 / 100;
        discount_2 = discount_2 * totAmount;
        totAmount = totAmount - discount_2;

//        d_v_2 = discount_2;


//        t_type = isTaxMrp;
//        t_mrp_type = tax_mrp;


        if (isTaxMrp == 2) {

            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);

            if (tax_mrp == 0) {

                if (db.CheckFilerNonFilerCust(CustId) == 0) {

                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
//                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;

                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {

                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = actualTotalAmount * totalTax;
//                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;

                }

                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            } else if (tax_mrp == 1) {

                if (db.CheckFilerNonFilerCust(CustId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
                    actualTotalAmount = (Quantity * mrp_price) * totalTax;
//                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
                    actualTotalAmount = (Quantity * mrp_price) * totalTax;
//                    t_val = actualTotalAmount;
                    totAmount = totAmount + actualTotalAmount;
                }
                Log.e("pricingCalculation", "tax_mrp " + tax_mrp);
            }


        } else if (isTaxMrp == 3) {
            Log.e("pricingCalculation", "isTaxMrp " + isTaxMrp);
            if (tax_mrp == 0) {


                if (db.CheckFilerNonFilerCust(CustId) == 0) {
                    totalTax = (tax_1 + tax_2 + tax_3) / 100;
//                    t_val = (totalTax * totAmount);
                    totAmount = totAmount + (totalTax * totAmount);
                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {
                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;
//                    t_val = (totalTax * totAmount);
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

//                    t_val = (totalTax * afterAmmountMrp1);
                    totAmount = totAmount + ((totalTax * afterAmmountMrp1));

                } else if (db.CheckFilerNonFilerCust(CustId) == 1) {

                    totalTax = (tax_f_1 + tax_f_2 + tax_f_3) / 100;

//                    t_val = (totalTax * afterAmmountMrp1);
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
        return mainList.size();
    }
    public void collapseCard(int expandPosition) {
        for (int i = 0; i < mainList.size(); i++) {
            if (i != expandPosition && mainList.get(i).isExpanded()) {
                mainList.get(i).setExpanded(false);
                notifyItemChanged(i);
            }
        }

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView brand_name, b_total_unit, b_total_price;
        ImageView ivIndicator;


        //for  dynamic layout;
        LinearLayout layoutTripDetails, layout_parent;
        RecyclerView rv_child;
        public MyViewHolder(View view) {
            super(view);
            brand_name = view.findViewById(R.id.tv_cash_memo_brand_name);
            b_total_unit = view.findViewById(R.id.tv_cash_memo_brand_unit);
            b_total_price = view.findViewById(R.id.tv_cash_memo_brand_total_price);
            ivIndicator = view.findViewById(R.id.ivIndicator);
            layoutTripDetails = view.findViewById(R.id.layoutTripDetails);
            layout_parent = view.findViewById(R.id.layout_parent);
            rv_child = view.findViewById(R.id.rv_child);


        }
    }
}
