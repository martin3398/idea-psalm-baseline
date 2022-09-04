package de.martin3398.ideapsalmbaseline.localInspection.classBaselineInspection

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

class Visitor(
    private val pattern: PsiElementPattern.Capture<PsiElement>,
    private val errorCount: Int,
    private val holder: ProblemsHolder
) : PsiElementVisitor() {
    override fun visitElement(element: PsiElement) {
        if (pattern.accepts(element)) {
            holder.registerProblem(
                element,
                "$errorCount errors are ignored by the Psalm Baseline.",
                ProblemHighlightType.WARNING
            )
            return
        }

        super.visitElement(element)
    }
}
