package com.cpm.bira.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.BrandMasterGetterSetter;
import com.cpm.bira.GetterSetter.JourneyPlanGetterSetter;
import com.cpm.bira.GetterSetter.MappingPOSMGetterSetter;
import com.cpm.bira.GetterSetter.MappingStockGetterSetter;
import com.cpm.bira.GetterSetter.NonPosmReasonGetterSetter;
import com.cpm.bira.GetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.bira.GetterSetter.PosmMasterGetterSetter;
import com.cpm.bira.GetterSetter.ReferenceVariablesForDownloadActivity;
import com.cpm.bira.GetterSetter.SkuMasterGetterSetter;
import com.cpm.bira.GetterSetter.TableQuery;
import com.cpm.bira.GetterSetter.TableStructureGetterSetter;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by upendra on 5/22/2018.
 */

public class DownloadDataWithRetrofit extends ReferenceVariablesForDownloadActivity {

    boolean isvalid;
    RequestBody body1;
    private Retrofit adapter;
    Context context;
    public static int uploadedFiles = 0;
    public int listSize = 0;
    int status = 0;
    BiraDB db;
    ProgressDialog pd;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String _UserId, date, app_ver;
    String[] jj;

    private NonWorkingReasonGetterSetter nonWorkingReasonObj;
    private SkuMasterGetterSetter skuMasterObj;
    private PosmMasterGetterSetter posmMasterObj;
    private NonPosmReasonGetterSetter nonPosmReasonObj;
    private MappingStockGetterSetter mappingStockObj;
    private BrandMasterGetterSetter brandMasterObj;
    private MappingPOSMGetterSetter mappingPOSMObj;
    private JourneyPlanGetterSetter journeyPlanObj;

    int from;
    public DownloadDataWithRetrofit(Context context) {
        this.context = context;
    }

    public DownloadDataWithRetrofit(Context context, BiraDB db, ProgressDialog pd, int from) {
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
            } else if (type == CommonString.SUP_ATTENDANCE) {
                call = api.setSupAttendanceData(jsonData);
            }else if (type == CommonString.DEVIATION_DETAILS) {
                call = api.setSupDeviationData(jsonData);
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
                            Crashlytics.logException(e);
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
            Crashlytics.logException(e);
            e.printStackTrace();
            return CommonString.KEY_FAILURE;
        }
    }



    public File saveBitmapToFileSmaller(File file) {
        File file2 = file;
        try {
            int inWidth = 0;
            int inHeight = 0;

            InputStream in = new FileInputStream(file2);
            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            // decode full image pre-resized
            in = new FileInputStream(file2);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)
            options.inSampleSize = Math.max(inWidth / 800, inHeight / 500);
            // decode full image
            Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

            // calc exact destination size
            Matrix m = new Matrix();
            RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
            RectF outRect = new RectF(0, 0, 800, 500);
            m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
            float[] values = new float[9];
            m.getValues(values);
            // resize bitmap
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]), (int) (roughBitmap.getHeight() * values[4]), true);
            // save image
            FileOutputStream out = new FileOutputStream(file2);
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

        } catch (Exception e) {
            Crashlytics.logException(e);
            Log.e("Image", e.toString(), e);
            return file;
        }
        return file2;
    }


    public void UploadImageRecursive(final Context context) {
        try {
            String filename = null, foldername = null;
            int totalfiles = 0;
            File f = new File(CommonString.FILE_PATH);
            File file[] = f.listFiles();
            if (file.length > 0) {
                filename = "";
                totalfiles = f.listFiles().length;
                for (int i = 0; i < file.length; i++) {
                    if (new File(CommonString.FILE_PATH + file[i].getName()).exists()) {
                        if (file[i].getName().contains("store_img")) {
                            foldername = "Coverage";
                        } else if (file[i].getName().contains("PrimWinImg")
                                || file[i].getName().contains("Sec_backwall")
                                || file[i].getName().contains("SecWin")
                                || file[i].getName().contains("POSM")
                                ) {
                            foldername = "StoreImages";
                        } else if (file[i].getName().contains("NonWorking")) {
                            foldername = "DealorBoardImages";
                        } else if (file[i].getName().contains("Geotag")) {
                            foldername = "StoreGeoTagImages";
                        } else if (file[i].getName().contains("MyPOSM")) {
                            foldername = "DistributorImages";
                        } else if (file[i].getName().contains("visitor_intime") || file[i].getName().contains("visitor_outtime")) {
                            foldername = "VisitorLoginImages";
                        } else if (file[i].getName().contains("backwall_topup")
                                || file[i].getName().contains("shelf1_topup")
                                || file[i].getName().contains("shelf2_topup")
                                || file[i].getName().contains("dealer_board_topup")
                                || file[i].getName().contains("POSM_topup")) {
                            foldername = "TopUpStore";
                        } else if (file[i].getName().contains("SSP")) {
                            foldername = "SS_Primary";
                        } else if (file[i].getName().contains("SSD")) {
                            foldername = "SS_Secondary";
                        } else if (file[i].getName().contains("SST")) {
                            foldername = "SS_Touchpoint";
                        } else if (file[i].getName().contains("SSC")) {
                            foldername = "SS_Competition";
                        } else if (file[i].getName().contains("SSPR")) {
                            foldername = "SS_Promotion";
                        } else {
                            foldername = "BulkImages";
                        }
                        filename = file[i].getName();
                    }
                    break;
                }


                status = 0;
                File originalFile = new File(CommonString.FILE_PATH + filename);
                final File finalFile = saveBitmapToFileSmaller(originalFile);
                isvalid = false;

                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), finalFile);
                // MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, requestFile);
                // MultipartBody.Part body = MultipartBody.Part.createFormData("file", filename, requestFile).createFormData("Foldername", foldername);
                //  RequestBody name = RequestBody.create(MediaType.parse("application/octet-stream"), "upload_test");
                // add another part within the multipart request

                body1 = new MultipartBody.Builder()
                        .setType(MediaType.parse("multipart/form-data"))
                        .addFormDataPart("file", finalFile.getName(), requestFile)
                        .addFormDataPart("Foldername", foldername)
                        .build();

                adapter = new Retrofit.Builder()
                        .baseUrl(CommonString.URL3)
                        //.addConverterFactory(new StringConverterFactory())
                        .build();

                PostApi api = adapter.create(PostApi.class);
                Call<String> observable = api.getUploadImage(body1);
                observable.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.message().equalsIgnoreCase("")) {
                            finalFile.delete();
                            uploadedFiles++;
                            isvalid = true;
                            status = 1;
                        } else {
                            isvalid = true;
                            status = 0;
                            uploadedFiles++;
                        }
                        if (status == 0) {
                            AlertandMessages.showAlert((Activity) context, "not uploaded", false);
                        } else {
                            UploadImageRecursive(context);
                        }

                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        isvalid = true;
                        // Toast.makeText(context, finalFile.getName() + " not uploaded", Toast.LENGTH_SHORT).show();
                        if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                            status = -1;
                            AlertandMessages.showAlert((Activity) context, "Error in upload", false);
                        }
                    }
                });

            }

        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public void UploadImageFileJsonList(final Context context, final String coverageDate) {
        try {
            String filename = null, foldername = null;
            int totalfiles = 0;
            String jsonString;
            File f = new File(CommonString.FILE_PATH);
            File file[] = f.listFiles();
            JSONObject list = new JSONObject();
            filename = "";
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
                    //uploadImage(coverageDate); :remove comment while upload
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
                    // uploadImage(coverageDate); :remove comment while upload
                    // Toast.makeText(context, finalFile.getName() + " not uploaded", Toast.LENGTH_SHORT).show();
                    if (t instanceof IOException || t instanceof SocketTimeoutException || t instanceof SocketException) {
                        status = -1;
                        // AlertandMessages.showAlert((Activity) context, "Error in FileList upload", false);
                    }
                }
            });
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public void downloadDataUniversalWithoutWait(final ArrayList<String> jsonStringList, final ArrayList<String> KeyNames, final int downloadindex, int type, final int downloadFlag) {
        status = 0;
        isvalid = false;
        final String[] data_global = {""};
        String jsonString = "", KeyName = "";
        int jsonIndex = 0;

        if (jsonStringList.size() > 0) {

            jsonString = jsonStringList.get(downloadindex);
            KeyName = KeyNames.get(downloadindex);
            jsonIndex = downloadindex;

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();

            pd.setMessage("Downloading (" + downloadindex + "/" + listSize + ") \n" + KeyName + "");
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL2)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
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

            final int[] finalJsonIndex = {jsonIndex};
            final String finalKeyName = KeyName;
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

                            } else {
                                data = data.substring(1, data.length() - 1).replace("\\", "");
                                data_global[0] = data;
                                if (finalKeyName.equalsIgnoreCase("Table_Structure")) {

                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    tableStructureObj = new Gson().fromJson(data, TableStructureGetterSetter.class);
                                    String isAllTableCreated = createTable(tableStructureObj);
                                    if (isAllTableCreated != CommonString.KEY_SUCCESS) {
                                        pd.dismiss();
                                        AlertandMessages.showAlert((Activity) context, isAllTableCreated + " not created", true);
                                    }
                                } else {
                                    editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    switch (finalKeyName) {

                                        case "Journey_Plan":
                                            journeyPlanObj = new Gson().fromJson(data, JourneyPlanGetterSetter.class);
                                            db.open();
                                            if (journeyPlanObj != null && !db.insertJourneyPlanData(journeyPlanObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Journey plan data not saved");
                                            } else if(journeyPlanObj != null && !db.insertLoginVisitedDate(journeyPlanObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "login time not inserted");
                                            }
                                            break;

                                        case "Sku_Master":
                                            skuMasterObj = new Gson().fromJson(data, SkuMasterGetterSetter.class);
                                            db.open();
                                            if (skuMasterObj != null && !db.insertSkuMasterData(skuMasterObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Sku master data not saved");
                                            }
                                            break;

                                        case "Posm_Master":
                                            posmMasterObj = new Gson().fromJson(data, PosmMasterGetterSetter.class);
                                            db.open();
                                            if (posmMasterObj != null && !db.insertPosmMasterData(posmMasterObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "POSM master data not saved");
                                            }
                                            break;

                                        case "Non_Posm_Reason":
                                            nonPosmReasonObj = new Gson().fromJson(data, NonPosmReasonGetterSetter.class);
                                            db.open();
                                            if (nonPosmReasonObj != null && !db.insertPosmReasonData(nonPosmReasonObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Non POSM data not saved");
                                            }
                                            break;

                                        case "Mapping_Stock":
                                            mappingStockObj = new Gson().fromJson(data, MappingStockGetterSetter.class);
                                            db.open();
                                            if (mappingStockObj != null && !db.insertMappingStockData(mappingStockObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping stock data not saved");
                                            }
                                            break;
                                        case "Brand_Master":
                                            brandMasterObj = new Gson().fromJson(data, BrandMasterGetterSetter.class);
                                            db.open();
                                            if (brandMasterObj != null && !db.brandMasterObj(brandMasterObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Brand master data not saved");
                                            }
                                            break;

                                        case "Non_Working_Reason":
                                            nonWorkingReasonObj = new Gson().fromJson(data, NonWorkingReasonGetterSetter.class);
                                            db.open();
                                            if (nonWorkingReasonObj != null && !db.insertNonWorkingResionData(nonWorkingReasonObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Non working reason not saved");
                                            }
                                            break;

                                        case "Mapping_Posm":
                                            mappingPOSMObj = new Gson().fromJson(data, MappingPOSMGetterSetter.class);
                                            db.open();
                                            if (mappingPOSMObj != null && !db.insertMappingPOSMData(mappingPOSMObj)) {
                                                pd.dismiss();
                                                AlertandMessages.showSnackbarMsg(context, "Mapping POSM data not saved");
                                            }
                                            break;
                                    }
                                }
                            }

                            finalJsonIndex[0]++;
                            if (finalJsonIndex[0] != KeyNames.size()) {
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                editor.apply();
                                downloadDataUniversalWithoutWait(jsonStringList, KeyNames, finalJsonIndex[0], CommonString.DOWNLOAD_ALL_SERVICE, 1);
                            } else {
                                editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
                                editor.putBoolean(CommonString.DOWNLOAD_STATUS, true);
                                editor.apply();
                                AlertandMessages.showAlert((Activity) context, "Data downloaded successfully", true);

                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                            editor.apply();
                            pd.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);
                        }
                    } else {
                        editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, finalJsonIndex[0]);
                        editor.apply();
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    isvalid = true;
                    pd.dismiss();
                    if (t instanceof SocketTimeoutException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else if (t instanceof IOException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else if (t instanceof SocketException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    }

                }
            });

        } else {
            editor.putInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
            editor.apply();
        }
    }



/*    private class DownloadImageTask extends AsyncTask<String, String, String> {
        int downloadFlag;

        public DownloadImageTask(int downloadFlag) {
            this.downloadFlag = downloadFlag;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                downloadImages();
                return CommonString.KEY_SUCCESS;
            } catch (FileNotFoundException ex) {
                return CommonString.KEY_FAILURE;
            } catch (IOException ex) {
                return CommonString.KEY_FAILURE;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                pd.dismiss();
                if(downloadFlag == 1){
                    db.open();
                  //  db.deleteDistributorDat(date);
                    AlertandMessages.showAlert((Activity) context, "Data downloaded successfully", true);
                }else{
                    com.cpm.bira.Constant.AlertandMessages.showSnackbarMsg((Activity) context, "Data downloaded successfully");
                }
            } else {
                pd.dismiss();
                if(downloadFlag == 1){
                    AlertandMessages.showAlert((Activity) context, "Error in downloading", true);
                }else{
                    com.cpm.bira.Constant.AlertandMessages.showSnackbarMsg((Activity) context, "Error in downloading");
                }
            }
        }
    }*/


    String createTable(TableStructureGetterSetter tableGetSet) {
        List<TableQuery> tableList = tableGetSet.getResult();
        for (int i = 0; i < tableList.size(); i++) {
            String table = tableList.get(i).getSqlText();
            if (db.createtable(table) == 0) {
                return table;
            }
        }
        return CommonString.KEY_SUCCESS;
    }


    // Downloading category images.
    void downloadImages() throws IOException, FileNotFoundException {
        //region Category Images
    /*    if (categoryMasterObj != null) {

            for (int i = 0; i < categoryMasterObj.getCategoryMaster().size(); i++) {

                String image_name = categoryMasterObj.getCategoryMaster().get(i).getIcon();
                if (image_name != null && !image_name.equalsIgnoreCase("NA")
                        && !image_name.equalsIgnoreCase("")) {
                    URL url = new URL(categoryMasterObj.getCategoryMaster().get(i).getImagePath() + image_name);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.getResponseCode();
                    c.setConnectTimeout(20000);
                    c.connect();

                    if (c.getResponseCode() == 200) {

                        int length = c.getContentLength();

                        String size = new DecimalFormat("##.##")
                                .format((double) ((double) length / 1024))
                                + " KB";

                        File file = new File(CommonString.FILE_PATH_Downloaded);
                        file.mkdirs();

                        if (!new File(CommonString.FILE_PATH_Downloaded
                                + image_name).exists()
                                && !size.equalsIgnoreCase("0 KB")) {

                            jj = image_name.split("\\/");
                            image_name = jj[jj.length - 1];

                            File outputFile = new File(file,
                                    image_name);
                            FileOutputStream fos = new FileOutputStream(
                                    outputFile);
                            InputStream is1 = (InputStream) c
                                    .getInputStream();

                            int bytes = 0;
                            byte[] buffer = new byte[1024];
                            int len1 = 0;

                            while ((len1 = is1.read(buffer)) != -1) {

                                bytes = (bytes + len1);
                                fos.write(buffer, 0, len1);
                            }

                            fos.close();
                            is1.close();
                        }
                    }
                }


                String image_name2 = categoryMasterObj.getCategoryMaster().get(i).getIconDone();
                if (image_name2 != null && !image_name2.equalsIgnoreCase("NA")
                        && !image_name2.equalsIgnoreCase("")) {
                    URL url = new URL(categoryMasterObj.getCategoryMaster().get(i).getImagePath() + image_name2);
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setRequestMethod("GET");
                    c.getResponseCode();
                    c.setConnectTimeout(20000);
                    c.connect();

                    if (c.getResponseCode() == 200) {

                        int length = c.getContentLength();

                        String size = new DecimalFormat("##.##")
                                .format((double) ((double) length / 1024))
                                + " KB";

                        File file = new File(CommonString.FILE_PATH_Downloaded);
                        file.mkdirs();

                        if (!new File(CommonString.FILE_PATH_Downloaded
                                + image_name2).exists()
                                && !size.equalsIgnoreCase("0 KB")) {

                            jj = image_name2.split("\\/");
                            image_name2 = jj[jj.length - 1];

                            File outputFile = new File(file,
                                    image_name2);
                            FileOutputStream fos = new FileOutputStream(
                                    outputFile);
                            InputStream is1 = (InputStream) c
                                    .getInputStream();

                            int bytes = 0;
                            byte[] buffer = new byte[1024];
                            int len1 = 0;

                            while ((len1 = is1.read(buffer)) != -1) {

                                bytes = (bytes + len1);
                                fos.write(buffer, 0, len1);
                            }
                            fos.close();
                            is1.close();
                        }
                    }
                }
            }

        }*/
        //endregion
    }

  /*
    public void downloadUserStoreData(final ArrayList<String> jsonList, final ArrayList<String> keyNames, int downloadindex, int type, final String outlet, final String empId) {

        status = 0;
        isvalid = false;
        final String[] data_global = {""};
        String jsonString = "", KeyName = "";
        int jsonIndex = 0;

        if (jsonList.size() > 0) {

            jsonString = jsonList.get(downloadindex);
            KeyName = keyNames.get(downloadindex);
            jsonIndex = downloadindex;

            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .build();

            pd.setMessage("Downloading (" + downloadindex + "/" + listSize + ") \n" + KeyName + "");
            RequestBody jsonData = RequestBody.create(MediaType.parse("application/json"), jsonString);
            adapter = new Retrofit.Builder().baseUrl(CommonString.URL2)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            PostApi api = adapter.create(PostApi.class);
            Call<ResponseBody> call = null;
            call = api.getDownloadAll(jsonData);

            final int[] finalJsonIndex = {jsonIndex};
            final String finalKeyName = KeyName;
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

                            } else {
                                data = data.substring(1, data.length() - 1).replace("\\", "");
                                data_global[0] = data;
                                db.open();

                                if (finalKeyName.equalsIgnoreCase("Sup_Jounry_Plan_Today")){
                                    supJournyPlanTodayGObj = new Gson().fromJson(data, JCPGetterSetter.class);
                                    List<JourneyPlan> list = db.getJournyPlanTodayData("Sup_Jounry_Plan_Today",empId);
                                    if(list.size() == 0) {
                                        if (supJournyPlanTodayGObj != null && !db.insertJCPData(supJournyPlanTodayGObj, finalKeyName)) {
                                            pd.dismiss();
                                            AlertandMessages.showSnackbarMsg(context, "Sup Journey Plan Today data not saved");
                                        }
                                    }
                                }
                                else if(finalKeyName.equalsIgnoreCase("Sup_Jounry_Plan_Previous")){
                                    supJournyPlanPreviousGObj = new Gson().fromJson(data, JCPGetterSetterPrevious.class);
                                    db.open();
                                    List<JourneyPlan> list = db.getJournyPlanTodayData("Sup_Jounry_Plan_Previous",empId);
                                    if(list.size() == 0) {
                                        if (supJournyPlanPreviousGObj != null && !db.insertJCPDataPrevious(supJournyPlanPreviousGObj, finalKeyName)) {
                                            pd.dismiss();
                                            AlertandMessages.showSnackbarMsg(context, "Sup Journey Plan Previous data not saved");
                                        }
                                    }
                                }else {
                                    editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    switch (finalKeyName) {

                                        case "Sup_Primary_Shelf_Audit":
                                            primarySelfObj = new Gson().fromJson(data, SupPrimarySelfAuditGetterSetter.class);
                                            List<SupPrimaryShelfAudit> list = db.getPrimaryShelfInsertedData(empId);
                                            if(list.size() == 0) {
                                                if (primarySelfObj != null && !db.insertPrimarySelfAuditData(primarySelfObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Primary shelf data not saved");
                                                }
                                            }
                                            break;
                                        case "Sup_Mapping_Stock":
                                            supMappingStockObj = new Gson().fromJson(data, SupMappingStockGetterSetter.class);
                                            List<SupMappingStock> list2 = db.getMappingStockInsertedData(empId);
                                            if(list2.size() == 0) {
                                                if (supMappingStockObj != null && !db.insertSupMappingStockData(supMappingStockObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "SupMappingStock data not saved");
                                                }
                                            }
                                            break;

                                        case "Sup_Touchpoint_Audit":
                                            touchPointObj = new Gson().fromJson(data, SupTouchpointAuditGetterSetter.class);
                                            List<SupTouchpointAudit> list3 = db.getTouchPointInsertedData(empId);
                                            if(list3.size() == 0) {
                                                if (touchPointObj != null && !db.insertUserTouchPointData(touchPointObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Touch Point data not saved");
                                                }
                                            }
                                            break;

                                        case "Sup_Window_Audit":
                                            supWindowAutitObj = new Gson().fromJson(data, SupWindowAuditGetterSetter.class);
                                            List<SupWindowAudit> list4 = db.getWinodwInsertedData(empId);
                                            if(list4.size() == 0) {
                                                if (supWindowAutitObj != null && !db.insertWindowAuditData(supWindowAutitObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Window Audit data not saved");
                                                }
                                            }
                                            break;

                                        case "Sup_SelfService_SecondaryWindow_Audit":
                                            supSelfServiceSecondaryWindowAuditObj = new Gson().fromJson(data, SupSelfServiceSecondaryWindowAuditGetterSetter.class);
                                            List<SupSelfServiceSecondaryWindowAudit> list5 = db.getShelfServiceSecondaryWindowInsertedData(empId);
                                            if(list5.size() == 0) {
                                                if (supSelfServiceSecondaryWindowAuditObj != null && !db.insertSupShelfServiceSecondryWindowData(supSelfServiceSecondaryWindowAuditObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Shelf Service Secondry Window data not saved");
                                                }
                                                break;
                                            }

                                        case "Sup_Mapping_Selfservice_Category":
                                            supMappingSelfserviceCategoryObj = new Gson().fromJson(data, SupMappingSelfserviceCategoryGetterSetter.class);
                                                if (supMappingSelfserviceCategoryObj != null && !db.insertSupMappingShelfServiceCategoryData(supMappingSelfserviceCategoryObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup mapping Shelf Service category not saved");
                                                }
                                            break;

                                        case "Sup_Mapping_Selfservice_Category_Display":
                                            supMappingSelfserviceCategoryDisplayObj = new Gson().fromJson(data, SupMappingSelfserviceCategoryDisplayGetterSetter.class);

                                                if (supMappingSelfserviceCategoryDisplayObj != null && !db.insertSupMappingShelfServiceCategoryDisplayData(supMappingSelfserviceCategoryDisplayObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup mapping Shelf Service category display not saved");
                                                }
                                            break;


                                        case "Sup_SelfService_Promotion_Audit":

                                            supSelfServicePromotionAuditObj = new Gson().fromJson(data, SupSelfServicePromotionAuditGetterSetter.class);
                                            List<SupSelfServicePromotionAudit> list8 = db.getShelfServicePromotionInsertedData(empId);
                                            if(list8.size() == 0) {
                                                if (supSelfServicePromotionAuditObj != null && !db.insertSupSelfServicePromotionData(supSelfServicePromotionAuditObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Self service promotion data not saved");
                                                }
                                                break;
                                            }

                                        case "Sup_Selfservice_Promotion_Competition_Audit":
                                            supSelfservicePromotionCompetitionAuditObj = new Gson().fromJson(data, SupSelfservicePromotionCompetitionAuditGetterSetter.class);
                                            List<SupSelfservicePromotionCompetitionAudit> list9 = db.getShelfServicePromotionCompetitionInsertedData(empId);
                                            if(list9.size() == 0) {
                                                if (supSelfservicePromotionCompetitionAuditObj != null && !db.insertSupSelfServicePromotionCompetitionData(supSelfservicePromotionCompetitionAuditObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup mapping Shelf Service promotion competition data not saved");
                                                }
                                            }
                                            break;

                                        case "Sup_Selfservice_Touchpoint_Audit":

                                            supSelfserviceTouchpointAuditObj = new Gson().fromJson(data, SupSelfserviceTouchpointAuditGetterSetter.class);
                                            List<SupSelfserviceTouchpointAudit> list10 = db.getShelfServiceTouchPointInsertedData(empId);
                                            if(list10.size() == 0) {
                                                if (supSelfserviceTouchpointAuditObj != null && !db.insertSupSelfServiceTouchPointAuditData(supSelfserviceTouchpointAuditObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Self service touchpoint audit data not saved");
                                                }
                                            }
                                            break;

                                        case "Sup_SelfService_Primary_Audit" :
                                            supSelfServicePrimaryAuditObj = new Gson().fromJson(data, SupSelfServicePrimaryAuditGetterSetter.class);
                                            List<SupSelfServicePrimaryAudit> list11 = db.getShelfServicePrimaryInsertedData(empId);
                                            if(list11.size() == 0) {
                                                if (supSelfServicePrimaryAuditObj != null && !db.insertSupSelfServicePrimaryAuditData(supSelfServicePrimaryAuditObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Self service primary audit data not saved");
                                                }
                                            }
                                            break;


                                        case "Sup_Competition_Visibility":

                                         *//*   supSelfserviceTouchpointAuditObj = new Gson().fromJson(data, SupSelfserviceTouchpointAuditGetterSetter.class);
                                            List<SupSelfserviceTouchpointAudit> list12 = db.getShelfServiceTouchPointInsertedData(empId);
                                            if(list12.size() == 0) {
                                                if (supSelfserviceTouchpointAuditObj != null && !db.insertSupSelfServiceTouchPointAuditData(supSelfserviceTouchpointAuditObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Self service touchpoint audit data not saved");
                                                }
                                            }*//*
                                            break;

                                        case "Sup_SelfService_Competition_Visibility" :
                                            *//*supSelfServicePrimaryAuditObj = new Gson().fromJson(data, SupSelfServicePrimaryAuditGetterSetter.class);
                                            List<SupSelfServicePrimaryAudit> list13 = db.getShelfServicePrimaryInsertedData(empId);
                                            if(list13.size() == 0) {
                                                if (supSelfServicePrimaryAuditObj != null && !db.insertSupSelfServicePrimaryAuditData(supSelfServicePrimaryAuditObj)) {
                                                    pd.dismiss();
                                                    AlertandMessages.showSnackbarMsg(context, "Sup Self service primary audit data not saved");
                                                }
                                            }*//*
                                            break;
                                    }
                                }
                            }
                            if(!data.equalsIgnoreCase("") && finalKeyName.equalsIgnoreCase("Sup_Jounry_Plan_Previous") || !data.equalsIgnoreCase("") && finalKeyName.equalsIgnoreCase("Sup_Jounry_Plan_Today")){
                                finalJsonIndex[0]++;
                                if (finalJsonIndex[0] != keyNames.size())
                                {
                                    editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    downloadUserStoreData(jsonList, keyNames, finalJsonIndex[0], CommonString.DOWNLOAD_ALL_SERVICE, outlet, empId);
                                }
                                else
                                {
                                    editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, 0);
                                    editor.apply();
                                    pd.dismiss();
                                    com.cpm.gskgtsupervisor.constant.AlertandMessages.showAlertMsg((Activity) context, "All data downloaded Successfully", true, outlet);
                                }
                            }else{
                                if(finalKeyName.equalsIgnoreCase("Sup_Jounry_Plan_Today")){
                                    AlertandMessages.showAlert((Activity) context, "Today journey plan not found for selected merchandiser" , true);
                                    editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    pd.dismiss();
                                }else if(finalKeyName.equalsIgnoreCase("Sup_Jounry_Plan_Previous")){
                                    AlertandMessages.showAlert((Activity) context, "Previous day journey plan not found for selected merchandiser" , true);
                                    editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                    editor.apply();
                                    pd.dismiss();
                                }else{
                                    finalJsonIndex[0]++;
                                    if (finalJsonIndex[0] != keyNames.size())
                                    {
                                        editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, finalJsonIndex[0]);
                                        editor.apply();
                                        downloadUserStoreData(jsonList, keyNames, finalJsonIndex[0], CommonString.DOWNLOAD_ALL_SERVICE, outlet, empId);
                                    }
                                    else
                                    {
                                        editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, 0);
                                        editor.apply();
                                        pd.dismiss();
                                        com.cpm.gskgtsupervisor.constant.AlertandMessages.showAlertMsg((Activity) context, "Data downloaded successfully", true,outlet);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, finalJsonIndex[0]);
                            editor.apply();
                            pd.dismiss();
                            AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);
                        }
                    } else {
                        editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, finalJsonIndex[0]);
                        editor.apply();
                        pd.dismiss();
                        AlertandMessages.showAlert((Activity) context, "Error in downloading Data at " + finalKeyName, true);

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    isvalid = true;
                    pd.dismiss();
                    if (t instanceof SocketTimeoutException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else if (t instanceof IOException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else if (t instanceof SocketException) {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    } else {
                        AlertandMessages.showAlert((Activity) context, CommonString.MESSAGE_INTERNET_NOT_AVALABLE, true);
                    }

                }
            });

        } else {
            editor.putInt(CommonString.KEY_STORE_DOWNLOAD_INDEX, 0);
            editor.apply();
        }
    }


    }*/
}
