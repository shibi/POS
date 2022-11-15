package com.rpos.pos.presentation.ui.report.adapter;

import android.content.Context;
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
import java.util.Locale;

public class SalesReportAdapter extends RecyclerView.Adapter<SalesReportAdapter.SalesReportViewHolder> implements Filterable {

    private List<InvoiceEntity> invoiceEntityList;
    private List<InvoiceEntity> filteredInvoiceList;
    private String DATE_PREFIX, MOP_PREFIX,NET_PREFIX,ID_PREFIX , GRAND_TOTAL_PREFIX, TAX_PREFIX;
    private String SEPARATOR = " : ";
    private float netAmount;

    public SalesReportAdapter(Context context, List<InvoiceEntity> invoiceEntityList) {
        this.invoiceEntityList = invoiceEntityList;
        filteredInvoiceList = invoiceEntityList;


        DATE_PREFIX = context.getResources().getString(R.string.date_label) + SEPARATOR;
        MOP_PREFIX = context.getResources().getString(R.string.mop) + SEPARATOR;
        NET_PREFIX = context.getResources().getString(R.string.net_amount) + SEPARATOR;
        GRAND_TOTAL_PREFIX = context.getResources().getString(R.string.gross_amount) + SEPARATOR;
        ID_PREFIX = "INV" + SEPARATOR;
        TAX_PREFIX = context.getResources().getString(R.string.tax_label) + SEPARATOR;
    }

    @NonNull
    @Override
    public SalesReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_sales_report_list_item, parent,false);
        return new SalesReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesReportViewHolder holder, int position) {

        InvoiceEntity invoice = filteredInvoiceList.get(position);

        holder.tv_custName.setText(invoice.getCustomerName());
        holder.tv_id.setText(ID_PREFIX + invoice.getId());
        holder.tv_date.setText(DATE_PREFIX + DateTimeUtils.convertTimerStampToDateTime(invoice.getTimestamp()));
        holder.tv_mop.setText(MOP_PREFIX + invoice.getGrossAmount());
        holder.tv_grosstotal.setText(GRAND_TOTAL_PREFIX + invoice.getBillAmount());

        netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();

        holder.tv_netTotal.setText(NET_PREFIX + netAmount);
        holder.tv_tax.setText(TAX_PREFIX + invoice.getTaxAmount());

    }

    @Override
    public int getItemCount() {
        return (filteredInvoiceList!=null)?filteredInvoiceList.size():0;
    }

    public static class SalesReportViewHolder extends RecyclerView.ViewHolder{
        private AppCompatTextView tv_custName, tv_date,tv_grosstotal, tv_netTotal,tv_mop,tv_id,tv_tax;
        public SalesReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_custName = itemView.findViewById(R.id.tv_custName);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_grosstotal = itemView.findViewById(R.id.tv_grantTotal);
            tv_netTotal = itemView.findViewById(R.id.tv_netAmount);
            tv_mop = itemView.findViewById(R.id.tv_mop);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_tax = itemView.findViewById(R.id.tv_tax);

        }
    }

    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }

    private Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<InvoiceEntity> filteredList = new ArrayList<>();
            if (constraint == null) {
                filteredList.addAll(invoiceEntityList);
            } else {
                String[] splitString = constraint.toString().split(":");
                if(splitString.length==0 || splitString.length ==1){
                    filteredList.addAll(invoiceEntityList);
                }else {

                    final long fromTimestamp = Long.parseLong(splitString[0]);
                    final long toTimestamp = Long.parseLong(splitString[1]);

                    for (InvoiceEntity invoice: invoiceEntityList){
                        if(invoice.getTimestamp()>= fromTimestamp && invoice.getTimestamp() <= toTimestamp){
                            filteredList.add(invoice);
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

    public Filter secondFilter(){
        return secondFilter;
    }

    private Filter secondFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<InvoiceEntity> filteredList = new ArrayList<>();
            if (constraint == null) {
                filteredList.addAll(invoiceEntityList);
            } else {
                String filterString = constraint.toString().toLowerCase(Locale.ROOT);
                if(filterString.isEmpty()){
                    filteredList.addAll(invoiceEntityList);
                }else {
                    String customerName;
                    for (InvoiceEntity invoice: invoiceEntityList){
                        customerName = invoice.getCustomerName().toLowerCase();
                        if(customerName.contains(filterString)){
                            filteredList.add(invoice);
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
