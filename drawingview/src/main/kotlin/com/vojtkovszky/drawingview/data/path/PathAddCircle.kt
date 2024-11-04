package com.vojtkovszky.drawingview.data.path

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PathAddCircle")
internal class PathAddCircle(
    val x: Float,
    val y: Float,
    val radius: Float
) : PathInfo()