package com.rpos.pos.presentation.ui.dashboard.fragment;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.dashboard.DashboardActivity;

public abstract class DashboardBaseFragment extends SharedFragment {

    protected AppDialogs progressDialog;
    protected AppExecutors appExecutors;
    protected AppDatabase localDb;

    protected void initDbComponents(){

        progressDialog = new AppDialogs(getContext());
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

    }


    private DashboardActivity getDashboardActivity(){
        if(getActivity()!=null){
            return ((DashboardActivity) getActivity());
        }else {
            return null;
        }
    }

    //sales
    protected void gotoSalesActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoSalesActivity();
        }
    }

    //purchase
    protected void gotoPurchaseActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoPurchaseActivity();
        }
    }

    //item
    protected void gotoItemActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoItemActivity();
        }
    }

    //settings
    protected void gotoSettingsActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoSettingsActivity();
        }
    }

    //shift
    protected void gotoShiftActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoShiftActivity();
        }
    }

    //customer
    protected void gotoCustomerListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoCustomerListActivity();
        }
    }

    //customer
    protected void gotoCategoryListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoCategoryListActivity();
        }
    }

    //Uom
    protected void gotoUOMListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoUOMListActivity();
        }
    }

    //payment modes
    protected void gotoPaymentModesListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoPaymentModeListActivity();
        }
    }

    //taxes modes
    protected void gotoTaxListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoTaxesListActivity();
        }
    }

    //Supplier list
    protected void gotoSupplierListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoSupplierListActivity();
        }
    }

    //Price list
    protected void gotoPriceListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoPriceListActivity();
        }
    }

    //item price list
    protected void gotoItemPriceListActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoItemPriceListActivity();
        }
    }

    //Report home activity
    protected void gotoReportHomeActivity(){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().gotoReportHomeActivity();
        }
    }

    protected void loadCurrentShiftStatus(AppExecutors appExecutors, AppDatabase localDb, AppDialogs progress){
        if(getDashboardActivity()!=null) {
            getDashboardActivity().getCurrentShiftStatus(appExecutors,localDb,progress);
        }
    }
}
