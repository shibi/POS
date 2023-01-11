package com.rpos.pos.presentation.ui.sales.bill;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mazenrashed.printooth.Printooth;
import com.mazenrashed.printooth.data.printable.Printable;
import com.mazenrashed.printooth.data.printable.RawPrintable;
import com.mazenrashed.printooth.data.printable.TextPrintable;
import com.mazenrashed.printooth.data.printer.DefaultPrinter;
import com.mazenrashed.printooth.ui.ScanningActivity;
import com.mazenrashed.printooth.utilities.Printing;
import com.mazenrashed.printooth.utilities.PrintingCallback;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CompanyAddressEntity;
import com.rpos.pos.data.local.entity.CompanyEntity;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.ZatcaResponse;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.sunmi_printer_utils.BluetoothUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.ESCUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.sunmi.peripheral.printer.InnerResultCallback;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "CP928", "Windows-1252", "CP866", "CP852", "CP858", "CP874", "Windows-775", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};
    private int record;

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

        printerQrData = "Empty data";
        printItemsList = new ArrayList<>();
        currentInvoice = null;

        //back press
        btn_back.setOnClickListener(view -> onBackPressed());

        //get the current invoice with id
        getCurrentInvoice();

        btn_print.setOnClickListener(view -> {
            try{
                print();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initObservers() {

    }

    private void print(){
        try {

            String content = createBillableContent_En();
            String textFont = "test.ttf";

            float size = 24.0f;
            if (!BluetoothUtil.isBlueToothPrinter) {
                //SunmiPrintHelper.getInstance().printText(content, size, false, false, textFont);
                //SunmiPrintHelper.getInstance().feedPaper();
                SunmiPrintHelper.getInstance().printTransaction_sales(BillViewActivity.this,currentInvoice,companyAddressEntity,printItemsList,printerQrData, new InnerResultCallback() {
                    @Override
                    public void onRunResult(boolean isSuccess) throws RemoteException {
                        Log.e("-------------","1> "+isSuccess);
                    }

                    @Override
                    public void onReturnString(String result) throws RemoteException {
                        Log.e("-------------","2> ");
                    }

                    @Override
                    public void onRaiseException(int code, String msg) throws RemoteException {
                        Log.e("-------------","3> ");
                    }

                    @Override
                    public void onPrintResult(int code, String msg) throws RemoteException {
                        Log.e("-------------","4> "+code+" > "+msg);
                    }
                });
            } else {
                printByBluTooth(content);
            }


        }catch (Exception e){
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


    private String createBillableContent_En(){

        String line = getString(R.string.print_line);

        String companyName = "             POS              ";
        String company_address = "        Company address       ";
        String phoneNumber = "       Ph: +91 9876543210     ";
        String emailId = "    Email:email@gmail.com    ";

        if(companyAddressEntity != null){
            String tempString = companyAddressEntity.getCompanyNameEng();
            if(tempString!=null){

                if(tempString.length() > 31){
                    companyName = tempString.substring(0,30);
                }else {
                    int remainingSpace = 31 - tempString.length();
                    String preSpace = "";
                    String postSpace = "";
                    if(remainingSpace%2 == 0){
                        preSpace = getLineFilledSpaces((int) remainingSpace/2);
                        postSpace = preSpace;
                    }else {
                        preSpace = getLineFilledSpaces((int) ((remainingSpace/2) -1));
                        postSpace = preSpace+" ";
                    }

                    companyName = preSpace + tempString + postSpace;
                }
            }
        }

        String company_header =  companyName +"\n"+ company_address + "\n" + phoneNumber + "\n" + emailId + "\n" +line;

        //----------------------------------------------------------------------------------------------------------------------------
        String billTo = getString(R.string.to_label) + ": "+ currentInvoice.getCustomerName();
        String invoiceNo = getString(R.string.invoice_no) + ": INV#"+ currentInvoice.getId();
        String billType = getString(R.string.bill_type) + ": Sales invoice";
        String currency = getString(R.string.currency) + ": "+ currentInvoice.getCurrency();
        String billDate = getString(R.string.bill_date) + ": "+ DateTimeUtils.convertTimerStampToDateTime(currentInvoice.getTimestamp());

        String billInfo = billTo + "\n" + invoiceNo + "\n" + billType + "\n" + currency + "\n" + billDate + "\n" + line;



        String tableHeader = "\nitem        rate   Qty   Total\n"+line+"\n";


        String finalString = company_header +"\n "+ tableHeader + "\n "+ billInfo;

        return finalString;
    }

    private String getLineFilledSpaces(int charCount){
        String fillSpace = "";
        for (int i=0;i<charCount;i++){
            fillSpace+=" ";
        }
        return fillSpace;
    }

    private void generateZatcaBase64(String sellerName, String taxNumber, float total,float taxAmount,String date){
        try {

            ApiService apiService = ApiGenerator.createZatcaApiService(ApiService.class);
            Map<String, String> params = new HashMap<String, String>();
            params.put("seller_name", sellerName);
            params.put("tax_number", taxNumber);
            params.put("total", ""+total);
            params.put("tax_amount", ""+taxAmount);
            params.put("date", date);


            Call<ZatcaResponse> call = apiService.generate(params);
            call.enqueue(new Callback<ZatcaResponse>() {
                @Override
                public void onResponse(Call<ZatcaResponse> call, Response<ZatcaResponse> response) {
                    Log.e("-----------", "sdfsdf>" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        ZatcaResponse response1 = response.body();
                        if (response1 != null) {

                            printerQrData = response1.getZatcaBase64();
                            Log.e("-----------", "res>" + response1.getZatcaBase64());
                            generateQr(response1.getZatcaBase64());
                        }
                    } else {
                        Log.e("-----------", "false>");
                    }
                }

                @Override
                public void onFailure(Call<ZatcaResponse> call, Throwable t) {
                    Log.e("-----------", "er>" + t.getMessage());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    static String getHexString(int tagNo, String tagValue) {
        String tagNumLengthHexString = Integer.toHexString(tagNo);
        int tagValueLength = tagValue.length();
        String tagValueLengthHexString = Integer.toHexString(tagValueLength);
        byte[] tagValueBytes = tagValue.getBytes(StandardCharsets.UTF_8);
        String tagValueHexString = Hex.encodeHexString(tagValueBytes);
        return (0 + tagNumLengthHexString) + (0 + tagValueLengthHexString) + tagValueHexString;
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
                            generateZatcaBase64(sellerName, savedCompanyDetails.getTaxNumber(), currentInvoice.getGrossAmount(), currentInvoice.getTaxAmount(), dateTime);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}