package com.cpm.bira.Constant;

import android.os.Environment;

/**
 * Created by deepakp on 19-02-2018.
 */

public class CommonString {

    public static final int LOGIN_SERVICE = 1;
    public static final int DOWNLOAD_ALL_SERVICE = 2;
    public static final int COVERAGE_DETAIL = 3;
    public static final int UPLOADJCPDetail = 4;
    public static final int UPLOADJsonDetail = 5;
    public static final int COVERAGEStatusDetail = 6;
    public static final int CHECKOUTDetail = 7;
    public static final int DELETE_COVERAGE = 8;
    public static final int SUP_ATTENDANCE = 10;
    public static final int DEVIATION_DETAILS = 11;
    public static final int TAG_FROM_PREVIOUS = 0;
    public static final int TAG_FROM_CURRENT = 1;

    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATE = "date";
    public static final String KEY_YYYYMMDD_DATE = "yyyymmddDate";
    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_FAILURE_IMAGE = "Images not uploaded please try again";
    public static final String KEY_PATH = "path";
    public static final String KEY_NOTICE_BOARD_LINK = "NOTICE_BOARD_LINK";
    public static final String KEY_VERSION = "APP_VERSION";
    public static final String KEY_STORE_IN_TIME = "STORE_IN_TIME";
    public static final String MESSAGE_NUMBER_FORMATE_EXEP = "Invalid Mid";
    public static final String KEY_UPLOAD_STATUS = "Upload_Status";
    public static final String KEY_CHECK_IN_IMAGE = "CheckInImage";
    public static final String KEY_SKU_ID = "Sku_Id";
    public static final String KEY_SKU = "Sku";
    public static final int CAPTURE_MEDIA = 1;
    public static final String KEY_IMAGE1 = "IMAGE1";
    public static final String KEY_EXIST = "Exist";

    public static final String URL2 = "http://bira.parinaam.in/webservice/bira.svc/";
    public static final String URL3 = "http://bira.parinaam.in/webservice/Imageupload.asmx/";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/.Bira_All_Images/";
    public static final String KEY_DOWNLOAD_INDEX = "download_Index";
    public static final String BACKUP_PATH = Environment.getExternalStorageDirectory() +"/Bira_backup/";
    public static final String MESSAGE_EXCEPTION = "Problem Occured : Report The Problem To Parinaam ";
    public static final String MESSAGE_CHANGED = "Invalid UserId Or Password";
    public static final String MESSAGE_INTERNET_NOT_AVALABLE = "No Internet Connection.Please Check Your Network Connection";
    public static final String MESSAGE_SOCKETEXCEPTION = "Network Communication Failure. Please Check Your Network Connection";
    public static final String MESSAGE_NO_RESPONSE_SERVER = "Server Not Responding.Please try again.";
    public static final String MESSAGE_INVALID_JSON = "Problem Occured while parsing Json : invalid json data";
    public static final String MESSAGE_XmlPull = "Problem Occured xml pull: Report The Problem To Parinaam";
    public static final String KEY_P = "P";
    public static final String KEY_D = "D";
    public static final String KEY_U = "U";
    public static final String KEY_C = "C";
    public static final String KEY_N = "N";
    public static final String KEY_CHECK_IN = "I";
    public static final String KEY_Y = "Y";
    public static final String KEY_OUTLET_TODAY = "OUTLET_today";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_DISTRIBUTOR_ID = "Distributor_Id";

    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_SUB_REASON_ID = "SUB_REASON_ID";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_ID = "_id";
    public static final String TAG_OBJECT = "OBJECT";
    public static final String TABLE_COVERAGE_DATA = "COVERAGE_DATA";
    public static final String KEY_IMAGE = "Image";
    public static final String KEY_COVERAGE_REMARK = "REMARK";

    public static final String DOWNLOAD_STATUS = "download_status";
    public static final String KEY_IMAGE2 = "IMAGE2";
    public static final String KEY_IMAGE3 = "IMAGE3";
    public static final String TABLE_CONSUMER_PROMO_COUPON = "Consumer_Promo_Coupon";
    public static final String TABLE_COMPETITION_VISIBILITY = "Competition_Visibility";
    public static final String TABLE_PREVIOUS_DAY_STOCK = "Previous_Day_Vsibility";
    public static final String TABLE_BIRA_CHILLER = "Bira_Chiller";
    public static final String TABLE_RETAILER_OWNED_CHILLER = "Retailer_Owned_Chiller";
    public static final String TABLE_POSM_DEPLOYMENT = "POSM_Deployment";


    public static final String KEY_STATE_ID = "State_Id";
    public static final String Store_Id="Store_Id";

    public static final String TABLE_LOGIN_VISITED_DATE = "LOGIN_VISITED_DATE";
    public static final String KEY_PRESENT = "Present";
    public static final String KEY_QUYANTITY = "Quantity";
    public static final String KEY_STORE_TYPE_ID = "Store_Type_Id";
    public static final String KEY_CLOSING_STOCK = "Closing_Stock";
    public static final String FILE_PATH_Downloaded = Environment.getExternalStorageDirectory() + "/Bira_download_Img/";
    public static final String KEY_CAHRGING = "Charging";
    public static final String KEY_CAPACITY = "Capacity";
    public static final String KEY_PURITY = "Purity";
    public static final String KEY_OLD_POSM_DEPLOYMENT = "Old_POSM_Deployment";
    public static final String KEY_NEW_POSM_DEPLOYMENT = "New_POSM_Deployment";
    public static final String KEY_POSM_ID = "Posm_Id";
    public static final String KEY_POSM = "Posm";
    public static final String CHILLER_FLAG = "Chiller_Flag";
    public static final String KEY_STOREVISITED_STATUS = "STOREVISITED_STATUS";

    public static final String CREATE_TABLE_CONSUMER_PROMO_COUPON = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CONSUMER_PROMO_COUPON
            + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Store_Id + " INTEGER,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_PRESENT + " VARCHAR,"
            + KEY_QUYANTITY + " VARCHAR)";


    public static final String CREATE_TABLE_COMPETITION_VISIBILITY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COMPETITION_VISIBILITY
            + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Store_Id + " INTEGER,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_PRESENT + " VARCHAR,"
            + KEY_IMAGE1 + " VARCHAR,"
            + KEY_IMAGE2 + " VARCHAR,"
            + KEY_IMAGE3 + " VARCHAR)";

    public static final String CREATE_TABLE_PREVIOUS_DAY_STOCK = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PREVIOUS_DAY_STOCK
            + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Store_Id + " INTEGER,"
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_SKU_ID + " VARCHAR,"
            + KEY_SKU+ " VARCHAR,"
            + KEY_PRESENT + " VARCHAR,"
            + KEY_CLOSING_STOCK + " VARCHAR)";


    public static final String CREATE_TABLE_VISITED_DATE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_LOGIN_VISITED_DATE + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_VISIT_DATE + " VARCHAR )";
    public static final String KEY_STORE_NAME = "Store_Name";

    private static final String KEY_CHECK_OUT_IMAGE = "Checkout_Image";
    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE  IF NOT EXISTS " + TABLE_COVERAGE_DATA
            + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Store_Id + " INTEGER,USER_ID VARCHAR, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_LATITUDE + " VARCHAR,"
            + KEY_LONGITUDE + " VARCHAR,"
            + KEY_IMAGE + " VARCHAR,"
            + KEY_REASON_ID + " INTEGER,"
            + KEY_SUB_REASON_ID + " INTEGER,"
            + KEY_COVERAGE_REMARK + " VARCHAR,"
            + KEY_CHECK_OUT_IMAGE + " VARCHAR,"
            + KEY_UPLOAD_STATUS + " VARCHAR,"
            + KEY_TYPE+ " VARCHAR,"
            + KEY_STATE_ID + " VARCHAR,"
            + KEY_CHECK_IN_IMAGE + " VARCHAR,"
            + KEY_REASON + " VARCHAR)";


    public static final String CREATE_TABLE_RETAILER_ONWED_CHILLER = "CREATE TABLE  IF NOT EXISTS " + TABLE_BIRA_CHILLER
            + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Store_Id + " INTEGER,USER_ID VARCHAR, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_EXIST + " VARCHAR,"
            + KEY_CAHRGING + " VARCHAR,"
            + KEY_CAPACITY + " VARCHAR,"
            + KEY_PURITY + " INTEGER,"
            + KEY_IMAGE1 + " INTEGER,"
            + KEY_SKU_ID + " VARCHAR,"
            + KEY_SKU + " VARCHAR,"
            + KEY_QUYANTITY + " VARCHAR,"
            + KEY_PRESENT + " VARCHAR)";

    public static final String CREATE_TABLE_BIRA_CHILLER = "CREATE TABLE  IF NOT EXISTS " + TABLE_RETAILER_OWNED_CHILLER
            + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Store_Id + " INTEGER,USER_ID VARCHAR, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_EXIST + " VARCHAR,"
            + KEY_CAHRGING + " VARCHAR,"
            + KEY_CAPACITY + " VARCHAR,"
            + KEY_PURITY + " INTEGER,"
            + KEY_IMAGE1 + " INTEGER,"
            + KEY_SKU_ID + " VARCHAR,"
            + KEY_SKU + " VARCHAR,"
            + KEY_QUYANTITY + " VARCHAR,"
            + KEY_PRESENT + " VARCHAR)";


    public static final String CREATE_TABLE_POSM_DEPLOYMENT = "CREATE TABLE  IF NOT EXISTS " + TABLE_POSM_DEPLOYMENT
            + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + Store_Id + " INTEGER,USER_ID VARCHAR, "
            + KEY_VISIT_DATE + " VARCHAR,"
            + KEY_EXIST + " VARCHAR,"
            + KEY_OLD_POSM_DEPLOYMENT + " VARCHAR,"
            + KEY_NEW_POSM_DEPLOYMENT + " VARCHAR,"
            + KEY_POSM_ID + " VARCHAR,"
            + KEY_POSM + " VARCHAR,"
            + KEY_REASON_ID + " VARCHAR,"
            + KEY_REASON + " VARCHAR,"
            + KEY_IMAGE1 + " VARCHAR)";

    public static final String TABLE_STORE_GEOTAGGING = "STORE_GEOTAGGING";

    public static final String CREATE_TABLE_STORE_GEOTAGGING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STORE_GEOTAGGING
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + "Store_Id"
            + " INTEGER,"

            + "LATITUDE"
            + " VARCHAR,"

            + "LONGITUDE"
            + " VARCHAR,"

            + "GEO_TAG"
            + " VARCHAR,"

            + "STATUS"
            + " VARCHAR,"

            + "FRONT_IMAGE" + " VARCHAR)";


}
