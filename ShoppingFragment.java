package com.example.fridgemate.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fridgemate.R;
import com.example.fridgemate.activity.AddShoppingItemActivity;
import com.example.fridgemate.adapter.ShoppingAdapter;
import com.example.fridgemate.entity.ShoppingItem;
import com.example.fridgemate.repository.ShoppingRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ShoppingFragment extends Fragment {
    private ShoppingRepository repository;
    private ShoppingAdapter adapter;
    private TextView textEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping, container, false);
        repository = new ShoppingRepository(requireContext());
        textEmpty = view.findViewById(R.id.textShoppingEmpty);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerShopping);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ShoppingAdapter(new ShoppingAdapter.Listener() {
            @Override
            public void onToggle(ShoppingItem item) {
                // 勾选框只切换 isBought 字段，随后重新加载以更新排序和样式。
                item.isBought = !item.isBought;
                repository.update(item, () -> requireActivity().runOnUiThread(() -> load()));
            }

            @Override
            public void onDelete(ShoppingItem item) {
                confirmDelete(item);
            }
        });
        recyclerView.setAdapter(adapter);
        FloatingActionButton fab = view.findViewById(R.id.fabAddShopping);
        fab.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddShoppingItemActivity.class)));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        // 每次返回页面都重新读取数据库，保证从菜谱详情加入的材料能立即显示。
        repository.getAll(items -> requireActivity().runOnUiThread(() -> {
            adapter.submitList(items);
            textEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        }));
    }

    private void confirmDelete(ShoppingItem item) {
        // 删除购物项同样加入确认提示，保持交互一致。
        new AlertDialog.Builder(requireContext())
                .setTitle("删除购物项")
                .setMessage("确定删除“" + item.name + "”吗？")
                .setPositiveButton("删除", (dialog, which) -> repository.delete(item, () -> requireActivity().runOnUiThread(() -> load())))
                .setNegativeButton("取消", null)
                .show();
    }
}
