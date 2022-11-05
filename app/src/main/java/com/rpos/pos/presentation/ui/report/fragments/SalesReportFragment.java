package com.rpos.pos.presentation.ui.report.fragments;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.Config;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.presentation.ui.report.adapter.SalesReportAdapter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SalesReportFragment extends ReportBaseFragment {

    private RecyclerView rv_salesReport;
    private SalesReportAdapter salesReportAdapter;
    private List<InvoiceEntity> invoiceEntityList;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_sales_report;
    }

    @Override
    protected void intiViews(View getView) {

        rv_salesReport = getView.findViewById(R.id.rv_salesReport);
        invoiceEntityList = new ArrayList<>();
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
            final DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String temp_value;

            CellStyle cellbodyStyle = workbook.createCellStyle();
            cellbodyStyle.setAlignment(CellStyle.ALIGN_CENTER);

            for (int i=0;i<invoiceEntityList.size();i++)
            {
                //get the invoice
                invoice = invoiceEntityList.get(i);
                //create a row
                row = sheet.createRow(i+1);

                //SL NO
                createCellWithData(row,0,""+(i+1),cellbodyStyle);
                //INV ID
                createCellWithData(row,1,"#INV "+(invoice.getId()),cellbodyStyle);
                //Customer name
                createCellWithData(row,2,invoice.getCustomerName(),cellbodyStyle);
                //DATE
                createCellWithData(row,3,invoice.getDate(),cellbodyStyle);
                //MOP
                createCellWithData(row,4,""+invoice.getGrossAmount(),cellbodyStyle);
                //TAX
                createCellWithData(row,5,""+(invoice.getTaxAmount()),cellbodyStyle);
                try{
                    //calculate net amount
                    netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    //round of to 2 digits and display
                    temp_value = decimalFormat.format(netAmount);
                }catch (Exception e){
                    temp_value = "Nil 01";
                }
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
}
