<p align="center">
  <img src="assets/icon.png" alt="QWeather Icon" width="120" height="120"/>
  <h2 align="center">QWeather 天气预测APP</h2>
</p>

轻量级的 Android 天气客户端示例（Jetpack Compose + MVVM + Hilt）。演示如何整合和风天气（QWeather）与天行数据（TianAPI）、定位服务及丰富的天气 UI（实况、逐小时图表、多日预报、空气质量、生活指数、节气等）。


## 主要特性
- 实况天气、逐小时图表、未来多日预报
- 空气质量（AQI）与生活指数展示
- 城市搜索、添加、管理与定位支持
- 节气、星座、新闻扩展信息
- 响应式主题：按天气/节气/昼夜自动切换背景
- 架构：Jetpack Compose + ViewModel + Repository + Hilt


## 页面展示
<p align="center">
  <img src="assets/WeatherScreen1.jpg" alt="主页面" width="320px"/>
</p>

更多图片与功能说明参见：[FEATURE_INTRODUCTION.md](./FEATURE_INTRODUCTION.md)


## 项目结构（高层次）
- app/src/main/java/com/qiuqiuqiu/weatherPredicate
  - service/ — QWeatherService、LocationService、ApiKeyProvider 等（查看: [service 目录](app/src/main/java/com/qiuqiuqiu/weatherPredicate/service)）
  - repository/ — 数据仓库（MyRepository.kt，路径: [MyRepository.kt](app/src/main/java/com/qiuqiuqiu/weatherPredicate/repository/MyRepository.kt)）
  - viewModel/ — 页面逻辑层（路径: [viewModel 目录](app/src/main/java/com/qiuqiuqiu/weatherPredicate/viewModel)）
  - ui/ — Compose 页面与组件（screen/、normal/、theme/）（路径: [ui 目录](app/src/main/java/com/qiuqiuqiu/weatherPredicate/ui)）
  - model/ — 各类数据模型（路径: [model 目录](app/src/main/java/com/qiuqiuqiu/weatherPredicate/model)）
  - manager/ — 城市、定位、地图管理（路径: [manager 目录](app/src/main/java/com/qiuqiuqiu/weatherPredicate/manager)）
  - network/ — TianApiCities 等第三方接口封装（查看: [TianApiCities.kt](app/src/main/java/com/qiuqiuqiu/weatherPredicate/network/TianApiCities.kt)）
  - tools/ — 时间、图标、颜色格式化工具（路径: [tools 目录](app/src/main/java/com/qiuqiuqiu/weatherPredicate/tools)）
- assets/ — 演示截图与静态资源
- FEATURE_INTRODUCTION.md — 功能详解与更多截图（[打开文件](./FEATURE_INTRODUCTION.md)）
- PROJECT_CONFIG.md — API 配置说明（[打开文件](./PROJECT_CONFIG.md)）


## 项目配置与 API 凭证
所有与 API Key / JWT 相关的配置集中到：[PROJECT_CONFIG.md](./PROJECT_CONFIG.md)  
请参阅该文件以了解如何在本地或 CI 中注入 TianAPI 的 API Key（TIAN_API_KEY）以及 QWeather 的 JWT 凭证（QWEATHER_JWT_PRIVATE、QWEATHER_JWT_KID、QWEATHER_JWT_ISSUER）。

代码读取位置示例：
- BuildConfig 注入：请查看 [app/build.gradle.kts](app/build.gradle.kts)
- QWeather 初始化使用：[service/QWeatherService.kt](app/src/main/java/com/qiuqiuqiu/weatherPredicate/service/QWeatherService.kt)
- TianAPI 使用：[network/TianApiCities.kt](app/src/main/java/com/qiuqiuqiu/weatherPredicate/network/TianApiCities.kt)


## 快速开始
1. 使用 Android Studio 打开项目根目录。
2. 在项目根目录运行：
```bash
# 构建 debug APK
.\gradlew assembleDebug

# 构建并安装到已连接设备
.\gradlew installDebug
```
3. 运行时请在设备/模拟器授予位置权限（ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION）。


## 使用到的第三方 API 概览

本项目同时使用两类第三方服务：
- 天行数据（TianAPI）：城市搜索、热门城市、城市元数据（搜索页与添加城市流程）
  - 封装实现：[network/TianApiCities.kt](app/src/main/java/com/qiuqiuqiu/weatherPredicate/network/TianApiCities.kt)
- 和风天气 / QWeather：气象主数据（实况、逐小时、逐日、空气质量、生活指数、预警、地理/POI 查询等）
  - 封装实现：[service/QWeatherService.kt](app/src/main/java/com/qiuqiuqiu/weatherPredicate/service/QWeatherService.kt)
- API Key / JWT 注入入口：[service/ApiKeyProvider.kt](app/src/main/java/com/qiuqiuqiu/weatherPredicate/service/ApiKeyProvider.kt)


## 开发与调试建议
- 使用 Android Studio 的 Logcat、Compose Preview 进行 UI 调试。
- 为 ViewModel 编写单元测试，使用 MockWebServer 模拟第三方接口。
- 对搜索输入做 debounce（例如 300ms）并缓存最近查询结果。
- 在 Retrofit/OkHttp 中配置 Debug 日志拦截器（仅 Debug 构建）。


## 贡献
欢迎提交 Issue 或 PR。请遵循：
- 描述复现步骤与期望行为
- UI 修改附截图
- 代码风格一致并添加必要注释