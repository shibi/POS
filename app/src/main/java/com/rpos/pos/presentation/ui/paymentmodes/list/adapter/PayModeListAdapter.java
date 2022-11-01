package com.rpos.pos.presentation.ui.paymentmodes.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.internal.$Gson$Preconditions;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.PaymentModeEntity;

import java.util.List;

public class PayModeListAdapter extends RecyclerView.Adapter<PayModeListAdapter.PayModeViewHolder>{

    private List<PaymentModeEntity> payModeList;
    private OnPaymentModeClickListener listener;

    public PayModeListAdapter(List<PaymentModeEntity> payModeList,OnPaymentModeClickListener listener) {
        this.payModeList = payModeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PayModeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_paymode_list_row,parent,false);
        return new PayModeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayModeViewHolder holder, int position) {
        try {

            PaymentModeEntity paymentMode = payModeList.get(position);

            holder.tv_payModeName.setText(paymentMode.getPaymentModeName());
            holder.tv_payType.setText(paymentMode.getCreatedOn());

            holder.iv_remove.setOnClickListener(null);
            holder.iv_remove.setOnClickListener(view -> {
                try{

                    if(listener!=null){
                        listener.onClickRemove(paymentMode);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (payModeList!=null)?payModeList.size():0;
    }

    public static class PayModeViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView tv_payModeName, tv_payType;
        public ImageView iv_remove;

        public PayModeViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_payModeName = itemView.findViewById(R.id.tv_paymode_name);
            tv_payType = itemView.findViewById(R.id.tv_paytype);

            iv_remove = itemView.findViewById(R.id.iv_remove);
        }
    }

    public interface OnPaymentModeClickListener{
        void onClickRemove(PaymentModeEntity paymentMode);
    }
}
