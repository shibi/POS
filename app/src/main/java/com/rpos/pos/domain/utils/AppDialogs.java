package com.rpos.pos.domain.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.rpos.pos.Constants;
import com.rpos.pos.R;


public class AppDialogs {

    private Context mContext;

    private boolean isExpanded = false;

    private Dialog progressDialog;

    /**
     * to prevent dialogbox showing multiple times
     * */
    private boolean isDialogBoxShowing = false;


    private int quantity_count;

    /**
     * constructor
     * */
    public AppDialogs(Context _context){
        mContext = _context;
    }

    /**
     * to show progress bar
     * */
    public void showProgressBar(){
        try{

            //check whether dialog box showing
            if(isDialogBoxShowing)
            {
                showToast("Dialog already showing");
                return;
            }

            if(progressDialog == null) {

                //set the flag true
                isDialogBoxShowing = true;

                //init the dialog for the alert
                progressDialog = createSimpleDialog(mContext, R.layout.diolog_progressbar, false);

                //avoid closing dialog on clicking outside
                progressDialog.setCancelable(false);

                //get the dialog window
                Window window = progressDialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                //set the layout params for the dialog window
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

            }else {
                progressDialog.show();
                Log.e("------------","progess showing");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * to show progress bar
     * */
    public void showProgressBar(String msg){
        try{

            //check whether dialog box showing
            if(isDialogBoxShowing)
            {
                showToast("Dialog already showing");
                return;
            }

            if(progressDialog == null) {

                //set the flag true
                isDialogBoxShowing = true;

                //init the dialog for the alert
                progressDialog = createSimpleDialog(mContext, R.layout.diolog_progressbar, false);

                AppCompatTextView tv_progressMessage = progressDialog.findViewById(R.id.tv_progressMessage);

                //avoid closing dialog on clicking outside
                progressDialog.setCancelable(false);

                tv_progressMessage.setText(msg);

                //get the dialog window
                Window window = progressDialog.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                //set the layout params for the dialog window
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

            }else {
                progressDialog.show();
                Log.e("------------","progess showing");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show progress bar
     * */
    public void hideProgressbar(){
        try{

            //set the flag false
            isDialogBoxShowing = false;

            if(progressDialog!=null){
                progressDialog.dismiss();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void showExitAlertDialog(OnDualActionButtonClickListener listener){

        createDualActionAlertDialogBox("Exit",
                "Are you sure want to exit?",
                R.drawable.ic_baseline_warning_24,
                false,
                "Exit","Cancel",listener);
    }


    public void showCommonSuccessDialog(String msg, View.OnClickListener listener){
        createSingleActionDialog(mContext.getResources().getString(R.string.success),msg,false,"Ok",listener);
    }

    public void showCommonAlertDialog(String msg, View.OnClickListener listener){
        createSingleActionDialog(mContext.getResources().getString(R.string.alert),msg,false,"Ok",listener);
    }

    public void showCommonSingleAlertDialog(String title,String msg, View.OnClickListener listener){
        createSingleActionDialog(title,msg,false,mContext.getResources().getString(R.string.btn_ok),listener);
    }

    public void showCommonDualActionAlertDialog(String title,String msg, OnDualActionButtonClickListener listener){

        String str_title = (title!=null && !title.isEmpty())?title:mContext.getResources().getString(R.string.alert);
        createDualActionAlertDialogBox(str_title, msg, R.drawable.ic_baseline_warning_24, true, "Yes", "Cancel", listener);

    }


    /**
     * to select quantity for items
     * */
    public void showOrderItemPicker(Float stock, float unitprice, String uom,boolean isMaintainStock,int requestedParent, ItemPickerListener listener){
        try{

            //check whether dialog box showing
            if(isDialogBoxShowing) {
                showToast("Dialog already showing");
                return;
            }

            //set the flag true
            isDialogBoxShowing = true;

            //init the dialog for the alert
            final Dialog dialog = createSimpleDialog(mContext, R.layout.dialog_order_item_pick, false);

            final AppCompatButton btn_add = dialog.findViewById(R.id.btn_add);
            final ImageView iv_plus = dialog.findViewById(R.id.iv_plus);
            final ImageView iv_minus = dialog.findViewById(R.id.iv_minus);
            final AppCompatTextView tv_quantity = dialog.findViewById(R.id.tv_qty_count);
            final AppCompatTextView tv_unitPrice = dialog.findViewById(R.id.tv_unit_price);
            final AppCompatTextView tv_stock = dialog.findViewById(R.id.tv_stock);
            final AppCompatTextView tv_uom = dialog.findViewById(R.id.tv_uom);

            //avoid closing dialog on clicking outside
            dialog.setCancelable(true);

            //get the dialog window
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            //set the layout params for the dialog window
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            quantity_count = 1;
            String str_stock = ""+((isMaintainStock)?stock:"service");

            tv_quantity.setText(""+quantity_count);
            tv_stock.setText(str_stock);
            tv_unitPrice.setText(""+unitprice);
            tv_uom.setText(uom);


            iv_plus.setOnClickListener(view -> {
                try{
                    if(requestedParent == Constants.PARENT_SALES){
                        //Sales
                        if(isMaintainStock){
                            //check stock available
                            if(quantity_count < stock){
                                //if available, increment quantity by one
                                quantity_count++;
                                tv_quantity.setText(""+quantity_count);
                            }else {
                                showToast(mContext.getResources().getString(R.string.exceed_stock));
                            }
                        }else {
                            //Non- stock item,
                            //just increment quantity by one
                            quantity_count++;
                            tv_quantity.setText(""+quantity_count);
                        }
                    }else {
                        //Purchase
                        //if available, increment quantity by one
                        quantity_count++;
                        tv_quantity.setText(""+quantity_count);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            iv_minus.setOnClickListener(view -> {
                try{
                    //increment
                    if(quantity_count> 0){
                        quantity_count--;
                        tv_quantity.setText(""+quantity_count);
                    }else {
                        showToast(mContext.getResources().getString(R.string.zero_quantity));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            //add
            btn_add.setOnClickListener(view -> {
                try{
                    if(quantity_count<1){
                        showToast(mContext.getResources().getString(R.string.select_quantity));
                        return;
                    }

                    //DO NOT REMOVE // ELSE CAUSE ERROR
                    dialog.dismiss();

                    if(listener!=null){
                        listener.onItemAddedToCart(quantity_count);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void createSingleActionDialog(String title, String message, boolean cancellable, String btnName, View.OnClickListener listener){

        try{

            //check whether dialog box showing
            if(isDialogBoxShowing)
            {
                showToast("Dialog already showing");
                return;
            }

            //set the flag true
            isDialogBoxShowing = true;

            //init the dialog for the alert
            final Dialog dialog = createSimpleDialog(mContext, R.layout.dialog_single_alert, false);

            //avoid closing dialog on clicking outside
            dialog.setCancelable(cancellable);

            //init the submit  button
            final AppCompatButton btn_yes = dialog.findViewById(R.id.btn_yes);
            final AppCompatTextView txt_message = dialog.findViewById(R.id.tv_message);
            final AppCompatTextView tv_title = dialog.findViewById(R.id.tv_title);

            tv_title.setText(title);
            txt_message.setText(message);
            btn_yes.setText(btnName);

            //get the dialog window
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            //set the layout params for the dialog window
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            //add click listener on YES button
            btn_yes.setOnClickListener(v -> {
                try {

                    isDialogBoxShowing = false;
                    dialog.dismiss();
                    if(listener!=null) {
                        listener.onClick(btn_yes);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * to create dual action dialog box
     * @param title title dialog box
     * @param message
     * @param alert_icon
     * @param cancellable is cancellable
     * @param yesBtnName positive button name
     * @param noBtnName  negetive button name
     * @param listener action listener
     * */
    private void createDualActionAlertDialogBox(String title,String message, int alert_icon,boolean cancellable,String yesBtnName,String noBtnName,OnDualActionButtonClickListener listener){
        try{

            //check whether dialog box showing
            if(isDialogBoxShowing)
            {
                showToast("Dialog already showing");
                return;
            }

            //set the flag true
            isDialogBoxShowing = true;

            //init the dialog for the alert
            final Dialog dialog = createSimpleDialog(mContext, R.layout.dialog_alert, false);

            //avoid closing dialog on clicking outside
            dialog.setCancelable(cancellable);

            //init the submit  button
            final AppCompatButton btn_yes = dialog.findViewById(R.id.btn_yes);
            final AppCompatButton btn_no = dialog.findViewById(R.id.btn_no);
            final AppCompatTextView txt_message = dialog.findViewById(R.id.tv_message);
            final AppCompatTextView tv_title = dialog.findViewById(R.id.tv_title);
            final ImageView iv_alert_icon = dialog.findViewById(R.id.iv_icon);

            tv_title.setText(title);
            txt_message.setText(message);
            btn_yes.setText(yesBtnName);
            btn_no.setText(noBtnName);
            iv_alert_icon.setImageResource(alert_icon);

            //get the dialog window
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            //set the layout params for the dialog window
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            //add click listener on YES button
            btn_yes.setOnClickListener(v -> {
                try {

                    isDialogBoxShowing = false;
                    dialog.dismiss();
                    listener.onClickPositive(mContext.getResources().getString(R.string.btn_yes));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            //add click listener on NO button
            btn_no.setOnClickListener(view -> {
                try{

                    isDialogBoxShowing = false;
                    dialog.dismiss();
                    listener.onClickNegetive(mContext.getResources().getString(R.string.btn_no));

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * show toast message
     * */
    private void showToast(String message){
        try {

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

        }catch (Exception e){ }
    }

    /**
     * simple dialog stub
     * */
    private static Dialog createSimpleDialog(Context context, int layout, boolean alignTop) {

        try {
            final Dialog dialog = new Dialog(context);

            /*----- Aligning the dialog in top -----*/
            if (alignTop) {
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.y = 25;
                wlp.gravity = Gravity.TOP;
                window.setAttributes(wlp);
            }

            dialog.setContentView(layout);
            dialog.show();

            return dialog;

        } catch (Exception ex) {

            throw ex;
        }

    }

    public interface OnDualActionButtonClickListener{
        void onClickPositive(String id);
        void onClickNegetive(String id);
    }

    public interface ItemPickerListener{
        void onItemAddedToCart(int quantity);
    }
}
