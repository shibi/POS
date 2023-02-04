package com.rpos.pos.presentation.ui.item.add;


import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import androidx.appcompat.widget.AppCompatEditText;
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
import com.rpos.pos.data.remote.dto.category.list.GetCategoryResponse;
import com.rpos.pos.data.remote.dto.items.add.AddItemMessage;
import com.rpos.pos.data.remote.dto.items.add.AddItemResponse;
import com.rpos.pos.data.remote.dto.uom.list.GetUomListResponse;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.requestmodel.item.add.AddItemRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.list.ItemActivity;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends SharedActivity {

    private AppCompatEditText et_itemName,et_item_desc,et_barcode,et_itemTax;
    private AppCompatEditText et_itemRate, et_stock;
    private CheckBox checkBox_maintainStock;
    private LinearLayout ll_back;
    private LinearLayout ll_submit;

    private List<UomItem> uomList;
    private List<CategoryItem> categoryItemsList;
    private List<ItemEntity> allItemsArray;

    private Spinner sp_uom;
    private Spinner sp_category;

    private UOMSpinnerAdapter uomSpinnerAdapter;
    private CategorySpinnerAdapter categorySpinnerAdapter;

    private AppDialogs progressDialog;

    private String str_uom_id,str_category_id, uomName, categoryName;
    private boolean isSaveClicked;

    private AppExecutors appExecutors;
    private AppDatabase localDb;

    private ItemEntity itemLocalInsertObj;

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_item;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {
        //progress dialog
        progressDialog = new AppDialogs(this);

        et_itemName = findViewById(R.id.et_itemName);
        et_item_desc = findViewById(R.id.et_item_desc);
        et_barcode = findViewById(R.id.et_barcode);
        et_itemTax = findViewById(R.id.et_itemtax);
        et_itemRate = findViewById(R.id.et_rate);
        et_stock = findViewById(R.id.et_stock);
        checkBox_maintainStock = findViewById(R.id.cb_maintainStock);
        ll_back = findViewById(R.id.ll_back);
        ll_submit = findViewById(R.id.ll_add_item);

        str_uom_id = "";
        str_category_id = "";
        uomName = "";
        categoryName ="";
        isSaveClicked = false;


        //list
        categoryItemsList = new ArrayList<>();
        allItemsArray = new ArrayList<>();

        //spinners
        sp_uom = findViewById(R.id.sp_uom);
        sp_category = findViewById(R.id.sp_category);

        //loading pre-fetched uom list
        uomList = getCoreApp().getUomList();
        //check list is valid
        if(uomList == null){
            //initialize list
            uomList = new ArrayList<>();
            //get the uom items from local db
            getUomListFromDb();
        }
        uomSpinnerAdapter = new UOMSpinnerAdapter(AddItemActivity.this,uomList);
        sp_uom.setAdapter(uomSpinnerAdapter);

        categorySpinnerAdapter = new CategorySpinnerAdapter(AddItemActivity.this, categoryItemsList);
        sp_category.setAdapter(categorySpinnerAdapter);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        //get the category list
        //getCategoryListApi();
        getCategoryFromLocalDb();

        //on click
        ll_submit.setOnClickListener(view -> {
            try {

                onClickSave();

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        sp_uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_uom_id = uomList.get(i).getUomId();
                uomName = uomList.get(i).getUomName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                str_category_id = categoryItemsList.get(i).getCategoryId();
                categoryName = categoryItemsList.get(i).getCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //back
        ll_back.setOnClickListener(view -> goBackToItemList());

    }

    @Override
    public void initObservers() {

    }

    /**
     * to validate and save new item
     * */
    private void onClickSave(){
        try {

            //flag to check status
            isSaveClicked = true;
            //progress showing
            showProgress();
            //method to validate user inputs
            if(validate()){
                //to save item locally
                //saveItemLocally(itemLocalInsertObj);
                String barcode = "";
                if(itemLocalInsertObj.getBarcodes()!=null && itemLocalInsertObj.getBarcodes().size()>0){
                    barcode = itemLocalInsertObj.getBarcodes().get(0);
                }

                //item save api call
                addNewItem(itemLocalInsertObj.getItemName(),
                        itemLocalInsertObj.getDescription(),
                        str_uom_id,
                        str_category_id,
                        itemLocalInsertObj.getRate(),
                        itemLocalInsertObj.getAvailableQty(),
                        barcode,itemLocalInsertObj.getItemTax(),
                        (itemLocalInsertObj.getMaintainStock()==1) );

            }else {
                hideProgress();
                isSaveClicked = false;
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * validate fields
     * */
    private boolean validate(){
        try {

            String itemName = et_itemName.getText().toString().trim();
            String item_description = et_item_desc.getText().toString();
            String item_barcode = et_barcode.getText().toString();
            String itemTax = et_itemTax.getText().toString();
            String itemRate = et_itemRate.getText().toString();
            String itemStock = et_stock.getText().toString();

            boolean isMaintainStock = checkBox_maintainStock.isChecked();

            if(itemName.isEmpty()){
                showFieldError(et_itemName, getString(R.string.item_name_required));
                return false;
            }

            //REMOVED
            /*if(item_description.isEmpty()){
                showFieldError(et_item_desc, getString(R.string.description_required));
                return;
            }*/

            if(itemRate.isEmpty() || itemRate.equals("0") || itemRate.equals("0.0")){
                showFieldError(et_itemRate, getString(R.string.rate_required));
                return false;
            }

            if(itemStock.isEmpty()){
                showFieldError(et_stock, getString(R.string.enter_stock));
                return false;
            }

            if(str_uom_id.isEmpty()){
                showToast(getString(R.string.select_uom));
                return false;
            }

            if(str_category_id.isEmpty()){
                showToast(getString(R.string.select_category));
                return false;
            }

            if(item_barcode.isEmpty()){
                showToast(getString(R.string.barcode_empty));
                return false;
            }

            float fl_rate = Float.parseFloat(itemRate);
            float fl_tax;
            if(itemTax.isEmpty()){
                fl_tax = 0.0f;
            }else {
                fl_tax = Float.parseFloat(itemTax);
            }
            int stock = Integer.parseInt(itemStock);

            List<String> barcodesList = new ArrayList<>();
            barcodesList.add(item_barcode);


            itemLocalInsertObj = new ItemEntity();
            itemLocalInsertObj.setItemName(itemName.trim());
            itemLocalInsertObj.setDescription(item_description);
            itemLocalInsertObj.setUom(str_uom_id);
            itemLocalInsertObj.setUomName(uomName);
            itemLocalInsertObj.setCategory(str_category_id);
            itemLocalInsertObj.setRate(fl_rate);
            itemLocalInsertObj.setAvailableQty(stock);
            itemLocalInsertObj.setItemTax(fl_tax);
            itemLocalInsertObj.setBarcodes(barcodesList);
            itemLocalInsertObj.setMaintainStock(isMaintainStock?1:0);

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void showFieldError(AppCompatEditText inputfield, String msg){
        inputfield.setError(msg);
        inputfield.requestFocus();
    }

    /**
     * Get saved category from local db
     * */
    private void getCategoryFromLocalDb(){
        try{

            appExecutors.diskIO().execute(() -> {
                try{

                    //all items
                    allItemsArray = localDb.itemDao().getAllItems();
                    //all category
                    List<CategoryEntity> savedCategory = localDb.categoryDao().getAllCategory();

                    if(savedCategory == null || savedCategory.isEmpty()){
                        showToast(getString(R.string.addcategoryfirst));

                        runOnUiThread(() -> {
                            AppDialogs appDialogs = new AppDialogs(AddItemActivity.this);
                            appDialogs.showCommonAlertDialog("Add Category first and proceed", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                        });

                    }else {
                        processCategoryList(savedCategory);
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
     * get uom list from local db
     * */
    private void getUomListFromDb(){
        try {

            AppExecutors uomExecutors = new AppExecutors();

            uomExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        List<UomItem> savedUomsList = localDb.uomDao().getAllUnitsOfMessurements();
                        if(savedUomsList!=null && savedUomsList.size()>0) {
                            uomList.clear();
                            uomList.addAll(savedUomsList);

                            //setting list to application class to easy fetch
                            getCoreApp().setUomList(uomList);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uomSpinnerAdapter.notifyDataSetChanged();
                                }
                            });
                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try{
                                        //if there is no uom data fetch it from API
                                        //getAllUOMList(); //not using now
                                        showToast(getString(R.string.list_empty));
                                        String alertMessage = getString(R.string.add_uom_and_procedd);
                                        AppDialogs appDialogs = new AppDialogs(AddItemActivity.this);
                                        appDialogs.showCommonSingleAlertDialog(getString(R.string.alert),alertMessage, view->finish());

                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
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

    private void processCategoryList(List<CategoryEntity> savedCategory){
        try {

            //clear previous items
            categoryItemsList.clear();

            CategoryItem categoryItem;
            CategoryEntity categorySaved;
            for (int i =0;i< savedCategory.size();i++){
                categoryItem = new CategoryItem();
                categorySaved = savedCategory.get(i);

                categoryItem.setCategoryId(categorySaved.getCategoryId());
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





    /**
     * add new item
     * */
    private void addNewItem(String itemName,String item_description,String uomId, String categoryId,float item_rate,int item_availableStock,String item_barcode,float itemTax,boolean stockMaintain){
        try {

            String userId = SharedPrefHelper.getInstance(this).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid));
                return;
            }


            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            AddItemRequest request = new AddItemRequest();
            request.setItemName(itemName);
            request.setDescription(item_description);
            request.setUserId(userId);
            request.setUom(uomId);
            request.setCategoryId(categoryId);
            request.setRate(item_rate);
            request.setBarcode(item_barcode);
            request.setItemTax(itemTax);
            request.setMaintainStock(stockMaintain);
            request.setItemGroup("Products");

            Call<AddItemResponse> call = apiService.addItem(request);
            call.enqueue(new Callback<AddItemResponse>() {
                @Override
                public void onResponse(Call<AddItemResponse> call, Response<AddItemResponse> response) {
                    Log.e("-----------","res"+response.isSuccessful());
                    hideProgress();
                    if(response.isSuccessful()){

                        AddItemResponse addItemResp = response.body();
                        if(addItemResp!=null){
                            AddItemMessage add_message = addItemResp.getMessage();
                            if(add_message!=null){
                                if(add_message.getSuccess()){

                                    //show success message
                                    AppDialogs appDialogs = new AppDialogs(AddItemActivity.this);
                                    appDialogs.showCommonSuccessDialog(getString(R.string.add_item_success), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    });

                                } else {
                                    showToast(add_message.getMessage());
                                }
                                return;
                            }
                        }
                    }

                    showToast(getString(R.string.item_add_failed));
                }

                @Override
                public void onFailure(Call<AddItemResponse> call, Throwable t) {
                    showToast(getString(R.string.please_check_internet));
                    hideProgress();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void showSuccess(){
        try {

            AppDialogs appDialogs = new AppDialogs(AddItemActivity.this);
            appDialogs.showCommonSuccessDialog(getString(R.string.add_item_success), view -> {
                try{
                    //finish();
                    goBackToItemList();

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void goBackToItemList(){

        Intent itemListIntent = new Intent(AddItemActivity.this, ItemActivity.class);
        startActivity(itemListIntent);
        finish();

    }

    /**
     * show toast
     * */
    private void showToast(String msg){
        showToast(msg,this);
    }

    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }


    /**
     * To get all uom values
     * */
    private void getAllUOMList(){
        try {

            ApiService apiService = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<GetUomListResponse> call = apiService.getUOMList();
            call.enqueue(new Callback<GetUomListResponse>() {
                @Override
                public void onResponse(Call<GetUomListResponse> call, Response<GetUomListResponse> response) {
                    try {

                        if(response.isSuccessful()){

                            GetUomListResponse getUomListResponse = response.body();
                            if(getUomListResponse!=null){
                                List<UomItem> dataList = getUomListResponse.getMessage();
                                if(dataList!=null && dataList.size()>0){
                                    uomList.clear();
                                    uomList.addAll(dataList);
                                    uomSpinnerAdapter.notifyDataSetChanged();

                                    AppExecutors uomInsertExecutors = new AppExecutors();
                                    uomInsertExecutors.diskIO().execute(() -> {
                                        try {
                                            for (int i=0;i<dataList.size();i++){
                                                dataList.get(i).setUomId(""+i);
                                            }
                                            localDb.uomDao().insertUom(dataList);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    });

                                }else {
                                    showToast(getString(R.string.list_empty));
                                }
                            }else {
                                showToast(getString(R.string.no_response));
                            }

                        }else {
                            showToast(getString(R.string.no_response));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<GetUomListResponse> call, Throwable t) {
                    showToast(getString(R.string.error_occurred));
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * to get category list
     * */
    private void getCategoryListApi(){
        try {

            showProgress();

            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            Call<GetCategoryResponse> call = api.getCategoryList();
            call.enqueue(new Callback<GetCategoryResponse>() {
                @Override
                public void onResponse(Call<GetCategoryResponse> call, Response<GetCategoryResponse> response) {
                    Log.e("-----------","APO"+response.isSuccessful());
                    hideProgress();
                    try {

                        if(response.isSuccessful()){
                            GetCategoryResponse getCategoryResponse = response.body();
                            if(getCategoryResponse!=null){
                                List<CategoryItem> categ_list = getCategoryResponse.getMessage();
                                if(categ_list!=null && categ_list.size()>0){

                                    categoryItemsList.addAll(categ_list);
                                    categorySpinnerAdapter.notifyDataSetChanged();

                                }else {
                                    showToast(getString(R.string.list_empty));
                                }
                            }else {
                                showToast(getString(R.string.list_empty));
                            }
                        }else {
                            showToast(getString(R.string.list_empty));
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<GetCategoryResponse> call, Throwable t) {
                    Log.e("-----------","failed");
                    hideProgress();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void saveItemLocally(ItemEntity newItem){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    localDb.itemDao().insertItem(newItem);

                    //use ui thread to update ui
                    runOnUiThread(() -> {
                        ll_submit.setEnabled(false);
                        hideProgress();
                        showSuccess();
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){

        }
    }
    /**
     * To check barcode is already used.
     * */
    private boolean checkBarcodeAvailable(String barcode){
        try {

            String itemBarcode;
            for(ItemEntity item:allItemsArray){
                if(item.getBarcodes()!=null && item.getBarcodes().size()>0){
                    itemBarcode = item.getBarcodes().get(0);
                    if(!itemBarcode.isEmpty() && itemBarcode.equals(barcode)){
                        return false;
                    }
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    private void getItemsList(){

    }

}
