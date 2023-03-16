package com.rpos.pos.data.remote.dto.payment_modes.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PaymentModeListMessage {

    @SerializedName("payment_mode_id")
    @Expose
    private String paymentModeId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("accounts")
    @Expose
    private List<PaymentModeAccount> accounts = null;

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

    public List<PaymentModeAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<PaymentModeAccount> accounts) {
        this.accounts = accounts;
    }

}
