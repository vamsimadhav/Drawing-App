package com.vmh.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.vmh.drawingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var brushSelector: ImageButton
    private lateinit var colorTray: LinearLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var mCurrentSelectedPaintButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView = binding.drawingSurface
        brushSelector = binding.brushSizeSelector
        colorTray = binding.colorTray
        mCurrentSelectedPaintButton = colorTray[1] as ImageButton

        drawingView.setBrushSize(20f)
        brushSelector.setOnClickListener { showBrushSizeDialog() }

        mCurrentSelectedPaintButton.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.color_tray_pressed
            )
        )
    }

     fun setBrushColor(view: View){
        if(view !== mCurrentSelectedPaintButton){
            val imageButton = view as ImageButton
            val color = imageButton.tag.toString()
            drawingView.setBrushColor(color)

            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.color_tray_pressed)
            )

            mCurrentSelectedPaintButton.setImageDrawable(
                ContextCompat.getDrawable(this,R.drawable.color_tray)
            )

            mCurrentSelectedPaintButton = view
        }
    }

    private fun showBrushSizeDialog(){
        val brushSelectionDialog = Dialog(this)
        brushSelectionDialog.setContentView(R.layout.brush_selection_dialog)
        brushSelectionDialog.setTitle("Brush Selector")

        val smallBrush: ImageButton = brushSelectionDialog.findViewById(R.id.smallBrush)
        val medBrush: ImageButton = brushSelectionDialog.findViewById(R.id.medBrush)
        val largeBrush: ImageButton = brushSelectionDialog.findViewById(R.id.largeBrush)

        smallBrush.setOnClickListener {
            drawingView.setBrushSize(10f)
            brushSelectionDialog.dismiss()
        }

        medBrush.setOnClickListener {
            drawingView.setBrushSize(20f)
            brushSelectionDialog.dismiss()
        }

        largeBrush.setOnClickListener {
            drawingView.setBrushSize(30f)
            brushSelectionDialog.dismiss()
        }

        brushSelectionDialog.show()
    }
}