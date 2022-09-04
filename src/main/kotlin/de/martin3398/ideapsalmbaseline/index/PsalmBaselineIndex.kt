package de.martin3398.ideapsalmbaseline.index

import com.intellij.util.indexing.*
import com.intellij.util.io.DataExternalizer
import com.intellij.util.io.EnumeratorStringDescriptor
import com.intellij.util.io.KeyDescriptor
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.PsalmBaselineDataIndexer
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.PsalmBaselineIndexExternalizer
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.BaselineFileModel

class PsalmBaselineIndex : FileBasedIndexExtension<String, BaselineFileModel>() {
    private val externalizer: DataExternalizer<BaselineFileModel> = PsalmBaselineIndexExternalizer()

    override fun getName(): ID<String, BaselineFileModel> = ID.create("de.martin3398.ideapsalmbaseline")

    override fun getIndexer(): DataIndexer<String, BaselineFileModel, FileContent> = PsalmBaselineDataIndexer()

    override fun getKeyDescriptor(): KeyDescriptor<String> = EnumeratorStringDescriptor.INSTANCE

    override fun getValueExternalizer(): DataExternalizer<BaselineFileModel> = externalizer

    override fun getVersion(): Int = VERSION

    override fun getInputFilter(): FileBasedIndex.InputFilter =
        FileBasedIndex.InputFilter { it.name == BASELINE_FILENAME }

    override fun dependsOnFileContent(): Boolean = true

    companion object {
        const val VERSION = 1

        // TODO: use config, import from psalm config
        const val BASELINE_FILENAME = "psalm-baseline.xml"
    }
}
