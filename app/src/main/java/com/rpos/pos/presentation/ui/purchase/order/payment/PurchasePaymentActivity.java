package com.rpos.pos.presentation.ui.purchase.order.payment;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CurrencyItem;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.Utility;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.purchase.bill.PurchaseBillView;
import com.rpos.pos.presentation.ui.sales.bill.BillViewActivity;
import java.util.ArrayList;
import java.util.List;

public class PurchasePaymentActivity extends SharedActivity {

    private AppCompatTextView tv_supplierName,tv_invoiceId,tv_billAmount,tv_date_label,tv_currency;
    private AppCompatEditText et_payment,et_balance, et_date ,et_referenceNo;
    private AppCompatButton btn_pay,btn_invoice;
    private ChipGroup chipGroup_pay_type;
    private LinearLayout ll_back, ll_payBalanceView;

    private int supplierId;
    private String supplierName;
    private float billAmount;
    private long invoiceId;
    private boolean isCreditSale;

    private PurchaseInvoiceEntity currentInvoice;
    private int defaultCurrency;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private List<PaymentModeEntity> payModeList;

    @Override
    public int setUpLayout() {
        return R.layout.activity_purchase_payment;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        tv_supplierName = findViewById(R.id.tv_customerName);
        tv_invoiceId = findViewById(R.id.tv_invoiceNumber);
        tv_billAmount = findViewById(R.id.tv_billAmount);
        tv_date_label = findViewById(R.id.tv_date_label);
        tv_currency = findViewById(R.id.tv_currency);
        ll_back = findViewById(R.id.ll_back);
        ll_payBalanceView = findViewById(R.id.ll_pay_balance_view);

        et_payment = findViewById(R.id.et_payment);
        et_balance = findViewById(R.id.et_balance);
        et_date = findViewById(R.id.et_date);
        et_referenceNo = findViewById(R.id.et_referenceNo);

        payModeList = new ArrayList<>();
        btn_pay = findViewById(R.id.btn_pay);
        btn_invoice = findViewById(R.id.btn_invoice);

        chipGroup_pay_type = findViewById(R.id.chip_group);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        currentInvoice = null;

        Intent data = getIntent();
        if(data!=null){
            invoiceId = data.getIntExtra(Constants.INVOICE_ID, Constants.EMPTY_INT);
        }

        //set the current date
        String date = DateTimeUtils.getCurrentDate();
        et_date.setText(date);

        //to calculate balance on payment entering
        et_payment.addTextChangedListener(balanceTextWatcher);

        //to pay button
        btn_pay.setOnClickListener(view -> {
            try {

                onPayButtonClick(btn_pay.getText().toString());

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //to open bill view
        btn_invoice.setOnClickListener(view -> gotoBillViewScreen());

        //pay type
        chipGroup_pay_type.setOnCheckedStateChangeListener((group, checkedIds) -> {
            try{

                if(checkedIds!=null && checkedIds.size()>0) {
                    Chip selectedChip = group.findViewById(checkedIds.get(0));
                    if (selectedChip != null) {
                        Log.d("----------", "onCheckedChanged: ."+selectedChip.getText());
                        PaymentModeEntity payMode = findChipWithName(selectedChip.getText().toString());
                        if(payMode!=null){

                            if(payMode.getType().equals(""+Constants.PAY_TYPE_CREDIT_SALE)){
                                btn_pay.setText(R.string.complete);
                                et_date.setText("");
                                tv_date_label.setText(R.string.due_date);
                                setPaymentFieldEnabled(false);
                                et_payment.setText("0");
                            }else {
                                btn_pay.setText(getString(R.string.btn_pay));
                                tv_date_label.setText(R.string.date_label);
                                setPaymentFieldEnabled(true);
                                et_payment.setText(""+calculateBalance());
                            }
                        }else {
                            showToast(getString(R.string.no_such_mode));
                        }
                    }else {
                        showToast(getString(R.string.no_such_item));
                    }
                }else {
                    showToast(getString(R.string.no_id_found));
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //DATE PICKER
        et_date.setOnClickListener(view -> Utility.showDatePicker(et_date, PurchasePaymentActivity.this));

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //get the current invoice with id
        getCurrentInvoice();

        //get default currency selected
        getDefaultCurrency();

    }

    @Override
    public void initObservers() {

    }


    /**
     * get invoice details
     * */
    private void getCurrentInvoice(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    currentInvoice = localDb.purchaseInvoiceDao().getInvoiceWithId((int)invoiceId);
                    if(currentInvoice != null){
                        updateValuesToUi();
                    }else {
                        showToast(getString(R.string.invalid_invoice));
                    }

                    loadAllPayModes();

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to get the default currency
     * */
    private void getDefaultCurrency(){
        try {
            //get saved default currency id
            defaultCurrency = SharedPrefHelper.getInstance(this).getDefaultCurrency();
            //check whether no default currency is saved
            if(defaultCurrency != Constants.EMPTY_INT){
                ArrayList<CurrencyItem> currencyItems = getCoreApp().getCurrencyList();
                for (CurrencyItem currency : currencyItems){
                    if(currency.getId() == defaultCurrency){
                        tv_currency.setText(currency.getSymbol());
                        break;
                    }
                }
            }else {

                AppDialogs appDialogs = new AppDialogs(PurchasePaymentActivity.this);
                String msg = getString(R.string.choose_currency);
                appDialogs.showCommonSingleAlertDialog(getString(R.string.alert), msg, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Do nothing
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * fill invoice detail in fields
     * */
    private void updateValuesToUi(){
        try {

            runOnUiThread(() -> {
                try {

                    supplierId = currentInvoice.getCustomerId();
                    supplierName = currentInvoice.getCustomerName();
                    billAmount = currentInvoice.getBillAmount();

                    tv_supplierName.setText(supplierName);
                    tv_invoiceId.setText("INV#"+invoiceId);
                    tv_billAmount.setText(""+billAmount);
                    et_referenceNo.setText(currentInvoice.getReferenceNo());
                    String invoiceDate;
                    if(currentInvoice.getDate()!=null){
                        if(currentInvoice.getDate().length()>=10)
                            invoiceDate = currentInvoice.getDate().substring(0,10);
                        else
                            invoiceDate = currentInvoice.getDate();
                    }else {
                        invoiceDate = getString(R.string.empty_data);
                    }
                    et_date.setText(invoiceDate);

                    //check whether payment type is credit sale
                    isCreditSale = (currentInvoice.getPaymentType() == Constants.PAY_TYPE_CREDIT_SALE);

                    //get the balance amount to pay
                    float balance = calculateBalance();

                    //To find invoice if fully paid , check the balance
                    boolean isFullyPaid = !(balance>0);
                    if(!isFullyPaid){
                        //if not fully paid, show pay button and balance
                        et_balance.setText(""+balance);
                        et_payment.setText(""+balance);
                        btn_pay.setText(getString(R.string.btn_pay));
                    }else {

                        //if fully paid, show invoice button,
                        btn_pay.setText(R.string.invoice_btn);

                        btn_pay.setVisibility((isCreditSale)?View.GONE:View.VISIBLE);

                        //disable other fields since it's already paid
                        et_referenceNo.setEnabled(false);
                        et_date.setEnabled(false);
                    }

                    if(!currentInvoice.getStatus().equals(Constants.PAYMENT_RETURN)) {
                        btn_invoice.setVisibility((isCreditSale) ? View.VISIBLE : View.GONE);

                        //to avoid selecting payment type and other fields
                        //check the payment status and hide them

                        //hide pay type if fully paid
                        chipGroup_pay_type.setVisibility((isFullyPaid) ? View.GONE : View.VISIBLE);

                        //if paid, hide payment views, else show
                        ll_payBalanceView.setVisibility((isFullyPaid) ? View.GONE : View.VISIBLE);

                    }else {
                        //RETURNED
                        //No need to show payment options
                        ll_payBalanceView.setVisibility(View.VISIBLE);
                        btn_pay.setVisibility(View.GONE);
                        btn_invoice.setVisibility(View.GONE);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private float calculateBalance(){
        try {
            if(currentInvoice == null){
                return 0.0f;
            }
            return currentInvoice.getBillAmount() - currentInvoice.getPaymentAmount();
        }catch (Exception e){
            e.printStackTrace();
            return 0.0f;
        }
    }


    /**
     *
     * */
    private TextWatcher balanceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                float balance;
                String enteredAmount = editable.toString();

                if(currentInvoice!=null){
                    billAmount = currentInvoice.getBillAmount();
                    float totalBalance = billAmount - currentInvoice.getPaymentAmount();

                    float preferedPayAmount;
                    if(enteredAmount.isEmpty()){
                        preferedPayAmount = 0;
                    }else {
                        preferedPayAmount = Float.parseFloat(enteredAmount);
                    }

                    balance = totalBalance - preferedPayAmount;
                    et_balance.setText("" + balance);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };


    /**
     * pay button click
     * */
    private void onPayButtonClick(String btnType){
        try {

            if(currentInvoice!=null) {

                String type_pay = getString(R.string.btn_pay);
                String type_complete = getString(R.string.btn_complete);

                //get the selected payment type
                int paymentType = getSelectedPaymentType();

                //if pay type is credit sale
                if(btnType.equals(type_complete)){

                    //generate and save credit sale bill
                    generateCreditSaleBill(paymentType);

                }else if(btnType.equals(type_pay)) {

                    //generate and save cash bill
                    generateNormalCashBill(paymentType);

                }else {
                    //for invoice print
                    gotoBillViewScreen();
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * to generate cash bill and save
     * */
    private void generateNormalCashBill(int paymentType){
        try {

            if(paymentType == Constants.EMPTY_INT){
                showToast(getString(R.string.select_payment_type));
                return;
            }

            //for invoice pay
            String str_paymentAmount = et_payment.getText().toString();
            float payment = Float.parseFloat(str_paymentAmount);

            if(payment == 0){
                showToast(getString(R.string.enter_amount));
                et_payment.requestFocus();
                return;
            }


            String referenceNo = et_referenceNo.getText().toString();
            String todayDate = et_date.getText().toString();

            float lastPayment = currentInvoice.getPaymentAmount();
            float newPayment = lastPayment + payment;

            currentInvoice.setPaymentAmount(newPayment);
            currentInvoice.setReferenceNo(referenceNo);
            currentInvoice.setDate(todayDate);
            currentInvoice.setCurrency(tv_currency.getText().toString());

            final double balance = currentInvoice.getBillAmount() - currentInvoice.getPaymentAmount();
            currentInvoice.setStatus((balance>0)?Constants.PAYMENT_UNPAID:Constants.PAYMENT_PAID);


            //For invoices generated with credit sale
            //always save payment mode as credit sale, regardless of which other pay types
            //are selected. A credit sale invoice is always considered as credit sale
            if(isCreditSale){
                currentInvoice.setPaymentType(Constants.PAY_TYPE_CREDIT_SALE);
            }else {
                currentInvoice.setPaymentType(paymentType);
            }


            appExecutors.diskIO().execute(() -> {
                try {
                    long id = localDb.purchaseInvoiceDao().insertInvoice(currentInvoice);
                    if(balance> 0){
                        finish();
                    }else {
                        gotoBillViewScreen();
                        finish();
                    }

                } catch (Exception e) {
                    showToast(getString(R.string.invoice_update_failed));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To save bill as credit sale type
     * */
    private void generateCreditSaleBill(int paymentType){
        try {

            if(paymentType == -1){
                showToast(getString(R.string.select_payment_type));
                return;
            }

            //due date is mandatory for credit sale
            String dueDate = et_date.getText().toString();
            if(dueDate.isEmpty()){
                showToast(getString(R.string.select_due_date));
                et_date.requestFocus();
                et_date.setError(getString(R.string.select_due_date));
                return;
            }

            String referenceNo = et_referenceNo.getText().toString();
            currentInvoice.setPaymentType(paymentType);
            currentInvoice.setReferenceNo(referenceNo);
            currentInvoice.setDate(dueDate);
            currentInvoice.setCurrency(tv_currency.getText().toString());

            final double balance = currentInvoice.getBillAmount() - currentInvoice.getPaymentAmount();
            currentInvoice.setStatus((balance>0)?Constants.PAYMENT_UNPAID:Constants.PAYMENT_PAID);


            appExecutors.diskIO().execute(() -> {
                try {

                    long id = localDb.purchaseInvoiceDao().insertInvoice(currentInvoice);
                    gotoBillViewScreen();
                    finish();

                } catch (Exception e) {
                    showToast(getString(R.string.invoice_update_failed));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get selected payment type integer
     * */
    private int getSelectedPaymentType(){
        try {

            int selectedChipId = chipGroup_pay_type.getCheckedChipId();
            if(selectedChipId==-1){
                return -1;
            }else {
                Chip chip = findViewById(selectedChipId);
                PaymentModeEntity payMode = findChipWithName(chip.getText().toString());
                return Integer.parseInt(payMode.getType());
            }

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * goto bill view
     * */
    private void gotoBillViewScreen(){
        Intent billIntent = new Intent(PurchasePaymentActivity.this, PurchaseBillView.class);
        billIntent.putExtra(Constants.INVOICE_ID, (int) invoiceId);
        startActivity(billIntent);
    }

    /**
     * To enable/disable payment field
     * sets zero value to avoid empty
     * */
    private void setPaymentFieldEnabled(boolean isEnable){
        et_payment.setEnabled(isEnable);
    }

    /**
     * load all available methods
     * */
    private void loadAllPayModes(){
        try {

            appExecutors.diskIO().execute(() -> {
                List<PaymentModeEntity> paylist = localDb.paymentModeDao().getAllPaymentModeList();
                if(paylist!=null && !paylist.isEmpty()){
                    payModeList.clear();
                    payModeList.addAll(paylist);
                    runOnUiThread(this::prepareChipForPayMode);
                }else {
                    runOnUiThread(() -> {
                        AppDialogs appDialogs = new AppDialogs(PurchasePaymentActivity.this);
                        appDialogs.showCommonAlertDialog(getString(R.string.add_pay_type_first), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * create chips with names
     * */
    private void prepareChipForPayMode(){
        try {

            //HIDE CREDIT SALE TYPE PAYMENT MODE SINCE INVOICE IS CREATED WITH CREDIT SALE OPTION. THEN ONLY CASH OR OTHER PAY MODE ARE AVAILABLE
            //ELSE SHOW ENTIRE LIST AS CHIP


            String payModeType;
            String creditType = ""+Constants.PAY_TYPE_CREDIT_SALE;

            for (int i=0;i< payModeList.size();i++){
                //check pay mode item type
                payModeType = payModeList.get(i).getType();
                //if invoice is credit type, do not show credit sale option again
                if(isCreditSale && payModeType.equals(creditType)){
                    continue;
                }

                Chip chip1 = new Chip(PurchasePaymentActivity.this);
                chip1.setText(payModeList.get(i).getPaymentModeName());
                chip1.setCheckable(true);
                chip1.setChipBackgroundColor(getResources().getColorStateList(R.color.pay_type_color_list));
                chip1.setId(ViewCompat.generateViewId());
                chipGroup_pay_type.addView(chip1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * find chip with names
     * @param chipName
     * */
    private PaymentModeEntity findChipWithName(String chipName){
        try {

            for (int i=0;i< payModeList.size();i++){
                if(payModeList.get(i).getPaymentModeName().equals(chipName)){
                    return payModeList.get(i);
                }
            }

            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private void showToast(String msg){
        runOnUiThread(() -> showToast(msg, PurchasePaymentActivity.this));
    }

}