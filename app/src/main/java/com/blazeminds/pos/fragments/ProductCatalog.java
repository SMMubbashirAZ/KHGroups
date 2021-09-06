package com.blazeminds.pos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.Constant;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.webservice_url.RetrofitWebService;
import com.blazeminds.pos.webservice_url.SyncData;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.blazeminds.pos.webservice_url.Url_Links.KEY_SUCCESS;

/**
 * Created by Blazeminds on 3/14/2018.
 */

public class ProductCatalog extends Fragment implements OnPageChangeListener, OnLoadCompleteListener, View.OnClickListener {
	
	public static final String SAMPLE_FILE = "navigation_drawer.pdf";
	public final static String TAG = ProductCatalog.class.getSimpleName();
	PDFView pdfView;
	Integer pageNumber = 0;
	File pdfFileName;
	File pdfFile;
	Button updateBtn;
	Loader loader;
	PosDB db;
	private String empId, enableCatalog;
	
	public ProductCatalog() {
	}
	
	public static ProductCatalog newInstance() {
		return new ProductCatalog();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_pdf_view, container, false);
		
		RelativeLayout lay = rootView.findViewById(R.id.pdfLayout);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		initUI(rootView);
		
		listeners();
		
		return rootView;
		
	}
	
	private void initUI(View rootView) {
		
		updateBtn = rootView.findViewById(R.id.updateBtn);
		
		loader = new Loader();
		
		pdfView = rootView.findViewById(R.id.pdfView);
		
		pdfFile = new File(Environment.getExternalStorageDirectory() + "/testthreepdf/" + "catalog.pdf");  // -> filename = maven.pdf
		
		db = PosDB.getInstance(getActivity());
		db.OpenDb();
		empId = db.getMobEmpId();
		//enableCatalog = db.getMobEmpEnableCatalog();
		db.CloseDb();
		
		//if (enableCatalog.equalsIgnoreCase("1")) {
		if (pdfFile.exists()) {
			displayFromAsset(pdfFile);
		} else {
			Toast.makeText(AppContextProvider.getContext(), "File Not Exists", Toast.LENGTH_SHORT).show();
		}
		/*}
		else {
            Toast.makeText(getActivity(), "File Disabled From Server", Toast.LENGTH_SHORT).show();

        }*/
		
		
	}
	
	private void listeners() {
		
		updateBtn.setOnClickListener(this);
	}
	
	private void displayFromAsset(File assetFileName) {
		pdfFileName = assetFileName;
		
		pdfView.fromFile(pdfFile)
				.defaultPage(pageNumber)
				.enableSwipe(true)
				.swipeHorizontal(false)
				.onPageChange(this)
				.enableAnnotationRendering(true)
				.onLoad(this)
				.scrollHandle(new DefaultScrollHandle(getActivity()))
				.load();
	}
	
	
	@Override
	public void onPageChanged(int page, int pageCount) {
		pageNumber = page;
		//setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
	}
	
	
	@Override
	public void loadComplete(int nbPages) {
		PdfDocument.Meta meta = pdfView.getDocumentMeta();
		printBookmarksTree(pdfView.getTableOfContents(), "-");
		
	}
	
	public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
		for (PdfDocument.Bookmark b : tree) {
			
			Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
			
			if (b.hasChildren()) {
				printBookmarksTree(b.getChildren(), sep + "-");
			}
		}
	}
	
	@Override
	public void onClick(View view) {
        
        if (view.getId() == R.id.updateBtn) {
            if (Constant.networkAvailable()) {
                SyncForPDF(loader, empId);
            } else {
                Constant.CustomDialogNoInternet(getActivity());
            }
        }
	}
	
	private void SyncForPDF(final Loader ldr, final String empID) {
		
		ldr.showDialog(getActivity());
		
		
		RestAdapter adapter = new RestAdapter.Builder()
									  .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
									  .build(); //Finally building the adapter
		
		RetrofitWebService api = adapter.create(RetrofitWebService.class);
		
		api.sync_alldata(
				
				empID,
				
				//Creating an anonymous callback
				new Callback<Response>() {
					@Override
					public void success(Response result, Response response) {
						//On success we will read the server's output using bufferedreader
						//Creating a bufferedreader object
						
						BufferedReader reader = null;
						
						//An string to store output from the server
						String output = "";
						
						try {
							//Initializing buffered reader
							reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
							
							//Reading the output in the string
							output = reader.readLine();
							
							JSONObject json = new JSONObject(output);
							
							//Log.d("Output Response", json.toString());
							Log.d("Prof", "OUT");
							
							if (json.getString(KEY_SUCCESS) != null) {
								
								String res = json.getString(KEY_SUCCESS);
								
								Log.d("Prof", "res Val " + res);
								
								if (Integer.parseInt(res) == 1) {
									
									
									try {
										
										
										String enable_catalog = json.getString("en_catalogue");
										String catalogURL = json.getString("catalogue_url");
										
										db.OpenDb();
										db.updateMobUserEnableCatalog(empID, enable_catalog);
										db.CloseDb();
										if (enable_catalog.equalsIgnoreCase("1")) {
											new SyncData.DownloadFile().execute(catalogURL,
													"catalog" +
													".pdf");
											Log.d("Download complete", "----------");
											
										}
										
										
										ldr.HideLoader();
										
										Intent loggedIn = new Intent(getActivity(), MainActivity.class);
										getActivity().finish();
										startActivity(loggedIn);
										
										
									} catch (JSONException e) {
										ldr.HideLoader();
										
										e.printStackTrace();
									}
									
								} else if (Integer.parseInt(res) == 0) {
									ldr.HideLoader();
									
									//Toast.makeText(getActivity(), "Unable to create Profile. Try again", Toast.LENGTH_SHORT).show();
									
								}
								
							}
							
							//Toast.makeText(getActivity(), "Synchronizing Done ", Toast.LENGTH_SHORT).show();
							
							
						} catch (IOException e) {
							
							ldr.HideLoader();
							
							Log.d("Prof", "Excep " + e.toString());
							//Toast.makeText(getActivity(), "I/O Exception\n"+e.toString(), Toast.LENGTH_SHORT).show();
							
							e.printStackTrace();
						} catch (JSONException e) {
							ldr.HideLoader();
							
							Log.d("Prof", "Excep JSOn " + e.toString());
							Toast.makeText(AppContextProvider.getContext(), "Json Exception\n" + e.toString(), Toast.LENGTH_SHORT).show();
							
							e.printStackTrace();
						}
						
						
					}
					
					@Override
					public void failure(RetrofitError error) {
						//If any error occured displaying the error as toast
						ldr.HideLoader();
						
						Toast.makeText(AppContextProvider.getContext(), "Kindly Check Your Internet Connection", Toast.LENGTH_LONG).show();
					}
				}
		);
		
		
	}
	
}

