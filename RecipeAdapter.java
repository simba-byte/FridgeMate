package com.example.fridgemate.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgemate.R;
import com.example.fridgemate.utils.RecipeMatchUtils;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    public interface Listener {
        void onOpen(RecipeMatchUtils.RecipeMatch match);
    }

    private final Listener listener;
    private final List<RecipeMatchUtils.RecipeMatch> items = new ArrayList<>();

    public RecipeAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<RecipeMatchUtils.RecipeMatch> data) {
        items.clear();
        if (data != null) {
            items.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeMatchUtils.RecipeMatch match = items.get(position);
        // Adapter 只负责展示匹配结果，匹配分数由 RecipeMatchUtils 统一计算。
        holder.name.setText(match.recipe.recipeName);
        holder.score.setText(match.score + "%");
        holder.materials.setText("所需食材：" + match.recipe.materials + " | " + match.recipe.difficulty + " | " + match.recipe.cookingTime + "分钟");
        holder.action.setText(match.actionText());
        holder.itemView.setOnClickListener(v -> listener.onOpen(match));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView score;
        final TextView materials;
        final TextView action;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textRecipeName);
            score = itemView.findViewById(R.id.textRecipeScore);
            materials = itemView.findViewById(R.id.textRecipeMaterials);
            action = itemView.findViewById(R.id.textRecipeAction);
        }
    }
}
