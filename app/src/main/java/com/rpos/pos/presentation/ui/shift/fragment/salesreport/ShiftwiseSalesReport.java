package com.rpos.pos.presentation.ui.shift.fragment.salesreport;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.shift.ShiftWiseFilterable;
import com.rpos.pos.presentation.ui.shift.adapter.ShiftWiseSalesReportAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShiftwiseSalesReport extends SharedFragment implements ShiftWiseFilterable {

    private RecyclerView rv_sales;
    private ShiftWiseSalesReportAdapter adapter;
    private View viewEmpty;


    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;


    private List<InvoiceEntity> invoiceList;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_shiftwise_salesreport;
    }

    @Override
    protected void onCreateView(View getView) {

        rv_sales = getView.findViewById(R.id.rv_shiftSales);
        viewEmpty = getView.findViewById(R.id.view_empty);

        progressDialog = new AppDialogs(getContext());

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        invoiceList = new ArrayList<>();
        adapter = new ShiftWiseSalesReportAdapter(getContext(), invoiceList);
        rv_sales.setAdapter(adapter);

        fetchAllInvoices();
    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    private void fetchAllInvoices() {
        try {

            progressDialog.showProgressBar();

            //new thread
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //get the saved invoice list
                        List<InvoiceEntity> savedInvoiceList = localDb.invoiceDao().getAllInvoices();
                        //check list is valid
                        if(savedInvoiceList!=null && !savedInvoiceList.isEmpty()){
                            //clear previous list and add new list
                            invoiceList.clear();
                            invoiceList.addAll(savedInvoiceList);

                            //update ui
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //hide empty view
                                    hideEmptyView();

                                    progressDialog.hideProgressbar();

                                    //refresh adapter
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
        try {

            adapter.getFilter().filter(""+shiftId);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
