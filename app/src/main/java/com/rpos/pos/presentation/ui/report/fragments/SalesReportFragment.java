package com.rpos.pos.presentation.ui.report.fragments;

import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.Config;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.Utility;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;
import com.rpos.pos.presentation.ui.report.adapter.SalesReportAdapter;
import com.rpos.pos.presentation.ui.sales.bill.BillViewActivity;
import com.sunmi.peripheral.printer.InnerResultCallback;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.ArrayList;
import java.util.List;

public class SalesReportFragment extends ReportBaseFragment {

    private RecyclerView rv_salesReport;
    private SalesReportAdapter salesReportAdapter;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_sales_report;
    }

    @Override
    protected void intiViews(View getView) {

        rv_salesReport = getView.findViewById(R.id.rv_salesReport);

        invoiceEntityList = new ArrayList<InvoiceEntity>();
        salesReportAdapter= new SalesReportAdapter(getContext(), invoiceEntityList);
        rv_salesReport.setAdapter(salesReportAdapter);

    }

    @Override
    protected void fetchAllInvoices() {
        try {
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
                            invoiceEntityList.clear();
                            invoiceEntityList.addAll(savedInvoiceList);

                            //update ui
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //hide empty view
                                    hideEmptyView();
                                    //refresh adapter
                                    salesReportAdapter.notifyDataSetChanged();
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

            onClickDownload();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void prepareDataForExcel(Sheet sheet) {
        try {

            Row row;
            InvoiceEntity invoice;
            float netAmount, outstanding;
            String temp_value;

            CellStyle cellbodyStyle = workbook.createCellStyle();
            cellbodyStyle.setAlignment(CellStyle.ALIGN_CENTER);

            for (int i=0;i<invoiceEntityList.size();i++)
            {
                //get the invoice
                invoice = (InvoiceEntity)invoiceEntityList.get(i);
                //create a row
                row = sheet.createRow(i+1);

                //SL NO
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
                    //calculate net amount
                    netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                    //round of to 2 digits and display
                    temp_value = Utility.roundOffDecimalValueTo2Digits(netAmount);
                }catch (Exception e){
                    temp_value = "Nil 01";
                }
                //net amount
                createCellWithData(row,6,temp_value,cellbodyStyle);

                //TOTAL
                createCellWithData(row,7,""+(invoice.getBillAmount()),cellbodyStyle);

                try{
                    outstanding = invoice.getBillAmount() - invoice.getPaymentAmount();
                    temp_value = Utility.roundOffDecimalValueTo2Digits(outstanding);
                }catch (Exception e){
                    temp_value = "Nil 02";
                }//OUTSTANDING
                createCellWithData(row,8,temp_value,cellbodyStyle);

            }//end of for loop


            //N.B
            // * FILENAME AND DIRECTORY NAMES ARE MANDATORY
            FILE_NAME = Config.SALES_REPORT_FILE_PREFIX + System.currentTimeMillis()+".xls";
            DIRECTORY = "Pos_Reports/Sales";

        }catch (Exception e){
           throw e;
        }
    }

    @Override
    protected void onFilterReport(int type, String fromDate, String toDate) {

        long fromTimeStamp = DateTimeUtils.convertDateToTimeStamp(fromDate);
        long toTimeStamp = DateTimeUtils.convertDateToTimeStamp(toDate);

        String filterString = ""+fromTimeStamp+":"+toTimeStamp;

        salesReportAdapter.getFilter().filter(filterString, i -> {
            Log.e("----------","complete");
            progressDialog.hideProgressbar();
        });

    }

    @Override
    protected void onSearchQueryChange(String query) {
        salesReportAdapter.secondFilter().filter(query);
    }

    @Override
    protected void onSearchClear() {

        progressDialog.showProgressBar();

        salesReportAdapter.secondFilter().filter("", new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int i) {
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

                    List<InvoiceEntity> reportList = salesReportAdapter.getCurrentList();
                    if(reportList!=null){

                        //using sunmi printing interface to print
                        //prepares printing data and prints
                        SunmiPrintHelper.getInstance().printSalesReportTransaction(getContext(),reportList, new InnerResultCallback() {
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
