package com.rpos.pos.domain.requestmodel.payment.details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PaymentModeDetailsRequest {

    @SerializedName("payment_mode_id")
    @Expose
    private String paymentModeId;

    public String getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

}
