package com.blazeminds.pos.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.Locale;

/**
 * Created by Blazeminds on 1/16/2019.
 */

public class SaleOrderFinal extends Fragment {

    public final static String TAG = SaleOrderFinal.class.getSimpleName();
    public static String ShopID = "0";
    ViewPager mViewPager;
    MyPagerAdapter myPagerAdapter;
    Context c;
    PosDB db;

    public static SaleOrderFinal newInstance() {
        ShopID = "0";
        return new SaleOrderFinal();
    }

    public static SaleOrderFinal newInstanceSwipe(String Shop_ID) {
        ShopID = Shop_ID;
        return new SaleOrderFinal();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_item_one, container, false);

        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());

        mViewPager = v.findViewById(R.id.pager);
        mViewPager.setAdapter(myPagerAdapter);

        c = getActivity();
        db = PosDB.getInstance(getActivity());
        db.OpenDb();
        return v;
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    if (db.getAppSettingsValueByKey("en_percent_discount") != 0) {
                        if (!ShopID.equals("0") && !ShopID.equals("")) {
                            return SalesOrderLorenzo.newInstances();
                        } else {
                            return SalesOrderLorenzo.newInstances();
                        }

                    } else {
                        if (!ShopID.equals("0") && !ShopID.equals("")) {
                            return SalesOrder.newInstancesSwipe(ShopID);
                        } else {
                            return SalesOrder.newInstances();
                        }
                    }
                case 1:
                    return SalesOrderList.newInstances();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "Sales Order".toUpperCase();
                case 1:
                    return "Order List".toUpperCase();

            }
            return null;
        }

    }


}