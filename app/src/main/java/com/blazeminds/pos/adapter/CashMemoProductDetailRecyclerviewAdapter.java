package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.pos.R;
import com.blazeminds.pos.model.ProductModel;

import java.util.ArrayList;

public class CashMemoProductDetailRecyclerviewAdapter extends RecyclerView.Adapter<CashMemoProductDetailRecyclerviewAdapter.MyViewHolder>{


    private ArrayList<ProductModel> productModelList;
    private Activity activity;

    public CashMemoProductDetailRecyclerviewAdapter(ArrayList<ProductModel> productModelList, Activity activity) {
        this.productModelList = productModelList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(  ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cash_memo_product_details_recylerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( MyViewHolder holder, int position) {

        ProductModel  model= productModelList.get(position);
        holder.tv_product_discount.setText(model.getDiscount()+"%");
        holder.tv_product_name.setText(model.getProd_name());
        if (model.getSchemeValue() == null) {


            if(model.getSchemeValue().equals("0")||model.getSchemeValue().equals("")){
                holder.ll_scheme_.setVisibility(View.GONE);
            }else {
                holder.ll_scheme_.setVisibility(View.VISIBLE);
                String schemeTxt= model.getSchemeTxt();
                String[] splited = schemeTxt.split("\\s+");
                String str= schemeTxt.substring(schemeTxt.lastIndexOf("/") + 1);
                holder.tv_product_scheme_qty.setText(String.valueOf((int)Double.parseDouble(splited[0])));
                holder.tv_product_scheme_product.setText(str);

            }
        }else{
            holder.ll_scheme_.setVisibility(View.GONE);
        }
        double price =price(Double.parseDouble(model.getDiscount()),"1",Double.parseDouble(model.getProd_price()));
        double total_price = price * model.getProd_qty();
        holder.tv_product_unit.setText(String.valueOf(model.getProd_qty()));
        holder.tv_product_trade.setText("Rs."+(model.getTradeOffer()==null?"0":model.getTradeOffer()));
//        holder.tv_product_unit1.setText(String.valueOf(model.getProd_qty()));
        holder.tv_product_price.setText(String.valueOf(Double.parseDouble(model.getProd_price())));
        holder.tv_product_total_price.setText(String.valueOf(total_price));
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name,tv_product_price,tv_product_unit,tv_product_trade,tv_product_scheme_qty,tv_product_scheme_product
                ,tv_product_discount,tv_product_total_price;
        LinearLayout ll_scheme_;

        public MyViewHolder(View view) {
            super(view);
            tv_product_name = view.findViewById(R.id.tv_product_name);
            ll_scheme_ = view.findViewById(R.id.ll_scheme_);
            tv_product_scheme_product = view.findViewById(R.id.tv_product_scheme_product);
            tv_product_scheme_qty = view.findViewById(R.id.tv_product_scheme_qty);
            tv_product_trade = view.findViewById(R.id.tv_product_trade);
            tv_product_price = view.findViewById(R.id.tv_product_price);
            tv_product_unit = view.findViewById(R.id.tv_product_unit);
//            tv_product_unit1 = view.findViewById(R.id.tv_product_unit_1);
            tv_product_total_price = view.findViewById(R.id.tv_product_total_price);
            tv_product_discount = view.findViewById(R.id.tv_product_discount);

        }
    }

}
