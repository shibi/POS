
package com.rpos.pos.data.remote.dto.sales.list;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SalesListData {

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
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("tax_id")
    @Expose
    private Object taxId;
    @SerializedName("pos_profile")
    @Expose
    private String posProfile;
    @SerializedName("is_pos")
    @Expose
    private Integer isPos;
    @SerializedName("is_consolidated")
    @Expose
    private Integer isConsolidated;
    @SerializedName("is_return")
    @Expose
    private Integer isReturn;
    @SerializedName("is_debit_note")
    @Expose
    private Integer isDebitNote;
    @SerializedName("update_billed_amount_in_sales_order")
    @Expose
    private Integer updateBilledAmountInSalesOrder;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("company_tax_id")
    @Expose
    private Object companyTaxId;
    @SerializedName("posting_date")
    @Expose
    private String postingDate;
    @SerializedName("posting_time")
    @Expose
    private String postingTime;
    @SerializedName("set_posting_time")
    @Expose
    private Integer setPostingTime;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("return_against")
    @Expose
    private Object returnAgainst;
    @SerializedName("amended_from")
    @Expose
    private Object amendedFrom;
    @SerializedName("project")
    @Expose
    private Object project;
    @SerializedName("cost_center")
    @Expose
    private Object costCenter;
    @SerializedName("po_no")
    @Expose
    private String poNo;
    @SerializedName("po_date")
    @Expose
    private Object poDate;
    @SerializedName("customer_address")
    @Expose
    private Object customerAddress;
    @SerializedName("address_display")
    @Expose
    private Object addressDisplay;
    @SerializedName("contact_person")
    @Expose
    private String contactPerson;
    @SerializedName("contact_display")
    @Expose
    private String contactDisplay;
    @SerializedName("contact_mobile")
    @Expose
    private String contactMobile;
    @SerializedName("contact_email")
    @Expose
    private String contactEmail;
    @SerializedName("territory")
    @Expose
    private String territory;
    @SerializedName("shipping_address_name")
    @Expose
    private String shippingAddressName;
    @SerializedName("shipping_address")
    @Expose
    private Object shippingAddress;
    @SerializedName("company_address")
    @Expose
    private Object companyAddress;
    @SerializedName("company_address_display")
    @Expose
    private Object companyAddressDisplay;
    @SerializedName("dispatch_address_name")
    @Expose
    private Object dispatchAddressName;
    @SerializedName("dispatch_address")
    @Expose
    private Object dispatchAddress;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("conversion_rate")
    @Expose
    private Float conversionRate;
    @SerializedName("selling_price_list")
    @Expose
    private String sellingPriceList;
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
    @SerializedName("set_target_warehouse")
    @Expose
    private Object setTargetWarehouse;
    @SerializedName("update_stock")
    @Expose
    private Integer updateStock;
    @SerializedName("scan_barcode")
    @Expose
    private Object scanBarcode;
    @SerializedName("total_billing_amount")
    @Expose
    private Float totalBillingAmount;
    @SerializedName("total_billing_hours")
    @Expose
    private Float totalBillingHours;
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
    @SerializedName("taxes_and_charges")
    @Expose
    private Object taxesAndCharges;
    @SerializedName("shipping_rule")
    @Expose
    private Object shippingRule;
    @SerializedName("tax_category")
    @Expose
    private String taxCategory;
    @SerializedName("other_charges_calculation")
    @Expose
    private Object otherChargesCalculation;
    @SerializedName("base_total_taxes_and_charges")
    @Expose
    private Float baseTotalTaxesAndCharges;
    @SerializedName("total_taxes_and_charges")
    @Expose
    private Float totalTaxesAndCharges;
    @SerializedName("loyalty_points")
    @Expose
    private Integer loyaltyPoints;
    @SerializedName("loyalty_amount")
    @Expose
    private Float loyaltyAmount;
    @SerializedName("redeem_loyalty_points")
    @Expose
    private Integer redeemLoyaltyPoints;
    @SerializedName("loyalty_program")
    @Expose
    private String loyaltyProgram;
    @SerializedName("loyalty_redemption_account")
    @Expose
    private Object loyaltyRedemptionAccount;
    @SerializedName("loyalty_redemption_cost_center")
    @Expose
    private Object loyaltyRedemptionCostCenter;
    @SerializedName("apply_discount_on")
    @Expose
    private String applyDiscountOn;
    @SerializedName("is_cash_or_non_trade_discount")
    @Expose
    private Integer isCashOrNonTradeDiscount;
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
    @SerializedName("write_off_amount")
    @Expose
    private Float writeOffAmount;
    @SerializedName("base_write_off_amount")
    @Expose
    private Float baseWriteOffAmount;
    @SerializedName("write_off_outstanding_amount_automatically")
    @Expose
    private Integer writeOffOutstandingAmountAutomatically;
    @SerializedName("write_off_account")
    @Expose
    private String writeOffAccount;
    @SerializedName("write_off_cost_center")
    @Expose
    private String writeOffCostCenter;
    @SerializedName("allocate_advances_automatically")
    @Expose
    private Integer allocateAdvancesAutomatically;
    @SerializedName("ignore_default_payment_terms_template")
    @Expose
    private Integer ignoreDefaultPaymentTermsTemplate;
    @SerializedName("payment_terms_template")
    @Expose
    private String paymentTermsTemplate;
    @SerializedName("cash_bank_account")
    @Expose
    private Object cashBankAccount;
    @SerializedName("base_paid_amount")
    @Expose
    private Float basePaidAmount;
    @SerializedName("paid_amount")
    @Expose
    private Float paidAmount;
    @SerializedName("base_change_amount")
    @Expose
    private Float baseChangeAmount;
    @SerializedName("change_amount")
    @Expose
    private Float changeAmount;
    @SerializedName("account_for_change_amount")
    @Expose
    private String accountForChangeAmount;
    @SerializedName("tc_name")
    @Expose
    private Object tcName;
    @SerializedName("terms")
    @Expose
    private Object terms;
    @SerializedName("letter_head")
    @Expose
    private Object letterHead;
    @SerializedName("group_same_items")
    @Expose
    private Integer groupSameItems;
    @SerializedName("select_print_heading")
    @Expose
    private Object selectPrintHeading;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("inter_company_invoice_reference")
    @Expose
    private Object interCompanyInvoiceReference;
    @SerializedName("represents_company")
    @Expose
    private Object representsCompany;
    @SerializedName("customer_group")
    @Expose
    private String customerGroup;
    @SerializedName("campaign")
    @Expose
    private Object campaign;
    @SerializedName("is_internal_customer")
    @Expose
    private Integer isInternalCustomer;
    @SerializedName("is_discounted")
    @Expose
    private Integer isDiscounted;
    @SerializedName("source")
    @Expose
    private Object source;
    @SerializedName("debit_to")
    @Expose
    private String debitTo;
    @SerializedName("party_account_currency")
    @Expose
    private String partyAccountCurrency;
    @SerializedName("is_opening")
    @Expose
    private String isOpening;
    @SerializedName("c_form_applicable")
    @Expose
    private String cFormApplicable;
    @SerializedName("c_form_no")
    @Expose
    private Object cFormNo;
    @SerializedName("unrealized_profit_loss_account")
    @Expose
    private Object unrealizedProfitLossAccount;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("sales_partner")
    @Expose
    private Object salesPartner;
    @SerializedName("amount_eligible_for_commission")
    @Expose
    private Float amountEligibleForCommission;
    @SerializedName("commission_rate")
    @Expose
    private Float commissionRate;
    @SerializedName("total_commission")
    @Expose
    private Float totalCommission;
    @SerializedName("from_date")
    @Expose
    private Object fromDate;
    @SerializedName("to_date")
    @Expose
    private Object toDate;
    @SerializedName("auto_repeat")
    @Expose
    private Object autoRepeat;
    @SerializedName("against_income_account")
    @Expose
    private String againstIncomeAccount;
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
    @SerializedName("_seen")
    @Expose
    private String seen;
    @SerializedName("pos_queue")
    @Expose
    private String posQueue;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("customer_name_in_arabic")
    @Expose
    private Object customerNameInArabic;
    @SerializedName("company_trn")
    @Expose
    private Object companyTrn;
    @SerializedName("permit_no")
    @Expose
    private Object permitNo;
    @SerializedName("vat_emirate")
    @Expose
    private String vatEmirate;
    @SerializedName("tourist_tax_return")
    @Expose
    private Float touristTaxReturn;
    @SerializedName("ksa_einv_qr")
    @Expose
    private Object ksaEinvQr;

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

    public Object getTaxId() {
        return taxId;
    }

    public void setTaxId(Object taxId) {
        this.taxId = taxId;
    }

    public String getPosProfile() {
        return posProfile;
    }

    public void setPosProfile(String posProfile) {
        this.posProfile = posProfile;
    }

    public Integer getIsPos() {
        return isPos;
    }

    public void setIsPos(Integer isPos) {
        this.isPos = isPos;
    }

    public Integer getIsConsolidated() {
        return isConsolidated;
    }

    public void setIsConsolidated(Integer isConsolidated) {
        this.isConsolidated = isConsolidated;
    }

    public Integer getIsReturn() {
        return isReturn;
    }

    public void setIsReturn(Integer isReturn) {
        this.isReturn = isReturn;
    }

    public Integer getIsDebitNote() {
        return isDebitNote;
    }

    public void setIsDebitNote(Integer isDebitNote) {
        this.isDebitNote = isDebitNote;
    }

    public Integer getUpdateBilledAmountInSalesOrder() {
        return updateBilledAmountInSalesOrder;
    }

    public void setUpdateBilledAmountInSalesOrder(Integer updateBilledAmountInSalesOrder) {
        this.updateBilledAmountInSalesOrder = updateBilledAmountInSalesOrder;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Object getCompanyTaxId() {
        return companyTaxId;
    }

    public void setCompanyTaxId(Object companyTaxId) {
        this.companyTaxId = companyTaxId;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public Object getReturnAgainst() {
        return returnAgainst;
    }

    public void setReturnAgainst(Object returnAgainst) {
        this.returnAgainst = returnAgainst;
    }

    public Object getAmendedFrom() {
        return amendedFrom;
    }

    public void setAmendedFrom(Object amendedFrom) {
        this.amendedFrom = amendedFrom;
    }

    public Object getProject() {
        return project;
    }

    public void setProject(Object project) {
        this.project = project;
    }

    public Object getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(Object costCenter) {
        this.costCenter = costCenter;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public Object getPoDate() {
        return poDate;
    }

    public void setPoDate(Object poDate) {
        this.poDate = poDate;
    }

    public Object getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(Object customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Object getAddressDisplay() {
        return addressDisplay;
    }

    public void setAddressDisplay(Object addressDisplay) {
        this.addressDisplay = addressDisplay;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactDisplay() {
        return contactDisplay;
    }

    public void setContactDisplay(String contactDisplay) {
        this.contactDisplay = contactDisplay;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
    }

    public String getShippingAddressName() {
        return shippingAddressName;
    }

    public void setShippingAddressName(String shippingAddressName) {
        this.shippingAddressName = shippingAddressName;
    }

    public Object getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Object shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Object getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(Object companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Object getCompanyAddressDisplay() {
        return companyAddressDisplay;
    }

    public void setCompanyAddressDisplay(Object companyAddressDisplay) {
        this.companyAddressDisplay = companyAddressDisplay;
    }

    public Object getDispatchAddressName() {
        return dispatchAddressName;
    }

    public void setDispatchAddressName(Object dispatchAddressName) {
        this.dispatchAddressName = dispatchAddressName;
    }

    public Object getDispatchAddress() {
        return dispatchAddress;
    }

    public void setDispatchAddress(Object dispatchAddress) {
        this.dispatchAddress = dispatchAddress;
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

    public String getSellingPriceList() {
        return sellingPriceList;
    }

    public void setSellingPriceList(String sellingPriceList) {
        this.sellingPriceList = sellingPriceList;
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

    public Object getSetTargetWarehouse() {
        return setTargetWarehouse;
    }

    public void setSetTargetWarehouse(Object setTargetWarehouse) {
        this.setTargetWarehouse = setTargetWarehouse;
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

    public Float getTotalBillingAmount() {
        return totalBillingAmount;
    }

    public void setTotalBillingAmount(Float totalBillingAmount) {
        this.totalBillingAmount = totalBillingAmount;
    }

    public Float getTotalBillingHours() {
        return totalBillingHours;
    }

    public void setTotalBillingHours(Float totalBillingHours) {
        this.totalBillingHours = totalBillingHours;
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

    public Object getTaxesAndCharges() {
        return taxesAndCharges;
    }

    public void setTaxesAndCharges(Object taxesAndCharges) {
        this.taxesAndCharges = taxesAndCharges;
    }

    public Object getShippingRule() {
        return shippingRule;
    }

    public void setShippingRule(Object shippingRule) {
        this.shippingRule = shippingRule;
    }

    public String getTaxCategory() {
        return taxCategory;
    }

    public void setTaxCategory(String taxCategory) {
        this.taxCategory = taxCategory;
    }

    public Object getOtherChargesCalculation() {
        return otherChargesCalculation;
    }

    public void setOtherChargesCalculation(Object otherChargesCalculation) {
        this.otherChargesCalculation = otherChargesCalculation;
    }

    public Float getBaseTotalTaxesAndCharges() {
        return baseTotalTaxesAndCharges;
    }

    public void setBaseTotalTaxesAndCharges(Float baseTotalTaxesAndCharges) {
        this.baseTotalTaxesAndCharges = baseTotalTaxesAndCharges;
    }

    public Float getTotalTaxesAndCharges() {
        return totalTaxesAndCharges;
    }

    public void setTotalTaxesAndCharges(Float totalTaxesAndCharges) {
        this.totalTaxesAndCharges = totalTaxesAndCharges;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Float getLoyaltyAmount() {
        return loyaltyAmount;
    }

    public void setLoyaltyAmount(Float loyaltyAmount) {
        this.loyaltyAmount = loyaltyAmount;
    }

    public Integer getRedeemLoyaltyPoints() {
        return redeemLoyaltyPoints;
    }

    public void setRedeemLoyaltyPoints(Integer redeemLoyaltyPoints) {
        this.redeemLoyaltyPoints = redeemLoyaltyPoints;
    }

    public String getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(String loyaltyProgram) {
        this.loyaltyProgram = loyaltyProgram;
    }

    public Object getLoyaltyRedemptionAccount() {
        return loyaltyRedemptionAccount;
    }

    public void setLoyaltyRedemptionAccount(Object loyaltyRedemptionAccount) {
        this.loyaltyRedemptionAccount = loyaltyRedemptionAccount;
    }

    public Object getLoyaltyRedemptionCostCenter() {
        return loyaltyRedemptionCostCenter;
    }

    public void setLoyaltyRedemptionCostCenter(Object loyaltyRedemptionCostCenter) {
        this.loyaltyRedemptionCostCenter = loyaltyRedemptionCostCenter;
    }

    public String getApplyDiscountOn() {
        return applyDiscountOn;
    }

    public void setApplyDiscountOn(String applyDiscountOn) {
        this.applyDiscountOn = applyDiscountOn;
    }

    public Integer getIsCashOrNonTradeDiscount() {
        return isCashOrNonTradeDiscount;
    }

    public void setIsCashOrNonTradeDiscount(Integer isCashOrNonTradeDiscount) {
        this.isCashOrNonTradeDiscount = isCashOrNonTradeDiscount;
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

    public Integer getWriteOffOutstandingAmountAutomatically() {
        return writeOffOutstandingAmountAutomatically;
    }

    public void setWriteOffOutstandingAmountAutomatically(Integer writeOffOutstandingAmountAutomatically) {
        this.writeOffOutstandingAmountAutomatically = writeOffOutstandingAmountAutomatically;
    }

    public String getWriteOffAccount() {
        return writeOffAccount;
    }

    public void setWriteOffAccount(String writeOffAccount) {
        this.writeOffAccount = writeOffAccount;
    }

    public String getWriteOffCostCenter() {
        return writeOffCostCenter;
    }

    public void setWriteOffCostCenter(String writeOffCostCenter) {
        this.writeOffCostCenter = writeOffCostCenter;
    }

    public Integer getAllocateAdvancesAutomatically() {
        return allocateAdvancesAutomatically;
    }

    public void setAllocateAdvancesAutomatically(Integer allocateAdvancesAutomatically) {
        this.allocateAdvancesAutomatically = allocateAdvancesAutomatically;
    }

    public Integer getIgnoreDefaultPaymentTermsTemplate() {
        return ignoreDefaultPaymentTermsTemplate;
    }

    public void setIgnoreDefaultPaymentTermsTemplate(Integer ignoreDefaultPaymentTermsTemplate) {
        this.ignoreDefaultPaymentTermsTemplate = ignoreDefaultPaymentTermsTemplate;
    }

    public String getPaymentTermsTemplate() {
        return paymentTermsTemplate;
    }

    public void setPaymentTermsTemplate(String paymentTermsTemplate) {
        this.paymentTermsTemplate = paymentTermsTemplate;
    }

    public Object getCashBankAccount() {
        return cashBankAccount;
    }

    public void setCashBankAccount(Object cashBankAccount) {
        this.cashBankAccount = cashBankAccount;
    }

    public Float getBasePaidAmount() {
        return basePaidAmount;
    }

    public void setBasePaidAmount(Float basePaidAmount) {
        this.basePaidAmount = basePaidAmount;
    }

    public Float getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Float paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Float getBaseChangeAmount() {
        return baseChangeAmount;
    }

    public void setBaseChangeAmount(Float baseChangeAmount) {
        this.baseChangeAmount = baseChangeAmount;
    }

    public Float getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(Float changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getAccountForChangeAmount() {
        return accountForChangeAmount;
    }

    public void setAccountForChangeAmount(String accountForChangeAmount) {
        this.accountForChangeAmount = accountForChangeAmount;
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

    public Integer getGroupSameItems() {
        return groupSameItems;
    }

    public void setGroupSameItems(Integer groupSameItems) {
        this.groupSameItems = groupSameItems;
    }

    public Object getSelectPrintHeading() {
        return selectPrintHeading;
    }

    public void setSelectPrintHeading(Object selectPrintHeading) {
        this.selectPrintHeading = selectPrintHeading;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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

    public Object getRepresentsCompany() {
        return representsCompany;
    }

    public void setRepresentsCompany(Object representsCompany) {
        this.representsCompany = representsCompany;
    }

    public String getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(String customerGroup) {
        this.customerGroup = customerGroup;
    }

    public Object getCampaign() {
        return campaign;
    }

    public void setCampaign(Object campaign) {
        this.campaign = campaign;
    }

    public Integer getIsInternalCustomer() {
        return isInternalCustomer;
    }

    public void setIsInternalCustomer(Integer isInternalCustomer) {
        this.isInternalCustomer = isInternalCustomer;
    }

    public Integer getIsDiscounted() {
        return isDiscounted;
    }

    public void setIsDiscounted(Integer isDiscounted) {
        this.isDiscounted = isDiscounted;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public String getDebitTo() {
        return debitTo;
    }

    public void setDebitTo(String debitTo) {
        this.debitTo = debitTo;
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

    public String getcFormApplicable() {
        return cFormApplicable;
    }

    public void setcFormApplicable(String cFormApplicable) {
        this.cFormApplicable = cFormApplicable;
    }

    public Object getcFormNo() {
        return cFormNo;
    }

    public void setcFormNo(Object cFormNo) {
        this.cFormNo = cFormNo;
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

    public Object getSalesPartner() {
        return salesPartner;
    }

    public void setSalesPartner(Object salesPartner) {
        this.salesPartner = salesPartner;
    }

    public Float getAmountEligibleForCommission() {
        return amountEligibleForCommission;
    }

    public void setAmountEligibleForCommission(Float amountEligibleForCommission) {
        this.amountEligibleForCommission = amountEligibleForCommission;
    }

    public Float getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(Float commissionRate) {
        this.commissionRate = commissionRate;
    }

    public Float getTotalCommission() {
        return totalCommission;
    }

    public void setTotalCommission(Float totalCommission) {
        this.totalCommission = totalCommission;
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

    public String getAgainstIncomeAccount() {
        return againstIncomeAccount;
    }

    public void setAgainstIncomeAccount(String againstIncomeAccount) {
        this.againstIncomeAccount = againstIncomeAccount;
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

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getPosQueue() {
        return posQueue;
    }

    public void setPosQueue(String posQueue) {
        this.posQueue = posQueue;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Object getCustomerNameInArabic() {
        return customerNameInArabic;
    }

    public void setCustomerNameInArabic(Object customerNameInArabic) {
        this.customerNameInArabic = customerNameInArabic;
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

    public String getVatEmirate() {
        return vatEmirate;
    }

    public void setVatEmirate(String vatEmirate) {
        this.vatEmirate = vatEmirate;
    }

    public Float getTouristTaxReturn() {
        return touristTaxReturn;
    }

    public void setTouristTaxReturn(Float touristTaxReturn) {
        this.touristTaxReturn = touristTaxReturn;
    }

    public Object getKsaEinvQr() {
        return ksaEinvQr;
    }

    public void setKsaEinvQr(Object ksaEinvQr) {
        this.ksaEinvQr = ksaEinvQr;
    }

}
