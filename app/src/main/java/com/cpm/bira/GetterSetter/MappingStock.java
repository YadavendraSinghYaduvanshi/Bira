package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MappingStock {

    @SerializedName("State_Id")
    @Expose
    private Integer stateId;
    @SerializedName("Distributor_Id")
    @Expose
    private Integer distributorId;
    @SerializedName("Store_Type_Id")
    @Expose
    private Integer storeTypeId;
    @SerializedName("Sku_Id")
    @Expose
    private Integer skuId;
    @SerializedName("MSL")
    @Expose
    private Boolean mSL;
    @SerializedName("MBQ")
    @Expose
    private Integer mBQ;

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(Integer distributorId) {
        this.distributorId = distributorId;
    }

    public Integer getStoreTypeId() {
        return storeTypeId;
    }

    public void setStoreTypeId(Integer storeTypeId) {
        this.storeTypeId = storeTypeId;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public Boolean getMSL() {
        return mSL;
    }

    public void setMSL(Boolean mSL) {
        this.mSL = mSL;
    }

    public Integer getMBQ() {
        return mBQ;
    }

    public void setMBQ(Integer mBQ) {
        this.mBQ = mBQ;
    }

}

