package com.vojtkovszky.drawingview.data

import com.vojtkovszky.drawingview.data.path.PathInfo
import java.io.Serializable

internal class SerializablePath: Serializable {
    val data: MutableList<PathInfo> = mutableListOf()

    fun add(pathInfo: PathInfo) {
        data.add(pathInfo)
    }
}