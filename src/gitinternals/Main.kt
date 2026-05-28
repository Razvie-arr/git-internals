package gitinternals

import gitinternals.commands.CatFileCommand
import gitinternals.commands.GitCommand
import gitinternals.commands.ListBranchesCommand
import gitinternals.commands.LogCommand
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.notExists

/**
 * Git internals CLI application.
 *
 * Supports the following commands:
 * - cat-file: Display Git object information (blob, commit, tree)
 * - list-branches: List repository branches
 *
 * Usage:
 * 1. Enter the path to the .git directory
 * 2. Enter a command name
 * 3. Follow command-specific prompts
 */

private val COMMAND_REGISTRY: Map<String, (gitDir: Path) -> GitCommand> = mapOf(
    "cat-file" to ::CatFileCommand,
    "list-branches" to ::ListBranchesCommand,
    "log" to ::LogCommand
)

fun main() {
    println("Enter .git directory location:")
    val gitDir = Path(readln())
    if (gitDir.notExists()) {
        error("Git path doesn't exist.")
    }

    println("Enter command:")
    val inputCommand = readln()

    val factory = COMMAND_REGISTRY[inputCommand]
    if (factory == null) {
        println("Unsupported command: $inputCommand.")
        return
    }

    val command = factory(gitDir)
    command.execute()
}