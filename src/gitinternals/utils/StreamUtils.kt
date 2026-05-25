package gitinternals.utils

import java.util.zip.InflaterInputStream

fun readHeader(iis: InflaterInputStream): String {
    val header = StringBuilder()
    var data = iis.read()
    while (!isHeaderSeparator(data)) {
        val char = data.toChar()
        header.append(char)
        data = iis.read()
    }
    return header.toString()
}

fun readUntilEnd(iis: InflaterInputStream): String {
    val content = StringBuilder()
    var data = iis.read()
    while (data != -1) {
        val char = data.toChar()
        content.append(char)
        data = iis.read()
    }
    return content.toString()
}

private fun isHeaderSeparator(data: Int) = data == 0
