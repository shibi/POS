package com.rpos.pos.data.remote.dto.royalty;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RoyaltyProgramListResponse {

    @SerializedName("message")
    @Expose
    private List<RoyaltyProgram> message = null;

    public List<RoyaltyProgram> getMessage() {
        return message;
    }

    public void setMessage(List<RoyaltyProgram> message) {
        this.message = message;
    }

}
