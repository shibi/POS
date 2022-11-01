package com.rpos.pos.presentation.ui.address.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.remote.dto.address.list.AddressListMessage;

import java.util.List;

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressListViewHolder>{

    private List<AddressListMessage> addressList;
    public AddressListAdapter(List<AddressListMessage> addressList){
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_address_list_row,parent,false);
        return new AddressListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressListViewHolder holder, int position) {
        try{

            AddressListMessage addressObj = addressList.get(position);

            holder.tv_supplierName.setText(addressObj.getAddressTitle());
            holder.tv_address1.setText(addressObj.getState());
            holder.tv_address2.setText(addressObj.getAddressLine1());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (addressList!=null)?addressList.size():0;
    }

    public static class AddressListViewHolder extends RecyclerView.ViewHolder{

        private AppCompatTextView tv_supplierName,tv_address1,tv_address2;

        public AddressListViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_supplierName = itemView.findViewById(R.id.tv_supplierName);
            tv_address1 = itemView.findViewById(R.id.tv_address1);
            tv_address2 = itemView.findViewById(R.id.tv_address2);
        }
    }
}
