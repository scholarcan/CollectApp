package com.collect.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_PERMISSION = 1001;
    private static final int REQ_MANAGE_STORAGE = 1002;

    private static final String FILE_PATH =
        Environment.getExternalStorageDirectory().getAbsolutePath()
        + "/Documents/231225TPR/231225TPR/收集.md";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        checkPermissionsAndShowDialog();
    }

    private void checkPermissionsAndShowDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                new AlertDialog.Builder(this)
                    .setTitle("需要存储权限")
                    .setMessage("此应用需要访问外部存储以读写「收集.md」，请在下一页开启「所有文件访问权限」。")
                    .setPositiveButton("去开启", (d, w) -> {
                        Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                        startActivityForResult(i, REQ_MANAGE_STORAGE);
                    })
                    .setNegativeButton("取消", (d, w) -> finish())
                    .show();
            } else {
                showInputDialog();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }, REQ_PERMISSION);
            } else {
                showInputDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
                showInputDialog();
            } else {
                Toast.makeText(this, "未获得权限，无法写入文件", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showInputDialog();
            } else {
                Toast.makeText(this, "未获得权限，无法写入文件", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void showInputDialog() {
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(16);
        container.setPadding(pad, pad / 2, pad, 0);

        TextView hint = new TextView(this);
        hint.setText("内容将加时间戳后写入 收集.md 顶部");
        hint.setTextSize(12);
        hint.setTextColor(0xFF888888);
        hint.setPadding(4, 0, 4, dp(8));
        container.addView(hint);

        EditText editText = new EditText(this);
        editText.setHint("在此输入内容…");
        editText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setMinLines(3);
        editText.setMaxLines(8);
        editText.setGravity(Gravity.TOP | Gravity.START);
        editText.setBackground(null);
        editText.setPadding(4, 4, 4, dp(8));
        editText.requestFocus();
        container.addView(editText);

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("📝  添加到收集")
            .setView(container)
            .setPositiveButton("保存", (d, w) -> {
                String input = editText.getText().toString().trim();
                if (input.isEmpty()) {
                    Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    saveToFile(input);
                }
            })
            .setNegativeButton("取消", (d, w) -> finish())
            .create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setOnDismissListener(d -> finish());
        dialog.show();
    }

    private void saveToFile(String inputText) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String newEntry = timestamp + " " + inputText + "\n";

        File file = new File(FILE_PATH);
        try {
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            String existing = "";
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) sb.append(line).append("\n");
                reader.close();
                existing = sb.toString();
            }
            FileWriter writer = new FileWriter(file, false);
            writer.write(newEntry + existing);
            writer.close();
            Toast.makeText(this, "✅ 已保存！\n" + timestamp, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "❌ 写入失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
