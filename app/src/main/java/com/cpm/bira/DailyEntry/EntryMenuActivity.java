package com.cpm.bira.DailyEntry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CommonChillerDataGetterSetter;
import com.cpm.bira.GetterSetter.CommonDataGetterSetter;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.GetterSetter.MenuGetterSetter;
import com.cpm.bira.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntryMenuActivity extends AppCompatActivity {

    BiraDB database;
    Context context;
    JourneyPlan journeyPlan;
    String  store_id = "", visit_date = "" ,store_name = "";
    List<MenuGetterSetter> data = new ArrayList<>();
    RecyclerView recyclerView;
    ValueAdapter adapter;
    private CommonDataGetterSetter consumerPromoCouponData;
    private ArrayList<CommonDataGetterSetter> competitionData;
    private ArrayList<CommonChillerDataGetterSetter> previousDayStockData;
    private ArrayList<CommonChillerDataGetterSetter> biraChillerData;
    private ArrayList<CommonChillerDataGetterSetter> retailerOwnedChillerData;
    private ArrayList<CommonChillerDataGetterSetter> posmDeploymentData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_menu);

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            journeyPlan = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = String.valueOf(journeyPlan.getStoreId());
            store_name = journeyPlan.getStoreName();
            visit_date = journeyPlan.getVisitDate();
        }

        declaration();
        MenuGetterSetter recData = new MenuGetterSetter();
        recData.setIconName("POSM Deployment");
        data.add(recData);

        recData = new MenuGetterSetter();
        recData.setIconName("BIRA Chiller");
        data.add(recData);

        recData = new MenuGetterSetter();
        recData.setIconName("Retailer Owned Chiller");
        data.add(recData);

        recData = new MenuGetterSetter();
        recData.setIconName("Competition Visibility");
        data.add(recData);

        recData = new MenuGetterSetter();
        recData.setIconName("Consumer Promo Coupon");
        data.add(recData);

        recData = new MenuGetterSetter();
        recData.setIconName("Previous Day Stock");
        data.add(recData);

    }

    @Override
    protected void onResume() {
        super.onResume();
        filldata();
    }


    private void filldata() {

        database.open();

        competitionData             =  database.getCompetitionData(Integer.valueOf(store_id),visit_date);
        previousDayStockData        =  database.getPreviousDayStockData(Integer.valueOf(store_id),visit_date);
        biraChillerData             =  database.getBiraChillerData(Integer.valueOf(store_id),visit_date);
        retailerOwnedChillerData    =  database.getRetailerOwnedChillerData(Integer.valueOf(store_id),visit_date);
        consumerPromoCouponData     =  database.getConsumerPromoCouponData(Integer.valueOf(store_id),visit_date);
        posmDeploymentData          = database.getPOSMDeploymentSavedData(Integer.valueOf(store_id),visit_date);


        if(posmDeploymentData.size() >0) {
            if (!posmDeploymentData.get(0).getExist().equalsIgnoreCase("")) {
                data.get(0).setIconImage(R.drawable.posm_deployment_done);
            } else {
                data.get(0).setIconImage(R.drawable.posm_deployment);
            }
        }else{
            data.get(0).setIconImage(R.drawable.posm_deployment);
        }

        if(biraChillerData.size() > 0){
            if (!biraChillerData.get(0).getExist().equalsIgnoreCase("")) {
                data.get(1).setIconImage(R.drawable.bira_chiller_done);
            } else {
                data.get(1).setIconImage(R.drawable.bira_chiller);
            }
        }else{
            data.get(1).setIconImage(R.drawable.bira_chiller);
        }

        if(retailerOwnedChillerData.size() > 0){
            if (!retailerOwnedChillerData.get(0).getExist().equalsIgnoreCase("")) {
                data.get(2).setIconImage(R.drawable.retailer_owned_chiller_done);
            } else {
                data.get(2).setIconImage(R.drawable.retailer_owned_chiller);
            }
        }else{
            data.get(2).setIconImage(R.drawable.retailer_owned_chiller);
        }

        if(competitionData.size() > 0) {
            if (!competitionData.get(0).getPresent().equalsIgnoreCase("")) {
                data.get(3).setIconImage(R.drawable.competition_visibility_done);
            } else {
                data.get(3).setIconImage(R.drawable.competition_visibility);
            }
        }else {
            data.get(3).setIconImage(R.drawable.competition_visibility);
        }

        if(!consumerPromoCouponData.getPresent().equalsIgnoreCase("")) {
            data.get(4).setIconImage(R.drawable.consumer_promo_coupon_done);
        }else{
            data.get(4).setIconImage(R.drawable.consumer_promo_coupon);
        }

        if(previousDayStockData.size() >0) {
            if (!previousDayStockData.get(0).getExist().equalsIgnoreCase("")) {
                data.get(5).setIconImage(R.drawable.previous_day_stock_done);
            } else {
                data.get(5).setIconImage(R.drawable.previous_day_stock);
            }
        }else{
            data.get(5).setIconImage(R.drawable.previous_day_stock);
        }


        adapter = new ValueAdapter(getApplicationContext(), data);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

    }


    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<MenuGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, List<MenuGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.custom_menu_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final MenuGetterSetter current = data.get(position);

            viewHolder.txt.setVisibility(View.GONE);
            viewHolder.icon.setImageResource(current.getIconImage());
            viewHolder.lay_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (current.getIconName().equalsIgnoreCase("POSM Deployment")) {
                        startActivity(new Intent(EntryMenuActivity.this, POSMDeploymentActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else if (current.getIconName().equalsIgnoreCase("BIRA Chiller")) {
                        startActivity(new Intent(EntryMenuActivity.this, BiraChillerActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan).putExtra(CommonString.CHILLER_FLAG,"1"));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else if (current.getIconName().equalsIgnoreCase("Retailer Owned Chiller")) {
                        startActivity(new Intent(EntryMenuActivity.this, BiraChillerActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan).putExtra(CommonString.CHILLER_FLAG,"0"));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else if (current.getIconName().equalsIgnoreCase("Competition Visibility")) {
                        startActivity(new Intent(EntryMenuActivity.this, CompetitionVisibiltyActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else if (current.getIconName().equalsIgnoreCase("Consumer Promo Coupon")) {
                        startActivity(new Intent(EntryMenuActivity.this, ConsumerPromoCouponActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }else if(current.getIconName().equalsIgnoreCase("Previous Day Stock")){
                        startActivity(new Intent(EntryMenuActivity.this, PreviousDayStockActivity.class).putExtra(CommonString.TAG_OBJECT, journeyPlan));
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt;
            ImageView icon;
            LinearLayout lay_menu;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt = (TextView) itemView.findViewById(R.id.list_txt);
                icon = (ImageView) itemView.findViewById(R.id.list_icon);
                lay_menu = (LinearLayout) itemView.findViewById(R.id.lay_menu);
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
            Intent in = new Intent(getApplicationContext(), StoreListActivity.class);
            startActivity(in);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    void declaration() {
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.ss_entry_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Entry Menu -" + visit_date);
        getSupportActionBar().setSubtitle(store_name + " - " + store_id);

        recyclerView = (RecyclerView) findViewById(R.id.entry_rec_menu);
        database = new BiraDB(context);
        database.open();
    }
}
