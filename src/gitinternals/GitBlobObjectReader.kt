package gitinternals

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Reads and prints uncompressed content of Git object
 */
fun main() {
    println("Enter git object location:")
    val objectLocationInput = readln()
    val objectLocationPath = Path(objectLocationInput)
    val content = readUncompressedContent(objectLocationPath)
    println(content)
}

private fun readUncompressedContent(objectLocation: Path): String {
    if (!objectLocation.exists()) {
        throw FileNotFoundException("Input git object location is not found, finishing program...")
    }

    return FileInputStream(objectLocation.toFile()).use { fis ->
        InflaterInputStream(fis).use { iis ->
            val sb = StringBuilder()
            var data = iis.read()
            while (data != -1) {
                val char = data.toChar()
                if (char == '\u0000') {
                    sb.append('\n') // Your clever header separator
                } else {
                    sb.append(char)
                }
                data = iis.read()
            }
            sb.toString()
        }
    }
}