package com.blazeminds.pos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.blazeminds.pos.autocomplete_resource.MyObject;
import com.blazeminds.pos.fragments.SalesOrder;
import com.blazeminds.pos.fragments.SalesReturn;
import com.blazeminds.pos.model.ProductModel;
import com.blazeminds.pos.resources.UserSettings;

import java.sql.Date;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.blazeminds.pos.Constant.AMOUNT_RECIEVED_COLUMN;
import static com.blazeminds.pos.Constant.CID_COLUMN;
import static com.blazeminds.pos.Constant.CODE_COLUMN;
import static com.blazeminds.pos.Constant.DATE_COLUMN;
import static com.blazeminds.pos.Constant.DELETE_ITEM_COLUMN;
import static com.blazeminds.pos.Constant.DISCOUNT_COLUMN;
import static com.blazeminds.pos.Constant.EID_COLUMN;
import static com.blazeminds.pos.Constant.EXE_COMPLETE_COLUMN;
import static com.blazeminds.pos.Constant.EXE_DATE_COLUMN;
import static com.blazeminds.pos.Constant.FIFTH_COLUMN;
import static com.blazeminds.pos.Constant.FIRST_COLUMN;
import static com.blazeminds.pos.Constant.FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.LATITUDE_COLUMN;
import static com.blazeminds.pos.Constant.LONGITUDE_COLUMN;
import static com.blazeminds.pos.Constant.NET_OID_COLUMN;
import static com.blazeminds.pos.Constant.NOTES_COLUMN;
import static com.blazeminds.pos.Constant.OID_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_CONFIRM_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_CUST_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_DATE_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_EXE_COMPLETE;
import static com.blazeminds.pos.Constant.ORDER_ID_COLUMN;
import static com.blazeminds.pos.Constant.ORDER_NEW_AMOUNT_COLUMN;
import static com.blazeminds.pos.Constant.PAYMENT_TYPE_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_FIFTH_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_FIRST_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_FOURTH_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_SECOND_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_SIXTH_COLUMN;
import static com.blazeminds.pos.Constant.REPORT_THIRD_COLUMN;
import static com.blazeminds.pos.Constant.SECOND_COLUMN;
import static com.blazeminds.pos.Constant.SELECTED_DISTRIBUTOR_COLUMN;
import static com.blazeminds.pos.Constant.SRCID_COLUMN;
import static com.blazeminds.pos.Constant.SRDATE_COLUMN;
import static com.blazeminds.pos.Constant.SRDELETE_ITEM_COLUMN;
import static com.blazeminds.pos.Constant.SRDISCOUNT_COLUMN;
import static com.blazeminds.pos.Constant.SREID_COLUMN;
import static com.blazeminds.pos.Constant.SRID_COLUMN;
import static com.blazeminds.pos.Constant.SRLATITUDE_COLUMN;
import static com.blazeminds.pos.Constant.SRLONGITUDE_COLUMN;
import static com.blazeminds.pos.Constant.SRNET_OID_COLUMN;
import static com.blazeminds.pos.Constant.SRNOTES_COLUMN;
import static com.blazeminds.pos.Constant.SRRETURN_REASON_COLUMN;
import static com.blazeminds.pos.Constant.SRSELECTED_DISTRIBUTOR_COLUMN;
import static com.blazeminds.pos.Constant.SRSTART_DATE_COLUMN;
import static com.blazeminds.pos.Constant.SRTOTAL2_COLUMN;
import static com.blazeminds.pos.Constant.SRTOTAL_COLUMN;
import static com.blazeminds.pos.Constant.SRVAL_COLUMN;
import static com.blazeminds.pos.Constant.START_DATE_COLUMN;
import static com.blazeminds.pos.Constant.THIRD_COLUMN;
import static com.blazeminds.pos.Constant.TOTAL2_COLUMN;
import static com.blazeminds.pos.Constant.TOTALEXE_COLUMN;
import static com.blazeminds.pos.Constant.TOTAL_COLUMN;
import static com.blazeminds.pos.Constant.VAL_COLUMN;
import static com.blazeminds.pos.Constant.getDateTimeSHORT;
import static com.blazeminds.pos.ConstantRoute.AreaName;
import static com.blazeminds.pos.ConstantRoute.Name;
import static com.blazeminds.pos.ConstantRoute.RouteID;
import static com.blazeminds.pos.ConstantRoute.Visits;
import static com.blazeminds.pos.ConstantRoute.VisitsDone;

/**
 * Created by blazeminds on 2/3/2015.
 */


public class PosDB {
    
    private static final String KEY_FNAME_REPORT = "fname";
    private static final String KEY_FNAME_VALID = "name";
    private static final String KEY_BRAND_NAME = "name";
    private static final String KEY_ITEM_NAME = "name";
    //Customer Details Column Names NT WORKING
    private static final String KEY_ID_CUSTOMER = "id";
    private static final String KEY_CUSTOMER_FNAME = "fname";
    private static final String KEY_CUSTOMER_LNAME = "lname";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_CUSTOMER_CELL = "cell";
    private static final String KEY_CUSTOMER_PHONE1 = "phone";
    private static final String KEY_CUSTOMER_PHONE2 = "phone2";
    private static final String KEY_CUSTOMER_ADDRESS = "address";
    private static final String KEY_CUSTOMER_CITY = "city";
    private static final String KEY_CUSTOMER_STATE = "state";
    private static final String KEY_CUSTOMER_COUNTRY = "country";
    private static final String KEY_CUSTOMER_AREA = "area";
    private static final String KEY_CUSTOMER_CNIC = "cnic";
    private static final String KEY_CUSTOMER_FILER_NON_FILER = "filer_non_filer";
    private static final String KEY_CUSTOMER_SHOP_CATEGORY_ID = "shop_cat_id";
    private static final String KEY_CUSTOMER_SUB_SHOP_CATEGORY_ID = "sub_shop_cat_id";
    private static final String KEY_CHKNET_TIME = "time";
    private static final String KEY_CHKNET_STATUS = "status";
    private static final String KEY_PHONESTART_TIME = "time";
    // Database Version
    private static final int DATABASE_VERSION = 20;
    // Database Name
    private static final String DATABASE_NAME = "pos";
    // Table Fields Name
    private static final String DATABASE_TABLE_PATIENT_ORDER="patient_order";
    private static final String DATABASE_TABLE_PATIENT_ORDER2="patient_order2";
    private static final String DATABASE_TABLE_PATIENT_ORDER_DETAIL ="patient_order_detail";
    private static final String DATABASE_TABLE_PATIENT_ORDER_DETAIL2 ="patient_order_detail2";
    private static final String DATABASE_TABLE_CUSTOMER_BRAND= "customer_brand";
    public static final String DATABASE_TABLE_INVENTORY = "inventory";
//    public static final String DATABASE_TABLE_TEMP_CART = "temp_cart";
    private static final String DATABASE_TABLE_BRAND = "product_brands";
    private static final String DATABASE_TABLE_ITEM_TYPE = "product_type";
    private static final String DATABASE_TABLE_ITEM_SUB_TYPE = "product_sub_type";
    private static final String DATABASE_TABLE_CUSTOMER_DETAILS = "customer";
    private static final String DATABASE_TABLE_SETTINGS = "settings";
    private static final String DATABASE_TABLE_CLOCKIN = "clock_in";
    private static final String DATABASE_TABLE_SALES = "sales";
    private static final String DATABASE_TABLE_SALES_DETAILS = "sales_details";
    private static final String Database_Table_mUser = "mob_user";
    private static final String Database_Table_Cart = "cart";
    private static final String DATABASE_TABLE_OFFLINE_TRACKING = "offline_tracking";
    private static final String DATABASE_TABLE_SALESMAN_SETTINGS = "salesman_settings";
    private static final String DATABASE_TABLE_SALESMAN_SALES_ORDER = "sales_order";
    private static final String DATABASE_TABLE_SALESMAN_SALES_RETURN = "sales_return";
    private static final String DATABASE_TABLE_ORDER_DETAILS = "order_details";
    private static final String DATABASE_TABLE_RETURN_DETAILS = "return_details";
    private static final String DATABASE_TABLE_CUSTOMER_ROUTE = "cust_route";
    private static final String Database_Table_Route = "route";
    private static final String Database_Table_Today_Route = "today_route";
    private static final String Database_Table_ReportTo = "report";
    private static final String Database_Table_VALID_REPORT = "valid_report";
    private static final String Database_Table_CHKNET = "check_net";
    private static final String Database_Table_PHONESTART = "phone_start";
    private static final String Database_Table_Return_Reason = "return_reason";
    private static final String DATABASE_TABLE_CUSTOMER_TYPE = "cust_type";
    private static final String DATABASE_TABLE_CUSTOMER_PRICING = "cust_pricing";
    private static final String DATABASE_TABLE_PAYMENT_RECIEVED = "payment_recieved";
    private static final String DATABASE_TABLE_CUSTOMER_CATEGORY = "customer_category";
    private static final String DATABASE_TABLE_CUSTOMER_CELEB = "customer_celeb";
    private static final String DATABASE_TABLE_EXPENSE_TYPE = "expense_type";
    
    //    private static final String DATABASE_TABLE_OFFICE = "Office";
    private static final String DATABASE_TABLE_EXPENSE_STATUS = "expense_status";
    private static final String DATABASE_TABLE_NO_REASON = "no_reason";
    private static final String DATABASE_TABLE_EXPENSE = "expense";
    private static final String DATABASE_TABLE_SHOP_VISIT = "shop_visit";
    private static final String DATABASE_TABLE_CLOCKIN_TIME = "clock_in_time";
    private static final String DATABASE_TABLE_MERCHANDIZING = "merchandizing";
    private static final String DATABASE_TABLE_MERCHANDIZING_PLAN = "merchandizing_plan";
    private static final String DATABASE_TABLE_TOWN = "town";
    private static final String DATABASE_TABLE_TOWN_TRAVEL = "town_travel";
    private static final String DATABASE_TABLE_TRAVEL_EXPENSE = "travel_expense";
    private static final String DATABASE_TABLE_TARGET = "target";
    private static final String DATABASE_TABLE_SUPPORT = "support";
    private static final String DATABASE_TABLE_SUPPORT_DETAIL = "support_detail";
    private static final String DATABASE_TABLE_SUPPORT_STATUS = "support_status";
    private static final String DATABASE_TABLE_COMMITMENT = "commitment";
    private static final String DATABASE_TABLE_TOTAL_DISCOUNT = "total_discount";
    private static final String DATABASE_TABLE_APP_SETTINGS = "app_settings";
    private static final String DATABASE_TABLE_SHOP_STOCK = "shop_stock";
    private static final String DATABASE_TABLE_ITEM_TARGET = "item_target";
    private static final String DATABASE_TABLE_DISTRIBUTOR_LIST = "dist_list";
    private static final String DATABASE_TABLE_SHOP_CATEGORY = "shop_category";
    private static final String DATABASE_TABLE_ORDER_TEMPLATE= "order_template";
    private static final String DATABASE_TABLE_SUB_SHOP_CATEGORY = "sub_shop_category";
    //Patient Order
    private static final String KEY_PATIENT_ORDER_ID="patient_order_id";
    private static final String KEY_PATIENT_ORDER_EMP_ID="emp_id";
    private static final String KEY_PATIENT_ORDER_CUST_ID="cust_id";
    private static final String KEY_PATIENT_ORDER_DIST_ID="dist_id";
    private static final String   KEY_PATIENT_ORDER_DATE_TIME ="patient_order_datetime";
    private static final String KEY_PATIENT_ORDER_PATIENT_NAME="patient_order_patient_name";
    private static final String KEY_PATIENT_ORDER_ORDER_UPDATE="update_order";


    //TEMP CART

    private static final String KEY_TEMP_CART_PROD_ID = "prod_id";
    private static final String KEY_TEMP_CART_BRAND_NAME = "brand_id";
    private static final String KEY_TEMP_CART_PROD_NAME = "prod_name";
    private static final String KEY_TEMP_CART_PROD_PRICE = "prod_price";
    private static final String KEY_TEMP_CART_PROD_UNIT = "prod_unit";
    private static final String KEY_TEMP_CART_PROD_DISCOUNT = "prod_discount";

    //CUSTOMER BRAND
    private static final String KEY_CUSTOMER_BRAND_ID="id";
    private static final String KEY_CUSTOMER_BRAND_BRAND_ID ="brand_id";
    private static final String KEY_CUSTOMER_BRAND_CUSTOMER_ID="customer_id";
    //Patient Order
    private static final String KEY_PATIENT_ORDER_DETAIL_ID="id";
    private static final String KEY_PATIENT_ORDER_DETAIL_ORDER_ID="patient_order_id";
    private static final String KEY_PATIENT_ORDER_DETAIL_ITEM_NAME="patient_order_detail_item_name";
    private static final String KEY_PATIENT_ORDER_DETAIL_QUANTITY="patient_order_detail_quanttity";
    private static final String KEY_PATIENT_ORDER_DETAIL_RATE="patient_order_detail_rate";
    // Distrubutor List Columns
    private static final String KEY_DISTRIBUTOR_LIST_ID = "dist_id";
    private static final String KEY_DISTRIBUTOR_LIST_NAME = "dist_name";
    private static final String KEY_DISTRIBUTOR_LIST_SYNC = "dist_sync";
    private static final String KEY_DISTRIBUTOR_LIST_SAVED = "dist_saved";
    // FIELDS APP_SETTINGS
    private static final String KEY_APP_SETTINGS_ID = "app_setting_id";
    private static final String KEY_APP_SETTINGS_KEY = "app_setting_key";
    private static final String KEY_APP_SETTINGS_VALUE = "app_setting_value";
    // ClockInTime Table Coulumn Names
    private static final String KEY_CLOCKIN_TIME_ID = "id";
    private static final String KEY_CLOCKIN_TIME_CLOCKIN = "clockin";
    private static final String KEY_CLOCKIN_TIME_CLOCKOUT = "clockout";
    // Merchandizing Table Coulumn Names
    private static final String KEY_MERCHANDIZING_ID = "m_id";
    private static final String KEY_MERCHANDIZING_SHOP_ID = "m_shop_id";
    private static final String KEY_MERCHANDIZING_BRAND_ID = "m_brand_id";
    private static final String KEY_MERCHANDIZING_CAMPAIGN_ID = "m_campaign_id";
    private static final String KEY_MERCHANDIZING_PRODUCT_ID = "m_product_id";
    private static final String KEY_MERCHANDIZING_DATETIME = "m_datetime";
    private static final String KEY_MERCHANDIZING_REMARKS = "m_remarks";
    // MerchandizingPlan Table Coulumn Names
    private static final String KEY_MERCHANDIZING_PLAN_ID = "m_plan_id";
    private static final String KEY_MERCHANDIZING_PLAN_NAME = "m_plan_name";
    private static final String KEY_MERCHANDIZING_PLAN_PRODUCT_1 = "m_plan_prod1";
    private static final String KEY_MERCHANDIZING_PLAN_PRODUCT_2 = "m_plan_prod2";
    private static final String KEY_MERCHANDIZING_PLAN_PRODUCT_3 = "m_plan_prod3";
    // Town Table Column Names
    private static final String KEY_TOWN_ID = "town_id";
    private static final String KEY_TOWN_NAME = "town_name";
    private static final String KEY_COMMITMENT_ID = "comm_id";
    private static final String KEY_COMMITMENT_SHOP_ID = "comm_shop_id";
    private static final String KEY_COMMITMENT_FROM_DATE = "comm_from_date";
    private static final String KEY_COMMITMENT_TO_DATE = "comm_to_date";
    private static final String KEY_COMMITMENT_SALE_AMOUNT = "comm_sale_amount";
    private static final String KEY_COMMITMENT_GIFT_AMOUNT = "comm_gift_amount";
    private static final String KEY_COMMITMENT_REMARKS = "comm_remarks";
    private static final String KEY_COMMITMENT_STATUS = "comm_status";
    private static final String KEY_COMMITMENT_DONE = "comm_done";
    private static final String KEY_COMMITMENT_DATETIME = "comm_datetime";
    private static final String KEY_COMMITMENT_UPDATE = "comm_update";
    // Town Travel Table Column Names
    private static final String KEY_TOWN_TRAVEL_ID = "town_travel_id";
    private static final String KEY_TOWN_TRAVEL_TOWN_1 = "town_travel_town_1";
    private static final String KEY_TOWN_TRAVEL_TOWN_2 = "town_travel_town_2";
    private static final String KEY_TOWN_TRAVEL_ONE_WAY = "town_travel_one_way";
    private static final String KEY_TOWN_TRAVEL_TWO_WAY = "town_travel_two_way";
    private static final String KEY_TOWN_TRAVEL_STAY_ACCOM = "town_travel_stay_accom";
    // Target Columns Names
    private static final String KEY_TARGET_ID = "target_id";
    private static final String KEY_TARGET_PRODUCT_ID = "target_prod_id";
    private static final String KEY_TARGET_QUANTITY = "target_qty";
    // Support Status Table Column Names
    private static final String KEY_SUPPORT_STATUS_ID = "id";
    private static final String KEY_SUPPORT_STATUS_NAME = "name";
    // Support Table Column Names
    private static final String KEY_SUPPORT_ID = "id";
    private static final String KEY_SUPPORT_TITLE = "title";
    private static final String KEY_SUPPORT_STATUS = "status";
    private static final String KEY_SUPPORT_DATETIME = "datetime";
    private static final String KEY_SUPPORT_NET_OID = "net_oid";
    private static final String KEY_SUPPORT_SYNC_UPDATE = "sync_update";
    // Support Detail Table Column Names
    private static final String KEY_SUPPORT_DETAIL_ID = "id";
    private static final String KEY_SUPPORT_DETAIL_SUPPORT_ID = "support_id";
    private static final String KEY_SUPPORT_DETAIL_MESSAGE = "message";
    private static final String KEY_SUPPORT_DETAIL_DATETIME = "datetime";
    private static final String KEY_SUPPORT_DETAIL_PERSON = "person";
    private static final String KEY_SUPPORT_DETAIL_SYNC_UPDATE = "sync_update";
    // Return Reason Table Column Names
    private static final String KEY_RETURN_REASON_ID = "id";
    private static final String KEY_RETURN_REASON_NAME = "name";
    // Expense TYpe Table Column Names
    private static final String KEY_EXPENSE_TYPE_ID = "id";
    private static final String KEY_EXPENSE_TYPE_NAME = "name";
    // Expense STATUS Table Column Names
    private static final String KEY_EXPENSE_STATUS_ID = "id";
    private static final String KEY_EXPENSE_STATUS_NAME = "name";
    // NO REASON Table Column Names
    private static final String KEY_NO_REASON_ID = "id";
    private static final String KEY_NO_REASON_NAME = "name";
    // Payment Recieved Table Column Names
    private static final String KEY_PAYMENT_RECIEVED_ID = "id";
    private static final String KEY_PAYMENT_RECIEVED_CUST_ID = "cid";
    private static final String KEY_PAYMENT_RECIEVED_EMP_ID = "empid";
    private static final String KEY_PAYMENT_RECIEVED_AMOUNT = "amount";
    private static final String KEY_PAYMENT_RECIEVED_START_DATETIME = "start_datetime";
    private static final String KEY_PAYMENT_RECIEVED_DATETIME = "datetime";
    private static final String KEY_PAYMENT_RECIEVED_DETAIL = "detail";
    private static final String KEY_PAYMENT_RECIEVED_UPDATE = "update_payment";
    private static final String KEY_PAYMENT_RECIEVED_LATITUDE = "latitude";
    private static final String KEY_PAYMENT_RECIEVED_LONGITUDE = "longitude";
    private static final String KEY_PAYMENT_RECIEVED_MAPNAME = "mapname";
    private static final String KEY_PAYMENT_RECIEVED_EXECUTE_COMPLETE = "exe_complete";
    private static final String KEY_PAYMENT_RECIEVED_CHEQUE_NO = "cheque_no";
    private static final String KEY_PAYMENT_RECIEVED_CHEQUE_DATE = "cheque_date";
    private static final String KEY_PAYMENT_RECIEVED_BANK_NAME = "bank_name";
    private static final String KEY_PAYMENT_RECIEVED_PAYMENT_TYPE = "payment_type";
    private static final String KEY_PAYMENT_RECIEVED_SELECTED_DISTRIBUTOR_ID = "sel_dist_id";
    // Expense Table Column Names
    private static final String KEY_EXPENSE_ID = "id";
    private static final String KEY_EXPENSE_SHOP_ID = "exp_shop_id";
    private static final String KEY_EXPENSE_COMMITMENT_ID = "exp_comm_id";
    private static final String KEY_EXPENSE_AMOUNT = "amount";
    private static final String KEY_EXPENSE_START_DATETIME = "start_datetime";
    private static final String KEY_EXPENSE_DATETIME = "datetime";
    private static final String KEY_EXPENSE_TYPE = "exp_type";
    private static final String KEY_EXPENSE_REMARKS = "remarks";
    private static final String KEY_EXPENSE_LATITUDE = "latitude";
    private static final String KEY_EXPENSE_LONGITUDE = "longitude";
    private static final String KEY_EXPENSE_MAPNAME = "mapname";
    private static final String KEY_EXPENSE_STATUS = "status";
    private static final String KEY_EXPENSE_UPDATE = "update_expense";
    // TRAVEL Expense Table Column Names
    private static final String KEY_TRAVEL_EXPENSE_ID = "t_exp_id";
    private static final String KEY_TRAVEL_EXPENSE_FROM_TOWN = "t_exp_from_town";
    private static final String KEY_TRAVEL_EXPENSE_TO_TOWN = "t_exp_to_town";
    private static final String KEY_TRAVEL_EXPENSE_FROM_DATE = "t_exp_from_date";
    private static final String KEY_TRAVEL_EXPENSE_TO_DATE = "t_exp_to_date";
    private static final String KEY_TRAVEL_EXPENSE_DAYS = "t_exp_days";
    private static final String KEY_TRAVEL_EXPENSE_TRAVELLING_AMOUNT = "t_exp_travelling_amount";
    private static final String KEY_TRAVEL_EXPENSE_STAY_COMPENSATION_AMOUNT = "t_exp_compensation_amount";
    private static final String KEY_TRAVEL_EXPENSE_AMOUNT = "t_exp_amount";
    private static final String KEY_TRAVEL_EXPENSE_REMARKS = "t_exp_remarks";
    private static final String KEY_TRAVEL_EXPENSE_START_DATETIME = "t_exp_start_datetime";
    private static final String KEY_TRAVEL_EXPENSE_DATETIME = "t_exp_datetime";
    private static final String KEY_TRAVEL_EXPENSE_LATITUDE = "t_exp_latitude";
    private static final String KEY_TRAVEL_EXPENSE_LONGITUDE = "t_exp_longitude";
    private static final String KEY_TRAVEL_EXPENSE_MAPNAME = "t_exp_mapname";
    private static final String KEY_TRAVEL_EXPENSE_STATUS = "t_exp_status";
    private static final String KEY_TRAVEL_EXPENSE_UPDATE = "t_exp_update";
    // Shop Visit Table Column Names
    private static final String KEY_SHOP_VISIT_ID = "sv_id";
    private static final String KEY_SHOP_VISIT_CUST_ID = "sv_cust_id";
    private static final String KEY_SHOP_VISIT_REASON_ID = "sv_reason_id";
    private static final String KEY_SHOP_VISIT_START_DATETIME = "sv_start_datetime";
    private static final String KEY_SHOP_VISIT_DATETIME = "sv_datetime";
    private static final String KEY_SHOP_VISIT_REMARKS = "sv_remarks";
    private static final String KEY_SHOP_VISIT_LATITUDE = "sv_latitude";
    private static final String KEY_SHOP_VISIT_LONGITUDE = "sv_longitude";
    private static final String KEY_SHOP_VISIT_MAPNAME = "sv_mapname";
    private static final String KEY_SHOP_VISIT_UPDATE = "sv_update";
    private static final String KEY_SHOP_VISIT_SELECTED_DISTRIBUTOR_ID = "sel_dist_id";
    // Order Details Column Names
    private static final String KEY_ORDER_DETAIL_ID = "id";
    private static final String KEY_ORDER_DETAIL_ORDER_ID = "order_id";
    private static final String KEY_ORDER_DETAIL_PRODUCT_ID = "product_id";
    private static final String KEY_ORDER_DETAIL_QUANTITY = "quantity";
    private static final String KEY_ORDER_DETAIL_QUANTITY_EXE = "quantity_exe";
    private static final String KEY_ORDER_DETAIL_TRADE_PRICE = "trade_price";
    private static final String KEY_ORDER_DETAIL_DISCOUNT_1 = "discount_one";
    private static final String KEY_ORDER_DETAIL_DISCOUNT_2 = "discount_two";
    private static final String KEY_ORDER_DETAIL_TRADE_OFFER = "trade_offer";
    private static final String KEY_ORDER_DETAIL_SCHEME = "scheme";
    private static final String KEY_ORDER_DETAIL_SCHEME_QTY = "scheme_qty";
    private static final String KEY_ORDER_DETAIL_SCHEME_FORMULA = "scheme_formula";
    private static final String KEY_ORDER_DETAIL_SCHEME_PRODUCT = "scheme_product";
    private static final String KEY_ORDER_DETAIL_TAX_1 = "tax_one";
    private static final String KEY_ORDER_DETAIL_TAX_2 = "tax_two";
    private static final String KEY_ORDER_DETAIL_TAX_3 = "tax_three";

    private static final String KEY_ORDER_DETAIL_TOV = "t_o_v";
    private static final String KEY_ORDER_DETAIL_DV1 = "d_v_1";
    private static final String KEY_ORDER_DETAIL_DV2 = "d_v_2";
    private static final String KEY_ORDER_DETAIL_T_TYPE = "t_type";
    private static final String KEY_ORDER_DETAIL_T_MRP_TYPE = "t_mrp_type";
    private static final String KEY_ORDER_DETAIL_T_VAL = "t_val";
    private static final String KEY_ORDER_DETAIL_MRP_PRICE = "mrp_price";
    private static final String KEY_ORDER_DETAIL_SUBTOTAL = "sub_total";
    // Return Details Column Names
    private static final String KEY_RETURN_DETAIL_ID = "id";
    private static final String KEY_RETURN_DETAIL_ORDER_ID = "order_id";
    private static final String KEY_RETURN_DETAIL_PRODUCT_ID = "product_id";
    private static final String KEY_RETURN_DETAIL_QUANTITY = "quantity";
    private static final String KEY_RETURN_DETAIL_QUANTITY_EXE = "quantity_exe";
    private static final String KEY_RETURN_DETAIL_TRADE_PRICE = "trade_price";
    private static final String KEY_RETURN_DETAIL_DISCOUNT_1 = "discount_one";
    private static final String KEY_RETURN_DETAIL_DISCOUNT_2 = "discount_two";
    private static final String KEY_RETURN_DETAIL_TRADE_OFFER = "trade_offer";
    private static final String KEY_RETURN_DETAIL_SCHEME = "scheme";
    private static final String KEY_RETURN_DETAIL_SCHEME_QTY = "scheme_qty";
    private static final String KEY_RETURN_DETAIL_SCHEME_FORMULA = "scheme_formula";
    private static final String KEY_RETURN_DETAIL_SCHEME_PRODUCT = "scheme_product";
    private static final String KEY_RETURN_DETAIL_TAX_1 = "tax_one";
    private static final String KEY_RETURN_DETAIL_TAX_2 = "tax_two";
    private static final String KEY_RETURN_DETAIL_TAX_3 = "tax_three";

    private static final String KEY_RETURN_DETAIL_TOV = "t_o_v";
    private static final String KEY_RETURN_DETAIL_DV1 = "d_v_1";
    private static final String KEY_RETURN_DETAIL_DV2 = "d_v_2";
    private static final String KEY_RETURN_DETAIL_T_TYPE = "t_type";
    private static final String KEY_RETURN_DETAIL_T_MRP_TYPE = "t_mrp_type";
    private static final String KEY_RETURN_DETAIL_T_VAL = "t_val";
    private static final String KEY_RETURN_DETAIL_MRP_PRICE = "mrp_price";
    private static final String KEY_RETURN_DETAIL_SUBTOTAL = "sub_total";
    // Customer Type Column Names
    private static final String KEY_CUSTOMER_TYPE_ID = "cust_id";
    private static final String KEY_CUSTOMER_TYPE_NAME = "cust_name";
    // Customer Catogery Column NAmes
    private static final String KEY_CUSTOMER_CATEGORY_ID = "cust_category_id";
    private static final String KEY_CUSTOMER_CATEGORY_NAME = "cust_category_name";
    // Customer Celeb Column Names
    private static final String KEY_CUSTOMER_CELEB_ID = "cust_celeb_id";
    private static final String KEY_CUSTOMER_CELEB_NAME = "cust_celeb_name";
    // Customer Pricing Column Names
    private static final String KEY_CUSTOMER_PRICING_ID = "id";
    private static final String KEY_CUSTOMER_PRICING_TYPE_ID = "type_id";
    private static final String KEY_CUSTOMER_PRICING_PRODUCT_ID = "product_id";
    private static final String KEY_CUSTOMER_PRICING_TRADE_PRICE = "trade_price";
    private static final String KEY_CUSTOMER_PRICING_DISCOUNT_1 = "discount_1";
    private static final String KEY_CUSTOMER_PRICING_DISCOUNT_2 = "discount_2";
    private static final String KEY_CUSTOMER_PRICING_TRADE_OFFER = "trade_offer";
    private static final String KEY_CUSTOMER_PRICING_SCHEME = "scheme";
    private static final String KEY_CUSTOMER_PRICING_SCHEME_VAL = "scheme_val";
    private static final String KEY_CUSTOMER_PRICING_SCHEME_PRODUCT = "scheme_product";
    private static final String KEY_CUSTOMER_PRICING_TAX_1 = "tax_1";
    private static final String KEY_CUSTOMER_PRICING_TAX_2 = "tax_2";
    private static final String KEY_CUSTOMER_PRICING_TAX_3 = "tax_3";
    private static final String KEY_CUSTOMER_PRICING_TAX_FILER_1 = "tax_filer_1";
    private static final String KEY_CUSTOMER_PRICING_TAX_FILER_2 = "tax_filer_2";
    private static final String KEY_CUSTOMER_PRICING_TAX_FILER_3 = "tax_filer_3";
    private static final String KEY_CUSTOMER_PRICING_SUBTOTAL = "sub_total";
    // Inventory Table Columns names
    private static final String KEY_ID_INVENT = "id";
    private static final String KEY_BRAND_ID_FROM_INVENT = "brand_id";
    private static final String KEY_ITEM_ID_FROM_INVENT = "type_id";
    private static final String KEY_VENDOR_ID = "vendor_id";
    public static final String KEY_INVENT_NAME = "name";
    public static final String KEY_QUANTITY = "quantity";
    private static final String KEY_SALE_PRICE = "sale_price";
    private static final String KEY_UNIT_COST = "unit_cost";
    private static final String KEY_SKU = "sku";
    private static final String KEY_PACKING = "packing";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_TAX_APPLIED = "tax_applied";
    private static final String KEY_INVENTORY_PUBLISHED = "published";
    private static final String KEY_INVENTORY_SEARCH = "item_search";
    private static final String KEY_INVENTORY_SUB_TYPE_ID = "subtypeid";

    private static final String KEY_INVENTORY_IMAGE = "image";
    // Report To Table Columns names
    private static final String KEY_ID_REPORT_EMP = "id";
    private static final String KEY_LNAME_REPORT = "lname";
    private static final String KEY_EMAIL_REPORT = "email";
    // VALID Report To Table Columns names
    private static final String KEY_ID_VALID = "id";
    private static final String KEY_INDEX_VALID = "index_report";
    // Brand Table Columns names
    private static final String KEY_ID_BRAND = "id";
    private static final String KEY_BRAND_PUBLISHED = "published";
    // Item_Type Table Columns names

    private static final String KEY_ID_SUB_ITEM = "id";
    private static final String KEY_NAME_SUB_ITEM = "name";
    private static final String KEY_ITEM_ID_SUB_ITEM = "p_type";

    private static final String KEY_ID_ITEM = "id";
    private static final String KEY_ITEMTYPE_PUBLISHED = "published";
    private static final String KEY_CUSTOMER_EMAIL = "email";
    private static final String KEY_CUSTOMER_NOTES = "notes";
    private static final String KEY_CUSTOMER_PUBLISHED = "published";
    private static final String[] ALL_CUSTOMER_KEYS = new String[]
            {KEY_ID_CUSTOMER, KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_COMPANY_NAME,
                    KEY_CUSTOMER_ADDRESS, KEY_CUSTOMER_CITY, KEY_CUSTOMER_STATE, KEY_CUSTOMER_COUNTRY,
                    KEY_CUSTOMER_PUBLISHED};
    private static final String KEY_CUSTOMER_UPDATE = "update_cust";
    private static final String KEY_CUSTOMER_ROUTE_ID = "cust_route_id";
    private static final String KEY_CUSTOMER_NET_ID = "cust_net_id";
    private static final String KEY_CUSTOMER_TYPE = "cust_type_id";
    private static final String KEY_CUSTOMER_CATEGORY = "cust_category";
    private static final String KEY_CUSTOMER_CELEB = "cust_celeb";
    private static final String KEY_CUSTOMER_APP_PAYABLE = "cust_app_payable";
    private static final String KEY_CUSTOMER_ADVANCE_PAYMENT = "advance_payment";
    private static final String KEY_CUSTOMER_OPENING_BALANCE_OLD = "opening_bal_old";
    private static final String KEY_CUSTOMER_OPENING_BALANCE_NEW = "opening_bal_new";
    private static final String KEY_CUSTOMER_CREDIT_AMOUNT = "credit_amount";
    private static final String KEY_CUSTOMER_SMS_CODE = "sms_code";
    private static final String KEY_CUSTOMER_NUMBER_VERIFIED = "number_verified";
    private static final String KEY_CUSTOMER_LAST_UPDATE = "last_update";
    private static final String KEY_CUSTOMER_LATTITUDE = "lattitude";
    private static final String KEY_CUSTOMER_LONGITUDE = "longitude";
    private static final String KEY_CUSTOMER_RADIUS = "radius";
    private static final String KEY_CUSTOMER_MAPNAME = "map_name";
    private static final String KEY_CUSTOMER_GPS = "customer_gps";
    private static final String KEY_CUSTOMER_SEARCH = "customer_search";
    //Offline Tracking Table Columns
    private static final String KEY_OFFLINE_ID = "id";
    private static final String KEY_OFFLINE_LATTITUDE = "latitude";
    private static final String KEY_OFFLINE_LONGITUDE = "longitude";
    private static final String KEY_OFFLINE_DATETIME = "datetime";
    private static final String KEY_OFFLINE_AREA = "area";
    //Salesman_Settings Table Columns
    private static final String KEY_SALESMAN_SETTINGS_ID = "id";
    private static final String KEY_SALESMAN_SETTINGS_EID = "eid";
    private static final String KEY_SALESMAN_SETTINGS_ADMIN_EMAIL = "admin_email";
    private static final String KEY_SALESMAN_SETTINGS_ADMIN_PHONE = "admin_phone";
    private static final String KEY_SALESMAN_SETTINGS_TRACKING_EN = "tracking_en";
    private static final String KEY_SALESMAN_SETTINGS_TRACKING_OFFLINE = "tracking_offline";
    private static final String KEY_SALESMAN_SETTINGS_TRACKING_TIMEIN = "tracking_timein";
    private static final String KEY_SALESMAN_SETTINGS_TRACKING_TIMEOUT = "tracking_timeout";
    private static final String KEY_SALESMAN_SETTINGS_TRACKING_DURATION = "tracking_duration";
    private static final String KEY_SALESMAN_SETTINGS_NOTIFY_ALERTBAR = "notify_alertbar";
    private static final String KEY_SALESMAN_SETTINGS_NOTIFY_VIBRATE = "notify_vibrate";
    private static final String KEY_SALESMAN_SETTINGS_NOTIFY_SOUND = "notify_sound";
    private static final String KEY_SALESMAN_SETTINGS_NOTIFY_LIGHT = "notify_light";
    private static final String KEY_SALESMAN_SETTINGS_NOTIFY_EMAIL = "notify_email";
    private static final String KEY_SALESMAN_SETTINGS_NOTIFY_PHONE_START = "notify_phonestart";
    private static final String KEY_SALESMAN_SETTINGS_ACCESS_ROUTE = "access_route";
    private static final String KEY_SALESMAN_SETTINGS_ACCESS_REPORT = "access_report";
    private static final String KEY_SALESMAN_SETTINGS_ACCESS_CUSTOMER = "access_customer";
    private static final String KEY_SALESMAN_SETTINGS_ACCESS_SYNC = "access_sync";
    private static final String KEY_SALESMAN_SETTINGS_ACCESS_ORDER = "access_order";
    private static final String KEY_SALESMAN_SETTINGS_ACCESS_INVENTORY = "access_inventory";
    private static final String KEY_SALESMAN_SETTINGS_ACCESS_LOGOUT = "access_logout";
    private static final String KEY_SALESMAN_SETTINGS_MIN_DISCOUNT = "min_discount";
    private static final String KEY_SALESMAN_SETTINGS_MAX_DISCOUNT = "max_discount";
    // SALES_ORDER FIELDS
    private static final String KEY_SALES_ORDER_ID = "id";
    private static final String KEY_SALES_ORDER_CUSTOMER_ID = "customer_id";
    private static final String KEY_SALES_ORDER_EMPLOYEE_ID = "employee_id";
    private static final String KEY_SALES_ORDER_VALUES = "values_order";
    private static final String KEY_SALES_ORDER_NOTES = "notes";
    private static final String KEY_SALES_ORDER_START_DATETIME = "datetime_start";
    private static final String KEY_SALES_ORDER_DATETIME = "datetime_orig";
    private static final String KEY_SALES_ORDER_DATESHORT = "datetime_short";
    private static final String KEY_SALES_ORDER_DISCOUNT = "discount";
    private static final String KEY_SALES_ORDER_TOTAL = "total";
    private static final String KEY_SALES_ORDER_TOTAL2 = "total2";
    private static final String KEY_SALES_ORDER_TOTAL_EXECUTE = "total_execute";
    private static final String KEY_SALES_ORDER_LATITUDE = "latitude";
    private static final String KEY_SALES_ORDER_LONGITUDE = "longitude";
    private static final String KEY_SALES_ORDER_CONFIRM = "item_confirm";
    private static final String KEY_SALES_ORDER_DELETE = "item_delete";
    private static final String KEY_SALES_ORDER_ANDROID_OID = "android_oid";
    private static final String KEY_SALES_ORDER_UPDATE = "update_order";
    private static final String KEY_SALES_ORDER_EXECUTE_COMPLETE = "execute_complete";
    private static final String KEY_SALES_ORDER_EXECUTION_DATE = "execute_date";
    private static final String KEY_SALES_ORDER_AMOUNT_RECIEVED = "amount_recieved";
    private static final String KEY_SALES_ORDER_PRINTING_DATE = "print_date";
    private static final String KEY_SALES_ORDER_PAYMENT_TYPE = "sale_type";
    private static final String KEY_SALES_ORDER_SELECTED_DISTRIBUTOR_ID = "sel_dist_id";
    private static final String KEY_SALES_ORDER_DISTRIBUTOR_INVOICE = "sel_dist_invoice";
    // SALES_ORDER FIELDS
    private static final String KEY_SALES_RETURN_ID = "id";
    private static final String KEY_SALES_RETURN_CUSTOMER_ID = "customer_id";
    private static final String KEY_SALES_RETURN_EMPLOYEE_ID = "employee_id";
    private static final String KEY_SALES_RETURN_VALUES = "values_RETURN";
    private static final String KEY_SALES_RETURN_NOTES = "notes";
    private static final String KEY_SALES_RETURN_START_DATE = "start_datetime";
    private static final String KEY_SALES_RETURN_DATE = "datetime";
    private static final String KEY_SALES_RETURN_DISCOUNT = "discount";
    private static final String KEY_SALES_RETURN_TOTAL = "total";
    private static final String KEY_SALES_RETURN_TOTAL2 = "total2";
    private static final String KEY_SALES_RETURN_LATITUDE = "latitude";
    private static final String KEY_SALES_RETURN_LONGITUDE = "longitude";
    private static final String KEY_SALES_RETURN_CONFIRM = "item_confirm";
    private static final String KEY_SALES_RETURN_DELETE = "item_delete";
    private static final String KEY_SALES_RETURN_ANDROID_OID = "android_oid";
    private static final String KEY_SALES_RETURN_UPDATE = "update_RETURN";
    private static final String KEY_SALES_RETURN_REASON = "return_reason";
    /*
	//OFFICE
    private static final String KEY_Office_id = "cid";
    private static final String KEY_FName = "fName";
    private static final String KEY_LName = "LName";
    private static final String KEY_Add = "Address";
    private static final String KEY_Contact = "Contact";
*/
    private static final String KEY_SALES_RETURN_PRINTING_DATE = "print_date";
    private static final String KEY_SALES_RETURN_EXECUTE_COMPLETE = "execute_complete";
    private static final String KEY_SALES_RETURN_EXECUTION_DATE = "execute_date";
    private static final String KEY_SALES_RETURN_SELECTED_DISTRIBUTOR_ID = "sel_dist_id";
    //Mobile User Columns
    private static final String KEY_mId = "id";
    private static final String KEY_emp_id = "eid";
    private static final String KEY_Emp_FName = "fname";
    private static final String KEY_Emp_LName = "lname";
    private static final String KEY_Emp_Password = "password";
    private static final String KEY_Emp_Comp = "company_name";
    private static final String KEY_Emp_P1 = "phone";
    private static final String KEY_Emp_P2 = "alt_phone";
    private static final String KEY_Emp_Cell = "cell";
    private static final String KEY_Emp_Add = "address";
    private static final String KEY_Emp_City = "city";
    private static final String KEY_Emp_State = "state";
    private static final String KEY_Emp_Country = "country";
    private static final String KEY_Emp_Zip = "zip_code";
    private static final String KEY_Emp_Email = "email_add";
    private static final String KEY_Emp_LastSync = "last_sync";
    private static final String KEY_Emp_APP_VERSION = "app_version";
    private static final String KEY_Emp_LAST_SYNCED = "last_synced";
    private static final String KEY_Emp_PRODUCT_SALE = "product_sale";
    private static final String KEY_Emp_TARGET = "target";
    private static final String KEY_Emp_REPORT_URL = "report_url";
    private static final String KEY_Emp_TIME_IN = "time_in";
    private static final String KEY_Emp_TIME_IN_TIME = "time_in_time";
    private static final String KEY_Emp_Enable_Catalog_Pdf = "emp_en_catalog";
    private static final String KEY_Emp_SALE_TYPE = "sale_type";
    private static final String KEY_Emp_DISCOUNT_TYPE = "discount_type";
    //SETTINGS TABLE COLUMNS
    private static final String KEY_SETTINGS_ID = "id";
    private static final String KEY_AutoSync_SETTINGS = "autosync";
    private static final String KEY_TimeIn_SETTINGS = "time_in";
    private static final String KEY_TimeOut_SETTINGS = "time_out";
    private static final String KEY_SyncDur_SETTINGS = "sync_duration";
    private static final String KEY_SyncDur_INDEX_SETTINGS = "sync_duration_index";
    private static final String KEY_CLockIn_SETTINGS = "clockin_id"; //Update == 0, insert id,
    private static final String KEY_CLockIn_Total_SETTINGS = "clockin_total";//0hrs
    private static final String KEY_CLockIn_Time_SETTINGS = "clockin_time";
    private static final String KEY_Route_SETTINGS = "route";
    private static final String KEY_Reporting_SETTINGS = "reporting";
    private static final String KEY_Timing_SETTINGS = "timing_page";
    private static final String KEY_inventPg_SETTINGS = "inventory_page";
    private static final String KEY_CustomerPg_SETTINGS = "customer_page";
    private static final String KEY_PlaceOrderPg_SETTINGS = "placeorder_page";
    private static final String KEY_SummaryPg_SETTINGS = "summary_page";
    private static final String KEY_SyncPg_SETTINGS = "sync_page";
    private static final String KEY_Password_SETTINGS = "password_settings";
    private static final String KEY_en_Password_SETTINGS = "enable_password";
    private static final String KEY_AdminEmail_SETTINGS = "admin_email";
    private static final String KEY_AdminPhone_SETTINGS = "admin_phone";
    //Clock In Table Columns
    private static final String KEY_id_CLOCKIN = "id";
    private static final String KEY_empId_CLOCKIN = "emp_id";
    private static final String KEY_status_CLOCKIN = "status";
    private static final String KEY_TOTALTime_CLOCKIN = "total_time";
    private static final String KEY_clockInTime_CLOCKIN = "clockin_time";
    private static final String KEY_clockInTime_PREV = "previous";
    private static final String KEY_clockInTime_NEXT = "next";
    //SALES TABLE COLUMNS
    private static final String KEY_SALES_ID = "id";
    private static final String KEY_CUSTOMER_ID_FR_SALES = "cid";
    private static final String KEY_EMP_ID_FR_SALES = "eid";
    private static final String KEY_DATETIME_FR_SALES = "datetime";
    private static final String KEY_DISCOUNT_FR_SALES = "discount";
    private static final String KEY_PENDING_AMOUNT_FR_SALES = "pending_amount";
    private static final String KEY_TOTAL_AMOUNT_FR_SALES = "total_amount";
    private static final String KEY_CASH_ACCOUNT_FR_SALES = "cash_account";
    private static final String KEY_PAYMENT_MODE_FR_SALES = "payment_mode";
    private static final String KEY_SHIPPING_FR_SALES = "shipping_charges";
    private static final String KEY_TAX_APPLIED_FR_SALES = "tax_applied";
    private static final String KEY_SALES_PUBLISHED = "published";
    //SALES_DETAILS COLUMNS
    private static final String KEY_SALESDETAILS_ID = "id";
    private static final String KEY_ORDER_ID_FR_SD = "order_id";
    private static final String KEY_COGS_FR_SD = "cogs";
    private static final String KEY_PRODUCT_ID_FR_SD = "product_id";
    private static final String KEY_QUANTITY_FR_SD = "quantity";
    private static final String KEY_PRODUCT_AMOUNT_FR_SD = "product_amount";
    private static final String KEY_SD_PUBLISHED = "published";
    //CART COLUMNS
    private static final String KEY_CART_ID = "id";
    private static final String KEY_CART_NAME = "name";
    private static final String KEY_CART_PRICE = "price";
    private static final String KEY_CART_QTY = "qty";
    private static final String KEY_CART_TOTAL = "total";
    private static final String KEY_UC_CART = "unit_cost";
    //Route COLUMNS
    /*private static final String KEY_ROUTE_ID = "id";*/
    private static final String KEY_ROUTE_emp_id = "emp_id";
    private static final String KEY_ROUTE_cid = "c_id";
    private static final String KEY_ROUTE_seq = "sequence";
    private static final String KEY_ROUTE_visit_month = "visit_month";
    private static final String KEY_ROUTE_day = "day";
    //CUSTOMER_ROUTE COLUMNS
    private static final String KEY_ROUTE_ID = "id";
    private static final String KEY_ROUTE_NET_ID = "net_id";
    private static final String KEY_ROUTE_NAME = "r_name";
    private static final String KEY_ROUTE_SAVED = "r_saved";
    private static final String KEY_ROUTE_UPDATE = "r_update";
    private static final String KEY_ROUTE_DAY = "route_day";
    //TodayRoute COLUMNS
    private static final String KEY_TodayROUTE_Primary_ID = "id";
    private static final String KEY_TodayROUTE_ID = "route_id";
    private static final String KEY_TodayROUTE_DateTime = "datetime";
    // CHECK_NET Table Columns names
    private static final String KEY_ID_CHKNET = "id";
    private static final String KEY_CHKNET_SYNC = "sync";
    // PHONE_START Table Columns names
    private static final String KEY_ID_PHONESTART = "id";
    private static final String KEY_PHONESTART_SYNC = "sync";
    // Total_Discount Columns
    private static final String KEY_TOTAL_DISCOUNT_ID = "id";
    private static final String KEY_TOTAL_DISCOUNT_VALUE = "value";
    private static final String KEY_TOTAL_DISCOUNT_DISCOUNT = "discount";
    // Shop Stock Columns
    private static final String KEY_SHOP_STOCK_ID = "id";
    private static final String KEY_SHOP_STOCK_CUSTOMER_ID = "customer_id";
    private static final String KEY_SHOP_STOCK_PRODUCT_ID = "product_id";
    private static final String KEY_SHOP_STOCK_EMP_ID = "emp_id";
    private static final String KEY_SHOP_STOCK_QUANTITY = "qty";
    private static final String KEY_SHOP_STOCK_DATETIME = "datetime";
    private static final String KEY_SHOP_STOCK_SYNC = "sync";
    // ItemTarget Columns Names
    private static final String KEY_ITEM_TARGET_ID = "item_target_id";
    private static final String KEY_ITEM_TARGET_ITEM = "item_target_item";
    private static final String KEY_ITEM_TARGET_TARGET = "item_target_target";
    private static final String KEY_ITEM_TARGET_SOLD = "item_target_sold";
    private static final String KEY_ITEM_TARGET_ACHIEVED = "item_target_achieved";

    //Shop Category

    private static final String KEY_SHOP_CATEGORY_ID = "id";
    private static final String KEY_SHOP_CATEGORY_NAME = "name";
    //ORDER TEMPLATE
    private static final String KEY_ORDER_TEMPLATE_ID="order_template_id";
    private static final String KEY_ORDER_TEMPLATE_ITEM_ID="order_template_item_id";
    private static final String KEY_ORDER_TEMPLATE_QUANTITY="order_template_quantity";
    private static final String	KEY_ORDER_TEMPLATE_CUSTOMER_ID="order_template_customer";
    //Sub Shop Category

    private static final String KEY_SUB_SHOP_CATEGORY_ID = "id";
    private static final String KEY_SUB_SHOP_CATEGORY_NAME = "name";
    private static final String KEY_SUB_SHOP_CATEGORY_CATEGORY = "shop_category";

    public static SQLiteDatabase sqlDB;
    //    public static final String[] ALL_KEYS = new String[] {KEY_ID, KEY_NAME, KEY_AGE, KEY_PHONE, KEY_PUBLISHED, KEY_LAST_UPDATE};
    private static SQLiteDatabase db;
    private static DBHelper dbHelper;
    private static String sql = "";
    private static PosDB posDB;
    private Context AppContext;
    private Cursor cursorMain;
    private String selectQueryInside;

    private PosDB() {

    }


    private PosDB(Context c) {
        AppContext = c;

    }

    public static synchronized PosDB getInstance(Context c) {
        if (posDB == null) {
            posDB = new PosDB(c);
            dbHelper = new DBHelper(c);

            Log.e("OpenDb", "openDb");

            db = dbHelper.getWritableDatabase();
            sqlDB = db;

        }
        return posDB;
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        DateFormat df = DateFormat.getDateTimeInstance();

        //SelectedDate = dateFormat.format(new Date());

        return dateFormat.format(new java.util.Date());
        //return df.format(new Date());
    }

    private static String getFormattedDateFromTimestamp(long timestampInMilliSeconds) {
        Date date = new Date(timestampInMilliSeconds);
        date.setTime(timestampInMilliSeconds);
        //String formattedDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return formattedDate;

    }

    /*public PosDB OpenReadDb() {

        //return this;
        if (db == null) {
            dbHelper = new DBHelper(AppContext);
            db = dbHelper.getReadableDatabase();
            return this;
        } else {
            return this;
        }
    }*/

    public synchronized PosDB OpenDb() {

        //return this;
        if (db == null) {

            dbHelper = new DBHelper(AppContext);

            Log.e("OpenDb", "openDb");

            db = dbHelper.getWritableDatabase();
            sqlDB = db;

            return this;
        } else {
            //Log.e("OpenDb", "Already openDb");
            return this;
        }
    }

    public void CloseDb() {


    }



    /*
    Get Inventory Detail Starts
     */

    public List<String> getDateForSpinner() {


        List<String> Data = new ArrayList<>();

        String que = "Select DISTINCT " + KEY_SALES_ORDER_DATESHORT + " as dt FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 0 ORDER BY " + KEY_SALES_ORDER_DATESHORT + " DESC";

        Cursor c = db.rawQuery(que, null);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);
        int iData = c.getColumnIndex("dt");

        java.util.Date dt = null;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            try {
                dt = df.parse(c.getString(iData));

                SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy");

                Data.add(sdf.format(dt));

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        c.close();

        return Data;
    }
    public List<String> getDateForReportsSpinner() {


        List<String> Data = new ArrayList<>();

        String que = "Select DISTINCT " + KEY_SALES_ORDER_DATESHORT + " as dt FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER +  " ORDER BY " + KEY_SALES_ORDER_DATESHORT + " DESC";

        Cursor c = db.rawQuery(que, null);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);
        int iData = c.getColumnIndex("dt");

        java.util.Date dt = null;

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            try {
                dt = df.parse(c.getString(iData));

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                Data.add(sdf.format(dt));
              //  Data.add(c.getString(iData));

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        c.close();

        return Data;
    }
    public List<String> getCustomersCompanyNameListForReportsSpinner() {


        List<String> Data = new ArrayList<>();

        String que = "Select DISTINCT " + KEY_CUSTOMER_SEARCH + " as dt FROM " + DATABASE_TABLE_CUSTOMER_DETAILS ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);
        int iData = c.getColumnIndex("dt");


        Data.add("All");
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {






                Data.add(c.getString(iData));




        }

        c.close();

        return Data;
    }

    public List<String> getCustomersCompanyNameListForReportsSpinnerByRoute(String SelectedRoute) {


        List<String> Data = new ArrayList<>();

        String que = "Select " + KEY_CUSTOMER_SEARCH + " as dt FROM " + DATABASE_TABLE_CUSTOMER_DETAILS +" INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" WHERE "+KEY_ROUTE_NAME+"='"+SelectedRoute+"' ;";

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);
        int iData = c.getColumnIndex("dt");


        Data.add("All");
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {






            Data.add(c.getString(iData));




        }

        c.close();

        return Data;
    }

    public List<String> getRouteListForReportsSpinner() {


        List<String> Data = new ArrayList<>();

        String que = "Select DISTINCT " + KEY_ROUTE_NAME + " as dt FROM " + DATABASE_TABLE_CUSTOMER_ROUTE ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);
        int iData = c.getColumnIndex("dt");

        Data.add("All");

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {






            Data.add(c.getString(iData));




        }

        c.close();

        return Data;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForLoadSheet() {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();


        String que = "SELECT " +
                             KEY_ORDER_DETAIL_PRODUCT_ID +", "+KEY_INVENTORY_SEARCH+", SUM("+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as qty_sum,"+KEY_SALES_ORDER_EXECUTE_COMPLETE+"*"+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                             " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                             " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                             "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+"  GROUP by "+KEY_ORDER_DETAIL_PRODUCT_ID+", "+KEY_INVENTORY_SEARCH+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




        map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
                    map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex("qty_sum")));
                    map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
         list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDeliverySheet() {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();


        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+","+KEY_ROUTE_NAME+","+KEY_INVENTORY_SEARCH+", sum( "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as "+KEY_ORDER_DETAIL_QUANTITY+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+"* "+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" GROUP by "+KEY_INVENTORY_SEARCH+", "+KEY_RETURN_DETAIL_ORDER_ID+";"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY)));
            map.put(REPORT_SIXTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForOrderSheet() {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();


        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+", "+KEY_ROUTE_NAME+", SUM("+KEY_ORDER_DETAIL_SUBTOTAL+") as "+KEY_ORDER_DETAIL_SUBTOTAL+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+"" +
                " GROUP by "+KEY_RETURN_DETAIL_ORDER_ID+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_SALES_ORDER_EXECUTE_COMPLETE)));
            list.add(map);
        }

        c.close();

        return list;
    }
    public ArrayList<HashMap<String, String>>  getorderDataForDSRSheet() {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();


        String que = "SELECT item_search, coalesce(SUM(order_details.quantity),0) as order_quantity, coalesce(sum(order_details.quantity_exe),0) as quantity_exe, coalesce(sum(return_details.quantity),0) as return_quantity FROM inventory  left JOIN order_details ON inventory.id =  order_details.product_id left JOIN sales_order ON sales_order.id =  order_details.order_id   left JOIN  customer ON sales_order.customer_id = customer.id left JOIN  cust_route ON customer.cust_route_id = cust_route.net_id  left JOIN  return_details ON inventory.id = return_details.product_id WHERE  order_details.quantity is not null or return_details.quantity is not null GROUP by inventory.item_search ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));

            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex("order_quantity")));
            map.put(REPORT_THIRD_COLUMN,String.valueOf(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe")))));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("return_quantity")));

            map.put(REPORT_FIFTH_COLUMN,String.valueOf((Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe"))))+Integer.parseInt(c.getString(c.getColumnIndex("return_quantity"))))));
            list.add(map);
        }

        c.close();

        return list;
    }
    public ArrayList<HashMap<String, String>>  getorderDataForLoadSheetAllQuery(String SearchDate,String SearchRoute,String SearchCustomer) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_DETAIL_PRODUCT_ID +", "+KEY_INVENTORY_SEARCH+", SUM("+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as qty_sum,"+KEY_SALES_ORDER_EXECUTE_COMPLETE+"*"+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                             " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                             "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"'" +
                             " AND "+KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+"' GROUP by "+KEY_ORDER_DETAIL_PRODUCT_ID+", "+KEY_INVENTORY_SEARCH+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex("qty_sum")));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }
    public ArrayList<HashMap<String, String>>  getorderDataForOrderSheetAllQuery(String SearchDate,String SearchRoute,String SearchCustomer) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+", "+KEY_ROUTE_NAME+", SUM("+KEY_ORDER_DETAIL_SUBTOTAL+") as "+KEY_ORDER_DETAIL_SUBTOTAL+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+
                " where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"'" +
                " AND "+KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+"' GROUP by "+KEY_RETURN_DETAIL_ORDER_ID+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_SALES_ORDER_EXECUTE_COMPLETE)));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDSRSheetAllQuery(String SearchDate,String SearchRoute,String SearchCustomer) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT item_search, coalesce(SUM(order_details.quantity),0) as order_quantity, coalesce(sum(order_details.quantity_exe),0) as quantity_exe, coalesce(sum(return_details.quantity),0) as return_quantity FROM inventory  left JOIN order_details ON inventory.id =  order_details.product_id left JOIN sales_order ON sales_order.id =  order_details.order_id   left JOIN  customer ON sales_order.customer_id = customer.id left JOIN  cust_route ON customer.cust_route_id = cust_route.net_id  left JOIN  return_details ON inventory.id = return_details.product_id WHERE  (order_details.quantity is not null or return_details.quantity is not null) AND ("

      +  " "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"'" +
                " AND "+KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+
                "') GROUP by inventory.item_search ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();





            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));

            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex("order_quantity")));
            map.put(REPORT_THIRD_COLUMN,String.valueOf(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe")))));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("return_quantity")));

            map.put(REPORT_FIFTH_COLUMN,String.valueOf((Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe"))))+Integer.parseInt(c.getString(c.getColumnIndex("return_quantity"))))));
            list.add(map);
        }

        c.close();

        return list;
    }


    public ArrayList<HashMap<String, String>>  getorderDataForDeliverySheetAllQuery(String SearchDate,String SearchRoute,String SearchCustomer) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+","+KEY_ROUTE_NAME+","+KEY_INVENTORY_SEARCH+", sum( "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as "+KEY_ORDER_DETAIL_QUANTITY+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+"* "+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"'" +
                " AND "+KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+"' GROUP by "+KEY_INVENTORY_SEARCH+", "+KEY_RETURN_DETAIL_ORDER_ID+";"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY)));
            map.put(REPORT_SIXTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForLoadSheetRouteQuery(String SearchDate,String SearchRoute) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_DETAIL_PRODUCT_ID +", "+KEY_INVENTORY_SEARCH+", SUM("+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as qty_sum,"+KEY_SALES_ORDER_EXECUTE_COMPLETE+"*"+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"'" +
               "GROUP by "+KEY_ORDER_DETAIL_PRODUCT_ID+", "+KEY_INVENTORY_SEARCH+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex("qty_sum")));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForOrderSheetRouteQuery(String SearchDate,String SearchRoute) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+", "+KEY_ROUTE_NAME+", SUM("+KEY_ORDER_DETAIL_SUBTOTAL+") as "+KEY_ORDER_DETAIL_SUBTOTAL+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"'" +
                "  GROUP by "+KEY_RETURN_DETAIL_ORDER_ID+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_SALES_ORDER_EXECUTE_COMPLETE)));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDSRSheetRouteQuery(String SearchDate,String SearchRoute){

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT item_search, coalesce(SUM(order_details.quantity),0) as order_quantity, coalesce(sum(order_details.quantity_exe),0) as quantity_exe, coalesce(sum(return_details.quantity),0) as return_quantity FROM inventory  left JOIN order_details ON inventory.id =  order_details.product_id left JOIN sales_order ON sales_order.id =  order_details.order_id   left JOIN  customer ON sales_order.customer_id = customer.id left JOIN  cust_route ON customer.cust_route_id = cust_route.net_id  left JOIN  return_details ON inventory.id = return_details.product_id WHERE  (order_details.quantity is not null or return_details.quantity is not null) AND ("

                +  " "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"') GROUP by inventory.item_search ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();





            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));

            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex("order_quantity")));
            map.put(REPORT_THIRD_COLUMN,String.valueOf(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe")))));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("return_quantity")));

            map.put(REPORT_FIFTH_COLUMN,String.valueOf((Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe"))))+Integer.parseInt(c.getString(c.getColumnIndex("return_quantity"))))));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDSRSheetDateQuery(String SearchDate){

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT item_search, coalesce(SUM(order_details.quantity),0) as order_quantity, coalesce(sum(order_details.quantity_exe),0) as quantity_exe, coalesce(sum(return_details.quantity),0) as return_quantity FROM inventory  left JOIN order_details ON inventory.id =  order_details.product_id left JOIN sales_order ON sales_order.id =  order_details.order_id   left JOIN  customer ON sales_order.customer_id = customer.id left JOIN  cust_route ON customer.cust_route_id = cust_route.net_id  left JOIN  return_details ON inventory.id = return_details.product_id WHERE  (order_details.quantity is not null or return_details.quantity is not null) AND ("

                +  " "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"') GROUP by inventory.item_search ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();





            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));

            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex("order_quantity")));
            map.put(REPORT_THIRD_COLUMN,String.valueOf(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe")))));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("return_quantity")));

            map.put(REPORT_FIFTH_COLUMN,String.valueOf((Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe"))))+Integer.parseInt(c.getString(c.getColumnIndex("return_quantity"))))));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDSRSheetCustomerQuery(String SearchDate,String SearchCustomer){

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT item_search, coalesce(SUM(order_details.quantity),0) as order_quantity, coalesce(sum(order_details.quantity_exe),0) as quantity_exe, coalesce(sum(return_details.quantity),0) as return_quantity FROM inventory  left JOIN order_details ON inventory.id =  order_details.product_id left JOIN sales_order ON sales_order.id =  order_details.order_id   left JOIN  customer ON sales_order.customer_id = customer.id left JOIN  cust_route ON customer.cust_route_id = cust_route.net_id  left JOIN  return_details ON inventory.id = return_details.product_id WHERE  (order_details.quantity is not null or return_details.quantity is not null) AND ("

        +KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND " +
        KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+"') GROUP by inventory.item_search ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();





            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));

            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex("order_quantity")));
            map.put(REPORT_THIRD_COLUMN,String.valueOf(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe")))));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("return_quantity")));

            map.put(REPORT_FIFTH_COLUMN,String.valueOf((Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-(Integer.parseInt(c.getString(c.getColumnIndex("order_quantity")))-Integer.parseInt(c.getString(c.getColumnIndex("quantity_exe"))))+Integer.parseInt(c.getString(c.getColumnIndex("return_quantity"))))));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDeliverySheetRouteQuery(String SearchDate,String SearchRoute) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+","+KEY_ROUTE_NAME+","+KEY_INVENTORY_SEARCH+", sum( "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as "+KEY_ORDER_DETAIL_QUANTITY+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+"* "+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"'" +
                " GROUP by "+KEY_INVENTORY_SEARCH+", "+KEY_RETURN_DETAIL_ORDER_ID+";"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY)));
            map.put(REPORT_SIXTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForLoadSheetCustomerQuery(String SearchDate,String SearchCustomer) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_DETAIL_PRODUCT_ID +", "+KEY_INVENTORY_SEARCH+", SUM("+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as qty_sum,"+KEY_SALES_ORDER_EXECUTE_COMPLETE+"*"+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND " +
                KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+"' GROUP by "+KEY_ORDER_DETAIL_PRODUCT_ID+", "+KEY_INVENTORY_SEARCH+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex("qty_sum")));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForOrderSheetCustomerQuery(String SearchDate,String SearchCustomer) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+", "+KEY_ROUTE_NAME+", SUM("+KEY_ORDER_DETAIL_SUBTOTAL+") as "+KEY_ORDER_DETAIL_SUBTOTAL+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND " +
                KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+"'  GROUP by "+KEY_RETURN_DETAIL_ORDER_ID+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_SALES_ORDER_EXECUTE_COMPLETE)));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDeliverySheetCustomerQuery(String SearchDate,String SearchCustomer) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+","+KEY_ROUTE_NAME+","+KEY_INVENTORY_SEARCH+", sum( "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as "+KEY_ORDER_DETAIL_QUANTITY+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+"* "+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND " +
                KEY_CUSTOMER_SEARCH+"='"+SearchCustomer+"' GROUP by "+KEY_INVENTORY_SEARCH+", "+KEY_RETURN_DETAIL_ORDER_ID+";"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY)));
            map.put(REPORT_SIXTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForLoadSheetDateQuery(String SearchDate) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_DETAIL_PRODUCT_ID +", "+KEY_INVENTORY_SEARCH+", SUM("+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as qty_sum,"+KEY_SALES_ORDER_EXECUTE_COMPLETE+"*"+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' " +
              "GROUP by "+KEY_ORDER_DETAIL_PRODUCT_ID+", "+KEY_INVENTORY_SEARCH+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex("qty_sum")));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForOrderSheetDateQuery(String SearchDate) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+", "+KEY_ROUTE_NAME+", SUM("+KEY_ORDER_DETAIL_SUBTOTAL+") as "+KEY_ORDER_DETAIL_SUBTOTAL+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' " +
                "  GROUP by "+KEY_RETURN_DETAIL_ORDER_ID+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_SALES_ORDER_EXECUTE_COMPLETE)));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForDeliverySheetDateQuery(String SearchDate) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_ID_FR_SD +", "+KEY_CUSTOMER_SEARCH+","+KEY_ROUTE_NAME+","+KEY_INVENTORY_SEARCH+", sum( "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as "+KEY_ORDER_DETAIL_QUANTITY+", "+KEY_SALES_ORDER_EXECUTE_COMPLETE+"* "+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+" where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' " +
                " GROUP by "+KEY_INVENTORY_SEARCH+", "+KEY_RETURN_DETAIL_ORDER_ID+";"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_ID_FR_SD)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_CUSTOMER_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex(KEY_ROUTE_NAME)));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_FIFTH_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY)));
            map.put(REPORT_SIXTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public ArrayList<HashMap<String, String>>  getorderDataForLoadSheetSelectiveQuery(String SearchDate,String SearchRoute) {

        ArrayList<HashMap<String, String>> list= new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map= new HashMap<String, String>();
        List<String> Data = new ArrayList<>();

        String que = "SELECT " +
                KEY_ORDER_DETAIL_PRODUCT_ID +", "+KEY_INVENTORY_SEARCH+", SUM("+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_QUANTITY+") as qty_sum,"+KEY_SALES_ORDER_EXECUTE_COMPLETE+"*"+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qty_execute"+
                " FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" " +
                             " INNER JOIN "+DATABASE_TABLE_ORDER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_ID+" =  "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_ORDER_ID+" INNER JOIN  "+DATABASE_TABLE_INVENTORY+" ON "+DATABASE_TABLE_ORDER_DETAILS+"."+KEY_ORDER_DETAIL_PRODUCT_ID+" = "+DATABASE_TABLE_INVENTORY+".id " +
                             "INNER JOIN  "+DATABASE_TABLE_CUSTOMER_DETAILS+" ON "+DATABASE_TABLE_SALESMAN_SALES_ORDER+"."+KEY_SALES_ORDER_CUSTOMER_ID+" = "+DATABASE_TABLE_CUSTOMER_DETAILS+".id INNER JOIN  "+DATABASE_TABLE_CUSTOMER_ROUTE+" ON "+DATABASE_TABLE_CUSTOMER_DETAILS+"."+KEY_CUSTOMER_ROUTE_ID+" = "+DATABASE_TABLE_CUSTOMER_ROUTE+"."+KEY_ROUTE_NET_ID+"" +
                             " where "+KEY_SALES_ORDER_DATESHORT+"='"+SearchDate+"' AND "+KEY_ROUTE_NAME+"='"+SearchRoute+"' GROUP by "+KEY_ORDER_DETAIL_PRODUCT_ID+", "+KEY_INVENTORY_SEARCH+" ;"  ;

        Cursor c = db.rawQuery(que, null);




        //int iData = c.getColumnIndex(KEY_SALES_ORDER_DATE);




        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

            map= new HashMap<String, String>();




            map.put(REPORT_FIRST_COLUMN,c.getString(c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID)));
            map.put(REPORT_SECOND_COLUMN,c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
            map.put(REPORT_THIRD_COLUMN,c.getString(c.getColumnIndex("qty_sum")));
            map.put(REPORT_FOURTH_COLUMN,c.getString(c.getColumnIndex("qty_execute")));
            list.add(map);
        }

        c.close();

        return list;
    }

    public List<String> getIdsForSpinner(String dateTime) {

        List<String> Data = new ArrayList<>();

        String que = "Select DISTINCT " + KEY_SALES_ORDER_DATESHORT + " as dt, " + KEY_SALES_ORDER_ID + " as sid FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 0 AND " + KEY_SALES_ORDER_DATETIME + " LIKE '" + dateTime + "%' ";


        Cursor c = db.rawQuery(que, null);

        int iData = c.getColumnIndex("sid");
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                Data.add(c.getString(iData));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {

                c.close();
            }
        }

        return Data;
    }

    public List<String> getDateLONGForSpinner() {

        List<String> Data = new ArrayList<>();

        String que = "Select DISTINCT " + KEY_SALES_ORDER_DATESHORT + " as dt FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 0 ORDER BY " + KEY_SALES_ORDER_DATESHORT + " DESC";
        Cursor c = db.rawQuery(que, null);

        int iData = c.getColumnIndex("dt");

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                Data.add(c.getString(iData));


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {

                c.close();
            }
        }


        return Data;
    }

    public int getProductsRow() {

        String que = "Select Count(+" + KEY_ID_INVENT + ") FROM " + DATABASE_TABLE_INVENTORY;

        Cursor c = db.rawQuery(que, null);
        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
        if (c != null && !c.isClosed())
            c.close();

        return count;
    }

    /*
    Get Inventory Detail Ends
     */

    public ArrayList<HashMap<String, String>> getInventoryList() {

        String selectQuery = "SELECT i." + KEY_ID_INVENT + " AS iid, i." + KEY_INVENT_NAME + " AS pname, b." + KEY_BRAND_NAME + " AS bname, t." + KEY_ITEM_NAME + " AS tname, i." + KEY_SALE_PRICE + " AS price"/*+", i."+KEY_INVENTORY_IMAGE+" AS img "*/ + " FROM " + DATABASE_TABLE_INVENTORY + " i, " + DATABASE_TABLE_BRAND + " b, " + DATABASE_TABLE_ITEM_TYPE
                + " t WHERE i." + KEY_BRAND_ID_FROM_INVENT + " = b." + KEY_ID_BRAND + " AND i." + KEY_ITEM_ID_FROM_INVENT + " = t." + KEY_ID_ITEM + " ORDER BY i." + KEY_INVENT_NAME + " ASC";

//        String selectQuery = "SELECT i."+KEY_INVENT_NAME+" AS pname  FROM "+DATABASE_TABLE_INVENTORY+" i";


        Log.d("SELECT Qu", selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

//        int iQty = c.getColumnIndex(KEY_QUANTITY);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    //map.put(CODE_COLUMN, c.getString(0));
                    map.put(FIRST_COLUMN, c.getString(1));
                    map.put(SECOND_COLUMN, c.getString(2));
                    map.put(THIRD_COLUMN, c.getString(3));
                    map.put(FOURTH_COLUMN, "Rs. " + c.getString(4));
                    //      map.put(CODE_PICTXT, c.getString(5));

                    //    Log.d("Hashid",c.getString(5));

                    result.add(map);
                } while (c.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedInventoryName(String ItemId) {

        int Id = Integer.parseInt(ItemId);

        String[] Column = new String[]{KEY_INVENT_NAME};

        Cursor c = db.query(DATABASE_TABLE_INVENTORY, Column, KEY_ID_INVENT + "= " + Id, null, null, null, null);

        String result = "";

        int iName = c.getColumnIndex(KEY_INVENT_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iName) + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    public ArrayList getInventorySearchName() {



        String[] Column = new String[]{KEY_INVENTORY_SEARCH};

        Cursor c = db.query(DATABASE_TABLE_INVENTORY, Column, null, null, null, null, null);

        ArrayList result = new ArrayList();

        int iName = c.getColumnIndex(KEY_INVENTORY_SEARCH);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result.add( c.getString(iName)) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    public long createOffline_Tracking(double latitude, double longitude, String datetime, String area) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_OFFLINE_LATTITUDE, latitude);
        cv.put(KEY_OFFLINE_LONGITUDE, longitude);
        cv.put(KEY_OFFLINE_DATETIME, datetime);
        cv.put(KEY_OFFLINE_AREA, area);

        long insertItemId = db.insert(DATABASE_TABLE_OFFLINE_TRACKING, null, cv);
        return insertItemId;
    }

    public ArrayList<HashMap<String, String>> getOfflineTrackingList() {


        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_OFFLINE_TRACKING;

        Cursor c = db.rawQuery(selectQuery, null);


        int iId = c.getColumnIndex(KEY_OFFLINE_ID);
        int iLati = c.getColumnIndex(KEY_OFFLINE_LATTITUDE);
        int iLongi = c.getColumnIndex(KEY_OFFLINE_LONGITUDE);
        int iArea = c.getColumnIndex(KEY_OFFLINE_AREA);


        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(FIRST_COLUMN, c.getString(iId));
                    map.put(SECOND_COLUMN, c.getString(iLati));
                    map.put(THIRD_COLUMN, c.getString(iLongi));
                    map.put(FOURTH_COLUMN, c.getString(iArea));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }


        return result;


    }

    public long createSalesman_Settings(String eid, String admin_email, String admin_phone, String Tracking_en, String Tracking_Timein, String Tracking_Timeout, String Tracking_Offline, String Tracking_Duration, String Notify_Alert, String Notify_Vibrate, String Notify_Sound, String Notify_Light, String Notify_Email, String Notify_Phone, String Access_Route, String Access_Report, String Access_Customer, String Access_Sync, String Access_Order, String Access_Inventory, String Access_Logout, String minDiscount, String maxDiscount) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_SALESMAN_SETTINGS_EID, eid);
        cv.put(KEY_SALESMAN_SETTINGS_ADMIN_EMAIL, admin_email);
        cv.put(KEY_SALESMAN_SETTINGS_ADMIN_PHONE, admin_phone);

        cv.put(KEY_SALESMAN_SETTINGS_TRACKING_EN, Tracking_en);
        cv.put(KEY_SALESMAN_SETTINGS_TRACKING_TIMEIN, Tracking_Timein);
        cv.put(KEY_SALESMAN_SETTINGS_TRACKING_TIMEOUT, Tracking_Timeout);
        cv.put(KEY_SALESMAN_SETTINGS_TRACKING_DURATION, Tracking_Duration);
        cv.put(KEY_SALESMAN_SETTINGS_TRACKING_OFFLINE, Tracking_Offline);

        cv.put(KEY_SALESMAN_SETTINGS_NOTIFY_ALERTBAR, Notify_Alert);
        cv.put(KEY_SALESMAN_SETTINGS_NOTIFY_VIBRATE, Notify_Vibrate);
        cv.put(KEY_SALESMAN_SETTINGS_NOTIFY_SOUND, Notify_Sound);
        cv.put(KEY_SALESMAN_SETTINGS_NOTIFY_LIGHT, Notify_Light);
        cv.put(KEY_SALESMAN_SETTINGS_NOTIFY_EMAIL, Notify_Email);
        cv.put(KEY_SALESMAN_SETTINGS_NOTIFY_PHONE_START, Notify_Phone);

        cv.put(KEY_SALESMAN_SETTINGS_ACCESS_ROUTE, Access_Route);
        cv.put(KEY_SALESMAN_SETTINGS_ACCESS_REPORT, Access_Report);
        cv.put(KEY_SALESMAN_SETTINGS_ACCESS_CUSTOMER, Access_Customer);
        cv.put(KEY_SALESMAN_SETTINGS_ACCESS_SYNC, Access_Sync);

        cv.put(KEY_SALESMAN_SETTINGS_ACCESS_INVENTORY, Access_Inventory);
        cv.put(KEY_SALESMAN_SETTINGS_ACCESS_ORDER, Access_Order);
        cv.put(KEY_SALESMAN_SETTINGS_ACCESS_LOGOUT, Access_Logout);

        cv.put(KEY_SALESMAN_SETTINGS_MIN_DISCOUNT, minDiscount);
        cv.put(KEY_SALESMAN_SETTINGS_MAX_DISCOUNT, maxDiscount);

        long insertItemId = db.insert(DATABASE_TABLE_SALESMAN_SETTINGS, null, cv);
        return insertItemId;
    }

    private String regex(String text) {

        // String firstName = "Umais & ^ Khan % Sons";
        String result = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]+", "");
        return result;

    }

    public long createCustomer(String FName, String LName, String comp_name, String cell,
                               String p1, String p2, String cnic, String Add, String city, String state, String country,
                               String email, String notes, String lati, String longi, String area, String openingBalOld,
                               String openingBalNew, int publish, int cust_route_id, int cust_type_id,
                               String customerCeleb, long filer_non_filer, String shopCategory,
                               String subShopCat) {

        // int QtyInt = Integer.parseInt(qty);
        // int PriceInt = Integer.parseInt(price);

        ContentValues cv = new ContentValues();

        cv.put(KEY_CUSTOMER_FNAME, regex(FName));
        cv.put(KEY_CUSTOMER_LNAME, regex(LName));
        cv.put(KEY_COMPANY_NAME, regex(comp_name));
        cv.put(KEY_CUSTOMER_CELL, cell);
        cv.put(KEY_CUSTOMER_PHONE1, p1);
        cv.put(KEY_CUSTOMER_PHONE2, p2);
        cv.put(KEY_CUSTOMER_ADDRESS, Add);
        cv.put(KEY_CUSTOMER_CITY, city);
        cv.put(KEY_CUSTOMER_AREA, area);
        cv.put(KEY_CUSTOMER_CNIC, cnic);
        cv.put(KEY_CUSTOMER_STATE, state);
        cv.put(KEY_CUSTOMER_COUNTRY, country);
        cv.put(KEY_CUSTOMER_EMAIL, email);
        cv.put(KEY_CUSTOMER_NOTES, notes);
        cv.put(KEY_CUSTOMER_ROUTE_ID, cust_route_id);
        cv.put(KEY_CUSTOMER_PUBLISHED, publish);
        cv.put(KEY_CUSTOMER_LATTITUDE, lati);
        cv.put(KEY_CUSTOMER_LONGITUDE, longi);
        cv.put(KEY_CUSTOMER_TYPE, cust_type_id);
        cv.put(KEY_CUSTOMER_OPENING_BALANCE_OLD, openingBalOld);
        cv.put(KEY_CUSTOMER_OPENING_BALANCE_NEW, openingBalNew);
        cv.put(KEY_CUSTOMER_MAPNAME, area);
        cv.put(KEY_CUSTOMER_UPDATE, 1);
        cv.put(KEY_CUSTOMER_NET_ID, 0);
        cv.put(KEY_CUSTOMER_LAST_UPDATE, getDateTimeSHORT());
        cv.put(KEY_CUSTOMER_CELEB, customerCeleb);
        cv.put(KEY_CUSTOMER_FILER_NON_FILER, filer_non_filer);
        cv.put(KEY_CUSTOMER_SHOP_CATEGORY_ID, shopCategory);
         cv.put(KEY_CUSTOMER_SUB_SHOP_CATEGORY_ID, subShopCat);


        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);


        long insertId = db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;

    }

    public void updateCustomerLocation(String id, String city, String area, String lati, String longi,String Address,String category, String SubCategory) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
       /* String updateQuery = "Update " + DATABASE_TABLE_CUSTOMER_DETAILS + " set " + KEY_CUSTOMER_OPENING_BALANCE_OLD + " = " + balance + " where " + KEY_ID_CUSTOMER + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);*/
        ContentValues cv = new ContentValues();
        cv.put(KEY_CUSTOMER_CITY, city);
        cv.put(KEY_CUSTOMER_AREA, area);
        cv.put(KEY_CUSTOMER_MAPNAME, area);
        cv.put(KEY_CUSTOMER_LATTITUDE, lati);
        cv.put(KEY_CUSTOMER_LONGITUDE, longi);
        cv.put(KEY_CUSTOMER_ADDRESS, Address);
        cv.put(KEY_CUSTOMER_SHOP_CATEGORY_ID, category);
        cv.put(KEY_CUSTOMER_SUB_SHOP_CATEGORY_ID, SubCategory);

        cv.put(KEY_CUSTOMER_UPDATE, 2);


        db.update(DATABASE_TABLE_CUSTOMER_DETAILS, cv, KEY_ID_CUSTOMER + " = " + idInt, null);

    }

    public long insertCustomerType(String id, String Named) {

        // int QtyInt = Integer.parseInt(qty);
        // int PriceInt = Integer.parseInt(price);
        int PriceInt = Integer.parseInt(id);
        ContentValues cv = new ContentValues();
        cv.put(KEY_CUSTOMER_TYPE_ID, PriceInt);
        cv.put(KEY_CUSTOMER_TYPE_NAME, Named);

        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);


        long insertId = db.insert(DATABASE_TABLE_CUSTOMER_TYPE, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;

    }

    public long insertExpenseType(String id, String Named) {

        // int QtyInt = Integer.parseInt(qty);
        // int PriceInt = Integer.parseInt(price);
        int PriceInt = Integer.parseInt(id);
        ContentValues cv = new ContentValues();
        cv.put(KEY_EXPENSE_TYPE_ID, PriceInt);
        cv.put(KEY_EXPENSE_TYPE_NAME, Named);

        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);


        long insertId = db.insert(DATABASE_TABLE_EXPENSE_TYPE, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;

    }

    public void deleteExpenseType() {


        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_EXPENSE_TYPE;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    private String getSelectedExpenseTypeName(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_EXPENSE_TYPE_NAME};
        Cursor c = db.query(DATABASE_TABLE_EXPENSE_TYPE, Column, KEY_EXPENSE_TYPE_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iComp = c.getColumnIndex(KEY_EXPENSE_TYPE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public long insertExpenseStatus(String id, String Named) {

        // int QtyInt = Integer.parseInt(qty);
        // int PriceInt = Integer.parseInt(price);
        int PriceInt = Integer.parseInt(id);
        ContentValues cv = new ContentValues();
        cv.put(KEY_EXPENSE_STATUS_ID, PriceInt);
        cv.put(KEY_EXPENSE_STATUS_NAME, Named);

        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);


        long insertId = db.insert(DATABASE_TABLE_EXPENSE_STATUS, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;

    }
     /*
        Create Data Ends
     */

    /*
   Cusotmer Details Starts
    */

    private String getSelectedExpenseStatusName(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_EXPENSE_STATUS_NAME};
        Cursor c = db.query(DATABASE_TABLE_EXPENSE_STATUS, Column, KEY_EXPENSE_STATUS_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iComp = c.getColumnIndex(KEY_EXPENSE_STATUS_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public void deleteExpenseStatus() {


        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_EXPENSE_STATUS;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public long insertNoReason(String id, String Named) {

        // int QtyInt = Integer.parseInt(qty);
        // int PriceInt = Integer.parseInt(price);
        int PriceInt = Integer.parseInt(id);
        ContentValues cv = new ContentValues();
        cv.put(KEY_NO_REASON_ID, PriceInt);
        cv.put(KEY_NO_REASON_NAME, Named);

        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);


        long insertId = db.insert(DATABASE_TABLE_NO_REASON, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;

    }

    public void deleteNoReason() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_NO_REASON;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public void deleteCustomerRoute() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_CUSTOMER_ROUTE;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public long createCustomerPricing(String custTypeId,
                                      String prodId,
                                      String tradePrice,
                                      String discount1,
                                      String discount2,
                                      String tradeOffer,
                                      String scheme,
                                      String schemeVal,
                                      String schemeProduct,
                                      String tax1,
                                      String tax2,
                                      String tax3,
                                      String filer1,
                                      String filer2,
                                      String filer3,
                                      String customer_id,
                                      String brand_id,
                                      String product_type_id,
                                      String product_sub_type_id,
                                      String min_qty,
                                      String max_qty,
                                      String multi,
                                      String customer_category_id,
                                      String customer_subcategory_id,
                                      String min_amount,
                                      String max_amount,
                                      String use_defaultprice,
                                      String mrp_price,
                                      String use_defaultmrp,
                                      String emp_id,
                                      String dist_id) {


        // int QtyInt = Integer.parseInt(qty);
        // int PriceInt = Integer.parseInt(price);


        ContentValues cv = new ContentValues();


        cv.put(KEY_CUSTOMER_PRICING_TYPE_ID, custTypeId);
        cv.put(KEY_CUSTOMER_PRICING_PRODUCT_ID, prodId);
        cv.put(KEY_CUSTOMER_PRICING_TRADE_PRICE, tradePrice);
        cv.put(KEY_CUSTOMER_PRICING_DISCOUNT_1, discount1);
        cv.put(KEY_CUSTOMER_PRICING_DISCOUNT_2, discount2);
        cv.put(KEY_CUSTOMER_PRICING_TRADE_OFFER, tradeOffer);
        cv.put(KEY_CUSTOMER_PRICING_SCHEME, scheme);
        cv.put(KEY_CUSTOMER_PRICING_SCHEME_VAL, schemeVal);
        cv.put(KEY_CUSTOMER_PRICING_SCHEME_PRODUCT, schemeProduct);
        cv.put(KEY_CUSTOMER_PRICING_TAX_1, tax1);
        cv.put(KEY_CUSTOMER_PRICING_TAX_2, tax2);
        cv.put(KEY_CUSTOMER_PRICING_TAX_3, tax3);
        cv.put("customer_id", customer_id);
        cv.put("brand_id", brand_id);
        cv.put("product_type_id", product_type_id);
        cv.put("product_sub_type_id", product_sub_type_id);
        cv.put("min_qty", min_qty);
        cv.put("max_qty", max_qty);
        cv.put("multi", multi);
        cv.put("customer_category_id", customer_category_id);
        cv.put("customer_subcategory_id", customer_subcategory_id);
        cv.put("min_amount", min_amount);
        cv.put("max_amount", max_amount);
        cv.put(KEY_CUSTOMER_PRICING_TAX_FILER_1, filer1);
        cv.put(KEY_CUSTOMER_PRICING_TAX_FILER_2, filer2);
        cv.put(KEY_CUSTOMER_PRICING_TAX_FILER_3, filer3);
        cv.put("use_defaultprice", use_defaultprice);
        cv.put("mrp_price", mrp_price);
        cv.put("use_defaultmrp", use_defaultmrp);
        cv.put("emp_id", emp_id);
        cv.put("dist_id", dist_id);

        return db.insert(DATABASE_TABLE_CUSTOMER_PRICING, null, cv);

    }

    public ArrayList<HashMap<String, String>> getSaleOrderListByOrderId(String prodId) {

        // int typId = Integer.parseInt(typeId);
        int proId = Integer.parseInt(prodId);
        int total = 0;
        //int rowId = Integer.parseInt(row);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_CUSTOMER_ID + " = " + proId + " AND " + KEY_SALES_ORDER_TOTAL2 + " != 0 AND " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " != 0 ";

        Cursor c = db.rawQuery(selectQuery, null);


        int iOrderId = c.getColumnIndex(KEY_SALES_ORDER_ID);
        int iAmount = c.getColumnIndex(KEY_SALES_ORDER_TOTAL2);
        int iAmountRecieved = c.getColumnIndex(KEY_SALES_ORDER_AMOUNT_RECIEVED);


        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("orderId", c.getString(iOrderId));
                    map.put("amount", c.getString(iAmount));
                    map.put("amountRecieved", c.getString(iAmountRecieved));
                    total = (int) (Double.parseDouble(c.getString(iAmount)) - Double.parseDouble(c.getString(iAmountRecieved)));
                    map.put("total", String.valueOf(total));
                    map.put("recieving", "");


                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;


    }

    public String[] getBrandAndProductTypeAndSubType(String pid) {

        if (pid.equals("")) {
            pid = "0";
        }
        String query = "select type_id,brand_id,subtypeid from inventory where id = " + pid;
        String[] val = new String[3];
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            val[0] = cursor.getString(0);
            val[1] = cursor.getString(1);
            val[2] = cursor.getString(2);
        } else {
            val[0] = "0";
            val[1] = "0";
            val[2] = "0";
        }


        cursor.close();
        return val;
    }

    public String[] getCustomerTypeAndSubTypeID(String cid) {

        if (cid.equals("")) {
            cid = "0";
        }
        String query = "select shop_cat_id,sub_shop_cat_id from customer where id = " + cid;
        String[] val = new String[2];
        Cursor cursor = db.rawQuery(query, null);
        val[0] = "0";
        val[1] = "0";
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            val[0] = cursor.getString(0);
            val[1] = cursor.getString(1);
        }


        cursor.close();
        return val;
    }

    public String[] getProductTypeAndSubType(String pid) {

        if (pid.equals("")) {
            pid = "0";
        }
        String query = "select type_id,brand_id from inventory where id = " + pid;
        String[] val = new String[2];
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();

            val[0] = cursor.getString(0);
            val[1] = cursor.getString(1);
        }


        cursor.close();
        return val;
    }

    public int getPricingCount() {

        int a = 0;

        String s = "select COUNT(id) from cust_pricing";

        Cursor cursor = db.rawQuery(s, null);

        cursor.moveToFirst();

        if (cursor.moveToFirst()) {

            a = cursor.getInt(0);

        }

        cursor.close();

        return a;
    }

    public ArrayList<HashMap<String, String>> getPricingData(String typeId,

                                                             String prodId) {

        int proId = 0, typeID = 0;
        if (!typeId.equalsIgnoreCase(""))
            typeID = Integer.parseInt(typeId);
        if (!prodId.equalsIgnoreCase("") && prodId != null)
            proId = Integer.parseInt(prodId);        //int rowId = Integer.parseInt(row);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

//        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_CUSTOMER_PRICING + " WHERE " + KEY_CUSTOMER_PRICING_TYPE_ID + " = " + typeID + " AND " + KEY_CUSTOMER_PRICING_PRODUCT_ID + " = " + proId;
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_CUSTOMER_PRICING + " WHERE " + KEY_CUSTOMER_PRICING_TYPE_ID + " = " + typeID + " AND brand_id = " + proId;
//        selectQuery = "SELECT * FROM cust_pricing WHERE (type_id = 1 AND customer_id = 0) AND (product_id = 35 AND brand_id = 0 AND product_type_id = 0 AND product_sub_type_id = 0) AND ( (min_qty <= 3  OR min_qty IS NULL) AND ( max_qty >= 3 OR max_qty IS NULL));";
        Cursor c = db.rawQuery(selectQuery, null);


        int iTradePrice = c.getColumnIndex(KEY_CUSTOMER_PRICING_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_CUSTOMER_PRICING_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME);
        int iSchemeVal = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME_VAL);
        int iSchemeProduct = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME_PRODUCT);
        int iTax1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_3);
        int iTaxF1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_1);
        int iTaxF2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_2);
        int iTaxF3 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_3);
        int iSubTotal = c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("tradePrice", c.getString(iTradePrice));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("scheme", c.getString(iScheme));
                    map.put("schemeVal", c.getString(iSchemeVal));
                    map.put("schemeProduct", c.getString(iSchemeProduct));
                    map.put("tax1", c.getString(iTax1));
                    map.put("tax2", c.getString(iTax2));
                    map.put("tax3", c.getString(iTax3));

                    map.put("tax_filer_1", c.getString(iTaxF1));
                    map.put("tax_filer_2", c.getString(iTaxF2));
                    map.put("tax_filer_3", c.getString(iTaxF3));
                    map.put("subTotal", c.getString(iSubTotal));
                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != c && !c.isClosed()) {
                c.close();
            }

        }


        return result;


    }

    public ArrayList<HashMap<String, String>> getPricingData(String typeId,
                                                             String cust_id,
                                                             String brand_id,
                                                             String product_type_id,
                                                             String qty,
                                                             String prodId,
                                                             String cust_cat_id,
                                                             String cust_sub_cat_id,
                                                             String product_sub_type_id)
    {


        int proId1 = 0, typeID1 = 0;

        if (qty.equals("")) {
            qty = "0";
        }
        if (!typeId.equalsIgnoreCase(""))
            typeID1 = Integer.parseInt(typeId);
        if (!prodId.equalsIgnoreCase(""))
            proId1 = Integer.parseInt(prodId);        //int rowId = Integer.parseInt(row);

        if (cust_id.equals("")){
            cust_id = "0";
        }
        if (cust_cat_id.equals("")){
            cust_cat_id = "0";
        }
        if (cust_sub_cat_id.equals("")){
            cust_sub_cat_id = "0";
        }
        if (product_sub_type_id.equals("")){
            product_sub_type_id = "0";
        }

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery;
        int empid= Integer.parseInt(getMobEmpId());
        selectQuery =
                " SELECT * FROM cust_pricing " +
                        "WHERE " + "(','||emp_id||',' like '%,"+ empid +",%' OR ','||emp_id||',' like '%,0,%') " +
                        " AND (','||dist_id||',' like '%,"+getSavedDistributorList()+",%' OR ','||dist_id||',' like '%,0,%') " +
                        " AND ((','||type_id||',' like '%,"+typeID1+",%' OR ','||type_id||',' like '%,0,%')" + " " +
                        " AND (','||customer_id||',' like '%,"+ cust_id +",%' OR ','||customer_id||',' like '%,0,%'))" +
                        " AND (','||product_id||',' like '%,"+ proId1 +",%'  OR ','||product_id||',' like '%,0,%') " +
                        " AND (','||brand_id||',' like '%,"+ brand_id +",%' OR ','||brand_id||',' like '%,0,%') " +
                        " AND (','||product_type_id||',' like '%,"+ product_type_id +",%' OR ','||product_type_id||',' like '%,0,%') " +
                        " AND ((min_qty <= " + qty + " OR (min_qty IS NULL) OR (min_qty = 'NULL')" + "  OR (min_qty = 0) OR min_qty = -1)" +
                        " AND (max_qty >= " + qty + " OR max_qty IS NULL  OR (max_qty = 'NULL')  " + "OR (max_qty = 0) OR max_qty = -1))  " +
                        " AND ( multi = 0 ) " +
                        " AND (','||customer_category_id||',' like '%,"+ cust_cat_id +",%' " + " OR ','||customer_category_id||',' like '%,0,%' )  " +
                        " AND (','||customer_subcategory_id||',' like '%,"+ cust_sub_cat_id +",%' " + "OR ','||customer_subcategory_id||',' like '%,0,%')  " +
                        " AND (','||product_sub_type_id||',' like '%,"+ product_sub_type_id +",%' OR ','||product_sub_type_id||',' like '%,0,%') " +
                        "ORDER BY " +
                        "type_id DESC, " +
                        "customer_id DESC, " +
                        "product_id DESC, " +
                        "brand_id DESC, " +
                        "product_type_id DESC ," +
                        "product_sub_type_id DESC ," +
                        "customer_category_id DESC ," +
                        "customer_subcategory_id DESC, " +
                        "emp_id DESC,dist_id DESC " +
                        "LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);


        int iTradePrice = c.getColumnIndex(KEY_CUSTOMER_PRICING_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_CUSTOMER_PRICING_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME);
        int iSchemeVal = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME_VAL);
        int iSchemeProduct = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME_PRODUCT);
        int iTax1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_3);
        int iTaxF1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_1);
        int iTaxF2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_2);
        int iTaxF3 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_3);
        int iSubTotal = c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL);
        int iMulti = c.getColumnIndex("multi");
        int brandID = c.getColumnIndex("brand_id");
        int productTypeId = c.getColumnIndex("product_type_id");
        int productSubTypeId = c.getColumnIndex("product_sub_type_id");
        int min = c.getColumnIndex("min_qty");
        int max = c.getColumnIndex("max_qty");
        int p_id = c.getColumnIndex("product_id");
        int defaultValue = c.getColumnIndex("use_defaultprice");

        try {


            if (c.moveToFirst()) {
                HashMap<String, String> map = new HashMap<>();
                String q = "select sale_price,mrp_price,tax_mrp,is_taxable from inventory where id = " + proId1;
                Cursor cursor = db.rawQuery(q, null);
                Log.e("defaultValue", c.getString(defaultValue));
                if (c.getString(defaultValue).equals("1")) {


                    if (cursor.moveToFirst()) {
                        map.put("tradePrice", cursor.getString(0));

                    } else {
                        map.put("tradePrice", "0");

                    }

                } else {
                    map.put("tradePrice", c.getString(iTradePrice));
                }

                if (cursor.moveToFirst()) {
                    map.put("mrp_price", cursor.getString(1));
                    map.put("tax_mrp", cursor.getString(2));
                    map.put("is_taxable", cursor.getString(3));
                } else {
                    map.put("mrp_price", "0");
                    map.put("tax_mrp", "0");
                    map.put("is_taxable", "0");
                }

                cursor.close();

                map.put("discount1", c.getString(iDiscount1));
                map.put("discount2", c.getString(iDiscount2));
                map.put("tradeOffer", c.getString(iTradeOffer));
                map.put("scheme", c.getString(iScheme));
                map.put("schemeVal", c.getString(iSchemeVal));
                map.put("schemeProduct", c.getString(iSchemeProduct));
                map.put("tax1", c.getString(iTax1));
                map.put("tax2", c.getString(iTax2));
                map.put("tax3", c.getString(iTax3));

                map.put("tax_filer_1", c.getString(iTaxF1));
                map.put("tax_filer_2", c.getString(iTaxF2));
                map.put("tax_filer_3", c.getString(iTaxF3));
                map.put("subTotal", c.getString(iSubTotal));
                map.put("multi", c.getString(iMulti));
                map.put("brand_id", c.getString(brandID));
                map.put("product_type_id", c.getString(productTypeId));
                map.put("product_sub_type_id", c.getString(productSubTypeId));
                map.put("min", c.getString(min));
                map.put("max", c.getString(max));
                map.put("p_id", c.getString(p_id));


                result.add(map);
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        c.close();
        return result;


    }

    public ArrayList<HashMap<String, String>> getPricingDataforscheme(String typeId,
                                                             String cust_id,
                                                             String brand_id,
                                                             String product_type_id,
                                                             String qty,
                                                             String prodId,
                                                             String cust_cat_id,
                                                             String cust_sub_cat_id,
                                                             String product_sub_type_id)
    {


        int proId1 = 0, typeID1 = 0;

        if (qty.equals("")) {
            qty = "0";
        }
        if (!typeId.equalsIgnoreCase(""))
            typeID1 = Integer.parseInt(typeId);
        if (!prodId.equalsIgnoreCase(""))
            proId1 = Integer.parseInt(prodId);        //int rowId = Integer.parseInt(row);

        if (cust_id.equals("")){
            cust_id = "0";
        }
        if (cust_cat_id.equals("")){
            cust_cat_id = "0";
        }
        if (cust_sub_cat_id.equals("")){
            cust_sub_cat_id = "0";
        }
        if (product_sub_type_id.equals("")){
            product_sub_type_id = "0";
        }

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery;
int empid= Integer.parseInt(getMobEmpId());
        selectQuery =
                " SELECT * FROM cust_pricing " +
                        "WHERE " + "(','||emp_id||',' like '%,"+ empid +",%' OR ','||emp_id||',' like '%,0,%') " +
                        " AND (','||dist_id||',' like '%,"+getSavedDistributorList()+",%' OR ','||dist_id||',' like '%,0,%') " +
                        " AND ((','||type_id||',' like '%,"+typeID1+",%' OR ','||type_id||',' like '%,0,%')" + " " +
                        " AND (','||customer_id||',' like '%,"+ cust_id +",%' OR ','||customer_id||',' like '%,0,%'))" +
                        " AND (','||product_id||',' like '%,"+ proId1 +",%'  OR ','||product_id||',' like '%,0,%') " +
                        " AND (','||brand_id||',' like '%,"+ brand_id +",%' OR ','||brand_id||',' like '%,0,%') " +
                        " AND (','||product_type_id||',' like '%,"+ product_type_id +",%' OR ','||product_type_id||',' like '%,0,%') " +
                        " AND ((min_qty <= " + qty + " OR (min_qty IS NULL) OR (min_qty = 'NULL')" + "  OR (min_qty = 0) OR min_qty = -1)" +
                        " AND (max_qty >= " + qty + " OR max_qty IS NULL  OR (max_qty = 'NULL')  " + "OR (max_qty = 0) OR max_qty = -1))  " +
                        " AND (multi = 1 ) " +
                        " AND (','||customer_category_id||',' like '%,"+ cust_cat_id +",%' " + " OR ','||customer_category_id||',' like '%,0,%' )  " +
                        " AND (','||customer_subcategory_id||',' like '%,"+ cust_sub_cat_id +",%' " + "OR ','||customer_subcategory_id||',' like '%,0,%')  " +
                        " AND (','||product_sub_type_id||',' like '%,"+ product_sub_type_id +",%' OR ','||product_sub_type_id||',' like '%,0,%') " +
                        "ORDER BY " +
                        "type_id DESC, " +
                        "customer_id DESC, " +
                        "product_id DESC, " +
                        "brand_id DESC, " +
                        "product_type_id DESC ," +
                        "product_sub_type_id DESC ," +
                        "customer_category_id DESC ," +
                        "customer_subcategory_id DESC, " +
                        "emp_id DESC,dist_id DESC " +
                        "LIMIT 1;";

        Cursor c = db.rawQuery(selectQuery, null);


        int iTradePrice = c.getColumnIndex(KEY_CUSTOMER_PRICING_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_CUSTOMER_PRICING_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME);
        int iSchemeVal = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME_VAL);
        int iSchemeProduct = c.getColumnIndex(KEY_CUSTOMER_PRICING_SCHEME_PRODUCT);
        int iTax1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_3);
        int iTaxF1 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_1);
        int iTaxF2 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_2);
        int iTaxF3 = c.getColumnIndex(KEY_CUSTOMER_PRICING_TAX_FILER_3);
        int iSubTotal = c.getColumnIndex(KEY_CUSTOMER_PRICING_SUBTOTAL);
        int iMulti = c.getColumnIndex("multi");
        int brandID = c.getColumnIndex("brand_id");
        int productTypeId = c.getColumnIndex("product_type_id");
        int productSubTypeId = c.getColumnIndex("product_sub_type_id");
        int min = c.getColumnIndex("min_qty");
        int max = c.getColumnIndex("max_qty");
        int p_id = c.getColumnIndex("product_id");
        int defaultValue = c.getColumnIndex("use_defaultprice");

        try {


            if (c.moveToFirst()) {
                HashMap<String, String> map = new HashMap<>();
                String q = "select sale_price,mrp_price,tax_mrp,is_taxable from inventory where id = " + proId1;
                Cursor cursor = db.rawQuery(q, null);
                Log.e("defaultValue", c.getString(defaultValue));
                if (c.getString(defaultValue).equals("1")) {


                    if (cursor.moveToFirst()) {
                        map.put("tradePrice", cursor.getString(0));

                    } else {
                        map.put("tradePrice", "0");

                    }

                } else {
                    map.put("tradePrice", c.getString(iTradePrice));
                }

                if (cursor.moveToFirst()) {
                    map.put("mrp_price", cursor.getString(1));
                    map.put("tax_mrp", cursor.getString(2));
                    map.put("is_taxable", cursor.getString(3));
                } else {
                    map.put("mrp_price", "0");
                    map.put("tax_mrp", "0");
                    map.put("is_taxable", "0");
                }

                cursor.close();

                map.put("discount1", c.getString(iDiscount1));
                map.put("discount2", c.getString(iDiscount2));
                map.put("tradeOffer", c.getString(iTradeOffer));
                map.put("scheme", c.getString(iScheme));
                map.put("schemeVal", c.getString(iSchemeVal));
                map.put("schemeProduct", c.getString(iSchemeProduct));
                map.put("tax1", c.getString(iTax1));
                map.put("tax2", c.getString(iTax2));
                map.put("tax3", c.getString(iTax3));

                map.put("tax_filer_1", c.getString(iTaxF1));
                map.put("tax_filer_2", c.getString(iTaxF2));
                map.put("tax_filer_3", c.getString(iTaxF3));
                map.put("subTotal", c.getString(iSubTotal));
                map.put("multi", c.getString(iMulti));
                map.put("brand_id", c.getString(brandID));
                map.put("product_type_id", c.getString(productTypeId));
                map.put("product_sub_type_id", c.getString(productSubTypeId));
                map.put("min", c.getString(min));
                map.put("max", c.getString(max));
                map.put("p_id", c.getString(p_id));


                result.add(map);
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        c.close();
        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetailAgainstProduct(String orderId, String prodId) {

        int typId = Integer.parseInt(orderId);
        int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_ORDER_DETAILS + " WHERE " + KEY_ORDER_DETAIL_ORDER_ID + " = " + typId + " AND " + KEY_ORDER_DETAIL_PRODUCT_ID + " = " + proId;

        Cursor c = db.rawQuery(selectQuery, null);

        int iTradePrice = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME);
        int iSchemeQty = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME_QTY);
        int iSchemeFormula = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME_FORMULA);
        int iSchemeProduct = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME_PRODUCT);
        int iTax1 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_3);


        int iSubTotal = c.getColumnIndex(KEY_ORDER_DETAIL_SUBTOTAL);

        try {

            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("tradePrice", c.getString(iTradePrice));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("scheme", c.getString(iScheme));
                    map.put("schemeQty", c.getString(iSchemeQty));
                    map.put("schemeFormula", c.getString(iSchemeFormula));
                    map.put("schemeProduct", c.getString(iSchemeProduct));
                    map.put("tax1", c.getString(iTax1));
                    map.put("tax2", c.getString(iTax2));
                    map.put("tax3", c.getString(iTax3));

                    map.put("subTotal", c.getString(iSubTotal));
                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetail(String orderId) {

        int typId = Integer.parseInt(orderId);
        // int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_ORDER_DETAILS + " WHERE " + KEY_ORDER_DETAIL_ORDER_ID + " = " + typId;

        Cursor c = db.rawQuery(selectQuery, null);


        int iProdId = c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID);
        int iQty = c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY);
        int iQtyExe = c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY_EXE);
        int iTradePrice = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME);
        int iSchemeQty = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME_QTY);
        int iSchemeFormula = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME_FORMULA);
        int iSchemeProduct = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME_PRODUCT);
        int iTax1 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_3);
        int iSubTotal = c.getColumnIndex(KEY_ORDER_DETAIL_SUBTOTAL);
        int multi_scheme = c.getColumnIndex("multi_scheme");
        int t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, mrp_price;

        t_o_v = c.getColumnIndex("t_o_v");
        d_v_1 = c.getColumnIndex("d_v_1");
        d_v_2 = c.getColumnIndex("d_v_2");
        t_type = c.getColumnIndex("t_type");
        t_mrp_type = c.getColumnIndex("t_mrp_type");
        t_val = c.getColumnIndex("t_val");
        mrp_price = c.getColumnIndex("mrp_price");

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", c.getString(iProdId));
                    map.put("qty", c.getString(iQty));
                    map.put("qtyExe", c.getString(iQtyExe));
                    map.put("tradePrice", c.getString(iTradePrice));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("scheme", c.getString(iScheme));
                    map.put("schemeQty", c.getString(iSchemeQty));
                    map.put("schemeFormula", c.getString(iSchemeFormula));
                    map.put("schemeProduct", c.getString(iSchemeProduct));
                    map.put("tax1", c.getString(iTax1));
                    map.put("tax2", c.getString(iTax2));
                    map.put("tax3", c.getString(iTax3));
                    map.put("subTotal", c.getString(iSubTotal));
                    map.put("multi_scheme", c.getString(multi_scheme));
                    map.put("t_o_v", c.getString(t_o_v));
                    map.put("d_v_1", c.getString(d_v_1));
                    map.put("d_v_2", c.getString(d_v_2));
                    map.put("t_type", c.getString(t_type));
                    map.put("t_mrp_type", c.getString(t_mrp_type));
                    map.put("t_val", c.getString(t_val));
                    map.put("mrp_price", c.getString(mrp_price));

                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetailForInvoice(String orderId) {

        int typId = Integer.parseInt(orderId);
        // int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        // "SELECT od."+KEY_ORDER_DETAIL_PRODUCT_ID+" as productId, od."+KEY_ORDER_DETAIL_QUANTITY+" as qty, od."+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qtyExe, od."+KEY_ORDER_DETAIL_ORDER_ID+" as oid, so."+KEY_SALES_ORDER_ID+" as oid, so."+KEY_SALES_ORDER_TOTAL2+" as tot2, so."+KEY_SALES_ORDER_TOTAL_EXECUTE+" as tot_exe ,c."+KEY_CUSTOMER_FNAME+" as fn, c."+KEY_CUSTOMER_LNAME+" as ln FROM "+DATABASE_TABLE_ORDER_DETAILS+" od, "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" so, "+DATABASE_TABLE_CUSTOMER_DETAILS+" c WHERE c."+KEY_ID_CUSTOMER+" = so."+KEY_SALES_ORDER_CUSTOMER_ID+" AND so."+KEY_SALES_ORDER_ID+" = od."+KEY_ORDER_DETAIL_ORDER_ID+" AND od."+KEY_ORDER_DETAIL_ORDER_ID+" = "+typId;

        String selectQuery = "SELECT od.*, i." + KEY_INVENT_NAME + " as invName, i. " + KEY_SKU + " as invSku FROM " + DATABASE_TABLE_ORDER_DETAILS + " od, " + DATABASE_TABLE_INVENTORY + " i WHERE od." + KEY_ORDER_DETAIL_ORDER_ID + " = " + typId + " AND od." + KEY_ORDER_DETAIL_PRODUCT_ID + " = i." + KEY_ID_INVENT;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("QueryInvoice", selectQuery);
        int iProdId = c.getColumnIndex(KEY_ORDER_DETAIL_PRODUCT_ID);
        int iQty = c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY);
        int iQtyExe = c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY_EXE);
        int iTradePrice = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME);
        int iTax1 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_3);
        int iSubTotal = c.getColumnIndex(KEY_ORDER_DETAIL_SUBTOTAL);
        int invName = c.getColumnIndex("invName");
        int invSku = c.getColumnIndex("invSku");

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", c.getString(iProdId));
                    map.put("productName", getSelectedItemName(String.valueOf(c.getString(iProdId))));
                    map.put("qty", c.getString(iQty));
                    map.put("qtyExe", c.getString(iQtyExe));
                    map.put("tradePrice", c.getString(iTradePrice));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("scheme", c.getString(iScheme));
                    map.put("tax1", c.getString(iTax1));
                    map.put("tax2", c.getString(iTax2));
                    map.put("tax3", c.getString(iTax3));
                    map.put("subTotal", c.getString(iSubTotal));
                    map.put("invName", c.getString(invName));
                    map.put("invSku", c.getString(invSku));
                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }

    public ArrayList<HashMap<String, String>> getReturnDetailForInvoice(String orderId) {

        int typId = Integer.parseInt(orderId);
        // int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        // "SELECT od."+KEY_ORDER_DETAIL_PRODUCT_ID+" as productId, od."+KEY_ORDER_DETAIL_QUANTITY+" as qty, od."+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qtyExe, od."+KEY_ORDER_DETAIL_ORDER_ID+" as oid, so."+KEY_SALES_ORDER_ID+" as oid, so."+KEY_SALES_ORDER_TOTAL2+" as tot2, so."+KEY_SALES_ORDER_TOTAL_EXECUTE+" as tot_exe ,c."+KEY_CUSTOMER_FNAME+" as fn, c."+KEY_CUSTOMER_LNAME+" as ln FROM "+DATABASE_TABLE_ORDER_DETAILS+" od, "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" so, "+DATABASE_TABLE_CUSTOMER_DETAILS+" c WHERE c."+KEY_ID_CUSTOMER+" = so."+KEY_SALES_ORDER_CUSTOMER_ID+" AND so."+KEY_SALES_ORDER_ID+" = od."+KEY_ORDER_DETAIL_ORDER_ID+" AND od."+KEY_ORDER_DETAIL_ORDER_ID+" = "+typId;

        String selectQuery = "SELECT od.*, i." + KEY_INVENT_NAME + " as invName, i. " + KEY_SKU + " as invSku FROM " + DATABASE_TABLE_RETURN_DETAILS + " od, " + DATABASE_TABLE_INVENTORY + " i WHERE od." + KEY_RETURN_DETAIL_ORDER_ID + " = " + typId + " AND od." + KEY_RETURN_DETAIL_PRODUCT_ID + " = i." + KEY_ID_INVENT;

        Cursor c = db.rawQuery(selectQuery, null);

        Log.d("QueryRetDetInvoice", selectQuery);
        int iProdId = c.getColumnIndex(KEY_RETURN_DETAIL_PRODUCT_ID);
        int iQty = c.getColumnIndex(KEY_RETURN_DETAIL_QUANTITY);
        int iQtyExe = c.getColumnIndex(KEY_RETURN_DETAIL_QUANTITY_EXE);
        int iTradePrice = c.getColumnIndex(KEY_RETURN_DETAIL_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_RETURN_DETAIL_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_RETURN_DETAIL_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_RETURN_DETAIL_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME);
        int iTax1 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_3);
        int iSubTotal = c.getColumnIndex(KEY_RETURN_DETAIL_SUBTOTAL);
        int invName = c.getColumnIndex("invName");
        int invSku = c.getColumnIndex("invSku");

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", c.getString(iProdId));
                    map.put("productName", getSelectedItemName(String.valueOf(c.getString(iProdId))));
                    map.put("qty", c.getString(iQty));
                    map.put("qtyExe", c.getString(iQtyExe));
                    map.put("tradePrice", c.getString(iTradePrice));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("scheme", c.getString(iScheme));
                    map.put("tax1", c.getString(iTax1));
                    map.put("tax2", c.getString(iTax2));
                    map.put("tax3", c.getString(iTax3));
                    map.put("subTotal", c.getString(iSubTotal));
                    map.put("invName", c.getString(invName));
                    map.put("invSku", c.getString(invSku));
                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }

    public ArrayList<HashMap<String, String>> getReturnDetail(String orderId) {

        int typId = Integer.parseInt(orderId);
        // int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_RETURN_DETAILS + " WHERE " + KEY_RETURN_DETAIL_ORDER_ID + " = " + typId;

        Cursor c = db.rawQuery(selectQuery, null);


        int iProdId = c.getColumnIndex(KEY_RETURN_DETAIL_PRODUCT_ID);
        int iQty = c.getColumnIndex(KEY_RETURN_DETAIL_QUANTITY);
        int iQtyExe = c.getColumnIndex(KEY_RETURN_DETAIL_QUANTITY_EXE);
        int iTradePrice = c.getColumnIndex(KEY_RETURN_DETAIL_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_RETURN_DETAIL_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_RETURN_DETAIL_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_RETURN_DETAIL_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME);
        int iSchemeQty = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME_QTY);
        int iSchemeFormula = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME_FORMULA);
        int iSchemeProduct = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME_PRODUCT);
        int iTax1 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_3);
        int iSubTotal = c.getColumnIndex(KEY_RETURN_DETAIL_SUBTOTAL);
        int multi_scheme = c.getColumnIndex("multi_scheme");
        int t_o_v, d_v_1, d_v_2, t_type, t_mrp_type, t_val, mrp_price;

        t_o_v = c.getColumnIndex("t_o_v");
        d_v_1 = c.getColumnIndex("d_v_1");
        d_v_2 = c.getColumnIndex("d_v_2");
        t_type = c.getColumnIndex("t_type");
        t_mrp_type = c.getColumnIndex("t_mrp_type");
        t_val = c.getColumnIndex("t_val");
        mrp_price = c.getColumnIndex("mrp_price");

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", c.getString(iProdId));
                    map.put("qty", c.getString(iQty));
                    map.put("qtyExe", c.getString(iQtyExe));
                    map.put("tradePrice", c.getString(iTradePrice));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("scheme", c.getString(iScheme));
                    map.put("schemeQty", c.getString(iSchemeQty));
                    map.put("schemeFormula", c.getString(iSchemeFormula));
                    map.put("schemeProduct", c.getString(iSchemeProduct));
                    map.put("tax1", c.getString(iTax1));
                    map.put("tax2", c.getString(iTax2));
                    map.put("tax3", c.getString(iTax3));
                    map.put("subTotal", c.getString(iSubTotal));
                    map.put("multi_scheme", c.getString(multi_scheme));
                    map.put("t_o_v", c.getString(t_o_v));
                    map.put("d_v_1", c.getString(d_v_1));
                    map.put("d_v_2", c.getString(d_v_2));
                    map.put("t_type", c.getString(t_type));
                    map.put("t_mrp_type", c.getString(t_mrp_type));
                    map.put("t_val", c.getString(t_val));
                    map.put("mrp_price", c.getString(mrp_price));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }

    public ArrayList<HashMap<String, String>> getReturnDetailAgainstProduct(String orderId, String prodId) {

        int typId = Integer.parseInt(orderId);
        int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_RETURN_DETAILS + " WHERE " + KEY_RETURN_DETAIL_ORDER_ID + " = " + typId + " AND " + KEY_RETURN_DETAIL_PRODUCT_ID + " = " + proId;

        Cursor c = db.rawQuery(selectQuery, null);


        int iTradePrice = c.getColumnIndex(KEY_RETURN_DETAIL_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_RETURN_DETAIL_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_RETURN_DETAIL_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_RETURN_DETAIL_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME);
        int iSchemeQty = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME_QTY);
        int iSchemeFormula = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME_FORMULA);
        int iSchemeProduct = c.getColumnIndex(KEY_RETURN_DETAIL_SCHEME_PRODUCT);
        int iTax1 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_RETURN_DETAIL_TAX_3);
        int iSubTotal = c.getColumnIndex(KEY_RETURN_DETAIL_SUBTOTAL);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("tradePrice", c.getString(iTradePrice));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("scheme", c.getString(iScheme));
                    map.put("schemeQty", c.getString(iSchemeQty));
                    map.put("schemeFormula", c.getString(iSchemeFormula));
                    map.put("schemeProduct", c.getString(iSchemeProduct));
                    map.put("tax1", c.getString(iTax1));
                    map.put("tax2", c.getString(iTax2));
                    map.put("tax3", c.getString(iTax3));
                    map.put("subTotal", c.getString(iSubTotal));
                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetailForEditList(String orderId) {

        int typId = Integer.parseInt(orderId);
        //int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery =
                "SELECT od.multi_scheme  as multi_scheme , od." + KEY_ORDER_DETAIL_PRODUCT_ID +
                        " as productId, od." + KEY_ORDER_DETAIL_QUANTITY + " as qty, od." + KEY_ORDER_DETAIL_QUANTITY_EXE + " as qtyExe, od." + KEY_ORDER_DETAIL_SCHEME_QTY + " as schemeQty, od." + KEY_ORDER_DETAIL_DISCOUNT_1 + " as discount1, od." + KEY_ORDER_DETAIL_DISCOUNT_2 + " as discount2, od." + KEY_ORDER_DETAIL_TRADE_OFFER + " as tradeOffer, od." + KEY_ORDER_DETAIL_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_TOTAL2 + " as tot2, so." + KEY_SALES_ORDER_TOTAL_EXECUTE + " as tot_exe ,c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " as ln, c.id as cust_id FROM " + DATABASE_TABLE_ORDER_DETAILS + " od, " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so, " + DATABASE_TABLE_CUSTOMER_DETAILS + " c WHERE c." + KEY_ID_CUSTOMER + " = so." + KEY_SALES_ORDER_CUSTOMER_ID + " AND so." + KEY_SALES_ORDER_ID + " = od." + KEY_ORDER_DETAIL_ORDER_ID + " AND od." + KEY_ORDER_DETAIL_ORDER_ID + " = " + typId + "  AND od.multi_scheme = 0  ORDER BY od.multi_scheme ASC ,od.quantity_exe ASC;";

        Log.e("GetOrderDetails", selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);


        int iProdId = c.getColumnIndex("productId");
        int iQty = c.getColumnIndex("qty");
        int iQtyExe = c.getColumnIndex("qtyExe");
        int iSchemeQty = c.getColumnIndex("schemeQty");
        int iTot2 = c.getColumnIndex("tot2");
        int iTotExe = c.getColumnIndex("tot_exe");
        int iDiscount1 = c.getColumnIndex("discount1");
        int iDiscount2 = c.getColumnIndex("discount2");
        int iTradeOffer = c.getColumnIndex("tradeOffer");
        int iCustId = c.getColumnIndex("cust_id");
        int multi_scheme = c.getColumnIndex("multi_scheme");

        try {
            if (c.moveToFirst()) {
                do {
                    if (!getSelectedItemName(String.valueOf(c.getString(iProdId))).equals("")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("productId", c.getString(iProdId));
                        map.put("productName", getSelectedItemName(String.valueOf(c.getString(iProdId))));
                        map.put("qty", String.format("%.2f",
                                (Double.parseDouble(c.getString(iQty)) - Double.parseDouble(c.getString(iSchemeQty)))));
                        map.put("qtyExe", String.format("%.2f", (Double.parseDouble(c.getString(iQtyExe)) - Double.parseDouble(c.getString(iSchemeQty)))));
                        map.put("schemeQty", c.getString(iSchemeQty));
                        map.put("discount1", c.getString(iDiscount1));
                        map.put("discount2", c.getString(iDiscount2));
                        map.put("tradeOffer", c.getString(iTradeOffer));
                        map.put("cust_id", c.getString(iCustId));
                        Log.e("multi_scheme", c.getString(multi_scheme));
                        map.put("multi_scheme", c.getString(multi_scheme));

                        result.add(map);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }
        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetailForEditListView(String orderId) {

        int typId = Integer.parseInt(orderId);
        //int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery =
                "SELECT od.multi_scheme  as multi_scheme , od." + KEY_ORDER_DETAIL_PRODUCT_ID +
                        " as productId, od." + KEY_ORDER_DETAIL_QUANTITY + " as qty, od." + KEY_ORDER_DETAIL_QUANTITY_EXE + " as qtyExe, od." + KEY_ORDER_DETAIL_SCHEME_QTY + " as schemeQty, od." + KEY_ORDER_DETAIL_DISCOUNT_1 + " as discount1, od." + KEY_ORDER_DETAIL_DISCOUNT_2 + " as discount2, od." + KEY_ORDER_DETAIL_TRADE_OFFER + " as tradeOffer, od." + KEY_ORDER_DETAIL_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_TOTAL2 + " as tot2, so." + KEY_SALES_ORDER_TOTAL_EXECUTE + " as tot_exe ,c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " as ln, c.id as cust_id FROM " + DATABASE_TABLE_ORDER_DETAILS + " od, " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so, " + DATABASE_TABLE_CUSTOMER_DETAILS + " c WHERE c." + KEY_ID_CUSTOMER + " = so." + KEY_SALES_ORDER_CUSTOMER_ID + " AND so." + KEY_SALES_ORDER_ID + " = od." + KEY_ORDER_DETAIL_ORDER_ID + " AND od." + KEY_ORDER_DETAIL_ORDER_ID + " = " + typId + " ORDER BY od.multi_scheme ASC ,od.quantity_exe ASC;";

        Log.e("GetOrderDetails", selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);


        int iProdId = c.getColumnIndex("productId");
        int iQty = c.getColumnIndex("qty");
        int iQtyExe = c.getColumnIndex("qtyExe");
        int iSchemeQty = c.getColumnIndex("schemeQty");
        int iTot2 = c.getColumnIndex("tot2");
        int iTotExe = c.getColumnIndex("tot_exe");
        int iDiscount1 = c.getColumnIndex("discount1");
        int iDiscount2 = c.getColumnIndex("discount2");
        int iTradeOffer = c.getColumnIndex("tradeOffer");
        int iCustId = c.getColumnIndex("cust_id");
        int multi_scheme = c.getColumnIndex("multi_scheme");

        try {
            if (c.moveToFirst()) {
                do {
                    if (!getSelectedItemName(String.valueOf(c.getString(iProdId))).equals("")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("productId", c.getString(iProdId));
                        map.put("productName", getSelectedItemName(String.valueOf(c.getString(iProdId))));
                        map.put("qty", String.format("%.2f",
                                (Double.parseDouble(c.getString(iQty)) - Double.parseDouble(c.getString(iSchemeQty)))));
                        map.put("qtyExe", String.format("%.2f", (Double.parseDouble(c.getString(iQtyExe)) - Double.parseDouble(c.getString(iSchemeQty)))));
                        map.put("schemeQty", c.getString(iSchemeQty));
                        map.put("discount1", c.getString(iDiscount1));
                        map.put("discount2", c.getString(iDiscount2));
                        map.put("tradeOffer", c.getString(iTradeOffer));
                        map.put("cust_id", c.getString(iCustId));
                        Log.e("multi_scheme", c.getString(multi_scheme));
                        map.put("multi_scheme", c.getString(multi_scheme));

                        result.add(map);
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }
        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetailReturnForEditList(String orderId) {

        int typId = Integer.parseInt(orderId);
        //int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        // String selectQuery = "SELECT rd."+KEY_RETURN_DETAIL_PRODUCT_ID+" as productId, rd."+KEY_RETURN_DETAIL_QUANTITY+" as qty, rd."+KEY_RETURN_DETAIL_QUANTITY_EXE+" as qtyExe, rd."+KEY_RETURN_DETAIL_ORDER_ID+" as oid, sr."+KEY_SALES_RETURN_ID+" as rid, sr."+KEY_SALES_RETURN_CONFIRM+" as conf, sr."+KEY_SALES_RETURN_TOTAL2+" as tot_exe, c."+KEY_CUSTOMER_FNAME+" as fn, c."+KEY_CUSTOMER_LNAME+" as ln FROM "+DATABASE_TABLE_RETURN_DETAILS+" rd, "+DATABASE_TABLE_SALESMAN_SALES_RETURN+" sr, "+DATABASE_TABLE_CUSTOMER_DETAILS+" c WHERE c."+KEY_ID_CUSTOMER+" = sr."+KEY_SALES_RETURN_CUSTOMER_ID+" AND sr."+KEY_SALES_RETURN_ID+" = rd."+KEY_RETURN_DETAIL_ORDER_ID+" AND rd."+KEY_RETURN_DETAIL_ORDER_ID+" = "+typId;

        String selectQuery =
                "SELECT rd.multi_scheme  as multi_scheme ,c.id as cust_id,rd." + KEY_RETURN_DETAIL_PRODUCT_ID + " as productId, rd" +
                        "." + KEY_RETURN_DETAIL_QUANTITY + " as qty, rd." + KEY_RETURN_DETAIL_QUANTITY_EXE + " as qtyExe, rd." + KEY_RETURN_DETAIL_SCHEME_QTY + " as schemeQty, rd." + KEY_RETURN_DETAIL_DISCOUNT_1 + " as discount1,rd. " + KEY_RETURN_DETAIL_DISCOUNT_2 + "  as discount2, rd." + KEY_RETURN_DETAIL_TRADE_OFFER + " as tradeOffer, rd." + KEY_RETURN_DETAIL_ORDER_ID + " as oid, sr." + KEY_SALES_RETURN_ID + " as rid, c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " as ln FROM " + DATABASE_TABLE_RETURN_DETAILS + " rd, " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr, " + DATABASE_TABLE_CUSTOMER_DETAILS + " c WHERE c." + KEY_ID_CUSTOMER + " = sr." + KEY_SALES_RETURN_CUSTOMER_ID + " AND sr." + KEY_SALES_RETURN_ID + " = rd." + KEY_RETURN_DETAIL_ORDER_ID + " AND rd." + KEY_RETURN_DETAIL_ORDER_ID + " = " + typId + "  AND rd.multi_scheme = 0 ORDER BY rd.multi_scheme ASC, rd.quantity_exe ASC;";

        Cursor c = db.rawQuery(selectQuery, null);


        int iCustId = c.getColumnIndex("cust_id");
        int iProdId = c.getColumnIndex("productId");
        int iQty = c.getColumnIndex("qty");
        int iQtyExe = c.getColumnIndex("qtyExe");
        int iSchemeQty = c.getColumnIndex("schemeQty");
        int iDiscount1 = c.getColumnIndex("discount1");
        int iDiscount2 = c.getColumnIndex("discount2");
        int iTot2 = c.getColumnIndex("tot2");
        int iTotExe = c.getColumnIndex("tot_exe");
        int iTradeOffer = c.getColumnIndex("tradeOffer");
        int multi_scheme = c.getColumnIndex("multi_scheme");
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", c.getString(iProdId));
                    map.put("productName", getSelectedItemName(String.valueOf(c.getString(iProdId))));
                    map.put("qty", String.format("%.2f", (Double.parseDouble(c.getString(iQty)) - Double.parseDouble(c.getString(iSchemeQty)))));
                    map.put("qtyExe", String.format("%.2f", (Double.parseDouble(c.getString(iQtyExe)) - Double.parseDouble(c.getString(iSchemeQty)))));
                    map.put("schemeQty", c.getString(iSchemeQty));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("cust_id", c.getString(iCustId));
                    Log.e("multi_scheme", c.getString(multi_scheme));
                    map.put("multi_scheme", c.getString(multi_scheme));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        Log.d("Ret_Det QUERY", selectQuery);
        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetailReturnForEditListView(String orderId) {

        int typId = Integer.parseInt(orderId);
        //int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        // String selectQuery = "SELECT rd."+KEY_RETURN_DETAIL_PRODUCT_ID+" as productId, rd."+KEY_RETURN_DETAIL_QUANTITY+" as qty, rd."+KEY_RETURN_DETAIL_QUANTITY_EXE+" as qtyExe, rd."+KEY_RETURN_DETAIL_ORDER_ID+" as oid, sr."+KEY_SALES_RETURN_ID+" as rid, sr."+KEY_SALES_RETURN_CONFIRM+" as conf, sr."+KEY_SALES_RETURN_TOTAL2+" as tot_exe, c."+KEY_CUSTOMER_FNAME+" as fn, c."+KEY_CUSTOMER_LNAME+" as ln FROM "+DATABASE_TABLE_RETURN_DETAILS+" rd, "+DATABASE_TABLE_SALESMAN_SALES_RETURN+" sr, "+DATABASE_TABLE_CUSTOMER_DETAILS+" c WHERE c."+KEY_ID_CUSTOMER+" = sr."+KEY_SALES_RETURN_CUSTOMER_ID+" AND sr."+KEY_SALES_RETURN_ID+" = rd."+KEY_RETURN_DETAIL_ORDER_ID+" AND rd."+KEY_RETURN_DETAIL_ORDER_ID+" = "+typId;

        String selectQuery =
                "SELECT rd.multi_scheme  as multi_scheme ,c.id as cust_id,rd." + KEY_RETURN_DETAIL_PRODUCT_ID + " as productId, rd" +
                        "." + KEY_RETURN_DETAIL_QUANTITY + " as qty, rd." + KEY_RETURN_DETAIL_QUANTITY_EXE + " as qtyExe, rd." + KEY_RETURN_DETAIL_SCHEME_QTY + " as schemeQty, rd." + KEY_RETURN_DETAIL_DISCOUNT_1 + " as discount1,rd. " + KEY_RETURN_DETAIL_DISCOUNT_2 + "  as discount2, rd." + KEY_RETURN_DETAIL_TRADE_OFFER + " as tradeOffer, rd." + KEY_RETURN_DETAIL_ORDER_ID + " as oid, sr." + KEY_SALES_RETURN_ID + " as rid, c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " as ln FROM " + DATABASE_TABLE_RETURN_DETAILS + " rd, " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr, " + DATABASE_TABLE_CUSTOMER_DETAILS + " c WHERE c." + KEY_ID_CUSTOMER + " = sr." + KEY_SALES_RETURN_CUSTOMER_ID + " AND sr." + KEY_SALES_RETURN_ID + " = rd." + KEY_RETURN_DETAIL_ORDER_ID + " AND rd." + KEY_RETURN_DETAIL_ORDER_ID + " = " + typId + "  ORDER BY rd.multi_scheme ASC, rd.quantity_exe ASC;";

        Cursor c = db.rawQuery(selectQuery, null);


        int iCustId = c.getColumnIndex("cust_id");
        int iProdId = c.getColumnIndex("productId");
        int iQty = c.getColumnIndex("qty");
        int iQtyExe = c.getColumnIndex("qtyExe");
        int iSchemeQty = c.getColumnIndex("schemeQty");
        int iDiscount1 = c.getColumnIndex("discount1");
        int iDiscount2 = c.getColumnIndex("discount2");
        int iTot2 = c.getColumnIndex("tot2");
        int iTotExe = c.getColumnIndex("tot_exe");
        int iTradeOffer = c.getColumnIndex("tradeOffer");
        int multi_scheme = c.getColumnIndex("multi_scheme");
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", c.getString(iProdId));
                    map.put("productName", getSelectedItemName(String.valueOf(c.getString(iProdId))));
                    map.put("qty", String.format("%.2f", (Double.parseDouble(c.getString(iQty)) - Double.parseDouble(c.getString(iSchemeQty)))));
                    map.put("qtyExe", String.format("%.2f", (Double.parseDouble(c.getString(iQtyExe)) - Double.parseDouble(c.getString(iSchemeQty)))));
                    map.put("schemeQty", c.getString(iSchemeQty));
                    map.put("discount1", c.getString(iDiscount1));
                    map.put("discount2", c.getString(iDiscount2));
                    map.put("tradeOffer", c.getString(iTradeOffer));
                    map.put("cust_id", c.getString(iCustId));
                    Log.e("multi_scheme", c.getString(multi_scheme));
                    map.put("multi_scheme", c.getString(multi_scheme));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        Log.d("Ret_Det QUERY", selectQuery);
        return result;


    }

    public ArrayList<HashMap<String, String>> getOrderDetailExecuteForEditList(String orderId) {

        int typId = Integer.parseInt(orderId);
        //int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        // String selectQuery = "SELECT c."+KEY_CUSTOMER_FNAME+" as fn, c."+KEY_CUSTOMER_LNAME+" as ln, so."+KEY_SALES_ORDER_ID+" as oid, so."+KEY_SALES_ORDER_CONFIRM+" as conf, so."+KEY_SALES_ORDER_VALUES+" as val, so."+KEY_SALES_ORDER_TOTAL2+" as tot2, so."+KEY_SALES_ORDER_TOTAL_EXECUTE+" as tot_exe FROM "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" so, "+DATABASE_TABLE_CUSTOMER_DETAILS+" c WHERE c."+KEY_ID_CUSTOMER+" = so."+KEY_SALES_ORDER_CUSTOMER_ID+" AND so."+KEY_SALES_ORDER_DELETE+" = 0 AND so."+KEY_SALES_ORDER_ID+" = "+orderID;

        /// WHERE c."+KEY_ID_CUSTOMER+" = so."+KEY_SALES_ORDER_CUSTOMER_ID+" AND od."+KEY_ORDER_DETAIL_ORDER_ID+" = "+typId;

        String selectQuery = "SELECT od." + KEY_ORDER_DETAIL_PRODUCT_ID + " as productId, od." + KEY_ORDER_DETAIL_QUANTITY + " as qty, od." + KEY_ORDER_DETAIL_QUANTITY_EXE + " as qtyExe, od." + KEY_ORDER_DETAIL_SCHEME_QTY + " as schemeQty, od." + KEY_ORDER_DETAIL_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_TOTAL2 + " as tot2, so." + KEY_SALES_ORDER_TOTAL_EXECUTE + " as tot_exe ,c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " as ln FROM " + DATABASE_TABLE_ORDER_DETAILS + " od, " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so, " + DATABASE_TABLE_CUSTOMER_DETAILS + " c WHERE c." + KEY_ID_CUSTOMER + " = so." + KEY_SALES_ORDER_CUSTOMER_ID + " AND so." + KEY_SALES_ORDER_ID + " = od." + KEY_ORDER_DETAIL_ORDER_ID + " AND od." + KEY_ORDER_DETAIL_ORDER_ID + " = " + typId;

        Cursor c = db.rawQuery(selectQuery, null);


        int iProdId = c.getColumnIndex("productId");
        int iQty = c.getColumnIndex("qty");
        int iQtyExe = c.getColumnIndex("qtyExe");
        int iSchemeQty = c.getColumnIndex("schemeQty");
        int iTot2 = c.getColumnIndex("tot2");
        int iTotExe = c.getColumnIndex("tot_exe");
        /*int iTradePrice = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_PRICE);
        int iDiscount1 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_1);
        int iDiscount2 = c.getColumnIndex(KEY_ORDER_DETAIL_DISCOUNT_2);
        int iTradeOffer = c.getColumnIndex(KEY_ORDER_DETAIL_TRADE_OFFER);
        int iScheme = c.getColumnIndex(KEY_ORDER_DETAIL_SCHEME);
        int iTax1 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_1);
        int iTax2 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_2);
        int iTax3 = c.getColumnIndex(KEY_ORDER_DETAIL_TAX_3);
        int iSubTotal = c.getColumnIndex(KEY_ORDER_DETAIL_SUBTOTAL);*/

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("productId", c.getString(iProdId));
                    map.put("productName", getSelectedItemName(String.valueOf(c.getString(iProdId))));
                    map.put("qty", String.format("%.0f", (Double.parseDouble(c.getString(iQty)) - Double.parseDouble(c.getString(iSchemeQty)))));
                    map.put("qtyExe", String.format("%.0f", (Double.parseDouble(c.getString(iQtyExe)) - Double.parseDouble(c.getString(iSchemeQty)))));
                    map.put("schemeQty", c.getString(iSchemeQty));

               /* map.put("tradePrice", c.getString(iTradePrice));
                map.put("discount1", c.getString(iDiscount1));
                map.put("discount2", c.getString(iDiscount2));
                map.put("tradeOffer", c.getString(iTradeOffer));
                map.put("scheme", c.getString(iScheme));
                map.put("tax1", c.getString(iTax1));
                map.put("tax2", c.getString(iTax2));
                map.put("tax3", c.getString(iTax3));
                map.put("subTotal", c.getString(iSubTotal));*/
                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }
        return result;


    }


    public ArrayList<HashMap<String, String>> getCustomerList() {

        String[] Column = new String[]{KEY_ID_CUSTOMER, KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_COMPANY_NAME, KEY_CUSTOMER_ADDRESS, KEY_CUSTOMER_COUNTRY, KEY_CUSTOMER_CITY, KEY_CUSTOMER_STATE, KEY_CUSTOMER_CELL, KEY_CUSTOMER_PHONE1, KEY_CUSTOMER_PHONE2, KEY_CUSTOMER_ROUTE_ID, KEY_CUSTOMER_ADVANCE_PAYMENT};


        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, null, null, null, null, KEY_COMPANY_NAME);

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        int iCo = c.getColumnIndex(KEY_COMPANY_NAME);
        int iAdvancePymt = c.getColumnIndex(KEY_CUSTOMER_ADVANCE_PAYMENT);

        int iAdd = c.getColumnIndex(KEY_CUSTOMER_ADDRESS);
        int iCity = c.getColumnIndex(KEY_CUSTOMER_CITY);
        int iCountry = c.getColumnIndex(KEY_CUSTOMER_COUNTRY);
        int iState = c.getColumnIndex(KEY_CUSTOMER_STATE);


        int iCell = c.getColumnIndex(KEY_CUSTOMER_CELL);
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_PHONE1);
        int iP2 = c.getColumnIndex(KEY_CUSTOMER_PHONE2);
        int iRoute = c.getColumnIndex(KEY_CUSTOMER_ROUTE_ID);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CODE_COLUMN, c.getString(iId));
                    map.put(FIRST_COLUMN, c.getString(iFn) + " " + c.getString(iLn));
                    map.put(SECOND_COLUMN, c.getString(iCo));
                    map.put(THIRD_COLUMN, String.format("%.2f", getBalanceOfCustomer(c.getString(iId))));
                    map.put(FIFTH_COLUMN,  getRouteNameFromNetID(c.getInt(iRoute)));



                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }


        return result;

    }

    public ArrayList<HashMap<String, String>> getCustomerListForRoutePlan(String check) {

        String selectQuery="";
        switch (check){
            case UserSettings.PENDING:
                selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
                        " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE id NOT IN" +
                        " (select sv_cust_id from shop_visit WHERE strftime('%Y',sv_datetime) = strftime('%Y',date('now')) " +
                        "AND strftime('%m',sv_datetime) = strftime('%m',date('now')) AND strftime('%d',sv_datetime) =" +
                        " strftime('%d',date('now'))) " +
                        " AND id NOT IN (select customer_id from sales_return  WHERE strftime('%Y',datetime) = strftime('%Y',date('now')) " +
                        "AND strftime('%m',datetime) = strftime('%m',date('now')) AND strftime('%d',datetime) = " +
                        " strftime('%d',date('now'))) " +
                        " AND id NOT IN (select customer_id from sales_order  WHERE strftime('%Y',datetime_orig) = strftime('%Y',date('now')) " +
                        "AND strftime('%m',datetime_orig) = strftime('%m',date('now')) AND strftime('%d',datetime_orig) = " +
                        "strftime('%d',date('now'))) ORDER BY distance";
                break;

            case UserSettings.PRODUCTIVE:
                selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
                        " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " +
                         " id in (select customer_id from sales_order WHERE strftime('%Y',datetime_orig) = " +
                        "strftime('%Y',date('now')) AND strftime('%m',datetime_orig) = strftime('%m',date('now')) AND" +
                        " strftime('%d',datetime_orig) = strftime('%d',date('now'))) ORDER BY distance";
                break;

            case UserSettings.UN_PRODUCTIVE:
                selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
                        " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " +
                        "( id in (select sv_cust_id from shop_visit  WHERE strftime('%Y',sv_datetime) = " +
                        "strftime('%Y',date('now')) AND strftime('%m',sv_datetime) = strftime('%m',date('now')) AND " +
                        "strftime('%d',sv_datetime) = strftime('%d',date('now'))) OR id IN (select customer_id from" +
                        " sales_return WHERE strftime('%Y',datetime) = strftime('%Y',date('now')) AND " +
                        "strftime('%m',datetime) = strftime('%m',date('now')) AND strftime('%d',datetime) =" +
                        " strftime('%d',date('now'))) ) AND id NOT IN (select customer_id from sales_order WHERE strftime('%Y',datetime_orig) " +
                        "= strftime('%Y',date('now')) AND strftime('%m',datetime_orig) = strftime('%m',date('now')) AND " +
                        "strftime('%d',datetime_orig) = strftime('%d',date('now'))) ORDER BY distance";
                break;


        }
//        selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
//                " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " ORDER BY distance";


        Log.d("TAG", "getCustomerListForRoutePlan: "+selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        int iCo = c.getColumnIndex(KEY_COMPANY_NAME);
        int iDistance = c.getColumnIndex("distance");

        int iRouteId = c.getColumnIndex(KEY_CUSTOMER_ROUTE_ID);
        int iLati = c.getColumnIndex(KEY_CUSTOMER_LATTITUDE);
        int iLongi = c.getColumnIndex(KEY_CUSTOMER_LONGITUDE);
        int iLastUpdate = c.getColumnIndex(KEY_CUSTOMER_LAST_UPDATE);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", c.getString(iId));
                    map.put("name", c.getString(iFn) + " " + c.getString(iLn));
                    map.put("shop_name", c.getString(iCo));
                    map.put("latitude", c.getString(iLati));
                    map.put("longitude", c.getString(iLongi));
                    map.put("distance", c.getString(iDistance));
                    map.put("route_id", c.getString(iRouteId));
                    map.put("route_name", getRouteNameFromNetID(c.getInt(iRouteId)));
                    map.put("person", getMobFName());
                    map.put("last_update", c.getString(iLastUpdate));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    private double getBalanceOfCustomer(String custID) {

        String oBal = getSelectedCustomerOpeningBalance(custID);
        String total2 = getSelectedCustomerOpeningBalanceTotal(custID);
        String amountRec = getSelectedCustomerAmountReceived(custID);
        String advancePymt = getSelectedCustomerAdvancePayment(custID);

        double sum = 0, openingBalance = 0, total = 0, amountRecieved = 0, advancePayment = 0;

        if (oBal != null && !oBal.equals(""))
            openingBalance = Double.parseDouble(oBal);

        if (total2 != null && !total2.equals(""))
            total = Double.parseDouble(total2);

        if (amountRec != null && !amountRec.equals(""))
            amountRecieved = Double.parseDouble(amountRec);

        if (advancePymt != null && !advancePymt.equals("")) {
            advancePayment = Double.parseDouble(advancePymt);
        }

        sum = openingBalance + (total - amountRecieved) - advancePayment;

        return sum;

    }

    public ArrayList<HashMap<String, String>> getCustomerListNonVerified() {

        String[] Column = new String[]{KEY_ID_CUSTOMER, KEY_CUSTOMER_SEARCH, KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_COMPANY_NAME, KEY_CUSTOMER_NUMBER_VERIFIED};


        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_CUSTOMER_NUMBER_VERIFIED + " = 0", null, null, null, null);

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        int iCo = c.getColumnIndex(KEY_COMPANY_NAME);
        int iSrch = c.getColumnIndex(KEY_CUSTOMER_SEARCH);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("shopId", c.getString(iId));
                    map.put("customerName", c.getString(iFn) + " " + c.getString(iLn));
                    map.put("shopName", c.getString(iId) + " " + c.getString(iCo));


                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public ArrayList<HashMap<String, String>> getCustomerListByRoute(int savedRouteId) {

        String[] Column = new String[]{KEY_ID_CUSTOMER, KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_COMPANY_NAME,
                KEY_CUSTOMER_ADDRESS, KEY_CUSTOMER_COUNTRY, KEY_CUSTOMER_CITY, KEY_CUSTOMER_STATE, KEY_CUSTOMER_CELL,
                KEY_CUSTOMER_PHONE1, KEY_CUSTOMER_PHONE2, KEY_CUSTOMER_ROUTE_ID, KEY_CUSTOMER_ADVANCE_PAYMENT};


//        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_CUSTOMER_ROUTE_ID + " = " + savedRouteId, null, null, null, KEY_COMPANY_NAME);
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, null, null, null, null, KEY_COMPANY_NAME);

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);
        int iAdvancePymt = c.getColumnIndex(KEY_CUSTOMER_ADVANCE_PAYMENT);

        int iCo = c.getColumnIndex(KEY_COMPANY_NAME);

        int iAdd = c.getColumnIndex(KEY_CUSTOMER_ADDRESS);
        int iCity = c.getColumnIndex(KEY_CUSTOMER_CITY);
        int iCountry = c.getColumnIndex(KEY_CUSTOMER_COUNTRY);
        int iState = c.getColumnIndex(KEY_CUSTOMER_STATE);


        int iCell = c.getColumnIndex(KEY_CUSTOMER_CELL);
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_PHONE1);
        int iP2 = c.getColumnIndex(KEY_CUSTOMER_PHONE2);
        int iRoute = c.getColumnIndex(KEY_CUSTOMER_ROUTE_ID);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(CODE_COLUMN, c.getString(iId));
                    map.put(FIRST_COLUMN, c.getString(iFn) + " " + c.getString(iLn));
                    map.put(SECOND_COLUMN, c.getString(iCo));
                    map.put(THIRD_COLUMN, String.format("%.2f", getBalanceOfCustomer(c.getString(iId))));
                    map.put(FIFTH_COLUMN,  getRouteNameFromNetID(c.getInt(iRoute)));


                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }
     public ArrayList<String> getCustomerNameListByRoute(int savedRouteId) {

        String[] Column = new String[]{KEY_CUSTOMER_SEARCH};

        ArrayList result = new ArrayList();
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_CUSTOMER_ROUTE_ID + " = " + savedRouteId, null, null, null, KEY_COMPANY_NAME);

        //HashMap result = new HashMap();


        int srch = c.getColumnIndex(KEY_CUSTOMER_SEARCH);






        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();



                    result.add(c.getString(srch));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }
 public ArrayList<String> getCustomerNameList() {

        String[] Column = new String[]{KEY_CUSTOMER_SEARCH};

        ArrayList result = new ArrayList();
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column,null, null, null, null, KEY_COMPANY_NAME);

        //HashMap result = new HashMap();


        int srch = c.getColumnIndex(KEY_CUSTOMER_SEARCH);






        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();



                    result.add(c.getString(srch));
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public ArrayList<HashMap<String, String>> getCustomerListForRoutePlanByRoute(int savedRouteId,String check) {
        String selectQuery="";
        switch (check){
            case UserSettings.PENDING:
                selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
                        " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_ROUTE_ID + " = " +
                        savedRouteId + " AND id NOT IN" +
                        " (select sv_cust_id from shop_visit WHERE strftime('%Y',sv_datetime) = strftime('%Y',date('now')) " +
                        "AND strftime('%m',sv_datetime) = strftime('%m',date('now')) AND strftime('%d',sv_datetime) =" +
                        " strftime('%d',date('now'))) " +
                        " AND id NOT IN (select customer_id from sales_return  WHERE strftime('%Y',datetime) = strftime('%Y',date('now')) " +
                        "AND strftime('%m',datetime) = strftime('%m',date('now')) AND strftime('%d',datetime) = " +
                        " strftime('%d',date('now'))) " +
                        " AND id NOT IN (select customer_id from sales_order  WHERE strftime('%Y',datetime_orig) = strftime('%Y',date('now')) " +
                        "AND strftime('%m',datetime_orig) = strftime('%m',date('now')) AND strftime('%d',datetime_orig) = " +
                        "strftime('%d',date('now'))) ORDER BY distance";
                break;

            case UserSettings.PRODUCTIVE:
                selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
                        " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_ROUTE_ID + " = " +
                        savedRouteId + " AND id in (select customer_id from sales_order WHERE strftime('%Y',datetime_orig) = " +
                        "strftime('%Y',date('now')) AND strftime('%m',datetime_orig) = strftime('%m',date('now')) AND" +
                        " strftime('%d',datetime_orig) = strftime('%d',date('now'))) ORDER BY distance";
                break;

            case UserSettings.UN_PRODUCTIVE:
                selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
                        " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_ROUTE_ID + " = " +
                        savedRouteId + " AND ( id in (select sv_cust_id from shop_visit  WHERE strftime('%Y',sv_datetime) = " +
                        "strftime('%Y',date('now')) AND strftime('%m',sv_datetime) = strftime('%m',date('now')) AND " +
                        "strftime('%d',sv_datetime) = strftime('%d',date('now'))) OR id IN (select customer_id from" +
                        " sales_return WHERE strftime('%Y',datetime) = strftime('%Y',date('now')) AND " +
                        "strftime('%m',datetime) = strftime('%m',date('now')) AND strftime('%d',datetime) =" +
                        " strftime('%d',date('now'))) ) AND id NOT IN (select customer_id from sales_order WHERE " +
                        "strftime('%Y',datetime_orig) = strftime('%Y',date('now')) AND strftime('%m',datetime_orig) = " +
                        "strftime('%m',date('now')) AND strftime('%d',datetime_orig) = strftime('%d',date('now'))) ORDER BY distance";
                break;


        }
        Log.d("TAG", "getCustomerListForRoutePlanByRoute: "+selectQuery);
//         selectQuery = "SELECT  *, " + (KEY_CUSTOMER_LATTITUDE + "+" + KEY_CUSTOMER_LONGITUDE) +
//                " as distance FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_ROUTE_ID + " = " +
//                savedRouteId + " ORDER BY distance";

        Cursor c = db.rawQuery(selectQuery, null);

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);
        int iAdvancePymt = c.getColumnIndex(KEY_CUSTOMER_ADVANCE_PAYMENT);
        int iLati = c.getColumnIndex(KEY_CUSTOMER_LATTITUDE);
        int iLongi = c.getColumnIndex(KEY_CUSTOMER_LONGITUDE);
        int iCo = c.getColumnIndex(KEY_COMPANY_NAME);
        int iDistance = c.getColumnIndex("distance");
        int iRouteId = c.getColumnIndex(KEY_CUSTOMER_ROUTE_ID);
        int iLastUpdate = c.getColumnIndex(KEY_CUSTOMER_LAST_UPDATE);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", c.getString(iId));
                    map.put("name", c.getString(iFn) + " " + c.getString(iLn));
                    map.put("shop_name", c.getString(iCo));
                    map.put("latitude", c.getString(iLati));
                    map.put("longitude", c.getString(iLongi));
                    map.put("distance", c.getString(iDistance));
                    map.put("route_id", c.getString(iRouteId));
                    map.put("route_name", getRouteNameFromNetID(c.getInt(iRouteId)));
                    map.put("person", getMobFName());
                    map.put("last_update", c.getString(iLastUpdate));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        Log.d("TAG", "getCustomerListForRoutePlanByRoute: "+result.toString());
        return result;

    }

    public String getSelectedCustomerName(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName) + " " + c.getString(iLName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }
    public String getSelectedCustomerSearchName(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_SEARCH};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int SearchName = c.getColumnIndex(KEY_CUSTOMER_SEARCH);


        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(SearchName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }
    public String getSelectedCustomerSearch(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_SEARCH};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iSearch = c.getColumnIndex(KEY_CUSTOMER_SEARCH);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iSearch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerCompName(String id) {
        int idInt = 0;
        if (!id.equals("") && !id.equalsIgnoreCase("null"))
            idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_COMPANY_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iComp = c.getColumnIndex(KEY_COMPANY_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    private String getSelectedCampaignName(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_MERCHANDIZING_PLAN_NAME};
        Cursor c = db.query(DATABASE_TABLE_MERCHANDIZING_PLAN, Column, KEY_MERCHANDIZING_PLAN_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iComp = c.getColumnIndex(KEY_MERCHANDIZING_PLAN_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedProductName(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_INVENT_NAME};
        Cursor c = db.query(DATABASE_TABLE_INVENTORY, Column, KEY_ID_INVENT + " = " + idInt, null, null, null, null);

        String result = "";
        int iComp = c.getColumnIndex(KEY_INVENT_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerEmail(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_EMAIL};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iEml = c.getColumnIndex(KEY_CUSTOMER_EMAIL);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEml);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerAdd(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_ADDRESS, KEY_CUSTOMER_CITY, KEY_CUSTOMER_COUNTRY, KEY_CUSTOMER_STATE};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iAd = c.getColumnIndex(KEY_CUSTOMER_ADDRESS);
        int iCi = c.getColumnIndex(KEY_CUSTOMER_CITY);
        int iCo = c.getColumnIndex(KEY_CUSTOMER_COUNTRY);
        int iSt = c.getColumnIndex(KEY_CUSTOMER_STATE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iAd) /* +" " + c.getString(iCi) + " " + c.getString(iSt) + " " + c.getString(iCo)*/;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerCell(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_CELL};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iCell = c.getColumnIndex(KEY_CUSTOMER_CELL);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iCell);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerSmsCode(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_SMS_CODE};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iSmsCode = c.getColumnIndex(KEY_CUSTOMER_SMS_CODE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iSmsCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerP1(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_PHONE1};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_PHONE1);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerP2(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_PHONE2};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP2 = c.getColumnIndex(KEY_CUSTOMER_PHONE2);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerOpeningBalance(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_OPENING_BALANCE_OLD};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_OPENING_BALANCE_OLD);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }


        return result;

    }

    public String getSelectedCustomerCreditAmountLimit(String id) {
        int idInt = 0;
        if (!id.equalsIgnoreCase("")) {
            idInt = Integer.parseInt(id);
        }
        String[] Column = new String[]{KEY_CUSTOMER_CREDIT_AMOUNT};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_CREDIT_AMOUNT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerCnic(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_CNIC};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_CNIC);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerNotes(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_NOTES};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_NOTES);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerRoute(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_ROUTE_ID};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_ROUTE_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    public String getSelectedCustomerType(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_TYPE};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);

        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_TYPE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iP1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;

    }

    private String getSoldForMonthlyTarget(String productId) {


        int prodId = Integer.parseInt(productId);
        String selectQuery = "SELECT SUM(" + KEY_ORDER_DETAIL_QUANTITY_EXE + ") FROM " + DATABASE_TABLE_ORDER_DETAILS + " WHERE " + KEY_ORDER_DETAIL_PRODUCT_ID + " = " + prodId + " AND " + KEY_ORDER_DETAIL_ORDER_ID + " IN ( SELECT " + KEY_SALES_ORDER_ID + " FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 1)";

        Log.d("SOLD Query", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        String maxid = (c.moveToFirst() ? c.getString(0) : "null");

        if (!c.isClosed())
            c.close();

        return maxid;


    }

    public String getSelectedCustomerOpeningBalanceTotal(String custId) {


        int customerId = Integer.parseInt(custId);
        String selectQuery = "SELECT SUM(" + KEY_SALES_ORDER_TOTAL2 + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_CUSTOMER_ID + " = " + customerId + " AND " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " != 0";

        Cursor c = db.rawQuery(selectQuery, null);

        String maxid = (c.moveToFirst() ? c.getString(0) : "0");

        if (!c.isClosed())
            c.close();
        return maxid;

    }



        /*
    Cusotmer Details Ends
     */

    /*
     * ITEMS DROP DOWN WORK Starts
     */

    public String getSelectedCustomerAmountReceived(String custId) {


        int customerId = Integer.parseInt(custId);
        String selectQuery = "SELECT SUM(" + KEY_SALES_ORDER_AMOUNT_RECIEVED + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_CUSTOMER_ID + " = " + customerId + " AND " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " != 0";

        Cursor c = db.rawQuery(selectQuery, null);

        String maxid = (c.moveToFirst() ? c.getString(0) : "0");

        if (!c.isClosed())
            c.close();

        return maxid;

    }

    public int getTotalCustomers() {


        String que = "Select Count(" + KEY_ID_CUSTOMER + ") FROM " + DATABASE_TABLE_CUSTOMER_DETAILS;

        Cursor c = db.rawQuery(que, null);
        //Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, null, null, null, null, null);

        //int result = c.getCount();
        int count = 0;
        if (null != c) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
            c.close();
        }


        return count;
    }

    public ArrayList<HashMap<String, String>> getCustomerRoutesForDropDown() {


        ArrayList<HashMap<String, String>> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NAME, KEY_ROUTE_DAY};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_UPDATE + " != 1", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NAME);
        int iDay = c.getColumnIndex(KEY_ROUTE_DAY);

        try {

            if (c.moveToFirst()) {
                do {

                    HashMap<String, String> map = new HashMap<>();

                    map.put("name", c.getString(iItemName));
                    map.put("day", c.getString(iDay));
                    ItemNames.add(map);

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return ItemNames;
    }
        public ArrayList<HashMap<String, String>> getCustomerRoutesForDropDownbyDay(String day) {


        ArrayList<HashMap<String, String>> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NAME, KEY_ROUTE_DAY};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_UPDATE + " != 1 and ("+ KEY_ROUTE_DAY + " = "+ day+" or "+ KEY_ROUTE_DAY + " = 0 ) ", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NAME);
        int iDay = c.getColumnIndex(KEY_ROUTE_DAY);

        try {

            if (c.moveToFirst()) {
                do {

                    HashMap<String, String> map = new HashMap<>();

                    map.put("name", c.getString(iItemName));
                    map.put("day", c.getString(iDay));
                    ItemNames.add(map);

                } while (c.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return ItemNames;
    }

    public List<String> getCustomerRoutesForDropDown(int day) {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_UPDATE + " != 1 AND " +
                        "route_day = " + day,
                null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return ItemNames;
    }
    public List<String> getCustomerRoutesForDropDownAllDay() {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_UPDATE + " != 1 ",
                null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return ItemNames;
    }

    public List<String> getCustomerRoutesNames() {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, null, null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemNames;
    }
    public ArrayList<HashMap<String, String>> getOrderTemplate(String CustomerId)
    {
       // String[] Column = new String[]{ KEY_ORDER_TEMPLATE_ITEM_ID, KEY_ORDER_TEMPLATE_QUANTITY};

    String query="Select "+KEY_ORDER_TEMPLATE_QUANTITY+" , "+KEY_INVENTORY_SEARCH+" from "+DATABASE_TABLE_ORDER_TEMPLATE+" INNER JOIN "+DATABASE_TABLE_INVENTORY+" ON "+KEY_ORDER_TEMPLATE_ITEM_ID+" =  id Where "+KEY_ORDER_TEMPLATE_CUSTOMER_ID+" = '"+CustomerId+ "'";
        Cursor c = db.rawQuery(query,null);

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();




        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", c.getString(c.getColumnIndex(KEY_INVENTORY_SEARCH)));
                    map.put("quantity", c.getString(c.getColumnIndex(KEY_ORDER_TEMPLATE_QUANTITY)));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    public ArrayList<HashMap<String, String>> getCustomerShopNamesAndCellNo() {

        String[] Column = new String[]{KEY_ID_CUSTOMER, KEY_COMPANY_NAME, KEY_CUSTOMER_FNAME};


        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, null, null, null, null, null);

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        int iCo = c.getColumnIndex(KEY_COMPANY_NAME);

        int iFname = c.getColumnIndex(KEY_CUSTOMER_FNAME);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("company_name", c.getString(iCo));
                    map.put("customer_name", c.getString(iFname));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public List<String> getCustomerRoutesForDrop(int id) {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_NET_ID + " NOT IN (" + id + ")", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemNames;
    }

    public List<String> getCustomerTypeForDropDown() {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_CUSTOMER_TYPE_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_TYPE, Column, null, null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_CUSTOMER_TYPE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemNames;
    }

    public List<String> getExpenseTypeIDForDropDown() {


        List<String> ItemID = new ArrayList<>();

        String[] Column = new String[]{KEY_EXPENSE_TYPE_ID};
        Cursor c = db.query(DATABASE_TABLE_EXPENSE_TYPE, Column, null, null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_EXPENSE_TYPE_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemID;
    }

    public List<String> getExpenseTypeForDropDown() {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_EXPENSE_TYPE_NAME};
        Cursor c = db.query(DATABASE_TABLE_EXPENSE_TYPE, Column, null, null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_EXPENSE_TYPE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemNames;
    }

    public List<String> getNoReasonIDForDropDown() {


        List<String> ItemID = new ArrayList<>();

        String[] Column = new String[]{KEY_NO_REASON_ID};
        Cursor c = db.query(DATABASE_TABLE_NO_REASON, Column, null, null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_NO_REASON_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemID;
    }

    public List<String> getNoReasonForDropDown() {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_NO_REASON_NAME};
        Cursor c = db.query(DATABASE_TABLE_NO_REASON, Column, null, null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_NO_REASON_NAME);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemNames;
    }

    private String getSelectedReasonName(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_NO_REASON_NAME};
        Cursor c = db.query(DATABASE_TABLE_NO_REASON, Column, KEY_NO_REASON_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iComp = c.getColumnIndex(KEY_NO_REASON_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }


        return result;

    }

    public List<String> getCustomerTypeForDrop(int id) {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_CUSTOMER_TYPE_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_TYPE, Column, KEY_CUSTOMER_TYPE_ID + " NOT IN (" + id + ")", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_CUSTOMER_TYPE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemNames;
    }

    /*public String getSelectedCustomerName( String custId ) {
        // TODO Auto-generated method stub

        String Columns[] = new String[]{KEY_CUSTOMER_SEARCH};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, KEY_ID_ITEM+" = "+custId, null, null, null, null);
        String result = "";

        int iData = c.getColumnIndex(KEY_CUSTOMER_SEARCH);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = c.getString(iData);
        }

        return result;
    }*/

    public List<String> getCustomerRoutesIDForDropDown() {


        List<String> ItemID = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NET_ID};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_UPDATE + " != 1", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NET_ID);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemID;
    }
      public List<String> getCustomerRoutesIDForDropDownByDay(String Day) {


        List<String> ItemID = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NET_ID};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_UPDATE + " != 1 and ("+ KEY_ROUTE_DAY + " = "+ Day+" or "+ KEY_ROUTE_DAY + " = 0 ) ", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NET_ID);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemID;
    }

    public List<String> getCustomerRoutesIDForDrop(int id) {


        List<String> ItemID = new ArrayList<>();

        String[] Column = new String[]{KEY_ROUTE_NET_ID};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, KEY_ROUTE_NET_ID + " NOT IN (" + id + ")", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_ROUTE_NET_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemID;
    }

    public List<String> getCustomerTypeIDForDropDown() {


        List<String> ItemID = new ArrayList<>();

        String[] Column = new String[]{KEY_CUSTOMER_TYPE_ID};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_TYPE, Column, null, null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_CUSTOMER_TYPE_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemID;
    }

    public List<String> getCustomerTypeIDForDrop(int id) {


        List<String> ItemID = new ArrayList<>();

        String[] Column = new String[]{KEY_CUSTOMER_TYPE_ID};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_TYPE, Column, KEY_CUSTOMER_TYPE_ID + " NOT IN (" + id + ")", null, null, null, null);

        int iItemName = c.getColumnIndex(KEY_CUSTOMER_TYPE_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return ItemID;
    }

    public String getSelectedItemName(String itemID) {
        // TODO Auto-generated method stub

        String[] Columns = new String[]{KEY_ITEM_NAME};
        Cursor c = db.query(DATABASE_TABLE_INVENTORY, Columns, KEY_ID_ITEM + " = " + itemID, null, null, null, null);
        String result = "";

        int iData = c.getColumnIndex(KEY_ITEM_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }


    public double getSelectedItemPrice(String id) {

        String[] Columns = new String[]{KEY_SALE_PRICE};
        Cursor c = db.query(DATABASE_TABLE_INVENTORY, Columns, KEY_ID_INVENT + " = " + id, null, null, null, null);
        //ArrayList<String> resultId = new ArrayList<String>();
        double result = 0;

        int iUC = c.getColumnIndex(KEY_SALE_PRICE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getInt(iUC);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public ArrayList<String> getSelectedCustomerID() {

        String[] Columns = new String[]{KEY_ID_CUSTOMER};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, null, null, null, null, null);
        ArrayList<String> resultId = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId.add("" + c.getInt(iId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return resultId;
    }

    public ArrayList<String> getSelectedCustomerId() {


        String[] Columns = new String[]{KEY_ID_CUSTOMER};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, null, null, null, null, KEY_COMPANY_NAME);
        ArrayList<String> resultId = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId.add("" + c.getInt(iId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return resultId;
    }

    public ArrayList<String> getSelectedCustomerIdByRoute(int route) {


        String[] Columns = new String[]{KEY_ID_CUSTOMER, KEY_CUSTOMER_ROUTE_ID};
//        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, KEY_CUSTOMER_ROUTE_ID + " = " + route, null, null, null, KEY_COMPANY_NAME);
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, null, null, null, null, KEY_COMPANY_NAME);
        ArrayList<String> resultId = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId.add("" + c.getInt(iId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return resultId;
    }

    public ArrayList<String> getSelectedCustomerIDByRoute(int route) {
        // TODO Auto-generated method stub

        String[] Columns = new String[]{KEY_ID_CUSTOMER, KEY_CUSTOMER_ROUTE_ID};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, KEY_CUSTOMER_ROUTE_ID + " = " + route, null, null, null, null);
        ArrayList<String> resultId = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId.add("" + c.getInt(iId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return resultId;
    }

    public long createMobUser(int emp_id, String FName, String LName, String Comp, String Cell, String P1, String P2, String Add, String City, String State, String Country, String Zip, String Email, String saleType, String discountType) {

        // int QtyInt = Integer.parseInt(qty);
        // int PriceInt = Integer.parseInt(price);

        //int empIDInt = Integer.parseInt(emp_id);
        ContentValues cv = new ContentValues();

        cv.put(KEY_emp_id, emp_id);
        cv.put(KEY_Emp_FName, FName);
        cv.put(KEY_Emp_LName, LName);
        cv.put(KEY_Emp_Comp, Comp);
        cv.put(KEY_Emp_Password, "admin");
        cv.put(KEY_Emp_Cell, Cell);
        cv.put(KEY_Emp_P1, P1);
        cv.put(KEY_Emp_P2, P2);
        cv.put(KEY_Emp_Add, Add);
        cv.put(KEY_Emp_City, City);
        cv.put(KEY_Emp_State, State);
        cv.put(KEY_Emp_Country, Country);
        cv.put(KEY_Emp_Zip, Zip);
        cv.put(KEY_Emp_LastSync, getCurrentTime());
        cv.put(KEY_Emp_Email, Email);
        cv.put(KEY_Emp_SALE_TYPE, saleType);
        cv.put(KEY_Emp_DISCOUNT_TYPE, discountType);

        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);
        String deleteQuery = "DELETE FROM " + Database_Table_mUser;

        db.execSQL(deleteQuery);

        long insertId = db.insert(Database_Table_mUser, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;

    }

    public String getMobUser() {


        String selectQuery = "SELECT " + KEY_Emp_FName + " FROM " + Database_Table_mUser;

        Cursor c = db.rawQuery(selectQuery, null);

        String result = "";

        int iEmpFName = c.getColumnIndex(KEY_Emp_FName);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEmpFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public int chkMobUserLogout() {


        String selectQuery = "SELECT " + KEY_SALESMAN_SETTINGS_ACCESS_LOGOUT + " FROM " + DATABASE_TABLE_SALESMAN_SETTINGS;

        Cursor c = db.rawQuery(selectQuery, null);

        int result = 0;

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_LOGOUT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getInt(iData);
            }


            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return result;

    }

    public int getMobUserMinDiscount() {


        String selectQuery = "SELECT " + KEY_SALESMAN_SETTINGS_MIN_DISCOUNT + " FROM " + DATABASE_TABLE_SALESMAN_SETTINGS;

        Cursor c = db.rawQuery(selectQuery, null);

        int result = 0;

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_MIN_DISCOUNT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getInt(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public int getMobUserMaxDiscount() {


        String selectQuery = "SELECT " + KEY_SALESMAN_SETTINGS_MAX_DISCOUNT + " FROM " + DATABASE_TABLE_SALESMAN_SETTINGS;

        Cursor c = db.rawQuery(selectQuery, null);

        int result = 0;

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_MAX_DISCOUNT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getInt(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public ArrayList<HashMap<String, String>> getMobUserDetailsSYNC() {
        ArrayList<HashMap<String, String>> UserDetails;

        UserDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Database_Table_mUser;
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_emp_id);
        int iFName = cursor.getColumnIndex(KEY_Emp_FName);
        int iLName = cursor.getColumnIndex(KEY_Emp_LName);
        int iCoName = cursor.getColumnIndex(KEY_Emp_Comp);
        int iCell = cursor.getColumnIndex(KEY_Emp_Cell);
        int iP1 = cursor.getColumnIndex(KEY_Emp_P1);
        int iP2 = cursor.getColumnIndex(KEY_Emp_P2);
        int iAdd = cursor.getColumnIndex(KEY_Emp_Add);
        int iCity = cursor.getColumnIndex(KEY_Emp_City);
        int iState = cursor.getColumnIndex(KEY_Emp_State);
        int iCountry = cursor.getColumnIndex(KEY_Emp_Country);
        int iZip = cursor.getColumnIndex(KEY_Emp_Zip);
        int iEmail = cursor.getColumnIndex(KEY_Emp_Email);

        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("fname", cursor.getString(iFName));
                    map.put("lname", cursor.getString(iLName));
                    map.put("comp_name", cursor.getString(iCoName));
                    map.put("cell", cursor.getString(iCell));
                    map.put("ph1", cursor.getString(iP1));
                    map.put("ph2", cursor.getString(iP2));
                    map.put("add", cursor.getString(iAdd));
                    map.put("city", cursor.getString(iCity));
                    map.put("state", cursor.getString(iState));
                    map.put("country", cursor.getString(iCountry));
                    map.put("zip", cursor.getString(iZip));
                    map.put("email", cursor.getString(iEmail));

                    UserDetails.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return UserDetails;

    }

    public ArrayList<String> getMobUserDetails() {


        String selectQuery = "SELECT * FROM " + Database_Table_mUser;

        Cursor c = db.rawQuery(selectQuery, null);

        ArrayList<String> result = new ArrayList<>();

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result.add("cust_id " + c.getString(1) + "\n" + "fname " + c.getString(2) + " " + "lname " + c.getString(3) + "\n" + "comp " + c.getString(4) + "\n" + "email " + c.getString(13) + "\n" + "cell " + c.getString(5) + "\n" + "p1 " + c.getString(6) + "\n" + "p2 " + c.getString(7) + "\n" + "add " + c.getString(8) + "\n" + "city " + c.getString(9) + "\n" + "country " + c.getString(11) + "\n" + "state " + c.getString(10) + "\n" + "zip " + c.getString(12) + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public void updateMobUserDetail(String id, String fname, String lname/*, String compname*/, String email, String add, String city, String state, String zip, String country, String cell, String p1, String p2, String secondrySaleType, String secondryDiscountType) {

        int idInt = Integer.parseInt(id);
        int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_FName + " = '" + fname + "', " + KEY_Emp_LName + "= '" + lname + "', " + KEY_Emp_Email + "= '" + email + "', "/* + KEY_Emp_Comp + "= '" + compname + "', "*/ + KEY_Emp_Cell + "= '" + cell + "', " + KEY_Emp_P1 + "= '" + p1 + "', " + KEY_Emp_P2 + "= '" + p2 + "', " + KEY_Emp_Add + "= '" + add + "', " + KEY_Emp_City + "= '" + city + "', " + KEY_Emp_State + "= '" + state + "', " + KEY_Emp_Zip + "= '" + zip + "', " + KEY_Emp_Country + "= '" + country + "', " + KEY_Emp_SALE_TYPE + "= '" + secondrySaleType + "', " + KEY_Emp_DISCOUNT_TYPE + " = '" + secondryDiscountType + "', " + KEY_Emp_LastSync + " = '" + getCurrentTime() + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserLastSynced(String id, String serverDate) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_LAST_SYNCED + " = '" + serverDate + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserEnableCatalog(String id, String en_Catalog) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_Enable_Catalog_Pdf + " = '" + en_Catalog + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserAppVersion(String id, String version) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_APP_VERSION + " = '" + version + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserProductSale(String id, String version) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_PRODUCT_SALE + " = '" + version + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserTarget(String id, String version) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_TARGET + " = '" + version + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserTimeIn(String id, String version) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_TIME_IN + " = '" + version + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserTimeInTIME(String id, String version) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_TIME_IN_TIME + " = '" + version + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserCompany(String id, String company) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_Comp + " = '" + company + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public void updateMobUserReportUrl(String id, String reportUrl) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
        String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_REPORT_URL + " = '" + reportUrl + "' where " + KEY_emp_id + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public long createProductSubType(String id, String name, String p_type) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_ID_SUB_ITEM, id);
        cv.put(KEY_NAME_SUB_ITEM, name);
        cv.put(KEY_ITEM_ID_SUB_ITEM, p_type);


        return db.insert(DATABASE_TABLE_ITEM_SUB_TYPE, null, cv);


    }

    public void deleteProductSubType() {


        db.execSQL("DELETE FROM " + DATABASE_TABLE_ITEM_SUB_TYPE);
    }

    public void DeleteAllRecords() {
        deleteVerifyStock();
        deleteVerifyStockDetail();
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SHOP_CATEGORY);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_INVENTORY);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_BRAND);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_ITEM_TYPE);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_ITEM_SUB_TYPE);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CUSTOMER_DETAILS);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CUSTOMER_ROUTE);
        //db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE_OFFICE);
        db.execSQL("DELETE FROM " + Database_Table_mUser);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SALES);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SALES_DETAILS);
        db.execSQL("DELETE FROM " + Database_Table_Cart);
        db.execSQL("DELETE FROM " + Database_Table_Route);
        db.execSQL("DELETE FROM " + Database_Table_Today_Route);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CLOCKIN);
        db.execSQL("DELETE FROM " + Database_Table_PHONESTART);
        db.execSQL("DELETE FROM " + Database_Table_CHKNET);
        db.execSQL("DELETE FROM " + Database_Table_ReportTo);
        db.execSQL("DELETE FROM " + Database_Table_VALID_REPORT);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_OFFLINE_TRACKING);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SALESMAN_SETTINGS);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SETTINGS);
        db.execSQL("DELETE FROM " + Database_Table_Return_Reason);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CUSTOMER_TYPE);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CUSTOMER_PRICING);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_ORDER_DETAILS);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_RETURN_DETAILS);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_PAYMENT_RECIEVED);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CUSTOMER_CATEGORY);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CUSTOMER_CELEB);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_EXPENSE_STATUS);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_EXPENSE_TYPE);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_NO_REASON);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_EXPENSE);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SHOP_VISIT);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_CLOCKIN_TIME);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_MERCHANDIZING);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_MERCHANDIZING_PLAN);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_TOWN);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_TOWN_TRAVEL);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_TRAVEL_EXPENSE);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_TARGET);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SUPPORT);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SUPPORT_DETAIL);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SUPPORT_STATUS);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_COMMITMENT);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_TOTAL_DISCOUNT);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_APP_SETTINGS);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_SHOP_STOCK);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_ITEM_TARGET);
        db.execSQL("DELETE FROM " + DATABASE_TABLE_DISTRIBUTOR_LIST);

    }

    public String getMobEmpId() {

        String result = "0";

        String[] Column = new String[]{KEY_emp_id};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_emp_id);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpSaleType() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_SALE_TYPE};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_SALE_TYPE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpDiscountType() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_DISCOUNT_TYPE};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_DISCOUNT_TYPE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpLastSynced() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_LAST_SYNCED};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_LAST_SYNCED);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpAppVersion() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_APP_VERSION};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_APP_VERSION);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpEnableCatalog() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_Enable_Catalog_Pdf};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_Enable_Catalog_Pdf);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpProductSale() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_PRODUCT_SALE};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_PRODUCT_SALE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpTarget() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_TARGET};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_TARGET);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpReportUrl() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_REPORT_URL};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_REPORT_URL);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpTimeIn() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_TIME_IN};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_TIME_IN);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobEmpTimeInTime() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_TIME_IN_TIME};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEid = c.getColumnIndex(KEY_Emp_TIME_IN_TIME);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getCustomerName4EditText(String orderID) {

        String result = "";

        String query = " SELECT c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " ln, c." + KEY_CUSTOMER_SEARCH + " srch FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c, " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_CUSTOMER_ID + " = c." + KEY_ID_CUSTOMER + " AND so." + KEY_SALES_ORDER_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iFn = c.getColumnIndex("fn");
        int iLn = c.getColumnIndex("ln");
        int iSrch = c.getColumnIndex("srch");
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                /*result = c.getString(iFn)+" "+c.getString(iLn);*/
                result = c.getString(iSrch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getCustomerName4EditText2(String orderID) {

        String result = "";

        String query = " SELECT c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " ln, c." + KEY_CUSTOMER_SEARCH + " srch FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c, " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_CUSTOMER_ID + " = c." + KEY_ID_CUSTOMER + " AND so." + KEY_SALES_ORDER_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iFn = c.getColumnIndex("fn");
        int iLn = c.getColumnIndex("ln");
        int iSrch = c.getColumnIndex("srch");

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                /*result = c.getString(iFn)+" "+c.getString(iLn);*/
                result = c.getString(iFn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getCustomerName4EditTextSALESRETURN(String orderID) {

        String result = "";

        String query = " SELECT c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " ln, c." + KEY_CUSTOMER_SEARCH + " as srch FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c, " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr WHERE sr." + KEY_SALES_RETURN_CUSTOMER_ID + " = c." + KEY_ID_CUSTOMER + " AND sr." + KEY_SALES_RETURN_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iFn = c.getColumnIndex("fn");
        int iLn = c.getColumnIndex("ln");
        int iSrch = c.getColumnIndex("srch");

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                /*result = c.getString(iFn)+" "+c.getString(iLn);*/
                result = c.getString(iSrch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getCustomerName4EditTextSALESRETURN2(String orderID) {

        String result = "";

        String query = " SELECT c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " ln, c." + KEY_CUSTOMER_SEARCH + " as srch FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c, " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr WHERE sr." + KEY_SALES_RETURN_CUSTOMER_ID + " = c." + KEY_ID_CUSTOMER + " AND sr." + KEY_SALES_RETURN_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iFn = c.getColumnIndex("fn");
        int iLn = c.getColumnIndex("ln");
        int iSrch = c.getColumnIndex("srch");

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                /*result = c.getString(iFn)+" "+c.getString(iLn);*/
                result = c.getString(iFn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    /*public String getNotes4EditTextSALESRETURN( String orderID ) {

        String result = "";

        String query = " SELECT "+KEY_SALES_RETURN_DATE+" FROM "+DATABASE_TABLE_SALESMAN_SALES_RETURN+" sr WHERE sr."+KEY_SALES_RETURN_ID+" = "+orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex( KEY_SALES_RETURN_DATE );

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = c.getString(iData);
        }

        return result;
    }*/

    public String getDate4EditText(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_ORDER_DATETIME + " FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_ORDER_DATETIME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getSaleOrderTotal4EditText(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_ORDER_TOTAL2 + " FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_ORDER_TOTAL2);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getSaleOrderDiscount4EditText(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_ORDER_DISCOUNT + " FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_ORDER_DISCOUNT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getNotes4EditText(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_ORDER_NOTES + " FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_ORDER_NOTES);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getDate4EditTextSALESRETURN(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_RETURN_DATE + " FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr WHERE sr." + KEY_SALES_RETURN_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_RETURN_DATE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getTotal4EditTextSALESRETURN(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_RETURN_TOTAL2 + " FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr WHERE sr." + KEY_SALES_RETURN_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_RETURN_TOTAL2);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getDiscount4EditTextSALESRETURN(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_RETURN_DISCOUNT + " FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr WHERE sr." + KEY_SALES_RETURN_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_RETURN_DISCOUNT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getNotes4EditTextSALESRETURN(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_RETURN_NOTES + " FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr WHERE sr." + KEY_SALES_RETURN_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_RETURN_NOTES);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getReason4EditTextSALESRETURN(String orderID) {

        String result = "";

        String query = " SELECT " + KEY_SALES_RETURN_REASON + " FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " sr WHERE sr." + KEY_SALES_RETURN_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_RETURN_REASON);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getReasonName4ReturnReasonWithId(int orderID) {

        String result = "";

        String query = " SELECT " + KEY_RETURN_REASON_NAME + " FROM " + Database_Table_Return_Reason + " WHERE " + KEY_RETURN_REASON_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_RETURN_REASON_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getCustomerTypeWithId(int orderID) {

        String result = "";

        String query = " SELECT " + KEY_CUSTOMER_TYPE_NAME + " FROM " + DATABASE_TABLE_CUSTOMER_TYPE + " WHERE " + KEY_CUSTOMER_TYPE_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_CUSTOMER_TYPE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getCustomerRouteWithId(int orderID) {

        String result = "";

        String query = " SELECT " + KEY_ROUTE_NAME + " FROM " + DATABASE_TABLE_CUSTOMER_ROUTE + " WHERE " + KEY_ROUTE_NET_ID + " = " + orderID;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_ROUTE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }
      public int getCustomerRouteIDByName(String orderID) {

        int result = 0;

        String query = " SELECT " +KEY_ROUTE_NET_ID  + " FROM " + DATABASE_TABLE_CUSTOMER_ROUTE + " WHERE " + KEY_ROUTE_NAME + " = '" + orderID+"'";

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_ROUTE_NET_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getInt(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    private int getTotalExecuteEditText(String orderID) {


        String selectQuery = "SELECT " + KEY_SALES_ORDER_VALUES + " as val FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_DELETE + " = 0 AND " + KEY_SALES_ORDER_ID + " = " + orderID + " AND " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 0";


        Cursor c = db.rawQuery(selectQuery, null);

        int iVal = c.getColumnIndex("val");

        int total3 = 0;

        if (c.moveToFirst()) {

            do {

                String Values = c.getString(iVal);

                //map.put(EDIT_ORDER_ITEM, Values );
                //map.put(EDIT_ORDER_OLDQTY, itemQTY );


                String[] parts = Values.split("--");

                for (String s : parts) {

                    String[] parts2 = s.split("!!!");


                    for (int i = 0; i <= parts2.length - 2; i++) {

                        String[] finalParts = parts2[i].split("!!");

                        String itemID = finalParts[0];
                        String itemQTY = finalParts[1];
                        String itemNewQTY = finalParts[2];
                        String itemExecuteQTY = finalParts[3];

                        if (itemID.equalsIgnoreCase("0") || itemID.trim().isEmpty()) {
                            itemID = "0";
                        }

                        double ItemPrice = getSelectedItemPrice(itemID);
                        double QtyDbl = Double.parseDouble(itemExecuteQTY);

                        total3 += QtyDbl * ItemPrice;

                    }

                }


            } while (c.moveToNext());
        }

        updateTotalExecuteSalesOrder(orderID, String.valueOf(total3));

        c.close();
        return total3;


    }

    public String getMobFName() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_FName};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iFName = c.getColumnIndex(KEY_Emp_FName);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + " " + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobLName() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_LName};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iLName = c.getColumnIndex(KEY_Emp_LName);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + " " + c.getString(iLName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobCompany() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_Comp};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_Comp);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobCell() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_Cell};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_Cell);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobP1() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_P1};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_P1);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobP2() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_P2};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_P2);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobAddress() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_Add};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_Add);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    /*
     * Mobile User Data Ends
     */






    /*
     * Brand Table Works Starts
     */

    public String getMobCity() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_City};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_City);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobState() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_State};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_State);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getMobCountry() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_Country};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_Country);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }



    /*
     * Brand Table Works Ends
     */


    /*
     * Item Table Works Starts
     */

    public String getMobZipCode() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_Zip};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_Emp_Zip);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }


    /*
     * Item Table Works Ends
     */


    /*
     * Sales & Sales Details Work Starts
     */

    public String getMobEmail() {

        String result = "";

        String[] Column = new String[]{KEY_Emp_Email};
        Cursor c = db.query(Database_Table_mUser, Column, null, null, null, null, null);

        int iEmail = c.getColumnIndex(KEY_Emp_Email);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return result;
    }

    public int getSalesRow() {

        String result = "";
        int rs = 0;
        /*String que = "Select SUM(" + KEY_TOTAL_AMOUNT_FR_SALES + ") FROM " + DATABASE_TABLE_SALES;*/
        String que = "Select SUM(" + KEY_SALES_ORDER_TOTAL2 + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 1 AND strftime('%Y'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%Y',date('now')) AND  strftime('%m'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%m',date('now'))";

        Cursor c = db.rawQuery(que, null);

        try {
            if (c.moveToFirst()) {
                rs = c.getInt(0);
            }
            //Log.d("QuerySalesTotal", que);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }

        }


        return rs;

    }

    public int getSalesRowUnexecuted() {

        String result = "";
        int rs = 0;
        /*String que = "Select SUM(" + KEY_TOTAL_AMOUNT_FR_SALES + ") FROM " + DATABASE_TABLE_SALES;*/
        String que = "Select SUM(" + KEY_SALES_ORDER_TOTAL2 + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 0 AND strftime('%Y'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%Y',date('now')) AND  strftime('%m'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%m',date('now'))";

        Cursor c = db.rawQuery(que, null);

        try {
            if (c.moveToFirst()) {
                rs = c.getInt(0);
            }
            // Log.d("QuerySalesTotal", que);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }

        }


        return rs;

    }

    public int getExecuteSaleOrdersRow() {

        String result = "";
        int rs = 0;
        /*String que = "Select SUM(" + KEY_TOTAL_AMOUNT_FR_SALES + ") FROM " + DATABASE_TABLE_SALES;*/
        String que = "Select Count(" + KEY_SALES_ORDER_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 1 AND strftime('%Y'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%Y',date('now')) AND  strftime('%m'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%m',date('now'))";

        Cursor c = db.rawQuery(que, null);

        try {
            if (c.moveToFirst()) {
                rs = c.getInt(0);
            }
            //  Log.d("QuerySalesTotal", que);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }

        }

        return rs;

    }

    public int getUnExecuteSaleOrdersRow() {

        String result = "";
        int rs = 0;
        /*String que = "Select SUM(" + KEY_TOTAL_AMOUNT_FR_SALES + ") FROM " + DATABASE_TABLE_SALES;*/
        String que = "Select Count(" + KEY_SALES_ORDER_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 0 ";

        Cursor c = db.rawQuery(que, null);

        try {
            if (c.moveToFirst()) {
                rs = c.getInt(0);
            }
            // Log.d("QuerySalesTotal", que);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }

        }

        return rs;

    }

    public int getOrderRow() {

        /*String que = "Select Count(" + KEY_SALES_ID + ") FROM " + DATABASE_TABLE_SALES;*/
        String que = "Select Count(" + KEY_SALES_ORDER_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE strftime('%Y'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%Y',date('now')) AND  strftime('%m'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%m',date('now'))";

        Cursor c = db.rawQuery(que, null);
        //Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, null, null, null, null, null);

        //int result = c.getCount();
        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
        if (c != null && !c.isClosed())
            c.close();

        return count;
    }

    public int getShopVisitRow() {

        String que = "Select Count(" + KEY_SHOP_VISIT_ID + ") FROM " + DATABASE_TABLE_SHOP_VISIT + " WHERE strftime('%Y'," + KEY_SHOP_VISIT_DATETIME + ") = strftime('%Y',date('now')) AND strftime('%m'," + KEY_SHOP_VISIT_DATETIME + ") = strftime('%m',date('now')) AND strftime('%d'," + KEY_SHOP_VISIT_DATETIME + ") = strftime('%d',date('now'))";

        Cursor c = db.rawQuery(que, null);
        //Log.d("QueryShopsVisit", que);

        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
        if (c != null && !c.isClosed())
            c.close();

        return count;
    }

    public int getOrdersRowToday() {

        String que = "Select Count(" + KEY_SALES_ORDER_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER +
                " WHERE strftime('%Y'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%Y',date('now'))" +
                " AND strftime('%m'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%m',date('now'))" +
                " AND strftime('%d'," + KEY_SALES_ORDER_DATETIME + ") = strftime('%d',date('now'))";

        Cursor c = db.rawQuery(que, null);
        //Log.d("QueryShopsVisit", que);

        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
        if (c != null && !c.isClosed())
            c.close();

        return count;
    }




    /*
     * Sales & Sales Details Work Ends
     */


    /*
     * Sync Data Starts
     */

    public int getReturnsRowToday() {

        String que = "Select Count(" + KEY_SALES_RETURN_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " WHERE strftime('%Y'," + KEY_SALES_RETURN_DATE + ") = strftime('%Y',date('now')) AND strftime('%m'," + KEY_SALES_RETURN_DATE + ") = strftime('%m',date('now')) AND strftime('%d'," + KEY_SALES_RETURN_DATE + ") = strftime('%d',date('now'))";

        Cursor c = db.rawQuery(que, null);
        //Log.d("QueryShopsVisit", que);

        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
            }
        if (c != null && !c.isClosed())
            c.close();

        return count;
    }

    public void DeleteSyncCustomer() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_PUBLISHED + " = 1";

        db.execSQL(deleteQuery);
        Log.d("query", deleteQuery);

    }

    public void TruncateCustomerRoutes() {
        // TODO Auto-generated method stub

        db.execSQL("DELETE FROM  " + DATABASE_TABLE_CUSTOMER_ROUTE);


        //CREATE Customer Route TABLE
		/*sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_ROUTE + " ("
					  + KEY_ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					  + KEY_ROUTE_NET_ID + " INTEGER NOT NULL, "
					  + KEY_ROUTE_SAVED + " INTEGER DEFAULT 0, "
					  + KEY_ROUTE_UPDATE + " INTEGER DEFAULT 0, "
					  + KEY_ROUTE_NAME + " TEXT NOT NULL); ";

		db.execSQL(sql);*/


    }

    public void TruncateCustomerType() {
        // TODO Auto-generated method stub

        db.execSQL("DELETE FROM  " + DATABASE_TABLE_CUSTOMER_TYPE);


        /*		// Create Customer Type Table
         *//*sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_TYPE + " ("
					  + KEY_CUSTOMER_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					  + KEY_CUSTOMER_TYPE_NAME + " TEXT );";

		db.execSQL(sql);*/


    }

    public ArrayList<HashMap<String, String>> getUnsyncCustomers() {
        ArrayList<HashMap<String, String>> customerDetails;

        customerDetails = new ArrayList<>();

        // String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_UPDATE + " = 1";
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_UPDATE + " = 1 OR " + KEY_CUSTOMER_UPDATE + " = 2";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_CUSTOMER);
        int iFName = cursor.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLName = cursor.getColumnIndex(KEY_CUSTOMER_LNAME);
        int iCoName = cursor.getColumnIndex(KEY_COMPANY_NAME);
        int iCell = cursor.getColumnIndex(KEY_CUSTOMER_CELL);
        int iP1 = cursor.getColumnIndex(KEY_CUSTOMER_PHONE1);
        int iP2 = cursor.getColumnIndex(KEY_CUSTOMER_PHONE2);
        int iAdd = cursor.getColumnIndex(KEY_CUSTOMER_ADDRESS);
        int iCity = cursor.getColumnIndex(KEY_CUSTOMER_CITY);
        // Umais starts here
        int iArea = cursor.getColumnIndex(KEY_CUSTOMER_AREA);
        int iRouteId = cursor.getColumnIndex(KEY_CUSTOMER_ROUTE_ID);
        int iOpeningBalOld = cursor.getColumnIndex(KEY_CUSTOMER_OPENING_BALANCE_OLD);
        int iOpeningBalNew = cursor.getColumnIndex(KEY_CUSTOMER_OPENING_BALANCE_NEW);
        int iCnic = cursor.getColumnIndex(KEY_CUSTOMER_CNIC);
        int iCustTypeId = cursor.getColumnIndex(KEY_CUSTOMER_TYPE);
        int iNumberVerified = cursor.getColumnIndex(KEY_CUSTOMER_NUMBER_VERIFIED);
        int iSmsCOde = cursor.getColumnIndex(KEY_CUSTOMER_SMS_CODE);
        int iAdvancePayment = cursor.getColumnIndex(KEY_CUSTOMER_ADVANCE_PAYMENT);
        // Umais end
        int iState = cursor.getColumnIndex(KEY_CUSTOMER_STATE);
        int iCountry = cursor.getColumnIndex(KEY_CUSTOMER_COUNTRY);
        int iNotes = cursor.getColumnIndex(KEY_CUSTOMER_NOTES);
        int iEmail = cursor.getColumnIndex(KEY_CUSTOMER_EMAIL);

        int iLat = cursor.getColumnIndex(KEY_CUSTOMER_LATTITUDE);
        int iLong = cursor.getColumnIndex(KEY_CUSTOMER_LONGITUDE);
        int iMap = cursor.getColumnIndex(KEY_CUSTOMER_MAPNAME);
        int iRad = cursor.getColumnIndex(KEY_CUSTOMER_RADIUS);
        int iNetId = cursor.getColumnIndex(KEY_CUSTOMER_NET_ID);
        int iLastUpdate = cursor.getColumnIndex(KEY_CUSTOMER_LAST_UPDATE);
        int iCustomerCeleb = cursor.getColumnIndex(KEY_CUSTOMER_CELEB);
        int iCustomerTaxtType = cursor.getColumnIndex(KEY_CUSTOMER_FILER_NON_FILER);
        int iCustomerShopCategory = cursor.getColumnIndex(KEY_CUSTOMER_SHOP_CATEGORY_ID);
         int iCustomerSubShopCategory = cursor.getColumnIndex(KEY_CUSTOMER_SUB_SHOP_CATEGORY_ID);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", cursor.getString(iId));
                map.put("fname", cursor.getString(iFName));
                map.put("lname", cursor.getString(iLName));
                map.put("comp_name", cursor.getString(iCoName));
                map.put("cell", cursor.getString(iCell));
                map.put("ph1", cursor.getString(iP1));
                map.put("ph2", cursor.getString(iP2));
                map.put("add", cursor.getString(iAdd));
                map.put("city", cursor.getString(iCity));
                map.put("area", cursor.getString(iArea));

                map.put("state", cursor.getString(iState));
                map.put("country", cursor.getString(iCountry));
                map.put("notes", cursor.getString(iNotes));
                map.put("email", cursor.getString(iEmail));

                map.put("lat", cursor.getString(iLat));
                map.put("long", cursor.getString(iLong));
                // iMap change to iArea by me
                map.put("map", cursor.getString(iMap));
                map.put("rad", cursor.getString(iRad));
                // Umais Starts here
                //map.put("area", cursor.getString(iArea));
                map.put("cust_type", cursor.getString(iCustTypeId));
                map.put("cust_route", cursor.getString(iRouteId));
                map.put("cnic", cursor.getString(iCnic));
                map.put("opening_bal_old", cursor.getString(iOpeningBalOld));
                map.put("opening_bal_new", cursor.getString(iOpeningBalNew));
                map.put("net_id", cursor.getString(iNetId));

                map.put("number_verified", cursor.getString(iNumberVerified));
                map.put("sms_code", cursor.getString(iSmsCOde));
                map.put("advance_payment", cursor.getString(iAdvancePayment));
                map.put("last_update", cursor.getString(iLastUpdate));

                map.put("customer_celeb", cursor.getString(iCustomerCeleb));
                map.put("customer_tax_type", cursor.getString(iCustomerTaxtType));
                map.put("shop_id", cursor.getString(iCustomerShopCategory));
               try {
                    map.put("sub_shop_id", cursor.getString(iCustomerSubShopCategory));
                } catch (Exception e) {
                    e.getMessage();
                }
                // End

                customerDetails.add(map);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return customerDetails;

    }

    public void updateCustomerSyncStatus(String id) {

        int stat = 1;

        if (!(id.trim().isEmpty())) {
            String updateQuery = "Update " + DATABASE_TABLE_CUSTOMER_DETAILS + " set " + KEY_CUSTOMER_PUBLISHED + " = " + stat + " where " + KEY_ID_CUSTOMER + "=" + id;

            Log.d("query", updateQuery);
            db.execSQL(updateQuery);

        }

    }

    public long InsertSyncedCustomer(String id, String netId, String fname, String lname,
                                     String co_name, String cell, String p1, String p2,
                                     String obOld, String obNew, String creditAmount,
                                     String cnic, String add, String city, String state,
                                     String country, String email, String notes, String lat,
                                     String longi, String mapName, String radius, int publish,
                                     int cust_route_id, int cust_type, String appPayable,
                                     String smsCode, String numberVerified,
                                     String advancePayment, String lastUpdate,
                                     String customerCeleb, String filer_non_filer,
                                     String customer_category, String cust_sub_cat) {

        ContentValues cv = new ContentValues();

        int IdInt = Integer.parseInt(id);

        cv.put(KEY_ID_CUSTOMER, IdInt);
        cv.put(KEY_CUSTOMER_NET_ID, netId);
        cv.put(KEY_CUSTOMER_FNAME, regex(fname));
        cv.put(KEY_CUSTOMER_LNAME, regex(lname));
        cv.put(KEY_COMPANY_NAME, regex(co_name));
        cv.put(KEY_CUSTOMER_CELL, cell);
        cv.put(KEY_CUSTOMER_PHONE1, p1);
        cv.put(KEY_CUSTOMER_PHONE2, p2);
        cv.put(KEY_CUSTOMER_OPENING_BALANCE_OLD, obOld);
        cv.put(KEY_CUSTOMER_OPENING_BALANCE_NEW, obNew);
        cv.put(KEY_CUSTOMER_CREDIT_AMOUNT, creditAmount);
        cv.put(KEY_CUSTOMER_CNIC, cnic);
        cv.put(KEY_CUSTOMER_ADDRESS, add);
        cv.put(KEY_CUSTOMER_CITY, city);
        cv.put(KEY_CUSTOMER_STATE, state);
        cv.put(KEY_CUSTOMER_COUNTRY, country);
        cv.put(KEY_CUSTOMER_EMAIL, email);
        cv.put(KEY_CUSTOMER_NOTES, notes);
        cv.put(KEY_CUSTOMER_ROUTE_ID, cust_route_id);
        cv.put(KEY_CUSTOMER_TYPE, cust_type);
        cv.put(KEY_CUSTOMER_APP_PAYABLE, appPayable);
        cv.put(KEY_CUSTOMER_LATTITUDE, lat);
        cv.put(KEY_CUSTOMER_LONGITUDE, longi);
        cv.put(KEY_CUSTOMER_MAPNAME, mapName);
        cv.put(KEY_CUSTOMER_RADIUS, radius);

        cv.put(KEY_CUSTOMER_ADVANCE_PAYMENT, advancePayment);
        cv.put(KEY_CUSTOMER_SMS_CODE, smsCode);
        cv.put(KEY_CUSTOMER_NUMBER_VERIFIED, numberVerified);

        cv.put(KEY_CUSTOMER_LAST_UPDATE, lastUpdate);
        cv.put(KEY_CUSTOMER_CELEB, customerCeleb);

        cv.put(KEY_CUSTOMER_UPDATE, 0);
        cv.put(KEY_CUSTOMER_GPS, 1);
        cv.put(KEY_CUSTOMER_SEARCH, IdInt + " " + regex(co_name) + " ( " + regex(fname) + " )");
        cv.put(KEY_CUSTOMER_PUBLISHED, publish);

        cv.put(KEY_CUSTOMER_FILER_NON_FILER, filer_non_filer);
        cv.put(KEY_CUSTOMER_SHOP_CATEGORY_ID, customer_category);
        cv.put(KEY_CUSTOMER_SUB_SHOP_CATEGORY_ID, cust_sub_cat);

        long insertId = db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;
    }


    //GPS CUSTOMER SYNC STARTS

    public int getMaxIdFromRoute() {

        String que = "SELECT MAX(" + KEY_ROUTE_NET_ID + ") FROM " + DATABASE_TABLE_CUSTOMER_ROUTE;

        Cursor c = db.rawQuery(que, null);

        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);

        c.close();
        return maxid;

    }

    public long InsertCustomerRoute(String net_id, String name, int saved, int update, int day) {

        ContentValues cv = new ContentValues();


        cv.put(KEY_ROUTE_NET_ID, net_id);
        cv.put(KEY_ROUTE_NAME, name);
        cv.put(KEY_ROUTE_SAVED, saved);
        cv.put(KEY_ROUTE_UPDATE, update);
        cv.put(KEY_ROUTE_DAY, day);


        long insertId = db.insert(DATABASE_TABLE_CUSTOMER_ROUTE, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;
    }

    public ArrayList<HashMap<String, String>> getCustomerRoute() {
        ArrayList<HashMap<String, String>> routeList;

        routeList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_CUSTOMER_ROUTE + " WHERE " + KEY_ROUTE_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iRouteId = cursor.getColumnIndex(KEY_ROUTE_NET_ID);
        int iRouteName = cursor.getColumnIndex(KEY_ROUTE_NAME);
        int iRouteSaved = cursor.getColumnIndex(KEY_ROUTE_SAVED);
        int iDay = cursor.getColumnIndex(KEY_ROUTE_DAY);



        /*int iLati = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iLongi = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iStatus = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        */


        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("route_id", cursor.getString(iRouteId));
                    map.put("route_name", cursor.getString(iRouteName));
                    map.put("route_saved", cursor.getString(iRouteSaved));
                    map.put("day", cursor.getString(iDay));

                    routeList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return routeList;

    }

    public ArrayList<HashMap<String, String>> getUnsyncGPSCustomers() {

        ArrayList<HashMap<String, String>> customerGPSDetails;

        customerGPSDetails = new ArrayList<>();

        String selectQuery = "SELECT  " + KEY_ID_CUSTOMER + ", " + KEY_CUSTOMER_LATTITUDE + ", " + KEY_CUSTOMER_LONGITUDE + ", " + KEY_CUSTOMER_MAPNAME + ", " + KEY_CUSTOMER_RADIUS + " FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_GPS + " = 0";

        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_CUSTOMER);

        int iLat = cursor.getColumnIndex(KEY_CUSTOMER_LATTITUDE);
        int iLong = cursor.getColumnIndex(KEY_CUSTOMER_LONGITUDE);
        int iMap = cursor.getColumnIndex(KEY_CUSTOMER_MAPNAME);
        int iRad = cursor.getColumnIndex(KEY_CUSTOMER_RADIUS);

        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("lat", cursor.getString(iLat));
                    map.put("long", cursor.getString(iLong));
                    map.put("map", cursor.getString(iMap));
                    map.put("rad", cursor.getString(iRad));

                    customerGPSDetails.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return customerGPSDetails;

    }


    //GPS CUSTOMER SYNC ENDS

    public void updateSavedRoute(String route_netID) {

        String updateQuery = "Update " + DATABASE_TABLE_CUSTOMER_ROUTE + " set " + KEY_ROUTE_SAVED + " = " + route_netID;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);

    }

    public int getSavedRouteID() {

        int result = 0;

        String[] Column = new String[]{KEY_ROUTE_SAVED};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Column, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_ROUTE_SAVED);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getInt(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return result;
    }

    public ArrayList<HashMap<String, String>> getUnsyncBrands() {
        ArrayList<HashMap<String, String>> customerDetails;

        customerDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_BRAND + " WHERE " + KEY_BRAND_PUBLISHED + " = 0";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_BRAND);
        int iBrandName = cursor.getColumnIndex(KEY_BRAND_NAME);

        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("brname", cursor.getString(iBrandName));

                    customerDetails.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return customerDetails;

    }

    public String getBrandName(String id) {

        String query = "select name from product_brands where id = " + id;

        Cursor cursor = db.rawQuery(query, null);
        String name = "No Product Found";
        if (cursor.moveToFirst()) {

            name = cursor.getString(0);

        }
        cursor.close();

        return name;
    }

    public ArrayList<HashMap<String, String>> getAllBrands() {
        ArrayList<HashMap<String, String>> customerDetails;

        customerDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_BRAND + " ;";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_BRAND);
        int iBrandName = cursor.getColumnIndex(KEY_BRAND_NAME);
        HashMap<String, String> map1 = new HashMap<>();
        map1.put("id", "0");
        map1.put("bname", "All Brands");

        customerDetails.add(map1);
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("bname", cursor.getString(iBrandName));

                    customerDetails.add(map);
                } while (cursor.moveToNext());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return customerDetails;

    }
   public ArrayList<HashMap<String, String>> getAllBrandsForOrderBooking() {
        ArrayList<HashMap<String, String>> customerDetails;

        customerDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_BRAND + " ;";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_BRAND);
        int iBrandName = cursor.getColumnIndex(KEY_BRAND_NAME);
//        HashMap<String, String> map1 = new HashMap<>();
////        map1.put("id", "0");
////        map1.put("bname", "All Brands");
//
//        customerDetails.add(map1);
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("bname", cursor.getString(iBrandName));

                    customerDetails.add(map);
                } while (cursor.moveToNext());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();
        return customerDetails;

    }

    public void updateBrandsSyncStatus(String id) {

        int stat = 1;

        if (!(id.trim().isEmpty())) {
            String updateQuery = "Update " + DATABASE_TABLE_BRAND + " set " + KEY_BRAND_PUBLISHED + " = " + stat + " where " + KEY_ID_BRAND + "=" + id;

            Log.d("query", updateQuery);
            db.execSQL(updateQuery);

        }

    }

    public ArrayList<HashMap<String, String>> getUnsyncItemType() {
        ArrayList<HashMap<String, String>> ItemTypeDetails;

        ItemTypeDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_ITEM_TYPE + " WHERE " + KEY_ITEMTYPE_PUBLISHED + " = 0";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_ITEM);
        int iItemName = cursor.getColumnIndex(KEY_ITEM_NAME);
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("itemtype", cursor.getString(iItemName));

                    ItemTypeDetails.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return ItemTypeDetails;

    }

    public long InsertSyncedInventory(String id, String brid, String tid, String vid, String name
            , String uc, String sp, String sku, String tax, String packing, String qty,
                                      String details/*, String image*/, String subtypeid,
                                      String mrp_price, String tax_mrp, String is_taxable) {

        ContentValues cv = new ContentValues();

        int IdInt = Integer.parseInt(id);
        int BrIdInt = Integer.parseInt(brid);
        int TypeIdInt = Integer.parseInt(tid);
      /*  int VendorIdInt = 0;

        if (!vid.equalsIgnoreCase("null"))
            VendorIdInt = Integer.parseInt(vid);*/
        //int PackingInt = Integer.parseInt(packing);
        //int QtyInt = Integer.parseInt(qty);

        double Tax = Double.parseDouble(tax);

        cv.put(KEY_ID_INVENT, IdInt);
        cv.put(KEY_BRAND_ID_FROM_INVENT, BrIdInt);
        cv.put(KEY_ITEM_ID_FROM_INVENT, TypeIdInt);
        cv.put(KEY_VENDOR_ID, "0");
        cv.put(KEY_INVENT_NAME, name);
        cv.put(KEY_UNIT_COST, uc);
        cv.put(KEY_SALE_PRICE, sp);
        cv.put(KEY_SKU, sku);
        cv.put(KEY_TAX_APPLIED, Tax);
        cv.put(KEY_PACKING, packing);
        cv.put(KEY_QUANTITY, qty);
        cv.put(KEY_DETAILS, details);

        cv.put("mrp_price", mrp_price);
        cv.put("tax_mrp", tax_mrp);
        cv.put("is_taxable", is_taxable);

        //cv.put(KEY_INVENTORY_IMAGE, image);
        cv.put(KEY_INVENTORY_PUBLISHED, 1);
        cv.put("subtypeid", subtypeid);
        cv.put(KEY_INVENTORY_SEARCH, sku + " ( " + name + " )");


        long insertId = db.insert(DATABASE_TABLE_INVENTORY, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;
    }

    public void updateItemTypeSyncStatus(String id) {

        int stat = 1;

        if (!(id.trim().isEmpty())) {
            String updateQuery = "Update " + DATABASE_TABLE_ITEM_TYPE + " set " + KEY_ITEMTYPE_PUBLISHED + " = " + stat + " where " + KEY_ID_ITEM + "=" + id;

            Log.d("query", updateQuery);
            db.execSQL(updateQuery);
        }

    }

    public void DeleteSyncInventory() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_INVENTORY + " WHERE " + KEY_INVENTORY_PUBLISHED + " = 1";

        db.execSQL(deleteQuery);
        Log.d("query", deleteQuery);

    }

    public void truncateCustomerPricing() {

        db.execSQL("DELETE FROM  " + DATABASE_TABLE_CUSTOMER_PRICING);

/*		sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_PRICING + " ("
					  + KEY_CUSTOMER_PRICING_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
					  + KEY_CUSTOMER_PRICING_TYPE_ID + " INTEGER, "
					  + KEY_CUSTOMER_PRICING_PRODUCT_ID + " INTEGER, "
					  + KEY_CUSTOMER_PRICING_TRADE_PRICE + " REAL, "
					  + KEY_CUSTOMER_PRICING_DISCOUNT_1 + " REAL, "
					  + KEY_CUSTOMER_PRICING_DISCOUNT_2 + " REAL, "
					  + KEY_CUSTOMER_PRICING_TRADE_OFFER + " REAL, "
					  + KEY_CUSTOMER_PRICING_SCHEME + " REAL, "
					  + KEY_CUSTOMER_PRICING_SCHEME_VAL + " REAL, "
					  + KEY_CUSTOMER_PRICING_SCHEME_PRODUCT + " INTEGER DEFAULT 0, "
					  + KEY_CUSTOMER_PRICING_TAX_1 + " REAL, "
					  + KEY_CUSTOMER_PRICING_TAX_2 + " REAL, "
					  + KEY_CUSTOMER_PRICING_TAX_3 + " REAL, "
					  + KEY_CUSTOMER_PRICING_SUBTOTAL + " REAL );";

		db.execSQL(sql);*/
    }

    public ArrayList<HashMap<String, String>> getUnsyncInventory() {
        ArrayList<HashMap<String, String>> InventoryDetails;

        InventoryDetails = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_INVENTORY + " WHERE " + KEY_INVENTORY_PUBLISHED + " = 0";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_INVENT);
        int iInventName = cursor.getColumnIndex(KEY_INVENT_NAME);
        int iBrandId = cursor.getColumnIndex(KEY_BRAND_ID_FROM_INVENT);
        int iTypeId = cursor.getColumnIndex(KEY_ITEM_ID_FROM_INVENT);
        int iUnitCost = cursor.getColumnIndex(KEY_UNIT_COST);
        int iSalePrice = cursor.getColumnIndex(KEY_SALE_PRICE);
        int iSku = cursor.getColumnIndex(KEY_SKU);
        int iPacking = cursor.getColumnIndex(KEY_PACKING);
        int iDetails = cursor.getColumnIndex(KEY_DETAILS);
        int iVendorId = cursor.getColumnIndex(KEY_VENDOR_ID);
        int iQty = cursor.getColumnIndex(KEY_QUANTITY);
        int iTax = cursor.getColumnIndex(KEY_TAX_APPLIED);

        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("inventoryname", cursor.getString(iInventName));
                    map.put("brandid", cursor.getString(iBrandId));
                    map.put("typeid", cursor.getString(iTypeId));
                    map.put("unitcost", cursor.getString(iUnitCost));
                    map.put("saleprice", cursor.getString(iSalePrice));
                    map.put("sku", cursor.getString(iSku));
                    map.put("packing", cursor.getString(iPacking));
                    map.put("details", cursor.getString(iDetails));
                    map.put("vendorid", cursor.getString(iVendorId));
                    map.put("qty", cursor.getString(iQty));
                    map.put("tax", cursor.getString(iTax));

                    InventoryDetails.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return InventoryDetails;

    }

    public void updateInventorySyncStatus(String id) {

        int stat = 1;

        if (!(id.trim().isEmpty())) {
            String updateQuery = "Update " + DATABASE_TABLE_INVENTORY + " set " + KEY_INVENTORY_PUBLISHED + " = " + stat + " where " + KEY_ID_INVENT + "=" + id;

            Log.d("query", updateQuery);
            db.execSQL(updateQuery);

        }

    }

    public void DeleteSyncBrands() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_BRAND + " WHERE " + KEY_BRAND_PUBLISHED + " = 1";

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public void deleteSaleOrder() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public void deleteSaleReturn() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public void deleteOrderDetail() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_ORDER_DETAILS;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public void deleteReturnDetail() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_RETURN_DETAILS;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public void deletePaymentRecieved() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_PAYMENT_RECIEVED;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public long InsertSyncedBrands(String id, String name) {

        ContentValues cv = new ContentValues();

        int IdInt = Integer.parseInt(id);

        cv.put(KEY_ID_BRAND, IdInt);
        cv.put(KEY_BRAND_NAME, name);
        cv.put(KEY_BRAND_PUBLISHED, 1);

        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);


        long insertId = db.insert(DATABASE_TABLE_BRAND, null, cv); //Gives Last Autoincremented Inserted value ID


        return insertId;
    }

    public long InsertReturnReason(String id, String name) {

        ContentValues cv = new ContentValues();

        int IdInt = Integer.parseInt(id);

        cv.put(KEY_RETURN_REASON_ID, IdInt);
        cv.put(KEY_RETURN_REASON_NAME, name);
        //cv.put(KEY_BRAND_PUBLISHED, 1);

        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);


        long insertId = db.insert(Database_Table_Return_Reason, null, cv); //Gives Last Autoincremented Inserted value ID


        return insertId;
    }


    //Sync Order

    public void deleteReturnReason() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + Database_Table_Return_Reason;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public void DeleteSyncItemtype() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_ITEM_TYPE + " WHERE " + KEY_ITEMTYPE_PUBLISHED + " = 1";

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }

    public long InsertSyncedItemType(String id, String name) {

        ContentValues cv = new ContentValues();

        int IdInt = Integer.parseInt(id);

        cv.put(KEY_ID_ITEM, IdInt);
        cv.put(KEY_ITEM_NAME, name);
        cv.put(KEY_ITEMTYPE_PUBLISHED, 1);


        long insertId = db.insert(DATABASE_TABLE_ITEM_TYPE, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertId;
    }

    public void updateOpeningBalanceCustomer(String id, String balance) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
       /* String updateQuery = "Update " + DATABASE_TABLE_CUSTOMER_DETAILS + " set " + KEY_CUSTOMER_OPENING_BALANCE_OLD + " = " + balance + " where " + KEY_ID_CUSTOMER + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);*/
        ContentValues cv = new ContentValues();
        cv.put(KEY_CUSTOMER_OPENING_BALANCE_OLD, balance);
        cv.put(KEY_CUSTOMER_UPDATE, 2);


        db.update(DATABASE_TABLE_CUSTOMER_DETAILS, cv, KEY_ID_CUSTOMER + " = " + idInt, null);

    }

    public void updateCustomerSmsCode(String id, String smsCode) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
       /* String updateQuery = "Update " + DATABASE_TABLE_CUSTOMER_DETAILS + " set " + KEY_CUSTOMER_OPENING_BALANCE_OLD + " = " + balance + " where " + KEY_ID_CUSTOMER + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);*/
        ContentValues cv = new ContentValues();
        cv.put(KEY_CUSTOMER_SMS_CODE, smsCode);
        cv.put(KEY_CUSTOMER_UPDATE, 2);


        db.update(DATABASE_TABLE_CUSTOMER_DETAILS, cv, KEY_ID_CUSTOMER + " = " + idInt, null);

    }

    public void updateCustomerLastUpdate(String id, String lastUpdateDatetime) {

        int idInt = Integer.parseInt(id);

        ContentValues cv = new ContentValues();
        cv.put(KEY_CUSTOMER_LAST_UPDATE, lastUpdateDatetime);
        cv.put(KEY_CUSTOMER_UPDATE, 2);


        db.update(DATABASE_TABLE_CUSTOMER_DETAILS, cv, KEY_ID_CUSTOMER + " = " + idInt, null);

    }


    /*
     * Sync Data Ends
     */



    /*
            SUMMARY STUFFS
     */

    public void updateCustomerNumberVerified(String id, int numberVerified) {

        int idInt = Integer.parseInt(id);
        //int stat = 1;
       /* String updateQuery = "Update " + DATABASE_TABLE_CUSTOMER_DETAILS + " set " + KEY_CUSTOMER_OPENING_BALANCE_OLD + " = " + balance + " where " + KEY_ID_CUSTOMER + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);*/
        ContentValues cv = new ContentValues();
        cv.put(KEY_CUSTOMER_NUMBER_VERIFIED, numberVerified);
        cv.put(KEY_CUSTOMER_UPDATE, 2);


        db.update(DATABASE_TABLE_CUSTOMER_DETAILS, cv, KEY_ID_CUSTOMER + " = " + idInt, null);

    }


    //CART STUFFS


    //SETTINGS TABLE DATA

    public void updateAmountRecievedSaleOrder(String order_id, String value) {

        int idInt = Integer.parseInt(order_id);
        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_ORDER_AMOUNT_RECIEVED, value);
        cv.put(KEY_SALES_ORDER_UPDATE, 1);


        db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + idInt, null);

    }

    public long createSettingsEntryAtStartup() {

        ContentValues cv = new ContentValues();

        cv.put(KEY_SETTINGS_ID, 1);
        cv.put(KEY_AutoSync_SETTINGS, 0);
        cv.put(KEY_TimeIn_SETTINGS, "0:0");
        cv.put(KEY_TimeOut_SETTINGS, "0:0");
        cv.put(KEY_SyncDur_SETTINGS, 0);
        cv.put(KEY_SyncDur_INDEX_SETTINGS, 0);

        cv.put(KEY_CLockIn_SETTINGS, 0);
        cv.put(KEY_CLockIn_Total_SETTINGS, "0");

        cv.put(KEY_Timing_SETTINGS, 1);
        cv.put(KEY_Route_SETTINGS, 1);
        cv.put(KEY_Reporting_SETTINGS, 1);
        cv.put(KEY_inventPg_SETTINGS, 1);
        cv.put(KEY_CustomerPg_SETTINGS, 1);
        cv.put(KEY_PlaceOrderPg_SETTINGS, 1);
        cv.put(KEY_SummaryPg_SETTINGS, 1);
        cv.put(KEY_SyncPg_SETTINGS, 1);

        cv.put(KEY_Password_SETTINGS, "");
        cv.put(KEY_AdminEmail_SETTINGS, "");
        cv.put(KEY_AdminPhone_SETTINGS, "");
        cv.put(KEY_en_Password_SETTINGS, 0);

        db.insert(DATABASE_TABLE_SETTINGS, null, cv);

        long insertSettingsId = db.insert(DATABASE_TABLE_SETTINGS, null, cv); //Gives Last Autoincremented Inserted value ID
        return insertSettingsId;
    }

    public int CheckSettingsData() {


/*
        String Column[] = new String[]{KEY_empId_CLOCKIN, KEY_status_CLOCKIN};
        Cursor c = db.query(DATABASE_TABLE_CLOCKIN, Column, KEY_empId_CLOCKIN+" = "+empid, null, null, null, null);
*/
        String que = "Select Count(+" + KEY_SETTINGS_ID + ") FROM " + DATABASE_TABLE_SETTINGS;

        Cursor c = db.rawQuery(que, null);


        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
                c.close();
            }


        return count;

    }
     public int checkRouteDayAvailable() {


/*
        String Column[] = new String[]{KEY_empId_CLOCKIN, KEY_status_CLOCKIN};
        Cursor c = db.query(DATABASE_TABLE_CLOCKIN, Column, KEY_empId_CLOCKIN+" = "+empid, null, null, null, null);
*/
        String que = "Select Count(+" + KEY_ROUTE_NET_ID + ") FROM " + DATABASE_TABLE_CUSTOMER_ROUTE + " where "+ KEY_ROUTE_DAY + " != 0 ";

        Cursor c = db.rawQuery(que, null);


        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
                c.close();
            }


        return count;

    }

    public void updateExecuteDone(List<String> id) {

        ContentValues cv = new ContentValues();

        for (String s : id) {
            cv.put(KEY_SALES_ORDER_EXECUTE_COMPLETE, 1);
            cv.put(KEY_SALES_ORDER_EXECUTION_DATE, getDateTime());
            cv.put(KEY_SALES_ORDER_UPDATE, 1);
            long idUPD = db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + s, null);

            Log.d("UPDid", idUPD + "ss");
        }

    }

    public void updateSalesOrderNewVales(int order_id, String newValues, String notes) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_ORDER_VALUES, newValues);
        cv.put(KEY_SALES_ORDER_NOTES, notes);
        //cv.put(KEY_SALES_ORDER_EXECUTE_COMPLETE, 1);
        cv.put(KEY_SALES_ORDER_UPDATE, 1);

        long id = db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

        Log.d("iUpd", id + "ss");

    }

    public void executeUpdateSalesOrderNewVales(int order_id, String newValues, String notes) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_ORDER_VALUES, newValues);
        cv.put(KEY_SALES_ORDER_NOTES, notes);
        cv.put(KEY_SALES_ORDER_EXECUTE_COMPLETE, 1);
        cv.put(KEY_SALES_ORDER_EXECUTION_DATE, getDateTime());
        cv.put(KEY_SALES_ORDER_UPDATE, 1);

        long id = db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

        Log.d("iUpd", id + "ss");

    }

    public void updateSalesOrderPrintExecute(int order_id) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_SALES_ORDER_EXECUTE_COMPLETE, 1);
        cv.put(KEY_SALES_ORDER_EXECUTION_DATE, getDateTime());
        cv.put(KEY_SALES_ORDER_PRINTING_DATE, getDateTime());
        cv.put(KEY_SALES_ORDER_UPDATE, 1);

        long id = db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

        Log.d("iUpd", id + "ss");

    }

    public void updateSalesReturnPrintExecute(int order_id) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_SALES_RETURN_EXECUTE_COMPLETE, 1);
        cv.put(KEY_SALES_RETURN_EXECUTION_DATE, getDateTime());
        cv.put(KEY_SALES_RETURN_PRINTING_DATE, getDateTime());
        cv.put(KEY_SALES_RETURN_UPDATE, 1);

        long id = db.update(DATABASE_TABLE_SALESMAN_SALES_RETURN, cv, KEY_SALES_RETURN_ID + " = " + order_id, null);

        Log.d("iUpd", id + "ss");

    }

    public void updateSalesOrderTotal(int order_id, String total, String total2, String discount) {

        ContentValues cv = new ContentValues();
        //cv.put(KEY_SALES_ORDER_TOTAL, total);
        cv.put(KEY_SALES_ORDER_TOTAL2, total2);
        cv.put(KEY_SALES_ORDER_DISCOUNT, discount);
        cv.put(KEY_SALES_ORDER_UPDATE, 1);

        long id = db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

        Log.d("iUpd", id + "ss");

    }

    public void updateSalesOrderTotalExecute(int order_id, String total2, String discount) {

        ContentValues cv = new ContentValues();
        //cv.put(KEY_SALES_ORDER_TOTAL, total);
        cv.put(KEY_SALES_ORDER_TOTAL2, total2);
        cv.put(KEY_SALES_ORDER_DISCOUNT, discount);
        cv.put(KEY_SALES_ORDER_UPDATE, 1);

        long id = db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

        Log.d("iUpd", id + "ss");

    }

    public void updateOrderDetails(int order_id, String productId, String quantity,
                                   String quantityExe, String tradePrice, String discount1, String discount2, String tradeOffer, String scheme, String schemeQty, String schemeFormula, String schemeProduct, String tax1, String tax2, String tax3, String subTotal,
                                   double t_o_v,
                                   double d_v_1,
                                   double d_v_2,
                                   double t_type,
                                   double t_mrp_type,
                                   double t_val,
                                   String mrp_price) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_ORDER_DETAIL_QUANTITY, quantity);
        cv.put(KEY_ORDER_DETAIL_QUANTITY_EXE, quantityExe);
        cv.put(KEY_ORDER_DETAIL_TRADE_PRICE, tradePrice);
        cv.put(KEY_ORDER_DETAIL_DISCOUNT_1, discount1);
        cv.put(KEY_ORDER_DETAIL_DISCOUNT_2, discount2);
        cv.put(KEY_ORDER_DETAIL_TRADE_OFFER, tradeOffer);
        cv.put(KEY_ORDER_DETAIL_SCHEME, scheme);
        cv.put(KEY_ORDER_DETAIL_SCHEME_QTY, schemeQty);
        cv.put(KEY_ORDER_DETAIL_SCHEME_FORMULA, schemeFormula);
        cv.put(KEY_ORDER_DETAIL_SCHEME_PRODUCT, schemeProduct);
        cv.put(KEY_ORDER_DETAIL_TAX_1, tax1);
        cv.put(KEY_ORDER_DETAIL_TAX_2, tax2);
        cv.put(KEY_ORDER_DETAIL_TAX_3, tax3);
        cv.put(KEY_ORDER_DETAIL_SUBTOTAL, subTotal);
        cv.put(KEY_ORDER_DETAIL_DV1, d_v_1);
        cv.put(KEY_ORDER_DETAIL_DV2, d_v_2);
        cv.put(KEY_ORDER_DETAIL_TOV, t_o_v);
        cv.put(KEY_ORDER_DETAIL_T_TYPE, t_type);
        cv.put(KEY_ORDER_DETAIL_T_MRP_TYPE, t_mrp_type);
        cv.put(KEY_ORDER_DETAIL_T_VAL, t_val);
        cv.put(KEY_ORDER_DETAIL_MRP_PRICE, mrp_price);


        long id = db.update(DATABASE_TABLE_ORDER_DETAILS, cv, KEY_ORDER_DETAIL_ORDER_ID + " = " + order_id + " AND " + KEY_ORDER_DETAIL_PRODUCT_ID + " = " + productId, null);

        Log.d("iUpdOrderDetail", id + "ss");

    }

    public void updateOrderDetails(int order_id, String productId, String quantity,
                                   String quantityExe, String tradePrice, String discount1,
                                   String discount2, String tradeOffer, String scheme, String schemeQty, String schemeFormula, String schemeProduct, String tax1, String tax2, String tax3, String subTotal) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_ORDER_DETAIL_QUANTITY, quantity);
        cv.put(KEY_ORDER_DETAIL_QUANTITY_EXE, quantityExe);
        cv.put(KEY_ORDER_DETAIL_TRADE_PRICE, tradePrice);
        cv.put(KEY_ORDER_DETAIL_DISCOUNT_1, discount1);
        cv.put(KEY_ORDER_DETAIL_DISCOUNT_2, discount2);
        cv.put(KEY_ORDER_DETAIL_TRADE_OFFER, tradeOffer);
        cv.put(KEY_ORDER_DETAIL_SCHEME, scheme);
        cv.put(KEY_ORDER_DETAIL_SCHEME_QTY, schemeQty);
        cv.put(KEY_ORDER_DETAIL_SCHEME_FORMULA, schemeFormula);
        cv.put(KEY_ORDER_DETAIL_SCHEME_PRODUCT, schemeProduct);
        cv.put(KEY_ORDER_DETAIL_TAX_1, tax1);
        cv.put(KEY_ORDER_DETAIL_TAX_2, tax2);
        cv.put(KEY_ORDER_DETAIL_TAX_3, tax3);
        cv.put(KEY_ORDER_DETAIL_SUBTOTAL, subTotal);
        /*cv.put(KEY_ORDER_DETAIL_DV1,d_v_1);
        cv.put(KEY_ORDER_DETAIL_DV2,d_v_2);
        cv.put(KEY_ORDER_DETAIL_TOV,t_o_v);
        cv.put(KEY_ORDER_DETAIL_T_TYPE,t_type);
        cv.put(KEY_ORDER_DETAIL_T_MRP_TYPE,t_mrp_type);
        cv.put(KEY_ORDER_DETAIL_T_VAL,t_val);*/

        long id = db.update(DATABASE_TABLE_ORDER_DETAILS, cv, KEY_ORDER_DETAIL_ORDER_ID + " = " + order_id + " AND " + KEY_ORDER_DETAIL_PRODUCT_ID + " = " + productId, null);

        Log.d("iUpdOrderDetail", id + "ss");

    }

    public void updateReturnDetails(int order_id, String productId, String quantity, String quantityExe, String tradePrice, String discount1, String discount2, String tradeOffer, String scheme, String schemeQty, String schemeFormula, String schemeProduct, String tax1, String tax2, String tax3, String subTotal,
                                    double t_o_v,
                                    double d_v_1,
                                    double d_v_2,
                                    double t_type,
                                    double t_mrp_type,
                                    double t_val,
                                    String mrp_price) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_RETURN_DETAIL_QUANTITY, quantity);
        cv.put(KEY_RETURN_DETAIL_QUANTITY_EXE, quantityExe);
        cv.put(KEY_RETURN_DETAIL_TRADE_PRICE, tradePrice);
        cv.put(KEY_RETURN_DETAIL_DISCOUNT_1, discount1);
        cv.put(KEY_RETURN_DETAIL_DISCOUNT_2, discount2);
        cv.put(KEY_RETURN_DETAIL_TRADE_OFFER, tradeOffer);
        cv.put(KEY_RETURN_DETAIL_SCHEME, scheme);
        cv.put(KEY_RETURN_DETAIL_SCHEME_QTY, schemeQty);
        cv.put(KEY_RETURN_DETAIL_SCHEME_FORMULA, schemeFormula);
        cv.put(KEY_RETURN_DETAIL_SCHEME_PRODUCT, schemeProduct);
        cv.put(KEY_RETURN_DETAIL_TAX_1, tax1);
        cv.put(KEY_RETURN_DETAIL_TAX_2, tax2);
        cv.put(KEY_RETURN_DETAIL_TAX_3, tax3);
        cv.put(KEY_RETURN_DETAIL_SUBTOTAL, subTotal);
        cv.put(KEY_RETURN_DETAIL_DV1, d_v_1);
        cv.put(KEY_RETURN_DETAIL_DV2, d_v_2);
        cv.put(KEY_RETURN_DETAIL_TOV, t_o_v);
        cv.put(KEY_RETURN_DETAIL_T_TYPE, t_type);
        cv.put(KEY_RETURN_DETAIL_T_MRP_TYPE, t_mrp_type);
        cv.put(KEY_RETURN_DETAIL_T_VAL, t_val);
        cv.put(KEY_RETURN_DETAIL_MRP_PRICE, mrp_price);

        long id = db.update(DATABASE_TABLE_RETURN_DETAILS, cv, KEY_RETURN_DETAIL_ORDER_ID + " = " + order_id + " AND " + KEY_RETURN_DETAIL_PRODUCT_ID + " = " + productId, null);

        Log.d("iUpdOrderDetail", id + "ss");

    }

    public void updateSalesReturnNewVales(int order_id, String newValues, String notes) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_RETURN_VALUES, newValues);
        cv.put(KEY_SALES_RETURN_NOTES, notes);
        cv.put(KEY_SALES_RETURN_UPDATE, 1);

        db.update(DATABASE_TABLE_SALESMAN_SALES_RETURN, cv, KEY_SALES_RETURN_ID + " = " + order_id, null);


    }

    public void updateSalesReturnTotal(int order_id, String newValues, String discount) {

        ContentValues cv = new ContentValues();
        //cv.put(KEY_SALES_ORDER_TOTAL, newValues);
        cv.put(KEY_SALES_RETURN_TOTAL2, newValues);
        cv.put(KEY_SALES_RETURN_DISCOUNT, discount);
        cv.put(KEY_SALES_RETURN_UPDATE, 1);

        db.update(DATABASE_TABLE_SALESMAN_SALES_RETURN, cv, KEY_SALES_RETURN_ID + " = " + order_id, null);


    }

    public void deleteSalesOrder(int order_id) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_ORDER_DELETE, 1);
        cv.put(KEY_SALES_ORDER_UPDATE, 1);

        db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

    }

    public void deleteSalesReturn(int order_id) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_RETURN_DELETE, 1);
        cv.put(KEY_SALES_RETURN_UPDATE, 1);

        db.update(DATABASE_TABLE_SALESMAN_SALES_RETURN, cv, KEY_SALES_RETURN_ID + " = " + order_id, null);

    }

    private void updateTotalExecuteSalesOrder(String order_id, String value) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_ORDER_TOTAL_EXECUTE, value);

        db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

    }

    private void updateTotal2SalesReturn(String order_return_id, String value) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_RETURN_TOTAL2, value);

        db.update(DATABASE_TABLE_SALESMAN_SALES_RETURN, cv, KEY_SALES_RETURN_ID + " = " + order_return_id, null);

    }

    public void confirmSalesOrder(int order_id) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_SALES_ORDER_CONFIRM, 1);

        db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv, KEY_SALES_ORDER_ID + " = " + order_id, null);

    }

    public void updateAutoSyncSettings(int autoSync, String TimeIn, String TimeOut, long SyncDur, long SyncDurIndex) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_AutoSync_SETTINGS, autoSync);
        cv.put(KEY_TimeIn_SETTINGS, TimeIn);
        cv.put(KEY_TimeOut_SETTINGS, TimeOut);
        cv.put(KEY_SyncDur_SETTINGS, SyncDur);
        cv.put(KEY_SyncDur_INDEX_SETTINGS, SyncDurIndex);


        db.update(DATABASE_TABLE_SETTINGS, cv, null, null);


    }

    public void updateAccessControlSettings(int routePg, int reportPg, int inventPg, int CustomerPg, int POPg, int SummaryPg, int SyncPg) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_Route_SETTINGS, routePg);
        cv.put(KEY_Reporting_SETTINGS, reportPg);
        cv.put(KEY_inventPg_SETTINGS, inventPg);
        cv.put(KEY_CustomerPg_SETTINGS, CustomerPg);
        cv.put(KEY_PlaceOrderPg_SETTINGS, POPg);
        cv.put(KEY_SummaryPg_SETTINGS, SummaryPg);
        cv.put(KEY_SyncPg_SETTINGS, SyncPg);

        db.update(DATABASE_TABLE_SETTINGS, cv, null, null);


    }

    public String getSettingsData() {


        String result = "";
        String[] Column = new String[]{KEY_SETTINGS_ID, KEY_AutoSync_SETTINGS, KEY_TimeIn_SETTINGS, KEY_TimeOut_SETTINGS, KEY_SyncDur_SETTINGS, KEY_inventPg_SETTINGS, KEY_CustomerPg_SETTINGS, KEY_PlaceOrderPg_SETTINGS, KEY_SummaryPg_SETTINGS, KEY_SyncPg_SETTINGS, KEY_Password_SETTINGS, KEY_en_Password_SETTINGS};
        Cursor c = db.query(DATABASE_TABLE_SETTINGS, Column, null, null, null, null, null);

        int iID = c.getColumnIndex(KEY_SETTINGS_ID);
        int iAS = c.getColumnIndex(KEY_AutoSync_SETTINGS);
        int iTI = c.getColumnIndex(KEY_TimeIn_SETTINGS);
        int iTO = c.getColumnIndex(KEY_TimeOut_SETTINGS);
        int iSyncD = c.getColumnIndex(KEY_SyncDur_SETTINGS);
        int iIPg = c.getColumnIndex(KEY_inventPg_SETTINGS);
        int iCPg = c.getColumnIndex(KEY_CustomerPg_SETTINGS);
        int iSumPg = c.getColumnIndex(KEY_SummaryPg_SETTINGS);
        int iSyncPg = c.getColumnIndex(KEY_SyncPg_SETTINGS);
        int iPass = c.getColumnIndex(KEY_Password_SETTINGS);
        int iEPass = c.getColumnIndex(KEY_en_Password_SETTINGS);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = "ID: " + c.getString(iID) + "\n AutoSync " + c.getString(iAS) + "\n TI " + c.getString(iTI) + "\n TO" + c.getString(iTO) + "\n SyncDur" + c.getString(iSyncD) + "\n Inv" + c.getString(iIPg) + "\n Cust" + c.getString(iCPg) + "\n Sumry" + c.getString(iSumPg) + "\n SyncPg" + c.getString(iSyncPg) + "\n Pass" + c.getString(iPass) + "\n EnPass" + c.getString(iEPass) + "\n";
        }
        c.close();
        return result;

    }

    public int CheckAutoSync() {

        int AutoSync = 0;

        String[] col = new String[]{KEY_SALESMAN_SETTINGS_TRACKING_EN};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);


        int iAsync = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_EN);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                AutoSync = c.getInt(iAsync);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return AutoSync;
    }

    public int CheckOfflineAutoSync() {

        int AutoSync = 0;

        String[] col = new String[]{KEY_SALESMAN_SETTINGS_TRACKING_OFFLINE};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);


        int iAsync = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_OFFLINE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                AutoSync = c.getInt(iAsync);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return AutoSync;
    }

    public String getTimeIn() {

        String timeIn = "";


        String[] col = new String[]{KEY_SALESMAN_SETTINGS_TRACKING_TIMEIN};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iTI = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_TIMEIN);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                timeIn = c.getString(iTI);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return timeIn;
    }

    public String getVibration() {

        String data = "";


        String[] col = new String[]{KEY_SALESMAN_SETTINGS_NOTIFY_VIBRATE};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_NOTIFY_VIBRATE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                data = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return data;
    }

    public String getSound() {

        String data = "";


        String[] col = new String[]{KEY_SALESMAN_SETTINGS_NOTIFY_SOUND};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_NOTIFY_SOUND);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                data = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return data;
    }

    public String getLights() {

        String data = "";


        String[] col = new String[]{KEY_SALESMAN_SETTINGS_NOTIFY_LIGHT};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_NOTIFY_LIGHT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                data = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return data;
    }

    public String getAlert() {

        String data = "";


        String[] col = new String[]{KEY_SALESMAN_SETTINGS_NOTIFY_ALERTBAR};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_NOTIFY_ALERTBAR);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                data = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return data;
    }

    public String getEmailNotify() {

        String data = "";


        String[] col = new String[]{KEY_SALESMAN_SETTINGS_NOTIFY_EMAIL};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iData = c.getColumnIndex(KEY_SALESMAN_SETTINGS_NOTIFY_EMAIL);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                data = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return data;
    }

    public String getTimeOut() {

        String timeOut = "";

/*
        String col[] = new String[]{KEY_TimeOut_SETTINGS};
        Cursor c = db.query(DATABASE_TABLE_SETTINGS, col, null, null, null, null, null);
*/

        String[] col = new String[]{KEY_SALESMAN_SETTINGS_TRACKING_TIMEOUT};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iTO = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_TIMEOUT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                timeOut = c.getString(iTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return timeOut;
    }

    public String getSyncDuration() {

        String SyncDur = "";

 /*       String col[] = new String[]{KEY_SyncDur_SETTINGS};
        Cursor c = db.query(DATABASE_TABLE_SETTINGS, col, null, null, null, null, null);
*/
        String[] col = new String[]{KEY_SALESMAN_SETTINGS_TRACKING_DURATION};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, col, null, null, null, null, null);

        int iSyncDur = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_DURATION);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                SyncDur = c.getString(iSyncDur);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return SyncDur;
    }

    public ArrayList<HashMap<String, String>> SettingsDataHash() {

        String[] Column = new String[]{KEY_SALESMAN_SETTINGS_ID, KEY_SALESMAN_SETTINGS_EID, KEY_SALESMAN_SETTINGS_ADMIN_EMAIL, KEY_SALESMAN_SETTINGS_ADMIN_PHONE,

                KEY_SALESMAN_SETTINGS_TRACKING_EN, KEY_SALESMAN_SETTINGS_TRACKING_TIMEIN, KEY_SALESMAN_SETTINGS_TRACKING_TIMEOUT, KEY_SALESMAN_SETTINGS_TRACKING_DURATION, KEY_SALESMAN_SETTINGS_TRACKING_OFFLINE,

                KEY_SALESMAN_SETTINGS_NOTIFY_ALERTBAR, KEY_SALESMAN_SETTINGS_NOTIFY_VIBRATE, KEY_SALESMAN_SETTINGS_NOTIFY_SOUND, KEY_SALESMAN_SETTINGS_NOTIFY_LIGHT, KEY_SALESMAN_SETTINGS_ADMIN_EMAIL, KEY_SALESMAN_SETTINGS_NOTIFY_PHONE_START,

                KEY_SALESMAN_SETTINGS_ACCESS_ROUTE, KEY_SALESMAN_SETTINGS_ACCESS_REPORT, KEY_SALESMAN_SETTINGS_ACCESS_CUSTOMER, KEY_SALESMAN_SETTINGS_ACCESS_SYNC, KEY_SALESMAN_SETTINGS_ACCESS_ORDER, KEY_SALESMAN_SETTINGS_ACCESS_INVENTORY, KEY_SALESMAN_SETTINGS_ACCESS_LOGOUT};


        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, Column, null, null, null, null, null);

        /*int iTiming = c.getColumnIndex(KEY_Timing_SETTINGS);*/
        /*int iTimeIn = c.getColumnIndex(KEY_CLockIn_SETTINGS);*/
        /*int iClockIn = c.getColumnIndex(KEY_CLockIn_SETTINGS);*/
        int iaSync = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_EN);
        int iTI = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_TIMEIN);
        int iTO = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_TIMEOUT);
        int iSyncDur = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_DURATION);

        /*int iSyncDurIndex = c.getColumnIndex(KEY_SyncDur_INDEX_SETTINGS);*/
        /*int iInv = c.getColumnIndex(KEY_inventPg_SETTINGS);*/

        int iroute = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_ROUTE);
        int ireport = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_REPORT);
        int iCust = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_CUSTOMER);
        int iSync = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_SYNC);

        /*int iPO = c.getColumnIndex(KEY_PlaceOrderPg_SETTINGS);
        int iSumry = c.getColumnIndex(KEY_SummaryPg_SETTINGS);*/

        int iOrder = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_ORDER);
        int iInvent = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_INVENTORY);
        int iLogout = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ACCESS_LOGOUT);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();


        if (c.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();

                /*
                map.put("timing", c.getString(iTiming));
                map.put("timein", c.getString(iTimeIn));
*/
                map.put("route", c.getString(iroute));
                map.put("reporting", c.getString(ireport));
/*
                map.put("clockin", c.getString(iClockIn));
*/
                map.put("async", c.getString(iaSync));
                map.put("ti", c.getString(iTI));
                map.put("to", c.getString(iTO));
                map.put("syncdur", c.getString(iSyncDur));

                /*
                map.put("syncdurindex", c.getString(iSyncDurIndex));
                map.put("inv", c.getString(iInv));
*/
                map.put("cust", c.getString(iCust));
/*
                map.put("po", c.getString(iPO));
                map.put("sumry", c.getString(iSumry));
*/

                map.put("syncpg", c.getString(iSync));
                map.put("order", c.getString(iOrder));
                map.put("inv", c.getString(iInvent));
                map.put("logout", c.getString(iLogout));

                //Log.d("Hashid",c.getString(iID));

                result.add(map);
            } while (c.moveToNext());

        }
        c.close();

        return result;

    }

    public void updateSettingsPhone(String PhoneSet) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_AdminPhone_SETTINGS, PhoneSet);

        db.update(DATABASE_TABLE_SETTINGS, cv, null, null);


    }

    public String getSettingsAdminEmail() {

        String result = "";

        String[] Column = new String[]{KEY_SALESMAN_SETTINGS_ADMIN_EMAIL};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, Column, null, null, null, null, null);

        int iEmail = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ADMIN_EMAIL);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getSettingsAdminPhone() {

        String result = "";

/*
        String Column[] = new String[]{KEY_AdminPhone_SETTINGS};
        Cursor c = db.query(DATABASE_TABLE_SETTINGS, Column, null, null, null, null, null);
*/

        String[] Column = new String[]{KEY_SALESMAN_SETTINGS_ADMIN_PHONE};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, Column, null, null, null, null, null);

        int iPhone = c.getColumnIndex(KEY_SALESMAN_SETTINGS_ADMIN_PHONE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iPhone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    public String getSettingsAutoSync() {

        String result = "";

        String[] Column = new String[]{KEY_SALESMAN_SETTINGS_TRACKING_EN};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SETTINGS, Column, null, null, null, null, null);

        int iPhone = c.getColumnIndex(KEY_SALESMAN_SETTINGS_TRACKING_EN);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iPhone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }

    //CLOCKIN
    public long CreateClockIn(int empid) {


        ContentValues cv = new ContentValues();

        cv.put(KEY_empId_CLOCKIN, empid);
        cv.put(KEY_TOTALTime_CLOCKIN, 0);
        cv.put(KEY_clockInTime_CLOCKIN, "0000-00-00 00:00:00");
        cv.put(KEY_status_CLOCKIN, 0);
        cv.put(KEY_clockInTime_PREV, 0);
        cv.put(KEY_clockInTime_NEXT, 0);

        long insertClockIn = db.insert(DATABASE_TABLE_CLOCKIN, null, cv);
        return insertClockIn;

    }

    public int getMaxOrderIdFromSaleOrder() {

        String que = "SELECT MAX(" + KEY_SALES_ORDER_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER;

        Cursor c = db.rawQuery(que, null);

        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);

        if (!c.isClosed())
            c.close();

        return maxid;

    }

    /*public String getMaxOrderID() {

        String result = "";

        String Column[] = new String[]{KEY_SALES_ORDER_ID};
        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SALES_ORDER, Column, null, null, null, null, null);

        int iPhone = c.getColumnIndex(KEY_SALES_ORDER_ID);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = c.getString(iPhone);
        }

        return result;
    }*/

    public int getMaxOrderIdFromSaleReturn() {

        String que = "SELECT MAX(" + KEY_SALES_RETURN_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN;

        Cursor c = db.rawQuery(que, null);

        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);

        if (!c.isClosed())
            c.close();
        return maxid;

    }

    public int getMaxIdFromPaymentRecieved() {

        String que = "SELECT MAX(" + KEY_PAYMENT_RECIEVED_ID + ") FROM " + DATABASE_TABLE_PAYMENT_RECIEVED;

        Cursor c = db.rawQuery(que, null);

        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);

        if (!c.isClosed())
            c.close();
        return maxid;

    }

    public String getPreviousCheckInCustomer() {
        String result = "";

        String[] Column = new String[]{KEY_clockInTime_PREV};
        Cursor c = db.query(DATABASE_TABLE_CLOCKIN, Column, KEY_empId_CLOCKIN + " = " + getMobEmpId(), null, null, null, null);

        int iId = c.getColumnIndex(KEY_clockInTime_PREV);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = c.getString(iId);
        }
        c.close();
        return result;

    }

    public String getNextCheckInCustomer() {
        String result = "";

        String[] Column = new String[]{KEY_clockInTime_NEXT};
        Cursor c = db.query(DATABASE_TABLE_CLOCKIN, Column, KEY_empId_CLOCKIN + " = " + getMobEmpId(), null, null, null, null);

        int iId = c.getColumnIndex(KEY_clockInTime_NEXT);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = c.getString(iId);
        }
        c.close();
        return result;

    }

    private String GetCurrentDateValues() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        String SelectedDay = "";


        switch (day) {

            case Calendar.MONDAY: {

                SelectedDay = "Monday";
            }
            break;

            case Calendar.TUESDAY: {
                SelectedDay = "Tuesday";
            }
            break;

            case Calendar.WEDNESDAY: {
                SelectedDay = "Wednesday";
            }
            break;

            case Calendar.THURSDAY: {
                SelectedDay = "Thursday";
            }
            break;

            case Calendar.FRIDAY: {
                SelectedDay = "Friday";
            }
            break;

            case Calendar.SATURDAY: {
                SelectedDay = "Saturday";
            }
            break;

            case Calendar.SUNDAY: {
                SelectedDay = "Sunday";
            }
            break;


        }

        return SelectedDay;
    }

    public String getCustomerAREAFromPREV(String cust_id) {
        String result = "";


        String[] Column = new String[]{KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_CUSTOMER_MAPNAME};

        Cursor c;
        if (cust_id.equalsIgnoreCase("")) {
            cust_id = "0";
            c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + cust_id, null, null, null, null);

        } else {
            c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + cust_id, null, null, null, null);
        }

        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);
        int iMap = c.getColumnIndex(KEY_CUSTOMER_MAPNAME);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String Name = c.getString(iFn) + " " + c.getString(iLn);
            String Area = c.getString(iMap);
            result = Name + "\n" + Area;
        }
        c.close();

        return result;

    }

    public String getCustomerAREAFromNEXT(String cust_id) {
        String result = "";


        String[] Column = new String[]{KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_CUSTOMER_MAPNAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + cust_id, null, null, null, null);


        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);
        int iMap = c.getColumnIndex(KEY_CUSTOMER_MAPNAME);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            String Name = c.getString(iFn) + " " + c.getString(iLn);
            String Area = c.getString(iMap);
            result = Name + "\n" + Area;
        }
        c.close();
        return result;

    }

    // NAVIGATION Stuffs Starts

    public int CheckEMPIdClockIn(int empid) {


/*
        String Column[] = new String[]{KEY_empId_CLOCKIN, KEY_status_CLOCKIN};
        Cursor c = db.query(DATABASE_TABLE_CLOCKIN, Column, KEY_empId_CLOCKIN+" = "+empid, null, null, null, null);
*/
        String que = "Select Count(+" + KEY_id_CLOCKIN + ") FROM " + DATABASE_TABLE_CLOCKIN + " WHERE " + KEY_empId_CLOCKIN + " = " + empid;

        Cursor c = db.rawQuery(que, null);


        int count = 0;
        if (null != c)
            if (c.getCount() > 0) {
                c.moveToFirst();
                count = c.getInt(0);
                c.close();
            }

        return count;

    }

    public List<String> GetCustomerWithAreaNamesDROPDOWN(double Newlat, double Newlongi) {


        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ID_CUSTOMER, KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_CUSTOMER_LATTITUDE, KEY_CUSTOMER_LONGITUDE, KEY_CUSTOMER_RADIUS};

        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_CUSTOMER_LONGITUDE + " != ''", null, null, null, null);

        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);
        int iLat = c.getColumnIndex(KEY_CUSTOMER_LATTITUDE);
        int iLong = c.getColumnIndex(KEY_CUSTOMER_LONGITUDE);
        int iRad = c.getColumnIndex(KEY_CUSTOMER_RADIUS);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {


            double OldLat = Double.parseDouble(c.getString(iLat));
            double OldLong = Double.parseDouble(c.getString(iLong));
            double CustRad = Double.parseDouble(c.getString(iRad));

            double distance = getDistanceFromLatLonInMeter(OldLat, OldLong, Newlat, Newlongi);

            if (distance <= CustRad) {
                ItemNames.add(c.getString(iFn) + " " + c.getString(iLn));
            }

        }
        c.close();
        return ItemNames;
    }

    public List<String> GetCustomerIDWithAreaNamesDROPDOWN(double Newlat, double Newlongi) {
        // TODO Auto-generated method stub
        List<String> ItemNames = new ArrayList<>();

        String[] Column = new String[]{KEY_ID_CUSTOMER, KEY_CUSTOMER_FNAME, KEY_CUSTOMER_LNAME, KEY_CUSTOMER_LATTITUDE, KEY_CUSTOMER_LONGITUDE, KEY_CUSTOMER_RADIUS};

        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_CUSTOMER_LONGITUDE + " != ''", null, null, null, null);

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        int iLat = c.getColumnIndex(KEY_CUSTOMER_LATTITUDE);
        int iLong = c.getColumnIndex(KEY_CUSTOMER_LONGITUDE);
        int iRad = c.getColumnIndex(KEY_CUSTOMER_RADIUS);

        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {


            double OldLat = Double.parseDouble(c.getString(iLat));
            double OldLong = Double.parseDouble(c.getString(iLong));
            double CustRad = Double.parseDouble(c.getString(iRad));

            double distance = getDistanceFromLatLonInMeter(OldLat, OldLong, Newlat, Newlongi);

            if (distance <= CustRad) {
                ItemNames.add(c.getString(iId));
            }

        }
        c.close();
        return ItemNames;
    }


         /*
        DISTANCE CALCULATION BETWEEN 2 GEO POINTS
         */

    private double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }


    // NAVIGATION Stuffs Ends

    private double getDistanceFromLatLonInMeter(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = (R * c) * 1000; // Distance in meter
        return d;
    }

    public ArrayList<HashMap<String, String>> getRoutesWhereDay(String Day) {

        //String selectQuery = "SELECT c." + KEY_CUSTOMER_FNAME + ", c." + KEY_CUSTOMER_LNAME + ", c." + KEY_CUSTOMER_MAPNAME + ", r." + KEY_ROUTE_seq + " ,r." + KEY_ROUTE_visit_month + ",r."+KEY_ROUTE_done+",r."+KEY_ROUTE_ID+" FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c, " + Database_Table_Route + " r WHERE r." + KEY_ROUTE_cid + " = c." + KEY_ID_CUSTOMER + " AND r." + KEY_ROUTE_day + " = '" + Day + "' AND r."+KEY_ROUTE_date+" >= "+getFormattedDateFromTimestamp( getCurrentTime() )+" ORDER BY " + KEY_ROUTE_seq;
        String selectQuery = "SELECT c." + KEY_CUSTOMER_FNAME + ", c." + KEY_CUSTOMER_LNAME + ", c." + KEY_CUSTOMER_MAPNAME + ", r." + KEY_ROUTE_seq + " ,r." + KEY_ROUTE_visit_month + ",r." + KEY_ROUTE_ID + " FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c, " + Database_Table_Route + " r WHERE r." + KEY_ROUTE_cid + " = c." + KEY_ID_CUSTOMER + " AND r." + KEY_ROUTE_day + " = '" + Day + "' ORDER BY " + KEY_ROUTE_seq;

        ArrayList<HashMap<String, String>> result = new ArrayList<>();


        Cursor c = db.rawQuery(selectQuery, null);


        int iFn = c.getColumnIndex(KEY_CUSTOMER_FNAME);
        int iLn = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        int iMap = c.getColumnIndex(KEY_CUSTOMER_MAPNAME);

        int iVisit = c.getColumnIndex(KEY_ROUTE_visit_month);
        int iRouteID = c.getColumnIndex(KEY_ROUTE_ID);


        // c.moveToFirst();
        if (c.moveToFirst()) {

            do {
                //for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                HashMap<String, String> map = new HashMap<>();

                map.put(Name, c.getString(iFn) + " " + c.getString(iLn));

                String Area = c.getString(iMap);

                map.put(AreaName, Area);

               /* int i=0;
                try {
                    if (!(Area.isEmpty() || !(Area.equalsIgnoreCase("null")) || !( Area.equals(null) ) )) {
                        map.put(AreaName, c.getString(iMap));


                    }
                }
                catch (Exception e){
                    Log.e("Error AREA NAME",e.toString()+"\n");
                    e.printStackTrace();
//                    if (Area.isEmpty() || Area.equalsIgnoreCase("null") || Area.equals(null) ) {
                        map.put(AreaName, "None");


                    Toast.makeText(AppContext,"AreaName in Catch: "+Area+"\n"+i++,Toast.LENGTH_LONG).show();

  //                  }

                }
*/

                map.put(VisitsDone, GetCountForTodayRoute(c.getString(iRouteID)));
                map.put(Visits, c.getString(iVisit));
                map.put(RouteID, c.getString(iRouteID));

                result.add(map);
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

    public ArrayList<HashMap<String, String>> getSalesOrderForList() {

        selectQueryInside =
                "SELECT c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME +
                        " as ln,c.id as cust_id, c." + KEY_CUSTOMER_TYPE + " as type, so." +
                        KEY_SALES_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_CONFIRM + " as conf, so." + KEY_SALES_ORDER_DATETIME +
                        " as dt, so." + KEY_SALES_ORDER_TOTAL2 + " as amt, so." + KEY_SALES_ORDER_AMOUNT_RECIEVED +
                        " as amt_rec, so." + KEY_SALES_ORDER_EXECUTE_COMPLETE + " as exe_comp, c." + KEY_CUSTOMER_SEARCH +
                        " as data_search FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so, " + DATABASE_TABLE_CUSTOMER_DETAILS +
                        " c WHERE c." + KEY_ID_CUSTOMER + " = so." + KEY_SALES_ORDER_CUSTOMER_ID + " AND so." + KEY_SALES_ORDER_DELETE +
                        " = 0 ORDER BY " + KEY_SALES_ORDER_DATETIME + " DESC";

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        Log.e("GetSalesOrderQuery", selectQueryInside);

        Cursor c = db.rawQuery(selectQueryInside, null);

        int iFn = c.getColumnIndex("fn");
        int iLn = c.getColumnIndex("ln");
        int custID = c.getColumnIndex("cust_id");
        int iID = c.getColumnIndex("oid");
        int iConf = c.getColumnIndex("conf");
        int iDate = c.getColumnIndex("dt");
        int iAmt = c.getColumnIndex("amt");
        int iAmtRec = c.getColumnIndex("amt_rec");
        int iExeComp = c.getColumnIndex("exe_comp");
        int iDataSearch = c.getColumnIndex("data_search");
        int iType = c.getColumnIndex("type");


        try {
            if (c.moveToFirst()) {

                do {

                    HashMap<String, String> map = new HashMap<>();

                    //map.put(ORDER_CUST_COLUMN, c.getString(iFn) + " " + c.getString(iLn));
                    map.put(ORDER_CUST_COLUMN, c.getString(iDataSearch));
                    map.put(ORDER_ID_COLUMN, c.getString(iID));
                    map.put(ORDER_CONFIRM_COLUMN, c.getString(iConf));
                    map.put(ORDER_DATE_COLUMN, c.getString(iDate));
                    // Below Line Uncommented By Umais
                    // map.put(ORDER_AMOUNT_COLUMN, c.getString(iAmt) );
                    // map.put(ORDER_NEW_AMOUNT_COLUMN, String.valueOf( getTotal2EditText( c.getString(iID) ) ) );
                    map.put(ORDER_NEW_AMOUNT_COLUMN, c.getString(iAmt));
                    map.put(ORDER_EXE_COMPLETE, c.getString(iExeComp));
                    map.put("amount_rec", c.getString(iAmtRec));
                    map.put("cust_type", c.getString(iType));
                    map.put("cust_id", c.getString(custID));

                    result.add(map);


                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return result;
    }
    public int getSalesOrderTotalPercent() {

        int result = 0;

        String query = " SELECT " + KEY_SALES_ORDER_ID + " FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 1";

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_ORDER_ID);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//                result += c.getString(iData);
                result += getSalesOrderDetailsQtyTotal(c.getString(iData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }
    public int getSalesOrderTotal() {

        int result = 0;

        String query = " SELECT " + KEY_SALES_ORDER_TOTAL2 + " FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE so." + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 1";

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_SALES_ORDER_TOTAL2);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result += c.getInt(iData);
//                result += getSalesOrderDetailsQtyTotal(c.getString(iData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }
    public int getSalesOrderDetailsQtyTotal(String OrderId) {

        int result = 0;

        String query = " SELECT " + KEY_ORDER_DETAIL_QUANTITY_EXE + " FROM " + DATABASE_TABLE_ORDER_DETAILS + " so WHERE so." + KEY_ORDER_DETAIL_ORDER_ID + " = "+OrderId;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_ORDER_DETAIL_QUANTITY_EXE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//                result += c.getString(iData);
                result += c.getInt(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }
    public int getTargetsTotal() {

        int result = 0;

        String query = " SELECT " + KEY_ITEM_TARGET_TARGET + " FROM " + DATABASE_TABLE_ITEM_TARGET ;

        Cursor c = db.rawQuery(query, null);

        int iData = c.getColumnIndex(KEY_ITEM_TARGET_TARGET);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result += c.getInt(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }
    public ArrayList<HashMap<String, String>> getSalesOrderForListFOREXECUTE(String dateTime) {

        String selectQuery = "SELECT c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_CUSTOMER_LNAME + " as ln, c." + KEY_CUSTOMER_TYPE + " as type, so." + KEY_SALES_ORDER_ID + " as oid, so." + KEY_SALES_ORDER_CONFIRM + " as conf, so." + KEY_SALES_ORDER_DATETIME + " as dt, so." + KEY_SALES_ORDER_TOTAL2 + " as amt, c." + KEY_CUSTOMER_SEARCH + " as data_search FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so, " + DATABASE_TABLE_CUSTOMER_DETAILS + " c WHERE c." + KEY_ID_CUSTOMER + " = so." + KEY_SALES_ORDER_CUSTOMER_ID + " AND so." + KEY_SALES_ORDER_DELETE + " = 0 AND " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " = 0 AND " + KEY_SALES_ORDER_DATETIME + " LIKE '" + dateTime + "%' ";

        ArrayList<HashMap<String, String>> result = new ArrayList<>();


        Cursor c = db.rawQuery(selectQuery, null);


        int iFn = c.getColumnIndex("fn");
        int iLn = c.getColumnIndex("ln");
        int iDataSearch = c.getColumnIndex("data_search");
        int iID = c.getColumnIndex("oid");
        int iConf = c.getColumnIndex("conf");
        int iDate = c.getColumnIndex("dt");
        int iAmt = c.getColumnIndex("amt");
        int iType = c.getColumnIndex("type");


        try {
            if (c.moveToFirst()) {

                do {

                    HashMap<String, String> map = new HashMap<>();

                    //map.put(ORDER_CUST_COLUMN, c.getString(iFn) + " " + c.getString(iLn));
                    map.put(ORDER_CUST_COLUMN, c.getString(iDataSearch));
                    map.put(ORDER_ID_COLUMN, c.getString(iID));
                    map.put(ORDER_CONFIRM_COLUMN, c.getString(iConf));
                    map.put(ORDER_DATE_COLUMN, c.getString(iDate));
                    /*map.put(ORDER_AMOUNT_COLUMN, c.getString(iAmt) );*/
                    //map.put(ORDER_NEW_AMOUNT_COLUMN, String.valueOf( getTotalExecuteEditText( c.getString(iID) ) ) );
                    map.put(ORDER_NEW_AMOUNT_COLUMN, c.getString(iAmt));
                    map.put("cust_type", c.getString(iType));


                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return result;
    }

    public ArrayList<HashMap<String, String>> getSalesReturnForList() {

        String selectQuery = "SELECT c.id as cust_id,c." + KEY_CUSTOMER_FNAME + " as fn, c." +
                KEY_CUSTOMER_LNAME + " as ln, c." + KEY_CUSTOMER_TYPE + " as type, sr." +
                KEY_SALES_RETURN_ID + " as rid, sr." + KEY_SALES_RETURN_CONFIRM +
                " as conf, sr." + KEY_SALES_RETURN_DATE + " as dt, sr." + KEY_SALES_RETURN_TOTAL2 +
                " as amt, sr." + KEY_SALES_RETURN_EXECUTE_COMPLETE + " as sr_exe_comp, c." +
                KEY_CUSTOMER_SEARCH + " as data_search FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN +
                " sr, " + DATABASE_TABLE_CUSTOMER_DETAILS + " c WHERE c." + KEY_ID_CUSTOMER +
                " = sr." + KEY_SALES_RETURN_CUSTOMER_ID + " AND sr." + KEY_SALES_RETURN_DELETE +
                " = 0 ORDER BY " + KEY_SALES_RETURN_DATE + " DESC";

        ArrayList<HashMap<String, String>> result = new ArrayList<>();


        Log.e("GetSalesReturn", selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        int iCustId = c.getColumnIndex("cust_id");
        int iFn = c.getColumnIndex("fn");
        int iLn = c.getColumnIndex("ln");
        int iDataSearch = c.getColumnIndex("data_search");
        int iID = c.getColumnIndex("rid");
        int iConf = c.getColumnIndex("conf");
        int iDate = c.getColumnIndex("dt");
        int iAmt = c.getColumnIndex("amt");
        int iType = c.getColumnIndex("type");
        int iExeComplete = c.getColumnIndex("sr_exe_comp");

        try {
            if (c.moveToFirst()) {

                do {

                    HashMap<String, String> map = new HashMap<>();

                    //map.put(ORDER_CUST_COLUMN, c.getString(iFn) + " " + c.getString(iLn));
                    map.put(ORDER_CUST_COLUMN, c.getString(iDataSearch));
                    map.put(ORDER_ID_COLUMN, c.getString(iID));
                    map.put(ORDER_CONFIRM_COLUMN, c.getString(iConf));
                    map.put(ORDER_DATE_COLUMN, c.getString(iDate));
                    /*map.put(ORDER_AMOUNT_COLUMN, c.getString(iAmt) );*/
                    //   map.put(ORDER_NEW_AMOUNT_COLUMN, String.valueOf( getTotal2EditTextSALESRETURN( c.getString(iID) ) ) );
                    map.put(ORDER_NEW_AMOUNT_COLUMN, c.getString(iAmt));
                    map.put(ORDER_EXE_COMPLETE, c.getString(iExeComplete));
                    map.put("cust_type", c.getString(iType));
                    map.put("cust_id", c.getString(iCustId));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return result;
    }

    private String GetCountForTodayRoute(String route_id) {

        String countQuery = "Select * From " + Database_Table_Today_Route + " WHERE  " + KEY_TodayROUTE_ID + " = " + route_id;

        //db.execSQL(deleteQuery);

        Cursor c = db.rawQuery(countQuery, null);

        /*int iCount = c.getColumnIndex("cou");


        String count = String.valueOf( iCount );
*/
        String count = String.valueOf(c.getCount());

        Log.d("update_count", count + "d");
        c.close();
        return count;

    }

    public void DeleteYesterdayOldRoutes() {
        // TODO Auto-generated method stub


        String deleteQuery = "Delete From " + Database_Table_Today_Route + " WHERE  " + KEY_TodayROUTE_DateTime + " < " + getFormattedDateFromTimestamp(getCurrentTime());

        //db.execSQL(deleteQuery);

        Cursor c = db.rawQuery(deleteQuery, null);

/*
        String result = "";

        int iBrandName = c.getColumnIndex(KEY_TodayROUTE_DateTime);

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()){
            result =result +c.getString(iBrandName)+"\n";
        }

        Log.d("YstrdayQuery", deleteQuery+"\n "+c.getCount()+"\n"+result );
*/
        Log.d("YstrdayQuery", deleteQuery + "\n " + c.getCount() + "\n");
        c.close();

    }

    public long createPhoneEntry(String time) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_PHONESTART_TIME, time);
        cv.put(KEY_PHONESTART_SYNC, 0);

        long insertedId = db.insert(Database_Table_PHONESTART, null, cv);

//        long insertId = db.insert(DATABASE_TABLE_INVENTORY, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertedId;

    }

    public long createCHKNETEntry(String time, int status) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_CHKNET_TIME, time);
        cv.put(KEY_CHKNET_STATUS, status);
        cv.put(KEY_CHKNET_SYNC, 0);

        long insertedId = db.insert(Database_Table_CHKNET, null, cv);

//        long insertId = db.insert(DATABASE_TABLE_INVENTORY, null, cv); //Gives Last Autoincremented Inserted value ID

        return insertedId;

    }

    public ArrayList<HashMap<String, String>> getUnsyncCHKNET() {
        ArrayList<HashMap<String, String>> ChkNetDetails;

        ChkNetDetails = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Database_Table_CHKNET/* + " WHERE " + KEY_CHKNET_SYNC + " = 0"*/;
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_CHKNET);
        int iTime = cursor.getColumnIndex(KEY_CHKNET_TIME);
        int iStatus = cursor.getColumnIndex(KEY_CHKNET_STATUS);

        try {


            if (cursor.moveToFirst()) {
                do {

                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("time", cursor.getString(iTime));
                    map.put("status", cursor.getString(iStatus));

                    ChkNetDetails.add(map);

                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (NullPointerException unsyncCHKNET) {
            unsyncCHKNET.getMessage();
        }
        return ChkNetDetails;
    }

    public ArrayList<HashMap<String, String>> getUnsentTracking() {
        ArrayList<HashMap<String, String>> OfflineTrack;

        OfflineTrack = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_OFFLINE_TRACKING;
        Cursor cursor = db.rawQuery(selectQuery, null);


        int iLat = cursor.getColumnIndex(KEY_OFFLINE_LATTITUDE);
        int iLong = cursor.getColumnIndex(KEY_OFFLINE_LONGITUDE);
        int iDate = cursor.getColumnIndex(KEY_OFFLINE_DATETIME);
        int iArea = cursor.getColumnIndex(KEY_OFFLINE_AREA);

        try {


            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("lati", cursor.getString(iLat));
                    map.put("longi", cursor.getString(iLong));
                    map.put("date", cursor.getString(iDate));
                    map.put("area", cursor.getString(iArea));

                    OfflineTrack.add(map);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (NullPointerException e) {
            e.getMessage();
        }
        return OfflineTrack;
    }

    public ArrayList<HashMap<String, String>> getReturnReasonFromDB() {
        ArrayList<HashMap<String, String>> returnReasonList;

        returnReasonList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Database_Table_Return_Reason;
        Cursor cursor = db.rawQuery(selectQuery, null);


        int id = cursor.getColumnIndex(KEY_RETURN_REASON_ID);
        int name = cursor.getColumnIndex(KEY_RETURN_REASON_NAME);
        //int iDate = cursor.getColumnIndex(KEY_OFFLINE_DATETIME);
        // int iArea = cursor.getColumnIndex(KEY_OFFLINE_AREA);

        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("iid", cursor.getString(id));
                    map.put("iname", cursor.getString(name));

                    returnReasonList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return returnReasonList;

    }

    public void updateRouteVisitDone(String id, int done) {

        switch (done) {

            case 0: {

                String deleteQuery = "DELETE FROM " + Database_Table_Today_Route + " WHERE " + KEY_TodayROUTE_ID + " = " + id;

                db.execSQL(deleteQuery);
                Log.d("update_route", deleteQuery);

            }
            break;
            case 1: {

                ContentValues cv = new ContentValues();

                cv.put(KEY_TodayROUTE_ID, id);
                cv.put(KEY_TodayROUTE_DateTime, getFormattedDateFromTimestamp(getCurrentTime()));

                long insertedId = db.insert(Database_Table_Today_Route, null, cv);

                Log.d("update_route", insertedId + "chk");
            }
            break;

        }

    }

    public void DeleteCHKNET() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + Database_Table_CHKNET;

        db.execSQL(deleteQuery);
        Log.d("query", deleteQuery);

    }

    public ArrayList<HashMap<String, String>> getUnsyncPHONESTART() {
        ArrayList<HashMap<String, String>> PhoneStartDetails;

        PhoneStartDetails = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Database_Table_PHONESTART/* + " WHERE " + KEY_PHONESTART_SYNC + " = 0"*/;
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_ID_PHONESTART);
        int iTime = cursor.getColumnIndex(KEY_PHONESTART_TIME);

        try {


            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("time", cursor.getString(iTime));

                    PhoneStartDetails.add(map);
                } while (cursor.moveToNext());
            }

        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
        return PhoneStartDetails;
    }

    public void DeletePHONESTART() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + Database_Table_PHONESTART;

        db.execSQL(deleteQuery);
        Log.d("query", deleteQuery);

    }

    public void DeleteSyncOfflineTracking() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_OFFLINE_TRACKING;

        db.execSQL(deleteQuery);
        Log.d("query", deleteQuery);

    }

    // Read records related to the search term
    public List<MyObject> GetBrandAutoCompleteData(String searchTerm) {

        List<MyObject> recordsList = new ArrayList<>();

        // select query
        String sql = "";
        sql += "SELECT " + KEY_BRAND_NAME + " FROM " + DATABASE_TABLE_BRAND;
        sql += " WHERE " + KEY_BRAND_NAME + " LIKE '%" + searchTerm + "%' ";
        //sql += " OR " + KEY_SKU + " LIKE '%" + searchTerm + "%' ) ";
        sql += " ORDER BY " + KEY_ID_BRAND + " ASC";
        //sql += " LIMIT 0,5";

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                // String objectName = cursor.getString(cursor.getColumnIndex(KEY_SKU))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_INVENT_NAME))+" ) ";
                String objectName = cursor.getString(cursor.getColumnIndex(KEY_BRAND_NAME));
                MyObject myObject = new MyObject(objectName);

                // add to editOrderList
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        // Umais commented below 2 lines
        cursor.close();

        // return the editOrderList of records
        return recordsList;
    }

    // Read records related to the search term
    public List<MyObject> GetInventoryAutoCompleteData(String searchTerm) {

        List<MyObject> recordsList = new ArrayList<>();

        // select query
        String sql = "";
        if (searchTerm.equals("")) {

            sql = "SELECT " + KEY_SKU + ",id , " + KEY_INVENT_NAME + ", " + KEY_INVENTORY_SEARCH + " FROM " + DATABASE_TABLE_INVENTORY
                    + " ORDER BY " + KEY_ID_INVENT + " ASC";
        }else{
            sql = "SELECT " + KEY_SKU + ", id ," + KEY_INVENT_NAME + ", " + KEY_INVENTORY_SEARCH + " FROM " + DATABASE_TABLE_INVENTORY
                    + " WHERE " + KEY_INVENTORY_SEARCH + " LIKE '%" + searchTerm + "%' "
            //sql += " OR " + KEY_SKU + " LIKE '%" + searchTerm + "%' ) ";
            + " ORDER BY " + KEY_ID_INVENT + " ASC";
        }
        Log.d("TAG", "GetInventoryAutoCompleteData: "+sql);

        //sql += " LIMIT 0,5";

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                // String objectName = cursor.getString(cursor.getColumnIndex(KEY_SKU))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_INVENT_NAME))+" ) ";
                String objectName = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_SEARCH));
                String objectId = cursor.getString(cursor.getColumnIndex("id"));
                MyObject myObject = new MyObject(objectName,objectId);

                // add to editOrderList
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        // Umais commented below 2 lines
        cursor.close();


        // return the editOrderList of records
        return recordsList;
    }

    public List<MyObject> GetInventoryAutoCompleteData(String searchTerm, String brand_id) {

        List<MyObject> recordsList = new ArrayList<>();

        // select query
        String sql = "";
        if (searchTerm.equals("")) {

            sql += "SELECT " + KEY_SKU + ", id, " + KEY_INVENT_NAME + ", " + KEY_INVENTORY_SEARCH + " FROM " + DATABASE_TABLE_INVENTORY;
            sql += " WHERE " + KEY_BRAND_ID_FROM_INVENT + " = " + brand_id;
            //sql += " OR " + KEY_SKU + " LIKE '%" + searchTerm + "%' ) ";
            sql += " ORDER BY " + KEY_ID_INVENT + " ASC";

        }else{

            sql += "SELECT " + KEY_SKU + ",  id, " + KEY_INVENT_NAME + ", " + KEY_INVENTORY_SEARCH + " FROM " + DATABASE_TABLE_INVENTORY;
            sql += " WHERE " + KEY_INVENTORY_SEARCH + " LIKE '%" + searchTerm + "%'  AND " + KEY_BRAND_ID_FROM_INVENT + " = " + brand_id;
            //sql += " OR " + KEY_SKU + " LIKE '%" + searchTerm + "%' ) ";
            sql += " ORDER BY " + KEY_ID_INVENT + " ASC";
            //sql += " LIMIT 0,5";
        }

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                // String objectName = cursor.getString(cursor.getColumnIndex(KEY_SKU))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_INVENT_NAME))+" ) ";
                String objectName = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_SEARCH));
                String objectId = cursor.getString(cursor.getColumnIndex("id"));
                MyObject myObject = new MyObject(objectName,objectId);

                // add to editOrderList
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        // Umais commented below 2 lines
        cursor.close();


        // return the editOrderList of records
        return recordsList;
    }

    public ArrayList<ProductModel> GetInventoryData(String brand_id){
        ArrayList<ProductModel> recordsList = new ArrayList<>();
        String sql="";
        sql += "SELECT " + KEY_SKU + ", id, " + KEY_INVENT_NAME + ", " +KEY_SALE_PRICE+ ", " + KEY_INVENTORY_SEARCH + " FROM " + DATABASE_TABLE_INVENTORY;
        sql += " WHERE " + KEY_BRAND_ID_FROM_INVENT + " = " + brand_id+" ORDER BY " + KEY_ID_INVENT + " ASC";
//        sql += ;
        //sql += " OR " + KEY_SKU + " LIKE '%" + searchTerm + "%' ) ";

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                // String objectName = cursor.getString(cursor.getColumnIndex(KEY_SKU))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_INVENT_NAME))+" ) ";
                String objectName = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_SEARCH));
                String objectPrice = cursor.getString(cursor.getColumnIndex(KEY_SALE_PRICE));
                String objectId = cursor.getString(cursor.getColumnIndex("id"));
                ProductModel myObject = new ProductModel(objectId,objectName,objectPrice,brand_id);

                // add to editOrderList
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        // Umais commented below 2 lines
        cursor.close();


        // return the editOrderList of records
        return recordsList;
    }
    public List<MyObject> GetInventoryAutoCompleteData( String brand_id,boolean chk) {

        List<MyObject> recordsList = new ArrayList<>();

        // select query
        String sql = "";
        sql += "SELECT " + KEY_SKU + ", id, " + KEY_INVENT_NAME + ", " + KEY_INVENTORY_SEARCH + " FROM " + DATABASE_TABLE_INVENTORY;
        sql += " WHERE " + KEY_BRAND_ID_FROM_INVENT + " = " + brand_id;
        //sql += " OR " + KEY_SKU + " LIKE '%" + searchTerm + "%' ) ";
        sql += " ORDER BY " + KEY_ID_INVENT + " ASC";
        //sql += " LIMIT 0,5";

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                // String objectName = cursor.getString(cursor.getColumnIndex(KEY_SKU))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_INVENT_NAME))+" ) ";
                String objectId = cursor.getString(cursor.getColumnIndex("id"));
                String objectName = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_SEARCH));
                MyObject myObject = new MyObject(objectName,objectId);

                // add to editOrderList
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        // Umais commented below 2 lines
        cursor.close();


        // return the editOrderList of records
        return recordsList;
    }

    // Read records related to the search term

    public List<MyObject> GetCustomerAutoCompleteData(CharSequence searchTerm) {

        List<MyObject> recordsList = new ArrayList<>();


//        int cou = CheckRoute();
        int test = getSavedRouteID();
        if (SalesOrder.DEFAULT_SO_ROUTE_ENABLE_DISABLE != null)
            if (SalesOrder.DEFAULT_SO_ROUTE_ENABLE_DISABLE.equals("1"))
                test = 0;

        if (SalesReturn.DEFAULT_SR_ROUTE_ENABLE_DISABLE != null)
            if (SalesReturn.DEFAULT_SR_ROUTE_ENABLE_DISABLE.equals("1"))
                test = 0;

        String sql = "";
        if (test == 0) {
            // select query
            sql = "SELECT DISTINCT c." + KEY_ID_CUSTOMER + ", c." + KEY_CUSTOMER_FNAME + " as fn, c." +
                    KEY_COMPANY_NAME + " as comp, c." + KEY_CUSTOMER_SEARCH + " as srch FROM " +
                    DATABASE_TABLE_CUSTOMER_DETAILS + " c ";

            sql += " WHERE c." + KEY_CUSTOMER_SEARCH + " LIKE '%" + searchTerm + "%'";
            //sql += " OR c."+KEY_COMPANY_NAME+" LIKE '%"+searchTerm+"%' )" ;
            //sql += " OR srch " + " LIKE '%" + searchTerm + "%' ) " ;
            sql += "  AND (c." + KEY_CUSTOMER_UPDATE + " = 0";
            sql += "  OR c." + KEY_CUSTOMER_UPDATE + " = 2 )";
            sql += " ORDER BY c." + KEY_ID_CUSTOMER + " ASC ;";
            //sql += " LIMIT 0,5";


        } else {
            sql = "SELECT DISTINCT c." + KEY_ID_CUSTOMER + ", c." + KEY_CUSTOMER_FNAME + " as fn, c." +
                    KEY_COMPANY_NAME + " as comp, c." + KEY_CUSTOMER_SEARCH + " as srch, r." + KEY_ROUTE_SAVED +
                    " as route FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c, " + DATABASE_TABLE_CUSTOMER_ROUTE + " r ";
/*
            Previous Clause
            sql += " WHERE ( ( comp " + " LIKE '%" + searchTerm + "%'  ";
            sql += " OR fn " + " LIKE '%" + searchTerm + "%' ) " ;

*/
            /* Previous Cluase
            sql += " WHERE ( ( c."+KEY_CUSTOMER_FNAME+" LIKE '%"+searchTerm+"%'";
            sql += " OR c."+KEY_COMPANY_NAME+" LIKE '%"+searchTerm+"%')" ;
            */
            //sql += " OR srch " + " LIKE '%" + searchTerm + "%' ) " ;
            // Now using below Clause
            sql += " WHERE ( c." + KEY_CUSTOMER_SEARCH + " LIKE '%" + searchTerm + "%'";

            sql += " AND c." + KEY_CUSTOMER_ROUTE_ID + " = route";
            sql += "  AND c." + KEY_CUSTOMER_UPDATE + " = 0 ";// yahan BRACKET CLOSE HORHA THA pehly
            sql += "  OR c." + KEY_CUSTOMER_UPDATE + " = 2 )";
            sql += " ORDER BY c." + KEY_ID_CUSTOMER + " ASC ;";
            //sql += " LIMIT 0,5";

        }


        Log.d("sql_query_cust", sql + ";");

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        //if (cursor.moveToFirst()) {
        //while (cursor.moveToNext()) {

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            //String objectName = cursor.getString(cursor.getColumnIndex(KEY_COMPANY_NAME))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_FNAME))+" "+ cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_LNAME))+" ) "  ;
            String objectName = cursor.getString(cursor.getColumnIndex("srch"));
            MyObject myObject = new MyObject(objectName);

            // add to editOrderList
            recordsList.add(myObject);

        }
        //}

        // Umais commented db.close
        cursor.close();

        // return the editOrderList of records
        return recordsList;

        //return recordsList;
    }
    public List<String> GetCustomerAutoCompleteDataArrayList() {

        List<String> recordsList = new ArrayList<>();


//        int cou = CheckRoute();
        int test = getSavedRouteID();
        if (SalesOrder.DEFAULT_SO_ROUTE_ENABLE_DISABLE != null)
            if (SalesOrder.DEFAULT_SO_ROUTE_ENABLE_DISABLE.equals("1"))
                test = 0;



        String sql = "";

            // select query
            sql = "SELECT DISTINCT c." + KEY_ID_CUSTOMER + ", c." + KEY_CUSTOMER_FNAME + " as fn, c." + KEY_COMPANY_NAME + " as comp, c." + KEY_CUSTOMER_SEARCH + " as srch FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " c ";

            sql += " WHERE 1==1 ";
            //sql += " OR c."+KEY_COMPANY_NAME+" LIKE '%"+searchTerm+"%' )" ;
            //sql += " OR srch " + " LIKE '%" + searchTerm + "%' ) " ;
            sql += "  AND (c." + KEY_CUSTOMER_UPDATE + " = 0";
            sql += "  OR c." + KEY_CUSTOMER_UPDATE + " = 2 )";
            sql += " ORDER BY c." + KEY_ID_CUSTOMER + " ASC ;";
            //sql += " LIMIT 0,5";





        Log.d("sql", sql + ";");

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        //if (cursor.moveToFirst()) {
        //while (cursor.moveToNext()) {

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            //String objectName = cursor.getString(cursor.getColumnIndex(KEY_COMPANY_NAME))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_FNAME))+" "+ cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_LNAME))+" ) "  ;


            // add to editOrderList
            recordsList.add( cursor.getString(cursor.getColumnIndex("srch")));

        }
        //}

        // Umais commented db.close
        cursor.close();

        // return the editOrderList of records
        return recordsList;

        //return recordsList;
    }

    public List<MyObject> GetBrandAutoCompleteData(CharSequence searchTerm) {

        List<MyObject> recordsList = new ArrayList<>();


//        int cou = CheckRoute();

            // select query
            sql = "SELECT DISTINCT c." + KEY_ID_BRAND + ", c." + KEY_BRAND_NAME + " as fn  FROM " + DATABASE_TABLE_BRAND + " c ";

            sql += " WHERE c." + KEY_BRAND_NAME + " LIKE '%" + searchTerm + "%'";
            //sql += " OR c."+KEY_COMPANY_NAME+" LIKE '%"+searchTerm+"%' )" ;
            //sql += " OR srch " + " LIKE '%" + searchTerm + "%' ) " ;

            sql += " ORDER BY c." + KEY_ID_CUSTOMER + " ASC ;";
            //sql += " LIMIT 0,5";







        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to editOrderList
        //if (cursor.moveToFirst()) {
        //while (cursor.moveToNext()) {

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

            //String objectName = cursor.getString(cursor.getColumnIndex(KEY_COMPANY_NAME))+" ( "+cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_FNAME))+" "+ cursor.getString(cursor.getColumnIndex(KEY_CUSTOMER_LNAME))+" ) "  ;
            String objectName = cursor.getString(cursor.getColumnIndex("fn"));
            MyObject myObject = new MyObject(objectName);

            // add to editOrderList
            recordsList.add(myObject);

        }
        //}

        // Umais commented db.close
        cursor.close();

        // return the editOrderList of records
        return recordsList;

        //return recordsList;
    }


    public String getInventoryID(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_ID_INVENT};
        Cursor c = db.query(DATABASE_TABLE_INVENTORY, Columns, KEY_INVENTORY_SEARCH + " = '" + name + "'", null, null, null, null);

        String resultId = "";

        int iId = c.getColumnIndex(KEY_ID_INVENT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }

            Log.d("IDForm", resultId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return resultId;
    }

    public String getCustomerID(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_ID_CUSTOMER};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, KEY_CUSTOMER_SEARCH + " = '" + name + "'", null, null, null, null);

        String resultId = "0";

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }

            Log.d("IDForm", resultId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return resultId;
    }
    public String getCustomerName(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_CUSTOMER_SEARCH};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, KEY_ID_CUSTOMER + " = '" + name + "'", null, null, null, null);

        String resultId = "0";

        int iId = c.getColumnIndex(KEY_CUSTOMER_SEARCH);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }

            Log.d("IDForm", resultId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return resultId;
    }

    public String getBrandID(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_ID_CUSTOMER};
        Cursor c = db.query(DATABASE_TABLE_BRAND, Columns, KEY_BRAND_NAME + " = '" + name + "'", null, null, null, null);

        String resultId = "0";

        int iId = c.getColumnIndex(KEY_ID_CUSTOMER);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }

            Log.d("IDForm", resultId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return resultId;
    }
    public String getBrandIDFromProductId(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_BRAND_ID_FROM_INVENT};
        Cursor c = db.query(DATABASE_TABLE_INVENTORY, Columns, " id = '" + name + "'", null, null, null, null);

        String resultId = "0";

        int iId = c.getColumnIndex(KEY_BRAND_ID_FROM_INVENT);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }

            Log.d("IDForm", resultId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return resultId;
    }
    public String getBrandsName(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_BRAND_NAME};
        Cursor c = db.query(DATABASE_TABLE_BRAND, Columns, KEY_ID_CUSTOMER + " = '" + name + "'", null, null, null, null);

        String resultId = "0";

        int iId = c.getColumnIndex(KEY_BRAND_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }

            Log.d("IDForm", resultId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        return resultId;
    }

    public String getCustomerTypeID(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_CUSTOMER_TYPE};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, KEY_CUSTOMER_SEARCH + " = '" + name + "'", null, null, null, null);

        String resultId = "";

        int iId = c.getColumnIndex(KEY_CUSTOMER_TYPE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        Log.d("IDForm", resultId);

        return resultId;
    }

    public String getCustomerTypeIDById(String name) {
        // TODO Auto-generated method stub

        if (name.trim().isEmpty()) {
            name = "0";
        }

        String[] Columns = new String[]{KEY_CUSTOMER_TYPE};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Columns, KEY_ID_CUSTOMER + " = '" + name + "'", null, null, null, null);

        String resultId = "";

        int iId = c.getColumnIndex(KEY_CUSTOMER_TYPE);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId = c.getString(iId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }

        Log.d("IDForm", resultId);

        return resultId;
    }

    public String getRouteNameFromNetID(int netID) {
        // TODO Auto-generated method stub

        String[] Columns = new String[]{KEY_ROUTE_NAME};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_ROUTE, Columns, KEY_ROUTE_NET_ID + " = " + netID, null, null, null, null);

        String result = "";

        int iData = c.getColumnIndex(KEY_ROUTE_NAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;
    }


    public long createSalesOrderEntry(String id, String CustID, String EmpID, String Values, String Notes, String startTime, String date, String dateSHORT, String total, String total2, String discount, String amountRecieved, String total3, double Latitude, double Longitude, String exec_Complete, String exeDate, String order_delete, String net_oid, boolean AfterLogin, String saleType, String distributorId, int updateVal,String DistInvoiceNumber) {


        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(id);
        cv.put(KEY_SALES_ORDER_ID, i);
        cv.put(KEY_SALES_ORDER_CUSTOMER_ID, CustID);
        cv.put(KEY_SALES_ORDER_EMPLOYEE_ID, EmpID);
        cv.put(KEY_SALES_ORDER_VALUES, Values);
        cv.put(KEY_SALES_ORDER_NOTES, Notes);
        cv.put(KEY_SALES_ORDER_START_DATETIME, startTime);
        cv.put(KEY_SALES_ORDER_DATETIME, date);
        cv.put(KEY_SALES_ORDER_DATESHORT, dateSHORT);
        cv.put(KEY_SALES_ORDER_TOTAL, total);
        cv.put(KEY_SALES_ORDER_TOTAL2, total2);
        cv.put(KEY_SALES_ORDER_DISCOUNT, discount);
        cv.put(KEY_SALES_ORDER_LATITUDE, Latitude);
        cv.put(KEY_SALES_ORDER_LONGITUDE, Longitude);
        cv.put(KEY_SALES_ORDER_UPDATE, updateVal);
        cv.put(KEY_SALES_ORDER_PAYMENT_TYPE, saleType);
        cv.put(KEY_SALES_ORDER_SELECTED_DISTRIBUTOR_ID, distributorId);
        cv.put(KEY_SALES_ORDER_DISTRIBUTOR_INVOICE, DistInvoiceNumber);

        if (exeDate == null) {
            cv.put(KEY_SALES_ORDER_EXECUTION_DATE, getDateTime());
        } else {
            cv.put(KEY_SALES_ORDER_EXECUTION_DATE, exeDate);
        }

        if (amountRecieved == null) {
            cv.put(KEY_SALES_ORDER_AMOUNT_RECIEVED, 0);
        } else {
            cv.put(KEY_SALES_ORDER_AMOUNT_RECIEVED, amountRecieved);
        }

        if (net_oid == null) {
            cv.put(KEY_SALES_ORDER_ANDROID_OID, 0);
        } else {
            cv.put(KEY_SALES_ORDER_ANDROID_OID, net_oid);
        }

        if (exec_Complete == null) {
            cv.put(KEY_SALES_ORDER_EXECUTE_COMPLETE, 0);
        } else {
            cv.put(KEY_SALES_ORDER_EXECUTE_COMPLETE, exec_Complete);
        }

        if (order_delete == null) {
            cv.put(KEY_SALES_ORDER_DELETE, 0);
        } else {
            cv.put(KEY_SALES_ORDER_DELETE, order_delete);
        }

        long insertedId = db.insert(DATABASE_TABLE_SALESMAN_SALES_ORDER, null, cv);

        if (AfterLogin) {
            ContentValues cv1 = new ContentValues();
            cv1.put(KEY_SALES_ORDER_VALUES, Values);

            db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv1, KEY_SALES_ORDER_ID + " = " + insertedId, null);

        } else {
            ContentValues cv1 = new ContentValues();
            cv1.put(KEY_SALES_ORDER_VALUES, Values + insertedId + "--");

            db.update(DATABASE_TABLE_SALESMAN_SALES_ORDER, cv1, KEY_SALES_ORDER_ID + " = " + insertedId, null);
        }

        /*
        if( total2 == null ){
            updateTotal2SalesOrder( String.valueOf( insertedId ), total );
        }else{
            updateTotal2SalesOrder( String.valueOf( insertedId ), total2 );
        }
        */
        /*if( total3 == null ){
            updateTotalExecuteSalesOrder( String.valueOf( insertedId ), total );
        }else{
            updateTotalExecuteSalesOrder( String.valueOf( insertedId ), total3 );
        }*/


        return insertedId;

    }


    public long createOrderDetails(String orderId,
                                   String productId,
                                   String quantity,
                                   String quantityExe,
                                   String tradePrice,
                                   String discount1,
                                   String discount2,
                                   String tradeOffer,
                                   String scheme,
                                   String schemeQty,
                                   String schemeFormula,
                                   String schemeProduct,
                                   String tax1,
                                   String tax2,
                                   String tax3,
                                   String subTotal,
                                   String multi_scheme,
                                   double t_o_v,
                                   double d_v_1,
                                   double d_v_2,
                                   double t_type,
                                   double t_mrp_type,
                                   double t_val, String mrp_price) {

        ContentValues cv = new ContentValues();
        //
        cv.put(KEY_ORDER_DETAIL_ORDER_ID, orderId);
        cv.put(KEY_ORDER_DETAIL_PRODUCT_ID, productId);
        cv.put(KEY_ORDER_DETAIL_QUANTITY, quantity);
        cv.put(KEY_ORDER_DETAIL_QUANTITY_EXE, quantityExe);
        cv.put(KEY_ORDER_DETAIL_TRADE_PRICE, tradePrice);
        cv.put(KEY_ORDER_DETAIL_DISCOUNT_1, discount1);
        cv.put(KEY_ORDER_DETAIL_DISCOUNT_2, discount2);
        cv.put(KEY_ORDER_DETAIL_TRADE_OFFER, tradeOffer);
        cv.put(KEY_ORDER_DETAIL_SCHEME, scheme);
        cv.put(KEY_ORDER_DETAIL_SCHEME_QTY, schemeQty);
        cv.put(KEY_ORDER_DETAIL_SCHEME_FORMULA, schemeFormula);
        cv.put(KEY_ORDER_DETAIL_SCHEME_PRODUCT, schemeProduct);
        cv.put(KEY_ORDER_DETAIL_TAX_1, tax1);
        cv.put(KEY_ORDER_DETAIL_TAX_2, tax2);
        cv.put(KEY_ORDER_DETAIL_TAX_3, tax3);
        cv.put("multi_scheme", multi_scheme);
        cv.put(KEY_ORDER_DETAIL_SUBTOTAL, subTotal);
        cv.put(KEY_ORDER_DETAIL_DV1, d_v_1);
        cv.put(KEY_ORDER_DETAIL_DV2, d_v_2);
        cv.put(KEY_ORDER_DETAIL_TOV, t_o_v);
        cv.put(KEY_ORDER_DETAIL_T_TYPE, t_type);
        cv.put(KEY_ORDER_DETAIL_T_MRP_TYPE, t_mrp_type);
        cv.put(KEY_ORDER_DETAIL_T_VAL, t_val);
        cv.put(KEY_ORDER_DETAIL_MRP_PRICE, mrp_price);


        long insertedOrderDetail = db.insert(DATABASE_TABLE_ORDER_DETAILS, null, cv);
        return insertedOrderDetail;

    }

    public long createReturnDetails(

            String orderId, String productId, String quantity,
            String quantityExe, String tradePrice, String discount1,
            String discount2, String tradeOffer, String scheme,
            String schemeQty, String schemeFormula,
            String schemeProduct, String tax1,
            String tax2, String tax3,
            String subTotal,
            String multi_scheme,
            double t_o_v,
            double d_v_1,
            double d_v_2,
            double t_type,
            double t_mrp_type,
            double t_val,
            String mrp_price) {

        ContentValues cv = new ContentValues();

        cv.put(KEY_RETURN_DETAIL_ORDER_ID, orderId);
        cv.put(KEY_RETURN_DETAIL_PRODUCT_ID, productId);
        cv.put(KEY_RETURN_DETAIL_QUANTITY, quantity);
        cv.put(KEY_RETURN_DETAIL_QUANTITY_EXE, quantityExe);
        cv.put(KEY_RETURN_DETAIL_TRADE_PRICE, tradePrice);
        cv.put(KEY_RETURN_DETAIL_DISCOUNT_1, discount1);
        cv.put(KEY_RETURN_DETAIL_DISCOUNT_2, discount2);
        cv.put(KEY_RETURN_DETAIL_TRADE_OFFER, tradeOffer);
        cv.put(KEY_RETURN_DETAIL_SCHEME, scheme);
        cv.put(KEY_RETURN_DETAIL_SCHEME_QTY, schemeQty);
        cv.put(KEY_RETURN_DETAIL_SCHEME_FORMULA, schemeFormula);
        cv.put(KEY_RETURN_DETAIL_SCHEME_PRODUCT, schemeProduct);
        cv.put(KEY_RETURN_DETAIL_TAX_1, tax1);
        cv.put(KEY_RETURN_DETAIL_TAX_2, tax2);
        cv.put(KEY_RETURN_DETAIL_TAX_3, tax3);

        cv.put("multi_scheme", multi_scheme);
        cv.put(KEY_RETURN_DETAIL_TOV, t_o_v);
        cv.put(KEY_RETURN_DETAIL_DV1, d_v_1);
        cv.put(KEY_RETURN_DETAIL_DV2, d_v_2);
        cv.put(KEY_RETURN_DETAIL_T_TYPE, t_type);
        cv.put(KEY_RETURN_DETAIL_T_MRP_TYPE, t_mrp_type);
        cv.put(KEY_RETURN_DETAIL_T_VAL, t_val);
        cv.put(KEY_RETURN_DETAIL_MRP_PRICE, mrp_price);


        cv.put(KEY_RETURN_DETAIL_SUBTOTAL, subTotal);


        long insertedReturnDetail = db.insert(DATABASE_TABLE_RETURN_DETAILS, null, cv);
        return insertedReturnDetail;

    }

    public long createPaymentRecieved(String paymentId, String cId, String empId, String amount, String dateTime, String detail, String chequeNo, String chequeDate, String bankName, int executeComplete, String paymentType, String distributorId, int update) {

        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(paymentId);

        cv.put(KEY_PAYMENT_RECIEVED_ID, i);
        cv.put(KEY_PAYMENT_RECIEVED_CUST_ID, cId);
        cv.put(KEY_PAYMENT_RECIEVED_EMP_ID, empId);
        cv.put(KEY_PAYMENT_RECIEVED_AMOUNT, amount);
        cv.put(KEY_PAYMENT_RECIEVED_DATETIME, dateTime);
        cv.put(KEY_PAYMENT_RECIEVED_DETAIL, detail);
        cv.put(KEY_PAYMENT_RECIEVED_CHEQUE_NO, chequeNo);
        cv.put(KEY_PAYMENT_RECIEVED_CHEQUE_DATE, chequeDate);
        cv.put(KEY_PAYMENT_RECIEVED_BANK_NAME, bankName);
        cv.put(KEY_PAYMENT_RECIEVED_EXECUTE_COMPLETE, executeComplete);
        cv.put(KEY_PAYMENT_RECIEVED_PAYMENT_TYPE, paymentType);
        cv.put(KEY_PAYMENT_RECIEVED_UPDATE, update);
        cv.put(KEY_PAYMENT_RECIEVED_SELECTED_DISTRIBUTOR_ID, distributorId);


        long insertedPaymentRecieved = db.insert(DATABASE_TABLE_PAYMENT_RECIEVED, null, cv);
        return insertedPaymentRecieved;

    }

    public long createPaymentRecievedLocal(String paymentId, String cId, String empId, String amount, String startTime, String dateTime, String detail, double Latitude, double Longitude, String mapName, String chequeNo, String chequeDate, String bankName, int executeComplete, String paymentType, String distributorId, int update) {

        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(paymentId);

        cv.put(KEY_PAYMENT_RECIEVED_ID, i);
        cv.put(KEY_PAYMENT_RECIEVED_CUST_ID, cId);
        cv.put(KEY_PAYMENT_RECIEVED_EMP_ID, empId);
        cv.put(KEY_PAYMENT_RECIEVED_AMOUNT, amount);
        cv.put(KEY_PAYMENT_RECIEVED_START_DATETIME, startTime);
        cv.put(KEY_PAYMENT_RECIEVED_DATETIME, dateTime);
        cv.put(KEY_PAYMENT_RECIEVED_DETAIL, detail);
        cv.put(KEY_PAYMENT_RECIEVED_LATITUDE, Latitude);
        cv.put(KEY_PAYMENT_RECIEVED_LONGITUDE, Longitude);
        cv.put(KEY_PAYMENT_RECIEVED_MAPNAME, mapName);
        cv.put(KEY_PAYMENT_RECIEVED_CHEQUE_NO, chequeNo);
        cv.put(KEY_PAYMENT_RECIEVED_CHEQUE_DATE, chequeDate);
        cv.put(KEY_PAYMENT_RECIEVED_BANK_NAME, bankName);
        cv.put(KEY_PAYMENT_RECIEVED_EXECUTE_COMPLETE, executeComplete);
        cv.put(KEY_PAYMENT_RECIEVED_PAYMENT_TYPE, paymentType);
        cv.put(KEY_PAYMENT_RECIEVED_UPDATE, update);
        cv.put(KEY_PAYMENT_RECIEVED_SELECTED_DISTRIBUTOR_ID, distributorId);


        long insertedPaymentRecieved = db.insert(DATABASE_TABLE_PAYMENT_RECIEVED, null, cv);
        return insertedPaymentRecieved;

    }

    /////////
    public String getSelectedCustomeIdFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_CUST_ID};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_CUST_ID);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public String getSelectedAmountFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_AMOUNT};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_AMOUNT);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public String getSelectedPaymentTypeFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_PAYMENT_TYPE};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_PAYMENT_TYPE);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public String getSelectedChequeNoFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_CHEQUE_NO};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_CHEQUE_NO);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public String getSelectedChequeDateFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_CHEQUE_DATE};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_CHEQUE_DATE);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public String getSelectedBankNameFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_BANK_NAME};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_BANK_NAME);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public String getSelectedDatetimeFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_DATETIME};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_DATETIME);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public String getSelectedDetailFromPaymentRec(String id) {

        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_PAYMENT_RECIEVED_DETAIL};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Column, KEY_PAYMENT_RECIEVED_ID + " = " + idInt, null, null, null, null);

        String result = "";
        int iFName = c.getColumnIndex(KEY_PAYMENT_RECIEVED_DETAIL);
        //int iLName = c.getColumnIndex(KEY_CUSTOMER_LNAME);

        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iFName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    //////

    public ArrayList<HashMap<String, String>> getPaymentRecieved() {
        ArrayList<HashMap<String, String>> paymentRecievedList;

        paymentRecievedList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_PAYMENT_RECIEVED + " WHERE " + KEY_PAYMENT_RECIEVED_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_ID);
        int iCustId = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_CUST_ID);
        int iEmpId = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_EMP_ID);
        int iAmount = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_AMOUNT);
        int iStartDateTime = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_START_DATETIME);
        int iDateTime = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_DATETIME);
        int iDetail = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_DETAIL);
        int iLati = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_LATITUDE);
        int iLongi = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_LONGITUDE);
        int iMapName = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_MAPNAME);
        int iChequeNo = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_CHEQUE_NO);
        int iChequeDate = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_CHEQUE_DATE);
        int iBankName = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_BANK_NAME);
        int iPaymentType = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_PAYMENT_TYPE);
        int iSelectedDistributor = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_SELECTED_DISTRIBUTOR_ID);

        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("payment_id", cursor.getString(iId));
                    map.put("cust_id", cursor.getString(iCustId));
                    map.put("emp_id", cursor.getString(iEmpId));
                    map.put("start_datetime", cursor.getString(iStartDateTime));
                    map.put("datetime", cursor.getString(iDateTime));
                    map.put("amount", cursor.getString(iAmount));
                    map.put("details", cursor.getString(iDetail));
                    map.put("latitude", cursor.getString(iLati));
                    map.put("longitude", cursor.getString(iLongi));
                    map.put("mapName", cursor.getString(iMapName));
                    map.put("cheque_no", cursor.getString(iChequeNo));
                    map.put("cheque_date", cursor.getString(iChequeDate));
                    map.put("bank_name", cursor.getString(iBankName));
                    map.put("payment_type", cursor.getString(iPaymentType));
                    map.put("selected_distributor_id", cursor.getString(iSelectedDistributor));


                    paymentRecievedList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return paymentRecievedList;

    }


    public ArrayList<HashMap<String, String>> getPaymentRecievedForList() {
        ArrayList<HashMap<String, String>> paymentRecievedList;

        paymentRecievedList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_PAYMENT_RECIEVED + " ORDER BY " + KEY_PAYMENT_RECIEVED_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_ID);
        int iCustId = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_CUST_ID);
        int iEmpId = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_EMP_ID);
        int iAmount = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_AMOUNT);
        int iDateTime = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_DATETIME);
        int iDetail = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_DETAIL);
        int iExecuteComplete = cursor.getColumnIndex(KEY_PAYMENT_RECIEVED_EXECUTE_COMPLETE);

        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    //map.put("payment_id", cursor.getString(iId));
                    map.put("custId", cursor.getString(iCustId));
                    //map.put("emp_id", cursor.getString(iEmpId));
                    map.put("customerName", getSelectedCustomerSearch(String.valueOf(cursor.getString(iCustId))));
                    map.put("datetime", cursor.getString(iDateTime));
                    map.put("amount", cursor.getString(iAmount));
                    map.put("details", cursor.getString(iDetail));
                    map.put("execute_complete", cursor.getString(iExecuteComplete));


                    paymentRecievedList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return paymentRecievedList;

    }

    public ArrayList<String> getSelectedPaymentRecievedID() {
        // TODO Auto-generated method stub

        String[] Columns = new String[]{KEY_PAYMENT_RECIEVED_ID};
        Cursor c = db.query(DATABASE_TABLE_PAYMENT_RECIEVED, Columns, null, null, null, null, KEY_PAYMENT_RECIEVED_DATETIME + " DESC");
        ArrayList<String> resultId = new ArrayList<>();

        int iId = c.getColumnIndex(KEY_PAYMENT_RECIEVED_ID);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId.add("" + c.getInt(iId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return resultId;
    }

    public long createSalesReturnEntry(String id, String CustID, String EmpID, String Values, String Notes, String startDate, String date, double Latitude, double Longitude, String order_delete, String net_oid, boolean AfterLogin, String total, String total2, String discount, int updateVal, int returnReason, String distributorId, int executeComplete) {


        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(id);
        cv.put(KEY_SALES_RETURN_ID, i);
        cv.put(KEY_SALES_RETURN_CUSTOMER_ID, CustID);
        cv.put(KEY_SALES_RETURN_EMPLOYEE_ID, EmpID);
        cv.put(KEY_SALES_RETURN_VALUES, Values);
        cv.put(KEY_SALES_RETURN_NOTES, Notes);
        cv.put(KEY_SALES_RETURN_START_DATE, startDate);
        cv.put(KEY_SALES_RETURN_DATE, date);
        cv.put(KEY_SALES_RETURN_TOTAL, total);
        cv.put(KEY_SALES_RETURN_TOTAL2, total2);
        cv.put(KEY_SALES_RETURN_DISCOUNT, discount);
        cv.put(KEY_SALES_RETURN_LATITUDE, Latitude);
        cv.put(KEY_SALES_RETURN_LONGITUDE, Longitude);
        cv.put(KEY_SALES_RETURN_UPDATE, updateVal);
        cv.put(KEY_SALES_RETURN_REASON, returnReason);
        cv.put(KEY_SALES_RETURN_EXECUTE_COMPLETE, executeComplete);
        cv.put(KEY_SALES_RETURN_SELECTED_DISTRIBUTOR_ID, distributorId);

        if (net_oid == null) {
            cv.put(KEY_SALES_RETURN_ANDROID_OID, 0);
        } else {
            cv.put(KEY_SALES_RETURN_ANDROID_OID, net_oid);
        }

        if (order_delete == null) {
            cv.put(KEY_SALES_RETURN_DELETE, 0);
        } else {
            cv.put(KEY_SALES_RETURN_DELETE, order_delete);
        }


        long insertedId = db.insert(DATABASE_TABLE_SALESMAN_SALES_RETURN, null, cv);

        if (AfterLogin) {
            ContentValues cv1 = new ContentValues();
            cv1.put(KEY_SALES_RETURN_VALUES, Values);

            db.update(DATABASE_TABLE_SALESMAN_SALES_RETURN, cv1, KEY_SALES_RETURN_ID + " = " + insertedId, null);

        } else {
            ContentValues cv1 = new ContentValues();
            cv1.put(KEY_SALES_RETURN_VALUES, Values + insertedId + "--");

            db.update(DATABASE_TABLE_SALESMAN_SALES_RETURN, cv1, KEY_SALES_RETURN_ID + " = " + insertedId, null);

        }



        /*if( total2 == null ){
            updateTotal2SalesReturn( String.valueOf( insertedId ), total );
        }else{
            updateTotal2SalesReturn( String.valueOf( insertedId ), total2 );
        }
*/

        return insertedId;

    }


    public ArrayList<HashMap<String, String>> getSalesOrder() {
        // TODO Auto-generated method stub


        String[] Columns = new String[]{KEY_SALES_ORDER_CUSTOMER_ID, KEY_SALES_ORDER_EMPLOYEE_ID, KEY_SALES_ORDER_VALUES, KEY_SALES_ORDER_NOTES, KEY_SALES_ORDER_START_DATETIME, KEY_SALES_ORDER_DATETIME, KEY_SALES_ORDER_TOTAL, KEY_SALES_ORDER_TOTAL2, KEY_SALES_ORDER_DISCOUNT, KEY_SALES_ORDER_TOTAL_EXECUTE, KEY_SALES_ORDER_LATITUDE, KEY_SALES_ORDER_LONGITUDE, KEY_SALES_ORDER_ID, KEY_SALES_ORDER_ANDROID_OID, KEY_SALES_ORDER_DELETE, KEY_SALES_ORDER_UPDATE, KEY_SALES_ORDER_EXECUTE_COMPLETE, KEY_SALES_ORDER_EXECUTION_DATE, KEY_SALES_ORDER_AMOUNT_RECIEVED, KEY_SALES_ORDER_PAYMENT_TYPE, KEY_SALES_ORDER_SELECTED_DISTRIBUTOR_ID};

        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SALES_ORDER, Columns, KEY_SALES_ORDER_UPDATE + " = 1 ", null, null, null, null);

        String resultId = "";


        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

//        int iQty = c.getColumnIndex(KEY_QUANTITY);
        int iCId = c.getColumnIndex(KEY_SALES_ORDER_CUSTOMER_ID);
        int iEId = c.getColumnIndex(KEY_SALES_ORDER_EMPLOYEE_ID);
        int iVal = c.getColumnIndex(KEY_SALES_ORDER_VALUES);
        int iNotes = c.getColumnIndex(KEY_SALES_ORDER_NOTES);
        int iDate = c.getColumnIndex(KEY_SALES_ORDER_DATETIME);
        int iStartDateTime = c.getColumnIndex(KEY_SALES_ORDER_START_DATETIME);
        int iTotal = c.getColumnIndex(KEY_SALES_ORDER_TOTAL);
        int iTotal2 = c.getColumnIndex(KEY_SALES_ORDER_TOTAL2);
        int iTotalEXE = c.getColumnIndex(KEY_SALES_ORDER_TOTAL_EXECUTE);
        int iLat = c.getColumnIndex(KEY_SALES_ORDER_LATITUDE);
        int iLongi = c.getColumnIndex(KEY_SALES_ORDER_LONGITUDE);
        int iOID = c.getColumnIndex(KEY_SALES_ORDER_ID);
        int iNETOid = c.getColumnIndex(KEY_SALES_ORDER_ANDROID_OID);
        int iDelete = c.getColumnIndex(KEY_SALES_ORDER_DELETE);
        int iEXE = c.getColumnIndex(KEY_SALES_ORDER_EXECUTE_COMPLETE);
        int iExeDate = c.getColumnIndex(KEY_SALES_ORDER_EXECUTION_DATE);
        int iAmtRecieved = c.getColumnIndex(KEY_SALES_ORDER_AMOUNT_RECIEVED);
        int iDiscount = c.getColumnIndex(KEY_SALES_ORDER_DISCOUNT);
        int iSaleType = c.getColumnIndex(KEY_SALES_ORDER_PAYMENT_TYPE);
        int iSelectedDistributor = c.getColumnIndex(KEY_SALES_ORDER_SELECTED_DISTRIBUTOR_ID);


        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(OID_COLUMN, c.getString(iOID));
                    map.put(CID_COLUMN, c.getString(iCId));
                    map.put(EID_COLUMN, c.getString(iEId));
                    map.put(VAL_COLUMN, c.getString(iVal));
                    map.put(NOTES_COLUMN, c.getString(iNotes));
                    map.put(START_DATE_COLUMN, c.getString(iStartDateTime));
                    map.put(DATE_COLUMN, c.getString(iDate));
                    map.put(TOTAL_COLUMN, c.getString(iTotal));
                    map.put(TOTAL2_COLUMN, c.getString(iTotal2));
                    map.put(DISCOUNT_COLUMN, c.getString(iDiscount));
                    map.put(PAYMENT_TYPE_COLUMN, c.getString(iSaleType));
                    map.put(SELECTED_DISTRIBUTOR_COLUMN, c.getString(iSelectedDistributor));

                    // map.put(TOTAL2_COLUMN, String.valueOf(getTotal2EditText( c.getString(iOID) )));

                    /*map.put(TOTALEXE_COLUMN, c.getString(iTotalEXE));*/

                    map.put(TOTALEXE_COLUMN, String.valueOf(getTotalExecuteEditText(c.getString(iOID))));

                    map.put(LATITUDE_COLUMN, c.getString(iLat));
                    map.put(LONGITUDE_COLUMN, c.getString(iLongi));
                    map.put(NET_OID_COLUMN, c.getString(iNETOid));
                    map.put(DELETE_ITEM_COLUMN, c.getString(iDelete));
                    map.put(EXE_COMPLETE_COLUMN, c.getString(iEXE));
                    map.put(EXE_DATE_COLUMN, c.getString(iExeDate));
                    map.put(AMOUNT_RECIEVED_COLUMN, c.getString(iAmtRecieved));


                    result.add(map);
                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }

    public ArrayList<HashMap<String, String>> getSalesReturn() {
        // TODO Auto-generated method stub


        String[] Columns = new String[]{KEY_SALES_RETURN_CUSTOMER_ID, KEY_SALES_RETURN_EMPLOYEE_ID, KEY_SALES_RETURN_VALUES, KEY_SALES_RETURN_REASON, KEY_SALES_RETURN_NOTES, KEY_SALES_RETURN_START_DATE, KEY_SALES_RETURN_DATE, KEY_SALES_RETURN_TOTAL, KEY_SALES_RETURN_TOTAL2, KEY_SALES_RETURN_DISCOUNT, KEY_SALES_RETURN_LATITUDE, KEY_SALES_RETURN_LONGITUDE, KEY_SALES_RETURN_ID, KEY_SALES_RETURN_ANDROID_OID, KEY_SALES_RETURN_DELETE, KEY_SALES_RETURN_UPDATE, KEY_SALES_RETURN_SELECTED_DISTRIBUTOR_ID};

        Cursor c = db.query(DATABASE_TABLE_SALESMAN_SALES_RETURN, Columns, KEY_SALES_RETURN_UPDATE + " = 1 ", null, null, null, null);

        String resultId = "";


        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

//        int iQty = c.getColumnIndex(KEY_QUANTITY);
        int iCId = c.getColumnIndex(KEY_SALES_RETURN_CUSTOMER_ID);
        int iEId = c.getColumnIndex(KEY_SALES_RETURN_EMPLOYEE_ID);
        int iVal = c.getColumnIndex(KEY_SALES_RETURN_VALUES);
        int iNotes = c.getColumnIndex(KEY_SALES_RETURN_NOTES);
        int iStartDate = c.getColumnIndex(KEY_SALES_RETURN_START_DATE);
        int iDate = c.getColumnIndex(KEY_SALES_RETURN_DATE);
        int iTotal = c.getColumnIndex(KEY_SALES_RETURN_TOTAL);
        int iTotal2 = c.getColumnIndex(KEY_SALES_RETURN_TOTAL2);
        int iLat = c.getColumnIndex(KEY_SALES_RETURN_LATITUDE);
        int iLongi = c.getColumnIndex(KEY_SALES_RETURN_LONGITUDE);
        int iOID = c.getColumnIndex(KEY_SALES_RETURN_ID);
        int iNETOid = c.getColumnIndex(KEY_SALES_RETURN_ANDROID_OID);
        int iDelete = c.getColumnIndex(KEY_SALES_RETURN_DELETE);
        int iReturnReason = c.getColumnIndex(KEY_SALES_RETURN_REASON);
        int iDiscount = c.getColumnIndex(KEY_SALES_RETURN_DISCOUNT);
        int iSelectedDistributor = c.getColumnIndex(KEY_SALES_RETURN_SELECTED_DISTRIBUTOR_ID);

        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(SRID_COLUMN, c.getString(iOID));
                    map.put(SRCID_COLUMN, c.getString(iCId));
                    map.put(SREID_COLUMN, c.getString(iEId));
                    map.put(SRVAL_COLUMN, c.getString(iVal));
                    map.put(SRNOTES_COLUMN, c.getString(iNotes));
                    map.put(SRSTART_DATE_COLUMN, c.getString(iStartDate));
                    map.put(SRDATE_COLUMN, c.getString(iDate));
                    map.put(SRTOTAL_COLUMN, c.getString(iTotal));
                    map.put(SRTOTAL2_COLUMN, c.getString(iTotal2));
                    map.put(SRDISCOUNT_COLUMN, c.getString(iDiscount));
                    map.put(SRLATITUDE_COLUMN, c.getString(iLat));
                    map.put(SRLONGITUDE_COLUMN, c.getString(iLongi));
                    map.put(SRNET_OID_COLUMN, c.getString(iNETOid));
                    map.put(SRDELETE_ITEM_COLUMN, c.getString(iDelete));
                    map.put(SRRETURN_REASON_COLUMN, c.getString(iReturnReason));
                    map.put(SRSELECTED_DISTRIBUTOR_COLUMN, c.getString(iSelectedDistributor));


                    result.add(map);
                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }


    public long createExpense(int expenseId, int shopId, int commitmentId, String amount, int expenseType, String startDateTime, String dateTime, String remarks, double latitude, double longitude, String mapName, int update) {

        ContentValues cv = new ContentValues();
        //int i = Integer.parseInt(paymentId);

        cv.put(KEY_EXPENSE_ID, expenseId);
        cv.put(KEY_EXPENSE_SHOP_ID, shopId);
        cv.put(KEY_EXPENSE_COMMITMENT_ID, commitmentId);
        cv.put(KEY_EXPENSE_AMOUNT, amount);
        cv.put(KEY_EXPENSE_TYPE, expenseType);
        cv.put(KEY_EXPENSE_START_DATETIME, startDateTime);
        cv.put(KEY_EXPENSE_DATETIME, dateTime);
        cv.put(KEY_EXPENSE_REMARKS, remarks);
        cv.put(KEY_EXPENSE_LATITUDE, latitude);
        cv.put(KEY_EXPENSE_LONGITUDE, longitude);
        cv.put(KEY_EXPENSE_MAPNAME, mapName);
        cv.put(KEY_EXPENSE_STATUS, 1);
        cv.put(KEY_EXPENSE_UPDATE, update);


        long insertedPaymentRecieved = db.insert(DATABASE_TABLE_EXPENSE, null, cv);
        return insertedPaymentRecieved;

    }

    public long createMarketingExpense(String expenseId, String shopId, String commitmentId, String amount, String expenseType, String dateTime, String remarks, String status, int update) {

        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(expenseId);

        cv.put(KEY_EXPENSE_ID, i);
        cv.put(KEY_EXPENSE_AMOUNT, amount);
        cv.put(KEY_EXPENSE_SHOP_ID, shopId);
        cv.put(KEY_EXPENSE_COMMITMENT_ID, commitmentId);
        cv.put(KEY_EXPENSE_TYPE, expenseType);
        cv.put(KEY_EXPENSE_DATETIME, dateTime);
        cv.put(KEY_EXPENSE_REMARKS, remarks);
        cv.put(KEY_EXPENSE_STATUS, status);
        /*cv.put(KEY_EXPENSE_LATITUDE, latitude);
        cv.put(KEY_EXPENSE_LONGITUDE, longitude);
        cv.put(KEY_EXPENSE_MAPNAME, mapName);
        cv.put(KEY_EXPENSE_STATUS, 1);*/
        cv.put(KEY_EXPENSE_UPDATE, update);


        long insertedPaymentRecieved = db.insert(DATABASE_TABLE_EXPENSE, null, cv);
        return insertedPaymentRecieved;

    }

    public int getMaxIdFromExpense() {

        String que = "SELECT MAX(" + KEY_EXPENSE_ID + ") FROM " + DATABASE_TABLE_EXPENSE;

        Cursor c = db.rawQuery(que, null);

        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);

        if (!c.isClosed())
            c.close();

        return maxid;

    }

    public ArrayList<HashMap<String, String>> getExpense() {
        ArrayList<HashMap<String, String>> expenseList;

        expenseList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_EXPENSE + " WHERE " + KEY_EXPENSE_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_EXPENSE_ID);
        int iShopId = cursor.getColumnIndex(KEY_EXPENSE_SHOP_ID);
        int iCommitmentId = cursor.getColumnIndex(KEY_EXPENSE_COMMITMENT_ID);
        int iAmount = cursor.getColumnIndex(KEY_EXPENSE_AMOUNT);
        int iExpType = cursor.getColumnIndex(KEY_EXPENSE_TYPE);
        int iStartDateTime = cursor.getColumnIndex(KEY_EXPENSE_START_DATETIME);
        int iDateTime = cursor.getColumnIndex(KEY_EXPENSE_DATETIME);
        int iremarks = cursor.getColumnIndex(KEY_EXPENSE_REMARKS);
        int iLati = cursor.getColumnIndex(KEY_EXPENSE_LATITUDE);
        int iLongi = cursor.getColumnIndex(KEY_EXPENSE_LONGITUDE);
        int iMapName = cursor.getColumnIndex(KEY_EXPENSE_MAPNAME);
        int iStatus = cursor.getColumnIndex(KEY_EXPENSE_STATUS);


        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("exp_id", cursor.getString(iId));
                    map.put("exp_shop_id", cursor.getString(iShopId));
                    map.put("exp_commitment_id", cursor.getString(iCommitmentId));
                    map.put("exp_amount", cursor.getString(iAmount));
                    map.put("exp_type", cursor.getString(iExpType));
                    map.put("exp_start_datetime", cursor.getString(iStartDateTime));
                    map.put("exp_datetime", cursor.getString(iDateTime));
                    map.put("exp_remarks", cursor.getString(iremarks));
                    map.put("exp_latitude", cursor.getString(iLati));
                    map.put("exp_longitude", cursor.getString(iLongi));
                    map.put("exp_mapName", cursor.getString(iMapName));
                    map.put("exp_status", cursor.getString(iStatus));


                    expenseList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return expenseList;

    }

    public ArrayList<HashMap<String, String>> getExpenseList() {
        ArrayList<HashMap<String, String>> expenseList;

        expenseList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_EXPENSE + " ORDER BY " + KEY_EXPENSE_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_EXPENSE_ID);
        int iShopId = cursor.getColumnIndex(KEY_EXPENSE_SHOP_ID);
        int iAmount = cursor.getColumnIndex(KEY_EXPENSE_AMOUNT);
        int iExpType = cursor.getColumnIndex(KEY_EXPENSE_TYPE);
        int iDateTime = cursor.getColumnIndex(KEY_EXPENSE_DATETIME);
        int iremarks = cursor.getColumnIndex(KEY_EXPENSE_REMARKS);
        int iLati = cursor.getColumnIndex(KEY_EXPENSE_LATITUDE);
        int iLongi = cursor.getColumnIndex(KEY_EXPENSE_LONGITUDE);
        int iStatus = cursor.getColumnIndex(KEY_EXPENSE_STATUS);


        try {

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("exp_id", cursor.getString(iId));
                    map.put("exp_amount", cursor.getString(iAmount));
                    map.put("exp_type", cursor.getString(iExpType));
                    map.put("exp_typeName", getSelectedExpenseTypeName(String.valueOf(cursor.getString(iExpType))));
                    map.put("exp_datetime", cursor.getString(iDateTime));
                    map.put("exp_remarks", cursor.getString(iremarks));
                    map.put("exp_latitude", cursor.getString(iLati));
                    map.put("exp_longitude", cursor.getString(iLongi));
                    map.put("exp_status", cursor.getString(iStatus));
                    map.put("exp_statusName", getSelectedExpenseStatusName(String.valueOf(cursor.getString(iStatus))));
                    map.put("exp_shop", cursor.getString(iShopId));
                    //if (!String.valueOf(cursor.getString(iShopId)).equals(""))
                    map.put("exp_shopName", getSelectedCustomerCompName(String.valueOf(cursor.getString(iShopId))));


                    expenseList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return expenseList;

    }

    public void deleteExpense() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_EXPENSE;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }


    public long createShopVisit(int shopVisitId, String shopID, int reasonId, String startTime, String dateTime, String remarks, double latitude, double longitude, String mapName, String distributorId, int update) {

        ContentValues cv = new ContentValues();
        //int i = Integer.parseInt(paymentId);

        cv.put(KEY_SHOP_VISIT_ID, shopVisitId);
        cv.put(KEY_SHOP_VISIT_CUST_ID, shopID);
        cv.put(KEY_SHOP_VISIT_REASON_ID, reasonId);
        cv.put(KEY_SHOP_VISIT_START_DATETIME, startTime);
        cv.put(KEY_SHOP_VISIT_DATETIME, dateTime);
        cv.put(KEY_SHOP_VISIT_REMARKS, remarks);
        cv.put(KEY_SHOP_VISIT_LATITUDE, latitude);
        cv.put(KEY_SHOP_VISIT_LONGITUDE, longitude);
        cv.put(KEY_SHOP_VISIT_MAPNAME, mapName);
        cv.put(KEY_SHOP_VISIT_UPDATE, update);
        cv.put(KEY_SHOP_VISIT_SELECTED_DISTRIBUTOR_ID, distributorId);


        long insertedPaymentRecieved = db.insert(DATABASE_TABLE_SHOP_VISIT, null, cv);
        return insertedPaymentRecieved;

    }

    public long createShopCheckIn(String shopVisitId, String shopID, String remarks, String reasonId, String dateTime, String distributorId, int update) {

        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(shopVisitId);

        cv.put(KEY_SHOP_VISIT_ID, i);
        cv.put(KEY_SHOP_VISIT_CUST_ID, shopID);
        cv.put(KEY_SHOP_VISIT_REASON_ID, reasonId);
        cv.put(KEY_SHOP_VISIT_DATETIME, dateTime);
        cv.put(KEY_SHOP_VISIT_REMARKS, remarks);
        /*cv.put(KEY_SHOP_VISIT_LATITUDE, latitude);
        cv.put(KEY_SHOP_VISIT_LONGITUDE, longitude);
        cv.put(KEY_SHOP_VISIT_MAPNAME, mapName);*/
        cv.put(KEY_SHOP_VISIT_UPDATE, update);
        cv.put(KEY_SHOP_VISIT_SELECTED_DISTRIBUTOR_ID, distributorId);


        long insertedPaymentRecieved = db.insert(DATABASE_TABLE_SHOP_VISIT, null, cv);
        return insertedPaymentRecieved;

    }

    public ArrayList<HashMap<String, String>> getShopVisit() {
        ArrayList<HashMap<String, String>> shopVisitList;

        shopVisitList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SHOP_VISIT + " WHERE " + KEY_SHOP_VISIT_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_SHOP_VISIT_ID);
        int iCustId = cursor.getColumnIndex(KEY_SHOP_VISIT_CUST_ID);
        int iReasonId = cursor.getColumnIndex(KEY_SHOP_VISIT_REASON_ID);
        int iStartDateTime = cursor.getColumnIndex(KEY_SHOP_VISIT_START_DATETIME);
        int iDateTime = cursor.getColumnIndex(KEY_SHOP_VISIT_DATETIME);
        int iremarks = cursor.getColumnIndex(KEY_SHOP_VISIT_REMARKS);
        int iLati = cursor.getColumnIndex(KEY_SHOP_VISIT_LATITUDE);
        int iLongi = cursor.getColumnIndex(KEY_SHOP_VISIT_LONGITUDE);
        int iMapName = cursor.getColumnIndex(KEY_SHOP_VISIT_MAPNAME);
        int iSelectedDistributor = cursor.getColumnIndex(KEY_SHOP_VISIT_SELECTED_DISTRIBUTOR_ID);


        try {

            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("sv_id", cursor.getString(iId));
                    map.put("sv_cust_id", cursor.getString(iCustId));
                    map.put("sv_reason_id", cursor.getString(iReasonId));
                    map.put("sv_start_datetime", cursor.getString(iStartDateTime));
                    map.put("sv_datetime", cursor.getString(iDateTime));
                    map.put("sv_remarks", cursor.getString(iremarks));
                    map.put("sv_latitude", cursor.getString(iLati));
                    map.put("sv_longitude", cursor.getString(iLongi));
                    map.put("sv_mapName", cursor.getString(iMapName));
                    map.put("sv_selected_distributor_id", cursor.getString(iSelectedDistributor));


                    shopVisitList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }

        }

        return shopVisitList;

    }

    public ArrayList<HashMap<String, String>> getShopVisitList() {
        ArrayList<HashMap<String, String>> shopVisitList;

        shopVisitList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SHOP_VISIT + " ORDER BY " + KEY_SHOP_VISIT_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        int iId = cursor.getColumnIndex(KEY_SHOP_VISIT_ID);
        int iCustId = cursor.getColumnIndex(KEY_SHOP_VISIT_CUST_ID);
        int iReasonId = cursor.getColumnIndex(KEY_SHOP_VISIT_REASON_ID);
        int iDateTime = cursor.getColumnIndex(KEY_SHOP_VISIT_DATETIME);
        int iremarks = cursor.getColumnIndex(KEY_SHOP_VISIT_REMARKS);
        int iLati = cursor.getColumnIndex(KEY_SHOP_VISIT_LATITUDE);
        int iLongi = cursor.getColumnIndex(KEY_SHOP_VISIT_LONGITUDE);
        int iMapName = cursor.getColumnIndex(KEY_SHOP_VISIT_MAPNAME);


        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("sv_id", cursor.getString(iId));
                    map.put("sv_cust_id", cursor.getString(iCustId));
                    map.put("sv_customerName", getSelectedCustomerCompName(String.valueOf(cursor.getString(iCustId))));
                    map.put("sv_reason_id", cursor.getString(iReasonId));
                    map.put("sv_reasonName", getSelectedReasonName(String.valueOf(cursor.getString(iReasonId))));
                    map.put("sv_datetime", cursor.getString(iDateTime));
                    map.put("sv_remarks", cursor.getString(iremarks));
                    map.put("sv_latitude", cursor.getString(iLati));
                    map.put("sv_longitude", cursor.getString(iLongi));
                    map.put("sv_mapName", cursor.getString(iMapName));
                    
                    
                    shopVisitList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return shopVisitList;
        
    }
    
    public int getMaxIdFromShopVisit() {
        
        String que = "SELECT MAX(" + KEY_SHOP_VISIT_ID + ") FROM " + DATABASE_TABLE_SHOP_VISIT;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        return maxid;
        
    }
    
    public void deleteShopVisit() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SHOP_VISIT;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    
    public long createClockInTime(String id, String clockIn, String clockOut) {
        
        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(id);
        
        cv.put(KEY_CLOCKIN_TIME_ID, i);
        cv.put(KEY_CLOCKIN_TIME_CLOCKIN, clockIn);
        cv.put(KEY_CLOCKIN_TIME_CLOCKOUT, clockOut);
        
        long insertedClockIn = db.insert(DATABASE_TABLE_CLOCKIN_TIME, null, cv);
        return insertedClockIn;
        
    }
    
    public String getTimeInLogic() {
        
        String result = "";
        
        String[] Column = new String[]{KEY_CLOCKIN_TIME_CLOCKIN};
        Cursor c = db.query(DATABASE_TABLE_CLOCKIN_TIME, Column, KEY_CLOCKIN_TIME_CLOCKOUT + " = " +
                "0 OR "+KEY_CLOCKIN_TIME_CLOCKOUT + " = '' ORDER BY " + KEY_CLOCKIN_TIME_ID + " " +
                "DESC LIMIT 1", null, null, null, null);
        
        int iEid = c.getColumnIndex(KEY_CLOCKIN_TIME_CLOCKIN);
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iEid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return result;
    }
    
    public ArrayList<HashMap<String, String>> getClockInTimeList() {
        ArrayList<HashMap<String, String>> clockInTimeList;
        
        clockInTimeList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_CLOCKIN_TIME + " ORDER BY " + KEY_CLOCKIN_TIME_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_CLOCKIN_TIME_ID);
        int iClockIn = cursor.getColumnIndex(KEY_CLOCKIN_TIME_CLOCKIN);
        int iClockOut = cursor.getColumnIndex(KEY_CLOCKIN_TIME_CLOCKOUT);
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("clock_in", cursor.getString(iClockIn));
                    map.put("clock_out", cursor.getString(iClockOut));
                    
                    clockInTimeList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return clockInTimeList;
        
    }
    
    public void deleteClockInTime() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_CLOCKIN_TIME;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    public long createMerchandizing(String id, String shopId, String campaignId, String productId, String datetime, String remarks) {
        
        ContentValues cv = new ContentValues();
        int i = Integer.parseInt(id);
        
        cv.put(KEY_MERCHANDIZING_ID, i);
        cv.put(KEY_MERCHANDIZING_SHOP_ID, shopId);
        cv.put(KEY_MERCHANDIZING_CAMPAIGN_ID, campaignId);
        cv.put(KEY_MERCHANDIZING_PRODUCT_ID, productId);
        cv.put(KEY_MERCHANDIZING_DATETIME, datetime);
        cv.put(KEY_MERCHANDIZING_REMARKS, remarks);
        
        
        long insertedMerchandizing = db.insert(DATABASE_TABLE_MERCHANDIZING, null, cv);
        return insertedMerchandizing;
        
    }
    
    public ArrayList<HashMap<String, String>> getMerchandizingList() {
        ArrayList<HashMap<String, String>> merchandizingList;
        
        merchandizingList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_MERCHANDIZING + " ORDER BY " + KEY_MERCHANDIZING_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_MERCHANDIZING_ID);
        int iShopId = cursor.getColumnIndex(KEY_MERCHANDIZING_SHOP_ID);
        // int iBrandId = cursor.getColumnIndex(KEY_MERCHANDIZING_BRAND_ID);
        int iCampaignId = cursor.getColumnIndex(KEY_MERCHANDIZING_CAMPAIGN_ID);
        
        int iProductId = cursor.getColumnIndex(KEY_MERCHANDIZING_PRODUCT_ID);
        int iDatetime = cursor.getColumnIndex(KEY_MERCHANDIZING_DATETIME);
        int iRemarks = cursor.getColumnIndex(KEY_MERCHANDIZING_REMARKS);
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("m_id", cursor.getString(iId));
                    map.put("m_shopId", cursor.getString(iShopId));
                    map.put("m_shopName", getSelectedCustomerCompName(String.valueOf(cursor.getString(iShopId))));
                    map.put("m_campaignId", cursor.getString(iCampaignId));
                    map.put("m_campaignName", getSelectedCampaignName(String.valueOf(cursor.getString(iCampaignId))));
                    map.put("m_productId", cursor.getString(iProductId));
                    map.put("m_productName", getSelectedProductName(String.valueOf(cursor.getString(iProductId))));
                    map.put("m_datetime", cursor.getString(iDatetime));
                    map.put("m_remarks", cursor.getString(iRemarks));
                    
                    merchandizingList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return merchandizingList;
        
    }
    
    public void deleteMerchandizing() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_MERCHANDIZING;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    //////////
    
    public long createMerchandizingPlan(int planId, String planName, int planProd1, int planProd2, int planProd3) {
        
        ContentValues cv = new ContentValues();
        //int i = Integer.parseInt(id);
        
        cv.put(KEY_MERCHANDIZING_PLAN_ID, planId);
        cv.put(KEY_MERCHANDIZING_PLAN_NAME, planName);
        cv.put(KEY_MERCHANDIZING_PLAN_PRODUCT_1, planProd1);
        cv.put(KEY_MERCHANDIZING_PLAN_PRODUCT_2, planProd2);
        cv.put(KEY_MERCHANDIZING_PLAN_PRODUCT_3, planProd3);
        
        
        long insertedMerchandizing = db.insert(DATABASE_TABLE_MERCHANDIZING_PLAN, null, cv);
        return insertedMerchandizing;
        
    }
    
    public ArrayList<HashMap<String, String>> getMerchandizingPlanFromDB() {
        ArrayList<HashMap<String, String>> planList;
        
        planList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_MERCHANDIZING_PLAN;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        
        int id = cursor.getColumnIndex(KEY_MERCHANDIZING_PLAN_ID);
        int name = cursor.getColumnIndex(KEY_MERCHANDIZING_PLAN_NAME);
        int prod1 = cursor.getColumnIndex(KEY_MERCHANDIZING_PLAN_PRODUCT_1);
        int prod2 = cursor.getColumnIndex(KEY_MERCHANDIZING_PLAN_PRODUCT_2);
        int prod3 = cursor.getColumnIndex(KEY_MERCHANDIZING_PLAN_PRODUCT_3);
        
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("plan_id", String.valueOf(cursor.getInt(id)));
                    map.put("plan_name", cursor.getString(name));
                    map.put("plan_prod1", cursor.getString(prod1));
                    map.put("plan_prod2", cursor.getString(prod2));
                    map.put("plan_prod3", cursor.getString(prod3));
                    
                    
                    planList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return planList;
        
    }
    
    public void deleteMerchandizingPlan() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_MERCHANDIZING_PLAN;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    /////////////
    
    ///////////////
    
    public long createTown(int townId, String townName) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_TOWN_ID, townId);
        cv.put(KEY_TOWN_NAME, townName);
        //cv.put(KEY_BRAND_PUBLISHED, 1);
        
        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);
        
        
        long insertId = db.insert(DATABASE_TABLE_TOWN, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getTownFromDB() {
        ArrayList<HashMap<String, String>> returnReasonList;
        
        returnReasonList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_TOWN;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        
        int id = cursor.getColumnIndex(KEY_TOWN_ID);
        int name = cursor.getColumnIndex(KEY_TOWN_NAME);
        //int iDate = cursor.getColumnIndex(KEY_OFFLINE_DATETIME);
        // int iArea = cursor.getColumnIndex(KEY_OFFLINE_AREA);
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("town_id", cursor.getString(id));
                    map.put("town_name", cursor.getString(name));
                    
                    returnReasonList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return returnReasonList;
        
    }
    
    public ArrayList<HashMap<String, String>> getTown2FromDB(int town) {
        ArrayList<HashMap<String, String>> returnReasonList;
        
        returnReasonList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_TOWN + " WHERE " + KEY_TOWN_ID + " NOT IN (" + town + ")";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        
        int id = cursor.getColumnIndex(KEY_TOWN_ID);
        int name = cursor.getColumnIndex(KEY_TOWN_NAME);
        //int iDate = cursor.getColumnIndex(KEY_OFFLINE_DATETIME);
        // int iArea = cursor.getColumnIndex(KEY_OFFLINE_AREA);
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("town_id", cursor.getString(id));
                    map.put("town_name", cursor.getString(name));
                    
                    returnReasonList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return returnReasonList;
        
    }
    
    public void deleteTown() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_TOWN;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    private String getSelectedTownName(String id) {
        
        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_TOWN_NAME};
        Cursor c = db.query(DATABASE_TABLE_TOWN, Column, KEY_TOWN_ID + " = " + idInt, null, null, null, null);
        
        String result = "";
        int iComp = c.getColumnIndex(KEY_TOWN_NAME);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return result;
        
    }
    ///////////
    
    
    //////////////
    public long createTownTravel(int townTravelId, int town1, int town2, String oneWay, String twoWay, String stayAccom) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_TOWN_TRAVEL_ID, townTravelId);
        cv.put(KEY_TOWN_TRAVEL_TOWN_1, town1);
        cv.put(KEY_TOWN_TRAVEL_TOWN_2, town2);
        cv.put(KEY_TOWN_TRAVEL_ONE_WAY, oneWay);
        cv.put(KEY_TOWN_TRAVEL_TWO_WAY, twoWay);
        cv.put(KEY_TOWN_TRAVEL_STAY_ACCOM, stayAccom);
        
        //cv.put(KEY_BRAND_PUBLISHED, 1);
        
        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);
        
        
        long insertId = db.insert(DATABASE_TABLE_TOWN_TRAVEL, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public void deleteTownTravel() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_TOWN_TRAVEL;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    public ArrayList<HashMap<String, String>> getTownTravelFromDB(String town1, String town2) {
        ArrayList<HashMap<String, String>> townTravelList;
        
        townTravelList = new ArrayList<>();
        
        String selectQuery = "SELECT  " + KEY_TOWN_TRAVEL_ONE_WAY + ", " + KEY_TOWN_TRAVEL_TWO_WAY + ", " + KEY_TOWN_TRAVEL_STAY_ACCOM + " FROM " + DATABASE_TABLE_TOWN_TRAVEL + " WHERE " + KEY_TOWN_TRAVEL_TOWN_1 + " = " + town1 + " AND " + KEY_TOWN_TRAVEL_TOWN_2 + " = " + town2;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        
        int oneWay = cursor.getColumnIndex(KEY_TOWN_TRAVEL_ONE_WAY);
        int twoWay = cursor.getColumnIndex(KEY_TOWN_TRAVEL_TWO_WAY);
        int stayAccom = cursor.getColumnIndex(KEY_TOWN_TRAVEL_STAY_ACCOM);
        // int iArea = cursor.getColumnIndex(KEY_OFFLINE_AREA);
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("oneWay", cursor.getString(oneWay));
                    map.put("twoWay", cursor.getString(twoWay));
                    map.put("stayAccom", cursor.getString(stayAccom));
                    
                    townTravelList.add(map);
                } while (cursor.moveToNext());
            }else
                {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("oneWay","0");
                    map.put("twoWay","0");
                    map.put("stayAccom", "0");

                    townTravelList.add(map);
                }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return townTravelList;
        
    }
    /////////////
    
    /////////////
    public long createTravelExpense(int id, int fromTown, int toTown, String fromDate, String toDate, String days, String amount, String travellingAmount, String stayCompensationAmount, String remarks, String startTime, String datetime, double latitude, double longitude, String mapName, int update) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_TRAVEL_EXPENSE_ID, id);
        cv.put(KEY_TRAVEL_EXPENSE_FROM_TOWN, fromTown);
        cv.put(KEY_TRAVEL_EXPENSE_TO_TOWN, toTown);
        cv.put(KEY_TRAVEL_EXPENSE_FROM_DATE, fromDate);
        cv.put(KEY_TRAVEL_EXPENSE_TO_DATE, toDate);
        cv.put(KEY_TRAVEL_EXPENSE_DAYS, days);
        cv.put(KEY_TRAVEL_EXPENSE_AMOUNT, amount);
        cv.put(KEY_TRAVEL_EXPENSE_TRAVELLING_AMOUNT, travellingAmount);
        cv.put(KEY_TRAVEL_EXPENSE_STAY_COMPENSATION_AMOUNT, stayCompensationAmount);
        cv.put(KEY_TRAVEL_EXPENSE_REMARKS, remarks);
        cv.put(KEY_TRAVEL_EXPENSE_START_DATETIME, startTime);
        cv.put(KEY_TRAVEL_EXPENSE_DATETIME, datetime);
        cv.put(KEY_TRAVEL_EXPENSE_LATITUDE, latitude);
        cv.put(KEY_TRAVEL_EXPENSE_LONGITUDE, longitude);
        cv.put(KEY_TRAVEL_EXPENSE_MAPNAME, mapName);
        cv.put(KEY_TRAVEL_EXPENSE_STATUS, 1);
        cv.put(KEY_TRAVEL_EXPENSE_UPDATE, update);
        
        
        //cv.put(KEY_BRAND_PUBLISHED, 1);
        
        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);
        
        
        long insertId = db.insert(DATABASE_TABLE_TRAVEL_EXPENSE, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public int getMaxIdFromTravelExpense() {
        
        String que = "SELECT MAX(" + KEY_TRAVEL_EXPENSE_ID + ") FROM " + DATABASE_TABLE_TRAVEL_EXPENSE;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        c.close();
        return maxid;
        
    }
    
    public ArrayList<HashMap<String, String>> getTravelExpenseList() {
        ArrayList<HashMap<String, String>> expenseList;
        
        expenseList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_TRAVEL_EXPENSE + " ORDER BY " + KEY_TRAVEL_EXPENSE_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iFromTown = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_FROM_TOWN);
        int iToTown = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_TO_TOWN);
        int iDays = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_DAYS);
        int iAmount = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_AMOUNT);
        int iStatus = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_STATUS);
        int iDatetime = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_DATETIME);

        /*int iLati = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iLongi = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iStatus = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        */
        
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("t_exp_id", cursor.getString(iId));
                    map.put("t_exp_from_town", cursor.getString(iFromTown));
                    map.put("t_exp_from_town_name", getSelectedTownName(cursor.getString(iFromTown)));
                    
                    map.put("t_exp_to_town", cursor.getString(iToTown));
                    map.put("t_exp_to_town_name", getSelectedTownName(cursor.getString(iToTown)));
                    
                    //map.put("exp_typeName",getSelectedExpenseTypeName(String.valueOf(cursor.getString(iExpType))));
                    map.put("t_exp_days", cursor.getString(iDays));
                    map.put("t_exp_amount", cursor.getString(iAmount));
                    map.put("t_exp_status", cursor.getString(iStatus));
                    map.put("t_exp_datetime", cursor.getString(iDatetime));
                    
                    map.put("t_exp_statusName", getSelectedExpenseStatusName(String.valueOf(cursor.getString(iStatus))));
                    
                    
                    expenseList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return expenseList;
        
    }
    
    public ArrayList<HashMap<String, String>> getTravelExpense() {
        ArrayList<HashMap<String, String>> expenseList;
        
        expenseList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_TRAVEL_EXPENSE + " WHERE " + KEY_TRAVEL_EXPENSE_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iFromTown = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_FROM_TOWN);
        int iToTown = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_TO_TOWN);
        int iFromDate = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_FROM_DATE);
        int iToDate = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_TO_DATE);
        int iDays = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_DAYS);
        int iAmount = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_AMOUNT);
        int iTravellingAmount = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_TRAVELLING_AMOUNT);
        int iStayCompensationAmount = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_STAY_COMPENSATION_AMOUNT);
        int iStartDateTime = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_START_DATETIME);
        int iDateTime = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_DATETIME);
        int iremarks = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_REMARKS);
        int iLati = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_LATITUDE);
        int iLongi = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_LONGITUDE);
        int iMapName = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_MAPNAME);
        int iStatus = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_STATUS);
        
        
        try {
            
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("t_exp_id", cursor.getString(iId));
                    map.put("t_exp_from_town", cursor.getString(iFromTown));
                    map.put("t_exp_to_town", cursor.getString(iToTown));
                    map.put("t_exp_from_date", cursor.getString(iFromDate));
                    map.put("t_exp_to_date", cursor.getString(iToDate));
                    map.put("t_exp_days", cursor.getString(iDays));
                    map.put("t_exp_amount", cursor.getString(iAmount));
                    map.put("t_exp_travelling_amount", cursor.getString(iTravellingAmount));
                    map.put("t_exp_stay_compensation_amount", cursor.getString(iStayCompensationAmount));
                    map.put("t_exp_start_datetime", cursor.getString(iStartDateTime));
                    map.put("t_exp_datetime", cursor.getString(iDateTime));
                    map.put("t_exp_remarks", cursor.getString(iremarks));
                    map.put("t_exp_latitude", cursor.getString(iLati));
                    map.put("t_exp_longitude", cursor.getString(iLongi));
                    map.put("t_exp_mapName", cursor.getString(iMapName));
                    map.put("t_exp_status", cursor.getString(iStatus));
                    
                    
                    expenseList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return expenseList;
        
    }
    
    ///////////
    
    //////////////
    
    public long createTarget(int productId, String quantity) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_TARGET_PRODUCT_ID, productId);
        cv.put(KEY_TARGET_QUANTITY, quantity);
        
        
        //cv.put(KEY_BRAND_PUBLISHED, 1);
        
        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);
        
        
        long insertId = db.insert(DATABASE_TABLE_TARGET, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public void deleteTarget() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_TARGET;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    
    ///////////////
    
    ///////////////
    
    public long createSupportStatus(int statusId, String statusName) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_SUPPORT_STATUS_ID, statusId);
        cv.put(KEY_SUPPORT_STATUS_NAME, statusName);
        //cv.put(KEY_BRAND_PUBLISHED, 1);
        
        //db.insert(DATABASE_TABLE_CUSTOMER_DETAILS, null, cv);
        
        
        long insertId = db.insert(DATABASE_TABLE_SUPPORT_STATUS, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public void deleteSupportStatus() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SUPPORT_STATUS;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    private String getSupportStatusName(String id) {
        
        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_SUPPORT_STATUS_NAME};
        Cursor c = db.query(DATABASE_TABLE_SUPPORT_STATUS, Column, KEY_SUPPORT_STATUS_ID + " = " + idInt, null, null, null, null);
        
        String result = "";
        int iComp = c.getColumnIndex(KEY_SUPPORT_STATUS_NAME);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return result;
        
    }
    
    public ArrayList<HashMap<String, String>> getSupportStatusFromDB() {
        ArrayList<HashMap<String, String>> supportStatusList;
        
        supportStatusList = new ArrayList<>();
        
        String selectQuery = "SELECT  * FROM " + DATABASE_TABLE_SUPPORT_STATUS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        
        int id = cursor.getColumnIndex(KEY_SUPPORT_STATUS_ID);
        int name = cursor.getColumnIndex(KEY_SUPPORT_STATUS_NAME);
        //int iDate = cursor.getColumnIndex(KEY_OFFLINE_DATETIME);
        // int iArea = cursor.getColumnIndex(KEY_OFFLINE_AREA);
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("s_status_id", cursor.getString(id));
                    map.put("s_status_name", cursor.getString(name));
                    
                    supportStatusList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return supportStatusList;
        
    }
    //////////////
    
    
    ///////////////
    
    //////////////
    public long createSupport(int id, String title, int status, String datetime, int syncUpdate, String netOid) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_SUPPORT_ID, id);
        cv.put(KEY_SUPPORT_TITLE, title);
        cv.put(KEY_SUPPORT_STATUS, status);
        cv.put(KEY_SUPPORT_DATETIME, datetime);
        cv.put(KEY_SUPPORT_SYNC_UPDATE, syncUpdate);
        if (netOid == null) {
            cv.put(KEY_SUPPORT_NET_OID, 0);
        } else {
            cv.put(KEY_SUPPORT_NET_OID, netOid);
        }
        
        long insertId = db.insert(DATABASE_TABLE_SUPPORT, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getSupport() {
        ArrayList<HashMap<String, String>> supportList;
        
        supportList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SUPPORT + " WHERE " + KEY_SUPPORT_SYNC_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_SUPPORT_ID);
        int iTitle = cursor.getColumnIndex(KEY_SUPPORT_TITLE);
        int iStatus = cursor.getColumnIndex(KEY_SUPPORT_STATUS);
        int iDatetime = cursor.getColumnIndex(KEY_SUPPORT_DATETIME);
        int iNetOid = cursor.getColumnIndex(KEY_SUPPORT_NET_OID);
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("support_id", cursor.getString(iId));
                    map.put("support_title", cursor.getString(iTitle));
                    map.put("support_status", cursor.getString(iStatus));
                    //map.put("support_status_name", getSupportStatusName(String.valueOf(cursor.getString(iStatus))));
                    map.put("support_datetime", cursor.getString(iDatetime));
                    map.put("support_net_oid", cursor.getString(iNetOid));
                    
                    
                    supportList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return supportList;
        
    }
    
    public int getMaxIdFromSupport() {
        
        String que = "SELECT MAX(" + KEY_SUPPORT_ID + ") FROM " + DATABASE_TABLE_SUPPORT;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        return maxid;
        
    }
    
    public int getSyncFromSupport(String id) {
        
        int idInt = Integer.parseInt(id);
        
        String que = "SELECT " + KEY_SUPPORT_SYNC_UPDATE + " FROM " + DATABASE_TABLE_SUPPORT + " WHERE " + KEY_SUPPORT_ID + " = " + idInt;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        return maxid;
        
    }
    
    public String getSupportTitleById(String id) {
        
        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_SUPPORT_TITLE};
        Cursor c = db.query(DATABASE_TABLE_SUPPORT, Column, KEY_SUPPORT_ID + " = " + idInt, null, null, null, null);
        
        String result = "";
        int iComp = c.getColumnIndex(KEY_SUPPORT_TITLE);
        
        try {
            
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = result + c.getString(iComp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return result;
        
    }
    
    public ArrayList<HashMap<String, String>> getSupportList() {
        ArrayList<HashMap<String, String>> supportList;
        
        supportList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SUPPORT + " ORDER BY " + KEY_SUPPORT_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_SUPPORT_ID);
        int iTitle = cursor.getColumnIndex(KEY_SUPPORT_TITLE);
        int iStatus = cursor.getColumnIndex(KEY_SUPPORT_STATUS);
        int iDatetime = cursor.getColumnIndex(KEY_SUPPORT_DATETIME);


        /*int iLati = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iLongi = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iStatus = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        */
        
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("support_id", cursor.getString(iId));
                    map.put("support_title", cursor.getString(iTitle));
                    map.put("support_status", cursor.getString(iStatus));
                    map.put("support_status_name", getSupportStatusName(String.valueOf(cursor.getString(iStatus))));
                    map.put("support_datetime", cursor.getString(iDatetime));
                    
                    
                    supportList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return supportList;
        
    }
    
    public ArrayList<String> getSelectedSupportID() {
        // TODO Auto-generated method stub
        
        String[] Columns = new String[]{KEY_SUPPORT_ID};
        Cursor c = db.query(DATABASE_TABLE_SUPPORT, Columns, null, null, null, null, KEY_SUPPORT_DATETIME + " DESC");
        ArrayList<String> resultId = new ArrayList<>();
        
        int iId = c.getColumnIndex(KEY_SUPPORT_ID);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                resultId.add("" + c.getInt(iId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return resultId;
    }
    
    public void deleteSupport() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SUPPORT;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    //////////////
    
    //////////////
    //////////////
    
    //////////////
    public long createSupportDetail(int id, int supportId, String message, String datetime, int person, int syncUpdate) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_SUPPORT_DETAIL_ID, id);
        cv.put(KEY_SUPPORT_DETAIL_SUPPORT_ID, supportId);
        cv.put(KEY_SUPPORT_DETAIL_MESSAGE, message);
        cv.put(KEY_SUPPORT_DETAIL_DATETIME, datetime);
        cv.put(KEY_SUPPORT_DETAIL_PERSON, person);
        cv.put(KEY_SUPPORT_DETAIL_SYNC_UPDATE, syncUpdate);
        
        
        long insertId = db.insert(DATABASE_TABLE_SUPPORT_DETAIL, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getSupportDetail() {
        ArrayList<HashMap<String, String>> supportList;
        
        supportList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SUPPORT_DETAIL + " WHERE " + KEY_SUPPORT_DETAIL_SYNC_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_ID);
        int iSupportId = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_SUPPORT_ID);
        int iMsg = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_MESSAGE);
        int iPerson = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_PERSON);
        int iDatetime = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_DATETIME);
        
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("support_detail_id", cursor.getString(iId));
                    map.put("support_detail_support_id", cursor.getString(iSupportId));
                    map.put("support_detail_message", cursor.getString(iMsg));
                    map.put("support_detail_person", cursor.getString(iPerson));
                    map.put("support_detail_datetime", cursor.getString(iDatetime));
                    
                    
                    supportList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return supportList;
        
    }
    
    public int getMaxIdFromSupportDetail() {
        
        String que = "SELECT MAX(" + KEY_SUPPORT_DETAIL_ID + ") FROM " + DATABASE_TABLE_SUPPORT_DETAIL;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        
        return maxid;
        
    }
    
    public ArrayList<HashMap<String, String>> getSupportDetailList(String supportId) {
        ArrayList<HashMap<String, String>> supportList;
        
        supportList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SUPPORT_DETAIL + " WHERE " + KEY_SUPPORT_DETAIL_SUPPORT_ID + " = " + supportId;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_ID);
        int iSupId = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_SUPPORT_ID);
        int iMsg = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_MESSAGE);
        int iPerson = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_PERSON);
        int iDatetime = cursor.getColumnIndex(KEY_SUPPORT_DETAIL_DATETIME);


        /*int iLati = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iLongi = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        int iStatus = cursor.getColumnIndex(KEY_TRAVEL_EXPENSE_ID);
        */
        
        try {
            
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("support_detail_id", cursor.getString(iId));
                    map.put("support_detail_support_id", cursor.getString(iSupId));
                    
                    map.put("support_detail_message", cursor.getString(iMsg));
                    map.put("support_detail_person", cursor.getString(iPerson));
                    //map.put("support_status_name", getSupportStatusName(String.valueOf(cursor.getString(iStatus))));
                    map.put("support_detail_datetime", cursor.getString(iDatetime));
                    map.put("user_id", getMobEmpId());
                    map.put("user", getMobFName());
                    
                    
                    supportList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return supportList;
        
    }
    
    public void deleteSupportDetail() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SUPPORT_DETAIL;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    //////////////
    
    /////////////
    
    //////////////
    
    public long createCommitment(int id, int shopId, String fromDate, String toDate, String saleAmount, String giftAmount, String remarks, String datetime, int status, int done, int update) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_COMMITMENT_ID, id);
        cv.put(KEY_COMMITMENT_SHOP_ID, shopId);
        cv.put(KEY_COMMITMENT_FROM_DATE, fromDate);
        cv.put(KEY_COMMITMENT_TO_DATE, toDate);
        cv.put(KEY_COMMITMENT_SALE_AMOUNT, saleAmount);
        cv.put(KEY_COMMITMENT_GIFT_AMOUNT, giftAmount);
        cv.put(KEY_COMMITMENT_REMARKS, remarks);
        cv.put(KEY_COMMITMENT_DATETIME, datetime);
        cv.put(KEY_COMMITMENT_STATUS, status);
        cv.put(KEY_COMMITMENT_DONE, done);
        cv.put(KEY_COMMITMENT_UPDATE, update);
        
        
        long insertId = db.insert(DATABASE_TABLE_COMMITMENT, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getCommitment() {
        ArrayList<HashMap<String, String>> commList;
        
        commList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_COMMITMENT + " WHERE " + KEY_COMMITMENT_UPDATE + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_COMMITMENT_ID);
        int iCustId = cursor.getColumnIndex(KEY_COMMITMENT_SHOP_ID);
        int iFromDate = cursor.getColumnIndex(KEY_COMMITMENT_FROM_DATE);
        int iToDate = cursor.getColumnIndex(KEY_COMMITMENT_TO_DATE);
        int iDateTime = cursor.getColumnIndex(KEY_COMMITMENT_DATETIME);
        int iSaleAmt = cursor.getColumnIndex(KEY_COMMITMENT_SALE_AMOUNT);
        int iGiftAmt = cursor.getColumnIndex(KEY_COMMITMENT_GIFT_AMOUNT);
        int iRemarks = cursor.getColumnIndex(KEY_COMMITMENT_REMARKS);
        int iStatus = cursor.getColumnIndex(KEY_COMMITMENT_STATUS);
        int iDone = cursor.getColumnIndex(KEY_COMMITMENT_DONE);
        
        
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                map.put("comm_id", cursor.getString(iId));
                map.put("comm_datetime", cursor.getString(iDateTime));
                map.put("comm_customer_id", cursor.getString(iCustId));
                //map.put("comm_customer_name", getSelectedCustomerCompName(cursor.getString(iCustId)));
                map.put("comm_from_date", cursor.getString(iFromDate));
                map.put("comm_to_date", cursor.getString(iToDate));
                map.put("comm_sale_amount", cursor.getString(iSaleAmt));
                map.put("comm_gift_amount", cursor.getString(iGiftAmt));
                map.put("comm_remarks", cursor.getString(iRemarks));
                map.put("comm_status", cursor.getString(iStatus));
                map.put("comm_done", cursor.getString(iDone));
                
                commList.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return commList;
        
    }
    
    public int getMaxIdFromCommitment() {
        
        String que = "SELECT MAX(" + KEY_COMMITMENT_ID + ") FROM " + DATABASE_TABLE_COMMITMENT;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        
        return maxid;
        
    }
    
    public List<String> getCommitmentIDForDropDown(int shopId) {
        
        
        List<String> ItemID = new ArrayList<>();
        
        String[] Column = new String[]{KEY_COMMITMENT_ID};
        Cursor c = db.query(DATABASE_TABLE_COMMITMENT, Column, KEY_COMMITMENT_SHOP_ID + " = " + shopId, null, null, null, null);
        
        int iItemName = c.getColumnIndex(KEY_COMMITMENT_ID);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemID.add(c.getString(iItemName));
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        return ItemID;
    }
    
    public List<String> getCommitmentForDropDown(int shopId) {
        
        
        List<String> ItemNames = new ArrayList<>();
        
        String[] Column = new String[]{KEY_COMMITMENT_ID, KEY_COMMITMENT_SALE_AMOUNT, KEY_COMMITMENT_STATUS};
        Cursor c = db.query(DATABASE_TABLE_COMMITMENT, Column, KEY_COMMITMENT_SHOP_ID + " = " + shopId, null, null, null, null);
        
        int iId = c.getColumnIndex(KEY_COMMITMENT_ID);
        int iSaleAmt = c.getColumnIndex(KEY_COMMITMENT_SALE_AMOUNT);
        int iStatus = c.getColumnIndex(KEY_COMMITMENT_STATUS);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iId) + " | " + c.getString(iSaleAmt) + " | " + getSelectedExpenseStatusName(String.valueOf(c.getString(iStatus))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        return ItemNames;
    }
    
    public ArrayList<HashMap<String, String>> getCommitmentList() {
        ArrayList<HashMap<String, String>> commList;
        
        commList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_COMMITMENT + " ORDER BY " + KEY_COMMITMENT_DATETIME + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_COMMITMENT_ID);
        int iCustId = cursor.getColumnIndex(KEY_COMMITMENT_SHOP_ID);
        int iDateTime = cursor.getColumnIndex(KEY_COMMITMENT_DATETIME);
        int iSaleAmt = cursor.getColumnIndex(KEY_COMMITMENT_SALE_AMOUNT);
        int iGiftAmt = cursor.getColumnIndex(KEY_COMMITMENT_GIFT_AMOUNT);
        
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("comm_id", cursor.getString(iId));
                    map.put("comm_datetime", cursor.getString(iDateTime));
                    map.put("comm_customer_id", cursor.getString(iCustId));
                    map.put("comm_customer_name", getSelectedCustomerCompName(cursor.getString(iCustId)));
                    map.put("comm_sale_amount", cursor.getString(iSaleAmt));
                    map.put("comm_gift_amount", cursor.getString(iGiftAmt));
                    
                    commList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        return commList;
        
    }
    
    ///////////////
    
    
    ////////////// TOTAL_DISCOUNT TABLE methods STARTS here
    
    
    public long createTotalDiscount(int id, double value, double discount) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_TOTAL_DISCOUNT_ID, id);
        cv.put(KEY_TOTAL_DISCOUNT_VALUE, value);
        cv.put(KEY_TOTAL_DISCOUNT_DISCOUNT, discount);
        
        long insertId = db.insert(DATABASE_TABLE_TOTAL_DISCOUNT, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public double getDiscountForSaleOrder(double totalAmount) {
        
        String result = "";
        
        String[] Column = new String[]{KEY_TOTAL_DISCOUNT_DISCOUNT, KEY_TOTAL_DISCOUNT_VALUE};
        Cursor c = db.query(DATABASE_TABLE_TOTAL_DISCOUNT, Column, totalAmount + " >= " + KEY_TOTAL_DISCOUNT_VALUE, null, null, null, KEY_TOTAL_DISCOUNT_VALUE + " DESC");
        
        int iData = c.getColumnIndex(KEY_TOTAL_DISCOUNT_DISCOUNT);
        
        double maxid = (c.moveToFirst() ? c.getDouble(0) : 0);
        
        if (!c.isClosed())
            c.close();
        
        return maxid;
    }
    
    public void deleteTotalDiscount() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_TOTAL_DISCOUNT;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    
    ///////////// TOTAL_DISCOUNT TABLE methods ENDS here
    
    
    //////////////
    
    
    ////////////// APP_SETTINGS TABLE methods STARTS here
    
    
    public long createAppSetting(int id, String key, int value) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_APP_SETTINGS_ID, id);
        cv.put(KEY_APP_SETTINGS_KEY, key);
        cv.put(KEY_APP_SETTINGS_VALUE, value);
        
        long insertId = db.insert(DATABASE_TABLE_APP_SETTINGS, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    
    public int getAppSettingsValueByKey(String key) {
        
        //int idInt = Integer.parseInt(id);
        //String updateQuery = "Update " + Database_Table_mUser + " set " + KEY_Emp_LastSync + " = '" + getCurrentTime() + "'";
        
        String[] Column = new String[]{KEY_APP_SETTINGS_VALUE};
        Cursor c = db.query(DATABASE_TABLE_APP_SETTINGS, Column, KEY_APP_SETTINGS_KEY + " = '" + key + "'", null, null, null, null);
        
        //String result = "";
        //int iValue = c.getColumnIndex(KEY_APP_SETTINGS_VALUE);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        
        
        return maxid;
        
    }
    
    public void deleteAppSettings() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_APP_SETTINGS;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    
    ///////////// APP_Settings TABLE methods ENDS here


//////////////
    
    
    ///////////////////
    
    /*
     *Shop Category Start
     */
    
    public long createShopCategory(String id, String name) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_SHOP_CATEGORY_ID, id);
        cv.put(KEY_SHOP_CATEGORY_NAME, name);
        
        
        long insertId = db.insert(DATABASE_TABLE_SHOP_CATEGORY, null, cv); //Gives Last Autoincremented
        // Inserted value ID
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getShopCategoryForDropDown() {
        
        
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SHOP_CATEGORY + ";";
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        int iItemId = c.getColumnIndex(KEY_SHOP_CATEGORY_ID);
        int iItemName = c.getColumnIndex(KEY_SHOP_CATEGORY_NAME);
        
        try {
            if (c.moveToFirst()) {
                do {
                    
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", c.getString(iItemId));
                    map.put("name", c.getString(iItemName));
                    
                    data.add(map);
                    
                    
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        c.close();
        
        return data;
    }
    public ArrayList getShopCategory() {


        ArrayList data = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SHOP_CATEGORY + ";";

        Cursor c = db.rawQuery(selectQuery, null);


        int iItemName = c.getColumnIndex(KEY_SHOP_CATEGORY_NAME);

        try {
            if (c.moveToFirst()) {
                do {



                    data.add(c.getString(iItemName));


                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        c.close();

        return data;
    }
     public ArrayList getShopSubCategory() {


        ArrayList data = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY + ";";

        Cursor c = db.rawQuery(selectQuery, null);

        int iItemId = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_ID);
        int iItemName = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_NAME);

        try {
            if (c.moveToFirst()) {
                do {



                    data.add(c.getString(iItemName));


                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        c.close();

        return data;
    }
   public ArrayList getShopSubCategoryByCategory(String CategoryID) {


        ArrayList data = new ArrayList<>();


        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY + " where "+KEY_SUB_SHOP_CATEGORY_CATEGORY+" = "+ CategoryID+" ;";

        Cursor c = db.rawQuery(selectQuery, null);

        int iItemId = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_ID);
        int iItemName = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_NAME);

        try {
            if (c.moveToFirst()) {
                do {



                    data.add(c.getString(iItemName));


                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        c.close();

        return data;
    }

    public String getShopCategoryNameById(String id) {
        
        
        String name = "";
        
        String selectQuery =
                "SELECT " + KEY_SHOP_CATEGORY_NAME + " FROM " + DATABASE_TABLE_SHOP_CATEGORY + " " +
                        "where " + KEY_SHOP_CATEGORY_ID + " = " + id + ";";
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        int iItemName = c.getColumnIndex(KEY_SHOP_CATEGORY_NAME);
        
        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        c.close();
        
        
        return name;
    }
    public String getShopCategoryId(String id) {


        String name = "0";

        String selectQuery =
                "SELECT " + KEY_SHOP_CATEGORY_ID + " FROM " + DATABASE_TABLE_SHOP_CATEGORY + " " +
                        "where " +  KEY_SHOP_CATEGORY_NAME+ " = '" + id + "';";

        Cursor c = db.rawQuery(selectQuery, null);

        int iItemName = c.getColumnIndex(KEY_SHOP_CATEGORY_ID);

        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);


            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        c.close();


        return name;
    }
      public String getShopSubCategoryId(String id) {


        String name = "0";

        String selectQuery =
                "SELECT " + KEY_SUB_SHOP_CATEGORY_ID + " FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY + " " +
                        "where " +  KEY_SUB_SHOP_CATEGORY_NAME+ " = '" + id + "';";

        Cursor c = db.rawQuery(selectQuery, null);

        int iItemName = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_ID);

        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);


            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        c.close();


        return name;
    }

    public String getShopCategoryNameByCustId(String id) {
        
        
        String name = "";
        
        String sub = "SELECT shop_cat_id from customer where " + KEY_ID_CUSTOMER + " = " + id;
        String selectQuery =
                "SELECT " + KEY_SHOP_CATEGORY_NAME + " FROM " + DATABASE_TABLE_SHOP_CATEGORY + " " +
                        "where " + KEY_SHOP_CATEGORY_ID + " = (" + sub + ");";
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        int iItemName = c.getColumnIndex(KEY_SHOP_CATEGORY_NAME);
        
        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        c.close();
        
        
        return name;
    }
     public String getShopCategoryIDByCustId(String id) {
        
        
        String name = "0";
        
        String sub = "SELECT shop_cat_id from customer where " + KEY_ID_CUSTOMER + " = " + id;
    
        
        Cursor c = db.rawQuery(sub, null);
        
        int iItemName = c.getColumnIndex(KEY_CUSTOMER_SHOP_CATEGORY_ID);

        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        c.close();
        
        
        return name;
    }
    public boolean getTodaysSalesOrderByShop(String id) {




        String sub = "SELECT "+KEY_SALES_ORDER_ID+" from "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" where " +
                KEY_SALES_ORDER_CUSTOMER_ID + " = " + id+
                " AND "+KEY_SALES_ORDER_EMPLOYEE_ID+" = '"+getMobEmpId()+
                "'   AND  strftime('%Y',"+KEY_SALES_ORDER_DATETIME+") = strftime('%Y',date('now'))" +
                " AND  strftime('%m',"+KEY_SALES_ORDER_DATETIME+") = strftime('%m',date('now'))" +
                " AND  strftime('%d',"+KEY_SALES_ORDER_DATETIME+") = strftime('%d',date('now'))";


        Cursor c = db.rawQuery(sub, null);




            if (c.moveToFirst()) {

                c.close();
              return true;


            }
            else{

                c.close();
                return false;
            }




    }
    public boolean getTodaysShopVisitByShop(String id) {



        String sub = "SELECT "+KEY_SHOP_VISIT_ID+" from "+DATABASE_TABLE_SHOP_VISIT+" where " +
                KEY_SHOP_VISIT_CUST_ID + " = '" + id+

                "'   AND  strftime('%Y',"+KEY_SHOP_VISIT_DATETIME+") = strftime('%Y',date('now'))" +
                "AND  strftime('%m',"+KEY_SHOP_VISIT_DATETIME+") = strftime('%m',date('now'))" +
                "AND  strftime('%d',"+KEY_SHOP_VISIT_DATETIME+") = strftime('%d',date('now'))";


        Cursor c = db.rawQuery(sub, null);



        if (c.moveToFirst()) {

            c.close();
            return true;


        }
        else{

            c.close();
            return false;
        }




    }

    public void deleteShopCategory() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SHOP_CATEGORY;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    /*
     *Shop Category End
     */
    
    
    /*
     *Sub Shop Category Start
     */
    
    public long createShopSubCategory(String id, String name, String cat_id) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_SUB_SHOP_CATEGORY_ID, id);
        cv.put(KEY_SUB_SHOP_CATEGORY_NAME, name);
        cv.put(KEY_SUB_SHOP_CATEGORY_CATEGORY, cat_id);
        
        
        long insertId = db.insert(DATABASE_TABLE_SUB_SHOP_CATEGORY, null, cv); //Gives Last Autoincremented
        // Inserted value ID
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getShopSubCategoryForDropDown(String id) {
        
        
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        
        
        String selectQuery =
                "SELECT * FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY + " WHERE " + KEY_SUB_SHOP_CATEGORY_CATEGORY + " = " + id + ";";
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        int iItemId = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_ID);
        int iItemName = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_NAME);
        int iItemNameId = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_CATEGORY);
        
        try {
            if (c.moveToFirst()) {
                do {
                    
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", c.getString(iItemId));
                    map.put("name", c.getString(iItemName));
                    map.put("cat_id", c.getString(iItemNameId));
                    
                    data.add(map);
                    
                    
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        c.close();
        
        return data;
    }
    
    public String getShopSubCategoryNameById(String id) {
        
        
        String name = "";
        
        String selectQuery =
                "SELECT " + KEY_SUB_SHOP_CATEGORY_NAME + " FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY +
                        " " +
                        "where " + KEY_SUB_SHOP_CATEGORY_ID + " = " + id + ";";
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        int iItemName = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_NAME);
        
        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        c.close();
        
        
        return name;
    }
    
    public String getShopSubCategoryNameByCustId(String id) {
        
        
        String name = "";
        
        String sub = "SELECT sub_shop_cat_id from customer where " + KEY_ID_CUSTOMER + " = " + id;
        String selectQuery =
                "SELECT " + KEY_SUB_SHOP_CATEGORY_NAME + " FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY + " " +
                        "where " + KEY_SUB_SHOP_CATEGORY_ID + " = (" + sub + ");";
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        int iItemName = c.getColumnIndex(KEY_SUB_SHOP_CATEGORY_NAME);
        
        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        c.close();
        
        
        return name;
    }
    public String getShopSubCategoryIDByCustId(String id) {
        
        
        String name = "0";
        
        String sub = "SELECT sub_shop_cat_id from customer where " + KEY_ID_CUSTOMER + " = " + id;
       
        
        Cursor c = db.rawQuery(sub, null);
        
        int iItemName = c.getColumnIndex(KEY_CUSTOMER_SUB_SHOP_CATEGORY_ID);
        
        try {
            if (c.moveToFirst()) {
                name = c.getString(iItemName);
                
                
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        c.close();
        
        
        return name;
    }
    
    public void deleteShopSubCategory() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SUB_SHOP_CATEGORY;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    /*
     *Sub Shop Category End
     */
    
    ////////////// DISTRIBUTOR_LIST TABLE methods STARTS here
    
    
    public long createDistributorList(int id, String name, String saved, int sync) {
        
        ContentValues cv = new ContentValues();
        
        //int IdInt = Integer.parseInt(id);
        
        cv.put(KEY_DISTRIBUTOR_LIST_ID, id);
        cv.put(KEY_DISTRIBUTOR_LIST_NAME, name);
        cv.put(KEY_DISTRIBUTOR_LIST_SAVED, saved);
        cv.put(KEY_DISTRIBUTOR_LIST_SYNC, sync);
        
        long insertId = db.insert(DATABASE_TABLE_DISTRIBUTOR_LIST, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public List<String> getDistributorIdForDropDown() {
        
        
        List<String> ItemNames = new ArrayList<>();
        
        String[] Column = new String[]{KEY_DISTRIBUTOR_LIST_ID};
        Cursor c = db.query(DATABASE_TABLE_DISTRIBUTOR_LIST, Column, null, null, null, null, null);
        
        int iItemName = c.getColumnIndex(KEY_DISTRIBUTOR_LIST_ID);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return ItemNames;
    }
    
    
    public List<String> getDistributorNameForDropDown() {
        
        
        List<String> ItemNames = new ArrayList<>();
        
        String[] Column = new String[]{KEY_DISTRIBUTOR_LIST_NAME};
        Cursor c = db.query(DATABASE_TABLE_DISTRIBUTOR_LIST, Column, null, null, null, null, null);
        
        int iItemName = c.getColumnIndex(KEY_DISTRIBUTOR_LIST_NAME);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                ItemNames.add(c.getString(iItemName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return ItemNames;
    }
    
    public void updateSavedDistributorList(String saved) {
        
        String updateQuery = "Update " + DATABASE_TABLE_DISTRIBUTOR_LIST + " set " + KEY_DISTRIBUTOR_LIST_SAVED + " = " + saved;
        
        Log.d("query", updateQuery);
        db.execSQL(updateQuery);
        
    }
    
    public String getSavedDistributorList() {
        
        String result = "0";
        
        String[] Column = new String[]{KEY_DISTRIBUTOR_LIST_SAVED};
        Cursor c = db.query(DATABASE_TABLE_DISTRIBUTOR_LIST, Column, null, null, null, null, null);
        
        int iData = c.getColumnIndex(KEY_DISTRIBUTOR_LIST_SAVED);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        return result;
    }
    
    
    public String getDistributorNameByID(int id) {
        // TODO Auto-generated method stub
        
        String[] Columns = new String[]{KEY_DISTRIBUTOR_LIST_NAME};
        Cursor c = db.query(DATABASE_TABLE_DISTRIBUTOR_LIST, Columns, KEY_DISTRIBUTOR_LIST_ID + " = " + id, null, null, null, null);
        
        String result = "";
        
        int iData = c.getColumnIndex(KEY_DISTRIBUTOR_LIST_NAME);
        
        try {
            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                result = c.getString(iData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return result;
    }
    
    
    public void deleteDistributorList() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_DISTRIBUTOR_LIST;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    
    ///////////// DISTRIBUTOR_LIST TABLE methods ENDS here
    
    
    ////////////////
    
    ////////////// SHOP STOCK TABLE methods STARTS here
    
    
    public long createShopStock(int id, String customerId, String productId, String quantity, String empId, String datetime, int syncUpdate) {
        
        ContentValues cv = new ContentValues();
        
        
        cv.put(KEY_SHOP_STOCK_ID, id);
        cv.put(KEY_SHOP_STOCK_CUSTOMER_ID, customerId);
        cv.put(KEY_SHOP_STOCK_PRODUCT_ID, productId);
        cv.put(KEY_SHOP_STOCK_QUANTITY, quantity);
        cv.put(KEY_SHOP_STOCK_EMP_ID, empId);
        cv.put(KEY_SHOP_STOCK_DATETIME, datetime);
        cv.put(KEY_SHOP_STOCK_SYNC, syncUpdate);
        
        long insertId = db.insert(DATABASE_TABLE_SHOP_STOCK, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getShopStock() {
        ArrayList<HashMap<String, String>> shopStockList;
        
        shopStockList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_SHOP_STOCK + " WHERE " + KEY_SHOP_STOCK_SYNC + " = 1";
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_SHOP_STOCK_ID);
        int iCustomerId = cursor.getColumnIndex(KEY_SHOP_STOCK_CUSTOMER_ID);
        int iProductId = cursor.getColumnIndex(KEY_SHOP_STOCK_PRODUCT_ID);
        int iQuantity = cursor.getColumnIndex(KEY_SHOP_STOCK_QUANTITY);
        int iEmpId = cursor.getColumnIndex(KEY_SHOP_STOCK_EMP_ID);
        int iDatetime = cursor.getColumnIndex(KEY_SHOP_STOCK_DATETIME);
        
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("customer_id", cursor.getString(iCustomerId));
                    map.put("product_id", cursor.getString(iProductId));
                    map.put("quantity", cursor.getString(iQuantity));
                    map.put("emp_id", cursor.getString(iEmpId));
                    map.put("datetime", cursor.getString(iDatetime));
                    
                    shopStockList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        
        return shopStockList;
        
    }
    
    public int getMaxIdFromShopStock() {
        
        String que = "SELECT MAX(" + KEY_SHOP_STOCK_ID + ") FROM " + DATABASE_TABLE_SHOP_STOCK;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        
        return maxid;
        
    }
    
    
    public void deleteShopStock() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_SHOP_STOCK;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    
    ///////////// SHOP STOCK TABLE methods ENDS here
    
    ///////////////////
    
    
    ////////////// ITEM TARGET methods STARTS here
    
    
    public long createItemTarget(int id, String item, String target, String sold, String achieved) {
        
        ContentValues cv = new ContentValues();
        
        
        cv.put(KEY_ITEM_TARGET_ID, id);
        cv.put(KEY_ITEM_TARGET_ITEM, item);
        cv.put(KEY_ITEM_TARGET_TARGET, target);
        cv.put(KEY_ITEM_TARGET_SOLD, sold);
        cv.put(KEY_ITEM_TARGET_ACHIEVED, achieved);
        
        long insertId = db.insert(DATABASE_TABLE_ITEM_TARGET, null, cv); //Gives Last Autoincremented Inserted value ID
        
        
        return insertId;
    }
    
    public ArrayList<HashMap<String, String>> getItemTarget() {
        ArrayList<HashMap<String, String>> shopStockList;
        
        shopStockList = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_ITEM_TARGET + " ORDER BY " + KEY_ITEM_TARGET_ID;
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        int iId = cursor.getColumnIndex(KEY_ITEM_TARGET_ID);
        int iItem = cursor.getColumnIndex(KEY_ITEM_TARGET_ITEM);
        int iTarget = cursor.getColumnIndex(KEY_ITEM_TARGET_TARGET);
        int iSold = cursor.getColumnIndex(KEY_ITEM_TARGET_SOLD);
        int iAch = cursor.getColumnIndex(KEY_ITEM_TARGET_ACHIEVED);
        
        
        try {
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", cursor.getString(iId));
                    map.put("item", cursor.getString(iItem));
                    map.put("target", cursor.getString(iTarget));
                    map.put("sold", cursor.getString(iSold));
                    map.put("achieved", cursor.getString(iAch));
                    
                    shopStockList.add(map);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
            
        }
        
        
        return shopStockList;
        
    }
    
    
    public void deleteItemTarget() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_ITEM_TARGET;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    
    ///////////// ITEM TARGET TABLE methods ENDS here
    
    
    /////////////
    public String getSelectedCustomerAdvancePayment(String id) {
        
        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_ADVANCE_PAYMENT};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt, null, null, null, null);
        
        String result = "";
        int iAdvancePayment = c.getColumnIndex(KEY_CUSTOMER_ADVANCE_PAYMENT);
        
        
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = result + c.getString(iAdvancePayment);
        }
        
        if (!c.isClosed())
            c.close();
        
        
        return result;
        
    }
    
    public String getSelectedCustomerOpeningBalanceForSaleReturn(String id) {
        
        int idInt = Integer.parseInt(id);
        String[] Column = new String[]{KEY_CUSTOMER_OPENING_BALANCE_OLD};
        Cursor c = db.query(DATABASE_TABLE_CUSTOMER_DETAILS, Column, KEY_ID_CUSTOMER + " = " + idInt + " AND " + KEY_CUSTOMER_OPENING_BALANCE_OLD + " != 0", null, null, null, null);
        
        String result = "";
        int iP1 = c.getColumnIndex(KEY_CUSTOMER_OPENING_BALANCE_OLD);
        
        
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            result = result + c.getString(iP1);
        }
        
        if (!c.isClosed())
            c.close();
        
        return result;
        
    }
    
    public ArrayList<HashMap<String, String>> getSalesDetailsForSaleReturn(String customerId) {
        
        int custId = Integer.parseInt(customerId);
        
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        
        /// WHERE c."+KEY_ID_CUSTOMER+" = so."+KEY_SALES_ORDER_CUSTOMER_ID+" AND od."+KEY_ORDER_DETAIL_ORDER_ID+" = "+typId;
        
        //String selectQuery = "SELECT od."+KEY_ORDER_DETAIL_PRODUCT_ID+" as productId, od."+KEY_ORDER_DETAIL_QUANTITY+" as qty, od."+KEY_ORDER_DETAIL_QUANTITY_EXE+" as qtyExe, od."+KEY_ORDER_DETAIL_SCHEME_QTY+" as schemeQty, od."+KEY_ORDER_DETAIL_ORDER_ID+" as oid, so."+KEY_SALES_ORDER_ID+" as oid, so."+KEY_SALES_ORDER_TOTAL2+" as tot2, so."+KEY_SALES_ORDER_TOTAL_EXECUTE+" as tot_exe ,c."+KEY_CUSTOMER_FNAME+" as fn, c."+KEY_CUSTOMER_LNAME+" as ln FROM "+DATABASE_TABLE_ORDER_DETAILS+" od, "+DATABASE_TABLE_SALESMAN_SALES_ORDER+" so, "+DATABASE_TABLE_CUSTOMER_DETAILS+" c WHERE c."+KEY_ID_CUSTOMER+" = so."+KEY_SALES_ORDER_CUSTOMER_ID+" AND so."+KEY_SALES_ORDER_ID+" = od."+KEY_ORDER_DETAIL_ORDER_ID+" AND od."+KEY_ORDER_DETAIL_ORDER_ID+" = "+typId;
        String selectQuery = "SELECT so." + KEY_SALES_ORDER_ID + " as id, so." + KEY_SALES_ORDER_TOTAL2 + " as total2, so." + KEY_SALES_ORDER_AMOUNT_RECIEVED + " as pending_amount FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " so WHERE " + KEY_SALES_ORDER_CUSTOMER_ID + " = " + custId + " AND " + KEY_SALES_ORDER_EXECUTE_COMPLETE + " != 0";
        Log.d("SaleDetails QUERY", selectQuery);
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        
        int iId = c.getColumnIndex("id");
        int iAmtRec = c.getColumnIndex("pending_amount");
        int iTotal2 = c.getColumnIndex("total2");
        
        
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", c.getString(iId));
                    map.put("amount_rec", c.getString(iAmtRec));
                    map.put("pending_amount", String.format("%.2f", (Double.parseDouble(c.getString(iTotal2)) - Double.parseDouble(c.getString(iAmtRec)))));
                    map.put("total2", c.getString(iTotal2));
                /*map.put("qty",String.format("%.0f",(Double.parseDouble(c.getString(iQty)) - Double.parseDouble(c.getString(iSchemeQty)))));
                map.put("qtyExe", String.format("%.0f",(Double.parseDouble(c.getString(iQtyExe)) - Double.parseDouble(c.getString(iSchemeQty)))));
                map.put("schemeQty", c.getString(iSchemeQty));*/
                    
                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        return result;
        
        
    }
    
    public void updateAdvancePaymentCustomer(String id, String advancePayment) {
        
        int idInt = Integer.parseInt(id);
        //int stat = 1;
       /* String updateQuery = "Update " + DATABASE_TABLE_CUSTOMER_DETAILS + " set " + KEY_CUSTOMER_OPENING_BALANCE_OLD + " = " + balance + " where " + KEY_ID_CUSTOMER + "=" + idInt;

        Log.d("query", updateQuery);
        db.execSQL(updateQuery);*/
        ContentValues cv = new ContentValues();
        cv.put(KEY_CUSTOMER_ADVANCE_PAYMENT, advancePayment);
        cv.put(KEY_CUSTOMER_UPDATE, 2);
        
        
        db.update(DATABASE_TABLE_CUSTOMER_DETAILS, cv, KEY_ID_CUSTOMER + " = " + idInt, null);
        
    }
    
    public double CheckDays() {
        
        long Current = getCurrentTime();
        long Last = getLastSync();
        
        long diff = Current - Last;
        //long diff = 1465326000000L - Last;
        
        
        long timeInSeconds = diff / 1000;
        long hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        
        
        double day = hours / 24;
        
        
        return day;
        
    }
    
    private double CheckDays(long Last) {
        
        
        long Current = getCurrentTime();
        
        long diff = Current - Last;
        //long diff = 1465326000000L - Last;
        
        
        long timeInSeconds = diff / 1000;
        long hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        
        
        double day = hours / 24;
        
        
        return day;
        
    }
    
    public long getCurrentTime() {
        
        return System.currentTimeMillis();
        
    }
    
    private long getLastSync() {
        // TODO Auto-generated method stub
        String[] columns = new String[]{KEY_Emp_LastSync};
        Cursor c = db.query(Database_Table_mUser, columns, null, null, null, null, null);
        long result = 0;
        int iData = c.getColumnIndex(KEY_Emp_LastSync);
        
        // looping through all rows and adding to editOrderList
        if (c.moveToFirst()) {
            do {
                result = c.getLong(iData);
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }
    
    public int CheckSalesOrderUpdate() {
        
        String que = "Select Count(" + KEY_SALES_ORDER_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " WHERE " + KEY_SALES_ORDER_UPDATE + " = 1 ";
        
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(que, null);
            
            
            if (null != c) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    count = c.getInt(0);
                }
            }
            if (c != null) {
                
                
                c.close();
                
            }
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        
        return count;
        
    }
    
    public int CheckReturnOrderUpdate() {
        
        String que = "Select Count(" + KEY_SALES_RETURN_ID + ") FROM " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " WHERE " + KEY_SALES_RETURN_UPDATE + " = 1 ";
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(que, null);
            
            
            if (null != c) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    count = c.getInt(0);
                }
            }
            if (c != null) {
                
                c.close();
                
            }
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        
        return count;
        
    }
    
    public int CheckCustomerUpdate() {
        
        String que = "Select Count(+" + KEY_ID_CUSTOMER + ") FROM " + DATABASE_TABLE_CUSTOMER_DETAILS + " WHERE " + KEY_CUSTOMER_UPDATE + " = 1 ";
        
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(que, null);
            
            
            if (null != c) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    count = c.getInt(0);
                }
            }
            if (c != null) {
                
                c.close();
                
            }
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        
        return count;
        
    }
    
    public int CheckFilerNonFilerCust(String id) {
        
        int val;
        sql = "select " + KEY_CUSTOMER_FILER_NON_FILER + " from customer where " + KEY_ID_CUSTOMER + " = " + id;
        
        Cursor cursor = db.rawQuery(sql, null);
        
        if (cursor.moveToFirst()) {
            val = cursor.getInt(0);
        } else val = 0;
        
        cursor.close();
        
        return val;
    }
    
    public int CheckPaymentRecievedUpdate() {
        
        String que = "Select Count(" + KEY_PAYMENT_RECIEVED_ID + ") FROM " + DATABASE_TABLE_PAYMENT_RECIEVED + " WHERE " + KEY_PAYMENT_RECIEVED_UPDATE + " = 1 ";
        
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(que, null);
            
            
            if (null != c) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    count = c.getInt(0);
                }
            }
            if (c != null) {
                
                c.close();
                
            }
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        
        return count;
        
    }
    
    public int CheckShopVisitUpdate() {
        
        String que = "Select Count(" + KEY_SHOP_VISIT_ID + ") FROM " + DATABASE_TABLE_SHOP_VISIT + " WHERE " + KEY_SHOP_VISIT_UPDATE + " = 1 ";
        
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(que, null);
            
            
            if (null != c) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    count = c.getInt(0);
                }
            }
            if (c != null) {
                
                c.close();
                
                
            }
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        return count;
        
    }
    
    public int CheckExpenseUpdate() {
        
        String que = "Select Count(" + KEY_EXPENSE_ID + ") FROM " + DATABASE_TABLE_EXPENSE + " WHERE " + KEY_EXPENSE_UPDATE + " = 1 ";
        
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(que, null);
            
            
            if (null != c) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    count = c.getInt(0);
                }
            }
            if (c != null) {
                
                c.close();
                
            }
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        
        return count;
        
    }
    
    public int CheckTravelExpenseUpdate() {
        
        String que = "Select Count(" + KEY_TRAVEL_EXPENSE_ID + ") FROM " + DATABASE_TABLE_TRAVEL_EXPENSE + " WHERE " + KEY_TRAVEL_EXPENSE_UPDATE + " = 1 ";
        
        Cursor c = null;
        int count = 0;
        try {
            c = db.rawQuery(que, null);
            
            
            if (null != c) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    count = c.getInt(0);
                }
            }
            if (c != null) {
                
                c.close();
                
            }
            
        } catch (SQLException e) {
            e.getMessage();
        }
        
        
        return count;
        
    }
    
    public String totalOrderSum() {
        
        String totalSumQuery = "Select   (total2)   FROM sales_order WHERE strftime('%Y'," +
                "\"datetime_orig\") = strftime('%Y',date('now')) AND strftime('%m', \"datetime_orig\") = strftime('%m',date('now')) AND strftime('%d', \"datetime_orig\") = strftime('%d',date('now'));";
        
        String sum = "";
        double sum1 = 0;
        
        try {
            Cursor cursor = db.rawQuery(totalSumQuery, null);
            
            
            if (cursor.moveToFirst()) {
                do {
                    sum = cursor.getString(0);
                    Log.e("TotalCountDB", "Size " + cursor.getString(0));
                    if (sum != null)
                        if (!sum.equals(""))
                            sum1 = sum1 + Double.parseDouble(sum);
                    
                } while (cursor.moveToNext());
            }
            
            
            cursor.close();
            
        } catch (SQLException e) {
            Log.e("MyLog", e.getMessage());
        }
        
        return String.valueOf((int) sum1);
        
    }
	
	public String totalOrderQty() {
		ArrayList sales_order_id_list_for_qty = new ArrayList<String>();
		String totalSumQuery = "Select   (id)   FROM sales_order WHERE strftime('%Y'," +
									   "\"datetime_orig\") = strftime('%Y',date('now')) AND strftime('%m', \"datetime_orig\") = strftime('%m',date('now')) AND strftime('%d', \"datetime_orig\") = strftime('%d',date('now'));";
		
		String sales_order_id_for_qty = "";
		int sales_order_qty=0;
        int Total_sales_order_qty=0;
		try {
			Cursor cursor = db.rawQuery(totalSumQuery, null);
			
			
			if (cursor.moveToFirst()) {
				do {
					sales_order_id_for_qty = cursor.getString(0);
					Log.e("TotalCountDB", "Size " + cursor.getString(0));
					if (sales_order_id_for_qty != null)
						if (!sales_order_id_for_qty.equals(""))
							sales_order_id_list_for_qty.add(sales_order_id_for_qty);
					
				} while (cursor.moveToNext());
                
                String[] Column = new String[]{KEY_ORDER_DETAIL_QUANTITY};
                for(int i =0;i<sales_order_id_list_for_qty.size();i++){
                    Cursor cursor2 = db.query( DATABASE_TABLE_ORDER_DETAILS,Column,KEY_ORDER_DETAIL_ORDER_ID+"="+sales_order_id_list_for_qty.get(i)+" AND "+KEY_ORDER_DETAIL_PRODUCT_ID+"!=0" ,null,null,null,null);
                    
                    if (cursor2.moveToFirst()) {
                        do {
                            sales_order_qty = cursor2.getInt(0);
                            
                            
                            if (sales_order_qty!=0)
                                Total_sales_order_qty=Total_sales_order_qty+sales_order_qty;
                            
                        } while (cursor2.moveToNext());
                    }
                    
                    
                    cursor.close();
                    
                }
			}
			
			
			cursor.close();
			
		} catch (SQLException e) {
			Log.e("MyLog", e.getMessage());
		}
  
		return String.valueOf((int) Total_sales_order_qty);
		
	}
    
    public int returnPercentage() {
        
        
        Cursor cursor = db.rawQuery("Select SUM(total2) from sales_order where execute_complete = 1  AND date(datetime_orig) >= date('now','localtime','start of month') AND date(datetime_orig) <= date('now','localtime')", null);
        
        if (cursor.moveToFirst()) {
            
            //String s = cursor.getString(0);
            if (!cursor.isNull(0)) {
                Log.e("TargetValueFromDB", String.valueOf(Integer.parseInt(cursor.getString(0))));
                int a = Integer.parseInt(cursor.getString(0));
                cursor.close();
                return a;
            } else {
                cursor.close();
                return 0;
            }
        } else {
            cursor.close();
            return 0;
        }
    }
    
    public ArrayList<HashMap<String, String>> getSelectedProductWithTypeId(String p_type_id) {
        
        ArrayList<HashMap<String, String>> stringList = new ArrayList<>();
        
        
        String query = "select id,item_search from inventory where type_id = " + p_type_id;
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {
                
                HashMap<String, String> map = new HashMap<>();
                
                map.put("id", cursor.getString(0));
                map.put("name", cursor.getString(1));
                stringList.add(map);
                
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stringList;
    }
    public ArrayList<HashMap<String, String>> getSelectedProductWithBrandId(String p_type_id) {

        ArrayList<HashMap<String, String>> stringList = new ArrayList<>();


        String query = "select id,item_search from inventory where brand_id = " + p_type_id;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                HashMap<String, String> map = new HashMap<>();

                map.put("id", cursor.getString(0));
                map.put("name", cursor.getString(1));
                stringList.add(map);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return stringList;
    }
    public ArrayList<HashMap<String, String>> getSelectedProductWithId(String p_type_id) {

        ArrayList<HashMap<String, String>> stringList = new ArrayList<>();


        String query = "select id,item_search from inventory where id = " + p_type_id;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                HashMap<String, String> map = new HashMap<>();

                map.put("id", cursor.getString(0));
                map.put("name", cursor.getString(1));
                stringList.add(map);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return stringList;
    }
    public ArrayList<HashMap<String, String>> getSelectedProductWithSubTypeId(String p_type_id) {

        ArrayList<HashMap<String, String>> stringList = new ArrayList<>();


        String query = "select id,item_search from inventory where subtypeid = " + p_type_id;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            do {

                HashMap<String, String> map = new HashMap<>();

                map.put("id", cursor.getString(0));
                map.put("name", cursor.getString(1));
                stringList.add(map);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return stringList;
    }
    public String getProductCategoryType(String p_type_id) {
        
        String s = "No Category Found...";
        
        
        String query = "select name from product_type where id = " + p_type_id;
        
        Cursor cursor = db.rawQuery(query, null);
        
        if (cursor.moveToFirst()) {
            
            
            s = cursor.getString(0);
            
        }
        cursor.close();
        return s;
    }
    
    public void deleteSalesOrderDetailScheme(String orderID) {
        
        String query = "delete from order_details where order_id = " + orderID + " AND multi_scheme = 1;";
        
        
        Log.d("query", query);
        db.execSQL(query);
    }
    
    public void deleteSalesReturnDetailScheme(String orderID) {
        
        String query = "delete from return_details where order_id = " + orderID + " AND " +
                "multi_scheme" + " " + "=" + " 1;";
        
        
        Log.d("query", query);
        db.execSQL(query);
    }
    
    
    public ArrayList<HashMap<String, String>> getSchemeDetailsByOrder(String oid) {
        
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        
        
        String query = "select * from order_details where order_id = " + oid + " AND multi_scheme = " +
                "1";
        
        Cursor c = db.rawQuery(query, null);
        
        
        int iProdId = c.getColumnIndex("productId");
        int iQty = c.getColumnIndex("quantity");
        
        
        if (c.moveToFirst()) {
            do {
                
                
                HashMap<String, String> map = new HashMap<>();
                map.put("p_id", c.getString(iProdId));
                map.put("qty", c.getString(iQty));
                
                list.add(map);
                
                
            } while (c.moveToNext());
        }
        
        
        c.close();
        
        return list;
    }
    
    private static class DBHelper extends SQLiteOpenHelper {
        
        
        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            
            sql = "CREATE TABLE " + DATABASE_TABLE_SHOP_CATEGORY + " ("
                    + KEY_SHOP_CATEGORY_ID + " INTEGER, "
                    + KEY_SHOP_CATEGORY_NAME + " TEXT );";
            
            db.execSQL(sql);
            sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER + " ("
                          + KEY_PATIENT_ORDER_ID + " INTEGER PRIMARY KEY , "
                          + KEY_PATIENT_ORDER_EMP_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_CUST_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_DIST_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_DATE_TIME + " TEXT , "
                          + KEY_PATIENT_ORDER_ORDER_UPDATE + " TEXT , "
                          + KEY_PATIENT_ORDER_PATIENT_NAME + " TEXT );";
    
            db.execSQL(sql);
            sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER2 + " ("
                          + KEY_PATIENT_ORDER_ID + " INTEGER PRIMARY KEY , "
                          + KEY_PATIENT_ORDER_EMP_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_CUST_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_DIST_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_DATE_TIME + " TEXT , "
                          + KEY_PATIENT_ORDER_ORDER_UPDATE + " TEXT , "
                          + KEY_PATIENT_ORDER_PATIENT_NAME + " TEXT );";

            db.execSQL(sql);
            sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_BRAND + " ("
                    + KEY_CUSTOMER_BRAND_ID + " TEXT PRIMARY KEY , "
                    + KEY_CUSTOMER_BRAND_BRAND_ID + " TEXT , "
                    + KEY_CUSTOMER_BRAND_CUSTOMER_ID + " TEXT );";

            db.execSQL(sql);

//            sql = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_TEMP_CART + " ("
//                    + KEY_CUSTOMER_BRAND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
//                    + KEY_CUSTOMER_BRAND_BRAND_ID + " TEXT , "
//                    + KEY_TEMP_CART_BRAND_NAME + " TEXT , "
//                    + KEY_TEMP_CART_PROD_ID + " TEXT , "
//                    + KEY_TEMP_CART_PROD_NAME + " TEXT , "
//                    + KEY_TEMP_CART_PROD_PRICE + " TEXT , "
//                    + KEY_TEMP_CART_PROD_UNIT + " TEXT , "
//                    + KEY_TEMP_CART_PROD_DISCOUNT + " TEXT );";
//            db.execSQL(sql);

            sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER_DETAIL + " ("
                          + KEY_PATIENT_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY , "
                          + KEY_PATIENT_ORDER_DETAIL_ORDER_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_DETAIL_ITEM_NAME + " TEXT , "
                          + KEY_PATIENT_ORDER_DETAIL_QUANTITY + " TEXT );";
    
            db.execSQL(sql);


            sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER_DETAIL2 + " ("
                          + KEY_PATIENT_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                          + KEY_PATIENT_ORDER_DETAIL_ORDER_ID + " TEXT , "
                          + KEY_PATIENT_ORDER_DETAIL_ITEM_NAME + " TEXT , "
                          + KEY_PATIENT_ORDER_DETAIL_RATE + " TEXT , "
                          + KEY_PATIENT_ORDER_DETAIL_QUANTITY + " TEXT );";

            db.execSQL(sql);


            sql = "CREATE TABLE " + DATABASE_TABLE_ORDER_TEMPLATE + " ("
                          + KEY_ORDER_TEMPLATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                          + KEY_ORDER_TEMPLATE_ITEM_ID + " TEXT,"
                          + KEY_ORDER_TEMPLATE_QUANTITY + " TEXT,"
                          + KEY_ORDER_TEMPLATE_CUSTOMER_ID + " TEXT);";
    
            db.execSQL(sql);
            
            
          
            
            
            
            sql = "CREATE TABLE " + DATABASE_TABLE_SUB_SHOP_CATEGORY + " ("
                    + KEY_SUB_SHOP_CATEGORY_ID + " INTEGER, "
                    + KEY_SUB_SHOP_CATEGORY_NAME + " TEXT, "
                    + KEY_SUB_SHOP_CATEGORY_CATEGORY + " INTEGER );";
            
            db.execSQL(sql);
            
            //CREATE DISTRIBUTOR_LIST TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_DISTRIBUTOR_LIST + " ("
                    + KEY_DISTRIBUTOR_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_DISTRIBUTOR_LIST_NAME + " TEXT , "
                    + KEY_DISTRIBUTOR_LIST_SAVED + " TEXT , "
                    + KEY_DISTRIBUTOR_LIST_SYNC + " INTEGER DEFAULT 0);";
            
            db.execSQL(sql);
            
            
            //CREATE APP_SETTING
            sql = "CREATE TABLE " + DATABASE_TABLE_APP_SETTINGS + " ("
                    + KEY_APP_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_APP_SETTINGS_KEY + " TEXT , "
                    + KEY_APP_SETTINGS_VALUE + " INTEGER DEFAULT 0);";
            
            db.execSQL(sql);
            
            //CREATE INVENTORY TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_INVENTORY + " ("
                    + KEY_ID_INVENT + " INTEGER PRIMARY KEY , "
                    + KEY_ITEM_ID_FROM_INVENT + " INTEGER NOT NULL, "
                    + KEY_BRAND_ID_FROM_INVENT + " INTEGER NOT NULL, "
                    + KEY_VENDOR_ID + " INTEGER NOT NULL, "
                    + KEY_INVENT_NAME + " TEXT NOT NULL, "
                    + KEY_QUANTITY + " INTEGER, "
                    + KEY_UNIT_COST + " INTEGER NOT NULL, "
                    + KEY_SALE_PRICE + " INTEGER NOT NULL,"
                    + KEY_SKU + " TEXT, "
                    + KEY_PACKING + " INTEGER, "
                    + KEY_INVENTORY_PUBLISHED + " INTEGER, "
                    + KEY_INVENTORY_SEARCH + " TEXT, "
                    + KEY_TAX_APPLIED + " REAL, "
                    + KEY_INVENTORY_IMAGE + " TEXT, "
                    + KEY_INVENTORY_SUB_TYPE_ID + " INTEGER, "
                    + "mrp_price " + " INTEGER, "
                    + "tax_mrp" + " INTEGER, "
                    + "is_taxable" + " INTEGER, "
                    + KEY_DETAILS + " TEXT);";
            
            db.execSQL(sql);
            
            //CREATE REPORT TABLE
            sql = "CREATE TABLE " + Database_Table_ReportTo + " ("
                    + KEY_ID_REPORT_EMP + " INTEGER NOT NULL, "
                    + KEY_FNAME_REPORT + " TEXT NOT NULL, "
                    + KEY_LNAME_REPORT + " TEXT NOT NULL,"
                    + KEY_EMAIL_REPORT + " TEXT NOT NULL);";
            
            db.execSQL(sql);
            
            //CREATE Offline Tracking TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_OFFLINE_TRACKING + " ("
                    + KEY_OFFLINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                    + KEY_OFFLINE_LATTITUDE + " TEXT , "
                    + KEY_OFFLINE_LONGITUDE + " TEXT ,"
                    + KEY_OFFLINE_DATETIME + " TEXT ,"
                    + KEY_OFFLINE_AREA + " TEXT );";
            
            db.execSQL(sql);
            
            
            //CREATE Salesman Settings TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_SALESMAN_SETTINGS + " ("
                    + KEY_SALESMAN_SETTINGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                    + KEY_SALESMAN_SETTINGS_EID + " INTEGER DEFAULT 0 , "
                    
                    + KEY_SALESMAN_SETTINGS_ADMIN_EMAIL + " TEXT DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_ADMIN_PHONE + " TEXT DEFAULT 0 ,"
                    
                    + KEY_SALESMAN_SETTINGS_TRACKING_EN + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_TRACKING_OFFLINE + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_TRACKING_TIMEIN + " TEXT DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_TRACKING_TIMEOUT + " TEXT DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_TRACKING_DURATION + " INTEGER DEFAULT 0 ,"
                    
                    + KEY_SALESMAN_SETTINGS_NOTIFY_ALERTBAR + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_NOTIFY_VIBRATE + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_NOTIFY_SOUND + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_NOTIFY_LIGHT + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_NOTIFY_EMAIL + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_NOTIFY_PHONE_START + " INTEGER DEFAULT 0 ,"
                    
                    + KEY_SALESMAN_SETTINGS_ACCESS_ROUTE + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_ACCESS_REPORT + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_ACCESS_CUSTOMER + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_ACCESS_INVENTORY + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_ACCESS_ORDER + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_ACCESS_LOGOUT + " INTEGER DEFAULT 1 ,"
                    
                    + KEY_SALESMAN_SETTINGS_MIN_DISCOUNT + " INTEGER DEFAULT 0 ,"
                    + KEY_SALESMAN_SETTINGS_MAX_DISCOUNT + " INTEGER DEFAULT 0 ,"
                    
                    + KEY_SALESMAN_SETTINGS_ACCESS_SYNC + " INTEGER DEFAULT 0 );";
            
            db.execSQL(sql);
            
            //CREATE REPORT TABLE VALID
            sql = "CREATE TABLE " + Database_Table_VALID_REPORT + " ("
                    + KEY_ID_VALID + " INTEGER NOT NULL, "
                    + KEY_INDEX_VALID + " INTEGER NOT NULL, "
                    + KEY_FNAME_VALID + " TEXT NOT NULL);";
            
            
            db.execSQL(sql);
            
            //CREATE BRAND TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_BRAND + " ("
                    + KEY_ID_BRAND + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_BRAND_PUBLISHED + " INTEGER, "
                    + KEY_BRAND_NAME + " TEXT NOT NULL);";
            
            db.execSQL(sql);
            
            //CREATE ITEM TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_ITEM_TYPE + " ("
                    + KEY_ID_ITEM + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ITEMTYPE_PUBLISHED + " INTEGER, "
                    + KEY_ITEM_NAME + " TEXT NOT NULL);";
            db.execSQL(sql);
            
            
            //CREATE ITEM SUB TYPE TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_ITEM_SUB_TYPE + " ("
                    + KEY_ID_SUB_ITEM + " INTEGER PRIMARY KEY , "
                    + KEY_NAME_SUB_ITEM + " TEXT NOT NULL, "
                    + KEY_ITEM_ID_SUB_ITEM + " INTEGER );";
            db.execSQL(sql);
            
            
            //CREATE CUSTOMER TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_DETAILS + " ("
                    + KEY_ID_CUSTOMER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CUSTOMER_FNAME + " TEXT NOT NULL, "
                    + KEY_CUSTOMER_LNAME + " TEXT NOT NULL, "
                    + KEY_COMPANY_NAME + " TEXT, "
                    + KEY_CUSTOMER_CELL + " TEXT, "
                    + KEY_CUSTOMER_PHONE1 + " TEXT, "
                    + KEY_CUSTOMER_PHONE2 + " TEXT, "
                    + KEY_CUSTOMER_ADDRESS + " TEXT, "
                    + KEY_CUSTOMER_CITY + " TEXT, "
                    + KEY_CUSTOMER_AREA + " TEXT, "
                    + KEY_CUSTOMER_CNIC + " TEXT, "
                    + KEY_CUSTOMER_STATE + " TEXT, "
                    + KEY_CUSTOMER_COUNTRY + " TEXT, "
                    + KEY_CUSTOMER_EMAIL + " TEXT, "
                    + KEY_CUSTOMER_PUBLISHED + " INTEGER , "
                    + KEY_CUSTOMER_UPDATE + " INTEGER , "
                    + KEY_CUSTOMER_NOTES + " TEXT,"
                    + KEY_CUSTOMER_ROUTE_ID + " INTEGER,"
                    + KEY_CUSTOMER_TYPE + " INTEGER,"
                    + KEY_CUSTOMER_CATEGORY + " INTEGER,"
                    + KEY_CUSTOMER_CELEB + " INTEGER,"
                    + KEY_CUSTOMER_NET_ID + " INTEGER DEFAULT 0,"
                    + KEY_CUSTOMER_ADVANCE_PAYMENT + " REAL DEFAULT 0,"
                    + KEY_CUSTOMER_OPENING_BALANCE_OLD + " TEXT, "
                    + KEY_CUSTOMER_OPENING_BALANCE_NEW + " TEXT, "
                    + KEY_CUSTOMER_CREDIT_AMOUNT + " TEXT, "
                    + KEY_CUSTOMER_APP_PAYABLE + " TEXT, "
                    + KEY_CUSTOMER_LATTITUDE + " TEXT, "
                    + KEY_CUSTOMER_LONGITUDE + " TEXT , "
                    + KEY_CUSTOMER_RADIUS + " REAL , "
                    + KEY_CUSTOMER_MAPNAME + " TEXT, "
                    + KEY_CUSTOMER_SEARCH + " TEXT, "
                    
                    + KEY_CUSTOMER_NUMBER_VERIFIED + " INTEGER DEFAULT 0,"
                    + KEY_CUSTOMER_SMS_CODE + " TEXT DEFAULT 0, "
                    + KEY_CUSTOMER_LAST_UPDATE + " DATETIME,"
                    + KEY_CUSTOMER_GPS + " INTEGER , "
                    + KEY_CUSTOMER_FILER_NON_FILER + " INTEGER DEFAULT 0 , "
                    + KEY_CUSTOMER_SHOP_CATEGORY_ID + " INTEGER , "
                    + KEY_CUSTOMER_SUB_SHOP_CATEGORY_ID + " INTEGER ); ";
            
            
            db.execSQL(sql);
            
            //CREATE SETTINGS TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_SETTINGS + " ("
                    //+KEY_mId+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SETTINGS_ID + " INTEGER NOT NULL , "
                    + KEY_AutoSync_SETTINGS + " INTEGER NOT NULL,  "
                    + KEY_TimeIn_SETTINGS + " INTEGER , "
                    + KEY_TimeOut_SETTINGS + " INTEGER , "
                    + KEY_SyncDur_SETTINGS + " REAL NOT NULL , "
                    + KEY_SyncDur_INDEX_SETTINGS + " INTEGER NOT NULL , "
                    
                    + KEY_CLockIn_SETTINGS + " INTEGER NOT NULL , "
                    + KEY_CLockIn_Time_SETTINGS + " TEXT , "
                    + KEY_CLockIn_Total_SETTINGS + " REAL , "
                    
                    + KEY_Route_SETTINGS + " INTEGER , "
                    + KEY_Reporting_SETTINGS + " INTEGER , "
                    + KEY_Timing_SETTINGS + " INTEGER , "
                    + KEY_inventPg_SETTINGS + " INTEGER , "
                    + KEY_CustomerPg_SETTINGS + " INTEGER , "
                    + KEY_PlaceOrderPg_SETTINGS + " INTEGER , "
                    + KEY_SummaryPg_SETTINGS + " INTEGER , "
                    + KEY_SyncPg_SETTINGS + " INTEGER , "
                    
                    + KEY_Password_SETTINGS + " TEXT, "
                    + KEY_AdminEmail_SETTINGS + " TEXT, "
                    + KEY_AdminPhone_SETTINGS + " TEXT, "
                    + KEY_en_Password_SETTINGS + " INTEGER );";
            
            db.execSQL(sql);
            
            
            //CREATE MUser TABLE
            sql = "CREATE TABLE " + Database_Table_mUser + " ("
                    + KEY_mId + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_emp_id + " INTEGER NOT NULL, "
                    + KEY_Emp_FName + " TEXT NOT NULL, "
                    + KEY_Emp_LName + " TEXT NOT NULL,"
                    + KEY_Emp_Comp + " TEXT,"
                    + KEY_Emp_Cell + " TEXT,"
                    + KEY_Emp_P1 + " TEXT,"
                    + KEY_Emp_P2 + " TEXT,"
                    + KEY_Emp_Add + " TEXT,"
                    + KEY_Emp_City + " TEXT,"
                    + KEY_Emp_State + " TEXT,"
                    + KEY_Emp_Country + " TEXT,"
                    + KEY_Emp_Zip + " TEXT,"
                    + KEY_Emp_LastSync + " REAL,"
                    + KEY_Emp_Email + " TEXT NOT NULL,"
                    + KEY_Emp_APP_VERSION + " TEXT,"
                    + KEY_Emp_PRODUCT_SALE + " TEXT,"
                    + KEY_Emp_TARGET + " TEXT,"
                    + KEY_Emp_REPORT_URL + " TEXT,"
                    + KEY_Emp_TIME_IN + " TEXT DEFAULT 0,"
                    + KEY_Emp_LAST_SYNCED + " TEXT,"
                    + KEY_Emp_SALE_TYPE + " TEXT,"
                    + KEY_Emp_DISCOUNT_TYPE + " TEXT,"
                    + KEY_Emp_TIME_IN_TIME + " DATETIME,"
                    + KEY_Emp_Enable_Catalog_Pdf + " INTEGER DEFAULT 0,"
                    + KEY_Emp_Password + " TEXT NOT NULL);";
            
            db.execSQL(sql);
            
            
            //CREATE SALES TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_SALES + " ("
                    + KEY_SALES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CUSTOMER_ID_FR_SALES + " INTEGER NOT NULL, "
                    + KEY_EMP_ID_FR_SALES + " INTEGER NOT NULL, "
                    + KEY_DATETIME_FR_SALES + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + KEY_DISCOUNT_FR_SALES + " INTEGER NOT NULL, "
                    + KEY_PENDING_AMOUNT_FR_SALES + " INTEGER NOT NULL, "
                    + KEY_TOTAL_AMOUNT_FR_SALES + " INTEGER NOT NULL, "
                    + KEY_CASH_ACCOUNT_FR_SALES + " INTEGER, "
                    + KEY_PAYMENT_MODE_FR_SALES + " INTEGER NOT NULL, "
                    + KEY_SHIPPING_FR_SALES + " INTEGER NOT NULL, "
                    + KEY_SALES_PUBLISHED + " INTEGER, "
                    + KEY_TAX_APPLIED_FR_SALES + " INTEGER NOT NULL );";
            
            db.execSQL(sql);
            
            
            //CREATE SALES Details TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_SALES_DETAILS + " ("
                    + KEY_SALESDETAILS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ORDER_ID_FR_SD + " INTEGER NOT NULL, "
                    + KEY_COGS_FR_SD + " INTEGER NOT NULL, "   /// Same as Unit Cost
                    + KEY_PRODUCT_ID_FR_SD + " INTEGER NOT NULL, "
                    + KEY_QUANTITY_FR_SD + " INTEGER NOT NULL, "
                    + KEY_SD_PUBLISHED + " INTEGER, "
                    + KEY_PRODUCT_AMOUNT_FR_SD + " INTEGER NOT NULL );";
            
            db.execSQL(sql);
            
            
            //CREATE CART TABLE
            sql = "CREATE TABLE " + Database_Table_Cart + " ("
                    + KEY_CART_ID + " INTEGER, "
                    + KEY_CART_NAME + " TEXT, "
                    + KEY_CART_PRICE + " INTEGER, "
                    + KEY_CART_QTY + " INTEGER, "
                    + KEY_UC_CART + " INTEGER, "
                    + KEY_CART_TOTAL + " REAL );";
            
            db.execSQL(sql);
            
            //CREATE Customer Route TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_ROUTE + " ("
                    + KEY_ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ROUTE_NET_ID + " INTEGER NOT NULL, "
                    + KEY_ROUTE_SAVED + " INTEGER DEFAULT 0, "
                    + KEY_ROUTE_UPDATE + " INTEGER DEFAULT 0, "
                    + KEY_ROUTE_NAME + " TEXT NOT NULL,"
                    + KEY_ROUTE_DAY + " INTEGER ); ";
            
            db.execSQL(sql);
            
            //CREATE Route TABLE
            sql = "CREATE TABLE " + Database_Table_Route + " ("
                    + KEY_ROUTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ROUTE_emp_id + " INTEGER NOT NULL, "
                    + KEY_ROUTE_cid + " INTEGER NOT NULL, "
                    + KEY_ROUTE_seq + " INTEGER NOT NULL, "
                    + KEY_ROUTE_visit_month + " INTEGER NOT NULL, "
                    + KEY_ROUTE_day + " TEXT NOT NULL); ";
            
            db.execSQL(sql);
            
            //CREATE TodayRoute TABLE
            sql = "CREATE TABLE " + Database_Table_Today_Route + " ("
                    + KEY_TodayROUTE_Primary_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TodayROUTE_ID + " INTEGER , "
                    + KEY_TodayROUTE_DateTime + " TEXT ); ";
            
            db.execSQL(sql);
            
            //CREATE CLockIn TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_CLOCKIN + " ("
                    + KEY_id_CLOCKIN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_empId_CLOCKIN + " INTEGER, "
                    + KEY_status_CLOCKIN + " INTEGER, "
                    + KEY_clockInTime_PREV + " INTEGER, "
                    + KEY_clockInTime_NEXT + " INTEGER, "
                    + KEY_TOTALTime_CLOCKIN + " REAL, "
                    + KEY_clockInTime_CLOCKIN + " TEXT );";
            
            db.execSQL(sql);
            
            
            //CREATE PHONESTART TABLE
            sql = "CREATE TABLE " + Database_Table_PHONESTART + " ("
                    + KEY_ID_PHONESTART + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_PHONESTART_TIME + " TEXT NOT NULL, "
                    + KEY_PHONESTART_SYNC + " INTEGER NOT NULL);";
            
            db.execSQL(sql);
            
            //CREATE CHKNET TABLE
            sql = "CREATE TABLE " + Database_Table_CHKNET + " ("
                    + KEY_ID_CHKNET + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CHKNET_TIME + " TEXT NOT NULL, "
                    + KEY_CHKNET_STATUS + " INTEGER NOT NULL, "
                    + KEY_CHKNET_SYNC + " INTEGER NOT NULL);";
            
            db.execSQL(sql);
            
            
            //CREATE SALES ORDER TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_SALESMAN_SALES_ORDER + " ("
                    + KEY_SALES_ORDER_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SALES_ORDER_CUSTOMER_ID + " INTEGER NOT NULL, "
                    + KEY_SALES_ORDER_EMPLOYEE_ID + " INTEGER NOT NULL, "
                    + KEY_SALES_ORDER_VALUES + " TEXT NOT NULL, "
                    + KEY_SALES_ORDER_NOTES + " TEXT, "
                    + KEY_SALES_ORDER_START_DATETIME + " TEXT, "
                    + KEY_SALES_ORDER_DATETIME + " TEXT, "
                    + KEY_SALES_ORDER_DATESHORT + " TEXT, "
                    + KEY_SALES_ORDER_DISCOUNT + " TEXT, "
                    + KEY_SALES_ORDER_TOTAL + " TEXT, "
                    + KEY_SALES_ORDER_TOTAL2 + " TEXT, "
                    + KEY_SALES_ORDER_TOTAL_EXECUTE + " TEXT, "
                    + KEY_SALES_ORDER_LATITUDE + " REAL, "
                    + KEY_SALES_ORDER_LONGITUDE + " REAL, "
                    + KEY_SALES_ORDER_CONFIRM + " INTEGER DEFAULT 0, "
                    + KEY_SALES_ORDER_DELETE + " INTEGER DEFAULT 0, "
                    + KEY_SALES_ORDER_UPDATE + " INTEGER DEFAULT 0, "
                    + KEY_SALES_ORDER_EXECUTE_COMPLETE + " INTEGER DEFAULT 0, "
                    + KEY_SALES_ORDER_AMOUNT_RECIEVED + " TEXT, "
                    + KEY_SALES_ORDER_EXECUTION_DATE + " TEXT, "
                    + KEY_SALES_ORDER_PRINTING_DATE + " TEXT, "
                    + KEY_SALES_ORDER_PAYMENT_TYPE + " TEXT, "
                    + KEY_SALES_ORDER_SELECTED_DISTRIBUTOR_ID + " TEXT, "
                    + KEY_SALES_ORDER_DISTRIBUTOR_INVOICE + " TEXT, "
                    + KEY_SALES_ORDER_ANDROID_OID + " INTEGER DEFAULT 0 );";
            
            db.execSQL(sql);
            
            //CREATE SALES RETURN TABLE
            sql = "CREATE TABLE " + DATABASE_TABLE_SALESMAN_SALES_RETURN + " ("
                    + KEY_SALES_RETURN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SALES_RETURN_CUSTOMER_ID + " INTEGER NOT NULL, "
                    + KEY_SALES_RETURN_EMPLOYEE_ID + " INTEGER NOT NULL, "
                    + KEY_SALES_RETURN_REASON + " INTEGER, "
                    + KEY_SALES_RETURN_VALUES + " TEXT NOT NULL, "
                    + KEY_SALES_RETURN_NOTES + " TEXT, "
                    + KEY_SALES_RETURN_START_DATE + " TEXT, "
                    + KEY_SALES_RETURN_DATE + " TEXT, "
                    + KEY_SALES_RETURN_DISCOUNT + " TEXT, "
                    + KEY_SALES_RETURN_TOTAL + " TEXT, "
                    + KEY_SALES_RETURN_TOTAL2 + " TEXT, "
                    + KEY_SALES_RETURN_LATITUDE + " REAL, "
                    + KEY_SALES_RETURN_LONGITUDE + " REAL, "
                    + KEY_SALES_RETURN_CONFIRM + " INTEGER DEFAULT 0, "
                    + KEY_SALES_RETURN_DELETE + " INTEGER DEFAULT 0, "
                    + KEY_SALES_RETURN_UPDATE + " INTEGER DEFAULT 0, "
                    + KEY_SALES_RETURN_EXECUTE_COMPLETE + " INTEGER DEFAULT 0, "
                    + KEY_SALES_RETURN_EXECUTION_DATE + " TEXT, "
                    + KEY_SALES_RETURN_PRINTING_DATE + " TEXT, "
                    + KEY_SALES_RETURN_SELECTED_DISTRIBUTOR_ID + " TEXT, "
                    + KEY_SALES_RETURN_ANDROID_OID + " INTEGER DEFAULT 0 );";
            
            db.execSQL(sql);
            
            // Create Return Reason Table
            sql = "CREATE TABLE " + Database_Table_Return_Reason + " ("
                    + KEY_RETURN_REASON_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_RETURN_REASON_NAME + " TEXT);";
            
            db.execSQL(sql);
            
            // Create Expense Type Table
            sql = "CREATE TABLE " + DATABASE_TABLE_EXPENSE_TYPE + " ("
                    + KEY_EXPENSE_TYPE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_EXPENSE_TYPE_NAME + " TEXT);";
            
            db.execSQL(sql);
            
            
            // Create Expense Status Table
            sql = "CREATE TABLE " + DATABASE_TABLE_EXPENSE_STATUS + " ("
                    + KEY_EXPENSE_STATUS_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_EXPENSE_STATUS_NAME + " TEXT);";
            
            db.execSQL(sql);
            
            // Create No Reason Table
            sql = "CREATE TABLE " + DATABASE_TABLE_NO_REASON + " ("
                    + KEY_NO_REASON_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_NO_REASON_NAME + " TEXT);";
            
            db.execSQL(sql);
            
            
            // Create Customer Type Table
            sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_TYPE + " ("
                    + KEY_CUSTOMER_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CUSTOMER_TYPE_NAME + " TEXT );";
            
            db.execSQL(sql);
            
            
            sql = "CREATE TABLE cust_pricing \n" +
                    "(\n" +
                    "    id                      INTEGER  NOT NULL                                     PRIMARY KEY AUTOINCREMENT, \n" +
                    "   type_id                 TEXT DEFAULT '0',\n" +
                    "   product_id              TEXT DEFAULT '0',   \n" +
                    "   trade_price             REAL, \n" +
                    "   discount_1              REAL,   \n" +
                    "   discount_2              REAL, \n" +
                    "   trade_offer             REAL,  \n" +
                    "   scheme                  REAL,  \n" +
                    "   scheme_val              REAL,  \n" +
                    "   scheme_product          INTEGER  DEFAULT 0, \n" +
                    "   tax_1                   REAL,    \n" +
                    "   tax_filer_1             REAL,  \n" +
                    "   tax_2                   REAL,  \n" +
                    "   tax_filer_2             REAL,   \n" +
                    "   tax_3                   REAL,  \n" +
                    "   tax_filer_3             REAL,  \n" +
                    "   sub_total               REAL, \n" +
                    "   customer_id             TEXT  DEFAULT '0', \n" +
                    "   brand_id                 TEXT  DEFAULT '0',\n" +
                    "   product_type_id          TEXT  DEFAULT '0',\n" +
                    "   product_sub_type_id      TEXT  DEFAULT '0', \n" +
                    "   min_qty                 INTEGER  DEFAULT NULL, \n" +
                    "   max_qty                 INTEGER  DEFAULT NULL, \n" +
                    "   multi                   INTEGER  DEFAULT 0,    \n" +
                    "   use_defaultprice        INTEGER  DEFAULT 0,   \n" +
                    "   mrp_price               INTEGER  DEFAULT 0,   \n" +
                    "   use_defaultmrp          INTEGER  DEFAULT 0,   \n" +
                    "   min_amount              INTEGER  DEFAULT 0,   \n" +
                    "   max_amount              INTEGER  DEFAULT 0,   \n" +
                    "   customer_category_id     TEXT  DEFAULT '0',   \n" +
                    "   customer_subcategory_id  TEXT  DEFAULT '0',   \n" +
                    "   emp_id  TEXT  DEFAULT '0',   \n" +
                    " dist_id  TEXT  DEFAULT '0');";

            db.execSQL(sql);
            
            
            sql = "CREATE TABLE IF NOT EXISTS  order_details (\n" +
                    "    id             INTEGER NOT NULL\n" +
                    "                           PRIMARY KEY AUTOINCREMENT,\n" +
                    "    order_id       INTEGER,\n" +
                    "    product_id     INTEGER,\n" +
                    "    quantity       REAL,\n" +
                    "    quantity_exe   REAL,\n" +
                    "    trade_price    REAL,\n" +
                    "    discount_one   REAL,\n" +
                    "    discount_two   REAL,\n" +
                    "    trade_offer    REAL,\n" +
                    "    scheme         REAL,\n" +
                    "    scheme_qty     INTEGER DEFAULT 0,\n" +
                    "    scheme_formula REAL,\n" +
                    "    scheme_product INTEGER DEFAULT 0,\n" +
                    "    tax_one        REAL,\n" +
                    "    tax_two        REAL,\n" +
                    "    tax_three      REAL,\n" +
                    "    sub_total      REAL,\n" +
                    "    multi_scheme   INTEGER DEFAULT 0," +
                    "t_o_v REAL DEFAULT 0,d_v_1 REAL DEFAULT 0,d_v_2 REAL DEFAULT 0,t_type" +
                    " REAL DEFAULT 0,t_mrp_type REAL DEFAULT 0,t_val REAL DEFAULT 0,mrp_price " +
                    "REAL DEFAULT 0" +
                    ");\n";
            
            db.execSQL(sql);
            
            // Create Return Detail Table
            sql = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_RETURN_DETAILS + " ("
                    + KEY_RETURN_DETAIL_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_RETURN_DETAIL_ORDER_ID + " INTEGER, "
                    + KEY_RETURN_DETAIL_PRODUCT_ID + " INTEGER, "
                    + KEY_RETURN_DETAIL_QUANTITY + " REAL, "
                    + KEY_RETURN_DETAIL_QUANTITY_EXE + " REAL, "
                    + KEY_RETURN_DETAIL_TRADE_PRICE + " REAL, "
                    + KEY_RETURN_DETAIL_DISCOUNT_1 + " REAL, "
                    + KEY_RETURN_DETAIL_DISCOUNT_2 + " REAL, "
                    + KEY_RETURN_DETAIL_TRADE_OFFER + " REAL, "
                    + KEY_RETURN_DETAIL_SCHEME + " REAL, "
                    + KEY_RETURN_DETAIL_SCHEME_QTY + " INTEGER DEFAULT 0, "
                    + KEY_RETURN_DETAIL_SCHEME_FORMULA + " REAL, "
                    + KEY_RETURN_DETAIL_SCHEME_PRODUCT + " INTEGER DEFAULT 0, "
                    + KEY_RETURN_DETAIL_TAX_1 + " REAL, "
                    + KEY_RETURN_DETAIL_TAX_2 + " REAL, "
                    + KEY_RETURN_DETAIL_TAX_3 + " REAL, "
                    + KEY_RETURN_DETAIL_SUBTOTAL + " REAL ,"
                    + "multi_scheme  INTEGER DEFAULT 0," +
                    "t_o_v REAL DEFAULT 0," +
                    "d_v_1 REAL DEFAULT 0," +
                    "d_v_2 REAL DEFAULT 0," +
                    "t_type REAL DEFAULT 0," +
                    "t_mrp_type REAL DEFAULT 0," +
                    "t_val REAL DEFAULT 0," +
                    "mrp_price  REAL DEFAULT 0);";
            
            db.execSQL(sql);
            
            
            // Create Payment Recieved Table
            sql = "CREATE TABLE " + DATABASE_TABLE_PAYMENT_RECIEVED + " ("
                    + KEY_PAYMENT_RECIEVED_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_PAYMENT_RECIEVED_CUST_ID + " INTEGER NOT NULL, "
                    + KEY_PAYMENT_RECIEVED_EMP_ID + " INTEGER NOT NULL, "
                    + KEY_PAYMENT_RECIEVED_UPDATE + " INTEGER, "
                    + KEY_PAYMENT_RECIEVED_AMOUNT + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_LATITUDE + " REAL, "
                    + KEY_PAYMENT_RECIEVED_LONGITUDE + " REAL, "
                    + KEY_PAYMENT_RECIEVED_MAPNAME + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_START_DATETIME + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_DATETIME + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_EXECUTE_COMPLETE + " INTEGER DEFAULT 0, "
                    + KEY_PAYMENT_RECIEVED_CHEQUE_NO + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_CHEQUE_DATE + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_BANK_NAME + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_PAYMENT_TYPE + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_SELECTED_DISTRIBUTOR_ID + " TEXT, "
                    + KEY_PAYMENT_RECIEVED_DETAIL + " TEXT );";
            
            db.execSQL(sql);
            
            // Create Expense Table
            sql = "CREATE TABLE " + DATABASE_TABLE_EXPENSE + " ("
                    + KEY_EXPENSE_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_EXPENSE_AMOUNT + " TEXT, "
                    + KEY_EXPENSE_UPDATE + " INTEGER DEFAULT 0, "
                    + KEY_EXPENSE_TYPE + " INTEGER, "
                    + KEY_EXPENSE_LATITUDE + " REAL, "
                    + KEY_EXPENSE_LONGITUDE + " REAL, "
                    + KEY_EXPENSE_MAPNAME + " TEXT, "
                    + KEY_EXPENSE_START_DATETIME + " TEXT, "
                    + KEY_EXPENSE_DATETIME + " TEXT, "
                    + KEY_EXPENSE_STATUS + " INTEGER, "
                    + KEY_EXPENSE_SHOP_ID + " INTEGER, "
                    + KEY_EXPENSE_COMMITMENT_ID + " INTEGER, "
                    + KEY_EXPENSE_REMARKS + " TEXT );";
            
            db.execSQL(sql);
            
            // Create ShopVisit Table
            sql = "CREATE TABLE " + DATABASE_TABLE_SHOP_VISIT + " ("
                    + KEY_SHOP_VISIT_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SHOP_VISIT_CUST_ID + " INTEGER, "
                    + KEY_SHOP_VISIT_REASON_ID + " INTEGER, "
                    + KEY_SHOP_VISIT_UPDATE + " INTEGER DEFAULT 0, "
                    + KEY_SHOP_VISIT_LATITUDE + " REAL, "
                    + KEY_SHOP_VISIT_LONGITUDE + " REAL, "
                    + KEY_SHOP_VISIT_MAPNAME + " TEXT, "
                    + KEY_SHOP_VISIT_START_DATETIME + " TEXT, "
                    + KEY_SHOP_VISIT_DATETIME + " TEXT, "
                    + KEY_SHOP_VISIT_SELECTED_DISTRIBUTOR_ID + " TEXT, "
                    + KEY_SHOP_VISIT_REMARKS + " TEXT );";
            
            db.execSQL(sql);
            
            
            // Create Customer CAtegory Table
            sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_CATEGORY + " ("
                    + KEY_CUSTOMER_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CUSTOMER_CATEGORY_NAME + " TEXT );";
            
            db.execSQL(sql);
            
            
            // Create Customer Celeb Table
            sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_CELEB + " ("
                    + KEY_CUSTOMER_CELEB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CUSTOMER_CELEB_NAME + " TEXT );";
            
            db.execSQL(sql);
            
            
            // Create ClockInTime Table
            sql = "CREATE TABLE " + DATABASE_TABLE_CLOCKIN_TIME + " ("
                    + KEY_CLOCKIN_TIME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_CLOCKIN_TIME_CLOCKIN + " TEXT, "
                    + KEY_CLOCKIN_TIME_CLOCKOUT + " TEXT );";
            
            db.execSQL(sql);
            
            // Create Merchanzing Table
            sql = "CREATE TABLE " + DATABASE_TABLE_MERCHANDIZING + " ("
                    + KEY_MERCHANDIZING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_MERCHANDIZING_SHOP_ID + " INTEGER, "
                    + KEY_MERCHANDIZING_BRAND_ID + " INTEGER, "
                    + KEY_MERCHANDIZING_CAMPAIGN_ID + " INTEGER, "
                    + KEY_MERCHANDIZING_PRODUCT_ID + " INTEGER, "
                    + KEY_MERCHANDIZING_DATETIME + " DATETIME, "
                    + KEY_MERCHANDIZING_REMARKS + " TEXT );";
            
            db.execSQL(sql);
            
            // Create Merchanzing PLan Table
            sql = "CREATE TABLE " + DATABASE_TABLE_MERCHANDIZING_PLAN + " ("
                    + KEY_MERCHANDIZING_PLAN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_MERCHANDIZING_PLAN_NAME + " TEXT, "
                    + KEY_MERCHANDIZING_PLAN_PRODUCT_1 + " INTEGER, "
                    + KEY_MERCHANDIZING_PLAN_PRODUCT_2 + " INTEGER, "
                    + KEY_MERCHANDIZING_PLAN_PRODUCT_3 + " INTEGER );";
            
            db.execSQL(sql);
            
            // Create TOWN Table
            sql = "CREATE TABLE " + DATABASE_TABLE_TOWN + " ("
                    + KEY_TOWN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TOWN_NAME + " TEXT );";
            
            db.execSQL(sql);
            
            // Create TOWN TRAVLE Table
            sql = "CREATE TABLE " + DATABASE_TABLE_TOWN_TRAVEL + " ("
                    + KEY_TOWN_TRAVEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TOWN_TRAVEL_TOWN_1 + " INTEGER, "
                    + KEY_TOWN_TRAVEL_TOWN_2 + " INTEGER, "
                    + KEY_TOWN_TRAVEL_ONE_WAY + " TEXT, "
                    + KEY_TOWN_TRAVEL_TWO_WAY + " TEXT, "
                    + KEY_TOWN_TRAVEL_STAY_ACCOM + " TEXT );";
            
            db.execSQL(sql);
            
            // Create TARGET Table
            sql = "CREATE TABLE " + DATABASE_TABLE_TARGET + " ("
                    + KEY_TARGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TARGET_PRODUCT_ID + " INTEGER, "
                    + KEY_TARGET_QUANTITY + " TEXT );";
            
            db.execSQL(sql);
            
            // Create Travel Expense Table
            sql = "CREATE TABLE " + DATABASE_TABLE_TRAVEL_EXPENSE + " ("
                    + KEY_TRAVEL_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TRAVEL_EXPENSE_FROM_TOWN + " INTEGER, "
                    + KEY_TRAVEL_EXPENSE_TO_TOWN + " INTEGER, "
                    + KEY_TRAVEL_EXPENSE_FROM_DATE + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_TO_DATE + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_DAYS + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_TRAVELLING_AMOUNT + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_STAY_COMPENSATION_AMOUNT + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_AMOUNT + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_UPDATE + " INTEGER DEFAULT 0, "
                    + KEY_TRAVEL_EXPENSE_LATITUDE + " REAL, "
                    + KEY_TRAVEL_EXPENSE_LONGITUDE + " REAL, "
                    + KEY_TRAVEL_EXPENSE_MAPNAME + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_START_DATETIME + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_DATETIME + " TEXT, "
                    + KEY_TRAVEL_EXPENSE_STATUS + " INTEGER, "
                    + KEY_TRAVEL_EXPENSE_REMARKS + " TEXT );";
            
            db.execSQL(sql);
            
            // Create SUPPORT STATUS Table
            sql = "CREATE TABLE " + DATABASE_TABLE_SUPPORT_STATUS + " ("
                    + KEY_SUPPORT_STATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SUPPORT_STATUS_NAME + " TEXT );";
            
            db.execSQL(sql);
            
            // Create SUPPORT Table
            sql = "CREATE TABLE " + DATABASE_TABLE_SUPPORT + " ("
                    + KEY_SUPPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SUPPORT_TITLE + " TEXT, "
                    + KEY_SUPPORT_STATUS + " INTEGER, "
                    + KEY_SUPPORT_DATETIME + " TEXT, "
                    + KEY_SUPPORT_NET_OID + " INTEGER DEFAULT 0, "
                    + KEY_SUPPORT_SYNC_UPDATE + " INTEGER DEFAULT 0 );";
            
            db.execSQL(sql);
            
            // Create SUPPORT DETAIL Table
            sql = "CREATE TABLE " + DATABASE_TABLE_SUPPORT_DETAIL + " ("
                    + KEY_SUPPORT_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SUPPORT_DETAIL_SUPPORT_ID + " INTEGER, "
                    + KEY_SUPPORT_DETAIL_MESSAGE + " TEXT, "
                    + KEY_SUPPORT_DETAIL_PERSON + " INTEGER, "
                    + KEY_SUPPORT_DETAIL_DATETIME + " TEXT, "
                    + KEY_SUPPORT_DETAIL_SYNC_UPDATE + " INTEGER DEFAULT 0 );";
            
            db.execSQL(sql);
            
            
            // Create Commitment Table
            sql = "CREATE TABLE " + DATABASE_TABLE_COMMITMENT + " ("
                    + KEY_COMMITMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_COMMITMENT_SHOP_ID + " INTEGER, "
                    + KEY_COMMITMENT_FROM_DATE + " TEXT, "
                    + KEY_COMMITMENT_TO_DATE + " TEXT, "
                    + KEY_COMMITMENT_SALE_AMOUNT + " TEXT, "
                    + KEY_COMMITMENT_GIFT_AMOUNT + " TEXT, "
                    + KEY_COMMITMENT_REMARKS + " TEXT, "
                    + KEY_COMMITMENT_STATUS + " INTEGER, "
                    + KEY_COMMITMENT_DONE + " TEXT, "
                    + KEY_COMMITMENT_DATETIME + " TEXT, "
                    + KEY_COMMITMENT_UPDATE + " INTEGER DEFAULT 0 );";
            
            db.execSQL(sql);
            
            // Create Total Discount Table
            sql = "CREATE TABLE " + DATABASE_TABLE_TOTAL_DISCOUNT + " ("
                    + KEY_TOTAL_DISCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_TOTAL_DISCOUNT_VALUE + " REAL, "
                    + KEY_TOTAL_DISCOUNT_DISCOUNT + " REAL );";
            
            db.execSQL(sql);
            
            // Create Shop Stock Table
            sql = "CREATE TABLE " + DATABASE_TABLE_SHOP_STOCK + " ("
                    + KEY_SHOP_STOCK_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                    + KEY_SHOP_STOCK_CUSTOMER_ID + " INTEGER NOT NULL, "
                    + KEY_SHOP_STOCK_PRODUCT_ID + " INTEGER NOT NULL, "
                    + KEY_SHOP_STOCK_EMP_ID + " INTEGER NOT NULL, "
                    + KEY_SHOP_STOCK_QUANTITY + " TEXT, "
                    + KEY_SHOP_STOCK_DATETIME + " TEXT, "
                    + KEY_SHOP_STOCK_SYNC + " INTEGER DEFAULT 0 );";
            
            db.execSQL(sql);
            
            
            // Create ITEM_TARGET Table
            sql = "CREATE TABLE " + DATABASE_TABLE_ITEM_TARGET + " ("
                    + KEY_ITEM_TARGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_ITEM_TARGET_ITEM + " TEXT, "
                    + KEY_ITEM_TARGET_TARGET + " TEXT, "
                    + KEY_ITEM_TARGET_SOLD + " TEXT, "
                    + KEY_ITEM_TARGET_ACHIEVED + " TEXT );";
            
            db.execSQL(sql);
     
            
        }
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
            /*if (oldVersion <= 20) {

                try{

                    sql = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_TEMP_CART + " ("
                            + KEY_CUSTOMER_BRAND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                            + KEY_CUSTOMER_BRAND_BRAND_ID + " TEXT , "
                            + KEY_TEMP_CART_BRAND_NAME + " TEXT , "
                            + KEY_TEMP_CART_PROD_ID + " TEXT , "
                            + KEY_TEMP_CART_PROD_NAME + " TEXT , "
                            + KEY_TEMP_CART_PROD_PRICE + " TEXT , "
                            + KEY_TEMP_CART_PROD_UNIT + " TEXT , "
                            + KEY_TEMP_CART_PROD_DISCOUNT + " TEXT );";
                    db.execSQL(sql);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }*/
			if(oldVersion<=17)
			{
                try{

                    sql = "ALTER TABLE cust_pricing  ADD COLUMN " + KEY_SALES_ORDER_DISTRIBUTOR_INVOICE + " TEXT ;";
                    db.execSQL(sql);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

			if(oldVersion<=16)
			{
                sql = "CREATE TABLE " + DATABASE_TABLE_CUSTOMER_BRAND + " ("
                        + KEY_CUSTOMER_BRAND_ID + " TEXT PRIMARY KEY , "
                        + KEY_CUSTOMER_BRAND_BRAND_ID + " TEXT , "
                        + KEY_CUSTOMER_BRAND_CUSTOMER_ID + " TEXT );";

                db.execSQL(sql);
                sql = "CREATE TABLE " + DATABASE_TABLE_ORDER_TEMPLATE + " ("
                              + KEY_ORDER_TEMPLATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                              + KEY_ORDER_TEMPLATE_ITEM_ID + " TEXT,"
                              + KEY_ORDER_TEMPLATE_QUANTITY + " TEXT,"
                              + KEY_ORDER_TEMPLATE_CUSTOMER_ID + " TEXT);";
                
                db.execSQL(sql);
                sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER + " ("
                              + KEY_PATIENT_ORDER_ID + " INTEGER PRIMARY KEY , "
                              + KEY_PATIENT_ORDER_EMP_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_CUST_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_DIST_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_DATE_TIME + " TEXT , "
                              + KEY_PATIENT_ORDER_ORDER_UPDATE + " TEXT , "
                              + KEY_PATIENT_ORDER_PATIENT_NAME + " TEXT );";
                
                db.execSQL(sql);
                sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER2 + " ("
                              + KEY_PATIENT_ORDER_ID + " INTEGER PRIMARY KEY , "
                              + KEY_PATIENT_ORDER_EMP_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_CUST_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_DIST_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_DATE_TIME + " TEXT , "
                              + KEY_PATIENT_ORDER_ORDER_UPDATE + " TEXT , "
                              + KEY_PATIENT_ORDER_PATIENT_NAME + " TEXT );";

                db.execSQL(sql);
                sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER_DETAIL + " ("
                              + KEY_PATIENT_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY , "
                              + KEY_PATIENT_ORDER_DETAIL_ORDER_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_DETAIL_ITEM_NAME + " TEXT , "
                              + KEY_PATIENT_ORDER_DETAIL_QUANTITY + " TEXT );";
                
                db.execSQL(sql);

                sql = "CREATE TABLE " + DATABASE_TABLE_PATIENT_ORDER_DETAIL2 + " ("
                              + KEY_PATIENT_ORDER_DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                              + KEY_PATIENT_ORDER_DETAIL_ORDER_ID + " TEXT , "
                              + KEY_PATIENT_ORDER_DETAIL_ITEM_NAME + " TEXT , "
                              + KEY_PATIENT_ORDER_DETAIL_RATE + " TEXT , "
                              + KEY_PATIENT_ORDER_DETAIL_QUANTITY + " TEXT );";

                db.execSQL(sql);

            }
            if (oldVersion <= 15)
			{
				try {
					try {
						sql = "\n" +
									  "CREATE TABLE cust_pricing_temp \n" +
									  "(\n" +
									  "    id                      INTEGER  NOT NULL                                     PRIMARY KEY AUTOINCREMENT, \n" +
									  "   type_id                 TEXT DEFAULT '0',\n" +
									  "   product_id              TEXT DEFAULT '0',   \n" +
									  "   trade_price             REAL, \n" +
									  "   discount_1              REAL,   \n" +
									  "   discount_2              REAL, \n" +
									  "   trade_offer             REAL,  \n" +
									  "   scheme                  REAL,  \n" +
									  "   scheme_val              REAL,  \n" +
									  "   scheme_product          INTEGER  DEFAULT 0, \n" +
									  "   tax_1                   REAL,    \n" +
									  "   tax_filer_1             REAL,  \n" +
									  "   tax_2                   REAL,  \n" +
									  "   tax_filer_2             REAL,   \n" +
									  "   tax_3                   REAL,  \n" +
									  "   tax_filer_3             REAL,  \n" +
									  "   sub_total               REAL, \n" +
									  "   customer_id             TEXT  DEFAULT '0', \n" +
									  "   brand_id                 TEXT  DEFAULT '0',\n" +
									  "   product_type_id          TEXT  DEFAULT '0',\n" +
									  "   product_sub_type_id      TEXT  DEFAULT '0', \n" +
									  "   min_qty                 INTEGER  DEFAULT NULL, \n" +
									  "   max_qty                 INTEGER  DEFAULT NULL, \n" +
									  "   multi                   INTEGER  DEFAULT 0,    \n" +
									  "   use_defaultprice        INTEGER  DEFAULT 0,   \n" +
									  "   mrp_price               INTEGER  DEFAULT 0,   \n" +
									  "   use_defaultmrp          INTEGER  DEFAULT 0,   \n" +
									  "   min_amount              INTEGER  DEFAULT 0,   \n" +
									  "   max_amount              INTEGER  DEFAULT 0,   \n" +
									  "   customer_category_id     TEXT  DEFAULT '0',   \n" +
									  "   customer_subcategory_id  TEXT  DEFAULT '0',   \n" +
									  "   emp_id  TEXT  DEFAULT '0',   \n" +
									  " dist_id  TEXT  DEFAULT '0');";
						db.execSQL(sql);
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
				
				
				
					try {
						sql = "INSERT INTO cust_pricing_temp SELECT * FROM cust_pricing;";
					
						db.execSQL(sql);
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}
					try {
						sql = "DROP TABLE cust_pricing;";
						db.execSQL(sql);
					}
					catch(SQLException e)
					{
						e.printStackTrace();
					}
					try{
						sql="ALTER TABLE cust_pricing_temp RENAME TO cust_pricing;";
						db.execSQL(sql);
					
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				catch (SQLException e) {
					e.getMessage();
				}
			}
			if (oldVersion <= 13) {
			
				try {
				
					try {
						sql = "ALTER TABLE cust_pricing  ADD COLUMN customer_id INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
				
					try {
						sql = "ALTER TABLE cust_pricing  ADD COLUMN brand_id INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing  ADD COLUMN product_type_id INTEGER DEFAULT" + " 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing  ADD COLUMN product_sub_type_id INTEGER " + "DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing  ADD COLUMN min_qty INTEGER DEFAULT NULL;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing  ADD COLUMN max_qty INTEGER DEFAULT NULL;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing  ADD COLUMN multi INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN multi_scheme   INTEGER DEFAULT 0";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE " + DATABASE_TABLE_CUSTOMER_ROUTE + " ADD COLUMN route_day INTEGER ";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN use_defaultprice INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN mrp_price INTEGER DEFAULT 0; ";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN use_defaultmrp INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN min_amount INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN max_amount INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN customer_category_id INETEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN customer_subcategory_id INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
				
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN emp_id INETEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE cust_pricing ADD COLUMN dist_id INTEGER DEFAULT 0;";
						db.execSQL(sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
				
					//CREATE ITEM SUB TYPE TABLE
					sql = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_ITEM_SUB_TYPE + " ("
								  + KEY_ID_SUB_ITEM + " INTEGER PRIMARY KEY , "
								  + KEY_NAME_SUB_ITEM + " TEXT NOT NULL, "
								  + KEY_ITEM_ID_SUB_ITEM + " INTEGER );";
					db.execSQL(sql);
				
					Log.e("onUpgrade", sql);
					try {
						sql = "ALTER TABLE inventory ADD COLUMN " + KEY_INVENTORY_SUB_TYPE_ID +
									  " INTEGER";
						db.execSQL(sql);
					
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE inventory ADD COLUMN " + "mrp_price INTEGER";
						db.execSQL(sql);
					
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE inventory ADD COLUMN " + "tax_mrp INTEGER";
						db.execSQL(sql);
					
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE inventory ADD COLUMN " + "is_taxable  INTEGER";
						db.execSQL(sql);
					
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN " + "t_o_v REAL DEFAULT 0";
						db.execSQL(sql);
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN " + "d_v_1 REAL DEFAULT 0";
						db.execSQL(sql);
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN " + "d_v_2 REAL DEFAULT 0";
						db.execSQL(sql);
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN " + "t_type REAL DEFAULT 0";
						db.execSQL(sql);
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN " + "t_mrp_type REAL DEFAULT 0";
						db.execSQL(sql);
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN " + "t_val REAL DEFAULT 0";
						db.execSQL(sql);
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
					try {
						sql = "ALTER TABLE order_details ADD COLUMN " + "mrp_price REAL DEFAULT 0";
						db.execSQL(sql);
						Log.e("onUpgrade", sql);
					} catch (SQLException e) {
						e.getMessage();
						Log.e("SQL_EXCEPTION", e.getMessage());
					}
				
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
			
				try {
					sql = "ALTER TABLE return_details ADD COLUMN multi_scheme   INTEGER DEFAULT 0";
					db.execSQL(sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
			
				try {
					sql = "ALTER TABLE return_details ADD COLUMN " + "t_o_v REAL DEFAULT 0";
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
				try {
					sql = "ALTER TABLE return_details ADD COLUMN " + "d_v_1 REAL DEFAULT 0";
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
				try {
					sql = "ALTER TABLE return_details ADD COLUMN " + "d_v_2 REAL DEFAULT 0";
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
				try {
					sql = "ALTER TABLE return_details ADD COLUMN " + "t_type REAL DEFAULT 0";
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
				try {
					sql = "ALTER TABLE return_details ADD COLUMN " + "t_mrp_type REAL DEFAULT 0";
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
				try {
					sql = "ALTER TABLE return_details ADD COLUMN " + "t_val REAL DEFAULT 0";
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
				try {
					sql = "ALTER TABLE return_details ADD COLUMN " + "mrp_price REAL DEFAULT 0";
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
			
			
			}
		
			if (oldVersion <= 12) {
			
				try {
					sql = "CREATE TABLE  IF NOT EXISTS " + DATABASE_TABLE_SUB_SHOP_CATEGORY + " ("
								  + KEY_SUB_SHOP_CATEGORY_ID + " INTEGER, "
								  + KEY_SUB_SHOP_CATEGORY_NAME + " TEXT, "
								  + KEY_SUB_SHOP_CATEGORY_CATEGORY + " INTEGER );";
				
					db.execSQL(sql);
					Log.e("onUpgrade", sql);
				} catch (SQLException e) {
					e.getMessage();
					Log.e("SQL_EXCEPTION", e.getMessage());
				}
			
				try {
				
					sql = "ALTER TABLE customer ADD COLUMN sub_shop_cat_id INTEGER;";
				
					Log.e("onUpgrade", sql);
				
					db.execSQL(sql);
				
				} catch (SQLException e) {
					e.getMessage();
				}
			
			}
		
			if (oldVersion <= 12) {
			
				try {
					sql = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_SHOP_CATEGORY + " ("
								  + KEY_SHOP_CATEGORY_ID + " INTEGER, "
								  + KEY_SHOP_CATEGORY_NAME + " TEXT );";
					Log.e("onUpgrade", sql);
					db.execSQL(sql);
				} catch (SQLException e) {
					e.getMessage();
				}
			
				try {
				
					sql = "ALTER TABLE customer ADD COLUMN shop_cat_id INTEGER;";
				
					Log.e("onUpgrade", sql);
				
					db.execSQL(sql);
				
				} catch (SQLException e) {
					e.getMessage();
				}
			}
		
			if (oldVersion <= 12) {
			
				try {
					sql = "ALTER TABLE customer ADD COLUMN filer_non_filer INTEGER DEFAULT 0;";
					Log.e("onUpgrade", sql);
					db.execSQL(sql);
				} catch (SQLException e) {
					e.getMessage();
				}
				try {
					sql = "ALTER TABLE cust_pricing ADD COLUMN tax_filer_1 REAL DEFAULT 0;";
				
					Log.e("onUpgrade", sql);
					db.execSQL(sql);
				
				} catch (SQLException e) {
				
					e.getMessage();
				}
				try {
				
					sql = "ALTER TABLE cust_pricing ADD COLUMN tax_filer_2 REAL DEFAULT 0;";
				
					Log.e("onUpgrade", sql);
					db.execSQL(sql);
				
				
				} catch (SQLException e) {
				
					e.getMessage();
				
				}
				try {
				
					sql = "ALTER TABLE cust_pricing ADD COLUMN tax_filer_3 REAL DEFAULT 0;";
				
					Log.e("onUpgrade", sql);
				
					db.execSQL(sql);
				
				} catch (SQLException e) {
					e.getMessage();
				}
			
			
			}
		
		}
        
        
    }
    
    
   
    public void deleteVerifyStockDetail() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_PATIENT_ORDER_DETAIL;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
         deleteQuery = "DELETE FROM " + DATABASE_TABLE_PATIENT_ORDER_DETAIL2;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    
    public void deleteVerifyStock() {
        // TODO Auto-generated method stub
        
        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_PATIENT_ORDER;
        
        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
         deleteQuery = "DELETE FROM " + DATABASE_TABLE_PATIENT_ORDER2;

        Log.d("query", deleteQuery);
        db.execSQL(deleteQuery);
    }
    public String getQTYofIemById(String id)
    {
        
        String result = "0";
        if(!id.equals("")) {
            String[] Column = new String[]{KEY_QUANTITY};
            Cursor c = db.query(DATABASE_TABLE_INVENTORY, Column, "id = " + id, null, null, null, null);
            
            int iData = c.getColumnIndex(KEY_QUANTITY);
            
            try {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    result = c.getString(iData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!c.isClosed()) {
                    c.close();
                }
                
            }
            
        }
        
        
        return result;
    }
  public String getRateofIemById(String id)
    {

        String result = "0";
        if(!id.equals("")) {
            String[] Column = new String[]{KEY_SALE_PRICE};
            Cursor c = db.query(DATABASE_TABLE_INVENTORY, Column, "id = " + id, null, null, null, null);

            int iData = c.getColumnIndex(KEY_SALE_PRICE);

            try {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    result = c.getString(iData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!c.isClosed()) {
                    c.close();
                }

            }

        }


        return result;
    }

    
    public int getMaxOrderIdFromPatientOrder() {
        
        String que = "SELECT MAX(" + KEY_PATIENT_ORDER_ID + ") FROM " + DATABASE_TABLE_PATIENT_ORDER;
        
        Cursor c = db.rawQuery(que, null);
        
        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);
        
        if (!c.isClosed())
            c.close();
        
        return maxid;
        
    }


    public int getMaxOrderIdFromPatientOrder2() {

        String que = "SELECT MAX(" + KEY_PATIENT_ORDER_ID + ") FROM " + DATABASE_TABLE_PATIENT_ORDER2;

        Cursor c = db.rawQuery(que, null);

        int maxid = (c.moveToFirst() ? c.getInt(0) : 0);

        if (!c.isClosed())
            c.close();

        return maxid;

    }

    public ArrayList<HashMap<String, String>> getInventoryByBrand(String brand) {
        ArrayList<HashMap<String, String>> zone_list = new ArrayList<>();
        String query = "SELECT item_search,quantity from " + DATABASE_TABLE_INVENTORY + " where " +
                               KEY_BRAND_ID_FROM_INVENT+" = " + brand + " ORDER BY name ASC;";
        Cursor cursor = db.rawQuery(query, null, null);
        
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> hashMap = new HashMap<>();
                
                hashMap.put("item_search", cursor.getString(0));
                hashMap.put("quantity", cursor.getString(1));
                
                zone_list.add(hashMap);
                
            } while (cursor.moveToNext());
        }
        cursor.close();
        return zone_list;
    }
    public long createPatientOrderEntry(int id,String empid,int Cust_ID,int Dist_ID ,String name,int updateqty,String DateTime) {
        
        
        ContentValues cv = new ContentValues();
        
        cv.put(KEY_PATIENT_ORDER_ID, id);
        cv.put(KEY_PATIENT_ORDER_EMP_ID, empid);
        cv.put(KEY_PATIENT_ORDER_CUST_ID, Cust_ID);
        cv.put(KEY_PATIENT_ORDER_DIST_ID, Dist_ID);
        cv.put(KEY_PATIENT_ORDER_PATIENT_NAME, name);
        cv.put(KEY_PATIENT_ORDER_DATE_TIME, DateTime);
        cv.put(KEY_PATIENT_ORDER_ORDER_UPDATE, updateqty);
        long insertedId = db.insert(DATABASE_TABLE_PATIENT_ORDER, null, cv);
        
        
        return insertedId;
        
    }
    public long createPatientOrderEntry2(int id,String empid,int Cust_ID,int Dist_ID ,String name,int updateqty,String DateTime) {


        ContentValues cv = new ContentValues();

        cv.put(KEY_PATIENT_ORDER_ID, id);
        cv.put(KEY_PATIENT_ORDER_EMP_ID, empid);
        cv.put(KEY_PATIENT_ORDER_CUST_ID, Cust_ID);
        cv.put(KEY_PATIENT_ORDER_DIST_ID, Dist_ID);
        cv.put(KEY_PATIENT_ORDER_PATIENT_NAME, name);
        cv.put(KEY_PATIENT_ORDER_DATE_TIME, DateTime);
        cv.put(KEY_PATIENT_ORDER_ORDER_UPDATE, updateqty);
        long insertedId = db.insert(DATABASE_TABLE_PATIENT_ORDER2, null, cv);


        return insertedId;

    }
    public ArrayList<HashMap<String, String>> getPatientOrderList() {
        
        
        
        Cursor c = db.rawQuery("select * from "+DATABASE_TABLE_PATIENT_ORDER+"",null );
        
        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        
        
        
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(REPORT_FIRST_COLUMN, c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_ID)));
                 //   map.put(REPORT_SECOND_COLUMN, getBrandName(c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_PATIENT_NAME))));
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
                    DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
                    java.util.Date dt = null;
                    
                    dt = df.parse(c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_DATE_TIME)));
                    map.put(REPORT_THIRD_COLUMN,dt.toLocaleString() );
                    map.put(REPORT_FOURTH_COLUMN, c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_PATIENT_NAME)));
                    
                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return result;
        
    }

    public ArrayList<HashMap<String, String>> getPatientOrderList2() {



        Cursor c = db.rawQuery("select * from "+DATABASE_TABLE_PATIENT_ORDER2+"",null );

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();



        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(REPORT_FIRST_COLUMN, c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_ID)));
                 //   map.put(REPORT_SECOND_COLUMN, getBrandName(c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_PATIENT_NAME))));
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    //DateFormat dfProper = new SimpleDateFormat("dd-MMM-yy\nK:mm a");
                    DateFormat dfProper = new SimpleDateFormat("d MMM yyyy, hh:mm a");
                    java.util.Date dt = null;

                    dt = df.parse(c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_DATE_TIME)));
                    map.put(REPORT_THIRD_COLUMN,dt.toLocaleString() );
                if( !c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_CUST_ID)).equals("0")) {
                    map.put(REPORT_FOURTH_COLUMN, getCustomerName(c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_CUST_ID))));
                }
                else{

                    map.put(REPORT_FOURTH_COLUMN, "N/A");

                }
                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    public void createPatientOrderDetail(int id, List<AutoCompleteTextView> ItemAutoCompleteList, List<EditText> NoOfItemsList)
    {
        ContentValues cv = new ContentValues();
        for(int i=0;i<ItemAutoCompleteList.size();i++) {
            cv.put(KEY_PATIENT_ORDER_DETAIL_ORDER_ID, id);
            cv.put(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME,getInventoryID( ItemAutoCompleteList.get(i).getText().toString()));
            cv.put(KEY_PATIENT_ORDER_DETAIL_QUANTITY, Integer.parseInt(NoOfItemsList.get(i).getText().toString()));
            long insertedId = db.insert(DATABASE_TABLE_PATIENT_ORDER_DETAIL, null, cv);
        }
        
        
        
    }
    public void createPatientOrderDetail2(int id, List<AutoCompleteTextView> ItemAutoCompleteList, List<EditText> NoOfItemsList,List<TextView> RateList)
    {
        ContentValues cv = new ContentValues();
        for(int i=0;i<ItemAutoCompleteList.size();i++) {
            cv.put(KEY_PATIENT_ORDER_DETAIL_ORDER_ID, id);
            cv.put(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME,getInventoryID( ItemAutoCompleteList.get(i).getText().toString()));
            cv.put(KEY_PATIENT_ORDER_DETAIL_QUANTITY, Integer.parseInt(NoOfItemsList.get(i).getText().toString()));
            cv.put(KEY_PATIENT_ORDER_DETAIL_RATE, Integer.parseInt(RateList.get(i).getText().toString()));
            long insertedId = db.insert(DATABASE_TABLE_PATIENT_ORDER_DETAIL2, null, cv);
        }



    }
    public void createPatientOrderDetailsync(int id, String Item, String Qty)
    {
        ContentValues cv = new ContentValues();
        //for(int i=0;i<ItemAutoCompleteList.size();i++) {
        cv.put(KEY_PATIENT_ORDER_DETAIL_ORDER_ID, id);
        cv.put(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME,Item);
        cv.put(KEY_PATIENT_ORDER_DETAIL_QUANTITY, Integer.parseInt(Qty));
        long insertedId = db.insert(DATABASE_TABLE_PATIENT_ORDER_DETAIL, null, cv);
        //}
        
        
        
    }
    public void createPatientOrderDetailsync2(int id, String Item, String Qty)
    {
        ContentValues cv = new ContentValues();
        //for(int i=0;i<ItemAutoCompleteList.size();i++) {
        cv.put(KEY_PATIENT_ORDER_DETAIL_ORDER_ID, id);
        cv.put(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME,Item);
        cv.put(KEY_PATIENT_ORDER_DETAIL_QUANTITY, Integer.parseInt(Qty));
        long insertedId = db.insert(DATABASE_TABLE_PATIENT_ORDER_DETAIL2, null, cv);
        //}



    }
    public ArrayList<HashMap<String, String>> getPatientOrderList_By_OrderID(String OrderID)
    {
        
        
        
        Cursor c = db.rawQuery("select * from "+DATABASE_TABLE_PATIENT_ORDER_DETAIL+" where "+KEY_PATIENT_ORDER_DETAIL_ORDER_ID+" = "+OrderID,null );
        
        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        
        
        
        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(REPORT_FIRST_COLUMN, getSelectedItemName(c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME))));
                    map.put(REPORT_SECOND_COLUMN, c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_QUANTITY)));
                    
                    
                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
            
        }
        
        
        return result;
        
    }
    public ArrayList<HashMap<String, String>> getPatientOrderList_By_OrderID2(String OrderID)
    {



        Cursor c = db.rawQuery("select * from "+DATABASE_TABLE_PATIENT_ORDER_DETAIL2+" where "+KEY_PATIENT_ORDER_DETAIL_ORDER_ID+" = "+OrderID,null );

        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();



        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put(REPORT_FIRST_COLUMN, getSelectedItemName(c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME))));
                    map.put(REPORT_SECOND_COLUMN, c.getString(c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_QUANTITY)));


                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    public ArrayList<HashMap<String, String>> getqtyOrderDetail(String orderId) {

        int typId = Integer.parseInt(orderId);
        // int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_PATIENT_ORDER_DETAIL + " WHERE " + KEY_PATIENT_ORDER_DETAIL_ORDER_ID + " = " + typId;

        Cursor c = db.rawQuery(selectQuery, null);


        int iProdId = c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME);
        int iQty = c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_QUANTITY);
        int iorderid = c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_ORDER_ID);


        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("prd_id", c.getString(iProdId));
                    map.put("qty", c.getString(iQty));
                    map.put("order_id", c.getString(iorderid));


                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }

    public ArrayList<HashMap<String, String>> getqtyOrderDetail2(String orderId) {

        int typId = Integer.parseInt(orderId);
        // int proId = Integer.parseInt(prodId);

        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + DATABASE_TABLE_PATIENT_ORDER_DETAIL2 + " WHERE " + KEY_PATIENT_ORDER_DETAIL_ORDER_ID + " = " + typId;

        Cursor c = db.rawQuery(selectQuery, null);


        int iProdId = c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_ITEM_NAME);
        int iQty = c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_QUANTITY);
        int iorderid = c.getColumnIndex(KEY_PATIENT_ORDER_DETAIL_ORDER_ID);


        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("prd_id", c.getString(iProdId));
                    map.put("qty", c.getString(iQty));
                    map.put("order_id", c.getString(iorderid));


                    //Log.d("Hashid", c.getString(0));

                    result.add(map);
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }
        }

        return result;


    }
    public ArrayList<HashMap<String, String>> getQtyOrder() {
        // TODO Auto-generated method stub


        String[] Columns = new String[]{
                KEY_PATIENT_ORDER_PATIENT_NAME,
                KEY_PATIENT_ORDER_ID,
                KEY_PATIENT_ORDER_EMP_ID,
                KEY_PATIENT_ORDER_CUST_ID,
                KEY_PATIENT_ORDER_DIST_ID,
                KEY_PATIENT_ORDER_DATE_TIME,
                KEY_PATIENT_ORDER_ORDER_UPDATE
        };


        Cursor c = db.query(DATABASE_TABLE_PATIENT_ORDER, Columns, KEY_PATIENT_ORDER_ORDER_UPDATE + " = 1 ", null, null, null, null);

        String resultId = "";


        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

//        int iQty = c.getColumnIndex(KEY_QUANTITY);
        int pname = c.getColumnIndex(   KEY_PATIENT_ORDER_PATIENT_NAME);
        int oid = c.getColumnIndex(KEY_PATIENT_ORDER_ID);
        int oeid = c.getColumnIndex(KEY_PATIENT_ORDER_EMP_ID);
        int ocid = c.getColumnIndex(KEY_PATIENT_ORDER_CUST_ID);
        int odid = c.getColumnIndex(KEY_PATIENT_ORDER_DIST_ID);
        int odate = c.getColumnIndex(KEY_PATIENT_ORDER_DATE_TIME);
        int oupdate = c.getColumnIndex(  KEY_PATIENT_ORDER_ORDER_UPDATE);



        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", c.getString(oid));
                    map.put("customer_id", c.getString(ocid));
                    map.put("emp_id", c.getString(oeid));
                    map.put("brand_id", c.getString(pname));
                    map.put("location_id", c.getString(       odid));
                    map.put("datetime", c.getString(odate));








                    result.add(map);
                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    public ArrayList<HashMap<String, String>> getQtyOrder2() {
        // TODO Auto-generated method stub


        String[] Columns = new String[]{
                KEY_PATIENT_ORDER_PATIENT_NAME,
                KEY_PATIENT_ORDER_ID,
                KEY_PATIENT_ORDER_EMP_ID,
                KEY_PATIENT_ORDER_CUST_ID,
                KEY_PATIENT_ORDER_DIST_ID,
                KEY_PATIENT_ORDER_DATE_TIME,
                KEY_PATIENT_ORDER_ORDER_UPDATE
        };


        Cursor c = db.query(DATABASE_TABLE_PATIENT_ORDER2, Columns, KEY_PATIENT_ORDER_ORDER_UPDATE + " = 1 ", null, null, null, null);

        String resultId = "";


        //HashMap result = new HashMap();
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

//        int iQty = c.getColumnIndex(KEY_QUANTITY);
        int pname = c.getColumnIndex(   KEY_PATIENT_ORDER_PATIENT_NAME);
        int oid = c.getColumnIndex(KEY_PATIENT_ORDER_ID);
        int oeid = c.getColumnIndex(KEY_PATIENT_ORDER_EMP_ID);
        int ocid = c.getColumnIndex(KEY_PATIENT_ORDER_CUST_ID);
        int odid = c.getColumnIndex(KEY_PATIENT_ORDER_DIST_ID);
        int odate = c.getColumnIndex(KEY_PATIENT_ORDER_DATE_TIME);
        int oupdate = c.getColumnIndex(  KEY_PATIENT_ORDER_ORDER_UPDATE);



        try {
            if (c.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", c.getString(oid));
                    map.put("customer_id", c.getString(ocid));
                    map.put("emp_id", c.getString(oeid));
                    map.put("brand_id", c.getString(pname));
                    map.put("location_id", c.getString(       odid));
                    map.put("datetime", c.getString(odate));








                    result.add(map);
                } while (c.moveToNext());


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!c.isClosed()) {
                c.close();
            }

        }


        return result;

    }
    public void DeleteFromCustomerBrand() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_CUSTOMER_BRAND + "";

        db.execSQL(deleteQuery);
        Log.d("query", deleteQuery);

    }
    public void insertInCustomerBrand(String id, String brand_id, String customer_id) {
        ContentValues values = new ContentValues();

        values.put(KEY_CUSTOMER_BRAND_ID, id);
        values.put(KEY_CUSTOMER_BRAND_BRAND_ID, brand_id);
        values.put(KEY_CUSTOMER_BRAND_CUSTOMER_ID, customer_id);

        db.insert(DATABASE_TABLE_CUSTOMER_BRAND, null, values);
    }
    public ArrayList<HashMap<String, String>> getStoreByZone() {
        ArrayList<HashMap<String, String>> zone_list = new ArrayList<>();
        String query = "SELECT id,company_name from " + DATABASE_TABLE_CUSTOMER_DETAILS + "   ORDER " +
                "BY company_name  ASC;";
        Cursor cursor = db.rawQuery(query, null, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("id", cursor.getString(0));
                hashMap.put("name", cursor.getString(1));

                zone_list.add(hashMap);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return zone_list;
    }
    public ArrayList<HashMap<String, String>> getBrandByStore(String store)

    {
        ArrayList<HashMap<String, String>> zone_list = new ArrayList<>();
        String query = "SELECT id,"+KEY_CUSTOMER_BRAND_BRAND_ID+" from " + DATABASE_TABLE_CUSTOMER_BRAND + " where " +
                KEY_CUSTOMER_BRAND_CUSTOMER_ID + " = " + store + " ORDER BY "+KEY_CUSTOMER_BRAND_BRAND_ID+" ASC;";
        Cursor cursor = db.rawQuery(query, null, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("id", cursor.getString(0));
                hashMap.put(KEY_CUSTOMER_BRAND_BRAND_ID, cursor.getString(1));

                zone_list.add(hashMap);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return zone_list;
    }
    public ArrayList<HashMap<String, String>> getBrandByStore()

    {
        ArrayList<HashMap<String, String>> zone_list = new ArrayList<>();
        String query = "SELECT id,"+KEY_CUSTOMER_BRAND_BRAND_ID+" from " + DATABASE_TABLE_CUSTOMER_BRAND +" ORDER BY "+KEY_CUSTOMER_BRAND_BRAND_ID+" ASC;";
        Cursor cursor = db.rawQuery(query, null, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put("id", cursor.getString(0));
                hashMap.put(KEY_CUSTOMER_BRAND_BRAND_ID, cursor.getString(1));

                zone_list.add(hashMap);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return zone_list;
    }
    public ArrayList<String> getBrandsName() {

        ArrayList<String> list = new ArrayList<>();
        try {

            Cursor cursor = db.rawQuery("select id,name from product_brands ORDER BY name ASC; ",
                    null);

            cursor.moveToFirst();
            if (!cursor.getString(cursor.getColumnIndex("name")).isEmpty()) {
                list.add(cursor.getString(cursor.getColumnIndex("name")));
                while (cursor.moveToNext()) {
                    list.add(cursor.getString(cursor.getColumnIndex("name")));
                }
            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.e("CrashException", e.getMessage());
        } catch (CursorIndexOutOfBoundsException e) {
            Log.e("CrashException", e.getMessage());
        }
        return list;
    }
    String getTaxOneByOrderId( String orderID )
    {
        Cursor cursor = db.rawQuery("select "+KEY_ORDER_DETAIL_TAX_1+" from   "+DATABASE_TABLE_ORDER_DETAILS+" where "+KEY_ORDER_DETAIL_ORDER_ID+" == "+orderID,null);
    
    if(cursor.moveToFirst())
    {
       return cursor.getString(cursor.getColumnIndex(KEY_ORDER_DETAIL_TAX_1));
    }
    else
        {
        return "0";
    }
    }
    String getTaxTwoByOrderId( String orderID )
    {
        Cursor cursor = db.rawQuery("select "+KEY_ORDER_DETAIL_TAX_2+" from   "+DATABASE_TABLE_ORDER_DETAILS+" where "+KEY_ORDER_DETAIL_ORDER_ID+" == "+orderID,null);
        
        if(cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(KEY_ORDER_DETAIL_TAX_2));
        }
        else
        {
            return "0";
        }
    }
    String getTaxThreeByOrderId( String orderID )
    {
        Cursor cursor = db.rawQuery("select "+KEY_ORDER_DETAIL_TAX_3+" from   "+DATABASE_TABLE_ORDER_DETAILS+" where "+KEY_ORDER_DETAIL_ORDER_ID+" == "+orderID,null);
        
        if(cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(KEY_ORDER_DETAIL_TAX_3));
        }
        else
        {
            return "0";
        }
    }
    
    
    String getTaxOneByReturnOrderId( String orderID )
    {
        Cursor cursor = db.rawQuery("select "+KEY_ORDER_DETAIL_TAX_1+" from   "+DATABASE_TABLE_RETURN_DETAILS+" where "+KEY_ORDER_DETAIL_ORDER_ID+" == "+orderID,null);
        
        if(cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(KEY_ORDER_DETAIL_TAX_1));
        }
        else
        {
            return "0";
        }
    }
    String getTaxTwoByReturnOrderId( String orderID )
    {
        Cursor cursor = db.rawQuery("select "+KEY_ORDER_DETAIL_TAX_2+" from   "+DATABASE_TABLE_RETURN_DETAILS+" where "+KEY_ORDER_DETAIL_ORDER_ID+" == "+orderID,null);
        
        if(cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(KEY_ORDER_DETAIL_TAX_2));
        }
        else
        {
            return "0";
        }
    }
    String getTaxThreeByReturnOrderId( String orderID )
    {
        Cursor cursor = db.rawQuery("select "+KEY_ORDER_DETAIL_TAX_3+" from   "+DATABASE_TABLE_RETURN_DETAILS+" where "+KEY_ORDER_DETAIL_ORDER_ID+" == "+orderID,null);
        
        if(cursor.moveToFirst())
        {
            return cursor.getString(cursor.getColumnIndex(KEY_ORDER_DETAIL_TAX_3));
        }
        else
        {
            return "0";
        }
    }
/*
    //TempCart table details
    public void DeleteFromTempCart() {
        // TODO Auto-generated method stub

        String deleteQuery = "DELETE FROM " + DATABASE_TABLE_TEMP_CART + "";

        db.execSQL(deleteQuery);
        Log.d("query", deleteQuery);

    }
    public void insertInTempCart(String brand_id, ProductModel productModel) {
        ContentValues values = new ContentValues();

//        values.put(KEY_CUSTOMER_BRAND_ID, id);
        values.put(KEY_CUSTOMER_BRAND_BRAND_ID, brand_id);
        values.put(KEY_TEMP_CART_BRAND_NAME, getBrandsName(brand_id));
        values.put(KEY_TEMP_CART_PROD_ID, productModel.getProd_id());
        values.put(KEY_TEMP_CART_PROD_NAME, productModel.getProd_name());
        values.put(KEY_TEMP_CART_PROD_PRICE, productModel.getProd_price());
        values.put(KEY_TEMP_CART_PROD_UNIT, productModel.getProd_qty());
        values.put(KEY_TEMP_CART_PROD_DISCOUNT, productModel.getDiscount());



        db.insert(DATABASE_TABLE_TEMP_CART, null, values);
    }*/
}