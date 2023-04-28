package com.rpos.pos.presentation.ui.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Config;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CurrencyItem;
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.domain.models.country.CountryItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.sunmi_printer_utils.BluetoothUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.ESCUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;
import com.rpos.pos.presentation.ui.common.PrintableContentActivity;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.login.LoginActivity;
import com.rpos.pos.presentation.ui.settings.adapter.pricelist.BuyPriceListSpinnerAdapter;
import com.rpos.pos.presentation.ui.settings.adapter.pricelist.SellPriceListSpinnerAdapter;
import com.rpos.pos.presentation.ui.settings.company_address.CompanyAddressActivity;
import com.rpos.pos.presentation.ui.settings.company_details.CompanyDetailsActivity;
import com.rpos.pos.presentation.ui.customer.select.CustomerSelectActivity;
import com.rpos.pos.presentation.ui.dashboard.DashboardActivity;
import com.rpos.pos.presentation.ui.settings.adapter.CountrySpinnerAdapter;
import com.rpos.pos.presentation.ui.settings.adapter.CurrencySpinnerAdapter;
import com.rpos.pos.presentation.ui.settings.data_backup.BackupActivity;

import org.apache.poi.ss.formula.ptg.Area2DPtgBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class SettingsActivity extends PrintableContentActivity {

    private AppCompatButton btn_cmpny_dtls_edit;
    private Spinner currencySpinner;
    private Spinner countrySpinner;
    private MaterialButtonToggleGroup toggleGroup_homelayout;
    private RadioGroup rg_language;
    private LinearLayout ll_back , ll_dflt_cust_add, ll_remove_dflt_cust;
    private LinearLayout ll_logout;
    private AppCompatTextView tv_defaultCustName;
    //private AppCompatButton btn_BT_pair;
    private AppCompatButton btn_icon_open;
    private AppCompatButton btn_bill_address_edit;
    private AppCompatTextView tv_selected_printer_method;

    private CurrencySpinnerAdapter currencySpinnerAdapter;
    private CountrySpinnerAdapter countrySpinnerAdapter;

    private int defaultCurrencyId, defaultBillingCountryId;

    private SharedPrefHelper prefHelper;
    private int homeScreenLayout;
    private List<CurrencyItem> currencyList;
    private List<CountryItem> countryList;

    private Spinner sp_priceListBuy, sp_priceListSell;
    private List<PriceListEntity> buyPriceListArray, sellPriceListArray;
    private BuyPriceListSpinnerAdapter buyPriceListSpinnerAdapter;
    private SellPriceListSpinnerAdapter sellPriceListSpinnerAdapter;

    private int defaultBuyingPriceListId, defaultSellingPriceListId;

    private AppDialogs progressDialog;
    private AppDialogs appDialogs;

    private AppDatabase localDb;

    private static final int PICK_FROM_GALLARY = 203;
    private static final int permission_Read_data = 101;
    private static final int PICK_IMAGE_REQUEST = 102;

    @Override
    public int setUpLayout() {
        return R.layout.activity_settings;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        super.initViews();

        toggleGroup_homelayout = findViewById(R.id.mbToggleGroup);
        ll_back = findViewById(R.id.ll_back);
        currencySpinner = findViewById(R.id.sp_currency);
        countrySpinner = findViewById(R.id.sp_country);
        rg_language = findViewById(R.id.rg_lang);
        btn_cmpny_dtls_edit = findViewById(R.id.btn_edit);
        ll_dflt_cust_add = findViewById(R.id.ll_add_customer);
        tv_defaultCustName = findViewById(R.id.tv_dflt_custName);
        //btn_BT_pair = findViewById(R.id.btn_bt_pair);
        tv_selected_printer_method = findViewById(R.id.tv_print_conn_selected);
        sp_priceListBuy = findViewById(R.id.sp_buy_pricelist);
        sp_priceListSell = findViewById(R.id.sp_sell_pricelist);
        ll_remove_dflt_cust = findViewById(R.id.ll_remove_customer);
        ll_logout = findViewById(R.id.ll_rightMenu);
        btn_bill_address_edit = findViewById(R.id.btn_bill_edit);
        btn_icon_open = findViewById(R.id.btn_appicon_open);
        AppCompatButton btn_db_backup = findViewById(R.id.btn_backup_settings);


        localDb = getCoreApp().getLocalDb();

        appDialogs = new AppDialogs(this);
        progressDialog = new AppDialogs(this);

        //shared preference instance
        prefHelper = SharedPrefHelper.getInstance(this);

        //get default id
        defaultCurrencyId = prefHelper.getDefaultCurrency();
        //get billing country id
        defaultBillingCountryId = prefHelper.getBillingCountry();

        defaultBuyingPriceListId = prefHelper.getBuyingPriceListId();
        defaultSellingPriceListId = prefHelper.getSellingPriceListId();

        //get default home screen style
        homeScreenLayout = prefHelper.getHomeScreenDefaultLayout();

        if(homeScreenLayout!=-1){
            switch (homeScreenLayout){
                case Constants.HOME_SCREEN_LIST:
                    toggleGroup_homelayout.check(R.id.mbtn_list);
                    break;

                case Constants.HOME_SCREEN_CARD:
                    toggleGroup_homelayout.check(R.id.mbtn_card);
                    break;
            }
        }else {
            toggleGroup_homelayout.check(R.id.mbtn_list);
        }

        toggleGroup_homelayout.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if(isChecked){
                switch (checkedId)
                {
                    case R.id.mbtn_list:
                        saveSelectedDefaultHomeLayout(Constants.HOME_SCREEN_LIST);
                        break;
                    case R.id.mbtn_card:
                        saveSelectedDefaultHomeLayout(Constants.HOME_SCREEN_CARD);
                        break;
                }
            }
        });


        currencyList = new ArrayList<>();
        currencySpinnerAdapter = new CurrencySpinnerAdapter(this, currencyList);
        currencySpinner.setAdapter(currencySpinnerAdapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    CurrencyItem currency = currencyList.get(i);
                    defaultCurrencyId = currency.getId();
                    prefHelper.setDefaultCurrency(defaultCurrencyId, currency.getSymbol());
                    getCoreApp().setDefaultCurrency(currency.getSymbol());

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        countryList = new ArrayList<>();
        countrySpinnerAdapter = new CountrySpinnerAdapter(this, countryList);
        countrySpinner.setAdapter(countrySpinnerAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    defaultBillingCountryId = countryList.get(i).getId();
                    prefHelper.setBillingCountry(defaultBillingCountryId);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        buyPriceListArray = new ArrayList<>();
        buyPriceListSpinnerAdapter = new BuyPriceListSpinnerAdapter(this, buyPriceListArray);
        sp_priceListBuy.setAdapter(buyPriceListSpinnerAdapter);
        sp_priceListBuy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    defaultBuyingPriceListId = buyPriceListArray.get(i).getId();
                    prefHelper.setBuyingPriceList(defaultBuyingPriceListId);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sellPriceListArray = new ArrayList<>();
        sellPriceListSpinnerAdapter = new SellPriceListSpinnerAdapter(this, sellPriceListArray);
        sp_priceListSell.setAdapter(sellPriceListSpinnerAdapter);
        sp_priceListSell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    defaultSellingPriceListId = sellPriceListArray.get(i).getId();
                    prefHelper.setSellingPriceList(defaultSellingPriceListId);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        String selectedLang = prefHelper.getSelectedLang();
        if(selectedLang.equals(Constants.LANG_EN)){
            rg_language.check(R.id.rb_eng);
        }else {
            rg_language.check(R.id.rb_arb);
        }


        String[] defaultCustDetails = prefHelper.getDefaultCustomer();
        if(defaultCustDetails!=null && defaultCustDetails.length>2){
            if(!defaultCustDetails[0].isEmpty()){
                showCustomerButtonsAdd_Remove(false, defaultCustDetails[1]);
            }else {
                showCustomerButtonsAdd_Remove(true, getString(R.string.none));
            }
        }else {
            showCustomerButtonsAdd_Remove(true, getString(R.string.none));
        }

        //language switch
        rg_language.setOnCheckedChangeListener((radioGroup, radioButtonId) -> {
            try {

                switch (radioButtonId) {
                    case R.id.rb_eng:

                        CoreApp.DEFAULT_LANG = Constants.LANG_EN;

                        break;
                    case R.id.rb_arb:

                        CoreApp.DEFAULT_LANG = Constants.LANG_AR;

                        break;
                }

                saveLanguagePref();

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //BACK PRESS
        ll_back.setOnClickListener(view -> gotoDashboardActivity());

        //button click - company details
        btn_cmpny_dtls_edit.setOnClickListener(view -> {
            // go to company details screen
            gotoCompanyDetailsScreen();
        });

        btn_bill_address_edit.setOnClickListener(this::gotoBillAddress);

        //add default customer
        ll_dflt_cust_add.setOnClickListener(this::selectCustomer);

        //remove default customer
        ll_remove_dflt_cust.setOnClickListener(this::removeDefaultCustomer);


        tv_selected_printer_method.setOnClickListener(view -> {
            try {

                String connectMethod = getString(R.string.connect_method);

                final ListDialog listDialog = DialogCreater.createListDialog(SettingsActivity.this, connectMethod, getResources().getString(R.string.btn_cancel), new String[]{"Api", "Bluetooth"});
                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        setMethod(position);
                        listDialog.cancel();
                    }
                });
                listDialog.show();

                /*String pVersion = SunmiPrintHelper.getInstance().getPrinterVersion();
                String deviceModel = SunmiPrintHelper.getInstance().getDeviceModel();
                String details = getString(R.string.printer_already_connected) + " \n Printer version :"+pVersion+"\n printer model : "+deviceModel;*/

                //showToast(details, SettingsActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //logout call
        ll_logout.setOnClickListener(this::logoutConfirmation);

        //app icon view
        btn_icon_open.setOnClickListener(this::showFileChoose);

        //goto backup activity
        btn_db_backup.setOnClickListener(this::gotoBackupAndRestoreActivity);


        //initBluetoothView();

        //get currency list
        getCurrencyList();

        //get country list
        getCountryList();

        //get all price list for spinner
        getPriceList();

        //to show default selected printer
        if (BluetoothUtil.isBlueToothPrinter) {
            tv_selected_printer_method.setText(R.string.connection_bluetooth);
        }else{
            tv_selected_printer_method.setText(R.string.connection_api);
        }

        //for printer support
        setService();

    }

    /**
     * Configure printer control via Bluetooth or API
     */
    private void setMethod(int position){
        if(position == 0){
            BluetoothUtil.disconnectBlueTooth(SettingsActivity.this);
            BluetoothUtil.isBlueToothPrinter = false;
            //method api
            selectMethodAPI();

        }else{

            if(!BluetoothUtil.connectBlueTooth(SettingsActivity.this)){
                BluetoothUtil.isBlueToothPrinter = false;

                //select method api
                selectMethodAPI();

            }else{

                BluetoothUtil.isBlueToothPrinter = true;

                tv_selected_printer_method.setText(R.string.connection_bluetooth);
                //save printer method
                prefHelper.setPrinterConnectionMethod(Constants.PRINTER_METHOD_BLUETOOTH);
                showToast(getString(R.string.bluetooth_printer_connected));

                appDialogs.showBluetoothConnected(new AppDialogs.OnDualActionButtonClickListener() {
                    @Override
                    public void onClickPositive(String id) {
                        //Sample print clicked
                        printByBluTooth("This is a sample test print");
                    }

                    @Override
                    public void onClickNegetive(String id) {
                        //cancelled dialog
                    }
                });
            }
        }
    }

    /**
     * API method selected
     * */
    private void selectMethodAPI(){
        try {

            //save printer method
            prefHelper.setPrinterConnectionMethod(Constants.PRINTER_METHOD_API);

            //change the button label
            tv_selected_printer_method.setText(R.string.connection_api);

            //show toast message
            showToast(getString(R.string.selected_api));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  Set print service connection status
     */
    private void setService(){
        if(SunmiPrintHelper.getInstance().sunmiPrinter == SunmiPrintHelper.FoundSunmiPrinter){
            Log.e("---------","found printer");
        }else if(SunmiPrintHelper.getInstance().sunmiPrinter == SunmiPrintHelper.CheckSunmiPrinter){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.e("---------","check restart");
                    setService();
                }
            }, 2000);
        }else if(SunmiPrintHelper.getInstance().sunmiPrinter == SunmiPrintHelper.LostSunmiPrinter){
            Log.e("---------","lost printer");
        } else{
            Log.e("---------","all else");
        }
    }

    /**
     * sample print code
     * */
    private void printByBluTooth(String content) {
        try {
            boolean isBold = false;
            boolean isUnderLine = false;
            int record = 17;
            String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "CP928", "Windows-1252",
                    "CP866", "CP852", "CP858", "CP874", "Windows-775", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};

            if (isBold) {
                BluetoothUtil.sendData(ESCUtil.boldOn());
            } else {
                BluetoothUtil.sendData(ESCUtil.boldOff());
            }

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

    /**
     * to show the default saved currency in spinner at the beginning
     * */
    private void showDefaultSelectedCurrency(){
        try {

            if(defaultCurrencyId != Constants.EMPTY_INT){
                for (int i=0;i<currencyList.size();i++){
                    if(currencyList.get(i).getId() == defaultCurrencyId){
                        currencySpinner.setSelection(i,false);
                        break;
                    }
                }
            }

            progressDialog.hideProgressbar();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show the default saved currency in spinner at the beginning
     * */
    private void showDefaultBillingCountry(){
        try {

            if(defaultBillingCountryId != Constants.EMPTY_INT){
                for (int i=0;i<countryList.size();i++){
                    if(countryList.get(i).getId() == defaultBillingCountryId){
                        countrySpinner.setSelection(i,false);
                        break;
                    }
                }
            }


            progressDialog.hideProgressbar();

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void initObservers() {

    }


    public void getCurrencyList(){

        try {

            progressDialog.showProgressBar();

            currencyList.addAll(getCoreApp().getCurrencyList());
            currencySpinnerAdapter.notifyDataSetChanged();

            showDefaultSelectedCurrency();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getCountryList(){
        try {

            progressDialog.showProgressBar();

            countryList.addAll(getCoreApp().getCountryList());
            countrySpinnerAdapter.notifyDataSetChanged();

            showDefaultBillingCountry();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to switch language
     * */
    private void saveLanguagePref(){
        try {

            prefHelper.setSelectedLang(CoreApp.DEFAULT_LANG);
            CoreApp.isRefreshRequired = true;

            recreate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveSelectedDefaultHomeLayout(int layout){
        try {

            prefHelper.setDefaultHomeScreenLayout(layout);

        }catch (Exception e){

        }
    }

    /**
     * to redirect to backup activity
     * */
    private void gotoBackupAndRestoreActivity(View view){
        Intent backupIntent = new Intent(this, BackupActivity.class);
        startActivity(backupIntent);
    }

    /**
     * prompt user for logout
     * */
    private void logoutConfirmation(View view){
        try {
            //show confirmation dialog
            new AppDialogs(this).showCommonDualActionAlertDialog(getString(R.string.logout), getString(R.string.logout_confirmation), new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                        //logout
                        logout();

                        //redirect to login screen
                        Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);

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

    private void logout(){
        //clear all saved values in shared preference
        getCoreApp().logoutSession();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("xxx", "onActivityResult "+requestCode);

        if(requestCode == PICK_IMAGE_REQUEST) {
            if(resultCode == RESULT_OK){
                if(data!=null && data.getData()!=null){
                    Uri filePath = data.getData();
                    try{

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        if(bitmap!=null) {
                            String path = saveImage(bitmap);
                            prefHelper.setAppLogoPath(path);
                            AppDialogs appDialogs = new AppDialogs(SettingsActivity.this);
                            //show success
                            appDialogs.showCommonSuccessDialog(getString(R.string.log_save_success), null);
                        }else {
                            showToast(getString(R.string.no_image));
                        }

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void showFileChoose(View view) {
        try {
            if (checkReadStoragePermissionAvailable()) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean checkReadStoragePermissionAvailable() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, permission_Read_data);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    /*public String getPath(Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }*/

    /**
     * select customer
     * */
    private void selectCustomer(View view){
        Intent selectIntent = new Intent(SettingsActivity.this, CustomerSelectActivity.class);
        launchCustomerPicker.launch(selectIntent);
    }

    private ActivityResultLauncher<Intent> launchCustomerPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();
                if(data!=null){

                    String customerId = data.getStringExtra(Constants.CUSTOMER_ID);
                    String customerName = data.getStringExtra(Constants.CUSTOMER_NAME);
                    String customerMobile = data.getStringExtra(Constants.CUSTOMER_PHONE);
                    prefHelper.setDefaultCustomer(customerId, customerName, customerMobile);
                    //hide add button and show customer name in display
                    showCustomerButtonsAdd_Remove(false, customerName);
                }
            }
        }
    });

    /**
     * to show or hide default customer add / remove button
     * */
    private void showCustomerButtonsAdd_Remove(boolean isShowAdd, String displayText){
        try {

            if(isShowAdd){
                ll_dflt_cust_add.setVisibility(View.VISIBLE);
                ll_remove_dflt_cust.setVisibility(View.GONE);
            }else {
                ll_dflt_cust_add.setVisibility(View.GONE);
                ll_remove_dflt_cust.setVisibility(View.VISIBLE);
            }

            tv_defaultCustName.setText(displayText);

        }catch (Exception e){
            throw e;
        }
    }

    private void removeDefaultCustomer(View v){
        try {

            prefHelper.setDefaultCustomer("","","");
            showCustomerButtonsAdd_Remove(true, getString(R.string.none));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getPriceList(){
        try {

            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    List<PriceListEntity> allPricesList = localDb.priceListDao().getAllPriceList();
                    buyPriceListArray.clear();
                    sellPriceListArray.clear();
                    for (PriceListEntity priceListEntity:allPricesList){
                        if(priceListEntity.getPriceType() == 0){
                            buyPriceListArray.add(priceListEntity);
                        }else {
                            sellPriceListArray.add(priceListEntity);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buyPriceListSpinnerAdapter.notifyDataSetChanged();
                            sellPriceListSpinnerAdapter.notifyDataSetChanged();

                            //TO show default saved id in spinner
                            //check there is default price list id for BUYING
                            if(defaultBuyingPriceListId != Constants.EMPTY_INT){
                                //loop through the list and find the position and manually select the default item
                                for(int i=0; i< buyPriceListArray.size(); i++){
                                    if(buyPriceListArray.get(i).getId() == defaultBuyingPriceListId){
                                        sp_priceListBuy.setSelection(i);
                                        break;
                                    }
                                }
                            }

                            //check there is default price list id for SELLING
                            if(defaultSellingPriceListId != Constants.EMPTY_INT){
                                //loop through the list and find the position and manually select the default item
                                for (int i=0;i< sellPriceListArray.size();i++){
                                    if(sellPriceListArray.get(i).getId() == defaultSellingPriceListId){
                                        sp_priceListSell.setSelection(i);
                                        break;
                                    }
                                }
                            }

                        }
                    });
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private String saveImage(Bitmap bitmap){
        try {

            File filefolder;
            filefolder = new File(this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), Config.LOGO_DIRECTORY);

            //if the folder does not exist, create a new folder to save the file
            if (!filefolder.exists()){
                filefolder.mkdirs();
            }

            String imageName = Config.APP_LOGO_NAME;
            Log.e("------------------","image Name "+imageName);

            //file path to for saving the user key
            String filePath =  filefolder+"/"+imageName; // External is the text file et_name

            //initialise the file
            File logoImage = new File(filePath);

            //create new file, if it is null
            if (logoImage == null) {
                logoImage.mkdir();
            }


            try (FileOutputStream out = new FileOutputStream(logoImage)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
                out.flush();
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return filefolder.getAbsolutePath();
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
    }

    private void gotoBillAddress(View view){
        Intent intent = new Intent(SettingsActivity.this, CompanyAddressActivity.class);
        startActivity(intent);
    }

    /**
     * to goto company details entering screen
     * */
    private void gotoCompanyDetailsScreen(){
        try {

            Intent cdIntent = new Intent(SettingsActivity.this, CompanyDetailsActivity.class);
            startActivity(cdIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoDashboardActivity(){
        Intent dashboardIntent = new Intent(this, DashboardActivity.class);
        startActivity(dashboardIntent);
        finish();
    }

    private void showToast(String msg){
        showToast(msg, SettingsActivity.this);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        gotoDashboardActivity();
    }


}