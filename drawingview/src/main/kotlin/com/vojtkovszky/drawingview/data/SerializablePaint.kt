package com.vojtkovszky.drawingview.data

import kotlinx.serialization.Serializable

@Serializable
internal class SerializablePaint(
    var color: Int,
    var strokeWidth: Float,
    var isErasing: Boolean
): java.io.Serializable