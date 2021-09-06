package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.TravelExpenseAdapter;
import com.blazeminds.pos.TravelExpenseHeaderAdapter;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;

/**
 * Created by Blazeminds on 4/12/2018.
 */

public class TravelExpense extends Fragment {
	
	public final static String TAG = TravelExpense.class.getSimpleName();
	
	
	static Spinner town1Drop, town2Drop, wayDrop, stayDrop;
	static long selectedTown1Id, selectedTown2Id, selectedWayId, selectedStayId;
	static TextView fromDateTV, toDateTV, noOfDaysTV, amountTV, travellingAmountTV, stayCompensationAmountTV, startTimeTV, noItemTxt, locStatusTxt;
	static EditText remarksEdtTxt;
	static Button submitBtn;
	static PosDB db;
	static String amount;
	static int days, sumAmount, stayAccom, totalStayAmt, compensationAmt;
	static int Year, Month, Day, checkDays;
	static String[] CityArea;
	
	static ArrayList<HashMap<String, String>> data;
	static ArrayList<String> dataCustId;
	static ArrayList<HashMap<String, String>> Hlist;
	static android.widget.ListView lview, hList, ListView;
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
	TravelExpense.SectionsPagerAdapter mSectionsPagerAdapter;
	
	// public static final String TAG = CustomerList.class.getSimpleName();
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	
	public static TravelExpense newInstance() {
		return new TravelExpense();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_item_one, container, false);
		mSectionsPagerAdapter = new TravelExpense.SectionsPagerAdapter(
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
		private static boolean isOne = true;
		private static String coordsToSend;
		private static String gAccuracy;
		private static double Latitude, Longitude;
		// Location manager
		private static LocationManager manager;
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
		
		private static ArrayList<HashMap<String, String>> populateListOfTravelExpense(TextView noItemText) {
			
			data = new ArrayList<>();
			//dataCustId = new ArrayList<String>();
			//dataCustId.clear();
			data.clear();
			
			PosDB db =PosDB.getInstance(c);
			
			db.OpenDb();
			
			///dataCustId = db.getSelectedCustomerID();
			data = db.getTravelExpenseList();
			
			db.CloseDb();
			
			if (data.size() > 0) {
				noItemText.setVisibility(View.GONE);
				ListView.setVisibility(View.VISIBLE);
				TravelExpenseAdapter adapter1 = new TravelExpenseAdapter(c, data);
				ListView.setAdapter(adapter1);
				
			} else {
				//Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
				ListView.setVisibility(View.GONE);
				noItemText.setVisibility(View.VISIBLE);
			}
			
			
			return data;
			
		}
		
		private static String getDateTime() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			
			DateFormat df = DateFormat.getDateTimeInstance();
			
			//SelectedDate = dateFormat.format(new Date());
			
			return dateFormat.format(new Date());
			//return df.format(new Date());
		}
		
		private void listeners() {
			
			submitBtn.setOnClickListener(this);
			fromDateTV.setOnClickListener(this);
			toDateTV.setOnClickListener(this);
			
			noOfDaysTV.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				
				}
				
				@Override
				public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				
				
				}
				
				@Override
				public void afterTextChanged(Editable editable) {

                    /*if (selectedTown1Id != 0 && selectedTown2Id != 0) {
						PopulateWayDropDown();
                        PopulateStayDropDown();
                    }*/
					
					db.OpenDb();
					ArrayList<HashMap<String, String>> townTravelFromDB = db.getTownTravelFromDB(String.valueOf(selectedTown1Id), String.valueOf(selectedTown2Id));
					db.CloseDb();
					final ArrayList<String> List = new ArrayList<>();
					final ArrayList<Long> ListID = new ArrayList<>();
					final ArrayList<String> oneWayList = new ArrayList<>();
					final ArrayList<String> twoWayList = new ArrayList<>();
					final ArrayList<String> stayAccomList = new ArrayList<>();
					HashMap<String, String> f = new HashMap<>();
					
					if (townTravelFromDB.size() > 0) {
						for (int i = 0; i < townTravelFromDB.size(); i++) {
							f = townTravelFromDB.get(i);
							
							// stayAccomList.add(f.get("stayAccom"));
							oneWayList.add(f.get("oneWay"));
							twoWayList.add(f.get("twoWay"));
							stayAccomList.add(f.get("stayAccom"));
							
						}
					}
					
					if (selectedWayId == 0 && selectedTown1Id != 0 && selectedTown2Id != 0) {
						
						if (selectedStayId == 0) {
							amount = f.get("oneWay");
							amountTV.setText(amount);
							travellingAmountTV.setText(amount);
							
						} else if (selectedStayId == 1) {
							
							amount = f.get("oneWay");
							sumAmount = Integer.parseInt(amount);
							
							travellingAmountTV.setText(amount);
							
							if (!noOfDaysTV.getText().toString().trim().isEmpty()) {
								days = Integer.parseInt(noOfDaysTV.getText().toString());
								
							} else {
								days = 0;
							}
							stayAccom = Integer.parseInt(f.get("stayAccom"));
							totalStayAmt = sumAmount + (days * stayAccom);
							compensationAmt = days * stayAccom;
							amountTV.setText(totalStayAmt + "");
							
							stayCompensationAmountTV.setText(compensationAmt + "");
							
						}
					} else if (selectedWayId == 1 && selectedTown1Id != 0 && selectedTown2Id != 0) {
						if (selectedStayId == 0) {
							amount = f.get("twoWay");
							amountTV.setText(amount);
							
							travellingAmountTV.setText(amount);
							
							
						} else if (selectedStayId == 1) {
							
							amount = f.get("twoWay");
							sumAmount = Integer.parseInt(amount);
							
							travellingAmountTV.setText(amount);
							
							if (!noOfDaysTV.getText().toString().trim().isEmpty()) {
								days = Integer.parseInt(noOfDaysTV.getText().toString());
								
							} else {
								days = 0;
							}
							stayAccom = Integer.parseInt(f.get("stayAccom"));
							totalStayAmt = sumAmount + (days * stayAccom);
							compensationAmt = days * stayAccom;
							amountTV.setText(totalStayAmt + "");
							
							stayCompensationAmountTV.setText(compensationAmt + "");
							
						}
						
					}
					
					
				}
			});
		}
		
		@Override
		public void onClick(View view) {
			
			
			switch (view.getId()) {
				
				case R.id.fromDateTxt: {
					
					final Calendar c = Calendar.getInstance();
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH); android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme,
							new android.app.DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
									Year = year;
									Month = month;
									Day = dayOfMonth;
									String date = String.format("%02d/%02d/%04d", dayOfMonth, (month + 1), year);
									fromDateTV.setText(date);

									if (!toDateTV.getText().toString().equalsIgnoreCase("To Date")) {
										String toDate = toDateTV.getText().toString();
										String fromDate = fromDateTV.getText().toString();
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

										noOfDaysTV.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + "");
									}

								}
							}, mYear, mMonth, mDay);

					datePickerDialog.show();
					
				}
				break;
				
				case R.id.toDateTxt: {
					
					final Calendar c = Calendar.getInstance();
					int mYear = c.get(Calendar.YEAR);
					int mMonth = c.get(Calendar.MONTH);
					int mDay = c.get(Calendar.DAY_OF_MONTH);
					DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DatePickerDialogTheme,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

									String date = String.format("%02d/%02d/%04d", dayOfMonth, (month + 1), year);
									toDateTV.setText(date);

									if (!fromDateTV.getText().toString().equalsIgnoreCase("From Date")) {
										String toDate = toDateTV.getText().toString();
										String fromDate = fromDateTV.getText().toString();
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

										noOfDaysTV.setText(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + "");
									}

								}
							}, mYear, mMonth, mDay);

					datePickerDialog.show();
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
								
								//callApi();
								// loader.HideLoader();
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
					
					if (selectedTown1Id == 0 || selectedTown2Id == 0 || checkDays()
								|| fromDateTV.getText().toString().equalsIgnoreCase("From Date")
								|| toDateTV.getText().toString().equalsIgnoreCase("To Date")) {
						
						if (selectedTown1Id == 0) {
							Toast.makeText(AppContextProvider.getContext(), "Please Select Town 1", Toast.LENGTH_SHORT).show();
						}
						
						if (selectedTown2Id == 0) {
							Toast.makeText(AppContextProvider.getContext(), "Please Select Town 2", Toast.LENGTH_SHORT).show();
						}
						
						if (checkDays()) {
							Toast.makeText(AppContextProvider.getContext(), "Invalid Dates Selected", Toast.LENGTH_SHORT).show();
						}
						
						if (fromDateTV.getText().toString().equalsIgnoreCase("From Date")) {
							Toast.makeText(AppContextProvider.getContext(), "Please Select From Date ", Toast.LENGTH_SHORT).show();
							
						}
						
						if (toDateTV.getText().toString().equalsIgnoreCase("To Date")) {
							Toast.makeText(AppContextProvider.getContext(), "Please Select To Date ", Toast.LENGTH_SHORT).show();
							
						}
						
					} else {
						
						submitBtn.setClickable(false);
						String cityArea = "";
						if (CityArea == null) {
							cityArea = "N/A";
						} else {
							cityArea = CityArea[1];
						}
						db.OpenDb();
						int maxId = db.getMaxIdFromTravelExpense();
						long idTravelExp = db.createTravelExpense(maxId + 1, Integer.parseInt(String.valueOf(selectedTown1Id)), Integer.parseInt(String.valueOf(selectedTown2Id)), fromDateTV.getText().toString(), toDateTV.getText().toString(), noOfDaysTV.getText().toString(), amountTV.getText().toString(), travellingAmountTV.getText().toString(), stayCompensationAmountTV.getText().toString(), Constant.testInput(remarksEdtTxt.getText().toString()), startTimeTV.getText().toString(), getDateTime(), Latitude, Longitude, /*CityArea[1]*/cityArea, 1);
						if (idTravelExp > 0) {
							Toast.makeText(AppContextProvider.getContext(), "Travel Expense Created", Toast.LENGTH_SHORT).show();
							getActivity().finish();
							getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
						} else {
							//Toast.makeText(getActivity(), "Travel Expense Not Inserted in DB, Something went wrong !", Toast.LENGTH_SHORT).show();
						}
						db.CloseDb();
					}
					
				}
				break;
			}
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_travel_expense,
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
						
						ScrollView travelExpense = rootView.findViewById(R.id.travelExpense);
						travelExpense.setVisibility(View.VISIBLE);
						
						Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_right);
						
						travelExpense.startAnimation(enter);
						
						initUI(rootView);
						
						listeners();
						
					}
					break;
					
					case 2: {
						
						LinearLayout travelExpList = rootView.findViewById(R.id.ShowTravelExpenses);
						travelExpList.setVisibility(View.VISIBLE);
						
						ListView = rootView.findViewById(R.id.listviewRList);
						ListView hList = rootView.findViewById(R.id.Hlist);
						noItemTxt = rootView.findViewById(R.id.NoItemTxt);
						Hlist = new ArrayList<>();
						Hlist.clear();
						
						HashMap<String,String> temp = new HashMap<>();
						temp.put("t_exp_from_town", "TOWN 1");
						temp.put("t_exp_to_town", "TOWN 2");
						temp.put("t_exp_datetime", "DATETIME");
						temp.put("t_exp_amount", "AMOUNT");
						//temp.put(FOURTH_COLUMN, "Contact");
						Hlist.add(temp);
						
						
						TravelExpenseHeaderAdapter adapter = new TravelExpenseHeaderAdapter(c, Hlist);
						hList.setAdapter(adapter);
						
						populateListOfTravelExpense(noItemTxt);
					}
					break;
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.fillInStackTrace());
				
			}
			
			return rootView;
		}
		
		private void PopulateTown1DropDown() {
			
			
			db.OpenDb();
			ArrayList<HashMap<String, String>> townFromDB = db.getTownFromDB();
			db.CloseDb();
			final ArrayList<String> List = new ArrayList<>();
			final ArrayList<Long> ListID = new ArrayList<>();
			
			List.add(0, "Select Town 1");
			ListID.add(0, (long) 0);
			
			if (townFromDB.size() > 0) {
				for (int i = 0; i < townFromDB.size(); i++) {
					HashMap<String, String> f = townFromDB.get(i);
					
					ListID.add(Long.valueOf(f.get("town_id")));
					List.add(f.get("town_name"));
				}
			}
			
			
			// Creating adapter for spinner
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, List);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			town1Drop.setAdapter(dataAdapter);
			
			town1Drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String label = parent.getItemAtPosition(position).toString();

                /*if(position == 0){
					((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black) );
                }*/
					
					
					// Showing selected spinner item
					selectedTown1Id = ListID.get(position);
					Log.d("Town1Id", selectedTown1Id + "");
					
					if (selectedTown1Id != 0) {
						PopulateTown2DropDown();
					}
					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				
				}
			});
			
		}
		
		private void PopulateTown2DropDown() {
			
			db.OpenDb();
			ArrayList<HashMap<String, String>> townFromDB = db.getTown2FromDB(Integer.parseInt(String.valueOf(selectedTown1Id)));
			db.CloseDb();
			final ArrayList<String> List = new ArrayList<>();
			final ArrayList<Long> ListID = new ArrayList<>();
			
			List.add(0, "Select Town 2");
			ListID.add(0, (long) 0);
			
			if (townFromDB.size() > 0) {
				for (int i = 0; i < townFromDB.size(); i++) {
					HashMap<String, String> f = townFromDB.get(i);
					
					ListID.add(Long.valueOf(f.get("town_id")));
					List.add(f.get("town_name"));
				}
			}
			
			// Creating adapter for spinner
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, List);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			town2Drop.setAdapter(dataAdapter);
			
			town2Drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String label = parent.getItemAtPosition(position).toString();

                /*if(position == 0){
					((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black) );
                }*/
					
					
					// Showing selected spinner item
					selectedTown2Id = ListID.get(position);
					Log.d("Town2Id", selectedTown2Id + "");
					if (selectedTown1Id != 0 && selectedTown2Id != 0) {
						PopulateWayDropDown();
						PopulateStayDropDown();
					}
					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				
				}
			});
			
		}
		
		private void PopulateWayDropDown() {
			
			
			db.OpenDb();
			ArrayList<HashMap<String, String>> townTravelFromDB = db.getTownTravelFromDB(String.valueOf(selectedTown1Id), String.valueOf(selectedTown2Id));
			db.CloseDb();
			final ArrayList<String> List = new ArrayList<>();
			final ArrayList<Long> ListID = new ArrayList<>();
			final ArrayList<String> oneWayList = new ArrayList<>();
			final ArrayList<String> twoWayList = new ArrayList<>();
			//final ArrayList<String> stayAccomList = new ArrayList<>();
			HashMap<String, String> f = new HashMap<>();
			List.add(0, "One Way");
			ListID.add(0, (long) 0);
			
			List.add(1, "Two Way");
			ListID.add(1, (long) 1);
			
			if (townTravelFromDB.size() > 0) {
				for (int i = 0; i < townTravelFromDB.size(); i++) {
					f = townTravelFromDB.get(i);
					
					// stayAccomList.add(f.get("stayAccom"));
					oneWayList.add(f.get("oneWay"));
					twoWayList.add(f.get("twoWay"));
					
				}
			}
			
			
			// Creating adapter for spinner
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, List);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			wayDrop.setAdapter(dataAdapter);
			
			final HashMap<String, String> finalF = f;
			wayDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String label = parent.getItemAtPosition(position).toString();

                /*if(position == 0){
					((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black) );
                }*/
					//List.get(position)
					//amountTV.setText(db.getTravelOneWayForDrop((int) selectedTown1Id));
					
					
					// Showing selected spinner item
					selectedWayId = ListID.get(position);
					Log.d("WayId", selectedWayId + "");
					
					if (ListID.get(position) == 0 && selectedTown1Id != 0 && selectedTown2Id != 0) {
						
						if (selectedStayId == 0) {
							amount = finalF.get("oneWay");
							amountTV.setText(amount);
							
							travellingAmountTV.setText(amount);
							
							
						} else if (selectedStayId == 1) {
							
							amount = finalF.get("oneWay");
							sumAmount = Integer.parseInt(amount);
							
							travellingAmountTV.setText(amount);
							
							if (!noOfDaysTV.getText().toString().trim().isEmpty()) {
								days = Integer.parseInt(noOfDaysTV.getText().toString());
								
							} else {
								days = 0;
							}
							stayAccom = Integer.parseInt(finalF.get("stayAccom"));
							totalStayAmt = sumAmount + (days * stayAccom);
							compensationAmt = days * stayAccom;
							amountTV.setText(totalStayAmt + "");
							
							stayCompensationAmountTV.setText(compensationAmt + "");
							
							
						}
					} else if (ListID.get(position) == 1 && selectedTown1Id != 0 && selectedTown2Id != 0) {
						if (selectedStayId == 0) {
							amount = finalF.get("twoWay");
							amountTV.setText(amount);
							travellingAmountTV.setText(amount);
							
							
						} else if (selectedStayId == 1) {
							
							amount = finalF.get("twoWay");
							sumAmount = Integer.parseInt(amount);
							
							travellingAmountTV.setText(amount);
							
							if (!noOfDaysTV.getText().toString().trim().isEmpty()) {
								days = Integer.parseInt(noOfDaysTV.getText().toString());
								
							} else {
								days = 0;
							}
							stayAccom = Integer.parseInt(finalF.get("stayAccom"));
							totalStayAmt = sumAmount + (days * stayAccom);
							compensationAmt = days * stayAccom;
							amountTV.setText(totalStayAmt + "");
							
							stayCompensationAmountTV.setText(compensationAmt + "");
						}
						
					}
					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				
				}
			});
			
		}
		
		private void PopulateStayDropDown() {
			
			
			db.OpenDb();
			ArrayList<HashMap<String, String>> townTravelFromDB = db.getTownTravelFromDB(String.valueOf(selectedTown1Id), String.valueOf(selectedTown2Id));
			db.CloseDb();
			final ArrayList<String> List = new ArrayList<>();
			final ArrayList<Long> ListID = new ArrayList<>();
			final ArrayList<String> stayAccomList = new ArrayList<>();
			final ArrayList<String> oneWayList = new ArrayList<>();
			final ArrayList<String> twoWayList = new ArrayList<>();
			
			HashMap<String, String> f = new HashMap<>();
			List.add(0, "No Stay");
			ListID.add(0, (long) 0);
			
			List.add(1, "Yes Stay");
			ListID.add(1, (long) 1);
			
			if (townTravelFromDB.size() > 0) {
				for (int i = 0; i < townTravelFromDB.size(); i++) {
					f = townTravelFromDB.get(i);
					oneWayList.add(f.get("oneWay"));
					twoWayList.add(f.get("twoWay"));
					stayAccomList.add(f.get("stayAccom"));
					
				}
			}
			
			// Creating adapter for spinner
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, List);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			stayDrop.setAdapter(dataAdapter);
			
			final HashMap<String, String> finalF = f;
			
			stayDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String label = parent.getItemAtPosition(position).toString();

                /*if(position == 0){
                    ((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black) );
                }*/
					
					
					// Showing selected spinner item
					selectedStayId = ListID.get(position);
					Log.d("StayId", selectedStayId + "");
					
					if (ListID.get(position) == 0 && selectedTown1Id != 0 && selectedTown2Id != 0) {
						//amountTV.setText(finalF.get("stayAccom"));
						if (selectedWayId == 0) {
							amount = finalF.get("oneWay");
							amountTV.setText(amount);
							
							travellingAmountTV.setText(amount);
							stayCompensationAmountTV.setText("0");
							
						} else if (selectedWayId == 1) {
							amount = finalF.get("twoWay");
							amountTV.setText(amount);
							
							travellingAmountTV.setText(amount);
							stayCompensationAmountTV.setText("0");
							
						}
					} else if (ListID.get(position) == 1 && selectedTown1Id != 0 && selectedTown2Id != 0) {
						
						if (selectedWayId == 0) {
							amount = finalF.get("oneWay");
							sumAmount = Integer.parseInt(amount);
							
							travellingAmountTV.setText(amount);
							
							if (!noOfDaysTV.getText().toString().trim().isEmpty()) {
								days = Integer.parseInt(noOfDaysTV.getText().toString());
								
							} else {
								days = 0;
							}
							stayAccom = Integer.parseInt(finalF.get("stayAccom"));
							totalStayAmt = sumAmount + (days * stayAccom);
							compensationAmt = days * stayAccom;
							amountTV.setText(totalStayAmt + "");
							
							stayCompensationAmountTV.setText(compensationAmt + "");
							
						} else if (selectedWayId == 1) {
							amount = finalF.get("twoWay");
							Log.wtf("TravelExpenseAmount",amount);
							sumAmount = Integer.parseInt(amount);
							
							travellingAmountTV.setText(amount);
							
							if (!noOfDaysTV.getText().toString().trim().isEmpty()) {
								days = Integer.parseInt(noOfDaysTV.getText().toString());
								
							} else {
								days = 0;
							}
							stayAccom = Integer.parseInt(finalF.get("stayAccom"));
							totalStayAmt = sumAmount + (days * stayAccom);
							compensationAmt = days * stayAccom;
							amountTV.setText(totalStayAmt + "");
							
							stayCompensationAmountTV.setText(compensationAmt + "");
							
						}
						
					}
					
					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				
				}
			});
			
		}
		
		private void initUI(View rootView) {
			
			LinearLayout lay = rootView.findViewById(R.id.travelExpenseFragment);
			
			// load the animation
			Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
			lay.startAnimation(enter);
			
			lay.setVisibility(View.VISIBLE);
			
			locStatusTxt = rootView.findViewById(R.id.locStatusTxt);
			
			manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			startLocation();
			
			startTimeTV = rootView.findViewById(R.id.startTime);
			startTimeTV.setText(getDateTime());
			
			startTimeTV.setVisibility(View.GONE);
			
			town1Drop = rootView.findViewById(R.id.town1Drop);
			town2Drop = rootView.findViewById(R.id.town2Drop);
			wayDrop = rootView.findViewById(R.id.wayDrop);
			stayDrop = rootView.findViewById(R.id.stayDrop);
			
			remarksEdtTxt = rootView.findViewById(R.id.remarksTxt);
			
			remarksEdtTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
			remarksEdtTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
			
			fromDateTV = rootView.findViewById(R.id.fromDateTxt);
			toDateTV = rootView.findViewById(R.id.toDateTxt);
			noOfDaysTV = rootView.findViewById(R.id.daysTxt);
			amountTV = rootView.findViewById(R.id.amountTxt);
			
			travellingAmountTV = rootView.findViewById(R.id.travelAmountTxt);
			stayCompensationAmountTV = rootView.findViewById(R.id.compensationAmountTxt);
			
			submitBtn = rootView.findViewById(R.id.SubmitBtn);
			
			db = PosDB.getInstance(getActivity());
			
			PopulateTown1DropDown();
			PopulateTown2DropDown();
			PopulateWayDropDown();
			PopulateStayDropDown();
		}
		
		private boolean checkDays() {
			boolean valid;
			if (noOfDaysTV.getText().toString().isEmpty()) {
				checkDays = 0;
			} else {
				checkDays = Integer.parseInt(noOfDaysTV.getText().toString());
			}
			valid = checkDays <= 0;
			return valid;
			
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
		
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			
			isOne = true;
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
			Fragment fragment = new TravelExpense.DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(TravelExpense.DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			args.putInt(TravelExpense.DummySectionFragment.pos1, position + 1);
			pos = position;
			fragment.setArguments(args);
			return fragment;
		}
		
		@Override
		public int getCount() {
			// Show 2 total pages (tabs).
			return 2;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				case 0:
					return "Travel Expense".toUpperCase();
				case 1:
					return "Travel Expense List".toUpperCase();
/*
			case 2:
                return "Shop gps".toUpperCase();
*/
			
			}
			return null;
		}
	}
}
