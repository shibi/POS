package com.rpos.pos.presentation.ui.item.select;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.CoreApp;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.remote.dto.customer.list.CustomerData;

import java.util.ArrayList;
import java.util.List;

public class ItemPickerListAdapter extends RecyclerView.Adapter<ItemPickerListAdapter.ItemViewHolder> implements Filterable {

    private List<ItemEntity> itemList;
    private List<ItemEntity> filteredItemList;
    private Context mContext;
    private OnItemClickListener listener;
    private String currencySymbol;
    private String stockPrefixLabel;
    private String stockType;
    private String MAINTAIN_STOCK, INDEPENDENT;
    private boolean isMaintainStock;
    private String IN_STOCK, OUT_OF_STOCK;
    private String stockStatus;

    public ItemPickerListAdapter(List<ItemEntity> itemList, String currencySymbol, Context mContext, OnItemClickListener listener) {
        this.itemList = itemList;
        this.filteredItemList = itemList;
        this.mContext = mContext;
        this.listener = listener;
        this.currencySymbol = currencySymbol;

        stockPrefixLabel = this.mContext.getString(R.string.available_stock_full_label) + ": ";
        stockType = this.mContext.getString(R.string.stock_type) + " : ";
        MAINTAIN_STOCK = this.mContext.getString(R.string.maintain);
        INDEPENDENT = this.mContext.getString(R.string.independent);

        IN_STOCK = mContext.getString(R.string.in_stock);
        OUT_OF_STOCK = mContext.getString(R.string.out_of_stock);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_item_list_row,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        try {

            ItemEntity item = filteredItemList.get(position);
            isMaintainStock = (item.getMaintainStock() != 0); //get the maintain stock flag

            if(!isMaintainStock){
                stockStatus = IN_STOCK;
            }else {
                stockStatus = (item.getAvailableQty()>0)?IN_STOCK:OUT_OF_STOCK;
            }

            holder.tv_itemName.setText(item.getItemName());
            holder.tv_item_stockstatus.setText(stockStatus);

            holder.tv_availstock.setText(stockPrefixLabel + ((isMaintainStock)?item.getAvailableQty():"none"));
            holder.tv_amount.setText(stockType + ((isMaintainStock)?MAINTAIN_STOCK:INDEPENDENT));


            holder.itemView.setOnClickListener(view -> {

                if(listener!=null){
                    listener.onItemClick(item);
                }

            });

            //on remove
            holder.iv_remove.setVisibility(View.GONE);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (filteredItemList!=null)?filteredItemList.size():0;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView iv_item;
        public ImageView iv_remove;
        public AppCompatTextView tv_itemName,tv_availstock,tv_uom,tv_item_stockstatus,tv_amount,tv_billId;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_item = itemView.findViewById(R.id.item_image);
            tv_itemName = itemView.findViewById(R.id.tv_itemName);
            tv_availstock = itemView.findViewById(R.id.tv_availstock);
            //tv_billId = itemView.findViewById(R.id.tv_billId);
            //tv_uom = itemView.findViewById(R.id.tv_uom);
            tv_item_stockstatus = itemView.findViewById(R.id.tv_item_stockstatus);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            iv_remove = itemView.findViewById(R.id.iv_remove);
        }
    }


    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }

    private Filter Searched_Filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<ItemEntity> filteredList = new ArrayList<>();
            if (constraint == null || constraint.toString().isEmpty()) {
                filteredList.addAll(itemList);
            } else {

                try {

                    String filterString = constraint.toString().toLowerCase();
                    String itemNameLowerCase;
                    String barcode;
                    ItemEntity item;
                    for (int i = 0; i < itemList.size(); i++) {
                        item = itemList.get(i);
                        itemNameLowerCase = item.getItemName();
                        if (itemNameLowerCase.contains(filterString)) {
                            filteredList.add(item);
                            continue;
                        }else if(item.getBarcodes()!=null && item.getBarcodes().size()>0){
                            barcode = item.getBarcodes().get(0);
                            if(barcode.contains(filterString)){
                                filteredList.add(item);
                                continue;
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItemList = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener{
        void onItemClick(ItemEntity item);
    }
}
