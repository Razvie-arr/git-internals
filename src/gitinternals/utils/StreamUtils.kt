package gitinternals.utils

import java.io.FileInputStream
import java.nio.file.Path
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

fun <T> useGitObjectStream(gitDir: Path, hash: String, action: (type: String, stream: InflaterInputStream) -> T): T {
    val gitObjectPath = buildGitObjectPath(gitDir, hash)
    return FileInputStream(gitObjectPath.toFile()).use { fis ->
        InflaterInputStream(fis).use { stream ->
            val type = readHeader(stream).substringBefore(' ')
            action(type, stream)
        }
    }
}

private fun isHeaderSeparator(data: Int) = data == 0
