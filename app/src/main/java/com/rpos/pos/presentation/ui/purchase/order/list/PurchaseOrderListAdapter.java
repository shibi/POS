package com.rpos.pos.presentation.ui.purchase.order.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.PurchaseOrderEntity;
import java.util.List;

public class PurchaseOrderListAdapter extends RecyclerView.Adapter<PurchaseOrderListAdapter.PurchaseOrderViewHolder>{

    private List<PurchaseOrderEntity> purchaseOrderList;
    private PurchaseOrderListItemClick listener;


    public PurchaseOrderListAdapter(List<PurchaseOrderEntity> purchaseOrderList , PurchaseOrderListItemClick listener) {
        this.purchaseOrderList = purchaseOrderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PurchaseOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_purchase_order_row, parent,false);
        return new PurchaseOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseOrderViewHolder holder, int position) {
        try{

            PurchaseOrderEntity orderEntity = purchaseOrderList.get(position);

            holder.tv_supplierName.setText(orderEntity.getSupplierName());
            holder.tv_date.setText(orderEntity.getDateTime());

            holder.itemView.setOnClickListener(null);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onClickPurchaseOrderItem(orderEntity.getId(), orderEntity.getSupplierId(), orderEntity.getSupplierName());
                    }
                }
            });

            holder.iv_remove.setOnClickListener(null);
            holder.iv_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onRemove(orderEntity.getId());
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return purchaseOrderList!=null ? purchaseOrderList.size():0;
    }

    public static class PurchaseOrderViewHolder extends RecyclerView.ViewHolder{
        private AppCompatTextView tv_supplierName, tv_date;
        private ImageView iv_remove;
        public PurchaseOrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_supplierName = itemView.findViewById(R.id.tv_customerName);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_remove = itemView.findViewById(R.id.iv_remove);
        }
    }

    public interface PurchaseOrderListItemClick{
        void onClickPurchaseOrderItem(int orderId, int supplierId, String supplierName);
        void onRemove(int orderId);
    }
}
