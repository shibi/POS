package com.rpos.pos.presentation.ui.item.select;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.AppExecutors;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.AppDatabase;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.local.entity.ItemPriceEntity;
import com.rpos.pos.data.local.entity.PriceListEntity;
import com.rpos.pos.data.remote.api.ApiGenerator;
import com.rpos.pos.data.remote.api.ApiService;
import com.rpos.pos.data.remote.dto.items.list.GetItemsListResponse;
import com.rpos.pos.data.remote.dto.items.list.ItemData;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;
import com.rpos.pos.domain.requestmodel.item.getlist.GetItemListRequest;
import com.rpos.pos.domain.utils.AppDialogs;
import com.rpos.pos.domain.utils.SharedPrefHelper;
import com.rpos.pos.presentation.ui.barcode_scanner.ItemBarcodeScanner;
import com.rpos.pos.presentation.ui.common.SharedActivity;


import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 *  class used to select item from list
 *  N.B : mandatory to pass selection type in intent to this -
 *          - activity to identify what to do after item selection.
 *
 * parent activity flag must be also in the intent to identify requested parent
 * */
public class ItemSelectActivity extends SharedActivity {


    private RecyclerView rv_itemList;
    private List<ItemData> itemDataList;
    private ItemPickerListAdapter itemPickerAdapter;
    private AppDialogs progressDialog;
    private View viewEmpty;
    private AppExecutors appExecutors;
    private AppDatabase localDb;
    private LinearLayout ll_back, ll_barcode;
    private SearchView sv_searchItem;
    private int selectionType;
    private int itemRequestedScreen;
    private int priceListId;
    private SharedPrefHelper prefHelper;

    @Override
    public int setUpLayout() {
        return R.layout.activity_item_select;
    }

    @Override
    public void initViewModels() {

    }

    @Override
    public void initViews() {

        rv_itemList = findViewById(R.id.rv_itemlist);
        progressDialog = new AppDialogs(this);
        itemDataList = new ArrayList<>();
        viewEmpty = findViewById(R.id.view_empty);
        ll_back = findViewById(R.id.ll_back);
        sv_searchItem = findViewById(R.id.sv_search);
        ll_barcode = findViewById(R.id.ll_barcode);

        //shared preference
        prefHelper = SharedPrefHelper.getInstance(this);

        appExecutors = new AppExecutors();
        localDb = getCoreApp().getLocalDb();

        Intent data = getIntent();
        if(data!=null){
            selectionType = data.getIntExtra(Constants.ITEM_SELECTION_TYPE, Constants.EMPTY_INT);
            itemRequestedScreen = data.getIntExtra(Constants.ITEM_REQUESTED_PARENT, Constants.EMPTY_INT);
        }else {
            selectionType = Constants.ITEM_SELECTION_QUANTITY_PICK;
            itemRequestedScreen = Constants.EMPTY_INT;
        }

        String defaultCurrencySymbol = getCoreApp().getDefaultCurrency();
        if(defaultCurrencySymbol == null || defaultCurrencySymbol.equals(Constants.NONE)){
            AppDialogs appDialogs = new AppDialogs(ItemSelectActivity.this);
            appDialogs.showCommonAlertDialog(getString(R.string.default_currency_not_selected), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            return;
        }


        if(itemRequestedScreen == Constants.PARENT_SALES){
            priceListId = prefHelper.getSellingPriceListId();
        }else if(itemRequestedScreen == Constants.PARENT_PURCHASE){
            priceListId = prefHelper.getBuyingPriceListId();
        }else {
            priceListId = Constants.EMPTY_INT;
        }

        getItemsListApiCall();


        //item list adapter
        itemPickerAdapter = new ItemPickerListAdapter(itemDataList, defaultCurrencySymbol,ItemSelectActivity.this, new ItemPickerListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ItemData item) {

                if(selectionType == Constants.ITEM_SELECTION_QUANTITY_PICK) {
                    //separate method call for sales and purchase
                    if(itemRequestedScreen == Constants.PARENT_SALES)
                        selectItemForSale(item); // item selection for sales
                    else if(itemRequestedScreen == Constants.PARENT_PURCHASE) {
                        selectItemForPurchase(item); // item selection for purchase
                    }

                }else if(selectionType == Constants.ITEM_SELECTION_SINGLE_PICK) {
                    //sendResultBack(item);
                    showToast("Not implemented", ItemSelectActivity.this);
                }
            }
        });
        rv_itemList.setLayoutManager(new LinearLayoutManager(this));
        rv_itemList.setAdapter(itemPickerAdapter);

        //back press
        ll_back.setOnClickListener(view -> onBackPressed());


        try {

            //This below code is to change the text color programmatically, since it is not
            //accessible from xml
            EditText searchField = ((EditText) sv_searchItem.findViewById(androidx.appcompat.R.id.search_src_text));
            searchField.setTextColor(getResources().getColor(R.color.color_accent_secondary));
            searchField.setHintTextColor(getResources().getColor(R.color.color_primarydark));
            searchField.setTextSize(14);
            //to set the color of search close icon
            ImageView searchClose = sv_searchItem.findViewById(R.id.search_close_btn);
            searchClose.setColorFilter(getResources().getColor(R.color.color_accent_secondary));

        }catch (Exception e){

        }


        //sv_searchItem.setFocusable(true);
        //sv_searchItem.requestFocusFromTouch();
        //filter list on search
        sv_searchItem.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemPickerAdapter.getFilter().filter(newText);
                return false;
            }
        });


        //barcode scanner click
        ll_barcode.setOnClickListener(view -> {
            //enable qr code for sales item selection only. else no need to scan bar code
            if(selectionType == Constants.ITEM_SELECTION_QUANTITY_PICK && itemRequestedScreen == Constants.PARENT_SALES) {
                //verify camera permission before
                //going to scanner screen
                onClickBarcodeScanner();
            }else {
                //Do nothing
            }
        });



        //get items list
        //getItemsList();



    }

    @Override
    public void initObservers() {

    }


    /**
     * To get items list  API
     * */
    private void getItemsListApiCall(){
        try {

            showProgress();

            String userId = SharedPrefHelper.getInstance(this).getUserId();
            if(userId.isEmpty()){
                showToast(getString(R.string.invalid_userid), ItemSelectActivity.this);
                hideProgress();
                return;
            }


            ApiService api = ApiGenerator.createApiService(ApiService.class, Constants.API_KEY,Constants.API_SECRET);
            //request params
            GetItemListRequest requestParams = new GetItemListRequest();
            requestParams.setUserId(userId);
            Call<GetItemsListResponse> call = api.getItemsList(requestParams);
            call.enqueue(new Callback<GetItemsListResponse>() {
                @Override
                public void onResponse(Call<GetItemsListResponse> call, Response<GetItemsListResponse> response) {
                    hideProgress();
                    Log.e("----------","res"+response.isSuccessful());
                    if(response.isSuccessful()){
                        GetItemsListResponse itemsListResponse = response.body();
                        if(itemsListResponse!=null){
                            List<ItemData> itemList = itemsListResponse.getMessage();
                            if(itemList!=null && itemList.size()>0) {
                                //hide empty view
                                hideEmptyView();
                                //clear list
                                itemDataList.clear();
                                itemDataList.addAll(itemList);
                                itemPickerAdapter.notifyDataSetChanged();

                                //All data set, stop proceeding
                                return;
                            }
                        }
                    }

                    //empty
                    showEmptyView();

                }

                @Override
                public void onFailure(Call<GetItemsListResponse> call, Throwable t) {
                    Log.e("----------","failed>"+t.getMessage());
                    hideProgress();
                    //empty
                    showEmptyView();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get locally saved item list
     * */
    private void getOfflineItemList(){
        try{
            //thread for room db function
            appExecutors.diskIO().execute(() -> {

                final List<ItemEntity> localItemList = localDb.itemDao().getAllItems();
                //check item list size
                if(localItemList == null || localItemList.isEmpty()){
                    //show alert for items empty
                    showAlertForItemEmpty();
                    return;
                }

                //if there are items,
                //then refresh list
                runOnUiThread(() -> {
                    try {
                        /*itemDataList.addAll(localItemList);
                        itemPickerAdapter.notifyDataSetChanged();*/
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });

            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * to show item empty alert
     * */
    private void showAlertForItemEmpty() {
        try {

            runOnUiThread(() -> {
                try {

                    AppDialogs appDialogs = new AppDialogs(ItemSelectActivity.this);
                    String alertMessage = getString(R.string.item_add_and_proceed);
                    appDialogs.showCommonAlertDialog(alertMessage, view -> finish());

                }catch (Exception e){
                    throw e;
                }
            });

        }catch (Exception e){
            throw e;
        }
    }

    /**
     * to set result and finish activity
     * it will send the selected item back to the original activity , whichever called this present activity
     * */
    private void sendResultBack(ItemEntity _item){
        try {

            Intent intent = new Intent();
            intent.putExtra(Constants.ITEM_ID, _item.getItemId());
            intent.putExtra(Constants.ITEM_NAME, _item.getItemName());
            intent.putExtra(Constants.ITEM_UOM_ID, _item.getUom());
            intent.putExtra(Constants.ITEM_DESC, _item.getDescription());
            setResult(RESULT_OK, intent);
            finish();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  to check app has camera permission
     *  on barcode scanner click
     * */
    private void onClickBarcodeScanner(){
        try {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                gotoBarcodeScannerActivity();

            }else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CAMERA_PERMISSION);
                return;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void gotoBarcodeScannerActivity(){
        Intent selecteIntent = new Intent(ItemSelectActivity.this, ItemBarcodeScanner.class);
        launchItemPicker.launch(selecteIntent);
    }

    private ActivityResultLauncher<Intent> launchItemPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == RESULT_OK){
                Intent data  = result.getData();
                if(data!=null){
                    String barcode = data.getStringExtra(Constants.BARCODE_NUMBER);
                    findItemWithBarcode(barcode);
                }
            }
        }
    });


    private void selectItemForSale(ItemData item){
        try{

            boolean isMaintainStock = true; //(item.getMaintainStock() != 0);
            showItemQuantitySelectionDialogBox(item, item.getUomName(), isMaintainStock, Constants.PARENT_SALES);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * item selection for purchase
     * */
    private void selectItemForPurchase(ItemData item){
        try {

            boolean isMaintainStock = true; //(item.getMaintainStock() != 0);
            showItemQuantitySelectionDialogBox(item, item.getUomName(), isMaintainStock, Constants.PARENT_PURCHASE);

            /*appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //check stock type
                        boolean isMaintainStock = (item.getMaintainStock() != 0);

                        //if stock need to maintained
                        if(isMaintainStock){

                            if(priceListId!= Constants.EMPTY_INT){
                                ItemPriceEntity itemPrice = localDb.itemPriceListDao().findPriceForItemWithPriceListId(priceListId, item.getItemId());
                                if(itemPrice!=null){
                                    //set the item price rate
                                    item.setRate(itemPrice.getRate());

                                    showItemQuantitySelectionDialogBox(item, item.getUomName(), isMaintainStock, Constants.PARENT_PURCHASE);

                                }else {
                                    PriceListEntity priceList = localDb.priceListDao().getPriceListWithId(priceListId);
                                    String priceListName = (priceList == null)?"":priceList.getPriceListName();
                                    //show alert , item price not set
                                    showAlertItemPriceNotSet(item.getItemName(), priceListName);
                                }
                            }
                        }else {
                            //no need to maintain stock
                            //simply select the item with quantity
                            showItemQuantitySelectionDialogBox(item, item.getUomName(), isMaintainStock, Constants.PARENT_PURCHASE);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });*/

        }catch (Exception e){
            throw e;
        }
    }

    private void showAlertItemPriceNotSet(String _itemName, String _priceListName){
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String msg;
                        if(_priceListName==null || _priceListName.isEmpty()){
                            msg = getString(R.string.add_price_list_and_proceed);
                        }else {
                            msg = getString(R.string.item_price_not_created) + "  \n" + _itemName + " (" + _priceListName + ")";
                        }

                        AppDialogs appDialogs = new AppDialogs(ItemSelectActivity.this);
                        appDialogs.showCommonAlertDialog(msg, null);
                    }catch (Exception e){
                        throw e;
                    }
                }
            });
        }catch (Exception e){
            throw e;
        }
    }


    /**
     * To find item with barcode
     * if not found, shows a message box
     * */
    private void findItemWithBarcode(String scannedBarcode){
        try {

            ItemData scannedItem = null;

            if(scannedBarcode!=null && !scannedBarcode.isEmpty()){
                for (ItemData item: itemDataList) {
                    if(item.getBarcodes()!=null && !item.getBarcodes().isEmpty()){
                        if(item.getBarcodes().get(0).getBarcode().equals(scannedBarcode)){
                            scannedItem = item;
                            break;
                        }
                    }
                }
            }

            //if scanned item not found
            if(scannedItem == null){
                //show barcode item not found
                showScannerBarcodeNotFound(scannedBarcode);
            }else {
                //scanned item found.
                //goto the quantity selection window
                selectItemForSale(scannedItem);
            }


            /*appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ItemEntity itemEntity = localDb.itemDao().findItemWithBarcode(code);
                        if(itemEntity!=null){

                            //goto the quantity selection window
                            selectItemForSales(itemEntity);

                        }else {
                            //show barcode item not found
                            showScannerBarcodeNotFound((code!=null)?code.get(0):"");
                        }
                    }catch (Exception e){
                        showToast("Result unknown",ItemSelectActivity.this);
                        e.printStackTrace();
                    }
                }
            });*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * dialog box to inform user that the scanned barcode is not found
     * @param code scanned code
     * */
    private void showScannerBarcodeNotFound(String code){
        runOnUiThread(() -> {
            try {

                String title = getString(R.string.not_found);
                String message = getString(R.string.item_barcode_not_found)+ code;

                AppDialogs appDialogs = new AppDialogs(ItemSelectActivity.this);
                appDialogs.showCommonSingleAlertDialog(title, message, view -> {
                    //Do nothing
                });

            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    /**
     * To show quantity selection window
     * */
    private void showItemQuantitySelectionDialogBox(ItemData _item,String uom_name,boolean isMaintainStock, int requestedParent){
        try {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AppDialogs appDialogs = new AppDialogs(ItemSelectActivity.this);
                    appDialogs.showOrderItemPicker(_item.getAvailableQty(), _item.getRate(), uom_name, isMaintainStock, requestedParent,quantity -> {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.ITEM_ID, _item.getItemId());
                        intent.putExtra(Constants.ITEM_QUANTITY, quantity);
                        intent.putExtra(Constants.ITEM_STOCK, _item.getAvailableQty());
                        intent.putExtra(Constants.ITEM_NAME, _item.getItemName());
                        intent.putExtra(Constants.ITEM_UOM_ID, _item.getUom());
                        intent.putExtra(Constants.ITEM_RATE, ""+_item.getRate());
                        intent.putExtra(Constants.ITEM_MAINTAIN_STOCK, isMaintainStock);
                        setResult(RESULT_OK, intent);
                        finish();
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * To show quantity selection window
     * */
    private void showItemQuantitySelectionDialog(ItemEntity _item,String uom_name, boolean isMaintainStock, int requestedParent){
        try {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AppDialogs appDialogs = new AppDialogs(ItemSelectActivity.this);
                    appDialogs.showOrderItemPicker(_item.getAvailableQty(), _item.getRate(), uom_name, isMaintainStock,requestedParent,quantity -> {
                        Intent intent = new Intent();
                        intent.putExtra(Constants.ITEM_ID, _item.getItemId());
                        intent.putExtra(Constants.ITEM_QUANTITY, quantity);
                        intent.putExtra(Constants.ITEM_STOCK, _item.getAvailableQty());
                        intent.putExtra(Constants.ITEM_NAME, _item.getItemName());
                        intent.putExtra(Constants.ITEM_RATE, ""+_item.getRate());
                        intent.putExtra(Constants.ITEM_MAINTAIN_STOCK, isMaintainStock);
                        setResult(RESULT_OK, intent);
                        finish();
                    });
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check request code
        if(requestCode == Constants.REQUEST_CAMERA_PERMISSION){
            //check permission granted
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //initialize scanner
                gotoBarcodeScannerActivity();
            }
        }
    }

    private void showProgress(){
        progressDialog.showProgressBar();
    }
    private void hideProgress(){
        progressDialog.hideProgressbar();
    }

    private void showEmptyView(){
        viewEmpty.setVisibility(View.VISIBLE);
    }
    private void hideEmptyView(){
        viewEmpty.setVisibility(View.GONE);
    }
}