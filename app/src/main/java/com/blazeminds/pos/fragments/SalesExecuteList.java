package com.blazeminds.pos.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.SalesExecuteListViewAdapter;
import com.blazeminds.pos.SalesOrderListViewHEADERAdapter;
import com.likebamboo.widget.SwipeListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.blazeminds.pos.Constant.ORDER_CUST_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_DATE_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_NEW_AMOUNT_COLUMN;

public class SalesExecuteList extends Fragment implements View.OnClickListener {
	
	public final static String TAG = SalesExecuteList.class.getSimpleName();
	
	
	PosDB db;
	Spinner DateSpinner;
	TextView NoitemTxt;
	SwipeListView OrderList;
	ListView HeaderList;
	Button ExecuteBtn;
	
	List<String> IdsForExecute;
	
	public static SalesExecuteList newInstance() {
		return new SalesExecuteList();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_sales_execute_list, container, false);
		
		RelativeLayout lay = rootView.findViewById(R.id.saleExecuteList);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		
		lay.startAnimation(enter);
		
		NoitemTxt = rootView.findViewById(R.id.NoItemTxt);
		OrderList = rootView.findViewById(R.id.OrderList);
		HeaderList = rootView.findViewById(R.id.HeaderList);
		
		ExecuteBtn = rootView.findViewById(R.id.ExecuteBtn);
		DateSpinner = rootView.findViewById(R.id.SelectDateSpinner);
		
		IdsForExecute = new ArrayList<>();
		ArrayList<HashMap<String, String>> Hlist = new ArrayList<>();
		
		Hlist.clear();
		
		HashMap<String,String> temp = new HashMap<>();
		
		temp.put(ORDER_DATE_COLUMN, "Date");
		temp.put(ORDER_CUST_COLUMN, "Customer Name");
		/*temp.put(ORDER_AMOUNT_COLUMN, "Old Total");*/
		temp.put(ORDER_NEW_AMOUNT_COLUMN, "Total");
		Hlist.add(temp);
		
		
		SalesOrderListViewHEADERAdapter adapter = new SalesOrderListViewHEADERAdapter(getActivity(), Hlist);
		HeaderList.setAdapter(adapter);
		
		
		db = PosDB.getInstance(getActivity());
		
		NoitemTxt.setVisibility(View.VISIBLE);
		OrderList.setVisibility(View.GONE);
		HeaderList.setVisibility(View.GONE);
		ExecuteBtn.setVisibility(View.GONE);
		
		SelectDayDropDown();
		
		ExecuteBtn.setOnClickListener(this);
		
		
		return rootView;
		
	}
	
	
	private void SelectDayDropDown() {
		// Spinner Drop down elements
		
		
		db.OpenDb();
		List<String> dates = db.getDateForSpinner();
		final List<String> datesLONG = db.getDateLONGForSpinner();
		
		db.CloseDb();
		
		
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapterDays = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dates);
		
		dataAdapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		DateSpinner.setAdapter(dataAdapterDays);
		
		
		// Spinner click listener
		DateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				
				populateOrderList(OrderList, NoitemTxt, datesLONG.get(i));
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
		
	}
	
	public void populateOrderList(SwipeListView itemList, TextView NoItemTxt, String datetime) {
		
		ArrayList<HashMap<String, String>> data = null;
		IdsForExecute = null;
		db.OpenDb();
		data = db.getSalesOrderForListFOREXECUTE(datetime);
		IdsForExecute = db.getIdsForSpinner(datetime);
		
		db.CloseDb();
		
		if (data.size() > 0) {
			NoItemTxt.setVisibility(View.GONE);
			ExecuteBtn.setVisibility(View.VISIBLE);
			itemList.setVisibility(View.VISIBLE);
			HeaderList.setVisibility(View.VISIBLE);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			(getActivity()).getWindowManager()
					.getDefaultDisplay()
					.getMetrics(displaymetrics);
			
			int height = displaymetrics.heightPixels;
			int width = displaymetrics.widthPixels;
			
			itemList.setRightViewWidth(width / 2);
			
			
			SalesExecuteListViewAdapter adapter1 = new SalesExecuteListViewAdapter(getActivity(), itemList.getRightViewWidth(), data, db, itemList, NoItemTxt, datetime);
			itemList.setAdapter(adapter1);
			
			
		} else {
			NoItemTxt.setVisibility(View.VISIBLE);
			itemList.setVisibility(View.GONE);
			HeaderList.setVisibility(View.GONE);
			ExecuteBtn.setVisibility(View.GONE);
			
		}
		
		
	}
	
	
	@Override
	public void onClick(View view) {
		
		if (view.getId() == R.id.ExecuteBtn) {
			AreYouSure(getActivity(), db, IdsForExecute, OrderList, NoitemTxt);
		}
		
	}
	
	public void AreYouSure(final Context ctx, final PosDB db, final List<String> orderIDs, final SwipeListView lView, final TextView noItemTxt) {
		
		final Dialog dialog = new Dialog(ctx);
		
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.popup_are_you_sure);
		
		DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.80);
		dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
		
		
		//  dialog.setTitle(ItemName);
		TextView msg = dialog.findViewById(R.id.msg);
		Button Yes = dialog.findViewById(R.id.YesBtn);
		Button No = dialog.findViewById(R.id.NoBtn);
		
		msg.setText("This action can't be reversed.");
		msg.setVisibility(View.VISIBLE);
		
		dialog.show();
		
		Yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				db.OpenDb();
				db.updateExecuteDone(orderIDs);
				db.CloseDb();
				
				//populateOrderList( lView, noItemTxt, "00" );
				
				SelectDayDropDown();
				Toast.makeText(ctx, "Order Executed", Toast.LENGTH_SHORT).show();
				
				dialog.dismiss();
				
			}
		});
		
		No.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		
	}
	
	
}

