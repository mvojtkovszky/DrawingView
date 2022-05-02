package com.vojtkovszky.drawingview

import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.vojtkovszky.drawingview.data.*
import com.vojtkovszky.drawingview.data.path.PathAddCircle
import com.vojtkovszky.drawingview.data.path.PathMoveTo
import com.vojtkovszky.drawingview.data.path.PathQuadTo
import com.vojtkovszky.drawingview.data.path.PathReset

/**
 * Get instance of [Paint] object from out own [SerializablePaint]
 */
internal fun SerializablePaint.getPaint(): Paint = Paint().apply { // drawing and canvas paint
    color = this@getPaint.color
    isAntiAlias = true
    style = Paint.Style.STROKE
    strokeJoin = Paint.Join.ROUND
    strokeCap = Paint.Cap.ROUND
    strokeWidth = this@getPaint.strokeWidth
    xfermode = if (this@getPaint.isErasing) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null
}

/**
 * Get instance of [Path] object from out own [SerializablePath]
 */
internal fun SerializablePath.getPath(): Path = Path().apply {
    for (pathInfo in this@getPath.data) {
        when (pathInfo) {
            is PathReset -> reset()
            is PathMoveTo -> moveTo(pathInfo.x, pathInfo.y)
            is PathQuadTo -> quadTo(pathInfo.x1, pathInfo.y1, pathInfo.x2, pathInfo.y2)
            is PathAddCircle -> addCircle(pathInfo.x, pathInfo.y, pathInfo.radius, Path.Direction.CW)
        }
    }
}