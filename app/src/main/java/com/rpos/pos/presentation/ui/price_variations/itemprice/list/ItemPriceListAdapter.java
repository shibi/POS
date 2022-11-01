package com.rpos.pos.presentation.ui.price_variations.itemprice.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.ItemPriceEntity;

import java.util.List;

public class ItemPriceListAdapter extends RecyclerView.Adapter<ItemPriceListAdapter.ItemPriceListViewHolder>{

    private List<ItemPriceEntity> itemPriceList;
    private Context mContext;
    private String typeLabel;
    private String rateLabel;
    private ItemPriceListListener listener;

    public ItemPriceListAdapter(Context mContext,List<ItemPriceEntity> itemPriceList,ItemPriceListListener listener) {
        this.itemPriceList = itemPriceList;
        this.mContext = mContext;
        this.listener = listener;
        
        typeLabel = mContext.getString(R.string.type);
        rateLabel = mContext.getString(R.string.rate);
    }

    @NonNull
    @Override
    public ItemPriceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_item_price_list_row, parent,false);
        return new ItemPriceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemPriceListViewHolder holder, int position) {
        try {

            ItemPriceEntity itemPriceEntity = itemPriceList.get(position);
            
            String type = typeLabel + " : " + ((itemPriceEntity.getPriceListType() == Constants.BUYING) ? mContext.getString(R.string.buying):mContext.getString(R.string.selling));
            String data = rateLabel+" : "+itemPriceEntity.getRate();
            holder.tv_name.setText(itemPriceEntity.getItemName());
            holder.tv_priceListName.setText(type);
            holder.tv_type.setText(data);


            holder.itemView.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onClickItemPrice(itemPriceEntity);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (itemPriceList!=null)?itemPriceList.size():0;
    }

    public static class ItemPriceListViewHolder extends RecyclerView.ViewHolder {
        private AppCompatTextView tv_name ,tv_priceListName, tv_type;
        public ItemPriceListViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_itemPriceName);
            tv_priceListName = itemView.findViewById(R.id.tv_pl_name);
            tv_type = itemView.findViewById(R.id.tv_type);
        }
    }

    public interface ItemPriceListListener{
        void onClickItemPrice(ItemPriceEntity itemPriceEntity);
    }
}
