package com.blazeminds.pos.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.Locale;

/**
 * Created by Blazeminds on 1/16/2019.
 */

public class SaleReturnFinal extends Fragment {

	public final static String TAG = SaleReturnFinal.class.getSimpleName();

	ViewPager mViewPager;
	SaleReturnFinal.MyPagerAdapter myPagerAdapter;
	Context c;
	PosDB db;
	public static String ShopReturnID="0";

//	public static SaleReturnFinal newInstance() {
//		return new SaleReturnFinal();
//	}
	public static SaleReturnFinal newInstance() {
		ShopReturnID="0";
		return new SaleReturnFinal();
	}
	public static SaleReturnFinal newInstanceSwipe(String Shop_ID) {
		ShopReturnID=Shop_ID;
		return new SaleReturnFinal();

	}
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_item_one, container, false);

		myPagerAdapter = new SaleReturnFinal.MyPagerAdapter(getChildFragmentManager());

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
						if(!ShopReturnID.equals("0") && !ShopReturnID.equals("")){

							return SalesReturnLorenzo.newInstances();
						}else{

							return SalesReturnLorenzo.newInstances();
						}
					}
					else {
						if (!ShopReturnID.equals("0") && !ShopReturnID.equals("")) {
							return SalesReturn.newInstancesSwipe(ShopReturnID);
						} else {
							return SalesReturn.newInstances();
						}
					}
				case 1:
					return SalesReturnList.newInstances();

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
					return "SALES RETURN".toUpperCase();
				case 1:
					return "SALES RETURN LIST".toUpperCase();


			}
			return null;
		}


	}


}
