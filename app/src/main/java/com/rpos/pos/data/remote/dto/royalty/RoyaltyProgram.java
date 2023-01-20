package com.rpos.pos.data.remote.dto.royalty;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RoyaltyProgram {

    @SerializedName("loyalty_program_id")
    @Expose
    private String loyaltyProgramId;
    @SerializedName("loyalty_program_name")
    @Expose
    private String loyaltyProgramName;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("program_type")
    @Expose
    private String programType;
    @SerializedName("conversion_factor")
    @Expose
    private Float conversionFactor;
    @SerializedName("expense_account")
    @Expose
    private Object expenseAccount;
    @SerializedName("expiry_duration")
    @Expose
    private Integer expiryDuration;
    @SerializedName("tiers")
    @Expose
    private List<RoyaltyTier> tiers = null;

    public String getLoyaltyProgramId() {
        return loyaltyProgramId;
    }

    public void setLoyaltyProgramId(String loyaltyProgramId) {
        this.loyaltyProgramId = loyaltyProgramId;
    }

    public String getLoyaltyProgramName() {
        return loyaltyProgramName;
    }

    public void setLoyaltyProgramName(String loyaltyProgramName) {
        this.loyaltyProgramName = loyaltyProgramName;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public Float getConversionFactor() {
        return conversionFactor;
    }

    public void setConversionFactor(Float conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public Object getExpenseAccount() {
        return expenseAccount;
    }

    public void setExpenseAccount(Object expenseAccount) {
        this.expenseAccount = expenseAccount;
    }

    public Integer getExpiryDuration() {
        return expiryDuration;
    }

    public void setExpiryDuration(Integer expiryDuration) {
        this.expiryDuration = expiryDuration;
    }

    public List<RoyaltyTier> getTiers() {
        return tiers;
    }

    public void setTiers(List<RoyaltyTier> tiers) {
        this.tiers = tiers;
    }

}
