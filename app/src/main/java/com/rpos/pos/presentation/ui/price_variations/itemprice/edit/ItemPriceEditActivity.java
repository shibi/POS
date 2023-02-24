package com.rpos.pos.presentation.ui.price_variations.itemprice.edit;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.presentation.ui.common.SharedActivity;
import com.rpos.pos.presentation.ui.item.select.ItemSelectActivity;
import com.rpos.pos.presentation.ui.price_variations.pricelist.select.SelectPriceListActivity;
import com.rpos.pos.presentation.ui.settings.adapter.pricelist.BuyPriceListSpinnerAdapter;
import com.rpos.pos.presentation.ui.settings.adapter.pricelist.SellPriceListSpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ItemPriceEditActivity extends SharedActivity {

    private LinearLayout ll_back, ll_save;
    private AppCompatButton btn_pick_item;
    private AppCompatTextView tv_itemName;

    private int itemId;
    private String itemName;

    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private AppDialogs progressDialog;

    private ItemPriceEntity buyItemPriceEntity;
    private ItemPriceEntity sellItemPriceEntity;

    private AppCompatEditText et_buy_rate,et_sell_rate;

    private String STRING_SEPARATOR = " : ";

    private int selectedPriceListId;

    private String buy_priceListName, sell_priceListName;
    private Integer buy_priceListId, sell_priceListId;

    private Spinner sp_priceListBuy, sp_priceListSell;
    private List<PriceListEntity> buyPriceListArray, sellPriceListArray;
    private BuyPriceListSpinnerAdapter buyPriceListSpinnerAdapter;
    private SellPriceListSpinnerAdapter sellPriceListSpinnerAdapter;

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
        ll_save = findViewById(R.id.ll_rightMenu);

        et_sell_rate = findViewById(R.id.et_sell_rate);
        et_buy_rate = findViewById(R.id.et_buy_rate);

        tv_itemName = findViewById(R.id.tv_itemNames);

        //price list spinners
        sp_priceListBuy = findViewById(R.id.sp_buy_pricelist);
        sp_priceListSell = findViewById(R.id.sp_sell_pricelist);


        Intent data = getIntent();
        if(data!=null){
            selectedPriceListId = data.getIntExtra(Constants.ITEM_PRICE_LIST_ID, Constants.EMPTY_INT);
        }else {
            selectedPriceListId = Constants.EMPTY_INT;
        }

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

        buyPriceListArray = new ArrayList<>();
        buyPriceListSpinnerAdapter = new BuyPriceListSpinnerAdapter(this, buyPriceListArray);
        sp_priceListBuy.setAdapter(buyPriceListSpinnerAdapter);
        sp_priceListBuy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {

                    buy_priceListId = buyPriceListArray.get(position).getId();
                    buy_priceListName = buyPriceListArray.get(position).getPriceListName();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sellPriceListArray = new ArrayList<>();
        sellPriceListSpinnerAdapter = new SellPriceListSpinnerAdapter(this, sellPriceListArray);
        sp_priceListSell.setAdapter(sellPriceListSpinnerAdapter);
        sp_priceListSell.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                try {

                    sell_priceListId = sellPriceListArray.get(position).getId();
                    sell_priceListName = sellPriceListArray.get(position).getPriceListName();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_pick_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem();
            }
        });

        //back
        ll_back.setOnClickListener(view -> onBackPressed());

        getPriceList();
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

                        ItemPriceEntity temp = localDb.itemPriceListDao().getItemPriceWithId(selectedPriceListId);

                        if(temp.getPriceListType() == Constants.BUYING){
                            buyItemPriceEntity = temp;
                            sellItemPriceEntity = localDb.itemPriceListDao().getItemPriceWithId(temp.getCompanionPriceListId());
                        }else {
                            sellItemPriceEntity = temp;
                            buyItemPriceEntity = localDb.itemPriceListDao().getItemPriceWithId(temp.getCompanionPriceListId());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateData();
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
                    itemId = data.getIntExtra(Constants.ITEM_ID,Constants.EMPTY_INT);
                    itemName = data.getStringExtra(Constants.ITEM_NAME);
                    tv_itemName.setText(itemName);
                }
            }
        }
    });

    /**
     * populate data
     * */
    private void populateData(){
        try {

            buy_priceListId = buyItemPriceEntity.getPriceListId();
            sell_priceListId = sellItemPriceEntity.getPriceListId();

            itemId = buyItemPriceEntity.getItemId();
            itemName = buyItemPriceEntity.getItemName();
            tv_itemName.setText(itemName);



            et_buy_rate.setText(String.valueOf(buyItemPriceEntity.getRate()));
            et_sell_rate.setText(String.valueOf(sellItemPriceEntity.getRate()));

            for(int i=0;i< buyPriceListArray.size();i++){
                if(buyPriceListArray.get(i).getId().equals(buy_priceListId)){
                    sp_priceListBuy.setSelection(i, true);
                    break;
                }
            }

            for(int i=0;i< sellPriceListArray.size();i++){
                if(sellPriceListArray.get(i).getId().equals(sell_priceListId)){
                    sp_priceListSell.setSelection(i, true);
                    break;
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean validate(){
        try {

            String itemCode = "no-data";
            String str_sell_rate = et_sell_rate.getText().toString();
            String str_buy_rate = et_buy_rate.getText().toString();

            String itemBatchNo = "empty";
            float sell_rate,buy_rate;

            //check whether item selected
            if(itemId == Constants.EMPTY_INT){
                showToast(getString(R.string.select_item));
                return false;
            }

            //check whether buying rate entered.
            if(str_buy_rate.isEmpty()){
                et_buy_rate.setError(getString(R.string.enter_amount));
                et_buy_rate.requestFocus();
                return false;
            }
            //check whether selling rate entered.
            if(str_sell_rate.isEmpty()){
                et_sell_rate.setError(getString(R.string.enter_amount));
                et_sell_rate.requestFocus();
                return false;
            }

            if(buy_priceListId == Constants.EMPTY_INT){
                showToast(getString(R.string.select_price_list));
                return false;
            }
            if(sell_priceListId == Constants.EMPTY_INT){
                showToast(getString(R.string.select_price_list));
                return false;
            }


            sell_rate = Float.parseFloat(str_sell_rate);
            if(sell_rate <= 0){
                showToast(getString(R.string.enter_amount));
                return false;
            }

            buy_rate = Float.parseFloat(str_buy_rate);
            if(buy_rate <= 0){
                showToast(getString(R.string.enter_amount));
                return false;
            }

            buyItemPriceEntity.setItemCode(itemCode); //
            buyItemPriceEntity.setItemId(itemId); //
            buyItemPriceEntity.setItemName(itemName); //
            buyItemPriceEntity.setPriceListId(buy_priceListId);
            buyItemPriceEntity.setPriceListName(buy_priceListName);
            buyItemPriceEntity.setCompanionPriceListId(sell_priceListId);
            buyItemPriceEntity.setBatchNo(itemBatchNo); //
            buyItemPriceEntity.setRate(buy_rate);//
            buyItemPriceEntity.setPriceListType(Constants.BUYING);



            sellItemPriceEntity.setItemCode(itemCode); //
            sellItemPriceEntity.setItemId(itemId); //
            sellItemPriceEntity.setItemName(itemName); //
            sellItemPriceEntity.setPriceListId(sell_priceListId);
            sellItemPriceEntity.setPriceListName(sell_priceListName);
            sellItemPriceEntity.setCompanionPriceListId(buy_priceListId);
            sellItemPriceEntity.setBatchNo(itemBatchNo); //
            sellItemPriceEntity.setRate(sell_rate);//
            sellItemPriceEntity.setPriceListType(Constants.SELLING);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    private void getPriceList(){
        try {

            AppExecutors appExecutors = new AppExecutors();
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //get all price list
                        List<PriceListEntity> allPricesList = localDb.priceListDao().getAllPriceList();
                        //clear all data in lists
                        buyPriceListArray.clear();
                        sellPriceListArray.clear();

                        //sort buying and selling price lists and add them separately
                        for (PriceListEntity priceListEntity : allPricesList) {
                            if (priceListEntity.getPriceType() == 0) {
                                buyPriceListArray.add(priceListEntity);
                            } else {
                                sellPriceListArray.add(priceListEntity);
                            }
                        }

                        //update list
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    buyPriceListSpinnerAdapter.notifyDataSetChanged();
                                    sellPriceListSpinnerAdapter.notifyDataSetChanged();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });


                        //get item price details
                        getItemPriceDetail();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


        }catch (Exception e){
            e.printStackTrace();
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

                        localDb.itemPriceListDao().insertItem(buyItemPriceEntity);
                        localDb.itemPriceListDao().insertItem(sellItemPriceEntity);

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