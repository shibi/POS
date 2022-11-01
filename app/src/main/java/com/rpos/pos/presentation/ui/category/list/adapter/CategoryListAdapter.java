package com.rpos.pos.presentation.ui.category.list.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.rpos.pos.R;
import com.rpos.pos.data.remote.dto.category.list.CategoryItem;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>{

    private ArrayList<CategoryItem> categoryArray;
    private CategoryClickListener listener;

    public CategoryListAdapter(ArrayList<CategoryItem> _categoryArray,CategoryClickListener _listener){
        this.categoryArray = _categoryArray;
        this.listener = _listener;
    }

    @NonNull
    @Override
    public CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_categorylist_item,parent,false);
        return new CategoryListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListViewHolder holder, int position) {
        try {

            //single item
            CategoryItem _category = categoryArray.get(position);
            String categoryName = _category.getCategory();

           holder.tv_categoryName.setText(categoryName);
           holder.tv_itemCount.setText("items : 0");



           String cateFirstLetter = categoryName.substring(0,1);
           holder.tv_categoryFirstLetter.setText(cateFirstLetter.toUpperCase(Locale.US));


            //item click listener
            holder.itemView.setOnClickListener(view -> {
                try{

                    if(listener!=null){
                        listener.onClickCategoryItem(_category);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            });

            //item remove
            holder.iv_remove.setOnClickListener(view -> {
                if(listener!=null){
                    listener.onRemoveClick(_category);
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return (categoryArray!=null)?categoryArray.size():0;
    }

    public static class CategoryListViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout ll_color_circle;
        public AppCompatTextView tv_categoryName,tv_itemCount,tv_categoryStatus,tv_billId, tv_categoryFirstLetter;
        public ImageView iv_remove;

        public CategoryListViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_color_circle = itemView.findViewById(R.id.ll_color_circle);
            tv_categoryName = itemView.findViewById(R.id.tv_categoryName);
            tv_itemCount = itemView.findViewById(R.id.tv_item_count);
            tv_categoryStatus = itemView.findViewById(R.id.tv_item_stockstatus);
            tv_categoryFirstLetter = itemView.findViewById(R.id.tv_categoryLetter);
            iv_remove= itemView.findViewById(R.id.iv_remove);

        }
    }

    public interface CategoryClickListener{
        void onClickCategoryItem(CategoryItem category);
        void onRemoveClick(CategoryItem category);
    }
}
