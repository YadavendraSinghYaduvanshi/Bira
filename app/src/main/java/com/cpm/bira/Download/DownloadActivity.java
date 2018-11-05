package com.cpm.bira.Download;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cpm.bira.R;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.upload.DownloadDataWithRetrofit;

import org.json.JSONObject;

import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity {

    //Toolbar toolbar;
    Context context;
    BiraDB db;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String userId, date;
    int downloadindex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        declaration();
    }

    private void declaration() {
        Toolbar toolbar = findViewById(R.id.download_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;
        db = new BiraDB(context);
        preferences     = PreferenceManager.getDefaultSharedPreferences(this);
        userId          = preferences.getString(CommonString.KEY_USERNAME, null);
        date            = preferences.getString(CommonString.KEY_DATE, "");
        downloadindex   = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        editor = preferences.edit();
        editor.putBoolean(CommonString.DOWNLOAD_STATUS, false);
        editor.apply();

        getSupportActionBar().setTitle("Download - " + date);
        DownloadDataTask();
    }

    public void DownloadDataTask() {

        try {
            ArrayList<String> keysList = new ArrayList<>();
            ArrayList<String> jsonList = new ArrayList<>();
            ArrayList<String> KeyNames = new ArrayList<>();
            KeyNames.clear();
            keysList.clear();

            keysList.add("Table_Structure");
            keysList.add("Non_Working_Reason");
            keysList.add("Journey_Plan");
            keysList.add("Mapping_Posm");
            keysList.add("Brand_Master");
            keysList.add("Sku_Master");
            keysList.add("Mapping_Stock");
            keysList.add("Posm_Master");
            keysList.add("Non_Posm_Reason");

            if (keysList.size() > 0) {
                for (int i = 0; i < keysList.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Downloadtype", keysList.get(i));
                    jsonObject.put("Username", userId);
                    jsonList.add(jsonObject.toString());
                    KeyNames.add(keysList.get(i));
                }

                if (jsonList.size() > 0) {
                    ProgressDialog pd = new ProgressDialog(context);
                    pd.setCancelable(false);
                    pd.setMessage("Downloading Data" + "(" + "/" + ")");
                    pd.show();
                    DownloadDataWithRetrofit downloadData = new DownloadDataWithRetrofit(context, db, pd, CommonString.TAG_FROM_CURRENT);
                    downloadData.listSize = jsonList.size();
                    downloadData.downloadDataUniversalWithoutWait(jsonList, KeyNames, downloadindex, CommonString.DOWNLOAD_ALL_SERVICE,1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
