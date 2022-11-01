
package com.rpos.pos.domain.requestmodel.supplier.add;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddSupplierRequest {

    @SerializedName("supplier_name")
    @Expose
    private String supplierName;
    @SerializedName("tax_id")
    @Expose
    private String taxId;

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

}
