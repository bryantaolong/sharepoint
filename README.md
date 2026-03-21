# SharePoint

一款简洁的本地推文分享应用，类似 Twitter 的使用体验，数据完全存储在本地。

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="100" alt="App Icon">
</p>

## ✨ 功能特性

- 📝 **发布推文** - 支持文字、图片（最多4张）、链接
- 🖼️ **图片展示** - 网格布局展示图片，支持 1-4 张自适应布局
- 💬 **评论系统** - 为每条推文添加评论
- ✏️ **编辑推文** - 随时修改已发布的内容
- 🗑️ **删除推文** - 删除时自动清理关联图片
- 🔖 **筛选查看** - 全部推文 / 含图推文切换
- 🎨 **Twitter 风格 UI** - 熟悉的蓝鸟界面设计
- 🔒 **数据隐私** - 所有数据存储在本地，卸载即清除

## 📱 截图

> （欢迎添加应用截图）

## 🏗️ 技术架构

| 层级 | 技术 |
|------|------|
| **UI** | Android XML Layout + Material Design Components |
| **架构模式** | MVVM (Model-View-ViewModel) |
| **数据库** | Room (SQLite) |
| **图片加载** | Coil |
| **异步处理** | Kotlin Coroutines |
| **依赖注入** | 手动注入（ViewModel + Repository） |

## 📁 项目结构

```
app/src/main/java/com/bryan/sharepoint/
├── data/
│   ├── dao/              # Room 数据访问对象
│   ├── database/         # 数据库配置
│   ├── entity/           # 数据实体 (Tweet, Comment)
│   └── repository/       # 数据仓库层
├── ui/
│   ├── adapter/          # RecyclerView 适配器
│   ├── fragment/         # 对话框 Fragment
│   └── viewmodel/        # ViewModel 层
├── util/                 # 工具类 (图片存储)
└── MainActivity.kt       # 主界面
```

## 🚀 快速开始

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- Android SDK 34+
- JDK 11+

### 构建运行

```bash
# 克隆项目
git clone <repository-url>
cd share-point

# 使用 Gradle 构建
./gradlew assembleDebug

# 或直接在 Android Studio 中运行
```

## 📦 数据存储

| 数据类型 | 存储位置 | 卸载时 |
|---------|---------|--------|
| 推文 & 评论 | Room 数据库 | ✅ 自动清除 |
| 图片文件 | 应用私有目录 (`files/images/`) | ✅ 自动清除 |
| 缓存 | 应用缓存目录 | ✅ 自动清除 |

### 图片存储说明

- 用户从相册选择的图片会**复制**到应用私有目录
- 原相册图片**不受影响**
- 删除推文时会自动清理关联图片
- 卸载应用后所有图片被彻底删除

## 🛠️ 技术细节

### 数据库模型

```kotlin
@Entity(tableName = "tweets")
data class Tweet(
    val content: String,      // 推文内容
    val images: String,       // JSON 数组格式的图片路径
    val link: String,         // 链接
    val createdAt: Long,      // 创建时间
    val updatedAt: Long       // 更新时间
)

@Entity(tableName = "comments")
data class Comment(
    val tweetId: Long,        // 关联推文ID
    val content: String,      // 评论内容
    val createdAt: Long       // 创建时间
)
```

### 图片处理流程

1. 用户选择图片 → 2. 复制到私有目录 → 3. 保存路径到数据库 → 4. 加载时从私有目录读取

## 📋 权限说明

```xml
<!-- 读取相册图片（Android 13+） -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Android 12 及以下兼容 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
```

**注意**：应用不需要写入外部存储权限，因为图片存储在私有目录。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

MIT License

---

<p align="center">Made with ❤️ by Bryan</p>
