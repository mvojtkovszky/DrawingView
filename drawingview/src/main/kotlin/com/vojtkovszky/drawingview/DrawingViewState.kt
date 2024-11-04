package com.vojtkovszky.drawingview

import com.vojtkovszky.drawingview.data.SerializablePaint
import com.vojtkovszky.drawingview.data.SerializablePath
import com.vojtkovszky.drawingview.data.SerializationUtils
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

/**
 * Class holding all the necessary information required to draw paths and paints
 * on the canvas.
 */
@Serializable
class DrawingViewState: java.io.Serializable {

    private val drawPathHistory = mutableListOf<SerializablePath>()
    private val drawPaintHistory = mutableListOf<SerializablePaint>()
    private val undonePaths = mutableListOf<SerializablePath>()
    private val undonePaints = mutableListOf<SerializablePaint>()

    companion object {
        /**
         * Helper function to decode [DrawingViewState] from [String]
         * 'null' will be returned if [jsonString] cannot be decoded.
         */
        @Suppress("unused")
        fun fromJson(jsonString: String): DrawingViewState? {
            return try {
                SerializationUtils.json.decodeFromString<DrawingViewState>(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Helper function to encode [DrawingViewState] into [String]
         * 'null' will be returned if [state] cannot be encoded.
         */
        @Suppress("unused")
        fun toJson(state: DrawingViewState?): String? {
            return try {
                if (state != null) {
                    SerializationUtils.json.encodeToString(state)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // region Public
    /**
     * return number of available history steps
     */
    fun numHistorySteps(): Int {
        return drawPathHistory.size
    }

    /**
     * return number of available undone steps
     */
    fun numUndoneSteps(): Int {
        return undonePaths.size
    }

    /**
     * Determine if draw history is empty
     */
    fun isHistoryEmpty(): Boolean {
        return drawPathHistory.isEmpty() //&& drawPaintHistory.isEmpty()
    }

    /**
     * Determine if we have any paths or paints in undo history
     */
    fun isUndoneEmpty(): Boolean {
        return undonePaths.isEmpty() //&& undonePaints.isEmpty()
    }
    // endregion Public



    // region Internal
    // add path and paint to history
    internal fun addToHistory(path: SerializablePath, paint: SerializablePaint) {
        drawPathHistory.add(path)
        drawPaintHistory.add(paint)
    }

    // clears redo history by cleaning undone paths and paints
    internal fun clearRedoHistory() {
        undonePaths.clear()
        undonePaints.clear()
    }

    // return single path from history on index
    internal fun getPathFromHistory(index: Int): SerializablePath {
        return drawPathHistory[index]
    }

    // return single paint from history on index
    internal fun getPaintFromHistory(index: Int): SerializablePaint {
        return drawPaintHistory[index]
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

    // moves latest path and pant from history list to undone list
    // by removing from history list and adding to undone list
    internal fun undo() {
        undonePaths.add(drawPathHistory.removeAt(drawPathHistory.size - 1))
        undonePaints.add(drawPaintHistory.removeAt(drawPaintHistory.size - 1))
    }
    // endregion Internal
}