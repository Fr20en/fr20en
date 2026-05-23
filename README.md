# Get Activation Code

一个基于 Miuix UI 组件库的 Android 应用，用于生成和管理激活码。

## 功能特性

- 🎨 使用 Miuix UI 组件库，Material You 风格
- 🔑 生成唯一激活码
- 📜 查看历史生成记录
- 🌓 支持亮色/暗色主题切换
- 📱 适配 Android 系统特性

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **组件库**: [Miuix](https://github.com/compose-miuix-ui/miuix)
- **构建工具**: Gradle + Kotlin DSL

## 构建

### 前置要求

- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK 35

### 本地构建

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本
./gradlew assembleRelease
```

生成的 APK 文件位于：
- Debug: `app/build/outputs/apk/debug/`
- Release: `app/build/outputs/apk/release/`

## 自动构建

项目配置了 GitHub Actions，会在以下情况自动构建：

- 推送到 `main` 分支
- 创建新的 tag（自动发布 Release）
- Pull Request

## 许可证

本项目采用 Apache 2.0 许可证

## 致谢

- [Miuix](https://github.com/compose-miuix-ui/miuix) - 优秀的 Compose 组件库
