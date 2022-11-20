package online.fatbook.fatbookapp.core.recipe

data class StaticDataLocalized(
        var locale: StaticDataLocale = StaticDataLocale.ENG,
        var title: String? = ""
)

