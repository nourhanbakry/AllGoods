package com.example.allgoods.UI.Seller.Inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.allgoods.R;
import com.example.allgoods.model.ProductModel;

import java.util.List;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private List<ProductModel> list;
    private OnInventoryActionListener listener;

    public InventoryAdapter(List<ProductModel> list, OnInventoryActionListener listener) {
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = list.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgDelete;
        TextView tvName, tvPrice, tvTotal, btnRestock;
        LinearLayout layoutSizes, layoutSizes2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnRestock = itemView.findViewById(R.id.btnRestock);
            layoutSizes = itemView.findViewById(R.id.layoutSizes);
            layoutSizes2 = itemView.findViewById(R.id.layoutSizes2);
        }

        void bind(ProductModel product, OnInventoryActionListener listener) {
            tvName.setText(product.getName());
            tvPrice.setText("$" + product.getPrice());
            
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(itemView.getContext()).load(product.getImage()).into(imgProduct);
            }

            // Clear and add sizes
            layoutSizes.removeAllViews();
            layoutSizes2.removeAllViews();

            Map<String, Integer> sizes = product.getSizesQuantity();
            int totalItems = 0;
            int count = 0;
            for (Map.Entry<String, Integer> entry : sizes.entrySet()) {
                TextView sizeView = new TextView(itemView.getContext());
                sizeView.setText(entry.getKey() + ": " + entry.getValue());
                sizeView.setTextSize(10);
                sizeView.setPadding(20, 10, 20, 10);
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 10, 0);
                sizeView.setLayoutParams(params);

                if (entry.getValue() > 0) {
                    sizeView.setBackgroundResource(R.drawable.bg_size_red);
                    sizeView.setTextColor(itemView.getContext().getResources().getColor(android.R.color.white));
                } else {
                    sizeView.setBackgroundResource(R.drawable.bg_size_white);
                    sizeView.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                }

                if (count < 3) {
                    layoutSizes.addView(sizeView);
                } else {
                    layoutSizes2.addView(sizeView);
                }
                totalItems += entry.getValue();
                count++;
            }

            tvTotal.setText("Total: " + totalItems + " items");

            imgDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDeleteClick(product, getAdapterPosition());
            });

            btnRestock.setOnClickListener(v -> {
                if (listener != null) listener.onRestockClick(product, getAdapterPosition());
            });
        }
    }

    public interface OnInventoryActionListener {
        void onDeleteClick(ProductModel product, int position);
        void onRestockClick(ProductModel product, int position);
    }
}
