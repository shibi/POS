package com.rpos.pos.data.remote.dto.royalty;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RoyaltyTier {

    @SerializedName("tier_name")
    @Expose
    private String tierName;
    @SerializedName("collection_factor")
    @Expose
    private Float collectionFactor;

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public Float getCollectionFactor() {
        return collectionFactor;
    }

    public void setCollectionFactor(Float collectionFactor) {
        this.collectionFactor = collectionFactor;
    }

}
