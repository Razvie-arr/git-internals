package gitinternals.commands

import java.nio.file.Path

interface GitCommand {

    fun execute(gitDir: Path)

}