package com.rpos.pos.presentation.ui.purchase.bill;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
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
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceItemHistory;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.ZatcaDataGenerator;
import com.rpos.pos.domain.utils.sunmi_printer_utils.BluetoothUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.ESCUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.sales.bill.BillViewActivity;
import com.sunmi.peripheral.printer.InnerResultCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseBillView extends SharedActivity {

    private AppCompatTextView tv_billTo,tv_billNo,tv_billDate, tv_billType,tv_billCurrency;
    private AppCompatTextView tv_grossTotal,tv_taxAmount, tv_netTotal,tv_discountAmount, tv_payment,
            tv_due_amount, tv_totalItems, tv_payCurrency, tv_cash_paid, tv_billAmount;
    private AppCompatTextView tv_companyName, tv_companyPhone,tv_companyEmail,tv_companyAddress;
    private ImageView iv_qrcode;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private RecyclerView rv_billedItems;

    private PurchaseInvoiceEntity currentInvoice;
    private long invoiceId;
    private CompanyEntity savedCompanyDetails;
    private int billingCountryId;
    private SharedPrefHelper prefHelper;

    private AppCompatButton btn_back, btn_print;

    private CompanyAddressEntity companyAddressEntity;
    private AppDialogs printerProgress;

    private String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "CP928", "Windows-1252", "CP866", "CP852", "CP858", "CP874", "Windows-775", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};
    private int record;

    //For to pass to printing
    private List<PurchaseInvoiceItemHistory> printItemsList;
    private String printerQrData;

    @Override
    public int setUpLayout() {
        return R.layout.activity_purchase_bill_view;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        record = 17;

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
        btn_print = findViewById(R.id.btn_print);
        tv_companyName = findViewById(R.id.tv_companyName);
        tv_companyPhone = findViewById(R.id.tv_companyPhone);
        tv_companyEmail = findViewById(R.id.tv_companyEmail);
        tv_companyAddress = findViewById(R.id.tv_companyAddress);
        tv_cash_paid = findViewById(R.id.tv_cash_paid);
        tv_billAmount = findViewById(R.id.tv_billAmount);

        printerProgress = new AppDialogs(this);


        //shared preference instance
        prefHelper = SharedPrefHelper.getInstance(this);

        Intent data = getIntent();
        if(data!=null){
            invoiceId = data.getIntExtra(Constants.INVOICE_ID, Constants.EMPTY_INT);
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

        //add print button listener
        enablePrintButton();
    }

    /**
     * enable print button
     * enable only if invoice details are loaded
     * */
    private void enablePrintButton(){

        btn_print.setOnClickListener(view -> {
            try{

                //to prepare and print receipt
                print();

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

                    currentInvoice = localDb.purchaseInvoiceDao().getInvoiceWithId((int)invoiceId);

                    if(currentInvoice != null){
                        List<PurchaseInvoiceItemHistory> billedItemsList = localDb.purchaseInvoiceDao().getInvoiceItemsWithId(currentInvoice.getId());
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
                        runOnUiThread(() -> showToast(getString(R.string.invoice_notfound), PurchaseBillView.this));
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
    private void updateValuesToUi(List<PurchaseInvoiceItemHistory> billedItemsList){
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
                        _billType = COLON_SEPARATOR + getString(R.string.purchase_label);
                        _billCurrency = COLON_SEPARATOR + currentInvoice.getCurrency();
                    }else {
                        _billToCustomer = currentInvoice.getCustomerName() + COLON_SEPARATOR;
                        _billNo =  invoiceId +"INV#"+ COLON_SEPARATOR;
                        _billDate = COLON_SEPARATOR + date ;
                        _billType = COLON_SEPARATOR + getString(R.string.purchase_label);
                        _billCurrency = COLON_SEPARATOR + currentInvoice.getCurrency();
                    }

                    tv_billTo.setText(_billToCustomer);
                    tv_billNo.setText(_billNo);
                    tv_billDate.setText(_billDate);
                    tv_billType.setText(_billType);
                    tv_billCurrency.setText(_billCurrency);

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
                    for (PurchaseInvoiceItemHistory item:billedItemsList){
                        itemCount += item.getQuantity();
                    }
                    tv_totalItems.setText(""+itemCount);


                    PurchaseInvoiceItemsAdapter invoiceItemsAdapter = new PurchaseInvoiceItemsAdapter(billedItemsList);
                    rv_billedItems.setAdapter(invoiceItemsAdapter);


                    //flag to verify whether to show or hide QR Code
                    //boolean isShowQr = false;   //FORCEFULLY HIDING QR CODE. NO QR CODE REQUIRED FOR PURCHASE ( BY CLIENT )  (update by client -23-02-2023 )

                    if(savedCompanyDetails==null) {
                        AppDialogs appDialogs = new AppDialogs(PurchaseBillView.this);
                            appDialogs.showCommonAlertDialog(getString(R.string.update_seller_details), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //do nothing...
                                }
                            });
                        }


                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

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
                    showToast(getString(R.string.company_address_missing), PurchaseBillView.this);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private void getZatcaQr(String sellerName, String taxNumber, float total, float taxAmount, String date){
        try {

            String qrData = ZatcaDataGenerator.getZatca(sellerName, taxNumber, date, String.valueOf(total),String.valueOf(taxAmount));
            printerQrData = qrData;
            generateQr(qrData);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to generate qr code from zatca base64 string
     * @param base64String
     * */
    private void generateQr(String base64String){
        try {

            Bitmap bitmap = new BarcodeEncoder().encodeBitmap(base64String, BarcodeFormat.QR_CODE, 400, 400);
            iv_qrcode.setImageBitmap(bitmap);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to prepare printing receipt and
     * initiating print
     * */
    private void print(){
        try {

            //check if bluetooth printer not connected
            /*if (!BluetoothUtil.isBlueToothPrinter) {*/

                //using sunmi printer interface to print
                SunmiPrintHelper.getInstance().printTransaction_purchase(PurchaseBillView.this,currentInvoice,companyAddressEntity, printItemsList, printerQrData, new InnerResultCallback() {
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
            /*} else {
                //printByBluTooth(content);
            }*/


        }catch (Exception e){
            showToast("purchase print error", this);
            e.printStackTrace();
        }
    }

    private void printByBluTooth(String content) {
        try {
            Boolean isBold = false;
            if (isBold) {
                BluetoothUtil.sendData(ESCUtil.boldOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.boldOff());
            }

            Boolean isUnderLine = false;
            if (isUnderLine) {
                BluetoothUtil.sendData(ESCUtil.underlineWithOneDotWidthOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.underlineOff());
            }

            if (record < 17) {
                BluetoothUtil.sendData(ESCUtil.singleByte());
                BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(record)));
            } else {
                BluetoothUtil.sendData(ESCUtil.singleByteOff());
                BluetoothUtil.sendData(ESCUtil.setCodeSystem(codeParse(record)));
            }

            BluetoothUtil.sendData(content.getBytes(mStrings[record]));
            BluetoothUtil.sendData(ESCUtil.nextLine(3));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte codeParse(int value) {
        byte res = 0x00;
        switch (value) {
            case 0:
                res = 0x00;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                res = (byte) (value + 1);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                res = (byte) (value + 8);
                break;
            case 12:
                res = 21;
                break;
            case 13:
                res = 33;
                break;
            case 14:
                res = 34;
                break;
            case 15:
                res = 36;
                break;
            case 16:
                res = 37;
                break;
            case 17:
            case 18:
            case 19:
                res = (byte) (value - 17);
                break;
            case 20:
                res = (byte) 0xff;
                break;
            default:
                break;
        }
        return (byte) res;
    }


    //TODO : need to fix payment pending option not showing
}