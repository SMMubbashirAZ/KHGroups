package com.blazeminds.pos.adapter;

import android.app.Activity;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.model.BrandModel;
import com.blazeminds.pos.model.OnvalueChanged;
import com.blazeminds.pos.model.ProductModel;

import java.util.ArrayList;

public class OrderBookingMainAdapter extends RecyclerView.Adapter<OrderBookingMainAdapter.MyViewHolder> {

    private final PosDB db;
    private final String CustId;
    private final String CustTypeId;
    private final OnvalueChanged onvalueChanged;
    Activity activity;
    private ArrayList<BrandModel> BrandList;
    private OrderBookingChildAdapter orderBookingChildAdapter;

    private int mExpandedPosition = -1;

    public OrderBookingMainAdapter(ArrayList<BrandModel> brandList, Activity activity, PosDB db, String custId,
                                   String custTypeId, OnvalueChanged onvalueChanged) {
        BrandList = brandList;
        this.activity = activity;
        this.db = db;
        this.CustId = custId;
        this.CustTypeId = custTypeId;
        this.onvalueChanged = onvalueChanged;
    }

    public void filterList(ArrayList<BrandModel> filterdNames) {
        this.BrandList = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_header_order_booking_recylerview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

//        if (BrandList.get(position).isExpanded()) {
//            holder.layoutTripDetails.setVisibility(View.VISIBLE);
//            holder.ivIndicator.getLayoutParams().height = 25;
//            holder.ivIndicator.getLayoutParams().width = 30;
//            holder.ivIndicator.requestLayout();
//            holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_dwn_arrow));
//        } else {
//            holder.layoutTripDetails.setVisibility(View.GONE);
//            holder.ivIndicator.getLayoutParams().height = 30;
//            holder.ivIndicator.getLayoutParams().width = 30;
//            holder.ivIndicator.requestLayout();
//            holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_frwd_icon));
//        }
        final boolean isExpanded = position == mExpandedPosition;
        holder.layoutTripDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mExpandedPosition = isExpanded ? -1:position;
                TransitionManager.beginDelayedTransition(holder.rv_child);
//                notifyDataSetChanged();
            }
        });
        // brandAdapter = new OrderBookingMainAdapter(brandDataList, this);
        //        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        //        rv_main_order.setLayoutManager(mLayoutManager);
        //        rv_main_order.setAdapter(brandAdapter);
        orderBookingChildAdapter = new OrderBookingChildAdapter(activity,
                db.GetInventoryData(BrandList
                        .get(position).getId()), CustId, CustTypeId, db, onvalueChanged);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        holder.rv_child.setLayoutManager(layoutManager);
//        holder.rv_child.addItemDecoration(new DividerItemDecoration(holder.rv_child.getContext(), DividerItemDecoration.VERTICAL));
        holder.rv_child.setAdapter(orderBookingChildAdapter);
        holder.tvTripTitle.setText(BrandList.get(position).getName());
        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExpandedPosition == -1) {
                    holder.layoutTripDetails.setVisibility(View.VISIBLE);
                    mExpandedPosition = 1;
                } else {
                    holder.layoutTripDetails.setVisibility(View.GONE);
                    mExpandedPosition = -1;
                }
            }
        });
//        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                boolean show = toggleLayout(!BrandList.get(position).isExpanded(), holder.ivIndicator, holder.layoutTripDetails);
////                if (show) {
////                    holder.itemView.setActivated(true);
////                }else{
////                    holder.itemView.setActivated(false);
////                }
//                BrandList.get(position).setExpanded(show);
//                final boolean visibility = holder.rv_child.getVisibility() == View.VISIBLE;
//
//                if (!visibility) {
//                    holder.itemView.setActivated(true);
//                    holder.rv_child.setVisibility(View.VISIBLE);
//
//                    holder.layoutTripDetails.setVisibility(View.VISIBLE);
//                } else {
//                    holder.itemView.setActivated(false);
//                    holder.rv_child.setVisibility(View.GONE);
//                    holder.layoutTripDetails.setVisibility(View.GONE);
//
//                }
//                toggleArrow(holder.ivIndicator, !BrandList.get(position).isExpanded());
//
////                if (!BrandList.get(position).isExpanded()) {
////
////                    BrandList.get(position).setExpanded(false);
//////                    holder.ivIndicator.getLayoutParams().height = 30;
//////                    holder.ivIndicator.getLayoutParams().width = 30;
//////                    holder.ivIndicator.requestLayout();
//////                    holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_frwd_icon));
//////                    Helper.collapse(holder.layoutTripDetails);
////                } else {
//////                    holder.ivIndicator.getLayoutParams().height = 25;
//////                    holder.ivIndicator.getLayoutParams().width = 30;
//////                    holder.ivIndicator.requestLayout();
//////
//////                    holder.ivIndicator.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_dwn_arrow));
////                    BrandList.get(position).setExpanded(true);
//////                    toggleArrow(holder.ivIndicator, !BrandList.get(position).isExpanded());
//////                    Helper.expand(holder.layoutTripDetails);
//////                    collapseCard(position); //to collapse expanded card
////                }
//                //notifyDataSetChanged();
//            }
//        });
    }

    private boolean toggleLayout(boolean isExpanded, View v, LinearLayout layoutExpand) {
        toggleArrow(v, isExpanded);
//        if (isExpanded) {
//            expand(layoutExpand);
//        } else {
//            collapse(layoutExpand);
//        }
        return isExpanded;

    }

    public ArrayList<ProductModel> getMainList() {

        return orderBookingChildAdapter.getList();
    }

    @Override
    public int getItemCount() {
        return BrandList.size();
    }

    @Override
    public long getItemId(int position) {
        return /*super.getItemId(*/position;
    }

    @Override
    public int getItemViewType(int position) {
        return /*super.getItemViewType(*/position;
    }

    public void collapseCard(int expandPosition) {
        for (int i = 0; i < BrandList.size(); i++) {
            if (i != expandPosition && BrandList.get(i).isExpanded()) {
                BrandList.get(i).setExpanded(false);
                notifyItemChanged(i);
            }
        }

    }

    public boolean toggleArrow(View view, boolean isExpanded) {

        if (isExpanded) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }


    public void expand(View view) {
        Animation animation = expandAction(view);
        view.startAnimation(animation);
    }

    private Animation expandAction(final View view) {

        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int actualheight = view.getMeasuredHeight();

        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                view.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (actualheight * interpolatedTime);
                view.requestLayout();
            }
        };


        animation.setDuration((long) (actualheight / view.getContext().getResources().getDisplayMetrics().density));

        view.startAnimation(animation);

        return animation;


    }

    public void collapse(final View view) {

        final int actualHeight = view.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {

                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = actualHeight - (int) (actualHeight * interpolatedTime);
                    view.requestLayout();

                }
            }
        };

        animation.setDuration((long) (actualHeight / view.getContext().getResources().getDisplayMetrics().density));
        view.startAnimation(animation);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTripTitle;
        ImageView ivIndicator;
        LinearLayout layoutTripDetails, layout_parent;
        RecyclerView rv_child;

        public MyViewHolder(View view) {
            super(view);

            layoutTripDetails = view.findViewById(R.id.layoutTripDetails);
            rv_child = view.findViewById(R.id.rv_child);
            tvTripTitle = view.findViewById(R.id.tvTripTitle);
            layout_parent = view.findViewById(R.id.layout_parent);


            ivIndicator = view.findViewById(R.id.ivIndicator);

        }
    }
}
