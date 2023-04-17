package com.rpos.pos.presentation.ui.common;

import android.os.Handler;
import android.util.Log;

import com.rpos.pos.domain.utils.sunmi_printer_utils.BluetoothUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.ESCUtil;
import com.rpos.pos.domain.utils.sunmi_printer_utils.SunmiPrintHelper;

public abstract class PrintableContentActivity extends SharedActivity{

    public Handler handler;

    @Override
    public void initViews() {
        Log.e("-----------","init it");

        handler = new Handler();
        initPrinterStyle();
    }

    /**
     *  Initialize the printer
     *  All style settings will be restored to default
     */
    private void initPrinterStyle() {
        if(BluetoothUtil.isBlueToothPrinter){
            BluetoothUtil.sendData(ESCUtil.init_printer());
        }else{
            SunmiPrintHelper.getInstance().initPrinter();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }
}
