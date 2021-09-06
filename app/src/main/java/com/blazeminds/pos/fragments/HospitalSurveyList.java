package com.blazeminds.pos.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.blazeminds.pos.adapter.ListViewAdapter2row;
import com.blazeminds.pos.adapter.ListViewAdapter3rowqty;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.REPORT_FIRST_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_SECOND_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_THIRD_COLUMN;


public class HospitalSurveyList extends Fragment {

	public final static String TAG = HospitalSurveyList.class.getSimpleName();
ListView PatientListView;

	PosDB db;
	public HospitalSurveyList() {
	}

	public static HospitalSurveyList newInstance() {
		return new HospitalSurveyList();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		db=PosDB.getInstance(getActivity());
		db.OpenDb();
		View rootView = inflater.inflate(R.layout.fragment_place_order_list, container, false);

		LinearLayout lay = rootView.findViewById(R.id.PatientOrderlistFrag);
		PatientListView= rootView.findViewById(R.id.PatientOrderListView);

		final ListViewAdapter3rowqty PatientListAdapter= new ListViewAdapter3rowqty(getActivity(),db.getPatientOrderList());
		PatientListView.setAdapter(PatientListAdapter);
		PatientListAdapter.notifyDataSetChanged();

		ResizeList();
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
