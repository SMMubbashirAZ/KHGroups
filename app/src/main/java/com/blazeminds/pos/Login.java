package com.blazeminds.pos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.resources.Loader;
import com.blazeminds.pos.resources.UserSettings;
import com.blazeminds.pos.webservice_url.SyncData;
import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.fabric.sdk.android.Fabric;


public class Login extends Activity implements OnClickListener {
    
    // JSON Response node names
    private static final String KEY_SUCCESS = "code";
    EditText getEmail;
    private EditText User;
    private EditText Password;
    private ImageButton showEnable;
    private boolean showHide = true, granted = true;
    private Loader loader;
    
    
    private boolean networkAvailable() {
            /*ConnectivityManager cm = (ConnectivityManager)
					getSystemService(this.CONNECTIVITY_SERVICE);
					NetworkInfo ni = cm.getActiveNetworkInfo();
					if (ni == null)
					    return false;
					return ni.isConnectedOrConnecting();*/
        
 /*       Runtime runtime = Runtime.getRuntime();
        try {
            
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 67.222.152.138");
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 google.com");
            int exitValue = ipProcess.waitFor();
            
            Log.d("IP ADDR", ipProcess.toString());
            Log.d("IP ADDR", String.valueOf(exitValue));
            return (exitValue == 0 || exitValue == 1);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return false;*/
        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress("35.190.168.249", 80);
            
            socket.connect(socketAddress, 15000);
            
            //Log.e("CheckResponse",String.valueOf(socket.isConnected())+" ::: "+  socketAddress.getHostName()+" ::: "+socketAddress.getAddress());
            socket.close();
            return true;
        } catch (Exception e) {
            
//            Log.e("CheckResponse",
//                    e.getMessage());
            return false;
        }
        
        
    }
    
    
    private void CustomDialogNoInternet() {
        /*
         * Custom Dialog box starts
         */
        
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("Sorry");
        alert.setMessage("Please Check Your Internet Connection");
        alert.setIcon(R.drawable.not_selected);
        alert.show();
        /*
         * Custom dialog box ends
         */
        
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        
        CustomActivityOnCrash.install(this);
        
        setContentView(R.layout.activity_login);
        
        //private JSONParser jsonParser = new JSONParser();
        PosDB db1 = PosDB.getInstance(getApplicationContext());
        
        SharedPreferences appPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isAppInstalled = appPreferences.getBoolean("isAppInstalled", false);
        if (!isAppInstalled) {
            
            Intent shortcutIntent = new Intent(getApplicationContext(), Login.class);
            shortcutIntent.setAction(Intent.ACTION_MAIN);
            Intent intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, R.string.app_name);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.drawable.ic_launcher));
            intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getApplicationContext().sendBroadcast(intent);
            
            SharedPreferences.Editor editor = appPreferences.edit();
            editor.putBoolean("isAppInstalled", true);
            editor.apply();
        }
        
        
        PosDB db = PosDB.getInstance(getApplicationContext());
        db.OpenDb();
        String regUser = db.getMobUser();
        
        int chkSettings = db.CheckSettingsData();
        
        if (chkSettings <= 0) {
            long SettingsID = db.createSettingsEntryAtStartup();
            String res1 = db.getSettingsData();
            
            Log.d("Setings DB Data LOGIn", res1 + "\n" + SettingsID);
            
        }
        
        db.CloseDb();
        
        
        if (regUser.trim().equals("")) {
            
            requestPermission();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            int dens = dm.densityDpi;
            double wi = (double) width / (double) dens;
            double hi = (double) height / (double) dens;
            double x = Math.pow(wi, 2);
            double y = Math.pow(hi, 2);
            double screenInches = Math.sqrt(x + y);
            
            
            RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
            RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
                    width, height);
            relativeLayout.setLayoutParams(rel_btn);
            
            DigitalClock clock = findViewById(R.id.DigitalClock);
            User = findViewById(R.id.UNameTxt);
            Password = findViewById(R.id.PassTxt);
            
            Button login = findViewById(R.id.LoginBtn);
            Button clearData = findViewById(R.id.clearData);
            Button forgetPass = findViewById(R.id.ForgetPassLogin);
            Button register = findViewById(R.id.RegBtn);
            ImageView bgImage = findViewById(R.id.imageView1);
            showEnable = findViewById(R.id.showPass);
            
            
            LayoutParams layoutParams = bgImage.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            bgImage.setLayoutParams(layoutParams);
            
            Typeface type = Typeface.createFromAsset(getAssets(), "verdana.ttf");
            clock.setTypeface(type);
            
            //User.setBackgroundColor(Color.rgb(4, 130, 190));
            //Password.setBackgroundColor(Color.WHITE);
            //login.setBackgroundColor(Color.rgb(4, 130, 190));
            
            
            User.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    //showPassword.setVisibility(View.VISIBLE);
                    User.setError(null);
                    
                }
            });
            
            Password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    
                    Password.setError(null);
                }
            });
            
            Password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
                
                }
                
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 1) {
                    
                    }
                }
                
                @Override
                public void afterTextChanged(Editable editable) {
                
                }
            });
            
            Button ShowImeiBtn = findViewById(R.id.ImeiBtn);
            if (Build.VERSION.SDK_INT >= 29)
            {
                ShowImeiBtn.setText("SHOW ANDROID ID");
            }
           
           
            ShowImeiBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(Login.this,
                                new String[]{Manifest.permission.READ_PHONE_STATE},
                                2);
                        
                    } else {
                        IMEIDialog();
                    }
                    
                    
                }
            });
            
            
            login.setOnClickListener(this);
            clearData.setOnClickListener(this);
            forgetPass.setOnClickListener(this);
            register.setOnClickListener(this);
            showEnable.setOnClickListener(this);
            
        } else {
            PrefManager prefManager;
            final Intent next = new Intent(getApplicationContext(), MainActivity.class);
            prefManager = new PrefManager(this);
            prefManager.setFirstTimeLaunch(false);

            startActivity(next);
            overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
            finish();
            
            Toast.makeText(AppContextProvider.getContext(), "Welcome Again " + regUser, Toast.LENGTH_LONG).show();
            
        }
        
    }
    
    
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
        switch (v.getId()) {
            
            case R.id.LoginBtn: {
                
                
                if (granted) {
                    if (User.getText().toString().trim().isEmpty() || Password.getText().toString().trim().isEmpty()) {
                        if (User.getText().toString().trim().isEmpty())
                            User.setError("REQUIRED");
                        if (Password.getText().toString().trim().isEmpty()) {
                            Password.setError("REQUIRED");
                            
                        }
                        
                    } else /*if (networkAvailable())*/ {


                        loader = new Loader();
                        loader.showDialog(Login.this);
                        String u = User.getText().toString(), p = Password.getText().toString();
                        new Sync().execute(u, p);
                    }
                        
//                    } else {
//                        CustomDialogNoInternet();
//                    }
                }
                else {
                    requestPermission();
                }
            }
            break;
            
            case R.id.RegBtn: {

                /*Intent i = new Intent(this,AddEmployee.class);
                startActivity(i);*/
            }
            break;
            
            case R.id.showPass:
                
                if (showHide) {
                    
                    showEnable.setBackgroundResource(R.drawable.ic_eye_off_black);
                    Password.setTransformationMethod(null);
                    Password.setSelection(Password.getText().toString().length());
                    showHide = false;
                    
                    
                } else {
                    showEnable.setBackgroundResource(R.drawable.eye_show);
                    Password.setTransformationMethod(new PasswordTransformationMethod());
                    Password.setSelection(Password.getText().toString().length());
                    showHide = true;
                }
                
                break;
            
            case R.id.clearData:
                
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
                
                break;
        }
        
    }
    
    
    @Override
    public void onBackPressed() {
        finish();
    }
    
    
    @SuppressLint("HardwareIds")
    private String getIMEI() {
        
        
        TelephonyManager mngr = (TelephonyManager) Login.this.getSystemService(TELEPHONY_SERVICE);
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "No Permission Access";
        }
		if (Build.VERSION.SDK_INT >= 29)
		{
			return  Settings.Secure.getString(Login.this.getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		else {
			return mngr.getDeviceId();
		}
    }
    
    
    private void IMEIDialog() {
        
        final Dialog dialog = new Dialog(Login.this);
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_imei);
        
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview
        
        
        //  dialog.setTitle(ItemName);
        TextView IMEI = dialog.findViewById(R.id.ImeiTxt);
        TextView textView9 = dialog.findViewById(R.id.textView9);
        if (Build.VERSION.SDK_INT >= 29)
        {
            textView9.setText("ANDROID ID INFORMATION");
        }
        Button Close = dialog.findViewById(R.id.CloseIMEIBtn);
        
        IMEI.setText(getIMEI());
        
        Close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
        dialog.show();
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                
                
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    
                    
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                
                }
                
            }
            break;
            
            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    
                    IMEIDialog();
                    
                    
                } else {
                    
                    Toast.makeText(AppContextProvider.getContext(), "Unable to get IMEI. Phone State Permission not Granted", Toast.LENGTH_LONG).show();
                    
                }
                
                
            }
            break;
            
            case 3: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new SyncData(Login.this).SignIn(new Loader(), User.getText().toString(),
                                    Password.getText().toString(), getIMEI());
                            
                        }
                    });
                    
                    
                } else {
                    
                    Toast.makeText(AppContextProvider.getContext(), "Unable to continue. Phone State Permission not Granted", Toast.LENGTH_LONG).show();
                    
                }
                
                
            }
            break;
            case 101: {
                
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) granted = false;
                
            }
            break;
            
            case 201: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                }
            }
            break;
            
            case 301: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                }
            }
            break;
            
            case 401: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED || grantResults[2] == PackageManager.PERMISSION_DENIED || grantResults[3] == PackageManager.PERMISSION_DENIED) {
                    granted = false;
                }
            }
            break;
            
        }
    }
    
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            
            granted = false;
            List<String> permissions = new ArrayList<>();
            
            int p1 = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.READ_PHONE_STATE);
            if (p1 < 0) permissions.add(Manifest.permission.READ_PHONE_STATE);
            int p2 = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (p2 < 0) permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

          if(! BuildConfig.FLAVOR.equalsIgnoreCase("lorenzo_industries")) {
              int p3 = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
              if (p3 < 0) permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
              int p4 = ContextCompat.checkSelfPermission(Login.this, Manifest.permission.CAMERA);
              if (p4 < 0) permissions.add(Manifest.permission.CAMERA);
          }
            
            String[] p = permissions.toArray(new String[permissions.size()]);
            if (p.length > 0) {
                ActivityCompat.requestPermissions(Login.this,
                        p,
                        Integer.parseInt(p.length + "01"));
            } else granted = true;
            
        }
    }
    
    
    private class Sync extends AsyncTask<String, String, String> {
        
        @Override
        protected String doInBackground(String... strings) {
            
            new SyncData(Login.this).SignIn(loader, strings[0], strings[1], getIMEI());
            
            return null;
        }
    }
}
