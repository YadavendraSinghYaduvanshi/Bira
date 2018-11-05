package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MappingStockGetterSetter {
    @SerializedName("Mapping_Stock")
    @Expose
    private List<MappingStock> mappingStock = null;

    public List<MappingStock> getMappingStock() {
        return mappingStock;
    }

    public void setMappingStock(List<MappingStock> mappingStock) {
        this.mappingStock = mappingStock;
    }
}
