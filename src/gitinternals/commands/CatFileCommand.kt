package gitinternals.commands

import gitinternals.parsers.BlobParser
import gitinternals.parsers.CommitParser
import gitinternals.parsers.GitObjectParser
import gitinternals.parsers.TreeParser
import gitinternals.utils.buildGitObjectPath
import gitinternals.utils.readHeader
import java.io.FileInputStream
import java.nio.file.Path
import java.util.zip.InflaterInputStream

class CatFileCommand : GitCommand {

    override fun execute(gitDir: Path) {
        println("Enter git object hash:")
        val gitObjectHash = readln()
        val gitObjectPath = buildGitObjectPath(gitDir, gitObjectHash)
        println(getObjectInfo(gitObjectPath))
    }

    private fun getObjectInfo(gitObjectPath: Path): String {
        FileInputStream(gitObjectPath.toFile()).use { fis ->
            InflaterInputStream(fis).use { iis ->
                val type = readHeader(iis).substringBefore(' ')
                val parser: GitObjectParser = when (type) {
                    "blob" -> BlobParser(iis)
                    "commit" -> CommitParser(iis)
                    "tree" -> TreeParser(iis)
                    else -> error("Unsupported git object type.")
                }
                return parser.parseToString()
            }
        }
    }

}