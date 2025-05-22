package de.martin3398.ideapsalmbaseline.intention

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.diagnostic.RuntimeExceptionWithAttachments
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.indexing.FileBasedIndex
import de.martin3398.ideapsalmbaseline.index.PsalmBaselineIndex
import org.w3c.dom.Document
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class RemoveFromBaselineIntention(private val baselineIndex: Int, private val baselineFilePath: String) :
    LocalQuickFix {
    override fun getFamilyName(): String = NAME

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        try {
            CommandProcessor.getInstance().executeCommand(
                project,
                removeFromBaselineCallback(project),
                NAME,
                null
            )
        } catch (e: RuntimeExceptionWithAttachments) {
            if (e.userMessage != "Access is allowed from write thread only") {
                throw e
            }
        }
    }

    private fun removeFromBaselineCallback(project: Project): () -> Unit {
        return fun() {
            ApplicationManager.getApplication().invokeLater {
                val document = getDocument(baselineFilePath)
                val files = document.documentElement

                files.removeChild(files.childNodes.item(baselineIndex))

                val os: OutputStream = FileOutputStream(baselineFilePath)
                os.write(BASELINE_XML_DECLARATION.toByteArray(Charsets.UTF_8))
                getTransformer().transform(
                    DOMSource(document),
                    StreamResult(os)
                )
                os.write("\n".toByteArray(Charsets.UTF_8))

                val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(File(baselineFilePath))
                virtualFile?.let {
                    it.refresh(false, false)

                    VcsDirtyScopeManager.getInstance(project).fileDirty(it)

                    FileBasedIndex.getInstance().requestRebuild(
                        PsalmBaselineIndex.key,
                        Throwable("Refresh Psalm Baseline Index")
                    )
                }
            }
        }
    }

    private fun getDocument(path: String): Document =
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(path)

    private fun getTransformer(): Transformer = TransformerFactory.newInstance().newTransformer().also {
        it.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        it.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
    }

    companion object {
        private const val NAME = "Remove this file from the Psalm baseline"
        private const val BASELINE_XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    }
}
