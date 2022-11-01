package com.rpos.pos.presentation.ui.price_variations.itemprice.add;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemPriceEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.select.ItemSelectActivity;
import com.rpos.pos.presentation.ui.price_variations.pricelist.select.SelectPriceListActivity;

public class AddItemPriceActivity extends SharedActivity {

    private LinearLayout ll_back, ll_save;
    private AppCompatButton btn_pick_item, btn_pick_priceList;
    private AppCompatTextView tv_itemId, tv_itemName, tv_itemDesc, tv_itemUom, tv_currency, tv_type;
    private AppCompatEditText et_itemRate, et_batchNo , et_selectedItem, et_selectedPLName;


    private int priceListId, priceListType;
    private String priceListName;
    private int itemId, item_uomId;
    private String itemName, uomName, itemDescription, currency;


    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;
    private AppDialogs appDialogs;

    private ItemPriceEntity itemPriceEntity;
    private String STRING_SEPARATOR = " : ";

    @Override
    public int setUpLayout() {
        return R.layout.activity_add_item_price;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        //progress dialog
        progressDialog = new AppDialogs(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        appDialogs = new AppDialogs(this);

        ll_back = findViewById(R.id.ll_back);
        btn_pick_item = findViewById(R.id.btn_select_item);
        btn_pick_priceList = findViewById(R.id.btn_select_pricelist);
        ll_save = findViewById(R.id.ll_rightMenu);

        et_itemRate = findViewById(R.id.et_rate);
        et_selectedItem = findViewById(R.id.et_itemName);
        et_selectedPLName = findViewById(R.id.et_priceListName);
        et_batchNo = findViewById(R.id.et_batchNo);


        tv_itemId = findViewById(R.id.tv_itemId);
        tv_itemName = findViewById(R.id.tv_itemName);
        tv_itemDesc = findViewById(R.id.tv_itemDesc);
        tv_itemUom = findViewById(R.id.tv_itemUom);
        tv_currency = findViewById(R.id.tv_currency);
        tv_type = findViewById(R.id.tv_type);

        priceListId = Constants.EMPTY_INT;;
        priceListType = Constants.EMPTY_INT;;
        priceListName = itemName = itemDescription = "";
        itemId = Constants.EMPTY_INT;


        ll_save.setOnClickListener(view -> {

            progressDialog.showProgressBar();
            if(validate()){
                saveItemPrice();
            }else {
                progressDialog.hideProgressbar();
            }

        });

        btn_pick_item.setOnClickListener(view -> selectItem());

        //choose price list
        btn_pick_priceList.setOnClickListener(view -> selectPriceList());

        //back
        ll_back.setOnClickListener(view -> onBackPressed());
    }

    @Override
    public void initObservers() {

    }

    private void selectPriceList(){
        Intent selecteIntent = new Intent(AddItemPriceActivity.this, SelectPriceListActivity.class);
        launchPriceListPicker.launch(selecteIntent);
    }

    private void selectItem(){
        Intent selecteIntent = new Intent(AddItemPriceActivity.this, ItemSelectActivity.class);
        selecteIntent.putExtra(Constants.ITEM_REQUESTED_PARENT, Constants.PARENT_PRICE_LIST); // Mandatory field
        selecteIntent.putExtra(Constants.ITEM_SELECTION_TYPE, Constants.ITEM_SELECTION_SINGLE_PICK); // Mandatory field
        launchItemPicker.launch(selecteIntent);
    }


    private ActivityResultLauncher<Intent> launchItemPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();

                if(data!=null){
                    itemId = data.getIntExtra(Constants.ITEM_ID,-1);
                    itemName = data.getStringExtra(Constants.ITEM_NAME);
                    itemDescription = data.getStringExtra(Constants.ITEM_DESC);
                    String str_uomId = data.getStringExtra(Constants.ITEM_UOM_ID);
                    item_uomId = Integer.parseInt(str_uomId);

                    et_selectedItem.setText(itemName);
                    tv_itemId.setText(STRING_SEPARATOR + itemId);
                    tv_itemName.setText(STRING_SEPARATOR + itemName);
                    tv_itemUom.setText(item_uomId + STRING_SEPARATOR);
                    tv_itemDesc.setText(STRING_SEPARATOR + itemDescription);
                    tv_currency.setText("default" + STRING_SEPARATOR);
                }
            }
        }
    });

    private ActivityResultLauncher<Intent> launchPriceListPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();
                if(data!=null){

                    priceListId = data.getIntExtra(Constants.PRICELIST_ID, Constants.EMPTY_INT);
                    priceListName = data.getStringExtra(Constants.PRICELIST_NAME);
                    priceListType = data.getIntExtra(Constants.PRICELIST_TYPE, Constants.EMPTY_INT);

                    et_selectedPLName.setText(priceListName);
                    String type =  (priceListType == Constants.BUYING)?getString(R.string.buying):getString(R.string.selling);
                    tv_type.setText(type + STRING_SEPARATOR);
                }
            }
        }
    });

    /**
     * To validate user entry fields
     * */
    private boolean validate(){
        try {

            String itemCode = "no-data";
            String str_itemRate = et_itemRate.getText().toString();
            String itemBatchNo = et_batchNo.getText().toString();
            float rate;

            /*if(itemCode.isEmpty()){
                et_itemCode.setError(getString(R.string.enter_valid_data));
                return false;
            }*/

            if(str_itemRate.isEmpty()){
                et_itemRate.setError(getString(R.string.enter_amount));
                return false;
            }

            if(priceListId == Constants.EMPTY_INT){
                showToast(getString(R.string.select_price_list));
                return false;
            }

            rate = Float.parseFloat(str_itemRate);
            if(rate <= 0){
                showToast(getString(R.string.enter_amount));
                return false;
            }

            if(itemId == Constants.EMPTY_INT){
                showToast(getString(R.string.select_item));
                return false;
            }

            itemPriceEntity = new ItemPriceEntity();
            itemPriceEntity.setItemCode(itemCode); //
            itemPriceEntity.setItemId(itemId); //
            itemPriceEntity.setItemName(itemName); //
            itemPriceEntity.setPriceListId(priceListId);
            itemPriceEntity.setPriceListName(priceListName);
            itemPriceEntity.setBatchNo(itemBatchNo); //
            itemPriceEntity.setRate(rate);//
            itemPriceEntity.setPriceListType(priceListType);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * to show toast message
     * */
    private void showToast(String msg){
        showToast(msg, AddItemPriceActivity.this);
    }

    /**
     * save item price
     * */
    private void saveItemPrice(){
        try {

            appExecutors.diskIO().execute(() -> {
                try {

                    //check whether item already exists
                    if(checkWhetherItemAlreadyExists(priceListId, itemId)){
                        //if exists , alert user
                        alertUserAboutItemExists();
                        return; //stop proceeding
                    }

                    //insert item
                    localDb.itemPriceListDao().insertItem(itemPriceEntity);

                    //Use ui thread to Show insertion success dialog
                    runOnUiThread(() -> {

                        progressDialog.hideProgressbar();

                        AppDialogs appDialogs = new AppDialogs(AddItemPriceActivity.this);
                        appDialogs.showCommonSuccessDialog(getString(R.string.add_item_success), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                finish();
                            }
                        });
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
     * To check whether item already exist
     * @param _priceListId
     * @param _itemId
     * */
    private boolean checkWhetherItemAlreadyExists(int _priceListId, int _itemId) throws Exception{
        try {
            //check whether item already exists
            ItemPriceEntity existingItem = localDb.itemPriceListDao().findPriceForItemWithPriceListId(_priceListId, _itemId);
            return (existingItem != null);
        }catch (Exception e){
            throw e;
        }
    }


    /**
     * to show alert to user
     *  item is already exist in the price list
     * */
    private void alertUserAboutItemExists() throws Exception{
        try {
            runOnUiThread(() -> {
                try {

                    String message = getString(R.string.item_exists) + "\n" + itemName + "(" + priceListName + ")";
                    appDialogs.showCommonAlertDialog(message, view -> {
                        try {
                            progressDialog.hideProgressbar();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                } catch (Exception e) {
                    throw e;
                }
            });
        }catch (Exception e){
            throw e;
        }
    }
}