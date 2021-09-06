package com.blazeminds.pos.webservice_url;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


public interface RetrofitWebService {

    @FormUrlEncoded
    @POST("/check_user.php")
    void sign_in(
            @Field("user") String username,
            @Field("pass") String password,
            @Field("imei") String imei,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/check_user_inactive.php")
    void chk_inactive(
            @Field("emp_id") String empID,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/sync_all_data_chk2.php")
    void sync_alldata(
            @Field("emp_id") String empID,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/sync_all_data_chk2.php")
    void sync_alldata(
            @Field("emp_id") String empID,
            @Field("selected_route") String selectedRoute,
            @Field("selected_distributor") String selectedDistributor,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/salesman_settings.php")
    void salesman_settings(
            @Field("salesman_id") String empID,
            Callback<Response> callback);
    
    
    @FormUrlEncoded
    @POST("/sync_everyinsert2.php")
    void sync_everyinsert(
            @Field("emp_id") String empID,
            @Field("order_val") String order_val,
            @Field("sr_val") String sales_return_val,
            @Field("cust_gps_val") String cust_gps_val,
            @Field("cust_val") String cust_val,
            @Field("brand_val") String brand_val,
            @Field("type_val") String type_val,
            @Field("item_val") String item_val,
            @Field("payment_val") String payment_val,
            @Field("marketing_expense") String expense_val,
            @Field("shop_checkin") String shopVisit_val,
            @Field("routes") String routes_val,
            @Field("support_val") String support_val,
            @Field("support_val_convo") String support_val_convo,
            @Field("shop_stock_val") String shop_stock_val,
            @Field("selected_route") String selectedRoute,
            @Field("selected_distributor") String selectedDistributor,
            @Field("store_qty_val") String store_qty_val,
            @Field("sample_request_values") String store_qty_val2,
            
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/sync_everything.php")
    void sync_everything(
            @Field("emp_id") String empID,
            Callback<Response> callback);
    
    
    @FormUrlEncoded
    @POST("/sync_everything.php")
    void sync_everythingTimeIn(
            @Field("emp_id") String empID,
            @Field("time_in") String time_vals,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/sync_attendance.php")
    void syncTimeSheet(
            @Field("emp_id") String empID,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/sync_everything.php")
    void sync_everythingMerchandizing(
            @Field("emp_id") String empID,
            @Field("merchandizing") String merchandizingVals,
            Callback<Response> callback);
    
    
    @FormUrlEncoded
    @POST("/sync_chknet.php")
    void sync_chknet(
            @Field("emp_id") String empID,
            @Field("time") String time,
            @Field("status") String status,
            Callback<Response> callback);
    
     
    
    @FormUrlEncoded
    @POST("/customer_last_details.php")
    void    customer_last_details(
            @Field("emp_id") String empID,
            @Field("cust_id") String cust_id,
       
            Callback<Response> callback);
    
    
    @FormUrlEncoded
    @POST("/sync_chknet.php")
    void sync_chknet(
            @Field("status1") String statusVals,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/sync_phonestart.php")
    void sync_phoneStart(
            @Field("status2") String statusVals,
            Callback<Response> callback
    );
    
    @FormUrlEncoded
    @POST("/sync_phonestart.php")
    void sync_phoneStart(
            @Field("emp_id") String empID,
            @Field("time") String time,
            Callback<Response> callback
    );
    
    @FormUrlEncoded
    @POST("/email_notification.php")
    void send_email(
            @Field("email") String emailAdd,
            @Field("empname") String emp_name,
            @Field("time") String time,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("area") String area,
            Callback<Response> callback
    );
    
    @FormUrlEncoded
    @POST("/salesman_checkin_random.php")
    void checkin_random(
            @Field("current_loc") String currentLoc,
            @Field("lat") String latitude,
            @Field("long") String longitude,
            @Field("emp_id") String empID,
            Callback<Response> callback);
    
    
    @FormUrlEncoded
    @POST("/salesman_checkin_offline.php")
    void checkin_offline(
            @Field("offline_values") String offlineVals,
            Callback<Response> callback
    );
    
    
    @FormUrlEncoded
    @POST("/forget_pass_admin.php")
    void forget_password(
            @Field("email") String email,
            @Field("id") String empID,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/insert_messaging.php")
    void SendMsgWeb(
            @Field("sent_to") String sent_to,
            @Field("sent_from") String sent_from,
            @Field("date") String date,
            @Field("subject") String subject,
            @Field("msg") String msg,
            @Field("imp") String imp,
            Callback<Response> callback
    );
    
    @FormUrlEncoded
    @POST("/report_to.php")
    void report_to(
            @Field("report") String report,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/timein.php")
    void time_in(
            @Field("emp_id") String empID,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/timeout.php")
    void time_out(
            @Field("emp_id") String empID,
            @Field("clockin_id") String clockin_id,
            @Field("task") String task,
            Callback<Response> callback);
    
    
    @FormUrlEncoded
    @POST("/salesman_checkin.php")
    void salesman_checkin(
            @Field("current_loc") String current_loc,
            @Field("lat") String lat,
            @Field("long") String longi,
            @Field("emp_id") String emp_id,
            @Field("cust_id") String cust_id,
            Callback<Response> callback);
    
    @FormUrlEncoded
    @POST("/salesman_checkin.php")
    void salesman_checkin(
            @Field("current_loc") String current_loc,
            @Field("lat") String lat,
            @Field("long") String longi,
            @Field("emp_id") String emp_id,
            Callback<Response> callback
    
    );
    
    @FormUrlEncoded
    @POST("/sync_profile.php")
    void sync_profile(
            @Field("id") String id,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("comp") String comp,
            @Field("cell") String cell,
            @Field("p1") String p1,
            @Field("p2") String p2,
            @Field("add") String add,
            @Field("city") String city,
            @Field("state") String state,
            @Field("country") String country,
            @Field("zip") String zip,
            @Field("email") String email,
            Callback<Response> callback
    
    );

    @FormUrlEncoded
    @POST("/stock_confirm.php")
    void stock_confirm(
            @Field("emp_id") String emp_id,


            @Field("store") String store,
            @Field("brand") String brand,


            Callback<Response> callback

    );
    @FormUrlEncoded
    @POST("/order_req.php")
    void order_req(
            @Field("emp_id") String emp_id,
            
            
            @Field("store") String store,
            @Field("brand") String brand,
            
            
            Callback<Response> callback
    
    );
}
