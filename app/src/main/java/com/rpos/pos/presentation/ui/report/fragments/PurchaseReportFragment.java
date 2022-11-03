package com.rpos.pos.presentation.ui.report.fragments;

import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.report.adapter.PurchaseReportAdapter;
import java.util.ArrayList;
import java.util.List;

public class PurchaseReportFragment extends SharedFragment {

    private LinearLayout ll_back;
    private RecyclerView rv_purchaseReport;
    private PurchaseReportAdapter purchaseReportAdapter;
    private List<PurchaseInvoiceEntity> invoiceEntityList;
    private View viewEmpty;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_purchase_report;
    }

    @Override
    protected void onCreateView(View getView) {

        ll_back = getView.findViewById(R.id.ll_back);
        rv_purchaseReport = getView.findViewById(R.id.rv_purchaseReport);
        viewEmpty = getView.findViewById(R.id.view_empty);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        invoiceEntityList = new ArrayList<>();
        purchaseReportAdapter = new PurchaseReportAdapter(getContext(), invoiceEntityList);
        rv_purchaseReport.setAdapter(purchaseReportAdapter);

        ll_back.setOnClickListener(view -> {
            getActivity().finish();
        });

        getAllInvoices();
    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    /**
     * to get all invoices list
     * */
    private void getAllInvoices(){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<PurchaseInvoiceEntity> savedInvoiceList = localDb.purchaseInvoiceDao().getAllInvoices();
                        if(savedInvoiceList!=null && !savedInvoiceList.isEmpty()){
                            invoiceEntityList.clear();
                            invoiceEntityList.addAll(savedInvoiceList);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideEmptyView();
                                    purchaseReportAdapter.notifyDataSetChanged();
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showEmptyList();
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
}
