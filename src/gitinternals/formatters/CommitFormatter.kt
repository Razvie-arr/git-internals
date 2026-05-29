package gitinternals.formatters

import gitinternals.objects.CommitObject

class CommitFormatter : GitObjectFormatter<CommitObject> {

    override fun format(gitObject: CommitObject): String {
        return buildString {
            appendLine("tree: ${gitObject.tree}")
            if (gitObject.parents.isNotEmpty()) {
                appendLine("parents: ${gitObject.parents.joinToString(" | ")}")
            }
            appendLine("author: ${gitObject.author}")
            appendLine("committer: ${gitObject.committer}")
            appendLine("commit message:")
            append(gitObject.message)
        }
    }

}