package com.pakollya.paginglist

import android.content.Context
import android.util.AttributeSet

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