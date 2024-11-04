package com.vojtkovszky.drawingview.data

import com.vojtkovszky.drawingview.data.path.PathInfo
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
internal class SerializablePath : java.io.Serializable {
    val data: MutableList<@Polymorphic PathInfo> = mutableListOf()

    fun add(pathInfo: PathInfo) {
        data.add(pathInfo)
    }
}