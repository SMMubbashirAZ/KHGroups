package com.blazeminds.pos.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blazeminds.pos.MonthlyTargetHeaderAdapter;
import com.blazeminds.pos.MonthlyTargetListAdapter;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;

/**
 * Created by Blazeminds on 2/28/2018.
 */

public class MonthlyTarget extends Fragment {
	
	public final static String TAG = MonthlyTarget.class.getSimpleName();
	private ListView headerList, monthlyTargetList;
	private ArrayList<HashMap<String, String>> Hlist;
	private ArrayList<HashMap<String, String>> list;
	
	public MonthlyTarget() {
	}
	
	public static MonthlyTarget newInstance() {
		return new MonthlyTarget();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_monthly_target, container, false);
		
		LinearLayout lay = rootView.findViewById(R.id.monthlyTargetLayout);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		// lay.setVisibility(View.VISIBLE);
		list = new ArrayList<>();
		Hlist = new ArrayList<>();
		
		headerList = rootView.findViewById(R.id.headerList);
		monthlyTargetList = rootView.findViewById(R.id.monthlyTargetList);
		
		
		Hlist.clear();
		
		HashMap<String,String> temp = new HashMap<>();
		//temp.put(CODE_COLUMN,"Code");
		//temp.put(CODE_PICTXT,"Picture");
		temp.put(FIRST_COLUMN, "Item");
		temp.put(SECOND_COLUMN, "Target");
		temp.put(THIRD_COLUMN, "Booked");
		temp.put(FOURTH_COLUMN, "Ach %");
		Hlist.add(temp);
		
		
		MonthlyTargetHeaderAdapter adapter1 = new MonthlyTargetHeaderAdapter(getActivity(), Hlist);
		headerList.setAdapter(adapter1);
		
		populateList();
		
		return rootView;
		
	}
	
	private ArrayList<HashMap<String, String>> populateList() {
		
		ArrayList<HashMap<String, String>> data;
		
		list = new ArrayList<>();
		
		data = new ArrayList<>();
		
		
		data.clear();
		PosDB db = PosDB.getInstance(getActivity());
		db.OpenDb();
//        data = db.getMonthlyTargetList();
		data = db.getItemTarget();
		
		db.CloseDb();
		
		if (data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				
				list.add(data.get(i));
				
				Log.d("All List:", i + "\n" + data.get(i));
			}
			
			
			MonthlyTargetListAdapter adapter = new MonthlyTargetListAdapter(getActivity(), list);
			
			monthlyTargetList.setAdapter(adapter);
			
			
		}
		
		
		return list;
		
	}
}