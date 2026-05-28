package gitinternals.formatters

interface GitObjectFormatter<T> {

    fun format(gitObject: T): String

}