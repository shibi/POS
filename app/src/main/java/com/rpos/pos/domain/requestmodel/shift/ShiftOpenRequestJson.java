package com.rpos.pos.domain.requestmodel.shift;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ShiftOpenRequestJson {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("opening_cash")
    @Expose
    private Integer openingCash;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getOpeningCash() {
        return openingCash;
    }

    public void setOpeningCash(Integer openingCash) {
        this.openingCash = openingCash;
    }

}
