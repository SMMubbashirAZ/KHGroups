package com.blazeminds.pos.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.R;
import com.blazeminds.pos.adapter.CustomerLast5OrdersAdapter;
import com.blazeminds.pos.adapter.CustomerLastOrderAdapter;
import com.blazeminds.pos.adapter.CustomerLastVisitDetailsAdapter;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.webservice_url.RetrofitWebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CustomerLastDetails extends DialogFragment {
	
	public final static String TAG = About.class.getSimpleName();
	TextView ShopNametxtview,ContactTV,PersonTxtView,LabelCustDetailTV,LastOrderLabel;
	ListView last5Visits,last5Orders,lastOrder;
	Button CloseBtn;
	LinearLayout CustDetailsMainLL;
static 	String emp_id,Cust_id;
	public CustomerLastDetails() {
	}
	
	public static CustomerLastDetails newInstance(String Emp_ID,String Customer_id) {
		emp_id=Emp_ID;
				Cust_id	=Customer_id;
		return new CustomerLastDetails();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		
		// TODO Auto-generated method stub
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		View rootView = inflater.inflate(R.layout.fragment_customer_last_details, container, false);




		LinearLayout lay = rootView.findViewById(R.id.AboutFrag);
		 CustDetailsMainLL= rootView.findViewById(R.id.CustDetailsMainLL);
		LabelCustDetailTV= rootView.findViewById(R.id.LabelCustDetailTV);
		LastOrderLabel= rootView.findViewById(R.id.LastOrderLabel);
		CustDetailsMainLL.setVisibility(View.GONE);
		LabelCustDetailTV.setText("LOADING...");
		ShopNametxtview = rootView.findViewById(R.id.ShopNametxtview);
		ContactTV = rootView.findViewById(R.id.ContactTV);
		PersonTxtView = rootView.findViewById(R.id.PersonTxtView);
		last5Visits = rootView.findViewById(R.id.last5Visits);
		last5Orders = rootView.findViewById(R.id.last5Orders);
		lastOrder = rootView.findViewById(R.id.lastOrder);
		CloseBtn = rootView.findViewById(R.id.CloseBtn);
		
		
		
		CloseBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			dismiss();
			}
		});
		
		
		RestAdapter adapter = new RestAdapter.Builder()
									  .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
									  .build(); //Finally building the adapter
		
		RetrofitWebService api = adapter.create(RetrofitWebService.class);
		
		api.customer_last_details(
				
				emp_id,
				Cust_id,
		
				new Callback<Response>() {
					@Override
					public void success(Response response, Response response2) {
						BufferedReader reader = null;
						
						//An string to store output from the server
						String output = "";


						try {
							//Initializing buffered reader
							reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
							
							//Reading the output in the string
							output = reader.readLine();
							
								JSONObject json = new JSONObject(output);


							ShopNametxtview.setText(json.getString("name"));
							ContactTV.setText(json.getString("contact"));
							PersonTxtView.setText(json.getString("person"));

							CustomerLastVisitDetailsAdapter LastVisitAdapter= new CustomerLastVisitDetailsAdapter(getActivity(),json.getJSONArray("values_last_five_visit"));
							LastVisitAdapter.notifyDataSetChanged();
						last5Visits.setAdapter(LastVisitAdapter);
							ResizeList(last5Visits);
							CustomerLast5OrdersAdapter CustomerLast5OrdersAdapterOB= new CustomerLast5OrdersAdapter(getActivity(),json.getJSONArray("values_last_five_order"));
							CustomerLast5OrdersAdapterOB.notifyDataSetChanged();
							last5Orders.setAdapter(CustomerLast5OrdersAdapterOB);
							ResizeList(last5Orders);
							CustomerLastOrderAdapter CustomerLastOrderAdapterOB= new CustomerLastOrderAdapter(getActivity(),json.getJSONArray("values_last_order_details"));
							CustomerLastOrderAdapterOB.notifyDataSetChanged();
							lastOrder.setAdapter(CustomerLastOrderAdapterOB);
							ResizeList(lastOrder);
							CustDetailsMainLL.setVisibility(View.VISIBLE);
							LabelCustDetailTV.setText("CUSTOMER HISTORY");


							if(json.getJSONArray("values_last_order_details").length()>0){
								LastOrderLabel.setText("Last Order:   "+json.getJSONArray("values_last_order_details").getJSONObject(0).getString("id")+ "     "+json.getJSONArray("values_last_order_details").getJSONObject(0).getString("date_formatted"));}
						}catch (JSONException e)
						{
							Toast.makeText(getActivity(), "Please Try Again",
									Toast.LENGTH_SHORT).show();
						}catch (IOException e)
						{
							Toast.makeText(getActivity(), "Please Try Again",
									Toast.LENGTH_SHORT).show();
						}catch (Exception e)
						{

						}
					}
					
					@Override
					public void failure(RetrofitError error) {
						Toast.makeText(getActivity(), "Please Try Again",
								Toast.LENGTH_LONG).show();
					}
				}
		);
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.pop_enter);
		lay.startAnimation(enter);
		
		lay.setVisibility(View.VISIBLE);
		
		
		
		
		
	
		
		
		return rootView;
		
	}


	void ResizeList(ListView OrderSheetListView)
	{
		ListAdapter listadp = OrderSheetListView.getAdapter();


			int totalHeight = 0;
			for (int i = 0; i < listadp.getCount(); i++) {
				View listItem = listadp.getView(i, null, OrderSheetListView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			ViewGroup.LayoutParams params = OrderSheetListView.getLayoutParams();
			params.height = totalHeight + (OrderSheetListView.getDividerHeight() * (listadp.getCount() - 1));
			OrderSheetListView.setLayoutParams(params);
			OrderSheetListView.requestLayout();}



	
}
