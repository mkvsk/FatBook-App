package online.fatbook.fatbookapp.design

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.fonts.Font
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.core.view.setPadding
import kotlinx.android.synthetic.main.view_common_button.view.*
import online.fatbook.fatbookapp.R


class ViewTabButtonItem(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private var tabButtonItemConfig: TabButtonItem? = null
//    private val invisibleColor = 268435456
    private var clickListener: OnClickListener? = null
    private var state: State = State.ENABLED
    private val view = inflate(context, R.layout.view_common_button, this)

    enum class State {
        ENABLED,
        DISABLED,
        DISABLED_BUT_CLICKABLE
    }

    init {


        val myAttrs = context.obtainStyledAttributes(
            attrs,
            R.styleable.ViewCommonButton, 0, 0
        )

        val text = myAttrs.getString(R.styleable.ViewCommonButton_android_text) ?: ""
        setText(text)

        val enabled = myAttrs.getBoolean(R.styleable.ViewCommonButton_android_enabled, true)
        isEnabled(enabled)
    }

    fun setStyle(tabButtonItemConfig: TabButtonItem?){
        this.tabButtonItemConfig = tabButtonItemConfig
        setFont()
        setDesignConfiguration()
        setOnTouchListener()
    }

    fun isEnabled(isEnabled: Boolean){
        view.click_field.isEnabled = isEnabled
        this.isEnabled = isEnabled
        setDesignConfiguration()
    }

    fun setState(state: State){
        this.state = state
        view.click_field.isEnabled = state == State.ENABLED || state == State.DISABLED_BUT_CLICKABLE
        this.isEnabled = state == State.ENABLED
        setDesignConfiguration()
    }

    fun getState(): State {
        return state
    }

    fun setText(text: String) {
        view.button_text.text = text
    }

    fun getText() = button_text.text.toString()

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnTouchListener(){
        view.click_field.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> setHoverDesignConfiguration()
                else -> setDesignConfiguration()
            }
            return@setOnTouchListener false
        }

        view.click_field.setOnClickListener {
            clickListener?.onClick(it)
        }
    }

    private fun setDesignConfiguration() {
        setBackground()
        setTextColor()
    }

    private fun setHoverDesignConfiguration() {
        setHoverBackground()
        setHoverTextColor()
    }

    override fun setOnClickListener(listener: OnClickListener?){
        clickListener = listener
    }

    private fun setBackground(){
        val borderColor: Int? = 1
        val borderWidth = 1
        val backgroundColor: Int? = 1
        val cornerRadius = 1f

        val background = GradientDrawable()

        backgroundColor?.let {
            background.setColor(backgroundColor)
        }
        borderColor?.let {
            background.setStroke(borderWidth, borderColor)
        }
        view.button_background.setPadding(borderWidth)
        background.cornerRadius = cornerRadius
        view.button_background.background = background
    }

    private fun setFont(){
        val font: Font? = null
        val textSize = 1f
        val fontName = ""

        view.button_text.textSize = textSize
    }

    private fun setHoverBackground(){
        val borderColor: Int? = 1
        val borderWidth = 1
        val backgroundColor: Int? = 1
        val cornerRadius = 1f

        val background = GradientDrawable()

        backgroundColor?.let {
            background.setColor(backgroundColor)
        }
        borderColor?.let {
            background.setStroke(borderWidth, borderColor)
        }
        button_background.setPadding(borderWidth)
        background.cornerRadius = cornerRadius
        view.button_background.background = background
    }

    private fun setTextColor(){
        val color: Int = 1
        view.button_text.setTextColor(color)
    }

    private fun setHoverTextColor(){
        val color: Int = 1
        view.button_text.setTextColor(color)
    }
}