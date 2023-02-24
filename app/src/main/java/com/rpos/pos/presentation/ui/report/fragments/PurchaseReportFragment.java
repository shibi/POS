package com.rpos.pos.presentation.ui.report.fragments;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.Config;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.Utility;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;
import com.rpos.pos.presentation.ui.report.adapter.PurchaseReportAdapter;
import com.sunmi.peripheral.printer.InnerResultCallback;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
                    temp_value = Utility.roundOffDecimalValueTo2Digits(netAmount);
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
                    temp_value = Utility.roundOffDecimalValueTo2Digits(outstanding);
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

    @Override
    protected void print() {

        //show print confirmation to avoid accidental clicks
        AppDialogs appDialogs = new AppDialogs(getActivity());
        appDialogs.showReportPrintConfirmation(new AppDialogs.OnDualActionButtonClickListener() {
            @Override
            public void onClickPositive(String id) {
                try{

                    List<PurchaseInvoiceEntity> reportList = purchaseReportAdapter.getCurrentList();
                    if(reportList!=null){

                        //using sunmi printing interface to print
                        //prepares printing data and prints
                        SunmiPrintHelper.getInstance().printPurchaseReportTransaction(getContext(),reportList, new InnerResultCallback() {
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



                    }else {
                        showToast("Nothing to print :)");
                    }

                    Log.e("-----------","print");

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onClickNegetive(String id) {
                //do nothing
            }
        });


    }

}
