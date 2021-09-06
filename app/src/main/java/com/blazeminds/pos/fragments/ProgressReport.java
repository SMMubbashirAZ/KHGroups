package com.blazeminds.pos.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.blazeminds.pos.R;

import static com.blazeminds.pos.MainActivity.FRAGMENT_TAG;
import static com.blazeminds.pos.MainActivity.trackCount;

public class ProgressReport extends Fragment {
	
	public final static String TAG = ProgressReport.class.getSimpleName();

	
	public ProgressReport() {
	}
	
	public static ProgressReport newInstance() {
		return new ProgressReport();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_progress_report, container, false);
		final Button LoadSheet=rootView.findViewById(R.id.LoadSheet);
		final Button OrderSheet=rootView.findViewById(R.id.OrderSheet);
		final Button DeliverySheet=rootView.findViewById(R.id.DeliverySheet);
		final Button DSRSheet=rootView.findViewById(R.id.DsrReport);
		LoadSheet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoadSheetBtnClick(v);
			}
		});
		OrderSheet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			OrderSheetBtnClick(v);
			}
		});
		DeliverySheet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DeliverySheetBtnClick(v);
			}
		});
		DSRSheet.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DSRSheetBtnClick(v);
			}
		});
	//	LoadSheet.setText("");
//		Button OrderSheet=rootView.findViewById(R.id.OrderSheet);
//		OrderSheet.setText("");
//		Button DeliveryList=rootView.findViewById(R.id.DeliveryList);
//		DeliveryList.setText("");
//
		
		// load the animation


		
		

		
		

		
		
		return rootView;
		
	}
	public void LoadSheetBtnClick(View v)
	{
		FRAGMENT_TAG = LoadSheet.TAG;
		
		trackCount = 0;
		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, LoadSheet.newInstance(), LoadSheet.TAG).commit();
		
	}

	public void OrderSheetBtnClick(View v)
	{
		FRAGMENT_TAG = OrderSheet.TAG;

		trackCount = 0;
		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, OrderSheet.newInstance(), OrderSheet.TAG).commit();

	}
	public void DeliverySheetBtnClick(View v)
	{
		FRAGMENT_TAG = DeliverySheet.TAG;

		trackCount = 0;
		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, DeliverySheet.newInstance(), DeliverySheet.TAG).commit();

	}
	
	public void DSRSheetBtnClick(View v)
	{
		
		FRAGMENT_TAG = DSRSheet.TAG;
		
		trackCount = 0;
		getActivity().getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, DSRSheet.newInstance(), DSRSheet.TAG).commit();
		
	}
	
}
