package online.fatbook.fatbookapp.ui.user.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.rv_feed_recipe_card_preview.view.*
import kotlinx.android.synthetic.main.rv_user_follow.view.*
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.core.user.UserSimpleObject
import online.fatbook.fatbookapp.ui.user.listeners.OnUserFollowClickListener
import online.fatbook.fatbookapp.util.BindableAdapter

class FollowAdapter : RecyclerView.Adapter<FollowAdapter.ViewHolder>(),
    BindableAdapter<UserSimpleObject> {

    private var data: List<UserSimpleObject> = ArrayList()
    private var user: User = User()
    private lateinit var listener: OnUserFollowClickListener
    private var context: Context? = null

    private var userFollow = false
    private var canSentMsg = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rv_user_follow, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<UserSimpleObject>?) {
        data?.let {
            this.data = it
            notifyDataSetChanged()
        }
    }

    fun setRvFollowClickListener(listener: OnUserFollowClickListener) {
        this.listener = listener
    }

    fun setContext(context: Context) {
        this.context = context
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(userSimpleObject: UserSimpleObject) {
            itemView.textview_username_rv_follow.setText(userSimpleObject.username)

            Glide.with(itemView.context)
                .load(userSimpleObject.profileImage)
                .placeholder(itemView.context.getDrawable(R.drawable.ic_default_userphoto))
                .into(itemView.imageview_userphoto_rv_follow)

            itemView.cardview_rv_follow.setOnClickListener {
                listener.onSimpleUserClick(userSimpleObject.username!!.toString())
            }
            listener.onUserFollowClick()
            listener.onUserSendMessageClick()
        }
    }

}