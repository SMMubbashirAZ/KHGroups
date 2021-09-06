package com.blazeminds.pos.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blazeminds.pos.BuildConfig;
import com.blazeminds.pos.MainActivity;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.webservice_url.RetrofitWebService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.blazeminds.pos.MainActivity.FRAGMENT_TAG;

public class Profile extends Fragment {
    
    public final static String TAG = Profile.class.getSimpleName();
    
    
    public Profile() {
    }
    
    public static Profile newInstance() {
        return new Profile();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        
        LinearLayout lay = rootView.findViewById(R.id.ProfileFrag);
        
        FRAGMENT_TAG = Profile.TAG;
        
        // load the animation
        Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_right);
        
        lay.startAnimation(enter);
        
        final TextView ID = rootView.findViewById(R.id.getCustID);
        
        final TextView Name = rootView.findViewById(R.id.NameTxt);
        final TextView CompName = rootView.findViewById(R.id.CompNameTxt);
        final TextView Email = rootView.findViewById(R.id.EmailTxt);
        final TextView Cell = rootView.findViewById(R.id.CellTxt);
        final TextView P1 = rootView.findViewById(R.id.P1Txt);
        final TextView P2 = rootView.findViewById(R.id.P2Txt);
        final TextView Address = rootView.findViewById(R.id.AddressTxt);
        final TextView City = rootView.findViewById(R.id.CityTxt);
        final TextView State = rootView.findViewById(R.id.StateTxt);
        final TextView Country = rootView.findViewById(R.id.CountryTxt);
        final TextView Zip = rootView.findViewById(R.id.ZipCodeTxt);
        
        Button Edit = rootView.findViewById(R.id.EditCustomerBtn);
        
        
        PosDB db = PosDB.getInstance(getActivity());
        db.OpenDb();
        
        Name.setText(db.getMobFName() + " " + db.getMobLName());
        
        if (db.getMobCompany().trim().isEmpty()) {
            CompName.setText("N/A");
        } else {
            CompName.setText(db.getMobCompany());
        }
        
        if (db.getMobEmail().trim().isEmpty()) {
            Email.setText("N/A");
        } else {
            Email.setText(db.getMobEmail());
        }
        
        if (db.getMobCell().trim().isEmpty()) {
            Cell.setText("N/A");
        } else {
            Cell.setText(db.getMobCell());
        }
        
        if (db.getMobP1().trim().isEmpty()) {
            P1.setText("N/A");
        } else {
            P1.setText(db.getMobP1());
        }
        
        if (db.getMobP2().trim().isEmpty()) {
            P2.setText("N/A");
        } else {
            P2.setText(db.getMobP2());
        }
        
        if (db.getMobAddress().trim().isEmpty()) {
            Address.setText("N/A");
        } else {
            Address.setText(db.getMobAddress());
        }
        
        if (db.getMobCity().trim().isEmpty()) {
            City.setText("N/A");
        } else {
            City.setText(db.getMobCity());
        }
        
        if (db.getMobState().trim().isEmpty()) {
            State.setText("N/A");
        } else {
            State.setText(db.getMobState());
        }
        
        if (db.getMobCountry().trim().isEmpty()) {
            Country.setText("N/A");
        } else {
            Country.setText(db.getMobCountry());
        }
        
        if (db.getMobZipCode().trim().isEmpty()) {
            Zip.setText("N/A");
        } else {
            Zip.setText(db.getMobZipCode());
        }
        db.CloseDb();
        
        
        final Activity a = getActivity();
        
        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                EditDialog(a);
                
                
            }
        });


                  /*
						SHOW Customer Ends
                 */
        
        return rootView;
        
    }
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!getActivity().getSupportFragmentManager().isDestroyed()) {
            Log.e("onDestroy", "1");
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            Log.e("onDestroy", "2");
        }
    }
    
    public void EditDialog(final Activity act) {
        
        final Dialog dialog = new Dialog(act);
        
        //       dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_profile);
        
        dialog.setTitle("Update Profile");
        
        final EditText FName = dialog.findViewById(R.id.FNameTxt);
        final EditText LName = dialog.findViewById(R.id.LNameTxt);
        final EditText CompName = dialog.findViewById(R.id.CompNameTxt);
        final EditText Email = dialog.findViewById(R.id.EmailTxt);
        final EditText Cell = dialog.findViewById(R.id.CellTxt);
        final EditText P1 = dialog.findViewById(R.id.P1Txt);
        final EditText P2 = dialog.findViewById(R.id.P2Txt);
        final EditText Address = dialog.findViewById(R.id.AddressTxt);
        final EditText City = dialog.findViewById(R.id.CityTxt);
        final EditText State = dialog.findViewById(R.id.StateTxt);
        final EditText Country = dialog.findViewById(R.id.CountryTxt);
        final EditText Zip = dialog.findViewById(R.id.ZipCodeTxt);
        
        Button Update = dialog.findViewById(R.id.UpdateCustomerBtn);
        
        PosDB db = PosDB.getInstance(act);
        db.OpenDb();
        ArrayList<String> mUser = db.getMobUserDetails();
        db.CloseDb();
        
        Log.d("M User", mUser.toString());
        
        String IdSelected = "";
        
        for (String parts : mUser) {
            
            System.out.println(parts);
            String[] part1 = parts.split("fname");
            String fname = part1[1];
            
            String id = part1[0];
            String[] ID = id.split("cust_id");
            IdSelected = ID[1];
            
            Log.d("ID Selccted", IdSelected.trim());
            
            String[] L = fname.split("lname");
            String Fname = L[0].trim();
            String Lname = L[1];
            String[] Last = Lname.split("comp");
            String Lst = Last[0].trim();
            FName.setText(Fname);
            LName.setText(Lst);
            
            String[] part3 = parts.split("comp");
            String comp = part3[1];
            String[] Co = comp.split("email");
            String Comp = Co[0].trim();
            CompName.setText(Comp);
            
            String[] part4 = parts.split("email");
            String email = part4[1];
            String[] Em = email.split("cell");
            String CELL = Em[0].trim();
            Cell.setText(CELL);
            
            String[] part5 = parts.split("cell");
            String cell = part5[1];
            String[] Ce = cell.split("p1");
            String Phone = Ce[0].trim();
            P1.setText(Phone);
            
            String[] part6 = parts.split("p1");
            String p1 = part6[1];
            String[] PP1 = p1.split("p2");
            String Phone2 = PP1[0].trim();
            P2.setText(Phone2);
            
            String[] part7 = parts.split("p2");
            String p2 = part7[1];
            String[] PP2 = p2.split("add");
            String Add = PP2[0].trim();
            Address.setText(Add);
            
            String[] part8 = parts.split("add");
            String add = part8[1];
            String[] Add1 = add.split("city");
            String CITY = Add1[0].trim();
            City.setText(CITY);
            
            String[] part9 = parts.split("city");
            String city = part9[1];
            String[] CI = city.split("country");
            String STATE = CI[0].trim();
            State.setText(STATE);
            
            String[] part10 = parts.split("state");
            String state = part10[1];
            String[] ST = state.split("zip");
            String ZIP = ST[0].trim();
            Zip.setText(ZIP);
            
            String[] part11 = parts.split("country");
            String country = part11[1];
            String[] CO = country.split("state");
            String Count = CO[0].trim();
            Country.setText(Count);
            
            String[] part12 = parts.split("zip");
            String EMAIL = part12[1].trim();
            Email.setText(EMAIL);
        }
        
        
        final String finalIdSelected = IdSelected.trim();
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                
                if (FName.getText().toString().trim().isEmpty()) {
                    FName.setError("REQUIRED");
                }
                
                if (LName.getText().toString().trim().isEmpty()) {
                    LName.setError("REQUIRED");
                }
                
                if (Cell.getText().toString().trim().isEmpty()) {
                    Cell.setError("REQUIRED");
                }
                
                if (Email.getText().toString().trim().isEmpty()) {
                    Email.setError("REQUIRED");
                }
                
                if (isInternetAvailable()) {
                    
                    
                    SyncProfileDialog();
                    
                    PosDB db = PosDB.getInstance(act);
                    db.OpenDb();
                    //db.updateMobUserDetail(finalIdSelected, FName.getText().toString().trim(), LName.getText().toString().trim()/*, CompName.getText().toString().trim(),*/, Email.getText().toString().trim(), Address.getText().toString().trim(), City.getText().toString().trim(), State.getText().toString().trim(), Zip.getText().toString().trim(), Country.getText().toString().trim(), Cell.getText().toString().trim(), P1.getText().toString().trim(), P2.getText().toString().trim());
                    
                    
                    db.CloseDb();
                    
                    dialog.dismiss();
                    
                    Intent i = new Intent(act, MainActivity.class);
                    
                    act.startActivity(i);
                    
                    
                    Toast.makeText(act.getApplicationContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                    
                    
                } else {
                    CustomDialogNoInternet();
                }
                
                
            }
        });
        
        
        dialog.show();
        
    }
    
    
    public boolean isInternetAvailable() {
    
  /*      Runtime runtime = Runtime.getRuntime();
        try {

//            Process ipProcess = runtime.exec("/system/bin/ping -c 1 67.222.152.138");
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 google.com");
            int exitValue = ipProcess.waitFor();
            
            Log.d("IP ADDR", ipProcess.toString());
            Log.d("IP ADDR", String.valueOf(exitValue));
            return (exitValue == 0);
            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.fillInStackTrace());
            
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.fillInStackTrace());
        }*/
    
        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress("35.190.168.249", 80);
        
            socket.connect(socketAddress, 15000);
        
            
            socket.close();
            return true;
        } catch (IOException e) {
        
            Log.e("CheckResponse",
                    e.getMessage());
            return false;
        }
        
        
    }
    
    
    private void CustomDialogNoInternet() {
        /*
         * Custom Dialog box starts
         */
        
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Sorry No Internet Connection Found")
                .setCancelable(false)
                /*.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (isInternetAvailable()) {
                            SyncData();
                        } else {
                            CustomDialogNoInternet();
                        }

                    }
                })*/
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                
                );
        AlertDialog alert = builder.create();
        alert.setTitle("Sorry");
        alert.setMessage("Please Check Your Internet Connection");
        alert.setIcon(R.drawable.not_selected);
        
        alert.show();
        
        /*
         * Custom dialog box ends
         */
        
    }
    
    
    public void SyncProfileDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Updating Profile ...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Here you should write your time consuming task...
                    SyncProfile();
                    
                    
                    ringProgressDialog.dismiss();
                } catch (final Exception e) {
                    
                    ringProgressDialog.dismiss();
                    
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "Thread Exception : " + e.toString(), Toast.LENGTH_SHORT).show();
                            
                            Log.e("thread exc: ", e.toString());
                        }
                    });
                    
                    e.printStackTrace();
                    throw new RuntimeException(e.fillInStackTrace());
                    
                }
                
            }
        }).start();
    }
    
    
    public void SyncProfile() {

                 /*
                    Profile SYNC
                 */
        
        PosDB mydb = PosDB.getInstance(getActivity());
        mydb.OpenDb();
        
        ArrayList<HashMap<String, String>> dataPro = mydb.getMobUserDetailsSYNC();
        
        
        if (dataPro.size() > 0) {
            for (int i = 0; i < dataPro.size(); i++) {
                HashMap<String, String> f = dataPro.get(i);
                
                final String id = f.get("id");
                String fname = f.get("fname");
                String lname = f.get("lname");
                String comp = f.get("comp_name");
                String cell = f.get("cell");
                String p1 = f.get("ph1");
                String p2 = f.get("ph2");
                String add = f.get("add");
                String city = f.get("city");
                String state = f.get("state");
                String country = f.get("country");
                String zip = f.get("zip");
                String email = f.get("email");
                
                SyncProfileJSON(id, fname, lname, comp, cell, p1, p2, add, city, state, country, zip, email);
                // json = SyncCustomer(fname, lname);
                
                Log.d("Customers", fname + " " + lname + " " + comp + " " + cell + " " + p1 + " " + p2 + " " + add + " " + city + " " + state + " " + country + " " + zip + " " + email);
                
                
            }
        }
        
        
    }
    
    
    private void SyncProfileJSON(String id, String fname, String lname, String comp_name, String cell, String ph1, String ph2, String add, String city, String state, String country, String zip, String email) {
        
        
        RestAdapter adapter = new RestAdapter.Builder()
                .setEndpoint(BuildConfig.BASE_URL) //Setting the Root URL
                .build(); //Finally building the adapter
        
        RetrofitWebService api = adapter.create(RetrofitWebService.class);
        
        api.sync_profile(
                
                id,
                fname,
                lname,
                comp_name,
                cell,
                ph1,
                ph2,
                add,
                city,
                state,
                country,
                zip,
                email,
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                    
                    }
                    
                    @Override
                    public void failure(RetrofitError error) {
                    
                    }
                }
        );
        
        
    }
    
}




