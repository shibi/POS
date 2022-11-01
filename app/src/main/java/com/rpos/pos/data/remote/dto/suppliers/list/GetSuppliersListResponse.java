
package com.rpos.pos.data.remote.dto.suppliers.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetSuppliersListResponse {

    @SerializedName("message")
    @Expose
    private List<SuppliersData> message = null;

    public List<SuppliersData> getMessage() {
        return message;
    }

    public void setMessage(List<SuppliersData> message) {
        this.message = message;
    }

}
