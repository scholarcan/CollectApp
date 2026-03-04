# 收集.md 快速记录 App

## 功能
- 点击图标弹出输入框（非全屏对话框）
- 输入文字后点「保存」
- 自动加时间戳（格式：2026-03-02 15:47:09），新内容插入文件**顶部**
- 文件：内部存储/Documents/231225TPR/231225TPR/收集.md

## 构建步骤（Android Studio，免费，5分钟）

1. 下载安装 Android Studio: https://developer.android.com/studio
2. File -> Open -> 选择 CollectApp 文件夹
3. 等待 Gradle 同步（首次需联网，约3分钟）
4. Build -> Build Bundle/APK -> Build APK(s)
5. APK 位于: app/build/outputs/apk/debug/app-debug.apk

## 安装
- 将 APK 传到手机，点击安装（需开启「允许未知来源」）
- 首次打开时授权「所有文件访问权限」

## 写入格式
新记录会插入文件顶部，格式如下：
2026-03-02 15:47:09 你的内容

---
