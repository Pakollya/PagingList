package com.pakollya.paginglist

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class CustomTextView : androidx.appcompat.widget.AppCompatTextView, BaseView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun show(text: CharSequence) {
        setText(text)
    }
}

class CustomConstraintLayout : ConstraintLayout, BaseView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun select(selected: Boolean) {
        setBackgroundColor(
            ContextCompat.getColor(
                this.context,
                if (selected) R.color.selected else R.color.white
            )
        )
    }
}