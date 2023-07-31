package com.vmh.drawingapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var mBrushColor = Color.BLACK
    private var mCanvas: Canvas? = null

    init {
        setUpDrawingCanvas()
    }

    private fun setUpDrawingCanvas(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(mBrushColor,mBrushSize)
        mDrawPaint!!.color = mBrushColor
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    internal inner class CustomPath(color: Int,brushThickness: Float): Path() {

    }
}