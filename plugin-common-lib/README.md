
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
