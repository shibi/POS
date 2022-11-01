package com.rpos.pos.data.remote.dto.address.list;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AddressListMessage {

    @SerializedName("address_id")
    @Expose
    private String addressId;
    @SerializedName("address_title")
    @Expose
    private String addressTitle;
    @SerializedName("address_line1")
    @Expose
    private String addressLine1;
    @SerializedName("address_line2")
    @Expose
    private String addressLine2;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("links")
    @Expose
    private List<SupplierLink> links = null;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<SupplierLink> getLinks() {
        return links;
    }

    public void setLinks(List<SupplierLink> links) {
        this.links = links;
    }

}
