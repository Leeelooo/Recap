package com.leeloo.post.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.leeloo.post.R
import kotlin.math.ceil


class AspectImageView : ShapeableImageView {
    /**
     * Set aspect ratio of View, value is
     * height-to-width ratio
     */
    var aspectRatio = 1f

    constructor(context: Context) : super(context)

    @SuppressLint("Recycle")
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        val typedArray = getContext().obtainStyledAttributes(
            attrs, R.styleable.AspectImageView, defStyleAttr, 0
        )
        aspectRatio = typedArray.getFloat(R.styleable.AspectImageView_aspectRatio, 1f)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = ceil((height.toDouble() / aspectRatio)).toInt()
        setMeasuredDimension(width, height)
    }
}