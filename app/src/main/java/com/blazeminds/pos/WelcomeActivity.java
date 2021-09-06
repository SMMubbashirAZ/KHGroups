package com.blazeminds.pos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.security.ProviderInstaller;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends AppCompatActivity {
    
    Handler handler = new Handler();
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private TextView textView;
    private TextView login;
    private PrefManager prefManager;
    private int count = 0;
    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            count = position;
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        
        }
        
        @Override
        public void onPageScrollStateChanged(int arg0) {
        
        }
        
    };
    private Runnable update = new Runnable() {
        public void run() {
            addBottomDots(count);
            count++;
        }
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkTls();
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        PosDB posDB = PosDB.getInstance(WelcomeActivity.this);
        if (!posDB.getMobUser().equals("")) {
            prefManager.setFirstTimeLaunch(false);
            
        }
        
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        
        setContentView(R.layout.activity_welcome);
        
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        login = findViewById(R.id.login);
        
        
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};
        
        // adding bottom dots
        textView = findViewById(R.id.text);
        
        addBottomDots(0);
        // making notification bar transparent
        changeStatusBarColor();
        
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        
        
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });
        
        
        new Timer().schedule(new TimerTask() {
            
            @Override
            public void run() {
                handler.post(update);
            }
        }, 1000, 3000);
    }
    
    private void addBottomDots(int currentPage) {
        
        if (currentPage >= 4) {
            currentPage = 0;
            count = 0;
        }
        switch (currentPage) {
            
            case 0:
                textView.setText("Order Booking");
                break;
            case 1:
                textView.setText("Stock Management");
                break;
            case 2:
                textView.setText("Delivery & Payments");
                break;
            case 3:
                textView.setText("Sales Analyzer");
                break;
            
        }
        
        dots = new TextView[layouts.length];
        
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        viewPager.setCurrentItem(currentPage);
        
        viewPager.startLayoutAnimation();
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }
    
    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }
    
    private void launchHomeScreen() {
        
        startActivity(new Intent(WelcomeActivity.this, Login.class));
        finish();
    }
    
    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    
    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        
        public MyViewPagerAdapter() {
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            
            return view;
        }
        
        @Override
        public int getCount() {
            return layouts.length;
        }
        
        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
        
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
    private void checkTls() {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            try {
                ProviderInstaller.installIfNeededAsync(this, new ProviderInstaller.ProviderInstallListener() {
                    @Override
                    public void onProviderInstalled() {
                    }

                    @Override
                    public void onProviderInstallFailed(int i, Intent intent) {
                    }
                });
            } catch (Exception e) {
                finish();
                e.printStackTrace();
            }
        }
    }
}
