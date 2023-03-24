package online.fatbook.fatbookapp.ui.user.listeners

import online.fatbook.fatbookapp.core.user.UserSimpleObject

interface OnUserFollowClickListener {
    fun onUserLinkClick(user: UserSimpleObject)
    fun onUserSendMessageClick()
    fun onUserFollowClick()
}