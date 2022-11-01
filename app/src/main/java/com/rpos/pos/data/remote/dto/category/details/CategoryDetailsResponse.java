
package com.rpos.pos.data.remote.dto.category.details;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class CategoryDetailsResponse {

    @SerializedName("message")
    @Expose
    private List<CategoryDetail> message = null;

    public List<CategoryDetail> getMessage() {
        return message;
    }

    public void setMessage(List<CategoryDetail> message) {
        this.message = message;
    }

}
