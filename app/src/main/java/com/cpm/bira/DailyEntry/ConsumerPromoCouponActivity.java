package com.cpm.bira.DailyEntry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CommonDataGetterSetter;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.R;
import com.cpm.bira.adapter.SpinnerAdapterView;

public class ConsumerPromoCouponActivity extends AppCompatActivity {

    Toolbar toolBar;
    EditText qtyTxt;
    Spinner spnTxt;
    FloatingActionButton saveBtn;
    String[] spinner_list = {"Select", "YES", "NO"};
    private CommonDataGetterSetter consumerPromoCouponData;
    LinearLayout layout;
    boolean checkflag = true;
    String Error_Message= "",visit_date;
    private BiraDB db;
    JourneyPlan jcpGetset;
    Context context;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_promo_code);
        db = new BiraDB(this);
        declaration();

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            jcpGetset = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        db.open();
        consumerPromoCouponData =  db.getConsumerPromoCouponData(jcpGetset.getStoreId(), visit_date);
        if(!consumerPromoCouponData.getPresent().equalsIgnoreCase("")){
            if(consumerPromoCouponData.getPresent().equalsIgnoreCase("1")){
                layout.setVisibility(View.VISIBLE);
                spnTxt.setSelection(1);
                qtyTxt.setText(consumerPromoCouponData.getQty());
            }else{
                layout.setVisibility(View.GONE);
                spnTxt.setSelection(2);
                qtyTxt.setText("");
            }
        }
    }

    private void declaration() {
        context = this;
        toolBar = findViewById(R.id.consumer_promocode_toolbar);
        spnTxt  = findViewById(R.id.consumer_promocode_spinner);
        qtyTxt  = findViewById(R.id.consumer_promocod_quy);
        saveBtn = findViewById(R.id.consumer_promocode_fab);
        layout  = findViewById(R.id.qty_layout);

        SpinnerAdapterView adapter = new SpinnerAdapterView(getApplicationContext(),spinner_list);
        spnTxt.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date  = preferences.getString(CommonString.KEY_DATE, null);

        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Consumer PromoCode - " + visit_date);

        spnTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                if (itemPos == 0) {
                    consumerPromoCouponData.setPresent("");
                } else if(itemPos == 1){
                    layout.setVisibility(View.VISIBLE);
                    consumerPromoCouponData.setPresent("1");
                }else{
                    layout.setVisibility(View.GONE);
                    qtyTxt.setText("");
                    consumerPromoCouponData.setPresent("0");
                }
                // set check flag true after select the value=
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        qtyTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hashFocus) {
                if (!hashFocus) {
                    final EditText Caption = (EditText) view;
                    String value1 = Caption.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                    if (value1.length() < Caption.getText().toString().length()) {
                        ((EditText) view).setText(value1);
                    }
                    if (!value1.equals("")) {
                        consumerPromoCouponData.setQty(value1);
                    } else {
                        consumerPromoCouponData.setQty("");
                    }
                }
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qtyTxt.clearFocus();
                if(checkValidation(consumerPromoCouponData)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to save Data?").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    saveConsumerPromoCouponData(consumerPromoCouponData);
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

                }else {
                    Snackbar.make(view, Error_Message, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveConsumerPromoCouponData(CommonDataGetterSetter consumerPromoCouponData) {
        db.open();
        long val =  db.insertConsumerPromoCouponData(consumerPromoCouponData,jcpGetset.getStoreId(),visit_date);
        if(val>0){
            AlertandMessages.showToastMsg(this,"Data has been saved.");
            finish();
            // AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_success_msg));
        }else{
            AlertandMessages.showToastMsg(this,"Data not saved try again.");
            //  AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_not_saved_msg));
        }
    }

    private boolean checkValidation(CommonDataGetterSetter consumerPromoCouponData) {
        checkflag = true;
        if (consumerPromoCouponData.getPresent().equals("")) {
            checkflag = false;
            Error_Message = getString(R.string.present_error);
            return checkflag;
        } else {
            if (consumerPromoCouponData.getPresent().equals("1")) {
                if(consumerPromoCouponData.getQty().equalsIgnoreCase("")){
                    checkflag = false;
                    Error_Message = getString(R.string.qty_error);
                    return checkflag;
                }else if(consumerPromoCouponData.getQty().equalsIgnoreCase("0")) {
                    checkflag = false;
                    qtyTxt.setText("");
                    Error_Message = getString(R.string.qty_zero_error);
                    return checkflag;
                }else{
                    checkflag = true;
                }
            }else{
                checkflag = true;
            }
        }
        return checkflag;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            new AlertandMessages(ConsumerPromoCouponActivity.this, null, null, null).backpressedAlert(ConsumerPromoCouponActivity.this);
            //finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new AlertandMessages(ConsumerPromoCouponActivity.this, null, null, null).backpressedAlert(ConsumerPromoCouponActivity.this);
    }

}
