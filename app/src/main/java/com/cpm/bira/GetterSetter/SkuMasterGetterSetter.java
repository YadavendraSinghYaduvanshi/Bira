package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SkuMasterGetterSetter {
    @SerializedName("Sku_Master")
    @Expose
    private List<SkuMaster> skuMaster = null;

    public List<SkuMaster> getSkuMaster() {
        return skuMaster;
    }

    public void setSkuMaster(List<SkuMaster> skuMaster) {
        this.skuMaster = skuMaster;
    }
}
