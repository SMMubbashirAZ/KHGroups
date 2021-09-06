package com.blazeminds.pos.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.DailyExpenseSheetListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import static android.content.Context.MODE_PRIVATE;


public class DailyExpenseSheetList extends Fragment {

	public final static String TAG = DailyExpenseSheetList.class.getSimpleName();
ListView PatientListView;

	PosDB db;
	public DailyExpenseSheetList() {
	}

	public static DailyExpenseSheetList newInstance() {
		return new DailyExpenseSheetList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		db = PosDB.getInstance(getActivity());
		db.OpenDb();
		View rootView = inflater.inflate(R.layout.fragment_daily_expense_list, container, false);

		LinearLayout lay = rootView.findViewById(R.id.PatientOrderlistFrag);
		PatientListView= rootView.findViewById(R.id.PatientOrderListView);
		SharedPreferences FormsDatasharedPreferences = getActivity().getSharedPreferences(
				"FormsData", MODE_PRIVATE);
		DailyExpenseSheetListAdapter PatientListAdapter= new DailyExpenseSheetListAdapter(getActivity(),new JSONArray());
		try {

			JSONArray tempjs= new JSONArray(FormsDatasharedPreferences.getString("DailyExpenseDataUnSync", "[]"));
		JSONArray tempjs0= new JSONArray(FormsDatasharedPreferences.getString("DailyExpenseDataValues", "[]"));
			JSONArray tempjs2= new JSONArray();
			for(int i=0;i<tempjs.length();i++)
			{


				tempjs2.put(tempjs.getJSONObject(i));
			}
			for(int i=0;i<tempjs0.length();i++)
			{


				tempjs2.put(tempjs0.getJSONObject(i));
			}

			PatientListAdapter = new DailyExpenseSheetListAdapter(getActivity(),tempjs2);

			PatientListView.setAdapter(PatientListAdapter);
			PatientListAdapter.notifyDataSetChanged();
			ResizeList();
		}catch (JSONException e) {
			e.printStackTrace();
		}
//		PatientListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//
//				AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
//
//				builderSingle.setTitle( PatientListAdapter.getItem(i).get(REPORT_SECOND_COLUMN)+": "+PatientListAdapter.getItem(i).get(REPORT_THIRD_COLUMN));
//
//				final ListViewAdapter2row arrayAdapter = new ListViewAdapter2row(getActivity(), db.getPatientOrderList_By_OrderID(PatientListAdapter.getItem(i).get(REPORT_FIRST_COLUMN)));
//
//
//				builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.dismiss();
//					}
//				});
//
//				builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
////						String strName = arrayAdapter.getItem(which);
////						AlertDialog.Builder builderInner = new AlertDialog.Builder(getActivity());
////						builderInner.setMessage(strName);
////						builderInner.setTitle("Your Selected Item is");
////						builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
////							@Override
////							public void onClick(DialogInterface dialog,int which) {
////								dialog.dismiss();
////							}
////						});
//		//				builderInner.show();
//					}
//				});
//				builderSingle.show();
//			}
//		});
	return  rootView;
	}



	void ResizeList()
	{
		ListAdapter listadp = PatientListView.getAdapter();
		if (listadp != null) {

			int totalHeight = 0;
			for (int i = 0; i < listadp.getCount(); i++) {
				View listItem = listadp.getView(i, null, PatientListView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = PatientListView.getLayoutParams();
			params.height = (totalHeight + (PatientListView.getDividerHeight() * (listadp.getCount() - 1)))*2;
			PatientListView.setLayoutParams(params);
			PatientListView.requestLayout();}



	}
}
