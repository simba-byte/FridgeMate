package com.example.fridgemate.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgemate.R;
import com.example.fridgemate.activity.AddEditFoodActivity;
import com.example.fridgemate.adapter.FoodAdapter;
import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.repository.FoodRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public class FoodFragment extends Fragment {
    private final List<String> categories = Arrays.asList("全部", "蔬菜", "水果", "肉类", "饮品", "主食", "调料", "其他");
    private FoodRepository repository;
    private FoodAdapter adapter;
    private EditText editSearch;
    private Spinner spinnerCategory;
    private TextView textEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);
        repository = new FoodRepository(requireContext());
        editSearch = view.findViewById(R.id.editSearch);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        textEmpty = view.findViewById(R.id.textEmpty);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerFood);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new FoodAdapter(new FoodAdapter.Listener() {
            @Override
            public void onEdit(FoodItem item) {
                // 通过 Intent 传递食材 id，编辑页再从数据库读取完整记录。
                Intent intent = new Intent(requireContext(), AddEditFoodActivity.class);
                intent.putExtra(AddEditFoodActivity.EXTRA_FOOD_ID, item.id);
                startActivity(intent);
            }

            @Override
            public void onDelete(FoodItem item) {
                confirmDelete(item);
            }

            @Override
            public void onMarkUsed(FoodItem item) {
                // 标记已使用后刷新列表，让状态标签和统计数据保持同步。
                repository.markUsed(item, () -> requireActivity().runOnUiThread(() -> load()));
            }
        });
        recyclerView.setAdapter(adapter);
        spinnerCategory.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories));
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                load();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                load();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        FloatingActionButton fab = view.findViewById(R.id.fabAddFood);
        fab.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddEditFoodActivity.class)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        if (repository == null || adapter == null) {
            return;
        }
        // 分类和关键字组合筛选，筛选逻辑放在 Repository 中统一处理。
        String category = spinnerCategory.getSelectedItem() == null ? "全部" : spinnerCategory.getSelectedItem().toString();
        String keyword = editSearch.getText() == null ? "" : editSearch.getText().toString();
        repository.getFiltered(category, keyword, items -> requireActivity().runOnUiThread(() -> {
            adapter.submitList(items);
            textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        }));
    }

    private void confirmDelete(FoodItem item) {
        // 删除属于不可逆操作，先弹窗确认，避免误触。
        new AlertDialog.Builder(requireContext())
                .setTitle("删除食材")
                .setMessage("确定删除“" + item.name + "”吗？")
                .setPositiveButton("删除", (dialog, which) -> repository.delete(item, () -> requireActivity().runOnUiThread(() -> load())))
                .setNegativeButton("取消", null)
                .show();
    }
}
