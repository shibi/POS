package com.rpos.pos.presentation.ui.units.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.R;
import com.rpos.pos.data.remote.dto.uom.list.UomItem;


import java.util.List;

public class UomListAdapter extends RecyclerView.Adapter<UomListAdapter.UomViewHolder>{

    private List<UomItem> uomItemList;
    private UOMClickListener listener;

    public UomListAdapter(List<UomItem> uomItemList, UOMClickListener listener) {
        this.uomItemList = uomItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_uom_list_row, parent, false);
        return new UomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UomViewHolder holder, int position) {
        try {

            UomItem uomItem = uomItemList.get(position);
            holder.tv_name.setText(uomItem.getUomName());

            holder.iv_remove.setOnClickListener(null);
            holder.iv_remove.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onRemoveItem(uomItem);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (uomItemList!=null)?uomItemList.size():0;
    }

    public static class UomViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView tv_name;
        public ImageView iv_remove;

        public UomViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_uom_name);
            iv_remove = itemView.findViewById(R.id.iv_remove);
        }
    }

    public interface UOMClickListener{
        void onRemoveItem(UomItem item);
    }
}
