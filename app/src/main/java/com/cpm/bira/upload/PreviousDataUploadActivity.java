package com.cpm.bira.upload;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CoverageBean;
import com.cpm.bira.R;

import java.util.ArrayList;

public class PreviousDataUploadActivity extends AppCompatActivity {

    BiraDB db;
    String date, userId, app_version,uploadFlag;
    String  Path;
    boolean flag = false;
    private ProgressDialog pd;
    private SharedPreferences preferences;
    Context context;
    ArrayList<CoverageBean> coverageList;
    String status = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        userId = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null){
            uploadFlag = bundle.getString("upload_flag");
        }

        db = new BiraDB(this);
        db.open();
        context = this;
        Path = CommonString.FILE_PATH;
        isDataValid();
    }


    void isDataValid() {
        db.open();
       // String date = "11/10/2018";
        ArrayList<CoverageBean> coverage_list = db.getCoverageDataPrevious(date);
        for (int i = 0; i < coverage_list.size(); i++) {

            status = db.getStoreData(coverage_list.get(i).getVisitDate(),coverage_list.get(i).getStoreId()).getUploadStatus();

            if(status != null) {
                if (status.equalsIgnoreCase(CommonString.KEY_C)) {
                    flag = true;
                } else {
                    flag = false;
                    db.open();
                    db.deleteTableWithStoreID(coverage_list.get(i).getStoreId());
                }
            }
        }
        if(flag){
            db.open();
            coverageList = db.getCoverageDataPrevious(date);
            if (coverageList.size() > 0) {
                pd = new ProgressDialog(context);
                pd.setCancelable(false);
                pd.setMessage("Uploading Data");
                pd.show();
                UploadImageWithRetrofit upload = new UploadImageWithRetrofit(context, db, pd, CommonString.TAG_FROM_PREVIOUS);
                upload.uploadDataUsingCoverageRecursive(coverageList, 0, uploadFlag,date);
            }
        }
    }
}
