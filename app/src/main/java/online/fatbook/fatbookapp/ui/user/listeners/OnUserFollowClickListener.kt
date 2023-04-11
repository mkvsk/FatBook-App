package online.fatbook.fatbookapp.ui.user.listeners

interface OnUserFollowClickListener {
    fun onSimpleUserClick(username: String)
    fun onUserSendMessageClick()
    fun onUserFollowClick()
}