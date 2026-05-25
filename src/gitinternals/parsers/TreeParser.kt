package gitinternals.parsers

import java.util.zip.InflaterInputStream

class TreeParser(val stream: InflaterInputStream) : GitObjectParser {

    override fun parseToString(): String {
        val treeElements = parseElements()
        return "*TREE*\n" + treeElements.joinToString("\n") { element ->
            "${element.metadata} ${element.hash} ${element.name}"
        }
    }

    private fun parseElements(): List<TreeElement> {
        return buildList {
            while (true) {
                val element = parseElement() ?: break
                add(element)
            }
        }
    }

    private fun parseElement(): TreeElement? {
        val metadata = readUntilDelimiter(' ') ?: return null
        val name = readUntilDelimiter('\u0000') ?: return null
        val hash = readHash()
        return TreeElement(metadata, name, hash)
    }

    private fun readUntilDelimiter(delimiter: Char): String? {
        val sb = StringBuilder()
        var data = stream.read()
        if (data == -1) return null

        while (data != -1 && data.toChar() != delimiter) {
            sb.append(data.toChar())
            data = stream.read()
        }
        return sb.toString()
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

private data class TreeElement(val metadata: String, val name: String, val hash: String)