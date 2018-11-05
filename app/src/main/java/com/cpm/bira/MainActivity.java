package com.cpm.bira;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.DailyEntry.StoreListActivity;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.Download.DownloadActivity;
import com.cpm.bira.GeoTag.GeoTagStoreList;
import com.cpm.bira.GetterSetter.CoverageBean;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.upload.PostApiForFile;
import com.cpm.bira.upload.PreviousDataUploadActivity;
import com.cpm.bira.upload.StringConverterFactory;
import com.cpm.bira.upload.UploadWithoutWaitActivity;
import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    Context context;
    SharedPreferences preferences;
    String date, user_name, url, path, backupDBPath, result = "",uploadStatus,error_msg="";
    ImageView imageView;
    WebView webView;
    BiraDB db;
    boolean isvalid = false;
    private int uploadFlag=0;
    private View headerView;
    TextView tv_username;
    private int downloadIndex;
    Intent downloadIntent, myAttendance, performance,myperformance,teamperformance;
    AlertDialog alert;
    boolean loginDateStatus = false;
    boolean downloadStatus;
    boolean flag = false;
    private ArrayList<JourneyPlan> storelist = new ArrayList<>();
    private ArrayList<CoverageBean> previousDaycoverageList;
    private ArrayList<CoverageBean> coverageList;
    private ArrayList<JourneyPlan> jcpStoreData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        declaration();

        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, navigationView, false);
        tv_username =  headerView.findViewById(R.id.nav_user_name);
        tv_username.setText(user_name);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.addHeaderView(headerView);
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/.Bira_All_Images");
        if (!dir.exists()) {
            dir.mkdirs();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!url.equals("")) {
            webView.loadUrl(url);
        }
        downloadIndex  = preferences.getInt(CommonString.KEY_DOWNLOAD_INDEX, 0);
        downloadStatus = preferences.getBoolean(CommonString.DOWNLOAD_STATUS, false);
        coverageList   = db.getCompleteCoverageData(date);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_daily_activity) {
            if(downloadStatus  && validateLoginDate(date) && downloadIndex == 0) {
                db.open();
                storelist = db.getStoreData(date);
                if(storelist.size() >0) {
                    Intent in = new Intent(getApplicationContext(), StoreListActivity.class);
                    startActivity(in);
                }else{
                    AlertandMessages.showAlert(context, getResources().getString(R.string.no_jcp_planned_error),false);
                }
            }else{
                AlertandMessages.showToastMsg(context, getResources().getString(R.string.download_data_error));
            }
        } else if (id == R.id.nav_download) {
            if (checkNetIsAvailable()) {
                boolean isDownloadValid = true;
                db.open();
               // date = "11/10/2018";
                if (db.isCoveragePreviousDataFilled(date)) {
                    if (isPrevousDayDataValid()) {
                        isDownloadValid = false;
                    }
                }

                if (isDownloadValid) {
                    db.open();
                    if (checkNetIsAvailable()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Parinaam");
                        builder.setMessage(getResources().getString(R.string.want_download_data)).setCancelable(false)
                                .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        startActivity(downloadIntent);
                                    }
                                })
                                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                        alert = builder.create();
                        alert.show();
                    }else{
                        Snackbar.make(webView, getResources().getString(R.string.nonetwork), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Parinaam");
                    builder.setMessage(getResources().getString(R.string.previous_data_upload)).setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent in = new Intent(getApplicationContext(), PreviousDataUploadActivity.class);
                                    in.putExtra("upload_flag","0");
                                    startActivity(in);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }else{
                Snackbar.make(webView, getResources().getString(R.string.nonetwork), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        } else if (id == R.id.nav_upload) {

            if (checkNetIsAvailable()) {
                if(downloadStatus && validateLoginDate(date) &&  downloadIndex == 0) {
                    if(db.isJournyCallCyclesDataFilled(date)) {
                        db.open();
                        jcpStoreData = db.getJCPData(date, "Journey_Plan");
                        if (jcpStoreData.size() > 0) {
                            for (int i = 0; i < jcpStoreData.size(); i++) {
                                if (jcpStoreData.get(i).getUploadStatus().equalsIgnoreCase(CommonString.KEY_D) || jcpStoreData.get(i).getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                                    flag = true;
                                    break;
                                } else {
                                    flag = false;
                                }
                            }
                        }

                        if(flag) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Parinaam");
                            builder.setMessage(getResources().getString(R.string.want_upload_data)).setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent i = new Intent(getBaseContext(), UploadWithoutWaitActivity.class);
                                            i.putExtra("upload_flag", "0");
                                            startActivity(i);
                                        }
                                    })
                                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                            alert = builder.create();
                            alert.show();
                        }else{
                            uploadFlag = isStoreCheckedIn();
                            if (uploadFlag != 0) {
                                if (uploadFlag == 1) {
                                    Snackbar.make(webView, error_msg, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                }
                            } else {
                                if (isValid()) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("Parinaam");
                                    builder.setMessage(getResources().getString(R.string.want_upload_data)).setCancelable(false)
                                            .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent i = new Intent(getBaseContext(), UploadWithoutWaitActivity.class);
                                                    i.putExtra("upload_flag", "0");
                                                    startActivity(i);
                                                }
                                            })
                                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });

                                    alert = builder.create();
                                    alert.show();
                                } else {
                                    AlertandMessages.showSnackbarMsg(context, "No data for Upload");
                                }
                            }
                        }
                    }else{
                        AlertandMessages.showToastMsg(context, getResources().getString(R.string.data_error));
                    }
                }else{
                    AlertandMessages.showToastMsg(context, getResources().getString(R.string.download_data_error));
                }
            }else{
                Snackbar.make(webView, getResources().getString(R.string.nonetwork), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }  else if (id == R.id.nav_geotag) {
            if (downloadStatus && validateLoginDate(date) &&  downloadIndex == 0) {
                Intent startDownload = new Intent(this, GeoTagStoreList.class);
                startActivity(startDownload);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }else {
                AlertandMessages.showToastMsg(context, getResources().getString(R.string.download_data_error));
            }

        }else if (id == R.id.nav_backup) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage(R.string.data_backup_msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @SuppressWarnings("resource")
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                File file = new File(Environment.getExternalStorageDirectory(), "Bira_Backup");
                                if (!file.isDirectory()) {
                                    file.mkdir();
                                }
                                File sd = Environment.getExternalStorageDirectory();
                                File data = Environment.getDataDirectory();
                                if (sd.canWrite()) {
                                    String currentDBPath = "//data//com.cpm.bira//databases//" + db.DATABASE_NAME;
                                    backupDBPath = user_name + "Bira_Backup" + date.replace("/", "") + ".db";
                                    path = Environment.getExternalStorageDirectory().getPath() + "/Bira_Backup";
                                    File currentDB = new File(data, currentDBPath);
                                    File backupDB = new File(path, backupDBPath);
                                    if (currentDB.exists()) {
                                        FileChannel src = new FileInputStream(currentDB).getChannel();
                                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                                        dst.transferFrom(src, 0, src.size());
                                        src.close();
                                        dst.close();
                                    }
                                }

                                File backupfile = new File(path + "/" + backupDBPath);
                                if (backupfile.exists()) {
                                    uploadBackup(MainActivity.this,backupfile.getName(), "DB_Backup");
                                }

                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                System.out.println(e.getMessage());
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder1.create();
            alert1.show();


        } else if (id == R.id.nav_exit) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage(R.string.app_exit_msg)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @SuppressWarnings("resource")
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                ActivityCompat.finishAffinity(MainActivity.this);
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

                            } catch (Exception e) {
                                Crashlytics.logException(e);
                                System.out.println(e.getMessage());
                            }
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert1 = builder1.create();
            alert1.show();

        }
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // check In validation for store in deviation and call cycle store
    private int isStoreCheckedIn() {
        int result_flag = 0;
        for (int i = 0; i < coverageList.size(); i++) {

            uploadStatus = db.getStoreData(date,coverageList.get(i).getStoreId()).getUploadStatus();
            if (uploadStatus != null && (uploadStatus.equals(CommonString.KEY_CHECK_IN))) {
                result_flag = 1;
                error_msg = getResources().getString(R.string.title_store_list_checkout);
                break;
            }
        }
        return result_flag;
    }


    private boolean isPrevousDayDataValid() {
        //String date = "11/10/2018";
        previousDaycoverageList = db.getPreviousDayCompleteCoverageData(date);
        boolean flag = false;
        String storestatus = null;
        for (int i = 0; i < previousDaycoverageList.size(); i++) {
            storestatus = db.getSpecificStoreUploadStatusPreviousDay(date, previousDaycoverageList.get(i).getStoreId(),"Journey_Plan").getUploadStatus();
            if(storestatus!=null) {
                if (!storestatus.equalsIgnoreCase(CommonString.KEY_U)) {
                    if(storestatus.equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                        db.deleteTableWithStoreID( previousDaycoverageList.get(i).getStoreId());
                    }else if ((storestatus.equalsIgnoreCase(CommonString.KEY_C) || storestatus.equalsIgnoreCase(CommonString.KEY_P) || storestatus.equalsIgnoreCase(CommonString.KEY_D))) {
                        flag = true;
                        //break;
                    }
                }
            }
        }

        return flag;
    }


    private boolean isValid() {
        boolean flag = false;
        String storestatus = null;
        for (int i = 0; i < coverageList.size(); i++) {
            storestatus = db.getStoreData(date, coverageList.get(i).getStoreId()).getUploadStatus();

            if (!storestatus.equalsIgnoreCase(CommonString.KEY_U)) {
                if ((storestatus.equalsIgnoreCase(CommonString.KEY_C) || storestatus.equalsIgnoreCase(CommonString.KEY_P) || storestatus.equalsIgnoreCase(CommonString.KEY_D))) {
                    flag = true;
                    break;
                }
            }
        }

        if (!flag)
            error_msg = getResources().getString(R.string.no_data_for_upload);
        return flag;
    }


    // check inserted login date match with current date or not
    private boolean validateLoginDate(String date) {
        db.open();
        //date = "11/10/2018";
        String loginDate = db.getLoginVisitedDate(date);
        if(!loginDate.equalsIgnoreCase("")){
            loginDateStatus = true;
        }
        return loginDateStatus;
    }


   void declaration() {
        setSupportActionBar(toolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        date = preferences.getString(CommonString.KEY_DATE, "");
        url = preferences.getString(CommonString.KEY_NOTICE_BOARD_LINK, "");
        user_name = preferences.getString(CommonString.KEY_USERNAME, null);
        imageView = findViewById(R.id.img_main);
        webView = findViewById(R.id.webview);
        toolbar.setTitle(getString(R.string.title_activity_main) + " - " + date);
        getSupportActionBar().setTitle(getString(R.string.title_activity_main) + " \n- " + date);
        db = new BiraDB(context);
        db.open();
        downloadIntent   = new Intent(context, DownloadActivity.class);
    }


    private void uploadBackup(final Context context, String file_name, String folder_name) {
        RequestBody body1;
        result = "";
        isvalid = false;
        final File originalFile = new File(CommonString.BACKUP_PATH + file_name);
        RequestBody photo = RequestBody.create(MediaType.parse("application/octet-stream"), originalFile);
        body1 = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", originalFile.getName(), photo)
                .addFormDataPart("FolderName", folder_name)
                .build();
        Retrofit adapter = new Retrofit.Builder()
                .baseUrl(CommonString.URL3 + "/")
                .addConverterFactory(new StringConverterFactory())
                .build();
        PostApiForFile api = adapter.create(PostApiForFile.class);
        Call<String> call = api.getUploadImage(body1);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response) {
                if (response.toString() != null) {
                    if (response.body().contains(CommonString.KEY_SUCCESS)) {
                        isvalid = true;
                        result = CommonString.KEY_SUCCESS;
                        Snackbar.make(webView, "Database Exported And Uploaded Successfully", Snackbar.LENGTH_SHORT).show();
                        originalFile.delete();
                    } else {
                        result = "Servererror!";
                    }
                } else {
                    result = "Servererror!";
                }
            }

            @Override
            public void onFailure(Throwable t) {
                isvalid = true;
                if (t instanceof UnknownHostException) {
                    result = CommonString.MESSAGE_SOCKETEXCEPTION;
                } else {
                    result = CommonString.MESSAGE_SOCKETEXCEPTION;
                }
                Toast.makeText(context, originalFile.getName() + " not uploaded", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        if (alert != null) {
            alert.dismiss();
            alert = null;
        }
    }

    private boolean checkNetIsAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (checkNetIsAvailable()) {
                imageView.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

    }
}
