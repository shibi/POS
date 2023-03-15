
package com.rpos.pos.data.remote.dto.uom.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddUomResponse {

    @SerializedName("message")
    @Expose
    private AddUomMessage message;

    public AddUomMessage getMessage() {
        return message;
    }

    public void setMessage(AddUomMessage message) {
        this.message = message;
    }

}
