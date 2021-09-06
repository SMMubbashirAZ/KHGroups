package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.CommitmentHeaderAdapter;
import com.blazeminds.pos.CommitmentListAdapter;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.ExpenseListAdapter;
import com.blazeminds.pos.ExpenseListHeaderAdapter;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;

/**
 * Created by Blazeminds on 3/7/2018.
 */

public class Expenses extends Fragment {
	
	public final static String TAG = Expenses.class.getSimpleName();
	static public FilterWithSpaceAdapter<String> myAdapterCustomer, myAdapterShop;
	static public String[] itemCustomer = new String[]{"Please search..."};
	static public String[] itemShop = new String[]{"Please search..."};
	static EditText amountEdtTxt, remarksEdtTxt;
	static TextView locStatusTxt, startTimeTxt, fromDateTxt, toDateTxt;
	static Spinner expenseTypeDrop, commitmentDrop;
	static long selectedExpenseType, selectedCommitment;
	static Button submitBtnExpense, submitBtnCommitment;
	static PosDB db;
	static String[] CityArea;
	static ArrayList<HashMap<String, String>> list;
	static ArrayList<HashMap<String, String>> data;
	static ArrayList<HashMap<String, String>> dataCommitment;
	static ArrayList<String> dataCustId;
	static ArrayList<HashMap<String, String>> Hlist;
	static ArrayList<HashMap<String, String>> HlistComm;
	static android.widget.ListView ListView, ListviewCommitment;
	static TextView noItemTxt, noItemTxtComm;
	static EditText saleAmountEdtTxt, giftAmountEdtTxt, remarksEdtTxtComm;
	static AutoCompleteTextView CustomerTxt, ShopTxt;
	static String SelectedCustomerId = "0", days = "0", SelectedShopId = "0";
	static LinearLayout expenseLinearLayout, commitmentLinearLayout;
	static boolean selectCustomerItem, selectShopItem;
	static int checkDays;
	
	static Context c;
	
	
	// New Work Start Here
	
	static int pos = 0;
	
	/**
	 * The {@link PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link FragmentStatePagerAdapter}.
	 */
	Expenses.SectionsPagerAdapter mSectionsPagerAdapter;
	
	// public static final String TAG = CustomerList.class.getSimpleName();
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	
	public static Expenses newInstance() {
		return new Expenses();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_item_one, container, false);
		mSectionsPagerAdapter = new Expenses.SectionsPagerAdapter(
																		 getChildFragmentManager());
		//locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
		//locStatusTV = (TextView) v.findViewById(R.id.locStatusTextView);
		mViewPager = v.findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		c = getActivity();
		
		
		return v;
	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	@SuppressLint("ValidFragment")
	public static class DummySectionFragment extends Fragment implements View.OnClickListener {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String pos1 = "";
		// My GPS states
		public static final int GPS_PROVIDER_DISABLED = 1;
		public static final int GPS_GETTING_COORDINATES = 2;
		public static final int GPS_GOT_COORDINATES = 3;
		//public static final int GPS_PROVIDER_UNAVIALABLE = 4;
		//public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
		public static final int GPS_PAUSE_SCANNING = 6;
		public static final int GPS_PROVIDER_DOESNOT_EXIST = 4;
		private static String coordsToSend;
		private static String gAccuracy;
		private static double Latitude, Longitude;
		// Location manager
		private static LocationManager manager;
		private static boolean isOne = true;
		// Location events (we use GPS only)
		private LocationListener locListener = new LocationListener() {
			
			public void onLocationChanged(Location argLocation) {
				printLocation(argLocation, GPS_GOT_COORDINATES);
			}
			
			@Override
			public void onProviderDisabled(String arg0) {
				if (isOne) {
					final String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
					final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					
					final String message = "Set your gps location to high accuracy";
					
					builder.setMessage(message)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int id) {
											startActivityForResult(new Intent(action), 1111);
											d.dismiss();
											
										}
									});
					
					builder.setCancelable(false);
					
					builder.create().show();
					
					printLocation(null, GPS_PROVIDER_DISABLED);
					
					isOne = false;
				}
			}
			
			@Override
			public void onProviderEnabled(String arg0) {
			}
			
			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			}
			
		};
		
		public DummySectionFragment() {
		}
		
		private static ArrayList<HashMap<String, String>> populateListOfExpenses(TextView noItemText) {
			
			data = new ArrayList<>();
			//dataCustId = new ArrayList<String>();
			//dataCustId.clear();
			data.clear();
			
			PosDB db = PosDB.getInstance(c);
			
			db.OpenDb();
			
			///dataCustId = db.getSelectedCustomerID();
			data = db.getExpenseList();
			
			db.CloseDb();
			
			if (data.size() > 0) {
				noItemText.setVisibility(View.GONE);
				ListView.setVisibility(View.VISIBLE);
				ExpenseListAdapter adapter1 = new ExpenseListAdapter(c, data);
				ListView.setAdapter(adapter1);
				
			} else {
				//Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
				ListView.setVisibility(View.GONE);
				noItemText.setVisibility(View.VISIBLE);
			}
			
			
			return data;
			
		}
		
		private static ArrayList<HashMap<String, String>> populateListOfCommitment(TextView noItemText) {
			
			data = new ArrayList<>();
			//dataCustId = new ArrayList<String>();
			//dataCustId.clear();
			data.clear();
			
			PosDB db =   PosDB.getInstance(c);
			
			db.OpenDb();
			
			///dataCustId = db.getSelectedCustomerID();
			data = db.getCommitmentList();
			
			db.CloseDb();
			
			if (data.size() > 0) {
				noItemText.setVisibility(View.GONE);
				ListviewCommitment.setVisibility(View.VISIBLE);
				CommitmentListAdapter adapter1 = new CommitmentListAdapter(c, data);
				ListviewCommitment.setAdapter(adapter1);
				
			} else {
				//Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
				ListviewCommitment.setVisibility(View.GONE);
				noItemText.setVisibility(View.VISIBLE);
			}
			
			
			return data;
			
		}
		
		public static String getDateTime() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			
			DateFormat df = DateFormat.getDateTimeInstance();
			
			//SelectedDate = dateFormat.format(new Date());
			
			return dateFormat.format(new Date());
			//return df.format(new Date());
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			isOne = true;
			
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_expense,
					container, false);
/*
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));
*/
			
			//locStatusTV = (TextView) rootView.findViewById(R.id.locStatusTextView);
			int pos2 = Integer.parseInt(Integer.toString(getArguments().getInt(
					pos1)));
			
			
			try {
				switch (pos2) {
					
					case 1: {
						
						ScrollView paymentRecieving = rootView.findViewById(R.id.expenseLayout);
						paymentRecieving.setVisibility(View.VISIBLE);
						
						Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
						
						paymentRecieving.startAnimation(enter);
						
						initUIExpense(rootView);
						
						listenersExpense();
						
						
					}
					break;
					
					case 2: {
						
						LinearLayout paymentRecievingList = rootView.findViewById(R.id.ShowExpenses);
						paymentRecievingList.setVisibility(View.VISIBLE);
						
						ListView = rootView.findViewById(R.id.listviewRList);
						ListView hList = rootView.findViewById(R.id.Hlist);
						noItemTxt = rootView.findViewById(R.id.NoItemTxt);
						Hlist = new ArrayList<>();
						Hlist.clear();
						
						HashMap<String,String> temp = new HashMap<>();
						temp.put("exp_datetime", "DATETIME");
						temp.put("exp_type", "EXP TYPE");
						temp.put("exp_amount", "AMOUNT");
						temp.put("exp_shop", "SHOP NAME");
						//temp.put(FOURTH_COLUMN, "Contact");
						Hlist.add(temp);
						
						
						ExpenseListHeaderAdapter adapter = new ExpenseListHeaderAdapter(c, Hlist);
						hList.setAdapter(adapter);
						
						populateListOfExpenses(noItemTxt);
						
						
					}
					break;
					
					case 3: {
						
						ScrollView commitmentLayout = rootView.findViewById(R.id.CommitmentLayout);
						commitmentLayout.setVisibility(View.VISIBLE);
						
						initUICommitment(rootView);
						
						listenersCommitment();
						
						
					}
					break;
					
					
					case 4: {
						
						LinearLayout showCommitmentInList = rootView.findViewById(R.id.ShowCommitment);
						showCommitmentInList.setVisibility(View.VISIBLE);
						
						ListviewCommitment = rootView.findViewById(R.id.subList);
						ListView hList = rootView.findViewById(R.id.headerList);
						noItemTxtComm = rootView.findViewById(R.id.noItemTxt);
						Hlist = new ArrayList<>();
						Hlist.clear();
						
						HashMap<String,String> temp = new HashMap<>();
						temp.put("comm_datetime", "DATETIME");
						temp.put("comm_customer_name", "SHOP NAME");
						temp.put("comm_sale_amount", "SALE AMT");
						temp.put("comm_gift_amount", "GIFT AMT");
						//temp.put(FOURTH_COLUMN, "Contact");
						Hlist.add(temp);
						
						
						CommitmentHeaderAdapter adapter = new CommitmentHeaderAdapter(c, Hlist);
						hList.setAdapter(adapter);
						
						populateListOfCommitment(noItemTxtComm);
						
						
					}
					break;
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.fillInStackTrace());
				
			}
			
			return rootView;
		}
		
		private void initUIExpense(View rootView) {
			
			db = PosDB.getInstance(c);
			
			
			locStatusTxt = rootView.findViewById(R.id.locStatusTxt);
			
			manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			startLocation();
			
			expenseLinearLayout = rootView.findViewById(R.id.expenseLinearLayout);
			selectShopItem = false;
			
			ShopTxt = rootView.findViewById(R.id.SelectShop);
			
			startTimeTxt = rootView.findViewById(R.id.startTime);
			submitBtnExpense = rootView.findViewById(R.id.SubmitBtn);
			remarksEdtTxt = rootView.findViewById(R.id.NotesTxt);
			expenseTypeDrop = rootView.findViewById(R.id.expenseTypeDrop);
			commitmentDrop = rootView.findViewById(R.id.commitmentDrop);
			amountEdtTxt = rootView.findViewById(R.id.amountEdtTxt);
			
			amountEdtTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			amountEdtTxt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			
			remarksEdtTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
			remarksEdtTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
			
			CityArea = new String[3];
			
			DropdownSetupForExpenseType(expenseTypeDrop);
			
			DropdownSetupForCommitment(commitmentDrop);
			startTimeTxt.setText(getDateTime());
			
			startTimeTxt.setVisibility(View.GONE);
		}
		
		private void listenersExpense() {
			
			submitBtnExpense.setOnClickListener(this);
			expenseLinearLayout.setOnClickListener(this);
			
			myAdapterShop = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, itemShop);
			ShopTxt.setAdapter(myAdapterShop);
			
			ShopTxt.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
				
				}
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
					
					ShopTxt.setError(null);
					
					// update the adapater
					myAdapterShop.notifyDataSetChanged();
					myAdapterShop = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, itemShop);
					ShopTxt.setAdapter(myAdapterShop);
					
					
					if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
						Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();
						
					} else {
						
						itemShop = getCustomerNameFromDb(Constant.testInput(s.toString()), db);
						
						
						db.OpenDb();
						
						SelectedShopId = (Constant.testInput(db.getCustomerID(s.toString())));
						//SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
						if (!SelectedShopId.equalsIgnoreCase("0")) {
							DropdownSetupForCommitment(commitmentDrop);
							
						}
						db.CloseDb();
					}
					
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				
				
				}
			});
			
			ShopTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					
					selectShopItem = true;
					ShopTxt.setEnabled(false);
					ShopTxt.setTextColor(Color.BLACK);
					
				}
			});
		}
		
		private void initUICommitment(View rootView) {
			
			commitmentLinearLayout = rootView.findViewById(R.id.CommitmentLinearLayout);
			selectCustomerItem = false;
			
			CustomerTxt = rootView.findViewById(R.id.SelectCustomer);
			fromDateTxt = rootView.findViewById(R.id.fromDateTxt);
			toDateTxt = rootView.findViewById(R.id.toDateTxt);
			saleAmountEdtTxt = rootView.findViewById(R.id.saleAmountEdtTxt);
			giftAmountEdtTxt = rootView.findViewById(R.id.giftAmountEdtTxt);
			remarksEdtTxtComm = rootView.findViewById(R.id.remarksEdtTxt);
			submitBtnCommitment = rootView.findViewById(R.id.SubmitBtnCommitment);
			
			
			saleAmountEdtTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			saleAmountEdtTxt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			
			giftAmountEdtTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			giftAmountEdtTxt.setRawInputType(InputType.TYPE_CLASS_NUMBER);
			
			remarksEdtTxtComm.setImeOptions(EditorInfo.IME_ACTION_DONE);
			remarksEdtTxtComm.setRawInputType(InputType.TYPE_CLASS_TEXT);
			
		}
		
		private void listenersCommitment() {
			
			fromDateTxt.setOnClickListener(this);
			toDateTxt.setOnClickListener(this);
			submitBtnCommitment.setOnClickListener(this);
			commitmentLinearLayout.setOnClickListener(this);
			
			myAdapterCustomer = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, itemCustomer);
			CustomerTxt.setAdapter(myAdapterCustomer);
			
			CustomerTxt.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
				
				}
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
					
					CustomerTxt.setError(null);
					
					// update the adapater
					myAdapterCustomer.notifyDataSetChanged();
					myAdapterCustomer = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, itemCustomer);
					CustomerTxt.setAdapter(myAdapterCustomer);
					
					
					if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
						Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();
						
					} else {
						
						itemCustomer = getCustomerNameFromDb(Constant.testInput(s.toString()), db);
						
						
						db.OpenDb();
						
						SelectedCustomerId = (db.getCustomerID(Constant.testInput(s.toString())));
						//SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
						db.CloseDb();
					}
					
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				
				
				}
			});
			
			CustomerTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					
					selectCustomerItem = true;
					CustomerTxt.setEnabled(false);
					CustomerTxt.setTextColor(Color.BLACK);
					
				}
			});
		}
		
		public String[] getCustomerNameFromDb(CharSequence searchTerm, PosDB db) {
			
			// add items on the array dynamically
			db.OpenDb();
			List<MyObject> products = db.GetCustomerAutoCompleteData(searchTerm);
			
			db.CloseDb();
			int rowCount = products.size();
			
			String[] item = new String[rowCount];
			int x = 0;
			
			for (MyObject record : products) {
				
				item[x] = record.objectName;
				x++;
				
			}
			
			return item;
		}
		
		public void DropdownSetupForExpenseType(Spinner dropDown) {
			
			List<String> ItemsData;
			List<String> ItemsID;
			
			db.OpenDb();
			ItemsData = db.getExpenseTypeForDropDown();
			ItemsID = db.getExpenseTypeIDForDropDown();
			db.CloseDb();
			
			
			ItemsData.add(0, "Select Expense Type ");
			ItemsID.add(0, "0");
			
			
			try {
				// Creating adapter for spinner
				
				
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
						android.R.layout.simple_spinner_item, ItemsData);
				
				// Drop down layout style - editOrderList view with radio button
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				// attaching data adapter to spinner
				dropDown.setAdapter(dataAdapter);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			
			final List<String> finalItemsID = ItemsID;
			dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
					
					
					selectedExpenseType = Long.parseLong(finalItemsID.get(i));
					/*db.OpenDb();
					db.updateSavedRoute(finalItemsID.get(i));
                    db.CloseDb();*/
					
					Log.d("sql", "expenseType id: " + finalItemsID.get(i));
					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {
				
				}
			});
			
			
		}
		
		public void DropdownSetupForCommitment(Spinner dropDown) {
			
			List<String> ItemsData = new ArrayList<>();
			List<String> ItemsID = new ArrayList<>();
			
			db.OpenDb();
			if (!SelectedShopId.isEmpty() && !SelectedShopId.equals("")) {
				ItemsData = db.getCommitmentForDropDown(Integer.parseInt(SelectedShopId));
				ItemsID = db.getCommitmentIDForDropDown(Integer.parseInt(SelectedShopId));
			}
			db.CloseDb();
			
			
			ItemsData.add(0, "Select Commitment ");
			ItemsID.add(0, "0");
			
			
			try {
				// Creating adapter for spinner
				
				
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActivity(),
						android.R.layout.simple_spinner_item, ItemsData);
				
				// Drop down layout style - editOrderList view with radio button
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				// attaching data adapter to spinner
				dropDown.setAdapter(dataAdapter);
			} catch (Exception e) {
				System.out.println(e.toString());
			}
			
			final List<String> finalItemsID = ItemsID;
			dropDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
					
					
					selectedCommitment = Long.parseLong(finalItemsID.get(i));
					/*db.OpenDb();
					db.updateSavedRoute(finalItemsID.get(i));
                    db.CloseDb();*/
					
					Log.d("sql", "commitment id: " + finalItemsID.get(i));
					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {
				
				}
			});
			
			
		}
		
		private boolean checkDays() {
			boolean valid;
			if (days.isEmpty()) {
				checkDays = 0;
			} else {
				checkDays = Integer.parseInt(days);
			}
			valid = checkDays <= 0;
			return valid;
			
		}
		
		@Override
		public void onClick(View view) {
			
			switch (view.getId()) {
				
				case R.id.fromDateTxt: {
					final Calendar c = Calendar.getInstance();
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH);
					com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog1 = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
							new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
									
									
									String date = String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear + 1), year);
									fromDateTxt.setText(date);
									
									if (!toDateTxt.getText().toString().equalsIgnoreCase("To Date")) {
										String toDate = toDateTxt.getText().toString();
										String fromDate = fromDateTxt.getText().toString();
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										Date fromdate = null, todate = null;
										try {
											fromdate = sdf.parse(fromDate);
											todate = sdf.parse(toDate);
										} catch (ParseException e) {
											e.printStackTrace();
										}
										
										
										long diff = todate.getTime() - fromdate.getTime();
										System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
										days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + "";
										//noOfDaysTV.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + "");
									}
									
								}
							}, mYear, mMonth, mDay);
					
					datePickerDialog1.setTitle("Select Date");
					//datePickerDialog1.setVersion(com.wdullaer.materialdatetimepicker.date.DatePickerDialog.Version.VERSION_1);
					datePickerDialog1.setAccentColor(getActivity().getResources().getColor(R.color.colorPrimary));
					datePickerDialog1.show(getActivity().getFragmentManager(), "DatePickerDialog");
					
				}
				break;
				
				case R.id.toDateTxt: {
					
					final Calendar c = Calendar.getInstance();
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH);
					com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog1 = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
							new com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
									
									String date = String.format("%02d/%02d/%04d", dayOfMonth, (monthOfYear + 1), year);
									toDateTxt.setText(date);
									
									if (!fromDateTxt.getText().toString().equalsIgnoreCase("From Date")) {
										
										String toDate = toDateTxt.getText().toString();
										String fromDate = fromDateTxt.getText().toString();
										SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
										Date fromdate = null, todate = null;
										try {
											fromdate = sdf.parse(fromDate);
											todate = sdf.parse(toDate);
										} catch (ParseException e) {
											e.printStackTrace();
										}
										
										
										long diff = todate.getTime() - fromdate.getTime();
										System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
										days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + "";
									}
									// noOfDaysTV.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+"");
									
								}
							}, mYear, mMonth, mDay);
					
					//datePickerDialog1.setMinDate(c.getTimeInMillis());
					
					datePickerDialog1.setTitle("Select Date");
					datePickerDialog1.setAccentColor(getActivity().getResources().getColor(R.color.colorPrimary));
					datePickerDialog1.show(getActivity().getFragmentManager(), "DatePickerDialog");
					
				}
				break;
				
				case R.id.SubmitBtnCommitment: {
					
					if (CustomerTxt.getText().toString().trim().isEmpty() || SelectedCustomerId.equalsIgnoreCase("0") || SelectedCustomerId.trim().isEmpty()
								|| checkDays()
								|| saleAmountEdtTxt.getText().toString().trim().isEmpty()
								|| giftAmountEdtTxt.getText().toString().trim().isEmpty()) {
						
						if (CustomerTxt.getText().toString().trim().isEmpty()) {
							CustomerTxt.setError("Customer Required");
						}
						
						if (SelectedCustomerId.equalsIgnoreCase("0")) {
							CustomerTxt.setError("Customer Invalid");
						}
						
						if (SelectedCustomerId.trim().isEmpty()) {
							CustomerTxt.setError("Customer Invalid");
							
						}
						
						if (checkDays()) {
							Toast.makeText(AppContextProvider.getContext(), "Invalid Dates Selected", Toast.LENGTH_SHORT).show();
						}
						
						if (saleAmountEdtTxt.getText().toString().trim().isEmpty()) {
							saleAmountEdtTxt.setError("Sale Amount Required");
						}
						
						if (giftAmountEdtTxt.getText().toString().trim().isEmpty()) {
							giftAmountEdtTxt.setError("Gift Amount Required");
						}
						
					} else {
						// Entry in DB
						submitBtnCommitment.setClickable(false);
						db.OpenDb();
						int maxIdComm = db.getMaxIdFromCommitment();
						long commInsId = db.createCommitment(maxIdComm + 1, Integer.parseInt(SelectedCustomerId), fromDateTxt.getText().toString(), toDateTxt.getText().toString(), saleAmountEdtTxt.getText().toString(), giftAmountEdtTxt.getText().toString(), remarksEdtTxtComm.getText().toString(), getDateTime(), 1, 0, 1);
						if (commInsId > 0) {
							
							CustomerTxt.setText("");
							fromDateTxt.setText("From Date");
							toDateTxt.setText("To Date");
							saleAmountEdtTxt.setText("");
							giftAmountEdtTxt.setText("");
							remarksEdtTxtComm.setText("");
							submitBtnCommitment.setClickable(true);
							populateListOfCommitment(noItemTxtComm);
							Toast.makeText(AppContextProvider.getContext(), "Commitment Created", Toast.LENGTH_SHORT).show();
						} else {
							//Toast.makeText(getActivity(), "Commitment Not Inserted in DB, Something went wrong",Toast.LENGTH_SHORT).show();
							
						}
					}
					
				}
				break;
				
				case R.id.SubmitBtn: {
					
					if (checkLocationPermission(getActivity())/*c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED*/) {
						
						//new Handler().postDelayed(new Runnable() {
						
						// @Override
						//  public void run() {
						
						
						if (Constant.networkAvailable()) {
							
							
							// If Check Commented By me because Lati and Logi I m getting are 0
							// if( Latitude !=0 && Longitude !=0 ){
							//CityArea = getCityAreaFromLatitudeLongitude(latitude, longitude, ShopRegActivity.this, ldr);
							
							if (Latitude != 0 && Longitude != 0) {
								if (getActivity() != null)
									CityArea = getCityAreaCountryFromLatitudeLongitude(Latitude, Longitude, getActivity());
								
								
							} else {
								CityArea = new String[3];
								//Toast.makeText(getActivity(), "Please Move to Open Area", Toast.LENGTH_SHORT).show();
								
							}
							//if (CityArea != null) {
							
							//}
							
							//}
							//cityEdt.setText(CityArea[0]+"");
							//areaEdt.setText(CityArea[1]+"");
							//CountryName = CityArea[2]+"";
							//locStatusTV.setText(Latitude + " , " + Longitude );
							
							
						} else {
							//Constant.CustomDialogNoInternet(getActivity());
							
						}
						
						//}
						//}, 1000);
						
					} else {
						// loader.HideLoader();
						Toast.makeText(AppContextProvider.getContext(), "Location Permission required", Toast.LENGTH_SHORT).show();
					}
					
					if (ShopTxt.getText().toString().trim().isEmpty() || SelectedShopId.equalsIgnoreCase("0") || SelectedShopId.trim().isEmpty()
								|| amountEdtTxt.getText().toString().trim().isEmpty()
								|| selectedExpenseType == 0
								|| selectedCommitment == 0
								|| locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")/*Latitude == 0.0 || Longitude == 0.0*/) {
						
						if (ShopTxt.getText().toString().trim().isEmpty()) {
							ShopTxt.setError("Customer Required");
						}
						
						if (SelectedShopId.equalsIgnoreCase("0")) {
							ShopTxt.setError("Customer Invalid");
						}
						
						if (SelectedShopId.trim().isEmpty()) {
							ShopTxt.setError("Customer Invalid");
							
						}
						
						if (amountEdtTxt.getText().toString().trim().isEmpty()) {
							
							amountEdtTxt.setError("Amount Required");
						}
						
						if (selectedExpenseType == 0) {
							Toast.makeText(AppContextProvider.getContext(), "Please Select Expense Type", Toast.LENGTH_SHORT).show();
							
						}
						
						if (selectedCommitment == 0) {
							Toast.makeText(AppContextProvider.getContext(), "Please Select Commitment", Toast.LENGTH_SHORT).show();
							
						}
						
						if (locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")) {
							Toast.makeText(AppContextProvider.getContext(), "Enable your GPS first", Toast.LENGTH_SHORT).show();
							
						}

                                    /*if (Latitude == 0.0){
                                        Toast.makeText(getActivity(), "Open GPS & Please Move to Open Area", Toast.LENGTH_SHORT).show();

                                    }

                                    if (Longitude == 0.0){
                                        Toast.makeText(getActivity(), "Open GPS & Please Move to Open Area", Toast.LENGTH_SHORT).show();

                                    }*/
						
					} else {
						
						submitBtnExpense.setClickable(false);
						String cityArea;
						if (CityArea == null) {
							cityArea = "N/A";
						} else {
							cityArea = CityArea[1];
						}
						db.OpenDb();
						int expenseId = db.getMaxIdFromExpense();
						
						long expenseInserted = db.createExpense(expenseId + 1, Integer.parseInt(SelectedShopId), Integer.parseInt(String.valueOf(selectedCommitment)), amountEdtTxt.getText().toString(), Integer.parseInt(String.valueOf(selectedExpenseType)), startTimeTxt.getText().toString(), getDateTime(), remarksEdtTxt.getText().toString(), Latitude, Longitude, /*CityArea[1]*/cityArea, 1);
						db.CloseDb();
						if (expenseInserted > 0) {
							Toast.makeText(AppContextProvider.getContext(), "Expense Created", Toast.LENGTH_SHORT).show();
							
							getActivity().finish();
							getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
						} else {
							//Toast.makeText(getActivity(), "Expense Not Inserted in DB, something went wrong", Toast.LENGTH_SHORT).show();
							
						}
						
					}
					
				}
				break;
				
				case R.id.expenseLinearLayout: {
					
					if (selectShopItem) {
						ShopTxt.setEnabled(true);
					}
					
				}
				break;
				
				case R.id.CommitmentLinearLayout: {
					
					if (selectCustomerItem) {
						CustomerTxt.setEnabled(true);
					}
					
				}
				break;
			}
		}
		
		private void printLocation(Location loc, int state) {
			
			String result = "";
			switch (state) {
				case GPS_PROVIDER_DOESNOT_EXIST:
					result = "GPS Doesn't Exist";
					if (locStatusTxt != null)
						locStatusTxt.setTextColor(Color.RED);
					break;
				case GPS_PROVIDER_DISABLED:
					result = "GPS Disabled";
					if (locStatusTxt != null)
						locStatusTxt.setTextColor(Color.RED);
					break;
				case GPS_GETTING_COORDINATES:
					
					result = "Fetching Coordinates";
					if (locStatusTxt != null)
						locStatusTxt.setTextColor(Color.BLACK);
					
					break;
				case GPS_PAUSE_SCANNING:
					result = "GPS Scanning Paused";
					if (locStatusTxt != null)
						locStatusTxt.setTextColor(Color.RED);
					
					break;
				case GPS_GOT_COORDINATES:
					if (loc != null) {
						if (locStatusTxt != null)
							locStatusTxt.setTextColor(Color.BLACK);
						
						//locStatusTxt.setTextColor(getColor(fragmentActivity, R.color.black));
						// Accuracy
						if (loc.getAccuracy() < 0.0001) {
							gAccuracy = "?";
						} else if (loc.getAccuracy() > 99) {
							gAccuracy = "> 99";
						} else {
							gAccuracy = String.format(Locale.US, "%2.0f",
									loc.getAccuracy());
						}
						
						String separ = System.getProperty("line.separator");
						
						String la = String
											.format(Locale.US, "%2.7f", loc.getLatitude());
						String lo = String.format(Locale.US, "%3.7f",
								loc.getLongitude());
						
						coordsToSend = la + "," + lo;
						
						Latitude = loc.getLatitude();
						Longitude = loc.getLongitude();
						result = coordsToSend;
						
						
					} else {
						result = "GPS_UNAVAILABLE";
						if (locStatusTxt != null)
							locStatusTxt.setTextColor(Color.RED);
						
					}
					break;
			}
			if (locStatusTxt != null)
				locStatusTxt.setText(result);
			
		}
		
		
		private void stopLocation() {
			
			try {
				manager.removeUpdates(locListener);
			} catch (SecurityException ignored) {
			}
		}
		
		private void startLocation() {
			// Возобновляем работу с GPS-приемником
			
			try {
				if (manager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
					manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locListener);
					
					
					if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						printLocation(null, GPS_GETTING_COORDINATES);
					}
				} else {
					Toast.makeText(AppContextProvider.getContext(), "GPS Doesn't Exist", Toast.LENGTH_SHORT).show();
					printLocation(null, GPS_PROVIDER_DOESNOT_EXIST);
					
				}
			} catch (SecurityException ignored) {
			}
		}
		
		@Override
		public void onPause() {
			super.onPause();
			stopLocation();
		}
		
		@Override
		public void onResume() {
			super.onResume();
			startLocation();

        /*Toast.makeText(getActivity(), "Latitude = "+Latitude+"" , Toast.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), "Longitude = "+Longitude+"" , Toast.LENGTH_SHORT).show();*/
		}
		
		
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		
		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new Expenses.DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(Expenses.DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			args.putInt(Expenses.DummySectionFragment.pos1, position + 1);
			pos = position;
			fragment.setArguments(args);
			return fragment;
		}
		
		@Override
		public int getCount() {
			// Show 2 total pages (tabs).
			return 4;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return "Expense".toUpperCase();
				case 1:
					return "Expense List".toUpperCase();
				case 2:
					return "Commitment".toUpperCase();
				case 3:
					return "Commitment List".toUpperCase();

/*
			case 2:
                return "Shop gps".toUpperCase();
*/
			
			}
			return null;
		}
	}
}
