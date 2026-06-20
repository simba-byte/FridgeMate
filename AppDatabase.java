package com.example.fridgemate.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.entity.Recipe;
import com.example.fridgemate.entity.ShoppingItem;
import com.example.fridgemate.utils.DateUtils;
import com.example.fridgemate.utils.StatusUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {FoodItem.class, Recipe.class, ShoppingItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    // 数据库读写统一放入后台线程，避免阻塞 Android 主线程。
    public static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(4);

    public abstract FoodDao foodDao();
    public abstract RecipeDao recipeDao();
    public abstract ShoppingDao shoppingDao();

    public static AppDatabase getDatabase(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    // 单例数据库实例，全 App 共用同一个 RoomDatabase。
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "fridgemate.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }

    public static void seedDefaults(Context context) {
        Context appContext = context.getApplicationContext();
        databaseExecutor.execute(() -> {
            AppDatabase db = getDatabase(appContext);
            seedRecipes(db);

            // 示例食材只在首次启动且食材表为空时插入，避免每次打开 App 重复造数据。
            SharedPreferences preferences = appContext.getSharedPreferences("fridgemate_seed", Context.MODE_PRIVATE);
            if (!preferences.getBoolean("sample_foods_inserted", false) && db.foodDao().count() == 0) {
                long now = System.currentTimeMillis();
                db.foodDao().insert(new FoodItem("鸡蛋", "其他", 8, "个", "冷藏区",
                        DateUtils.today(), DateUtils.daysFromToday(5), StatusUtils.statusFor(DateUtils.daysFromToday(5)), "早餐常用", now));
                db.foodDao().insert(new FoodItem("番茄", "蔬菜", 3, "个", "冷藏区",
                        DateUtils.today(), DateUtils.daysFromToday(2), StatusUtils.statusFor(DateUtils.daysFromToday(2)), "优先食用", now));
                db.foodDao().insert(new FoodItem("牛奶", "饮品", 1, "盒", "冷藏区",
                        DateUtils.today(), DateUtils.daysFromToday(-1), StatusUtils.statusFor(DateUtils.daysFromToday(-1)), "已开封", now));
                db.foodDao().insert(new FoodItem("面包", "主食", 1, "袋", "常温区",
                        DateUtils.today(), DateUtils.daysFromToday(4), StatusUtils.statusFor(DateUtils.daysFromToday(4)), "", now));
                preferences.edit().putBoolean("sample_foods_inserted", true).apply();
            }
        });
    }

    private static void seedRecipes(AppDatabase db) {
        if (db.recipeDao().count() > 0) {
            return;
        }
        // 预置菜谱用于本地推荐算法，不依赖网络接口。
        db.recipeDao().insert(new Recipe("番茄炒蛋", "番茄、鸡蛋", "1. 番茄切块，鸡蛋打散。2. 先炒鸡蛋盛出。3. 下番茄炒出汁，倒回鸡蛋翻炒调味。", "简单", 12));
        db.recipeDao().insert(new Recipe("鸡蛋炒饭", "米饭、鸡蛋、葱", "1. 米饭打散，鸡蛋炒碎。2. 加入米饭翻炒。3. 撒葱花并调味。", "简单", 15));
        db.recipeDao().insert(new Recipe("牛奶燕麦", "牛奶、燕麦", "1. 牛奶加热。2. 加入燕麦小火煮软。3. 可按口味加入水果。", "简单", 8));
        db.recipeDao().insert(new Recipe("青椒肉丝", "青椒、猪肉", "1. 肉丝腌制，青椒切丝。2. 肉丝滑炒。3. 加青椒快速翻炒。", "中等", 20));
        db.recipeDao().insert(new Recipe("水果沙拉", "苹果、香蕉、酸奶", "1. 水果切块。2. 倒入酸奶拌匀。3. 冷藏后口感更佳。", "简单", 10));
        db.recipeDao().insert(new Recipe("土豆炖牛肉", "土豆、牛肉", "1. 牛肉焯水。2. 与土豆一起炖煮。3. 收汁调味。", "较难", 50));
        db.recipeDao().insert(new Recipe("紫菜蛋花汤", "紫菜、鸡蛋", "1. 水烧开加入紫菜。2. 倒入蛋液形成蛋花。3. 加盐和香油。", "简单", 10));
        db.recipeDao().insert(new Recipe("黄瓜鸡蛋饼", "黄瓜、鸡蛋、面粉", "1. 黄瓜擦丝。2. 加鸡蛋和面粉调糊。3. 平底锅煎至两面金黄。", "中等", 18));
        db.recipeDao().insert(new Recipe("蔬菜三明治", "面包、生菜、番茄", "1. 面包烘热。2. 夹入生菜和番茄。3. 可按口味加酱。", "简单", 8));
        db.recipeDao().insert(new Recipe("酸奶水果杯", "酸奶、草莓、香蕉", "1. 水果切片。2. 与酸奶分层装杯。3. 冷藏后食用。", "简单", 8));
    }
}
