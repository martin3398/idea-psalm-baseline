package de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex

import com.intellij.openapi.project.ProjectManager
import com.intellij.util.indexing.DataIndexer
import com.intellij.util.indexing.FileContent
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.BaselineErrorsModel
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.BaselineFileModel
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.Path
import kotlin.io.path.relativeTo

class PsalmBaselineDataIndexer : DataIndexer<String, BaselineFileModel, FileContent> {
    override fun map(inputData: FileContent): MutableMap<String, BaselineFileModel> {
        val parsedBaseline = mutableMapOf<String, BaselineFileModel>()
        val baselinePath = inputData.file.path
        val baselineDir = Path(baselinePath).parent
        val projectPath = Path(ProjectManager.getInstance().openProjects[0].basePath!!)
        val relativeBaselinePath = baselineDir.relativeTo(projectPath)

        val doc =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(ByteArrayInputStream(inputData.content))
        val files = doc.documentElement.childNodes

        for (i in 0 until files.length) {
            val fileNode = files.item(i)
            if (fileNode !is Element || fileNode.nodeName != "file") continue

            val fileName = relativeBaselinePath.resolve(fileNode.getAttribute("src")).toString()
            val errors = parseFile(fileNode)

            parsedBaseline[fileName] = BaselineFileModel(errors, i, baselinePath)
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
            val code = parseCodeNodes(errorNode)
            val occurrences = errorNode.getAttribute("occurrences").toIntOrNull() ?: code.size

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
