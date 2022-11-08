package com.rpos.pos.presentation.ui.dashboard.fragment;

import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatTextView;
import com.rpos.pos.R;

public class HomeCardFragment extends DashboardBaseFragment {

    public HomeCardFragment(){

    }

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_home_card;
    }

    @Override
    protected void onCreateView(View getView) {

        //local db access components
        initDbComponents();

        //card views
        View salesView = getView.findViewById(R.id.view_sales);
        View purchaseView = getView.findViewById(R.id.view_purchase);
        View itemView = getView.findViewById(R.id.view_item);
        View shiftView = getView.findViewById(R.id.view_shift);
        View reportView = getView.findViewById(R.id.view_report);
        View settingsView = getView.findViewById(R.id.view_settings);
        View customerView = getView.findViewById(R.id.view_customer);
        View uomView = getView.findViewById(R.id.view_units);
        View paymentModeView = getView.findViewById(R.id.view_paymentmodes);
        View categoryView = getView.findViewById(R.id.view_category);
        View taxesView = getView.findViewById(R.id.view_taxes);
        View supplierView = getView.findViewById(R.id.view_supplier);
        View priceListView = getView.findViewById(R.id.view_pricelist);
        View itemPriceListView = getView.findViewById(R.id.view_itemPrice);

        //card name textfields
        AppCompatTextView tvSales = salesView.findViewById(R.id.tv_name);
        AppCompatTextView tvPurchase = purchaseView.findViewById(R.id.tv_name);
        AppCompatTextView tvItem = itemView.findViewById(R.id.tv_name);
        AppCompatTextView tvShift = shiftView.findViewById(R.id.tv_name);
        AppCompatTextView tvReport = reportView.findViewById(R.id.tv_name);
        AppCompatTextView tvSettings = settingsView.findViewById(R.id.tv_name);
        AppCompatTextView tvCustomer = customerView.findViewById(R.id.tv_name);
        AppCompatTextView tvCategory = categoryView.findViewById(R.id.tv_name);
        AppCompatTextView tvUom = uomView.findViewById(R.id.tv_name);
        AppCompatTextView tvPaymentModes = paymentModeView.findViewById(R.id.tv_name);
        AppCompatTextView tvTaxes = taxesView.findViewById(R.id.tv_name);
        AppCompatTextView tvSupplier = supplierView.findViewById(R.id.tv_name);
        AppCompatTextView tv_priceList = priceListView.findViewById(R.id.tv_name);
        AppCompatTextView tv_itemPrice = itemPriceListView.findViewById(R.id.tv_name);

        //card icon imageviews
        ImageView icon_sales = salesView.findViewById(R.id.iv_card_icon);
        ImageView icon_purchase = purchaseView.findViewById(R.id.iv_card_icon);
        ImageView icon_item = itemView.findViewById(R.id.iv_card_icon);
        ImageView icon_shift = shiftView.findViewById(R.id.iv_card_icon);
        ImageView icon_report = reportView.findViewById(R.id.iv_card_icon);
        ImageView icon_settings = settingsView.findViewById(R.id.iv_card_icon);
        ImageView icon_customer = customerView.findViewById(R.id.iv_card_icon);
        ImageView icon_category = categoryView.findViewById(R.id.iv_card_icon);
        ImageView icon_uom = uomView.findViewById(R.id.iv_card_icon);
        ImageView icon_paymentMode = paymentModeView.findViewById(R.id.iv_card_icon);
        ImageView icon_taxes = taxesView.findViewById(R.id.iv_card_icon);
        ImageView icon_supplier = supplierView.findViewById(R.id.iv_card_icon);
        ImageView icon_priceList = priceListView.findViewById(R.id.iv_card_icon);
        ImageView icon_itemPrice = itemPriceListView.findViewById(R.id.iv_card_icon);

        //set card names
        tvSales.setText(getString(R.string.sales_label));
        tvPurchase.setText(getString(R.string.purchase_label));
        tvItem.setText(getString(R.string.item_label));
        tvShift.setText(getString(R.string.shift_label));
        tvReport.setText(getString(R.string.report_label));
        tvSettings.setText(getString(R.string.settings_label));
        tvCustomer.setText(getString(R.string.customer_label));
        tvCategory.setText(R.string.category_label);
        tvUom.setText(R.string.units_btn);
        tvPaymentModes.setText(R.string.payment_modes);
        tvTaxes.setText(R.string.taxes);
        tvSupplier.setText(R.string.supplier_label);
        tv_priceList.setText(R.string.price_list);
        tv_itemPrice.setText(R.string.item_price);


        //set card icons
        icon_sales.setImageResource(R.drawable.icon_sales);
        icon_purchase.setImageResource(R.drawable.icon_purchase_1);
        icon_item.setImageResource(R.drawable.icon_item);
        icon_shift.setImageResource(R.drawable.shift);
        icon_report.setImageResource(R.drawable.icon_report);
        icon_settings.setImageResource(R.drawable.icon_settings);
        icon_customer.setImageResource(R.drawable.icon_customer);
        icon_category.setImageResource(R.drawable.ic_items);
        icon_uom.setImageResource(R.drawable.ic_measurement);
        icon_paymentMode.setImageResource(R.drawable.ic_payment);
        icon_taxes.setImageResource(R.drawable.report);
        icon_supplier.setImageResource(R.drawable.supplier);
        icon_priceList.setImageResource(R.drawable.ic_pricelist);
        icon_itemPrice.setImageResource(R.drawable.ic_item_price);


        //sales option click
        salesView.setOnClickListener(view -> gotoSalesActivity());

        //purchase click
        purchaseView.setOnClickListener(view -> {
            try {
                gotoPurchaseActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //items option click
        itemView.setOnClickListener(view -> gotoItemActivity());

        //settings option click
        settingsView.setOnClickListener(view -> gotoSettingsActivity());

        //shift option click
        shiftView.setOnClickListener(view -> gotoShiftActivity());

        //customer click
        customerView.setOnClickListener(view -> gotoCustomerListActivity());

        //category click
        categoryView.setOnClickListener(view -> gotoCategoryListActivity());

        //uom
        uomView.setOnClickListener(view -> gotoUOMListActivity());

        //payment
        paymentModeView.setOnClickListener(view -> gotoPaymentModesListActivity());

        //taxes
        taxesView.setOnClickListener(view -> {
            gotoTaxListActivity();
        });

        //supplier
        supplierView.setOnClickListener(view -> {
            gotoSupplierListActivity();
        });

        //priceList
        priceListView.setOnClickListener(view -> {
            gotoPriceListActivity();
        });

        //item price List
        itemPriceListView.setOnClickListener(view -> {
            gotoItemPriceListActivity();
        });

        //report home
        reportView.setOnClickListener(view -> {
            gotoReportHomeActivity();
        });


        progressDialog.showProgressBar();
        loadCurrentShiftStatus(appExecutors,localDb, progressDialog);

    }

    @Override
    protected void initViewModels() {

    }


    @Override
    protected void initObservers() {

    }


}
