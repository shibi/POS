package com.rpos.pos.data.remote.dto.tax.edit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EditTaxResponse {

    @SerializedName("message")
    @Expose
    private EditTaxMessage message;

    public EditTaxMessage getMessage() {
        return message;
    }

    public void setMessage(EditTaxMessage message) {
        this.message = message;
    }

}
