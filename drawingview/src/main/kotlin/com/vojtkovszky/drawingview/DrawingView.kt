package com.vojtkovszky.drawingview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.vojtkovszky.drawingview.data.*
import com.vojtkovszky.drawingview.data.path.PathAddCircle
import com.vojtkovszky.drawingview.data.path.PathMoveTo
import com.vojtkovszky.drawingview.data.path.PathQuadTo
import com.vojtkovszky.drawingview.data.path.PathReset
import kotlin.math.abs

@Suppress("MemberVisibilityCanBePrivate", "unused")
class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    companion object {
        private const val DEFAULT_TOUCH_TOLERANCE = 4f
        private const val DEFAULT_PAINT_COLOR = Color.BLACK
        private const val DEFAULT_CANVAS_COLOR = Color.WHITE
        private const val DEFAULT_BRUSH_SIZE = 8f
    }

    // region Private attributes, representing state of
    private var drawPath: SerializablePath = SerializablePath() // drawing path
    private var drawPaint: SerializablePaint = SerializablePaint(DEFAULT_PAINT_COLOR, 30f, false)
    private var xStart: Float = 0f // reference positions for last move (x)
    private var yStart: Float = 0f // reference positions for last move (y)
    private var isCurrentlyMoving = false
    // endregion

    // region Public attributes
    /**
     * State holding all necessary information to populate the view and hold its history.
     * Setting state will cause view to redraw with new state.
     */
    var state = DrawingViewState()
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Color of the paint.
     */
    var paintColor = DEFAULT_PAINT_COLOR
        set(value) {
            field = value
            invalidate()
            drawPaint.color = value
        }

    /**
     * Color of the canvas
     */
    var canvasColor = DEFAULT_CANVAS_COLOR
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Size of the brush in pixels
     */
    var brushSize: Float = DEFAULT_BRUSH_SIZE
        set(value) {
            field = value
            drawPaint.strokeWidth = value
        }

    /**
     * Flag to determine if drawing is allowed.
     * If false, all touch events will be ignored.
     */
    var isDrawingEnabled = true

    /**
     * Eraser. If set to true, instead of painting, canvas will be clearing.
     */
    var isErasing = false
        set(value) {
            field = value
            drawPaint.isErasing = value
        }

    /**
     * In pixels, minimum absolute distance of travel needed in order to register start of an ongoing move.
     * However, only a tap on the canvas will be drawn as a circle with diameter of [brushSize]
     */
    var touchTolerance = DEFAULT_TOUCH_TOLERANCE // number of pixels

    /**
     * Callback when canvas changes from empty to non-empty or non-empty to empty.
     * For example, callback will return true when drawing anything on an empty canvas and false
     * when calling [startNew] or [redo] a last element.
     * But it will not return anything if you happen to use eraser to clear the whole canvas
     */
    var listenerEmptyState: ((isCanvasEmpty: Boolean) -> Unit)? = null

    /**
     * Callback when user starts or stops drawing (dragging).
     */
    var listenerDrawingInProgress: ((drawInProgress: Boolean) -> Unit)? = null
    // endregion

    override fun onDraw(canvas: Canvas) {
        // background color
        canvas.drawColor(canvasColor)

        // draw whole history
        val numStepsInHistory = state.numHistorySteps()
        if (numStepsInHistory > 0) {
            for (i in 0 until numStepsInHistory) {
                canvas.drawPath(
                    state.getPathFromHistory(i).getPath(),
                    state.getPaintFromHistory(i).getPaint()
                )
            }
        }

        // and the current path
        canvas.drawPath(drawPath.getPath(), drawPaint.getPaint())

        super.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDrawingEnabled) {
            return false
        }

        // mark current coordinates
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                state.clearRedoHistory()

                drawPath.add(PathReset())
                drawPath.add(PathMoveTo(x = touchX, y = touchY))

                xStart = touchX
                yStart = touchY

                listenerDrawingInProgress?.invoke(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = abs(touchX - xStart)
                val dy = abs(touchY - yStart)
                if (dx >= touchTolerance || dy >= touchTolerance) {
                    drawPath.add(PathQuadTo(
                        x1 = xStart,
                        y1 = yStart,
                        x2 = (touchX + xStart) / 2,
                        y2 = (touchY + yStart) / 2))
                    xStart = touchX
                    yStart = touchY
                    isCurrentlyMoving = true
                }
            }

            MotionEvent.ACTION_UP -> {
                val drawingEmptyBeforeAdding = state.isHistoryEmpty()

                // simply touched the canvas, do a dot instead
                if (!isCurrentlyMoving && xStart == touchX && yStart == touchY) {
                    drawPath.add(PathAddCircle(x = touchX, y = touchY, radius = 0.1f))
                }

                // add path to history
                state.addToHistory(drawPath, drawPaint)
                // recreate path and paint for new step
                drawPath = SerializablePath()
                drawPaint = SerializablePaint(paintColor, brushSize, isErasing)

                isCurrentlyMoving = false

                // moved from empty to no longer empty
                if (drawingEmptyBeforeAdding) {
                    listenerEmptyState?.invoke(false)
                }
                // draw no longer in progress
                listenerDrawingInProgress?.invoke(false)
            }

            else -> return false
        }

        invalidate()
        return true
    }

    // region Public methods
    /**
     * Clear canvas. Will also clear all history
     */
    fun startNew() {
        state.startNew()
        invalidate()
        listenerEmptyState?.invoke(true)
    }

    /**
     * Undo
     */
    fun undo() {
        if (!state.isHistoryEmpty()) {
            state.undo()
            invalidate()
        }

        if (state.isHistoryEmpty()) {
            listenerEmptyState?.invoke(true)
        }
    }

    /**
     * Undo all known steps
     * Will look similar as [startNew], but the whole history remains and can be redone using
     * [redo] or [redoAll]
     */
    fun undoAll() {
        repeat(state.numHistorySteps()) {
            undo()
        }
    }

    /**
     * Redo
     */
    fun redo() {
        if (!state.isUndoneEmpty()) {
            state.redo()
            invalidate()
        }
    }

    /**
     * Redo all known undone steps
     */
    fun redoAll() {
        repeat(state.numUndoneSteps()) {
            redo()
        }
    }

    /**
     * Clears undone history, essentially making [redo] do nothing
     */
    fun clearRedoHistory() {
        state.clearRedoHistory()
    }

    /**
     * Cancel current path in progress
     */
    fun invalidateLastPath() {
        drawPath = SerializablePath()
        invalidate()
    }

    /**
     * Determine if canvas is empty.
     */
    fun isDrawingEmpty(): Boolean {
        return state.isHistoryEmpty()
    }
    // endregion
}