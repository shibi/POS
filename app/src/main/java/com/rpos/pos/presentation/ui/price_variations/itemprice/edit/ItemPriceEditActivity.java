package com.rpos.pos.presentation.ui.price_variations.itemprice.edit;

import android.content.Intent;
import android.util.Log;
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

public class ItemPriceEditActivity extends SharedActivity {

    private LinearLayout ll_back, ll_save;
    private AppCompatButton btn_pick_item, btn_pick_priceList;
    private AppCompatTextView tv_itemId, tv_itemName, tv_itemDesc, tv_itemUom, tv_currency, tv_type;
    private AppCompatEditText et_itemRate, et_batchNo , et_selectedItem, et_selectedPLName;

    private int priceListId, priceListType;
    private String priceListName;
    private int itemId, item_uomId;
    private String itemName, uomName, itemDescription, currency;
    private int itemPriceId;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;

    private ItemPriceEntity itemPriceEntity;
    private String STRING_SEPARATOR = " : ";




    @Override
    public int setUpLayout() {
        return R.layout.activity_item_price_edit;
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


        Intent data = getIntent();
        if(data!=null){
            itemPriceId = data.getIntExtra(Constants.ITEM_PRICE_LIST_ID, Constants.EMPTY_INT);
        }else {
            itemPriceId = Constants.EMPTY_INT;
        }

        priceListId = Constants.EMPTY_INT;;
        priceListType = Constants.EMPTY_INT;;
        priceListName = itemName = itemDescription = "";
        itemId = Constants.EMPTY_INT;


        ll_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.showProgressBar();
                if(validate()){
                    saveItemPrice();
                }else {
                    progressDialog.hideProgressbar();
                }
            }
        });

        btn_pick_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem();
            }
        });

        //choose price list
        btn_pick_priceList.setOnClickListener(view -> selectPriceList());

        //back
        ll_back.setOnClickListener(view -> onBackPressed());

        //get item price details
        getItemPriceDetail();
    }

    @Override
    public void initObservers() {

    }


    private void getItemPriceDetail(){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        itemPriceEntity = localDb.itemPriceListDao().getItemPriceWithId(itemPriceId);
                        if(itemPriceEntity!=null){
                            Log.e("------------",">>"+itemPriceEntity.getBatchNo());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    populateData();
                                }
                            });

                        }else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showToast(getString(R.string.details_load_failed));
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

    private void selectPriceList(){
        Intent selecteIntent = new Intent(ItemPriceEditActivity.this, SelectPriceListActivity.class);
        launchPriceListPicker.launch(selecteIntent);
    }

    private void selectItem(){
        Intent selecteIntent = new Intent(ItemPriceEditActivity.this, ItemSelectActivity.class);
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
                    Log.e("------------","asdfasdfasdfasd");
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

    private void populateData(){
        try {

            et_itemRate.setText(""+itemPriceEntity.getRate());
            et_selectedPLName.setText(itemPriceEntity.getPriceListName());
            et_selectedItem.setText(itemPriceEntity.getItemName());
            et_batchNo.setText(""+itemPriceEntity.getBatchNo());

            priceListId = itemPriceEntity.getPriceListId();
            priceListType = itemPriceEntity.getPriceListType();
            priceListName = itemPriceEntity.getPriceListName();
            itemId = itemPriceEntity.getItemId();
            item_uomId = itemPriceEntity.getItemUomId();
            itemName = itemPriceEntity.getItemName();
            itemDescription = " none";


            tv_itemId.setText(STRING_SEPARATOR + itemId);
            tv_itemName.setText(STRING_SEPARATOR + itemName);
            tv_itemUom.setText(item_uomId + STRING_SEPARATOR);
            tv_itemDesc.setText(STRING_SEPARATOR + itemDescription);
            tv_currency.setText("default" + STRING_SEPARATOR);

            String type =  (priceListType == Constants.BUYING)?getString(R.string.buying):getString(R.string.selling);
            tv_type.setText(type + STRING_SEPARATOR);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

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

            itemPriceEntity.setItemCode(itemCode); //
            itemPriceEntity.setItemId(itemId); //
            itemPriceEntity.setItemName(itemName); //
            itemPriceEntity.setPriceListId(priceListId);
            itemPriceEntity.setPriceListName(priceListName);
            itemPriceEntity.setBatchNo(itemBatchNo); //
            itemPriceEntity.setRate(rate);//
            itemPriceEntity.setPriceListType(priceListType);
            itemPriceEntity.setCustomerId(""+Constants.EMPTY_INT);
            itemPriceEntity.setCurrencyId(Constants.EMPTY_INT);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    private void showToast(String msg){
        showToast(msg, ItemPriceEditActivity.this);
    }

    private void saveItemPrice(){
        try {

            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        localDb.itemPriceListDao().insertItem(itemPriceEntity);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                progressDialog.hideProgressbar();

                                AppDialogs appDialogs = new AppDialogs(ItemPriceEditActivity.this);
                                appDialogs.showCommonSuccessDialog(getString(R.string.update_success), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finish();
                                    }
                                });
                            }
                        });

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