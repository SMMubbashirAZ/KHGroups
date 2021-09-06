package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PaymentListAdapter;
import com.blazeminds.pos.PaymentListHeaderAdapter;
import com.blazeminds.pos.PaymentRecievingListViewAdapter;
import com.blazeminds.pos.PaymentRecievingListviewHeaderAdapter;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.InputFilterMinMax;
import com.blazeminds.pos.autocomplete_resource.MyObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;
import static com.blazeminds.pos.MainActivity.granted;
import static com.blazeminds.pos.MainActivity.requestPermission;


/**
 * Created by Blazeminds on 1/9/2018.
 */

public class PaymentRecieving extends Fragment {
	
	public final static String TAG = PaymentRecieving.class.getSimpleName();
	static public FilterWithSpaceAdapter<String> myAdapterCustomer;
	static public String[] itemCustomer = new String[]{"Please search..."};
	static String[] CityArea;
	static ArrayList<HashMap<String, String>> list;
	static ArrayList<HashMap<String, String>> data;
	static HashMap<String, String> map;
	static ArrayList<String> dataCustId;
	static ArrayList<HashMap<String, String>> Hlist;
	static ListView lview, hList, ListView;
	static TextView openingBalance, totalAmountTxt, noItemTxt, oldReceivable, startTime, locStatusTxt, advancePaymentTV;
	static EditText recievingEdtTxt, notesEdtTxt;
	static Button submitBtn;
	static AutoCompleteTextView CustomerTxt;
	static String SelectedCustomerId = "0";
	static boolean selectCustomerItem;
	static Spinner paymentTypeDrop;
	static String selectedPaymentType;
	static LinearLayout chequeDetailsLayout;
	static EditText chequeNoEdtTxt, chequeDateEdtTxt, bankNameEdtTxt;
	static PosDB db;
	
	static Context c;
	
	static BluetoothAdapter mBluetoothAdapter;
	static BluetoothSocket mmSocket;
	static BluetoothDevice mmDevice;
	
	// needed for communication to bluetooth device / network
	static OutputStream mmOutputStream;
	static InputStream mmInputStream;
	static Thread workerThread;
	
	static byte[] readBuffer;
	static int readBufferPosition;
	static volatile boolean stopWorker;
	
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
	SectionsPagerAdapter mSectionsPagerAdapter;
	
	// public static final String TAG = CustomerList.class.getSimpleName();
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	
	public static PaymentRecieving newInstance() {
		return new PaymentRecieving();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_item_one, container, false);
		mSectionsPagerAdapter = new SectionsPagerAdapter(
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
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public static final String pos1 = "";
		// My GPS states
		public static final int GPS_PROVIDER_DISABLED = 1;
		public static final int GPS_GETTING_COORDINATES = 2;
		
		// New Work End Here
		public static final int GPS_GOT_COORDINATES = 3;
		//public static final int GPS_PROVIDER_UNAVIALABLE = 4;
		//public static final int GPS_PROVIDER_OUT_OF_SERVICE = 5;
		public static final int GPS_PAUSE_SCANNING = 6;
		public static final int GPS_PROVIDER_DOESNOT_EXIST = 4;
		private static String coordsToSend;
		private static String gAccuracy;
		private static double Latitude, Longitude;
		private static boolean isOne = true;
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
		
		//Date Picker
		private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
			
			// when dialog box is closed, below method will be called.
			public void onDateSet(DatePicker view, int selectedYear,
								  int selectedMonth, int selectedDay) {
				String year1 = String.valueOf(selectedYear);
				String month1 = String.valueOf(selectedMonth + 1);
				String day1 = String.valueOf(selectedDay);
				
				chequeDateEdtTxt.setText(day1 + "/" + month1 + "/" + year1);
				
			}
		};
		
		public DummySectionFragment() {
		}
		
		private static ArrayList<HashMap<String, String>> populateList() {
			
			
			list = new ArrayList<>();
			
			data = new ArrayList<>();
			
			map = new HashMap<>();
			
			data.clear();
			
			String total, amtRec;
			double total2 = 0, amountRec = 0;
			PosDB db = PosDB.getInstance(c);
			db.OpenDb();
			data = db.getSaleOrderListByOrderId(SelectedCustomerId);
			
			db.CloseDb();
			
			if (data.size() > 0) {
				
				
				for (int i = 0; i < data.size(); i++) {
					map = data.get(i);
					
					total = map.get("amount");
					amtRec = map.get("amountRecieved");
					if (total != null && !total.equals("")) {
						total2 = Double.parseDouble(total);
					}
					if (amtRec != null && !amtRec.equals("")) {
						amountRec = Double.parseDouble(amtRec);
					}
					if ((total2 - amountRec) != 0) {
						list.add(data.get(i));
						
						Log.d("All List:", i + "\n" + data.get(i));
					}
				}
				
				
				PaymentRecievingListViewAdapter adapter = new PaymentRecievingListViewAdapter((Activity) c, list);
				
				lview.setAdapter(adapter);
				
				
			}
			
			
			return list;
			
			
		}
		
		private static ArrayList<HashMap<String, String>> populateListShopWali(TextView noItemText) {
			
			data = new ArrayList<>();
			dataCustId = new ArrayList<>();
			dataCustId.clear();
			data.clear();
			
			PosDB db = PosDB.getInstance(c);
			
			db.OpenDb();
			
			dataCustId = db.getSelectedPaymentRecievedID();
			data = db.getPaymentRecievedForList();
			
			db.CloseDb();
			
			if (data.size() > 0) {
				noItemText.setVisibility(View.GONE);
				ListView.setVisibility(View.VISIBLE);
				PaymentListAdapter adapter1 = new PaymentListAdapter((Activity) c, data);
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
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			
			isOne = true;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_payment_recieving,
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
						
						ScrollView paymentRecieving = rootView.findViewById(R.id.paymentRecievingLayout);
						paymentRecieving.setVisibility(View.VISIBLE);
						
						LinearLayout paymentLayout = rootView.findViewById(R.id.paymentLayout);
						
						Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
						
						paymentRecieving.startAnimation(enter);
						
						db = PosDB.getInstance(c);
						
						CityArea = new String[3];
						
						Hlist = new ArrayList<>();
						list = new ArrayList<>();
						
						locStatusTxt = rootView.findViewById(R.id.locStatusTxt);
						
						manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
						startLocation();
						
						selectCustomerItem = false;
						
						startTime = rootView.findViewById(R.id.startTime);
						openingBalance = rootView.findViewById(R.id.openingBalance);
						oldReceivable = rootView.findViewById(R.id.oldReceivableTV);
						recievingEdtTxt = rootView.findViewById(R.id.recievingEdtTxt);
						advancePaymentTV = rootView.findViewById(R.id.advancePaymentTV);
						totalAmountTxt = rootView.findViewById(R.id.totAmountTxtView);
						notesEdtTxt = rootView.findViewById(R.id.Notes);
						notesEdtTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
						notesEdtTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
						paymentTypeDrop = rootView.findViewById(R.id.paymentTypeDrop);
						chequeDetailsLayout = rootView.findViewById(R.id.chequeDetailLayout);
						chequeNoEdtTxt = rootView.findViewById(R.id.chequeNoEdtTxt);
						chequeDateEdtTxt = rootView.findViewById(R.id.chequeDateEdtTxt);
						bankNameEdtTxt = rootView.findViewById(R.id.bankNameEdtTxt);
						bankNameEdtTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
						bankNameEdtTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
						
						
						submitBtn = rootView.findViewById(R.id.SubmitBtn);
						CustomerTxt = rootView.findViewById(R.id.SelectCustomer);
						CustomerTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
						CustomerTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
						
						myAdapterCustomer = new FilterWithSpaceAdapter<>(c, R.layout.layout_custom_spinner, R.id.item, itemCustomer);
						CustomerTxt.setAdapter(myAdapterCustomer);
						
						startTime.setText(getDateTime());
						
						startTime.setVisibility(View.GONE);
						
						lview = rootView.findViewById(R.id.listview);
						hList = rootView.findViewById(R.id.HeaderList);
						
						Hlist.clear();
						
						HashMap<String,String> temp = new HashMap<>();
						
						temp.put("orderId", "ORDER ID");
						temp.put("amount", "AMOUNT");
						temp.put("recieving", "RECIEVING");
						Hlist.add(temp);
						
						
						PaymentRecievingListviewHeaderAdapter adapter = new PaymentRecievingListviewHeaderAdapter(c, Hlist);
						hList.setAdapter(adapter);
						
						
						lview.setOnTouchListener(new ListView.OnTouchListener() {
							@Override
							public boolean onTouch(View v, MotionEvent event) {
								int action = event.getAction();
								switch (action) {
									case MotionEvent.ACTION_DOWN:
										// Disallow ScrollView to intercept touch events.
										v.getParent().requestDisallowInterceptTouchEvent(true);
										break;
									case MotionEvent.ACTION_UP:
										// Allow ScrollView to intercept touch events.
										v.getParent().requestDisallowInterceptTouchEvent(false);
										break;
								}
								// Handle ListView touch events.
								v.onTouchEvent(event);
								return true;
							}
						});
						
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
								
								//newLogicMethodForPayment();
								
								populateList();
								//Toast.makeText(getActivity(),"Hello",Toast.LENGTH_SHORT).show();
								double total = 0, sum, amtReceived = 0, advPayment = 0;
								int obal;
								db.OpenDb();
								String total2 = db.getSelectedCustomerOpeningBalanceTotal(SelectedCustomerId);
								String amountReceived = db.getSelectedCustomerAmountReceived(SelectedCustomerId);
								String ob = db.getSelectedCustomerOpeningBalance(SelectedCustomerId);
								String advancePayment = db.getSelectedCustomerAdvancePayment(SelectedCustomerId);
								db.CloseDb();
								if (total2 != null && !total2.equals("")) {
									total = Double.parseDouble(total2);
								}
								if (amountReceived != null && !amountReceived.equals("")) {
									amtReceived = Double.parseDouble(amountReceived);
								}
								if (advancePayment != null && !advancePayment.equals("")) {
									advPayment = Double.parseDouble(advancePayment);
								}
								obal = (int) Double.parseDouble(ob);
								sum = obal + (total - amtReceived) - advPayment;
								
								
								//openingBalance.setText(String.format("%.2f", sum));
								oldReceivable.setText(String.format("%.2f", sum));
								advancePaymentTV.setText(String.format("%.2f", advPayment));
								openingBalance.setText(obal + "");
								recievingEdtTxt.setFilters(new InputFilter[]{new InputFilterMinMax("0", openingBalance.getText().toString())});
								
							}
						});
						
						chequeDateEdtTxt.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								
								Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
								
								// Create the DatePickerDialog instance
								DatePickerDialog datePicker = new DatePickerDialog(getContext(),
																						  R.style.Theme_AppCompat, datePickerListener,
																						  cal.get(Calendar.YEAR),
																						  cal.get(Calendar.MONTH),
																						  cal.get(Calendar.DAY_OF_MONTH));
								
								datePicker.setCancelable(false);
								datePicker.setTitle("Select the date");
								datePicker.show();
								
							}
						});
						
						PopulatePaymentTypeDropDown();
						
						submitBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								requestPermission(getActivity());
								if (!granted) {
									return;
								}
								paymentRecievingSubmit();
							}
						});
						
						paymentLayout.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								
								if (selectCustomerItem) {
									CustomerTxt.setEnabled(true);
								}
								
							}
						});
						
					}
					break;
					
					case 2: {
						
						LinearLayout paymentRecievingList = rootView.findViewById(R.id.ShowPayments);
						paymentRecievingList.setVisibility(View.VISIBLE);
						
						Animation animation = AnimationUtils.loadAnimation(getActivity(),
								R.anim.enter_from_right);
						paymentRecievingList.setAnimation(animation);
						ListView = rootView.findViewById(R.id.listviewRList);
						ListView hList = rootView.findViewById(R.id.Hlist);
						noItemTxt = rootView.findViewById(R.id.NoItemTxt);
						
						Hlist.clear();
						
						HashMap<String,String> temp = new HashMap<>();
						temp.put("date", "DATE");
						temp.put("custId", "CUSTOMER");
						temp.put("amount", "AMOUNT");
						//temp.put("notes", "NOTES");
						//temp.put(FOURTH_COLUMN, "Contact");
						Hlist.add(temp);
						
						
						PaymentListHeaderAdapter adapter = new PaymentListHeaderAdapter(c, Hlist);
						hList.setAdapter(adapter);
						
						populateListShopWali(noItemTxt);
						
						ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
								
								String SelectedPaymentId = dataCustId.get(position);
								
								PaymentRecievedDialog(SelectedPaymentId);
							}
						});
						
					}
					break;
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.fillInStackTrace());
				
			}
			
			return rootView;
		}
		
		private void PopulatePaymentTypeDropDown() {
			
			final ArrayList<String> List = new ArrayList<>();
			final ArrayList<String> ListID = new ArrayList<>();
			
			List.add(0, "Cash");
			List.add(1, "Cheque");
			
			ListID.add(0, "1");
			ListID.add(1, "2");
			
			// Creating adapter for spinner
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, List);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			paymentTypeDrop.setAdapter(dataAdapter);
			
			paymentTypeDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String label = parent.getItemAtPosition(position).toString();

//                    if(position == 0){
//                        ((TextView)parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.light_red) );
//                    }
					
					// Showing selected spinner item
					selectedPaymentType = ListID.get(position);
					if (selectedPaymentType.equalsIgnoreCase("1")) {
						
						// Gone Views and set to N/A
						chequeDetailsLayout.setVisibility(View.GONE);
						
						chequeNoEdtTxt.setVisibility(View.GONE);
						chequeNoEdtTxt.setText("N/A");
						
						chequeDateEdtTxt.setVisibility(View.GONE);
						chequeDateEdtTxt.setText("N/A");
						
						bankNameEdtTxt.setVisibility(View.GONE);
						bankNameEdtTxt.setText("N/A");
						
					} else {
						// Visible views and set to ""
						chequeDetailsLayout.setVisibility(View.VISIBLE);
						
						chequeNoEdtTxt.setVisibility(View.VISIBLE);
						chequeNoEdtTxt.setText("");
						
						chequeDateEdtTxt.setVisibility(View.VISIBLE);
						chequeDateEdtTxt.setText("");
						
						bankNameEdtTxt.setVisibility(View.VISIBLE);
						bankNameEdtTxt.setText("");
					}
					
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				
				}
			});
			
		}
		
		private String[] getCustomerNameFromDb(CharSequence searchTerm, PosDB db) {
			
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
		
		private void paymentRecievingSubmit() {
			
			if (checkLocationPermission(getActivity())/*c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED*/) {
				
				//new Handler().postDelayed(new Runnable() {
				
				//@Override
				// public void run() {
				
				
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
				
				// }
				// }, 1000);
				
			} else {
				// loader.HideLoader();
				Toast.makeText(AppContextProvider.getContext(), "Location Permission required", Toast.LENGTH_SHORT).show();
			}
			
			if (CustomerTxt.getText().toString().trim().isEmpty() ||
						SelectedCustomerId.equalsIgnoreCase("0") ||
						SelectedCustomerId.trim().isEmpty() ||
//                    notesEdtTxt.getText().toString().trim().isEmpty() ||
						recievingEdtTxt.getText().toString().trim().isEmpty() ||
						locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled") ||
//                    selectedPaymentType.equalsIgnoreCase("0") ||
						chequeNoEdtTxt.getText().toString().trim().isEmpty() ||
						chequeDateEdtTxt.getText().toString().trim().isEmpty() ||
						bankNameEdtTxt.getText().toString().trim().isEmpty()) {
				
				if (CustomerTxt.getText().toString().trim().isEmpty()) {
					CustomerTxt.setError("Customer Required");
				}
				
				if (SelectedCustomerId.equalsIgnoreCase("0")) {
					CustomerTxt.setError("Customer Invalid");
				}
				
				if (SelectedCustomerId.trim().isEmpty()) {
					CustomerTxt.setError("Customer Invalid");
					
				}

//                if (notesEdtTxt.getText().toString().trim().isEmpty()) {
//                    notesEdtTxt.setError("Notes Required");
//                }
				
				if (recievingEdtTxt.getText().toString().trim().isEmpty()) {
					recievingEdtTxt.setError("Recieving Amount Required");
				}
				
				if (locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")) {
					Toast.makeText(AppContextProvider.getContext(), "Enable your GPS first", Toast.LENGTH_SHORT).show();
					
				}

//                if (selectedPaymentType.equalsIgnoreCase("0")){
//                    Toast.makeText(getActivity(), "Please Select Payment Type", Toast.LENGTH_SHORT).show();
//                }
				
				if (chequeNoEdtTxt.getText().toString().trim().isEmpty()) {
					chequeNoEdtTxt.setError("Cheque No Required");
				}
				
				if (chequeDateEdtTxt.getText().toString().trim().isEmpty()) {
					chequeDateEdtTxt.setError("Cheque Date Required");
				}
				
				if (bankNameEdtTxt.getText().toString().trim().isEmpty()) {
					bankNameEdtTxt.setError("Bank Name Required");
				}
				
			} else {
				AreYouSure();
			}
			
		}
		
		public void AreYouSure() {
			
			final Dialog dialog = new Dialog(c);
			
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.popup_are_you_sure);
			//dialog.getWindow().setBac(getResources().getColor(R.color.login_bg));
			// dialog.getWindow().
			
			
			//dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			
			
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			int screenWidth = (int) (metrics.widthPixels * 0.80);
			dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
			
			
			//  dialog.setTitle(ItemName);
			final Button Yes = dialog.findViewById(R.id.YesBtn);
			Button No = dialog.findViewById(R.id.NoBtn);
			
			dialog.show();
			
			Yes.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					Yes.setClickable(false);
					//String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
					final ArrayList<HashMap<String, String>> finalData = data;
					String recieving, orderId, amount, recievingEditAmount, amountRecieved, cityArea;
					double recievingAmount, totalAmountOfRecievings = 0, openingBalanc, recievingAmountSubFromOB, subtractOpeningBalance;
					double AmountRecieved = 0, RecievingAmount = 0, SubtractedAmount;
					db.OpenDb();
					int paymentId = db.getMaxIdFromPaymentRecieved();
					String savedDistributor = db.getSavedDistributorList();
					for (int i = 0; i < finalData.size(); i++) {
						
						HashMap map = finalData.get(i);
						recieving = map.get("recieving").toString();
						if (!recieving.equals("")) {
							recievingAmount = Double.parseDouble(recieving);
						} else {
							recievingAmount = 0;
						}
						totalAmountOfRecievings += recievingAmount;
						
					}
					totalAmountOfRecievings += Double.parseDouble(recievingEdtTxt.getText().toString().trim());
					
					if (CityArea[1] == null) {
						cityArea = "N/A";
					} else {
						cityArea = CityArea[1];
					}
					db.createPaymentRecievedLocal(String.valueOf(paymentId + 1), SelectedCustomerId, db.getMobEmpId(), totalAmountOfRecievings + "", startTime.getText().toString(), getDateTime(), Constant.testInput(notesEdtTxt.getText().toString()), Latitude, Longitude, cityArea, chequeNoEdtTxt.getText().toString(), chequeDateEdtTxt.getText().toString(), bankNameEdtTxt.getText().toString(), 0, selectedPaymentType, savedDistributor, 1);

//                    openingBalanc = Double.parseDouble(openingBalance.getText().toString());
//                    recievingAmountSubFromOB = Double.parseDouble(recievingEdtTxt.getText().toString().trim());
//                    subtractOpeningBalance = openingBalanc - recievingAmountSubFromOB;
//                    db.updateOpeningBalanceCustomer(SelectedCustomerId, String.valueOf(subtractOpeningBalance));
					
					db.updateCustomerLastUpdate(SelectedCustomerId, Constant.getDateTimeSHORT());

//                    for (int i = 0; i < finalData.size(); i++) {
//
//                        HashMap map = finalData.get(i);
//                        orderId = map.get("orderId").toString();
//                        amount = map.get("total").toString();
//                        recievingEditAmount = map.get("recieving").toString();
//                        amountRecieved = map.get("amountRecieved").toString();
//                        if (!amountRecieved.equals(""))
//                            AmountRecieved = Double.parseDouble(amountRecieved);
//                        if (!recievingEditAmount.equals("")) {
//                            RecievingAmount = AmountRecieved + Double.parseDouble(recievingEditAmount);
//
//                            //SubtractedAmount = Amount - RecievingAmount;
//
//                            db.updateAmountRecievedSaleOrder(orderId, String.valueOf(RecievingAmount));
//                        }
//                    /*SubtractedAmount = Amount - RecievingAmount;
//                    db.updateTotal2Sales(orderId,String.valueOf(SubtractedAmount));*/
//
//                    }
					
					db.CloseDb();
					
					//newLogicMethodForPayment();
					
					dialog.dismiss();
					
					getActivity().finish();
					getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
					
					
				}
			});
			
			No.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
			
		}
		
		private void newLogicMethodForPayment() {
			
			double PA = 0, BALANCE = 0, PENDING, AMOUNT_REC, TOTAL2 = 0;
			String id, balance, total2;
			ArrayList<HashMap<String, String>> salesDetailsFromDB;
			HashMap<String, String> map;
			//String pa = TotalAmountString;
			db.OpenDb();
			String advancePayment = db.getSelectedCustomerAdvancePayment(SelectedCustomerId);
			if (!advancePayment.equalsIgnoreCase("") && advancePayment != null)
				PA = /*Double.parseDouble(pa) +*/ Double.parseDouble(advancePayment);
			
			if (PA > 0) {
				
				String ob = db.getSelectedCustomerOpeningBalanceForSaleReturn(SelectedCustomerId);
				if (!ob.equalsIgnoreCase("") && ob != null)
					BALANCE = Double.parseDouble(ob);
				PENDING = BALANCE - PA;
				
				if (PENDING <= 0) {
					PENDING = 0;
				}
				PA = PA - BALANCE;
				if (PA <= 0) {
					PA = 0;
				}
				
				db.updateOpeningBalanceCustomer(SelectedCustomerId, String.valueOf(PENDING));
				
				salesDetailsFromDB = db.getSalesDetailsForSaleReturn(SelectedCustomerId);
				if (salesDetailsFromDB.size() > 0) {
					
					for (int i = 0; i < salesDetailsFromDB.size(); i++) {
						
						map = salesDetailsFromDB.get(i);
						id = map.get("id");
						balance = map.get("pending_amount");
						total2 = map.get("total2");
						if (!balance.equalsIgnoreCase("") && balance != null)
							BALANCE = Double.parseDouble(balance);
						if (!total2.equalsIgnoreCase("") && total2 != null)
							TOTAL2 = Double.parseDouble(total2);
						
						if (BALANCE != 0) {
							PENDING = BALANCE - PA;
							if (PENDING <= 0) {
								PENDING = 0;
							}
							PA = PA - BALANCE;
							if (PA <= 0) {
								PA = 0;
							}
							AMOUNT_REC = TOTAL2 - PENDING;
							db.updateAmountRecievedSaleOrder(id, String.valueOf(AMOUNT_REC));
						}
					}
				}
				
				if (PA >= 0) {
					db.updateAdvancePaymentCustomer(SelectedCustomerId, String.valueOf(PA));
				}
			}
			
			db.CloseDb();
			
		}
		
		public void PaymentRecievedDialog(final String paymentId) {
			
			final Dialog dialog;
			dialog = new Dialog(c);
			
			dialog.setContentView(R.layout.popup_payment_recieved);
			dialog.setTitle("Payment Recieved Details");
			
			final TextView dateTV = dialog.findViewById(R.id.dateTV);
			final TextView customerNameTV = dialog.findViewById(R.id.customerNameTV);
			final TextView amountTV = dialog.findViewById(R.id.amountTV);
			final TextView remarksTV = dialog.findViewById(R.id.remarksTV);
			final TextView paymentTypeTV = dialog.findViewById(R.id.paymentTypeTV);
			final TextView chequeNoTV = dialog.findViewById(R.id.chequeNoTV);
			final TextView chequeDateTV = dialog.findViewById(R.id.chequeDateTV);
			final TextView bankNameTV = dialog.findViewById(R.id.bankNameTV);
			TableRow chequeNoRow = dialog.findViewById(R.id.chequeNoRow);
			TableRow chequeDateRow = dialog.findViewById(R.id.chequeDateRow);
			TableRow bankNameRow = dialog.findViewById(R.id.bankNameRow);
			
			Button printBtn = dialog.findViewById(R.id.PrintBtn);
			Button CloseBtn = dialog.findViewById(R.id.CloseDialog);
			
			PosDB db = PosDB.getInstance(c);
			String paymentType;
			db.OpenDb();
			
			dateTV.setText(convertDate(db.getSelectedDatetimeFromPaymentRec(paymentId)));
			customerNameTV.setText(db.getSelectedCustomerName(db.getSelectedCustomeIdFromPaymentRec(paymentId)));
			amountTV.setText(db.getSelectedAmountFromPaymentRec(paymentId));
			remarksTV.setText(db.getSelectedDetailFromPaymentRec(paymentId));
			paymentType = db.getSelectedPaymentTypeFromPaymentRec(paymentId);
			if (paymentType.equalsIgnoreCase("1")) {
				chequeNoRow.setVisibility(View.GONE);
				chequeDateRow.setVisibility(View.GONE);
				bankNameRow.setVisibility(View.GONE);
				paymentTypeTV.setText("Cash");
				
			} else if (paymentType.equalsIgnoreCase("2")) {
				chequeNoRow.setVisibility(View.VISIBLE);
				chequeDateRow.setVisibility(View.VISIBLE);
				bankNameRow.setVisibility(View.VISIBLE);
				paymentTypeTV.setText("Cheque");
				chequeNoTV.setText(db.getSelectedChequeNoFromPaymentRec(paymentId));
				chequeDateTV.setText(db.getSelectedChequeDateFromPaymentRec(paymentId));
				bankNameTV.setText(db.getSelectedBankNameFromPaymentRec(paymentId));
			}
			db.CloseDb();
			
			printBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					
					try {
						findBT();
						openBT();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						sendData(paymentId);
						
						//db.OpenDb();
						
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						closeBT();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
				}
			});
			
			CloseBtn.setOnTouchListener(new View.OnTouchListener() {
				
				@Override
				public boolean onTouch(View view, MotionEvent event) {
					// TODO Auto-generated method stub
					
					int action = event.getAction() & MotionEvent.ACTION_MASK;
					
					try {
						if (action == MotionEvent.ACTION_DOWN) {
							
							dialog.dismiss();
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e.fillInStackTrace());
						
					}
					
					return false;
				}
				
			});
			
			
			dialog.show();
			
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getActivity().getWindowManager()
					.getDefaultDisplay()
					.getMetrics(displaymetrics);
			int widht = displaymetrics.widthPixels;
			int height = displaymetrics.heightPixels;
			dialog.getWindow().setLayout(widht, (int) (height * 0.8f));
			// Creating Dynamic
//            Rect displayRectangle = new Rect();
//
//            Window window = getActivity().getWindow();
//            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//            dialog.getWindow().setLayout((int) (displayRectangle.width() *
//                    0.8f), /*dialog.getWindow().getAttributes().height*/(int) (displayRectangle.width() *
//                    0.8f));
		
		}
		
		public String convertDate(String dbDate) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			//DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
			DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
			Date dt = null;
			try {
				dt = df.parse(dbDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			return dfProper.format(dt);
		}
		
		void findBT() {
			
			try {
				mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				
				if (mBluetoothAdapter == null) {
					//myLabel.setText("No bluetooth adapter available");
					Toast.makeText(AppContextProvider.getContext(), "No Bluetooth Adapter Available", Toast.LENGTH_SHORT).show();
				}
				
				if (!mBluetoothAdapter.isEnabled()) {
					Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					getActivity().startActivityForResult(enableBluetooth, 0);
				}
				
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
				
				if (pairedDevices.size() > 0) {
					for (BluetoothDevice device : pairedDevices) {
						
						// RPP300 is the name of the bluetooth printer device
						// we got this name from the editOrderList of paired devices
						if (device.getName().equals("MTP-II")) {
							mmDevice = device;
							//myLabel.setText("Bluetooth device found.");
							Toast.makeText(AppContextProvider.getContext(), "Bluetooth Device Found.", Toast.LENGTH_SHORT).show();
							break;
						}
					}
				} else {
					Toast.makeText(AppContextProvider.getContext(), "Bluetooth Device Not Found.", Toast.LENGTH_SHORT).show();
					//myLabel.setText("Bluetooth device NOT found.");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// tries to open a connection to the bluetooth printer device
		void openBT() {
			try {
				
				// Standard SerialPortService ID
				UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
				mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
				mmSocket.connect();
				mmOutputStream = mmSocket.getOutputStream();
				mmInputStream = mmSocket.getInputStream();
				
				beginListenForData();
				Toast.makeText(AppContextProvider.getContext(), "Bluetooth Opened.", Toast.LENGTH_SHORT).show();
				//myLabel.setText("Bluetooth Opened");
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * after opening a connection to bluetooth printer device,
		 * we have to listen and check if a data were sent to be printed.
		 */
		void beginListenForData() {
			try {
				final Handler handler = new Handler();
				
				// this is the ASCII code for a newline character
				final byte delimiter = 10;
				
				stopWorker = false;
				readBufferPosition = 0;
				readBuffer = new byte[1024];
				
				workerThread = new Thread(new Runnable() {
					public void run() {
						
						while (!Thread.currentThread().isInterrupted() && !stopWorker) {
							
							try {
								
								int bytesAvailable = mmInputStream.available();
								
								if (bytesAvailable > 0) {
									
									byte[] packetBytes = new byte[bytesAvailable];
									mmInputStream.read(packetBytes);
									
									for (int i = 0; i < bytesAvailable; i++) {
										
										byte b = packetBytes[i];
										if (b == delimiter) {
											
											byte[] encodedBytes = new byte[readBufferPosition];
											System.arraycopy(
													readBuffer, 0,
													encodedBytes, 0,
													encodedBytes.length
											);
											
											// specify US-ASCII encoding
											final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
											readBufferPosition = 0;
											
											// tell the user data were sent to bluetooth printer device
											handler.post(new Runnable() {
												public void run() {
													//myLabel.setText(data);
													Toast.makeText(AppContextProvider.getContext(), "" + data, Toast.LENGTH_SHORT).show();
												}
											});
											
										} else {
											readBuffer[readBufferPosition++] = b;
										}
									}
								}
								
							} catch (IOException ex) {
								stopWorker = true;
							}
							
						}
					}
				});
				
				workerThread.start();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// this will send text data to be printed by the bluetooth printer
		void sendData(String paymentId) {
			try {
				
				// the text typed by the user
				db.OpenDb();
				
				
				//String msg = myTextbox.getText().toString();
				String msg = "        " + " " + db.getMobCompany().toUpperCase() + " " + "     ";
				msg += "\n";
				// msg += "Item"+"\t"+"Qty"+"\t"+"SubTotal";
				msg += "        " + " Payment Receipt # " + paymentId + "        " + "        ";
				msg += "\n";
				String dbDate = db.getSelectedDatetimeFromPaymentRec(paymentId);
				//convertDate(dbDate);
				msg += "Date : " + convertDate(dbDate);
				msg += "\n";
				msg += "Customer : " + db.getSelectedCustomerName(db.getSelectedCustomeIdFromPaymentRec(paymentId));
				msg += "\n";
				msg += "Salesman : " + db.getMobFName();
				msg += "\n";
				msg += "Remarks : " + db.getSelectedDetailFromPaymentRec(paymentId);
				msg += "\n";
				
				msg += "\n";
				msg += "********************************";
				String total = db.getSelectedAmountFromPaymentRec(paymentId) + "       ";
				msg += "Amount Received : " + total.substring(0, 7);
				msg += "\n";
				msg += "********************************";
				msg += "\n\n";
				msg += "Printed By : " + db.getMobFName();
				msg += "\n";
				// msg += "Print Time: "+convertDate2(getDateTime());
				msg += "\n";
				msg += "Software Powered By";
				msg += "\n";
				msg += "Blaze Minds Solutions".toUpperCase();
				msg += "\n";
				msg += "Website : " + "www.blazeminds.com";
				
				msg += "\n";
				msg += "Contact : +92 321 3880558";
				//db.CloseDb();
				msg += "\n";
				msg += "\n";
				msg += "\n";
				
				
				Log.d("MSG Print --> ", msg);
				// Html.fromHtml(String.valueOf(msg.getBytes()))
				mmOutputStream.write(msg.getBytes());
				//db.updateSalesOrderPrintExecute(Integer.parseInt(orderId));
				// tell the user data were sent
				// myLabel.setText("Data sent.");
				//Toast.makeText(activity,"Data Sent.",Toast.LENGTH_SHORT).show();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// close the connection to bluetooth printer.
		void closeBT() {
			try {
				stopWorker = true;
				mmOutputStream.close();
				mmInputStream.close();
				mmSocket.close();
				// myLabel.setText("Bluetooth Closed");
				Toast.makeText(AppContextProvider.getContext(), "Bluetooth Closed.", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
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
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			args.putInt(DummySectionFragment.pos1, position + 1);
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
					return "Payment Collection".toUpperCase();
				case 1:
					return "Payment Collection List".toUpperCase();
/*
			case 2:
                return "Shop gps".toUpperCase();
*/
			
			}
			return null;
		}
	}
}
