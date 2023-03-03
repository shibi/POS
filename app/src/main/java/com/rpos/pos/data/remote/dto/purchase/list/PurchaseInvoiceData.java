
package com.rpos.pos.data.remote.dto.purchase.list;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PurchaseInvoiceData {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("creation")
    @Expose
    private String creation;
    @SerializedName("modified")
    @Expose
    private String modified;
    @SerializedName("modified_by")
    @Expose
    private String modifiedBy;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("docstatus")
    @Expose
    private Integer docstatus;
    @SerializedName("parent")
    @Expose
    private Object parent;
    @SerializedName("parentfield")
    @Expose
    private Object parentfield;
    @SerializedName("parenttype")
    @Expose
    private Object parenttype;
    @SerializedName("idx")
    @Expose
    private Integer idx;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("naming_series")
    @Expose
    private String namingSeries;
    @SerializedName("supplier")
    @Expose
    private String supplier;
    @SerializedName("supplier_name")
    @Expose
    private String supplierName;
    @SerializedName("tax_id")
    @Expose
    private String taxId;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("tax_withholding_category")
    @Expose
    private Object taxWithholdingCategory;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("posting_date")
    @Expose
    private String postingDate;
    @SerializedName("posting_time")
    @Expose
    private String postingTime;
    @SerializedName("set_posting_time")
    @Expose
    private Integer setPostingTime;
    @SerializedName("is_paid")
    @Expose
    private Integer isPaid;
    @SerializedName("is_return")
    @Expose
    private Integer isReturn;
    @SerializedName("apply_tds")
    @Expose
    private Integer applyTds;
    @SerializedName("amended_from")
    @Expose
    private Object amendedFrom;
    @SerializedName("cost_center")
    @Expose
    private Object costCenter;
    @SerializedName("project")
    @Expose
    private Object project;
    @SerializedName("bill_no")
    @Expose
    private Object billNo;
    @SerializedName("bill_date")
    @Expose
    private Object billDate;
    @SerializedName("return_against")
    @Expose
    private Object returnAgainst;
    @SerializedName("supplier_address")
    @Expose
    private Object supplierAddress;
    @SerializedName("address_display")
    @Expose
    private Object addressDisplay;
    @SerializedName("contact_person")
    @Expose
    private Object contactPerson;
    @SerializedName("contact_display")
    @Expose
    private Object contactDisplay;
    @SerializedName("contact_mobile")
    @Expose
    private Object contactMobile;
    @SerializedName("contact_email")
    @Expose
    private Object contactEmail;
    @SerializedName("shipping_address")
    @Expose
    private Object shippingAddress;
    @SerializedName("shipping_address_display")
    @Expose
    private Object shippingAddressDisplay;
    @SerializedName("billing_address")
    @Expose
    private Object billingAddress;
    @SerializedName("billing_address_display")
    @Expose
    private Object billingAddressDisplay;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("conversion_rate")
    @Expose
    private Float conversionRate;
    @SerializedName("buying_price_list")
    @Expose
    private String buyingPriceList;
    @SerializedName("price_list_currency")
    @Expose
    private String priceListCurrency;
    @SerializedName("plc_conversion_rate")
    @Expose
    private Float plcConversionRate;
    @SerializedName("ignore_pricing_rule")
    @Expose
    private Integer ignorePricingRule;
    @SerializedName("set_warehouse")
    @Expose
    private Object setWarehouse;
    @SerializedName("rejected_warehouse")
    @Expose
    private Object rejectedWarehouse;
    @SerializedName("set_from_warehouse")
    @Expose
    private Object setFromWarehouse;
    @SerializedName("supplier_warehouse")
    @Expose
    private Object supplierWarehouse;
    @SerializedName("is_subcontracted")
    @Expose
    private String isSubcontracted;
    @SerializedName("update_stock")
    @Expose
    private Integer updateStock;
    @SerializedName("scan_barcode")
    @Expose
    private Object scanBarcode;
    @SerializedName("total_qty")
    @Expose
    private Float totalQty;
    @SerializedName("base_total")
    @Expose
    private Float baseTotal;
    @SerializedName("base_net_total")
    @Expose
    private Float baseNetTotal;
    @SerializedName("total_net_weight")
    @Expose
    private Float totalNetWeight;
    @SerializedName("total")
    @Expose
    private Float total;
    @SerializedName("net_total")
    @Expose
    private Float netTotal;
    @SerializedName("tax_category")
    @Expose
    private String taxCategory;
    @SerializedName("shipping_rule")
    @Expose
    private Object shippingRule;
    @SerializedName("taxes_and_charges")
    @Expose
    private Object taxesAndCharges;
    @SerializedName("other_charges_calculation")
    @Expose
    private Object otherChargesCalculation;
    @SerializedName("base_taxes_and_charges_added")
    @Expose
    private Float baseTaxesAndChargesAdded;
    @SerializedName("base_taxes_and_charges_deducted")
    @Expose
    private Float baseTaxesAndChargesDeducted;
    @SerializedName("base_total_taxes_and_charges")
    @Expose
    private Float baseTotalTaxesAndCharges;
    @SerializedName("taxes_and_charges_added")
    @Expose
    private Float taxesAndChargesAdded;
    @SerializedName("taxes_and_charges_deducted")
    @Expose
    private Float taxesAndChargesDeducted;
    @SerializedName("total_taxes_and_charges")
    @Expose
    private Float totalTaxesAndCharges;
    @SerializedName("apply_discount_on")
    @Expose
    private String applyDiscountOn;
    @SerializedName("base_discount_amount")
    @Expose
    private Float baseDiscountAmount;
    @SerializedName("additional_discount_account")
    @Expose
    private Object additionalDiscountAccount;
    @SerializedName("additional_discount_percentage")
    @Expose
    private Float additionalDiscountPercentage;
    @SerializedName("discount_amount")
    @Expose
    private Float discountAmount;
    @SerializedName("base_grand_total")
    @Expose
    private Float baseGrandTotal;
    @SerializedName("base_rounding_adjustment")
    @Expose
    private Float baseRoundingAdjustment;
    @SerializedName("base_rounded_total")
    @Expose
    private Float baseRoundedTotal;
    @SerializedName("base_in_words")
    @Expose
    private String baseInWords;
    @SerializedName("grand_total")
    @Expose
    private Float grandTotal;
    @SerializedName("rounding_adjustment")
    @Expose
    private Float roundingAdjustment;
    @SerializedName("rounded_total")
    @Expose
    private Float roundedTotal;
    @SerializedName("in_words")
    @Expose
    private String inWords;
    @SerializedName("total_advance")
    @Expose
    private Float totalAdvance;
    @SerializedName("outstanding_amount")
    @Expose
    private Float outstandingAmount;
    @SerializedName("disable_rounded_total")
    @Expose
    private Integer disableRoundedTotal;
    @SerializedName("mode_of_payment")
    @Expose
    private Object modeOfPayment;
    @SerializedName("cash_bank_account")
    @Expose
    private Object cashBankAccount;
    @SerializedName("clearance_date")
    @Expose
    private Object clearanceDate;
    @SerializedName("paid_amount")
    @Expose
    private Float paidAmount;
    @SerializedName("base_paid_amount")
    @Expose
    private Float basePaidAmount;
    @SerializedName("write_off_amount")
    @Expose
    private Float writeOffAmount;
    @SerializedName("base_write_off_amount")
    @Expose
    private Float baseWriteOffAmount;
    @SerializedName("write_off_account")
    @Expose
    private Object writeOffAccount;
    @SerializedName("write_off_cost_center")
    @Expose
    private Object writeOffCostCenter;
    @SerializedName("allocate_advances_automatically")
    @Expose
    private Integer allocateAdvancesAutomatically;
    @SerializedName("payment_terms_template")
    @Expose
    private Object paymentTermsTemplate;
    @SerializedName("ignore_default_payment_terms_template")
    @Expose
    private Integer ignoreDefaultPaymentTermsTemplate;
    @SerializedName("tc_name")
    @Expose
    private Object tcName;
    @SerializedName("terms")
    @Expose
    private Object terms;
    @SerializedName("letter_head")
    @Expose
    private Object letterHead;
    @SerializedName("select_print_heading")
    @Expose
    private Object selectPrintHeading;
    @SerializedName("group_same_items")
    @Expose
    private Integer groupSameItems;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("on_hold")
    @Expose
    private Integer onHold;
    @SerializedName("release_date")
    @Expose
    private Object releaseDate;
    @SerializedName("hold_comment")
    @Expose
    private Object holdComment;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("inter_company_invoice_reference")
    @Expose
    private Object interCompanyInvoiceReference;
    @SerializedName("represents_company")
    @Expose
    private String representsCompany;
    @SerializedName("is_internal_supplier")
    @Expose
    private Integer isInternalSupplier;
    @SerializedName("credit_to")
    @Expose
    private String creditTo;
    @SerializedName("party_account_currency")
    @Expose
    private String partyAccountCurrency;
    @SerializedName("is_opening")
    @Expose
    private String isOpening;
    @SerializedName("against_expense_account")
    @Expose
    private String againstExpenseAccount;
    @SerializedName("unrealized_profit_loss_account")
    @Expose
    private Object unrealizedProfitLossAccount;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("from_date")
    @Expose
    private Object fromDate;
    @SerializedName("to_date")
    @Expose
    private Object toDate;
    @SerializedName("auto_repeat")
    @Expose
    private Object autoRepeat;
    @SerializedName("per_received")
    @Expose
    private Float perReceived;
    @SerializedName("_user_tags")
    @Expose
    private Object userTags;
    @SerializedName("_comments")
    @Expose
    private Object comments;
    @SerializedName("_assign")
    @Expose
    private Object assign;
    @SerializedName("_liked_by")
    @Expose
    private Object likedBy;
    @SerializedName("supplier_name_in_arabic")
    @Expose
    private Object supplierNameInArabic;
    @SerializedName("company_trn")
    @Expose
    private Object companyTrn;
    @SerializedName("permit_no")
    @Expose
    private Object permitNo;
    @SerializedName("recoverable_standard_rated_expenses")
    @Expose
    private Float recoverableStandardRatedExpenses;
    @SerializedName("reverse_charge")
    @Expose
    private String reverseCharge;
    @SerializedName("recoverable_reverse_charge")
    @Expose
    private Float recoverableReverseCharge;
    @SerializedName("user")
    @Expose
    private String user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getDocstatus() {
        return docstatus;
    }

    public void setDocstatus(Integer docstatus) {
        this.docstatus = docstatus;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public Object getParentfield() {
        return parentfield;
    }

    public void setParentfield(Object parentfield) {
        this.parentfield = parentfield;
    }

    public Object getParenttype() {
        return parenttype;
    }

    public void setParenttype(Object parenttype) {
        this.parenttype = parenttype;
    }

    public Integer getIdx() {
        return idx;
    }

    public void setIdx(Integer idx) {
        this.idx = idx;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNamingSeries() {
        return namingSeries;
    }

    public void setNamingSeries(String namingSeries) {
        this.namingSeries = namingSeries;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Object getTaxWithholdingCategory() {
        return taxWithholdingCategory;
    }

    public void setTaxWithholdingCategory(Object taxWithholdingCategory) {
        this.taxWithholdingCategory = taxWithholdingCategory;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(String postingTime) {
        this.postingTime = postingTime;
    }

    public Integer getSetPostingTime() {
        return setPostingTime;
    }

    public void setSetPostingTime(Integer setPostingTime) {
        this.setPostingTime = setPostingTime;
    }

    public Integer getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Integer isPaid) {
        this.isPaid = isPaid;
    }

    public Integer getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(Integer isReturn) {
        this.isReturn = isReturn;
    }

    public Integer getApplyTds() {
        return applyTds;
    }

    public void setApplyTds(Integer applyTds) {
        this.applyTds = applyTds;
    }

    public Object getAmendedFrom() {
        return amendedFrom;
    }

    public void setAmendedFrom(Object amendedFrom) {
        this.amendedFrom = amendedFrom;
    }

    public Object getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(Object costCenter) {
        this.costCenter = costCenter;
    }

    public Object getProject() {
        return project;
    }

    public void setProject(Object project) {
        this.project = project;
    }

    public Object getBillNo() {
        return billNo;
    }

    public void setBillNo(Object billNo) {
        this.billNo = billNo;
    }

    public Object getBillDate() {
        return billDate;
    }

    public void setBillDate(Object billDate) {
        this.billDate = billDate;
    }

    public Object getReturnAgainst() {
        return returnAgainst;
    }

    public void setReturnAgainst(Object returnAgainst) {
        this.returnAgainst = returnAgainst;
    }

    public Object getSupplierAddress() {
        return supplierAddress;
    }

    public void setSupplierAddress(Object supplierAddress) {
        this.supplierAddress = supplierAddress;
    }

    public Object getAddressDisplay() {
        return addressDisplay;
    }

    public void setAddressDisplay(Object addressDisplay) {
        this.addressDisplay = addressDisplay;
    }

    public Object getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Object contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Object getContactDisplay() {
        return contactDisplay;
    }

    public void setContactDisplay(Object contactDisplay) {
        this.contactDisplay = contactDisplay;
    }

    public Object getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(Object contactMobile) {
        this.contactMobile = contactMobile;
    }

    public Object getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(Object contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Object getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Object shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Object getShippingAddressDisplay() {
        return shippingAddressDisplay;
    }

    public void setShippingAddressDisplay(Object shippingAddressDisplay) {
        this.shippingAddressDisplay = shippingAddressDisplay;
    }

    public Object getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Object billingAddress) {
        this.billingAddress = billingAddress;
    }

    public Object getBillingAddressDisplay() {
        return billingAddressDisplay;
    }

    public void setBillingAddressDisplay(Object billingAddressDisplay) {
        this.billingAddressDisplay = billingAddressDisplay;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Float getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(Float conversionRate) {
        this.conversionRate = conversionRate;
    }

    public String getBuyingPriceList() {
        return buyingPriceList;
    }

    public void setBuyingPriceList(String buyingPriceList) {
        this.buyingPriceList = buyingPriceList;
    }

    public String getPriceListCurrency() {
        return priceListCurrency;
    }

    public void setPriceListCurrency(String priceListCurrency) {
        this.priceListCurrency = priceListCurrency;
    }

    public Float getPlcConversionRate() {
        return plcConversionRate;
    }

    public void setPlcConversionRate(Float plcConversionRate) {
        this.plcConversionRate = plcConversionRate;
    }

    public Integer getIgnorePricingRule() {
        return ignorePricingRule;
    }

    public void setIgnorePricingRule(Integer ignorePricingRule) {
        this.ignorePricingRule = ignorePricingRule;
    }

    public Object getSetWarehouse() {
        return setWarehouse;
    }

    public void setSetWarehouse(Object setWarehouse) {
        this.setWarehouse = setWarehouse;
    }

    public Object getRejectedWarehouse() {
        return rejectedWarehouse;
    }

    public void setRejectedWarehouse(Object rejectedWarehouse) {
        this.rejectedWarehouse = rejectedWarehouse;
    }

    public Object getSetFromWarehouse() {
        return setFromWarehouse;
    }

    public void setSetFromWarehouse(Object setFromWarehouse) {
        this.setFromWarehouse = setFromWarehouse;
    }

    public Object getSupplierWarehouse() {
        return supplierWarehouse;
    }

    public void setSupplierWarehouse(Object supplierWarehouse) {
        this.supplierWarehouse = supplierWarehouse;
    }

    public String getIsSubcontracted() {
        return isSubcontracted;
    }

    public void setIsSubcontracted(String isSubcontracted) {
        this.isSubcontracted = isSubcontracted;
    }

    public Integer getUpdateStock() {
        return updateStock;
    }

    public void setUpdateStock(Integer updateStock) {
        this.updateStock = updateStock;
    }

    public Object getScanBarcode() {
        return scanBarcode;
    }

    public void setScanBarcode(Object scanBarcode) {
        this.scanBarcode = scanBarcode;
    }

    public Float getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(Float totalQty) {
        this.totalQty = totalQty;
    }

    public Float getBaseTotal() {
        return baseTotal;
    }

    public void setBaseTotal(Float baseTotal) {
        this.baseTotal = baseTotal;
    }

    public Float getBaseNetTotal() {
        return baseNetTotal;
    }

    public void setBaseNetTotal(Float baseNetTotal) {
        this.baseNetTotal = baseNetTotal;
    }

    public Float getTotalNetWeight() {
        return totalNetWeight;
    }

    public void setTotalNetWeight(Float totalNetWeight) {
        this.totalNetWeight = totalNetWeight;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }

    public Float getNetTotal() {
        return netTotal;
    }

    public void setNetTotal(Float netTotal) {
        this.netTotal = netTotal;
    }

    public String getTaxCategory() {
        return taxCategory;
    }

    public void setTaxCategory(String taxCategory) {
        this.taxCategory = taxCategory;
    }

    public Object getShippingRule() {
        return shippingRule;
    }

    public void setShippingRule(Object shippingRule) {
        this.shippingRule = shippingRule;
    }

    public Object getTaxesAndCharges() {
        return taxesAndCharges;
    }

    public void setTaxesAndCharges(Object taxesAndCharges) {
        this.taxesAndCharges = taxesAndCharges;
    }

    public Object getOtherChargesCalculation() {
        return otherChargesCalculation;
    }

    public void setOtherChargesCalculation(Object otherChargesCalculation) {
        this.otherChargesCalculation = otherChargesCalculation;
    }

    public Float getBaseTaxesAndChargesAdded() {
        return baseTaxesAndChargesAdded;
    }

    public void setBaseTaxesAndChargesAdded(Float baseTaxesAndChargesAdded) {
        this.baseTaxesAndChargesAdded = baseTaxesAndChargesAdded;
    }

    public Float getBaseTaxesAndChargesDeducted() {
        return baseTaxesAndChargesDeducted;
    }

    public void setBaseTaxesAndChargesDeducted(Float baseTaxesAndChargesDeducted) {
        this.baseTaxesAndChargesDeducted = baseTaxesAndChargesDeducted;
    }

    public Float getBaseTotalTaxesAndCharges() {
        return baseTotalTaxesAndCharges;
    }

    public void setBaseTotalTaxesAndCharges(Float baseTotalTaxesAndCharges) {
        this.baseTotalTaxesAndCharges = baseTotalTaxesAndCharges;
    }

    public Float getTaxesAndChargesAdded() {
        return taxesAndChargesAdded;
    }

    public void setTaxesAndChargesAdded(Float taxesAndChargesAdded) {
        this.taxesAndChargesAdded = taxesAndChargesAdded;
    }

    public Float getTaxesAndChargesDeducted() {
        return taxesAndChargesDeducted;
    }

    public void setTaxesAndChargesDeducted(Float taxesAndChargesDeducted) {
        this.taxesAndChargesDeducted = taxesAndChargesDeducted;
    }

    public Float getTotalTaxesAndCharges() {
        return totalTaxesAndCharges;
    }

    public void setTotalTaxesAndCharges(Float totalTaxesAndCharges) {
        this.totalTaxesAndCharges = totalTaxesAndCharges;
    }

    public String getApplyDiscountOn() {
        return applyDiscountOn;
    }

    public void setApplyDiscountOn(String applyDiscountOn) {
        this.applyDiscountOn = applyDiscountOn;
    }

    public Float getBaseDiscountAmount() {
        return baseDiscountAmount;
    }

    public void setBaseDiscountAmount(Float baseDiscountAmount) {
        this.baseDiscountAmount = baseDiscountAmount;
    }

    public Object getAdditionalDiscountAccount() {
        return additionalDiscountAccount;
    }

    public void setAdditionalDiscountAccount(Object additionalDiscountAccount) {
        this.additionalDiscountAccount = additionalDiscountAccount;
    }

    public Float getAdditionalDiscountPercentage() {
        return additionalDiscountPercentage;
    }

    public void setAdditionalDiscountPercentage(Float additionalDiscountPercentage) {
        this.additionalDiscountPercentage = additionalDiscountPercentage;
    }

    public Float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Float getBaseGrandTotal() {
        return baseGrandTotal;
    }

    public void setBaseGrandTotal(Float baseGrandTotal) {
        this.baseGrandTotal = baseGrandTotal;
    }

    public Float getBaseRoundingAdjustment() {
        return baseRoundingAdjustment;
    }

    public void setBaseRoundingAdjustment(Float baseRoundingAdjustment) {
        this.baseRoundingAdjustment = baseRoundingAdjustment;
    }

    public Float getBaseRoundedTotal() {
        return baseRoundedTotal;
    }

    public void setBaseRoundedTotal(Float baseRoundedTotal) {
        this.baseRoundedTotal = baseRoundedTotal;
    }

    public String getBaseInWords() {
        return baseInWords;
    }

    public void setBaseInWords(String baseInWords) {
        this.baseInWords = baseInWords;
    }

    public Float getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Float grandTotal) {
        this.grandTotal = grandTotal;
    }

    public Float getRoundingAdjustment() {
        return roundingAdjustment;
    }

    public void setRoundingAdjustment(Float roundingAdjustment) {
        this.roundingAdjustment = roundingAdjustment;
    }

    public Float getRoundedTotal() {
        return roundedTotal;
    }

    public void setRoundedTotal(Float roundedTotal) {
        this.roundedTotal = roundedTotal;
    }

    public String getInWords() {
        return inWords;
    }

    public void setInWords(String inWords) {
        this.inWords = inWords;
    }

    public Float getTotalAdvance() {
        return totalAdvance;
    }

    public void setTotalAdvance(Float totalAdvance) {
        this.totalAdvance = totalAdvance;
    }

    public Float getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(Float outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public Integer getDisableRoundedTotal() {
        return disableRoundedTotal;
    }

    public void setDisableRoundedTotal(Integer disableRoundedTotal) {
        this.disableRoundedTotal = disableRoundedTotal;
    }

    public Object getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(Object modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public Object getCashBankAccount() {
        return cashBankAccount;
    }

    public void setCashBankAccount(Object cashBankAccount) {
        this.cashBankAccount = cashBankAccount;
    }

    public Object getClearanceDate() {
        return clearanceDate;
    }

    public void setClearanceDate(Object clearanceDate) {
        this.clearanceDate = clearanceDate;
    }

    public Float getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Float paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Float getBasePaidAmount() {
        return basePaidAmount;
    }

    public void setBasePaidAmount(Float basePaidAmount) {
        this.basePaidAmount = basePaidAmount;
    }

    public Float getWriteOffAmount() {
        return writeOffAmount;
    }

    public void setWriteOffAmount(Float writeOffAmount) {
        this.writeOffAmount = writeOffAmount;
    }

    public Float getBaseWriteOffAmount() {
        return baseWriteOffAmount;
    }

    public void setBaseWriteOffAmount(Float baseWriteOffAmount) {
        this.baseWriteOffAmount = baseWriteOffAmount;
    }

    public Object getWriteOffAccount() {
        return writeOffAccount;
    }

    public void setWriteOffAccount(Object writeOffAccount) {
        this.writeOffAccount = writeOffAccount;
    }

    public Object getWriteOffCostCenter() {
        return writeOffCostCenter;
    }

    public void setWriteOffCostCenter(Object writeOffCostCenter) {
        this.writeOffCostCenter = writeOffCostCenter;
    }

    public Integer getAllocateAdvancesAutomatically() {
        return allocateAdvancesAutomatically;
    }

    public void setAllocateAdvancesAutomatically(Integer allocateAdvancesAutomatically) {
        this.allocateAdvancesAutomatically = allocateAdvancesAutomatically;
    }

    public Object getPaymentTermsTemplate() {
        return paymentTermsTemplate;
    }

    public void setPaymentTermsTemplate(Object paymentTermsTemplate) {
        this.paymentTermsTemplate = paymentTermsTemplate;
    }

    public Integer getIgnoreDefaultPaymentTermsTemplate() {
        return ignoreDefaultPaymentTermsTemplate;
    }

    public void setIgnoreDefaultPaymentTermsTemplate(Integer ignoreDefaultPaymentTermsTemplate) {
        this.ignoreDefaultPaymentTermsTemplate = ignoreDefaultPaymentTermsTemplate;
    }

    public Object getTcName() {
        return tcName;
    }

    public void setTcName(Object tcName) {
        this.tcName = tcName;
    }

    public Object getTerms() {
        return terms;
    }

    public void setTerms(Object terms) {
        this.terms = terms;
    }

    public Object getLetterHead() {
        return letterHead;
    }

    public void setLetterHead(Object letterHead) {
        this.letterHead = letterHead;
    }

    public Object getSelectPrintHeading() {
        return selectPrintHeading;
    }

    public void setSelectPrintHeading(Object selectPrintHeading) {
        this.selectPrintHeading = selectPrintHeading;
    }

    public Integer getGroupSameItems() {
        return groupSameItems;
    }

    public void setGroupSameItems(Integer groupSameItems) {
        this.groupSameItems = groupSameItems;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getOnHold() {
        return onHold;
    }

    public void setOnHold(Integer onHold) {
        this.onHold = onHold;
    }

    public Object getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Object releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Object getHoldComment() {
        return holdComment;
    }

    public void setHoldComment(Object holdComment) {
        this.holdComment = holdComment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getInterCompanyInvoiceReference() {
        return interCompanyInvoiceReference;
    }

    public void setInterCompanyInvoiceReference(Object interCompanyInvoiceReference) {
        this.interCompanyInvoiceReference = interCompanyInvoiceReference;
    }

    public String getRepresentsCompany() {
        return representsCompany;
    }

    public void setRepresentsCompany(String representsCompany) {
        this.representsCompany = representsCompany;
    }

    public Integer getIsInternalSupplier() {
        return isInternalSupplier;
    }

    public void setIsInternalSupplier(Integer isInternalSupplier) {
        this.isInternalSupplier = isInternalSupplier;
    }

    public String getCreditTo() {
        return creditTo;
    }

    public void setCreditTo(String creditTo) {
        this.creditTo = creditTo;
    }

    public String getPartyAccountCurrency() {
        return partyAccountCurrency;
    }

    public void setPartyAccountCurrency(String partyAccountCurrency) {
        this.partyAccountCurrency = partyAccountCurrency;
    }

    public String getIsOpening() {
        return isOpening;
    }

    public void setIsOpening(String isOpening) {
        this.isOpening = isOpening;
    }

    public String getAgainstExpenseAccount() {
        return againstExpenseAccount;
    }

    public void setAgainstExpenseAccount(String againstExpenseAccount) {
        this.againstExpenseAccount = againstExpenseAccount;
    }

    public Object getUnrealizedProfitLossAccount() {
        return unrealizedProfitLossAccount;
    }

    public void setUnrealizedProfitLossAccount(Object unrealizedProfitLossAccount) {
        this.unrealizedProfitLossAccount = unrealizedProfitLossAccount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Object getFromDate() {
        return fromDate;
    }

    public void setFromDate(Object fromDate) {
        this.fromDate = fromDate;
    }

    public Object getToDate() {
        return toDate;
    }

    public void setToDate(Object toDate) {
        this.toDate = toDate;
    }

    public Object getAutoRepeat() {
        return autoRepeat;
    }

    public void setAutoRepeat(Object autoRepeat) {
        this.autoRepeat = autoRepeat;
    }

    public Float getPerReceived() {
        return perReceived;
    }

    public void setPerReceived(Float perReceived) {
        this.perReceived = perReceived;
    }

    public Object getUserTags() {
        return userTags;
    }

    public void setUserTags(Object userTags) {
        this.userTags = userTags;
    }

    public Object getComments() {
        return comments;
    }

    public void setComments(Object comments) {
        this.comments = comments;
    }

    public Object getAssign() {
        return assign;
    }

    public void setAssign(Object assign) {
        this.assign = assign;
    }

    public Object getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Object likedBy) {
        this.likedBy = likedBy;
    }

    public Object getSupplierNameInArabic() {
        return supplierNameInArabic;
    }

    public void setSupplierNameInArabic(Object supplierNameInArabic) {
        this.supplierNameInArabic = supplierNameInArabic;
    }

    public Object getCompanyTrn() {
        return companyTrn;
    }

    public void setCompanyTrn(Object companyTrn) {
        this.companyTrn = companyTrn;
    }

    public Object getPermitNo() {
        return permitNo;
    }

    public void setPermitNo(Object permitNo) {
        this.permitNo = permitNo;
    }

    public Float getRecoverableStandardRatedExpenses() {
        return recoverableStandardRatedExpenses;
    }

    public void setRecoverableStandardRatedExpenses(Float recoverableStandardRatedExpenses) {
        this.recoverableStandardRatedExpenses = recoverableStandardRatedExpenses;
    }

    public String getReverseCharge() {
        return reverseCharge;
    }

    public void setReverseCharge(String reverseCharge) {
        this.reverseCharge = reverseCharge;
    }

    public Float getRecoverableReverseCharge() {
        return recoverableReverseCharge;
    }

    public void setRecoverableReverseCharge(Float recoverableReverseCharge) {
        this.recoverableReverseCharge = recoverableReverseCharge;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
