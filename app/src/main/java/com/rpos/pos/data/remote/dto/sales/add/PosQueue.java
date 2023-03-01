
package com.rpos.pos.data.remote.dto.sales.add;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PosQueue {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("creation")
    @Expose
    private String creation;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("modified_by")
    @Expose
    private String modifiedBy;
    @SerializedName("idx")
    @Expose
    private Integer idx;
    @SerializedName("docstatus")
    @Expose
    private Integer docstatus;
    @SerializedName("naming_series")
    @Expose
    private String namingSeries;
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("sales_invoice")
    @Expose
    private String salesInvoice;
    @SerializedName("doctype")
    @Expose
    private String doctype;
    @SerializedName("items")
    @Expose
    private List<AddSalesItem> items;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreation() {
        return creation;
    }

    public void setCreation(String creation) {
        this.creation = creation;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public Integer getDocstatus() {
        return docstatus;
    }

    public void setDocstatus(Integer docstatus) {
        this.docstatus = docstatus;
    }

    public String getNamingSeries() {
        return namingSeries;
    }

    public void setNamingSeries(String namingSeries) {
        this.namingSeries = namingSeries;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSalesInvoice() {
        return salesInvoice;
    }

    public void setSalesInvoice(String salesInvoice) {
        this.salesInvoice = salesInvoice;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public List<AddSalesItem> getItems() {
        return items;
    }

    public void setItems(List<AddSalesItem> items) {
        this.items = items;
    }

}
