package me.yangxiaobin.lib.log


class ILogImpl : AbsLogger()

typealias ExternalImpl = ExternalLogAdapter



val innerLogImpl: ILog by lazy { ILogImpl().apply { setGlobalSuffix("==>") } }

val externalLogImpl: ILog by lazy { ExternalLogAdapter().apply { setGlobalSuffix("-->") } }

object InternalLogger : ILog by externalLogImpl
