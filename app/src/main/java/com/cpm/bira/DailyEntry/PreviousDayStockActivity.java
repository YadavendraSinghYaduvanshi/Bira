package com.cpm.bira.DailyEntry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CommonChillerDataGetterSetter;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.R;
import com.cpm.bira.adapter.SpinnerAdapterView;

import java.util.ArrayList;

public class PreviousDayStockActivity extends AppCompatActivity {


    Toolbar toolBar;
    Spinner spnTxt;
    FloatingActionButton saveBtn;
    Context context ;
    String[] spinner_list = {"Select", "YES", "NO"};
    LinearLayout layout;
    String visit_date,str, visit_date_formatted,store_id,_UserId,Error_Message= "";
    private BiraDB db;
    JourneyPlan jcpGetset;
    private SharedPreferences preferences;
    private CommonChillerDataGetterSetter stockData = new CommonChillerDataGetterSetter();
    private ArrayList<CommonChillerDataGetterSetter> skuList = new ArrayList<>();
    ArrayList<CommonChillerDataGetterSetter> skuListdata = new ArrayList<>();

    RecyclerView recyclerView;
    PreviousDaySockViewAdapter adapter;
    boolean checkflag = true;
    private ArrayList<CommonChillerDataGetterSetter> previousDayStockData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_day_stock);

        db = new BiraDB(this);
        declaration();

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            jcpGetset = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = String.valueOf(jcpGetset.getStoreId());
        }
        getSkuData();
    }


    private void getSkuData() {
        db.open();
        previousDayStockData    =  db.getPreviousDayStockData(Integer.valueOf(store_id),visit_date);
        if(previousDayStockData.size() >0){
            if(previousDayStockData.get(0).getExist().equalsIgnoreCase("1")){
                layout.setVisibility(View.VISIBLE);
                spnTxt.setSelection(1);
                createView(previousDayStockData);
            }else{
                layout.setVisibility(View.GONE);
                skuList = db.getBiraSkuData(jcpGetset);
                createView(skuList);
                spnTxt.setSelection(2);
            }
        }else{
            skuList = db.getBiraSkuData(jcpGetset);
            createView(skuList);
        }
    }

    private void createView(ArrayList<CommonChillerDataGetterSetter> skuList) {
        adapter = new PreviousDaySockViewAdapter(this,skuList);
        recyclerView.setHasFixedSize(true);
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            // hidding fab button when scrolling layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                 recyclerView.clearFocus();
                 adapter.notifyDataSetChanged();
                if (dy < 0) {
                    saveBtn.show();
                } else if (dy > 0) {
                    saveBtn.hide();
                }
            }
        });
    }


    private void declaration() {

        context = this;
        toolBar                 = findViewById(R.id.previous_day_stock_toolbar);
        spnTxt                  = findViewById(R.id.previous_day_stock_spn);
        saveBtn                 = findViewById(R.id.previous_day_stock_fab);
        recyclerView            = findViewById(R.id.sku_view);
        layout                  = findViewById(R.id.layout_view);

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
        getSupportActionBar().setTitle("Previous Day Stock - " + visit_date);

        spnTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                if (itemPos == 0) {
                    layout.setVisibility(View.GONE);
                    stockData.setExist("");
                } else if (itemPos == 1) {
                    layout.setVisibility(View.VISIBLE);

                    stockData.setExist("1");
                } else {

                    layout.setVisibility(View.GONE);
                    stockData.setExist("0");
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

                if(checkValidation(stockData,skuListdata)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to save Data?").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveConsumerPreviousDayStockData(stockData,skuListdata);
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



    private void saveConsumerPreviousDayStockData(CommonChillerDataGetterSetter stockData, ArrayList<CommonChillerDataGetterSetter> skuListdata) {
        db.open();
        if(stockData.getExist().equalsIgnoreCase("1")) {
            long val = db.insertPreviousDayData(stockData, jcpGetset, skuListdata);
            if (val > 0) {
                AlertandMessages.showToastMsg(this, "Data has been saved.");
                finish();
                // AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_success_msg));
            } else {
                AlertandMessages.showToastMsg(this, "Data not saved try again.");
                //  AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_not_saved_msg));
            }
        }else{

            long val = db.insertPreviousDayData(stockData, jcpGetset);
            if (val > 0) {
                AlertandMessages.showToastMsg(this, "Data has been saved.");
                finish();
                // AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_success_msg));
            } else {
                AlertandMessages.showToastMsg(this, "Data not saved try again.");
                //  AlertandMessages.showSnackbarMsg(rec_primary_self, getString(R.string.data_not_saved_msg));
            }
        }
    }


    private boolean checkValidation(CommonChillerDataGetterSetter stockData, ArrayList<CommonChillerDataGetterSetter> skuListdata) {
        checkflag = true;
        if (stockData.getExist().equals("")) {
            checkflag = false;
            Error_Message = getString(R.string.stock_present_error);
            return checkflag;
        } else {
            if (stockData.getExist().equals("1")) {

              for(int i = 0; i < this.skuListdata.size() ; i++){
                  if(this.skuListdata.get(i).getBiraQty().equalsIgnoreCase("")){
                      checkflag = false;
                      Error_Message = getString(R.string.qty_error);
                      break;
                  }else if(this.skuListdata.get(i).getBiraQty().equalsIgnoreCase("0")){
                      checkflag = false;
                      Error_Message = getString(R.string.qty_zero_error);
                      break;
                  }else{
                      checkflag = true;
                  }
              }
            }else{
                checkflag = true;
            }
        }
        recyclerView.clearFocus();
        recyclerView.invalidate();
        adapter.notifyDataSetChanged();
        return checkflag;
    }


    private class PreviousDaySockViewAdapter extends RecyclerView.Adapter<PreviousDaySockViewAdapter.ViewHolder> {

        Context context;

        public PreviousDaySockViewAdapter(PreviousDayStockActivity context, ArrayList<CommonChillerDataGetterSetter> skuList) {
            this.context = context;
             skuListdata = skuList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_day_stock_view,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.headerTxt.setText(skuListdata.get(position).getSku_name());

            holder.biraQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hashFocus) {
                    if (!hashFocus) {
                        final EditText Caption = (EditText) view;
                        String value1 = Caption.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                        if (value1.length() < Caption.getText().toString().length()) {
                            ((EditText) view).setText(value1);
                        }
                        if (!value1.equals("")) {
                            skuListdata.get(position).setBiraQty(value1);
                        } else {
                            skuListdata.get(position).setBiraQty("");
                        }
                    }
                }
            });


            if(!skuListdata.get(position).getBiraQty().equalsIgnoreCase("")){
               holder.biraQty.setText(skuListdata.get(position).getBiraQty());
            }else{
                holder.biraQty.setText("");
            }


            if (!checkflag)
            {
                boolean flag = true;
                if (skuListdata.get(position).getBiraQty().equals("") || skuListdata.get(position).getBiraQty().equals("0") ) {
                    flag = false;
                }
                if (!flag) {
                    holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.white));
                }
            } else {
                holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.white));
            }

        }

        @Override
        public int getItemCount() {
            return skuListdata.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView headerTxt;
            EditText biraQty;
            CardView card_view;

            public ViewHolder(View itemView) {
                super(itemView);

                headerTxt       = (TextView)itemView.findViewById(R.id.header_txt);
                biraQty         = (EditText)itemView.findViewById(R.id.bira_chiller_qty);
                card_view       = (CardView)itemView.findViewById(R.id.card_view);
            }
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
            new AlertandMessages(   PreviousDayStockActivity.this, null, null, null).backpressedAlert(PreviousDayStockActivity.this);
           /* finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);*/
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new AlertandMessages(   PreviousDayStockActivity.this, null, null, null).backpressedAlert(PreviousDayStockActivity.this);
    }

}
