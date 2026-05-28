package gitinternals.formatters

import gitinternals.objects.TreeObject

class TreeFormatter : GitObjectFormatter<TreeObject> {

    override fun format(gitObject: TreeObject): String {
        return gitObject.elements.joinToString("\n") { element ->
            "${element.metadata} ${element.hash} ${element.name}"
        }
    }

}