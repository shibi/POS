package com.rpos.pos.presentation.ui.purchase.order.checkout;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceItemHistory;
import com.rpos.pos.data.local.entity.PurchaseOrderDetailsEntity;
import com.rpos.pos.data.local.entity.PurchaseOrderEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.purchase.order.payment.PurchasePaymentActivity;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderCheckout extends SharedActivity {

    private LinearLayout ll_back;
    private AppCompatTextView tv_total,tv_tax_amount, tv_item_discount;
    private AppCompatEditText et_roundOfDiscount;
    private AppCompatTextView tv_netPayable;
    private AppCompatButton btn_cancel, btn_checkout;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private int orderId, supplierId;
    private double totalAmount, taxPercent, taxAmount, itemDiscount,discountPercent, netPayable,roundOfDisc;
    private String supplierName;

    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    @Override
    public int setUpLayout() {
        return R.layout.activity_purchase_order_checkout;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        tv_total = findViewById(R.id.tv_toalValue);
        tv_tax_amount = findViewById(R.id.tv_taxAmount);
        tv_item_discount = findViewById(R.id.tv_itemDiscount);
        et_roundOfDiscount = findViewById(R.id.et_roundOfdiscount);
        tv_netPayable = findViewById(R.id.tv_netpayable);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_checkout = findViewById(R.id.btn_checkout);



        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        supplierName = "";

        roundOfDisc = 0;


        Intent data = getIntent();
        if(data !=null){
            orderId = data.getIntExtra(Constants.ORDER_ID,Constants.EMPTY_INT);
            totalAmount = data.getDoubleExtra(Constants.TOTAL_AMOUNT,0.0);
            taxPercent = data.getDoubleExtra(Constants.TAX_PERCENT, 0.0);
            taxAmount = data.getDoubleExtra(Constants.TAX_AMOUNT, 0.0);
            itemDiscount = data.getDoubleExtra(Constants.ITEM_DISCOUNT, 0.0);
            discountPercent = data.getDoubleExtra(Constants.DISCOUNT_PERCENT, 0.0);
            supplierId =  data.getIntExtra(Constants.SUPPLIER_ID,Constants.EMPTY_INT);
            supplierName =  data.getStringExtra(Constants.SUPPLIER_NAME);
        }

        //check whether order id is empty, if yes then stop proceeding
        if(orderId == Constants.EMPTY_INT){
            showToast(getString(R.string.invalid_orderid), PurchaseOrderCheckout.this);
            return;
        }


        tv_total.setText(""+totalAmount);
        tv_tax_amount.setText(""+taxAmount);
        tv_item_discount.setText(""+itemDiscount);
        et_roundOfDiscount.setText("0");

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //checkout button click
        btn_checkout.setOnClickListener(view -> {

            //show confirmation dialog
            showCheckoutConfirmation();

        });

        et_roundOfDiscount.addTextChangedListener(roundOffTextWatcher);

        // go back on cancel click
        btn_cancel.setOnClickListener(view -> onBackPressed());

        // back press
        ll_back.setOnClickListener(view -> onBackPressed());


        //calculate payable
        calculatePayable();

    }

    @Override
    public void initObservers() {

    }


    private void calculatePayable(){
        try {

            netPayable = calculateNetAmount();
            tv_netPayable.setText(""+netPayable);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to calculate net amount
     * */
    private double calculateNetAmount(){
        try {

            double tempTotal = totalAmount;
            tempTotal-= itemDiscount;
            tempTotal+=taxAmount;
            tempTotal-= roundOfDisc;

            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
            String roundedTotal = decimalFormat.format(tempTotal);
            return Double.parseDouble(roundedTotal);

        }catch (Exception e){
            return -101.101;
        }
    }


    private TextWatcher roundOffTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {

                String str_roundOf = editable.toString();
                float tempRoundOfAmount;
                if(!str_roundOf.isEmpty()){
                    //TO AVOID ERROR WHILE PARSING NUMBER WITH DECIMAL POINT (".") . Example ".58"
                    //CHECK WHETHER NUMBER IS START WITH DECIMAL POINT
                    if(str_roundOf.startsWith(".")){
                        //APPEND A ZERO IN FRONT OF THE NUMBER
                        str_roundOf = "0"+str_roundOf;
                    }
                    tempRoundOfAmount = Float.parseFloat(str_roundOf);

                    //TO avoid negative value, check whether discount is exceeded total amount
                    if(tempRoundOfAmount > totalAmount){
                        et_roundOfDiscount.setError(getString(R.string.discount_amount_exceeded_total));
                        et_roundOfDiscount.setText("0");
                        et_roundOfDiscount.setSelection(et_roundOfDiscount.length());
                        return;
                    }

                }else {
                    tempRoundOfAmount = 0.0f;
                }

                roundOfDisc = tempRoundOfAmount;

                calculatePayable();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /**
     * to show confirmation before proceeding to checkout
     * */
    private void showCheckoutConfirmation(){
        try {

            AppDialogs checkoutConfirm = new AppDialogs(PurchaseOrderCheckout.this);
            checkoutConfirm.showCommonDualActionAlertDialog(getString(R.string.checkout_title), getString(R.string.checkout_confirmation), new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                        String extra_discount = et_roundOfDiscount.getText().toString();
                        float additional_discount;
                        if(!extra_discount.isEmpty()){
                            //TO AVOID ERROR WHILE PARSING NUMBER WITH DECIMAL POINT (".") . Example ".58"
                            //CHECK WHETHER NUMBER IS START WITH DECIMAL POINT
                            if(extra_discount.startsWith(".")){
                                //APPEND A ZERO IN FRONT OF THE NUMBER
                                extra_discount = "0"+extra_discount;
                            }
                            additional_discount = Float.parseFloat(extra_discount);
                        }else {
                            additional_discount = 0.0f;
                        }
                        checkout(orderId, (float)totalAmount, (float) netPayable,(float)taxPercent,(float)taxAmount,(float)discountPercent,(float)itemDiscount,additional_discount, supplierId, supplierName);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClickNegetive(String id) {
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkout(int _orderId,float _grossAmount,float netPayable,float _taxPercent,float _taxAmount,float _discPercent,float _discAmount,float _additionalDiscount, int _suppId,String _suppName){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    PurchaseOrderEntity order = localDb.purchaseOrderDao().getOrderWithId(_orderId);
                    if(order!=null){
                        order.setAmount(netPayable);
                        order.setStatus(Constants.ORDER_COMPLETED);
                        localDb.purchaseOrderDao().insertOrder(order);

                        String date = DateTimeUtils.getCurrentDateTime();

                        PurchaseInvoiceEntity invoice = new PurchaseInvoiceEntity();
                        invoice.setOrderId(_orderId);
                        invoice.setCustomerId(_suppId);
                        invoice.setCustomerName(_suppName);
                        invoice.setGrossAmount(_grossAmount);
                        invoice.setBillAmount(netPayable);
                        invoice.setTaxPercent(_taxPercent);
                        invoice.setTaxAmount(_taxAmount);
                        invoice.setDiscountPercent(_discPercent);
                        invoice.setDiscountAmount(_discAmount);
                        invoice.setAdditionalDiscount(_additionalDiscount);
                        invoice.setCurrency("USD");
                        invoice.setPaymentType(Constants.PAY_TYPE_NONE);
                        invoice.setPaymentAmount(0.0f); // customers payment against bill amount
                        invoice.setDate(date);
                        long invoiceId = localDb.purchaseInvoiceDao().insertInvoice(invoice);


                        //items list
                        List<PurchaseOrderDetailsEntity> orderDetailsList = localDb.purchaseOrderDetailsDao().getAllItemsInOrder(_orderId);
                        if(orderDetailsList!=null){
                            //to fetch all ordered item fro db at once,
                            //first get all ids of ordered item to one list
                            List<Integer> itemIdsList = new ArrayList<>();
                            for (PurchaseOrderDetailsEntity orderDetails: orderDetailsList){
                                itemIdsList.add(orderDetails.getItemId());
                            }
                            //then get the items with ids list
                            List<ItemEntity> selectedItems = localDb.itemDao().getSelectedItems(itemIdsList);

                            if(selectedItems!=null && selectedItems.size()>0){

                                List<PurchaseInvoiceItemHistory> invoiceItemList = new ArrayList<>();
                                PurchaseInvoiceItemHistory invoiceItem;
                                int availableStock;
                                for (PurchaseOrderDetailsEntity orderItem:orderDetailsList){
                                    for (ItemEntity originalItem:selectedItems){
                                        if(orderItem.getItemId() == originalItem.getItemId()){
                                            //update stock of each items
                                            availableStock = originalItem.getAvailableQty() + orderItem.getQuantity();
                                            originalItem.setAvailableQty(availableStock);

                                            //create invoice item list
                                            invoiceItem = convertItemToInvoiceItem(originalItem,invoiceId,orderItem);
                                            if(invoiceItem!=null) {
                                                invoiceItemList.add(invoiceItem);
                                            }
                                        }
                                    }
                                }

                                localDb.purchaseInvoiceDao().insertInvoiceItems(invoiceItemList);

                                //update stock for the selected items
                                localDb.itemDao().updateList(selectedItems);
                            }

                        }

                        gotoPayment(_suppId, _suppName,invoiceId, netPayable);

                    }else {
                        showToast(getString(R.string.no_such_order),PurchaseOrderCheckout.this);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private PurchaseInvoiceItemHistory convertItemToInvoiceItem(ItemEntity item,long _invoiceId,PurchaseOrderDetailsEntity orderedItem){
        try {

            PurchaseInvoiceItemHistory invoiceItem = new PurchaseInvoiceItemHistory();
            invoiceItem.setOrderId((int)orderedItem.getOrderId());
            invoiceItem.setInvoiceId((int)_invoiceId);

            invoiceItem.setItemId(item.getItemId());
            invoiceItem.setItemName(item.getItemName());
            invoiceItem.setItemDescription(item.getDescription());

            float rate = orderedItem.getVariableRate();
            invoiceItem.setItemRate(rate);

            invoiceItem.setQuantity(orderedItem.getQuantity());
            float total = orderedItem.getQuantity() * rate;
            invoiceItem.setTotal(total);

            return invoiceItem;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void gotoPayment(int suppId,String suppName,long _invoiceId,float billAmount){
        try {

            Intent paymentIntent = new Intent(PurchaseOrderCheckout.this, PurchasePaymentActivity.class);
            paymentIntent.putExtra(Constants.SUPPLIER_ID, suppId);
            paymentIntent.putExtra(Constants.SUPPLIER_NAME, suppName);
            paymentIntent.putExtra(Constants.BILL_AMOUNT,billAmount);
            paymentIntent.putExtra(Constants.INVOICE_ID, (int) _invoiceId);
            startActivity(paymentIntent);
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}