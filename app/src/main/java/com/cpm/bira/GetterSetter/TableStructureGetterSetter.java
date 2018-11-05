
package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TableStructureGetterSetter {

    @SerializedName("Table_Structure")
    @Expose
    private List<TableQuery> result = null;

    public List<TableQuery> getResult() {
        return result;
    }

    public void setResult(List<TableQuery> result) {
        this.result = result;
    }

}
