package com.rpos.pos.presentation.ui.purchase.list;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.ChipGroup;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceItemHistory;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.purchase.list.adapter.PurchaseInvoiceAdapter;
import com.rpos.pos.presentation.ui.purchase.order.create.CreatePurchaseActivity;
import com.rpos.pos.presentation.ui.purchase.order.list.PurchaseOrderListActivity;
import com.rpos.pos.presentation.ui.purchase.order.payment.PurchasePaymentActivity;
import com.rpos.pos.presentation.ui.supplier.lsit.SuppliersListActivity;
import java.util.ArrayList;
import java.util.List;

public class PurchaseActivity extends SharedActivity {

    private LinearLayout ll_supplier,ll_createPurchase;
    private RecyclerView rv_purchase;
    private LinearLayout ll_back;
    private LinearLayout ll_purchaseOrders;
    private TextView tv_badge;
    private View viewEmpty;
    private ChipGroup filter_chipGroup;

    private ArrayList<PurchaseInvoiceEntity> invoiceArrayList;
    private PurchaseInvoiceAdapter purchaseInvoiceAdapter;

    private AppDatabase localDb;
    private AppExecutors appExecutors;

    //loading view
    private AppDialogs progressDialog;

    @Override
    public int setUpLayout() {
        return R.layout.activity_purchase;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        progressDialog = new AppDialogs(this);

        ll_supplier = findViewById(R.id.ll_leftIcon);
        ll_createPurchase = findViewById(R.id.ll_rightIcon);
        rv_purchase = findViewById(R.id.rv_purchaseList);
        ll_back = findViewById(R.id.ll_back);
        ll_purchaseOrders = findViewById(R.id.ll_rightMenu);
        tv_badge = findViewById(R.id.tv_badge);
        viewEmpty = findViewById(R.id.view_empty);
        filter_chipGroup = findViewById(R.id.chip_group);

        localDb = getCoreApp().getLocalDb();
        appExecutors = new AppExecutors();

        invoiceArrayList = new ArrayList<>();
        purchaseInvoiceAdapter = new PurchaseInvoiceAdapter(this, invoiceArrayList, purchaseInvoiceCallBack);
        rv_purchase.setAdapter(purchaseInvoiceAdapter);

        //select filter all chip by default
        filter_chipGroup.check(R.id.chip_all);

        //filter on chip select
        filter_chipGroup.setOnCheckedStateChangeListener(invoiceFilterListener);

        //supplier click
        ll_supplier.setOnClickListener(this::onClickView);

        //new purchase order
        ll_createPurchase.setOnClickListener(this::onClickView);

        //order list
        ll_purchaseOrders.setOnClickListener(this::onClickView);

        //on back
        ll_back.setOnClickListener(this::onClickView);

    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //to get count of orders
        getOnGoingOrdersCount();

        //to get all invoices list
        getAllInvoices();

    }

    private void getAllInvoices(){
        try {

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<PurchaseInvoiceEntity> savedInvoiceList = localDb.purchaseInvoiceDao().getAllInvoices();
                        if(savedInvoiceList!=null && !savedInvoiceList.isEmpty()){
                            updateInvoiceAdapter(savedInvoiceList);
                        }else {
                            runOnUiThread(() -> {
                                progressDialog.hideProgressbar();
                                showEmpty();
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
     * To get the ongoing order count
     * */
    private void getOnGoingOrdersCount(){
        try {

            AppExecutors countExecutors = new AppExecutors();
            countExecutors.diskIO().execute(() -> {
                try {

                    int count = localDb.purchaseOrderDao().getOrdersWithStatus(Constants.ORDER_CREATED);
                    setBadgeCount(""+count);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateInvoiceAdapter(List<PurchaseInvoiceEntity> _invoicesList){
        try {

            runOnUiThread(() -> {
                try {

                    progressDialog.hideProgressbar();

                    invoiceArrayList.clear();
                    invoiceArrayList.addAll(_invoicesList);
                    purchaseInvoiceAdapter.notifyDataSetChanged();

                    hideEmpty();

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to filter invoice adapter
     *  NB: pass filterPrefix - filter identify search and sort with prefix
     *
     * */
    private void filterList(String filterPrefix,String filterString){
        try {
            String searchString = filterPrefix +":"+ filterString;
            purchaseInvoiceAdapter.getFilter().filter(searchString);
        }catch (Exception e){
            throw e;
        }
    }

    private void onClickView(View view){
        try {

            switch (view.getId())
            {
                case R.id.ll_leftIcon:
                    gotoSuppliersListScreen();
                    break;
                case R.id.ll_rightIcon:
                    gotoCreatePurchaseScreen();
                    break;
                case R.id.ll_rightMenu:
                    gotoPurchaseOrderListActivity();
                    break;
                case R.id.ll_back:
                    onBackPressed();
                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Listener call back for invoice item click
     * */
    private PurchaseInvoiceAdapter.PurchaseInvoiceListener purchaseInvoiceCallBack = new PurchaseInvoiceAdapter.PurchaseInvoiceListener() {
        @Override
        public void onClickPurchaseInvoice(int invoiceId) {
            gotoPurchasePaymentScreen(invoiceId);
        }

        @Override
        public void onCancelPurchaseInvoice(PurchaseInvoiceEntity pInvoice) {
            onClickInvoiceReturn(pInvoice);
        }
    };

    /**
     * Chip group click to filter recyclerview
     * */
    private ChipGroup.OnCheckedStateChangeListener invoiceFilterListener = (group, checkedIds) -> {
        try {

            if(!checkedIds.isEmpty()){
                int selectedChipId = checkedIds.get(0);
                switch (selectedChipId) {

                    case R.id.chip_all:
                        filterList(Constants.FILTER_SORT, ""+Constants.FILTER_ALL);
                        break;
                    case R.id.chip_paid:
                        filterList(Constants.FILTER_SORT, ""+Constants.FILTER_PAID);
                        break;
                    case R.id.chip_unpaid:
                        filterList(Constants.FILTER_SORT, ""+Constants.FILTER_UNPAID);
                        break;
                    case R.id.chip_overdue:
                        filterList(Constants.FILTER_SORT, ""+Constants.FILTER_OVERDUE);
                        break;
                    case R.id.chip_return:
                        filterList(Constants.FILTER_SORT, ""+Constants.FILTER_RETURN);
                        break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    };

    /**
     * on click cancel ( return )
     *  show confirmation for user and mark invoice as return when user allows
     * */
    private void onClickInvoiceReturn(PurchaseInvoiceEntity invoice){
        try {
            if(invoice.getStatus().equals(Constants.PAYMENT_RETURN)){
                showToast(getString(R.string.cancelled),PurchaseActivity.this);
                return;
            }

            //inform user for confirmation with dialog box
            AppDialogs appDialogs = new AppDialogs(PurchaseActivity.this);
            String title = getString(R.string.return_label);
            String message = getString(R.string.mark_as_return);
            appDialogs.showCommonDualActionAlertDialog(title, message, new AppDialogs.OnDualActionButtonClickListener() {
                @Override
                public void onClickPositive(String id) {
                    markInvoiceAsReturn(invoice);
                }

                @Override
                public void onClickNegetive(String id) {
                    //do nothing
                }
            });

        }catch (Exception e){
            throw e;
        }
    }

    /**
     * to change and save invoice status to RETURN.
     * */
    private void markInvoiceAsReturn(PurchaseInvoiceEntity invoice){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    //BEFORE RETURNING ITEMS , CHECK WHETHER ITEMS ARE AVAILABLE TO RETURN

                    //first get the items ids and quantity from invoice items table
                    List<PurchaseInvoiceItemHistory> invoiceItemsList = localDb.purchaseInvoiceDao().getInvoiceItemsWithId(invoice.getId());
                    //check whether items are not null or empty
                    if(invoiceItemsList!=null && invoiceItemsList.size()>0){
                        //To fetch ALL original items and available quantity at once , gather the item id's of all items from invoice items list
                        //and put the ids in an list
                        List<Integer> itemIdsList = new ArrayList<>();
                        for (PurchaseInvoiceItemHistory invoiceItem: invoiceItemsList){
                            itemIdsList.add(invoiceItem.getItemId());
                        }

                        //Use the item ids list to fetch the original items from database with query
                        List<ItemEntity> selectedItems = localDb.itemDao().getSelectedItems(itemIdsList);
                        //check the item list not null or empty
                        if(selectedItems !=null && selectedItems.size()>0){
                            //flag to check item stock available
                            boolean isQuantityInsufficient = false;
                            //loop through and find the original item has enough stock to deduct after returned
                            for(PurchaseInvoiceItemHistory invoiceItem:invoiceItemsList){
                                for (ItemEntity item:selectedItems){
                                    if(invoiceItem.getItemId() == item.getItemId()){
                                        if(item.getAvailableQty() < invoiceItem.getQuantity()){
                                            isQuantityInsufficient = true;
                                            break;
                                        }
                                    }
                                }
                            }

                            //if any items have insufficient stock to deduct
                            //show alert message
                            if(isQuantityInsufficient){
                                //CANNOT RETURN ITEMS, SOME ITEMS ARE ALREADY SOLD OUT
                                showSomeItemsAlreadySold();
                            }else {
                                //items available for return
                                invoice.setStatus(Constants.PAYMENT_RETURN);
                                localDb.purchaseInvoiceDao().insertInvoice(invoice);

                                int balanceStock;
                                //loop through and find the original item has enough stock to deduct after returned
                                for(PurchaseInvoiceItemHistory invoiceItem:invoiceItemsList){
                                    for (ItemEntity item:selectedItems){
                                        if(invoiceItem.getItemId() == item.getItemId()){
                                            balanceStock = item.getAvailableQty() - invoiceItem.getQuantity();
                                            item.setAvailableQty(balanceStock);
                                        }
                                    }
                                }
                                //update items with new quantity
                                localDb.itemDao().updateList(selectedItems);

                                //refresh the adapter to reflect change
                                refreshInvoiceInAdapter(invoice.getId());
                            }

                        }else {
                            //TODO need to work out what to do when items are deleted
                        }


                    }else {

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To find the position of invoice in array list and refresh adapter in that position
     * So that the cancelled is reflected in recyclerview list item
     * */
    private void refreshInvoiceInAdapter(int invoice_id){
        try {

            runOnUiThread(() -> {
                try {

                    int refreshPosition = 0;
                    boolean isRefresh = false;

                    //find the invoice position in array list with invoice id
                    for (int i=0;i<invoiceArrayList.size();i++){
                        if(invoiceArrayList.get(i).getId() == invoice_id){
                            isRefresh = true;
                            refreshPosition = i;
                            break;
                        }
                    }

                    //refresh only if needed.
                    if(isRefresh) {
                        purchaseInvoiceAdapter.notifyItemChanged(refreshPosition);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
           throw e;
        }
    }

    /**
     * some items already sold. Cannot return
     * */
    private void showSomeItemsAlreadySold(){
        try {

            runOnUiThread(() -> {
                //alert dialog box
                //items already sold
                AppDialogs appDialogs = new AppDialogs(PurchaseActivity.this);
                appDialogs.showCommonAlertDialog(getString(R.string.some_items_sold), null);
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * To set badge count and visibility
     * */
    private void setBadgeCount(String badgeCount){
        try {

            runOnUiThread(() -> {

                tv_badge.setText(badgeCount);
                if(badgeCount.isEmpty() || badgeCount.equals("0")){
                    tv_badge.setVisibility(View.INVISIBLE);
                }else {
                    tv_badge.setVisibility(View.VISIBLE);
                }

            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showEmpty(){
        viewEmpty.setVisibility(View.VISIBLE);
    }
    private void hideEmpty(){
        viewEmpty.setVisibility(View.GONE);
    }

    private void gotoPurchasePaymentScreen(int invoice_id){
        Intent intent = new Intent(PurchaseActivity.this, PurchasePaymentActivity.class);
        intent.putExtra(Constants.INVOICE_ID, invoice_id);
        startActivity(intent);
    }

    private void gotoSuppliersListScreen(){
        Intent supplierIntent = new Intent(PurchaseActivity.this, SuppliersListActivity.class);
        startActivity(supplierIntent);
    }

    private void gotoCreatePurchaseScreen(){
        Intent createPurchase = new Intent(PurchaseActivity.this, CreatePurchaseActivity.class);
        startActivity(createPurchase);
    }

    private void gotoPurchaseOrderListActivity(){
        Intent purchaseOrdersIntent = new Intent(PurchaseActivity.this, PurchaseOrderListActivity.class);
        startActivity(purchaseOrdersIntent);
    }

}