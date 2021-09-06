package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MyService;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.AdapterHospitalSurvey;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.adapter.ListViewAdapter3row;
import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.webservice_url.RetrofitWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_FIRST_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_SECOND_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_THIRD_COLUMN;
import static com.blazeminds.pos.Constant.customToast;
import static com.blazeminds.pos.PosDB.DATABASE_TABLE_INVENTORY;
import static com.blazeminds.pos.PosDB.KEY_QUANTITY;


public class HospitalSurvey extends Fragment {
	
	public final static String TAG = HospitalSurvey.class.getSimpleName();
AutoCompleteTextView HospitalEdtxt;
Spinner PurchasingCycle,PurchaseProcedure,TypeOfHospital,GeoArea;
ListView ServicesProvidedbyHospital,ComitteMembers;
	Button submitBtn,Addbtn;
//	/tcher txtwatcher;
AdapterHospitalSurvey	listAdapt;
	PosDB db;
	public HospitalSurvey() {
	}
	
	public static HospitalSurvey newInstance() {
		return new HospitalSurvey();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		db=PosDB.getInstance(getActivity());
		db.OpenDb();
		View rootView = inflater.inflate(R.layout.fragment_hospital_survey, container, false);
		HospitalEdtxt= rootView.findViewById(R.id.HospitalEdtxt);
		PurchasingCycle= rootView.findViewById(R.id.PurchasingCycle);
		PurchaseProcedure= rootView.findViewById(R.id.PurchaseProcedure);
		TypeOfHospital= rootView.findViewById(R.id.TypeOfHospital);
		GeoArea= rootView.findViewById(R.id.GeoArea);
		ServicesProvidedbyHospital= rootView.findViewById(R.id.ServicesProvidedbyHospital);
		ComitteMembers= rootView.findViewById(R.id.ComitteMembers);

		Addbtn= rootView.findViewById(R.id.Addbtn);

		JSONObject ListOb= new JSONObject();
		JSONArray ListArray= new JSONArray();
		try {	ListOb.put("name","");
			ListOb.put("position","");

			ListOb.put("contact","");



		} catch (JSONException e) {
			e.printStackTrace();
		}
		ListArray.put(ListOb);
		listAdapt= new AdapterHospitalSurvey(getActivity(),ListArray);
		listAdapt.notifyDataSetChanged();
		ComitteMembers.setAdapter(listAdapt);
		Addbtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				JSONObject ListOb= new JSONObject();
				JSONArray ListArray= listAdapt.getList();
				try {		ListOb.put("name","");
					ListOb.put("position","");

					ListOb.put("contact","");




				} catch (JSONException e) {
					e.printStackTrace();
				}
				ListArray.put(ListOb);
				listAdapt= new AdapterHospitalSurvey(getActivity(),ListArray);

				listAdapt.notifyDataSetChanged();
				ComitteMembers.setAdapter(listAdapt);
				ResizeList(ComitteMembers);




			}});


		FilterWithSpaceAdapter PatientNameAdapter= new FilterWithSpaceAdapter(getActivity(), android.R.layout.simple_list_item_1);

//
		PatientNameAdapter.addAll(db.getCustomerNameList());
		HospitalEdtxt.setAdapter(PatientNameAdapter);
		PatientNameAdapter.notifyDataSetChanged();
		return rootView;
		
	}
	void ResizeList(ListView lv)
	{
		ListAdapter listadp = lv.getAdapter();
		if (listadp != null) {

			int totalHeight = 0;
			for (int i = 0; i < listadp.getCount(); i++) {
				View listItem = listadp.getView(i, null, lv);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = lv.getLayoutParams();
			params.height = (totalHeight + (lv.getDividerHeight() * (listadp.getCount() )*2));
			lv.setLayoutParams(params);
			lv.requestLayout();}



	}

}
