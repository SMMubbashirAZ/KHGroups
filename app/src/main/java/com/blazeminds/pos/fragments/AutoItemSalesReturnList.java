package com.blazeminds.pos.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.SalesOrderListViewHEADERAdapter;
import com.blazeminds.pos.SalesReturnListViewAdapter;
import com.likebamboo.widget.SwipeListView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.ORDER_CUST_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_DATE_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_NEW_AMOUNT_COLUMN;

public class AutoItemSalesReturnList extends Fragment {
	
	public final static String TAG = AutoItemSalesReturnList.class.getSimpleName();
	
	
	PosDB db;
	
	public static AutoItemSalesReturnList newInstance() {
		return new AutoItemSalesReturnList();
	}
	
	public static Fragment newInstances() {
		return new AutoItemSalesReturnList();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_sales_order_list, container, false);
		
		LinearLayout saleOrderList = rootView.findViewById(R.id.saleOrderList);
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		
		saleOrderList.startAnimation(enter);
		
		//TextView title = (TextView) rootView.findViewById(R.id.aboutTxt);
		//title.setText("SALES RETURN LIST");
		
		
		TextView NoitemTxt = rootView.findViewById(R.id.NoItemTxt);
		SwipeListView OrderList = rootView.findViewById(R.id.OrderList);
		ListView HeaderList = rootView.findViewById(R.id.HeaderList);
		
		ArrayList<HashMap<String, String>> Hlist = new ArrayList<>();
		
		Hlist.clear();
		
		HashMap<String, String> temp = new HashMap<>();
		
		temp.put(ORDER_DATE_COLUMN, "Date");
		temp.put(ORDER_CUST_COLUMN, "Shop Name");
		/*temp.put(ORDER_AMOUNT_COLUMN, "Old Total");*/
		temp.put(ORDER_NEW_AMOUNT_COLUMN, "Total");
		Hlist.add(temp);
		
		
		SalesOrderListViewHEADERAdapter adapter = new SalesOrderListViewHEADERAdapter(getActivity(), Hlist);
		HeaderList.setAdapter(adapter);
		
		
		db =PosDB.getInstance(getActivity());
		
		populateOrderList(OrderList, NoitemTxt);
		
		OrderList.setOnTouchListener(new ListView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
					case MotionEvent.ACTION_DOWN:
						// Disallow ScrollView to intercept touch events.
						v.getParent().requestDisallowInterceptTouchEvent(true);
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						// Allow ScrollView to intercept touch events.
						v.getParent().requestDisallowInterceptTouchEvent(false);
						break;
				}
				// Handle ListView touch events.
				v.onTouchEvent(event);
				return true;
			}
		});
		
		return rootView;
	}
	
	public void populateOrderList(SwipeListView itemList, TextView NoItemTxt) {
		
		ArrayList<HashMap<String, String>> data = null;
		db.OpenDb();
		data = db.getSalesReturnForList();
		
		db.CloseDb();
		
		if (data.size() > 0) {
			NoItemTxt.setVisibility(View.GONE);
			itemList.setVisibility(View.VISIBLE);
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			(getActivity()).getWindowManager()
					.getDefaultDisplay()
					.getMetrics(displaymetrics);
			
			int height = displaymetrics.heightPixels;
			int width = displaymetrics.widthPixels;
			
			itemList.setRightViewWidth(width / 2);
			
			
			SalesReturnListViewAdapter adapter1 = new SalesReturnListViewAdapter(getActivity(), itemList.getRightViewWidth(), data, db, itemList, NoItemTxt);
			itemList.setAdapter(adapter1);

			
		} else {
			NoItemTxt.setVisibility(View.VISIBLE);
			itemList.setVisibility(View.GONE);
			
		}
		
		
	}
	
	
}

