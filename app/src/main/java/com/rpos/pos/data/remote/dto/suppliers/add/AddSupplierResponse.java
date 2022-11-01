package com.rpos.pos.data.remote.dto.suppliers.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddSupplierResponse {

    @SerializedName("message")
    @Expose
    private AddSupplierMessage message;

    public AddSupplierMessage getMessage() {
        return message;
    }

    public void setMessage(AddSupplierMessage message) {
        this.message = message;
    }

}
