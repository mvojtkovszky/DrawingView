package com.vojtkovszky.drawingview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

@Suppress("unused")
class DrawingView : View {

    companion object {
        private const val TOUCH_TOLERANCE = 4f
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

    private lateinit var drawCanvas: Canvas // drawing canvas
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
    var paintColor = 0x000000 // initial color
        set(value) {
            field = value
            invalidate()
            drawPaint.color = paintColor
        }
    var brushSize: Float = 0f // initial brush size
        set(value) {
            field = value
            drawPaint.strokeWidth = value
        }
    var isDrawingEnabled = true // drawing enabled flag
    var isErasing = false // isErasing flag
        set(value) {
            field = value
            drawPaint.xfermode = if (value) PorterDuffXfermode(PorterDuff.Mode.CLEAR) else null
        }
    var listenerEmptyState: ((Boolean) -> Unit)? = null // callback when canvas changes to empty or from empty to not empty
    var listenerDrawingInProgress: ((Boolean) -> Unit)? = null // callback when user starts or stops drawing
    // endregion

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        //view given size
        super.onSizeChanged(w, h, oldw, oldh)

        val canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)
    }

    override fun onDraw(canvas: Canvas) {
        for (i in drawPathHistory.indices) {
            val path = drawPathHistory[i]
            val paint = drawPaintHistory[i]
            canvas.drawPath(path, paint)
        }
        canvas.drawPath(drawPath, drawPaint)
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
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
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

                drawCanvas.drawPath(drawPath, drawPaint)

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
     * Clear canvas and history
     */
    fun startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR)

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
     * Redo
     */
    fun redo() {
        if (undonePaths.size > 0) {
            drawPathHistory.add(undonePaths.removeAt(undonePaths.size - 1))
            drawPaintHistory.add(undonePaints.removeAt(undonePaints.size - 1))
            invalidate()
        }
    }

    /**
     * Cancel current path in progress
     */
    fun invalidateLastPath() {
        drawPath.reset()
        invalidate()
    }
    // endregion
}