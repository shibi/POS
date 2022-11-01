
package com.rpos.pos.data.remote.dto.suppliers.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SuppliersData {

    @SerializedName("supplier_id")
    @Expose
    private String supplierId;
    @SerializedName("supplier_name")
    @Expose
    private String supplierName;
    @SerializedName("tax_id")
    @Expose
    private String taxId;

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

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
