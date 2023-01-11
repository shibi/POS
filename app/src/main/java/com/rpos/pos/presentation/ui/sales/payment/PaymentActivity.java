package com.rpos.pos.presentation.ui.sales.payment;

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
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.Utility;
import com.rpos.pos.presentation.ui.sales.bill.BillViewActivity;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends SharedActivity {

    private AppCompatTextView tv_customerName,tv_invoiceId,tv_billAmount,tv_date_label,tv_currency;
    private AppCompatEditText et_payment,et_balance, et_date ,et_referenceNo;
    private AppCompatButton btn_pay,btn_invoice;
    private ChipGroup chipGroup_pay_type;
    private LinearLayout ll_back, ll_payBalanceView;

    private int customerId;
    private String customerName;
    private float billAmount;
    private long invoiceId;
    private boolean isCreditSale;

    private InvoiceEntity currentInvoice;
    private int defaultCurrency;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private List<PaymentModeEntity> payModeList;

    @Override
    public int setUpLayout() {
        return R.layout.activity_payment;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        tv_customerName = findViewById(R.id.tv_customerName);
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
        et_date.setOnClickListener(view -> Utility.showDatePicker(et_date, PaymentActivity.this));

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

                    currentInvoice = localDb.invoiceDao().getInvoiceWithId((int)invoiceId);
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

                AppDialogs appDialogs = new AppDialogs(PaymentActivity.this);
                String msg = getString(R.string.choose_currency);
                appDialogs.showCommonSingleAlertDialog(getString(R.string.alert), msg, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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

                    customerId = currentInvoice.getCustomerId();
                    customerName = currentInvoice.getCustomerName();
                    billAmount = currentInvoice.getBillAmount();

                    tv_customerName.setText(customerName);
                    tv_invoiceId.setText("INV#"+invoiceId);
                    tv_billAmount.setText(""+billAmount);
                    et_referenceNo.setText(currentInvoice.getReferenceNo());

                    String date = DateTimeUtils.convertTimerStampToDateTime(currentInvoice.getTimestamp());
                    et_date.setText(date);

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
                        ll_payBalanceView.setVisibility(View.VISIBLE);
                        btn_invoice.setVisibility(View.GONE);
                        btn_pay.setVisibility(View.GONE);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * calculate
     * */
    private float calculateBalance(){
        try {
            if(currentInvoice == null){
                return 0.0f;
            }

            //balance value without round off
            float balance = currentInvoice.getBillAmount() - currentInvoice.getPaymentAmount();
            //balance with round of to two digits after decimal point
            return roundOffToTwoDigits(balance);
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
                    float totalBalance = calculateBalance();

                    float preferredPayAmount;
                    if(enteredAmount.isEmpty()){
                        preferredPayAmount = 0;
                    }else {
                        //TO AVOID ERROR WHILE PARSING NUMBER WITH DECIMAL POINT (".") . Example ".58"
                        //CHECK WHETHER NUMBER IS START WITH DECIMAL POINT
                        if(enteredAmount.startsWith(".")){
                            //APPEND A ZERO IN FRONT OF THE NUMBER
                            enteredAmount = "0"+enteredAmount;
                        }
                        float fl_enteredAmount = Float.parseFloat(enteredAmount);
                        preferredPayAmount = roundOffToTwoDigits(fl_enteredAmount);

                    }

                    //TO UPDATE THE BALANCE FIELD AFTER DEDUCTING USER ENTERED AMOUNT
                    //calculate the balance
                    balance = totalBalance - preferredPayAmount;
                    //round of to show in display
                    String str_rounded_balance = ""+roundOffToTwoDigits(balance);
                    //display the balance
                    et_balance.setText(str_rounded_balance);
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
            //get the user entered amount
            String str_paymentAmount = et_payment.getText().toString();
            //convert amount to float for to pass  into round of method
            float float_paymentAmount = Float.parseFloat(str_paymentAmount);
            //get the rounded off amount for further calculation
            float user_payment = roundOffToTwoDigits(float_paymentAmount);
            //check if the value is zero, if true, stop proceeding
            if(user_payment == 0){
                showToast(getString(R.string.enter_amount));
                et_payment.requestFocus();
                return;
            }
            //TO AVOID NEGATIVE VALUE APPEARING WHEN PAYING AMOUNT IS MORE THAN THE ACTUAL BALANCE
            //get the balance
            float previousBalance = calculateBalance();
            //check whether user entered amount greater than actual balance (which cause negative value)
            //if true, stops execution there. else continue
            if(user_payment > previousBalance){
                showToast(getString(R.string.enter_valid_data));
                et_payment.requestFocus();
                return;
            }


            String referenceNo = et_referenceNo.getText().toString();
            long todayTimestamp = DateTimeUtils.getCurrentDateTimeStamp();

            //To update the current payment to existing one
            // get the last paid amount
            float lastPayment = currentInvoice.getPaymentAmount();
            //add the current payment with last payment
            float newPayment = lastPayment + user_payment;
            //N.B always round off the final value to avoid infinite decimal points. (ex : 20.9856985669....)
            //round off to two digits after decimal point
            float fl_roundedNewPayment = roundOffToTwoDigits(newPayment);

            //set the values to the invoice objects
            currentInvoice.setPaymentAmount(fl_roundedNewPayment);
            currentInvoice.setReferenceNo(referenceNo);
            currentInvoice.setCurrency(tv_currency.getText().toString());

            //To check whether the payment is complete,
            // re- calculate balance and check whether balance is greater than zero.
            final double balance = calculateBalance();
            //set the status according to balance
            // STATUS - PAID ( balance = 0 or balance < 0 )
            // STATUS - UNPAID (balance > 0 )
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
                    long id = localDb.invoiceDao().insertInvoice(currentInvoice);

                    /*if(balance> 0){
                        gotoBillViewScreen();
                        finish();
                    }else {
                        gotoBillViewScreen();
                        finish();
                    }*/

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

            long dueDateTimeStamp = DateTimeUtils.convertDateToTimeStamp(dueDate);

            String referenceNo = et_referenceNo.getText().toString();
            currentInvoice.setPaymentType(paymentType);
            currentInvoice.setReferenceNo(referenceNo);
            currentInvoice.setDueDate(dueDateTimeStamp);
            currentInvoice.setCurrency(tv_currency.getText().toString());


            final double balance = calculateBalance();
            currentInvoice.setStatus((balance>0)?Constants.PAYMENT_UNPAID:Constants.PAYMENT_PAID);


            appExecutors.diskIO().execute(() -> {
                try {

                    long id = localDb.invoiceDao().insertInvoice(currentInvoice);
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
                Log.e("-----------","ci["+chip.getText());
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
        Intent billIntent = new Intent(PaymentActivity.this, BillViewActivity.class);
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
                        AppDialogs appDialogs = new AppDialogs(PaymentActivity.this);
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
     * to round off value to two digits after decimal point. Ex: 0.00
     * @param float_value value to round off
     * */
    private float roundOffToTwoDigits(float float_value){
        try {

            //round off method settings
            //round of to two digits after decimal
            String rounded_paymentAmount = Utility.roundOffDecimalValueTo2Digits(float_value);
            //convert the formatted amount to float
             return Float.parseFloat(rounded_paymentAmount);

        }catch (Exception e){
            e.printStackTrace();
            return 0.0f;
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

                Chip chip1 = new Chip(PaymentActivity.this);
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
        runOnUiThread(() -> showToast(msg, PaymentActivity.this));
    }


}