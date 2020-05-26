package com.vojtkovszky.drawingview.example

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        drawingView.apply {
            paintColor = Color.BLACK
            brushSize = 20f
            listenerEmptyState = {
                println("Canvas empty: $it")
            }
            listenerDrawingInProgress = {
                Toast.makeText(context, "Finger ${if(it) "down" else "up"}", Toast.LENGTH_SHORT).show()
            }
        }

        textClear.setOnClickListener { drawingView.startNew() }
        textUndo.setOnClickListener { drawingView.undo() }
        textRedo.setOnClickListener { drawingView.redo() }
    }
}
