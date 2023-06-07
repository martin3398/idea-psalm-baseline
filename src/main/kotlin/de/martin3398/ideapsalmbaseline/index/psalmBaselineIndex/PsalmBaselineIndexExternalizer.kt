package de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex

import com.intellij.util.io.DataExternalizer
import de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model.PsalmBaselineModel
import java.io.*

class PsalmBaselineIndexExternalizer : DataExternalizer<PsalmBaselineModel> {
    override fun save(out: DataOutput, value: PsalmBaselineModel?) {
        val stream = ByteArrayOutputStream()
        val output: ObjectOutput = ObjectOutputStream(stream)

        output.writeObject(value)
        out.writeInt(stream.size())
        out.write(stream.toByteArray())
    }

    override fun read(input: DataInput): PsalmBaselineModel? {
        val size = input.readInt()
        val buffer = ByteArray(size)

        input.readFully(buffer, 0, size)
        val stream = ByteArrayInputStream(buffer)
        val objInput: ObjectInput = ObjectInputStream(stream)

        try {
            return objInput.readObject() as PsalmBaselineModel
        } catch (ignored: ClassNotFoundException) {
        } catch (ignored: ClassCastException) {
        }

        return null
    }
}
