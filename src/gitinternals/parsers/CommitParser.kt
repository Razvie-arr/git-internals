package gitinternals.parsers

import gitinternals.utils.readUntilEnd
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream

class CommitParser(val stream: InflaterInputStream) : GitObjectParser {

    override fun parseToString(): String {
        var tree = ""
        val parents = mutableListOf<String>()
        var author = ""
        var committer = ""

        val content = readUntilEnd(stream)
        val lines = content.lines()
        var index = 0

        while (index < lines.size && lines[index].isNotBlank()) {
            val line = lines[index]
            when {
                line.startsWith("tree") -> tree = line.removePrefix("tree ")
                line.startsWith("parent") -> parents += line.removePrefix("parent ")
                line.startsWith("author") -> author = formatAuthor(line.removePrefix("author "))
                line.startsWith("committer ") -> committer = formatCommitter(line.removePrefix("committer "))
            }
            index++
        }

        val commitMessage = lines.drop(index + 1).joinToString("\n")

        return "*COMMIT*\n" + buildString {
            appendLine("tree: $tree")
            if (parents.isNotEmpty()) {
                appendLine("parents: ${parents.joinToString(" | ")}")
            }
            appendLine("author: $author")
            appendLine("committer: $committer")
            appendLine("commit message:")
            appendLine(commitMessage)
        }
    }

    private fun formatAuthor(rawAuthor: String): String {
        val devInfo = parseDeveloperInfo(rawAuthor)
        return "${devInfo.name} ${devInfo.email} original timestamp: ${devInfo.dateTimeWithZone}"
    }

    private fun formatCommitter(rawCommiter: String): String {
        val devInfo = parseDeveloperInfo(rawCommiter)
        return "${devInfo.name} ${devInfo.email} commit timestamp: ${devInfo.dateTimeWithZone}"
    }

    private fun parseDeveloperInfo(rawDeveloper: String): CommitDeveloperInfo {
        val split = rawDeveloper.split(" ")
        val name = split[0]
        val email = split[1].removePrefix("<").removeSuffix(">")
        val timestamp = split[2]
        val zoneOffset = split[3]
        val instant = Instant.ofEpochSecond(timestamp.toLong())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
            .withZone(ZoneOffset.of(zoneOffset))

        return CommitDeveloperInfo(name, email, formatter.format(instant))
    }

}

private data class CommitDeveloperInfo(
    val name: String,
    val email: String,
    val dateTimeWithZone: String
)