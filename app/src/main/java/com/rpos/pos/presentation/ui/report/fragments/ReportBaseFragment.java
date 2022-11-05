package com.rpos.pos.presentation.ui.report.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedFragment;

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

public abstract class ReportBaseFragment extends SharedFragment {

    protected LinearLayout ll_back;
    protected View viewEmpty;
    protected LinearLayout ll_downloadFile;

    protected AppExecutors appExecutors;
    protected AppDatabase localDb;

    protected Sheet sheet = null;
    protected Cell cell = null;
    protected Workbook workbook;
    protected String EXCEL_SHEET_NAME = "Sheet1";
    protected String FILE_NAME,DIRECTORY;

    protected final int REQUEST_PERMISSION_READ_WRITE = 103;
    protected AppDialogs progressDialog;
    protected boolean isSaveButtonPressed;

    @Override
    protected void onCreateView(View getView) {

        progressDialog = new AppDialogs(getContext());
        ll_back = getView.findViewById(R.id.ll_back);
        viewEmpty = getView.findViewById(R.id.view_empty);
        ll_downloadFile = getView.findViewById(R.id.ll_rightMenu);

        isSaveButtonPressed = false;

        FILE_NAME = "";
        DIRECTORY = "";

        intiViews(getView);

        //For converting report to excel file
        workbook = new HSSFWorkbook();
        // Create a new sheet in a Workbook and assign a name to it
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);


        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        ll_back.setOnClickListener(view -> {
            getActivity().finish();
        });

        ll_downloadFile.setOnClickListener(this::onClickDownloadReport);

        fetchAllInvoices();

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

    protected abstract void intiViews(View getView);
    protected abstract void fetchAllInvoices();
    protected abstract void onClickDownloadReport(View view);
    protected abstract void prepareDataForExcel(Sheet sheet);

    /**
     *
     * */
    protected void onClickDownload(){
        try {

            //if there is data
            //check app has read write permission
            if(checkReadStoragePermissionAvailable()){
                if(isSaveButtonPressed){
                    showToast(getString(R.string.saving));
                    return;
                }
                //flag to
                isSaveButtonPressed = true;
                //prepare excel for download
                prepareExcel();
            }

        }catch (Exception e){
            throw e;
        }
    }

    /**
     * to prepare excel with data
     * */
    protected void prepareExcel(){
        try {

            progressDialog.showProgressBar();

            //adjust width for each columns
            adjustColumnWidth(sheet);

            //create excel title row with style(color)
            createExcelTitleRow(sheet.createRow(0));

            //to arrange all invoice data to excel cells
            prepareDataForExcel(sheet);

            //check file name is valid
            if(FILE_NAME.isEmpty()){
                Log.e("ALERT ALERT", "Provide FILE NAME FOR SAVING");
                return;
            }

            //check file name is valid
            if(DIRECTORY.isEmpty()){
                Log.e("ALERT ALERT", "Provide DIRECTORY NAME FOR SAVING");
                return;
            }

            boolean isSaved = storeExcelInStorage(DIRECTORY, FILE_NAME);
            if(isSaved){
                progressDialog.hideProgressbar();
                isSaveButtonPressed = false;
                String savedMessage = getString(R.string.report_saved) + " to SD Card Documents/"+DIRECTORY;
                AppDialogs appDialogs = new AppDialogs(getActivity());
                appDialogs.showCommonSuccessDialog(savedMessage, null);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to set width for columns in excel
     * Do this only for onetime,
     * */
    protected void adjustColumnWidth(Sheet sheet){
        try {
            //setting up column width
            sheet.setColumnWidth(2, (15 * 400));
            sheet.setColumnWidth(3, (15 * 300));
            sheet.setColumnWidth(4, (15 * 300));
            sheet.setColumnWidth(5, (15 * 300));
            sheet.setColumnWidth(6, (15 * 300));
            sheet.setColumnWidth(7, (15 * 300));
            sheet.setColumnWidth(8, (15 * 300));

        }catch (Exception e){
            throw e;
        }
    }

    /**
     * to create title row ( sl no, id, cust name etc... )
     * style added to color background
     * */
    protected void createExcelTitleRow(Row row){
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
     * to create individual cells
     * */
    protected void createCellWithData(Row row, int index, String cellValue, CellStyle style){
        try {

            //SL NO
            cell = row.createCell(index);
            cell.setCellValue(cellValue);
            cell.setCellStyle(style);

        }catch (Exception e){
            throw e;
        }
    }

    /**
     * Storage permission checking
     * */
    protected boolean checkReadStoragePermissionAvailable() {

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

    /*"Pos_Reports/Sales"*/
    /**
     * Store Excel Workbook in external storage
     * @param fileName - name of workbook which will be stored in device
     * @return boolean - returns state whether workbook is written into storage or not
     */
    protected boolean storeExcelInStorage(String directory, String fileName) {
        boolean isSuccess;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), directory);
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

    protected void showEmptyList(){
        viewEmpty.setVisibility(View.VISIBLE);
    }
    protected void hideEmptyView(){
        viewEmpty.setVisibility(View.GONE);
    }

}
