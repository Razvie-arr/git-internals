package gitinternals.parsers

import gitinternals.utils.readUntilEnd
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream

class CommitParser(val stream: InflaterInputStream) : GitObjectParser {

    override fun parseToString(): String {
        val commit = parse()
        return buildString {
            appendLine("tree: ${commit.tree}")
            if (commit.parents.isNotEmpty()) {
                appendLine("parents: ${commit.parents.joinToString(" | ")}")
            }
            appendLine("author: $commit.author")
            appendLine("committer: ${commit.committer}")
            appendLine("commit message:")
            append(commit.message)
        }
    }

    fun parse(): ParsedCommit {
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
                line.startsWith("gpgsig ") -> index = skipSignature(lines, index)
            }
            index++
        }

        val message = lines.drop(index + 1).joinToString("\n")

        return ParsedCommit(tree, parents, author, committer, message)
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
        val name = rawDeveloper.substringBefore(" <")
        val email = rawDeveloper.substringAfter("<").substringBefore(">")
        val timeData = rawDeveloper.substringAfter("> ").split(" ")
        val timestamp = timeData[0]
        val zoneOffset = timeData[1]

        val instant = Instant.ofEpochSecond(timestamp.toLong())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
            .withZone(ZoneOffset.of(zoneOffset))

        return CommitDeveloperInfo(name, email, formatter.format(instant))
    }

    private fun skipSignature(lines: List<String>, startIndex: Int): Int {
        var index = startIndex + 1

        while (index < lines.size) {
            if (lines[index].isBlank()) {
                return index + 1
            }
            index++
        }

        return index
    }

}

data class ParsedCommit(
    val tree: String,
    val parents: List<String>,
    val author: String,
    val committer: String,
    val message: String
)

private data class CommitDeveloperInfo(
    val name: String,
    val email: String,
    val dateTimeWithZone: String
)