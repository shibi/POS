package com.rpos.pos.presentation.ui.sales.order.list;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.OrderDetailsEntity;
import com.rpos.pos.data.local.entity.OrderEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.sales.order.details.OrderDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderQueueActivity extends SharedActivity implements OrdersAdapter.OrderClickListener{

    private RecyclerView rv_orders;
    private OrdersAdapter ordersAdapter;
    private List<OrderEntity> ordersList;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private LinearLayout ll_back;
    private View viewEmpty;

    @Override
    public int setUpLayout() {
        return R.layout.activity_order_queue;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        localDb = getCoreApp().getLocalDb();
        appExecutors = new AppExecutors();
        ll_back = findViewById(R.id.ll_back);
        viewEmpty = findViewById(R.id.view_empty);

        ordersList = new ArrayList<>();

        rv_orders = findViewById(R.id.rv_orders);
        ordersAdapter = new OrdersAdapter(ordersList, this);
        rv_orders.setAdapter(ordersAdapter);

        ll_back.setOnClickListener(view -> onBackPressed());



    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            //get orders
            getOrdersFromLocalDb();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * get locally saved orders
     * */
    private void getOrdersFromLocalDb(){
        try{

            appExecutors.diskIO().execute(() -> {
                try{

                    List<OrderEntity> orderEntityList = localDb.ordersDao().getAllOrders();
                    if(orderEntityList == null || orderEntityList.isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showEmptyView();
                                showToast(getString(R.string.no_orders),OrderQueueActivity.this);
                            }
                        });
                        return;
                    }


                    List<OrderEntity> filteredList = new ArrayList<>();
                    OrderEntity order;
                    for (int i=0;i<orderEntityList.size();i++){
                        order = orderEntityList.get(i);
                        if(order.getStatus().equals(Constants.ORDER_CREATED) || order.getStatus().equals(Constants.ORDER_PENDING)){
                            filteredList.add(order);
                        }
                    }


                    populateOrders(filteredList);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void populateOrders(List<OrderEntity> _orderEntityList) throws Exception{
        try{

            runOnUiThread(() -> {
                hideEmptyView();
                ordersList.clear();
                ordersList.addAll(_orderEntityList);
                ordersAdapter.notifyDataSetChanged();
            });

        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void onOrderClick(OrderEntity order) {
        try{

            Intent orderDetailsIntent = new Intent(OrderQueueActivity.this, OrderDetailsActivity.class);
            orderDetailsIntent.putExtra(Constants.ORDER_ID, order.getId());
            orderDetailsIntent.putExtra(Constants.CUSTOMER_NAME, order.getCustomerName());
            orderDetailsIntent.putExtra(Constants.CUSTOMER_ID, order.getCustomerId());
            startActivity(orderDetailsIntent);
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoveClick(OrderEntity order) {
        try{

            String message = getString(R.string.delete_confirmation);

            AppDialogs appDialogs = new AppDialogs(OrderQueueActivity.this);
            appDialogs.showCommonDualActionAlertDialog(getString(R.string.delete_label), message, new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    try {

                        appExecutors.diskIO().execute(() -> {
                            try {
                                //Mandatory to remove items before deleting order
                                //check whether any items exists
                                List<OrderDetailsEntity> orderedItemsList = localDb.orderDetailsDao().getAllItemsInOrder(order.getId());
                                if(orderedItemsList!=null && !orderedItemsList.isEmpty()){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            appDialogs.showCommonAlertDialog(getString(R.string.there_are_items_in_order), null);
                                        }
                                    });
                                    return;
                                }

                                localDb.ordersDao().deleteOrder(order);

                                for (int i=0;i<ordersList.size();i++){
                                    if(ordersList.get(i).getId() == order.getId()){
                                        ordersList.remove(i);
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


    private void refreshAfterRemove(final int position){
        try {

            runOnUiThread(() -> ordersAdapter.notifyItemRemoved(position));

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showEmptyView(){
        viewEmpty.setVisibility(View.VISIBLE);
    }
    private void hideEmptyView(){
        viewEmpty.setVisibility(View.GONE);
    }
}