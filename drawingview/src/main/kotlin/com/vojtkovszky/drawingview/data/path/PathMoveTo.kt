package com.vojtkovszky.drawingview.data.path

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PathMoveTo")
internal class PathMoveTo(
    val x: Float,
    val y: Float
) : PathInfo()