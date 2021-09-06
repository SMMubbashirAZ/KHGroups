package com.blazeminds.pos.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MerchandizingListAdapter;
import com.blazeminds.pos.MerchandizingListHeaderAdapter;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.webservice_url.RetrofitWebService;
import com.blazeminds.pos.webservice_url.SyncData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.blazeminds.pos.Constant.MIN_CLICK_INTERVAL;
import static com.blazeminds.pos.Constant.checkCameraPermission;
import static com.blazeminds.pos.Constant.checkLocationPermission;
import static com.blazeminds.pos.Constant.getCityAreaCountryFromLatitudeLongitude;
import static com.blazeminds.pos.MainActivity.granted;
import static com.blazeminds.pos.MainActivity.requestPermission;
import static com.blazeminds.pos.webservice_url.Url_Links.KEY_SUCCESS;

/**
 * Created by Blazeminds on 3/8/2018.
 */

public class MerchandizingFinal extends Fragment {
	
	public final static String TAG = MerchandizingFinal.class.getSimpleName();
	
	static AutoCompleteTextView shopTxt;
	static FilterWithSpaceAdapter<String> myAdapterShop;
	static String[] itemShop = new String[]{"Please search..."};
	//static String[] itemBrand = new String[]{"Please search..."};
	//static String[] itemProduct = new String[]{"Please search..."};
	static ImageView beforeImagePic, takeBeforeImagePic, afterImagePic, takeAfterImagePic;
	static String selectedShopId = "0", selectedProduct1Id = "0", selectedProduct2Id = "0", selectedProduct3Id = "0";
	static boolean selectShopItem;
	static Spinner campaignDrop;
	static long selectedCampaignId;
	static Button submitBtn;
	static TextView locStatusTxt, startTimeTxt, product1TV, product2TV, product3TV;
	static EditText remarksEdtTxt;
	static Uri imageUri;
	static int CAMERA_BEFORE_IMAGE = 1;
	static int CAMERA_AFTER_IMAGE = 2;
	
	static StrictMode.VmPolicy.Builder builder;
	static PosDB db;
	static String[] CityArea;
	static String empId;

	static ArrayList<HashMap<String, String>> list;
	static ArrayList<HashMap<String, String>> data;
	static ArrayList<String> dataCustId;
	static ArrayList<HashMap<String, String>> Hlist;
	static android.widget.ListView lview, hList, ListView;
	static TextView openingBalance, totalAmountTxt, noItemTxt, oldReceivable;
	static Context c;
	static long lastClickTime = 0;
	static int pos = 0;
	private static Loader loader;
	
	
	// New Work Start Here
	private static String merchandizingVal = "";
	/**
	 * The {@link PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link FragmentStatePagerAdapter}.
	 */
	MerchandizingFinal.SectionsPagerAdapter mSectionsPagerAdapter;
	
	// public static final String TAG = CustomerList.class.getSimpleName();
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	
	public static MerchandizingFinal newInstance() {
		return new MerchandizingFinal();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.activity_item_one, container, false);
		mSectionsPagerAdapter = new MerchandizingFinal.SectionsPagerAdapter(
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
		String ImagePathtoUploadBeforeImage = "";
		// Location events (we use GPS only)
		String ImagePathtoUploadAfterImage = "";
		
		private FragmentActivity activity;
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
		
		public static String getDateTime() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			
			DateFormat df = DateFormat.getDateTimeInstance();
			
			//SelectedDate = dateFormat.format(new Date());
			
			return dateFormat.format(new Date());
			//return df.format(new Date());
		}
		
		private static ArrayList<HashMap<String, String>> populateListOfMerchandizing(TextView noItemText) {
			
			data = new ArrayList<>();
			//dataCustId = new ArrayList<String>();
			//dataCustId.clear();
			data.clear();
			
			PosDB db = PosDB.getInstance(c);
			
			db.OpenDb();
			
			///dataCustId = db.getSelectedCustomerID();
			data = db.getMerchandizingList();
			
			db.CloseDb();
			
			if (data.size() > 0) {
				noItemText.setVisibility(View.GONE);
				ListView.setVisibility(View.VISIBLE);
				MerchandizingListAdapter adapter1 = new MerchandizingListAdapter(c, data);
				ListView.setAdapter(adapter1);
				
			} else {
				//Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
				ListView.setVisibility(View.GONE);
				noItemText.setVisibility(View.VISIBLE);
			}
			
			
			return data;
			
		}
		//String ImagePathtoUploadSeating="";
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_merchandizing,
					container, false);
/*
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
                    ARG_SECTION_NUMBER)));
*/
			activity = getActivity();
			//locStatusTV = (TextView) rootView.findViewById(R.id.locStatusTextView);
			int pos2 = Integer.parseInt(Integer.toString(getArguments().getInt(
					pos1)));
			
			
			try {
				switch (pos2) {
					
					case 1: {
						
						ScrollView paymentRecieving = rootView.findViewById(R.id.merchandizingLayout);
						paymentRecieving.setVisibility(View.VISIBLE);
						LinearLayout merchandizingLinearLayout = rootView.findViewById(R.id.merchandizingLinearLayout);
						
						Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
						
						paymentRecieving.startAnimation(enter);
						
						db = PosDB.getInstance(c);
						
						builder = new StrictMode.VmPolicy.Builder();
						StrictMode.setVmPolicy(builder.build());
						
						if (checkCameraPermission(getActivity())) {
						
						} else {
							Toast.makeText(AppContextProvider.getContext(), "Camera Permission Required", Toast.LENGTH_SHORT).show();
						}
						
						initUI(rootView);
						
						c = getActivity();
						
						listeners();
						
						takeBeforeImagePic.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								requestPermission(getActivity());
								if (!granted) {
									return;
								}
								if (checkCameraPermission(getActivity())) {
									TakePicture(1);
									
								} else {
									Toast.makeText(AppContextProvider.getContext(), "Camera Permission Required", Toast.LENGTH_SHORT).show();
								}
								
							}
						});
						
						
						takeAfterImagePic.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								requestPermission(getActivity());
								if (!granted) {
									return;
								}
								if (checkCameraPermission(getActivity())) {
									TakePicture(2);
									
								} else {
									Toast.makeText(AppContextProvider.getContext(), "Camera Permission Required", Toast.LENGTH_SHORT).show();
								}
							}
						});
						submitBtn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								requestPermission(getActivity());
								if (!granted) {
									return;
								}
								if (checkLocationPermission(getActivity())/*c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && c.checkCallingOrSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED*/) {
									
									//new Handler().postDelayed(new Runnable() {
									
									//    @Override
									//    public void run() {
									
									
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
								
								
								if (shopTxt.getText().toString().trim().isEmpty() || selectedShopId.equalsIgnoreCase("0") || selectedShopId.trim().isEmpty()
											|| selectedCampaignId == 0
											|| locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")
											|| ImagePathtoUploadBeforeImage.trim().isEmpty() || ImagePathtoUploadAfterImage.trim().isEmpty()) {
									
									if (shopTxt.getText().toString().trim().isEmpty()) {
										shopTxt.setError("Shop Required");
									}
									
									if (selectedShopId.equalsIgnoreCase("0")) {
										shopTxt.setError("Shop Invalid");
									}
									
									if (selectedShopId.trim().isEmpty()) {
										shopTxt.setError("Shop Invalid");
									}
									
									if (selectedCampaignId == 0) {
										Toast.makeText(AppContextProvider.getContext(), "Please Select Campaign", Toast.LENGTH_SHORT).show();
										
									}
									
									if (locStatusTxt.getText().toString().equalsIgnoreCase("GPS Disabled")) {
										Toast.makeText(AppContextProvider.getContext(), "Enable your GPS first", Toast.LENGTH_SHORT).show();
										
									}
									
									try {
										if (ImagePathtoUploadBeforeImage.trim().isEmpty()) {
											Toast.makeText(AppContextProvider.getContext(), "First Image Required", Toast.LENGTH_SHORT).show();
											
										}
									} catch (NullPointerException e) {
										Toast.makeText(AppContextProvider.getContext(), "First Image Required", Toast.LENGTH_SHORT).show();
										
									}
									
									try {
										if (ImagePathtoUploadAfterImage.trim().isEmpty()) {
											Toast.makeText(AppContextProvider.getContext(), "Second Image Required", Toast.LENGTH_SHORT).show();
											
										}
									} catch (NullPointerException e) {
										Toast.makeText(AppContextProvider.getContext(), "Second Image Required", Toast.LENGTH_SHORT).show();
										
									}
									
									
								} else {
									//com.blazeminds.pos.resources.Loader loader = new com.blazeminds.pos.resources.Loader();
									//syncData.SyncMerchandizing(loader, "","","","","","","","","");
									if (Constant.networkAvailable()) {
										
										String startTime = startTimeTxt.getText().toString();
										String time = getDateTime();
										
										String cityArea;
										if (CityArea == null) {
											cityArea = "N/A";
										} else {
											cityArea = CityArea[1];
										}
										
										merchandizingVal += GetValues2(selectedShopId, String.valueOf(selectedCampaignId), "0",
												empId, String.valueOf(Latitude), String.valueOf(Longitude), /*CityArea[1]*/cityArea,
												remarksEdtTxt.getText().toString(), startTime, getDateTime(), ImagePathtoUploadBeforeImage,
												ImagePathtoUploadAfterImage);
										
										Log.d("MerchandizingVals", merchandizingVal);
										loader.showDialog(getActivity());
										new Sync().execute();
										
									} else {
										Constant.CustomDialogNoInternet(getActivity());
									}
								}
								
							}
						});
						
						merchandizingLinearLayout.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								
								if (selectShopItem) {
									shopTxt.setEnabled(true);
								}
							}
						});
						
					}
					break;
					
					case 2: {
						
						LinearLayout paymentRecievingList = rootView.findViewById(R.id.ShowMerchandizingList);
						paymentRecievingList.setVisibility(View.VISIBLE);
						
						paymentRecievingList.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_right));
						ListView = rootView.findViewById(R.id.listviewRList);
						ListView hList = rootView.findViewById(R.id.Hlist);
						noItemTxt = rootView.findViewById(R.id.NoItemTxt);
						Hlist = new ArrayList<>();
						Hlist.clear();
						
						HashMap<String,String> temp = new HashMap<>();
						temp.put("m_datetime", "DATETIME");
						temp.put("m_shopId", "SHOP NAME");
						temp.put("m_campaignId", "CAMPAIGN");
						//temp.put("m_productId", "PROD");
						
						//temp.put(FOURTH_COLUMN, "Contact");
						Hlist.add(temp);
						
						
						MerchandizingListHeaderAdapter adapter = new MerchandizingListHeaderAdapter(c, Hlist);
						hList.setAdapter(adapter);
						
						populateListOfMerchandizing(noItemTxt);
						
						
					}
					break;
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.fillInStackTrace());
				
			}
			
			return rootView;
		}
		
		private void initUI(View rootView) {
			
			locStatusTxt = rootView.findViewById(R.id.locStatusTxt);
			startTimeTxt = rootView.findViewById(R.id.startTime);
			
			manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			startLocation();
			
			remarksEdtTxt = rootView.findViewById(R.id.NotesTxt);
			
			loader = new Loader();
			shopTxt = rootView.findViewById(R.id.SelectShop);
			
			selectShopItem = false;
			//brandTxt = (AutoCompleteTextView) rootView.findViewById(R.id.SelectBrand);
			product1TV = rootView.findViewById(R.id.product1TV);
			product2TV = rootView.findViewById(R.id.product2TV);
			product3TV = rootView.findViewById(R.id.product3TV);
			
			beforeImagePic = rootView.findViewById(R.id.beforeImagePic);
			takeBeforeImagePic = rootView.findViewById(R.id.takeBeforeImagePic);
			afterImagePic = rootView.findViewById(R.id.afterImagePic);
			takeAfterImagePic = rootView.findViewById(R.id.takeAfterImagePic);
			
			submitBtn = rootView.findViewById(R.id.SubmitBtn);
			
			campaignDrop = rootView.findViewById(R.id.campaignDrop);
			
			DropdownSetupForCampaign(campaignDrop);
			
			shopTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			shopTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
			
			//brandTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
			//brandTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
			
			remarksEdtTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
			remarksEdtTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
			
			// set our adapter
			myAdapterShop = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemShop);
			shopTxt.setAdapter(myAdapterShop);
			
			
			CityArea = new String[3];
			
			db.OpenDb();
			empId = db.getMobEmpId();
			db.CloseDb();
			
			startTimeTxt.setText(getDateTime());
			
			startTimeTxt.setVisibility(View.GONE);
			
		}
		
		private void listeners() {

            /*takeBeforeImagePic.setOnClickListener(this);
			takeAfterImagePic.setOnClickListener(this);
            submitBtn.setOnClickListener( this);*/
			
			shopTxt.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
				
				}
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					
					
					shopTxt.setError(null);
					
					// update the adapater
					myAdapterShop.notifyDataSetChanged();
					myAdapterShop = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemShop);
					shopTxt.setAdapter(myAdapterShop);
					
					
					if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
						Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();
						
					} else {
						
						itemShop = getCustomerNameFromDb(Constant.testInput(s.toString()), db);
						
						
						db.OpenDb();
						
						selectedShopId = (db.getCustomerID(Constant.testInput(s.toString())));
						//SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
						Log.d("ShopID", selectedShopId);
						db.CloseDb();
					}
					
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
				
				
				}
			});
			
			shopTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
					selectShopItem = true;
					shopTxt.setEnabled(false);
					shopTxt.setTextColor(Color.BLACK);
				}
			});


            /*brandTxt.addTextChangedListener(new TextWatcher() {
				@Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {



                    brandTxt.setError(null);

                    // update the adapater
                    myAdapterBrand.notifyDataSetChanged();
                    myAdapterBrand = new ArrayAdapter<String>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemBrand);
                    brandTxt.setAdapter(myAdapterBrand);


                    if( s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&") ){
                        Toast.makeText( getActivity() , " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();

                    }else {

                        itemBrand = getBrandNameFromDb(s.toString(), db);


                        db.OpenDb();

                        selectedBrandId = (db.getBrandID(s.toString()));
                        Log.d("BrandID", selectedBrandId);
                        //SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
                        db.CloseDb();
                    }




                }

                @Override
                public void afterTextChanged(Editable s) {




                }
            });*/

            /*productTxt1.addTextChangedListener(new TextWatcher() {
				@Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {



                    productTxt1.setError(null);

                    // update the adapater
                    myAdapterProduct.notifyDataSetChanged();
                    myAdapterProduct = new ArrayAdapter<String>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemProduct);
                    productTxt1.setAdapter(myAdapterProduct);


                    if( s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&") ){
                        Toast.makeText( getActivity() , " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();

                    }else {

                        itemProduct = getInventoryNameFromDb(s.toString(), db);


                        db.OpenDb();

                        selectedProduct1Id = (db.getInventoryID(s.toString()));
                        //SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
                        Log.d("Product 1", selectedProduct1Id);
                        db.CloseDb();
                    }




                }

                @Override
                public void afterTextChanged(Editable s) {




                }
            });


            productTxt2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {



                    productTxt2.setError(null);

                    // update the adapater
                    myAdapterProduct.notifyDataSetChanged();
                    myAdapterProduct = new ArrayAdapter<String>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemProduct);
                    productTxt2.setAdapter(myAdapterProduct);


                    if( s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&") ){
                        Toast.makeText( getActivity() , " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();

                    }else {

                        itemProduct = getInventoryNameFromDb(s.toString(), db);


                        db.OpenDb();

                        selectedProduct2Id = (db.getInventoryID(s.toString()));
                        //SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
                        Log.d("Product 2", selectedProduct2Id);
                        db.CloseDb();
                    }




                }

                @Override
                public void afterTextChanged(Editable s) {




                }
            });

            productTxt3.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {



                    productTxt3.setError(null);

                    // update the adapater
                    myAdapterProduct.notifyDataSetChanged();
                    myAdapterProduct = new ArrayAdapter<String>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemProduct);
                    productTxt3.setAdapter(myAdapterProduct);


                    if( s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&") ){
                        Toast.makeText( getActivity() , " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();

                    }else {

                        itemProduct = getInventoryNameFromDb(s.toString(), db);


                        db.OpenDb();

                        selectedProduct3Id = (db.getInventoryID(s.toString()));
                        //SelectedCustomerTypeId = db.getCustomerTypeID(s.toString());
                        Log.d("Product 3", selectedProduct3Id);
                        db.CloseDb();
                    }




                }

                @Override
                public void afterTextChanged(Editable s) {




                }
            });
*/
		
		}
		
		public void DropdownSetupForCampaign(Spinner dropDown) {
			
			List<String> ItemsData = new ArrayList<>();
			List<String> ItemsID = new ArrayList<>();
			final List<String> ProductId1 = new ArrayList<>();
			final List<String> ProductId2 = new ArrayList<>();
			final List<String> ProductId3 = new ArrayList<>();
			
			db.OpenDb();
			ArrayList<HashMap<String, String>> merchantPlanFromDB = db.getMerchandizingPlanFromDB();
			
			db.CloseDb();
			
			ItemsData.add(0, "Select Campaign ");
			ItemsID.add(0, "0");
			ProductId1.add(0, "");
			ProductId2.add(0, "");
			ProductId3.add(0, "");
			
			if (merchantPlanFromDB.size() > 0) {
				for (int i = 0; i < merchantPlanFromDB.size(); i++) {
					HashMap<String, String> f = merchantPlanFromDB.get(i);
					
					ItemsID.add(f.get("plan_id"));
					ItemsData.add(f.get("plan_name"));
					ProductId1.add(f.get("plan_prod1"));
					ProductId2.add(f.get("plan_prod2"));
					ProductId3.add(f.get("plan_prod3"));
					
				}
			}
			
			
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
				public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
					
					
					selectedCampaignId = Long.parseLong(finalItemsID.get(position));
                    /*db.OpenDb();
                    db.updateSavedRoute(finalItemsID.get(i));
                    db.CloseDb();*/
					Log.d("sql", "Campaign ID : " + finalItemsID.get(position));
					
					if (position == 0) {
						
						product1TV.setVisibility(View.GONE);
						product2TV.setVisibility(View.GONE);
						product3TV.setVisibility(View.GONE);
						
					}
					
					
					if (!ProductId1.get(position).equals("0") && selectedCampaignId != 0) {
						
						product1TV.setVisibility(View.VISIBLE);
						
						product1TV.setText(db.getSelectedProductName(ProductId1.get(position)));
					}
					
					if (!ProductId2.get(position).equals("0") && selectedCampaignId != 0) {
						
						product2TV.setVisibility(View.VISIBLE);
						
						product2TV.setText(db.getSelectedProductName(ProductId2.get(position)));
					}
					
					if (!ProductId3.get(position).equals("0") && selectedCampaignId != 0) {
						
						product3TV.setVisibility(View.VISIBLE);
						
						product3TV.setText(db.getSelectedProductName(ProductId3.get(position)));
					}
					
					if (ProductId1.get(position).equals("0")) {
						product1TV.setVisibility(View.GONE);
						
					}
					
					if (ProductId2.get(position).equals("0")) {
						product2TV.setVisibility(View.GONE);
						
					}
					if (ProductId3.get(position).equals("0")) {
						product3TV.setVisibility(View.GONE);
						
					}
				}
				
				@Override
				public void onNothingSelected(AdapterView<?> adapterView) {
				
				}
			});
			
			
		}
		
		// this function is used in CustomAutoCompleteTextChangedListener.java
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
		
		// this function is used in CustomAutoCompleteTextChangedListener.java
		public String[] getInventoryNameFromDb(String searchTerm, PosDB db) {
			
			// add items on the array dynamically
			db.OpenDb();
			List<MyObject> products = db.GetInventoryAutoCompleteData(searchTerm);
			
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
		
		// this function is used in CustomAutoCompleteTextChangedListener.java
		public String[] getBrandNameFromDb(String searchTerm, PosDB db) {
			
			// add items on the array dynamically
			db.OpenDb();
			List<MyObject> products = db.GetBrandAutoCompleteData(searchTerm);
			
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
		
		private String GetValues2(String... vals) {
			
			String result = "";
			
			for (String val : vals) {
				
				result = result + val + "@@";

            /*if( i == vals.length - 1 ){
                result = result + vals[i] + "##";
            }*/
				
			}
			return result;
			
		}
		
		public void SyncMerchandizing(final Loader loader, final String empID, final String merchandizingVals) {
			
			
			RestAdapter adapter = new RestAdapter.Builder()
										  .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
										  .build(); //Finally building the adapter
			
			RetrofitWebService api = adapter.create(RetrofitWebService.class);
			
			api.sync_everythingMerchandizing(
					
					empID,
					merchandizingVals,
					
					
					//Creating an anonymous callback
					new Callback<Response>() {
						@Override
						public void success(Response result, Response response) {
							//On success we will read the server's output using bufferedreader
							//Creating a bufferedreader object
							
							BufferedReader reader;
							
							//An string to store output from the server
							String output;
							
							try {
								//Initializing buffered reader
								reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
								
								//Reading the output in the string
								output = reader.readLine();
								
								JSONObject json = new JSONObject(output);
								
								Log.d("Prof", "OUT");
								
								if (json.getString(KEY_SUCCESS) != null) {
									
									String res = json.getString(KEY_SUCCESS);
									
									Log.d("Prof", "res Val " + res);
									
									if (Integer.parseInt(res) == 1) {
										
										
										if (getActivity() != null) {
											
											long currentClickTime = SystemClock.uptimeMillis();
											long elapsedTime = currentClickTime - lastClickTime;
											
											lastClickTime = currentClickTime;
											if (elapsedTime <= MIN_CLICK_INTERVAL) {
												Log.d("SYNC_DATA", "Returned");
												
											} else {
												new SyncData(loader, getActivity(), db);
											}
											
										}
										
										
										//Toast.makeText( getActivity() , "Data Synced", Toast.LENGTH_SHORT).show();
										
										// timeInBtn.setText("Clock Out");
										
										
									} else if (Integer.parseInt(res) == 0) {
										
										activity.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												loader.HideLoader();
												Toast.makeText(AppContextProvider.getContext(), "Res 0.", Toast.LENGTH_SHORT).show();
											}
										});
									}
								}
							} catch (final IOException e) {
								
								
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										loader.HideLoader();
										Toast.makeText(AppContextProvider.getContext(), "I/O Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
									}
								});
								Log.d("Prof", "Excep " + e.toString());
								
								
								e.printStackTrace();
							} catch (final JSONException e) {
								
								activity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										loader.HideLoader();
										
										Log.d("Prof", "Excep JSOn " + e.toString());
										Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
										
										e.printStackTrace();
									}
								});
							}
							
							
						}
						
						@Override
						public void failure(RetrofitError error) {
							
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									loader.HideLoader();
									Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
								}
							});
						}
					}
			);
		}
		
		private void TakePicture(int option) {
			
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			
			File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
			//cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
			
			imageUri = null;
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				builder.detectFileUriExposure();
				imageUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", photo);
			} else {
				imageUri = Uri.fromFile(photo);
			}
			
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			
			switch (option) {
				
				case 1: {
					startActivityForResult(cameraIntent, CAMERA_BEFORE_IMAGE);
				}
				break;
				
				case 2: {
					startActivityForResult(cameraIntent, CAMERA_AFTER_IMAGE);
				}
				break;
				
				
			}
			
			
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			
			if (requestCode == 1111)
				isOne = true;
			
			if (requestCode == CAMERA_BEFORE_IMAGE && resultCode == Activity.RESULT_OK) {
				
				//Uri selectedURI = data.getData();
				
				//Uri selectedImage = imageUri;
				getActivity().getContentResolver().notifyChange(imageUri, null);
				//getContentResolver().notifyChange(selectedURI, null);
				ContentResolver cr = getActivity().getContentResolver();
				Bitmap bitmap;
				try {
					bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, imageUri);
					//bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedURI );
					
					Bitmap convertedImg = getResizedBitmap(bitmap, 100);
					
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					//convertedImg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bao);
					byte[] ba = bao.toByteArray();
//                    ImagePathtoUploadBeforeImage = Base64.encodeToString(ba, Base64.NO_WRAP);
//
//                    Log.d("BASE64_Before_Image", ImagePathtoUploadBeforeImage+"--");
					ImagePathtoUploadBeforeImage = Base64.encodeToString(ba, Base64.NO_WRAP);
					Log.d("BASE64_Before_Image", ImagePathtoUploadBeforeImage + "--");
                    /*ImagePathtoUploadBeforeImage = resizeBase64Image(Base64.encodeToString(ba, Base64.NO_WRAP));
                    Log.d("BASE64_Before_Image_New", ImagePathtoUploadBeforeImage+"--");*/
					
					beforeImagePic.setImageBitmap(convertedImg);
					
					//CameraDialog( getActivity(), imageUri, bitmap, ImagePathtoUpload );
					
					
				} catch (Exception e) {
					Toast.makeText(AppContextProvider.getContext(), "Failed to develop image", Toast.LENGTH_SHORT)
							.show();
					
				} catch (OutOfMemoryError e) {
					Toast.makeText(AppContextProvider.getContext(), "Out of Memory", Toast.LENGTH_SHORT)
							.show();
				}
			} else if (requestCode == CAMERA_AFTER_IMAGE && resultCode == Activity.RESULT_OK) {
				
				//Uri selectedImage = imageUri;
				getActivity().getContentResolver().notifyChange(imageUri, null);
				ContentResolver cr = getActivity().getContentResolver();
				Bitmap bitmap;
				try {
					bitmap = android.provider.MediaStore.Images.Media
									 .getBitmap(cr, imageUri);
					
					Bitmap convertedImg = getResizedBitmap(bitmap, 100);
					
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					//convertedImg.compress(Bitmap.CompressFormat.JPEG, 90, bao);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bao);
					byte[] ba = bao.toByteArray();
					ImagePathtoUploadAfterImage = Base64.encodeToString(ba, Base64.NO_WRAP);
					Log.d("BASE64_After_Image", ImagePathtoUploadAfterImage + "--");
                    /*ImagePathtoUploadAfterImage = resizeBase64Image(Base64.encodeToString(ba, Base64.NO_WRAP));
                    Log.d("BASE64_After_Image_New", ImagePathtoUploadAfterImage+"--");*/
					
					afterImagePic.setImageBitmap(convertedImg);
					
					//CameraDialog( getActivity(), imageUri, bitmap, ImagePathtoUpload );
					
					
				} catch (Exception e) {
					Toast.makeText(AppContextProvider.getContext(), "Failed to develop image", Toast.LENGTH_SHORT)
							.show();
					
				} catch (OutOfMemoryError e) {
					Toast.makeText(AppContextProvider.getContext(), "Out of Memory", Toast.LENGTH_SHORT)
							.show();
				}
			}
			
			
		}
		
		public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
			int width = image.getWidth();
			int height = image.getHeight();
			
			float bitmapRatio = (float) width / (float) height;
			if (bitmapRatio > 1) {
				width = maxSize;
				height = (int) (width / bitmapRatio);
			} else {
				height = maxSize;
				width = (int) (height * bitmapRatio);
			}
			return Bitmap.createScaledBitmap(image, width, height, true);
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
		
		private class Sync extends AsyncTask<String, String, String> {
			
			@Override
			protected String doInBackground(String... strings) {
				
				SyncMerchandizing(loader, empId, merchandizingVal);
				
				return null;
			}
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
			Fragment fragment = new MerchandizingFinal.DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(MerchandizingFinal.DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			args.putInt(MerchandizingFinal.DummySectionFragment.pos1, position + 1);
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
					return "Merchandizing".toUpperCase();
				case 1:
					return "Merchandizing List".toUpperCase();
/*
			case 2:
                return "Shop gps".toUpperCase();
*/
			
			}
			return null;
		}
	}
	
	
}
