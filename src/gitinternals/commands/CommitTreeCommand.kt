package gitinternals.commands

import gitinternals.objects.CommitObject
import gitinternals.objects.TreeEntryType
import gitinternals.objects.TreeObject
import gitinternals.parsers.CommitParser
import gitinternals.parsers.TreeParser
import gitinternals.utils.useGitObjectStream
import java.nio.file.Path

class CommitTreeCommand(private val gitDir: Path) : GitCommand {

    override fun execute() {
        println("Enter commit-hash:")
        val commitHash = readln()
        val commit: CommitObject = parseCommit(commitHash)
        printTreeFiles(commit.tree, "")
    }

    private fun printTreeFiles(treeHash: String, prefix: String) {
        val tree = parseTree(treeHash)
        for (entry in tree.entries) {
            val type = entry.type()
            when (type) {
                TreeEntryType.BLOB -> {
                    val filePath = if (prefix.isNotEmpty()) "$prefix/${entry.name}" else entry.name
                    println(filePath)
                }

                TreeEntryType.TREE -> printTreeFiles(entry.hash, entry.name)
            }
        }
    }

    private fun parseCommit(commitHash: String): CommitObject {
        return useGitObjectStream(gitDir, commitHash) { type, stream ->
            if (type != "commit") {
                error("Incorrect object type provided. Expected commit.")
            }
            CommitParser(stream).parse()
        }
    }

    private fun parseTree(treeHash: String): TreeObject {
        return useGitObjectStream(gitDir, treeHash) { type, stream ->
            if (type != "tree") {
                error("Incorrect object type provided. Expected tree.")
            }
            TreeParser(stream).parse()
        }
    }

}