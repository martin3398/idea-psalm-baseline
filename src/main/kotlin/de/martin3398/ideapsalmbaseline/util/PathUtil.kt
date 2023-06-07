package de.martin3398.ideapsalmbaseline.util

import kotlin.io.path.Path
import kotlin.io.path.relativeTo

fun getRelativePath(abs: String, base: String): String {
    val absPath = Path(abs)
    val basePath = Path(base)

    return absPath.relativeTo(basePath).toString()
}
