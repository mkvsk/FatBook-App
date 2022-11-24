package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.StaticDataObject

interface OnSearchItemClickListener {
    fun onItemClick(item: StaticDataObject)
    fun onSelectAllClick()
}