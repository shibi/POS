package com.rpos.pos.data.remote.dto.payment_modes.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PaymentModeDetailsResponse {

    @SerializedName("message")
    @Expose
    private PaymentModeMessage message;

    public PaymentModeMessage getMessage() {
        return message;
    }

    public void setMessage(PaymentModeMessage message) {
        this.message = message;
    }

}
