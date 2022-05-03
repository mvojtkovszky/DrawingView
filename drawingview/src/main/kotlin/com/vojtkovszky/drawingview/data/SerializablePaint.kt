package com.vojtkovszky.drawingview.data

import java.io.Serializable

internal class SerializablePaint(
    var color: Int,
    var strokeWidth: Float,
    var isErasing: Boolean
): Serializable