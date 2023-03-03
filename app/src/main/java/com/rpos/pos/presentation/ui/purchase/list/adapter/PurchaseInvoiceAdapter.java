package com.rpos.pos.presentation.ui.purchase.list.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.dao.PurchaseInvoiceDao;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.data.remote.dto.purchase.list.PurchaseInvoiceData;
import com.rpos.pos.domain.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PurchaseInvoiceAdapter extends RecyclerView.Adapter<PurchaseInvoiceAdapter.PurchaseInvoiceViewHolder> implements Filterable {

    private List<PurchaseInvoiceData> purchaseInvoiceEntityList;
    private List<PurchaseInvoiceData> filteredList;
    private PurchaseInvoiceListener listener;

    private boolean isCreditSale;
    private String paymentStatus;

    private String DATE_PREFIX;
    private String AMOUNT_PREFIX;
    private String SUPPLIER_PREFIX;
    private String CANCELLED;
    private String date;


    public PurchaseInvoiceAdapter(Context context, List<PurchaseInvoiceData> purchaseInvoiceEntityList, PurchaseInvoiceListener listener) {

        this.purchaseInvoiceEntityList = purchaseInvoiceEntityList;
        this.filteredList = purchaseInvoiceEntityList;
        this.listener = listener;

        SUPPLIER_PREFIX = context.getString(R.string.supplier_label) +" : ";
        AMOUNT_PREFIX = context.getResources().getString(R.string.amount_label) + " : ";
        DATE_PREFIX = context.getString(R.string.date_label) + " : ";
        CANCELLED = context.getResources().getString(R.string.cancelled);
    }

    @NonNull
    @Override
    public PurchaseInvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_invoice_list_item, parent, false);
        return new PurchaseInvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseInvoiceViewHolder holder, int position) {
        try{

            PurchaseInvoiceData invoice = purchaseInvoiceEntityList.get(position);

            holder.tv_invoiceId.setText(invoice.getName());
            holder.tv_supplierName.setText(SUPPLIER_PREFIX + invoice.getSupplierName());
            holder.tv_amount.setText(AMOUNT_PREFIX + invoice.getBaseRoundedTotal());

            date = DateTimeUtils.getFormattedDateF1(invoice.getCreation());
            Log.e("------------","P_I_A_OB TS TO DT:"+date);
            holder.tv_date.setText(DATE_PREFIX + date);

            if(invoice.getStatus().equals(Constants.PAYMENT_PAID)){
                holder.tv_status.setTextColor(Color.GREEN);
                paymentStatus = Constants.PAYMENT_PAID;
            }else if(invoice.getStatus().equals(Constants.PAYMENT_RETURN)){
                holder.tv_status.setTextColor(Color.BLUE);
                paymentStatus = Constants.PAYMENT_RETURN;
            }else if(invoice.getStatus().equals(Constants.PAYMENT_UNPAID)){
                //check whether invoice is credit sale
                /*isCreditSale = (invoice.getPaymentType() == Constants.PAY_TYPE_CREDIT_SALE);
                if(isCreditSale){

                    if(DateTimeUtils.checkDatePassed(invoice.getDueDate())) {
                        paymentStatus = Constants.PAYMENT_OVERDUE;
                    }else {
                        paymentStatus = Constants.PAYMENT_UNPAID;
                    }
                }else {
                    paymentStatus = Constants.PAYMENT_UNPAID;
                }

                holder.tv_status.setTextColor(Color.RED);*/
            }

            holder.tv_status.setText(paymentStatus);

            if(!invoice.getStatus().equals(Constants.PAYMENT_RETURN)) {
                //cancel click
                holder.btn_cancel.setOnClickListener(null); //clearing previous listeners
                holder.btn_cancel.setOnClickListener(view -> {
                    try {
                        if (listener != null) {
                        //    listener.onCancelPurchaseInvoice(invoice);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }else {
                holder.btn_cancel.setText(CANCELLED);
            }

            holder.itemView.setOnClickListener(null);
            holder.itemView.setOnClickListener(view -> {
                if(listener!=null) listener.onClickPurchaseInvoice(invoice.getName());
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (filteredList!=null)?filteredList.size():0;
    }

    public static class PurchaseInvoiceViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView tv_invoiceId, tv_amount,tv_date, tv_supplierName, tv_status;
        private AppCompatButton btn_cancel;

        public PurchaseInvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_invoiceId = itemView.findViewById(R.id.tv_invoiceId);
            tv_supplierName = itemView.findViewById(R.id.tv_customerName);
            tv_amount = itemView.findViewById(R.id.tv_invoiceAmount);
            tv_date = itemView.findViewById(R.id.tv_invoiceDate);
            tv_status = itemView.findViewById(R.id.tv_customerStatus);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);
        }
    }

    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }


    private Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<PurchaseInvoiceData> filteredList = new ArrayList<>();
            if (constraint == null) {

                filteredList.addAll(purchaseInvoiceEntityList);

            } else {

                String[] splitString = constraint.toString().split(":");
                if(splitString==null || splitString.length==1){
                    filteredList.addAll(purchaseInvoiceEntityList);
                }else {
                    if(splitString[0].equals(Constants.FILTER_SORT)){
                        //sort
                        final int filterID = Integer.parseInt(splitString[1]);
                        switch (filterID)
                        {
                            case Constants.FILTER_ALL:
                                filteredList.addAll(purchaseInvoiceEntityList);
                                break;
                            case Constants.FILTER_PAID:

                                for (PurchaseInvoiceData invoice : purchaseInvoiceEntityList) {
                                    if(invoice.getStatus().equals(Constants.PAYMENT_PAID)){
                                        filteredList.add(invoice);
                                    }
                                }

                                break;
                            case Constants.FILTER_UNPAID:

                                for (PurchaseInvoiceData invoice : purchaseInvoiceEntityList) {
                                    if(invoice.getStatus().equals(Constants.PAYMENT_UNPAID)){
                                        filteredList.add(invoice);
                                    }
                                }

                                break;

                            case Constants.FILTER_OVERDUE:

                                //loop through invoice list
                                /*for (PurchaseInvoiceEntity invoice : purchaseInvoiceEntityList) {
                                    //find the credit sale type invoices
                                    if(invoice.getPaymentType() == Constants.PAY_TYPE_CREDIT_SALE){
                                        //check amount paid. find unpaid with balance payable
                                        if(invoice.getPaymentAmount() < invoice.getBillAmount()){
                                            //check the due date passed
                                            if(DateTimeUtils.checkDatePassed(invoice.getDueDate())){
                                                //if date exceeded, then show it on overdue
                                                filteredList.add(invoice);
                                            }
                                        }
                                    }
                                }*/

                                break;

                            case Constants.FILTER_RETURN:

                                //loop through invoice list
                                /*for (PurchaseInvoiceEntity invoice : purchaseInvoiceEntityList) {
                                    //find the credit sale type invoices
                                    if(invoice.getStatus().equals(Constants.PAYMENT_RETURN)){
                                        //check amount paid. find unpaid with balance payable
                                        filteredList.add(invoice);
                                    }
                                }*/

                                break;

                            default:
                                break;
                        }

                    }else {

                        //search
                        final String filterString = splitString[1];
                        String str_invoiceId;
                        String str_customerName;

                        for (PurchaseInvoiceData invoice : purchaseInvoiceEntityList) {
                            str_invoiceId = Constants.INVOICE_PREFIX + ""+invoice.getName();
                            str_customerName = invoice.getName().toLowerCase();
                            if(str_invoiceId.contains(filterString) || str_customerName.contains(filterString.toLowerCase())){
                                filteredList.add(invoice);
                            }
                        }
                    }
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    public interface PurchaseInvoiceListener{
        void onClickPurchaseInvoice(String invoiceId);
        void onCancelPurchaseInvoice(PurchaseInvoiceData pInvoice);
    }
}
