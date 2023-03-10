package com.rpos.pos.presentation.ui.item.view;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.content.Intent;
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
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.add.AddItemActivity;
import com.rpos.pos.presentation.ui.item.add.CategorySpinnerAdapter;
import com.rpos.pos.presentation.ui.item.add.UOMSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;


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


        Intent data = getIntent();
        if(data!=null){
            String str_itemId = data.getStringExtra(Constants.ITEM_ID);
            if(str_itemId!=null && !str_itemId.isEmpty()){
                itemId = Integer.parseInt(str_itemId);
            }else {
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
        getItemDetailsFromLocalDb(itemId);




        categorySpinnerAdapter = new CategorySpinnerAdapter(ItemViewActivity.this, categoryItemsList);
        sp_category.setAdapter(categorySpinnerAdapter);


        sp_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(savedItemEntity!=null) {
                    savedItemEntity.setCategory(""+categoryItemsList.get(i).getCategoryId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_uom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(savedItemEntity!=null) {
                    savedItemEntity.setUom(uomList.get(i).getUomId());
                }
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
    private void getItemDetailsFromLocalDb(int _itemId){
        try {

            appExecutors.diskIO().execute(() -> {
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
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Get saved category from local db
     * */
    private void getCategoryFromLocalDb(){
        try{

            //all category
            List<CategoryEntity> savedCategory = localDb.categoryDao().getAllCategory();

            if(savedCategory == null || savedCategory.isEmpty()){
                showToast("Category not loaded",ItemViewActivity.this);
            }else {
                processCategoryList(savedCategory);
            }

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

                categoryItem.setCategoryId(Integer.parseInt(categorySaved.getCategoryId()));
                categoryItem.setCategory(categorySaved.getCategoryName());
                categoryItemsList.add(categoryItem);
            }

            runOnUiThread(() -> {
                try {

                    categorySpinnerAdapter.notifyDataSetChanged();

                    for (int i=0; i< categoryItemsList.size(); i++){
                        if(categoryItemsList.get(i).getCategoryId().equals(savedItemEntity.getCategory())){
                            sp_category.setSelection(i, true);
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

    /**
     * get uom list
     * */
    private void getUomListFromDb(){
        try {

            List<UomItem> savedUomsList = localDb.uomDao().getAllUnitsOfMessurements();
            if(savedUomsList!=null && savedUomsList.size()>0) {
                uomList.clear();
                uomList.addAll(savedUomsList);
                //set the loaded list to application class
                getCoreApp().setUomList(uomList);

                runOnUiThread(() -> {
                    try {

                        uomSpinnerAdapter.notifyDataSetChanged();
                        setUomSpinnerSelection(savedItemEntity.getUom());

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            showToast(getString(R.string.list_empty), ItemViewActivity.this);
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

    /**
     * to set the spinner selection in specific id
     * */
    private void setUomSpinnerSelection(String uomId){
        try{
            for (int i=0;i< uomList.size();i++){
                if(uomList.get(i).getUomId().equals(uomId)){
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


            savedItemEntity.setItemName(itemName);
            savedItemEntity.setDescription(item_description);
            savedItemEntity.setRate(fl_rate);
            savedItemEntity.setAvailableQty(stock);
            savedItemEntity.setItemTax(fl_tax);

            if(!item_barcode.isEmpty()) {
                if(!item_barcode.equals(getString(R.string.barcode_empty))){
                    List<String> barcodesList = new ArrayList<>();
                    barcodesList.add(item_barcode);
                    savedItemEntity.setBarcodes(barcodesList);
                }
            }
            savedItemEntity.setMaintainStock(isMaintainStock?1:0);

            showProgress();

            saveItemLocally(savedItemEntity);


            //addNewItem(itemName,item_description,str_uom_id,str_category_id,fl_rate,stock,item_barcode,fl_tax,isMaintainStock);

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