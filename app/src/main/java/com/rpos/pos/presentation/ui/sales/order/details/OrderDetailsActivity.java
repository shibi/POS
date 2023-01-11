package com.rpos.pos.presentation.ui.sales.order.details;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.OrderDetailsEntity;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.domain.models.item.PickedItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.domain.utils.TextWatcherExtended;
import com.rpos.pos.domain.utils.Utility;
import com.rpos.pos.presentation.ui.sales.order.checkout.CheckoutActivity;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.select.ItemSelectActivity;
import com.rpos.pos.presentation.ui.sales.adapter.SelectedItemAdapter;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailsActivity extends SharedActivity {

    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout ll_bottomsheet , ll_next;
    private LinearLayout ll_checkout;
    private AppCompatTextView tv_grossAmount,tv_netAmount,tv_itemCount,tv_top_discount,tv_top_taxPercent,tv_top_taxAmount;
    private RecyclerView rv_itemList;
    private List<PickedItem> pickedItemList;
    private SelectedItemAdapter selectedItemAdapter;
    private LinearLayout ll_back, ll_add_items;
    private TaxSpinnerAdapter taxSpAdapter;
    private AppCompatSpinner spinner_taxslab;
    private List<TaxSlabEntity> taxSlabList;

    //bottom sheet
    private AppCompatButton btn_bs_checkout,btn_bs_minimize;
    private AppCompatEditText et_discountAmount, et_discountPercent, et_taxAmount;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private int orderId,customerId, priceListId;
    private String customerName;
    private SharedPrefHelper sharedPref;
    private boolean isDbOperationOn;
    private int defaultSelectedTaxId;
    private DISCOUNT discount_type;

    //top options
    private double gross_amount,net_amount,discount_amount,tax_amount;
    private int totalItemCount;

    private int presentShiftId;

    //loading view
    private AppDialogs progressDialog;

    private final String ACTION_BROADCAST_DELETE_ITEM = "action.broadcast.delete";

    private enum DISCOUNT{
        AMOUNT,
        PERCENTAGE,
        NONE
    }


    @Override
    public int setUpLayout() {
        return R.layout.activity_order_details;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rv_itemList = findViewById(R.id.rv_orderItems);
        ll_checkout = findViewById(R.id.ll_checkout);
        tv_grossAmount = findViewById(R.id.tv_grossamount);
        tv_netAmount = findViewById(R.id.tv_netamount);
        ll_back = findViewById(R.id.ll_back);
        ll_add_items = findViewById(R.id.ll_rightMenu);
        ll_next = findViewById(R.id.ll_next);

        tv_itemCount = findViewById(R.id.tv_item_count);
        tv_top_discount = findViewById(R.id.tv_item_discount);
        tv_top_taxPercent = findViewById(R.id.tv_taxPercent);
        tv_top_taxAmount = findViewById(R.id.tv_taxAmount);


        //bottom sheet
        btn_bs_checkout = findViewById(R.id.btn_ok);
        btn_bs_minimize = findViewById(R.id.btn_close);
        et_discountAmount = findViewById(R.id.et_discAmount);
        et_discountPercent = findViewById(R.id.et_discPercent);
        et_taxAmount = findViewById(R.id.et_taxAmount);
        spinner_taxslab = findViewById(R.id.sp_tax);
        //et_taxPercent = findViewById(R.id.et_TaxPercent);

        //get the shift id
        if(getCoreApp().getRunningShift()!=null){
            presentShiftId = getCoreApp().getRunningShift().getId();
        }else {
            presentShiftId = -1;
        }


        sharedPref = SharedPrefHelper.getInstance(OrderDetailsActivity.this);
        defaultSelectedTaxId = sharedPref.getDefaultTaxId();

        //get the default price list (id ) for the Selling
        //N.B - value can be empty (-1)
        priceListId = sharedPref.getSellingPriceListId();
        if(priceListId == Constants.EMPTY_INT){
            showToast(getString(R.string.default_pricelist_not_selected), OrderDetailsActivity.this);
        }

        ll_bottomsheet = findViewById(R.id.ll_discount_bottomsheet);
        bottomSheetBehavior = BottomSheetBehavior.from(ll_bottomsheet);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels;
        int maxHeight = (int) (height*0.80);
        bottomSheetBehavior.setMaxHeight(maxHeight);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);


        progressDialog = new AppDialogs(OrderDetailsActivity.this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();


        //reset values
        gross_amount = 0.0;
        net_amount = 0.0;
        discount_amount = 0.0;
        tax_amount = 0.0;
        totalItemCount = 0;
        isDbOperationOn = false;
        discount_type = DISCOUNT.NONE;

        Intent data = getIntent();
        if(data!=null){
            orderId = data.getIntExtra(Constants.ORDER_ID, Constants.EMPTY_INT);
            customerId = data.getIntExtra(Constants.CUSTOMER_ID, Constants.EMPTY_INT);
            customerName = data.getStringExtra(Constants.CUSTOMER_NAME);
        }

        if(orderId == Constants.EMPTY_INT){
            showToast(getString(R.string.invalid_orderid),OrderDetailsActivity.this);
            return;
        }

        if(customerId == Constants.EMPTY_INT){
            showToast(getString(R.string.invalid_customerid),OrderDetailsActivity.this);
            return;
        }

        taxSlabList = new ArrayList<>();

        taxSpAdapter = new TaxSpinnerAdapter(OrderDetailsActivity.this, taxSlabList);
        spinner_taxslab.setAdapter(taxSpAdapter);


        pickedItemList = new ArrayList<>();
        String defaultCurrencySymbol = getCoreApp().getDefaultCurrency();


        selectedItemAdapter = new SelectedItemAdapter(pickedItemList, OrderDetailsActivity.this, defaultCurrencySymbol,Constants.PARENT_SALES_ORDER_DETAILS, new SelectedItemAdapter.OnPickedItemClick() {
            @Override
            public void onItemClick(PickedItem item) {
                //item click
            }

            @Override
            public void onItemQuantityChange(PickedItem item) {
                try{
                    if(item.getQuantity() < 1){
                        if(isDbOperationOn){
                            Log.e("----------","already deleting");
                            return;
                        }
                        //delete for zero quantity
                        deleteItem(orderId, item);
                    }else {
                        if(isDbOperationOn){
                            return;
                        }
                        //update quantity
                        updateSelectedItems(item);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        rv_itemList.setAdapter(selectedItemAdapter);


        et_discountAmount.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void onAfterTextChange(String value) {
                try {

                    discount_type = DISCOUNT.AMOUNT;
                    //type amount
                    reCalculateDiscount(discount_type);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        et_discountPercent.addTextChangedListener(new TextWatcherExtended() {
            @Override
            public void onAfterTextChange(String value) {
                try {

                    discount_type = DISCOUNT.PERCENTAGE;

                    reCalculateDiscount(discount_type);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        ll_checkout.setOnClickListener(view -> {
            try{
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //checkout
        btn_bs_checkout.setOnClickListener(view -> {
            try {

                // To prevent ordering without items check-
                // whether items present
                if(pickedItemList.size()>0) {
                    //order checkout
                    checkoutOrder();

                }else {
                    showToast(getString(R.string.item_missing),OrderDetailsActivity.this);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });


        //minimise window
        btn_bs_minimize.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            hideSoftKeyboard();
        });

        ll_next.setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        //back
        ll_back.setOnClickListener(view -> onBackPressed());


        ll_add_items.setOnClickListener(view -> {
            try{
                selectItem();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //get the item details from local db
        getSelectedItemListFromLocalDb();

        spinner_taxslab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{

                    float slab_rate = taxSlabList.get(i).getSlabAmount();
                    et_taxAmount.setText(String.valueOf(slab_rate));
                    tv_top_taxPercent.setText(String.valueOf(slab_rate));

                    calculateTaxAmount();

                    calculateNetAmount();

                    updateTotals();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.e("----------","selected nothing");
            }
        });

    }

    @Override
    public void initObservers() {

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_BROADCAST_DELETE_ITEM);
        registerReceiver(DeleteItemUIUpdaterBroadcast, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(DeleteItemUIUpdaterBroadcast);
    }

    private void getSelectedItemListFromLocalDb(){
        try {

            appExecutors.diskIO().execute(() -> {
                try{

                    List<OrderDetailsEntity> detailsList = localDb.orderDetailsDao().getAllItemsInOrder(orderId);
                    List<ItemEntity> allItemsList = localDb.itemDao().getAllItems();

                    if(detailsList!=null && allItemsList!=null){

                        PickedItem pickedItem;
                        ItemEntity savedItemEntity;
                        OrderDetailsEntity orderDetails;
                        List<PickedItem> formatedItems = new ArrayList<>();

                        for (int i=0;i<detailsList.size();i++){

                            orderDetails = detailsList.get(i);

                            for (int j=0;j<allItemsList.size();j++){

                                savedItemEntity = allItemsList.get(j);

                                if(orderDetails.getItemId().equals(savedItemEntity.getItemId())){
                                    pickedItem = new PickedItem();
                                    pickedItem.convertFromSavedItemEntity(savedItemEntity);
                                    pickedItem.setQuantity(orderDetails.getQuantity());
                                    pickedItem.setRate(orderDetails.getVariablePrice());// saving current price in order details table to fetch later in checkout
                                    formatedItems.add(pickedItem);
                                    break;
                                }
                            }
                        }

                        pickedItemList.clear();
                        pickedItemList.addAll(formatedItems);
                        //update the adapter and re-calculate totals
                        runOnUiThread(() -> {
                            try {
                                //use ui thread to update adapter
                                selectedItemAdapter.notifyDataSetChanged();
                                //calculate total and display
                                calculateTotalAndUpdate();

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        });


                        //THIS IS SEPARATE CALL
                        //Initiate default tax call from the end of item list method(here)
                        //get tax slabs for spinner
                        getAllTaxSlabs();
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
     * to get all taxes
     * */
    private void getAllTaxSlabs(){
        try {

            List<TaxSlabEntity> taxTempList = localDb.taxesDao().getAllSlabs();
            if(taxTempList!=null && taxTempList.size() > 0){
                taxSlabList.clear();
                taxSlabList.addAll(taxTempList);

                int defaultPosition = -1;
                for (int i = 0; i < taxSlabList.size(); i++) {
                    if (taxSlabList.get(i).getId() == defaultSelectedTaxId) {
                        taxSlabList.get(i).setSelected(true);
                        defaultPosition = i;
                        break;
                    }
                }

                final int positionToHighlight = defaultPosition;
                runOnUiThread(() -> {
                    taxSpAdapter.notifyDataSetChanged();
                    if(positionToHighlight > -1){
                        spinner_taxslab.setSelection(positionToHighlight);
                    }
                });

            }else {

                runOnUiThread(() -> {
                    AppDialogs appDialogs = new AppDialogs(OrderDetailsActivity.this);
                    appDialogs.showCommonSingleAlertDialog(getString(R.string.alert), getString(R.string.add_tax_first), view -> finish());
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to re-calculate discount on user entering discount(amount/percent)
     * @param discountType to identify type
     * */
    private void reCalculateDiscount(DISCOUNT discountType){
        try {

            //calculate discount amount with type
            calculateDiscount(discountType);

            //calculate tax on new net amount
            calculateTaxAmount();

            //find net amount after discount
            calculateNetAmount();

            //update total
            updateTotals();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to calculate and display totals
     * */
    private void calculateTotalAndUpdate(){
        try {

            //calculate total
            calculateTotalAndItemCount();
            //calculate discount on gross amount
            calculateDiscount(discount_type);
            //calculate tax
            calculateTaxAmount();
            //calculate net amount
            calculateNetAmount();
            //show total on screen
            updateTotals();

        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    /**
     * calculate discount
     * @param discountType use type to differentiate amount and percent
     * */
    private void calculateDiscount(DISCOUNT discountType){
        try {

            double temp_disc_amount = 0.0;

            if(discountType == DISCOUNT.AMOUNT){

                //calculate amount from flat
                temp_disc_amount = calculateFlatDiscountAmount();

            }else if(discountType == DISCOUNT.PERCENTAGE){

                //calculate amount from percent
                temp_disc_amount = calculatePercentDiscount();

            }

            String roundedDiscountAmount = Utility.roundOffDecimalValueTo2Digits(temp_disc_amount);
            discount_amount = Double.parseDouble(roundedDiscountAmount);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * calculate flat amount
     * */
    private double calculateFlatDiscountAmount(){

        try {
            double temp_disc_amount = 0.0;
            double tempGrossAmount = gross_amount;
            String str_discountAmount = et_discountAmount.getText().toString();
            if(!str_discountAmount.isEmpty()) {
                //TO AVOID ERROR WHILE PARSING A NUMBER THAT STARTS WITH DECIMAL POINT DESPITE ZERO. EXAMPLE (".58")//
                //CHECK AND APPEND ZERO AT THE BEGINNING OF THE NUMBER IF THE NUMBER STARTS WITH DECIMAL
                if(str_discountAmount.startsWith(".")){
                    str_discountAmount = "0"+str_discountAmount;
                }
                temp_disc_amount = Double.parseDouble(str_discountAmount);

                if(temp_disc_amount > tempGrossAmount){
                    et_discountAmount.setError(getString(R.string.discount_amount_exceeded_total));
                    et_discountAmount.setText("0");
                    return 0;
                }

            }else {
                temp_disc_amount = 0;
            }
            return temp_disc_amount;
        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * to calculate percent discount
     * */
    private double calculatePercentDiscount(){
        try {

            double temp_disc_amount = 0.0;
            String str_discountPercent = et_discountPercent.getText().toString();;
            double tempGrossAmount = gross_amount;
            if(!str_discountPercent.isEmpty()){
                //TO AVOID ERROR WHILE PARSING A NUMBER THAT STARTS WITH DECIMAL POINT DESPITE ZERO. EXAMPLE (".58")//
                //CHECK AND APPEND ZERO AT THE BEGINNING OF THE NUMBER IF THE NUMBER STARTS WITH DECIMAL
                if(str_discountPercent.startsWith(".")){
                    str_discountPercent = "0"+str_discountPercent;
                }
                double discount = Double.parseDouble(str_discountPercent);

                //check whether discount amount exceeds 100%
                if(discount>100){
                    et_discountPercent.setText("0");
                    et_discountPercent.setError(getString(R.string.discount_perc_exceed));
                    return 0;
                }

                temp_disc_amount = tempGrossAmount * discount/100;
            }else {
                temp_disc_amount = 0;
            }

            return temp_disc_amount;

        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * To calculate tax amount on net amount
     * always calculate net amount before finding tax
     * since tax is levied on net amount
     * */
    private void calculateTaxAmount(){
        try {

            //NB - tax always calculated after discount
            //so deduct the discount amount from total and calculate tax
            //discount amount variable is updated each time anything entered in discount field.

            String taxRate = tv_top_taxPercent.getText().toString();
            float tax = Float.parseFloat(taxRate);

            double tempGrossAmount = gross_amount;
            double tempNetAmount = tempGrossAmount - discount_amount;
            double tempTax =  (tempNetAmount * tax /100);

            String roundedTaxAmount = Utility.roundOffDecimalValueTo2Digits(tempTax);
            tax_amount = Double.parseDouble(roundedTaxAmount);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to calculate net amount
     * */
    private void calculateNetAmount(){
        try {

            double tempGrossAmount = gross_amount;
            double db_net_amount = tempGrossAmount - discount_amount + tax_amount;

            String roundedNetAmount = Utility.roundOffDecimalValueTo2Digits(db_net_amount);
            net_amount = Double.parseDouble(roundedNetAmount);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * update selected item quantity
     * */
    private void updateSelectedItems(PickedItem updateItem){
        try {

            isDbOperationOn = true;

            //show progress
            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(() -> {
                try {

                    OrderDetailsEntity modifyItem = localDb.orderDetailsDao().getSingleItem(orderId,updateItem.getId());
                    modifyItem.setQuantity(updateItem.getQuantity());
                    localDb.orderDetailsDao().insertOrderDetail(modifyItem);

                    if(updateItem.getMaintainStock()) { //stock update for maintain stock enabled items.
                        //get original item
                        ItemEntity originalItem = localDb.itemDao().getItemDetails(updateItem.getId());
                        //set the new stock
                        originalItem.setAvailableQty(updateItem.getStock());
                        //update the item
                        localDb.itemDao().insertItem(originalItem);
                    }

                    runOnUiThread(() -> {

                        showToast(getString(R.string.item_updated), OrderDetailsActivity.this);
                        //hide progress
                        progressDialog.hideProgressbar();

                        //calculate and display total
                        calculateTotalAndUpdate();

                        isDbOperationOn = false;
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteItem(long _orderId,PickedItem item){
        try {

            int _itemId = item.getId();

            //flag to check deleting status
            isDbOperationOn = true;

            progressDialog.showProgressBar();

            appExecutors.diskIO().execute(() -> {
                try{
                    Log.e("-----------","3-0");
                    //delete item from order details table
                    localDb.orderDetailsDao().deleteItem(_orderId,_itemId);


                    if(item.getMaintainStock()) {//------------------------------------------------------------------------------------------------start
                        //TO UPDATE THE STOCK OF ORIGINAL ITEM ON DELETING.THE LAST QUANTITY NEEDS TO BE UPDATED HERE.
                        // QUANTITY GREATER THAN 1 WILL BE UPDATED IN THE ELSE PART OF ADAPTER INCREMENT FUNCTION.   IE : - onItemQuantityChange();
                        //get original item
                        ItemEntity originalItem = localDb.itemDao().getItemDetails(_itemId); //get the original item
                        int updateStock = originalItem.getAvailableQty() + 1; //updated stock
                        originalItem.setAvailableQty(updateStock);  //set the new stock
                        localDb.itemDao().insertItem(originalItem); //update the item
                    } //------------------------------------------------------------------------------------------------end

                    //update ui details outside the thread
                    //use broadcast to update adapter and ui
                    Intent intent = new Intent();
                    intent.putExtra(Constants.ITEM_ID, _itemId);
                    intent.setAction(ACTION_BROADCAST_DELETE_ITEM);
                    sendBroadcast(intent);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void selectItem(){
        Intent selecteIntent = new Intent(OrderDetailsActivity.this, ItemSelectActivity.class);
        selecteIntent.putExtra(Constants.ITEM_REQUESTED_PARENT, Constants.PARENT_SALES); // Mandatory field
        selecteIntent.putExtra(Constants.ITEM_SELECTION_TYPE, Constants.ITEM_SELECTION_QUANTITY_PICK); // Mandatory field
        launchItemPicker.launch(selecteIntent);
    }

    private ActivityResultLauncher<Intent> launchItemPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();

                if(data!=null){
                    int itemId = data.getIntExtra(Constants.ITEM_ID,-1);
                    int quantity = data.getIntExtra(Constants.ITEM_QUANTITY, 0);
                    String itemName = data.getStringExtra(Constants.ITEM_NAME);
                    String itemRate = data.getStringExtra(Constants.ITEM_RATE);
                    int stock = data.getIntExtra(Constants.ITEM_STOCK, 0);
                    boolean isMaintainStock = data.getBooleanExtra(Constants.ITEM_MAINTAIN_STOCK, true);
                    float rate = Float.parseFloat(itemRate);

                    PickedItem existingItem = checkItemAlreadyAdded(itemId);
                    if(existingItem == null){
                        int remainingStock = stock - quantity;
                        PickedItem item  = new PickedItem().getPickedItemFrom(itemId,itemName,rate,quantity,remainingStock,isMaintainStock);
                        addItemToList(item, orderId);
                    }else {
                        existingItem.setQuantity(quantity);
                        //refresh to reflect the change in recyclerview
                        selectedItemAdapter.notifyDataSetChanged();
                        //update quantity
                        updateSelectedItems(existingItem);
                    }
                }
            }
        }
    });

    private void updateItemCount(int count){
        tv_itemCount.setText(""+count);
    }

    private PickedItem checkItemAlreadyAdded(int itemId){

        for (int i =0;i< pickedItemList.size();i++){
            if(pickedItemList.get(i).getId() == itemId){
                return pickedItemList.get(i);
            }
        }

        return null;
    }

    private void addItemToList(PickedItem pickedItem,long _orderId){
        try{

            appExecutors.diskIO().execute(() -> {
                try {

                    OrderDetailsEntity newItem= new OrderDetailsEntity();
                    newItem.setOrderId(_orderId);  //order id
                    newItem.setItemId(pickedItem.getId());  //item id
                    newItem.setQuantity(pickedItem.getQuantity()); //set quantity
                    localDb.orderDetailsDao().insertOrderDetail(newItem);

                    pickedItemList.add(pickedItem);
                    runOnUiThread(() -> {
                        try {
                            showToast(getString(R.string.new_item_added), OrderDetailsActivity.this);
                            selectedItemAdapter.notifyItemChanged(pickedItemList.size() - 1);

                            calculateTotalAndUpdate();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private BroadcastReceiver DeleteItemUIUpdaterBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                int _itemId = intent.getIntExtra(Constants.ITEM_ID, -1);
                if (_itemId == -1) {
                    showToast(getString(R.string.invalid_item), OrderDetailsActivity.this);
                    return;
                }


                //to avoid unnecessary refresh of adapter, use flag
                boolean isRefresh = false;
                int refreshPosition = -1;

                //To remove item from recycler view
                //find the item in array
                //remove and update adapter
                for (int i = 0; i < pickedItemList.size(); i++) {
                    if (pickedItemList.get(i).getId() == _itemId) {
                        pickedItemList.remove(i);
                        refreshPosition = i;
                        isRefresh = true;
                        break;
                    }
                }


                //refresh adapter only if the flag is true.
                if (isRefresh) {
                    if (refreshPosition > -1) {
                        selectedItemAdapter.notifyItemRemoved(refreshPosition);
                    } else {
                        selectedItemAdapter.notifyDataSetChanged();
                    }
                }

                //hide progress
                progressDialog.hideProgressbar();

                calculateTotalAndUpdate();

                isDbOperationOn = false;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    /**
     * calculate total
     * */
    private void calculateTotalAndItemCount(){
        try {

            gross_amount = 0.0;
            int item_quantity = 0;
            float total = 0;
            float item_rate = 0.0f;
            int itemCount = 0;
            PickedItem item;

            for (int i=0;i<pickedItemList.size();i++){
                item = pickedItemList.get(i);
                item_quantity = item.getQuantity();
                item_rate = item.getRate();
                total = item_quantity * item_rate;
                gross_amount += total;
                itemCount+= item.getQuantity();
            }

            totalItemCount = itemCount;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * update values to ui
     * use ui thread to update
     * */
    private void updateTotals(){
        try {

           String grossAmountLabel = ""+gross_amount;
           String netAmountLabel = ""+net_amount;

           tv_grossAmount.setText(grossAmountLabel);
           tv_netAmount.setText(netAmountLabel);

           updateItemCount(totalItemCount);

           tv_top_taxAmount.setText(""+tax_amount);
           tv_top_discount.setText(""+discount_amount);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * change order to status complete
     * */
    private void checkoutOrder(){
        try {

            //close bottomsheet
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            String str_discountPercent = et_discountPercent.getText().toString();
            String str_taxPercentage = tv_top_taxPercent.getText().toString();

            double discountPercent;
            double tax_Percentage;


            if(!str_discountPercent.isEmpty()){
                discountPercent = Double.parseDouble(str_discountPercent);
            }else {
                discountPercent = 0.0;
            }

            if(!str_taxPercentage.isEmpty()) {
                tax_Percentage = Double.parseDouble(str_taxPercentage);
            }else {
                tax_Percentage = 0.0;
            }



            //gotoCheckout(orderId,gross_amount, tax_amount, discount_amount,discountPercent, customerId, customerName);
            gotoCheckout(orderId,gross_amount,tax_Percentage, tax_amount, discount_amount,discountPercent, customerId, customerName , presentShiftId);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to hide soft keyboard
     * */
    protected void hideSoftKeyboard() {
        try {
            // Hide soft-keyboard:
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoCheckout(int _orderId, double total,double taxPercent,double taxAmount, double discountAmount, double discountPercent, int _customerId, String _custName, int shiftId){
        Intent checkoutIntent = new Intent(OrderDetailsActivity.this, CheckoutActivity.class);
        checkoutIntent.putExtra(Constants.ORDER_ID, _orderId);
        checkoutIntent.putExtra(Constants.TOTAL_AMOUNT, total);
        checkoutIntent.putExtra(Constants.TAX_PERCENT, taxPercent);
        checkoutIntent.putExtra(Constants.TAX_AMOUNT, taxAmount);
        checkoutIntent.putExtra(Constants.ITEM_DISCOUNT, discountAmount);
        checkoutIntent.putExtra(Constants.DISCOUNT_PERCENT, discountPercent);
        checkoutIntent.putExtra(Constants.CUSTOMER_ID, _customerId);
        checkoutIntent.putExtra(Constants.CUSTOMER_NAME, _custName);
        checkoutIntent.putExtra(Constants.SHIFT_ID, shiftId);

        startActivity(checkoutIntent);

        finish();
    }

}