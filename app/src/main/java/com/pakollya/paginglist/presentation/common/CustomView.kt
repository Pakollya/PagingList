package com.pakollya.paginglist.presentation.common

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.pakollya.paginglist.R

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