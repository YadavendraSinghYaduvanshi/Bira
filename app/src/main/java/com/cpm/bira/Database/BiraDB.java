package com.cpm.bira.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.GetterSetter.BrandMaster;
import com.cpm.bira.GetterSetter.BrandMasterGetterSetter;
import com.cpm.bira.GetterSetter.CommonChillerDataGetterSetter;
import com.cpm.bira.GetterSetter.CommonDataGetterSetter;
import com.cpm.bira.GetterSetter.CoverageBean;
import com.cpm.bira.GetterSetter.GeotaggingBeans;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.GetterSetter.JourneyPlanGetterSetter;
import com.cpm.bira.GetterSetter.MappingPOSMGetterSetter;
import com.cpm.bira.GetterSetter.MappingPosm;
import com.cpm.bira.GetterSetter.MappingStock;
import com.cpm.bira.GetterSetter.MappingStockGetterSetter;
import com.cpm.bira.GetterSetter.NonPosmReason;
import com.cpm.bira.GetterSetter.NonPosmReasonGetterSetter;
import com.cpm.bira.GetterSetter.NonWorkingReason;
import com.cpm.bira.GetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.bira.GetterSetter.PosmMaster;
import com.cpm.bira.GetterSetter.PosmMasterGetterSetter;
import com.cpm.bira.GetterSetter.SkuMaster;
import com.cpm.bira.GetterSetter.SkuMasterGetterSetter;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neeraj on 21-04-2018.
 */

public class BiraDB extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BIRA_DB";
    public static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    Context context;

    public BiraDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public void open() {
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            db.execSQL(CommonString.CREATE_TABLE_CONSUMER_PROMO_COUPON);
            db.execSQL(CommonString.CREATE_TABLE_COMPETITION_VISIBILITY);
            db.execSQL(CommonString.CREATE_TABLE_VISITED_DATE);
            db.execSQL(CommonString.CREATE_TABLE_COVERAGE_DATA);
            db.execSQL(CommonString.CREATE_TABLE_PREVIOUS_DAY_STOCK);
            db.execSQL(CommonString.CREATE_TABLE_RETAILER_ONWED_CHILLER);
            db.execSQL(CommonString.CREATE_TABLE_BIRA_CHILLER);
            db.execSQL(CommonString.CREATE_TABLE_POSM_DEPLOYMENT);
            db.execSQL(CommonString.CREATE_TABLE_STORE_GEOTAGGING);

        }catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int createtable(String sqltext) {
        try {
            db.execSQL(sqltext);
            return 1;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            return 0;
        }
    }


    public long insertConsumerPromoCouponData(CommonDataGetterSetter consumerPromoCouponData, Integer storeId, String visit_date) {
        long id = 0;
        db.delete(CommonString.TABLE_CONSUMER_PROMO_COUPON, "Store_Id" + "='" + storeId + "'", null);
        ContentValues values = new ContentValues();

        try {

            values.put(CommonString.Store_Id,storeId);
            values.put(CommonString.KEY_VISIT_DATE, visit_date);
            values.put(CommonString.KEY_PRESENT,consumerPromoCouponData.getPresent());

            if(consumerPromoCouponData.getPresent().equalsIgnoreCase("0")){
                values.put(CommonString.KEY_QUYANTITY, 0);
            }else{
                values.put(CommonString.KEY_QUYANTITY, consumerPromoCouponData.getQty());
            }

            id = db.insert(CommonString.TABLE_CONSUMER_PROMO_COUPON, null, values);

            if (id > 0) {
                return id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }


    public CommonDataGetterSetter getConsumerPromoCouponData(Integer storeId, String visit_date) {
        Log.d("Fetching Data", "------------------");
        Cursor dbcursor = null;
        CommonDataGetterSetter psd = new CommonDataGetterSetter();
        try {
            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_CONSUMER_PROMO_COUPON + " where "
                    + CommonString.Store_Id + " = '" + storeId + "' and "+CommonString.KEY_VISIT_DATE+" = '"+visit_date+"'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    psd.setStore_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.Store_Id)));
                    psd.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    psd.setPresent(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PRESENT)));
                    psd.setQty(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_QUYANTITY)));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return psd;
            }

        } catch (Exception e) {
            Log.d("Exception ", e.getMessage());
            return psd;
        }
        return psd;
    }


    public long insertCompetitionData(CommonDataGetterSetter competitionData, Integer storeId) {
        long id = 0;
        db.delete(CommonString.TABLE_COMPETITION_VISIBILITY, "Store_Id" + "='" + storeId + "'", null);
        ContentValues values = new ContentValues();

        try {

            values.put(CommonString.Store_Id,storeId);
            values.put(CommonString.KEY_VISIT_DATE, competitionData.getVisit_date());
            values.put(CommonString.KEY_PRESENT,competitionData.getPresent());
            values.put(CommonString.KEY_IMAGE1, competitionData.getImg1());
            values.put(CommonString.KEY_IMAGE2, competitionData.getImg2());
            values.put(CommonString.KEY_IMAGE3, competitionData.getImg3());

            id = db.insert(CommonString.TABLE_COMPETITION_VISIBILITY, null, values);

            if (id > 0) {
                return id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }


    public ArrayList<CommonDataGetterSetter> getCompetitionData(Integer storeId, String visit_date) {

        Log.d("Fetching Data", "------------------");
        Cursor dbcursor = null;
        ArrayList<CommonDataGetterSetter> list = new ArrayList<>();
        try {
            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_COMPETITION_VISIBILITY + " where "
                    + CommonString.Store_Id + " = '" + storeId + "' and "+CommonString.KEY_VISIT_DATE+" = '"+visit_date+"'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CommonDataGetterSetter psd = new CommonDataGetterSetter();
                    psd.setStore_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.Store_Id)));
                    psd.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    psd.setPresent(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PRESENT)));
                    psd.setImg1(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE1)));
                    psd.setImg2(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE2)));
                    psd.setImg3(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE3)));
                    list.add(psd);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception ", e.getMessage());
            return list;
        }
        return list;
    }

    public boolean insertNonWorkingResionData(NonWorkingReasonGetterSetter nonWorkingReasonObj) {
        db.delete("Non_Working_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonWorkingReason> data = nonWorkingReasonObj.getNonWorkingReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Reason_Id", data.get(i).getReasonId());
                values.put("Reason", data.get(i).getReason());
                values.put("Entry_Allow", data.get(i).getEntryAllow());
                values.put("Image_Allow", data.get(i).getImageAllow());
                values.put("GPS_Mandatory", data.get(i).getGPSMandatory());


                long id = db.insert("Non_Working_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertSkuMasterData(SkuMasterGetterSetter skuMasterObj) {
        db.delete("Sku_Master", null, null);
        ContentValues values = new ContentValues();
        List<SkuMaster> data = skuMasterObj.getSkuMaster();

        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Sku_Id", data.get(i).getSkuId());
                values.put("Sku", data.get(i).getSku());
                values.put("Brand_Id", data.get(i).getBrandId());
                values.put("Sku_Sequence", data.get(i).getSkuSequence());

                long id = db.insert("Sku_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }

            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database Sku_Master", ex.toString());
            return false;
        }
    }


    public boolean brandMasterObj(BrandMasterGetterSetter brandMasterObj) {
        db.delete("Brand_Master", null, null);
        ContentValues values = new ContentValues();
        List<BrandMaster> data = brandMasterObj.getBrandMaster();

        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Brand_Id", data.get(i).getBrandId());
                values.put("Brand", data.get(i).getBrand());
                values.put("Sub_Category_Id", data.get(i).getSubCategoryId());
                values.put("Brand_Sequence", data.get(i).getBrandSequence());
                values.put("Company_Id",data.get(i).getCompanyId());

                long id = db.insert("Brand_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }

            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database Brand_Master", ex.toString());
            return false;
        }
    }

    public boolean insertJourneyPlanData(JourneyPlanGetterSetter jcpdata) {
        db.delete("Journey_Plan", null, null);
        ContentValues values = new ContentValues();
        List<JourneyPlan> data = jcpdata.getJourneyPlan();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Store_Id", data.get(i).getStoreId());
                values.put("Visit_Date", data.get(i).getVisitDate());
                values.put("Store_Name", data.get(i).getStoreName());
                values.put("Address",data.get(i).getAddress());
                values.put("City", data.get(i).getCity());
                values.put("Store_Type", data.get(i).getStoreType());
                values.put("Classification_Id", data.get(i).getClassificationId());
                values.put("Distributor", data.get(i).getDistributor());
                values.put("Distributor_Id", data.get(i).getDistributorId());
                values.put("Geo_Tag", data.get(i).getGeoTag());
                values.put("Reason_Id", data.get(i).getReasonId());
                values.put("Store_Category", data.get(i).getStoreCategory());
                values.put("Store_Category_Id", data.get(i).getStoreCategoryId());
                values.put("Store_Type_Id", data.get(i).getStoreTypeId());
                values.put("Upload_Status", data.get(i).getUploadStatus());
                values.put("State_Id",data.get(i).getStateId());

                long id = db.insert("Journey_Plan", null, values);
                if (id == -1) {
                    throw new Exception();
                }

            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Journey Plan Data", ex.toString());
            return false;
        }

    }

    public boolean insertMappingStockData(MappingStockGetterSetter mappingStockObj) {
         db.delete("Mapping_Stock", null, null);
        ContentValues values = new ContentValues();
        List<MappingStock> data = mappingStockObj.getMappingStock();

        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("State_Id", data.get(i).getStateId());
                values.put("Distributor_Id", data.get(i).getDistributorId());
                values.put("Store_Type_Id", data.get(i).getStoreTypeId());
                values.put("Sku_Id", data.get(i).getSkuId());
                values.put("MSL", data.get(i).getMSL());
                values.put("MBQ", data.get(i).getMBQ());


                long id = db.insert("Mapping_Stock", null, values);
                if (id == -1) {
                    throw new Exception();
                }

            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database Stock", ex.toString());
            return false;
        }
    }


    public ArrayList<JourneyPlan> getStoreData(String visit_date) {

        ArrayList<JourneyPlan> list = new ArrayList<JourneyPlan>();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT distinct * from Journey_Plan where Visit_Date ='" + visit_date + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {

                    JourneyPlan sb = new JourneyPlan();

                    sb.setAddress(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setClassificationId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Classification_Id"))));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStateId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id"))));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetc", e.toString());
            return list;
        }

        Log.d("FetchingStore", "-------------------");
        return list;

    }



    public ArrayList<CoverageBean> getCoverageWithStoreID_Data(String store_id, String visitDate) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where " + CommonString.Store_Id + "='" + store_id + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visitDate + "'",
                    null);


            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.Store_Id)));
                    sb.setVisitDate((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setImage((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE))))));

                    sb.setReasonid((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    if (dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)) == null) {
                        sb.setRemark("");
                    } else {
                        sb.setRemark((((dbcursor.getString(dbcursor
                                .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK))))));
                    }
                    sb.setReason((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON))))));

                    sb.setMID(Integer.parseInt(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_ID))))));


                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public void deleteTableWithStoreID(String storeid) {
        db.delete(CommonString.TABLE_COVERAGE_DATA, CommonString.Store_Id + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_CONSUMER_PROMO_COUPON, CommonString.Store_Id + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_COMPETITION_VISIBILITY, CommonString.Store_Id + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_PREVIOUS_DAY_STOCK, CommonString.Store_Id + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_BIRA_CHILLER, CommonString.Store_Id + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_RETAILER_OWNED_CHILLER, CommonString.Store_Id + "='" + storeid + "'", null);
        db.delete(CommonString.TABLE_POSM_DEPLOYMENT, CommonString.Store_Id + "='" + storeid + "'", null);
    }

    public void updateStoreStatus(String storeid, String visitdate,
                                  String status) {

        try {
            ContentValues values = new ContentValues();
            values.put("Upload_Status", status);

            db.update("Journey_Plan", values, "Store_Id ='" + storeid + "' AND " + CommonString.KEY_VISIT_DATE + " ='" + visitdate + "'", null);
        } catch (Exception e) {

        }
    }



    public boolean insertLoginVisitedDate(JourneyPlanGetterSetter teamlistdata) {
        db.delete("LOGIN_VISITED_DATE", null, null);
        ContentValues values = new ContentValues();

        List<JourneyPlan> data = teamlistdata.getJourneyPlan();
        try {
            if (data.size() == 0) {
                return false;
            }

            values.put(CommonString.KEY_VISIT_DATE, data.get(0).getVisitDate());

            long id = db.insert("LOGIN_VISITED_DATE", null, values);
            if (id == -1) {
                throw new Exception();
            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database call cycle ", ex.toString());
            return false;
        }
    }


    public String getLoginVisitedDate(String date) {
        String loginDate = "";
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("Select * from "+CommonString.TABLE_LOGIN_VISITED_DATE+" where "+CommonString.KEY_VISIT_DATE+" = '"+date+"'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    loginDate = dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return loginDate;
            }

        } catch (Exception e) {
            return loginDate;
        }
        return loginDate;
    }



        public long InsertCoverageData(CoverageBean data) {
            db.delete(CommonString.TABLE_COVERAGE_DATA, "Store_Id" + "='" + data.getStoreId() + "'", null);
            ContentValues values = new ContentValues();
            long id = 0;
            try {
                values.put(CommonString.Store_Id, data.getStoreId());
                values.put(CommonString.KEY_VISIT_DATE, data.getVisitDate());
                values.put(CommonString.KEY_LATITUDE, data.getLatitude());
                values.put(CommonString.KEY_LONGITUDE, data.getLongitude());
                values.put(CommonString.KEY_IMAGE, data.getImage());
                values.put(CommonString.KEY_COVERAGE_REMARK, data.getRemark());
                values.put(CommonString.KEY_REASON_ID, data.getReasonid());
                values.put(CommonString.KEY_REASON, data.getReason());
                values.put(CommonString.KEY_SUB_REASON_ID, data.getSub_reasonId());
                values.put(CommonString.KEY_UPLOAD_STATUS, data.getUploadStatus());
                values.put(CommonString.KEY_TYPE, data.getCoverage_type());
                values.put(CommonString.KEY_CHECK_IN_IMAGE, data.getImage());
                values.put(CommonString.KEY_STATE_ID, data.getState_Id());
                values.put("Checkout_Image", "");

//                values.put(CommonString.KEY_MERCHENDISER_ID, data.getMerId());
//                values.put(CommonString.KEY_MER_MID, data.getMerchandiser_MID());

                id = db.insert(CommonString.TABLE_COVERAGE_DATA, null, values);
                if (id > 0) {
                    return id;
                } else {
                    return 0;
                }
            } catch (Exception ex) {
                Log.d("Database Exception ", ex.toString());
                return 0;
            }
        }


    public long updateUploadStatus(String id, String status) {

        ContentValues values = new ContentValues();
        try {
            values.put("Upload_Status", status);
            return db.update("Journey_Plan", values, "Store_Id " + " = '" + id + "'", null);
        } catch (Exception ex) {
            Log.e("Exception", "Coverage Journey_Plan" + ex.toString());
            return 0;
        }
    }


    public ArrayList<CommonChillerDataGetterSetter> getBiraSkuData(JourneyPlan jcpGetset) {
        ArrayList<CommonChillerDataGetterSetter> list = new ArrayList<CommonChillerDataGetterSetter>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery( " select distinct SM.Sku, MS.State_Id,MS.Distributor_Id,MS.Store_Type_Id,MS.Sku_Id from Mapping_Stock MS " +
                                         " inner join Sku_Master SM on MS.Sku_Id = SM.Sku_Id  where MS.Store_Type_Id = '"+jcpGetset.getStoreTypeId()+"' " +
                                         " and  MS.State_Id = '"+jcpGetset.getStateId()+"' and MS.Distributor_Id = '"+jcpGetset.getDistributorId()+"' ",null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CommonChillerDataGetterSetter sb = new CommonChillerDataGetterSetter();

                    sb.setSku_id(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_SKU_ID)));
                    sb.setSku_name((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_SKU))))));
                    sb.setState_id(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STATE_ID)))));
                    sb.setDistribution_id(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_DISTRIBUTOR_ID)))));
                    sb.setStore_type_id((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STORE_TYPE_ID))))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public long insertPreviousDayData(CommonChillerDataGetterSetter data, JourneyPlan jcp) {
        db.delete(CommonString.TABLE_PREVIOUS_DAY_STOCK, "Store_Id" + "='" + jcp.getStoreId() + "'", null);
        ContentValues values = new ContentValues();
        long id = 0;
        try {
            values.put(CommonString.Store_Id, jcp.getStoreId());
            values.put(CommonString.KEY_VISIT_DATE, jcp.getVisitDate());
            values.put(CommonString.KEY_PRESENT,data.getExist());
            values.put(CommonString.KEY_CLOSING_STOCK, 0);
            values.put(CommonString.KEY_SKU, "");
            values.put(CommonString.KEY_SKU_ID, 0);
            id = db.insert(CommonString.TABLE_PREVIOUS_DAY_STOCK, null, values);

            if (id > 0) {
                return id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }

    public long insertPreviousDayData(CommonChillerDataGetterSetter data, JourneyPlan jcp, ArrayList<CommonChillerDataGetterSetter> skuListdata) {
        db.delete(CommonString.TABLE_PREVIOUS_DAY_STOCK, "Store_Id" + "='" + jcp.getStoreId() + "'", null);
        ContentValues values = new ContentValues();
        long id = 0;
        try {
            for(int i=0;i<skuListdata.size();i++){
                values.put(CommonString.Store_Id, jcp.getStoreId());
                values.put(CommonString.KEY_VISIT_DATE, jcp.getVisitDate());
                values.put(CommonString.KEY_PRESENT,data.getExist());
                values.put(CommonString.KEY_CLOSING_STOCK, skuListdata.get(i).getBiraQty());
                values.put(CommonString.KEY_SKU, skuListdata.get(i).getSku_name());
                values.put(CommonString.KEY_SKU_ID, skuListdata.get(i).getSku_id());

                id = db.insert(CommonString.TABLE_PREVIOUS_DAY_STOCK, null, values);
            }


            if (id > 0) {
                return id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }


    public ArrayList<CommonChillerDataGetterSetter> getPreviousDayStockData(Integer store_id, String visit_date) {
        ArrayList<CommonChillerDataGetterSetter> list = new ArrayList<CommonChillerDataGetterSetter>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_PREVIOUS_DAY_STOCK + " where " + CommonString.Store_Id + "='" + store_id + "' AND " + CommonString.KEY_VISIT_DATE + "='" + visit_date + "'",
                    null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CommonChillerDataGetterSetter sb = new CommonChillerDataGetterSetter();
                    sb.setStore_id(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.Store_Id)));
                    sb.setExist(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_PRESENT)));
                    sb.setBiraQty(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_CLOSING_STOCK)));
                    sb.setSku_id(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_SKU_ID)));
                    sb.setSku_name(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_SKU)));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public boolean insertPosmMasterData(PosmMasterGetterSetter posmMasterObj) {
        db.delete("Posm_Master", null, null);
        ContentValues values = new ContentValues();
        List<PosmMaster> data = posmMasterObj.getPosmMaster();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("Posm_Id", data.get(i).getPosmId());
                values.put("Posm", data.get(i).getPosm());
                values.put("Posm_Type_Id", data.get(i).getPosmTypeId());
                values.put("Posm_Type", data.get(i).getPosmType());

                long id = db.insert("Posm_Master", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertPosmReasonData(NonPosmReasonGetterSetter nonPosmReasonObj) {
        db.delete("Non_Posm_Reason", null, null);
        ContentValues values = new ContentValues();
        List<NonPosmReason> data = nonPosmReasonObj.getNonPosmReason();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {
                values.put("Preason_Id", data.get(i).getPreasonId());
                values.put("Preason", data.get(i).getPreason());
                values.put("Remark", data.get(i).getRemark());

                long id = db.insert("Non_Posm_Reason", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public boolean insertMappingPOSMData(MappingPOSMGetterSetter mappingPOSMObj) {
        db.delete("Mapping_Posm", null, null);
        ContentValues values = new ContentValues();
        List<MappingPosm> data = mappingPOSMObj.getMappingPosm();
        try {
            if (data.size() == 0) {
                return false;
            }

            for (int i = 0; i < data.size(); i++) {

                values.put("State_Id", data.get(i).getStateId());
                values.put("Distributor_Id", data.get(i).getDistributorId());
                values.put("Store_Type_Id", data.get(i).getStoreTypeId());
                values.put("Posm_Id", data.get(i).getPosmId());

                long id = db.insert("Mapping_Posm", null, values);
                if (id == -1) {
                    throw new Exception();
                }
            }
            return true;
        } catch (Exception ex) {
            Crashlytics.logException(ex);
            ex.printStackTrace();
            Log.d("Database Exception  ", ex.toString());
            return false;
        }
    }

    public long insertConsumerChillerData(ArrayList<CommonChillerDataGetterSetter> chillerList, CommonChillerDataGetterSetter chillerObj, JourneyPlan jcp, String visit_date, String chiller_flag, String image1) {
        if(chiller_flag.equalsIgnoreCase("1")) {
            db.delete(CommonString.TABLE_BIRA_CHILLER, null, null);
        }else{
            db.delete(CommonString.TABLE_RETAILER_OWNED_CHILLER, null, null);
        }
        ContentValues values = new ContentValues();

        long id = 0;
        try {
            for(int i=0;i<chillerList.size();i++){
                values.put(CommonString.Store_Id, jcp.getStoreId());
                values.put(CommonString.KEY_VISIT_DATE, jcp.getVisitDate());
                values.put(CommonString.KEY_EXIST,chillerObj.getExist());
                values.put(CommonString.KEY_CAPACITY, chillerObj.getCapacity());
                values.put(CommonString.KEY_CAHRGING, chillerObj.getCharging());
                values.put(CommonString.KEY_PURITY, chillerObj.getPurity());
                values.put(CommonString.KEY_IMAGE1, image1);
                values.put(CommonString.KEY_SKU_ID, chillerList.get(i).getSku_id());
                values.put(CommonString.KEY_SKU, chillerList.get(i).getSku_name());
                values.put(CommonString.KEY_PRESENT, chillerList.get(i).getBiraPresence());
                values.put(CommonString.KEY_QUYANTITY, chillerList.get(i).getBiraQty());


                if(chiller_flag.equalsIgnoreCase("1")) {
                    id = db.insert(CommonString.TABLE_BIRA_CHILLER, null, values);
                }else{
                    id = db.insert(CommonString.TABLE_RETAILER_OWNED_CHILLER, null, values);
                }
            }


            if (id > 0) {
                return id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }


    public long insertConsumerChillerData(CommonChillerDataGetterSetter chillerObj, JourneyPlan jcp, String visit_date, String chiller_flag, String image1) {
        if(chiller_flag.equalsIgnoreCase("1")) {
            db.delete(CommonString.TABLE_BIRA_CHILLER, null, null);
        }else{
            db.delete(CommonString.TABLE_RETAILER_OWNED_CHILLER, null, null);
        }
        ContentValues values = new ContentValues();

        long id = 0;
        try {
            values.put(CommonString.Store_Id, jcp.getStoreId());
            values.put(CommonString.KEY_VISIT_DATE, jcp.getVisitDate());
            values.put(CommonString.KEY_EXIST,chillerObj.getExist());
            values.put(CommonString.KEY_CAPACITY, "");
            values.put(CommonString.KEY_CAHRGING, "");
            values.put(CommonString.KEY_PURITY,"");
            values.put(CommonString.KEY_IMAGE1, "");
            values.put(CommonString.KEY_SKU_ID, "");
            values.put(CommonString.KEY_SKU, "");
            values.put(CommonString.KEY_PRESENT, "");
            values.put(CommonString.KEY_QUYANTITY, "");

            if(chiller_flag.equalsIgnoreCase("1")) {
                id = db.insert(CommonString.TABLE_BIRA_CHILLER, null, values);
            }else{
                id = db.insert(CommonString.TABLE_RETAILER_OWNED_CHILLER, null, values);
            }


            if (id > 0) {
                return id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }


    public ArrayList<CommonChillerDataGetterSetter> getBiraChillerData(Integer storeId, String visit_date) {
        Log.d("Fetching Data", "------------------");
        Cursor dbcursor = null;
        ArrayList<CommonChillerDataGetterSetter> psd = new ArrayList<>();
        try {
            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_BIRA_CHILLER + " where "
                    + CommonString.Store_Id + " = '" + storeId + "' and "
                    + CommonString.KEY_VISIT_DATE + " = '"+ visit_date +"'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CommonChillerDataGetterSetter psd1 = new CommonChillerDataGetterSetter();
                    psd1.setStore_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.Store_Id)));
                    psd1.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    psd1.setExist(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EXIST)));
                    psd1.setCapacity(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CAPACITY)));
                    psd1.setCharging(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CAHRGING)));
                    psd1.setPurity(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PURITY)));
                    psd1.setImg1(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE1)));
                    psd1.setSku_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU_ID)));
                    psd1.setSku_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU)));
                    psd1.setBiraPresence(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PRESENT)));
                    psd1.setBiraQty(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_QUYANTITY)));

                    psd.add(psd1);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return psd;
            }

        } catch (Exception e) {
            Log.d("Exception ", e.getMessage());
            return psd;
        }
        return psd;
    }

    public ArrayList<CommonChillerDataGetterSetter> getRetailerOwnedChillerData(Integer storeId, String visit_date) {
        Log.d("Fetching Data", "------------------");
        Cursor dbcursor = null;
        ArrayList<CommonChillerDataGetterSetter> psd = new ArrayList<>();
        try {
            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_RETAILER_OWNED_CHILLER + " where "
                    + CommonString.Store_Id + " = '" + storeId + "' and "
                    + CommonString.KEY_VISIT_DATE + " = '"+ visit_date +"'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CommonChillerDataGetterSetter psd1 = new CommonChillerDataGetterSetter();
                    psd1.setStore_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.Store_Id)));
                    psd1.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    psd1.setExist(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EXIST)));
                    psd1.setCapacity(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CAPACITY)));
                    psd1.setCharging(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_CAHRGING)));
                    psd1.setPurity(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PURITY)));
                    psd1.setImg1(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE1)));
                    psd1.setSku_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU_ID)));
                    psd1.setSku_name(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_SKU)));
                    psd1.setBiraPresence(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_PRESENT)));
                    psd1.setBiraQty(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_QUYANTITY)));

                    psd.add(psd1);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return psd;
            }

        } catch (Exception e) {
            Log.d("Exception ", e.getMessage());
            return psd;
        }
        return psd;
    }

    public ArrayList<CommonChillerDataGetterSetter> getPOSMDeploymentData(JourneyPlan jcpGetset) {
        ArrayList<CommonChillerDataGetterSetter> list = new ArrayList<CommonChillerDataGetterSetter>();
        Cursor dbcursor = null;
        try {

            dbcursor = db.rawQuery( " select distinct PM.Posm,PM.Posm_Type_Id,MP.Posm_Id,MP.State_Id,MP.Distributor_Id,MP.Store_Type_Id from Mapping_Posm MP " +
                    " inner join Posm_Master PM ON MP.Posm_Id = PM.Posm_Id  where MP.Store_Type_Id = '"+jcpGetset.getStoreTypeId()+"' " +
                    " and  MP.State_Id = '"+jcpGetset.getStateId()+"' and MP.Distributor_Id = '"+jcpGetset.getDistributorId()+"' ",null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CommonChillerDataGetterSetter sb = new CommonChillerDataGetterSetter();

                    sb.setPosm_id(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_POSM_ID)));
                    sb.setPosm((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_POSM))))));
                    sb.setState_id(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STATE_ID)))));
                    sb.setDistribution_id(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_DISTRIBUTOR_ID)))));
                    sb.setStore_type_id((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STORE_TYPE_ID))))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    public long insertPOSMDeploymentData(ArrayList<CommonChillerDataGetterSetter> deploymentData, JourneyPlan jcp) {

        db.delete(CommonString.TABLE_POSM_DEPLOYMENT, null, null);
        ContentValues values = new ContentValues();

        long id = 0;
        try {
            for(int i=0;i<deploymentData.size();i++){
                values.put(CommonString.Store_Id, jcp.getStoreId());
                values.put(CommonString.KEY_VISIT_DATE, jcp.getVisitDate());
                values.put(CommonString.KEY_EXIST,deploymentData.get(i).getExist());
                if(deploymentData.get(i).getExist().equalsIgnoreCase("1")) {
                    values.put(CommonString.KEY_OLD_POSM_DEPLOYMENT, deploymentData.get(i).getOld_deployment());
                    values.put(CommonString.KEY_NEW_POSM_DEPLOYMENT, deploymentData.get(i).getNew_deployment());
                    values.put(CommonString.KEY_IMAGE1,deploymentData.get(i).getImg1());
                    values.put(CommonString.KEY_REASON_ID, "");
                    values.put(CommonString.KEY_REASON, "");
                }else{
                    values.put(CommonString.KEY_OLD_POSM_DEPLOYMENT, "");
                    values.put(CommonString.KEY_NEW_POSM_DEPLOYMENT, "");
                    values.put(CommonString.KEY_IMAGE1,"");
                    values.put(CommonString.KEY_REASON_ID, deploymentData.get(i).getReason_id());
                    values.put(CommonString.KEY_REASON, deploymentData.get(i).getReason());
                }
                values.put(CommonString.KEY_POSM_ID, deploymentData.get(i).getPosm_id());
                values.put(CommonString.KEY_POSM, deploymentData.get(i).getPosm());



                id = db.insert(CommonString.TABLE_POSM_DEPLOYMENT, null, values);

            }


            if (id > 0) {
                return id;
            } else {
                return 0;
            }
        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }

    public ArrayList<CommonChillerDataGetterSetter> getPOSMDeploymentSavedData(Integer storeId, String visit_date) {
        Log.d("Fetching Data", "------------------");
        Cursor dbcursor = null;
        ArrayList<CommonChillerDataGetterSetter> list = new ArrayList<>();
        try {
            dbcursor = db.rawQuery("SELECT  * from "
                    + CommonString.TABLE_POSM_DEPLOYMENT + " where "
                    + CommonString.Store_Id + " = '" + storeId + "' and "+CommonString.KEY_VISIT_DATE+" =  '"+visit_date+"' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CommonChillerDataGetterSetter psd = new CommonChillerDataGetterSetter();

                    psd.setStore_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.Store_Id)));
                    psd.setVisit_date(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE)));
                    psd.setImg1(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_IMAGE1)));
                    psd.setOld_deployment(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_OLD_POSM_DEPLOYMENT)));
                    psd.setNew_deployment(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_NEW_POSM_DEPLOYMENT)));
                    psd.setExist(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_EXIST)));
                    psd.setPosm(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM)));
                    psd.setPosm_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_POSM_ID)));
                    psd.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON)));
                    psd.setReason_id(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_REASON_ID)));

                    list.add(psd);
                    dbcursor.moveToNext();

                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception ", e.getMessage());
            return list;
        }
        return list;
    }

    public ArrayList<NonPosmReason> getPOSMReason() {
        Log.d("Fetching Data", "------------------");
        Cursor dbcursor = null;
        ArrayList<NonPosmReason> list = new ArrayList<>();
        NonPosmReason reason1 = new NonPosmReason();
        try {
            reason1.setRemark(false);
            reason1.setPreasonId(0);
            reason1.setPreason("Select Reason");
            list.add(reason1);

            dbcursor = db.rawQuery("SELECT  * from Non_Posm_Reason ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonPosmReason reason = new NonPosmReason();
                    reason.setPreason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Preason")));
                    reason.setPreasonId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Preason_Id"))));
                    reason.setRemark(Boolean.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Remark"))));
                    list.add(reason);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception ", e.getMessage());
            return list;
        }
        return list;
    }


    public long updateCheckoutStatus(String storeId, String status) {
        ContentValues values = new ContentValues();
        long l = 0;
        try {
            values.put("Upload_Status", status);
            l = db.update("Journey_Plan", values, "Store_Id =" + Integer.parseInt(storeId) + "", null);
        } catch (Exception ex) {
            Log.e("Exception", "checkOut Journey_Plan" + ex.toString());
            return l;
        }
        return l;
    }

    public ArrayList<CoverageBean> getCompleteCoverageData(String date) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "='" + date + "' ", null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.Store_Id)));
                    sb.setVisitDate((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setImage((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE))))));
                    sb.setReason((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setSub_reasonId((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_SUB_REASON_ID))))));
                    sb.setState_Id(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_STATE_ID)));
                    sb.setCheckOut_Image(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("Checkout_Image")));

                    sb.setCoverage_type((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TYPE))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_UPLOAD_STATUS))));

                    if (dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)) == null) {
                        sb.setRemark("");
                    } else {
                        sb.setRemark((((dbcursor.getString(dbcursor
                                .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK))))));
                    }

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }


    public JourneyPlan getStoreData(String visit_date,String storeId) {

        Cursor dbcursor = null;
        JourneyPlan sb = new JourneyPlan();
        try {

            dbcursor = db.rawQuery("SELECT distinct * from Journey_Plan where Store_Id = '"+storeId+"' and Visit_Date ='" + visit_date + "' ", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setAddress(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Address")));
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setClassificationId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Classification_Id"))));
                    sb.setDistributor(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor")));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStateId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("State_Id"))));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));

                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.d("Exception when fetc", e.toString());
            return sb;
        }

        Log.d("FetchingStore", "-------------------");
        return sb;

    }


    public ArrayList<CoverageBean> getCoverageDataPrevious(String date) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "<>'" + date + "' ", null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.Store_Id)));
                    sb.setVisitDate((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setImage((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE))))));
                    sb.setReason((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setSub_reasonId((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_SUB_REASON_ID))))));
                    sb.setMID(Integer.parseInt(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_ID))))));

                    sb.setCoverage_type((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TYPE))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_UPLOAD_STATUS))));

                    if (dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)) == null) {
                        sb.setRemark("");
                    } else {
                        sb.setRemark((((dbcursor.getString(dbcursor
                                .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK))))));
                    }

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }


    public boolean isCoveragePreviousDataFilled(String date) {
        boolean filled = false;
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "<>'" + date + "' ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
            Log.d("Exception ", " when fetching Records!!!!!!!!!!!!!!!!!!!!! " + e.toString());
            return filled;
        }
        return filled;
    }

    public ArrayList<CoverageBean> getPreviousDayCompleteCoverageData(String date) {
        ArrayList<CoverageBean> list = new ArrayList<CoverageBean>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT  * from " + CommonString.TABLE_COVERAGE_DATA + " where "
                    + CommonString.KEY_VISIT_DATE + "<>'" + date + "' ", null);

            if (dbcursor != null) {

                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    CoverageBean sb = new CoverageBean();

                    sb.setStoreId(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.Store_Id)));
                    sb.setVisitDate((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_VISIT_DATE))))));
                    sb.setLatitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LATITUDE)))));
                    sb.setLongitude(((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_LONGITUDE)))));
                    sb.setImage((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_IMAGE))))));
                    sb.setReason((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON))))));
                    sb.setReasonid((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_REASON_ID))))));
                    sb.setSub_reasonId((((dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_SUB_REASON_ID))))));
                    sb.setCheckOut_Image(dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow("Checkout_Image")));

                    sb.setCoverage_type((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_TYPE))));
                    sb.setUploadStatus((dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_UPLOAD_STATUS))));

                    if (dbcursor.getString(dbcursor
                            .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK)) == null) {
                        sb.setRemark("");
                    } else {
                        sb.setRemark((((dbcursor.getString(dbcursor
                                .getColumnIndexOrThrow(CommonString.KEY_COVERAGE_REMARK))))));
                    }

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }
        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }
        return list;
    }

    //get specific store data
    public JourneyPlan getSpecificStoreUploadStatusPreviousDay(String date, String store_id, String jcp) {
        JourneyPlan sb = new JourneyPlan();
        Cursor dbcursor = null;

        try {

            dbcursor = db.rawQuery("SELECT * FROM " + jcp + " where Visit_Date <>'" + date + "' and Store_Id = '" + store_id + "'", null);


            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setClassificationId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Classification_Id"))));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return sb;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return sb;
        }

        return sb;
    }

    public boolean isJournyCallCyclesDataFilled(String date) {
        boolean filled = false;
        Cursor dbcursor = null;

        try {
            dbcursor = db.rawQuery("SELECT * FROM Journey_Plan where Visit_Date = '" + date + "' ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                int icount = dbcursor.getInt(0);
                dbcursor.close();
                if (icount > 0) {
                    filled = true;
                } else {
                    filled = false;
                }
            }
        } catch (Exception e) {
//            Crashlytics.logException(e);
            Log.d("Exception ", " when fetching Records!!!!!!!!!!!!!!!!!!!!! " + e.toString());
            return filled;
        }
        return filled;
    }

    //get specific store data
    public ArrayList<JourneyPlan> getJCPData(String date, String jcp) {

        Cursor dbcursor = null;
        ArrayList<JourneyPlan> list = new ArrayList<>();
        try {

            dbcursor = db.rawQuery("SELECT * FROM " + jcp + " where Visit_Date = '" + date + "' ", null);
            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    JourneyPlan sb = new JourneyPlan();
                    sb.setCity(dbcursor.getString(dbcursor.getColumnIndexOrThrow("City")));
                    sb.setClassificationId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Classification_Id"))));
                    sb.setDistributorId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Distributor_Id"))));
                    sb.setGeoTag(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Geo_Tag")));
                    sb.setReasonId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setStoreCategory(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category")));
                    sb.setStoreCategoryId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Category_Id"))));
                    sb.setStoreId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Id"))));
                    sb.setStoreName(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Name")));
                    sb.setStoreType(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type")));
                    sb.setStoreTypeId(Integer.parseInt(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Store_Type_Id"))));
                    sb.setUploadStatus(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Upload_Status")));
                    sb.setVisitDate(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Visit_Date")));
                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception get JCP!", e.toString());
            return list;
        }

        return list;
    }

    public ArrayList<NonWorkingReason> getNonWorkingData() {
        ArrayList<NonWorkingReason> list = new ArrayList<NonWorkingReason>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("SELECT * FROM Non_Working_Reason", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    NonWorkingReason sb = new NonWorkingReason();

                    sb.setReasonId(Integer.valueOf(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason_Id"))));
                    sb.setReason(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Reason")));
                    sb.setEntryAllow("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Entry_Allow"))));
                    sb.setImageAllow("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("Image_Allow"))));
                    sb.setGPSMandatory("1".equalsIgnoreCase(dbcursor.getString(dbcursor.getColumnIndexOrThrow("GPS_Mandatory"))));

                    list.add(sb);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {

            return list;
        }

        return list;
    }

    public long updateStoreStatusOnLeave(String storeid, String visitdate, String status, String outlet) {
        long id = 0;
        try {
            ContentValues values = new ContentValues();
            values.put("UPLOAD_STATUS", status);

            id = db.update(outlet, values, CommonString.Store_Id + "='" + storeid + "' AND "
                    + CommonString.KEY_VISIT_DATE + "='" + visitdate
                    + "'", null);

            return id;
        } catch (Exception e) {
            return 0;
        }
    }


    public long InsertSTOREgeotag(String storeid, double lat, double longitude, String path, String status) {

        db.delete(CommonString.TABLE_STORE_GEOTAGGING, CommonString.Store_Id + "='" + storeid + "'", null);
        ContentValues values = new ContentValues();
        try {
            values.put("STORE_ID", storeid);
            values.put("LATITUDE", Double.toString(lat));
            values.put("LONGITUDE", Double.toString(longitude));
            values.put("FRONT_IMAGE", path);
            values.put("GEO_TAG", status);
            values.put("STATUS", status);

            return db.insert(CommonString.TABLE_STORE_GEOTAGGING, null, values);

        } catch (Exception ex) {
            Log.d("Database Exception ", ex.toString());
            return 0;
        }
    }


    public ArrayList<GeotaggingBeans> getinsertGeotaggingData(String storeid, String status) {
        ArrayList<GeotaggingBeans> list = new ArrayList<>();
        Cursor dbcursor = null;
        try {
            dbcursor = db.rawQuery("Select * from " + CommonString.TABLE_STORE_GEOTAGGING + "" +
                    " where " + CommonString.Store_Id + " ='" + storeid + "' and " + CommonString.KEY_STATUS + " = '" + status + "'", null);

            if (dbcursor != null) {
                dbcursor.moveToFirst();
                while (!dbcursor.isAfterLast()) {
                    GeotaggingBeans geoTag = new GeotaggingBeans();
                    geoTag.setStoreid(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.Store_Id)));
                    geoTag.setLatitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LATITUDE))));
                    geoTag.setLongitude(Double.parseDouble(dbcursor.getString(dbcursor.getColumnIndexOrThrow(CommonString.KEY_LONGITUDE))));
                    geoTag.setImage(dbcursor.getString(dbcursor.getColumnIndexOrThrow("FRONT_IMAGE")));
                    list.add(geoTag);
                    dbcursor.moveToNext();
                }
                dbcursor.close();
                return list;
            }

        } catch (Exception e) {
            Log.d("Exception Brands",
                    e.toString());
            return list;
        }
        return list;
    }

    public void updateStatus(String id, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put("GEO_TAG", status);
            db.update("Journey_Plan", values, CommonString.Store_Id + "='" + id + "'", null);
        } catch (Exception ex) {
        }
    }


    public long updateInsertedGeoTagStatus(String id, String status) {
        ContentValues values = new ContentValues();
        try {
            values.put("GEO_TAG", status);
            values.put("STATUS", status);
            return db.update(CommonString.TABLE_STORE_GEOTAGGING, values, CommonString.Store_Id + "='" + id + "'", null);
        } catch (Exception ex) {
            return 0;
        }
    }


}
