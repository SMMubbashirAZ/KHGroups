package com.blazeminds.pos.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.FilterWithSpaceAdapter;
import com.blazeminds.pos.autocomplete_resource.MyObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Blazeminds on 11/2/2018.
 */

public class ShopStock extends Fragment implements View.OnClickListener {
	
	public final static String TAG = About.class.getSimpleName();
	View space;
	List<EditText> allQty = new ArrayList<>();
	private Context c;
	private AutoCompleteTextView CustomerTxt;
	private FilterWithSpaceAdapter<String> myAdapterCustomer;
	private String[] itemCustomer = new String[]{"Please search..."};
	private String SelectedCustomerId = "0";
	private LinearLayout DynamicLayout;
	private LinearLayout OrderChildLayout;
	private LinearLayout shopStockLayout;
	private int Incre = 0;
	private AutoCompleteTextView pro;
	private FilterWithSpaceAdapter<String> myAdapter2;
	private String[] item2 = new String[]{"Please search..."};
	private String SelectedProductId = "0";
	private EditText qty;
	private Button minusBtn;
	private TextView proVal;
	private List<TextView> ProVal = new ArrayList<>();
	private List<AutoCompleteTextView> allPro = new ArrayList<>();
	private Button Add, Submit;
	private boolean selectCustomerItem;
	private boolean selectProductItem;
	private PosDB db;
	
	public ShopStock() {
	}
	
	public static ShopStock newInstance() {
		return new ShopStock();
	}
	
	private static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		
		DateFormat df = DateFormat.getDateTimeInstance();
		
		//SelectedDate = dateFormat.format(new Date());
		
		return dateFormat.format(new Date());
		//return df.format(new Date());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_shop_stock, container, false);
		
		initUI(rootView);
		
		return rootView;
		
	}
	
	private void initUI(View rootView) {
		
		shopStockLayout = rootView.findViewById(R.id.shopStockLayout);
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		shopStockLayout.startAnimation(enter);
		c = getActivity();
		
		db = PosDB.getInstance(getActivity());
		
		selectCustomerItem = false;
		selectProductItem = false;
		
		CustomerTxt = rootView.findViewById(R.id.SelectCustomer);
		CustomerTxt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		CustomerTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
		
		// set our adapter
		myAdapterCustomer = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemCustomer);
		CustomerTxt.setAdapter(myAdapterCustomer);
		
		OrderChildLayout = rootView.findViewById(R.id.orderChildLayout);
		DynamicLayout = rootView.findViewById(R.id.DynamicLayout);
		
		Submit = rootView.findViewById(R.id.SubmitBtn);
		Add = rootView.findViewById(R.id.AddMoreBtn);
		
		listeners();
		
		createDynamicView();
		
	}
	
	private void listeners() {
		
		Submit.setOnClickListener(this);
		Add.setOnClickListener(this);
		OrderChildLayout.setOnClickListener(this);
		shopStockLayout.setOnClickListener(this);
		
		CustomerTxt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				
				CustomerTxt.setError(null);
				
				// update the adapater
				myAdapterCustomer = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, itemCustomer);
				CustomerTxt.setAdapter(myAdapterCustomer);
				myAdapterCustomer.notifyDataSetChanged();
				
				
				if (s.toString().contains("'") || s.toString().contains("%") || s.toString().contains("&")) {
					Toast.makeText(AppContextProvider.getContext(), " Symbols like \" ' \" \" % \" \" & \"\n not acceptable ", Toast.LENGTH_SHORT).show();
					
				} else {
					
					itemCustomer = getCustomerNameFromDb(Constant.testInput(s.toString()), db);
					
					
					db.OpenDb();
					
					SelectedCustomerId = (db.getCustomerID(Constant.testInput(s.toString())));
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
	
	// this function is used in CustomAutoCompleteTextChangedListener.java
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
	
	private void createDynamicView() {
		
		LinearLayout.LayoutParams paramsTable = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT, 10);
		paramsTable.setMargins(2, 2, 2, 2);
		
		TableRow trProduct = new TableRow(getActivity());
		
		trProduct.setLayoutParams(paramsTable);
		trProduct.setWeightSum(10);
		
		db.OpenDb();
		
		proVal = new TextView(getActivity());
		proVal.setId(Incre);
		ProVal.add(proVal);
		
		pro = new AutoCompleteTextView(getActivity());
		pro.setId(Incre);
		pro.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		
		qty = new EditText(getActivity());
		qty.setId(Incre);
		qty.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		
		minusBtn = new Button(getActivity());
		minusBtn.setId(Incre);
		minusBtn.setBackgroundResource(R.drawable.minus);
		
		pro.setHint("Select Product");
		pro.setTextSize(15);
		pro.setEms(10);
		
		// set our adapter
		myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
		pro.setAdapter(myAdapter2);
		
		
		pro.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void onTextChanged(CharSequence userInput, int start, int before, int count) {
				
				
				myAdapter2 = new FilterWithSpaceAdapter<>(getActivity(), R.layout.layout_custom_spinner, R.id.item, item2);
				pro.setAdapter(myAdapter2);
				myAdapter2.notifyDataSetChanged();
				
				// query the database based on the user input
				item2 = getInventoryNameFromDb(Constant.testInput(userInput.toString()), db);
				
				db.OpenDb();
				
				proVal.setText(db.getInventoryID(Constant.testInput(pro.getText().toString())));
				qty.setText("");
				
				db.CloseDb();
				
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		pro.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				
				selectProductItem = true;
				pro.setEnabled(false);
				pro.setTextColor(Color.BLACK);
			}
		});
		
		pro.setInputType(InputType.TYPE_CLASS_TEXT);
		pro.setBackgroundResource(R.drawable.edittxt_bg);
		pro.setSingleLine(false);
		pro.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
		
		LinearLayout.LayoutParams paramsTableComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 5.50f);
		paramsTableComponets.setMargins(10, 2, 10, 2);
		
		pro.setLayoutParams(paramsTableComponets);
		
		trProduct.addView(pro);
		
		allPro.add(pro);
		
		space = new View(getActivity());
		
		space.setLayoutParams(paramsTableComponets);
		
		qty.setHint("Enter Quantity");
		qty.setEms(8);
		
		qty.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		
		LinearLayout.LayoutParams paramsQtyComponets = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 4.50f);
		
		paramsQtyComponets.setMargins(10, 2, 10, 2);
		
		qty.setLayoutParams(paramsQtyComponets);
		
		LinearLayout.LayoutParams paramsMinusBtnComponets = new TableRow.LayoutParams(50, 50/*, 0.10f*/);
		
		paramsMinusBtnComponets.setMargins(10, 2, 10, 2);
		
		minusBtn.setLayoutParams(paramsMinusBtnComponets);
		
		minusBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				final TableRow parent = (TableRow) v.getParent();
				Log.d("DYNID", String.valueOf(v.getId()));
				
				DeleteValues(v.getId());
				
				DynamicLayout.removeView(parent);
				
			}
		});
		
		trProduct.addView(qty);
		trProduct.addView(minusBtn);
		
		trProduct.setGravity(Gravity.CENTER_HORIZONTAL);
		
		allQty.add(qty);
		
		db.CloseDb();
		
		DynamicLayout.addView(trProduct);
		DynamicLayout.addView(space);
		
		Incre++;
		
	}
	
	private void DeleteValues(int id) {
		
		int p = ProVal.get(id).getId();
		if (ProVal.get(id).getId() == id) {
			ProVal.get(id).setText("0");
		}
		
		if (allQty.get(id).getId() == id) {
			allQty.get(id).setText("0");
		}
		
		
	}
	
	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
			
			case R.id.SubmitBtn: {
				
				submitShopStock();
			}
			break;
			
			case R.id.AddMoreBtn: {
				
				if (ProVal.get(Incre - 1).getText().toString().trim().isEmpty()) {
					Toast.makeText(AppContextProvider.getContext(), "Please fill last item first", Toast.LENGTH_SHORT).show();
				} else {
					createDynamicView();
				}
				
			}
			break;
			
			case R.id.orderChildLayout: {
				
				if (selectCustomerItem) {
					CustomerTxt.setEnabled(true);
				}
				if (selectProductItem) {
					pro.setEnabled(true);
				}
			}
			break;
			
			case R.id.shopStockLayout: {
				
				if (selectCustomerItem) {
					CustomerTxt.setEnabled(true);
				}
				if (selectProductItem) {
					pro.setEnabled(true);
				}
			}
			break;
		}
	}
	
	private boolean CheckProduct() {
		
		boolean chk = false;
		
		for (int i = 0; i < Incre; i++) {
			
			if (ProVal.get(i).getText().toString().isEmpty()) {
				
				final int finalI = i;
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						allPro.get(finalI).setError("Product not found");
					}
				});
				
				chk = false;
				break;
			} else {
				chk = true;
			}
			
		}
		
		return chk;
	}
	
	private boolean CheckQuantity() {
		
		boolean chk = false;
		
		for (int i = 0; i < Incre; i++) {
			
			if (allQty.get(i).getText().toString().isEmpty()) {
				
				final int finalI = i;
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						allQty.get(finalI).setError("Quantity Required");
					}
				});
				
				chk = false;
				break;
			} else {
				chk = true;
			}
			
		}
		
		return chk;
	}
	
	private boolean CheckProductDupplicate() {
		
		boolean chk = false;
		
		for (int i = 0; i < Incre; i++) {
			
			for (int j = 0; j < Incre; j++) {
				
				if (i != j && ProVal.get(i).getText().toString().equals(ProVal.get(j).getText().toString())) {
					
					final int finalI = j;
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							allPro.get(finalI).setError("Product Already Selected");
						}
					});
					
					chk = false;
					break;
				} else {
					chk = true;
				}
			}
			
		}
		
		return chk;
	}
	
	private void submitShopStock() {
		
		if (CheckProduct()) {
			
			if (CheckProductDupplicate()) {
				
				if (CheckQuantity()) {
					
					if (CustomerTxt.getText().toString().trim().isEmpty() || SelectedCustomerId.equalsIgnoreCase("0") || SelectedCustomerId.trim().isEmpty()) {
						
						if (CustomerTxt.getText().toString().trim().isEmpty()) {
							CustomerTxt.setError("Customer Required");
						}
						
						if (SelectedCustomerId.equalsIgnoreCase("0")) {
							CustomerTxt.setError("Customer Invalid");
						}
						
						if (SelectedCustomerId.trim().isEmpty()) {
							CustomerTxt.setError("Customer Invalid");
						}
					} else {
						
						Submit.setClickable(false);
						db.OpenDb();
						String empId = db.getMobEmpId();
						long insIdShopStock = 0;
						for (int i = 0; i < Incre; i++) {
							if (!ProVal.get(i).getText().toString().equalsIgnoreCase("0") && !allQty.get(i).getText().toString().equalsIgnoreCase("0")) {
								int maxIdFromShopStock = db.getMaxIdFromShopStock();
								insIdShopStock = db.createShopStock(maxIdFromShopStock + 1, SelectedCustomerId, ProVal.get(i).getText().toString(), allQty.get(i).getText().toString(), empId, getDateTime(), 1);
							}
						}
						if (insIdShopStock > 0) {
							
							db.updateCustomerLastUpdate(SelectedCustomerId, Constant.getDateTimeSHORT());
							
							Toast.makeText(AppContextProvider.getContext(), "Shop Stock Created", Toast.LENGTH_SHORT).show();
							getActivity().finish();
							getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
						} else {
							//Toast.makeText(getActivity(), "Shop Stock Not Inserted in DB, Something went wrong !", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		}
		
	}
}
