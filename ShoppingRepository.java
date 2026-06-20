package com.example.fridgemate.repository;

import android.content.Context;

import com.example.fridgemate.database.AppDatabase;
import com.example.fridgemate.database.ShoppingDao;
import com.example.fridgemate.entity.ShoppingItem;

import java.util.List;

public class ShoppingRepository {
    private final ShoppingDao shoppingDao;

    public ShoppingRepository(Context context) {
        shoppingDao = AppDatabase.getDatabase(context).shoppingDao();
    }

    public void getAll(DataCallback<List<ShoppingItem>> callback) {
        // 购物清单读写也通过 Repository 封装，页面层不直接依赖 DAO。
        AppDatabase.databaseExecutor.execute(() -> callback.onData(shoppingDao.getAll()));
    }

    public void getById(int id, DataCallback<ShoppingItem> callback) {
        AppDatabase.databaseExecutor.execute(() -> callback.onData(shoppingDao.getById(id)));
    }

    public void insert(ShoppingItem item, Runnable done) {
        AppDatabase.databaseExecutor.execute(() -> {
            shoppingDao.insert(item);
            run(done);
        });
    }

    public void update(ShoppingItem item, Runnable done) {
        AppDatabase.databaseExecutor.execute(() -> {
            shoppingDao.update(item);
            run(done);
        });
    }

    public void delete(ShoppingItem item, Runnable done) {
        AppDatabase.databaseExecutor.execute(() -> {
            shoppingDao.delete(item);
            run(done);
        });
    }

    private void run(Runnable done) {
        if (done != null) {
            done.run();
        }
    }
}
