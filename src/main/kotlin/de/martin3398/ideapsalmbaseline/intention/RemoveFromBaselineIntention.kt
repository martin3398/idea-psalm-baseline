package de.martin3398.ideapsalmbaseline.intention

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import com.intellij.util.indexing.FileBasedIndex
import de.martin3398.ideapsalmbaseline.index.PsalmBaselineIndex
import org.w3c.dom.Document
import java.io.FileOutputStream
import java.io.OutputStream
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class RemoveFromBaselineIntention(private val baselineIndex: Int) : LocalQuickFix {
    override fun getFamilyName(): String = NAME

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        CommandProcessor.getInstance().executeCommand(
            project,
            removeFromBaseline(project.basePath + "/" + PsalmBaselineIndex.BASELINE_FILENAME),
            NAME,
            null
        )
    }

    private fun removeFromBaseline(baselineFilename: String): () -> Unit {
        return fun() {
            val domFactory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val domBuilder: DocumentBuilder = domFactory.newDocumentBuilder()
            val document: Document = domBuilder.parse(baselineFilename)
            val os: OutputStream = FileOutputStream(baselineFilename)

            val files = document.documentElement
            files.removeChild(files.childNodes.item(baselineIndex))

            val transformerFactory: TransformerFactory = TransformerFactory.newInstance()
            val transformer: Transformer = transformerFactory.newTransformer()
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")

            os.write(BASELINE_XML_DECLARATION.toByteArray(Charsets.UTF_8))
            transformer.transform(
                DOMSource(document),
                StreamResult(os)
            )

            FileBasedIndex.getInstance()
                .scheduleRebuild(PsalmBaselineIndex.key, Throwable("Refresh Psalm Baseline Index"))
        }
    }

    companion object {
        private const val NAME = "Remove this file from the Psalm baseline"
        private const val BASELINE_XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    }
}
