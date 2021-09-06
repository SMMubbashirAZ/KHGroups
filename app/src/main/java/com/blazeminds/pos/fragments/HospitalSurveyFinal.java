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

import com.blazeminds.pos.R;

import java.util.Locale;

/**
 * Created by Blazeminds on 1/16/2019.
 */

public class HospitalSurveyFinal extends Fragment {
	
	public final static String TAG = HospitalSurveyFinal.class.getSimpleName();
	
	ViewPager mViewPager;
	MyPagerAdapter myPagerAdapter;
	Context c;
	
	public static HospitalSurveyFinal newInstance() {
		return new HospitalSurveyFinal();
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
					return HospitalSurvey.newInstance();
				case 1:
					return HospitalSurveyList.newInstance();
				
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
					return "Hospital Survet".toUpperCase();
				case 1:
					return "Hospital Survet List".toUpperCase();
				
			}
			return null;
		}
		
	}
	
	
	
	
}