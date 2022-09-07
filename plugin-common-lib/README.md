
# Introduction
编写 Custom Android Gradle Plugin 的 codeLab.

- 独立 Project
- 使用根 Project 的 libs.version.toml


# Content
- Transform 抽象
- Bytecode manipulation 抽象
- Abs plugin + Logger


###注意
改动这里代码，需要 publishToMavenLocal 之后才会生效

> gradle :logger-jvm:publishToMavenLocal  :plugin-common-lib:publishToMavenLocal -s


### TODO
1. 多线程处理 Jar
2. 多 gradle plugin 合并 Transform
3. 抽象 byteArray 处理 Pipeline
4. 支持 ClassGraph
