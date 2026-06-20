package com.example.fridgemate.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgemate.R;
import com.example.fridgemate.activity.RecipeDetailActivity;
import com.example.fridgemate.adapter.RecipeAdapter;
import com.example.fridgemate.repository.FoodRepository;
import com.example.fridgemate.repository.RecipeRepository;
import com.example.fridgemate.utils.RecipeMatchUtils;

public class RecipeFragment extends Fragment {
    private FoodRepository foodRepository;
    private RecipeRepository recipeRepository;
    private RecipeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        foodRepository = new FoodRepository(requireContext());
        recipeRepository = new RecipeRepository(requireContext());
        RecyclerView recyclerView = view.findViewById(R.id.recyclerRecipe);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new RecipeAdapter(match -> {
            Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
            intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, match.recipe.id);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        // 菜谱推荐需要同时读取库存食材和预置菜谱，再交给工具类计算匹配度。
        foodRepository.getAll(foods -> recipeRepository.getAll(recipes -> {
            java.util.List<RecipeMatchUtils.RecipeMatch> matches = RecipeMatchUtils.buildRecommendations(foods, recipes);
            requireActivity().runOnUiThread(() -> adapter.submitList(matches));
        }));
    }
}
