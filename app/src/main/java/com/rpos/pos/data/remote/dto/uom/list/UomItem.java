package com.rpos.pos.data.remote.dto.uom.list;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@Entity(tableName = "uomtable")
public class UomItem {

    @PrimaryKey
    @NonNull
    @SerializedName("uom_id")
    @Expose
    private Integer uomId;
    @SerializedName("uom_name")
    @Expose
    private String uomName;

    public Integer getUomId() {
        return uomId;
    }

    public void setUomId(Integer uomId) {
        this.uomId = uomId;
    }

    public String getUomName() {
        return uomName;
    }

    public void setUomName(String uomName) {
        this.uomName = uomName;
    }

}
