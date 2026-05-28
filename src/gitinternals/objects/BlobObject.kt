package gitinternals.objects

import gitinternals.formatters.BlobFormatter

data class BlobObject(val content: String) : GitObject {

    override fun format() = BlobFormatter().format(this)

}
