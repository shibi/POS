package com.rpos.pos.presentation.ui.sales.bill;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.rpos.pos.presentation.ui.common.SharedActivity;

import org.apache.commons.codec.binary.Hex;

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
    private AppCompatTextView tv_grossTotal,tv_taxAmount, tv_netTotal,tv_discountAmount, tv_payment,tv_due_amount, tv_totalItems, tv_payCurrency;
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

    private Printing printing = null;
    PrintingCallback printingCallback=null;

    private CompanyAddressEntity companyAddressEntity;
    private AppDialogs printerProgress;


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
        btn_print = findViewById(R.id.btn_print);
        tv_companyName = findViewById(R.id.tv_companyName);
        tv_companyPhone = findViewById(R.id.tv_companyPhone);
        tv_companyEmail = findViewById(R.id.tv_companyEmail);
        tv_companyAddress = findViewById(R.id.tv_companyAddress);

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

        currentInvoice = null;

        //back press
        btn_back.setOnClickListener(view -> onBackPressed());

        //get the current invoice with id
        getCurrentInvoice();

        if (Printooth.INSTANCE.hasPairedPrinter()) {
            printing = Printooth.INSTANCE.printer();
        }

        btn_print.setOnClickListener(view -> {
            try{

                if (!Printooth.INSTANCE.hasPairedPrinter())
                    startActivityForResult(new Intent(this, ScanningActivity.class ),ScanningActivity.SCANNING_FOR_PRINTER);
                else {
                    printReceipt();
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //init bluetooth printer
        initListeners();
    }

    @Override
    public void initObservers() {

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

    /*public void generateZatcaBase64(String sellerName, String taxNumber, float total,float taxAmount,String date) throws DecoderException {

        String tag1 = getHexString(1, sellerName);
        String tag2 = getHexString(2, taxNumber);
        String tag3 = getHexString(3, date);
        String tag4 = getHexString(4, ""+total);
        String tag5 = getHexString(5, ""+taxAmount);

        String finalString = tag1 + tag2 + tag3 + tag4 + tag5;
        byte[] decodedHex = Hex.decodeHex(finalString.toCharArray());
        String result = Base64.encodeBase64String(decodedHex);
        System.out.println("==> " + result);
        generateQr(result);

    }*/

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

                    tv_billTo.setText(""+currentInvoice.getCustomerName());
                    tv_billNo.setText("INV#"+invoiceId);
                    tv_billDate.setText(currentInvoice.getDate());
                    tv_billType.setText("Sales invoice");
                    tv_grossTotal.setText(""+currentInvoice.getGrossAmount());
                    tv_billCurrency.setText(currentInvoice.getCurrency());
                    tv_taxAmount.setText(""+currentInvoice.getTaxAmount());
                    tv_discountAmount.setText(""+currentInvoice.getDiscountAmount());
                    tv_netTotal.setText(""+currentInvoice.getBillAmount());
                    tv_payment.setText(""+currentInvoice.getBillAmount()+"("+currentInvoice.getCurrency()+")");
                    float dueAmount = currentInvoice.getBillAmount() - currentInvoice.getPaymentAmount();
                    tv_due_amount.setText(""+dueAmount);
                    tv_payCurrency.setText(currentInvoice.getCurrency());

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

    private void initListeners() {
        if (printing!=null && printingCallback==null) {
            Log.d("xxx", "initListeners ");
            printingCallback = new PrintingCallback() {

                public void connectingWithPrinter() {
                    Toast.makeText(getApplicationContext(), "Connecting with printer", Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "Connecting");
                    printerProgress.showProgressBar("Connecting with printer");
                }
                public void printingOrderSentSuccessfully() {
                    Toast.makeText(getApplicationContext(), "printingOrderSentSuccessfully", Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "printingOrderSentSuccessfully");
                    printerProgress.hideProgressbar();
                }
                public void connectionFailed(@NonNull String error) {
                    Toast.makeText(getApplicationContext(), "connectionFailed :"+error, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "connectionFailed : "+error);
                    printerProgress.hideProgressbar();
                }
                public void onError(@NonNull String error) {
                    Toast.makeText(getApplicationContext(), "onError :"+error, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "onError : "+error);
                    printerProgress.hideProgressbar();
                }
                public void onMessage(@NonNull String message) {
                    Toast.makeText(getApplicationContext(), "onMessage :" +message, Toast.LENGTH_SHORT).show();
                    Log.d("xxx", "onMessage : "+message);
                    printerProgress.hideProgressbar();
                }
            };

            Printooth.INSTANCE.printer().setPrintingCallback(printingCallback);
        }
    }


    private void printReceipt(){

        if (printing!=null) {
            //printing.print(getSomePrintables());
            Printooth.INSTANCE.printer().print(getSomePrintables());
        }

    }

    private ArrayList<Printable> getSomePrintables() {

        ArrayList<Printable> al = new ArrayList<>();
        al.add(new RawPrintable.Builder(new byte[]{27, 100, 4}).build()); // feed lines example in raw mode

        //---------------------------------------------------------------------------0

        String title = "POS";
        String mobile = "+909876543210";
        String email = "Email : email@gmail.com";
        String billTo = "To : "+currentInvoice.getCustomerName();
        String invoiceNo = "Invoice no : "+invoiceId;
        String billType = "Bill type : Sales invoice";
        String billDate = "Bill date : "+currentInvoice.getDate();
        String currency = "Currency : "+currentInvoice.getCurrency();
        String lineDraw = "-----------------------------";
        String item_title = "item";
        String quantity_title = "quantity";
        String rate_title = "rate";
        String total_items = "Total items  "+tv_totalItems.getText().toString();
        String total = "Total "+currentInvoice.getGrossAmount();
        String tax = "Tax "+currentInvoice.getTaxAmount();
        String discount = "Discount "+currentInvoice.getDiscountAmount();
        String netAmount = "Net amount  "+currentInvoice.getBillAmount();
        String payment_title = "Payment";

        //String tableTitle = String.format("%.15s %5d %10.2f\n", item_title, quantity_title, rate_title);



        //Title
        al.add( (new TextPrintable.Builder())
                .setText(title)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //Mobile
        al.add( (new TextPrintable.Builder())
                .setText(mobile)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());
        //Email
        al.add( (new TextPrintable.Builder())
                .setText(email)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //Line separator
        al.add( (new TextPrintable.Builder())
                .setText(lineDraw)  //--------------------------------------------------------
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //TO
        al.add( new TextPrintable.Builder()
                .setText(billTo)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Invoice no
        al.add( new TextPrintable.Builder()
                .setText(invoiceNo)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Bill type
        al.add( new TextPrintable.Builder()
                .setText(billType)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //currency
        al.add( new TextPrintable.Builder()
                .setText(currency)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Bill date
        al.add( new TextPrintable.Builder()
                .setText(billDate)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Line separator
        al.add( (new TextPrintable.Builder())
                .setText(lineDraw)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());


        //dummy
        al.add( new TextPrintable.Builder()
                .setText("Items loading ")
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());


        //Line separator
        al.add( (new TextPrintable.Builder())
                .setText(lineDraw)  //--------------------------------------------------------
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //Total items
        al.add( new TextPrintable.Builder()
                .setText(total_items)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Line separator
        al.add( (new TextPrintable.Builder())
                .setText(lineDraw)  //--------------------------------------------------------
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //Total
        al.add( new TextPrintable.Builder()
                .setText(total)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Tax
        al.add( new TextPrintable.Builder()
                .setText(tax)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());


        //Discount
        al.add( new TextPrintable.Builder()
                .setText(discount)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Line separator
        al.add( (new TextPrintable.Builder())
                .setText(lineDraw)  //--------------------------------------------------------
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //Net amount
        al.add( new TextPrintable.Builder()
                .setText(netAmount)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());

        //Net amount
        al.add( new TextPrintable.Builder()
                .setText(payment_title)
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_LEFT())
                .setNewLinesAfter(1)
                .build());


        //Line separator
        al.add( (new TextPrintable.Builder())
                .setText(lineDraw)  //--------------------------------------------------------
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_PC1252())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setNewLinesAfter(1)
                .build());

        //---------------------------------------------------------------------------1

        //ORIGINAL DATA FROM SAMPLE PROJECT. REFER THIS. DO NOT DELETE
        /*al.add( (new TextPrintable.Builder())
                .setText("Hello World")
                .setLineSpacing(DefaultPrinter.Companion.getLINE_SPACING_60())
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setNewLinesAfter(1)
                .build());

        al.add( (new TextPrintable.Builder())
                .setText("Hello World")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_RIGHT())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setNewLinesAfter(1)
                .build());

        al.add( (new TextPrintable.Builder())
                .setText("اختبار العربية")
                .setAlignment(DefaultPrinter.Companion.getALIGNMENT_CENTER())
                .setEmphasizedMode(DefaultPrinter.Companion.getEMPHASIZED_MODE_BOLD())
                .setFontSize(DefaultPrinter.Companion.getFONT_SIZE_NORMAL())
                .setUnderlined(DefaultPrinter.Companion.getUNDERLINED_MODE_ON())
                .setCharacterCode(DefaultPrinter.Companion.getCHARCODE_ARABIC_FARISI())
                .setNewLinesAfter(1)
                .setCustomConverter(new ArabicConverter()) // change only the converter for this one
                .build());*/

        return al;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("xxx", "onActivityResult "+requestCode);

        if (requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode == Activity.RESULT_OK) {
            printing = Printooth.INSTANCE.printer();
            initListeners();
            printReceipt();
        }
    }
}