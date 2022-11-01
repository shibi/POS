package com.rpos.pos.data.remote.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZatcaResponse {

    @SerializedName("to_base64")
    @Expose
    private String zatcaBase64;

    public String getZatcaBase64() {
        return zatcaBase64;
    }

    public void setZatcaBase64(String zatcaBase64) {
        this.zatcaBase64 = zatcaBase64;
    }
}
