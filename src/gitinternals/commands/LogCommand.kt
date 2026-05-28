package gitinternals.commands

import gitinternals.objects.CommitObject
import gitinternals.parsers.CommitParser
import gitinternals.utils.useGitObjectStream
import java.nio.file.Path
import kotlin.io.path.notExists
import kotlin.io.path.readText

class LogCommand(private val gitDir: Path) : GitCommand {

    override fun execute() {
        println("Enter branch name:")
        val branchName = readln()
        val branchFile = gitDir.resolve("refs/heads/$branchName")
        if (branchFile.notExists()) {
            error("Branch file doesn't exist.")
        }

        val branchCommitHash = getBranchCommitHash(branchFile)
        printLog(branchCommitHash)
    }

    private fun getBranchCommitHash(branchFile: Path) = branchFile.readText().trimEnd()

    private fun printLog(commitHash: String) {
        val commit = parseCommit(commitHash)
        printCommitInfo(commitHash, commit, false)

        if (commit.parents.isEmpty()) { // initial commit
            return
        }
        if (commit.parents.size == 1) {
            printLog(commit.parents[0])
            return
        }
        if (commit.parents.size == 2) {
            val mergedCommitHash = commit.parents[1]
            val mergedCommit = parseCommit(mergedCommitHash)
            printCommitInfo(mergedCommitHash, mergedCommit, true)
            printLog(commit.parents[0])
            return
        }

        error("More than two parent commits are not supported.")
    }

    private fun parseCommit(commitHash: String): CommitObject {
        return useGitObjectStream(gitDir, commitHash) { _, stream ->
            CommitParser(stream).parse()
        }
    }

    private fun printCommitInfo(commitHash: String, commit: CommitObject, mergedParent: Boolean) {
        val mergedParentLabel = if (mergedParent) " (merged)" else ""
        println("Commit: $commitHash$mergedParentLabel")
        println(commit.committer)
        println(commit.message)
    }

}