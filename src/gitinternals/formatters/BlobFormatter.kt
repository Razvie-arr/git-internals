package gitinternals.formatters

import gitinternals.objects.BlobObject

class BlobFormatter : GitObjectFormatter<BlobObject> {

    override fun format(gitObject: BlobObject): String {
        return gitObject.content
    }

}