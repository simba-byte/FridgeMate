package com.example.fridgemate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fridgemate.R;
import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.repository.FoodRepository;
import com.example.fridgemate.utils.StatusUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsFragment extends Fragment {
    private TextView textSummary;
    private TextView textUsageRate;
    private ProgressBar progressUsage;
    private LinearLayout layoutCategoryStats;
    private FoodRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        textSummary = view.findViewById(R.id.textSummary);
        textUsageRate = view.findViewById(R.id.textUsageRate);
        progressUsage = view.findViewById(R.id.progressUsage);
        layoutCategoryStats = view.findViewById(R.id.layoutCategoryStats);
        repository = new FoodRepository(requireContext());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        repository.getAll(items -> requireActivity().runOnUiThread(() -> show(items)));
    }

    private void show(List<FoodItem> items) {
        int normal = 0;
        int soon = 0;
        int expired = 0;
        int used = 0;
        Map<String, Integer> categoryCounts = new LinkedHashMap<>();
        for (FoodItem item : items) {
            // 统计页面不直接查聚合 SQL，而是在本地列表上计算，便于展示多种指标。
            if (StatusUtils.NORMAL.equals(item.status)) {
                normal++;
            } else if (StatusUtils.SOON.equals(item.status)) {
                soon++;
            } else if (StatusUtils.EXPIRED.equals(item.status)) {
                expired++;
            } else if (StatusUtils.USED.equals(item.status)) {
                used++;
            }
            Integer count = categoryCounts.get(item.category);
            categoryCounts.put(item.category, count == null ? 1 : count + 1);
        }
        int total = items.size();
        // 利用率用于反映食材是否被及时使用，空数据时避免除零。
        int rate = total == 0 ? 0 : Math.round(used * 100f / total);
        textSummary.setText("食材总数：" + total + "\n正常：" + normal + "  即将过期：" + soon + "  已过期：" + expired + "  已使用：" + used);
        textUsageRate.setText("食材利用率：" + rate + "%");
        progressUsage.setProgress(rate);

        layoutCategoryStats.removeAllViews();
        if (categoryCounts.isEmpty()) {
            addCategoryLine("暂无分类数据");
        } else {
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                addCategoryLine(entry.getKey() + "：" + entry.getValue() + " 件");
            }
        }
    }

    private void addCategoryLine(String text) {
        // 分类统计使用轻量 TextView 列表，不额外引入图表库，降低项目依赖复杂度。
        TextView view = new TextView(requireContext());
        view.setText(text);
        view.setTextColor(getResources().getColor(R.color.text_primary));
        view.setTextSize(16);
        view.setPadding(18, 14, 18, 14);
        view.setBackgroundResource(R.drawable.bg_chip_green);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 10);
        layoutCategoryStats.addView(view, params);
    }
}
