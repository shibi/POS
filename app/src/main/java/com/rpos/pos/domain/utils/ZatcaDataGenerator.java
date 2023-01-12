package com.rpos.pos.domain.utils;

import com.chilkatsoft.CkBinData;
import com.rpos.pos.Constants;

public class ZatcaDataGenerator {


    public static String getZatca(String _sellerName, String _vatNumber, String _timeStamp, String _invoiceTotal, String _vatTotal){
        try{

            // This example requires the Chilkat API to have been previously unlocked.
            // See Global Unlock Sample for sample code.

            // In Step 1, we applied a signature to an e-invoice Zakat, Tax and Customs Authority (ZATCA) of Saudi Arabia
            // This example is Step 2, where we compose the TLV encoding of the QR code, apply the ECDSA signature, and then insert into the signed XML without disturbing the signed XML.

            boolean success;
            // Construct TLV (Tag-Length-Value) encoding of the QR data.
            // We have 5 pieces of data: Seller Name, VAT Number, Time Stamp, Invoice Total, and VAT Total.
            // For example:
            /*String sellerName = "Firoz Ashraf";
            String vatNumber = "312345678912563";
            String timeStamp = "2021-11-17";
            String invoiceTotal = "100.00";
            String vatTotal = "15.00";*/

            String sellerName = _sellerName;
            String vatNumber = _vatNumber;
            String timeStamp = _timeStamp;
            String invoiceTotal = _invoiceTotal;
            String vatTotal = _vatTotal;


            // TLV encode into a Chilkat BinData object.
            CkBinData bdTlv = new CkBinData();

            String charset = "utf-8";

            int tag = 1;
            bdTlv.AppendByte(tag);
            bdTlv.AppendCountedString(1,false,sellerName,charset);
            tag = tag+1;
            // This is tag 2
            bdTlv.AppendByte(tag);
            bdTlv.AppendCountedString(1,false,vatNumber,charset);
            tag = tag+1;
            // This is tag 3
            bdTlv.AppendByte(tag);
            bdTlv.AppendCountedString(1,false,timeStamp,charset);
            tag = tag+1;
            // This is tag 4
            bdTlv.AppendByte(tag);
            bdTlv.AppendCountedString(1,false,invoiceTotal,charset);
            tag = tag+1;
            // This is tag 5
            bdTlv.AppendByte(tag);
            bdTlv.AppendCountedString(1,false,vatTotal,charset);


            String qr_base64 = bdTlv.getEncoded("base64");
            System.out.println("ssssQR: " + qr_base64);
            return qr_base64;
        }catch (Exception e){
            e.printStackTrace();
            return Constants.EMPTY;
        }
    }

}
