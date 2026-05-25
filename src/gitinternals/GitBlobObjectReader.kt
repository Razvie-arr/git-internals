package gitinternals

import gitinternals.utils.readHeader
import gitinternals.utils.readUntilEnd
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Stage 1: Reads and prints uncompressed content of Git object
 */
fun printObjectContent() {
    println("Enter git object location:")
    val objectLocationInput = readln()
    val objectLocationPath = Path(objectLocationInput)

    if (!objectLocationPath.exists()) {
        throw FileNotFoundException("Input git object location is not found, finishing program...")
    }

    val content = parseFullObjectContent(objectLocationPath)
    println(content)
}

private fun parseFullObjectContent(objectLocation: Path): String {
    return FileInputStream(objectLocation.toFile()).use { fis ->
        InflaterInputStream(fis).use { iis ->
            readHeader(iis) + "\n" + readUntilEnd(iis)
        }
    }
}