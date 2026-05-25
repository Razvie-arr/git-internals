package gitinternals

import gitinternals.utils.buildGitObjectPath
import gitinternals.utils.readHeader
import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path

/**
 * Stage 2: Reads and prints Git object type and length
 */
fun printGitObjectType() {
    println("Enter .git directory location:")
    val gitPath = Path(readln())
    println("Enter git object hash:")
    val gitObjectHash = readln()

    val gitObjectPath = buildGitObjectPath(gitPath, gitObjectHash)
    val (type, length) = parseObjectHeader(gitObjectPath).split(' ')
    println("type:$type length:$length")
}

private fun parseObjectHeader(objectLocation: Path): String {
    return FileInputStream(objectLocation.toFile()).use { fis ->
        InflaterInputStream(fis).use { iis ->
            readHeader(iis)
        }
    }
}