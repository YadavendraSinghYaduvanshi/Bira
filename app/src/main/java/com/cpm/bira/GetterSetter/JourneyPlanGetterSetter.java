package com.cpm.bira.GetterSetter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JourneyPlanGetterSetter {
    @SerializedName("Journey_Plan")
    @Expose
    private List<JourneyPlan> journeyPlan = null;

    public List<JourneyPlan> getJourneyPlan() {
        return journeyPlan;
    }

    public void setJourneyPlan(List<JourneyPlan> journeyPlan) {
        this.journeyPlan = journeyPlan;
    }
}
