package com.cpm.bira.DailyEntry;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.bira.Constant.CommonFunctions;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by neeraj goyal on 10-04-2018
 */
public class StoreimageActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = StoreimageActivity.class.getSimpleName();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters
    private GoogleApiClient mGoogleApiClient;
    private BiraDB database;
    private SharedPreferences preferences;
    private FusedLocationProviderClient mFusedLocationClient;
    private Dialog dialog;
    private TextView percentage, message;
    private ProgressBar pb;
    private String _pathforcheck, _path, str,img_str, strflag,store_id = "0", visit_date, visit_date_formatted, username, intime, app_ver;
    ImageView img_cam, img_clicked;
    Button btn_save;
    AlertDialog alert;
    double lat, lon;
    Toolbar toolbar;
    boolean ResultFlag = true,enabled;
    JourneyPlan jcpGetset;
    LocationManager locationManager;
    Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storeimage);
        declaration();

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            jcpGetset = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = jcpGetset.getStoreId().toString();
        }

        try {
            app_ver = String.valueOf(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    StoreimageActivity.this);

            // Setting Dialog Title
            alertDialog.setTitle(getResources().getString(R.string.gps));

            // Setting Dialog Message
            alertDialog.setMessage(getResources().getString(R.string.gpsebale));

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    });

            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(getResources().getString(R.string.no),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event

                            dialog.cancel();
                        }
                    });

            // Showing Alert Message
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

    @Override
    public void onConnected(Bundle bundle) {

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
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        //client.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        // AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }



    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {

            case R.id.img_cam_selfie:
                _pathforcheck = store_id + "_" + username.replace(".", "") + "_StoreImg-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                intime = getCurrentTime();
                //CommonFunctions.startCameraActivity((Activity) context, _path);
                CommonFunctions.startAnncaCameraActivity(context, _path,null,false);
                break;

            case R.id.btn_save_selfie:

                if (img_str != null)
                // if (true)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(StoreimageActivity.this);
                    builder.setMessage(getResources().getString(R.string.title_activity_save_data))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

                                    CoverageBean cdata = new CoverageBean();
                                    cdata.setStoreId(store_id);
                                    cdata.setVisitDate(visit_date);
                                    cdata.setUserId(username);
                                    cdata.setInTime(intime);
                                    cdata.setReason("");
                                    cdata.setReasonid("");
                                    cdata.setLatitude(lat + "");
                                    cdata.setLongitude(lon + "");
                                    cdata.setImage(img_str);
                                    cdata.setRemark("");
                                    cdata.setCheckOut_Image("");
                                    cdata.setUploadStatus(CommonString.KEY_CHECK_IN);

                                    new GeoTagUpload(cdata).execute();
                                }
                            })
                            .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    alert = builder.create();
                    alert.show();

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.clickimage), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {

            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(CommonString.FILE_PATH  + _pathforcheck).exists()) {
                        try {
                            //Bitmap bmp = BitmapFactory.decodeFile(str + _pathforcheck);
                            Bitmap bmp = convertBitmap(CommonString.FILE_PATH  + _pathforcheck);
                            img_cam.setImageBitmap(bmp);
                        } catch (OutOfMemoryError ex) {
                            CommonFunctions.setScaledImage(img_cam, CommonString.FILE_PATH  + _pathforcheck);
                        }
                        img_clicked.setVisibility(View.GONE);
                        img_cam.setVisibility(View.VISIBLE);

                        img_str = _pathforcheck;
                        _pathforcheck = "";

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:mmm");
        String cdate = formatter.format(m_cal.getTime());

        return cdate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }

    }

    public static Bitmap convertBitmap(String path) {
        Bitmap bitmap = null;
        BitmapFactory.Options ourOptions = new BitmapFactory.Options();
        ourOptions.inDither = false;
        ourOptions.inPurgeable = true;
        ourOptions.inInputShareable = true;
        ourOptions.inTempStorage = new byte[32 * 1024];
        File file = new File(path);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, ourOptions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    public class GeoTagUpload extends AsyncTask<Void, Void, String> {

        private CoverageBean cdata;

        GeoTagUpload(CoverageBean cdata) {
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
            pb          =  dialog.findViewById(R.id.progressBar1);
            percentage  =  dialog.findViewById(R.id.percentage);
            message     =  dialog.findViewById(R.id.message);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                BiraDB db = new BiraDB(StoreimageActivity.this);
                db.open();

                DownloadDataWithRetrofit upload = new DownloadDataWithRetrofit(context);
                JSONObject jsonObject;
                String jsonString2 = "", result = "5";

                //region Coverage Data
                jsonObject = new JSONObject();
                jsonObject.put("StoreId", cdata.getStoreId());
                jsonObject.put("VisitDate", cdata.getVisitDate());
                jsonObject.put("Latitude", cdata.getLatitude());
                jsonObject.put("Longitude", cdata.getLongitude());
                jsonObject.put("ReasonId", jcpGetset.getReasonId());
                jsonObject.put("SubReasonId", "");
                jsonObject.put("Remark", cdata.getRemark());
                jsonObject.put("ImageName", cdata.getImage());
                jsonObject.put("Checkout_Image", cdata.getCheckOut_Image());
                jsonObject.put("AppVersion", app_ver);
                jsonObject.put("UploadStatus", cdata.getUploadStatus());
                jsonObject.put("UserId", username);
                jsonObject.put("Distributor_Id", jcpGetset.getDistributorId());
                jsonObject.put("State_Id", jcpGetset.getDistributorId());
                jsonObject.put("Store_Type_Id", jcpGetset.getStoreTypeId());
                jsonObject.put("Store_Category_Id", jcpGetset.getStoreCategoryId());
                jsonObject.put("Classification_Id", jcpGetset.getClassificationId());

                jsonString2 = jsonObject.toString();
                result = upload.downloadDataUniversal(jsonString2, CommonString.COVERAGE_DETAIL);

                if (result.equalsIgnoreCase(CommonString.MESSAGE_NO_RESPONSE_SERVER)) {
                    throw new SocketTimeoutException();
                } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_SOCKETEXCEPTION)) {
                    throw new IOException();
                } else if (result.toString().equalsIgnoreCase(CommonString.MESSAGE_INVALID_JSON)) {
                    throw new JsonSyntaxException(CommonString.MESSAGE_INVALID_JSON);
                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    throw new Exception();
                } else {
                    int mid = 0;
                    try {
                        mid = Integer.parseInt(result);
                        if (mid > 0) {
                            return CommonString.KEY_SUCCESS;
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        throw new NumberFormatException();
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

            } catch (IOException e) {

                ResultFlag = false;
                strflag = CommonString.MESSAGE_SOCKETEXCEPTION;

            } catch (NumberFormatException e) {
                ResultFlag = false;
                strflag = CommonString.MESSAGE_NUMBER_FORMATE_EXEP;

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
                dialog.dismiss();
                database.open();
                if(database.InsertCoverageData(cdata) > 0) {
                    if (database.updateUploadStatus(store_id, CommonString.KEY_CHECK_IN ) > 0) {
                        Intent in = new Intent(StoreimageActivity.this, EntryMenuActivity.class);
                        in.putExtra(CommonString.TAG_OBJECT, jcpGetset);
                        startActivity(in);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        dialog.cancel();
                        finish();
                    }
                }
            }
            else {
                showAlert(result);
                 showAlert(getString(R.string.covrageError) + " " + result);
            }
        }
    }

    public void showAlert(String str) {

        AlertDialog.Builder builder = new AlertDialog.Builder(StoreimageActivity.this);
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
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        toolbar     =  findViewById(R.id.image_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        img_cam              =  findViewById(R.id.img_selfie);
        img_clicked          =  findViewById(R.id.img_cam_selfie);
        btn_save             =  findViewById(R.id.btn_save_selfie);
        visit_date           =  preferences.getString(CommonString.KEY_DATE, null);
        username             =  preferences.getString(CommonString.KEY_USERNAME, null);
        intime               =  preferences.getString(CommonString.KEY_STORE_IN_TIME, "");
        visit_date_formatted =  preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");

        getSupportActionBar().setTitle("Store Image - " + visit_date);

        database = new BiraDB(this);
        database.open();

        img_cam.setOnClickListener(this);
        img_clicked.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }
}
