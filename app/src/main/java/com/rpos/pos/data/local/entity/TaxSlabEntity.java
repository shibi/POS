package com.rpos.pos.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "taxslabsTable")
public class TaxSlabEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;
    private String taxSlabName;
    private float slabAmount;
    @Ignore
    private boolean isSelected;


    @NonNull
    public Integer getId() {
        return id;
    }

    public void setId(@NonNull Integer id) {
        this.id = id;
    }

    public String getTaxSlabName() {
        return taxSlabName;
    }

    public void setTaxSlabName(String taxSlabName) {
        this.taxSlabName = taxSlabName;
    }

    public float getSlabAmount() {
        return slabAmount;
    }

    public void setSlabAmount(float slabAmount) {
        this.slabAmount = slabAmount;
    }

    @Ignore
    public boolean isSelected() {
        return isSelected;
    }

    @Ignore
    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
