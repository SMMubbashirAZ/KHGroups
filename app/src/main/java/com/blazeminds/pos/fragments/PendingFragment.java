package com.blazeminds.pos.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.blazeminds.AppContextProvider;
import com.blazeminds.pos.PosDB;
import com.blazeminds.pos.R;
import com.blazeminds.pos.RoutePlanAdapter;
import com.blazeminds.pos.RoutePlanHeaderAdapter;
import com.blazeminds.pos.adapter.MyListener;
import com.blazeminds.pos.adapter.RoutePlanRecylerviewAdapter;
import com.blazeminds.pos.resources.UserSettings;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;
import static com.blazeminds.pos.Constant.customToast;
import static com.blazeminds.pos.MainActivity.FRAGMENT_TAG;
import static com.blazeminds.pos.MainActivity.trackCount;

public class PendingFragment extends Fragment  implements MyListener {

    public final static String TAG = PendingFragment.class.getSimpleName();

    static public boolean hideLabel = false;
    public TableRow aboutTxtTableRow;
    SwipeActionAdapter adapter2;
    TextView tv;
    boolean checkItemClicked = true;
    private ArrayList<HashMap<String, String>> data;
    private int pend_count=0,prod_count=0,un_prod_count=0;
    private ArrayList<String> dataCustId;
    private ArrayList<HashMap<String, String>> list;
    private ArrayList<HashMap<String, String>> headerlist;
    private TextView noItemTxt;
    private String  empTimeIn;
    private int enableAttendanceMust;
    private SwipeMenuListView subListView;
    private ListView headerListView;
    private RecyclerView recyclerView;
    private Context c;
    public PendingFragment() {
    }

    public static PendingFragment newInstance() {
        return new PendingFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_pending, container, false);

        initUI(rootView);
        return rootView;
    }
    private void initUI(View rootView) {


        LinearLayout lay = rootView.findViewById(R.id.routePlanLayout);

        // load the animation
        //	Animation enter = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_from_left);

        //	lay.startAnimation(enter);

        c = getActivity();
        list = new ArrayList<>();
//        headerlist = new ArrayList<>();

//        subListView = rootView.findViewById(R.id.subItemsListView);
//        headerListView = rootView.findViewById(R.id.headerListView);
        recyclerView = rootView.findViewById(R.id.pending_rv);
        noItemTxt = rootView.findViewById(R.id.NoItemTxt);
        aboutTxtTableRow = rootView.findViewById(R.id.aboutTxtTableRow);
        if (hideLabel) {
            aboutTxtTableRow.setVisibility(View.GONE);
        }
//        headerlist.clear();
//
//        HashMap<String, String> temp = new HashMap<>();
//        //temp.put(CODE_COLUMN,"Code");
//        //temp.put(CODE_PICTXT,"Picture");
//        temp.put(FIRST_COLUMN, "Shop Name");
//        temp.put(SECOND_COLUMN, "Person");
//        temp.put(THIRD_COLUMN, "Route");
//        temp.put(FOURTH_COLUMN, "View");
//        headerlist.add(temp);
//
//
//        RoutePlanHeaderAdapter adapter1 = new RoutePlanHeaderAdapter(getActivity(), headerlist);
//        headerListView.setAdapter(adapter1);

        populateList(noItemTxt);


    }

    private ArrayList<HashMap<String, String>> populateList(TextView noItemText) {

        data = new ArrayList<>();
        dataCustId = new ArrayList<>();
        dataCustId.clear();
        data.clear();

        final PosDB db = PosDB.getInstance(c);

        db.OpenDb();
        int savedRoute = db.getSavedRouteID();
        if (savedRoute == 0) {
            dataCustId = db.getSelectedCustomerID();
            data = db.getCustomerListForRoutePlan( UserSettings.PENDING);
        } else {
            dataCustId = db.getSelectedCustomerIDByRoute(savedRoute);
            data = db.getCustomerListForRoutePlanByRoute(savedRoute, UserSettings.PENDING);

        }
        db.CloseDb();

        pend_count=data.size();
        un_prod_count=data.size();
        prod_count=data.size();

        if (data.size() > 0) {
            noItemText.setVisibility(View.GONE);
            final RoutePlanAdapter adapter1 = new RoutePlanAdapter(c, data, this);
            final RoutePlanRecylerviewAdapter recylerviewAdapter=new RoutePlanRecylerviewAdapter(c, data);
//			 adapter2 = new SwipeActionAdapter(adapter1);
//			adapter2.setListView(subListView);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.addItemDecoration(new DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.divider)));
            // Set adapter to recyclerView
            recyclerView.setAdapter(recylerviewAdapter);
//            subListView.setAdapter(adapter1);
//            SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//                @Override
//                public void create(SwipeMenu menu) {
//                    // create "open" item
//                    SwipeMenuItem openItem = new SwipeMenuItem(
//                            getActivity());
//                    // set item background
//                    openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
//                            0xCE)));
//                    // set item width
//                    openItem.setWidth(dp2px(90));
//                    openItem.setBackground(R.color.light_red);
//                    // set item title
//                    openItem.setTitle("Shop Visit");
//                    // set item title fontsize
//                    openItem.setTitleSize(18);
//                    // set item title font color
//                    openItem.setTitleColor(Color.WHITE);
//                    // add to menu
//                    menu.addMenuItem(openItem);
//
//                    // create "delete" item
//                    SwipeMenuItem deleteItem = new SwipeMenuItem(
//                            getActivity());
//                    // set item background
//                    deleteItem.setBackground(R.color.bg_screen2);
//                    // set item width
//                    deleteItem.setWidth(dp2px(90));
//                    // set a icon
//                    deleteItem.setTitle("Place Order");
//                    // set item title fontsize
//                    deleteItem.setTitleSize(18);
//                    // set item title font color
//                    deleteItem.setTitleColor(Color.WHITE);
//                    // add to menu
//                    menu.addMenuItem(deleteItem);
//                }
//            };
//
//// set creator
//
//            subListView.setMenuCreator(creator);


//			subListView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
//
//				@Override
//				public void onSwipeStart(int position) {
//					subListView.smoothOpenMenu(position);
//				}
//
//				@Override
//				public void onSwipeEnd(int position) {
//
//				}
//			});
//            subListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
//                    switch (index) {
//                        case 0:
//                            db.OpenDb();
//                            enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");
//                            empTimeIn = db.getMobEmpTimeIn();
//                            db.CloseDb();
//                            if (enableAttendanceMust == 1){
//
//                                if (empTimeIn.equalsIgnoreCase("1")){
//
//                                    getActivity().getSupportFragmentManager()
//                                            .beginTransaction()
//                                            .replace(R.id.content_frame,
//                                                    ShopsVisit.newInstanceSwipe(data.get(position).get("id")),
//                                                    ShopsVisit.TAG).addToBackStack("RoutePlan").commit();
//                                    FRAGMENT_TAG = ShopsVisit.TAG;
//                                    trackCount = 2;
//                                }  else {
//                                    //Toast.makeText(context, "Can't Create Sale Return, Time In first to Create Sale Return", Toast.LENGTH_LONG).show();
//                                    customToast(AppContextProvider.getContext(), "Can't Create Shop Visit, Time In first to Create Shop Visit");
//                                }
//                            }else{
//                                getActivity().getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .replace(R.id.content_frame,
//                                                ShopsVisit.newInstanceSwipe(data.get(position).get("id")),
//                                                ShopsVisit.TAG).addToBackStack("RoutePlan").commit();
//                                FRAGMENT_TAG = ShopsVisit.TAG;
//                                trackCount = 2;
//                            }
////							dataCustId.get(position);
//
//                            break;
//                        case 1:
//                            db.OpenDb();
//                            enableAttendanceMust = db.getAppSettingsValueByKey("attendance_must");
//                            empTimeIn = db.getMobEmpTimeIn();
//                            db.CloseDb();
//                            if (enableAttendanceMust == 1){
//                                if (empTimeIn.equalsIgnoreCase("1")){
//                                    getActivity().getSupportFragmentManager()
//                                            .beginTransaction()
//                                            .replace(R.id.content_frame,
//                                                    SaleOrderFinal.newInstanceSwipe(data.get(position).get("id")),
//                                                    SaleOrderFinal.TAG).addToBackStack("RoutePlan").commit();
//                                    FRAGMENT_TAG = SaleOrderFinal.TAG;
//                                } else {
//                                    //Toast.makeText(context, "Can't Create Sale Order, Time In first to Create Order", Toast.LENGTH_LONG).show();
//                                    customToast(AppContextProvider.getContext(), "Can't Create Sale Order, Time In first to Create Order");
//                                }
//                            }else{
//                                getActivity().getSupportFragmentManager()
//                                        .beginTransaction()
//                                        .replace(R.id.content_frame,
//                                                SaleOrderFinal.newInstanceSwipe(data.get(position).get("id")),
//                                                SaleOrderFinal.TAG).addToBackStack("RoutePlan").commit();
//                                FRAGMENT_TAG = SaleOrderFinal.TAG;
//                            }
//
//                            break;
//                    }
//                    // false : close the menu; true : not close the menu
//                    return false;
//                }
//            });
//			adapter2
//					.addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left_far)
//
//					.addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right_far);
//				;
//
//
//			adapter2.setSwipeActionListener(new SwipeActionAdapter.SwipeActionListener(){
//				@Override
//				public boolean hasActions(int position, SwipeDirection direction){
//					if(direction.isLeft()) return true; // Change this to false to disable left swipes
//					if(direction.isRight()) return true;
//					return false;
//				}
//
//				@Override
//				public boolean shouldDismiss(int position, SwipeDirection direction){
//					// Only dismiss an item when swiping normal left
//					return false;
//				}
//
//				@Override
//				public void onSwipe(int[] positionList, SwipeDirection[] directionList){
//				if(directionList[0].isLeft())
//				{
////					getActivity().	getSupportFragmentManager()
////							.beginTransaction()
////							.replace(R.id.content_frame,
////									ShopsVisit.newInstanceSwipe(dataCustId.get(positionList[0])),
////									ShopsVisit.TAG).addToBackStack("RoutePlan").commit();
////					FRAGMENT_TAG = ShopsVisit.TAG;
////					trackCount=2;
//				}
//				else if(directionList[0].isRight())
//				{
//					//adapter1.getItem(positionList[0]);
//
////				getActivity().	getSupportFragmentManager()
////							.beginTransaction()
////							.replace(R.id.content_frame,
////									SaleOrderFinal.newInstanceSwipe(dataCustId.get(positionList[0])),
////									SaleOrderFinal.TAG).addToBackStack("RoutePlan").commit();
////					FRAGMENT_TAG = SaleOrderFinal.TAG;
////
//
//
//
//				}
//				}
//
//
//			});

        } else {
            noItemText.setVisibility(View.VISIBLE);
            //Toast.makeText(c, "No Data", Toast.LENGTH_SHORT).show();
        }


        return data;

    }

    public int dp2px(float dips) {

        return (int) (dips * getActivity().getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void TriggerOnItemClick(int position) {
        if (checkItemClicked) {
            subListView.smoothOpenMenu(position);
            checkItemClicked = false;
        } else {
            subListView.smoothCloseMenu();
            checkItemClicked = true;
        }
    }

}