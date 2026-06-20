package com.example.fridgemate.activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fridgemate.R;
import com.example.fridgemate.entity.ShoppingItem;
import com.example.fridgemate.repository.ShoppingRepository;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

public class AddShoppingItemActivity extends AppCompatActivity {
    private final List<String> categories = Arrays.asList("蔬菜", "水果", "肉类", "饮品", "主食", "调料", "其他");
    private TextInputEditText editName;
    private TextInputEditText editQuantity;
    private TextInputEditText editUnit;
    private Spinner spinnerCategory;
    private ShoppingRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_item);
        repository = new ShoppingRepository(this);
        editName = findViewById(R.id.editShoppingName);
        editQuantity = findViewById(R.id.editShoppingQuantity);
        editUnit = findViewById(R.id.editShoppingUnit);
        spinnerCategory = findViewById(R.id.spinnerShoppingCategory);
        spinnerCategory.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));
        editQuantity.setText("1");
        editUnit.setText("份");

        Button save = findViewById(R.id.buttonSaveShopping);
        save.setOnClickListener(v -> save());
    }

    private void save() {
        // 购物项表单只校验核心字段，分类默认通过 Spinner 选择。
        String name = textOf(editName);
        String quantityText = textOf(editQuantity);
        String unit = textOf(editUnit);
        if (name.isEmpty() || quantityText.isEmpty() || unit.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        ShoppingItem item = new ShoppingItem(name, spinnerCategory.getSelectedItem().toString(),
                Integer.parseInt(quantityText), unit, false, System.currentTimeMillis());
        repository.insert(item, () -> runOnUiThread(this::finish));
    }

    private String textOf(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }
}
