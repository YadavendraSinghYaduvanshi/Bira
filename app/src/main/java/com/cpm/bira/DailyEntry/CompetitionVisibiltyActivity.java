package com.cpm.bira.DailyEntry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonFunctions;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CommonDataGetterSetter;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.R;
import com.cpm.bira.adapter.SpinnerAdapterView;

import java.io.File;
import java.util.ArrayList;

public class CompetitionVisibiltyActivity extends AppCompatActivity {


    Toolbar toolBar;
    Spinner spnTxt;
    FloatingActionButton saveBtn;
    ImageView img1,img2,img3;
    Context context ;
    String[] spinner_list = {"Select", "YES", "NO"};
    LinearLayout layout;
    boolean checkflag = true;

    String Error_Message= "",visit_date,_pathforcheck1 = "",str, image1 = "",image3="",image2="", visit_date_formatted, _path = "",store_id,_UserId,_pathforcheck2="",_pathforcheck3="";
    private BiraDB db;
    JourneyPlan jcpGetset;
    private SharedPreferences preferences;
    CommonDataGetterSetter competitionData = new CommonDataGetterSetter();
    private ArrayList<CommonDataGetterSetter> competitionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_visibilty);

        db = new BiraDB(this);
        declaration();

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            jcpGetset = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = String.valueOf(jcpGetset.getStoreId());
        }

        db.open();
        competitionList =  db.getCompetitionData(jcpGetset.getStoreId(), visit_date);
        if(competitionList.size() > 0){
            if(competitionList.get(0).getPresent().equalsIgnoreCase("1")){
                layout.setVisibility(View.VISIBLE);
                spnTxt.setSelection(1);
                if (competitionList.get(0).getImg1() != null && !competitionList.get(0).getImg1().equals("")) {
                    if (new File(str + competitionList.get(0).getImg1()).exists()) {
                        img1.setImageResource(R.drawable.camera_green);
                        image1 = competitionList.get(0).getImg1();
                    }
                }

                if (competitionList.get(0).getImg2() != null && !competitionList.get(0).getImg2().equals("")) {
                    if (new File(str + competitionList.get(0).getImg2()).exists()) {
                        img2.setImageResource(R.drawable.camera_green);
                        image2 = competitionList.get(0).getImg2();
                    }
                }

                if (competitionList.get(0).getImg3() != null && !competitionList.get(0).getImg3().equals("")) {
                    if (new File(str + competitionList.get(0).getImg3()).exists()) {
                        img3.setImageResource(R.drawable.camera_green);
                        image3 = competitionList.get(0).getImg3();
                    }
                }
            }else{
                layout.setVisibility(View.GONE);
                spnTxt.setSelection(2);
            }
        }
    }

    private void declaration() {
        context = this;
        toolBar = findViewById(R.id.competition_toolbar);
        spnTxt = findViewById(R.id.competition_spinner);
        saveBtn = findViewById(R.id.competition_fab);
        layout = findViewById(R.id.image_layout);
        img1 = findViewById(R.id.cameraImg1);
        img2 = findViewById(R.id.cameraImg2);
        img3 = findViewById(R.id.cameraImg3);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _pathforcheck1 = store_id + "_" + _UserId.replace(".", "") + "_Competition_Visibility-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck1;
                //CommonFunctions.startCameraActivity(activity, _path);
                CommonFunctions.startAnncaCameraActivity(context, _path, null,false);
            }
        });


        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _pathforcheck2 = store_id + "_" + _UserId.replace(".", "") + "_Competition_Visibility-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck2;
                //CommonFunctions.startCameraActivity(activity, _path);
                CommonFunctions.startAnncaCameraActivity(context, _path, null,false);
            }
        });


        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _pathforcheck3 = store_id + "_" + _UserId.replace(".", "") + "_Competition_Visibility-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck3;
                //CommonFunctions.startCameraActivity(activity, _path);
                CommonFunctions.startAnncaCameraActivity(context, _path, null,false);

            }
        });

        SpinnerAdapterView adapter = new SpinnerAdapterView(getApplicationContext(), spinner_list);
        spnTxt.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");
        str = CommonString.FILE_PATH;
        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Competition Visibilty - " + visit_date);

        spnTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                if (itemPos == 0) {
                    layout.setVisibility(View.GONE);
                    competitionData.setPresent("");
                } else if (itemPos == 1) {
                    layout.setVisibility(View.VISIBLE);
                    competitionData.setPresent("1");
                } else {
                    layout.setVisibility(View.GONE);
                    competitionData.setPresent("0");
                    if(!image1.equalsIgnoreCase("")) {
                        File file = new File(CommonString.FILE_PATH + image1);
                        if (file.exists()) {
                            file.delete();
                            img1.setImageResource(R.mipmap.camera_red);
                            image1 = "";
                        }
                    }
                    if(!image2.equalsIgnoreCase("")) {
                        File file = new File(CommonString.FILE_PATH + image2);
                        if (file.exists()) {
                            file.delete();
                            img2.setImageResource(R.mipmap.camera_red);
                            image2 = "";
                        }
                    }
                    if(!image3.equalsIgnoreCase("")) {
                        File file = new File(CommonString.FILE_PATH + image3);
                        if (file.exists()) {
                            file.delete();
                            img3.setImageResource(R.mipmap.camera_red);
                            image3 = "";
                        }
                    }
                }
                // set check flag true after select the value=
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkFields(competitionData,image1,image2, image3)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to save Data?").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    CommonDataGetterSetter data = new CommonDataGetterSetter();

                                    data.setStore_id(store_id);
                                    data.setPresent(competitionData.getPresent());
                                    data.setImg1(image1);
                                    data.setImg2(image2);
                                    data.setImg3(image3);
                                    data.setVisit_date(visit_date);
                                    saveCompetitionData(data);
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    AlertandMessages.showToastMsg(context, Error_Message);
                }
            }
        });
    }

    private boolean checkFields(CommonDataGetterSetter competitionData, String image1, String image2, String image3) {
        checkflag = true;
        if(competitionData.getPresent().equalsIgnoreCase("")){
            checkflag = false;
            Error_Message = getString(R.string.present_error);
            return checkflag;
        }else{
            if(competitionData.getPresent().equalsIgnoreCase("1")) {
                if (!image1.equalsIgnoreCase("") || !image2.equalsIgnoreCase("") || !image3.equalsIgnoreCase("")) {
                    checkflag = true;
                    return checkflag;
                } else {
                    checkflag = false;
                    Error_Message = getString(R.string.one_single_image_error);
                    return checkflag;
                }
            }else{
                checkflag = true;
                return checkflag;
            }
        }
    }

    private void saveCompetitionData(CommonDataGetterSetter competitionData) {
        db.open();
        long val =  db.insertCompetitionData(competitionData,jcpGetset.getStoreId());
        if(val>0){
            AlertandMessages.showToastMsg(this,"Data has been saved.");
            finish();
            // AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_success_msg));
        }else{
            AlertandMessages.showToastMsg(this,"Data not saved try again.");
            //  AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_not_saved_msg));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:

                if (_pathforcheck1 != null && !_pathforcheck1.equals("")) {
                    if (new File(str + _pathforcheck1).exists()) {
                        img1.setImageResource(R.drawable.camera_green);
                        image1 = _pathforcheck1;
                    }
                }

                if (_pathforcheck2 != null && !_pathforcheck2.equals("")) {
                    if (new File(str + _pathforcheck2).exists()) {
                        img2.setImageResource(R.drawable.camera_green);
                        image2 = _pathforcheck2;
                    }
                }

                if (_pathforcheck3 != null && !_pathforcheck3.equals("")) {
                    if (new File(str + _pathforcheck3).exists()) {
                        img3.setImageResource(R.drawable.camera_green);
                        image3 = _pathforcheck3;
                    }
                }
                break;
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            new AlertandMessages(   CompetitionVisibiltyActivity.this, null, null, null).backpressedAlert(CompetitionVisibiltyActivity.this);
           /* finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);*/
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new AlertandMessages(   CompetitionVisibiltyActivity.this, null, null, null).backpressedAlert(CompetitionVisibiltyActivity.this);
    }
}
