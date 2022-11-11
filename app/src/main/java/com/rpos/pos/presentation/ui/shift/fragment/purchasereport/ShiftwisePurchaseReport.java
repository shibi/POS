package com.rpos.pos.presentation.ui.shift.fragment.purchasereport;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;


import com.rpos.pos.AppExecutors;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.shift.ShiftWiseFilterable;
import com.rpos.pos.presentation.ui.shift.adapter.ShiftWisePurchaseReportAdapter;

import java.util.ArrayList;
import java.util.List;


public class ShiftwisePurchaseReport extends SharedFragment implements ShiftWiseFilterable {

    private RecyclerView rv_purchase;
    private View viewEmpty;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;

    private ShiftWisePurchaseReportAdapter adapter;


    private List<PurchaseInvoiceEntity> invoiceList;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_shiftwise_purchase_report;
    }

    @Override
    protected void onCreateView(View getView) {

        rv_purchase = getView.findViewById(R.id.rv_shiftPurchase);
        viewEmpty = getView.findViewById(R.id.view_empty);

        progressDialog = new AppDialogs(getContext());

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        invoiceList = new ArrayList<>();
        adapter = new ShiftWisePurchaseReportAdapter(getContext(), invoiceList);
        rv_purchase.setAdapter(adapter);

        fetchAllInvoices();

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    protected void fetchAllInvoices() {
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<PurchaseInvoiceEntity> savedInvoiceList = localDb.purchaseInvoiceDao().getAllInvoices();
                        if(savedInvoiceList!=null && !savedInvoiceList.isEmpty()){
                            invoiceList.clear();
                            invoiceList.addAll(savedInvoiceList);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideEmptyView();
                                    progressDialog.hideProgressbar();
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showEmptyList();
                                    progressDialog.hideProgressbar();
                                    showToast(getString(R.string.empty_data));
                                }
                            });
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showEmptyList(){
        viewEmpty.setVisibility(View.VISIBLE);
    }
    private void hideEmptyView(){
        viewEmpty.setVisibility(View.GONE);
    }

    @Override
    public void onFilterWithShift(int shiftId) {
        adapter.getFilter().filter(""+shiftId);
    }
}
