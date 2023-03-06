package com.rpos.pos.data.remote.dto.purchase.add;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddPurchaseResponse {

    @SerializedName("message")
    @Expose
    private AddPurchaseMessage message;
    @SerializedName("_server_messages")
    @Expose
    private String serverMessages;

    public AddPurchaseMessage getMessage() {
        return message;
    }

    public void setMessage(AddPurchaseMessage message) {
        this.message = message;
    }

    public String getServerMessages() {
        return serverMessages;
    }

    public void setServerMessages(String serverMessages) {
        this.serverMessages = serverMessages;
    }

}
