package com.rpos.pos.domain.utils.sunmi_printer_utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.CompanyAddressEntity;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import com.rpos.pos.domain.utils.DateTimeUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 *  Simple package for connecting a sunmi printer via Bluetooth
 */
public class BluetoothUtil {

    private static String[] mStrings = new String[]{"CP437", "CP850", "CP860", "CP863", "CP865", "CP857", "CP737", "CP928", "Windows-1252", "CP866", "CP852", "CP858", "CP874", "Windows-775", "CP855", "CP862", "CP864", "GB18030", "BIG5", "KSC5601", "utf-8"};

    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String Innerprinter_Address = "00:11:22:33:44:55";

    public static boolean isBlueToothPrinter = false;

    private static BluetoothSocket bluetoothSocket;

    private static BluetoothAdapter getBTAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    private static BluetoothDevice getDevice(BluetoothAdapter bluetoothAdapter) {
        BluetoothDevice innerprinter_device = null;
        Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            if (device.getAddress().equals(Innerprinter_Address)) {
                innerprinter_device = device;
                break;
            }
        }
        return innerprinter_device;
    }

    private static BluetoothSocket getSocket(BluetoothDevice device) throws IOException {
        BluetoothSocket socket;
        socket = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
        socket.connect();
        return  socket;
    }

    private static int record = 17;
    /**
     * connect bluetooth
     */
    public static boolean connectBlueTooth(Context context) {
        if (bluetoothSocket == null) {
            if (getBTAdapter() == null) {
                Toast.makeText(context,  R.string.toast_3, Toast.LENGTH_SHORT).show();
                return false;
            }
            if (!getBTAdapter().isEnabled()) {
                Toast.makeText(context, R.string.toast_4, Toast.LENGTH_SHORT).show();
                return false;
            }
            BluetoothDevice device;
            if ((device = getDevice(getBTAdapter())) == null) {
                Toast.makeText(context, R.string.toast_5, Toast.LENGTH_SHORT).show();
                return false;
            }

            try {
                bluetoothSocket = getSocket(device);
            } catch (IOException e) {
                Toast.makeText(context, R.string.toast_6, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    /**
     * disconnect bluethooth
     */
    public static void disconnectBlueTooth(Context context) {
        if (bluetoothSocket != null) {
            try {
                OutputStream out = bluetoothSocket.getOutputStream();
                out.close();
                bluetoothSocket.close();
                bluetoothSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  send esc cmd
     */
    public static void sendData(byte[] bytes) {
        if (bluetoothSocket != null) {
            OutputStream out = null;
            try {
                out = bluetoothSocket.getOutputStream();
                out.write(bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //TODO handle disconnect event
        }
    }

    private static byte codeParse(int value) {
        byte res = 0x00;
        switch (value) {
            case 0:
                res = 0x00;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                res = (byte) (value + 1);
                break;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
                res = (byte) (value + 8);
                break;
            case 12:
                res = 21;
                break;
            case 13:
                res = 33;
                break;
            case 14:
                res = 34;
                break;
            case 15:
                res = 36;
                break;
            case 16:
                res = 37;
                break;
            case 17:
            case 18:
            case 19:
                res = (byte) (value - 17);
                break;
            case 20:
                res = (byte) 0xff;
                break;
            default:
                break;
        }
        return (byte) res;
    }

    public static void printSalesInvoice(Context context, InvoiceEntity currentInvoice, CompanyAddressEntity companyDetails, List<InvoiceItemHistory> printItemsList, String printerQrData){
        try{

            String companyName, address, phone, email;
            String ph_label = context.getResources().getString(R.string.phone_label);
            String email_label = context.getResources().getString(R.string.email);
            if (companyDetails != null) {

                if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                    companyName = companyDetails.getCompanyNameEng();
                    phone = ph_label + ": " + companyDetails.getMobile();
                    email = email_label + ": " + companyDetails.getEmail();
                } else {
                    companyName = companyDetails.getCompanyNameAr();
                    phone = companyDetails.getMobile() + ": "+ ph_label;
                    email = companyDetails.getEmail()+": "+ email_label;
                }
                address = companyDetails.getAddress();

            } else {
                companyName = "TEST";
                address = "Enter address";
                phone = "Ph: +91 9876543210";
                email = "email: email@gmail.com";
            }

            BluetoothUtil.sendData(ESCUtil.alignCenter());
            BluetoothUtil.sendData(ESCUtil.boldOn());
            if (record < 17) {
                BluetoothUtil.sendData(ESCUtil.singleByte());
                BluetoothUtil.sendData(ESCUtil.setCodeSystemSingle(codeParse(record)));
            } else {
                BluetoothUtil.sendData(ESCUtil.singleByteOff());
                BluetoothUtil.sendData(ESCUtil.setCodeSystem(codeParse(record)));
            }

            printTextWithLineBreak(companyName, 1);

            BluetoothUtil.sendData(ESCUtil.boldOff());

            printTextWithLineBreak(address, 1);

            printTextWithLineBreak(phone, 1);

            printTextWithLineBreak(email, 1);

            printLine(1);

            //Setting alignment according to language
            byte[] ALIGNMENT = (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) ? ESCUtil.alignLeft() : ESCUtil.alignRight();
            BluetoothUtil.sendData(ALIGNMENT);

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

            String str_type = context.getString(R.string.sales_invoice);

            if (CoreApp.DEFAULT_LANG.equals(Constants.LANG_EN)) {
                billTo = label_to + COLON_SEPARATOR + currentInvoice.getCustomerName();
                invoiceNo = label_inv + COLON_SEPARATOR + "INV#" + currentInvoice.getId();
                billType = label_type + COLON_SEPARATOR + str_type;
                currency = label_currency + COLON_SEPARATOR + currentInvoice.getCurrency();
                billDate = label_date + COLON_SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(currentInvoice.getTimestamp());
            } else {
                billTo = currentInvoice.getCustomerName() + COLON_SEPARATOR + label_to;
                invoiceNo = "INV#" + currentInvoice.getId() + COLON_SEPARATOR + label_inv;
                billType = str_type + COLON_SEPARATOR + label_type;
                currency = label_currency + COLON_SEPARATOR + currentInvoice.getCurrency();
                billDate = label_date + COLON_SEPARATOR + DateTimeUtils.convertTimerStampToDateTime(currentInvoice.getTimestamp());
            }

            printTextWithLineBreak(billTo, 1);
            printTextWithLineBreak(invoiceNo, 1);
            printTextWithLineBreak(billType, 1);
            printTextWithLineBreak(currency, 1);
            printTextWithLineBreak(billDate, 1);
            printLine(1);

            String itemName;
            String itemQty;
            for (int i=0;i< printItemsList.size();i++){
                BluetoothUtil.sendData(ESCUtil.alignLeft());
                itemName = printItemsList.get(i).getItemName();
                printTextWithLineBreak(itemName, 1);
                itemQty = ""+printItemsList.get(i).getQuantity()+ " X "+printItemsList.get(i).getItemRate() + "        "+printItemsList.get(i).getTotal();
                printTextWithLineBreak(itemQty, 1);
            }

            printLine(3);



        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * to print the text content and goto next line start
     * @param content printable text content
     * @param lineNum number of lines needed to skip after text content. ( 3 is better)
     * */
    private static void printTextWithLineBreak(String content, int lineNum){
        try {

            BluetoothUtil.sendData(content.getBytes(mStrings[record]));
            BluetoothUtil.sendData(ESCUtil.nextLine(lineNum));

        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * To print a line
     * */
    private static void printLine(int lineNum) throws Exception{
        try {

            String line = "-------------------------------";
            BluetoothUtil.sendData(line.getBytes(mStrings[record]));
            BluetoothUtil.sendData(ESCUtil.nextLine(lineNum));

        }catch (Exception e){
            throw e;
        }
    }

}
