package com.vojtkovszky.drawingview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

@Suppress("unused")
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

    // region Private attributes
    private var drawPath: Path = Path() // drawing path
    private var drawPaint: Paint = Paint().apply { // drawing and canvas paint
        color = paintColor
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 30f
    }

    private val drawPathHistory = mutableListOf<Path>()
    private val drawPaintHistory = mutableListOf<Paint>()
    private val undonePaths = mutableListOf<Path>()
    private val undonePaints = mutableListOf<Paint>()

    private var xStart: Float = 0f // reference positions for last move (x)
    private var yStart: Float = 0f // reference positions for last move (y)
    private var isCurrentlyMoving = false
    private var isDrawingEmpty = true // track if anything written
        set(value) {
            field = value
            if (value) {
                listenerEmptyState?.let { it(true) }
            }
        }
    // endregion

    // region Public attributes
    /**
     * Color of the paint. Default is
     */
    var paintColor = DEFAULT_PAINT_COLOR
        set(value) {
            field = value
            invalidate()
            drawPaint.color = paintColor
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
    var isDrawingEnabled = true // drawing enabled flag

    /**
     * Eraser. If set to true, instead of painting, canvas will be clearing.
     */
    var isErasing = false // isErasing flag
        set(value) {
            field = value
            drawPaint.xfermode = if (value) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null
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
        canvas.drawColor(canvasColor)

        for (i in drawPathHistory.indices) {
            val path = drawPathHistory[i]
            val paint = drawPaintHistory[i]
            canvas.drawPath(path, paint)
        }
        canvas.drawPath(drawPath, drawPaint)

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
                undonePaths.clear()
                undonePaints.clear()

                drawPath.reset()
                drawPath.moveTo(touchX, touchY)

                xStart = touchX
                yStart = touchY

                listenerDrawingInProgress?.let { it(true) }
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = abs(touchX - xStart)
                val dy = abs(touchY - yStart)
                if (dx >= touchTolerance || dy >= touchTolerance) {
                    drawPath.quadTo(xStart, yStart, (touchX + xStart) / 2, (touchY + yStart) / 2)
                    xStart = touchX
                    yStart = touchY
                    isCurrentlyMoving = true
                }
            }

            MotionEvent.ACTION_UP -> {
                // simply touched the canvas, do a dot instead
                if (!isCurrentlyMoving && xStart == touchX && yStart == touchY) {
                    drawPath.addCircle(touchX, touchY, 0.1f, Path.Direction.CW)
                }

                drawPathHistory.add(drawPath)
                drawPaintHistory.add(Paint(drawPaint))

                drawPath = Path()

                if (isDrawingEmpty) listenerEmptyState?.let { it(false) }
                isDrawingEmpty = false

                isCurrentlyMoving = false
                listenerDrawingInProgress?.let { it(false) }
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
        drawPathHistory.clear()
        drawPaintHistory.clear()
        undonePaths.clear()
        undonePaints.clear()

        invalidate()

        isDrawingEmpty = true
    }

    /**
     * Undo
     */
    fun undo() {
        if (drawPathHistory.size > 0) {
            undonePaths.add(drawPathHistory.removeAt(drawPathHistory.size - 1))
            undonePaints.add(drawPaintHistory.removeAt(drawPaintHistory.size - 1))
            invalidate()
        }

        if (drawPathHistory.isEmpty()) {
            isDrawingEmpty = true
        }
    }

    /**
     * Undo all known steps
     * Will look similar as [startNew], but the whole history remains and can be redone using
     * [redo] or [redoAll]
     */
    fun undoAll() {
        repeat(drawPathHistory.size) {
            undo()
        }
    }

    /**
     * Redo
     */
    fun redo() {
        if (undonePaths.size > 0) {
            drawPathHistory.add(undonePaths.removeAt(undonePaths.size - 1))
            drawPaintHistory.add(undonePaints.removeAt(undonePaints.size - 1))

            invalidate()

            isDrawingEmpty = false
        }
    }

    /**
     * Redo all known undone steps
     */
    fun redoAll() {
        repeat(undonePaths.size) {
            redo()
        }
    }

    /**
     * Cancel current path in progress
     */
    fun invalidateLastPath() {
        drawPath.reset()
        invalidate()
    }

    /**
     * Determine if canvas is empty.
     */
    fun isDrawingEmpty(): Boolean {
        return isDrawingEmpty
    }
    // endregion
}