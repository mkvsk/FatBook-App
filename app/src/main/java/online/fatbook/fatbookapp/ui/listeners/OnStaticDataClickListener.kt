package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.StaticDataObject

interface OnStaticDataClickListener {

    fun onItemClick(item: StaticDataObject)
    fun onItemClickChoose(item: StaticDataObject)
}