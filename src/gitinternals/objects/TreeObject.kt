package gitinternals.objects

import gitinternals.formatters.TreeFormatter

data class TreeObject(val entries: List<TreeEntry>) : GitObject {

    override fun format() = TreeFormatter().format(this)

}

data class TreeEntry(val permissions: String, val name: String, val hash: String) {

    fun type(): TreeEntryType = when (permissions) {
        "40000" -> TreeEntryType.TREE
        "100644" -> TreeEntryType.BLOB
        else -> error("Unsupported tree entry type.")
    }

}

enum class TreeEntryType {
    BLOB,
    TREE
}