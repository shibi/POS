package com.rpos.pos.presentation.ui.dashboard;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.rpos.pos.Config;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.category.list.CategoryListActivity;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.customer.list.CustomerListActivity;
import com.rpos.pos.presentation.ui.dashboard.fragment.HomeCardFragment;
import com.rpos.pos.presentation.ui.dashboard.fragment.HomeListFragment;
import com.rpos.pos.presentation.ui.item.list.ItemActivity;
import com.rpos.pos.presentation.ui.price_variations.itemprice.list.ItemPriceListActivity;
import com.rpos.pos.presentation.ui.paymentmodes.list.PaymentModeListActivity;
import com.rpos.pos.presentation.ui.price_variations.pricelist.list.PriceListActivity;
import com.rpos.pos.presentation.ui.purchase.list.PurchaseActivity;
import com.rpos.pos.presentation.ui.sales.sales_list.SalesActivity;
import com.rpos.pos.presentation.ui.settings.SettingsActivity;
import com.rpos.pos.presentation.ui.shift.ShiftActivity;
import com.rpos.pos.presentation.ui.supplier.lsit.SuppliersListActivity;
import com.rpos.pos.presentation.ui.taxes.list.TaxesActivity;
import com.rpos.pos.presentation.ui.units.list.UOMListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DashboardActivity extends SharedActivity{

    private RadioGroup rg_switch_tab;
    private String currentFragmentTag = "";
    private AppDialogs appDialogs;
    private ImageView iv_appLogo;

    @Override
    public int setUpLayout() {
        return R.layout.activity_dashboard;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rg_switch_tab = findViewById(R.id.rb_switchTab);
        appDialogs = new AppDialogs(this);
        iv_appLogo = findViewById(R.id.iv_dash_logo);

        RadioButton rb_left = findViewById(R.id.rb_left);
        rb_left.setChecked(true);

        //get the saved currency details
        getSavedDefaultCurrency();


        rg_switch_tab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                try {

                    switch (i)
                    {
                        case R.id.rb_left:
                            loadFragment(new HomeListFragment(), Constants.FRAGMENT_HOME_LIST);
                            break;
                        case R.id.rb_right:
                            loadFragment(new HomeCardFragment(), Constants.FRAGMENT_HOME_CARD);
                            break;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        int home_layout = SharedPrefHelper.getInstance(DashboardActivity.this).getHomeScreenDefaultLayout();
        if(home_layout == -1 || home_layout == Constants.HOME_SCREEN_LIST) {
            loadFragment(new HomeListFragment(), Constants.FRAGMENT_HOME_LIST);
            //rg_switch_tab.check(R.id.rb_left);
        }else {
            loadFragment(new HomeCardFragment(), Constants.FRAGMENT_HOME_CARD);
            //rg_switch_tab.check(R.id.rb_right);
        }

        //get custom logo available
        getAvailableCustomLogo();
    }

    /**
     *  to retrieve default currency and set to core app
     * */
    private void getSavedDefaultCurrency(){
        try {
            String defaultCurrencySymbol = SharedPrefHelper.getInstance(this).getDefaultCurrencySymbol();
            ((CoreApp) getApplication()).setDefaultCurrency(defaultCurrencySymbol);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    /**
     * check custom logo available and show
     * */
    private void getAvailableCustomLogo(){
        try {

            //check saved logo path available
            String appIconPath = SharedPrefHelper.getInstance(this).getAppLogoPath();
            //check whether icon path is valid
            if(appIconPath != null && !appIconPath.isEmpty()){
                //load saved image from external storage
                Bitmap logoBitmap = getCoreApp().loadImageFromStorage(appIconPath);
                //check image is valid
                if(logoBitmap!= null){
                    //get the logo view
                    ImageView iv_logo = findViewById(R.id.iv_dash_logo);
                    //display image in logo view
                    iv_logo.setImageBitmap(logoBitmap);
                }
            }

        }catch (Exception e){
            throw e;
        }
    }

    public void gotoSalesActivity(){
        Intent salesIntent = new Intent(this, SalesActivity.class);
        startActivity(salesIntent);
    }
    public void gotoItemActivity(){
        Intent itemIntent = new Intent(this, ItemActivity.class);
        startActivity(itemIntent);
    }
    public void gotoSettingsActivity(){
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
        finish();
    }

    public void gotoShiftActivity(){
        Intent shiftIntent = new Intent(this, ShiftActivity.class);
        startActivity(shiftIntent);
    }

    public void gotoCustomerListActivity(){
        Intent customerListActivity = new Intent(this, CustomerListActivity.class);
        startActivity(customerListActivity);
    }

    public void gotoCategoryListActivity(){
        Intent categoryListActivity = new Intent(this, CategoryListActivity.class);
        startActivity(categoryListActivity);
    }

    public void gotoPurchaseActivity(){
        Intent purchaseIntent = new Intent(this, PurchaseActivity.class);
        startActivity(purchaseIntent);
    }

    public void gotoUOMListActivity(){
        Intent uomListActivity = new Intent(this, UOMListActivity.class);
        startActivity(uomListActivity);
    }

    public void gotoPaymentModeListActivity(){
        Intent paymentModeListActivity = new Intent(this, PaymentModeListActivity.class);
        startActivity(paymentModeListActivity);
    }

    public void gotoTaxesListActivity(){
        Intent taxListIntent = new Intent(this, TaxesActivity.class);
        startActivity(taxListIntent);
    }

    public void gotoSupplierListActivity(){
        Intent supplierList = new Intent(this, SuppliersListActivity.class);
        startActivity(supplierList);
    }

    public void gotoPriceListActivity(){
        Intent priceList = new Intent(this, PriceListActivity.class);
        startActivity(priceList);
    }

    public void gotoItemPriceListActivity(){
        Intent itemPriceList = new Intent(this, ItemPriceListActivity.class);
        startActivity(itemPriceList);
    }


    public void loadFragment(Fragment fragment, String tag) {
        try {

            currentFragmentTag = tag;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, fragment, tag)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if(currentFragmentTag!=null && (currentFragmentTag.equals(Constants.FRAGMENT_HOME_LIST) || currentFragmentTag.equals(Constants.FRAGMENT_HOME_CARD))){
            appDialogs.showExitAlertDialog(new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    finish();
                }

                @Override
                public void onClickNegetive(String id) {

                }
            });
        }
    }
}