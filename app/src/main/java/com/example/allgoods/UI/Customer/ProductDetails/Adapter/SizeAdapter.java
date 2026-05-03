package com.example.allgoods.UI.Customer.ProductDetails.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.allgoods.R;
import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    private final List<String> sizes;
    private int selectedPosition = -1;
    private final OnSizeSelectedListener listener;

    public interface OnSizeSelectedListener {
        void onSizeSelected(String size);
    }

    public SizeAdapter(List<String> sizes, OnSizeSelectedListener listener) {
        this.sizes = sizes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_size_selection, parent, false);
        return new SizeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String size = sizes.get(position);
        holder.tvSize.setText(size);

        if (selectedPosition == position) {
            holder.tvSize.setBackgroundResource(R.drawable.bg_black_rounded);
            holder.tvSize.setTextColor(holder.itemView.getContext().getColor(R.color.white));
        } else {
            holder.tvSize.setBackgroundResource(R.drawable.bg_gray_fa);
            holder.tvSize.setTextColor(holder.itemView.getContext().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            listener.onSizeSelected(size);
        });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    static class SizeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSize;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSize = itemView.findViewById(R.id.tvSize);
        }
    }
}
