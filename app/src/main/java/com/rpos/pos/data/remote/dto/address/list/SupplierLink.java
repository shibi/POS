package com.rpos.pos.data.remote.dto.address.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SupplierLink {

    @SerializedName("link_document_type")
    @Expose
    private String linkDocumentType;
    @SerializedName("link_document_id")
    @Expose
    private String linkDocumentId;

    public String getLinkDocumentType() {
        return linkDocumentType;
    }

    public void setLinkDocumentType(String linkDocumentType) {
        this.linkDocumentType = linkDocumentType;
    }

    public String getLinkDocumentId() {
        return linkDocumentId;
    }

    public void setLinkDocumentId(String linkDocumentId) {
        this.linkDocumentId = linkDocumentId;
    }

}
