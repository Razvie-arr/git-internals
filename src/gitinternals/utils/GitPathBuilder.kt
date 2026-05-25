package gitinternals.utils

import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.exists

fun buildGitObjectPath(gitPath: Path, gitObjectHash: String): Path {
    if (!gitPath.exists()) {
        throw FileNotFoundException("$gitPath not found!")
    }

    val objectFolder = gitObjectHash.substring(0, 2)
    val objectFile = gitObjectHash.substring(2)
    val gitObjectPath = gitPath
        .resolve("objects")
        .resolve(objectFolder)
        .resolve(objectFile)

    if (!gitObjectPath.exists()) {
        throw FileNotFoundException("Input git object location is not found, finishing program...")
    }

    return gitObjectPath
}
