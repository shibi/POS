package com.rpos.pos.domain.responsemodels.shift;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ShiftOpenData {

    @SerializedName("opening_entry_id")
    @Expose
    private String openingEntryId;

    public String getOpeningEntryId() {
        return openingEntryId;
    }

    public void setOpeningEntryId(String openingEntryId) {
        this.openingEntryId = openingEntryId;
    }

}
