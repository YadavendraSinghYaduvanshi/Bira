package com.cpm.bira.DailyEntry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonFunctions;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CommonChillerDataGetterSetter;
import com.cpm.bira.GetterSetter.CommonDataGetterSetter;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.R;
import com.cpm.bira.adapter.SpinnerAdapterView;

import java.io.File;
import java.util.ArrayList;

public class BiraChillerActivity extends AppCompatActivity {

    Toolbar toolBar;
    Spinner spnTxt,puritySpnTxt,chargingSpnTxt;
    EditText capacityTxt;
    FloatingActionButton saveBtn;
    ImageView img1;
    Context context ;
    String[] spinner_list = {"Select", "YES", "NO"};
    String [] spinner_pruity = {"Select","10%","20%","30%","40%","50%","60%","70%","80%","90%","100%"};
    LinearLayout layout;
    String visit_date,_pathforcheck1 = "",str, image1 = "", visit_date_formatted, _path = "",store_id,_UserId,chargingTxt = "",Error_Message= "",chiller_flag="";
    private BiraDB db;
    JourneyPlan jcpGetset;
    private SharedPreferences preferences;
    private CommonChillerDataGetterSetter chillerData = new CommonChillerDataGetterSetter();
    private ArrayList<CommonChillerDataGetterSetter> skuList = new ArrayList<>();
    ArrayList<CommonChillerDataGetterSetter> chillerList = new ArrayList<>();
    private ArrayList<CommonChillerDataGetterSetter> biraChillerData = new ArrayList<>();

    RecyclerView recyclerView;
    BiraChillerViewAdapter biraChillerViewAdapter;
    boolean checkflag = true;
    int indexVal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bira_chiller);

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null && getIntent().getStringExtra(CommonString.CHILLER_FLAG) != null) {
            jcpGetset = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = String.valueOf(jcpGetset.getStoreId());
            chiller_flag = getIntent().getStringExtra(CommonString.CHILLER_FLAG);
        }
        db = new BiraDB(this);
        declaration();
        getSkuData();
    }

    private void getSkuData() {
        db.open();
        // 1 shows bira chiller data and else if 0 then retailer chiller data
        if(chiller_flag.equalsIgnoreCase("1")) {
            biraChillerData =  db.getBiraChillerData(Integer.valueOf(store_id),visit_date);
        }else{
            biraChillerData =  db.getRetailerOwnedChillerData(Integer.valueOf(store_id),visit_date);
        }

        if (biraChillerData.size() > 0) {
            createView(biraChillerData);
        } else {
            skuList = db.getBiraSkuData(jcpGetset);
            createView(skuList);
        }
    }


    private void createView(ArrayList<CommonChillerDataGetterSetter> skuList) {
        biraChillerViewAdapter = new BiraChillerViewAdapter(this,skuList);
        recyclerView.setHasFixedSize(true);
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(biraChillerViewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            // hidding fab button when scrolling layout
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                recyclerView.clearFocus();
                biraChillerViewAdapter.notifyDataSetChanged();
               /* if (dy < 0) {
                    saveBtn.show();
                } else if (dy > 0) {
                    saveBtn.hide();
                }*/
            }
        });
    }


    private void declaration() {

        context = this;
        toolBar                 = findViewById(R.id.bira_chiller_toolbar);
        spnTxt                  = findViewById(R.id.bira_chiller_spinner);
        puritySpnTxt            = findViewById(R.id.bira_chiller_purity_spinner);
        chargingSpnTxt          = findViewById(R.id.bira_chiller_charging_spinner);
        capacityTxt             = findViewById(R.id.bira_chiller_capacity);
        saveBtn                 = findViewById(R.id.bira_chiller_fab);
        recyclerView            = findViewById(R.id.sku_view);
        img1                    = findViewById(R.id.cameraImg);
        layout                  = findViewById(R.id.bira_chiller_layout);

        SpinnerAdapterView adapter = new SpinnerAdapterView(getApplicationContext(), spinner_list);
        spnTxt.setAdapter(adapter);
        puritySpnTxt.setAdapter(adapter);

        SpinnerAdapterView adapter2 = new SpinnerAdapterView(getApplicationContext(), spinner_pruity);
        chargingSpnTxt.setAdapter(adapter2);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");
        str = CommonString.FILE_PATH;
        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(chiller_flag.equalsIgnoreCase("1")) {
            getSupportActionBar().setTitle("BIRA Chiller - " + visit_date);
        }else{
            getSupportActionBar().setTitle("Retailer Owned Chiller - " + visit_date);
        }

        spnTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                if (itemPos == 0) {
                    layout.setVisibility(View.GONE);
                    chillerData.setExist("");
                } else if (itemPos == 1) {

                    // 1 shows bira chiller data and else if 0 then retailer chiller data
                    if(chiller_flag.equalsIgnoreCase("1")) {
                        biraChillerData =  db.getBiraChillerData(Integer.valueOf(store_id),visit_date);
                    }else{
                        biraChillerData =  db.getRetailerOwnedChillerData(Integer.valueOf(store_id),visit_date);
                    }

                    if(biraChillerData.size() >0){
                        if(biraChillerData.get(0).getBiraPresence().equalsIgnoreCase("")){
                            skuList = db.getBiraSkuData(jcpGetset);
                            createView(skuList);
                        }else{
                            createView(biraChillerData);
                        }
                    }

                    layout.setVisibility(View.VISIBLE);
                    chillerData.setExist("1");
                    checkflag = true;
                } else {
                    layout.setVisibility(View.GONE);
                    chillerData.setExist("0");
                }
                // set check flag true after select the value=
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        db.open();
        // 1 shows bira chiller data and else if 0 then retailer chiller data
        if(chiller_flag.equalsIgnoreCase("1")) {
            biraChillerData =  db.getBiraChillerData(Integer.valueOf(store_id),visit_date);
        }else{
            biraChillerData =  db.getRetailerOwnedChillerData(Integer.valueOf(store_id),visit_date);
        }

        if(biraChillerData.size()>0){
            if(biraChillerData.get(0).getExist().equalsIgnoreCase("1")) {
                spnTxt.setSelection(1);
                layout.setVisibility(View.VISIBLE);
            }else{
                layout.setVisibility(View.GONE);
                spnTxt.setSelection(2);
            }

            if(!biraChillerData.get(0).getCharging().equalsIgnoreCase("")) {
                // getting text position from spinner purity list
                for (int itemPos = 0; itemPos < spinner_pruity.length; itemPos++) {
                    if (spinner_pruity[itemPos].equalsIgnoreCase(biraChillerData.get(0).getCharging())) {
                        indexVal = itemPos;
                        break;
                    }
                }
                chargingSpnTxt.setSelection(indexVal);
            }else{
                chargingSpnTxt.setSelection(0);
            }


            if(!biraChillerData.get(0).getImg1().equalsIgnoreCase("")){
                img1.setImageResource(R.drawable.camera_green);
                image1 = biraChillerData.get(0).getImg1();
            }else{
                img1.setImageResource(R.mipmap.camera_red);
            }

            if(biraChillerData.get(0).getPurity().equalsIgnoreCase("1")){
                puritySpnTxt.setSelection(1);
            }else{
                if(biraChillerData.get(0).getPurity().equalsIgnoreCase("0")) {
                    puritySpnTxt.setSelection(2);
                }else{
                    puritySpnTxt.setSelection(0);
                }
            }

            capacityTxt.setText(biraChillerData.get(0).getCapacity());

        }

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chiller_flag.equalsIgnoreCase("1")){
                    _pathforcheck1 = store_id + "_" + _UserId.replace(".", "") + "_Bira_Chiller-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                }else{
                    _pathforcheck1 = store_id + "_" + _UserId.replace(".", "") + "_Retailer_Owned_Chiller-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                }
                _path = CommonString.FILE_PATH + _pathforcheck1;
                //CommonFunctions.startCameraActivity(activity, _path);
                CommonFunctions.startAnncaCameraActivity(context, _path, null,false);

            }
        });

        chargingSpnTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int itemPos, long l) {
                if (itemPos == 0) {
                    chillerData.setCharging("");
                } else if (itemPos == 10) {
                    chargingTxt = spinner_pruity[itemPos];
                    chillerData.setCharging(chargingTxt);
                    puritySpnTxt.setSelection(1);
                    puritySpnTxt.setEnabled(false);
                } else{
                    chargingTxt = spinner_pruity[itemPos];
                    chillerData.setCharging(chargingTxt);
                    puritySpnTxt.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        puritySpnTxt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                if (itemPos == 0) {
                    chillerData.setPurity("");
                } else if (itemPos == 1) {
                    chillerData.setPurity("1");
                } else {
                    chillerData.setPurity("0");
                }
                // set check flag true after select the value=
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        capacityTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hashFocus) {
                if (!hashFocus) {
                    final EditText Caption = (EditText) view;
                    String value1 = Caption.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                    if (value1.length() < Caption.getText().toString().length()) {
                        ((EditText) view).setText(value1);
                    }
                    if (!value1.equals("")) {
                        chillerData.setCapacity(value1);
                    } else {
                        chillerData.setCapacity("");
                    }
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                capacityTxt.clearFocus();
                recyclerView.clearFocus();
                if(checkFields(chillerData,image1,chillerList)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to save Data?").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveChillerData(chillerList,chillerData,visit_date,jcpGetset,chiller_flag,image1);
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

    private void saveChillerData(ArrayList<CommonChillerDataGetterSetter> chillerList, CommonChillerDataGetterSetter chillerData, String visit_date, JourneyPlan jcpGetset, String chiller_flag, String image1) {
        db.open();
        if(chillerData.getExist().equalsIgnoreCase("1")) {
            long val = db.insertConsumerChillerData(chillerList, chillerData, jcpGetset, visit_date, chiller_flag, image1);
            if (val > 0) {
                AlertandMessages.showToastMsg(this, "Data has been saved.");
                finish();
            } else {
                AlertandMessages.showToastMsg(this, "Data not saved try again.");
            }
        }else{
            long val = db.insertConsumerChillerData(chillerData, jcpGetset, visit_date, chiller_flag, image1);
            if (val > 0) {
                AlertandMessages.showToastMsg(this, "Data has been saved.");
                finish();
            } else {
                AlertandMessages.showToastMsg(this, "Data not saved try again.");
            }
        }
    }

    private boolean checkFields(CommonChillerDataGetterSetter chillerData, String image1, ArrayList<CommonChillerDataGetterSetter> chillerList) {
        checkflag = true;
        if(chillerData.getExist().equalsIgnoreCase("")){
            checkflag = false;
            Error_Message = getString(R.string.bira_exist_error);
            return checkflag;
        }else {
            if (chillerData.getExist().equalsIgnoreCase("1")) {
                if (chillerData.getCharging().equalsIgnoreCase("")) {
                    checkflag = false;
                    Error_Message = getString(R.string.charging_error);
                    return checkflag;
                } else if (chillerData.getCapacity().equalsIgnoreCase("")) {
                    checkflag = false;
                    Error_Message = getString(R.string.capacity_error);
                    return checkflag;
                } else if (chillerData.getPurity().equalsIgnoreCase("")) {
                    checkflag = false;
                    Error_Message = getString(R.string.purity_error);
                    return checkflag;
                } else if (image1.equalsIgnoreCase("")) {
                    checkflag = false;
                    Error_Message = getString(R.string.single_image_error);
                    return checkflag;
                } else {
                    for (int i = 0; i < this.chillerList.size(); i++) {
                        if(this.chillerList.get(i).getBiraPresence().equalsIgnoreCase("")){
                            checkflag = false;
                            Error_Message = getString(R.string.sku_present_error);
                            break;
                        }
                        else if(this.chillerList.get(i).getBiraPresence().equalsIgnoreCase("1"))
                        {
                            if (this.chillerList.get(i).getBiraQty().equalsIgnoreCase("")) {
                                checkflag = false;
                                Error_Message = getString(R.string.qty_error);
                                break;
                            } else if (this.chillerList.get(i).getBiraQty().equalsIgnoreCase("0")) {
                                checkflag = false;
                                Error_Message = getString(R.string.qty_zero_error);
                                break;
                            } else {
                                checkflag = true;
                            }
                        }else{
                            checkflag = true;
                        }
                    }
                }
            } else {
                checkflag = true;
                return checkflag;
            }
        }

        biraChillerViewAdapter.notifyDataSetChanged();
        return checkflag;
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
                break;
        }
    }

    private class BiraChillerViewAdapter extends RecyclerView.Adapter<BiraChillerViewAdapter.ViewHolder> {

        Context context;
        public BiraChillerViewAdapter(BiraChillerActivity context, ArrayList<CommonChillerDataGetterSetter> skuList) {
            this.context = context;
            chillerList = skuList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bira_chiller_custom_view,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.headerTxt.setText(chillerList.get(position).getSku_name());

            SpinnerAdapterView adapter = new SpinnerAdapterView(getApplicationContext(), spinner_list);
            holder.presentSpn.setAdapter(adapter);

            holder.presentSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                    if (itemPos == 0) {
                        holder.bira_qty_layout.setVisibility(View.GONE);
                        chillerList.get(position).setBiraPresence("");
                    } else if (itemPos == 1) {
                        holder.bira_qty_layout.setVisibility(View.VISIBLE);
                        chillerList.get(position).setBiraPresence("1");
                    } else {
                        chillerList.get(position).setBiraQty("");
                        holder.bira_qty_layout.setVisibility(View.GONE);
                        holder.biraQty.setText("");
                        chillerList.get(position).setBiraPresence("0");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if(chillerList.get(position).getBiraPresence().equalsIgnoreCase("")){
                holder.presentSpn.setSelection(0);
            }else if(chillerList.get(position).getBiraPresence().equalsIgnoreCase("1")){
                holder.presentSpn.setSelection(1);
            }else{
                holder.presentSpn.setSelection(2);
            }

            if(!chillerList.get(position).getBiraQty().equalsIgnoreCase("")){
                holder.biraQty.setText(chillerList.get(position).getBiraQty());
            }else{
                holder.biraQty.setText("");
            }


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
                            chillerList.get(position).setBiraQty(value1);
                        } else {
                            chillerList.get(position).setBiraQty("");
                        }
                    }
                }
            });

            if (!checkflag)
            {
                boolean flag = true;
                if (chillerList.get(position).getBiraQty().equals("") || chillerList.get(position).getBiraQty().equals("0") ) {
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
            return chillerList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            Spinner presentSpn;
            TextView headerTxt;
            EditText biraQty;
            LinearLayout bira_qty_layout;
            CardView card_view;

            public ViewHolder(View itemView) {
                super(itemView);

                headerTxt       = (TextView)itemView.findViewById(R.id.header_txt);
                presentSpn      = (Spinner) itemView.findViewById(R.id.bira_chiller_present);
                biraQty         = (EditText)itemView.findViewById(R.id.bira_chiller_qty);
                bira_qty_layout = (LinearLayout)itemView.findViewById(R.id.bira_qty_layout);
                card_view = (CardView)itemView.findViewById(R.id.card_view);
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
            new AlertandMessages(   BiraChillerActivity.this, null, null, null).backpressedAlert(BiraChillerActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
            new AlertandMessages(   BiraChillerActivity.this, null, null, null).backpressedAlert(BiraChillerActivity.this);
    }


}
