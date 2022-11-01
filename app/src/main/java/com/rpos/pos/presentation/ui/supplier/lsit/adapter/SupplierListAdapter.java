package com.rpos.pos.presentation.ui.supplier.lsit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.local.dao.SupplierDao;
import com.rpos.pos.data.local.entity.ItemEntity;
import com.rpos.pos.data.remote.dto.suppliers.list.SuppliersData;

import java.util.ArrayList;
import java.util.List;

public class SupplierListAdapter extends RecyclerView.Adapter<SupplierListAdapter.SupplierListViewHolder> implements Filterable {

    private List<SuppliersData> suppliersDataList;
    private List<SuppliersData> filteredSupplierList;
    private SupplierClickListener listener;

    public SupplierListAdapter(List<SuppliersData> suppliersDataList, SupplierClickListener listener){
        this.suppliersDataList = suppliersDataList;
        this.filteredSupplierList = suppliersDataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupplierListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_suppliers_list_row, parent,false);
        return new SupplierListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierListViewHolder holder, int position) {
       try{

           SuppliersData suppliersData = filteredSupplierList.get(position);

           holder.tv_supplierName.setText(suppliersData.getSupplierName());
           holder.tv_id.setText("#1548"+position);
           holder.tv_place.setText("place : none");

           holder.itemView.setOnClickListener(null);
           holder.itemView.setOnClickListener(view -> {
               if(listener!=null){
                   listener.onClickSupplier(suppliersData);
               }
           });

           holder.iv_remove.setOnClickListener(null);
           holder.iv_remove.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(listener!=null){
                       listener.onRemove(suppliersData);
                   }
               }
           });

       }catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public int getItemCount() {
        return (filteredSupplierList!=null)?filteredSupplierList.size():0;
    }

    public static class SupplierListViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tv_supplierName,tv_id,tv_place;
        private ImageView iv_remove;

        public SupplierListViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_supplierName = itemView.findViewById(R.id.tv_supplierName);
            tv_id = itemView.findViewById(R.id.tv_sup_id);
            tv_place = itemView.findViewById(R.id.tv_place);
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

            ArrayList<SuppliersData> filteredList = new ArrayList<>();
            if (constraint == null || constraint.toString().isEmpty()) {
                filteredList.addAll(suppliersDataList);
            } else {

                try {

                    String filterString = constraint.toString().toLowerCase();
                    String itemNameLowerCase;
                    SuppliersData supplier;
                    for (int i = 0; i < suppliersDataList.size(); i++) {
                        supplier = suppliersDataList.get(i);
                        itemNameLowerCase = supplier.getSupplierName().toLowerCase();
                        if (itemNameLowerCase.contains(filterString)) {
                            filteredList.add(supplier);
                            continue;
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
            filteredSupplierList= (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    public interface SupplierClickListener{
        void onClickSupplier(SuppliersData supplier);
        void onRemove(SuppliersData supplier);
    }
}
