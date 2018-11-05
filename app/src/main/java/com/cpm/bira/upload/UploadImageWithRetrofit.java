package com.cpm.bira.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CommonChillerDataGetterSetter;
import com.cpm.bira.GetterSetter.CommonDataGetterSetter;
import com.cpm.bira.GetterSetter.CoverageBean;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.GetterSetter.ReferenceVariablesForDownloadActivity;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Retrofit;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;


public class UploadImageWithRetrofit extends ReferenceVariablesForDownloadActivity {

    boolean isvalid;
    private Retrofit adapter;
    Context context;
    public static int uploadedFiles = 0;
    int status = 0;
    BiraDB db;
    ProgressDialog pd;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String _UserId, date, app_ver;
    boolean statusUpdated = true;
    int from;


    public UploadImageWithRetrofit(Context context) {
        this.context = context;
    }

    public UploadImageWithRetrofit(Context context, BiraDB db, ProgressDialog pd, int from) {
        this.context = context;
        this.db = db;
        this.pd = pd;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        this.from = from;
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        date = preferences.getString(CommonString.KEY_DATE, null);
        try {
            app_ver = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        db.open();
    }



    public void uploadDataUsingCoverageRecursive(ArrayList<CoverageBean> coverageList, int coverageIndex, String uploadFlag, String date) {
        try {
            ArrayList<String> keyList = new ArrayList<>();
            keyList.clear();
            String status = null;
            if(coverageList.size() >0) {
                pd.setMessage("Uploading store " + (coverageIndex + 1) + "/" + coverageList.size());
                db.open();
                status = db.getStoreData(coverageList.get(coverageIndex).getVisitDate(),coverageList.get(coverageIndex).getStoreId()).getUploadStatus();

                if (status != null && status.equalsIgnoreCase(CommonString.KEY_C)) {
                        keyList.add("CoverageDetail_latest");
                        keyList.add("POSM_Deployment");
                        keyList.add("Bira_Chiller");
                        keyList.add("Retailer_Owned_Chiller");
                        keyList.add("Competition_Visibility");
                        keyList.add("Consumer_Promo_Coupon");
                        keyList.add("Previous_Day_Stock");

                }

                if (keyList.size() > 0) {
                    UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context, db, pd, from);
                    upload.uploadDataWithoutWait(keyList, 0, coverageList, coverageIndex, uploadFlag);
                } else {

                    if (++coverageIndex != coverageList.size()) {
                        uploadDataUsingCoverageRecursive(coverageList, coverageIndex, uploadFlag, date);
                    } else {
                        String coverageDate = null;
                        if (coverageList.size() > 0) {
                            coverageDate = coverageList.get(0).getVisitDate();
                        } else {
                            coverageDate = this.date;
                        }
                        UploadImageFileJsonList(context, coverageDate, uploadFlag);
                    }
                }
            }else{
                UploadImageFileJsonList(context, date, uploadFlag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pd.dismiss();
        }
    }



    public void uploadDataWithoutWait(final ArrayList<String> keyList, final int keyIndex, final ArrayList<CoverageBean> coverageList, final int coverageIndex, final String uploadFlag) {

        try {
            status = 0;
            isvalid = false;
            final String[] data_global = {""};
            String jsonString = "";
            int type = 0;
            JSONObject jsonObject;

            //region Creating json data
            switch (keyList.get(keyIndex)) {

                case "CoverageDetail_latest":
                    JourneyPlan journeyPlan = new JourneyPlan();
                    db.open();

                    journeyPlan = db.getStoreData(coverageList.get(coverageIndex).getVisitDate(),coverageList.get(coverageIndex).getStoreId());

                    //region Coverage Data
                    jsonObject = new JSONObject();
                    jsonObject.put("StoreId", coverageList.get(coverageIndex).getStoreId());
                    jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                    jsonObject.put("Latitude", coverageList.get(coverageIndex).getLatitude());
                    jsonObject.put("Longitude", coverageList.get(coverageIndex).getLongitude());
                    jsonObject.put("ReasonId", coverageList.get(coverageIndex).getReasonid());
                    jsonObject.put("SubReasonId", coverageList.get(coverageIndex).getSub_reasonId());
                    jsonObject.put("Remark", coverageList.get(coverageIndex).getRemark());
                    jsonObject.put("ImageName", coverageList.get(coverageIndex).getImage());
                    jsonObject.put("AppVersion", app_ver);
                    jsonObject.put("Coverage_Type", coverageList.get(coverageIndex).getCoverage_type());
                    jsonObject.put("UploadStatus", CommonString.KEY_P);
                    jsonObject.put("UserId", _UserId);
                    jsonObject.put("Distributor_Id", journeyPlan.getDistributorId());
                    jsonObject.put("Store_Type_Id", journeyPlan.getStoreTypeId());
                    jsonObject.put("Store_Category_Id", journeyPlan.getStoreCategoryId());
                    jsonObject.put("Classification_Id", journeyPlan.getClassificationId());
                    jsonObject.put("Checkout_Image",coverageList.get(coverageIndex).getCheckOut_Image());


                    jsonString = jsonObject.toString();
                    type = CommonString.COVERAGE_DETAIL;
                    //endregion
                    break;


                case "POSM_Deployment":
                    db.open();

                    ArrayList<CommonChillerDataGetterSetter> posmData = db.getPOSMDeploymentSavedData(Integer.valueOf(coverageList.get(coverageIndex).getStoreId()),coverageList.get(coverageIndex).getVisitDate());
                    if(posmData.size() > 0){
                        JSONArray posmDetails = new JSONArray();
                        for(int i=0;i<posmData.size();i++)
                        {
                            jsonObject = new JSONObject();
                            jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                            jsonObject.put("User_Id", _UserId);
                            jsonObject.put(CommonString.Store_Id, posmData.get(i).getStore_id());
                            jsonObject.put(CommonString.KEY_EXIST, posmData.get(i).getExist());
                            jsonObject.put(CommonString.KEY_IMAGE, posmData.get(i).getImg1());
                            if(posmData.get(i).getOld_deployment().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_OLD_POSM_DEPLOYMENT, 0);
                            }else{
                                jsonObject.put(CommonString.KEY_OLD_POSM_DEPLOYMENT, posmData.get(i).getOld_deployment());
                            }
                            if(posmData.get(i).getNew_deployment().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_NEW_POSM_DEPLOYMENT, 0);
                            }else{
                                jsonObject.put(CommonString.KEY_NEW_POSM_DEPLOYMENT, posmData.get(i).getNew_deployment());
                            }
                            jsonObject.put(CommonString.KEY_POSM_ID, posmData.get(i).getPosm_id());
                            if(posmData.get(i).getReason_id().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_REASON_ID,"0");
                            }else{
                                jsonObject.put(CommonString.KEY_REASON_ID, posmData.get(i).getReason_id());
                            }
                            jsonObject.put(CommonString.KEY_VISIT_DATE, posmData.get(i).getVisit_date());
                            posmDetails.put(jsonObject);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "POSM_Deployment");
                        jsonObject.put("JsonData", posmDetails.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    break;


                case "Bira_Chiller":
                    db.open();

                    ArrayList<CommonChillerDataGetterSetter> biraChillerData = db.getBiraChillerData(Integer.valueOf(coverageList.get(coverageIndex).getStoreId()),coverageList.get(coverageIndex).getVisitDate());

                    if(biraChillerData.size() > 0){
                        JSONArray biraChillerDetail = new JSONArray();
                        for(int i=0;i<biraChillerData.size();i++)
                        {
                            jsonObject = new JSONObject();
                            jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                            jsonObject.put("User_Id", _UserId);
                            jsonObject.put(CommonString.Store_Id, biraChillerData.get(i).getStore_id());
                            jsonObject.put(CommonString.KEY_EXIST, biraChillerData.get(i).getExist());
                            jsonObject.put(CommonString.KEY_VISIT_DATE, biraChillerData.get(i).getVisit_date());

                            if(biraChillerData.get(i).getCharging().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_CAHRGING, "0");
                            }else{
                                jsonObject.put(CommonString.KEY_CAHRGING, biraChillerData.get(i).getCharging());
                            }

                            if( biraChillerData.get(i).getCapacity().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_CAPACITY, 0);
                            }else{
                                jsonObject.put(CommonString.KEY_CAPACITY, biraChillerData.get(i).getCapacity());
                            }

                            if(biraChillerData.get(i).getPurity().equalsIgnoreCase("")){
                                jsonObject.put(CommonString.KEY_PURITY, "0");
                            }else{
                                jsonObject.put(CommonString.KEY_PURITY, biraChillerData.get(i).getPurity());
                            }

                            jsonObject.put(CommonString.KEY_IMAGE, biraChillerData.get(i).getImg1());
                            jsonObject.put(CommonString.KEY_SKU_ID, biraChillerData.get(i).getSku_id());

                            if(biraChillerData.get(i).getBiraPresence().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_PRESENT, "0");
                            }else{
                                jsonObject.put(CommonString.KEY_PRESENT, biraChillerData.get(i).getBiraPresence());
                            }
                            if (biraChillerData.get(i).getBiraQty().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_QUYANTITY, 0);
                            }else{
                                jsonObject.put(CommonString.KEY_QUYANTITY, biraChillerData.get(i).getBiraQty());
                            }

                            biraChillerDetail.put(jsonObject);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Bira_Chiller");
                        jsonObject.put("JsonData", biraChillerDetail.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    break;


                case "Retailer_Owned_Chiller":
                    db.open();
                    ArrayList<CommonChillerDataGetterSetter> retailerChillerData = db.getRetailerOwnedChillerData(Integer.valueOf(coverageList.get(coverageIndex).getStoreId()),coverageList.get(coverageIndex).getVisitDate());

                    if(retailerChillerData.size() > 0){
                        JSONArray biraChillerDetail = new JSONArray();
                        for(int i=0;i<retailerChillerData.size();i++)
                        {
                            jsonObject = new JSONObject();
                            jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                            jsonObject.put("User_Id", _UserId);
                            jsonObject.put(CommonString.Store_Id, retailerChillerData.get(i).getStore_id());
                            jsonObject.put(CommonString.KEY_EXIST, retailerChillerData.get(i).getExist());
                            jsonObject.put(CommonString.KEY_VISIT_DATE, retailerChillerData.get(i).getVisit_date());

                            if(retailerChillerData.get(i).getCharging().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_CAHRGING, "0");
                            }else{
                                jsonObject.put(CommonString.KEY_CAHRGING, retailerChillerData.get(i).getCharging());
                            }

                            if( retailerChillerData.get(i).getCapacity().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_CAPACITY, 0);
                            }else{
                                jsonObject.put(CommonString.KEY_CAPACITY, retailerChillerData.get(i).getCapacity());
                            }

                            if(retailerChillerData.get(i).getPurity().equalsIgnoreCase("")){
                                jsonObject.put(CommonString.KEY_PURITY, "0");
                            }else{
                                jsonObject.put(CommonString.KEY_PURITY, retailerChillerData.get(i).getPurity());
                            }

                            jsonObject.put(CommonString.KEY_IMAGE, retailerChillerData.get(i).getImg1());
                            jsonObject.put(CommonString.KEY_SKU_ID, retailerChillerData.get(i).getSku_id());

                            if(retailerChillerData.get(i).getBiraPresence().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_PRESENT, "0");
                            }else{
                                jsonObject.put(CommonString.KEY_PRESENT, retailerChillerData.get(i).getBiraPresence());
                            }
                            if (retailerChillerData.get(i).getBiraQty().equalsIgnoreCase("")) {
                                jsonObject.put(CommonString.KEY_QUYANTITY, 0);
                            }else{
                                jsonObject.put(CommonString.KEY_QUYANTITY, retailerChillerData.get(i).getBiraQty());
                            }

                            biraChillerDetail.put(jsonObject);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Retailer_Owned_Chiller");
                        jsonObject.put("JsonData", biraChillerDetail.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    break;

                case "Competition_Visibility":
                    db.open();
                    ArrayList<CommonDataGetterSetter> competition_visibility = db.getCompetitionData(Integer.valueOf(coverageList.get(coverageIndex).getStoreId()),coverageList.get(coverageIndex).getVisitDate());
                    if (competition_visibility.size() > 0) {
                        JSONArray competitionVisibility = new JSONArray();
                        for (int j = 0; j < competition_visibility.size(); j++) {
                            JSONObject obj = new JSONObject();
                            obj.put("MID", coverageList.get(coverageIndex).getMID());
                            obj.put("User_Id", _UserId);
                            obj.put(CommonString.Store_Id, competition_visibility.get(j).getStore_id());
                            obj.put(CommonString.KEY_VISIT_DATE, competition_visibility.get(j).getVisit_date());
                            obj.put(CommonString.KEY_PRESENT, competition_visibility.get(j).getPresent());
                            obj.put(CommonString.KEY_IMAGE1, competition_visibility.get(j).getImg1());
                            obj.put(CommonString.KEY_IMAGE2, competition_visibility.get(j).getImg2());
                            obj.put(CommonString.KEY_IMAGE3, competition_visibility.get(j).getImg3());
                            competitionVisibility.put(obj);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Competition_Visibility");
                        jsonObject.put("JsonData", competitionVisibility.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    break;

                case "Consumer_Promo_Coupon":
                    db.open();
                    CommonDataGetterSetter consumerPromoCoupon  =  db.getConsumerPromoCouponData(Integer.valueOf(coverageList.get(coverageIndex).getStoreId()),coverageList.get(coverageIndex).getVisitDate());

                        JSONArray  consumerPromoCouponDetails = new JSONArray();
                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("User_Id", _UserId);
                        jsonObject.put(CommonString.Store_Id, consumerPromoCoupon.getStore_id());
                        jsonObject.put(CommonString.KEY_VISIT_DATE, consumerPromoCoupon.getVisit_date());
                        jsonObject.put(CommonString.KEY_PRESENT, consumerPromoCoupon.getPresent());
                        jsonObject.put(CommonString.KEY_QUYANTITY, consumerPromoCoupon.getQty());

                        consumerPromoCouponDetails.put(jsonObject);

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Consumer_Promo_Coupon");
                        jsonObject.put("JsonData", consumerPromoCouponDetails.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;


                    break;

                case "Previous_Day_Stock":

                    db.open();

                    ArrayList<CommonChillerDataGetterSetter> previousDyaStockData = db.getPreviousDayStockData(Integer.valueOf(coverageList.get(coverageIndex).getStoreId()),coverageList.get(coverageIndex).getVisitDate());
                    if(previousDyaStockData.size() > 0){
                        JSONArray previousDyaStockDetails = new JSONArray();

                        for(int i=0;i<previousDyaStockData.size();i++)
                        {
                            jsonObject = new JSONObject();
                            jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                            jsonObject.put("User_Id", _UserId);
                            jsonObject.put(CommonString.Store_Id, previousDyaStockData.get(i).getStore_id());
                            jsonObject.put(CommonString.KEY_VISIT_DATE, previousDyaStockData.get(i).getVisit_date());
                            jsonObject.put(CommonString.KEY_EXIST, previousDyaStockData.get(i).getExist());
                            jsonObject.put(CommonString.KEY_CLOSING_STOCK, previousDyaStockData.get(i).getBiraQty());
                            jsonObject.put(CommonString.KEY_SKU_ID, previousDyaStockData.get(i).getSku_id());
                            previousDyaStockDetails.put(jsonObject);
                        }

                        jsonObject = new JSONObject();
                        jsonObject.put("MID", coverageList.get(coverageIndex).getMID());
                        jsonObject.put("Keys", "Previous_Day_Stock");
                        jsonObject.put("JsonData", previousDyaStockDetails.toString());
                        jsonObject.put("UserId", _UserId);

                        jsonString = jsonObject.toString();
                        type = CommonString.UPLOADJsonDetail;

                    }
                    break;



            }
            //endregion
            try {
                final String finalKeyName = keyList.get(keyIndex);
                final int[] finalJsonIndex = {keyIndex};

                if (jsonString != null && !jsonString.equalsIgnoreCase("")) {

                    OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .readTimeout(20, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .build();

                    pd.setMessage("Uploading (" + keyIndex + "/" + keyList.size() + ") \n" + keyList.get(keyIndex) + "\n Store uploading " + (coverageIndex + 1) + "/" + coverageList.size());
                    RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
                    adapter = new Retrofit.Builder().baseUrl(CommonString.URL2).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
                    PostApi api = adapter.create(PostApi.class);
                    Call<ResponseBody> call = null;

                    if (type == CommonString.COVERAGE_DETAIL) {
                        call = api.getCoverageDetail(jsonData);
                    } else if (type == CommonString.UPLOADJsonDetail) {
                        call = api.getUploadJsonDetail(jsonData);
                    }


                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            ResponseBody responseBody = response.body();
                            String data = null;
                            if (responseBody != null && response.isSuccessful()) {
                                try {
                                    data = response.body().string();
                                    if (data.equalsIgnoreCase("")) {
                                        data_global[0] = "";
                                        AlertandMessages.showAlert((Activity) context, "Invalid Data : problem occured at " + keyList.get(keyIndex), true);
                                    } else {
                                        data = data.substring(1, data.length() - 1).replace("\\", "");
                                        data_global[0] = data;

                                        if (finalKeyName.equalsIgnoreCase("CoverageDetail_latest")) {
                                            try {
                                                coverageList.get(coverageIndex).setMID(Integer.parseInt(data_global[0]));
                                                finalJsonIndex[0]++;
                                                if (finalJsonIndex[0] != keyList.size()) {
                                                    uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex, uploadFlag);
                                                } else {
                                                    pd.setMessage("updating status :" + coverageIndex);
                                                    //uploading status D for current store from coverageList
                                                    updateStatus(coverageList, coverageIndex, CommonString.KEY_D,uploadFlag);
                                                }
                                            } catch (NumberFormatException ex) {
                                                AlertandMessages.showAlertMessage((Activity) context, "Error in Uploading Data at " + finalKeyName, true,uploadFlag);
                                            }
                                        } else if (data_global[0].contains(CommonString.KEY_SUCCESS)) {
                                            finalJsonIndex[0]++;
                                            if (finalJsonIndex[0] != keyList.size()) {
                                                uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex, uploadFlag);
                                            } else {
                                                pd.setMessage("updating status :" + coverageIndex);
                                                //uploading status D for current store from coverageList
                                                updateStatus(coverageList, coverageIndex, CommonString.KEY_D, uploadFlag);
                                            }
                                        } else {
                                            AlertandMessages.showAlertMessage((Activity) context, "Error in Uploading Data at " + finalKeyName + " : " + data_global[0], true,uploadFlag);
                                        }
                                    }

                                } catch (Exception e) {
                                    pd.dismiss();
                                    AlertandMessages.showAlertMessage((Activity) context, "Error in Uploading Data at " + finalKeyName, true,uploadFlag);
                                }
                            } else {
                                pd.dismiss();
                                AlertandMessages.showAlertMessage((Activity) context, "Error in Uploading Data at " + finalKeyName, true,uploadFlag);

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            isvalid = true;
                            pd.dismiss();
                            if (t instanceof SocketTimeoutException) {
                                AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                            } else if (t instanceof IOException) {
                                AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                            } else if (t instanceof SocketException) {
                                AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                            } else {
                                AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                            }

                        }
                    });

                } else {
                    finalJsonIndex[0]++;
                    if (finalJsonIndex[0] != keyList.size()) {
                        uploadDataWithoutWait(keyList, finalJsonIndex[0], coverageList, coverageIndex, uploadFlag);
                    } else {
                        pd.setMessage("updating status :" + coverageIndex);
                        //uploading status D for current store from coverageList
                        updateStatus(coverageList, coverageIndex, CommonString.KEY_D, uploadFlag);
                    }
                }
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public void UploadImageFileJsonList(final Context context, final String coverageDate, final String uploadFlag) {
        try {
            int totalfiles = 0;
            String jsonString;
            File f = new File(CommonString.FILE_PATH);
            File file[] = f.listFiles();
            JSONObject list = new JSONObject();

            totalfiles = f.listFiles().length;
            if (totalfiles == 0) {
                list.put("[ 0 ]", "no files");
            } else {
                for (int i = 0; i < file.length; i++) {
                    list.put("[" + i + "]", file[i].getName());
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("MID", "0");
            jsonObject.put("Keys", "FileList");
            jsonObject.put("JsonData", list.toString());
            jsonObject.put("UserId", _UserId);
            jsonString = jsonObject.toString();
            status = 0;
            isvalid = false;

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();

            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL2).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
            PostApi api = adapter.create(PostApi.class);
            Call<JSONObject> observable = api.getUploadJsonDetailForFileList(jsonData);
            observable.enqueue(new Callback<JSONObject>() {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    uploadImage(coverageDate,uploadFlag);
                    if (response.isSuccessful() && response.message().equalsIgnoreCase("OK")) {
                        isvalid = true;
                        status = 1;
                    } else {
                        isvalid = true;
                        status = 0;
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t) {
                    isvalid = true;
                    uploadImage(coverageDate, uploadFlag);
                    if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                        status = -1;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    void updateStatus(final ArrayList<CoverageBean> coverageList, final int coverageIndex, String status, final String uploadFlag) {
        if (coverageList.get(coverageIndex) != null) {
            try {

                final int[] tempcoverageIndex = {coverageIndex};
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StoreId", coverageList.get(coverageIndex).getStoreId());
                jsonObject.put("VisitDate", coverageList.get(coverageIndex).getVisitDate());
                jsonObject.put("UserId", _UserId);
                jsonObject.put("Status", status);
                jsonObject.put("Mer_Id", coverageList.get(coverageIndex).getMerId());

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .readTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .build();

                RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                adapter = new Retrofit.Builder().baseUrl(CommonString.URL2).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
                PostApi api = adapter.create(PostApi.class);
                Call<ResponseBody> call = null;

                call = api.getCoverageStatusDetail(jsonData);

                pd.setMessage("Uploading store status " + (coverageIndex + 1) + "/" + coverageList.size());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        String data = null;
                        if (responseBody != null && response.isSuccessful()) {
                            try {
                                data = response.body().string();
                                if (data.equalsIgnoreCase("")) {
                                    pd.dismiss();
                                    AlertandMessages.showAlert((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true);
                                } else {
                                    data = data.substring(1, data.length() - 1).replace("\\", "");
                                    if (data.contains("1")) {
                                        db.open();
                                        db.updateCheckoutStatus(coverageList.get(tempcoverageIndex[0]).getStoreId(), CommonString.KEY_D);

                                        db.deleteTableWithStoreID(coverageList.get(tempcoverageIndex[0]).getStoreId());
                                        tempcoverageIndex[0]++;
                                        if (tempcoverageIndex[0] != coverageList.size()) {
                                            uploadDataUsingCoverageRecursive(coverageList, tempcoverageIndex[0], uploadFlag, date);

                                        } else {

                                            pd.setMessage("Uploading images");
                                            String coverageDate = null;
                                            if (coverageList.size() > 0) {
                                                coverageDate = coverageList.get(0).getVisitDate();
                                            } else {
                                                coverageDate = date;
                                            }
                                            UploadImageFileJsonList(context, coverageDate, uploadFlag);
                                        }

                                    } else {
                                        pd.dismiss();
                                        AlertandMessages.showAlertMessage((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true,uploadFlag);
                                    }

                                }
                            } catch (Exception e) {
                                pd.dismiss();
                                AlertandMessages.showAlertMessage((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true,uploadFlag);
                            }
                        } else {
                            pd.dismiss();
                            AlertandMessages.showAlertMessage((Activity) context, "Error in Uploading status at coverage :" + coverageIndex, true,uploadFlag);

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isvalid = true;
                        pd.dismiss();
                        if (t instanceof SocketTimeoutException) {
                            AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                        } else if (t instanceof IOException) {
                            AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                        } else if (t instanceof SocketException) {
                            AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                        } else {
                            AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true,uploadFlag);
                        }

                    }
                });


            } catch (JSONException ex) {

            }
        }

    }

    void uploadImage(String coverageDate, String uploadFlag) {

        File f = new File(CommonString.FILE_PATH);
        File file[] = f.listFiles();

        if (file.length > 0) {
            UploadImageWithRetrofitOne.uploadedFiles = 0;
            UploadImageWithRetrofitOne.totalFiles = file.length;
            UploadImageWithRetrofitOne uploadImg = new UploadImageWithRetrofitOne(date, _UserId, context);
            uploadImg.UploadImageRecursive(context,uploadFlag);
        } else {
            UploadImageWithRetrofitOne.totalFiles = file.length;
            new StatusUpload(coverageDate,uploadFlag).execute();
        }
    }

    class StatusUpload extends AsyncTask<String, String, String> {
        String coverageDate;
        String uploadFlag="";

        public StatusUpload(String coverageDate, String uploadFlag) {
            this.coverageDate = coverageDate;
            this.uploadFlag = uploadFlag;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                db = new BiraDB(context);
                db.open();

                ArrayList<JourneyPlan> JCP = db.getStoreData(coverageDate);
                for (int i = 0; i < JCP.size(); i++) {
                    if (JCP.get(i).getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("StoreId", JCP.get(i).getStoreId());
                        jsonObject.put("VisitDate", coverageDate);
                        jsonObject.put("UserId", _UserId);
                        jsonObject.put("Status", CommonString.KEY_U);

                        UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context);
                        String jsonString2 = jsonObject.toString();
                        String result = upload.downloadDataUniversal(jsonString2, CommonString.COVERAGEStatusDetail);

                        if (result.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                            statusUpdated = false;
                            throw new SocketTimeoutException();
                        } else if (result.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                            statusUpdated = false;
                            throw new IOException();
                        } else if (result.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                            statusUpdated = false;
                            throw new JsonSyntaxException("Coverage Status Detail");
                        } else if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                            statusUpdated = false;
                            throw new Exception();
                        } else {
                            statusUpdated = true;
                            db.open();
                            db.updateCheckoutStatus(String.valueOf(JCP.get(i).getStoreId()), CommonString.KEY_U);
                        }
                    }
                }


            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true,uploadFlag);
            } catch (IOException e) {
                e.printStackTrace();
                AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_SOCKETEXCEPTION, true,uploadFlag);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                AlertandMessages.showAlertMessage((Activity) context, CommonString.MESSAGE_INVALID_JSON, true,uploadFlag);
            } catch (Exception e) {
                e.printStackTrace();

            }
            if (statusUpdated) {
                return CommonString.KEY_SUCCESS;
            } else {
                return CommonString.KEY_FAILURE;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pd.dismiss();
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                if (UploadImageWithRetrofitOne.totalFiles == uploadedFiles && statusUpdated) {
                    AlertandMessages.showAlertMessage((Activity) context, "All images uploaded Successfully", true,uploadFlag);
                } else if (UploadImageWithRetrofitOne.totalFiles == uploadedFiles && !statusUpdated) {
                    AlertandMessages.showAlertMessage((Activity) context, "All images uploaded Successfully, but status not updated", true,uploadFlag);
                } else {
                    AlertandMessages.showAlertMessage((Activity) context, "Some images not uploaded", true,uploadFlag);
                }
            }
        }
    }


    public String downloadDataUniversal(final String jsonString, int type) {
        try {
            status = 0;
            isvalid = false;
            final String[] data_global = {""};
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();

            adapter = new Retrofit.Builder()
                    .baseUrl(CommonString.URL2)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
            PostApi api = adapter.create(PostApi.class);
            Call<ResponseBody> call = null;
            if (type == CommonString.LOGIN_SERVICE) {
                call = api.getLogindetail(jsonData);
            } else if (type == CommonString.DOWNLOAD_ALL_SERVICE) {
                call = api.getDownloadAll(jsonData);
            } else if (type == CommonString.COVERAGE_DETAIL) {
                call = api.getCoverageDetail(jsonData);
            } else if (type == CommonString.UPLOADJCPDetail) {
                call = api.getUploadJCPDetail(jsonData);
            } else if (type == CommonString.UPLOADJsonDetail) {
                call = api.getUploadJsonDetail(jsonData);
            } else if (type == CommonString.COVERAGEStatusDetail) {
                call = api.getCoverageStatusDetail(jsonData);
            } else if (type == CommonString.CHECKOUTDetail) {
                call = api.getCheckout(jsonData);
            } else if (type == CommonString.DELETE_COVERAGE) {
                call = api.deleteCoverageData(jsonData);
            }

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ResponseBody responseBody = response.body();
                    String data = null;
                    if (responseBody != null && response.isSuccessful()) {
                        try {
                            data = response.body().string();
                            if (data.equalsIgnoreCase("")) {
                                data_global[0] = "";
                                isvalid = true;
                                status = 1;
                            } else {
                                data = data.substring(1, data.length() - 1).replace("\\", "");
                                data_global[0] = data;
                                isvalid = true;
                                status = 1;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            isvalid = true;
                            status = -2;
                        }
                    } else {
                        isvalid = true;
                        status = -1;
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    isvalid = true;
                    if (t instanceof SocketTimeoutException) {
                        status = 3;
                    } else if (t instanceof IOException) {
                        status = 3;
                    } else {
                        status = 3;
                    }

                }
            });

            while (isvalid == false) {
                synchronized (this) {
                    this.wait(25);
                }
            }
            if (isvalid) {
                synchronized (this) {
                    this.notify();
                }
            }
            if (status == 1) {
                return data_global[0];
            } else if (status == 2) {
                return CommonString.MESSAGE_NO_RESPONSE_SERVER;
            } else if (status == 3) {
                return CommonString.MESSAGE_SOCKETEXCEPTION;
            } else if (status == -2) {
                return CommonString.MESSAGE_INVALID_JSON;
            } else {
                return CommonString.KEY_FAILURE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonString.KEY_FAILURE;
        }
    }

}
