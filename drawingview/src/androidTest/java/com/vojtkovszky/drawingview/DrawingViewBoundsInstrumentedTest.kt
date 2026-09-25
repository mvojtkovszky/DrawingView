package com.vojtkovszky.drawingview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vojtkovszky.drawingview.test.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DrawingViewBoundsInstrumentedTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun clampDrawingToBoundsCanBeSetFromXml() {
        instrumentation.runOnMainSync {
            val view = LayoutInflater.from(instrumentation.targetContext)
                .inflate(R.layout.test_bounded_drawing_view, null) as DrawingView

            assertTrue(view.clampDrawingToBounds)
        }
    }

    @Test
    fun clampedGestureDoesNotRenderBelowOriginalBoundsAfterResize() {
        instrumentation.runOnMainSync {
            val view = DrawingView(instrumentation.targetContext).apply {
                brushSize = 20f
                clampDrawingToBounds = true
                layout(0, 0, 100, 100)
            }

            sendTouch(view, MotionEvent.ACTION_DOWN, 50f, 50f)
            sendTouch(view, MotionEvent.ACTION_MOVE, 50f, 150f)
            sendTouch(view, MotionEvent.ACTION_UP, 50f, 150f)

            view.layout(0, 0, 100, 200)
            val bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))

            for (y in 100 until bitmap.height) {
                for (x in 0 until bitmap.width) {
                    assertEquals(Color.WHITE, bitmap.getPixel(x, y))
                }
            }
        }
    }

    @Test
    fun tapWithinTouchToleranceRendersSolidBrushSizedDot() {
        instrumentation.runOnMainSync {
            val view = DrawingView(instrumentation.targetContext).apply {
                brushSize = 20f
                paintColor = Color.BLACK
                layout(0, 0, 100, 100)
            }

            sendTouch(view, MotionEvent.ACTION_DOWN, 50f, 50f)
            sendTouch(view, MotionEvent.ACTION_UP, 52f, 51f)

            val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
            view.draw(Canvas(bitmap))

            assertEquals(Color.BLACK, bitmap.getPixel(52, 51))
            assertEquals(Color.BLACK, bitmap.getPixel(52, 56))
            assertEquals(Color.WHITE, bitmap.getPixel(52, 62))
        }
    }

    private fun sendTouch(view: DrawingView, action: Int, x: Float, y: Float) {
        MotionEvent.obtain(0L, 0L, action, x, y, 0).also { event ->
            view.onTouchEvent(event)
            event.recycle()
        }
    }
}
