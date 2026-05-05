package com.example.allgoods.UI.Seller.Stats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.allgoods.R;
import java.util.List;
import java.util.Locale;

public class TopSellingAdapter extends RecyclerView.Adapter<TopSellingAdapter.ViewHolder> {

    private final List<StatsFragment.ProductSalesInfo> productList;

    public TopSellingAdapter(List<StatsFragment.ProductSalesInfo> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_selling, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatsFragment.ProductSalesInfo item = productList.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(item.name);
        holder.tvSoldCount.setText(item.soldCount + " sold");
        holder.tvPrice.setText("$" + String.format(Locale.US, "%.2f", item.price));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvSoldCount, tvPrice;

        ViewHolder(View view) {
            super(view);
            tvRank = view.findViewById(R.id.tvRank);
            tvName = view.findViewById(R.id.tvName);
            tvSoldCount = view.findViewById(R.id.tvSoldCount);
            tvPrice = view.findViewById(R.id.tvPrice);
        }
    }
}
