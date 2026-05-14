package gitinternals

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path
import kotlin.io.path.exists

fun main() {
    println("Enter .git directory location:")
    val gitPath = Path(readln())
    println("Enter git object hash:")
    val gitObjectHash = readln()

    val gitObjectPath = buildGitObjectPath(gitPath, gitObjectHash)
    val (type, length) = readHeader(gitObjectPath).split(' ')
    println("type:$type length:$length")
}

private fun buildGitObjectPath(gitPath: Path, gitObjectHash: String): Path {
    if (!gitPath.exists()) {
        throw FileNotFoundException("$gitPath not found!")
    }

    val objectFolder = gitObjectHash.substring(0, 2)
    val objectFile = gitObjectHash.substring(2)
    return gitPath
        .resolve("objects")
        .resolve(objectFolder)
        .resolve(objectFile)
}

private fun readHeader(objectLocation: Path): String {
    if (!objectLocation.exists()) {
        throw FileNotFoundException("Input git object location is not found, finishing program...")
    }

    return FileInputStream(objectLocation.toFile()).use { fis ->
        InflaterInputStream(fis).use { iis ->
            val sb = StringBuilder()
            var data = iis.read()
            while (data != -1) {
                val char = data.toChar()
                if (isHeaderSeparator(char)) break
                sb.append(char)
                data = iis.read()
            }
            sb.toString()
        }
    }
}

private fun isHeaderSeparator(ch: Char) = ch == '\u0000'