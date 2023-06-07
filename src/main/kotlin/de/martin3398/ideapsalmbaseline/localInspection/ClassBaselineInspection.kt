package de.martin3398.ideapsalmbaseline.localInspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.util.indexing.FileBasedIndex
import com.jetbrains.php.lang.lexer.PhpTokenTypes
import com.jetbrains.php.lang.psi.PhpFile
import com.jetbrains.php.lang.psi.elements.PhpClass
import de.martin3398.ideapsalmbaseline.index.PsalmBaselineIndex
import de.martin3398.ideapsalmbaseline.localInspection.classBaselineInspection.Visitor
import de.martin3398.ideapsalmbaseline.util.getRelativePath

class ClassBaselineInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val file = holder.file
        val filename = try {
            getRelativePath(file.virtualFile.path, holder.project.basePath!!)
        } catch (e: IllegalArgumentException) {
            return super.buildVisitor(holder, isOnTheFly)
        }

        if (file !is PhpFile || IGNORED_DIRS.any { filename.startsWith(it) }) {
            return super.buildVisitor(holder, isOnTheFly)
        }

        val baselineErrors = FileBasedIndex.getInstance()
            .getValues(PsalmBaselineIndex.key, filename, GlobalSearchScope.allScope(holder.project))

        if (baselineErrors.isEmpty()) {
            return super.buildVisitor(holder, isOnTheFly)
        }

        val errorCount = baselineErrors.sumOf { fileModel ->
            fileModel.errors.sumOf { it.occurrences }
        }

        return Visitor(classNameIdentifierPattern, errorCount, holder, baselineErrors[0])
    }

    companion object {
        val IGNORED_DIRS = listOf("vendor/")
        val classNameIdentifierPattern = PlatformPatterns
            .psiElement(PhpTokenTypes.IDENTIFIER)
            .withParent(PlatformPatterns.psiElement(PhpClass::class.java))
    }
}
