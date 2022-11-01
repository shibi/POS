package com.rpos.pos.data.remote.dto.suppliers.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rpos.pos.data.remote.dto.suppliers.list.SuppliersData;


public class AddSupplierMessage {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private SuppliersData data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SuppliersData getData() {
        return data;
    }

    public void setData(SuppliersData data) {
        this.data = data;
    }

}
