package me.yangxiaobin.lib.ext

import java.util.concurrent.TimeUnit

fun Long.toFormat(
    hour: Boolean = true,
    minute: Boolean = true,
    second: Boolean = true,
    millSecond: Boolean = true
): String {

    if (!(hour || minute || second || millSecond)) return ""

    val hourStr = if (hour) "%02dh:" else ""
    val minuteStr = if (minute) "%02dm:" else ""
    val secondStr = if (second) "%02ds:" else ""
    val millSendStr = if (millSecond) "%02dms" else ""

    val actualFormat = hourStr + minuteStr + secondStr + millSendStr
    //logV("Long.toFormat,actualFormat : $actualFormat")

    if (actualFormat.isEmpty()) return ""

    val longList = mutableListOf<Long>()

    if (hour) longList += TimeUnit.MILLISECONDS.toHours(this)

    if (minute) longList += TimeUnit.MILLISECONDS.toMinutes(this) % TimeUnit.HOURS.toMinutes(1)

    if (second) longList += TimeUnit.MILLISECONDS.toSeconds(this) % TimeUnit.MINUTES.toSeconds(1)

    if (millSecond) longList += TimeUnit.MILLISECONDS.toMillis(this) % TimeUnit.SECONDS.toMillis(1)

    val longArray: Array<Long> = longList.toTypedArray()
    //logV("Long.toFormat,longArray : ${longArray.contentToString()}")

    return String.format(actualFormat, *longArray)
}

