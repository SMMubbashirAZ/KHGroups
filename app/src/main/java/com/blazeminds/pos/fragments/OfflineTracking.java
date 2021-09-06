package com.blazeminds.pos.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.ListViewAdapter4OfflineTracking;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;

/**
 * Created by Blazeminds on 2/22/2018.
 */

public class OfflineTracking extends Fragment {
	
	public final static String TAG = OfflineTracking.class.getSimpleName();
	private ListView headerList, offlineTrackingList;
	private ArrayList<HashMap<String, String>> Hlist;
	private ArrayList<HashMap<String, String>> data;
	
	public OfflineTracking() {
	}
	
	public static OfflineTracking newInstance() {
		return new OfflineTracking();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_offline_tracking, container, false);
		
		RelativeLayout lay = rootView.findViewById(R.id.offlineTrackingLayout);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		lay.setVisibility(View.VISIBLE);
		
		headerList = rootView.findViewById(R.id.headerList);
		offlineTrackingList = rootView.findViewById(R.id.offlineTrackingList);
		
		Hlist = new ArrayList<>();
		Hlist.clear();
		
		HashMap<String,String> temp = new HashMap<>();
		temp.put(FIRST_COLUMN, "ID");
		temp.put(SECOND_COLUMN, "LATI");
		temp.put(THIRD_COLUMN, "LONGI");
		temp.put(FOURTH_COLUMN, "AREA");
		//temp.put(THIRD_COLUMN, "Route ID");
		//temp.put(FOURTH_COLUMN, "Contact");
		Hlist.add(temp);
		
		
		ListViewAdapter4OfflineTracking adapter = new ListViewAdapter4OfflineTracking(getActivity(), Hlist);
		headerList.setAdapter(adapter);
		
		populateList();
		
		return rootView;
		
	}
	
	private ArrayList<HashMap<String, String>> populateList() {
		
		data = new ArrayList<>();
		//dataCustId = new ArrayList<String>();
		data.clear();
		
		PosDB db = PosDB.getInstance(getActivity());
		
		db.OpenDb();
		
		data = db.getOfflineTrackingList();
		
		db.CloseDb();
		
		if (data.size() > 0) {
			ListViewAdapter4OfflineTracking adapter1 = new ListViewAdapter4OfflineTracking(getActivity(), data);
			offlineTrackingList.setAdapter(adapter1);
			
		} else {
			Toast.makeText(AppContextProvider.getContext(), "No Data", Toast.LENGTH_SHORT).show();
		}
		
		
		return data;
		
	}
}
