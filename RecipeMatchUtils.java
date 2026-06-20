package com.example.fridgemate.utils;

import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.entity.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class RecipeMatchUtils {
    private RecipeMatchUtils() {
    }

    public static class RecipeMatch {
        // 包装菜谱推荐结果：原菜谱、匹配度和缺少材料列表。
        public final Recipe recipe;
        public final int score;
        public final List<String> missingMaterials;

        public RecipeMatch(Recipe recipe, int score, List<String> missingMaterials) {
            this.recipe = recipe;
            this.score = score;
            this.missingMaterials = missingMaterials;
        }

        public String actionText() {
            if (score == 100) {
                return "可直接制作";
            }
            if (score > 0) {
                return "缺少：" + join(missingMaterials, "、");
            }
            return "库存暂不匹配";
        }
    }

    public static List<RecipeMatch> buildRecommendations(List<FoodItem> foods, List<Recipe> recipes) {
        Set<String> available = new HashSet<>();
        for (FoodItem food : foods) {
            // 已过期和已使用的食材不参与推荐，避免推荐不可用库存。
            if (!StatusUtils.EXPIRED.equals(food.status) && !StatusUtils.USED.equals(food.status)) {
                available.add(food.name.trim().toLowerCase(Locale.CHINA));
            }
        }

        List<RecipeMatch> matches = new ArrayList<>();
        for (Recipe recipe : recipes) {
            // 菜谱材料拆成列表后逐项匹配，匹配数越多，推荐分越高。
            List<String> required = splitMaterials(recipe.materials);
            List<String> missing = new ArrayList<>();
            int hit = 0;
            for (String item : required) {
                if (containsMaterial(available, item)) {
                    hit++;
                } else {
                    missing.add(item);
                }
            }
            int score = required.isEmpty() ? 0 : Math.round(hit * 100f / required.size());
            matches.add(new RecipeMatch(recipe, score, missing));
        }
        Collections.sort(matches, (a, b) -> Integer.compare(b.score, a.score));
        return matches;
    }

    public static List<String> splitMaterials(String materials) {
        List<String> result = new ArrayList<>();
        if (materials == null || materials.trim().isEmpty()) {
            return result;
        }
        // 兼容中文顿号、中文逗号和英文逗号三种常见输入。
        String[] parts = materials.replace(",", "、").replace("，", "、").split("、");
        for (String part : parts) {
            String value = part.trim();
            if (!value.isEmpty()) {
                result.add(value);
            }
        }
        return result;
    }

    private static boolean containsMaterial(Set<String> available, String material) {
        String target = material.toLowerCase(Locale.CHINA);
        for (String food : available) {
            // 使用包含匹配，允许“土鸡蛋”匹配“鸡蛋”这类简单同义扩展。
            if (food.contains(target) || target.contains(food)) {
                return true;
            }
        }
        return false;
    }

    private static String join(List<String> values, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(separator);
            }
            builder.append(values.get(i));
        }
        return builder.toString();
    }
}
