package gitinternals.commands

import java.nio.file.Path
import kotlin.io.path.*

class ListBranchesCommand(private val gitDir: Path) : GitCommand {

    override fun execute() {
        val gitHeadFile: Path = gitDir.resolve("HEAD")
        if (gitHeadFile.notExists()) {
            error("Git HEAD file doesn't exist.")
        }
        val currentBranchName = parseCurrentBranchName(gitHeadFile)

        val branchesDir = gitDir.resolve("refs/heads")
        if (branchesDir.notExists()) {
            error("Branches directory doesn't exist.")
        }
        val branchNames = parseBranchNamesSorted(branchesDir)

        for (branchName in branchNames) {
            if (branchName == currentBranchName) {
                println("* $branchName")
            } else {
                println("  $branchName")
            }
        }
    }

    private fun parseCurrentBranchName(gitHeadFile: Path) =
        gitHeadFile.readText().substringAfterLast("/").trimEnd()

    private fun parseBranchNamesSorted(branchesDir: Path) =
        branchesDir.listDirectoryEntries().filter { it.isRegularFile() }.map { it.name }.sorted()

}