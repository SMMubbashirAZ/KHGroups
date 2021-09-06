package com.blazeminds.pos.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


import com.blazeminds.pos.ListViewAdapter4OfflineTracking;
import com.blazeminds.pos.ListViewAdapter4row;

import com.blazeminds.pos.ListViewAdapterdsr;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DSRSheet extends Fragment {
	
	public final static String TAG = DSRSheet.class.getSimpleName();

	
	public DSRSheet() {
	}
	
	public static DSRSheet newInstance() {
		return new DSRSheet();
	}
	PosDB db;
	Spinner DateSpinner,RouteSpinner;
	java.util.Date TempDate = null;
	ListView DSRSheetListView;
AutoCompleteTextView ShopAutoComplete;
	ListViewAdapterdsr listadapter;
Button Search,SearchAll;
	DateFormat SearchingDateFormat;
	SimpleDateFormat VisibleDateFormat;
	TextView NoResult;
	ArrayAdapter<String> Shopadapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_dsr_sheet, container, false);
		db=PosDB.getInstance(getActivity());
		
		DateSpinner= rootView.findViewById(R.id.DateSpinner);
		RouteSpinner=rootView.findViewById(R.id.RouteSpinner);
		ShopAutoComplete=rootView.findViewById(R.id.ShopAutoComplete);
		DSRSheetListView= rootView.findViewById(R.id.DSRSheetListView);
		Search=rootView.findViewById(R.id.Search);
		SearchAll=rootView.findViewById(R.id.SearchAll);
	NoResult=rootView.findViewById(R.id.NoResult);
		ArrayAdapter<String> Dateadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,db.getDateForReportsSpinner() );
		ArrayAdapter<String> Routeadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,db.getRouteListForReportsSpinner() );
Shopadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,db.getCustomersCompanyNameListForReportsSpinner() );
Dateadapter.notifyDataSetChanged();
		Routeadapter.notifyDataSetChanged();
		Shopadapter.notifyDataSetChanged();
		RouteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				if(RouteSpinner.getSelectedItem().toString().equals("All")){
				Shopadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,db.getCustomersCompanyNameListForReportsSpinner() );
				
				Shopadapter.notifyDataSetChanged();
					ShopAutoComplete.setAdapter(Shopadapter);
				}
				else
					{
						Shopadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,db.getCustomersCompanyNameListForReportsSpinnerByRoute(RouteSpinner.getSelectedItem().toString()) );
						
						Shopadapter.notifyDataSetChanged();
						ShopAutoComplete.setAdapter(Shopadapter);
					}
				
				
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			
			}
		});
		if(Dateadapter.getCount()==0)
		{
			DateSpinner.setEnabled(false);
		}
DateSpinner.setAdapter(Dateadapter);

RouteSpinner.setAdapter(Routeadapter);
		ShopAutoComplete.setAdapter(Shopadapter);
	 VisibleDateFormat = new SimpleDateFormat("dd MMM yyyy");
	 SearchingDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		listadapter = new ListViewAdapterdsr(getActivity(),db.getorderDataForDSRSheet() );
		//DSRSheetListView.setAdapter(listadapter);
		ResizeList();
		Search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					TempDate=VisibleDateFormat.parse(DateSpinner.getSelectedItem().toString());
				String SearchDateStr=	SearchingDateFormat.format(TempDate);
				if(!ShopAutoComplete.getText().toString().equals("") && !RouteSpinner.getSelectedItem().toString().equals("All")) {
			if(!ShopAutoComplete.getText().toString().equals("All")){
				listadapter = new ListViewAdapterdsr(getActivity(), db.getorderDataForDSRSheetAllQuery(SearchDateStr,RouteSpinner.getSelectedItem().toString(),ShopAutoComplete.getText().toString()));
				}
			else
				{
					listadapter = new ListViewAdapterdsr(getActivity(), db.getorderDataForDSRSheetRouteQuery(SearchDateStr,RouteSpinner.getSelectedItem().toString()));

				}
				}
				else if(!ShopAutoComplete.getText().toString().equals("")&& RouteSpinner.getSelectedItem().toString().equals("All"))
				{
					if(ShopAutoComplete.getText().toString().equals("All"))
					{
						listadapter = new ListViewAdapterdsr(getActivity(), db.getorderDataForDSRSheetDateQuery(SearchDateStr));

					}
					else
						{
							listadapter = new ListViewAdapterdsr(getActivity(), db.getorderDataForDSRSheetCustomerQuery(SearchDateStr, ShopAutoComplete.getText().toString()));

						}
				}
				else if(ShopAutoComplete.getText().toString().equals("")&& !RouteSpinner.getSelectedItem().toString().equals("All"))
				{

					listadapter = new ListViewAdapterdsr(getActivity(), db.getorderDataForDSRSheetRouteQuery(SearchDateStr,RouteSpinner.getSelectedItem().toString()));

				}

				else
					{
					 listadapter = new ListViewAdapterdsr(getActivity(), db.getorderDataForDSRSheetDateQuery(SearchDateStr));
				}
				listadapter.notifyDataSetChanged();
					DSRSheetListView.setAdapter(listadapter);
			}
			catch (ParseException e)
			{

			}
				ResizeList();
			}
		});
		SearchAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ListViewAdapter4row listadapter = new ListViewAdapter4row(getActivity(),db.getorderDataForDSRSheet() );
				listadapter.notifyDataSetChanged();
				DSRSheetListView.setAdapter(listadapter);
			}
		});

		
		

		
		
		return rootView;
		
	}
	
void ResizeList()
{
	ListAdapter listadp =DSRSheetListView.getAdapter();
	if (listadp != null) {
		NoResult.setVisibility(View.GONE);
		int totalHeight = 0;
		for (int i = 0; i < listadp.getCount(); i++) {
			View listItem = listadp.getView(i, null, DSRSheetListView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = DSRSheetListView.getLayoutParams();
		params.height = totalHeight + (DSRSheetListView.getDividerHeight() * (listadp.getCount() - 1));
		DSRSheetListView.setLayoutParams(params);
		DSRSheetListView.requestLayout();}
	else
		{
			NoResult.setVisibility(View.VISIBLE);
		}
	if(listadapter.getCount()>0)
	{
		NoResult.setVisibility(View.GONE);
	}
	else
		{
			NoResult.setVisibility(View.VISIBLE);
		}
}
	
	
}
