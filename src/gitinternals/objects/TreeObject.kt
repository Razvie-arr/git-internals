package gitinternals.objects

import gitinternals.formatters.TreeFormatter

data class TreeObject(val elements: List<TreeElement>) : GitObject {

    override fun format() = TreeFormatter().format(this)

}

data class TreeElement(val metadata: String, val name: String, val hash: String)