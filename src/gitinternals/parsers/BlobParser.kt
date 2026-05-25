package gitinternals.parsers

import gitinternals.utils.readContent
import java.util.zip.InflaterInputStream

class BlobParser(val stream: InflaterInputStream) : GitObjectParser {

    override fun parseToString(): String {
        return "*BLOB*\n" + readContent(stream)
    }

}