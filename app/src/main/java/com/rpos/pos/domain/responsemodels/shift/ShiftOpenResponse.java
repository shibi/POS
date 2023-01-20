package com.rpos.pos.domain.responsemodels.shift;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShiftOpenResponse {

    @SerializedName("message")
    @Expose
    private ShiftOpenMessage message;

    public ShiftOpenMessage getMessage() {
        return message;
    }

    public void setMessage(ShiftOpenMessage message) {
        this.message = message;
    }

}
