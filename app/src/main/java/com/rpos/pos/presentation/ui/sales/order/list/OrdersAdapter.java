package com.rpos.pos.presentation.ui.sales.order.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.OrderEntity;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>{

    private List<OrderEntity> orderList;
    private OrderClickListener listener;

    public OrdersAdapter(List<OrderEntity> orderList, OrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_order_row,parent,false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        try{

            OrderEntity order = orderList.get(position);

            holder.tv_customerName.setText(order.getCustomerName());
            holder.tv_date.setText(order.getDateTime());

            holder.itemView.setOnClickListener(view -> {
                try{

                    if(listener!=null){
                        listener.onOrderClick(order);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            //clear previous listeners
            holder.iv_remove.setOnClickListener(null);
            holder.iv_remove.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onRemoveClick(order);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (orderList!=null)?orderList.size():0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tv_customerName,tv_date;
        private ImageView iv_remove;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_customerName = itemView.findViewById(R.id.tv_customerName);
            tv_date = itemView.findViewById(R.id.tv_date);
            iv_remove = itemView.findViewById(R.id.iv_remove);

        }
    }

    public interface OrderClickListener{
        void onOrderClick(OrderEntity order);
        void onRemoveClick(OrderEntity order);
    }
}
