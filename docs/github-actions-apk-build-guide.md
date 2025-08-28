# GitHub Actions 自动构建APK操作指南

## 概述

本文档提供了使用GitHub Actions自动构建Android APK文件的详细操作指南。通过本指南，您将了解如何配置、触发和使用自动构建流程。

## 已创建的配置文件

已在项目根目录下创建了GitHub Actions配置文件：
`.github/workflows/build-apk.yml`

## 配置文件详解

### 触发条件

配置文件设置了三种触发构建的方式：

```yaml
on:
  push:               # 当代码推送到以下分支时触发
    branches: [ main, master ]
  pull_request:       # 当对以下分支创建或更新Pull Request时触发
    branches: [ main, master ]
  workflow_dispatch:  # 允许手动在GitHub界面上触发构建
```

### 构建环境

构建作业在Ubuntu最新版本的虚拟环境中运行：

```yaml
jobs:
  build:
    runs-on: ubuntu-latest
```

### 构建步骤

1. **检出代码仓库**：从GitHub获取最新代码
2. **设置JDK 11**：配置Java开发环境
3. **设置Gradle可执行权限**：确保Gradle包装器有执行权限
4. **构建Debug版本APK**：使用Gradle构建调试版本
5. **构建Release版本APK**：使用Gradle构建发布版本
6. **上传Debug APK**：将构建好的调试版本作为构建产物上传
7. **上传Release APK**：将构建好的发布版本作为构建产物上传

## 使用指南

### 步骤1：确保配置文件已添加到版本控制

执行以下命令将GitHub Actions配置文件添加到版本控制并提交：

```bash
# 添加新创建的文件
git add .github/workflows/build-apk.yml docs/github-actions-apk-build-guide.md

# 提交更改
git commit -m "Add GitHub Actions configuration for auto-building APK"

# 推送到远程仓库
git push origin master  # 或您当前使用的分支名称
```

### 步骤2：触发构建

构建将在以下情况自动触发：

1. **代码推送**：当您将代码推送到`main`或`master`分支时
2. **Pull Request**：当创建或更新针对`main`或`master`分支的Pull Request时
3. **手动触发**：在GitHub仓库页面的"Actions"标签页中手动触发构建

### 步骤3：查看构建状态和获取APK

1. 打开GitHub仓库页面，点击顶部的"Actions"标签
2. 在左侧工作流列表中，点击"Android APK Build"
3. 在右侧的工作流运行列表中，点击最新的运行
4. 在运行详情页面，您可以：
   - 查看每个步骤的执行状态和日志
   - 在页面底部的"Artifacts"部分下载构建好的APK文件

## 配置说明

### 构建环境说明

- **操作系统**：Ubuntu最新版本
- **JDK版本**：Java 11
- **Gradle版本**：项目中配置的Gradle 7.5
- **Android Gradle Plugin版本**：7.3.1

### 项目配置要求

- **compileSdk**：33
- **minSdk**：29
- **targetSdk**：33

## 扩展建议

### 1. 添加签名配置（可选）

如需自动构建签名的Release APK，可以修改`app/build.gradle`文件添加签名配置，并在GitHub Actions中使用secrets存储签名密钥：

```gradle
// 在app/build.gradle文件中添加
android {
    // ... 现有配置 ...
    signingConfigs {
        release {
            storeFile file(System.getenv("KEYSTORE_PATH"))
            storePassword System.getenv("KEYSTORE_PASSWORD")
            keyAlias System.getenv("KEY_ALIAS")
            keyPassword System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            // ... 现有配置 ...
            signingConfig signingConfigs.release
        }
    }
}
```

### 2. 自定义构建触发条件（可选）

您可以修改`.github/workflows/build-apk.yml`文件来自定义构建触发条件，例如添加特定分支或标签：

```yaml
on:
  push:
    branches: [ main, master, develop ]  # 添加其他分支
    tags: [ 'v*.*.*' ]  # 添加版本标签触发
  # ... 其他触发条件 ...
```

## 常见问题排查

### 构建失败

1. **Gradle权限问题**：确保`chmod +x ./gradlew`步骤成功执行
2. **依赖下载失败**：检查网络连接或考虑使用镜像源
3. **构建配置错误**：检查`build.gradle`文件中的配置是否正确

### APK文件缺失

1. 确认构建步骤成功完成
2. 检查构建日志中是否有关于APK生成的错误信息
3. 验证APK输出路径是否与配置文件中指定的路径一致

## 注意事项

1. GitHub Actions构建有时间限制，超过限制会被自动终止
2. 构建产物（APK文件）默认保存90天
3. 敏感信息（如签名密钥）应使用GitHub Secrets存储，不要直接写入配置文件
4. 定期更新GitHub Actions使用的各个action版本，以获取最新功能和安全修复