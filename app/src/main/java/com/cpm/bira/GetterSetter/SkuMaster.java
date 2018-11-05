package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SkuMaster {
    @SerializedName("Sku_Id")
    @Expose
    private Integer skuId;
    @SerializedName("Sku")
    @Expose
    private String sku;
    @SerializedName("Brand_Id")
    @Expose
    private Integer brandId;
    @SerializedName("Sku_Sequence")
    @Expose
    private Integer skuSequence;

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getSkuSequence() {
        return skuSequence;
    }

    public void setSkuSequence(Integer skuSequence) {
        this.skuSequence = skuSequence;
    }

}
