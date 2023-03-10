package com.rpos.pos.presentation.ui.sales.order.create;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Transformations;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.OrderDetailsEntity;
import com.rpos.pos.data.local.entity.OrderEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.sales.add.AddSalesInvoiceResponse;
import com.rpos.pos.data.remote.dto.sales.add.AddSalesMessage;
import com.rpos.pos.data.remote.dto.sales.list.SalesListResponse;
import com.rpos.pos.domain.models.item.PickedItem;
import com.rpos.pos.domain.requestmodel.RequestWithUserId;
import com.rpos.pos.domain.requestmodel.sales.add.AddSalesRequest;
import com.rpos.pos.domain.requestmodel.sales.add.SalesItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.customer.select.CustomerSelectActivity;
import com.rpos.pos.presentation.ui.item.select.ItemSelectActivity;
import com.rpos.pos.presentation.ui.sales.adapter.SelectedItemAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateOrderActivity extends SharedActivity {

    private CardView cv_customer_view;
    private AppCompatTextView tv_customerName, tv_taxid,tv_grossAmount,tv_itemCount;
    private String customerId,customerName,customerTaxid,customerMobile;
    private RecyclerView rv_itemList;
    private List<PickedItem> pickedItemList;
    private LinearLayout ll_select_customer;
    private FloatingActionButton fab_item_add;
    private AppCompatButton btn_confirm_order;
    private LinearLayout ll_back;
    private AppCompatTextView tv_customerChooseBtn;

    //loading view
    private AppDialogs progressDialog;

    private SelectedItemAdapter selectedItemAdapter;
    private SharedPrefHelper prefHelper;

    private double grossAmount;
    private int totalItemCount;

    private int presentShiftId;

    @Override
    public int setUpLayout() {
        return R.layout.activity_create_order;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        cv_customer_view = findViewById(R.id.cv_customer);
        fab_item_add = findViewById(R.id.fab_add_item);
        btn_confirm_order = findViewById(R.id.btn_confirm);
        ll_select_customer = findViewById(R.id.ll_select_customer);
        ll_back = findViewById(R.id.ll_back);
        tv_grossAmount = findViewById(R.id.tv_grossamount);
        tv_itemCount = findViewById(R.id.tv_item_count);
        tv_customerName = findViewById(R.id.tv_customerName);
        tv_taxid = findViewById(R.id.tv_customerPhone);
        tv_customerChooseBtn = findViewById(R.id.tv_customer_choose);

        prefHelper = SharedPrefHelper.getInstance(this);
        progressDialog = new AppDialogs(CreateOrderActivity.this);

        customerId = "";
        customerName = "";
        grossAmount = 0;
        totalItemCount = 0;

        updateTotalInUi();

        //get the shift id
        if(getCoreApp().getRunningShift()!=null){
            presentShiftId = getCoreApp().getRunningShift().getId();
        }else {
            presentShiftId = -1;
        }

        pickedItemList = new ArrayList<>();

        String defaultCurrencySymbol = getCoreApp().getDefaultCurrency();

        rv_itemList = findViewById(R.id.rv_selectedItems);
        selectedItemAdapter = new SelectedItemAdapter(pickedItemList, CreateOrderActivity.this, defaultCurrencySymbol,Constants.PARENT_SALES, new SelectedItemAdapter.OnPickedItemClick() {
            @Override
            public void onItemClick(PickedItem item) {
                //item click
            }

            @Override
            public void onItemQuantityChange(PickedItem item) {
                try{
                    //on remove click
                    try{

                        if(item.getQuantity() < 1){
                            //delete for zero quantity
                            removeItemFromList(item.getId());
                        }

                        //re-calculate total
                        reCalculateTotalAndUpdate();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        rv_itemList.setAdapter(selectedItemAdapter);

        //check any default customer details saved in preference storage
        String[] defaultCustomer = prefHelper.getDefaultCustomer();
        //if available, get the values
        if(defaultCustomer!=null){
            customerId = defaultCustomer[0];
            customerName = defaultCustomer[1];
            customerMobile = defaultCustomer[2];
            //show default customer in display
            tv_customerName.setText(customerName);
            tv_taxid.setText(customerMobile);
            if(customerId == null || customerId.isEmpty()){
                tv_customerChooseBtn.setText(R.string.select_customer);
            }else {
                tv_customerChooseBtn.setText(R.string.remove_customer);
            }

        }else {
            tv_customerChooseBtn.setText(R.string.select_customer);
        }


        //Submit order
        btn_confirm_order.setOnClickListener(this::createOnlineOrder);

        //add item click
        fab_item_add.setOnClickListener(this::selectItem);

        //select customer
        ll_select_customer.setOnClickListener(this::selectCustomer);
        cv_customer_view.setOnClickListener(this::selectCustomer); //not using right now

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

    }

    @Override
    public void initObservers() {

    }

    /**
     * To remove item from list only
     * since order is not save at this moment, we can
     * just remove the items from the list and update the adapter
     * */
    private void removeItemFromList(String _itemId){
        try {

            //to avoid unnecessary refresh of adapter, use flag
            boolean isRefresh = false;
            int refreshPosition = -1;

            //To remove item from recycler view
            //find the item in array
            //remove and update adapter
            for (int i=0;i< pickedItemList.size();i++){
                if(pickedItemList.get(i).getId().equals(_itemId)) {
                    pickedItemList.remove(i);
                    refreshPosition = i;
                    isRefresh = true;
                    break;
                }
            }

            //refresh adapter only if the flag is true.
            if(isRefresh) {
                if(refreshPosition > -1){
                    selectedItemAdapter.notifyItemRemoved(refreshPosition);
                }else {
                    selectedItemAdapter.notifyDataSetChanged();
                }
            }

            //hide progress
            progressDialog.hideProgressbar();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  To SELECT  OR REMOVE CUSTOMER
     *  toggle action.
     *  if customer is present, then removes the customer on click.
     *  if no customer is selected , then redirect to select customer screen on click
     * */
    private void selectCustomer(View view){
        try {
            //check whether customer id is present
            if(customerId ==null || customerId.isEmpty()){
                //No customer id , no customer selected
                // then on click goto customer select screen
                //after customer selected , change button name to "remove customer"
                Intent selectIntent = new Intent(CreateOrderActivity.this, CustomerSelectActivity.class);
                launchCustomerPicker.launch(selectIntent);
            }else {
                //Customer already selected,
                // then remove customer

                //clear customer id
                customerId = "";
                //set default "none" value in fields
                tv_customerName.setText(Constants.NONE);
                tv_taxid.setText(Constants.NONE);
                //change button name to select customer
                tv_customerChooseBtn.setText(R.string.select_customer);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To open item list screen and choose one.
     *  always pass parent and selection type parameters ( N.B)
     * */
    private void selectItem(View view){
        try {

            Intent selectIntent = new Intent(CreateOrderActivity.this, ItemSelectActivity.class);
            selectIntent.putExtra(Constants.ITEM_REQUESTED_PARENT, Constants.PARENT_SALES); // Mandatory field
            selectIntent.putExtra(Constants.ITEM_SELECTION_TYPE, Constants.ITEM_SELECTION_QUANTITY_PICK); // Mandatory field
            launchItemPicker.launch(selectIntent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to calculate and update total
     * */
    private void reCalculateTotalAndUpdate(){
        //calculate total and item count
        calculateTotal();
        updateTotalInUi();
        //show item count
        updateItemCount(totalItemCount);
    }

    /**
     * calculate total
     * */
    private void calculateTotal(){
        try {
            grossAmount = 0;
            float  total;
            PickedItem item;
            int itemCount = 0;

            for (int i=0; i<pickedItemList.size();i++){
                item = pickedItemList.get(i);
                total = item.getQuantity() * item.getRate();
                grossAmount += total;
                itemCount+= item.getQuantity();
            }

            totalItemCount = itemCount;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to update the gross amount in ui
     * */
    private void updateTotalInUi(){
        String label = getString(R.string.gross_amount);
        tv_grossAmount.setText(label+" : "+grossAmount);
    }

    private ActivityResultLauncher<Intent> launchCustomerPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();
                if(data!=null){
                    customerId = data.getStringExtra(Constants.CUSTOMER_ID);
                    customerName = data.getStringExtra(Constants.CUSTOMER_NAME);
                    customerTaxid = data.getStringExtra(Constants.CUSTOMER_TAXID);
                    customerMobile = data.getStringExtra(Constants.CUSTOMER_PHONE);

                    tv_customerName.setText(customerName);
                    tv_taxid.setText(customerMobile);

                    tv_customerChooseBtn.setText(getString(R.string.remove_customer));
                }
            }
        }
    });

    private ActivityResultLauncher<Intent> launchItemPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == RESULT_OK){

                Intent data  = result.getData();

                if(data!=null){

                    Integer _itemId = data.getIntExtra(Constants.ITEM_ID, Constants.EMPTY_INT);
                    String itemId = String.valueOf(_itemId);
                    int quantity = data.getIntExtra(Constants.ITEM_QUANTITY, 0);
                    Integer uomId = data.getIntExtra(Constants.ITEM_UOM_ID, Constants.EMPTY_INT);
                    String itemName = data.getStringExtra(Constants.ITEM_NAME);
                    String itemRate = data.getStringExtra(Constants.ITEM_RATE);
                    float stock = data.getFloatExtra(Constants.ITEM_STOCK, 0.0f);
                    boolean isMaintainStock = data.getBooleanExtra(Constants.ITEM_MAINTAIN_STOCK, true);

                    float rate = Float.parseFloat(itemRate);

                    PickedItem existingItem = checkItemAlreadyAdded(itemId);
                    if(existingItem == null){
                        PickedItem item  = new PickedItem().getPickedItemFromFields(itemId,itemName,uomId, rate,quantity,stock,isMaintainStock);
                        addItemToList(item);
                    }else {
                        existingItem.setQuantity(quantity);
                        selectedItemAdapter.notifyDataSetChanged();
                        showToast(getString(R.string.item_updated), CreateOrderActivity.this);
                    }
                }
            }
        }
    });

    /**
     * create order
     * */
    private void createOnlineOrder(View view){
        try {

            String userId = SharedPrefHelper.getInstance(this).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid), CreateOrderActivity.this);
                return;
            }

            //validate items
            if(pickedItemList.isEmpty()){
                showToast(getString(R.string.select_items), CreateOrderActivity.this);
                return;
            }

            //validate customer
            if(customerId.isEmpty()){
                showToast(getString(R.string.select_customer), CreateOrderActivity.this);
                return;
            }

            /*"item_code": "21",
                    "item_name": "apple",
                    "rate": "23",
                    "qty": "3",
                    "uom": "120"*/

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            AddSalesRequest request = new AddSalesRequest();
            request.setCustomerId(customerId);
            request.setUserId(userId);

            PickedItem pickedItem;
            SalesItem requestItem;
            List<SalesItem> salesItemList = new ArrayList<>();
            for (int i= 0;i<pickedItemList.size();i++){
                requestItem = new SalesItem();
                pickedItem = pickedItemList.get(i);

                requestItem.setItemCode(pickedItem.getId());
                requestItem.setItemName(pickedItem.getItemName());
                requestItem.setQty(String.valueOf(pickedItem.getQuantity()));
                requestItem.setRate(String.valueOf(pickedItem.getRate()));
                requestItem.setUom(pickedItem.getUom());
                salesItemList.add(requestItem);
            }
            request.setItems(salesItemList);

            Call<AddSalesInvoiceResponse> call = api.addSalesInvoice(request);
            call.enqueue(new Callback<AddSalesInvoiceResponse>() {
                @Override
                public void onResponse(Call<AddSalesInvoiceResponse> call, Response<AddSalesInvoiceResponse> response) {
                    try {
                        Log.e("-----------","resp "+response.isSuccessful());
                        if(response.isSuccessful()){
                            Log.e("-----------","resp true");
                            AddSalesInvoiceResponse addSalesInvoiceResponse = response.body();
                            if(addSalesInvoiceResponse!=null){
                                AddSalesMessage addSalesMessage = addSalesInvoiceResponse.getMessage();
                                Log.e("-----------","resp mess "+addSalesMessage.getSuccess());
                                if(addSalesMessage.getSuccess()){
                                    showSuccess();
                                    return;
                                }
                            }
                        }

                        showToast(getString(R.string.item_add_failed), CreateOrderActivity.this);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddSalesInvoiceResponse> call, Throwable t) {
                   showToast(t.getMessage(), CreateOrderActivity.this);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private PickedItem checkItemAlreadyAdded(String itemId){

        for (int i =0;i< pickedItemList.size();i++){
            if(pickedItemList.get(i).getId() == itemId){
                return pickedItemList.get(i);
            }
        }

        return null;
    }

    private void showSuccess(){
        try {

            AppDialogs appDialogs = new AppDialogs(CreateOrderActivity.this);
            appDialogs.showCommonSuccessDialog(getString(R.string.order_success), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{

                        finish();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addItemToList(PickedItem item){
        try{

            pickedItemList.add(item);
            selectedItemAdapter.notifyItemChanged(pickedItemList.size()-1);
            showToast(getString(R.string.new_item_added), CreateOrderActivity.this);

            //calculate total and update
            reCalculateTotalAndUpdate();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateItemCount(int count){
        String itemCunt = getString(R.string.total_items) +": "+ count;
        tv_itemCount.setText(itemCunt);
    }

}