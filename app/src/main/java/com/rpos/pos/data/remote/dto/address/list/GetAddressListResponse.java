
package com.rpos.pos.data.remote.dto.address.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAddressListResponse {

    @SerializedName("message")
    @Expose
    private List<AddressListMessage> message = null;

    public List<AddressListMessage> getMessage() {
        return message;
    }

    public void setMessage(List<AddressListMessage> message) {
        this.message = message;
    }

}
