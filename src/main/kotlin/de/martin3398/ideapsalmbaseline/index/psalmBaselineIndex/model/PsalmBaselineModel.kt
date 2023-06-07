package de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model

import java.io.Serializable

data class PsalmBaselineModel(val errors: List<PsalmBaselineErrorModel>, val index: Int, val file: String) : Serializable
