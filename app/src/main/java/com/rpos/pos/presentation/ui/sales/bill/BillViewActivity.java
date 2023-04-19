package com.rpos.pos.presentation.ui.sales.bill;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.RemoteException;

import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CompanyAddressEntity;
import com.rpos.pos.data.local.entity.CompanyEntity;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.ZatcaDataGenerator;
import com.rpos.pos.domain.utils.sunmi_printer_utils.BluetoothUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.sunmi.peripheral.printer.InnerResultCallback;

import java.util.ArrayList;
import java.util.List;

public class BillViewActivity extends SharedActivity {

    private AppCompatTextView tv_billTo,tv_billNo,tv_billDate, tv_billType,tv_billCurrency;
    private AppCompatTextView tv_grossTotal,tv_taxAmount, tv_netTotal,tv_discountAmount, tv_payment,tv_due_amount, tv_totalItems, tv_payCurrency, tv_cash_paid,tv_billAmount;
    private AppCompatTextView tv_companyName, tv_companyPhone,tv_companyEmail, tv_companyAddress;
    private ImageView iv_qrcode;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private RecyclerView rv_billedItems;

    private InvoiceEntity currentInvoice;
    private long invoiceId;
    private CompanyEntity savedCompanyDetails;
    private int billingCountryId;
    private SharedPrefHelper prefHelper;

    private AppCompatButton btn_back, btn_print;

    private CompanyAddressEntity companyAddressEntity;
    private AppDialogs printerProgress;

    //For to pass to printing
    private List<InvoiceItemHistory> printItemsList;
    private String printerQrData;

    @Override
    public int setUpLayout() {
        return R.layout.activity_bill_view;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        tv_billTo = findViewById(R.id.tv_invoiceTo);
        tv_billNo = findViewById(R.id.tv_invoiceNo);
        tv_billType = findViewById(R.id.tv_billType);
        tv_billCurrency = findViewById(R.id.tv_currency);
        tv_billDate = findViewById(R.id.tv_billDate);
        tv_grossTotal = findViewById(R.id.tv_grossTotal);
        tv_taxAmount = findViewById(R.id.tv_tax);
        tv_discountAmount = findViewById(R.id.tv_discountAmount);
        tv_netTotal = findViewById(R.id.tv_net_total);
        tv_payment = findViewById(R.id.tv_payment);
        tv_due_amount = findViewById(R.id.tv_due_amount);
        iv_qrcode = findViewById(R.id.iv_qrcode);
        rv_billedItems = findViewById(R.id.rv_billedItems);
        btn_back = findViewById(R.id.btn_back);
        tv_totalItems = findViewById(R.id.tv_totalItems);
        tv_payCurrency = findViewById(R.id.tv_pay_currency);
        tv_cash_paid = findViewById(R.id.tv_cash);
        btn_print = findViewById(R.id.btn_print);
        tv_companyName = findViewById(R.id.tv_companyName);
        tv_companyPhone = findViewById(R.id.tv_companyPhone);
        tv_companyEmail = findViewById(R.id.tv_companyEmail);
        tv_companyAddress = findViewById(R.id.tv_companyAddress);
        tv_billAmount = findViewById(R.id.tv_billAmount);

        printerProgress = new AppDialogs(this);

        //shared preference instance
        prefHelper = SharedPrefHelper.getInstance(this);

        Intent data = getIntent();
        if(data!=null){
            invoiceId = data.getIntExtra(Constants.INVOICE_ID, -1);
        }

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        billingCountryId = prefHelper.getBillingCountry();

        printerQrData = Constants.EMPTY;
        printItemsList = new ArrayList<>();
        currentInvoice = null;

        //back press
        btn_back.setOnClickListener(view -> onBackPressed());

        //get the current invoice with id
        getCurrentInvoice();

        //add click listener in print button
        enablePrintButton();
    }

    /**
     * to add click listener on print button
     * */
    private void enablePrintButton(){
        btn_print.setOnClickListener(view -> {
            try{

                //to select printing method and prints
                printReceipt();

            }catch (Exception e){
                e.printStackTrace();
            }
        });
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
                        List<InvoiceItemHistory> billedItemsList = localDb.invoiceDao().getInvoiceItemsWithId(currentInvoice.getId());
                        List<CompanyEntity> sellerDataList = localDb.companyDetailsDao().getAllCompanyDetails();

                        //check seller details exists
                        if(sellerDataList!=null && sellerDataList.size()>0){
                            savedCompanyDetails = sellerDataList.get(0);
                        }else {
                            savedCompanyDetails = null;
                        }

                        printItemsList = billedItemsList;
                        updateValuesToUi(billedItemsList);

                        List<CompanyAddressEntity> companyAddressList = localDb.companyAddressDao().getCompanyDetails();
                        updateCompanyAddress(companyAddressList);

                    }else {
                        runOnUiThread(() -> showToast(getString(R.string.invoice_notfound), BillViewActivity.this));
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
     * fill invoice detail in fields
     * */
    private void updateValuesToUi(List<InvoiceItemHistory> billedItemsList){
        try {

            runOnUiThread(() -> {
                try {

                    String COLON_SEPARATOR = " : ";
                    String _billToCustomer,_billNo,_billDate,_billType, _billCurrency;
                    String date = DateTimeUtils.convertTimerStampToDateTime(currentInvoice.getTimestamp());

                    if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)){
                        _billToCustomer = COLON_SEPARATOR + currentInvoice.getCustomerName();
                        _billNo = COLON_SEPARATOR +"INV#"+invoiceId;
                        _billDate = COLON_SEPARATOR + date;
                        _billType = COLON_SEPARATOR + getString(R.string.sales_invoice);
                        _billCurrency = COLON_SEPARATOR + currentInvoice.getCurrency();
                    }else {
                        _billToCustomer = currentInvoice.getCustomerName() + COLON_SEPARATOR;
                        _billNo =  invoiceId +"INV#"+ COLON_SEPARATOR;
                        _billDate = COLON_SEPARATOR + date ;
                        _billType = COLON_SEPARATOR + getString(R.string.sales_invoice);
                        _billCurrency = COLON_SEPARATOR + currentInvoice.getCurrency();
                    }

                    tv_billTo.setText(_billToCustomer);
                    tv_billNo.setText(_billNo);
                    tv_billType.setText(_billType);
                    tv_billCurrency.setText(_billCurrency);
                    tv_billDate.setText(_billDate);


                    tv_grossTotal.setText(""+currentInvoice.getGrossAmount());
                    tv_taxAmount.setText(""+currentInvoice.getTaxAmount());
                    tv_discountAmount.setText(""+currentInvoice.getDiscountAmount());
                    tv_netTotal.setText(""+currentInvoice.getBillAmount());
                    tv_payment.setText(""+currentInvoice.getBillAmount()+"("+currentInvoice.getCurrency()+")");
                    float dueAmount = currentInvoice.getBillAmount() - currentInvoice.getPaymentAmount();
                    tv_due_amount.setText(""+dueAmount);
                    tv_payCurrency.setText(currentInvoice.getCurrency());
                    tv_cash_paid.setText(""+currentInvoice.getPaymentAmount());
                    tv_billAmount.setText(""+currentInvoice.getBillAmount());

                    int itemCount = 0;
                    for (InvoiceItemHistory item:billedItemsList){
                        itemCount += item.getQuantity();
                    }
                    tv_totalItems.setText(""+itemCount);


                    InvoiceItemsAdapter invoiceItemsAdapter = new InvoiceItemsAdapter(billedItemsList);
                    rv_billedItems.setAdapter(invoiceItemsAdapter);

                    //flag to verify whether to show or hide QR Code
                    boolean isShowQr = true;
                    //show qr code only if country is saudi arabia
                    //hide for all other countries
                    if(billingCountryId!=Constants.COUNTRY_SAUDI_ARABIA){
                        iv_qrcode.setVisibility(View.GONE);
                        isShowQr = false;
                    }

                    //To generate qr code , requires seller details
                    //check whether saved seller details available.
                    //if flag is false ,then qr code is hidden.So no need to generate qr code
                    if(savedCompanyDetails!=null && isShowQr) {

                        String dateTime = DateTimeUtils.getCurrentDateTimeInvoice();
                        String sellerName = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)?savedCompanyDetails.getCompanyNameInEng():savedCompanyDetails.getCompanyNameInArb());
                        try {
                            getZatcaQr(sellerName, savedCompanyDetails.getTaxNumber(), currentInvoice.getGrossAmount(), currentInvoice.getTaxAmount(), dateTime);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else {

                        if(isShowQr) {
                            AppDialogs appDialogs = new AppDialogs(BillViewActivity.this);
                            appDialogs.showCommonAlertDialog(getString(R.string.update_seller_details), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //do nothing...
                                }
                            });
                        }
                    }

                    //check whether payment type is credit sale
                    //boolean isCreditSale = (currentInvoice.getPaymentType() == Constants.PAY_TYPE_CREDIT_SALE);


                    /*
                    //get the balance amount to pay
                    float balance = currentInvoice.getBillAmount() - currentInvoice.getPaymentAmount();

                    //To find invoice if fully paid , check the balance
                    boolean isFullyPaid = !(balance>0);*/

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show the saved company details on top
     * */
    private void updateCompanyAddress(List<CompanyAddressEntity> cmpnyAddressList){
        runOnUiThread(() -> {
            try {

                if(cmpnyAddressList!=null && !cmpnyAddressList.isEmpty()){
                    companyAddressEntity = cmpnyAddressList.get(0);
                    //display values
                    String companyName = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)?companyAddressEntity.getCompanyNameEng():companyAddressEntity.getCompanyNameAr());

                    tv_companyName.setText(companyName);
                    tv_companyPhone.setText("Ph : "+ companyAddressEntity.getMobile());
                    String emailPrefix = getString(R.string.email) + " : ";
                    tv_companyEmail.setText(emailPrefix + companyAddressEntity.getEmail());
                    tv_companyAddress.setText(companyAddressEntity.getAddress().trim());
                }else {
                    showToast(getString(R.string.company_address_missing), BillViewActivity.this);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * generate zatca data
     * */
    private void getZatcaQr(String sellerName, String taxNumber, float total, float taxAmount, String date){
        try {

            String qrData = ZatcaDataGenerator.getZatca(sellerName, taxNumber, date, String.valueOf(total),String.valueOf(taxAmount));
            printerQrData = qrData;
            generateQrBitmap(qrData);

            if(printerQrData!=null && printerQrData.equals(Constants.EMPTY)){
                AppDialogs appDialogs = new AppDialogs(BillViewActivity.this);
                appDialogs.showCommonAlertDialog(getString(R.string.zatca_qr_failed), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //do nothing...
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to generate qr code from zatca base64 string
     * @param base64String
     * */
    private void generateQrBitmap(String base64String){
        try {

            //generate qr code from data and display in bill
            Bitmap bitmap = new BarcodeEncoder().encodeBitmap(base64String, BarcodeFormat.QR_CODE, 400, 400);
            iv_qrcode.setImageBitmap(bitmap);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to select a method for receipt printing
     * */
    private void printReceipt(){
        try {
            //check if not connected to bluetooth printer
            if (!BluetoothUtil.isBlueToothPrinter) {
                //print with sunmi device inbuilt printing interface api
                printByInBuiltApiInterface();
            } else {
                //if bluetooth connected,
                printByBlueTooth();
            }

        }catch (Exception e){
            showToast("sales print error", this);
            e.printStackTrace();
        }
    }

    /**
     * to use the sunmi device inbuilt printing api interface
     * */
    private void printByInBuiltApiInterface(){
        try {
            //using sunmi printing interface to print
            //prepares printing data and prints
            SunmiPrintHelper.getInstance().printTransaction_sales(BillViewActivity.this,currentInvoice,companyAddressEntity,printItemsList,printerQrData, new InnerResultCallback() {
                @Override
                public void onRunResult(boolean isSuccess) throws RemoteException {
                }

                @Override
                public void onReturnString(String result) throws RemoteException {
                }

                @Override
                public void onRaiseException(int code, String msg) throws RemoteException {
                }

                @Override
                public void onPrintResult(int code, String msg) throws RemoteException {
                }
            });

        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * to print via bluetooth printer
     * */
    private void printByBlueTooth() {
        BluetoothUtil.printSalesInvoice(BillViewActivity.this, currentInvoice, null, printItemsList, null);
    }
}