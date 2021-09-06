package com.blazeminds.pos.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blazeminds.pos.R;

public class About extends Fragment {
	
	public final static String TAG = About.class.getSimpleName();
	TextView aboutSoftware, aboutCompany, aboutCompanyLocation, aboutCompanyPhone;
	ImageView skype, twitter, fb;
	
	public About() {
	}
	
	public static About newInstance() {
		return new About();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		
		LinearLayout lay = rootView.findViewById(R.id.AboutFrag);
		
		// load the animation
		Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);
		lay.startAnimation(enter);
		
		lay.setVisibility(View.VISIBLE);
		
		
		aboutSoftware = rootView.findViewById(R.id.aboutpos1Txt);
		aboutCompany = rootView.findViewById(R.id.aboutpos2Txt);
		aboutCompanyLocation = rootView.findViewById(R.id.locationTxt);
		aboutCompanyPhone = rootView.findViewById(R.id.PhoneTxt);
		
		skype = rootView.findViewById(R.id.skypelogo);
		twitter = rootView.findViewById(R.id.twitterlogo);
		fb = rootView.findViewById(R.id.fblogo);
		
		
		AboutPos();
		
		
		return rootView;
		
	}
	
	public void AboutPos() {
		
		
		String aboutpos1Txt = "Blaze Minds Point of Sale is a system that replaces a retailer's cash register, tracks its inventory," +
									  "sales, and customer information, and provides reports for managing its business and serving its customers. <br></br><br></br>" +
				
									  "<b>Boost your bottom line. Guaranteed.</b><br></br>" +
									  "View your business at a glance. Inventory, sales and customer data is at your fingertips. Work more efficiently. Adjust prices," +
									  "discounts and transfer data to Point of Sales with a click. Make your customers happier. Track key information to stay in touch" +
									  " and provide better service. <br></br><br></br>" +
				
									  " <b>Track Inventory with Less Effort and More Profit</b><br></br>" +
									  " Point of Sale Pro automatically adjusts your inventory every time you make a sale, order, or return - which helps you keep the most" +
									  "popular items on-hand without overstocking or selling out.<br></br><br></br>" +
				
									  "<b>Make Your Customer Happier </b><br></br>" +
									  "Track customer contact info and purchase histories automatically. It's easy to stay in touch and use customer loyalty tools to keep" +
									  "them coming back for more.<br></br><br></br>" +
				
									  "<b>Work More Efficiently</b><br></br>" +
									  "Point of Sale makes the things you do everyday, from ringing sales to bookkeeping, fast and easy. <br></br><br></br>" +
				
									  "<b>Advanced Tools in Pro and Multi-Store</b><br></br>" +
									  "Point of Sale Pro and Multi-Store contain additional tools not found in Basic to help you manage more aspects of your store and" +
									  "tailor everything to your specific needs. <br></br><br></br>" +
				
									  "<b>Save Time and Money Accepting Credit Cards</b><br></br>" +
									  "With integrated merchant service from Blaze Minds, there's no double-entry between your terminal and software and you don't need" +
									  "to buy or lease a separate terminal to take credit and debit cards.<br></br><br></br>" +
				
									  "<b>Get the Hardware You Need for a Complete System</b><br></br>" +
									  "With Point of Sale, you can choose from a complete solution with hardware and PC or build your own custom solution with hardware" +
									  "that is guaranteed to work seamlessly with your software.<br></br><br></br>";
		
		aboutSoftware.setText(Html.fromHtml(aboutpos1Txt));
		
		
		String aboutpos2Txt = "<div style='text-align:justify;'>Blaze Minds is an IT company, established in the year 2010. The aim of our company is " +
									  "to provide quality services to our customers and present them with the satisfaction they want in their work.<br></br><br></br>" +
									  "We provide services to our customers in the fields of Web Development, Graphic Designing, Search Engine Optimization, Social Marketing, " +
									  "Domain names, Web Hosting as well as Software Designing.<br></br><br></br>" +
				
									  "Due to the rapid evolution in the field of information technology, the competition has expanded and it has become crucial for all organizations, " +
									  "especially business firms, to increase efficiency as well as incorporate all possible information and web solutions to develop their business.<br></br><br></br>" +
				
									  "In order to help such organizations in achieving the goals, we have put together strong management with a consortium of highly qualified, experienced " +
									  "and skilled professionals. We here at Blaze Minds, take care of all information technology related issues. We make sure that you get the services and satisfaction " +
									  "you want at reasonable rates.<br></br><br></br></div>";
		
		String location = "<b> LOCATION </b><br></br>" +
								  "Address: <br></br>" +
								  "Shahnaz Arcade 1,<br></br>" +
								  "Behind Chase-Up Mart Bahadurabad,<br></br>" +
								  "Karachi, Pakistan.<br></br><br></br>";
		
		String contact = "<b> CONTACTS </b><br></br>" +
								 "Phone : +92 21 34522132 <br></br>" +
								 "Cell : +92 321 3880558<br></br>";
		// "Email : support@blazeminds.com<br></br>"+
		//"Skype : blazeminds";+
		
		
		aboutCompany.setText(Html.fromHtml(aboutpos2Txt));
		aboutCompanyLocation.setText(Html.fromHtml(location));
		aboutCompanyPhone.setText(Html.fromHtml(contact));
		
		
	}
	
	
}
