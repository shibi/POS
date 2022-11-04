package com.rpos.pos.presentation.ui.report.fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Config;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedFragment;
import com.rpos.pos.presentation.ui.report.adapter.SalesReportAdapter;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SalesReportFragment extends SharedFragment {

    private LinearLayout ll_back;
    private RecyclerView rv_salesReport;
    private SalesReportAdapter salesReportAdapter;
    private List<InvoiceEntity> invoiceEntityList;
    private View viewEmpty;
    private LinearLayout ll_downloadFile;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private Sheet sheet = null;
    private Cell cell = null;
    private Workbook workbook;
    private static String EXCEL_SHEET_NAME = "Sheet1";
    private String FILE_NAME;

    private final int REQUEST_PERMISSION_READ_WRITE = 103;
    private AppDialogs progressDialog;
    private boolean isSaveButtonPressed;

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_sales_report;
    }

    @Override
    protected void onCreateView(View getView) {

        progressDialog = new AppDialogs(getContext());
        ll_back = getView.findViewById(R.id.ll_back);
        rv_salesReport = getView.findViewById(R.id.rv_salesReport);
        viewEmpty = getView.findViewById(R.id.view_empty);
        ll_downloadFile = getView.findViewById(R.id.ll_rightMenu);

        isSaveButtonPressed = false;

        //For converting report to excel file
        workbook = new HSSFWorkbook();
        // Create a new sheet in a Workbook and assign a name to it
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);


        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();


        invoiceEntityList = new ArrayList<>();
        salesReportAdapter= new SalesReportAdapter(getContext(), invoiceEntityList);
        rv_salesReport.setAdapter(salesReportAdapter);



        ll_back.setOnClickListener(view -> {
            getActivity().finish();
        });

        ll_downloadFile.setOnClickListener(this::onClickDownload);

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
                        List<InvoiceEntity> savedInvoiceList = localDb.invoiceDao().getAllInvoices();
                        if(savedInvoiceList!=null && !savedInvoiceList.isEmpty()){
                            invoiceEntityList.clear();
                            invoiceEntityList.addAll(savedInvoiceList);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideEmptyView();
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


    /**
     * on click download
     * */
    private void onClickDownload(View view){
        try {
            //check whether any data available for download
            if(invoiceEntityList==null || invoiceEntityList.isEmpty()){
                //no data, show alert toast
                showToast(getString(R.string.empty_data));
                //stop proceeding
                return;
            }

            //if there is data
            //check app has read write permission
            if(checkReadStoragePermissionAvailable()){
                if(isSaveButtonPressed){
                    showToast(getString(R.string.saving));
                    return;
                }
                //flag to
                isSaveButtonPressed = true;
                prepareExcel();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private boolean checkReadStoragePermissionAvailable() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_WRITE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return false;
    }

    /**
     * to prepare file
     * */
    private void prepareExcel(){
        try {

            sheet.setColumnWidth(2, (15 * 400));
            sheet.setColumnWidth(3, (15 * 300));
            sheet.setColumnWidth(4, (15 * 300));
            sheet.setColumnWidth(5, (15 * 300));
            sheet.setColumnWidth(6, (15 * 300));
            sheet.setColumnWidth(7, (15 * 300));
            sheet.setColumnWidth(8, (15 * 300));

            createExcelTitleRow(sheet.createRow(0));

            Row row;
            Cell cell;
            InvoiceEntity invoice;
            float netAmount, outstanding;
            final DecimalFormat decimalFormat = new DecimalFormat("0.00");
            String temp_value;

            CellStyle cellbodyStyle = workbook.createCellStyle();
            cellbodyStyle.setAlignment(CellStyle.ALIGN_CENTER);

            for (int i=0;i<invoiceEntityList.size();i++)
            {
                invoice = invoiceEntityList.get(i);
                row = sheet.createRow(i+1);

                //SLNO
                cell = row.createCell(0);
                cell.setCellValue(""+(i+1));
                cell.setCellStyle(cellbodyStyle);

                //INV ID
                cell = row.createCell(1);
                cell.setCellValue("#INV "+(invoice.getId()));
                cell.setCellStyle(cellbodyStyle);

                //Customer name
                cell = row.createCell(2);
                cell.setCellValue(invoice.getCustomerName());
                cell.setCellStyle(cellbodyStyle);

                //Date
                cell = row.createCell(3);
                cell.setCellValue(invoice.getDate());
                cell.setCellStyle(cellbodyStyle);

                //MOP
                cell = row.createCell(4);
                cell.setCellValue(""+invoice.getGrossAmount());
                cell.setCellStyle(cellbodyStyle);

                //TAX
                cell = row.createCell(5);
                cell.setCellValue(""+(invoice.getTaxAmount()));
                cell.setCellStyle(cellbodyStyle);

                try{
                    netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    temp_value = decimalFormat.format(netAmount);
                }catch (Exception e){
                    temp_value = "Nil 01";
                }
                //NET
                cell = row.createCell(6);
                cell.setCellValue(temp_value);
                cell.setCellStyle(cellbodyStyle);

                //TOTAL
                cell = row.createCell(7);
                cell.setCellValue(""+(invoice.getBillAmount()));
                cell.setCellStyle(cellbodyStyle);


                try{
                    outstanding = invoice.getBillAmount() - invoice.getPaymentAmount();
                    decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
                    temp_value = decimalFormat.format(outstanding);
                }catch (Exception e){
                    temp_value = "Nil 02";
                }

                //OUTSTANDING
                cell = row.createCell(8);
                cell.setCellValue(temp_value);
                cell.setCellStyle(cellbodyStyle);


            }//end of for loop


            FILE_NAME = Config.SALES_REPORT_FILE_PREFIX + System.currentTimeMillis()+".xls";

            boolean isSaved = storeExcelInStorage(getContext(), FILE_NAME);
            if(isSaved){

                progressDialog.hideProgressbar();
                isSaveButtonPressed = false;
                String savedMessage = getString(R.string.report_saved) + " to SD Card Documents/Pos_Reports/Sales";
                AppDialogs appDialogs = new AppDialogs(getActivity());
                appDialogs.showCommonSuccessDialog(savedMessage, null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createExcelTitleRow(Row row){
        try {
            // Cell style for a cell
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

            //HEADERS
            //SL NO
            // Creating a cell and assigning it to a row
            cell = row.createCell(0);
            cell.setCellValue("SL No");
            cell.setCellStyle(cellStyle);

            //INV ID
            cell = row.createCell(1);
            cell.setCellValue("Invoice id");
            cell.setCellStyle(cellStyle);


            //customer name
            cell = row.createCell(2);
            cell.setCellValue("Customer name");
            cell.setCellStyle(cellStyle);


            //Date
            cell = row.createCell(3);
            cell.setCellValue("Date");
            cell.setCellStyle(cellStyle);


            //MOP
            cell = row.createCell(4);
            cell.setCellValue("MOP");
            cell.setCellStyle(cellStyle);

            //TAX
            cell = row.createCell(5);
            cell.setCellValue("Tax amount");
            cell.setCellStyle(cellStyle);

            //NET
            cell = row.createCell(6);
            cell.setCellValue("Net amount");
            cell.setCellStyle(cellStyle);


            //TOTAL
            cell = row.createCell(7);
            cell.setCellValue("Grand total");
            cell.setCellStyle(cellStyle);

            //Outstanding
            cell = row.createCell(8);
            cell.setCellValue("Outstanding");
            cell.setCellStyle(cellStyle);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Store Excel Workbook in external storage
     *
     * @param context  - application context
     * @param fileName - name of workbook which will be stored in device
     * @return boolean - returns state whether workbook is written into storage or not
     */
    private boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;
        //File file = new File(context.getExternalFilesDir(null), "Sales/Report"); //new File(context.getExternalFilesDir(null), fileName);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Pos_Reports/Sales");
        //if the folder does not exist, create a new folder to save the file
        if (!file.exists()){
            file.mkdirs();
        }

        String excelName = fileName;
        Log.e("------------------","image Name "+excelName);

        //file path to for saving the user key
        String filePath =  file+"/"+excelName; // External is the text file et_name

        //initialise the file
        File excelFile = new File(filePath);

        //create new file, if it is null
        if (excelFile == null) {
            excelFile.mkdir();
        }

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(excelFile);
            workbook.write(fileOutputStream);
            Log.e("TAG", "Writing file" + excelFile);
            isSuccess = true;
        } catch (IOException e) {
            Log.e("TAG", "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e("TAG", "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return isSuccess;
    }

    private void showEmptyList(){
        viewEmpty.setVisibility(View.VISIBLE);
    }

    private void hideEmptyView(){
        viewEmpty.setVisibility(View.GONE);
    }
}
