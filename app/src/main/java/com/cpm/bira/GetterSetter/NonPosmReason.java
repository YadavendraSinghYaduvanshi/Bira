package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NonPosmReason {
    @SerializedName("Preason_Id")
    @Expose
    private Integer preasonId;
    @SerializedName("Preason")
    @Expose
    private String preason;
    @SerializedName("Remark")
    @Expose
    private Boolean remark;

    public Integer getPreasonId() {
        return preasonId;
    }

    public void setPreasonId(Integer preasonId) {
        this.preasonId = preasonId;
    }

    public String getPreason() {
        return preason;
    }

    public void setPreason(String preason) {
        this.preason = preason;
    }

    public Boolean getRemark() {
        return remark;
    }

    public void setRemark(Boolean remark) {
        this.remark = remark;
    }

}
