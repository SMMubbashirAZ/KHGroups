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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MyService;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
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


public class QtyForm extends Fragment {
	
	public final static String TAG = QtyForm.class.getSimpleName();
Button SalesOrderSubmitBTN
		//,Addbtn,Removebtn
		;
	private ArrayList<String> arrayList, arrayList1;
	private ArrayList<HashMap<String, String>>  getStoreByZoneList = new ArrayList<>(),
			   getBrandByStoreList = new ArrayList<>();
	EditText Searchedtxt;
	Spinner spinner1,spinner3;
	private ArrayAdapter adapter1;
	private ArrayAdapter<String> adapter;
	private List<AutoCompleteTextView> ItemAutoCompleteList = new ArrayList<AutoCompleteTextView>();
	private List<EditText> NoOfItemsList = new ArrayList<EditText>();
	private List<TextView> NoOfItemsListTV = new ArrayList<TextView>();
	private List<ImageView> MinusIVList = new ArrayList<ImageView>();
	private List<LinearLayout> edittextLayoutList = new ArrayList<LinearLayout>();
	private String  store, brands;
	private JSONObject json;
	Button submit ;
	int id=0;
	LinearLayout main;
	
//	/tcher txtwatcher;
	PosDB db;
	public QtyForm() {
	}
	
	public static QtyForm newInstance() {
		return new QtyForm();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		db=PosDB.getInstance(getActivity());
		db.OpenDb();
		View rootView = inflater.inflate(R.layout.fragment_place_order, container, false);
		
		LinearLayout lay = rootView.findViewById(R.id.PatientRegistrationFrag);
		main = rootView.findViewById(R.id.ItemsListLinearLayout);
		main.setOrientation(LinearLayout.VERTICAL);
		SalesOrderSubmitBTN= rootView.findViewById(R.id.SalesOrderSubmitBTN);
		//Addbtn= rootView.findViewById(R.id.Addbtn);
		//Removebtn= rootView.findViewById(R.id.Removebtn);
	//	PatientName= rootView.findViewById(R.id.PatientAutoComplete);
		ArrayAdapter PatientNameAdapter= new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1);
		PatientNameAdapter.add("Select Brand");
		SharedPreferences myPref = getActivity().getSharedPreferences(
				"TARGET", Context.MODE_PRIVATE);
		String[] brands= myPref.getString("userBrands", "").split(",");
		for(int i=0;i< brands.length;i++)
		{
			if(!brands[i].equals("") && !brands[i].equals(" "))
			{
			PatientNameAdapter.add(	brands[i].trim());
			}
		}
		//PatientNameAdapter.addAll(db.GetBrandAutoCompleteDataqty(""));
		SalesOrderSubmitBTN.setVisibility(View.GONE);
		SalesOrderSubmitBTN.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				final Loader loader = new Loader();
				loader.showDialog(getActivity());
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {



//					if(!PatientName.getSelectedItem().toString().equals("")&&!PatientName.getSelectedItem().toString().isEmpty())
//				{
//					if(!db.getBrandID(PatientName.getSelectedItem().toString()).equals("") && !db.getBrandID(PatientName.getSelectedItem().toString()).equals("0"))
					//{
					boolean emptyCheck=false;
					for(int i=0;i<ItemAutoCompleteList.size();i++){
					if(ItemAutoCompleteList.get(i).getText().toString().equals("") ||ItemAutoCompleteList.get(i).getText().toString().isEmpty() ||NoOfItemsList.get(i).getText().toString().equals("") )
					{
						emptyCheck=true;
					}
				}
					if(!emptyCheck && ItemAutoCompleteList.size()>0 ) {
						boolean	allItemValid=true;
						for(int i=0;i<ItemAutoCompleteList.size();i++){
							if(db.getInventoryID( ItemAutoCompleteList.get(i).getText().toString()).equals("")){
								ItemAutoCompleteList.get(i).setError("Enter Valid Item");
								allItemValid=false;}
						}
				if(allItemValid){
					int orderId = db.getMaxOrderIdFromPatientOrder();
						orderId++;
					SharedPreferences myPref = getActivity().getSharedPreferences(
							"TARGET", Context.MODE_PRIVATE);
						db.createPatientOrderEntry(orderId,db.getMobEmpId(),Integer.parseInt(store),myPref.getInt("distributor_id",0),db.getBrandID( spinner1.getSelectedItem().toString()),1,PosDB.getDateTime());
						db.createPatientOrderDetail(orderId, ItemAutoCompleteList, NoOfItemsList);
						
						Toast.makeText(getActivity(),"Created",Toast.LENGTH_LONG).show();
					getActivity().getSupportFragmentManager().popBackStack();
					getActivity().getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.content_frame,
									QtyFormFinal.newInstance(),
									QtyFormFinal.TAG).addToBackStack(QtyFormFinal.TAG).commit();}
				else{
					Toast.makeText(getActivity(),"Please Enter Valid Item",Toast.LENGTH_LONG).show();
				}
					}
					else if( ItemAutoCompleteList.size()==0)
					{
						Toast.makeText(getActivity(),"Add Item Please",Toast.LENGTH_LONG).show();
					}
					else
					{
						Toast.makeText(getActivity(),"Add positive quantity please.",Toast.LENGTH_LONG).show();
					}
		//	}
//			else
//				{
//
//					customToast(getActivity(),"Please Select a valid Brand name");
//
//				}}
//				else {
//						Toast.makeText(getActivity(),"Add Brand Name",Toast.LENGTH_LONG).show();
//				}
				loader.HideLoader();
					}},500);
			}
		});
//		Addbtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				addEditText();
//			}
//		});
//		Removebtn.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View view) {
//				removeEditText();
//			}
//		});
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		lay.setVisibility(View.VISIBLE);
		
		
		
		



	 submit = rootView.findViewById(R.id.submit);








		spinner1 = rootView.findViewById(R.id.select_store);
		Searchedtxt = rootView.findViewById(R.id.Searchedtxt);
		spinner3 = rootView.findViewById(R.id.select_brand);


		arrayList = new ArrayList<>();
		arrayList1 = new ArrayList<>();



		initiateSpinners();


		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
if(!spinner3.getSelectedItem().toString().equals("Select Brand") && !spinner1.getSelectedItem().toString().equals("Select Customer")) {

	removeAllEditText();

	getProgress();
}
else if(spinner3.getSelectedItem().toString().equals("Select Brand") ){
	customToast(getActivity(),"Please select brand.");
}
else if(spinner1.getSelectedItem().toString().equals("Select Customer")){
                customToast(getActivity(),"Please select Customer.");
			}}
		});
		
		return rootView;
		
	}
	private void addEditText(String InventoryName,String Quantity) {
		LinearLayout editTextLayout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				1.0f
		);
		param.setMargins(14,5,14,5);
		editTextLayout.setWeightSum(1.0f);
		editTextLayout.setOrientation(LinearLayout.HORIZONTAL);
		//  editTextLayout.setLayoutParams(param);
		main.addView(editTextLayout);
		
		
	

	
		param= new LinearLayout.LayoutParams(
				0,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				0.7f
		);	param.setMargins(14,5,14,5);
		final AutoCompleteTextView ItemAutoComplete = new AutoCompleteTextView(getActivity());
		ItemAutoComplete.setId(id);
		ItemAutoComplete.setText(InventoryName);
		ItemAutoComplete.setLayoutParams(param);
		ItemAutoComplete.setEnabled(false);
		//ArrayList<HashMap<String, String>> inventoryList= new ArrayList<HashMap<String, String>>();
	//	inventoryList.addAll(db.getInventoryList());

		ArrayList inventoryNameList= new 	ArrayList ();
		inventoryNameList.addAll(db.getInventorySearchName());
//		for(int i=0 ;i< inventoryList.size();i++)
//		{
//			inventoryNameList.add(inventoryList.get(i).get(FIRST_COLUMN));
//		}

		FilterWithSpaceAdapter ItemAdapter= new FilterWithSpaceAdapter(getActivity(), R.layout.layout_custom_spinner, R.id.item,inventoryNameList);
		ItemAutoComplete.setAdapter(ItemAdapter);
		param= new LinearLayout.LayoutParams(
				0,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				0.1f
		);	param.setMargins(14,5,14,5);
		final TextView NoOfItemstv = new TextView(getActivity());

		NoOfItemstv.setId(id);
		NoOfItemstv.setLayoutParams(param);
		param= new LinearLayout.LayoutParams(
				0,
				110,
				0.2f
		);	param.setMargins(14,5,14,5);
		//if(Integer.parseInt(Quantity)>0) {
		NoOfItemstv.setText(Quantity);
	//	}
	//	else
		//{NoOfItems.setText("0");}



		final EditText NoOfItems = new EditText(getActivity());

		NoOfItems.setId(id);
		NoOfItems.setLayoutParams(param);
		param= new LinearLayout.LayoutParams(
				0,
				110,
				0.1f
		);	param.setMargins(14,5,14,5);
		if(Integer.parseInt(Quantity)>0) {
			NoOfItems.setText(Quantity);
		}
		else
		{NoOfItems.setText("0");}

		editTextLayout.addView(ItemAutoComplete);
		editTextLayout.addView(NoOfItemstv);
		editTextLayout.addView(NoOfItems);

		ItemAutoComplete.setHint("Item Name");

		NoOfItems.setHint("Qty");

		  //ItemAutoComplete.addTextChangedListener( txtwatcher);
		//ItemAutoComplete.setInputType(InputType.TYPE_CLASS_NUMBER);
		NoOfItems.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		editTextLayout.setId(id);
		ItemAutoCompleteList.add(ItemAutoComplete);
		NoOfItemsList.add(NoOfItems);
		NoOfItemsListTV.add(NoOfItemstv);
		edittextLayoutList.add(editTextLayout);

		id++;
		
	}

	private void addEditText() {
		LinearLayout editTextLayout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				1.0f
		);
		param.setMargins(14,5,14,5);
		editTextLayout.setWeightSum(1.0f);
		editTextLayout.setOrientation(LinearLayout.HORIZONTAL);
		//  editTextLayout.setLayoutParams(param);
		main.addView(editTextLayout);





		param= new LinearLayout.LayoutParams(
				0,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				0.7f
		);	param.setMargins(14,5,14,5);
		final AutoCompleteTextView ItemAutoComplete = new AutoCompleteTextView(getActivity());
		ItemAutoComplete.setId(id);
	//	ItemAutoComplete.setText(InventoryName);
		ItemAutoComplete.setLayoutParams(param);
	//	ItemAutoComplete.setEnabled(false);
		//ArrayList<HashMap<String, String>> inventoryList= new ArrayList<HashMap<String, String>>();
		//	inventoryList.addAll(db.getInventoryList());

		ArrayList inventoryNameList= new 	ArrayList ();
		inventoryNameList.addAll(db.getInventorySearchName());

//		for(int i=0 ;i< inventoryList.size();i++)
//		{
//			inventoryNameList.add(inventoryList.get(i).get(FIRST_COLUMN));
//		}

		FilterWithSpaceAdapter ItemAdapter= new FilterWithSpaceAdapter(getActivity(), R.layout.layout_custom_spinner, R.id.item,inventoryNameList);
		ItemAutoComplete.setAdapter(ItemAdapter);
		ItemAutoComplete.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
										  int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start,
									  int before, int count) {
//				if(s.toString().equals(""))
//				{
//					ArrayList inventoryNameList= new 	ArrayList ();
//					inventoryNameList.addAll(db.getInventorySearchName());
//					ArrayAdapter ItemAdapter= new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,inventoryNameList);
//					ItemAutoComplete.setAdapter(ItemAdapter);
//					ItemAdapter.notifyDataSetChanged();
//				}
//				else
//					{
//						ArrayList inventoryNameList= new 	ArrayList ();
//						inventoryNameList.addAll(db.getInventorySearchName());
//						FilterWithSpaceAdapter ItemAdapter= new FilterWithSpaceAdapter(getActivity(), android.R.layout.simple_list_item_1,getInventoryNameFromDb(Constant.testInput(s.toString()), db));
//						ItemAutoComplete.setAdapter(ItemAdapter);
//						ItemAdapter.notifyDataSetChanged();
//
//					}

			}
		});

		ItemAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int pos,
									long id) {




				for(int i=0;i< ItemAutoCompleteList.size();i++)
				{
					NoOfItemsListTV.get(i).setText(db.getQTYofIemById(db.getInventoryID(ItemAutoCompleteList.get(i).getText().toString())));
					if(NoOfItemsList.get(i).getText().toString().equals("") )
					{
						if(Integer.parseInt(db.getQTYofIemById(db.getInventoryID(ItemAutoCompleteList.get(i).getText().toString())))>0) {
						NoOfItemsList.get(i).setText(db.getQTYofIemById(db.getInventoryID(ItemAutoCompleteList.get(i).getText().toString())));
					}
				else{
						NoOfItemsList.get(i).setText("0");

					}}}
			}
		});
		param= new LinearLayout.LayoutParams(
				0,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				0.1f
		);	param.setMargins(14,5,14,5);
		final TextView NoOfItemstv = new TextView(getActivity());

		NoOfItemstv.setId(id);
		NoOfItemstv.setLayoutParams(param);
		param= new LinearLayout.LayoutParams(
				0,
				110,
				0.1f
		);	param.setMargins(14,5,14,5);
		//if(Integer.parseInt(Quantity)>0) {
	//	NoOfItemstv.setText(Quantity);
		//	}
		//	else
		//{NoOfItems.setText("0");}



		final EditText NoOfItems = new EditText(getActivity());

		NoOfItems.setId(id);
		NoOfItems.setLayoutParams(param);
		param= new LinearLayout.LayoutParams(
				0,
				110,
				0.1f
		);	param.setMargins(14,5,14,5);
//		if(Integer.parseInt(Quantity)>0) {
//			NoOfItems.setText(Quantity);
//		}
//		else
//		{NoOfItems.setText("0");}
		ImageView MinusIV=  new ImageView(getActivity());
		MinusIV.setId(id);
		MinusIV.setBackgroundResource(R.drawable.minus);
		MinusIV.setLayoutParams(param);
		MinusIV.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				removeEditTextFromIndex(view.getId());
			}
		});

		editTextLayout.addView(ItemAutoComplete);
		editTextLayout.addView(NoOfItemstv);
		editTextLayout.addView(NoOfItems);
		editTextLayout.addView(MinusIV);

		ItemAutoComplete.setHint("Item Name");

		NoOfItems.setHint("Qty");

		//ItemAutoComplete.addTextChangedListener( txtwatcher);
		//ItemAutoComplete.setInputType(InputType.TYPE_CLASS_NUMBER);
		NoOfItems.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED);
		editTextLayout.setId(id);
		ItemAutoCompleteList.add(ItemAutoComplete);
		NoOfItemsList.add(NoOfItems);
		NoOfItemsListTV.add(NoOfItemstv);
		MinusIVList.add(MinusIV);
		edittextLayoutList.add(editTextLayout);

		id++;

	}
	void removeEditText()
	{
		if(id>0){main.removeViewAt(main.getChildCount()-1);
		id--;
		ItemAutoCompleteList.remove(id);
				NoOfItemsList.remove(id);
			MinusIVList.remove(id);
	}}
	void removeEditTextFromIndex(int index)
	{
		//if(id>0){main.removeViewAt(main.getChildCount()-1);
for(int i=0;i<ItemAutoCompleteList.size();i++) {
	if(ItemAutoCompleteList.get(i).getId()==index){
	ItemAutoCompleteList.remove(i);
	NoOfItemsList.remove(i);
	MinusIVList.remove(i);
	edittextLayoutList.get(i).removeAllViews();
	edittextLayoutList.remove(i);

}}
//		}
}
	void removeAllEditText()
	{
		//if(id>0){main.removeViewAt(main.getChildCount()-1);
		for(int i=ItemAutoCompleteList.size()-1;i>=0;i--) {

				ItemAutoCompleteList.remove(i);
				NoOfItemsList.remove(i);

				edittextLayoutList.get(i).removeAllViews();
				edittextLayoutList.remove(i);
			}
		id=0;
//		}
	}
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

	private void initiateSpinners() {




	//	arrayList1.add("All Stores");




		getStoreByZoneList = db.getStoreByZone();
        arrayList1.add("Select Customer");
		for (int i = 0; i < getStoreByZoneList.size(); i++) {
			arrayList1.add(getStoreByZoneList.get(i).get("name"));
		}



		adapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_list_item_1, arrayList);

		adapter1 =
				new ArrayAdapter<>(getActivity(),
						android.R.layout.simple_list_item_1,  arrayList1);






		spinner1.setAdapter(adapter1);
		// spinner1.setThreshold(1);


		spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				ArrayList<String> brandsList = new ArrayList<>();
				ArrayAdapter<String> brandsAdapter = new ArrayAdapter<>(getActivity(),
						android.R.layout.simple_list_item_1, brandsList);
				if (!parent.getItemAtPosition(position).toString().equals("All Stores")) {

					for (HashMap<String, String> serach : getStoreByZoneList) {
						if (serach.get("name").equals(spinner1.getSelectedItem().toString())) {


							getBrandByStoreList = db.getBrandByStore(serach.get("id"));
							store = serach.get("id");
							break;
						}
					}

					brandsList.clear();
					brandsList.add("Select Brand");
					for (int i = 0; i < getBrandByStoreList.size(); i++) {

						brandsList  .add(db.getBrandName(getBrandByStoreList.get(i).get("brand_id")));

					}



					// spinner2.setText("");
				} else
				{
					getBrandByStoreList = db.getBrandByStore();

					brandsList.clear();
					brandsList.add("Select Brand");

					brandsList.addAll(db.getBrandsName());
					store = "0";





					//   spinner2.setText("");
				}
				brandsAdapter  = new ArrayAdapter<>(getActivity(),
						android.R.layout.simple_list_item_1, brandsList);
				brandsAdapter.notifyDataSetChanged();
				spinner3.setAdapter(brandsAdapter);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

//        spinner1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//               // spinner1.setError(null);
//                if (s.length() == 0) {
//
//                    getBAByStoreList = posDB.getBAByStore();
//
//                    arrayList2.clear();
//                    arrayList2.add("All BA");
//
//
//                    for (int i = 0; i < getBAByStoreList.size(); i++) {
//
//                        arrayList2.add(getBAByStoreList.get(i).get("name"));
//
//                    }
//
//                    adapter2 =  new FilterWithSpaceAdapter<>(fragmentActivity,
//                            R.layout.layout_custom_spinner, R.id.item, arrayList2);;
//                    spinner2.setAdapter(adapter2);
//                    adapter2.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


//        spinner2.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            //    spinner2.setError(null);
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });



		//spinner2.setThreshold(1);


		ArrayList<String> brandsList = new ArrayList<>();

	//	brandsList.add("All Brands");
		brandsList.addAll(db.getBrandsName());
		ArrayAdapter<String> brandsAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_list_item_1, brandsList);

		spinner3.setAdapter(brandsAdapter);

		spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


				if (!parent.getItemAtPosition(position).toString().equals("All Brands")) {
					brands = db.getBrandID(parent.getItemAtPosition(position).toString());


				}
				else {
					brands = "0";
				}






			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}



	private void getProgress() {

		String a;
		boolean aa = false, bb = false;
		a = spinner1.getSelectedItem().toString();


		ArrayList<HashMap<String, String>> list, list1;
		list = db.getStoreByZone();


		if (!a.equals("")) {
			if (a.equals("All Stores")) {
				aa = true;
				store = "0";
			} else {
				for (int i = 0; i < list.size(); i++) {
					if (a.equalsIgnoreCase(list.get(i).get("name"))) {
						store = list.get(i).get("id");
						aa = true;
						break;
					}
				}
			}
		} else {

			aa = true;
			store = "0";
		}



		if (!a.equals("")) {

			if (!aa) {
				//   spinner1.setError("Please Select Valid Store");
				return;
			}
		}



		getProgress(
				db.getMobEmpId(),

				store,
				brands);


	}

	private void getProgress(String emp_id,
							 String store, String brand) {

		final Loader loader = new Loader();
		loader.showDialog(getActivity());
		RestAdapter adapter = new RestAdapter.Builder()
				.setEndpoint(BuildConfig.BASE_URL)
				.build();

		RetrofitWebService api = adapter.create(RetrofitWebService.class);

		api.stock_confirm(emp_id, store, brand, new Callback<Response>() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public void success(Response response, Response response2) {





				BufferedReader reader;

				//An string to store output from the server
				String output=null;


				//Initializing buffered reader
				try {
					reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

					while(output==null)
					{
						output = reader.readLine();
					}
					json = new JSONObject(output);

					JSONArray values_stock=   json.getJSONArray("values_stock");
for(int i=0;i<values_stock.length();i++)
{

	addEditText(values_stock.getJSONObject(i).getString("sku") + " ( " + values_stock.getJSONObject(i).getString("name")+ " )",values_stock.getJSONObject(i).getString("balanceqty"));

}
			//		submit.setVisibility(View.GONE);

					SalesOrderSubmitBTN.setVisibility(View.VISIBLE);
					loader.HideLoader();

				} catch (JSONException e) {
					loader.HideLoader();
					e.printStackTrace();
				} catch (IOException e) {
					loader.HideLoader();
					e.printStackTrace();
				}
				catch (Exception e) {
					loader.HideLoader();
					e.printStackTrace();
				}
			}


			@Override
			public void failure(RetrofitError error ) {
				loader.HideLoader();
				Toast.makeText(getActivity(), "Your internet connection is not working properly.",
						Toast.LENGTH_LONG).show();
				Log.e("GetError", error.getMessage());
			}
		});


	}

}
