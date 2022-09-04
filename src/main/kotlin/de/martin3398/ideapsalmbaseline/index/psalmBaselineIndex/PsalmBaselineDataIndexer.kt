package de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex

import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.BaselineErrorsModel
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.BaselineFileModel
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory

class PsalmBaselineDataIndexer : DataIndexer<String, BaselineFileModel, FileContent> {
    override fun map(inputData: FileContent): MutableMap<String, BaselineFileModel> {
        val parsedBaseline = mutableMapOf<String, BaselineFileModel>()

        val doc =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ByteArrayInputStream(inputData.content))
        val files = doc.documentElement.childNodes

        for (i in 0 until files.length) {
            val fileNode = files.item(i)
            if (fileNode !is Element || fileNode.nodeName != "file") continue

            val fileName = fileNode.getAttribute("src")
            val errors = parseFile(fileNode)

            parsedBaseline[fileName] = BaselineFileModel(errors, i)
        }

        return parsedBaseline
    }

    private fun parseFile(node: Element): List<BaselineErrorsModel> {
        val errors = mutableListOf<BaselineErrorsModel>()

        val errorNodes = node.childNodes
        for (i in 0 until errorNodes.length) {
            val errorNode = errorNodes.item(i)
            if (errorNode !is Element) continue

            val type = errorNode.nodeName
            val occurrences = errorNode.getAttribute("occurrences").toInt()
            val code = parseCodeNodes(errorNode)

            errors.add(BaselineErrorsModel(type, occurrences, code))
        }

        return errors
    }

    private fun parseCodeNodes(errorNode: Element): List<String> {
        val codeNodes = errorNode.childNodes
        val codes = mutableListOf<String>()
        for (i in 0 until codeNodes.length) {
            val codeNode = codeNodes.item(i)
            if (codeNode !is Element || codeNode.textContent == null) continue

            codes.add(codeNode.textContent)
        }

        return codes
    }
}
