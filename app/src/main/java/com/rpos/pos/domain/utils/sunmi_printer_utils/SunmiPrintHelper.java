package com.rpos.pos.domain.utils.sunmi_printer_utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.widget.Toast;

import com.google.gson.internal.bind.JsonTreeReader;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.CompanyAddressEntity;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceItemHistory;
import com.rpos.pos.data.remote.dto.Invoice;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.sunmi.peripheral.printer.ExceptionConst;
import com.sunmi.peripheral.printer.InnerLcdCallback;
import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallback;
import com.sunmi.peripheral.printer.SunmiPrinterService;
import com.sunmi.peripheral.printer.WoyouConsts;

import java.util.List;

/**
 * <pre>
 *      This class is used to demonstrate various printing effects
 *      Developers need to repackage themselves, for details please refer to
 *      http://sunmi-ota.oss-cn-hangzhou.aliyuncs.com/DOC/resource/re_cn/Sunmiprinter%E5%BC%80%E5%8F%91%E8%80%85%E6%96%87%E6%A1%A31.1.191128.pdf
 *  </pre>
 *
 * @author kaltin
 * @since create at 2020-02-14
 */
public class SunmiPrintHelper {

    public static int NoSunmiPrinter = 0x00000000;
    public static int CheckSunmiPrinter = 0x00000001;
    public static int FoundSunmiPrinter = 0x00000002;
    public static int LostSunmiPrinter = 0x00000003;

    /**
     *  sunmiPrinter means checking the printer connection status
     */
    public int sunmiPrinter = CheckSunmiPrinter;
    /**
     *  SunmiPrinterService for API
     */
    private SunmiPrinterService sunmiPrinterService;

    private static SunmiPrintHelper helper = new SunmiPrintHelper();

    private SunmiPrintHelper() {}

    public static SunmiPrintHelper getInstance() {
        return helper;
    }

    private InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
        @Override
        protected void onConnected(SunmiPrinterService service) {
            sunmiPrinterService = service;
            checkSunmiPrinterService(service);
        }

        @Override
        protected void onDisconnected() {
            sunmiPrinterService = null;
            sunmiPrinter = LostSunmiPrinter;
        }
    };

    /**
     * init sunmi print service
     */
    public void initSunmiPrinterService(Context context){
        try {
            boolean ret =  InnerPrinterManager.getInstance().bindService(context,
                    innerPrinterCallback);
            if(!ret){
                sunmiPrinter = NoSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     *  deInit sunmi print service
     */
    public void deInitSunmiPrinterService(Context context){
        try {
            if(sunmiPrinterService != null){
                InnerPrinterManager.getInstance().unBindService(context, innerPrinterCallback);
                sunmiPrinterService = null;
                sunmiPrinter = LostSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check the printer connection,
     * like some devices do not have a printer but need to be connected to the cash drawer through a print service
     */
    private void checkSunmiPrinterService(SunmiPrinterService service){
        boolean ret = false;
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
        sunmiPrinter = ret?FoundSunmiPrinter:NoSunmiPrinter;
    }

    /**
     *  Some conditions can cause interface calls to fail
     *  For example: the version is too low、device does not support
     *  You can see {@link ExceptionConst}
     *  So you have to handle these exceptions
     */
    private void handleRemoteException(RemoteException e){
        //TODO process when get one exception
    }

    /**
     * send esc cmd
     */
    public void sendRawData(byte[] data) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.sendRAWData(data, null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     *  Printer cuts paper and throws exception on machines without a cutter
     */
    public void cutpaper(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.cutPaper(null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     *  Initialize the printer
     *  All style settings will be restored to default
     */
    public void initPrinter(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.printerInit(null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     *  paper feed three lines
     *  Not disabled when line spacing is set to 0
     */
    public void print3Line(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.lineWrap(3, null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Get printer serial number
     */
    public String getPrinterSerialNo(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterSerialNo();
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get device model
     */
    public String getDeviceModel(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterModal();
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get firmware version
     */
    public String getPrinterVersion(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterVersion();
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get paper specifications
     */
    public String getPrinterPaper(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterPaper() == 1?"58mm":"80mm";
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get paper specifications
     */
    public void getPrinterHead(InnerResultCallback callbcak){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
             sunmiPrinterService.getPrinterFactory(callbcak);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Get printing distance since boot
     * Get printing distance through interface callback since 1.0.8(printerlibrary)
     */
    public void getPrinterDistance(InnerResultCallback callback){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.getPrintedLength(callback);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Set printer alignment
     */
    public void setAlign(int align){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.setAlignment(align, null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     *  Due to the distance between the paper hatch and the print head,
     *  the paper needs to be fed out automatically
     *  But if the Api does not support it, it will be replaced by printing three lines
     */
    public void feedPaper(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.autoOutPaper(null);
        } catch (RemoteException e) {
            print3Line();
        }
    }

    /**
     * print text
     * setPrinterStyle:Api require V4.2.22 or later, So use esc cmd instead when not supported
     *  More settings reference documentation {@link WoyouConsts}
     * printTextWithFont:
     *  Custom fonts require V4.14.0 or later!
     *  You can put the custom font in the 'assets' directory and Specify the font name parameters
     *  in the Api.
     */
    public void printText(String content, float size, boolean isBold, boolean isUnderLine,
                          String typeface) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, isBold?
                        WoyouConsts.ENABLE: WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                if (isBold) {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
                } else {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
                }
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_UNDERLINE, isUnderLine?
                        WoyouConsts.ENABLE: WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                if (isUnderLine) {
                    sunmiPrinterService.sendRAWData(ESCUtil.underlineWithOneDotWidthOn(), null);
                } else {
                    sunmiPrinterService.sendRAWData(ESCUtil.underlineOff(), null);
                }
            }
            sunmiPrinterService.printTextWithFont(content, typeface, size, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * print Bar Code
     */
    public void printBarCode(String data, int symbology, int height, int width, int textposition) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.printBarCode(data, symbology, height, width, textposition, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * print Qr Code
     */
    public void printQr(String data, int modulesize, int errorlevel) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.printQRCode(data, modulesize, errorlevel, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print a row of a table
     */
    public void printTable(String[] txts, int[] width, int[] align) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.printColumnsString(txts, width, align, null);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Print pictures and text in the specified orde
     *  After the picture is printed,
     *  the line feed output needs to be called,
     *  otherwise it will be saved in the cache
     *  In this example, the image will be printed because the print text content is added
     */
    public void printBitmap(Bitmap bitmap, int orientation) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            if(orientation == 0){
                sunmiPrinterService.printBitmap(bitmap, null);
                sunmiPrinterService.printText("横向排列\n", null);
                sunmiPrinterService.printBitmap(bitmap, null);
                sunmiPrinterService.printText("横向排列\n", null);
            }else{
                sunmiPrinterService.printBitmap(bitmap, null);
                sunmiPrinterService.printText("\n纵向排列\n", null);
                sunmiPrinterService.printBitmap(bitmap, null);
                sunmiPrinterService.printText("\n纵向排列\n", null);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets whether the current printer is in black mark mode
     */
    public boolean isBlackLabelMode(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return false;
        }
        try {
            return sunmiPrinterService.getPrinterMode() == 1;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Gets whether the current printer is in label-printing mode
     */
    public boolean isLabelMode(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return false;
        }
        try {
            return sunmiPrinterService.getPrinterMode() == 2;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     *  Transaction printing:
     *  enter->print->exit(get result) or
     *  enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     *  result)
     */
    public void printTrans(Context context, InnerResultCallback callbcak){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.enterPrinterBuffer(true);
            printExample(context);
            sunmiPrinterService.exitPrinterBufferWithCallback(true, callbcak);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Transaction printing:
     *  enter->print->exit(get result) or
     *  enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     *  result)
     */
    public void printTransaction_sales(Context context, InvoiceEntity invoice, CompanyAddressEntity companyDetails, List<InvoiceItemHistory> printItemsList,String printerQrData, InnerResultCallback callbcak){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {

            sunmiPrinterService.enterPrinterBuffer(true);
            printReceipt_sales(context, invoice, companyDetails, printItemsList, printerQrData);
            sunmiPrinterService.exitPrinterBufferWithCallback(true, callbcak);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private int ALIGN_LEFT = 0;
    private int ALIGN_CENTER = 1;
    private int ALIGN_RIGHT = 2;
    private int ALIGNMENT = ALIGN_LEFT;

    /**
     * to print receipt for sales
     * */
    private void printReceipt_sales(Context context, InvoiceEntity invoice , CompanyAddressEntity companyDetails, List<InvoiceItemHistory> printItemsList, String printerQrData){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }

        try {

            String companyName, address, phone, email;
            if(companyDetails!=null){

                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)){
                    companyName = companyDetails.getCompanyNameEng();
                }else {
                    companyName = companyDetails.getCompanyNameAr();
                }
                address = companyDetails.getAddress();
                phone = "Ph: "+ companyDetails.getMobile();
                email = "email: "+ companyDetails.getEmail();
            }else {
                companyName = "POS";
                address = "Address";
                phone = "Ph: +91 9876543210";
                email = "email: email@gmail.com";
            }




            //----------------------------------------------------------------------------------------------------------------------------

            int paper = sunmiPrinterService.getPrinterPaper();

            sunmiPrinterService.printerInit(null);


            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText(companyName+"\n", null); //company name
            sunmiPrinterService.printText(address+"\n", null); //company address
            sunmiPrinterService.printText(phone+"\n", null); //company phone
            sunmiPrinterService.printText(email+"\n", null); //company email

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
            //sunmiPrinterService.printBitmap(bitmap, null);
            //sunmiPrinterService.lineWrap(1, null);


            ALIGNMENT = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN))? ALIGN_LEFT: ALIGN_RIGHT;
            sunmiPrinterService.setAlignment(ALIGNMENT, null);
            printBillCustomerInfo(context, invoice.getCustomerName(), invoice.getId(), invoice.getCurrency(),1, invoice.getTimestamp());

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }
            //sunmiPrinterService.printTextWithFont("address\n", null, 12, null);
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }

            String title_item = context.getString(R.string.item_label);
            String title_rate = context.getString(R.string.rate_label);
            String title_qty = context.getString(R.string.qty_label);
            String title_total = context.getString(R.string.total_label);

            String txts_four_clmn[];
            int width_four_clmn[];
            int align_four_clmn[];
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)){
                txts_four_clmn = new String[]{title_item, title_rate, title_qty,title_total};
                width_four_clmn = new int[]{1, 1, 1, 1};
                align_four_clmn = new int[]{0, 2, 2 ,2};
            }else {
                txts_four_clmn = new String[]{title_total, title_qty, title_rate,title_item};
                width_four_clmn = new int[]{1, 1, 1, 1};
                align_four_clmn = new int[]{0, 0, 0 ,2};
            }

            sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String txts[] = new String[]{"empty", "empty"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};


            if(printItemsList!=null && printItemsList.size()>0){

                int totalProductsQuantity = 0;
                for (int i=0; i<printItemsList.size();i++){
                    if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getItemName();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getTotal();
                    }else {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getTotal();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getItemName();
                    }

                    totalProductsQuantity += printItemsList.get(i).getQuantity();

                    sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
                }

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {

                    txts[0] = context.getString(R.string.total_items);
                    txts[1] = "" + totalProductsQuantity;
                }else {

                    txts[0] = "" + totalProductsQuantity;
                    txts[1] =  context.getString(R.string.total_items);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                //-----------------total
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = context.getString(R.string.total_label);
                    txts[1] = "" + invoice.getGrossAmount();
                }else {
                    txts[0] = "" + invoice.getGrossAmount();
                    txts[1] = context.getString(R.string.total_label);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }
            }


            //-----------------Tax
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.tax);
                txts[1] = "" + invoice.getTaxAmount();
            }else {
                txts[0] = "" + invoice.getTaxAmount();
                txts[1] = context.getString(R.string.tax);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //-----------------Discount
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.discount);
                txts[1] = "" + invoice.getDiscountAmount();
            }else {
                txts[0] = "" + invoice.getDiscountAmount();
                txts[1] = context.getString(R.string.discount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }


            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }
            //-----------------Net total

            float calculatedTotal = invoice.getBillAmount();
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.net_total);
                txts[1] = "" + calculatedTotal;
            }else {
                txts[0] = "" + calculatedTotal;
                txts[1] = context.getString(R.string.net_total);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if(invoice.getAdditionalDiscount() > 0) {
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = "";
                    txts[1] = "(ad.dsc -" + invoice.getAdditionalDiscount() + ")";
                }else {
                    txts[0] = "( -" + invoice.getAdditionalDiscount() + ")";
                    txts[1] = "";
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payTitle = context.getString(R.string.payment) + "\b\n";
            sunmiPrinterService.printText(payTitle, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payment_str = context.getString(R.string.payment) + "(c)";
            String txts_3_colmn[];
            int width_3_colmn[];
            int align_3_colmn[];
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn = new String[]{payment_str, context.getString(R.string.mode_label), invoice.getCurrency()};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            }else {
                txts_3_colmn = new String[]{ invoice.getCurrency(), context.getString(R.string.mode_label),payment_str};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payMode = convertPaymentypeToMode(invoice.getPaymentType());

            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn[0] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount();
            }else {
                txts_3_colmn[0] =  "" + invoice.getBillAmount();
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.cash_label);
                txts[1] = "" + invoice.getPaymentAmount();
            }else {
                txts[0] = "" + invoice.getPaymentAmount();
                txts[1] = context.getString(R.string.cash_label);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.due_amount);
                txts[1] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
            }else {
                txts[0] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
                txts[1] = context.getString(R.string.due_amount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText(context.getString(R.string.visit_again), null);

            sunmiPrinterService.lineWrap(1, null);

            if(printerQrData!=null && !printerQrData.isEmpty()) {
                sunmiPrinterService.printQRCode(printerQrData, 10, 0, null);
            }

            sunmiPrinterService.autoOutPaper(null);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * to print the customer info section
     * language support for english and arabic
     * */
    private void printBillCustomerInfo(Context context, String customerName, Integer inv_id, String currency_value, int type, long timestamp){
        try{

            //----------------------------------------------------------------------------------------------------------------------------
            String billTo;
            String invoiceNo;
            String billType;
            String currency;
            String billDate;

            String label_to = context.getString(R.string.to_label);
            String label_inv = context.getString(R.string.invoice_no);
            String label_type = context.getString(R.string.bill_type);
            String label_currency = context.getString(R.string.currency);
            String label_date = context.getString(R.string.bill_date);

            String COLON_SEPARATOR = " : ";
            String str_type;
            if(type == 1){
                str_type = context.getString(R.string.sales_invoice);
            }else {
                str_type = context.getString(R.string.purchase_label);
            }


            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)){
                billTo = label_to + COLON_SEPARATOR + customerName;
                invoiceNo = label_inv + COLON_SEPARATOR +"INV#"+inv_id;
                billType = label_type + COLON_SEPARATOR + str_type;
                currency = label_currency + COLON_SEPARATOR + currency_value;
                billDate = label_date + COLON_SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(timestamp);
            }else {
                billTo = customerName + COLON_SEPARATOR + label_to;
                invoiceNo = "INV#"+inv_id + COLON_SEPARATOR + label_inv;
                billType = str_type + COLON_SEPARATOR + label_type;
                currency = currency_value + COLON_SEPARATOR + label_currency;
                billDate = label_date + COLON_SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(timestamp);

            }


            sunmiPrinterService.printText(billTo+"\n", null); //bill to
            sunmiPrinterService.printText(invoiceNo+"\n", null); //bill inv
            sunmiPrinterService.printText(billType+"\n", null); //bill type
            sunmiPrinterService.printText(currency+"\n", null); //bill currency
            sunmiPrinterService.printText(billDate+"\n", null); //bill date


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to convert integer payment type to string payment mode
     * */
    private String convertPaymentypeToMode(int paymentType){
        switch (paymentType){
            case Constants.PAY_TYPE_CASH:
                return "cash";
            case Constants.PAY_TYPE_ON_ACCOUNT:
                return "bank";
            case Constants.PAY_TYPE_CREDIT_SALE:
                return "credit";
            default:
                return "other";
        }
    }


    /**
     *  Transaction printing:
     *  enter->print->exit(get result) or
     *  enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     *  result)
     */
    public void printTransaction_purchase(Context context, PurchaseInvoiceEntity invoice, CompanyAddressEntity companyDetails, List<PurchaseInvoiceItemHistory> printItemsList, String printerQrData, InnerResultCallback callbcak){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }
        try {
            sunmiPrinterService.enterPrinterBuffer(true);
            printReceipt_purchase(context, invoice, companyDetails, printItemsList, printerQrData);
            sunmiPrinterService.exitPrinterBufferWithCallback(true, callbcak);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * */
    private void printReceipt_purchase1(Context context, PurchaseInvoiceEntity invoice , CompanyAddressEntity companyDetails, List<InvoiceItemHistory> printItemsList, String printerQrData){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }

        try {
            String companyName, address, phone, email;
            if(companyDetails!=null){
                companyName = companyDetails.getCompanyNameEng();
                address = companyDetails.getAddress();
                phone = "Ph: "+ companyDetails.getMobile();
                email = "email: "+ companyDetails.getEmail();
            }else {
                companyName = "POS";
                address = "Address";
                phone = "Ph: +91 9876543210";
                email = "email: email@gmail.com";
            }

            //----------------------------------------------------------------------------------------------------------------------------
            String billTo = context.getString(R.string.to_label) + ": "+ invoice.getCustomerName();
            String invoiceNo = context.getString(R.string.invoice_no) + ": INV#"+ invoice.getId();
            String billType = context.getString(R.string.bill_type) + ": Purchase invoice";
            String currency = context.getString(R.string.currency) + ": "+ invoice.getCurrency();
            String billDate = context.getString(R.string.bill_date) + ": "+ DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp());

            int paper = sunmiPrinterService.getPrinterPaper();

            sunmiPrinterService.printerInit(null);

            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText(companyName+"\n", null); //company name
            sunmiPrinterService.printText(address+"\n", null); //company address
            sunmiPrinterService.printText(phone+"\n", null); //company phone
            sunmiPrinterService.printText(email+"\n", null); //company email

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
            //sunmiPrinterService.printBitmap(bitmap, null);
            //sunmiPrinterService.lineWrap(1, null);

            //sunmiPrinterService.printTextWithFont("address\n", null, 12, null);


            sunmiPrinterService.setAlignment(0, null);

            sunmiPrinterService.printText(billTo+"\n", null); //bill to
            sunmiPrinterService.printText(invoiceNo+"\n", null); //bill inv
            sunmiPrinterService.printText(billType+"\n", null); //bill type
            sunmiPrinterService.printText(currency+"\n", null); //bill currency
            sunmiPrinterService.printText(billDate+"\n", null); //bill date


            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }
            String txts_four_clmn[] = new String[]{"Item", "Rate", "Qty","Total"};
            int width_four_clmn[] = new int[]{1, 1, 1, 1};
            int align_four_clmn[] = new int[]{0, 2, 2 ,2};
            sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String txts[] = new String[]{"Item", "Qty"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};

            if(printItemsList!=null && printItemsList.size()>0){

                int totalProductsQuantity = 0;
                for (int i=0; i<printItemsList.size();i++){
                    txts_four_clmn[0] = ""+printItemsList.get(i).getItemName();
                    txts_four_clmn[1] = ""+printItemsList.get(i).getItemRate();
                    txts_four_clmn[2] = ""+printItemsList.get(i).getQuantity();
                    txts_four_clmn[3] = ""+printItemsList.get(i).getTotal();

                    totalProductsQuantity += printItemsList.get(i).getQuantity();

                    sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
                }

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }


                txts[0] = "Total items";
                txts[1] = ""+totalProductsQuantity;
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                //-----------------total
                txts[0] = "total";
                txts[1] = ""+invoice.getGrossAmount();
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

            }


            //-----------------Tax
            txts[0] = "Tax";
            txts[1] = ""+invoice.getTaxAmount();
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //-----------------Discount
            txts[0] = "Discount";
            txts[1] = ""+invoice.getDiscountAmount();
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }

            //-----------------Net total
            float calculatedTotal = invoice.getBillAmount();

            txts[0] = "Net total";
            txts[1] = ""+calculatedTotal;
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if(invoice.getAdditionalDiscount() > 0) {
                txts[0] = "";
                txts[1] = "(ad.dsc -" + invoice.getAdditionalDiscount()+")";
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            sunmiPrinterService.printText("Payment\b\n", null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payment_str = "payment(c)";
            String txts_3_colmn[] = new String[]{payment_str, "Mode", invoice.getCurrency()};
            int width_3_colmn[] = new int[]{1, 1, 1};
            int align_3_colmn[] = new int[]{0, 1, 2};
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payMode = convertPaymentypeToMode(invoice.getPaymentType());

            txts_3_colmn[0] = ""+invoice.getBillAmount()+"("+invoice.getCurrency()+")";
            txts_3_colmn[1] = payMode;
            txts_3_colmn[2] = ""+invoice.getBillAmount();
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            //-----------------Cash
            txts[0] = "Cash";
            txts[1] = ""+invoice.getPaymentAmount();
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            //-----------------Cash
            txts[0] = "Due amount";
            txts[1] = ""+(invoice.getBillAmount() - invoice.getPaymentAmount());
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText(context.getString(R.string.visit_again), null);

            sunmiPrinterService.lineWrap(1, null);

            if(printerQrData!=null && !printerQrData.isEmpty()) {
                sunmiPrinterService.printQRCode(printerQrData, 10, 0, null);
            }

            sunmiPrinterService.autoOutPaper(null);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * to print receipt for sales
     * */
    private void printReceipt_purchase(Context context, PurchaseInvoiceEntity invoice , CompanyAddressEntity companyDetails, List<PurchaseInvoiceItemHistory> printItemsList, String printerQrData){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }

        try {

            String companyName, address, phone, email;
            if(companyDetails!=null){

                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)){
                    companyName = companyDetails.getCompanyNameEng();
                }else {
                    companyName = companyDetails.getCompanyNameAr();
                }
                address = companyDetails.getAddress();
                phone = "Ph: "+ companyDetails.getMobile();
                email = "email: "+ companyDetails.getEmail();
            }else {
                companyName = "POS";
                address = "Address";
                phone = "Ph: +91 9876543210";
                email = "email: email@gmail.com";
            }

            //----------------------------------------------------------------------------------------------------------------------------

            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);

            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText(companyName+"\n", null); //company name
            sunmiPrinterService.printText(address+"\n", null); //company address
            sunmiPrinterService.printText(phone+"\n", null); //company phone
            sunmiPrinterService.printText(email+"\n", null); //company email

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);
            //sunmiPrinterService.printBitmap(bitmap, null);
            //sunmiPrinterService.lineWrap(1, null);

            ALIGNMENT = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN))? ALIGN_LEFT: ALIGN_RIGHT;
            sunmiPrinterService.setAlignment(ALIGNMENT, null);
            printBillCustomerInfo(context, invoice.getCustomerName(), invoice.getId(), invoice.getCurrency(),2, invoice.getTimestamp());

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }
            //sunmiPrinterService.printTextWithFont("address\n", null, 12, null);
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }

            String title_item = context.getString(R.string.item_label);
            String title_rate = context.getString(R.string.rate_label);
            String title_qty = context.getString(R.string.qty_label);
            String title_total = context.getString(R.string.total_label);

            String txts_four_clmn[];
            int width_four_clmn[];
            int align_four_clmn[];
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)){
                txts_four_clmn = new String[]{title_item, title_rate, title_qty,title_total};
                width_four_clmn = new int[]{1, 1, 1, 1};
                align_four_clmn = new int[]{0, 2, 2 ,2};
            }else {
                txts_four_clmn = new String[]{title_total, title_qty, title_rate,title_item};
                width_four_clmn = new int[]{1, 1, 1, 1};
                align_four_clmn = new int[]{0, 0, 0 ,2};
            }

            sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            String txts[] = new String[]{"empty", "empty"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};


            if(printItemsList!=null && printItemsList.size()>0){

                int totalProductsQuantity = 0;
                for (int i=0; i<printItemsList.size();i++){
                    if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getItemName();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getTotal();
                    }else {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getTotal();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getItemName();
                    }

                    totalProductsQuantity += printItemsList.get(i).getQuantity();

                    sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
                }

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {

                    txts[0] = context.getString(R.string.total_items);
                    txts[1] = "" + totalProductsQuantity;
                }else {

                    txts[0] = "" + totalProductsQuantity;
                    txts[1] =  context.getString(R.string.total_items);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                //-----------------total
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = context.getString(R.string.total_label);
                    txts[1] = "" + invoice.getGrossAmount();
                }else {
                    txts[0] = "" + invoice.getGrossAmount();
                    txts[1] = context.getString(R.string.total_label);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if(paper == 1){
                    sunmiPrinterService.printText("--------------------------------\n", null);
                }else{
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }
            }


            //-----------------Tax
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.tax);
                txts[1] = "" + invoice.getTaxAmount();
            }else {
                txts[0] = "" + invoice.getTaxAmount();
                txts[1] = context.getString(R.string.tax);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //-----------------Discount
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.discount);
                txts[1] = "" + invoice.getDiscountAmount();
            }else {
                txts[0] = "" + invoice.getDiscountAmount();
                txts[1] = context.getString(R.string.discount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }


            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }
            //-----------------Net total

            float calculatedTotal = invoice.getBillAmount();
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.net_total);
                txts[1] = "" + calculatedTotal;
            }else {
                txts[0] = "" + calculatedTotal;
                txts[1] = context.getString(R.string.net_total);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if(invoice.getAdditionalDiscount() > 0) {
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = "";
                    txts[1] = "(ad.dsc -" + invoice.getAdditionalDiscount() + ")";
                }else {
                    txts[0] = "( -" + invoice.getAdditionalDiscount() + ")";
                    txts[1] = "";
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payTitle = context.getString(R.string.payment) + "\b\n";
            sunmiPrinterService.printText(payTitle, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payment_str = context.getString(R.string.payment) + "(c)";
            String txts_3_colmn[];
            int width_3_colmn[];
            int align_3_colmn[];
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn = new String[]{payment_str, context.getString(R.string.mode_label), invoice.getCurrency()};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            }else {
                txts_3_colmn = new String[]{ invoice.getCurrency(), context.getString(R.string.mode_label),payment_str};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            String payMode = convertPaymentypeToMode(invoice.getPaymentType());

            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn[0] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount();
            }else {
                txts_3_colmn[0] =  "" + invoice.getBillAmount();
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.cash_label);
                txts[1] = "" + invoice.getPaymentAmount();
            }else {
                txts[0] = "" + invoice.getPaymentAmount();
                txts[1] = context.getString(R.string.cash_label);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if(CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.due_amount);
                txts[1] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
            }else {
                txts[0] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
                txts[1] = context.getString(R.string.due_amount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText(context.getString(R.string.visit_again), null);

            sunmiPrinterService.lineWrap(1, null);

            if(printerQrData!=null && !printerQrData.isEmpty()) {
                sunmiPrinterService.printQRCode(printerQrData, 10, 0, null);
            }

            sunmiPrinterService.autoOutPaper(null);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Open cash box
     *  This method can be used on Sunmi devices with a cash drawer interface
     *  If there is no cash box (such as V1、P1) or the call fails, an exception will be thrown
     *
     *  Reference to https://docs.sunmi.com/general-function-modules/external-device-debug/cash-box-driver/}
     */
    public void openCashBox(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.openDrawer(null);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * LCD screen control
     * @param flag 1 —— Initialization
     *             2 —— Light up screen
     *             3 —— Extinguish screen
     *             4 —— Clear screen contents
     */
    public void controlLcd(int flag){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.sendLCDCommand(flag);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    /**
     * Display text SUNMI,font size is 16 and format is fill
     * sendLCDFillString(txt, size, fill, callback)
     * Since the screen pixel height is 40, the font should not exceed 40
     */
    public void sendTextToLcd(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.sendLCDFillString("SUNMI", 16, true, new InnerLcdCallback() {
                @Override
                public void onRunResult(boolean show) throws RemoteException {
                    //TODO handle result
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Display two lines and one empty line in the middle
     */
    public void sendTextsToLcd(){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            String[] texts = {"SUNMI", null, "SUNMI"};
            int[] align = {2, 1, 2};
            sunmiPrinterService.sendLCDMultiString(texts, align, new InnerLcdCallback() {
                @Override
                public void onRunResult(boolean show) throws RemoteException {
                    //TODO handle result
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Display one 128x40 pixels and opaque picture
     */
    public void sendPicToLcd(Bitmap pic){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return;
        }

        try {
            sunmiPrinterService.sendLCDBitmap(pic, new InnerLcdCallback() {
                @Override
                public void onRunResult(boolean show) throws RemoteException {
                    //TODO handle result
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     *  Sample print receipt
     */
    public void printExample(Context context){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }

        try {
            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText("测试样张\n", null);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_background);
            sunmiPrinterService.printBitmap(bitmap, null);
            sunmiPrinterService.lineWrap(1, null);
            sunmiPrinterService.setAlignment(0, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }
            sunmiPrinterService.printTextWithFont("说明：这是一个自定义的小票样式例子,开发者可以仿照此进行自己的构建\n",
                    null, 12, null);
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
            }
            String txts[] = new String[]{"商品", "价格"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            txts[0] = "汉堡";
            txts[1] = "17¥";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "可乐";
            txts[1] = "10¥";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "薯条";
            txts[1] = "11¥";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "炸鸡";
            txts[1] = "11¥";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            txts[0] = "圣代";
            txts[1] = "10¥";
            sunmiPrinterService.printColumnsString(txts, width, align, null);
            if(paper == 1){
                sunmiPrinterService.printText("--------------------------------\n", null);
            }else{
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }
            sunmiPrinterService.printTextWithFont("总计:          59¥\b", null, 40, null);
            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printQRCode("谢谢惠顾", 10, 0, null);
            sunmiPrinterService.setFontSize(36, null);
            sunmiPrinterService.printText("谢谢惠顾", null);
            sunmiPrinterService.autoOutPaper(null);
         } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to report the real-time query status of the printer, which can be used before each
     * printing
     */
    public void showPrinterStatus(Context context){
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }
        String result = "Interface is too low to implement interface";
        try {
            int res = sunmiPrinterService.updatePrinterState();
            switch (res){
                case 1:
                    result = "printer is running";
                    break;
                case 2:
                    result = "printer found but still initializing";
                    break;
                case 3:
                    result = "printer hardware interface is abnormal and needs to be reprinted";
                    break;
                case 4:
                    result = "printer is out of paper";
                    break;
                case 5:
                    result = "printer is overheating";
                    break;
                case 6:
                    result = "printer's cover is not closed";
                    break;
                case 7:
                    result = "printer's cutter is abnormal";
                    break;
                case 8:
                    result = "printer's cutter is normal";
                    break;
                case 9:
                    result = "not found black mark paper";
                    break;
                case 505:
                    result = "printer does not exist";
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }

    /**
     * Demo printing a label
     * After printing one label, in order to facilitate the user to tear the paper, call
     * labelOutput to push the label paper out of the paper hatch
     * 演示打印一张标签
     * 打印单张标签后为了方便用户撕纸可调用labelOutput,将标签纸推出纸舱口
     */
    public void printOneLabel() {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }
        try {
            sunmiPrinterService.labelLocate();
            printLabelContent();
            sunmiPrinterService.labelOutput();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Demo printing multi label
     *
     After printing multiple labels, choose whether to push the label paper to the paper hatch according to the needs
     * 演示打印多张标签
     * 打印多张标签后根据需求选择是否推出标签纸到纸舱口
     */
    public void printMultiLabel(int num) {
        if(sunmiPrinterService == null){
            //TODO Service disconnection processing
            return ;
        }
        try {
            for(int i = 0; i < num; i++){
                sunmiPrinterService.labelLocate();
                printLabelContent();
            }
            sunmiPrinterService.labelOutput();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     *  Custom label ticket content
     *  In the example, not all labels can be applied. In actual use, please pay attention to adapting the size of the label. You can adjust the font size and content position.
     *  自定义的标签小票内容
     *  例子中并不能适用所有标签纸，实际使用时注意要自适配标签纸大小，可通过调节字体大小，内容位置等方式
     */
    private void printLabelContent() throws RemoteException {
        sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.ENABLE);
        sunmiPrinterService.lineWrap(1, null);
        sunmiPrinterService.setAlignment(0, null);
        sunmiPrinterService.printText("商品         豆浆\n", null);
        sunmiPrinterService.printText("到期时间         12-13  14时\n", null);
        sunmiPrinterService.printBarCode("{C1234567890123456",  8, 90, 2, 2, null);
        sunmiPrinterService.lineWrap(1, null);
    }
}
