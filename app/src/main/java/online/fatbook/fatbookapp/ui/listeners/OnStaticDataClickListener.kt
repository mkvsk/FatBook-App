package online.fatbook.fatbookapp.ui.listeners

import online.fatbook.fatbookapp.core.recipe.StaticDataBase

interface OnStaticDataClickListener {

    fun onItemClick(item: StaticDataBase)
    fun onItemClickChoose(item: StaticDataBase)
}