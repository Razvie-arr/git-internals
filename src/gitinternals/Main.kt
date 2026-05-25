package gitinternals

import gitinternals.parsers.BlobParser
import gitinternals.parsers.CommitParser
import gitinternals.parsers.GitObjectParser
import gitinternals.parsers.TreeParser
import gitinternals.utils.buildGitObjectPath
import gitinternals.utils.readHeader
import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path

/**
 * Stages 3-4: Prints Git object information. Supports blob, commit and a tree types.
 */
fun main() {
    println("Enter .git directory location:")
    val gitPath = Path(readln())
    println("Enter git object hash:")
    val gitObjectHash = readln()
    val gitObjectPath = buildGitObjectPath(gitPath, gitObjectHash)
    println(getObjectInfo(gitObjectPath))
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