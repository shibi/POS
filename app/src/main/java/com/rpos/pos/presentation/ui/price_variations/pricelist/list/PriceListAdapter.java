package com.rpos.pos.presentation.ui.price_variations.pricelist.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.PriceListEntity;

import java.util.List;

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.PriceListHolder>{

    private List<PriceListEntity> priceList;
    private Context mContext;
    private PriceListViewListener listener;

    public PriceListAdapter(Context context,List<PriceListEntity> priceList, PriceListViewListener listener) {
        this.mContext = context;
        this.priceList = priceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PriceListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_price_list_row, parent, false);
        return new PriceListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceListHolder holder, int position) {
        try{

            PriceListEntity priceListEntity = priceList.get(position);
            String typeName = (priceListEntity.getPriceType() == 0)? mContext.getString(R.string.buying): mContext.getString(R.string.selling);
            String priceType = mContext.getString(R.string.type)+ ": "+ typeName;

            holder.tv_priceListName.setText(priceListEntity.getPriceListName());
            holder.tv_type.setText(priceType);

            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onClickRemove(priceListEntity);
                    }
                }
            });

            holder.itemView.setOnClickListener(null);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onClickPriceList(priceListEntity);
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return priceList!=null?priceList.size():0;
    }

    public static class PriceListHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView tv_priceListName, tv_type;
        private ImageView iv_remove;

        public PriceListHolder(@NonNull View itemView) {
            super(itemView);

            tv_priceListName = itemView.findViewById(R.id.tv_pricelist_name);
            tv_type = itemView.findViewById(R.id.tv_priceListType);
            iv_remove = itemView.findViewById(R.id.iv_remove);
        }
    }

    public interface PriceListViewListener{
        void onClickPriceList(PriceListEntity priceListEntity);
        void onClickRemove(PriceListEntity priceListEntity);
    }
}
