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

    private int ALIGN_LEFT = 0;
    private int ALIGN_CENTER = 1;
    private int ALIGN_RIGHT = 2;
    private int ALIGNMENT = ALIGN_LEFT;

    /**
     * sunmiPrinter means checking the printer connection status
     */
    public int sunmiPrinter = CheckSunmiPrinter;
    /**
     * SunmiPrinterService for API
     */
    private SunmiPrinterService sunmiPrinterService;

    private static SunmiPrintHelper helper = new SunmiPrintHelper();

    private SunmiPrintHelper() {
    }

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
    public void initSunmiPrinterService(Context context) {
        try {
            boolean ret = InnerPrinterManager.getInstance().bindService(context,
                    innerPrinterCallback);
            if (!ret) {
                sunmiPrinter = NoSunmiPrinter;
            }
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }

    /**
     * deInit sunmi print service
     */
    public void deInitSunmiPrinterService(Context context) {
        try {
            if (sunmiPrinterService != null) {
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
    private void checkSunmiPrinterService(SunmiPrinterService service) {
        boolean ret = false;
        try {
            ret = InnerPrinterManager.getInstance().hasPrinter(service);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
        sunmiPrinter = ret ? FoundSunmiPrinter : NoSunmiPrinter;
    }

    /**
     * Some conditions can cause interface calls to fail
     * For example: the version is too low、device does not support
     * You can see {@link ExceptionConst}
     * So you have to handle these exceptions
     */
    private void handleRemoteException(RemoteException e) {
        //TODO process when get one exception
    }

    /**
     * send esc cmd
     */
    public void sendRawData(byte[] data) {
        if (sunmiPrinterService == null) {
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
     * Printer cuts paper and throws exception on machines without a cutter
     */
    public void cutpaper() {
        if (sunmiPrinterService == null) {
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
     * Initialize the printer
     * All style settings will be restored to default
     */
    public void initPrinter() {
        if (sunmiPrinterService == null) {
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
     * paper feed three lines
     * Not disabled when line spacing is set to 0
     */
    public void print3Line() {
        if (sunmiPrinterService == null) {
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
    public String getPrinterSerialNo() {
        if (sunmiPrinterService == null) {
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
    public String getDeviceModel() {
        if (sunmiPrinterService == null) {
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
    public String getPrinterVersion() {
        if (sunmiPrinterService == null) {
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
    public String getPrinterPaper() {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return "";
        }
        try {
            return sunmiPrinterService.getPrinterPaper() == 1 ? "58mm" : "80mm";
        } catch (RemoteException e) {
            handleRemoteException(e);
            return "";
        }
    }

    /**
     * Get paper specifications
     */
    public void getPrinterHead(InnerResultCallback callbcak) {
        if (sunmiPrinterService == null) {
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
    public void getPrinterDistance(InnerResultCallback callback) {
        if (sunmiPrinterService == null) {
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
    public void setAlign(int align) {
        if (sunmiPrinterService == null) {
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
     * Due to the distance between the paper hatch and the print head,
     * the paper needs to be fed out automatically
     * But if the Api does not support it, it will be replaced by printing three lines
     */
    public void feedPaper() {
        if (sunmiPrinterService == null) {
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
     * More settings reference documentation {@link WoyouConsts}
     * printTextWithFont:
     * Custom fonts require V4.14.0 or later!
     * You can put the custom font in the 'assets' directory and Specify the font name parameters
     * in the Api.
     */
    public void printText(String content, float size, boolean isBold, boolean isUnderLine,
                          String typeface) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, isBold ?
                        WoyouConsts.ENABLE : WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                if (isBold) {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOn(), null);
                } else {
                    sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
                }
            }
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_UNDERLINE, isUnderLine ?
                        WoyouConsts.ENABLE : WoyouConsts.DISABLE);
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
        if (sunmiPrinterService == null) {
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
        if (sunmiPrinterService == null) {
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
        if (sunmiPrinterService == null) {
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
     * Print pictures and text in the specified orde
     * After the picture is printed,
     * the line feed output needs to be called,
     * otherwise it will be saved in the cache
     * In this example, the image will be printed because the print text content is added
     */
    public void printBitmap(Bitmap bitmap, int orientation) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {
            if (orientation == 0) {
                sunmiPrinterService.printBitmap(bitmap, null);
                sunmiPrinterService.printText("横向排列\n", null);
                sunmiPrinterService.printBitmap(bitmap, null);
                sunmiPrinterService.printText("横向排列\n", null);
            } else {
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
     * Transaction printing:
     * enter->print->exit(get result) or
     * enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     * result)
     */
    public void printTransaction_sales(Context context, InvoiceEntity invoice, CompanyAddressEntity companyDetails, List<InvoiceItemHistory> printItemsList, String printerQrData, InnerResultCallback callbcak) {
        if (sunmiPrinterService == null) {
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

    /**
     * to print receipt for sales
     */
    private void printReceipt_sales(Context context, InvoiceEntity invoice, CompanyAddressEntity companyDetails, List<InvoiceItemHistory> printItemsList, String printerQrData) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {

            String companyName, address, phone, email;
            if (companyDetails != null) {

                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    companyName = companyDetails.getCompanyNameEng();
                } else {
                    companyName = companyDetails.getCompanyNameAr();
                }
                address = companyDetails.getAddress();
                phone = "Ph: " + companyDetails.getMobile();
                email = "email: " + companyDetails.getEmail();
            } else {
                companyName = "POS";
                address = "Address";
                phone = "Ph: +91 9876543210";
                email = "email: email@gmail.com";
            }


            //----------------------------------------------------------------------------------------------------------------------------

            int paper = sunmiPrinterService.getPrinterPaper();

            sunmiPrinterService.printerInit(null);


            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText(companyName + "\n", null); //company name
            sunmiPrinterService.printText(address + "\n", null); //company address
            sunmiPrinterService.printText(phone + "\n", null); //company phone
            sunmiPrinterService.printText(email + "\n", null); //company email

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            ALIGNMENT = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) ? ALIGN_LEFT : ALIGN_RIGHT;
            sunmiPrinterService.setAlignment(ALIGNMENT, null);
            printBillCustomerInfo(context, invoice.getCustomerName(), invoice.getId(), invoice.getCurrency(), 1, invoice.getTimestamp());

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }
            //sunmiPrinterService.printTextWithFont("address\n", null, 12, null);
            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
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
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_four_clmn = new String[]{title_item, title_rate, title_qty, title_total};
                width_four_clmn = new int[]{2, 1, 1, 1};
                align_four_clmn = new int[]{0, 2, 1, 2};
            } else {
                txts_four_clmn = new String[]{title_total, title_qty, title_rate, title_item};
                width_four_clmn = new int[]{1, 1, 1, 2};
                align_four_clmn = new int[]{0, 1, 0, 2};
            }

            sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }
            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String txts[] = new String[]{"empty", "empty"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};


            if (printItemsList != null && printItemsList.size() > 0) {

                int totalProductsQuantity = 0;
                for (int i = 0; i < printItemsList.size(); i++) {
                    if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getItemName();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getTotal();
                    } else {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getTotal();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getItemName();
                    }

                    totalProductsQuantity += printItemsList.get(i).getQuantity();

                    sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
                }

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {

                    txts[0] = context.getString(R.string.total_items);
                    txts[1] = "" + totalProductsQuantity;
                } else {

                    txts[0] = "" + totalProductsQuantity;
                    txts[1] = context.getString(R.string.total_items);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                //-----------------total
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = context.getString(R.string.total_label);
                    txts[1] = "" + invoice.getGrossAmount();
                } else {
                    txts[0] = "" + invoice.getGrossAmount();
                    txts[1] = context.getString(R.string.total_label);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }
            }


            //-----------------Tax
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.tax);
                txts[1] = "" + invoice.getTaxAmount();
            } else {
                txts[0] = "" + invoice.getTaxAmount();
                txts[1] = context.getString(R.string.tax);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //-----------------Discount
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.discount);
                txts[1] = "" + invoice.getDiscountAmount();
            } else {
                txts[0] = "" + invoice.getDiscountAmount();
                txts[1] = context.getString(R.string.discount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
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
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.net_total);
                txts[1] = "" + calculatedTotal;
            } else {
                txts[0] = "" + calculatedTotal;
                txts[1] = context.getString(R.string.net_total);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if (invoice.getAdditionalDiscount() > 0) {
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = "";
                    txts[1] = "(ad.dsc -" + invoice.getAdditionalDiscount() + ")";
                } else {
                    txts[0] = "( -" + invoice.getAdditionalDiscount() + ")";
                    txts[1] = "";
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payTitle = context.getString(R.string.payment) + "\b\n";
            sunmiPrinterService.printText(payTitle, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payment_str = context.getString(R.string.payment) + "(c)";
            String txts_3_colmn[];
            int width_3_colmn[];
            int align_3_colmn[];
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn = new String[]{payment_str, context.getString(R.string.mode_label), invoice.getCurrency()};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            } else {
                txts_3_colmn = new String[]{invoice.getCurrency(), context.getString(R.string.mode_label), payment_str};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payMode = convertPaymentypeToMode(invoice.getPaymentType());

            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn[0] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount();
            } else {
                txts_3_colmn[0] = "" + invoice.getBillAmount();
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.cash_label);
                txts[1] = "" + invoice.getPaymentAmount();
            } else {
                txts[0] = "" + invoice.getPaymentAmount();
                txts[1] = context.getString(R.string.cash_label);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.due_amount);
                txts[1] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
            } else {
                txts[0] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
                txts[1] = context.getString(R.string.due_amount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText(context.getString(R.string.visit_again), null);

            sunmiPrinterService.lineWrap(1, null);

            if (printerQrData != null && !printerQrData.isEmpty() && !printerQrData.equals(Constants.EMPTY)) {
                sunmiPrinterService.printQRCode(printerQrData, 6, 0, null);
            }

            sunmiPrinterService.autoOutPaper(null);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * to convert integer payment type to string payment mode
     */
    private String convertPaymentypeToMode(int paymentType) {
        switch (paymentType) {
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
     * Transaction printing:
     * enter->print->exit(get result) or
     * enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     * result)
     */
    public void printTransaction_purchase(Context context, PurchaseInvoiceEntity invoice, CompanyAddressEntity companyDetails, List<PurchaseInvoiceItemHistory> printItemsList, String printerQrData, InnerResultCallback callbcak) {
        if (sunmiPrinterService == null) {
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
     * to print receipt for sales
     */
    private void printReceipt_purchase(Context context, PurchaseInvoiceEntity invoice, CompanyAddressEntity companyDetails, List<PurchaseInvoiceItemHistory> printItemsList, String printerQrData) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            return;
        }

        try {

            String companyName, address, phone, email;
            if (companyDetails != null) {

                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    companyName = companyDetails.getCompanyNameEng();
                } else {
                    companyName = companyDetails.getCompanyNameAr();
                }
                address = companyDetails.getAddress();
                phone = "Ph: " + companyDetails.getMobile();
                email = "email: " + companyDetails.getEmail();
            } else {
                companyName = "POS";
                address = "Address";
                phone = "Ph: +91 9876543210";
                email = "email: email@gmail.com";
            }

            //----------------------------------------------------------------------------------------------------------------------------

            int paper = sunmiPrinterService.getPrinterPaper();
            sunmiPrinterService.printerInit(null);

            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText(companyName + "\n", null); //company name
            sunmiPrinterService.printText(address + "\n", null); //company address
            sunmiPrinterService.printText(phone + "\n", null); //company phone
            sunmiPrinterService.printText(email + "\n", null); //company email

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }


            ALIGNMENT = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) ? ALIGN_LEFT : ALIGN_RIGHT;
            sunmiPrinterService.setAlignment(ALIGNMENT, null);
            printBillCustomerInfo(context, invoice.getCustomerName(), invoice.getId(), invoice.getCurrency(), 2, invoice.getTimestamp());

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.SET_LINE_SPACING, 0);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(new byte[]{0x1B, 0x33, 0x00}, null);
            }

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
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
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_four_clmn = new String[]{title_item, title_rate, title_qty, title_total};
                width_four_clmn = new int[]{2, 1, 1, 1};
                align_four_clmn = new int[]{0, 2, 1, 2};
            } else {
                txts_four_clmn = new String[]{title_total, title_qty, title_rate, title_item};
                width_four_clmn = new int[]{1, 1, 1, 2};
                align_four_clmn = new int[]{0, 1, 0, 2};
            }

            sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            String txts[] = new String[]{"empty", "empty"};
            int width[] = new int[]{1, 1};
            int align[] = new int[]{0, 2};


            if (printItemsList != null && printItemsList.size() > 0) {

                int totalProductsQuantity = 0;
                for (int i = 0; i < printItemsList.size(); i++) {
                    if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getItemName();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getTotal();
                    } else {
                        txts_four_clmn[0] = "" + printItemsList.get(i).getTotal();
                        txts_four_clmn[1] = "" + printItemsList.get(i).getQuantity();
                        txts_four_clmn[2] = "" + printItemsList.get(i).getItemRate();
                        txts_four_clmn[3] = "" + printItemsList.get(i).getItemName();
                    }

                    totalProductsQuantity += printItemsList.get(i).getQuantity();

                    sunmiPrinterService.printColumnsString(txts_four_clmn, width_four_clmn, align_four_clmn, null);
                }

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {

                    txts[0] = context.getString(R.string.total_items);
                    txts[1] = "" + totalProductsQuantity;
                } else {

                    txts[0] = "" + totalProductsQuantity;
                    txts[1] = context.getString(R.string.total_items);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }

                //-----------------total
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = context.getString(R.string.total_label);
                    txts[1] = "" + invoice.getGrossAmount();
                } else {
                    txts[0] = "" + invoice.getGrossAmount();
                    txts[1] = context.getString(R.string.total_label);
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);

                if (paper == 1) {
                    sunmiPrinterService.printText("--------------------------------\n", null);
                } else {
                    sunmiPrinterService.printText("------------------------------------------------\n", null);
                }
            }


            //-----------------Tax
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.tax);
                txts[1] = "" + invoice.getTaxAmount();
            } else {
                txts[0] = "" + invoice.getTaxAmount();
                txts[1] = context.getString(R.string.tax);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            //-----------------Discount
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.discount);
                txts[1] = "" + invoice.getDiscountAmount();
            } else {
                txts[0] = "" + invoice.getDiscountAmount();
                txts[1] = context.getString(R.string.discount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
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
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.net_total);
                txts[1] = "" + calculatedTotal;
            } else {
                txts[0] = "" + calculatedTotal;
                txts[1] = context.getString(R.string.net_total);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            try {
                sunmiPrinterService.setPrinterStyle(WoyouConsts.ENABLE_BOLD, WoyouConsts.DISABLE);
            } catch (RemoteException e) {
                sunmiPrinterService.sendRAWData(ESCUtil.boldOff(), null);
            }

            if (invoice.getAdditionalDiscount() > 0) {
                width = new int[]{1, 1};
                align = new int[]{0, 2};
                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    txts[0] = "";
                    txts[1] = "(ad.dsc -" + invoice.getAdditionalDiscount() + ")";
                } else {
                    txts[0] = "( -" + invoice.getAdditionalDiscount() + ")";
                    txts[1] = "";
                }
                sunmiPrinterService.printColumnsString(txts, width, align, null);
            }

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payTitle = context.getString(R.string.payment) + "\b\n";
            sunmiPrinterService.printText(payTitle, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n",
                        null);
            }

            String payment_str = context.getString(R.string.payment) + "(c)";
            String txts_3_colmn[];
            int width_3_colmn[];
            int align_3_colmn[];
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn = new String[]{payment_str, context.getString(R.string.mode_label), invoice.getCurrency()};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            } else {
                txts_3_colmn = new String[]{invoice.getCurrency(), context.getString(R.string.mode_label), payment_str};
                width_3_colmn = new int[]{1, 1, 1};
                align_3_colmn = new int[]{0, 1, 2};
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            String payMode = convertPaymentypeToMode(invoice.getPaymentType());

            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts_3_colmn[0] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount();
            } else {
                txts_3_colmn[0] = "" + invoice.getBillAmount();
                txts_3_colmn[1] = payMode;
                txts_3_colmn[2] = "" + invoice.getBillAmount() + "(" + invoice.getCurrency() + ")";
            }
            sunmiPrinterService.printColumnsString(txts_3_colmn, width_3_colmn, align_3_colmn, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.cash_label);
                txts[1] = "" + invoice.getPaymentAmount();
            } else {
                txts[0] = "" + invoice.getPaymentAmount();
                txts[1] = context.getString(R.string.cash_label);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            //-----------------Cash
            width = new int[]{1, 1};
            align = new int[]{0, 2};
            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                txts[0] = context.getString(R.string.due_amount);
                txts[1] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
            } else {
                txts[0] = "" + (invoice.getBillAmount() - invoice.getPaymentAmount());
                txts[1] = context.getString(R.string.due_amount);
            }
            sunmiPrinterService.printColumnsString(txts, width, align, null);

            if (paper == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

            sunmiPrinterService.setAlignment(1, null);
            sunmiPrinterService.printText(context.getString(R.string.visit_again), null);

            sunmiPrinterService.lineWrap(1, null);

            if (printerQrData != null && !printerQrData.isEmpty() && !printerQrData.equals(Constants.EMPTY)) {
                sunmiPrinterService.printQRCode(printerQrData, 6, 0, null);
            }

            sunmiPrinterService.autoOutPaper(null);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * Transaction printing:
     * enter->print->exit(get result) or
     * enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     * result)
     */
    public void printSalesReportTransaction(Context context, List<InvoiceEntity> reportList, InnerResultCallback callbcak) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            showToast(context, "printer service null");
            return;
        }

        try {

            sunmiPrinterService.enterPrinterBuffer(true);
            printSalesReport(context, reportList);
            sunmiPrinterService.exitPrinterBufferWithCallback(true, callbcak);

        } catch (RemoteException e) {
            showToast(context, "Report transaction");
            e.printStackTrace();
        }
    }

    private void printSalesReport(Context context, List<InvoiceEntity> reportList) {
        try {

            if (sunmiPrinterService == null) {
                //TODO Service disconnection processing
                return;
            }


            //-------  BEGINNING --------
            //get paper type
            int paper = sunmiPrinterService.getPrinterPaper();

            //ini printer
            sunmiPrinterService.printerInit(null);
            //get title
            String title = context.getString(R.string.sales_report);
            //set alignment to print title
            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText(title + "\n", null);

            //add a line separator
            addLineSeparator(paper); //---------------

            ALIGNMENT = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) ? ALIGN_LEFT : ALIGN_RIGHT;
            sunmiPrinterService.setAlignment(ALIGNMENT, null);


            String SEPARATOR = " : ";
            String DATE_PREFIX, MOP_PREFIX,NET_PREFIX,ID_PREFIX , GRAND_TOTAL_PREFIX, TAX_PREFIX, CUSTOMER_PREFIX;
            DATE_PREFIX = context.getResources().getString(R.string.date_label);
            MOP_PREFIX = context.getResources().getString(R.string.mop);
            NET_PREFIX = context.getResources().getString(R.string.net_amount);
            GRAND_TOTAL_PREFIX = context.getResources().getString(R.string.gross_amount);
            ID_PREFIX = "INV";
            TAX_PREFIX = context.getResources().getString(R.string.tax_label);
            CUSTOMER_PREFIX = context.getResources().getString(R.string.customer);

            String invoiceLabel, customerName , dateLabel , mop_label , grandTotal_label , netAmountLabel ,tax_label;

            if (reportList.size() > 0) {

                InvoiceEntity invoice;
                for (int i = 0; i < reportList.size(); i++) {

                    invoice = reportList.get(i);

                    if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                        //set alignment to print title
                        sunmiPrinterService.setAlignment(ALIGN_LEFT, null);

                        //ID
                        invoiceLabel = ID_PREFIX + SEPARATOR + invoice.getId();
                        sunmiPrinterService.printText(invoiceLabel + "\n", null);

                        //Customer
                        customerName = CUSTOMER_PREFIX + SEPARATOR + invoice.getCustomerName();
                        sunmiPrinterService.printText(customerName + "\n", null);

                        //Date
                        dateLabel = DATE_PREFIX + SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp());
                        sunmiPrinterService.printText(dateLabel + "\n", null);

                        //Mop
                        mop_label = MOP_PREFIX + SEPARATOR +invoice.getGrossAmount();
                        sunmiPrinterService.printText(mop_label + "\n", null);

                        //Grand total
                        grandTotal_label = GRAND_TOTAL_PREFIX + SEPARATOR + invoice.getBillAmount();
                        sunmiPrinterService.printText(grandTotal_label + "\n", null);

                        //net amount
                        float netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                        netAmountLabel = NET_PREFIX + SEPARATOR + netAmount;
                        sunmiPrinterService.printText(netAmountLabel + "\n", null);

                        //tax
                        tax_label = TAX_PREFIX + SEPARATOR + invoice.getTaxAmount();
                        sunmiPrinterService.printText(tax_label + "\n", null);

                    } else {
                        //set alignment to print title
                        sunmiPrinterService.setAlignment(ALIGN_RIGHT, null);

                        //ID
                        String idString = ""+invoice.getId();
                        invoiceLabel = ID_PREFIX + SEPARATOR + idString;
                        sunmiPrinterService.printText(invoiceLabel + "\n", null);

                        //Customer
                        customerName = invoice.getCustomerName() + SEPARATOR +  CUSTOMER_PREFIX;
                        sunmiPrinterService.printText(customerName + "\n", null);

                        //Date
                        dateLabel = DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp())+ SEPARATOR + DATE_PREFIX;
                        sunmiPrinterService.printText(dateLabel + "\n", null);

                        //Mop
                        String mop = ""+invoice.getGrossAmount();
                        mop_label = mop + SEPARATOR + MOP_PREFIX;
                        sunmiPrinterService.printText(mop_label + "\n", null);

                        //Grand total
                        grandTotal_label = GRAND_TOTAL_PREFIX + SEPARATOR + ""+invoice.getBillAmount();
                        sunmiPrinterService.printText(grandTotal_label + "\n", null);

                        //net amount
                        float netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                        String str_netamount = ""+netAmount;
                        netAmountLabel = str_netamount + SEPARATOR  + NET_PREFIX;
                        sunmiPrinterService.printText(netAmountLabel + "\n", null);

                        //tax
                        tax_label = TAX_PREFIX + SEPARATOR + invoice.getTaxAmount();
                        sunmiPrinterService.printText(tax_label + "\n", null);
                    }

                    //line separator after each report item
                    addLineSeparator(paper);
                }
            }

            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText("******"+ "\n", null);

            sunmiPrinterService.autoOutPaper(null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Transaction printing:
     * enter->print->exit(get result) or
     * enter->first print->commit(get result)->twice print->commit(get result)->exit(don't care
     * result)
     */
    public void printPurchaseReportTransaction(Context context, List<PurchaseInvoiceEntity> reportList, InnerResultCallback callbcak) {
        if (sunmiPrinterService == null) {
            //TODO Service disconnection processing
            showToast(context, "printer service null");
            return;
        }

        try {

            sunmiPrinterService.enterPrinterBuffer(true);
            printPurchaseReport(context, reportList);
            sunmiPrinterService.exitPrinterBufferWithCallback(true, callbcak);

        } catch (RemoteException e) {
            showToast(context, "Report transaction");
            e.printStackTrace();
        }
    }

    private void printPurchaseReport(Context context, List<PurchaseInvoiceEntity> reportList) {
        try {

            if (sunmiPrinterService == null) {
                //TODO Service disconnection processing
                return;
            }


            //-------  BEGINNING --------
            //get paper type
            int paper = sunmiPrinterService.getPrinterPaper();

            //ini printer
            sunmiPrinterService.printerInit(null);
            //get title
            String title = context.getString(R.string.purchase_report);
            //set alignment to print title
            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText(title + "\n", null);

            //add a line separator
            addLineSeparator(paper); //---------------

            ALIGNMENT = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) ? ALIGN_LEFT : ALIGN_RIGHT;
            sunmiPrinterService.setAlignment(ALIGNMENT, null);


            String SEPARATOR = " : ";
            String DATE_PREFIX, MOP_PREFIX,NET_PREFIX,ID_PREFIX , GRAND_TOTAL_PREFIX, TAX_PREFIX, CUSTOMER_PREFIX;
            DATE_PREFIX = context.getResources().getString(R.string.date_label);
            MOP_PREFIX = context.getResources().getString(R.string.mop);
            NET_PREFIX = context.getResources().getString(R.string.net_amount);
            GRAND_TOTAL_PREFIX = context.getResources().getString(R.string.gross_amount);
            ID_PREFIX = "INV";
            TAX_PREFIX = context.getResources().getString(R.string.tax_label);
            CUSTOMER_PREFIX = context.getResources().getString(R.string.customer);

            String invoiceLabel, customerName , dateLabel , mop_label , grandTotal_label , netAmountLabel ,tax_label;

            if (reportList.size() > 0) {

                PurchaseInvoiceEntity invoice;
                for (int i = 0; i < reportList.size(); i++) {

                    invoice = reportList.get(i);

                    if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                        //set alignment to print title
                        sunmiPrinterService.setAlignment(ALIGN_LEFT, null);

                        //ID
                        invoiceLabel = ID_PREFIX + SEPARATOR + invoice.getId();
                        sunmiPrinterService.printText(invoiceLabel + "\n", null);

                        //Customer
                        customerName = CUSTOMER_PREFIX + SEPARATOR + invoice.getCustomerName();
                        sunmiPrinterService.printText(customerName + "\n", null);

                        //Date
                        dateLabel = DATE_PREFIX + SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp());
                        sunmiPrinterService.printText(dateLabel + "\n", null);

                        //Mop
                        mop_label = MOP_PREFIX + SEPARATOR +invoice.getGrossAmount();
                        sunmiPrinterService.printText(mop_label + "\n", null);

                        //Grand total
                        grandTotal_label = GRAND_TOTAL_PREFIX + SEPARATOR + invoice.getBillAmount();
                        sunmiPrinterService.printText(grandTotal_label + "\n", null);

                        //net amount
                        float netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                        netAmountLabel = NET_PREFIX + SEPARATOR + netAmount;
                        sunmiPrinterService.printText(netAmountLabel + "\n", null);

                        //tax
                        tax_label = TAX_PREFIX + SEPARATOR + invoice.getTaxAmount();
                        sunmiPrinterService.printText(tax_label + "\n", null);

                    } else {
                        //set alignment to print title
                        sunmiPrinterService.setAlignment(ALIGN_RIGHT, null);

                        //ID
                        String idString = ""+invoice.getId();
                        invoiceLabel = ID_PREFIX + SEPARATOR + idString;
                        sunmiPrinterService.printText(invoiceLabel + "\n", null);

                        //Customer
                        customerName = invoice.getCustomerName() + SEPARATOR +  CUSTOMER_PREFIX;
                        sunmiPrinterService.printText(customerName + "\n", null);

                        //Date
                        dateLabel = DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp())+ SEPARATOR + DATE_PREFIX;
                        sunmiPrinterService.printText(dateLabel + "\n", null);

                        //Mop
                        String mop = ""+invoice.getGrossAmount();
                        mop_label = mop + SEPARATOR + MOP_PREFIX;
                        sunmiPrinterService.printText(mop_label + "\n", null);

                        //Grand total
                        grandTotal_label = GRAND_TOTAL_PREFIX + SEPARATOR + ""+invoice.getBillAmount();
                        sunmiPrinterService.printText(grandTotal_label + "\n", null);

                        //net amount
                        float netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();
                        String str_netamount = ""+netAmount;
                        netAmountLabel = str_netamount + SEPARATOR  + NET_PREFIX;
                        sunmiPrinterService.printText(netAmountLabel + "\n", null);

                        //tax
                        tax_label = TAX_PREFIX + SEPARATOR + invoice.getTaxAmount();
                        sunmiPrinterService.printText(tax_label + "\n", null);
                    }

                    //line separator after each report item
                    addLineSeparator(paper);
                }
            }

            sunmiPrinterService.setAlignment(ALIGN_CENTER, null);
            sunmiPrinterService.printText("******"+ "\n", null);

            sunmiPrinterService.autoOutPaper(null);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add line
     */
    private void addLineSeparator(int paperType) {
        try {

            if (paperType == 1) {
                sunmiPrinterService.printText("--------------------------------\n", null);
            } else {
                sunmiPrinterService.printText("------------------------------------------------\n", null);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * to print the customer info section
     * language support for english and arabic
     */
    private void printBillCustomerInfo(Context context, String customerName, Integer inv_id, String currency_value, int type, long timestamp) {
        try {

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
            if (type == 1) {
                str_type = context.getString(R.string.sales_invoice);
            } else {
                str_type = context.getString(R.string.purchase_label);
            }


            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                billTo = label_to + COLON_SEPARATOR + customerName;
                invoiceNo = label_inv + COLON_SEPARATOR + "INV#" + inv_id;
                billType = label_type + COLON_SEPARATOR + str_type;
                currency = label_currency + COLON_SEPARATOR + currency_value;
                billDate = label_date + COLON_SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(timestamp);
            } else {
                billTo = customerName + COLON_SEPARATOR + label_to;
                invoiceNo = "INV#" + inv_id + COLON_SEPARATOR + label_inv;
                billType = str_type + COLON_SEPARATOR + label_type;
                currency = currency_value + COLON_SEPARATOR + label_currency;
                billDate = label_date + COLON_SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(timestamp);

            }


            sunmiPrinterService.printText(billTo + "\n", null); //bill to
            sunmiPrinterService.printText(invoiceNo + "\n", null); //bill inv
            sunmiPrinterService.printText(billType + "\n", null); //bill type
            sunmiPrinterService.printText(currency + "\n", null); //bill currency
            sunmiPrinterService.printText(billDate + "\n", null); //bill date


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showToast(Context context, String msg) {
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
