package gitinternals.parsers

import gitinternals.objects.TreeEntry
import gitinternals.objects.TreeObject
import gitinternals.utils.readUntilDelimiter
import java.util.zip.InflaterInputStream

class TreeParser(private val stream: InflaterInputStream) : GitObjectParser<TreeObject> {

    override fun parse(): TreeObject {
        return TreeObject(parseEntries())
    }

    private fun parseEntries(): List<TreeEntry> {
        return buildList {
            while (true) {
                val entry = parseNextEntry() ?: break
                add(entry)
            }
        }
    }

    private fun parseNextEntry(): TreeEntry? {
        val permissions = readUntilDelimiter(stream, ' ') ?: return null
        val name = readUntilDelimiter(stream, '\u0000') ?: return null
        val hash = readHash()
        return TreeEntry(permissions, name, hash)
    }

    private fun readHash(): String {
        val bytes = ByteArray(20)
        val readCount = stream.read(bytes)
        if (readCount != 20) error("Unexpected end of tree object")

        return bytes.joinToString("") { byte ->
            "%02x".format(byte)
        }
    }

}