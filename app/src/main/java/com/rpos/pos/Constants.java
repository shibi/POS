package com.rpos.pos;


public class Constants {

    public static String API_KEY = "";
    public static String API_SECRET = "";

    public static final String FRAGMENT_HOME_LIST = "fragment_home_list";
    public static final String FRAGMENT_HOME_CARD = "fragment_home_card";
    public static final String FRAGMENT_ITEM_LIST = "fragment_item_list";
    public static final String FRAGMENT_SHIFT = "fragment_shift";
    public static final String FRAGMENT_SHIFT_REPORT = "fragment_shift_report";
    public static final String FRAGMENT_SALES_REPORT = "fragment_sales_report";
    public static final String FRAGMENT_PURCHASE_REPORT = "fragment_purchase_report";

    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CUSTOMER_ID = "customerId";

    public static final String ORDER_ID = "OrderId";
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final String TAX_AMOUNT = "taxAmount";
    public static final String ITEM_DISCOUNT = "item_discount";
    public static final String DISCOUNT_PERCENT = "discount_percent";
    public static final String INVOICE_ID = "invoiceId";
    public static final String TAX_ID = "taxId";
    public static final String TAX_PERCENT = "taxPercent";


    public static final String SUPPLIER_ID = "supplierId";
    public static final String SUPPLIER_NAME = "supplierName";
    public static final String SUPPLIER_TAXID = "supplierTax";


    public static final String CUSTOMER_NAME = "customerName";
    public static final String CUSTOMER_PHONE = "customerPhone";
    public static final String CUSTOMER_TAXID = "customerTaxid";
    public static final String CUSTOMER_EMAIL = "customerEmail";

    public static final String BILL_AMOUNT = "bill_amount";

    public static final String BARCODE_NUMBER = "barcode_number";
    public static final String INVOICE_PREFIX = "SNo";

    public static final String ITEM_REQUESTED_PARENT = "item_requested_parent";
    public static final int PARENT_SALES = 1;
    public static final int PARENT_PURCHASE = 2;
    public static final int PARENT_PRICE_LIST = 3;
    public static final int PARENT_SALES_ORDER_DETAILS = 4;

    public static final String SHIFT_ID = "ShiftId";

    public static final String FILTER_SORT = "SORT";
    public static final String FILTER_SEARCH = "SEARCH";


    public static final String ITEM_ID = "itemID";
    public static final String ITEM_NAME = "itemName";
    public static final String ITEM_RATE = "itemRate";
    public static final String ITEM_QUANTITY= "itemQuantity";
    public static final String ITEM_STOCK = "itemStock";
    public static final String ITEM_UOM_NAME = "itemUomName";
    public static final String ITEM_UOM_ID = "itemUomId";
    public static final String ITEM_DESC = "itemDescription";
    public static final String ITEM_MAINTAIN_STOCK = "itemMaintainStock";


    public static final String PRICELIST_ID = "PriceListId";
    public static final String PRICELIST_NAME = "PriceListName";
    public static final String PRICELIST_TYPE = "PriceListType";

    public static final String ITEM_PRICE_LIST_ID = "item_PriceListId";


    public static final String ORDER_CREATED = "Created";
    public static final String ORDER_PENDING = "Pending";
    public static final String ORDER_ONGOING = "OnGoing";
    public static final String ORDER_COMPLETED = "Completed";
    public static final String ORDER_CANCELLED = "Cancelled";

    public static final String PAYMENT_PAID = "Paid";
    public static final String PAYMENT_UNPAID = "Un-paid";
    public static final String PAYMENT_OVERDUE = "Overdue";
    public static final String PAYMENT_RETURN = "Return";
    public static final String PAYMENT_COMPLETE = "Completed";


    public static final String ACTIVE = "active";
    public static final String IN_ACTIVE = "in_active";


    public static final int PAY_TYPE_NONE = 0;
    public static final int PAY_TYPE_CASH = 1;
    public static final int PAY_TYPE_ON_ACCOUNT = 2;
    public static final int PAY_TYPE_CARD = 3;
    public static final int PAY_TYPE_CHEQUE = 4;
    public static final int PAY_TYPE_TRANSFER = 5;
    public static final int PAY_TYPE_CREDIT_SALE = 6;

    public static final int FILTER_ALL = 0;
    public static final int FILTER_PAID = 1;
    public static final int FILTER_UNPAID = 2;
    public static final int FILTER_OVERDUE = 3;
    public static final int FILTER_RETURN = 4;

    public static final int REQUEST_CAMERA_PERMISSION = 201;

    public static final int HOME_SCREEN_LIST = 0;
    public static final int HOME_SCREEN_CARD = 1;

    public static final String LANG_EN = "en";
    public static final String LANG_AR = "ar";

    public static final String EMPTY = "empty";
    public static final int EMPTY_INT = -1;
    public static final String NONE="none";


    public static final int COUNTRY_SAUDI_ARABIA = 4;

    public static final int PERMISSION_BLUETOOTH = 161;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 162;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 163;
    public static final int PERMISSION_BLUETOOTH_SCAN = 164;


    //PRICE LIST TYPES ( BUY , SELL )
    public static final int BUYING = 0;
    public static final int SELLING = 1;

    public static final String ITEM_SELECTION_TYPE = "itemSelectionType";
    public static final int ITEM_SELECTION_SINGLE_PICK = 1;
    public static final int ITEM_SELECTION_QUANTITY_PICK  = 2;


    public static final String REPORT_TYPE = "Report_Type";

    public static final String SHIFT_OPEN = "Shift_open";
    public static final String SHIFT_CLOSED = "Shift_closed";


}
