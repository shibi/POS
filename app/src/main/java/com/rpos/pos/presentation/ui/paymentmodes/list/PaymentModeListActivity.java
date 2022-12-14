package com.rpos.pos.presentation.ui.paymentmodes.list;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PaymentModeEntity;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.paymentmodes.add.AddPayModeActivity;
import com.rpos.pos.presentation.ui.paymentmodes.list.adapter.PayModeListAdapter;
import com.rpos.pos.presentation.ui.units.list.UOMListActivity;

import java.util.ArrayList;
import java.util.List;

public class PaymentModeListActivity extends SharedActivity {

    private LinearLayout ll_back;
    private LinearLayout ll_addPayModes;
    private RecyclerView rv_paymodes;
    private PayModeListAdapter payModeListAdapter;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;
    private AppDialogs appDialogs;

    private List<PaymentModeEntity> payModeList;

    @Override
    public int setUpLayout() {
        return R.layout.activity_payment_mode_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        ll_addPayModes = findViewById(R.id.ll_add_units);
        rv_paymodes = findViewById(R.id.rv_paymodes);

        progressDialog = new AppDialogs(this);
        appDialogs = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        payModeList = new ArrayList<>();

        payModeListAdapter = new PayModeListAdapter(payModeList, onPaymentModeClickListener);
        rv_paymodes.setAdapter(payModeListAdapter);

        //to goto add screen on click
        ll_addPayModes.setOnClickListener(view -> {
            try {
                gotoAddPaymentModeActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());


    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //load all pay methods at first
        //this list is used to check name duplication N.B
        loadAllPayModes();
    }

    /**
     * load all available methods
     * */
    private void loadAllPayModes(){
        try {

            appExecutors.diskIO().execute(() -> {
                List<PaymentModeEntity> paylist = localDb.paymentModeDao().getAllPaymentModeList();
                if(paylist!=null && !paylist.isEmpty()){
                    payModeList.clear();
                    payModeList.addAll(paylist);
                    runOnUiThread(() -> payModeListAdapter.notifyDataSetChanged());
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private PayModeListAdapter.OnPaymentModeClickListener onPaymentModeClickListener = paymentMode -> {
        try {

            //prompt user to let delete
            promptDeleteConfirmation(paymentMode);

        }catch (Exception e){
            e.printStackTrace();
        }
    };

    /**
     * prompt dialog to let user confirm deletion
     * @param payModeItem paymentMode item to delete
     * */
    private void promptDeleteConfirmation(PaymentModeEntity payModeItem){
        try {

            String message = getString(R.string.delete_confirmation);

            appDialogs = new AppDialogs(PaymentModeListActivity.this);
            appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), message, new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                        appExecutors.diskIO().execute(() -> {
                            try {

                                localDb.paymentModeDao().delete(payModeItem);

                                for (int i=0;i<payModeList.size();i++){
                                    if(payModeList.get(i).getId() == payModeItem.getId()){
                                        payModeList.remove(i);
                                        refreshAfterRemove(i);
                                        break;
                                    }
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        });

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClickNegetive(String id) {

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * refresh adapter position after item remove
     * @param position position of the item removed
     * */
    private void refreshAfterRemove(final int position){
        try {

            runOnUiThread(() -> payModeListAdapter.notifyItemRemoved(position));

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * goto add payment mode activity
     * */
    private void gotoAddPaymentModeActivity(){
        Intent addPaymentModeIntent = new Intent(PaymentModeListActivity.this, AddPayModeActivity.class);
        startActivity(addPaymentModeIntent);
    }

}