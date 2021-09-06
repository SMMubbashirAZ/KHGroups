package com.blazeminds.pos.utils;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.fxn.cue.Cue;
import com.fxn.cue.enums.Duration;
import com.fxn.cue.enums.Type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Helper {

    public static void showToast(String message, String type, Activity activity) {
        if (type.equalsIgnoreCase("TOAST_SUCCESS")) {
            Cue.init().with(activity)
                    .setMessage(message)
                    .setType(Type.SUCCESS)
                    .setGravity(Gravity.BOTTOM)
                    .setPadding(20)
                    .setDuration(Duration.LONG)
                    .show();
        } else if (type.equalsIgnoreCase("TOAST_ERROR")) {
            vibrate(activity);
            Cue.init().with(activity)
                    .setMessage(message)
                    .setType(Type.DANGER)
                    .setGravity(Gravity.BOTTOM)
                    .setPadding(20)
                    .setDuration(Duration.LONG)
                    .show();
        } else {
            Cue.init().with(activity)
                    .setMessage(message)
                    .setType(Type.DARK)
                    .setGravity(Gravity.BOTTOM)
                    .setPadding(20)
                    .setDuration(Duration.LONG)
                    .show();
        }
    }


    public static String getFormattedDate(String mDate) {

        if(mDate==null){
            return "Not Available";
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat targetFormat = new SimpleDateFormat("dd MMMM, yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return mDate;
        }
        return targetFormat.format(date);
    }

    public static String getFormattedTime(String mDate) {

        if(mDate==null){
            return "Not Available";
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        DateFormat targetFormat = new SimpleDateFormat("h:mm a");
        Date date = null;
        try {
            date = originalFormat.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return mDate;
        }
        return targetFormat.format(date);
    }

    public static String getSinceDate(String mDate) {

        if(mDate==null){
            return "Not Available";
        }
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat targetFormat = new SimpleDateFormat("MMMM yyyy");
        Date date = null;
        try {
            date = originalFormat.parse(mDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return mDate;
        }
        return targetFormat.format(date);
    }

    /**
     * @param v : view object which you want to expand
     *          <p>
     *          to expand the view animation
     */
    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    /**
     * @param v : view object which you want to expand
     *          <p>
     *          to collpase the current view
     */

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
//        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(100);
        v.startAnimation(a);
    }

    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(200);
            }
        }
    }

    public boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}