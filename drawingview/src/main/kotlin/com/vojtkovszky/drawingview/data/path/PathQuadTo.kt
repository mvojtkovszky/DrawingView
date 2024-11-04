package com.vojtkovszky.drawingview.data.path

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PathQuadTo")
internal class PathQuadTo(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float
) : PathInfo()
