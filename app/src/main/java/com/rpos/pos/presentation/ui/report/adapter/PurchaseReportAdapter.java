package com.rpos.pos.presentation.ui.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.PurchaseInvoiceEntity;

import java.util.List;

public class PurchaseReportAdapter extends RecyclerView.Adapter<PurchaseReportAdapter.SalesReportViewHolder>{

    private List<PurchaseInvoiceEntity> invoiceEntityList;
    private String DATE_PREFIX, MOP_PREFIX,NET_PREFIX,ID_PREFIX , GRAND_TOTAL_PREFIX, TAX_PREFIX;
    private String SEPARATOR = " : ";
    private float netAmount;

    public PurchaseReportAdapter(Context context, List<PurchaseInvoiceEntity> invoiceEntityList) {
        this.invoiceEntityList = invoiceEntityList;

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

        PurchaseInvoiceEntity invoice = invoiceEntityList.get(position);

        holder.tv_supplierName.setText(invoice.getCustomerName());
        holder.tv_id.setText(ID_PREFIX + invoice.getId());
        holder.tv_date.setText(DATE_PREFIX + invoice.getDate());
        holder.tv_mop.setText(MOP_PREFIX + invoice.getGrossAmount());
        holder.tv_grosstotal.setText(GRAND_TOTAL_PREFIX + invoice.getBillAmount());

        netAmount = invoice.getGrossAmount() - invoice.getDiscountAmount();

        holder.tv_netTotal.setText(NET_PREFIX + netAmount);
        holder.tv_tax.setText(TAX_PREFIX + invoice.getTaxAmount());

    }

    @Override
    public int getItemCount() {
        return (invoiceEntityList!=null)?invoiceEntityList.size():0;
    }

    public static class SalesReportViewHolder extends RecyclerView.ViewHolder{
        private AppCompatTextView tv_supplierName, tv_date,tv_grosstotal, tv_netTotal,tv_mop,tv_id,tv_tax;
        public SalesReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_supplierName = itemView.findViewById(R.id.tv_custName);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_grosstotal = itemView.findViewById(R.id.tv_grantTotal);
            tv_netTotal = itemView.findViewById(R.id.tv_netAmount);
            tv_mop = itemView.findViewById(R.id.tv_mop);
            tv_id = itemView.findViewById(R.id.tv_id);
            tv_tax = itemView.findViewById(R.id.tv_tax);

        }
    }

}
