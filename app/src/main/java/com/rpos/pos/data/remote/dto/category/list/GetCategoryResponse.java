
package com.rpos.pos.data.remote.dto.category.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class GetCategoryResponse {

    @SerializedName("message")
    @Expose
    private List<CategoryItem> message = null;

    public List<CategoryItem> getMessage() {
        return message;
    }

    public void setMessage(List<CategoryItem> message) {
        this.message = message;
    }

}
