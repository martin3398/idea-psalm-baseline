package de.martin3398.ideapsalmbaseline.localInspection.classBaselineInspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.BaselineFileModel
import de.martin3398.ideapsalmbaseline.intention.RemoveFromBaselineIntention

class Visitor(
    private val pattern: PsiElementPattern.Capture<PsiElement>,
    private val errorCount: Int,
    private val holder: ProblemsHolder,
    private val baselineFileModel: BaselineFileModel
) : PsiElementVisitor() {
    override fun visitElement(element: PsiElement) {
        if (pattern.accepts(element)) {
            holder.registerProblem(
                element,
                "$errorCount errors are ignored by the Psalm Baseline.",
                ProblemHighlightType.WARNING,
                RemoveFromBaselineIntention(baselineFileModel.index, baselineFileModel.file)
            )
            return
        }

        super.visitElement(element)
    }
}
