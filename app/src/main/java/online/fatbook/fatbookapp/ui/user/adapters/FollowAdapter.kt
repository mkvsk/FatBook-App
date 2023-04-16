package online.fatbook.fatbookapp.ui.user.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import online.fatbook.fatbookapp.R
import online.fatbook.fatbookapp.core.user.User
import online.fatbook.fatbookapp.core.user.UserSimpleObject
import online.fatbook.fatbookapp.databinding.RvUserFollowBinding
import online.fatbook.fatbookapp.ui.user.listeners.OnUserFollowClickListener
import online.fatbook.fatbookapp.util.BindableAdapter

class FollowAdapter(private val context: Context) :
    RecyclerView.Adapter<FollowAdapter.FollowItemViewHolder>(),
    BindableAdapter<UserSimpleObject> {

    private var followItemList: List<UserSimpleObject> = ArrayList()
    private var user: User = User()
    private lateinit var listener: OnUserFollowClickListener

    private var userFollow = false
    private var canSentMsg = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowItemViewHolder {
        val binding = RvUserFollowBinding.inflate(LayoutInflater.from(context), parent, false)

        return FollowItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return followItemList.size
    }

    override fun onBindViewHolder(holder: FollowItemViewHolder, position: Int) {
        val followItem = followItemList[position]
        holder.bind(followItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(data: List<UserSimpleObject>?) {
        data?.let {
            this.followItemList = it
            notifyDataSetChanged()
        }
    }

    fun setRvFollowClickListener(listener: OnUserFollowClickListener) {
        this.listener = listener
    }

    inner class FollowItemViewHolder(rvUserFollowBinding: RvUserFollowBinding) :
        RecyclerView.ViewHolder(rvUserFollowBinding.root) {
        private val binding = rvUserFollowBinding
        fun bind(userSimpleObject: UserSimpleObject) {
            binding.textviewUsernameRvFollow.text = userSimpleObject.username

            Glide.with(context)
                .load(userSimpleObject.profileImage)
                .placeholder(
                    AppCompatResources.getDrawable(
                        context,
                        R.drawable.ic_default_userphoto
                    )
                )
                .into(binding.imageviewUserphotoRvFollow)

            binding.cardviewRvFollow.setOnClickListener {
                listener.onSimpleUserClick(userSimpleObject.username!!.toString())
            }
            listener.onUserFollowClick()
            listener.onUserSendMessageClick()
        }
    }

}