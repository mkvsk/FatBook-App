package online.fatbook.fatbookapp.design

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.core.view.setPadding
import androidx.core.view.size
import kotlinx.android.synthetic.main.view_tab_button.view.*
import online.fatbook.fatbookapp.R


class ViewTabButton(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val view = inflate(context, R.layout.view_tab_button, this)
    private var buttonConfig: TabButton? = null
//    private val invisibleColor = 268435456
    private var onTabSelectedListener: TabSelectedListener? = null
    private var tabList = ArrayList<ViewTabButtonItem>()
    private val animDuration = 100L
    var selectedTab: Int = 0

    fun setStyle(buttonConfig: TabButton?){
        this.buttonConfig = buttonConfig
        setBackground()
        setCarriageStyle()
        tabList.forEach {
            it.setStyle(buttonConfig?.inactive)
        }
    }

    fun setTabSelectedListener(listener: TabSelectedListener){
        this.onTabSelectedListener = listener
    }

    fun addTab(tabName: String?){
        val name = tabName?: ""
        val tab = ViewTabButtonItem(context)
        tab.setText(name)
        tab.tag = name
        tab.layoutParams = view.example_tab.layoutParams
        view.tabs_container.addView(tab)
        val position = view.tabs_container.size - 1
        tab.id = position
        tab.setOnClickListener {
            selectTab(position)
            selectedTab = position
        }
        tabList.add(tab)

        resizeCarriage()
        setSelectedTabName(tabList[0].tag.toString())
    }

    fun selectTab(position: Int){
        setSelectedTabName(tabList[position].tag.toString())
        animCarriage(position)
        onTabSelectedListener?.onTabSelected(position)
    }

    fun setTab(position: Int) {
        setSelectedTabName(tabList[position].tag.toString())
        view.carriage.x = view.carriage.layoutParams.width.toFloat() * position
    }

    private fun resizeCarriage(){
        val tab = tabs_container.getChildAt(view.tabs_container.size - 1)
        tab.post {
            val params = LayoutParams(tab.width, tab.height)
            view.carriage.layoutParams = params
        }
    }

    private fun animCarriage(position: Int){
        view.carriage.animate()
            .translationX((carriage.width * position).toFloat())
            .setDuration(animDuration)
            .setInterpolator(LinearInterpolator())
            .start()
    }

    private fun setSelectedTabName(name: String){
        view.carriage.setText(name)
    }

    private fun setCarriageStyle(){
        view.carriage.setStyle(buttonConfig?.active)
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
        view.tab_button_background.setPadding(borderWidth)

        background.cornerRadius = cornerRadius
        view.tab_button_background.background = background
    }
}

interface TabSelectedListener{
    fun onTabSelected(position: Int)
}