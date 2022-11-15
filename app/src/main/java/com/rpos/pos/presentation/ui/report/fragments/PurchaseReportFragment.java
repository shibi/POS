package com.rpos.pos.presentation.ui.report.fragments;

import android.util.Log;
import android.view.View;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.Config;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.presentation.ui.report.adapter.PurchaseReportAdapter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PurchaseReportFragment extends ReportBaseFragment {

    private RecyclerView rv_purchaseReport;
    private PurchaseReportAdapter purchaseReportAdapter;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_purchase_report;
    }

    @Override
    protected void intiViews(View getView) {

        rv_purchaseReport = getView.findViewById(R.id.rv_purchaseReport);
        invoiceEntityList = new ArrayList<>();
        purchaseReportAdapter = new PurchaseReportAdapter(getContext(), invoiceEntityList);
        rv_purchaseReport.setAdapter(purchaseReportAdapter);

    }

    @Override
    protected void fetchAllInvoices() {
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

    @Override
    protected void onClickDownloadReport(View view) {
        try {

            //check whether any data available for download
            if(invoiceEntityList==null || invoiceEntityList.isEmpty()){
                //no data, show alert toast
                showToast(getString(R.string.empty_data));
                //stop proceeding
                return;
            }

            /**
             * calling parent method to execute export methods
             * */
            onClickDownload();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To create cell and fill invoice data
     * */
    @Override
    protected void prepareDataForExcel(Sheet sheet) {
        try {

            Row row;
            PurchaseInvoiceEntity invoice;
            float netAmount, outstanding;
            final DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String temp_value;

            CellStyle cellbodyStyle = workbook.createCellStyle();
            cellbodyStyle.setAlignment(CellStyle.ALIGN_CENTER);

            for (int i=0;i<invoiceEntityList.size();i++){
                invoice = (PurchaseInvoiceEntity)invoiceEntityList.get(i);
                row = sheet.createRow(i+1);

                //SLNO
                createCellWithData(row,0,""+(i+1),cellbodyStyle);
                //INV ID
                createCellWithData(row,1,"#INV "+(invoice.getId()),cellbodyStyle);
                //Customer name
                createCellWithData(row,2,invoice.getCustomerName(),cellbodyStyle);

                String date = DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp());
                //DATE
                createCellWithData(row,3,date,cellbodyStyle);
                //MOP
                createCellWithData(row,4,""+invoice.getGrossAmount(),cellbodyStyle);
                //TAX
                createCellWithData(row,5,""+(invoice.getTaxAmount()),cellbodyStyle);

                try{
                    netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    temp_value = decimalFormat.format(netAmount);
                }catch (Exception e){
                    temp_value = "Nil 01";
                }
                //NET
                //net amount
                createCellWithData(row,6,temp_value,cellbodyStyle);

                //TOTAL
                createCellWithData(row,7,""+(invoice.getBillAmount()),cellbodyStyle);

                try{
                    outstanding = invoice.getBillAmount() - invoice.getPaymentAmount();
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    temp_value = decimalFormat.format(outstanding);
                }catch (Exception e){
                    temp_value = "Nil 02";
                }

                //OUTSTANDING
                createCellWithData(row,8,temp_value,cellbodyStyle);


            }//end of for loop

            //N.B
            // * FILENAME AND DIRECTORY NAMES ARE MANDATORY
            FILE_NAME = Config.PURCHASE_REPORT_FILE_PREFIX + System.currentTimeMillis()+".xls";
            DIRECTORY = "Pos_Reports/Purchase";

        }catch (Exception e){
            e.printStackTrace();
            progressDialog.hideProgressbar();
        }
    }

    @Override
    protected void onFilterReport(int type, String fromDate, String toDate) {


        long fromTimeStamp = DateTimeUtils.convertDateToTimeStamp(fromDate);
        long toTimeStamp = DateTimeUtils.convertDateToTimeStamp(toDate);

        String filterString = ""+fromTimeStamp+":"+toTimeStamp;

        purchaseReportAdapter.getFilter().filter(filterString, i -> {
            Log.e("----------","complete");
            progressDialog.hideProgressbar();
        });

    }

    @Override
    protected void onSearchQueryChange(String query) {

        purchaseReportAdapter.secondFilter().filter(query);
    }

    @Override
    protected void onSearchClear() {

        //progress bar
        progressDialog.hideProgressbar();

        //filter
        purchaseReportAdapter.getFilter().filter("", new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int i) {
                //hide progress bar
                progressDialog.hideProgressbar();
            }
        });
    }

}
