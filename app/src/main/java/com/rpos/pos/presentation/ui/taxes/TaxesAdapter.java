package com.rpos.pos.presentation.ui.taxes;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.TaxSlabEntity;
import com.rpos.pos.data.remote.dto.tax.list.TaxData;

import java.util.List;

public class TaxesAdapter extends RecyclerView.Adapter<TaxesAdapter.TaxesViewHolder> {

    private List<TaxData> taxSlabEntityList;
    private TaxClickListener listener;
    private Context mContext;
    private int lastSelectedId;


    public TaxesAdapter(Context context,List<TaxData> taxSlabEntityList, TaxClickListener listener) {
        this.taxSlabEntityList = taxSlabEntityList;
        this.listener = listener;
        this.mContext = context;
    }

    @NonNull
    @Override
    public TaxesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_taxes_slab_row,parent, false);
        return new TaxesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxesViewHolder holder, int position) {

        TaxData taxes = taxSlabEntityList.get(position);

        holder.tv_slabName.setText(taxes.getSlabName());
        holder.tv_slabAmount.setText(""+taxes.getTaxPercentage());


        //CODE TO SELECT TAX AS DEFAULT
        /*Drawable backgroundDrawable;
        if(taxes.isSelected()){
            backgroundDrawable = mContext.getResources().getDrawable(R.drawable.shape_selected_highlight_no_curve);
            holder.tv_default.setText(R.string.default_label);
            holder.iv_remove.setVisibility(View.GONE);
        }else {
            backgroundDrawable = mContext.getResources().getDrawable(R.drawable.shape_primary_button_bg_selector_no_curve);
            holder.tv_default.setText("");
            holder.iv_remove.setVisibility(View.VISIBLE);
        }*
        holder.ll_frame.setBackground(backgroundDrawable);*/


        holder.iv_remove.setOnClickListener(view -> {
            try{

                if(listener!=null){
                    listener.onClickRemove(taxes);
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        });

        holder.iv_view.setOnClickListener(view -> {
            if(listener!=null){
                listener.onClickTaxView(taxes.getTaxSlabId());
            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i=0;i<taxSlabEntityList.size();i++){
                    taxSlabEntityList.get(i).setSelected(false);
                }
                taxes.setSelected(true);
                lastSelectedId = taxes.getId();

                if(listener!=null){
                    listener.onSelectTaxDefault(taxes.getId(),taxes.getTaxSlabName(),taxes.getSlabAmount());
                }

                notifyDataSetChanged();
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return (taxSlabEntityList!=null)?taxSlabEntityList.size():0;
    }

    public static class TaxesViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tv_slabName,tv_slabAmount,tv_default;
        private ImageView iv_remove,iv_view;
        private LinearLayout ll_frame;

        public TaxesViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_slabName = itemView.findViewById(R.id.tv_slab_name);
            tv_slabAmount = itemView.findViewById(R.id.tv_taxAmount);
            iv_remove = itemView.findViewById(R.id.iv_remove);
            iv_view = itemView.findViewById(R.id.iv_view);
            ll_frame = itemView.findViewById(R.id.ll_tax_frame);
            tv_default = itemView.findViewById(R.id.tv_default);
        }
    }

    public interface TaxClickListener{
        void onSelectTaxDefault(int taxid, String slabName,float taxRate);
        void onClickTaxView(int taxid);
        void onClickRemove(TaxData tax);
    }
}
