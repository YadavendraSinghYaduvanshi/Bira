package com.cpm.bira.upload;

import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;

public interface PostApiForFile {
    @POST("Uploadimages")
    Call<String> getUploadImage(@Body RequestBody reqesBody);

}
