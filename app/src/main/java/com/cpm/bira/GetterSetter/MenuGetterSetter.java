package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by deepakp on 12/12/2016.
 */

public class MenuGetterSetter implements Serializable{

    @SerializedName("iconName")
    @Expose
    private String iconName;

    @SerializedName("symbol")
    @Expose
    private String symbol;

    @SerializedName("iconImage")
    @Expose
    private int iconImage;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getIconImage() {
        return iconImage;
    }

    public void setIconImage(int iconImage) {
        this.iconImage = iconImage;
    }



}
