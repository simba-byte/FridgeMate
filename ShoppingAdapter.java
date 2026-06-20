package com.example.fridgemate.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgemate.R;
import com.example.fridgemate.entity.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingViewHolder> {
    public interface Listener {
        void onToggle(ShoppingItem item);
        void onDelete(ShoppingItem item);
    }

    private final Listener listener;
    private final List<ShoppingItem> items = new ArrayList<>();

    public ShoppingAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<ShoppingItem> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {
        ShoppingItem item = items.get(position);
        holder.check.setOnCheckedChangeListener(null);
        holder.check.setChecked(item.isBought);
        holder.name.setText(item.name);
        holder.info.setText(item.category + " | " + item.quantity + item.unit);
        int color = ContextCompat.getColor(holder.itemView.getContext(), item.isBought ? R.color.accent_gray : R.color.text_primary);
        holder.name.setTextColor(color);
        holder.name.setPaintFlags(item.isBought ? holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : holder.name.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
        // 先清空监听再设置勾选状态，避免 RecyclerView 复用时误触发更新。
        holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onToggle(item));
        holder.delete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ShoppingViewHolder extends RecyclerView.ViewHolder {
        final CheckBox check;
        final TextView name;
        final TextView info;
        final Button delete;

        ShoppingViewHolder(@NonNull View itemView) {
            super(itemView);
            check = itemView.findViewById(R.id.checkBought);
            name = itemView.findViewById(R.id.textShoppingName);
            info = itemView.findViewById(R.id.textShoppingInfo);
            delete = itemView.findViewById(R.id.buttonDeleteShopping);
        }
    }
}
