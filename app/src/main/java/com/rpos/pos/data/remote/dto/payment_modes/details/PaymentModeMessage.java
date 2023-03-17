package com.rpos.pos.data.remote.dto.payment_modes.details;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentModeMessage {

    @SerializedName("payment_mode_id")
    @Expose
    private String paymentModeId;

    @SerializedName("success")
    @Expose
    private boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("accounts")
    @Expose
    private List<PaymentAccount> accounts;

    public String getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<PaymentAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<PaymentAccount> accounts) {
        this.accounts = accounts;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
