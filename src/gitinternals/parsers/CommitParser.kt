package gitinternals.parsers

import gitinternals.objects.CommitObject
import gitinternals.utils.readUntilEnd
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream

class CommitParser(val stream: InflaterInputStream) : GitObjectParser<CommitObject> {

    override fun parse(): CommitObject {
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
                line.startsWith("author") -> author = parseAuthor(line.removePrefix("author "))
                line.startsWith("committer ") -> committer = parseCommitter(line.removePrefix("committer "))
                line.startsWith("gpgsig ") -> index = skipSignature(lines, index)
            }
            index++
        }

        val message = lines.drop(index + 1).joinToString("\n")

        return CommitObject(tree, parents, author, committer, message)
    }

    private fun parseAuthor(rawAuthor: String): String {
        val devInfo = parseDeveloperInfo(rawAuthor)
        return "${devInfo.name} ${devInfo.email} original timestamp: ${devInfo.dateTimeWithZone}"
    }

    private fun parseCommitter(rawCommiter: String): String {
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

    private data class CommitDeveloperInfo(
        val name: String,
        val email: String,
        val dateTimeWithZone: String
    )

}