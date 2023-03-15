package com.rpos.pos.data.remote.dto.tax.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddTaxResponse {

    @SerializedName("message")
    @Expose
    private AddTaxMessage message;

    public AddTaxMessage getMessage() {
        return message;
    }

    public void setMessage(AddTaxMessage message) {
        this.message = message;
    }

}
