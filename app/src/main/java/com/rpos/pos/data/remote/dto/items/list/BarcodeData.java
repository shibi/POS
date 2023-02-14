package com.rpos.pos.data.remote.dto.items.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BarcodeData {

    @SerializedName("barcode")
    @Expose
    private String barcode;

}
