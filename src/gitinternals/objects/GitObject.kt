package gitinternals.objects

sealed interface GitObject {

    fun format(): String

}