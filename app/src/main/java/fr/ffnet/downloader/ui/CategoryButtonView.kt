package fr.ffnet.downloader.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import fr.ffnet.downloader.R

class CategoryButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var isSelectable = false
    private var isChecked = false

    init {
        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.CategoryButtonView,
            defStyleAttr,
            0
        )
        isSelectable = array.getBoolean(R.styleable.CategoryButtonView_isSelectable, false)
        isChecked = array.getBoolean(R.styleable.CategoryButtonView_isChecked, false)
        array.recycle()

        if (isSelectable) {
            setOnClickListener {
                isChecked = isChecked.not()
                setStyle()
            }
        }
    }

    private fun setStyle() {
        if (isChecked) {
            setTextColor(context.getColor(R.color.ff_blue_lighter))
            background = ContextCompat.getDrawable(context, R.drawable.button_border_checked)
        } else {
            setTextColor(context.getColor(R.color.ff_grey_darker))
            background = ContextCompat.getDrawable(context, R.drawable.button_border_unchecked)
        }
    }
}
