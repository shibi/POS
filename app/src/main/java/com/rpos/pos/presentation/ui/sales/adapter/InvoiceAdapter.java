package com.rpos.pos.presentation.ui.sales.adapter;


import android.content.Context;
import android.graphics.Color;
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
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.domain.utils.DateTimeUtils;

import java.util.ArrayList;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> implements Filterable {

    private ArrayList<InvoiceEntity> invoicesList;
    private OnInvoiceClickListener listener;
    private ArrayList<InvoiceEntity> filteredInvoiceList;
    //private Context mContext;

    private String formatedDate;
    private boolean isCreditSale;
    private String paymentStatus;
    private String date;

    private String amount_prefix_label;
    private String cust_prefix_label, date_prefix_label;
    private String CANCELLED;


    public InvoiceAdapter(Context context,ArrayList<InvoiceEntity> _invoicesList, OnInvoiceClickListener listener){
        this.invoicesList = _invoicesList;
        this.listener = listener;
        this.filteredInvoiceList = _invoicesList;
        //this.mContext = context;

        amount_prefix_label = context.getResources().getString(R.string.amount_label) + " : ";
        cust_prefix_label = context.getString(R.string.cust_label) + " : ";
        date_prefix_label = context.getString(R.string.date_label) + " : ";

        CANCELLED = context.getResources().getString(R.string.cancelled);
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_invoice_list_item, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        try {

            InvoiceEntity invoice = filteredInvoiceList.get(position);

            holder.tv_invoiceid.setText("SNo : "+invoice.getId());
            holder.tv_name.setText(cust_prefix_label + invoice.getCustomerName());
            holder.tv_invoiceAmount.setText(amount_prefix_label + invoice.getBillAmount());
            holder.tv_custId.setText("INV#"+invoice.getId());

            try {

                date = DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp());
                if(date!=null && !date.isEmpty()){
                    formatedDate = date;
                }else {
                    formatedDate = "date : none";
                }

                holder.tv_invoiceDate.setText(date_prefix_label+ formatedDate);
            }catch (Exception e){
                holder.tv_invoiceDate.setText("date : none");
                e.printStackTrace();
            }


            if(invoice.getStatus().equals(Constants.PAYMENT_PAID)){
                holder.tv_status.setTextColor(Color.GREEN);
                paymentStatus = Constants.PAYMENT_PAID;
            }else if(invoice.getStatus().equals(Constants.PAYMENT_RETURN)){
                holder.tv_status.setTextColor(Color.BLUE);
                paymentStatus = Constants.PAYMENT_RETURN;
            } else if(invoice.getStatus().equals(Constants.PAYMENT_UNPAID)){
                //check whether invoice is credit sale
                isCreditSale = (invoice.getPaymentType() == Constants.PAY_TYPE_CREDIT_SALE);
                if(isCreditSale){
                    if(DateTimeUtils.checkDatePassed(invoice.getDueDate())) {
                        paymentStatus = Constants.PAYMENT_OVERDUE;
                    }else {
                        paymentStatus = Constants.PAYMENT_UNPAID;
                    }
                }else {
                    paymentStatus = Constants.PAYMENT_UNPAID;
                }
                holder.tv_status.setTextColor(Color.RED);
            }

            holder.tv_status.setText(paymentStatus);


            if(!invoice.getStatus().equals(Constants.PAYMENT_RETURN)) {
                //cancel click
                holder.btn_cancel.setOnClickListener(null); //clearing previous listeners
                holder.btn_cancel.setOnClickListener(view -> {
                    try {
                        if (listener != null) {
                            listener.onInvoiceCancel(invoice);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }else {
                holder.btn_cancel.setText(CANCELLED);
            }

            //item click
            holder.itemView.setOnTouchListener(null); //clearing previous listeners
            holder.itemView.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onInvoiceClick(invoice);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (filteredInvoiceList!=null)?filteredInvoiceList.size():0;
    }

    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }

    public static class InvoiceViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView tv_invoiceid, tv_name,tv_invoiceDate,tv_invoiceAmount,tv_custId,tv_status;
        public AppCompatButton btn_cancel;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_invoiceid = itemView.findViewById(R.id.tv_invoiceId);
            tv_name = itemView.findViewById(R.id.tv_customerName);
            tv_invoiceAmount = itemView.findViewById(R.id.tv_invoiceAmount);
            tv_invoiceDate = itemView.findViewById(R.id.tv_invoiceDate);
            tv_custId = itemView.findViewById(R.id.tv_customerId);
            tv_status = itemView.findViewById(R.id.tv_customerStatus);
            btn_cancel = itemView.findViewById(R.id.btn_cancel);
        }
    }

    public interface OnInvoiceClickListener{
        void onInvoiceClick(InvoiceEntity invoice);
        void onInvoiceCancel(InvoiceEntity invoice);
    }

    private Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<InvoiceEntity> filteredList = new ArrayList<>();
            if (constraint == null) {

                filteredList.addAll(invoicesList);

            } else {

                String[] splitString = constraint.toString().split(":");
                if(splitString==null || splitString.length==1){
                    filteredList.addAll(invoicesList);
                }else {
                    if(splitString[0].equals(Constants.FILTER_SORT)){
                        //sort
                        final int filterID = Integer.parseInt(splitString[1]);
                        switch (filterID)
                        {
                            case Constants.FILTER_ALL:
                                filteredList.addAll(invoicesList);
                                break;
                            case Constants.FILTER_PAID:

                                for (InvoiceEntity invoice : invoicesList) {
                                    if(invoice.getStatus().equals(Constants.PAYMENT_PAID)){
                                        filteredList.add(invoice);
                                    }
                                }

                                break;
                            case Constants.FILTER_UNPAID:

                                for (InvoiceEntity invoice : invoicesList) {
                                    if(invoice.getStatus().equals(Constants.PAYMENT_UNPAID)){
                                        filteredList.add(invoice);
                                    }
                                }

                                break;

                            case Constants.FILTER_OVERDUE:

                                //loop through invoice list
                                for (InvoiceEntity invoice : invoicesList) {
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
                                }
                                break;
                            case Constants.FILTER_RETURN:

                                //loop through invoice list
                                for (InvoiceEntity invoice : invoicesList) {
                                    //check status for return
                                    if(invoice.getStatus().equals(Constants.PAYMENT_RETURN)){
                                        //if date exceeded, then show it on overdue
                                        filteredList.add(invoice);
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

                        for (InvoiceEntity invoice : invoicesList) {
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
            filteredInvoiceList = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };


}
