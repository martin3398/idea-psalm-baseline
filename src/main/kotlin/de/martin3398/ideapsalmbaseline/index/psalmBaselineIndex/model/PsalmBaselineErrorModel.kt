package de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model

import java.io.Serializable

data class PsalmBaselineErrorModel(val type: String, val occurrences: Int, val code: List<String>) : Serializable
