package com.rpos.pos.presentation.ui.purchase.list.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;
import com.rpos.pos.domain.utils.DateTimeUtils;

import java.util.ArrayList;
import java.util.List;

public class PurchaseInvoiceAdapter extends RecyclerView.Adapter<PurchaseInvoiceAdapter.PurchaseInvoiceViewHolder> implements Filterable {

    private List<PurchaseInvoiceEntity> purchaseInvoiceEntityList;
    private List<PurchaseInvoiceEntity> filteredList;
    private PurchaseInvoiceListener listener;

    private boolean isCreditSale;
    private String paymentStatus;

    private String DATE_PREFIX;
    private String AMOUNT_PREFIX;
    private String SUPPLIER_PREFIX;


    public PurchaseInvoiceAdapter(Context context, List<PurchaseInvoiceEntity> purchaseInvoiceEntityList, PurchaseInvoiceListener listener) {

        this.purchaseInvoiceEntityList = purchaseInvoiceEntityList;
        this.filteredList = purchaseInvoiceEntityList;
        this.listener = listener;

        SUPPLIER_PREFIX = context.getString(R.string.supplier_label) +" : ";
        AMOUNT_PREFIX = context.getResources().getString(R.string.amount_label) + " : ";
        DATE_PREFIX = context.getString(R.string.date_label) + " : ";
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

            PurchaseInvoiceEntity invoice = purchaseInvoiceEntityList.get(position);

            holder.tv_invoiceId.setText("PNo : "+invoice.getId());
            holder.tv_supplierName.setText(SUPPLIER_PREFIX +" : "+ invoice.getCustomerName());
            holder.tv_amount.setText(AMOUNT_PREFIX +" : "+ invoice.getBillAmount());
            holder.tv_date.setText(DATE_PREFIX+ " : "+invoice.getDate().substring(0,10));

            if(invoice.getPaymentAmount() < invoice.getBillAmount())
            {
                //check whether invoice is credit sale
                isCreditSale = (invoice.getPaymentType() == Constants.PAY_TYPE_CREDIT_SALE);

                if(isCreditSale){

                    if(DateTimeUtils.checkDatePassed(invoice.getDate())) {
                        paymentStatus = Constants.PAYMENT_OVERDUE;
                    }else {
                        paymentStatus = Constants.PAYMENT_UNPAID;
                    }
                }else {
                    paymentStatus = Constants.PAYMENT_UNPAID;
                }

                holder.tv_status.setTextColor(Color.RED);
            }else {

                holder.tv_status.setTextColor(Color.GREEN);
                paymentStatus = Constants.PAYMENT_PAID;
            }
            holder.tv_status.setText(paymentStatus);


            holder.itemView.setOnClickListener(null);
            holder.itemView.setOnClickListener(view -> {
                if(listener!=null) listener.onClickPurchaseInvoice(invoice.getId());
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

        public PurchaseInvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_invoiceId = itemView.findViewById(R.id.tv_invoiceId);
            tv_supplierName = itemView.findViewById(R.id.tv_customerName);
            tv_amount = itemView.findViewById(R.id.tv_invoiceAmount);
            tv_date = itemView.findViewById(R.id.tv_invoiceDate);
            tv_status = itemView.findViewById(R.id.tv_customerStatus);
        }
    }

    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }


    private Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<PurchaseInvoiceEntity> filteredList = new ArrayList<>();
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

                                for (PurchaseInvoiceEntity invoice : purchaseInvoiceEntityList) {
                                    if(invoice.getPaymentAmount() >= invoice.getBillAmount()){
                                        filteredList.add(invoice);
                                    }
                                }

                                break;
                            case Constants.FILTER_UNPAID:

                                for (PurchaseInvoiceEntity invoice : purchaseInvoiceEntityList) {
                                    if(invoice.getPaymentAmount() < invoice.getBillAmount()){
                                        filteredList.add(invoice);
                                    }
                                }

                                break;

                            case Constants.FILTER_OVERDUE:

                                //loop through invoice list
                                for (PurchaseInvoiceEntity invoice : purchaseInvoiceEntityList) {
                                    //find the credit sale type invoices
                                    if(invoice.getPaymentType() == Constants.PAY_TYPE_CREDIT_SALE){
                                        //check amount paid. find unpaid with balance payable
                                        if(invoice.getPaymentAmount() < invoice.getBillAmount()){
                                            //check the due date passed
                                            if(DateTimeUtils.checkDatePassed(invoice.getDate())){
                                                //if date exceeded, then show it on overdue
                                                filteredList.add(invoice);
                                            }
                                        }
                                    }
                                }

                                break;
                            default:
                                break;
                        }

                    }else {

                        //search
                        final String filterString = splitString[1];
                        String str_invoiceId;
                        String str_customerName;

                        for (PurchaseInvoiceEntity invoice : purchaseInvoiceEntityList) {
                            str_invoiceId = Constants.INVOICE_PREFIX + ""+invoice.getId();
                            str_customerName = invoice.getCustomerName().toLowerCase();
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
        void onClickPurchaseInvoice(int invoiceId);
    }
}
