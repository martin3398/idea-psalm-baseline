package de.martin3398.ideapsalmbaseline.index

import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.PsalmBaselineDataIndexer
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.PsalmBaselineIndexExternalizer
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.PsalmBaselineModel

class PsalmBaselineIndex : FileBasedIndexExtension<String, PsalmBaselineModel>() {
    private val externalizer: DataExternalizer<PsalmBaselineModel> = PsalmBaselineIndexExternalizer()

    override fun getName(): ID<String, PsalmBaselineModel> = key

    override fun getIndexer(): DataIndexer<String, PsalmBaselineModel, FileContent> = PsalmBaselineDataIndexer()

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<PsalmBaselineModel> = externalizer

    override fun getVersion(): Int = VERSION

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        FileBasedIndex.InputFilter { it.name == BASELINE_FILENAME }

    override fun dependsOnFileContent(): Boolean = true

    companion object {
        const val VERSION = 1
        const val BASELINE_FILENAME = "psalm-baseline.xml"
        val key = ID.create<String, PsalmBaselineModel>("de.martin3398.ideapsalmbaseline")
    }
}
