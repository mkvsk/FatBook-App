package online.fatbook.fatbookapp.core.recipe

data class StaticDataLocalizedObject(
        var locale: StaticDataLocale = StaticDataLocale.ENG,
        var title: String? = ""
)

