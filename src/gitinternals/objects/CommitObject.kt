package gitinternals.objects

import gitinternals.formatters.CommitFormatter

data class CommitObject(
    val tree: String,
    val parents: List<String>,
    val author: String,
    val committer: String,
    val message: String
) : GitObject {

    override fun format() = CommitFormatter().format(this)

}