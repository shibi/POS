package com.rpos.pos.presentation.ui.customer.list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.Constants;
import com.rpos.pos.R;
import com.rpos.pos.data.local.entity.CustomerEntity;
import com.rpos.pos.data.local.entity.InvoiceEntity;
import com.rpos.pos.data.remote.dto.Customer;
import com.rpos.pos.data.remote.dto.customer.list.CreditLimit;
import com.rpos.pos.data.remote.dto.customer.list.CustomerData;

import java.util.ArrayList;
import java.util.List;

public class CustomerListAdapter extends RecyclerView.Adapter<CustomerListAdapter.CustomerListViewHolder> implements Filterable {

    private OnClickCustomer listener;
    private List<CustomerEntity> customerList;
    private List<CustomerEntity> filteredCustomerList;
    private Context mContext;

    public CustomerListAdapter(Context context,List<CustomerEntity> _customerList, OnClickCustomer listener){
        this.customerList = _customerList;
        this.filteredCustomerList = _customerList;
        this.listener = listener;
        mContext = context;
    }

    @NonNull
    @Override
    public CustomerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_customer_list_item, parent, false);
        return new CustomerListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerListViewHolder holder, int position) {
        try {

            CustomerEntity customer = filteredCustomerList.get(position);

            holder.tv_name.setText(customer.getCustomerName());

            String mobile = mContext.getString(R.string.mobile_field)+customer.getMobileNo();
            holder.tv_phone.setText(mobile);

            String custId = "ID : #" + customer.getCustomerId();
            holder.tv_custId.setText(custId);

            holder.itemView.setOnClickListener(view -> {
                try{
                    if(listener!=null){
                        listener.onViewCustomer(customer);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            holder.ll_remove.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onRemoveCustomer(customer);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (filteredCustomerList!=null)?filteredCustomerList.size():0;
    }

    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }

    public static class CustomerListViewHolder extends RecyclerView.ViewHolder{

        public AppCompatTextView tv_name,tv_phone,tv_custId;
        public LinearLayout ll_remove;

        public CustomerListViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_customerName);
            tv_phone = itemView.findViewById(R.id.tv_customerPhone);
            ll_remove = itemView.findViewById(R.id.ll_remove);
            tv_custId = itemView.findViewById(R.id.tv_customerId);
        }
    }

    private Filter Searched_Filter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<CustomerEntity> filteredList = new ArrayList<>();

            if (constraint == null || constraint.toString().isEmpty()) {
                filteredList.addAll(customerList);
            } else {

                try {

                    String filterString = constraint.toString().toLowerCase();
                    String customerNameLowerCase;
                    String mobileNumber;
                    for (int i = 0; i < customerList.size(); i++) {

                        customerNameLowerCase = customerList.get(i).getCustomerName().toLowerCase();
                        mobileNumber = customerList.get(i).getMobileNo();
                        if (customerNameLowerCase.contains(filterString) || mobileNumber.contains(filterString)) {
                            filteredList.add(customerList.get(i));
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
            filteredCustomerList = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    public interface OnClickCustomer{
        void onViewCustomer(CustomerEntity customer);
        void onRemoveCustomer(CustomerEntity customer);
    }
}
