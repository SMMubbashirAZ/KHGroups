package com.blazeminds.pos;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.blazeminds.AppContextProvider;

import java.util.ArrayList;
import java.util.HashMap;

public class AccessControlSettings extends Fragment {
    
    
    public static final String TAG = AccessControlSettings.class.getSimpleName();
    private CheckBox Route, Reporting, Inventory, Customer, POrder, Summary, Sync;
    private int routeVal, reportVal, inventVal, CustVal, POVal, SummaryVal, SyncVal;
    private Button SaveSettingsBtn;
    
    public AccessControlSettings() {
    }
    
    public static AccessControlSettings newInstance() {
        return new AccessControlSettings();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        View rootView = inflater.inflate(R.layout.access_control_settings, container, false);
        
        
        Route = rootView.findViewById(R.id.RouteCheck);
        Reporting = rootView.findViewById(R.id.ReportCheck);
        Inventory = rootView.findViewById(R.id.InventoryCheck);
        Customer = rootView.findViewById(R.id.CustomerCheck);
        POrder = rootView.findViewById(R.id.PlaceOrderCheck);
        Summary = rootView.findViewById(R.id.SummaryCheck);
        Sync = rootView.findViewById(R.id.SyncCheck);
        
        SaveSettingsBtn = rootView.findViewById(R.id.SaveSettings);
        
        
        String SettingsInfo = ShowMenuAsSettings();
        
        if (SettingsInfo.contains("route1")) {
            Route.setChecked(true);
        }
        if (SettingsInfo.contains("route0")) {
            Route.setChecked(false);
        }
        
        if (SettingsInfo.contains("reporting1")) {
            Reporting.setChecked(true);
        }
        if (SettingsInfo.contains("reporting0")) {
            Reporting.setChecked(false);
        }
        
        if (SettingsInfo.contains("inv0")) {
            Inventory.setChecked(false);
        }
        if (SettingsInfo.contains("inv1")) {
            Inventory.setChecked(true);
        }
        if (SettingsInfo.contains("cust0")) {
            Customer.setChecked(false);
        }
        if (SettingsInfo.contains("cust1")) {
            Customer.setChecked(true);
        }
        if (SettingsInfo.contains("po0")) {
            POrder.setChecked(false);
        }
        if (SettingsInfo.contains("po1")) {
            POrder.setChecked(true);
        }
        if (SettingsInfo.contains("sumry0")) {
            Summary.setChecked(false);
        }
        if (SettingsInfo.contains("sumry1")) {
            Summary.setChecked(true);
        }
        if (SettingsInfo.contains("syncpg0")) {
            Sync.setChecked(false);
        }
        if (SettingsInfo.contains("syncpg1")) {
            Sync.setChecked(true);
        }
        
        
        SaveSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                if (Route.isChecked())
                    routeVal = 1;
                else
                    routeVal = 0;
                
                if (Reporting.isChecked())
                    reportVal = 1;
                else
                    reportVal = 0;
                
                if (Inventory.isChecked())
                    inventVal = 1;
                else
                    inventVal = 0;
                
                if (Customer.isChecked())
                    CustVal = 1;
                else
                    CustVal = 0;
                
                if (POrder.isChecked())
                    POVal = 1;
                else
                    POVal = 0;
                
                if (Summary.isChecked())
                    SummaryVal = 1;
                else
                    SummaryVal = 0;
                
                if (Sync.isChecked())
                    SyncVal = 1;
                else
                    SyncVal = 0;
                
                PosDB db =  PosDB.getInstance(getActivity());
                db.OpenDb();
                db.updateAccessControlSettings(routeVal, reportVal, inventVal, CustVal, POVal, SummaryVal, SyncVal);
                
                String res = db.getSettingsData();
                db.CloseDb();
                Log.d("Setings DB Data UPDATED", res);
                
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AppContextProvider.getContext(), "Settings Updated", Toast.LENGTH_SHORT).show();
                    }
                });
                
            }
        });
        
        
        return rootView;
        
    }
    
    
    public String ShowMenuAsSettings() {
        
        String Options = "";
        ArrayList<HashMap<String, String>> DBVal;
        
        PosDB db = PosDB.getInstance(getActivity());
        db.OpenDb();
        DBVal = db.SettingsDataHash();
        db.CloseDb();
        
        
        Log.d("DBSetSize", DBVal.size() + "");
        
        String timing = "", route = "", report = "", ASync = "", SyncDur = "", SyncDurIndex = "", Inv = "", Cust = "", PO = "", Sumry = "", SyncPg = "", EPass = "";
        String TI = "", TO = "", Pass = "";
        
        HashMap<String, String> f;
        
        if (DBVal.size() > 0) {
            
            for (int i = 0; i < DBVal.size(); i++) {
                
                f = DBVal.get(i);
                
                timing = f.get("timing");
                route = f.get("route");
                report = f.get("reporting");
                ASync = f.get("async");
                SyncDur = f.get("syncdur");
                SyncDurIndex = f.get("syncdurindex");
                Inv = f.get("inv");
                Cust = f.get("cust");
                PO = f.get("po");
                Sumry = f.get("sumry");
                SyncPg = f.get("syncpg");
                TI = f.get("ti");
                TO = f.get("to");
                Pass = f.get("pass");
                EPass = f.get("epass");
                
                
            }
        }
        
        Log.d("Timing", timing);
        Log.d("ROute", route);
        Log.d("ASync", ASync);
        Log.d("TI", TI);
        Log.d("TO", TO);
        Log.d("SyncDur", SyncDur);
        Log.d("SyncDurIndex", SyncDurIndex);
        Log.d("Inv", Inv);
        Log.d("Cust", Cust);
        Log.d("Sumry", Sumry);
        Log.d("SyncPg", SyncPg);
        Log.d("Pass", Pass);
        Log.d("EPass", EPass);
        Log.d("PO", PO);
        
        
        return "timing" + timing + "route" + route + "reporting" + report + "async" + ASync + "syncdur" + SyncDur + "syncdurindex" + SyncDurIndex + "inv" + Inv + "cust" + Cust + "sumry" + Sumry + "syncpg" + SyncPg + "pass" + Pass + "epass" + EPass + "po" + PO + "timein" + TI + "timeout" + TO;
        
    }
    
    
}
