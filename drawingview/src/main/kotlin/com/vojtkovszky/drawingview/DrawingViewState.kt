package com.vojtkovszky.drawingview

import android.graphics.Paint
import android.graphics.Path
import java.io.Serializable

class DrawingViewState: Serializable {

    internal val drawPathHistory = mutableListOf<Path>()
    internal val drawPaintHistory = mutableListOf<Paint>()
    internal val undonePaths = mutableListOf<Path>()
    internal val undonePaints = mutableListOf<Paint>()

    // moves latest path and pant from history list to undone list
    // by removing from history list and adding to undone list
    internal fun undo() {
        undonePaths.add(drawPathHistory.removeAt(drawPathHistory.size - 1))
        undonePaints.add(drawPaintHistory.removeAt(drawPaintHistory.size - 1))
    }

    // moves latest path and pant from undone list to history list
    // by removing from undone list and adding to history list
    internal fun redo() {
        drawPathHistory.add(undonePaths.removeAt(undonePaths.size - 1))
        drawPaintHistory.add(undonePaints.removeAt(undonePaints.size - 1))
    }

    // reset the history and undone lists
    internal fun startNew() {
        drawPathHistory.clear()
        drawPaintHistory.clear()
        undonePaths.clear()
        undonePaints.clear()
    }

    fun isDrawingEmpty(): Boolean {
        return drawPathHistory.isEmpty() && drawPaintHistory.isEmpty()
    }
}