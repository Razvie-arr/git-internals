package gitinternals

import gitinternals.parsers.BlobParser
import gitinternals.parsers.CommitParser
import gitinternals.parsers.GitObjectParser
import gitinternals.parsers.TreeParser
import gitinternals.utils.readHeader
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Path
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Stages 3-4: Prints Git object information.
 * <p>
 *   <li>Blob: prints type and content.</li>
 *   <li>Commit: prints formatted commit information.</li>
 *   <li>Tree: prints directory contents.</li>
 * </p>
 */
fun main() {
    println("Enter .git directory location:")
    val gitPath = Path(readln())
    println("Enter git object hash:")
    val gitObjectHash = readln()
    val gitObjectPath = buildGitObjectPath(gitPath, gitObjectHash)
    println(getObjectInfo(gitObjectPath))
}

private fun buildGitObjectPath(gitPath: Path, gitObjectHash: String): Path {
    if (!gitPath.exists()) {
        throw FileNotFoundException("$gitPath not found!")
    }

    val objectFolder = gitObjectHash.substring(0, 2)
    val objectFile = gitObjectHash.substring(2)
    val gitObjectPath = gitPath
        .resolve("objects")
        .resolve(objectFolder)
        .resolve(objectFile)

    if (!gitObjectPath.exists()) {
        throw FileNotFoundException("Input git object location is not found, finishing program...")
    }

    return gitObjectPath
}

private fun getObjectInfo(gitObjectPath: Path): String {
    FileInputStream(gitObjectPath.toFile()).use { fis ->
        InflaterInputStream(fis).use { iis ->
            val type = readHeader(iis).split(" ")[0]
            val parser: GitObjectParser = when (type) {
                "blob" -> BlobParser(iis)
                "commit" -> CommitParser(iis)
                "tree" -> TreeParser(iis)
                else -> error("Not supported git object provided.")
            }
            return parser.parseToString()
        }
    }
}