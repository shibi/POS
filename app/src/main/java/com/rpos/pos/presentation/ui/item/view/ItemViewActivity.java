package com.rpos.pos.presentation.ui.item.view;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.CategoryEntity;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;
import com.rpos.pos.data.remote.dto.items.add.AddItemMessage;
import com.rpos.pos.data.remote.dto.items.add.AddItemResponse;
import com.rpos.pos.data.remote.dto.items.edit.ItemEditMessage;
import com.rpos.pos.data.remote.dto.items.edit.ItemEditResponse;
import com.rpos.pos.data.remote.dto.items.list.BarcodeData;
import com.rpos.pos.data.remote.dto.items.list.ItemData;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.requestmodel.item.add.AddItemRequest;
import com.rpos.pos.domain.requestmodel.item.edit.ItemEditRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.add.AddItemActivity;
import com.rpos.pos.presentation.ui.item.add.CategorySpinnerAdapter;
import com.rpos.pos.presentation.ui.item.add.UOMSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ItemViewActivity extends SharedActivity {

    private LinearLayout ll_back;
    private Integer itemId;
    private AppCompatEditText et_itemName,et_itemDescription,et_itemTax,et_barcode;
    private CheckBox checkBox_maintain_stock;
    private LinearLayout ll_update;
    private AppDialogs appDialogs;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private List<CategoryItem> categoryItemsList;
    private AppCompatEditText et_stock;

    private List<UomItem> uomList;
    private List<ItemEntity> allItemList;

    private Spinner sp_uom;
    private Spinner sp_category;

    private UOMSpinnerAdapter uomSpinnerAdapter;
    private CategorySpinnerAdapter categorySpinnerAdapter;

    private ItemEntity savedItemEntity;
    private AppDialogs progressDialog;

    private ItemEditRequest itemEditRequest;

    private ItemData selectedItem;
    private String categoryId, uomId;

    @Override
    public int setUpLayout() {
        return R.layout.activity_item_view;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        //progress dialog
        progressDialog = new AppDialogs(this);
        appDialogs = new AppDialogs(this);

        ll_back = findViewById(R.id.ll_back);
        ll_update = findViewById(R.id.ll_rightMenu);
        et_stock = findViewById(R.id.et_stock);

        //spinners
        sp_uom = findViewById(R.id.sp_uom);
        sp_category = findViewById(R.id.sp_category);

        categoryItemsList = new ArrayList<>();
        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        selectedItem = null;
        categoryId = "";
        uomId = "";


        Intent data = getIntent();
        if(data!=null){
            itemId = data.getIntExtra(Constants.ITEM_ID, Constants.EMPTY_INT);
            if(itemId == -1){
                showToast(getString(R.string.invalid_item), ItemViewActivity.this);
                return;
            }
        }


        et_itemName = findViewById(R.id.et_itemName);
        et_itemDescription = findViewById(R.id.et_itemDescription);
        et_barcode = findViewById(R.id.et_barcode);
        et_itemTax = findViewById(R.id.et_itemTax);
        checkBox_maintain_stock = findViewById(R.id.cb_maintainStock);

        //back arrow click
        ll_back.setOnClickListener(view -> onBackPressed());

        ll_update.setOnClickListener(view -> onClickUpdate());


        uomList = getCoreApp().getUomList();
        if(uomList == null){
            uomList = new ArrayList<>();
        }
        uomSpinnerAdapter = new UOMSpinnerAdapter(ItemViewActivity.this,uomList);
        sp_uom.setAdapter(uomSpinnerAdapter);

        //get item details from db
        getItemDetails(itemId);


        categorySpinnerAdapter = new CategorySpinnerAdapter(ItemViewActivity.this, categoryItemsList);
        sp_category.setAdapter(categorySpinnerAdapter);


        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryId = ""+categoryItemsList.get(i).getCategoryId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                uomId = ""+uomList.get(i).getUomId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void initObservers() {

    }

    /**
     * get item details from local db
     * @param _itemId
     * */
    private void getItemDetails(int _itemId){
        try {

            List<ItemData> itemsList = getCoreApp().getAllItemsList();
            if(itemsList!=null && !itemsList.isEmpty()){
                for (int i=0;i< itemsList.size();i++){
                    if(itemsList.get(i).getItemId() == _itemId){
                        selectedItem = itemsList.get(i);
                        populateData();
                        return;
                    }
                }
            }

            /*appExecutors.diskIO().execute(() -> {
                try {

                    savedItemEntity = localDb.itemDao().getItemDetails(_itemId);
                    if(savedItemEntity!=null){
                        //update ui
                        updateUI(savedItemEntity);

                        //get the category list
                        getCategoryFromLocalDb();

                        //check pre-loaded uom list available
                        if(uomList!=null && !uomList.isEmpty()){

                            setUomSpinnerSelection(savedItemEntity.getUom());

                        }else {
                            //if no list available , load from local storage to application class
                            //get uom list
                            getUomListFromDb();
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void populateData(){
        try {

            et_itemName.setText(selectedItem.getItemName());
            et_itemDescription.setText(selectedItem.getDescription());
            et_itemTax.setText(""+selectedItem.getItemTax());
            et_stock.setText(""+selectedItem.getAvailableQty());

            boolean isMaintainStock = (selectedItem.getMaintainStock() == 1)?true: false;
            checkBox_maintain_stock.setChecked(isMaintainStock);

            List<BarcodeData> barcodeList = selectedItem.getBarcodes();
            if(barcodeList!=null && !barcodeList.isEmpty()){
                et_barcode.setText(barcodeList.get(0).getBarcode());
            }

            getCategoryAndUomList();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getCategoryAndUomList(){
        try{

            appExecutors.diskIO().execute(() -> {
                try {
                    //all category
                    final List<CategoryItem> savedCategory = localDb.categoryDao().getAllCategories();
                    final List<UomItem> savedUomsList = localDb.uomDao().getAllUnitsOfMessurements();

                    if(savedCategory == null || savedCategory.isEmpty()){
                        showToastInUiThread("No category available");
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            categoryItemsList.clear();
                            categoryItemsList.addAll(savedCategory);
                            categorySpinnerAdapter.notifyDataSetChanged();

                            //select the category in spinner
                            setCategorySpinnerSelection(selectedItem.getCategory());
                        }
                    });


                    if(savedUomsList == null || savedUomsList.isEmpty()){
                        showToastInUiThread("No uom available");
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uomList.clear();
                            uomList.addAll(savedUomsList);
                            uomSpinnerAdapter.notifyDataSetChanged();

                            //select the uom in spinner
                            setUomSpinnerSelection(selectedItem.getUom());
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

    private void showToastInUiThread(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(msg, ItemViewActivity.this);
            }
        });
    }

    private void processCategoryList(List<CategoryEntity> savedCategory){
        try {

            //clear previous items
            categoryItemsList.clear();

            CategoryItem categoryItem;
            CategoryEntity categorySaved;
            for (int i =0;i< savedCategory.size();i++){
                categoryItem = new CategoryItem();
                categorySaved = savedCategory.get(i);

                categoryItem.setCategoryId(Integer.parseInt(categorySaved.getCategoryId()));
                categoryItem.setCategory(categorySaved.getCategoryName());
                categoryItemsList.add(categoryItem);
            }

            runOnUiThread(() -> {
                try {

                    categorySpinnerAdapter.notifyDataSetChanged();



                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setCategorySpinnerSelection(int categoryId){
        try {

            for (int i = 0; i < categoryItemsList.size(); i++) {
                if (categoryItemsList.get(i).getCategoryId() == categoryId) {
                    sp_category.setSelection(i, true);
                    break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to set the spinner selection in specific id
     * */
    private void setUomSpinnerSelection(int uomId){
        try{
            for (int i=0;i< uomList.size();i++){
                if(uomList.get(i).getUomId() == uomId){
                    sp_uom.setSelection(i, true);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * update values to ui
     * */
    private void updateUI(ItemEntity item){
        try {

            runOnUiThread(() -> {

                et_itemName.setText(item.getItemName());
                et_itemDescription.setText(item.getDescription());
                et_itemTax.setText(""+item.getItemTax());
                checkBox_maintain_stock.setChecked((item.getMaintainStock() !=0));
                et_stock.setText(""+item.getAvailableQty());

                if(item.getBarcodes()!=null && item.getBarcodes().size() >0) {
                    String barcode = item.getBarcodes().get(0);
                    if(barcode.equals(""+Constants.EMPTY_INT)){
                        et_barcode.setText(getString(R.string.barcode_empty));
                    }else {
                        et_barcode.setText(barcode);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * update new values
     * */
    private void onClickUpdate(){
        try {

            String itemName = et_itemName.getText().toString();
            String item_description = et_itemDescription.getText().toString();
            String itemTax = et_itemTax.getText().toString();
            String item_barcode = et_barcode.getText().toString();
            boolean isMaintainStock = checkBox_maintain_stock.isChecked();

            if(itemName.isEmpty()){
                showFieldError(et_itemName, getString(R.string.item_name_required));
                return;
            }


            float fl_rate = 0.0f;   //N.B :- ZERO AT THE BEGINNING. RATE FOR EACH ITEMS ARE UPDATED FROM ITEM PRICE SCREEN
            float fl_tax;
            if(itemTax.isEmpty()){
                fl_tax = 0.0f;
            }else {
                fl_tax = Float.parseFloat(itemTax);
            }

            Integer stock = 0;  // N.B :- NO NEED TO PROVIDE STOCK HERE. STOCK WILL BE AUTOMATICALLY UPDATED FROM PURCHASE CHECKOUT



            if(categoryId.isEmpty()){
                String msg = getString(R.string.invalid_category);
                showToast(msg, ItemViewActivity.this);
                return;
            }

            if(uomId.isEmpty()){
                String msg = getString(R.string.invalid_uom);
                showToast(msg, ItemViewActivity.this);
                return;
            }

            /*

                    "item_group":"Products",






                    */


            String userId = SharedPrefHelper.getInstance(ItemViewActivity.this).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid), ItemViewActivity.this);
                return;
            }

            itemEditRequest = new ItemEditRequest();

            itemEditRequest.setItemId(String.valueOf(itemId));
            itemEditRequest.setItemName(itemName);
            itemEditRequest.setDescription(item_description);
            itemEditRequest.setUserId(userId);
            itemEditRequest.setCategoryId(categoryId);
            itemEditRequest.setUom(uomId);
            itemEditRequest.setItemGroup("Products");
            itemEditRequest.setBarcode(item_barcode);
            itemEditRequest.setItemTax(fl_tax);
            itemEditRequest.setRate(fl_rate);
            itemEditRequest.setStockQty(stock);
            itemEditRequest.setMaintainStock(isMaintainStock?1:0);


            updateItemDetails();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateItemDetails(){
        try {

            showProgress();

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<ItemEditResponse> call = apiService.updateItem(itemEditRequest);
            call.enqueue(new Callback<ItemEditResponse>() {
                @Override
                public void onResponse(Call<ItemEditResponse> call, Response<ItemEditResponse> response) {
                    Log.e("-----------","res"+response.isSuccessful());
                    hideProgress();

                    if(response.isSuccessful()){
                        ItemEditResponse itemEditResponse = response.body();
                        if(itemEditResponse!=null && itemEditResponse.getMessage()!=null){
                            ItemEditMessage itemEditMessage = itemEditResponse.getMessage();
                            if(itemEditMessage.getSuccess()){
                                showSuccess();
                                return;
                            }else {
                                showToast(itemEditMessage.getMessage(), ItemViewActivity.this);
                            }
                        }
                    }

                    showToast(getString(R.string.item_add_failed), ItemViewActivity.this);
                }

                @Override
                public void onFailure(Call<ItemEditResponse> call, Throwable t) {
                    showToast(getString(R.string.please_check_internet), ItemViewActivity.this);
                    hideProgress();
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showFieldError(AppCompatEditText inputfield, String msg){
        inputfield.setError(msg);
        inputfield.requestFocus();
    }

    private void saveItemLocally(ItemEntity newItem){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    String newItemName = newItem.getItemName().trim();
                    allItemList = localDb.itemDao().getAllItems();
                    if(allItemList!=null) {
                        //loop through all items and check whether name exists or not ?
                        for (ItemEntity item : allItemList) {
                            if (item.getItemName().trim().equals(newItemName) && (item.getItemId() != newItem.getItemId())) {
                                //use ui thread to update ui
                                runOnUiThread(() -> {
                                    hideProgress();
                                    appDialogs.showCommonAlertDialog(getString(R.string.item_name_exists), null);
                                });
                                return;
                            }
                        }


                        List<String> barcodeList = newItem.getBarcodes();
                        if(barcodeList!=null && barcodeList.size()>0){
                            String newBarcode = barcodeList.get(0);
                            if(newBarcode.equals(Constants.EMPTY))
                            if(!checkBarcodeAvailable(newItem.getItemId(), newBarcode)){
                                runOnUiThread(()->{
                                    et_barcode.setError(getString(R.string.barcode_used));
                                    et_barcode.requestFocus();
                                    hideProgress();
                                });
                                return;
                            }
                        }
                    }

                    localDb.itemDao().insertItem(newItem);

                    //use ui thread to update ui
                    runOnUiThread(() -> {
                        hideProgress();
                        showSuccess();
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To check barcode is already used.
     * */
    private boolean checkBarcodeAvailable(int itemId, String barcode){
        try {

            String itemBarcode;
            for(ItemEntity item:allItemList){
                if(item.getItemId() != itemId) {
                    if (item.getBarcodes() != null && item.getBarcodes().size() > 0) {
                        itemBarcode = item.getBarcodes().get(0);
                        if (!itemBarcode.isEmpty() && itemBarcode.equals(barcode)) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void showSuccess(){
        try {

            AppDialogs appDialogs = new AppDialogs(ItemViewActivity.this);
            appDialogs.showCommonSuccessDialog(getString(R.string.item_update_success), view -> {
                try{

                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }
}