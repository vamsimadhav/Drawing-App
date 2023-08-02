package com.vmh.drawingapp

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.vmh.drawingapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.AccessController.getContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var colorTray: LinearLayout
    private lateinit var drawingContainer: FrameLayout
    private lateinit var canvasBackgroundImage: ImageView
    private lateinit var drawingView: DrawingView

    private lateinit var brushSelector: ImageButton
    private lateinit var mCurrentSelectedPaintButton: ImageButton
    private lateinit var imagePicker: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var redoButton: ImageButton
    private lateinit var saveButton: ImageButton
//    private lateinit var shareButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView = binding.drawingSurface
        drawingContainer = binding.drawingSurfaceContainer
        colorTray = binding.colorTray
        canvasBackgroundImage = binding.backgroundImage

        imagePicker = binding.imageSelector
        brushSelector = binding.brushSizeSelector
        undoButton = binding.undoSelector
        redoButton = binding.redoSelector
        saveButton = binding.saveSelector
//        shareButton = binding.shareSelector

        mCurrentSelectedPaintButton = colorTray[1] as ImageButton

        drawingView.setBrushSize(20f)
        brushSelector.setOnClickListener { showBrushSizeDialog() }

        mCurrentSelectedPaintButton.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.color_tray_pressed
            )
        )

        imagePicker.setOnClickListener { openImagePicker() }
        undoButton.setOnClickListener { drawingView.undoPrevDraw() }
        redoButton.setOnClickListener { drawingView.redoPrevDraw() }

        saveButton.setOnClickListener {
            val bitmap = getBitmapFromView(drawingContainer)
            lifecycleScope.launch {
                saveBitmapToFile(bitmap)
            }
        }

//        shareButton.setOnClickListener {
//            val bitmap = getBitmapFromView(drawingContainer)
//            lifecycleScope.launch {
//                val result = saveBitmapToFile(bitmap)
//                shareDrawing(result)
//            }
//
//        }
    }

    private fun openImagePicker() {
        getImagePickerLauncher.launch("image/*")
    }

    private val getImagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let{
                    canvasBackgroundImage.setImageURI(uri)
                }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width,view.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background

        if(bgDrawable != null){
            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)

        return returnedBitmap
    }

    private suspend fun saveBitmapToFile(bitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO){
            if(bitmap != null){
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)

                    val f = File(externalCacheDir?.absoluteFile.toString() +
                            File.separator + "DrawingApp_" + System.currentTimeMillis()/1000 + ".png")

                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result = f.absolutePath

                    runOnUiThread {
                        if(result.isNotEmpty()){
                            Toast.makeText(this@MainActivity,"Saved",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@MainActivity,"Failed",Toast.LENGTH_SHORT).show()
                        }
                    }

                }catch (e: Exception){
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return  result
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

//    private fun shareDrawing(url: String) {
//        val shareIntent = Intent(Intent.ACTION_SEND)
//        shareIntent.type = "image/png"
//        shareIntent.putExtra(Intent.EXTRA_STREAM, url)
//
//        // Check if there are any apps that can handle the sharing Intent
//        val packageManager: PackageManager = baseContext.packageManager
//        val activities = packageManager.queryIntentActivities(shareIntent, 0)
//        val isIntentSafe = activities.size > 0
//
//        // Start the sharing Intent if there are suitable apps available
//        if (isIntentSafe) {
//            startActivity(Intent.createChooser(shareIntent, "Share Deep Link via"))
//        } else {
//            // Handle the case when no apps can handle the sharing Intent
//            Toast.makeText(this, "No apps available to handle sharing", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }
}