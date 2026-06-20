package com.example.fridgemate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgemate.R;
import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.utils.StatusUtils;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    public interface Listener {
        void onEdit(FoodItem item);
        void onDelete(FoodItem item);
        void onMarkUsed(FoodItem item);
    }

    private final Listener listener;
    private final List<FoodItem> items = new ArrayList<>();

    public FoodAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<FoodItem> data) {
        // 当前项目数据量较小，直接刷新整个列表即可，代码更简单直观。
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = items.get(position);
        holder.name.setText(item.name);
        holder.info.setText(item.category + " | " + item.quantity + item.unit + " | " + item.storageLocation + " | 过期：" + item.expireDate);
        holder.status.setText(item.status);
        holder.status.setBackgroundResource(StatusUtils.chipBackgroundFor(item.status));
        holder.status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), StatusUtils.colorFor(item.status)));
        holder.use.setEnabled(!StatusUtils.USED.equals(item.status));
        // 点击卡片进入编辑，长按或删除按钮都可以删除。
        holder.itemView.setOnClickListener(v -> listener.onEdit(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onDelete(item);
            return true;
        });
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
        holder.use.setOnClickListener(v -> listener.onMarkUsed(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView info;
        final TextView status;
        final Button delete;
        final Button use;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textFoodName);
            info = itemView.findViewById(R.id.textFoodInfo);
            status = itemView.findViewById(R.id.textFoodStatus);
            delete = itemView.findViewById(R.id.buttonDelete);
            use = itemView.findViewById(R.id.buttonUse);
        }
    }
}
