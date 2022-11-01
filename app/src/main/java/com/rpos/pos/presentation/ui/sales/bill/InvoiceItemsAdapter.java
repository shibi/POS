package com.rpos.pos.presentation.ui.sales.bill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.InvoiceItemHistory;
import java.util.List;

public class InvoiceItemsAdapter extends RecyclerView.Adapter<InvoiceItemsAdapter.InvoiceItemViewHolder>{

    private List<InvoiceItemHistory> invoiceItemsList;

    public InvoiceItemsAdapter(List<InvoiceItemHistory> invoiceItemsList) {
        this.invoiceItemsList = invoiceItemsList;
    }

    @NonNull
    @Override
    public InvoiceItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_invoice_billed_item_row,parent,false);
        return new InvoiceItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceItemViewHolder holder, int position) {
        try {

            InvoiceItemHistory invoiceItemData = invoiceItemsList.get(position);

            holder.tv_itemName.setText(invoiceItemData.getItemName());
            holder.tv_itemDescription.setText(invoiceItemData.getItemDescription());
            holder.tv_itemQty.setText(""+invoiceItemData.getQuantity());
            holder.tv_itemRate.setText(""+invoiceItemData.getItemRate());
            holder.tv_itemTotal.setText(""+invoiceItemData.getTotal());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (invoiceItemsList!=null)?invoiceItemsList.size():0;
    }

    static class InvoiceItemViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView tv_itemName,tv_itemDescription,tv_itemQty,tv_itemRate,tv_itemTotal;
        public InvoiceItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_itemName = itemView.findViewById(R.id.tv_invoiceItemName);
            tv_itemDescription = itemView.findViewById(R.id.tv_invoiceItemDescript);
            tv_itemQty = itemView.findViewById(R.id.tv_invoiceItemQty);
            tv_itemRate = itemView.findViewById(R.id.tv_invoiceItemRate);
            tv_itemTotal = itemView.findViewById(R.id.tv_invoiceItemTotal);

        }
    }
}
