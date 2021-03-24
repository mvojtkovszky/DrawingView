package com.vojtkovszky.drawingview.example

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.vojtkovszky.drawingview.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.drawingView.apply {
            paintColor = Color.BLACK
            brushSize = 20f
            listenerEmptyState = {
                println("Canvas empty: $it")
            }
            listenerDrawingInProgress = {
                Toast.makeText(context, "Finger ${if(it) "down" else "up"}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textClear.setOnClickListener {
            binding.drawingView.startNew()
        }

        binding.textUndo.setOnClickListener {
            binding.drawingView.undo()
        }
        binding.textUndo.setOnLongClickListener {
            binding.drawingView.undoAll()
            return@setOnLongClickListener false
        }

        binding.textRedo.setOnClickListener {
            binding.drawingView.redo()
        }
        binding.textRedo.setOnLongClickListener {
            binding.drawingView.redoAll()
            return@setOnLongClickListener false
        }
    }
}
