package com.rpos.pos.presentation.ui.purchase.order.create;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.PurchaseOrderDetailsEntity;
import com.rpos.pos.data.local.entity.PurchaseOrderEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.purchase.add.AddPurchaseMessage;
import com.rpos.pos.data.remote.dto.purchase.add.AddPurchaseResponse;
import com.rpos.pos.data.remote.dto.purchase.list.PurchaseInvoiceData;
import com.rpos.pos.data.remote.dto.purchase.list.PurchaseListMessage;
import com.rpos.pos.data.remote.dto.purchase.list.PurchaseListResponse;
import com.rpos.pos.domain.models.item.PickedItem;
import com.rpos.pos.domain.requestmodel.RequestWithUserId;
import com.rpos.pos.domain.requestmodel.purchase.add.AddPurchaseRequest;
import com.rpos.pos.domain.requestmodel.purchase.add.PurchaseItem;
import com.rpos.pos.domain.requestmodel.sales.add.SalesItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.DateTimeUtils;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.select.ItemSelectActivity;
import com.rpos.pos.presentation.ui.purchase.list.PurchaseActivity;
import com.rpos.pos.presentation.ui.sales.adapter.SelectedItemAdapter;
import com.rpos.pos.presentation.ui.supplier.selection.SupplierSelectionActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePurchaseActivity extends SharedActivity {

    private RecyclerView rv_itemList;
    private List<PickedItem> pickedItemList;
    private FloatingActionButton fab_item_add;
    private LinearLayout ll_back;
    private SelectedItemAdapter selectedItemAdapter;
    private SharedPrefHelper prefHelper;
    private LinearLayout ll_select_supplier;
    private AppCompatTextView tv_customerName, tv_taxid ,tv_grossAmount,tv_itemCount;
    private AppCompatButton btn_confirm_order;

    private AppDatabase localDb;
    private AppExecutors appExecutors;

    //loading view
    private AppDialogs progressDialog;

    private Map<Integer, ItemEntity> hashmap_source;

    private int presentShiftId;

    private double grossAmount;
    private int totalItemCount;
    private int supplierId;
    private String supplierName;

    @Override
    public int setUpLayout() {
        return R.layout.activity_create_purchase;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        progressDialog = new AppDialogs(this);

        fab_item_add = findViewById(R.id.fab_add_item);
        ll_back = findViewById(R.id.ll_back);
        ll_select_supplier = findViewById(R.id.ll_select_customer);
        tv_customerName = findViewById(R.id.tv_customerName);
        tv_taxid = findViewById(R.id.tv_customerPhone);
        tv_grossAmount = findViewById(R.id.tv_grossamount);
        tv_itemCount = findViewById(R.id.tv_item_count);
        btn_confirm_order = findViewById(R.id.btn_confirm);

        localDb = getCoreApp().getLocalDb();
        appExecutors = new AppExecutors();

        //get the shift id
        if(getCoreApp().getRunningShift()!=null){
            presentShiftId = getCoreApp().getRunningShift().getId();
        }else {
            presentShiftId = -1;
        }

        grossAmount = 0;
        totalItemCount = 0;
        supplierId = Constants.EMPTY_INT;
        supplierName = ""; // empty

        hashmap_source = new HashMap<>();

        updateTotalInUi();

        String defaultCurrencySymbol = getCoreApp().getDefaultCurrency();

        pickedItemList = new ArrayList<>();
        rv_itemList = findViewById(R.id.rv_selectedItems);
        selectedItemAdapter = new SelectedItemAdapter(pickedItemList, CreatePurchaseActivity.this, defaultCurrencySymbol,Constants.PARENT_PURCHASE, new SelectedItemAdapter.OnPickedItemClick() {
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
                    //        removeItemFromList(item.getId());
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

        //order confirm
        btn_confirm_order.setOnClickListener(view -> createOnlineOrder());

        //item select
        fab_item_add.setOnClickListener(view -> selectItem());

        //select supplier
        ll_select_supplier.setOnClickListener(view -> selectSupplier());

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());

        //items preload to make item selection screen faster
        preLoadAllItems();
    }

    @Override
    public void initObservers() {

    }



    private void selectItem(){
        Intent selecteIntent = new Intent(CreatePurchaseActivity.this, ItemSelectActivity.class);
        selecteIntent.putExtra(Constants.ITEM_REQUESTED_PARENT, Constants.PARENT_PURCHASE); // Mandatory field
        selecteIntent.putExtra(Constants.ITEM_SELECTION_TYPE, Constants.ITEM_SELECTION_QUANTITY_PICK); // Mandatory field
        launchItemPicker.launch(selecteIntent);
    }

    private ActivityResultLauncher<Intent> launchItemPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();

                if(data!=null){
                    Integer _itemId = data.getIntExtra(Constants.ITEM_ID,Constants.EMPTY_INT);
                    String itemId = String.valueOf(_itemId);
                    int quantity = data.getIntExtra(Constants.ITEM_QUANTITY, 0);
                    String itemName = data.getStringExtra(Constants.ITEM_NAME);
                    Integer uomId = data.getIntExtra(Constants.ITEM_UOM_ID,Constants.EMPTY_INT);
                    String itemRate = data.getStringExtra(Constants.ITEM_RATE);
                    float stock = data.getFloatExtra(Constants.ITEM_STOCK, 0.0f);
                    boolean isMaintainStock = data.getBooleanExtra(Constants.ITEM_MAINTAIN_STOCK, true);

                    float rate = Float.parseFloat(itemRate);

                    PickedItem existingItem = checkItemAlreadyAdded(itemId);
                    if(existingItem == null){
                        PickedItem item  = new PickedItem().getPickedItemFromFields(itemId,itemName,uomId,rate,quantity,stock,isMaintainStock);
                        addItemToList(item);
                    }else {
                        existingItem.setQuantity(quantity);
                        //refresh to reflect the change in recyclerview
                        selectedItemAdapter.notifyDataSetChanged();
                        showToast(getString(R.string.item_updated), CreatePurchaseActivity.this);
                    }
                }
            }
        }
    });


    private void selectSupplier(){
        Intent selectIntent = new Intent(CreatePurchaseActivity.this, SupplierSelectionActivity.class);
        launchSupplierPicker.launch(selectIntent);
    }

    private ActivityResultLauncher<Intent> launchSupplierPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();
                if(data!=null){
                    Log.e("------------","asdfasdfasdfasd");
                    String supplId = data.getStringExtra(Constants.SUPPLIER_ID);
                    supplierId = Integer.parseInt(supplId);
                    supplierName = data.getStringExtra(Constants.SUPPLIER_NAME);
                    String supplierTaxId = data.getStringExtra(Constants.SUPPLIER_TAXID);

                    tv_customerName.setText(supplierName);
                    tv_taxid.setText(supplierTaxId);
                }
            }
        }
    });

    private PickedItem checkItemAlreadyAdded(String itemId){

        for (int i =0;i< pickedItemList.size();i++){
            if(pickedItemList.get(i).getId().equals(itemId)){
                return pickedItemList.get(i);
            }
        }

        return null;
    }

    private void addItemToList(PickedItem item){
        try{

            pickedItemList.add(item);
            selectedItemAdapter.notifyItemChanged(pickedItemList.size()-1);
            showToast(getString(R.string.new_item_added), CreatePurchaseActivity.this);

            //calculate total and update
             reCalculateTotalAndUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get locally saved item list
     * */
    private void preLoadAllItems(){
        try{
            //thread for room db function
            appExecutors.diskIO().execute(() -> {

                final List<ItemEntity> localItemList = localDb.itemDao().getAllItems();
                //check item list size
                if(localItemList == null || localItemList.isEmpty()){
                    //show alert for items empty
                    getCoreApp().setAllItemsList(null);
                }else {
                    getCoreApp().setAllItemsList(localItemList);
                }
            });

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

    private void updateItemCount(int count){
        String itemCunt = getString(R.string.total_items) +": "+ count;
        tv_itemCount.setText(itemCunt);
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
     * to remove item entity objects from the temporary list
     * hash map is used to store database item entities to  update the item stock on order create
     */
    private void removeItemSourceHashMap(Integer _itemId){
        try {

            Iterator itemIterator = hashmap_source.entrySet().iterator();
            while (itemIterator.hasNext()){
                Map.Entry entry = (Map.Entry)itemIterator.next();

                ItemEntity itemEntity = (ItemEntity)entry.getValue();
                if(itemEntity.getItemId() == _itemId){
                    itemIterator.remove();
                    break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void createOfflineOrder(){
        try{

            if(pickedItemList.isEmpty()){
                showToast(getString(R.string.select_items), CreatePurchaseActivity.this);
                return;
            }

            if(supplierId == Constants.EMPTY_INT){
                showToast(getString(R.string.select_supplier), CreatePurchaseActivity.this);
                return;
            }

            String date_time = DateTimeUtils.getCurrentDateTime();

            final PurchaseOrderEntity newOrder = new PurchaseOrderEntity();
            newOrder.setSupplierId(supplierId);
            newOrder.setSupplierName(supplierName);
            newOrder.setDateTime(date_time);
            newOrder.setShift(presentShiftId);
            newOrder.setStatus(Constants.ORDER_CREATED);


            float totalAmount = 0.0f;
            for (int i =0;i< pickedItemList.size();i++){
                totalAmount+= pickedItemList.get(i).getRate();
            }
            newOrder.setAmount(totalAmount);



            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try{

                        final long orderId = localDb.purchaseOrderDao().insertOrder(newOrder);
                        Log.e("------------","long>"+orderId);

                        List<PurchaseOrderDetailsEntity> orderDetailsList = new ArrayList<>();
                        PurchaseOrderDetailsEntity orderDetails;
                        PickedItem pickedItem;
                        for (int i= 0;i<pickedItemList.size();i++){
                            orderDetails= new PurchaseOrderDetailsEntity();
                            pickedItem = pickedItemList.get(i);

                            orderDetails.setOrderId(orderId);  //order id
                            //orderDetails.setItemId(pickedItem.getId());  //item id
                            orderDetails.setQuantity(pickedItem.getQuantity());
                            orderDetails.setVariableRate(pickedItem.getRate()); //adding new price
                            orderDetailsList.add(orderDetails);
                        }

                        //insert details
                        localDb.purchaseOrderDetailsDao().insertOrderDetailsList(orderDetailsList);

                        //To update stock on order create
                        //update item with new stock quantity
                        /*ItemEntity tempItemEntity;
                        int stock, balanceStock;
                        for (int i=0;i< pickedItemList.size();i++){
                            //get database item from hashmap
                            tempItemEntity = hashmap_source.get(pickedItemList.get(i).getId());
                            //get the current stock
                            stock = tempItemEntity.getAvailableQty();
                            //calculate balance
                            balanceStock = stock + pickedItemList.get(i).getQuantity();
                            //set the new stock to database entity
                            tempItemEntity.setAvailableQty(balanceStock);
                            //update the item
                            localDb.itemDao().insertItem(tempItemEntity);
                        }*/

                        //use the ui thread to update success dialog
                        runOnUiThread(() -> showSuccess());

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createOnlineOrder(){
        try {

            if(pickedItemList.isEmpty()){
                showToast(getString(R.string.select_items), CreatePurchaseActivity.this);
                return;
            }

            if(supplierId == Constants.EMPTY_INT){
                showToast(getString(R.string.select_supplier), CreatePurchaseActivity.this);
                return;
            }

            addPurchaseInvoice();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * to get all invoice list from web service
     * */
    private void addPurchaseInvoice(){
        try {

            String userId = SharedPrefHelper.getInstance(this).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid), CreatePurchaseActivity.this);
                return;
            }

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);

            //parameter
            AddPurchaseRequest request = new AddPurchaseRequest();
            request.setUserId(userId);
            request.setSupplierId(String.valueOf(supplierId));


            PickedItem pickedItem;
            PurchaseItem requestItem;
            List<PurchaseItem> purchaseItemList = new ArrayList<>();
            for (int i= 0;i<pickedItemList.size();i++){
                requestItem = new PurchaseItem();
                pickedItem = pickedItemList.get(i);

                requestItem.setItemCode(pickedItem.getId());
                requestItem.setItemName(pickedItem.getItemName());
                requestItem.setQty(String.valueOf(pickedItem.getQuantity()));
                requestItem.setRate(String.valueOf(pickedItem.getRate()));
                requestItem.setUom(pickedItem.getUom());
                purchaseItemList.add(requestItem);
            }
            request.setItems(purchaseItemList);

            Call<AddPurchaseResponse> call = api.addNewPurchaseInvoice(request);
            call.enqueue(new Callback<AddPurchaseResponse>() {
                @Override
                public void onResponse(Call<AddPurchaseResponse> call, Response<AddPurchaseResponse> response) {
                    try {

                        if(response.isSuccessful()){

                            AddPurchaseResponse addPurchaseResponse = response.body();

                            if(addPurchaseResponse!=null){

                                AddPurchaseMessage addPurchaseMessage = addPurchaseResponse.getMessage();

                                if(addPurchaseMessage.getSuccess()){
                                    Log.e("------------","received");
                                    showSuccess();
                                    return;
                                }
                            }
                        }

                        showToast(getString(R.string.please_check_internet), CreatePurchaseActivity.this);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AddPurchaseResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void showSuccess(){
        try {

            AppDialogs appDialogs = new AppDialogs(CreatePurchaseActivity.this);
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

}