package com.blazeminds.pos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.MyListener;
import com.blazeminds.pos.resources.UserSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Blazeminds on 11/3/2018.
 */

public class RoutePlan extends Fragment implements MyListener {

    public final static String TAG = RoutePlan.class.getSimpleName();
    static public boolean hideLabel = false;
    public TableRow aboutTxtTableRow;
    boolean checkItemClicked = true;
    private ArrayList<HashMap<String, String>> prod_data,pend_data,un_prod_data;
    private int pend_count=0,prod_count=0,un_prod_count=0;

    private Context c;
    ViewPager mViewPager;
    MyPagerAdapter myPagerAdapter;

    public RoutePlan() {
    }

    public static RoutePlan newInstance() {
        hideLabel = false;
        return new RoutePlan();
    }

    public static RoutePlan newInstanceNoLabel() {
        hideLabel = true;
        return new RoutePlan();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub


        View rootView = inflater.inflate(R.layout.fragment_route_plan, container, false);

        initUI(rootView);


        return rootView;

    }

    private void initUI(View rootView) {


        LinearLayout lay = rootView.findViewById(R.id.routePlanLayout);

        // load the animation
        //	Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);

        //	lay.startAnimation(enter);

        c = getActivity();
        aboutTxtTableRow = rootView.findViewById(R.id.aboutTxtTableRow);
        if (hideLabel) {
            aboutTxtTableRow.setVisibility(View.GONE);
        }


        populateList();

        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager(),pend_count,prod_count,un_prod_count);

        mViewPager = rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(myPagerAdapter);

    }

    private void populateList() {

        prod_data = new ArrayList<>();
        un_prod_data = new ArrayList<>();
        pend_data = new ArrayList<>();

        prod_data.clear();
        un_prod_data.clear();
        pend_data.clear();

        PosDB db = PosDB.getInstance(c);

        db.OpenDb();
        int savedRoute = db.getSavedRouteID();
        if (savedRoute == 0) {
            prod_data = db.getCustomerListForRoutePlan( UserSettings.PRODUCTIVE);
            un_prod_data = db.getCustomerListForRoutePlan( UserSettings.UN_PRODUCTIVE);
            pend_data = db.getCustomerListForRoutePlan( UserSettings.PENDING);
        } else {
            prod_data = db.getCustomerListForRoutePlanByRoute(savedRoute, UserSettings.PRODUCTIVE);
            un_prod_data = db.getCustomerListForRoutePlanByRoute(savedRoute, UserSettings.UN_PRODUCTIVE);
            pend_data = db.getCustomerListForRoutePlanByRoute(savedRoute, UserSettings.PENDING);

        }
        db.CloseDb();

        pend_count= pend_data.size();
        un_prod_count= un_prod_data.size();
        prod_count= prod_data.size();






//        if (prod_data.size() > 0) {
//        } else {
//            un_prod_count=0;
//            prod_count=0;
//        }


//        return prod_data;

    }

    public int dp2px(float dips) {

        return (int) (dips * getActivity().getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void TriggerOnItemClick(int position) {
        if (checkItemClicked) {
            checkItemClicked = false;
        } else {
            checkItemClicked = true;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        int pending_count,prod_count,un_prod_count;
        public MyPagerAdapter(FragmentManager fm,int pending_count,int prod_count,int un_prod_count) {
            super(fm);
            this.pending_count=pending_count;
            this.prod_count=prod_count;
            this.un_prod_count=un_prod_count;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return PendingFragment.newInstance();
                case 1:
                    return ProductiveFragment.newInstance();
                case 2:
                    return UnProductiveFragment.newInstance();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Pending Shops".toUpperCase()+"("+pending_count+")";
                case 1:
                    return "Productive Shops".toUpperCase()+"("+prod_count+")";
                case 2:
                    return "Visited Shops".toUpperCase()+"("+un_prod_count+")";

            }
            return null;
        }

    }
}
