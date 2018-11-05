package com.cpm.bira.DailyEntry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpm.bira.Constant.AlertandMessages;
import com.cpm.bira.Constant.CommonFunctions;
import com.cpm.bira.Constant.CommonString;
import com.cpm.bira.Database.BiraDB;
import com.cpm.bira.GetterSetter.CommonChillerDataGetterSetter;
import com.cpm.bira.GetterSetter.JourneyPlan;
import com.cpm.bira.GetterSetter.NonPosmReason;
import com.cpm.bira.R;
import com.cpm.bira.adapter.SpinnerAdapterView;

import java.io.File;
import java.util.ArrayList;

public class POSMDeploymentActivity extends AppCompatActivity {

    Toolbar toolBar;

    FloatingActionButton saveBtn;
    String[] spinner_list = {"Select", "YES", "NO"};
    private ArrayList<CommonChillerDataGetterSetter> deploymentData = new ArrayList<>();
    boolean checkflag = true;
    String Error_Message= "",visit_date,_pathforcheck1 = "",str, image1 = "", visit_date_formatted, _path = "",store_id,_UserId;
    private BiraDB db;
    JourneyPlan jcpGetset;
    RecyclerView recyclerView;
    Context context;
    int cameraPosition =-1;
    int indexVal = 0;
    POSMDeploymentAdapter adapter;
    private SharedPreferences preferences;
    private ArrayList<Integer> global_List = new ArrayList<>();
    private ArrayList<CommonChillerDataGetterSetter> posmDeploymentData = new ArrayList<>();
    private ArrayList<NonPosmReason>  reasonData = new ArrayList<>();
    private ArrayList<CommonChillerDataGetterSetter> posmDeploymentSavedData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posmdeployment);

        db = new BiraDB(this);
        declaration();

        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null) {
            jcpGetset = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            store_id = String.valueOf(jcpGetset.getStoreId());
        }

        db.open();
        reasonData = db.getPOSMReason();
        posmDeploymentSavedData = db.getPOSMDeploymentSavedData(Integer.valueOf(store_id),visit_date);
        if(posmDeploymentSavedData.size() >0){
            createView(posmDeploymentSavedData);
        }else{
            posmDeploymentData = db.getPOSMDeploymentData(jcpGetset);
            createView(posmDeploymentData);
        }
    }


    private void createView(ArrayList<CommonChillerDataGetterSetter> posmDeploymentData) {
        adapter = new POSMDeploymentAdapter(this,posmDeploymentData);
        recyclerView.setHasFixedSize(true);
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }


    private class POSMDeploymentAdapter extends RecyclerView.Adapter<POSMDeploymentAdapter.ViewHolder>{
        Context context;

        public POSMDeploymentAdapter(POSMDeploymentActivity context, ArrayList<CommonChillerDataGetterSetter> posmDeploymentData) {
            this.context = context;
            deploymentData = posmDeploymentData;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posm_deployment_custom_view,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.posmTxt.setText(deploymentData.get(position).getPosm());

            SpinnerAdapterView adapter = new SpinnerAdapterView(getApplicationContext(), spinner_list);
            holder.existSpinner.setAdapter(adapter);

            holder.existSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                    if (itemPos == 0) {
                        holder.deploymentLayout.setVisibility(View.GONE);
                        holder.reasonLayout.setVisibility(View.GONE);
                        deploymentData.get(position).setExist("");
                    } else if (itemPos == 1) {

                        if (!deploymentData.get(position).getOld_deployment().equalsIgnoreCase("")) {
                            holder.old_deployment.setText(deploymentData.get(position).getOld_deployment());
                        } else {
                            holder.old_deployment.setText("");
                        }

                        if (!deploymentData.get(position).getNew_deployment().equalsIgnoreCase("")) {
                            holder.new_deployment.setText(deploymentData.get(position).getNew_deployment());
                        } else {
                            holder.new_deployment.setText("");
                        }

                        holder.deploymentLayout.setVisibility(View.VISIBLE);
                        holder.reasonLayout.setVisibility(View.GONE);
                        deploymentData.get(position).setExist("1");
                    } else {
                        holder.deploymentLayout.setVisibility(View.GONE);
                        holder.reasonLayout.setVisibility(View.VISIBLE);
                        deploymentData.get(position).setExist("0");
                        deploymentData.get(position).setOld_deployment("");
                        deploymentData.get(position).setNew_deployment("");

                        if(!deploymentData.get(position).getImg1().equalsIgnoreCase("")){
                            holder.deploymentImage.setImageResource(R.mipmap.camera_red);
                            image1 ="";
                        }

                        File file = new File(CommonString.FILE_PATH + deploymentData.get(position).getImg1());
                        if (file.exists()) {
                            deploymentData.get(position).setImg1("");
                            file.delete();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            holder.old_deployment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hashFocus) {
                    if (!hashFocus) {
                        final EditText Caption = (EditText) view;
                        String value1 = Caption.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                        if (!value1.equals("")) {
                            ((EditText) view).setText(value1);
                            deploymentData.get(position).setOld_deployment(value1);
                        } else {
                            deploymentData.get(position).setOld_deployment("");
                        }
                    }
                }
            });

            holder.new_deployment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hashFocus) {
                    if (!hashFocus) {
                        final EditText Caption = (EditText) view;
                        String value1 = Caption.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                        if (!value1.equals("")) {
                            ((EditText) view).setText(value1);
                            deploymentData.get(position).setNew_deployment(value1);
                        } else {
                            deploymentData.get(position).setNew_deployment("");
                        }
                    }
                }
            });


            holder.deploymentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cameraPosition = position;
                    _pathforcheck1 = store_id + "_" + _UserId.replace(".", "") + "_POSM_Deployment-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck1;
                    //CommonFunctions.startCameraActivity(activity, _path);
                    CommonFunctions.startAnncaCameraActivity(context, _path, null,false);
                }
            });

            CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),reasonData);
            holder.reasonSpinner.setAdapter(customAdapter);

            holder.reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    deploymentData.get(position).setReason_id(String.valueOf(reasonData.get(i).getPreasonId()));
                    deploymentData.get(position).setReason(reasonData.get(i).getPreason());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            if(deploymentData.get(position).getExist().equalsIgnoreCase("1")) {
                if (deploymentData.get(position).getExist().equalsIgnoreCase("")) {
                    holder.existSpinner.setSelection(0);
                } else if (deploymentData.get(position).getExist().equalsIgnoreCase("1")) {
                    holder.existSpinner.setSelection(1);
                } else {
                    holder.existSpinner.setSelection(2);
                }

                if (!deploymentData.get(position).getOld_deployment().equalsIgnoreCase("")) {
                    holder.old_deployment.setText(deploymentData.get(position).getOld_deployment());
                } else {
                    holder.old_deployment.setText("");
                }

                if (!deploymentData.get(position).getNew_deployment().equalsIgnoreCase("")) {
                    holder.new_deployment.setText(deploymentData.get(position).getNew_deployment());
                } else {
                    holder.new_deployment.setText("");
                }
            }else{
                if (deploymentData.get(position).getExist().equalsIgnoreCase("")) {
                    holder.existSpinner.setSelection(0);
                } else if (deploymentData.get(position).getExist().equalsIgnoreCase("1")) {
                    holder.existSpinner.setSelection(1);
                } else {
                    holder.existSpinner.setSelection(2);
                }

                if (deploymentData.get(position).getReason_id().equalsIgnoreCase("0")) {
                    holder.reasonSpinner.setSelection(0);
                } else {
                    for(int i=0;i<reasonData.size();i++){
                        if(reasonData.get(i).getPreason().equalsIgnoreCase(deploymentData.get(position).getReason())){
                            indexVal = i;
                            break;
                        }
                    }
                    holder.reasonSpinner.setSelection(indexVal);
                }
            }

            if(!image1.equalsIgnoreCase("")){
                if(cameraPosition == position){
                    deploymentData.get(position).setImg1(image1);
                    image1 ="";
                }
            }

            if(deploymentData.get(position).getImg1().equalsIgnoreCase("")){
                holder.deploymentImage.setImageResource(R.mipmap.camera_red);
            }else{
                holder.deploymentImage.setImageResource(R.drawable.camera_green);
            }


            if (!checkflag)
            {
                boolean flag = true;
                if(deploymentData.get(position).getExist().equalsIgnoreCase("")) {
                   holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                }else if(deploymentData.get(position).getExist().equalsIgnoreCase("1")) {
                    if (deploymentData.get(position).getOld_deployment().equalsIgnoreCase("") || deploymentData.get(position).getOld_deployment().equalsIgnoreCase("0")
                            || deploymentData.get(position).getNew_deployment().equalsIgnoreCase("") || deploymentData.get(position).getNew_deployment().equalsIgnoreCase("0")
                            || deploymentData.get(position).getImg1().equalsIgnoreCase("")) {
                        flag = false;
                    }
                    if (!flag) {
                        holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                    } else {
                        holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.white));
                    }
                }else{
                    if (deploymentData.get(position).getReason_id().equalsIgnoreCase("0")) {
                        flag = false;
                    }
                    if (!flag) {
                        holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                    } else {
                        holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.white));
                    }
                }
            } else {
                holder.card_view.setCardBackgroundColor(getResources().getColor(R.color.white));
            }

        }

        @Override
        public int getItemCount() {
            return deploymentData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView posmTxt,old_deployment,new_deployment;
            Spinner existSpinner,reasonSpinner;
            LinearLayout deploymentLayout,reasonLayout;
            CardView card_view;
            ImageView deploymentImage;

            public ViewHolder(View itemView) {
                super(itemView);

                posmTxt = (TextView)itemView.findViewById(R.id.posm_txt);
                old_deployment = (TextView)itemView.findViewById(R.id.old_deployment_txt);
                new_deployment = (TextView)itemView.findViewById(R.id.new_deployment_txt);
                existSpinner = (Spinner)itemView.findViewById(R.id.POSM_exist_spinner);
                reasonSpinner = (Spinner)itemView.findViewById(R.id.deployment_reason);
                deploymentLayout = (LinearLayout)itemView.findViewById(R.id.deploymentLayout);
                reasonLayout = (LinearLayout)itemView.findViewById(R.id.reason_layout);
                card_view = (CardView) itemView.findViewById(R.id.card_view);
                deploymentImage = (ImageView)itemView.findViewById(R.id.deploymentImage);

            }
        }
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
                        image1 = _pathforcheck1;
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }


    private void declaration() {
        context = this;
        toolBar = findViewById(R.id.posm_deployment_toolbar);
        saveBtn = findViewById(R.id.posm_deployment_fab);
        recyclerView  = findViewById(R.id.posm_deployment_view);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date  = preferences.getString(CommonString.KEY_DATE, null);
        _UserId = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date_formatted = preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");

        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("POSM Deployment - " + visit_date);
        str = CommonString.FILE_PATH;

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.clearFocus();

                if(checkValidation(deploymentData)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to save Data?").setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    savePOSMDeploymentData(deploymentData);
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
                    AlertandMessages.showToastMsg(POSMDeploymentActivity.this, Error_Message);
                }
            }
        });
    }

    private void savePOSMDeploymentData(ArrayList<CommonChillerDataGetterSetter> deploymentData) {
        db.open();
        for(int i=0;i<deploymentData.size();i++){
            if(deploymentData.get(i).getExist().equalsIgnoreCase("0")){
                if(!deploymentData.get(i).getImg1().equalsIgnoreCase("")) {
                    File file = new File(CommonString.FILE_PATH + deploymentData.get(i).getImg1());
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
        long val = db.insertPOSMDeploymentData(deploymentData, jcpGetset);
        if (val > 0) {
            AlertandMessages.showToastMsg(this, "Data has been saved.");
            finish();
        } else {
            AlertandMessages.showToastMsg(this, "Data not saved try again.");
        }

    }

    private boolean checkValidation(ArrayList<CommonChillerDataGetterSetter> deploymentData) {
        checkflag = true;
        for(int i=0;i<deploymentData.size();i++){
            if(deploymentData.get(i).getExist().equalsIgnoreCase("")){
                checkflag = false;
                Error_Message = getString(R.string.posm_exist_error);
                break;
            }else{
                if(deploymentData.get(i).getExist().equalsIgnoreCase("1")) {

                    if (deploymentData.get(i).getOld_deployment().equalsIgnoreCase("")) {
                        checkflag = false;
                        Error_Message = getString(R.string.old_posm_error);
                        break;
                    }
                    else if (deploymentData.get(i).getNew_deployment().equalsIgnoreCase("")) {
                        checkflag = false;
                        Error_Message = getString(R.string.new_posm_error);
                        break;
                    }
                    else if (deploymentData.get(i).getOld_deployment().equalsIgnoreCase("0")) {
                        checkflag = false;
                        Error_Message = getString(R.string.non_zero_old_posm_error);
                        break;
                    }
                    else if (deploymentData.get(i).getNew_deployment().equalsIgnoreCase("0")) {
                        checkflag = false;
                        Error_Message = getString(R.string.non_zero_new_posm_error);
                        break;
                    }
                    else if (deploymentData.get(i).getImg1().equalsIgnoreCase("")) {
                        checkflag = false;
                        Error_Message = getString(R.string.single_image_error);
                        if (!global_List.contains(i)) {
                            global_List.add(i);
                        }
                        break;
                    }
                }else{
                    if (deploymentData.get(i).getReason_id().equalsIgnoreCase("0")) {
                        checkflag = false;
                        Error_Message = getString(R.string.select_reason_error);
                        break;
                    }else {
                        checkflag = true;
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
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
            new AlertandMessages(POSMDeploymentActivity.this, null, null, null).backpressedAlert(POSMDeploymentActivity.this);
            //finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        new AlertandMessages(POSMDeploymentActivity.this, null, null, null).backpressedAlert(POSMDeploymentActivity.this);
    }


    private class CustomAdapter extends BaseAdapter {

        Context context;
        ArrayList<NonPosmReason> reasonData;

        public CustomAdapter(Context context, ArrayList<NonPosmReason> reasonData) {
            this.context = context;
            this.reasonData = reasonData;
        }

        @Override
        public int getCount() {
            return reasonData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item,null);
            TextView names = (TextView) view.findViewById(R.id.spinner_txt);
            names.setText(reasonData.get(i).getPreason());
            return view;
        }
    }
}
