package com.blazeminds.pos.fragments;

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

import com.blazeminds.pos.ListViewAdapter;
import com.blazeminds.pos.ListViewAdapterHEADER;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;

public class ItemListSalesOrder extends Fragment
{
	
	
	public final static String TAG = ItemListSalesOrder.class.getSimpleName();
	ArrayList<HashMap<String, String>> list;
	ArrayList<HashMap<String, String>> Hlist;
	ListView lview, hList;
	TextView tv;
	
	public ItemListSalesOrder() {
	}
	
	public static ItemListSalesOrder newInstance() {
		return new ItemListSalesOrder();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub

    /*    if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }*/
		
		
		View rootView = inflater.inflate(R.layout.fragment_itemlist_salesorder, container, false);
		
		LinearLayout lay = rootView.findViewById(R.id.SyncFrag);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		
		lay.startAnimation(enter);
		
		list = new ArrayList<>();
		Hlist = new ArrayList<>();
		
		lview = rootView.findViewById(R.id.listview);
		hList = rootView.findViewById(R.id.Hlist);
		
		
		Hlist.clear();
		
		HashMap<String,String> temp = new HashMap<>();
		//temp.put(CODE_COLUMN,"Code");
		//temp.put(CODE_PICTXT,"Picture");
		temp.put(FIRST_COLUMN, "Name");
		temp.put(SECOND_COLUMN, "Brand");
		temp.put(THIRD_COLUMN, "Item Type");
		temp.put(FOURTH_COLUMN, "Price");
		Hlist.add(temp);
		
		
		ListViewAdapterHEADER adapter1 = new ListViewAdapterHEADER(getActivity(), Hlist);
		hList.setAdapter(adapter1);
		
		populateList();
		
		
		return rootView;
		
	}
	
	
	private ArrayList<HashMap<String, String>> populateList() {
		
		ArrayList<HashMap<String, String>> data;
		
		list = new ArrayList<>();
		
		data = new ArrayList<>();
		
		
		data.clear();
		PosDB db =  PosDB.getInstance(getActivity());
		db.OpenDb();
		data = db.getInventoryList();
		
		db.CloseDb();
		
		if (data.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				
				list.add(data.get(i));
				
			 
			}
			
			
			ListViewAdapter adapter = new ListViewAdapter(getActivity(), list);
			
			lview.setAdapter(adapter);
			
			
		}
		
		
		return list;
		
		
	}
	
	
}
