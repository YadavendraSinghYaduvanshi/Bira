package com.cpm.bira.upload;


import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface PostApi {

    @POST("LoginDetaillatest")
    Call<ResponseBody> getLogindetail(@Body RequestBody request);

    @POST("Uploadimages")
    Call<String> getUploadImage(@Body RequestBody request);

    @POST("DownloadAll")
    Call<ResponseBody> getDownloadAll(@Body RequestBody request);

    @POST("CoverageDetail_latest")
    Call<ResponseBody> getCoverageDetail(@Body RequestBody request);

    @POST("UploadJCPDetail")
    Call<ResponseBody> getUploadJCPDetail(@Body RequestBody request);

    @POST("UploadJsonDetail")
    Call<ResponseBody> getUploadJsonDetail(@Body RequestBody request);

    @POST("UploadJsonDetail")
    Call<JSONObject> getUploadJsonDetailForFileList(@Body RequestBody request);

    @POST("CoverageStatusDetail")
    Call<ResponseBody> getCoverageStatusDetail(@Body RequestBody request);


    @POST("CheckoutDetail")
    Call<ResponseBody> getCheckout(@Body RequestBody request);

    @POST("DeleteCoverage")
    Call<ResponseBody> deleteCoverageData(@Body RequestBody request);

    @POST("SUP_ATTENDANCE")
    Call<ResponseBody> setSupAttendanceData(@Body RequestBody request);

    @POST("SUP_DEVIATION")
    Call<ResponseBody> setSupDeviationData(@Body RequestBody jsonData);

    @POST("UploadJsonDetail")
    Call<JsonObject> getGeotag(@Body RequestBody request);

}
