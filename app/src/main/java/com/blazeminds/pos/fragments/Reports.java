package com.blazeminds.pos.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.resources.Loader;

/**
 * Created by Blazeminds on 3/1/2018.
 */

public class Reports extends Fragment {
	
	public final static String TAG = Reports.class.getSimpleName();
	private WebView webView;
	private Loader loader;
	private PosDB db;
	
	public Reports() {
	}
	
	public static Reports newInstance() {
		return new Reports();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
		
		LinearLayout lay = rootView.findViewById(R.id.reportsLayout);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		webView = rootView.findViewById(R.id.webView);
		loader = new Loader();
		
		db = PosDB.getInstance(getActivity());
		
		db.OpenDb();
		String reportUrl = db.getMobEmpReportUrl();
		String empId = db.getMobEmpId();
		db.CloseDb();
		
		startWebView(reportUrl + "" + empId);
		
		return rootView;
		
	}
	
	private void startWebView(String url) {
		
		WebSettings settings = webView.getSettings();
		
		settings.setJavaScriptEnabled(true);
		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		
		loader.showDialog(getActivity());
		
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				
				loader.HideLoader();
				
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				//Code here
				//Clearing the WebView
				try {
					webView.stopLoading();
				} catch (Exception ignored) {
				}
				try {
					webView.clearView();
				} catch (Exception ignored) {
				}
				if (webView.canGoBack()) {
					webView.goBack();
				}
				webView.loadUrl("about:blank");
				
				//Showing and creating an alet dialog
				AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
				alertDialog.setTitle("Sorry");
				alertDialog.setMessage("Please Check Your Internet Connection");
				alertDialog.setButton("Again", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						getActivity().finish();
						startActivity(getActivity().getIntent());
					}
				});
				
				alertDialog.show();
				
				//Don't forget to call supper!
				super.onReceivedError(webView, errorCode, description, failingUrl);
				
			}
		});
		webView.loadUrl(url);
	}
}
