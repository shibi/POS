package com.rpos.pos.data.remote.dto.licenceserver;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class LicenceServerLoginResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private LicenceOwnerData data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LicenceOwnerData getData() {
        return data;
    }

    public void setData(LicenceOwnerData data) {
        this.data = data;
    }

}
