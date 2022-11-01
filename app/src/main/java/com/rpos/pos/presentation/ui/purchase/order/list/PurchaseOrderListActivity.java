package com.rpos.pos.presentation.ui.purchase.order.list;

import android.content.Intent;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.PurchaseOrderEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.purchase.order.details.PurchaseOrderDetailsActivity;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderListActivity extends SharedActivity {

    private LinearLayout ll_back;
    private RecyclerView rv_purchase_orders;
    private List<PurchaseOrderEntity> purchaseOrderList;
    private PurchaseOrderListAdapter purchaseOrderListAdapter;

    private AppDatabase localDb;
    private AppExecutors appExecutors;

    //loading view
    private AppDialogs progressDialog;
    private AppDialogs appDialogs;

    @Override
    public int setUpLayout() {
        return R.layout.activity_purchase_order_list;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        ll_back = findViewById(R.id.ll_back);
        rv_purchase_orders = findViewById(R.id.rv_purchase_orders);

        localDb = getCoreApp().getLocalDb();
        appExecutors = new AppExecutors();

        progressDialog = new AppDialogs(this);
        appDialogs = new AppDialogs(this);

        purchaseOrderList = new ArrayList<>();
        purchaseOrderListAdapter = new PurchaseOrderListAdapter(purchaseOrderList, new PurchaseOrderListAdapter.PurchaseOrderListItemClick() {
            @Override
            public void onClickPurchaseOrderItem(int orderId, int supplierId, String supplierName) {
                gotoPurchaseOrderDetailsActivity(orderId, supplierId, supplierName);
            }

            @Override
            public void onRemove(int orderId) {
                //requires user confirmation to delete
                showDeleteConfirmationDialog(orderId);
            }
        });
        rv_purchase_orders.setAdapter(purchaseOrderListAdapter);



        //on back
        ll_back.setOnClickListener(view -> onBackPressed());



    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        getPurchaseOrdersFromLocalDb();
    }

    /**
     * to get order list from local db
     * */
    private void getPurchaseOrdersFromLocalDb(){
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<PurchaseOrderEntity> tempList = localDb.purchaseOrderDao().getAllOrders();
                        if(tempList == null || tempList.size() < 1){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hideProgressbar();
                                    showToast(getString(R.string.no_orders), PurchaseOrderListActivity.this);
                                }
                            });
                            return;
                        }


                        List<PurchaseOrderEntity> filteredList = new ArrayList<>();
                        PurchaseOrderEntity order;
                        for (int i=0;i<tempList.size();i++){
                            order = tempList.get(i);
                            if(order.getStatus().equals(Constants.ORDER_CREATED) || order.getStatus().equals(Constants.ORDER_PENDING)){
                                filteredList.add(order);
                            }
                        }

                        if(filteredList.size()>0){
                            purchaseOrderList.clear();
                            purchaseOrderList.addAll(filteredList);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.hideProgressbar();
                                    purchaseOrderListAdapter.notifyDataSetChanged();
                                }
                            });
                        }else {

                          runOnUiThread(() -> {
                              progressDialog.hideProgressbar();
                              showToast(getString(R.string.no_orders), PurchaseOrderListActivity.this);
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
     * to prompt user to allow permission for deleting order
     * */
    private void showDeleteConfirmationDialog(int orderId){
        try{

            appDialogs.showCommonDualActionAlertDialog(getString(R.string.alert), getString(R.string.delete_confirmation), new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    //delete order
                    deleteOrder(orderId);
                }

                @Override
                public void onClickNegetive(String id) {
                    //do nothing
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteOrder(int orderId){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        PurchaseOrderEntity order = new PurchaseOrderEntity();
                        order.setId(orderId);
                        localDb.purchaseOrderDao().deleteOrder(order);


                        for (int i=0;i<purchaseOrderList.size();i++){
                            if(purchaseOrderList.get(i).getId() == order.getId()){
                                purchaseOrderList.remove(i);
                                refreshAfterRemove(i);
                                break;
                            }
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
     * to remove item from adapter and refresh
     * */
    private void refreshAfterRemove(final int position){
        try {

            runOnUiThread(() -> purchaseOrderListAdapter.notifyItemRemoved(position));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoPurchaseOrderDetailsActivity(int order_id, int supplier_id, String supplier_name){
        Intent orderDetailsIntent = new Intent(PurchaseOrderListActivity.this, PurchaseOrderDetailsActivity.class);
        orderDetailsIntent.putExtra(Constants.ORDER_ID, order_id);
        orderDetailsIntent.putExtra(Constants.SUPPLIER_ID, supplier_id);
        orderDetailsIntent.putExtra(Constants.SUPPLIER_NAME, supplier_name);
        startActivity(orderDetailsIntent);
        finish();
    }
}