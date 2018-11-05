
package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PosmMasterGetterSetter {

    @SerializedName("Posm_Master")
    @Expose
    private List<PosmMaster> posmMaster = null;

    public List<PosmMaster> getPosmMaster() {
        return posmMaster;
    }

    public void setPosmMaster(List<PosmMaster> posmMaster) {
        this.posmMaster = posmMaster;
    }
}
