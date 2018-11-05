package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BrandMasterGetterSetter {
    @SerializedName("Brand_Master")
    @Expose
    private List<BrandMaster> brandMaster = null;

    public List<BrandMaster> getBrandMaster() {
        return brandMaster;
    }

    public void setBrandMaster(List<BrandMaster> brandMaster) {
        this.brandMaster = brandMaster;
    }
}
