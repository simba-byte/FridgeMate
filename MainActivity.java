package com.example.fridgemate.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.fridgemate.R;
import com.example.fridgemate.database.AppDatabase;
import com.example.fridgemate.fragment.FoodFragment;
import com.example.fridgemate.fragment.HomeFragment;
import com.example.fridgemate.fragment.RecipeFragment;
import com.example.fridgemate.fragment.ShoppingFragment;
import com.example.fridgemate.fragment.StatisticsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 启动时初始化默认菜谱和示例食材，保证第一次运行也能看到演示数据。
        AppDatabase.seedDefaults(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // 底部导航只负责切换 Fragment，具体业务逻辑放在各 Fragment 内部。
            if (id == R.id.nav_food) {
                switchFragment(new FoodFragment());
            } else if (id == R.id.nav_recipe) {
                switchFragment(new RecipeFragment());
            } else if (id == R.id.nav_shopping) {
                switchFragment(new ShoppingFragment());
            } else if (id == R.id.nav_stats) {
                switchFragment(new StatisticsFragment());
            } else {
                switchFragment(new HomeFragment());
            }
            return true;
        });

        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void switchFragment(Fragment fragment) {
        // 使用 replace 保持主界面只有一个当前页面，避免多个 Fragment 叠加。
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
