# SharePoint

A lightweight local tweet-sharing app with a Twitter-like experience. All data is stored locally on your device.

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" width="100" alt="App Icon">
</p>

## ✨ Features

- 📝 **Post Tweets** - Support text, images (up to 4), and links
- 🖼️ **Image Gallery** - Grid layout with adaptive 1-4 image display
- 💬 **Comments** - Add comments to any tweet
- ✏️ **Edit Tweets** - Modify published content anytime
- 🗑️ **Delete Tweets** - Automatic cleanup of associated images
- 🔖 **Filter View** - Toggle between all tweets and image-only tweets
- 🎨 **Twitter-style UI** - Familiar blue bird interface design
- 🔒 **Data Privacy** - All data stored locally, cleared upon uninstall

## 📱 Screenshots

> (Screenshots are welcome to be added)

## 🏗️ Tech Stack

| Layer | Technology |
|-------|------------|
| **UI** | Android XML Layout + Material Design Components |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Database** | Room (SQLite) |
| **Image Loading** | Coil |
| **Async Processing** | Kotlin Coroutines |
| **Dependency Injection** | Manual Injection (ViewModel + Repository) |

## 📁 Project Structure

```
app/src/main/java/com/bryan/sharepoint/
├── data/
│   ├── dao/              # Room Data Access Objects
│   ├── database/         # Database configuration
│   ├── entity/           # Data entities (Tweet, Comment)
│   └── repository/       # Repository layer
├── ui/
│   ├── adapter/          # RecyclerView adapters
│   ├── fragment/         # Dialog Fragments
│   └── viewmodel/        # ViewModel layer
├── util/                 # Utility classes (image storage)
└── MainActivity.kt       # Main activity
```

## 🚀 Quick Start

### Requirements

- Android Studio Hedgehog (2023.1.1) or higher
- Android SDK 34+
- JDK 11+

### Build & Run

```bash
# Clone the repository
git clone "https://github.com/bryantaolong/sharepoint.git"
cd sharepoint

# Build with Gradle
./gradlew assembleDebug

# Or run directly in Android Studio
```

## 📦 Data Storage

| Data Type | Storage Location | On Uninstall |
|-----------|------------------|--------------|
| Tweets & Comments | Room Database | ✅ Auto-cleared |
| Image Files | App private directory (`files/images/`) | ✅ Auto-cleared |
| Cache | App cache directory | ✅ Auto-cleared |

### Image Storage Notes

- Images selected from the gallery are **copied** to the app's private directory
- Original gallery images **remain unaffected**
- Associated images are automatically cleaned up when deleting tweets
- All images are permanently deleted when the app is uninstalled

## 🛠️ Technical Details

### Database Schema

```kotlin
@Entity(tableName = "tweets")
data class Tweet(
    val content: String,      // Tweet content
    val images: String,       // Image paths in JSON array format
    val link: String,         // Link URL
    val createdAt: Long,      // Creation timestamp
    val updatedAt: Long       // Last update timestamp
)

@Entity(tableName = "comments")
data class Comment(
    val tweetId: Long,        // Associated tweet ID
    val content: String,      // Comment content
    val createdAt: Long       // Creation timestamp
)
```

### Image Processing Flow

1. User selects images → 2. Copy to private directory → 3. Save paths to database → 4. Load from private directory

## 📋 Permissions

```xml
<!-- Read gallery images (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Android 12 and below compatibility -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
```

**Note**: The app does not require write external storage permission, as images are stored in the private directory.

## 🤝 Contributing

Issues and Pull Requests are welcome!

## 📄 License

MIT License

---

<p align="center">Made with ❤️ by Bryan</p>
