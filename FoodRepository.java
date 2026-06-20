package com.example.fridgemate.repository;

import android.content.Context;

import com.example.fridgemate.database.AppDatabase;
import com.example.fridgemate.database.FoodDao;
import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.utils.StatusUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodRepository {
    private final FoodDao foodDao;

    public FoodRepository(Context context) {
        foodDao = AppDatabase.getDatabase(context).foodDao();
    }

    public void getAll(DataCallback<List<FoodItem>> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            List<FoodItem> items = foodDao.getAll();
            // 每次读取时顺带刷新状态，保证临期/过期判断随日期变化自动更新。
            refreshStatuses(items);
            callback.onData(foodDao.getAll());
        });
    }

    public void getFiltered(String category, String keyword, DataCallback<List<FoodItem>> callback) {
        AppDatabase.databaseExecutor.execute(() -> {
            List<FoodItem> source = foodDao.getAll();
            refreshStatuses(source);
            String safeCategory = category == null ? "全部" : category;
            String safeKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.CHINA);
            List<FoodItem> result = new ArrayList<>();
            for (FoodItem item : foodDao.getAll()) {
                // 分类筛选和关键字搜索在本地完成，数据量小，逻辑直观。
                boolean categoryOk = "全部".equals(safeCategory) || safeCategory.equals(item.category);
                boolean keywordOk = safeKeyword.isEmpty()
                        || item.name.toLowerCase(Locale.CHINA).contains(safeKeyword)
                        || item.category.toLowerCase(Locale.CHINA).contains(safeKeyword);
                if (categoryOk && keywordOk) {
                    result.add(item);
                }
            }
            callback.onData(result);
        });
    }

    public void getById(int id, DataCallback<FoodItem> callback) {
        AppDatabase.databaseExecutor.execute(() -> callback.onData(foodDao.getById(id)));
    }

    public void insert(FoodItem item, Runnable done) {
        AppDatabase.databaseExecutor.execute(() -> {
            // 保存前重新计算状态，避免表单传入过期状态不准确。
            item.status = StatusUtils.statusFor(item.expireDate);
            foodDao.insert(item);
            run(done);
        });
    }

    public void update(FoodItem item, Runnable done) {
        AppDatabase.databaseExecutor.execute(() -> {
            if (!StatusUtils.USED.equals(item.status)) {
                item.status = StatusUtils.statusFor(item.expireDate);
            }
            foodDao.update(item);
            run(done);
        });
    }

    public void markUsed(FoodItem item, Runnable done) {
        AppDatabase.databaseExecutor.execute(() -> {
            item.status = StatusUtils.USED;
            foodDao.update(item);
            run(done);
        });
    }

    public void delete(FoodItem item, Runnable done) {
        AppDatabase.databaseExecutor.execute(() -> {
            foodDao.delete(item);
            run(done);
        });
    }

    private void refreshStatuses(List<FoodItem> items) {
        for (FoodItem item : items) {
            // “已使用”是用户主动操作结果，不再被过期日期覆盖。
            if (!StatusUtils.USED.equals(item.status)) {
                String status = StatusUtils.statusFor(item.expireDate);
                if (!status.equals(item.status)) {
                    item.status = status;
                    foodDao.update(item);
                }
            }
        }
    }

    private void run(Runnable done) {
        if (done != null) {
            done.run();
        }
    }
}
