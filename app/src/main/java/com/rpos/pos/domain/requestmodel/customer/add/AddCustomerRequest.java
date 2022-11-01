
package com.rpos.pos.domain.requestmodel.customer.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddCustomerRequest {

    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("tax_id")
    @Expose
    private String taxId;
    @SerializedName("credit_limit")
    @Expose
    private Integer creditLimit;
    @SerializedName("credit_days")
    @Expose
    private Integer creditDays;
    @SerializedName("loyalty_program")
    @Expose
    private String loyaltyProgram;
    @SerializedName("mobile_no")
    @Expose
    private String mobileNo;
    @SerializedName("email")
    @Expose
    private String email;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public Integer getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Integer creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    public String getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(String loyaltyProgram) {
        this.loyaltyProgram = loyaltyProgram;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
