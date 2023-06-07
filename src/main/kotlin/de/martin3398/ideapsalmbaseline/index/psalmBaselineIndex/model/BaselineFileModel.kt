package de.martin3398.ideapsalmbaseline.index.psalmBaselineIndex.model

import java.io.Serializable

data class BaselineFileModel(val errors: List<BaselineErrorsModel>, val index: Int, val file: String) : Serializable
