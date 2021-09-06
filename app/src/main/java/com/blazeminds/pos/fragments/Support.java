package com.blazeminds.pos.fragments;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.SupportAdapter;
import com.blazeminds.pos.SupportDetailListAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Blazeminds on 4/17/2018.
 */

public class Support extends Fragment implements View.OnClickListener {
	
	public final static String TAG = Support.class.getSimpleName();
	private TextView startTimeTV;
	private ListView listView, headerList;
	private TextView noItemTxt;
	private ArrayList<String> dataSupportId;
	private ArrayList<HashMap<String, String>> Hlist;
	private ArrayList<HashMap<String, String>> data;
	private Button addTicketBtn;
	private PosDB db;
	
	public Support() {
	}
	
	public static Support newInstance() {
		return new Support();
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
		
		View rootView = inflater.inflate(R.layout.fragment_support, container, false);
		
		
		initUI(rootView);
		
		listeners();
		
		return rootView;
		
	}
	
	private void initUI(View rootView) {
		
		db = PosDB.getInstance(getActivity());
		LinearLayout lay = rootView.findViewById(R.id.supportFragment);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		lay.setVisibility(View.VISIBLE);
		
		startTimeTV = rootView.findViewById(R.id.startTime);
		startTimeTV.setText(getDateTime());
		
		startTimeTV.setVisibility(View.GONE);
		
		
		addTicketBtn = rootView.findViewById(R.id.addTicketBtn);
		
		listView = rootView.findViewById(R.id.listviewRList);
		headerList = rootView.findViewById(R.id.Hlist);
		noItemTxt = rootView.findViewById(R.id.NoItemTxt);
		
		
		populateListOfSupport(noItemTxt);
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				
				String selectedSupportId = dataSupportId.get(position);
				
				//Toast.makeText(getActivity(), "Support Id "+selectedSupportId, Toast.LENGTH_SHORT).show();
				
				supportDetailsDialog(selectedSupportId);
				
			}
		});
		
		
	}
	
	private void listeners() {
		
		addTicketBtn.setOnClickListener(this);
		
		
	}
	
	@Override
	public void onClick(View view) {
		
		if (view.getId() == R.id.addTicketBtn) {
			addTicketsDialog();
		}
	}
	
	private void addTicketsDialog() {
		
		final Dialog dialog = new Dialog(getActivity());
		
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.popup_add_ticket);
		dialog.setTitle("Add Ticket");
		//dialog.getWindow().setBac(getResources().getColor(R.color.login_bg));
		// dialog.getWindow().
		
		
		//dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.90);
		dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
		
		
		//  dialog.setTitle(ItemName);
		final Button submitBtn = dialog.findViewById(R.id.SubmitBtn);
		final EditText titleEdtTxt = dialog.findViewById(R.id.titleEdtTxt);
		final EditText messageEdtTxt = dialog.findViewById(R.id.messageEdtTxt);
		Button cancelBtn = dialog.findViewById(R.id.CancelBtn);
		messageEdtTxt.setImeOptions(EditorInfo.IME_ACTION_DONE);
		messageEdtTxt.setRawInputType(InputType.TYPE_CLASS_TEXT);
		
		dialog.show();
		
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		
		submitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				
				if (titleEdtTxt.getText().toString().trim().isEmpty() || messageEdtTxt.getText().toString().trim().isEmpty()) {
					
					if (titleEdtTxt.getText().toString().trim().isEmpty()) {
						titleEdtTxt.setError("Title Required");
					}
					
					if (messageEdtTxt.getText().toString().trim().isEmpty()) {
						messageEdtTxt.setError("Message Required");
					}
				} else {
					
					submitBtn.setClickable(false);
					db.OpenDb();
					String datetime = getDateTime();
					int maxSupportId = db.getMaxIdFromSupport();
					int maxSupportDetailId = db.getMaxIdFromSupportDetail();
					String empID = db.getMobEmpId();
					long supportInsId = db.createSupport(maxSupportId + 1, titleEdtTxt.getText().toString(), 1, datetime, 1, null);
					int maxSupportID = db.getMaxIdFromSupport();
					
					long supportDetailInsId = db.createSupportDetail(maxSupportDetailId + 1, maxSupportID, messageEdtTxt.getText().toString(), datetime, Integer.parseInt(empID), 0);
					if (supportInsId > 0 && supportDetailInsId > 0) {
						submitBtn.setClickable(true);
						Toast.makeText(AppContextProvider.getContext(), "Support Created", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
						populateListOfSupport(noItemTxt);
						
					} else {
						//Toast.makeText(getActivity(), "Support doesn't Inserted in DB, Something went wrong", Toast.LENGTH_SHORT).show();
						
					}
					
					
				}
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				dialog.dismiss();
			}
		});
	}
	
	private void supportDetailsDialog(final String supportId) {
		
		final Dialog dialog = new Dialog(getActivity());
		
		//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.popup_support_details);
		dialog.setTitle(supportId + " | " + db.getSupportTitleById(supportId));
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		
		
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.90);
		int screenHeight = (int) (metrics.heightPixels * 0.80);
		
		dialog.getWindow().setLayout(screenWidth, screenHeight); //set below the setContentview
		
		
		//  dialog.setTitle(ItemName);
		final Button submitBtn = dialog.findViewById(R.id.SubmitBtn);
		final EditText messageEdtTxt = dialog.findViewById(R.id.messageEdtTxt);
		final TextView noItemTxt = dialog.findViewById(R.id.NoItemTxt);
		final ListView headerlistViewSupportDetails = dialog.findViewById(R.id.headerListSupportDetail);
		final ListView listViewSupportDetails = dialog.findViewById(R.id.listviewSupportDetails);
		final ScrollView scrollView = dialog.findViewById(R.id.scrollView1);
		Button cancelBtn = dialog.findViewById(R.id.CancelBtn);
		
		//scrollView.setScrollBarFadeDuration(0);
		
		populateListOfSupportDetails(supportId, noItemTxt, listViewSupportDetails, headerlistViewSupportDetails);
		
		
		submitBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				//Toast.makeText(getActivity(), "Submit Click",Toast.LENGTH_SHORT).show();
				
				if (messageEdtTxt.getText().toString().trim().isEmpty()) {
					messageEdtTxt.setError("Message Required For Submit");
				} else {
					
					submitBtn.setClickable(false);
					
					int maxSupportDetailId = db.getMaxIdFromSupportDetail();
					String empID = db.getMobEmpId();
					int syncFromSupport = db.getSyncFromSupport(supportId);
					long supportDetailInsId = 0;
					if (syncFromSupport == 1) {
						supportDetailInsId = db.createSupportDetail(maxSupportDetailId + 1, Integer.parseInt(supportId), messageEdtTxt.getText().toString(), getDateTime(), Integer.parseInt(empID), 0);
					} else if (syncFromSupport == 0) {
						supportDetailInsId = db.createSupportDetail(maxSupportDetailId + 1, Integer.parseInt(supportId), messageEdtTxt.getText().toString(), getDateTime(), Integer.parseInt(empID), 1);
					}
					if (supportDetailInsId > 0) {
						submitBtn.setClickable(true);
						messageEdtTxt.setText("");
						populateListOfSupportDetails(supportId, noItemTxt, listViewSupportDetails, headerlistViewSupportDetails);
					} else {
						//Toast.makeText(getActivity(), "Support Detail doesn't Inserted in DB, Something went wrong", Toast.LENGTH_SHORT).show();
						
					}
					
				}
				
				
			}
		});
		
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				dialog.dismiss();
			}
		});
		
		dialog.show();
		
	}
	
	private ArrayList<HashMap<String, String>> populateListOfSupport(TextView noItemText) {
		
		data = new ArrayList<>();
		dataSupportId = new ArrayList<>();
		dataSupportId.clear();
		data.clear();
		
		PosDB db =PosDB.getInstance(getActivity());
		
		db.OpenDb();
		
		///dataCustId = db.getSelectedCustomerID();
		dataSupportId = db.getSelectedSupportID();
		data = db.getSupportList();
		
		db.CloseDb();
		
		if (data.size() > 0) {
			noItemText.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			SupportAdapter adapter1 = new SupportAdapter(getActivity(), data);
			listView.setAdapter(adapter1);
			
		} else {
			//Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
			listView.setVisibility(View.GONE);
			noItemText.setVisibility(View.VISIBLE);
		}
		
		
		return data;
		
	}
	
	private ArrayList<HashMap<String, String>> populateListOfSupportDetails(String supportId, TextView noItemText, ListView listView, ListView headerList) {
		
		data = new ArrayList<>();
		//dataSupportId = new ArrayList<String>();
		//dataSupportId.clear();
		data.clear();
		
		PosDB db = PosDB.getInstance(getActivity());
		
		db.OpenDb();
		
		///dataCustId = db.getSelectedCustomerID();
		//dataSupportId = db.getSelectedSupportID();
		data = db.getSupportDetailList(supportId);
		
		db.CloseDb();
		
		if (data.size() > 0) {
			
			
			noItemText.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			SupportDetailListAdapter adapter1 = new SupportDetailListAdapter(getActivity(), data);
			listView.setAdapter(adapter1);
			
		} else {
			//Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
			listView.setVisibility(View.GONE);
			noItemText.setVisibility(View.VISIBLE);
		}
		
		
		return data;
		
	}
	
	
}
