# 项目 API 配置（PROJECT_CONFIG）

本文件说明如何为本项目配置第三方 API 凭证：天行数据（TianAPI，使用 API Key）与和风天气（QWeather，使用 JWT 认证）。同时说明项目如何读取这些凭证（当前构建配置）以及安全建议。

## 概览
- TianAPI（城市搜索、热门城市等）使用简单的 API Key（TIAN_API_KEY）。
- QWeather（气象数据）使用 JWT 认证，需提供 private key / kid / issuer 三项信息（QWEATHER_JWT_PRIVATE、QWEATHER_JWT_KID、QWEATHER_JWT_ISSUER）。
- 项目在 app/build.gradle.kts 中通过 buildConfigField 将这些值注入 BuildConfig，运行时可通过 BuildConfig 或 ApiKeyProvider 获取。

## 在本地设置（推荐）
1. 在项目根目录编辑或创建 `local.properties`（注意：该文件不应提交到版本库）。
2. 添加如下条目（示例）：

```
# TianAPI
TIAN_API_KEY=your_tian_api_key_here

# QWeather (JWT 认证相关)
QWEATHER_JWT_PRIVATE=-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----
QWEATHER_JWT_KID=your_key_id
QWEATHER_JWT_ISSUER=your_issuer_id
```

说明：QWEATHER_JWT_PRIVATE 的格式可根据你获得的私钥调整（如果私钥包含换行，可以使用 \n 转义或将私钥压缩成一行，项目示例中采用单行存放）。

## 在 CI / 生产环境注入（推荐）
- 在 CI（例如 GitHub Actions / GitLab CI）中通过 Secrets 注入环境变量，然后在构建流水中将其传入 Gradle（例如通过命令行 -PTIAN_API_KEY=... 或设置 gradle.properties）。
- 切勿在公开仓库中直接存放敏感凭证。

## 构建时注入（项目已配置）
app/build.gradle.kts 中已加入如下 buildConfigField（项目已实现）：
- BuildConfig.TIAN_API_KEY
- BuildConfig.QWEATHER_JWT_PRIVATE
- BuildConfig.QWEATHER_JWT_KID
- BuildConfig.QWEATHER_JWT_ISSUER

这意味着在应用代码中可以直接通过 BuildConfig.* 读取以上值；也推荐通过 ApiKeyProvider（app/src/.../service/ApiKeyProvider.kt）以便更灵活地更换读取来源（BuildConfig、Datastore、远端配置等）。

## 在代码中使用
- TianAPI：network/TianApiCities.kt（示例）会从 ApiKeyProvider 或 BuildConfig 读取 TIAN_API_KEY，调用城市搜索/热门城市接口。
- QWeather：service/QWeatherService.kt 会使用 JWTGenerator 初始化 QWeather SDK，请确保 QWEATHER_JWT_PRIVATE / KID / ISSUER 正确可用。示例逻辑：
  - 从 BuildConfig 或 ApiKeyProvider 获取 privateKey、kid、issuer
  - 使用 JWTGenerator(privateKey, kid, issuer) 并 setTokenGenerator 注入 QWeather SDK

注意：如果你修改了读取方式（例如把凭证改为从 ApiKeyProvider 注入），请同时更新 QWeatherService 的初始化代码以使用注入的值。

## 安全建议
- 将 local.properties / 包含敏感信息的文件加入 .gitignore。
- 在日志中勿打印私钥或完整 JWT。
- 在可能的情况下使用短期凭证或服务端代理来生成 JWT，减少客户端持有长期私钥的风险。
- 对搜索类接口做去抖（debounce）和本地缓存，避免触发第三方限流。

## 验证与调试
- 在 Android Studio 运行时，可以在 Logcat 中打印 BuildConfig 的非敏感字段以确认注入（不要打印私钥）。
- 若 QWeather 初始化失败，检查 QWEATHER_JWT_* 的值是否为空或格式不正确（常见问题是换行没有正确转义）。
- TianAPI 请求失败时检查 TIAN_API_KEY 是否正确并留意配额限制。