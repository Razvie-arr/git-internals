package gitinternals.parsers

sealed interface GitObjectParser<T> {

    fun parse(): T

}