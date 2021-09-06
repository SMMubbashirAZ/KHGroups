package com.blazeminds.pos.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blazeminds.pos.AttendanceListAdapter;
import com.blazeminds.pos.AttendanceListHeaderAdapter;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Blazeminds on 7/13/2018.
 */

public class AttendanceList extends Fragment {
	
	public final static String TAG = About.class.getSimpleName();
	private ListView headerList, listview;
	private TextView noItemTxt;
	private ArrayList<HashMap<String, String>> hList;
	private ArrayList<HashMap<String, String>> data;
	private Context c;
	
	public AttendanceList() {
	}
	
	public static AttendanceList newInstance() {
		return new AttendanceList();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_attendance_list, container, false);
		
		initUI(rootView);
		
		populateHeaderList();
		
		return rootView;
		
	}
	
	private void initUI(View rootView) {
		
		
		LinearLayout lay = rootView.findViewById(R.id.ShowAttendanceList);
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		c = getActivity();
		listview = rootView.findViewById(R.id.listviewRList);
		headerList = rootView.findViewById(R.id.Hlist);
		noItemTxt = rootView.findViewById(R.id.NoItemTxt);
		
		
	}
	
	private void populateHeaderList() {
		
		hList = new ArrayList<>();
		hList.clear();
		
		HashMap<String,String> temp = new HashMap<>();
		//temp.put("exp_amount", "EX_AMT");
		temp.put("id", "DATE");
		temp.put("clock_in", "CLOCK IN");
		temp.put("clock_out", "CLOCK OUT");
		//temp.put(FOURTH_COLUMN, "Contact");
		hList.add(temp);
		
		
		AttendanceListHeaderAdapter adapter = new AttendanceListHeaderAdapter(c, hList);
		headerList.setAdapter(adapter);
		
		populateListOfAttendance(noItemTxt);
	}
	
	private ArrayList<HashMap<String, String>> populateListOfAttendance(TextView noItemText) {
		
		data = new ArrayList<>();
		data.clear();
		
		PosDB db = PosDB.getInstance(c);
		
		db.OpenDb();
		data = db.getClockInTimeList();
		
		db.CloseDb();
		
		if (data.size() > 0) {
			noItemText.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			AttendanceListAdapter adapter1 = new AttendanceListAdapter(c, data);
			listview.setAdapter(adapter1);
			
		} else {
			listview.setVisibility(View.GONE);
			noItemText.setVisibility(View.VISIBLE);
		}
		
		return data;
		
	}
	
	
}