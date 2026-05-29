package gitinternals.parsers

import gitinternals.objects.BlobObject
import gitinternals.utils.readUntilEnd
import java.util.zip.InflaterInputStream

class BlobParser(private val stream: InflaterInputStream) : GitObjectParser<BlobObject> {

    override fun parse(): BlobObject {
        return BlobObject(readUntilEnd(stream))
    }

}