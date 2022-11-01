package com.rpos.pos.presentation.ui.dashboard.fragment;


import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.widget.AppCompatTextView;
import com.rpos.pos.R;

public class HomeListFragment extends DashboardBaseFragment {

    private View view_sales,view_purchase,view_item,view_customer,
            view_category,view_shift,view_uom,view_tax,view_paymentmode,
            view_report,view_settings, view_supplier, view_pricelist,view_itemPrice;

    public HomeListFragment(){

    }

    @Override
    protected int setContentLayout() {
        return R.layout.fragment_home_list;
    }

    @Override
    protected void onCreateView(View getView) {

        view_sales = getView.findViewById(R.id.view_sales_btn);
        view_purchase = getView.findViewById(R.id.view_purchase_btn);
        view_item = getView.findViewById(R.id.view_item_btn);
        view_shift = getView.findViewById(R.id.view_shift_btn);
        view_category = getView.findViewById(R.id.view_category_btn);
        view_customer = getView.findViewById(R.id.view_customer_btn);
        view_uom = getView.findViewById(R.id.view_uom_btn);
        view_paymentmode= getView.findViewById(R.id.view_payment_mode_btn);
        view_report = getView.findViewById(R.id.view_report_btn);
        view_settings = getView.findViewById(R.id.view_settings_btn);
        view_tax = getView.findViewById(R.id.view_tax_btn);
        view_supplier = getView.findViewById(R.id.view_supplier_btn);
        view_pricelist = getView.findViewById(R.id.view_pricelist_btn);
        view_itemPrice = getView.findViewById(R.id.view_itemPrice_btn);

        AppCompatTextView tv_sales = view_sales.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_purchase = view_purchase.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_item = view_item.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_customer = view_customer.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_shift = view_shift.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_category = view_category.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_uom = view_uom.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_paymentMode = view_paymentmode.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_report = view_report.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_settings = view_settings.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_tax = view_tax.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_supplier = view_supplier.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_priceList = view_pricelist.findViewById(R.id.tv_item_name);
        AppCompatTextView tv_itemPrice = view_itemPrice.findViewById(R.id.tv_item_name);

        ImageView iv_sales = view_sales.findViewById(R.id.iv_list_icon);
        ImageView iv_purchase = view_purchase.findViewById(R.id.iv_list_icon);
        ImageView iv_item = view_item.findViewById(R.id.iv_list_icon);
        ImageView iv_customer = view_customer.findViewById(R.id.iv_list_icon);
        ImageView iv_shift = view_shift.findViewById(R.id.iv_list_icon);
        ImageView iv_category= view_category.findViewById(R.id.iv_list_icon);
        ImageView iv_uom= view_uom.findViewById(R.id.iv_list_icon);
        ImageView iv_paymentMode= view_paymentmode.findViewById(R.id.iv_list_icon);
        ImageView iv_report = view_report.findViewById(R.id.iv_list_icon);
        ImageView iv_settings = view_settings.findViewById(R.id.iv_list_icon);
        ImageView iv_tax = view_tax.findViewById(R.id.iv_list_icon);
        ImageView iv_supplier = view_supplier.findViewById(R.id.iv_list_icon);
        ImageView iv_priceList = view_pricelist.findViewById(R.id.iv_list_icon);
        ImageView iv_itemPrice = view_itemPrice.findViewById(R.id.iv_list_icon);


        tv_sales.setText(getString(R.string.sales_label));
        tv_purchase.setText(getString(R.string.purchase_label));
        tv_item.setText(getString(R.string.item_label));
        tv_customer.setText(getString(R.string.customer_label));
        tv_shift.setText(getString(R.string.shift_label));
        tv_category.setText(getString(R.string.category_label));
        tv_uom.setText(R.string.units_btn);
        tv_paymentMode.setText(R.string.payment_modes);
        tv_report.setText(getString(R.string.report_label));
        tv_settings.setText(getString(R.string.settings_label));
        tv_tax.setText(getString(R.string.taxes_label));
        tv_supplier.setText(getString(R.string.supplier_label));
        tv_priceList.setText(getString(R.string.price_list));
        tv_itemPrice.setText(R.string.item_price);

        iv_sales.setImageResource(R.drawable.icon_sales);
        iv_purchase.setImageResource(R.drawable.icon_purchase_1);
        iv_item.setImageResource(R.drawable.icon_item);
        iv_customer.setImageResource(R.drawable.icon_customer);
        iv_shift.setImageResource(R.drawable.shift);
        iv_category.setImageResource(R.drawable.ic_items);
        iv_uom.setImageResource(R.drawable.ic_measurement);
        iv_paymentMode.setImageResource(R.drawable.ic_payment);
        iv_report.setImageResource(R.drawable.icon_report);
        iv_settings.setImageResource(R.drawable.icon_settings);
        iv_tax.setImageResource(R.drawable.report);
        iv_supplier.setImageResource(R.drawable.supplier);
        iv_priceList.setImageResource(R.drawable.ic_pricelist);
        iv_itemPrice.setImageResource(R.drawable.ic_item_price);


        //sales
        view_sales.setOnClickListener(view -> {
            try {
                gotoSalesActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //purchase activity
        view_purchase.setOnClickListener(view -> {
            try {
                gotoPurchaseActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //items
        view_item.setOnClickListener(view -> {
            try {
                gotoItemActivity();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        //settings
        view_settings.setOnClickListener(view -> {
            gotoSettingsActivity();
        });

        //Customer
        view_customer.setOnClickListener(view -> {
            gotoCustomerListActivity();
        });

        //shift
        view_shift.setOnClickListener(view -> gotoShiftActivity());

        //category
        view_category.setOnClickListener(view -> {
            gotoCategoryListActivity();
        });

        //uom
        view_uom.setOnClickListener(view -> {
            gotoUOMListActivity();
        });

        //payment
        view_paymentmode.setOnClickListener(view -> {
            gotoPaymentModesListActivity();
        });

        //tax
        view_tax.setOnClickListener(view -> {
            gotoTaxListActivity();
        });

        //supplier
        view_supplier.setOnClickListener(view -> {
            gotoSupplierListActivity();
        });

        //Price list
        view_pricelist.setOnClickListener(view -> {
            gotoPriceListActivity();
        });


        //Item list
        view_itemPrice.setOnClickListener(view -> {
            gotoItemPriceListActivity();
        });

    }

    @Override
    protected void initViewModels() {

    }

    @Override
    protected void initObservers() {

    }

}
