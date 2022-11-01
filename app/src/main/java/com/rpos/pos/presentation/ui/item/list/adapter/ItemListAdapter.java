package com.rpos.pos.presentation.ui.item.list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.remote.dto.items.list.ItemData;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {

    private List<ItemData> itemList;
    private Context mContext;
    private String currencySymbol;
    private ItemListAdapter.OnItemClickListener listener;
    private String stockPrefixLabel;
    private String IN_STOCK, OUT_OF_STOCK;
    private boolean isMaintainStock;
    private String stockType;
    private String MAINTAIN_STOCK, INDEPENDENT;
    private String stockStatus;
    private String UOM_PREFIX;

    public ItemListAdapter(List<ItemData> _itemList, Context context, String currencySymbol,ItemListAdapter.OnItemClickListener listener){
        this.itemList = _itemList;
        mContext = context;
        this.currencySymbol = currencySymbol;
        this.listener = listener;

        stockPrefixLabel = mContext.getString(R.string.available_stock) + ": ";
        IN_STOCK = mContext.getString(R.string.in_stock);
        OUT_OF_STOCK = mContext.getString(R.string.out_of_stock);
        UOM_PREFIX = mContext.getString(R.string.uom) + " : ";

        stockType = this.mContext.getString(R.string.stock_type) + " : ";
        MAINTAIN_STOCK = this.mContext.getString(R.string.maintain);
        INDEPENDENT = this.mContext.getString(R.string.independent);
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

            ItemData item = itemList.get(position);
            isMaintainStock = (item.getMaintainStock() != 0); //get maintain stock flag

            if(!isMaintainStock){
                stockStatus = IN_STOCK;
            }else {
                stockStatus = (item.getAvailableQty()>0)?IN_STOCK:OUT_OF_STOCK;
            }

            holder.tv_itemName.setText(item.getItemName());
            holder.tv_item_stockstatus.setText(stockStatus);
            holder.tv_availstock.setText(stockPrefixLabel + ((isMaintainStock)?item.getAvailableQty():"0"));
            holder.tv_amount.setText(stockType + ((isMaintainStock)?MAINTAIN_STOCK:INDEPENDENT));
            holder.tv_unit.setText(UOM_PREFIX + item.getUomName());

            //on item click
            holder.itemView.setOnClickListener(view -> {

                if(listener!=null){
                    listener.onItemClick(item);
                }

            });

            //on remove
            holder.iv_remove.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onRemoveClick(item);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder{

        public ImageView iv_item;
        public ImageView iv_remove;
        public AppCompatTextView tv_itemName,tv_availstock,tv_unit,tv_item_stockstatus,tv_amount;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_item = itemView.findViewById(R.id.item_image);
            tv_itemName = itemView.findViewById(R.id.tv_itemName);
            tv_availstock = itemView.findViewById(R.id.tv_availstock);
            tv_unit = itemView.findViewById(R.id.tv_unit);
            tv_item_stockstatus = itemView.findViewById(R.id.tv_item_stockstatus);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            iv_remove = itemView.findViewById(R.id.iv_remove);
        }
    }


    public interface OnItemClickListener{
        void onItemClick(ItemData item);
        void onRemoveClick(ItemData item);
    }
}
