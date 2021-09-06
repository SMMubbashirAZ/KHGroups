package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.ListViewAdapterDailyExpense;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.blazeminds.pos.PosDB.getDateTime;


public class DailyExpenseSheet extends Fragment {

	public final static String TAG = DailyExpenseSheet.class.getSimpleName();

	PosDB db;
	public DailyExpenseSheet() {
	}
	
	public static DailyExpenseSheet newInstance() {
		return new DailyExpenseSheet();
	}


ListView DailyExpenseinputList;
	TextView DateSelected;

	Calendar myCalendarStart;
	DatePickerDialog.OnDateSetListener Startdate;
Button submitBtn;
	ListViewAdapterDailyExpense listAdapt;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		db = PosDB.getInstance(getActivity());
		db.OpenDb();

		View rootView = inflater.inflate(R.layout.daily_expense_fragment, container, false);

		myCalendarStart = Calendar.getInstance();
		Startdate= new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
								  int dayOfMonth) {

				// TODO Auto-generated method stub
				myCalendarStart.set(Calendar.YEAR, year);
				myCalendarStart.set(Calendar.MONTH, monthOfYear);
				myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				String myFormat = "yyyy-MM-dd"; //In which you need put here
				SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

				DateSelected.setText(sdf.format(myCalendarStart.getTime()));
			}

		};

		DateSelected= rootView.findViewById(R.id.DateSelected);
		submitBtn = rootView.findViewById(R.id.submitBtn);
		DateSelected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(getActivity(),  R.style.DateDialogTheme, Startdate, myCalendarStart
						.get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
						myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		SharedPreferences FormsElementDatasharedPreferences = getActivity().getSharedPreferences(
				"FormsElementData", MODE_PRIVATE);
	String values_expense_type=	FormsElementDatasharedPreferences.getString("values_expense_type","[]");
		listAdapt= new ListViewAdapterDailyExpense(getActivity(),new JSONArray());
		try {
			 listAdapt= new ListViewAdapterDailyExpense(getActivity(),new JSONArray(values_expense_type));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		listAdapt.notifyDataSetChanged();
		DailyExpenseinputList= rootView.findViewById(R.id.DailyExpenseinputList);
		DailyExpenseinputList.setAdapter(listAdapt);
ResizeList(DailyExpenseinputList);


		submitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(DateSelected.getText().toString().equals("")|| DateSelected.getText().toString().equals("")|| DateSelected.getText().toString().equals("Date Selected"))
				{
					Toast.makeText(getActivity(), " Please Select Date ", Toast.LENGTH_SHORT).show();
					return;
				}

					SharedPreferences FormsDatasharedPreferences = getActivity().getSharedPreferences(
							"FormsData", MODE_PRIVATE);

						SharedPreferences.Editor editor =
								FormsDatasharedPreferences.edit();
					JSONArray DailyExpenseDataUnSync= null;
					try {
						DailyExpenseDataUnSync = new JSONArray(FormsDatasharedPreferences.getString("DailyExpenseDataUnSync","[]"));

					JSONObject DailyExpenseDataUnSyncObject = new JSONObject();

						DailyExpenseDataUnSyncObject.put("date",DateSelected.getText().toString());
DailyExpenseDataUnSyncObject.put("details",listAdapt.getList());
						DailyExpenseDataUnSyncObject.put("date_time",getDateTime());
						DailyExpenseDataUnSyncObject.put("synced","0");
						DailyExpenseDataUnSyncObject.put("Approved","0");
						DailyExpenseDataUnSync.put(DailyExpenseDataUnSyncObject);

					} catch (JSONException e) {
						Toast.makeText(getActivity(),"Please Try Again",Toast.LENGTH_LONG).show();

						return;
					}
					editor.putString("DailyExpenseDataUnSync",DailyExpenseDataUnSync.toString());


						editor.apply();



					Toast.makeText(getActivity(), "Daily Expense Submitted Successfully", Toast.LENGTH_SHORT).show();
					getActivity().getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.content_frame, DailyExpenseSheetFinal.newInstance(), DailyExpenseSheetFinal.TAG).addToBackStack(DailyExpenseSheetFinal.TAG).commit();




		}});

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
			params.height = (totalHeight + (lv.getDividerHeight() * (listadp.getCount() )));
			lv.setLayoutParams(params);
			lv.requestLayout();}



	}

}
