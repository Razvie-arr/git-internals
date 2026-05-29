package gitinternals.utils

import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.InflaterInputStream

fun readHeader(stream: InflaterInputStream): String {
    val header = StringBuilder()
    var data = stream.read()
    while (!isHeaderSeparator(data)) {
        val char = data.toChar()
        header.append(char)
        data = stream.read()
    }
    return header.toString()
}

fun readUntilEnd(stream: InflaterInputStream): String {
    val content = StringBuilder()
    var data = stream.read()
    while (data != -1) {
        val char = data.toChar()
        content.append(char)
        data = stream.read()
    }
    return content.toString()
}

fun readUntilDelimiter(stream: InflaterInputStream, delimiter: Char): String? {
    val sb = StringBuilder()
    var data = stream.read()
    if (data == -1) return null

    while (data != -1 && data.toChar() != delimiter) {
        sb.append(data.toChar())
        data = stream.read()
    }
    return sb.toString()
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
