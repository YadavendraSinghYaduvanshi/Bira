package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MappingPOSMGetterSetter {
    @SerializedName("Mapping_Posm")
    @Expose
    private List<MappingPosm> mappingPosm = null;

    public List<MappingPosm> getMappingPosm() {
        return mappingPosm;
    }

    public void setMappingPosm(List<MappingPosm> mappingPosm) {
        this.mappingPosm = mappingPosm;
    }
}
