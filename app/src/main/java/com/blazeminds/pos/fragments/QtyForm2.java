package com.blazeminds.pos.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
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

import com.blazeminds.pos.Constant;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.customToast;



public class QtyForm2 extends Fragment {
	
	public final static String TAG = QtyForm2.class.getSimpleName();
Button SalesOrderSubmitBTN,Addbtn,Removebtn;
	private List<AutoCompleteTextView> ItemAutoCompleteList = new ArrayList<AutoCompleteTextView>();
	private List<EditText> NoOfItemsList = new ArrayList<EditText>();
	private List<TextView> NoOfItemsListTV = new ArrayList<TextView>();
	private List<ImageView> MinusIVList = new ArrayList<ImageView>();
	private List<LinearLayout> edittextLayoutList = new ArrayList<LinearLayout>();
AutoCompleteTextView PatientName;
Spinner BrickEdtxt;
	int id=0;
	LinearLayout main;
	TextView TotalEdText;
//	/tcher txtwatcher;
	PosDB db;
	public QtyForm2() {
	}
	
	public static QtyForm2 newInstance() {
		return new QtyForm2();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		db=PosDB.getInstance(getActivity());
		db.OpenDb();
		View rootView = inflater.inflate(R.layout.fragment_place_order2, container, false);
		
		LinearLayout lay = rootView.findViewById(R.id.PatientRegistrationFrag);
		main = rootView.findViewById(R.id.ItemsListLinearLayout);
		main.setOrientation(LinearLayout.VERTICAL);
		SalesOrderSubmitBTN= rootView.findViewById(R.id.SalesOrderSubmitBTN);
		Addbtn= rootView.findViewById(R.id.Addbtn);
		Removebtn= rootView.findViewById(R.id.Removebtn);
		PatientName= rootView.findViewById(R.id.PatientAutoComplete);
		TotalEdText= rootView.findViewById(R.id.TotalEdText);
		BrickEdtxt= rootView.findViewById(R.id.BrickEdtxt);
		FilterWithSpaceAdapter PatientNameAdapter= new FilterWithSpaceAdapter(getActivity(), android.R.layout.simple_list_item_1);

//
		PatientNameAdapter.addAll(db.getCustomerNameList());
		PatientName.setAdapter(PatientNameAdapter);
		PatientNameAdapter.notifyDataSetChanged();
		FilterWithSpaceAdapter BrickAdapt= new FilterWithSpaceAdapter(getActivity(), android.R.layout.simple_list_item_1);
BrickAdapt.add("Please Select Brick");
//
		BrickAdapt.addAll(db.getCustomerRoutesForDropDownAllDay());
		BrickEdtxt.setAdapter(BrickAdapt);
		BrickAdapt.notifyDataSetChanged();
		BrickEdtxt	.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
			if(BrickEdtxt.getSelectedItem().toString().equals("Please Select Brick")){
				FilterWithSpaceAdapter PatientNameAdapter= new FilterWithSpaceAdapter(getActivity(), android.R.layout.simple_list_item_1);

//
				PatientNameAdapter.addAll(db.getCustomerNameList());
				PatientName.setAdapter(PatientNameAdapter);
				PatientNameAdapter.notifyDataSetChanged();

			}
			else{

				FilterWithSpaceAdapter PatientNameAdapter= new FilterWithSpaceAdapter(getActivity(), android.R.layout.simple_list_item_1);

//
				PatientNameAdapter.addAll(db.getCustomerNameListByRoute(db.getCustomerRouteIDByName(BrickEdtxt.getSelectedItem().toString())));
				PatientName.setAdapter(PatientNameAdapter);
				PatientNameAdapter.notifyDataSetChanged();
			}
		}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				// your code here
			}

		});
		SalesOrderSubmitBTN.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
			
				

					if(!db.getCustomerID(PatientName.getText().toString()).equals("") && !db.getCustomerID(PatientName.getText().toString()).equals("0"))
					{
					boolean emptyCheck=false;
					for(int i=0;i<ItemAutoCompleteList.size();i++){
					if(ItemAutoCompleteList.get(i).getText().toString().equals("") ||ItemAutoCompleteList.get(i).getText().toString().isEmpty() ||NoOfItemsList.get(i).getText().toString().equals("") )
					{
						emptyCheck=true;
					}
				}
					if(!emptyCheck && ItemAutoCompleteList.size()>0 ) {
						boolean	allItemValid=true;
//						for(int i=0;i<ItemAutoCompleteList.size();i++){
//							if(db.getInventoryID( ItemAutoCompleteList.get(i).getText().toString()).equals("")){
//								ItemAutoCompleteList.get(i).setError("Enter Valid Item");
//								allItemValid=false;}
//						}

				if(allItemValid){
					int orderId = db.getMaxOrderIdFromPatientOrder2();
						orderId++;
					SharedPreferences myPref = getActivity().getSharedPreferences(
							"TARGET", Context.MODE_PRIVATE);
						db.createPatientOrderEntry2(orderId,db.getMobEmpId(),Integer.parseInt(db.getCustomerID( PatientName.getText().toString())),myPref.getInt("distributor_id",0),db.getCustomerID( PatientName.getText().toString()),1,PosDB.getDateTime());
						db.createPatientOrderDetail2(orderId, ItemAutoCompleteList, NoOfItemsList,NoOfItemsListTV);
						
						Toast.makeText(getActivity(),"Created",Toast.LENGTH_LONG).show();
					getActivity().getSupportFragmentManager().popBackStack();
					getActivity().getSupportFragmentManager()
							.beginTransaction()
							.replace(R.id.content_frame,
									QtyFormFinal2.newInstance(),
									QtyFormFinal2.TAG).addToBackStack(QtyFormFinal2.TAG).commit();
				}
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
			}
			else
				{
//
					customToast(getActivity(),"Please Select a Doctor");
//
				}

			}
		});
		Addbtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				addEditText();
			}
		});
		Removebtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				removeEditText();
			}
		});
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		lay.setVisibility(View.VISIBLE);
		
		
		
		
		addEditText();

		
		
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
					NoOfItemsListTV.get(i).setText(db.getRateofIemById(db.getInventoryID(ItemAutoCompleteList.get(i).getText().toString())));
				}
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
NoOfItems.addTextChangedListener(new TextWatcher() {

	@Override
	public void afterTextChanged(Editable s) {}

	@Override
	public void beforeTextChanged(CharSequence s, int start,
								  int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start,
							  int before, int count) {
int totalBill=0;
for(int i=0; i<NoOfItemsList.size();i++)

{
	if(!NoOfItemsList.get(i).getText().toString().equals("") && !NoOfItemsListTV.get(i).getText().toString().equals("")){
		totalBill=totalBill+Integer.parseInt(NoOfItemsList.get(i).getText().toString())*Integer.parseInt(NoOfItemsListTV.get(i).getText().toString());
	}}
TotalEdText.setText(String.valueOf(totalBill));
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
	NoOfItemsListTV.remove(i);
	MinusIVList.remove(i);
	edittextLayoutList.get(i).removeAllViews();
	edittextLayoutList.remove(i);
}}
//		}
		int totalBill=0;
		for(int i=0; i<NoOfItemsList.size();i++)

		{
			if(!NoOfItemsList.get(i).getText().toString().equals("")){
			totalBill=totalBill+Integer.parseInt(NoOfItemsList.get(i).getText().toString())*Integer.parseInt(NoOfItemsListTV.get(i).getText().toString());
		}}
		TotalEdText.setText(String.valueOf(totalBill));
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
}
