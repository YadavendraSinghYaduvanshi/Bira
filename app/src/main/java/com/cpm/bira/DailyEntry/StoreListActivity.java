package com.cpm.bira.DailyEntry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.Download.DownloadActivity;
import com.cpm.bira.GetterSetter.CommonChillerDataGetterSetter;
import com.cpm.bira.GetterSetter.CommonDataGetterSetter;
import com.cpm.bira.GetterSetter.CoverageBean;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.R;
import com.cpm.bira.upload.DownloadDataWithRetrofit;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonSyntaxException;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StoreListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    BiraDB database;
    Context context;
    SharedPreferences preferences;
    String visit_date;
    Toolbar toolbar;
    private ArrayList<JourneyPlan> storelist = new ArrayList<>();
    private ValueAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout linearlay;
    private FloatingActionButton fab;
    private Dialog dialog;
    private boolean ResultFlag = true;
    private  String user_id;
    GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters
    LocationManager locationManager;
    boolean enabled;
    private FusedLocationProviderClient mFusedLocationClient;
    double lat, lon;
    int store_id;

    private CommonDataGetterSetter consumerPromoCouponData = new CommonDataGetterSetter();
    private ArrayList<CommonDataGetterSetter> competitionData = new ArrayList<>();
    private ArrayList<CommonChillerDataGetterSetter> previousDayStockData = new ArrayList<>();
    private ArrayList<CommonChillerDataGetterSetter> biraChillerData = new ArrayList<>();
    private ArrayList<CommonChillerDataGetterSetter> retailerOwnedChillerData = new ArrayList<>();
    private ArrayList<CommonChillerDataGetterSetter> posmDeploymentData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        declaration();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle(getResources().getString(R.string.gps));
            alertDialog.setMessage(getResources().getString(R.string.gpsebale));
            alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });
            alertDialog.setNegativeButton(getResources().getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();

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
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }

        return super.onOptionsItemSelected(item);
    }

    void declaration() {

        context = this;
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferences  = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date   = preferences.getString(CommonString.KEY_DATE, "");
        user_id      = preferences.getString(CommonString.KEY_USERNAME, "");

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Stores List - " + visit_date);

        linearlay        =  findViewById(R.id.no_data_lay);
        recyclerView     =  findViewById(R.id.drawer_layout_recycle);
        fab              =  findViewById(R.id.fab);
        database = new BiraDB(context);
        database.open();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(), DownloadActivity.class);
                startActivity(in);
                finish();
            }
        });
    }


    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {

        private LayoutInflater inflator;

        List<JourneyPlan> data = Collections.emptyList();

        public ValueAdapter(Context context, ArrayList<JourneyPlan> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.storeviewlist, parent, false);
            return new MyViewHolder(view);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {

            final JourneyPlan current = data.get(position);

            viewHolder.txt.setText(current.getStoreName() + "(" + current.getStoreId() +")");
            viewHolder.address.setText("Address -" +current.getAddress());

            viewHolder.chkbtn.setBackgroundResource(R.mipmap.checkout);
            viewHolder.cancelbtn.setBackgroundResource(R.mipmap.cancel_icon);

            if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_u);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.cancelbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_d);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.cancelbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.drawable.tick_p);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.cancelbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
            } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {
                viewHolder.imageview.setVisibility(View.VISIBLE);
                viewHolder.imageview.setBackgroundResource(R.mipmap.tick);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.cancelbtn.setVisibility(View.INVISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
            } else if (isValid(current)) {
                viewHolder.cancelbtn.setVisibility(View.GONE);
                viewHolder.chkbtn.setVisibility(View.VISIBLE);
                viewHolder.imageview.setVisibility(View.INVISIBLE);
            }
            else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                viewHolder.imageview.setVisibility(View.INVISIBLE);
                viewHolder.chkbtn.setVisibility(View.GONE);
                viewHolder.cancelbtn.setVisibility(View.VISIBLE);
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.green));
            } else {
                viewHolder.Cardbtn.setCardBackgroundColor(getResources().getColor(R.color.colorOrange));
                viewHolder.imageview.setVisibility(View.INVISIBLE);
                viewHolder.chkbtn.setVisibility(View.INVISIBLE);
                viewHolder.cancelbtn.setVisibility(View.INVISIBLE);
            }

            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    store_id = current.getStoreId();

                   if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_U)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_already_done, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_D)) {

                        Snackbar.make(v, R.string.title_store_list_activity_store_data_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_C)) {

                        Snackbar.make(v, R.string.title_store_list_activity_store_already_checkout, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    } else if (current.getUploadStatus().equalsIgnoreCase(CommonString.KEY_P)) {
                        Snackbar.make(v, R.string.title_store_list_activity_store_again_uploaded, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } else {
                        boolean entry_flag = true;
                        for (int j = 0; j < storelist.size(); j++) {
                            if (storelist.get(j).getUploadStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                                if (store_id != storelist.get(j).getStoreId()) {
                                    entry_flag = false;
                                    break;
                                } else {
                                    break;
                                }
                            }
                        }

                        if (entry_flag) {
                            showMyDialog(current, false);
                        } else {
                            Snackbar.make(v, R.string.title_store_list_checkout_current, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        }

                    }
                }
            });


            viewHolder.cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ArrayList<CoverageBean> coverage = database.getCoverageWithStoreID_Data(current.getStoreId() + "",visit_date);
                    if (coverage.size() > 0) {
                        deleteCoverageData(current,coverage);
                        File file = new File(CommonString.FILE_PATH + coverage.get(0).getImage());
                        if (file.exists()) {
                            file.delete();
                        }
                    } else {
                        deleteCoverageData(current,coverage);
                    }
                }
            });

            viewHolder.chkbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
                    builder.setMessage(R.string.wantcheckout)
                            .setCancelable(false)
                            .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    if (CheckNetAvailability()) {
                                        new checkoutData(current).execute();
                                    } else {
                                        Snackbar.make(recyclerView, R.string.nonetwork, Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.closed, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

        }


        @SuppressWarnings("deprecation")
        public boolean CheckNetAvailability() {

            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTED
                    || connectivityManager.getNetworkInfo(
                    ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                // we are connected to a network
                connected = true;
            }
            return connected;
        }



        public class checkoutData extends AsyncTask<Void, Void, String> {

            private JourneyPlan cdata;

            checkoutData(JourneyPlan cdata) {
                this.cdata = cdata;
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
                String strflag = null;
                try {

                    // for failure
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("UserId", user_id);
                    jsonObject.put("StoreId", cdata.getStoreId());
                    jsonObject.put("Latitude", lat);
                    jsonObject.put("Longitude", lon);
                    jsonObject.put("Checkout_Date", cdata.getVisitDate());

                    String jsonString2 = jsonObject.toString();

                    DownloadDataWithRetrofit upload = new DownloadDataWithRetrofit(context);
                    String result_str = upload.downloadDataUniversal(jsonString2, CommonString.CHECKOUTDetail);

                    if (result_str.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                        throw new IOException();
                    } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                        throw new SocketTimeoutException();
                    } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                        throw new JsonSyntaxException("Check out Upload");
                    } else if (result_str.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        throw new Exception();
                    } else {
                        ResultFlag = true;
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

                } catch (IOException e) {

                    ResultFlag = false;
                    strflag = CommonString.MESSAGE_SOCKETEXCEPTION;

                } catch (JsonSyntaxException e) {
                    ResultFlag = false;
                    strflag = CommonString.MESSAGE_INVALID_JSON;

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
                    long id = 0,id1=0;
                    database.open();
                    id1 = database.updateCheckoutStatus(String.valueOf(cdata.getStoreId()), CommonString.KEY_C);
                    if (id1 >0) {
                        recyclerView.invalidate();
                        adapter.notifyDataSetChanged();
                        setListData();
                        Intent i = new Intent(getBaseContext(), com.cpm.bira.upload.UploadWithoutWaitActivity.class);
                        i.putExtra("upload_flag","1");
                        startActivity(i);
                    }else{
                        showAlert(getString(R.string.checkoutError) + " " + result);
                    }

                } else {
                    showAlert(getString(R.string.checkoutError) + " " + result);
                }
            }

        }


        private void showAlert(String str) {

            AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
            builder.setTitle("Parinaam");
            builder.setMessage(str).setCancelable(false)
                    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txt, address,mer_reason,mer_sub_reason,mer_audit_date;
            LinearLayout linearLayout;
            ImageView imageview;
            Button chkbtn,cancelbtn;
            CardView Cardbtn;

            public MyViewHolder(View itemView) {
                super(itemView);
                mer_reason      = itemView.findViewById(R.id.mer_reason);
                mer_sub_reason  = itemView.findViewById(R.id.mer_sub_reason);
                txt             = itemView.findViewById(R.id.storelistviewxml_storename);
                address         = itemView.findViewById(R.id.storelistviewxml_storeaddress);
                linearLayout    = itemView.findViewById(R.id.storenamelistview_layout);
                imageview       = itemView.findViewById(R.id.storelistviewxml_storeico);
                chkbtn          = itemView.findViewById(R.id.chkout);
                cancelbtn       = itemView.findViewById(R.id.cancel);
                Cardbtn         = itemView.findViewById(R.id.card_view);
                mer_audit_date  = itemView.findViewById(R.id.mer_audit_date);
            }
        }
    }

    private boolean isValid(JourneyPlan current) {
        boolean result = false;

        competitionData             =  database.getCompetitionData(Integer.valueOf(current.getStoreId()),visit_date);
        previousDayStockData        =  database.getPreviousDayStockData(Integer.valueOf(current.getStoreId()),visit_date);
        biraChillerData             =  database.getBiraChillerData(Integer.valueOf(current.getStoreId()),visit_date);
        retailerOwnedChillerData    =  database.getRetailerOwnedChillerData(Integer.valueOf(current.getStoreId()),visit_date);
        consumerPromoCouponData     =  database.getConsumerPromoCouponData(Integer.valueOf(current.getStoreId()),visit_date);
        posmDeploymentData          = database.getPOSMDeploymentSavedData(Integer.valueOf(current.getStoreId()),visit_date);

        if(competitionData.size() >0){
            result = true;
        }

        if(result){
            if(previousDayStockData.size()>0){
                result = true;
            }else{
                result = false;
            }
        }

        if(result){
            if(biraChillerData.size()>0){
                result = true;
            }else{
                result = false;
            }
        }
        if(result){
            if(retailerOwnedChillerData.size()>0){
                result = true;
            }else{
                result = false;
            }
        }
        if(result){
            if(!consumerPromoCouponData.getStore_id().equalsIgnoreCase("")){
                result = true;
            }else{
                result = false;
            }
        }

        if(result){
            if(posmDeploymentData.size() > 0){
                result = true;
            }else{
                result = false;
            }
        }
        return result;
    }


    // coverage data deleted if user cancel the store and image also deleted
    private void deleteCoverageData(final JourneyPlan current, final ArrayList<CoverageBean> coverage) {
        if (current.getUploadStatus().equals(CommonString.KEY_CHECK_IN) || isValid(current)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
            builder.setMessage(R.string.DELETE_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // here 0 is used for working data
                                    new DeleteCoverageData(current, user_id,0).execute();
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        setListData();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    private void setListData() {
        database.open();
        storelist = database.getStoreData(visit_date);

        if (storelist.size() > 0) {
            adapter = new ValueAdapter(context, storelist);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            linearlay.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }


    private void showMyDialog(final JourneyPlan current, final boolean isVisitLater) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);

        RadioGroup radioGroup =  dialog.findViewById(R.id.radiogrpvisit);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    dialog.cancel();
                    boolean flag = true;

                    if(!current.getUploadStatus().equals(CommonString.KEY_CHECK_IN)){
                        flag = true;
                    }else{
                        flag = false;
                    }
                    if (flag == true) {
                        Intent in = new Intent(StoreListActivity.this, StoreimageActivity.class);
                        in.putExtra(CommonString.TAG_OBJECT,current);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }else{
                        // for self service store data
                        Intent in = new Intent(StoreListActivity.this, EntryMenuActivity.class);
                        in.putExtra(CommonString.TAG_OBJECT, current);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        dialog.cancel();
                        finish();

                    }
                } else if (checkedId == R.id.no) {
                    dialog.cancel();

                    final ArrayList<CoverageBean> coverage = database.getCoverageWithStoreID_Data(current.getStoreId() + "",visit_date);
                    if (coverage.size() > 0) {
                        deleteCoverageNoWorkingData(current,coverage);
                        File file = new File(CommonString.FILE_PATH + coverage.get(0).getImage());
                        if (file.exists()) {
                            file.delete();
                        }

                    } else {
                        deleteCoverageNoWorkingData(current,coverage);
                    }
                }
            }
        });

        dialog.show();
    }

    // coverage data deleted if user cancel the store and image also deleted

    private void deleteCoverageNoWorkingData(final JourneyPlan current, final ArrayList<CoverageBean> coverage) {
        if(current.getUploadStatus().equals(CommonString.KEY_CHECK_IN) || isValid(current)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
            builder.setMessage(R.string.DELETE_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                    // here 1 is used for non working
                                  /*  gotoNonWorkingActivity(current);*/

                                   new DeleteCoverageData(current, user_id, 1).execute();
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {

                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            gotoNonWorkingActivity(current);
        }
    }

    private void gotoNonWorkingActivity(JourneyPlan current) {
        Intent in = new Intent(StoreListActivity.this, NonWorkingActivity.class);
        in.putExtra(CommonString.Store_Id, current.getStoreId());
        startActivity(in);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    private void UpdateStore(String storeid) {
        database.open();
        database.deleteTableWithStoreID(storeid);
        database.updateStoreStatus(storeid, storelist.get(0).getVisitDate(), "N");
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        // Logic to handle location object
                    }
                }
            });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("error", "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notsuppoted)
                        , Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    /*
     *   LocationRequest initialization procedure has changed into latest
     *   Google Play Service dependencies ( > 12.0.0).
     *   So we have  Use static methodLocationRequest in create ().
     *   We  have to intialize LocationRequest internally
     *   If Google Play Service < 12.0.0 We can intailize it outside
     *   Like mLocationRequest = new LocationRequest();
     * */


    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }
    }

    public class DeleteCoverageData extends AsyncTask<Void, Void, String> {

        String  userId;
        int val;
        JourneyPlan current;

        public DeleteCoverageData(JourneyPlan current, String userId, int val) {
            this.current = current;
            this.userId = userId;
            this.val = val;
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
            String strflag = null;
            try {
                // for failure
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("StoreId", current.getStoreId());
                jsonObject.put("VisitDate", current.getVisitDate());
                jsonObject.put("UserId", userId);

                String jsonString2 = jsonObject.toString();

                DownloadDataWithRetrofit download = new DownloadDataWithRetrofit(context);
                String result_str = download.downloadDataUniversal(jsonString2, CommonString.DELETE_COVERAGE);

                if (result_str.equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                    throw new IOException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                    throw new SocketTimeoutException();
                } else if (result_str.equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                    throw new JsonSyntaxException("Check out Upload");
                } else if (result_str.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    throw new Exception();
                } else {
                    ResultFlag = true;
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

            } catch (IOException e) {

                ResultFlag = false;
                strflag = CommonString.MESSAGE_SOCKETEXCEPTION;

            } catch (JsonSyntaxException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_INVALID_JSON;

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
                // here 0 is used for if user worked on store
                if(val == 0) {
                    UpdateStore(current.getStoreId() + "");
                    AlertandMessages.showToastMsg(context, "Store Coverage Deleted Successfully.");
                    finish();
                }else{
                    // 1 is used for store non working
                    AlertandMessages.showToastMsg(context, "Store Coverage Deleted Successfully.");
                    gotoNonWorkingActivity(current);
                    UpdateStore(current.getStoreId() + "");
                }
            } else {
                showAlert(getString(R.string.datanotfound) + " " + result);
            }
        }
    }

    private void showAlert(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(StoreListActivity.this);
        builder.setTitle("Parinaam");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }

}
