
package com.rpos.pos.data.remote.dto.customer.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CustomerListResponse {

    @SerializedName("message")
    @Expose
    private List<CustomerData> message = null;

    public List<CustomerData> getMessage() {
        return message;
    }

    public void setMessage(List<CustomerData> message) {
        this.message = message;
    }

}
