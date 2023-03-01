package com.rpos.pos.presentation.ui.sales.sales_list;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.ChipGroup;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.ShiftRegEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.sales.list.SalesListData;
import com.rpos.pos.data.remote.dto.sales.list.SalesListMessage;
import com.rpos.pos.data.remote.dto.sales.list.SalesListResponse;
import com.rpos.pos.domain.requestmodel.RequestWithUserId;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.category.list.CategoryListActivity;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.sales.order.list.OrderQueueActivity;
import com.rpos.pos.presentation.ui.sales.payment.PaymentActivity;
import com.rpos.pos.presentation.ui.sales.adapter.InvoiceAdapter;
import com.rpos.pos.presentation.ui.sales.order.create.CreateOrderActivity;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesActivity extends SharedActivity {

    private RecyclerView rv_invoices;
    private ArrayList<SalesListData> invoiceArrayList;
    private InvoiceAdapter invoiceAdapter;
    private LinearLayout ll_back;
    private LinearLayout ll_category;
    private LinearLayout ll_queue;
    private TextView tv_badge;
    private FrameLayout fl_empty;
    private SearchView sv_invoiceSearch;
    private LinearLayout ll_add_sales;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private ChipGroup filter_chipGroup;

    @Override
    public int setUpLayout() {
        return R.layout.activity_sales;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rv_invoices = findViewById(R.id.rv_bills);
        ll_back = findViewById(R.id.ll_back);
        ll_category = findViewById(R.id.ll_leftIcon);
        ll_queue = findViewById(R.id.ll_rightMenu);
        tv_badge = findViewById(R.id.tv_badge);
        fl_empty = findViewById(R.id.fl_emptyView);
        sv_invoiceSearch = findViewById(R.id.sv_invoiceSearch);

        //Add sales icon
        ll_add_sales = findViewById(R.id.ll_rightIcon);
        ImageView iv_addIcon = findViewById(R.id.iv_right_icon);
        iv_addIcon.setImageResource(R.drawable.ic_plus);

        filter_chipGroup = findViewById(R.id.chip_group);

        //Empty array
        invoiceArrayList = new ArrayList<>();
        invoiceAdapter = new InvoiceAdapter(SalesActivity.this, invoiceArrayList, new InvoiceAdapter.OnInvoiceClickListener() {
            @Override
            public void onInvoiceClick(SalesListData invoice) {
                onSelectedInvoice(invoice);
            }

            @Override
            public void onInvoiceCancel(SalesListData invoice) {
                //onClickInvoiceReturn(invoice);
            }
        });
        rv_invoices.setAdapter(invoiceAdapter);


        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        ll_category.setOnClickListener(view -> {
            try {

                gotoCategoryScreen();

            }catch (Exception e){
                e.printStackTrace();
            }
        });


        ll_queue.setOnClickListener(view -> {
            try{

                gotoOrderQueueScreen();

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //select filter all chip by default
        filter_chipGroup.check(R.id.chip_all);

        //filter on chip select
        filter_chipGroup.setOnCheckedStateChangeListener(invoiceFilterListener);

        filter_chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
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
        });

        //Back arrow press
        ll_back.setOnClickListener(view -> onBackPressed());

        //search invoice
        sv_invoiceSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //
                filterList(Constants.FILTER_SEARCH, newText);
                return false;
            }
        });

        //add sales
        ll_add_sales.setOnClickListener(view -> {
            try {
                //forcefully avoiding shift open check to test order creation
                if(1*1 == 1){
                    gotoAddOrderActivity();
                    return;
                }

                if(checkShiftOpen()){
                    gotoAddOrderActivity();
                }else {
                    AppDialogs appDialogs = new AppDialogs(SalesActivity.this);
                    appDialogs.showCommonAlertDialog(getString(R.string.shift_not_open_inform), null);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        getAllInvoiceFromWebservice();

    }


    /**
     * to check whether shift is opened
     * step 1 : get running shift from application class(N.B -running shift data is loaded to application class from dashboardActivity on startup)
     * step 2 : check shift status is OPEN
     * */
    private boolean checkShiftOpen(){
        try {
            //get running shift
            ShiftRegEntity runningShift = getCoreApp().getRunningShift();
            if(runningShift!=null){
                //return status
               return (runningShift.getStatus().equals(Constants.SHIFT_OPEN));
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * to filter invoice adapter
     *  NB: pass filterPrefix - filter identify search and sort with prefix
     * */
    private void filterList(String filterPrefix,String filterString){
        try {
            String searchString = filterPrefix +":"+ filterString;
            invoiceAdapter.getFilter().filter(searchString);
        }catch (Exception e){
            throw e;
        }
    }


    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        //refresh onGoing order count
        //getOnGoingOrdersCount();

    }

    /**
     * to get all invoices list
     * */
    private void getAllInvoices(){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<InvoiceEntity> savedInvoiceList = localDb.invoiceDao().getAllInvoices();
                        if(savedInvoiceList!=null && !savedInvoiceList.isEmpty()){
                            showEmptyView(false);
                            updateInvoiceAdapter(savedInvoiceList);
                        }else {
                            showEmptyView(true);
                            showToast(getString(R.string.no_orders));
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
     * to get all invoice list from web service
     * */
    private void getAllInvoiceFromWebservice(){
        try {

            String userId = SharedPrefHelper.getInstance(this).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid));
                return;
            }

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            RequestWithUserId request = new RequestWithUserId();
            request.setUserId(userId);
            Call<SalesListResponse> call = api.getAllInvoicesList(request);
            call.enqueue(new Callback<SalesListResponse>() {
                @Override
                public void onResponse(Call<SalesListResponse> call, Response<SalesListResponse> response) {
                    try {
                        invoiceArrayList.clear();

                        if(response.isSuccessful()){
                            SalesListResponse salesListResponse = response.body();
                            if(salesListResponse!=null){
                                SalesListMessage salesListMessage = salesListResponse.getMessage();
                                if(salesListMessage.getSuccess()){
                                    List<SalesListData> list = salesListMessage.getData();
                                    if(list!=null && !list.isEmpty()){
                                        Log.e("------------","received");
                                        invoiceArrayList.addAll(list);
                                        invoiceAdapter.notifyDataSetChanged();
                                        showEmptyView(false);
                                        return;
                                    }
                                }
                            }
                        }

                        showEmptyView(true);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SalesListResponse> call, Throwable t) {
                    try {



                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showEmptyView(boolean isShow){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fl_empty.setVisibility((isShow)?View.VISIBLE:View.GONE);
            }
        });
    }

    /**
     * on select invoice
     * */
    private void onSelectedInvoice(SalesListData invoice){
        try {

            Intent intent = new Intent(SalesActivity.this, PaymentActivity.class);
            intent.putExtra(Constants.INVOICE_ID, ""+invoice.getName());
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * on click cancel ( return )
     *  show confirmation for user and mark invoice as return when user allows
     * */
    private void onClickInvoiceReturn(InvoiceEntity invoice){
        try {

            if(invoice.getStatus().equals(Constants.PAYMENT_RETURN)){
                showToast(getString(R.string.cancelled), SalesActivity.this);
                return;
            }

            //inform user for confirmation with dialog box
            AppDialogs appDialogs = new AppDialogs(SalesActivity.this);
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
    private void markInvoiceAsReturn(InvoiceEntity invoice){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    invoice.setStatus(Constants.PAYMENT_RETURN);
                    localDb.invoiceDao().insertInvoice(invoice);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * invoice filter
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
     * adapter refresh
     * */
    private void updateInvoiceAdapter(List<InvoiceEntity> _invoicesList){
        try {

            runOnUiThread(() -> {
                try {

                    /*invoiceArrayList.clear();
                    invoiceArrayList.addAll(_invoicesList);
                    invoiceAdapter.notifyDataSetChanged();*/

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showToast(String msg){
        runOnUiThread(() -> showToast(msg, SalesActivity.this));
    }

    private void hideKeyboardOnSearchView(){
        View view = getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * To get the ongoing order count
     * */
    private void getOnGoingOrdersCount(){
        try {

            AppExecutors countExecutors = new AppExecutors();
            countExecutors.diskIO().execute(() -> {
                try {

                    int count = localDb.ordersDao().getOrdersWithStatus(Constants.ORDER_CREATED);
                    setBadgeCount(""+count);

                }catch (Exception e){
                    e.printStackTrace();
                }
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_badge.setText(badgeCount);
                    if(badgeCount.isEmpty() || badgeCount.equals("0")){
                        tv_badge.setVisibility(View.INVISIBLE);
                    }else {
                        tv_badge.setVisibility(View.VISIBLE);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoAddOrderActivity(){
        Intent createOrderIntent = new Intent(SalesActivity.this, CreateOrderActivity.class);
        startActivity(createOrderIntent);
    }

    private void gotoCategoryScreen(){
        Intent categoryIntent = new Intent(SalesActivity.this, CategoryListActivity.class);
        startActivity(categoryIntent);
    }

    private void gotoOrderQueueScreen(){
        Intent orderQueueIntent = new Intent(SalesActivity.this, OrderQueueActivity.class);
        startActivity(orderQueueIntent);
    }

}