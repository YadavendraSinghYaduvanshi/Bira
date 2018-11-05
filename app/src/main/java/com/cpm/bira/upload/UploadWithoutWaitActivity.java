package com.cpm.bira.upload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CoverageBean;
import com.cpm.bira.R;

import java.util.ArrayList;


public class UploadWithoutWaitActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private ProgressDialog pb;
    BiraDB db;
    String date, userId, app_version,Path, app_ver,uploadFlag;
    ArrayList<CoverageBean> coverageList;
    Toolbar toolbar;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            uploadFlag = bundle.getString("upload_flag");
        }

        date = preferences.getString(CommonString.KEY_DATE, null);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        toolbar.setTitle("Upload - " + date);
        Path = CommonString.FILE_PATH;
        db = new BiraDB(this);
        db.open();
        coverageList = db.getCompleteCoverageData(date);

        try{
            pb = new ProgressDialog(context);
            pb.setCancelable(false);
            pb.setMessage("Uploading Data");
            pb.show();
            UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context, db, pb, CommonString.TAG_FROM_CURRENT);
            upload.uploadDataUsingCoverageRecursive(coverageList, 0,uploadFlag, date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            if (pb != null) {
                pb.dismiss();
                pb = null;
            }
        }
    }
}
