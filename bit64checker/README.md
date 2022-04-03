
# Introduction

用于检测 Android project 有哪些 so 文件不支持 64 位

# 背景

- Google play 2019.8.1 上架必须支持 64 位
- 小米 / Oppo 应用商店 2021 32 + 64  / 2022 Only 64 / 2023  ~~32~~
- 高端旗舰设备上，支持 64 位的核心更多性能更优

# 32 vs 64

- 32 : armeabi   第5代、第6代的ARM处理器，早期的手机用的比较多，基本可以淘汰了
  - 32 位 vss 最大为 2 的 32 次方（4G），超过会 OOM，据华为应用商店统计，已有大量 app 出现 vss > 4G
- 64 : arm64-v8a 第8代、64位ARM处理器

# 影响

## 负向
- 各流程测试
- webView 缓存问题

## 正向
- 理论性能提升 
- 解决部分地址不足导致的 OOM
- 部分官方性能优化工具只支持 64 位

# 参考
- [掘金 Android 适配 64 位](https://juejin.cn/post/6964737926617890853)