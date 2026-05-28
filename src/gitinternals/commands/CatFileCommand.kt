package gitinternals.commands

import gitinternals.parsers.BlobParser
import gitinternals.parsers.CommitParser
import gitinternals.parsers.TreeParser
import gitinternals.utils.useGitObjectStream
import java.nio.file.Path

class CatFileCommand(private val gitDir: Path) : GitCommand {

    override fun execute() {
        println("Enter git object hash:")
        val gitObjectHash = readln()
        println(getObjectInfo(gitObjectHash))
    }

    private fun getObjectInfo(gitObjectHash: String): String {
        return useGitObjectStream(gitDir, gitObjectHash) { type, stream ->
            val parser = when (type) {
                "blob" -> BlobParser(stream)
                "commit" -> CommitParser(stream)
                "tree" -> TreeParser(stream)
                else -> error("Unsupported git object type.")
            }

            val parsedObject = parser.parse()

            buildString {
                appendLine("*${type.uppercase()}*")
                append(parsedObject.format())
            }
        }
    }

}