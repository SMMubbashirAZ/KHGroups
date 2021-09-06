package com.blazeminds.pos.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.blazeminds.pos.ListViewAdapter4Route;
import com.blazeminds.pos.ListViewAdapter4RouteHEADER;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.blazeminds.pos.ConstantRoute.AreaName;
import static com.blazeminds.pos.ConstantRoute.Name;
import static com.blazeminds.pos.ConstantRoute.Visits;

public class Route extends Fragment {
	
	public final static String TAG = Route.class.getSimpleName();

/*
	static Context context;
	static Context c;
*/
	
	static ArrayList<HashMap<String, String>> data;
	static ListView RouteList;
	static Spinner SelectDay;
	
	public Route() {
		// TODO Auto-generated constructor stub
	}
	
	public static Route newInstance() {
		return new Route();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_route, container, false);

/*
		LinearLayout lay = (LinearLayout)rootView.findViewById(R.id.RouteFrag);

		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_right);

		lay.startAnimation(enter);
*/
		
		
		ListView hList = rootView.findViewById(R.id.HRouteList);
		RouteList = rootView.findViewById(R.id.RouteList);
		
		SelectDay = rootView.findViewById(R.id.SelectDay);
		
		ArrayList<HashMap<String, String>> Hlist = new ArrayList<>();
		
		
		HashMap<String,String> temp = new HashMap<>();
		
		temp.put(Name, "CUSTOMER");
		temp.put(AreaName, "ADDRESS");
		temp.put(Visits, "VISIT");
		
		Hlist.add(temp);
		
		
		ListViewAdapter4RouteHEADER adapter1 = new ListViewAdapter4RouteHEADER(getActivity(), Hlist);
		hList.setAdapter(adapter1);
		
		SelectDayDropDown();
		GetCurrentDateValues();
		
		
		return rootView;
	}
	
	
	private void GetCurrentDateValues() {
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		
		switch (day) {
			
			case Calendar.MONDAY: {
				
				data = new ArrayList<>();
				
				data.clear();
				
				ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Monday"));
				
				RouteList.setAdapter(adapter1);
				
				SelectDay.setSelection(0);
			}
			break;
			
			case Calendar.TUESDAY: {
				data = new ArrayList<>();
				
				data.clear();
				
				ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Tuesday"));
				
				RouteList.setAdapter(adapter1);
				SelectDay.setSelection(1);
			}
			break;
			
			case Calendar.WEDNESDAY: {
				data = new ArrayList<>();
				
				data.clear();
				
				ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Wednesday"));
				
				RouteList.setAdapter(adapter1);
				SelectDay.setSelection(2);
			}
			break;
			
			case Calendar.THURSDAY: {
				data = new ArrayList<>();
				
				data.clear();
				
				ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Thursday"));
				
				RouteList.setAdapter(adapter1);
				SelectDay.setSelection(3);
			}
			break;
			
			case Calendar.FRIDAY: {
				data = new ArrayList<>();
				
				data.clear();
				
				ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Friday"));
				
				RouteList.setAdapter(adapter1);
				SelectDay.setSelection(4);
			}
			break;
			
			case Calendar.SATURDAY: {
				data = new ArrayList<>();
				
				data.clear();
				
				ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Saturday"));
				
				RouteList.setAdapter(adapter1);
				SelectDay.setSelection(5);
			}
			break;
			
			case Calendar.SUNDAY: {
				data = new ArrayList<>();
				
				data.clear();
				
				ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Sunday"));
				
				RouteList.setAdapter(adapter1);
				SelectDay.setSelection(6);
			}
			break;
			
			
		}
		
	}
	
	private void SelectDayDropDown() {
		// Spinner Drop down elements
		
		List<String> Days = new ArrayList<>();
		Days.add("Monday");
		Days.add("Tuesday");
		Days.add("Wednesday");
		Days.add("Thursday");
		Days.add("Friday");
		Days.add("Saturday");
		Days.add("Sunday");
		
		
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapterDays = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Days);
		
		// Drop down layout style
		dataAdapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// attaching data adapter to spinner
		SelectDay.setAdapter(dataAdapterDays);
		
		//	SelectDay.setBackgroundResource(R.drawable.arrow);
		
		
		// Spinner click listener
		SelectDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				
				if (i == 0) { //MONDAY
					data = new ArrayList<>();
					data.clear();
					ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Monday"));
					RouteList.setAdapter(adapter1);
					
					
				}
				
				if (i == 1) {//TUESDAY
					data = new ArrayList<>();
					data.clear();
					ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Tuesday"));
					RouteList.setAdapter(adapter1);
					
				}
				
				if (i == 2) {//WEDNESDAY
					data = new ArrayList<>();
					data.clear();
					ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Wednesday"));
					RouteList.setAdapter(adapter1);
					
				}
				
				if (i == 3) {//THURSDAY
					data = new ArrayList<>();
					data.clear();
					ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Thursday"));
					RouteList.setAdapter(adapter1);
				}
				
				if (i == 4) {//FRIDAY
					data = new ArrayList<>();
					data.clear();
					ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Friday"));
					RouteList.setAdapter(adapter1);
					
				}
				
				if (i == 5) {//SATURDAY
					data = new ArrayList<>();
					data.clear();
					ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Saturday"));
					RouteList.setAdapter(adapter1);
					
				}
				
				if (i == 6) {//SUNDAY
					data = new ArrayList<>();
					data.clear();
					ListViewAdapter4Route adapter1 = new ListViewAdapter4Route(getActivity(), populateList("Sunday"));
					RouteList.setAdapter(adapter1);
					
				}
				
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
		
	}
	
	private ArrayList<HashMap<String, String>> populateList(String Day) {
		
		PosDB db = PosDB.getInstance(getActivity());
		
		
		db.OpenDb();
		
		data = db.getRoutesWhereDay(Day);
		
		db.CloseDb();

/*
		// HashMap<String, String> f = new HashMap<String, String>();
		if (data.size() > 0) {
			for (int i = 1; i < data.size(); i++) {
				//f = data.get(i);
				editOrderList.add(data.get(i));
			}

		}
*/
		
		return data;
		
	}
	
	
}
