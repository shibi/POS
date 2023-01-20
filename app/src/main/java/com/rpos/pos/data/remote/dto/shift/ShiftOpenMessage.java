package com.rpos.pos.data.remote.dto.shift;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShiftOpenMessage {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ShiftOpenData data;

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

    public ShiftOpenData getData() {
        return data;
    }

    public void setData(ShiftOpenData data) {
        this.data = data;
    }

}
