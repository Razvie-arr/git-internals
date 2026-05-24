package gitinternals

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.file.Path
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.zip.InflaterInputStream
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Stage 3: For blob objects prints type and a content. For commit prints structured data about comment.
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
            val content = readContent(iis)
            return when (type) {
                "blob" -> "*BLOB*\n$content"
                "commit" -> "*COMMIT*\n${CommitParser(content).parseToString()}"
                else -> throw IllegalArgumentException("Not supported git object type.")
            }
        }
    }
}

private fun readHeader(iis: InflaterInputStream): String {
    val header = StringBuilder()
    var data = iis.read()
    while (!isHeaderSeparator(data)) {
        val char = data.toChar()
        header.append(char)
        data = iis.read()
    }
    return header.toString()
}

private fun readContent(iis: InflaterInputStream): String {
    val content = StringBuilder()
    var data = iis.read()
    while (data != -1) {
        val char = data.toChar()
        content.append(char)
        data = iis.read()
    }
    return content.toString()
}

private fun isHeaderSeparator(data: Int) = data == 0

private class CommitParser(val content: String) {

    fun parseToString(): String {
        var tree = ""
        val parents = mutableListOf<String>()
        var author = ""
        var committer = ""

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

        return buildString {
            appendLine("tree: $tree")
            if (parents.isNotEmpty()) {
                appendLine("parents: ${parents.joinToString(" | ")}")
            }
            appendLine("author: $author")
            appendLine("committer: $committer")
            appendLine("commit message:")
            append(commitMessage)
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

    private fun parseDeveloperInfo(rawDeveloper: String): DeveloperInfo {
        val split = rawDeveloper.split(" ")
        val name = split[0]
        val email = split[1].removePrefix("<").removeSuffix(">")
        val timestamp = split[2]
        val zoneOffset = split[3]
        val instant = Instant.ofEpochSecond(timestamp.toLong())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX")
            .withZone(ZoneOffset.of(zoneOffset))

        return DeveloperInfo(name, email, formatter.format(instant))
    }

}

private data class DeveloperInfo(
    val name: String,
    val email: String,
    val dateTimeWithZone: String
)