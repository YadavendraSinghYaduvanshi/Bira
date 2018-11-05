package com.cpm.bira.DailyEntry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonFunctions;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CoverageBean;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.GetterSetter.NonWorkingReason;
import com.cpm.bira.R;
import com.cpm.bira.upload.DownloadDataWithRetrofit;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class NonWorkingActivity extends AppCompatActivity implements
        OnItemSelectedListener, OnClickListener {

    private Spinner reasonspinner;
    private BiraDB database;
    String reasonname, reasonid, entry_allow, image, remark="", reason_reamrk,intime, image_allow, gps_mandatory;
    FloatingActionButton save;
    boolean ResultFlag = true;
    private ArrayAdapter<CharSequence> reason_adapter;
    protected String _path, str, strflag;
    protected String _pathforcheck = "";
    private String image1, app_ver = "0";
    private SharedPreferences preferences;
    String _UserId, visit_date, visit_date_formatted, store_id, username;
    protected boolean status = true;
    AlertDialog alert;
    ImageButton camera;
    RelativeLayout  rel_cam;
    ArrayList<NonWorkingReason> reasondata = new ArrayList<>();
    Context context;
    ArrayList<CoverageBean> coverage = new ArrayList<CoverageBean>();
    JourneyPlan storelist;
    private Dialog dialog;
    String error_msg = "";
    TextView txt_label;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonworking_activity);
        declaration();

        database.open();
        storelist = database.getStoreData(visit_date,store_id);


        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        coverage   = database.getCoverageWithStoreID_Data(store_id,visit_date);
        // Getting Non Working data for non attendance
        reasondata = database.getNonWorkingData();
        reason_adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        reason_adapter.add(getResources().getString(R.string.select_reason));
        for (int i = 0; i < reasondata.size(); i++) {
            reason_adapter.add(reasondata.get(i).getReason());
        }

        reasonspinner.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reasonspinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        // TODO Auto-generated method stub

        switch (arg0.getId()) {
            case R.id.nonWorkingReason_spinner:
                if (position != 0) {
                    image1          = "";
                    reason_reamrk   = "";
                    camera.setImageDrawable(getResources().getDrawable(R.mipmap.camera_red));
                    reasonname      = reasondata.get(position - 1).getReason();
                    reasonid        = String.valueOf(reasondata.get(position - 1).getReasonId());
                    entry_allow     = String.valueOf(reasondata.get(position - 1).getEntryAllow());
                    image_allow     = String.valueOf(reasondata.get(position - 1).getImageAllow());
                    gps_mandatory   = String.valueOf(reasondata.get(position - 1).getGPSMandatory());

                    if (image_allow.equalsIgnoreCase("true")) {
                        rel_cam.setVisibility(View.VISIBLE);
                        image = "true";

                    } else {
                        rel_cam.setVisibility(View.GONE);
                        image = "false";
                    }

                } else {
                    reasonname = "";
                    reasonid = "0";
                    entry_allow = "false";
                    image_allow = "false";
                    image = "false";
                    gps_mandatory = "false";
                    rel_cam.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:

                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {
                        camera.setImageDrawable(getResources().getDrawable(R.mipmap.camera_green));
                        image1 = _pathforcheck;
                        _pathforcheck = "";
                    }
                }

                break;
        }

    }

    public boolean imageAllowed() {
        boolean result = true;
        if (image.equalsIgnoreCase("true")) {
            if (image1.equalsIgnoreCase("")) {
                result = false;
            }
        }
        return result;
    }

   /* public boolean textAllowed() {
        boolean result = true;
         if (reamrk.equalsIgnoreCase("true")) {
             remark = remark_text.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");;
            if(remark.equalsIgnoreCase("")){
                result = false;
            }else{
                result = true;
            }
        }
        return result;
    }*/


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.imgcam) {

            _pathforcheck = store_id + "_" + username.replace(".", "") + "_NonWorking-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
            _path = CommonString.FILE_PATH + _pathforcheck;
           // CommonFunctions.startCameraActivity((Activity) context, _path);
            CommonFunctions.startAnncaCameraActivity(context, _path,null,false);
        }
        if (v.getId() == R.id.save) {
            if (validatedata()) {
                if (imageAllowed()) {
                   /* if (textAllowed()) {*/
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    NonWorkingActivity.this);
                            builder.setMessage(R.string.title_activity_save_data)
                                    .setCancelable(false)
                                    .setPositiveButton(R.string.Ok,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    alert.getButton(
                                                            AlertDialog.BUTTON_POSITIVE)
                                                            .setEnabled(false);

                                                    ArrayList<CoverageBean> coverageBeanList = new ArrayList<>();

                                                    CoverageBean cdata = new CoverageBean();
                                                    cdata.setStoreId(store_id);
                                                    cdata.setVisitDate(visit_date);
                                                    cdata.setUserId(_UserId);
                                                    cdata.setInTime(intime);
                                                    cdata.setOutTime(getCurrentTime());
                                                    cdata.setReason(reasonname);
                                                    cdata.setReasonid(reasonid);
                                                    cdata.setLatitude("0.0");
                                                    cdata.setLongitude("0.0");
                                                    cdata.setImage(image1);
                                                    cdata.setRemark(remark);
                                                    cdata.setUploadStatus("U");
                                                    cdata.setSub_reasonId("0");
                                                    cdata.setCheckOut_Image("");

                                                    coverageBeanList.add(cdata);
                                                    uploadNonWorkingDataOnServer(coverageBeanList);

                                                }
                                            })
                                    .setNegativeButton(R.string.closed,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int id) {
                                                    dialog.cancel();
                                                }
                                            });

                            alert = builder.create();
                            alert.show();
                   /* } else {
                        Toast.makeText(getApplicationContext(), R.string.pleaseenterRemarks, Toast.LENGTH_SHORT).show();
                    }*/
                } else {
                    Toast.makeText(getApplicationContext(), R.string.title_activity_take_image, Toast.LENGTH_SHORT).show();
                }
            } else {
                AlertandMessages.showToastMsg(context, error_msg);
            }
        }

    }

    private void uploadNonWorkingDataOnServer(ArrayList<CoverageBean> coverageBeanList) {

        if (database.updateStoreStatusOnLeave(store_id, visit_date, "U", "Journey_Plan") > 0) {
            new GeoTagUpload(coverageBeanList).execute();
        } else {
           // AlertandMessages.showToastMsg(context, "Coverage not saved!!");
            AlertandMessages.showToastMsg(context, "Store status not updated!!");
        }

    }

    public boolean validatedata() {
        boolean result = false;
        if (reasonid != null && !reasonid.equalsIgnoreCase("") && !reasonid.equalsIgnoreCase("0")) {
            result = true;
        } else {
            error_msg = "Please select Reason";
        }
        return result;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:mmm");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            finish();

            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public class GeoTagUpload extends AsyncTask<Void, Void, String> {

        private ArrayList<CoverageBean> coverageBeanList;

        GeoTagUpload(ArrayList<CoverageBean> coverageBeanList) {
            this.coverageBeanList = coverageBeanList;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle(getResources().getString(R.string.dialog_title));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                for (int i = 0; i < coverageBeanList.size(); i++) {
                    DownloadDataWithRetrofit upload = new DownloadDataWithRetrofit(context);

                    JSONObject jsonObject;
                    String jsonString2 = "", result = "5";

                    //region Coverage Data
                    jsonObject = new JSONObject();

                    jsonObject.put("StoreId", coverageBeanList.get(i).getStoreId());
                    jsonObject.put("VisitDate", coverageBeanList.get(i).getVisitDate());
                    jsonObject.put("Latitude", coverageBeanList.get(i).getLatitude());
                    jsonObject.put("Longitude", coverageBeanList.get(i).getLongitude());
                    jsonObject.put("ReasonId", coverageBeanList.get(i).getReasonid());
                    jsonObject.put("SubReasonId", coverageBeanList.get(i).getSub_reasonId());
                    jsonObject.put("Remark", coverageBeanList.get(i).getRemark());
                    jsonObject.put("ImageName", coverageBeanList.get(i).getImage());
                    jsonObject.put("Checkout_Image", "");
                    jsonObject.put("AppVersion", app_ver);
                    jsonObject.put("UploadStatus", coverageBeanList.get(i).getUploadStatus());
                    jsonObject.put("UserId", _UserId);
                    jsonObject.put("Distributor_Id", storelist.getDistributorId());
                    jsonObject.put("Store_Type_Id", storelist.getStoreTypeId());
                    jsonObject.put("Store_Category_Id", storelist.getStoreCategoryId());
                    jsonObject.put("Classification_Id", storelist.getClassificationId());

                    jsonString2 = jsonObject.toString();
                    result = upload.downloadDataUniversal(jsonString2, CommonString.COVERAGE_DETAIL);

                    if (result.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                        throw new SocketTimeoutException();
                    } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                        throw new IOException();
                    } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                        throw new JsonSyntaxException("non_working");
                    } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        throw new Exception();
                    } else {
                        int mid = 0;
                        try {
                            mid = Integer.parseInt(result);
                            if (mid > 0) {
                                ResultFlag = true;
                            }

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            throw new NumberFormatException();
                        }

                    }

                }


            } catch (MalformedURLException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_EXCEPTION;
            } catch (SocketTimeoutException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_SOCKETEXCEPTION;
            } catch (InterruptedIOException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_EXCEPTION;
            } catch (NumberFormatException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_NUMBER_FORMATE_EXEP;
            } catch (IOException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_SOCKETEXCEPTION;
            } catch (XmlPullParserException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_XmlPull;
            } catch (Exception e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_EXCEPTION;
            }
            if (ResultFlag) {
                return CommonString.KEY_SUCCESS;
            } else {
                return strflag;
            }

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                coverageBeanList.clear();
                dialog.dismiss();
                finish();
            } else {
                coverageBeanList.clear();
                BiraDB db = new BiraDB(NonWorkingActivity.this);
                db.open();
                dialog.dismiss();
                db.deleteTableWithStoreID(store_id);
                db.updateStoreStatus(store_id, visit_date, CommonString.KEY_N);
                showAlert(getString(R.string.non_working_error) + " " + result);
            }
        }
    }

    public void showAlert(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(NonWorkingActivity.this);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void declaration() {
        context = this;
        reasonspinner = (Spinner) findViewById(R.id.nonWorkingReason_spinner);
        camera = (ImageButton) findViewById(R.id.imgcam);
        save = (FloatingActionButton) findViewById(R.id.save);
        rel_cam = (RelativeLayout) findViewById(R.id.relimgcam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        store_id = String.valueOf(getIntent().getIntExtra(CommonString.Store_Id, 0));


        txt_label = (TextView) findViewById(R.id.txt_label);
        getSupportActionBar().setTitle("");
        txt_label.setText("Non Working Reason - " + visit_date);
        database = new BiraDB(context);
        database.open();
        str = CommonString.FILE_PATH;
        intime = getCurrentTime();
        camera.setOnClickListener(this);
        save.setOnClickListener(this);

    }
}
