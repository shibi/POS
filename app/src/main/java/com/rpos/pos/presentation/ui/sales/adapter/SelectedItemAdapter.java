package com.rpos.pos.presentation.ui.sales.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.internal.$Gson$Preconditions;
import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.domain.models.item.PickedItem;
import com.rpos.pos.presentation.ui.common.SharedActivity;

import java.util.List;

public class SelectedItemAdapter extends RecyclerView.Adapter<SelectedItemAdapter.SelectedItemViewHolder>{

    private List<PickedItem> itemsList;
    private Context mContext;
    private OnPickedItemClick listener;
    private String currencySymbol;
    private int activity_parent;

    public SelectedItemAdapter(List<PickedItem> itemsList,Context context, String currencySymbol,int parent, OnPickedItemClick listener){
        this.itemsList = itemsList;
        this.mContext = context;
        this.currencySymbol = currencySymbol;
        this.listener = listener;
        this.activity_parent = parent;
    }

    @NonNull
    @Override
    public SelectedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_picked_item_list_row,parent,false);
        return new SelectedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedItemViewHolder holder, int position) {

        try {

            PickedItem item = itemsList.get(position);

            holder.tv_itemName.setText(item.getItemName());
            holder.tv_qty.setText(""+item.getQuantity());
            String amountFormated = currencySymbol + " "+item.getRate();
            String rate_label = mContext.getResources().getString(R.string.rate_label);
            String rate_display = rate_label + " : "+amountFormated;
            holder.tv_rate.setText(rate_display);
            String stock  = mContext.getResources().getString(R.string.available_stock) + " : "+item.getStock();
            holder.tv_stock.setText(stock);


            holder.iv_minus.setOnClickListener(view -> {
                try{
                    int quantity = item.getQuantity();
                    //Irrespective of SALE AND PURCHASE, Always check quantity is non zero before deducting
                    if(quantity > 0){
                        quantity--;
                    }

                    if(activity_parent == Constants.PARENT_SALES_ORDER_DETAILS){
                        int remainingStock = item.getStock() + 1;
                        item.setStock(remainingStock);
                        String str_stock  = mContext.getResources().getString(R.string.available_stock) + " : "+remainingStock;
                        holder.tv_stock.setText(str_stock);
                    }

                    updateQuantity(holder.tv_qty,item, quantity);

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            holder.iv_plus.setOnClickListener(view -> {
                try{
                    int quantity = item.getQuantity();
                    boolean isMaintainStock = item.getMaintainStock();

                    if(activity_parent == Constants.PARENT_SALES){
                        if(isMaintainStock){
                            if(quantity < item.getStock()){
                                quantity++;
                                updateQuantity(holder.tv_qty,item, quantity);
                            }else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.exceed_stock),Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            quantity++;
                            updateQuantity(holder.tv_qty,item, quantity);
                        }



                    }else if(activity_parent == Constants.PARENT_SALES_ORDER_DETAILS){
                        //SPECIFIC CASE
                        int overallStock = quantity + item.getStock();
                        if(isMaintainStock){
                            if(quantity < overallStock){
                                quantity++;
                                int remainingStock = item.getStock() - 1;
                                item.setStock(remainingStock);
                                String str_stock  = mContext.getResources().getString(R.string.available_stock) + " : "+remainingStock;
                                holder.tv_stock.setText(str_stock);
                            }else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.exceed_stock),Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            quantity++;
                        }
                        updateQuantity(holder.tv_qty,item, quantity);
                    }
                    else if(activity_parent == Constants.PARENT_PURCHASE){
                        quantity++;
                        updateQuantity(holder.tv_qty,item, quantity);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void updateQuantity(AppCompatTextView tv_qty, PickedItem item, int quantity){
        try{

            item.setQuantity(quantity);
            tv_qty.setText(""+quantity);
            if(listener!=null){
                listener.onItemQuantityChange(item);
            }

        }catch (Exception e){
            throw e;
        }
    }

    /*private String formatQuantityForDisplay(int quantity){
        try{
            String quantity_label = mContext.getResources().getString(R.string.quantity_label);
            String quantity_display = quantity_label+ " : "+quantity;
            return quantity_display;
        } catch (Exception e){
            e.printStackTrace();
            return "-1";
        }
    }*/

    @Override
    public int getItemCount() {
        return (itemsList!=null)?itemsList.size():0;
    }

    public static class SelectedItemViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView tv_itemName,tv_rate, tv_qty,tv_stock;
        public ImageView iv_plus,iv_minus;

        public SelectedItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_itemName = itemView.findViewById(R.id.tv_itemName);
            tv_rate = itemView.findViewById(R.id.tv_rate);

            iv_plus = itemView.findViewById(R.id.iv_plus);
            iv_minus = itemView.findViewById(R.id.iv_minus);
            tv_qty = itemView.findViewById(R.id.tv_qty_count);
            tv_stock = itemView.findViewById(R.id.tv_stock);
        }
    }

    public interface OnPickedItemClick{
        void onItemClick(PickedItem item);
        void onItemQuantityChange(PickedItem item);
    }
}
