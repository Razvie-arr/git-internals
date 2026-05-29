package gitinternals.objects

import gitinternals.formatters.TreeFormatter

data class TreeObject(val elements: List<TreeEntry>) : GitObject {

    override fun format() = TreeFormatter().format(this)

}

data class TreeEntry(val permissions: String, val name: String, val hash: String)