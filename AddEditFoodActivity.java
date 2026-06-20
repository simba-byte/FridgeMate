package com.example.fridgemate.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fridgemate.R;
import com.example.fridgemate.entity.FoodItem;
import com.example.fridgemate.repository.FoodRepository;
import com.example.fridgemate.utils.DateUtils;
import com.example.fridgemate.utils.StatusUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEditFoodActivity extends AppCompatActivity {
    public static final String EXTRA_FOOD_ID = "food_id";
    private final List<String> categories = Arrays.asList("蔬菜", "水果", "肉类", "饮品", "主食", "调料", "其他");
    private final List<String> locations = Arrays.asList("冷藏区", "冷冻区", "常温区");

    private FoodRepository repository;
    private FoodItem editingItem;
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editUnit;
    private TextInputEditText editPurchaseDate;
    private TextInputEditText editExpireDate;
    private TextInputEditText editNote;
    private Spinner spinnerCategory;
    private Spinner spinnerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_food);

        repository = new FoodRepository(this);
        bindViews();
        setupSpinners();
        setupDatePicker(editPurchaseDate);
        setupDatePicker(editExpireDate);

        int id = getIntent().getIntExtra(EXTRA_FOOD_ID, -1);
        if (id > 0) {
            // 有 food_id 表示编辑模式，需要先从数据库读取原记录并回填表单。
            ((TextView) findViewById(R.id.textFormTitle)).setText("编辑食材");
            repository.getById(id, item -> runOnUiThread(() -> showFood(item)));
        } else {
            // 新增模式提供常用默认值，减少用户首次录入成本。
            editQuantity.setText("1");
            editUnit.setText("个");
            editPurchaseDate.setText(DateUtils.today());
            editExpireDate.setText(DateUtils.daysFromToday(7));
        }

        Button save = findViewById(R.id.buttonSaveFood);
        save.setOnClickListener(v -> saveFood());
    }

    private void bindViews() {
        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        editUnit = findViewById(R.id.editUnit);
        editPurchaseDate = findViewById(R.id.editPurchaseDate);
        editExpireDate = findViewById(R.id.editExpireDate);
        editNote = findViewById(R.id.editNote);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerLocation = findViewById(R.id.spinnerLocation);
    }

    private void setupSpinners() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        spinnerLocation.setAdapter(locationAdapter);
    }

    private void setupDatePicker(TextInputEditText editText) {
        editText.setOnClickListener(v -> {
            // 日期使用 DatePickerDialog 选择，最后仍保存为 yyyy-MM-dd 字符串。
            Date date = DateUtils.parse(textOf(editText));
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String value = String.format(java.util.Locale.CHINA, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                editText.setText(value);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void showFood(FoodItem item) {
        if (item == null) {
            Toast.makeText(this, "食材不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        editingItem = item;
        editName.setText(item.name);
        editQuantity.setText(String.valueOf(item.quantity));
        editUnit.setText(item.unit);
        editPurchaseDate.setText(item.purchaseDate);
        editExpireDate.setText(item.expireDate);
        editNote.setText(item.note);
        spinnerCategory.setSelection(Math.max(0, categories.indexOf(item.category)));
        spinnerLocation.setSelection(Math.max(0, locations.indexOf(item.storageLocation)));
    }

    private void saveFood() {
        String name = textOf(editName);
        String quantityText = textOf(editQuantity);
        String unit = textOf(editUnit);
        String purchaseDate = textOf(editPurchaseDate);
        String expireDate = textOf(editExpireDate);
        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty() || purchaseDate.isEmpty() || expireDate.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        // 日期格式校验放在保存前，避免无效日期进入数据库影响状态判断。
        if (!purchaseDate.matches("\\d{4}-\\d{2}-\\d{2}") || !expireDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            Toast.makeText(this, "日期格式应为 yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityText);
        String category = spinnerCategory.getSelectedItem().toString();
        String location = spinnerLocation.getSelectedItem().toString();
        String note = textOf(editNote);

        if (editingItem == null) {
            // 新增食材时直接根据过期日期生成初始状态。
            FoodItem item = new FoodItem(name, category, quantity, unit, location, purchaseDate, expireDate,
                    StatusUtils.statusFor(expireDate), note, System.currentTimeMillis());
            repository.insert(item, () -> runOnUiThread(this::finish));
        } else {
            // 编辑食材时复用原 id 和创建时间，只更新用户修改过的字段。
            editingItem.name = name;
            editingItem.category = category;
            editingItem.quantity = quantity;
            editingItem.unit = unit;
            editingItem.storageLocation = location;
            editingItem.purchaseDate = purchaseDate;
            editingItem.expireDate = expireDate;
            editingItem.note = note;
            repository.update(editingItem, () -> runOnUiThread(this::finish));
        }
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
