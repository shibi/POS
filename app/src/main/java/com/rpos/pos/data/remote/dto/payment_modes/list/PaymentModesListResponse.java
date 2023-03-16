package com.rpos.pos.data.remote.dto.payment_modes.list;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentModesListResponse {

    @SerializedName("message")
    @Expose
    private List<PaymentModeListMessage> message = null;

    public List<PaymentModeListMessage> getMessage() {
        return message;
    }

    public void setMessage(List<PaymentModeListMessage> message) {
        this.message = message;
    }

}
